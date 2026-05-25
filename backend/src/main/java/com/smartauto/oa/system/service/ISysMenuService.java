package com.smartauto.oa.system.service;

import com.smartauto.oa.system.entity.SysMenu;

import java.util.List;

/**
 * 菜单管理服务接口
 */
public interface ISysMenuService {

    /**
     * 查询菜单列表
     */
    List<SysMenu> list(SysMenu query);

    /**
     * 根据ID查询菜单
     */
    SysMenu getById(Long id);

    /**
     * 新增菜单
     */
    void create(SysMenu menu);

    /**
     * 修改菜单
     */
    void update(SysMenu menu);

    /**
     * 删除菜单
     */
    void delete(Long id);

    /**
     * 查询菜单树
     */
    List<SysMenu> selectMenuTree();

    /**
     * 根据角色ID查询菜单树（已选中的菜单ID集合）
     */
    List<Long> selectMenuTreeByRoleId(Long roleId);
}
