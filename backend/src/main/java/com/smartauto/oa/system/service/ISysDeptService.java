package com.smartauto.oa.system.service;

import com.smartauto.oa.system.entity.SysDept;

import java.util.List;

/**
 * 部门管理服务接口
 */
public interface ISysDeptService {

    /**
     * 查询部门列表
     */
    List<SysDept> list(SysDept query);

    /**
     * 根据ID查询部门
     */
    SysDept getById(Long id);

    /**
     * 新增部门
     */
    void create(SysDept dept);

    /**
     * 修改部门
     */
    void update(SysDept dept);

    /**
     * 删除部门
     */
    void delete(Long id);

    /**
     * 查询部门树
     */
    List<SysDept> selectDeptTree();

    /**
     * 移动部门（修改父级）
     */
    void move(Long deptId, Long newParentId);
}
