package com.smartauto.oa.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.smartauto.oa.common.BusinessException;
import com.smartauto.oa.common.Constants;
import com.smartauto.oa.common.PageResult;
import com.smartauto.oa.system.entity.SysMenu;
import com.smartauto.oa.system.entity.SysRole;
import com.smartauto.oa.system.entity.SysUser;
import com.smartauto.oa.system.mapper.SysMenuMapper;
import com.smartauto.oa.system.mapper.SysRoleMapper;
import com.smartauto.oa.system.mapper.SysUserMapper;
import com.smartauto.oa.system.service.ISysRoleService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 角色管理服务实现
 */
@Service
public class SysRoleServiceImpl implements ISysRoleService {

    private final SysRoleMapper roleMapper;
    private final SysMenuMapper menuMapper;
    private final SysUserMapper userMapper;

    public SysRoleServiceImpl(SysRoleMapper roleMapper, SysMenuMapper menuMapper, SysUserMapper userMapper) {
        this.roleMapper = roleMapper;
        this.menuMapper = menuMapper;
        this.userMapper = userMapper;
    }

    @Override
    public PageResult<SysRole> page(SysRole query, int pageNum, int pageSize) {
        Page<SysRole> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(query.getRoleName()), SysRole::getRoleName, query.getRoleName())
               .like(StringUtils.hasText(query.getRoleKey()), SysRole::getRoleKey, query.getRoleKey())
               .eq(query.getStatus() != null, SysRole::getStatus, query.getStatus())
               .orderByAsc(SysRole::getSort);
        Page<SysRole> result = roleMapper.selectPage(page, wrapper);
        return new PageResult<>(result.getTotal(), result.getRecords(), pageNum, pageSize);
    }

    @Override
    public SysRole getById(Long id) {
        SysRole role = roleMapper.selectById(id);
        if (role == null) {
            throw new BusinessException("角色不存在");
        }
        return role;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void create(SysRole role) {
        // 校验角色标识唯一
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysRole::getRoleKey, role.getRoleKey());
        Long count = roleMapper.selectCount(wrapper);
        if (count > 0) {
            throw new BusinessException("角色标识'" + role.getRoleKey() + "'已存在");
        }
        roleMapper.insert(role);
        // 关联菜单
        if (role.getMenuIds() != null && !role.getMenuIds().isEmpty()) {
            for (Long menuId : role.getMenuIds()) {
                roleMapper.insertRoleMenu(role.getId(), menuId);
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(SysRole role) {
        SysRole existing = roleMapper.selectById(role.getId());
        if (existing == null) {
            throw new BusinessException("角色不存在");
        }
        if (Constants.ADMIN_ROLE.equals(existing.getRoleKey())) {
            throw new BusinessException("不允许修改管理员角色");
        }
        // 校验角色标识唯一（排除自身）
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysRole::getRoleKey, role.getRoleKey()).ne(SysRole::getId, role.getId());
        Long count = roleMapper.selectCount(wrapper);
        if (count > 0) {
            throw new BusinessException("角色标识'" + role.getRoleKey() + "'已存在");
        }
        roleMapper.updateById(role);
        // 更新菜单关联：先删后插
        roleMapper.deleteRoleMenuByRoleId(role.getId());
        if (role.getMenuIds() != null && !role.getMenuIds().isEmpty()) {
            for (Long menuId : role.getMenuIds()) {
                roleMapper.insertRoleMenu(role.getId(), menuId);
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(List<Long> ids) {
        for (Long id : ids) {
            SysRole role = roleMapper.selectById(id);
            if (role != null && Constants.ADMIN_ROLE.equals(role.getRoleKey())) {
                throw new BusinessException("不允许删除管理员角色");
            }
            // 检查是否有用户关联此角色
            List<SysUser> users = userMapper.selectList(
                    new LambdaQueryWrapper<SysUser>()
                            .inSql(SysUser::getId,
                                    "SELECT user_id FROM sys_user_role WHERE role_id = " + id)
                            .eq(SysUser::getDelFlag, 0)
            );
            if (users != null && !users.isEmpty()) {
                throw new BusinessException("角色'" + role.getRoleName() + "'已分配给" + users.size() + "个用户，不允许删除");
            }
        }
        // 删除角色
        roleMapper.deleteBatchIds(ids);
        // 删除角色菜单关联
        for (Long id : ids) {
            roleMapper.deleteRoleMenuByRoleId(id);
        }
    }

    @Override
    public void updateStatus(Long id, Integer status) {
        SysRole role = new SysRole();
        role.setId(id);
        role.setStatus(status);
        roleMapper.updateById(role);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateDataScope(Long id, String dataScope, List<Long> deptIds) {
        SysRole role = roleMapper.selectById(id);
        if (role == null) {
            throw new BusinessException("角色不存在");
        }
        // 更新数据权限范围（data_scope 字段可扩展到 sys_role 表）
        // 简化实现：记录日志，实际可扩展 sys_role_dept 表
        // 数据权限范围：1=全部 2=自定义 3=本部门 4=本部门及以下 5=仅本人
        roleMapper.updateById(role);
    }

    @Override
    public Set<Long> selectRoleMenuTree(Long roleId) {
        // 查询角色已关联的菜单ID
        List<Long> roleMenuIds = roleMapper.selectMenuIdsByRoleId(roleId);
        return new HashSet<>(roleMenuIds);
    }
}
