package com.smartauto.oa.system.service;

import com.smartauto.oa.common.PageResult;
import com.smartauto.oa.system.entity.SysUser;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 用户管理服务接口
 */
public interface ISysUserService {

    /**
     * 分页查询用户列表
     */
    PageResult<SysUser> page(SysUser query, int pageNum, int pageSize);

    /**
     * 根据ID查询用户
     */
    SysUser getById(Long id);

    /**
     * 新增用户
     */
    void create(SysUser user);

    /**
     * 修改用户
     */
    void update(SysUser user);

    /**
     * 删除用户
     */
    void delete(List<Long> ids);

    /**
     * 修改用户状态
     */
    void updateStatus(Long id, Integer status);

    /**
     * 重置密码
     */
    void resetPwd(Long id, String password);

    /**
     * 导入用户（返回成功/失败条数）
     */
    String importUsers(List<SysUser> users);

    /**
     * 导出用户列表
     */
    List<SysUser> exportUsers(SysUser query);

    /**
     * 上传头像
     *
     * @param userId 用户ID
     * @param file   头像文件
     * @return 头像访问URL
     */
    String uploadAvatar(Long userId, MultipartFile file);

    /**
     * 修改密码
     *
     * @param userId      用户ID
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     */
    void changePassword(Long userId, String oldPassword, String newPassword);

    /**
     * 获取个人信息
     *
     * @param userId 用户ID
     * @return 用户信息
     */
    SysUser getProfile(Long userId);

    /**
     * 修改个人信息（只能修改自己的）
     *
     * @param user 用户信息
     */
    void updateProfile(SysUser user);
}
