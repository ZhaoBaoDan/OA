package com.smartauto.oa.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.smartauto.oa.system.entity.SysDictData;
import com.smartauto.oa.system.mapper.SysDictDataMapper;
import com.smartauto.oa.system.service.ISysDictDataService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 字典数据服务实现
 */
@Service
public class SysDictDataServiceImpl implements ISysDictDataService {

    private final SysDictDataMapper dictDataMapper;

    public SysDictDataServiceImpl(SysDictDataMapper dictDataMapper) {
        this.dictDataMapper = dictDataMapper;
    }

    @Override
    public List<SysDictData> listByDictType(String dictType) {
        LambdaQueryWrapper<SysDictData> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysDictData::getDictType, dictType)
               .eq(SysDictData::getStatus, 1)
               .orderByAsc(SysDictData::getSort);
        return dictDataMapper.selectList(wrapper);
    }

    @Override
    public List<SysDictData> list(SysDictData query) {
        LambdaQueryWrapper<SysDictData> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(query.getDictType() != null, SysDictData::getDictType, query.getDictType())
               .like(query.getDictLabel() != null, SysDictData::getDictLabel, query.getDictLabel())
               .eq(query.getStatus() != null, SysDictData::getStatus, query.getStatus())
               .orderByAsc(SysDictData::getSort);
        return dictDataMapper.selectList(wrapper);
    }

    @Override
    public void create(SysDictData dictData) {
        dictDataMapper.insert(dictData);
    }

    @Override
    public void update(SysDictData dictData) {
        dictDataMapper.updateById(dictData);
    }

    @Override
    public void delete(List<Long> ids) {
        dictDataMapper.deleteBatchIds(ids);
    }
}
