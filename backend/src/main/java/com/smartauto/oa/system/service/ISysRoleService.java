package com.smartauto.oa.system.service;

import com.smartauto.oa.common.PageResult;
import com.smartauto.oa.system.entity.SysRole;

import java.util.List;
import java.util.Set;

/**
 * 角色管理服务接口
 */
public interface ISysRoleService {

    /**
     * 分页查询角色列表
     */
    PageResult<SysRole> page(SysRole query, int pageNum, int pageSize);

    /**
     * 根据ID查询角色
     */
    SysRole getById(Long id);

    /**
     * 新增角色
     */
    void create(SysRole role);

    /**
     * 修改角色
     */
    void update(SysRole role);

    /**
     * 删除角色
     */
    void delete(List<Long> ids);

    /**
     * 修改角色状态
     */
    void updateStatus(Long id, Integer status);

    /**
     * 修改数据权限
     */
    void updateDataScope(Long id, String dataScope, List<Long> deptIds);

    /**
     * 查询角色菜单树（已选中的菜单ID集合）
     */
    Set<Long> selectRoleMenuTree(Long roleId);
}
