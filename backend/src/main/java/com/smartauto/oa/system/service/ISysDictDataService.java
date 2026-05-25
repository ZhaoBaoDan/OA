package com.smartauto.oa.system.service;

import com.smartauto.oa.system.entity.SysDictData;

import java.util.List;

/**
 * 字典数据服务接口
 */
public interface ISysDictDataService {

    /**
     * 根据字典类型查询字典数据列表
     */
    List<SysDictData> listByDictType(String dictType);

    /**
     * 查询字典数据列表
     */
    List<SysDictData> list(SysDictData query);

    /**
     * 新增字典数据
     */
    void create(SysDictData dictData);

    /**
     * 修改字典数据
     */
    void update(SysDictData dictData);

    /**
     * 删除字典数据
     */
    void delete(List<Long> ids);
}
