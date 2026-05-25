package com.smartauto.oa.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.smartauto.oa.common.BusinessException;
import com.smartauto.oa.system.entity.SysDept;
import com.smartauto.oa.system.entity.SysUser;
import com.smartauto.oa.system.mapper.SysDeptMapper;
import com.smartauto.oa.system.mapper.SysUserMapper;
import com.smartauto.oa.system.service.ISysDeptService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 部门管理服务实现（深度增强版）
 */
@Service
public class SysDeptServiceImpl implements ISysDeptService {

    private final SysDeptMapper deptMapper;
    private final SysUserMapper userMapper;

    public SysDeptServiceImpl(SysDeptMapper deptMapper, SysUserMapper userMapper) {
        this.deptMapper = deptMapper;
        this.userMapper = userMapper;
    }

    @Override
    public List<SysDept> list(SysDept query) {
        LambdaQueryWrapper<SysDept> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(query.getDeptName() != null && !query.getDeptName().isEmpty(),
                     SysDept::getDeptName, query.getDeptName())
               .eq(query.getStatus() != null, SysDept::getStatus, query.getStatus())
               .orderByAsc(SysDept::getSort);
        List<SysDept> depts = deptMapper.selectList(wrapper);
        // 填充每个部门的用户数
        depts.forEach(dept -> {
            Long count = userMapper.selectCount(
                new LambdaQueryWrapper<SysUser>().eq(SysUser::getDeptId, dept.getId())
                                                  .eq(SysUser::getDelFlag, 0));
            dept.setUserCount(count.intValue());
        });
        return depts;
    }

    @Override
    public SysDept getById(Long id) {
        SysDept dept = deptMapper.selectById(id);
        if (dept == null) {
            throw new BusinessException("部门不存在");
        }
        // 填充用户数和负责人信息
        Long count = userMapper.selectCount(
            new LambdaQueryWrapper<SysUser>().eq(SysUser::getDeptId, id).eq(SysUser::getDelFlag, 0));
        dept.setUserCount(count.intValue());
        return dept;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void create(SysDept dept) {
        // 校验同级部门名称唯一
        LambdaQueryWrapper<SysDept> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysDept::getParentId, dept.getParentId())
               .eq(SysDept::getDeptName, dept.getDeptName())
               .eq(SysDept::getDelFlag, 0);
        if (deptMapper.selectCount(wrapper) > 0) {
            throw new BusinessException("同级部门'" + dept.getDeptName() + "'已存在");
        }
        // 设置祖级列表
        if (dept.getParentId() != null && dept.getParentId() != 0) {
            SysDept parent = deptMapper.selectById(dept.getParentId());
            if (parent != null) {
                dept.setAncestors(parent.getAncestors() + "," + parent.getId());
            }
        } else {
            dept.setAncestors("0");
        }
        deptMapper.insert(dept);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(SysDept dept) {
        SysDept existing = deptMapper.selectById(dept.getId());
        if (existing == null) {
            throw new BusinessException("部门不存在");
        }
        // 不能将自己设为自己的子部门
        if (dept.getParentId() != null && dept.getParentId().equals(dept.getId())) {
            throw new BusinessException("上级部门不能是自己");
        }
        // 校验同级部门名称唯一（排除自身）
        LambdaQueryWrapper<SysDept> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysDept::getParentId, dept.getParentId())
               .eq(SysDept::getDeptName, dept.getDeptName())
               .ne(SysDept::getId, dept.getId())
               .eq(SysDept::getDelFlag, 0);
        if (deptMapper.selectCount(wrapper) > 0) {
            throw new BusinessException("同级部门'" + dept.getDeptName() + "'已存在");
        }
        // 更新祖级列表
        if (dept.getParentId() != null && dept.getParentId() != 0) {
            SysDept parent = deptMapper.selectById(dept.getParentId());
            if (parent != null) {
                dept.setAncestors(parent.getAncestors() + "," + parent.getId());
            }
        } else {
            dept.setAncestors("0");
        }
        deptMapper.updateById(dept);
        // 同步更新子部门的祖级列表
        updateChildrenAncestors(dept);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        // 检查是否有子部门
        Long childCount = deptMapper.selectCount(
            new LambdaQueryWrapper<SysDept>().eq(SysDept::getParentId, id).eq(SysDept::getDelFlag, 0));
        if (childCount > 0) {
            throw new BusinessException("存在子部门，不允许删除");
        }
        // 检查是否有用户关联
        Long userCount = userMapper.selectCount(
            new LambdaQueryWrapper<SysUser>().eq(SysUser::getDeptId, id).eq(SysUser::getDelFlag, 0));
        if (userCount > 0) {
            throw new BusinessException("部门下存在" + userCount + "个用户，不允许删除");
        }
        deptMapper.deleteById(id);
    }

    @Override
    public List<SysDept> selectDeptTree() {
        LambdaQueryWrapper<SysDept> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysDept::getStatus, 0).eq(SysDept::getDelFlag, 0).orderByAsc(SysDept::getSort);
        List<SysDept> depts = deptMapper.selectList(wrapper);
        return buildDeptTree(depts, 0L);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void move(Long deptId, Long newParentId) {
        SysDept dept = deptMapper.selectById(deptId);
        if (dept == null) {
            throw new BusinessException("部门不存在");
        }
        if (deptId.equals(newParentId)) {
            throw new BusinessException("不能移动到自身");
        }
        // 检查新父部门是否是自己的子部门（防止循环引用）
        String newAncestors = "0";
        if (newParentId != 0) {
            SysDept newParent = deptMapper.selectById(newParentId);
            if (newParent != null) {
                newAncestors = newParent.getAncestors() + "," + newParentId;
                if (newAncestors.contains(String.valueOf(deptId))) {
                    throw new BusinessException("不能移动到自己的子部门下");
                }
            }
        }
        dept.setParentId(newParentId);
        dept.setAncestors(newAncestors);
        deptMapper.updateById(dept);
        updateChildrenAncestors(dept);
    }

    /**
     * 更新子部门的祖级列表
     */
    private void updateChildrenAncestors(SysDept dept) {
        List<SysDept> children = deptMapper.selectList(
            new LambdaQueryWrapper<SysDept>().eq(SysDept::getParentId, dept.getId()).eq(SysDept::getDelFlag, 0));
        for (SysDept child : children) {
            child.setAncestors(dept.getAncestors() + "," + dept.getId());
            deptMapper.updateById(child);
            updateChildrenAncestors(child);
        }
    }

    /**
     * 构建部门树（递归）
     */
    private List<SysDept> buildDeptTree(List<SysDept> depts, Long parentId) {
        return depts.stream()
                .filter(dept -> parentId.equals(dept.getParentId()))
                .peek(dept -> dept.setChildren(buildDeptTree(depts, dept.getId())))
                .collect(Collectors.toList());
    }
}
