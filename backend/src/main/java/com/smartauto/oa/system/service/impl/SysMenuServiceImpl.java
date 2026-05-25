package com.smartauto.oa.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.smartauto.oa.common.BusinessException;
import com.smartauto.oa.common.Constants;
import com.smartauto.oa.system.entity.SysMenu;
import com.smartauto.oa.system.mapper.SysMenuMapper;
import com.smartauto.oa.system.mapper.SysRoleMapper;
import com.smartauto.oa.system.service.ISysMenuService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 菜单管理服务实现
 */
@Service
public class SysMenuServiceImpl implements ISysMenuService {

    private final SysMenuMapper menuMapper;
    private final SysRoleMapper roleMapper;

    public SysMenuServiceImpl(SysMenuMapper menuMapper, SysRoleMapper roleMapper) {
        this.menuMapper = menuMapper;
        this.roleMapper = roleMapper;
    }

    @Override
    public List<SysMenu> list(SysMenu query) {
        LambdaQueryWrapper<SysMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(query.getMenuName() != null, SysMenu::getMenuName, query.getMenuName())
               .eq(query.getStatus() != null, SysMenu::getStatus, query.getStatus())
               .orderByAsc(SysMenu::getSort);
        return menuMapper.selectList(wrapper);
    }

    @Override
    public SysMenu getById(Long id) {
        SysMenu menu = menuMapper.selectById(id);
        if (menu == null) {
            throw new BusinessException("菜单不存在");
        }
        return menu;
    }

    @Override
    public void create(SysMenu menu) {
        // 校验同级菜单名称唯一
        LambdaQueryWrapper<SysMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysMenu::getParentId, menu.getParentId())
               .eq(SysMenu::getMenuName, menu.getMenuName());
        Long count = menuMapper.selectCount(wrapper);
        if (count > 0) {
            throw new BusinessException("同级菜单'" + menu.getMenuName() + "'已存在");
        }
        menuMapper.insert(menu);
    }

    @Override
    public void update(SysMenu menu) {
        SysMenu existing = menuMapper.selectById(menu.getId());
        if (existing == null) {
            throw new BusinessException("菜单不存在");
        }
        // 不能将自己设为自己的子菜单
        if (menu.getId().equals(menu.getParentId())) {
            throw new BusinessException("上级菜单不能选择自己");
        }
        // 校验同级菜单名称唯一（排除自身）
        LambdaQueryWrapper<SysMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysMenu::getParentId, menu.getParentId())
               .eq(SysMenu::getMenuName, menu.getMenuName())
               .ne(SysMenu::getId, menu.getId());
        Long count = menuMapper.selectCount(wrapper);
        if (count > 0) {
            throw new BusinessException("同级菜单'" + menu.getMenuName() + "'已存在");
        }
        menuMapper.updateById(menu);
    }

    @Override
    public void delete(Long id) {
        // 检查是否有子菜单
        LambdaQueryWrapper<SysMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysMenu::getParentId, id);
        Long count = menuMapper.selectCount(wrapper);
        if (count > 0) {
            throw new BusinessException("存在子菜单，不允许删除。请先删除子菜单。");
        }
        // 检查是否有角色关联此菜单
        List<Long> roleIds = roleMapper.selectRoleIdsByMenuId(id);
        if (roleIds != null && !roleIds.isEmpty()) {
            throw new BusinessException("菜单已被角色引用，不允许删除。请先取消角色的菜单权限。");
        }
        menuMapper.deleteById(id);
    }

    @Override
    public List<SysMenu> selectMenuTree() {
        LambdaQueryWrapper<SysMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysMenu::getStatus, 1).orderByAsc(SysMenu::getSort);
        List<SysMenu> menus = menuMapper.selectList(wrapper);
        return buildMenuTree(menus, 0L);
    }

    @Override
    public List<Long> selectMenuTreeByRoleId(Long roleId) {
        return roleMapper.selectMenuIdsByRoleId(roleId);
    }

    /**
     * 构建菜单树（递归）
     */
    private List<SysMenu> buildMenuTree(List<SysMenu> menus, Long parentId) {
        List<SysMenu> tree = new ArrayList<>();
        for (SysMenu menu : menus) {
            if (parentId.equals(menu.getParentId())) {
                List<SysMenu> children = buildMenuTree(menus, menu.getId());
                menu.setChildren(children.isEmpty() ? null : children);
                tree.add(menu);
            }
        }
        return tree;
    }
}
