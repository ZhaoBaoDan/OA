package com.smartauto.oa.system.service;

import com.smartauto.oa.common.PageResult;
import com.smartauto.oa.system.entity.SysPost;

import java.util.List;

/**
 * 岗位管理服务接口
 */
public interface ISysPostService {

    /**
     * 分页查询岗位列表
     */
    PageResult<SysPost> page(SysPost query, int pageNum, int pageSize);

    /**
     * 根据ID查询岗位
     */
    SysPost getById(Long id);

    /**
     * 新增岗位
     */
    void create(SysPost post);

    /**
     * 修改岗位
     */
    void update(SysPost post);

    /**
     * 删除岗位
     */
    void delete(List<Long> ids);
}
