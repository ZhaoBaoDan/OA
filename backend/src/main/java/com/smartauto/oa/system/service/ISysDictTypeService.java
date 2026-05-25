package com.smartauto.oa.system.service;

import com.smartauto.oa.common.PageResult;
import com.smartauto.oa.system.entity.SysDictType;

import java.util.List;

/**
 * 字典类型服务接口
 */
public interface ISysDictTypeService {

    /**
     * 分页查询字典类型列表
     */
    PageResult<SysDictType> page(SysDictType query, int pageNum, int pageSize);

    /**
     * 查询所有字典类型
     */
    List<SysDictType> listAll();

    /**
     * 根据ID查询字典类型
     */
    SysDictType getById(Long id);

    /**
     * 新增字典类型
     */
    void create(SysDictType dictType);

    /**
     * 修改字典类型
     */
    void update(SysDictType dictType);

    /**
     * 删除字典类型
     */
    void delete(List<Long> ids);

    /**
     * 刷新全部字典缓存
     */
    void refreshCache();

    /**
     * 清除指定字典缓存
     */
    void clearCache(String dictType);
}
