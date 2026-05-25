package com.smartauto.oa.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.smartauto.oa.common.BusinessException;
import com.smartauto.oa.common.MinioStorageService;
import com.smartauto.oa.common.PageResult;
import com.smartauto.oa.common.SecurityUtils;
import com.smartauto.oa.system.entity.SysRole;
import com.smartauto.oa.system.entity.SysUser;
import com.smartauto.oa.system.mapper.SysDeptMapper;
import com.smartauto.oa.system.mapper.SysPostMapper;
import com.smartauto.oa.system.mapper.SysRoleMapper;
import com.smartauto.oa.system.mapper.SysUserMapper;
import com.smartauto.oa.system.service.ISysUserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

/**
 * 用户管理服务实现
 */
@Service
public class SysUserServiceImpl implements ISysUserService {

    private final SysUserMapper userMapper;
    private final SysRoleMapper roleMapper;
    private final SysPostMapper postMapper;
    private final SysDeptMapper deptMapper;
    private final MinioStorageService storageService;

    public SysUserServiceImpl(SysUserMapper userMapper,
                              SysRoleMapper roleMapper,
                              SysPostMapper postMapper,
                              SysDeptMapper deptMapper,
                              MinioStorageService storageService) {
        this.userMapper = userMapper;
        this.roleMapper = roleMapper;
        this.postMapper = postMapper;
        this.deptMapper = deptMapper;
        this.storageService = storageService;
    }

    @Override
    public PageResult<SysUser> page(SysUser query, int pageNum, int pageSize) {
        Page<SysUser> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(query.getUsername()), SysUser::getUsername, query.getUsername())
               .like(StringUtils.hasText(query.getNickname()), SysUser::getNickname, query.getNickname())
               .like(StringUtils.hasText(query.getPhone()), SysUser::getPhone, query.getPhone())
               .eq(query.getStatus() != null, SysUser::getStatus, query.getStatus())
               .eq(query.getDeptId() != null, SysUser::getDeptId, query.getDeptId())
               .orderByAsc(SysUser::getId);
        Page<SysUser> result = userMapper.selectPage(page, wrapper);
        return new PageResult<>(result.getTotal(), result.getRecords(), pageNum, pageSize);
    }

    @Override
    public SysUser getById(Long id) {
        SysUser user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        // 查询用户角色
        List<SysRole> roles = roleMapper.selectRolesByUserId(id);
        user.setRoles(roles);
        user.setRoleIds(roles.stream().map(SysRole::getId).toList());
        return user;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void create(SysUser user) {
        // 校验用户名唯一
        SysUser existing = userMapper.selectByUsername(user.getUsername());
        if (existing != null) {
            throw new BusinessException("用户名'" + user.getUsername() + "'已存在");
        }
        // 加密密码
        user.setPassword(SecurityUtils.encryptPassword(user.getPassword()));
        userMapper.insert(user);
        // 关联角色
        if (user.getRoleIds() != null && !user.getRoleIds().isEmpty()) {
            for (Long roleId : user.getRoleIds()) {
                userMapper.insertUserRole(user.getId(), roleId);
            }
        }
        // 关联岗位
        if (user.getPostIds() != null && !user.getPostIds().isEmpty()) {
            for (Long postId : user.getPostIds()) {
                userMapper.insertUserPost(user.getId(), postId);
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(SysUser user) {
        SysUser existing = userMapper.selectById(user.getId());
        if (existing == null) {
            throw new BusinessException("用户不存在");
        }
        // 校验用户名唯一（排除自身）
        SysUser byName = userMapper.selectByUsername(user.getUsername());
        if (byName != null && !byName.getId().equals(user.getId())) {
            throw new BusinessException("用户名'" + user.getUsername() + "'已存在");
        }
        // 不更新密码
        user.setPassword(null);
        userMapper.updateById(user);
        // 更新角色关联：先删后插
        userMapper.deleteUserRoleByUserId(user.getId());
        if (user.getRoleIds() != null && !user.getRoleIds().isEmpty()) {
            for (Long roleId : user.getRoleIds()) {
                userMapper.insertUserRole(user.getId(), roleId);
            }
        }
        // 更新岗位关联：先删后插
        userMapper.deleteUserPostByUserId(user.getId());
        if (user.getPostIds() != null && !user.getPostIds().isEmpty()) {
            for (Long postId : user.getPostIds()) {
                userMapper.insertUserPost(user.getId(), postId);
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(List<Long> ids) {
        for (Long id : ids) {
            if (SecurityUtils.isAdmin(id)) {
                throw new BusinessException("不允许删除管理员用户");
            }
        }
        userMapper.deleteBatchIds(ids);
    }

    @Override
    public void updateStatus(Long id, Integer status) {
        SysUser user = new SysUser();
        user.setId(id);
        user.setStatus(status);
        userMapper.updateById(user);
    }

    @Override
    public void resetPwd(Long id, String password) {
        SysUser user = new SysUser();
        user.setId(id);
        user.setPassword(SecurityUtils.encryptPassword(password));
        userMapper.updateById(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String importUsers(List<SysUser> users) {
        int success = 0;
        int fail = 0;
        StringBuilder failMsg = new StringBuilder();
        for (SysUser user : users) {
            try {
                // 校验用户名重复
                SysUser existing = userMapper.selectByUsername(user.getUsername());
                if (existing != null) {
                    fail++;
                    failMsg.append("用户名'").append(user.getUsername()).append("'已存在; ");
                    continue;
                }
                // 校验部门是否存在
                if (user.getDeptId() != null) {
                    if (deptMapper.selectById(user.getDeptId()) == null) {
                        fail++;
                        failMsg.append("用户名'").append(user.getUsername()).append("': 部门不存在; ");
                        continue;
                    }
                }
                user.setPassword(SecurityUtils.encryptPassword(user.getPassword()));
                userMapper.insert(user);
                success++;
            } catch (Exception e) {
                fail++;
                failMsg.append("用户名'").append(user.getUsername()).append("': ").append(e.getMessage()).append("; ");
            }
        }
        return "导入完成：成功" + success + "条，失败" + fail + "条" +
               (fail > 0 ? "。失败原因：" + failMsg : "");
    }

    @Override
    public List<SysUser> exportUsers(SysUser query) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(query.getUsername()), SysUser::getUsername, query.getUsername())
               .like(StringUtils.hasText(query.getNickname()), SysUser::getNickname, query.getNickname())
               .eq(query.getStatus() != null, SysUser::getStatus, query.getStatus())
               .eq(query.getDeptId() != null, SysUser::getDeptId, query.getDeptId())
               .orderByAsc(SysUser::getId);
        return userMapper.selectList(wrapper);
    }

    @Override
    public String uploadAvatar(Long userId, MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException("上传文件不能为空");
        }

        // 校验文件类型
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            throw new BusinessException("文件名不能为空");
        }
        String extension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1).toLowerCase();
        Set<String> allowedTypes = Set.of("jpg", "jpeg", "png", "gif", "bmp", "webp");
        if (!allowedTypes.contains(extension)) {
            throw new BusinessException("不支持的图片格式，仅支持: " + String.join(", ", allowedTypes));
        }

        // 校验文件大小（最大5MB）
        if (file.getSize() > 5 * 1024 * 1024) {
            throw new BusinessException("图片大小不能超过5MB");
        }

        // 上传到 MinIO
        String objectName = storageService.upload(file, "avatars");
        String avatarUrl = storageService.getFileUrl(objectName);

        // 更新用户头像URL
        SysUser user = new SysUser();
        user.setId(userId);
        user.setAvatar(avatarUrl);
        userMapper.updateById(user);

        return avatarUrl;
    }

    @Override
    public void changePassword(Long userId, String oldPassword, String newPassword) {
        SysUser user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        // 验证旧密码
        if (!SecurityUtils.matchesPassword(oldPassword, user.getPassword())) {
            throw new BusinessException("旧密码错误");
        }

        // 校验新密码不能与旧密码相同
        if (SecurityUtils.matchesPassword(newPassword, user.getPassword())) {
            throw new BusinessException("新密码不能与旧密码相同");
        }

        // 更新密码
        SysUser updateUser = new SysUser();
        updateUser.setId(userId);
        updateUser.setPassword(SecurityUtils.encryptPassword(newPassword));
        userMapper.updateById(updateUser);
    }

    @Override
    public SysUser getProfile(Long userId) {
        SysUser user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        // 查询用户角色
        List<SysRole> roles = roleMapper.selectRolesByUserId(userId);
        user.setRoles(roles);
        user.setRoleIds(roles.stream().map(SysRole::getId).toList());
        return user;
    }

    @Override
    public void updateProfile(SysUser user) {
        // 获取当前登录用户ID
        Long currentUserId = SecurityUtils.getUserId();
        if (currentUserId == null) {
            throw new BusinessException("用户未登录");
        }
        // 只能修改自己的信息
        if (!currentUserId.equals(user.getId())) {
            throw new BusinessException("只能修改自己的个人信息");
        }

        // 只允许修改部分字段
        SysUser updateUser = new SysUser();
        updateUser.setId(currentUserId);
        updateUser.setNickname(user.getNickname());
        updateUser.setEmail(user.getEmail());
        updateUser.setPhone(user.getPhone());
        updateUser.setSex(user.getSex());
        userMapper.updateById(updateUser);
    }
}
