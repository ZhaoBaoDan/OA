package com.smartauto.oa.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.smartauto.oa.common.BusinessException;
import com.smartauto.oa.common.PageResult;
import com.smartauto.oa.system.entity.SysDictType;
import com.smartauto.oa.system.mapper.SysDictTypeMapper;
import com.smartauto.oa.system.service.ISysDictTypeService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 字典类型服务实现
 */
@Service
public class SysDictTypeServiceImpl implements ISysDictTypeService {

    private final SysDictTypeMapper dictTypeMapper;

    public SysDictTypeServiceImpl(SysDictTypeMapper dictTypeMapper) {
        this.dictTypeMapper = dictTypeMapper;
    }

    @Override
    public PageResult<SysDictType> page(SysDictType query, int pageNum, int pageSize) {
        Page<SysDictType> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<SysDictType> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(query.getDictName()), SysDictType::getDictName, query.getDictName())
               .like(StringUtils.hasText(query.getDictType()), SysDictType::getDictType, query.getDictType())
               .eq(query.getStatus() != null, SysDictType::getStatus, query.getStatus())
               .orderByAsc(SysDictType::getId);
        Page<SysDictType> result = dictTypeMapper.selectPage(page, wrapper);
        return new PageResult<>(result.getTotal(), result.getRecords(), pageNum, pageSize);
    }

    @Override
    public void create(SysDictType dictType) {
        // 校验字典类型唯一
        LambdaQueryWrapper<SysDictType> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysDictType::getDictType, dictType.getDictType());
        Long count = dictTypeMapper.selectCount(wrapper);
        if (count > 0) {
            throw new BusinessException("字典类型'" + dictType.getDictType() + "'已存在");
        }
        dictTypeMapper.insert(dictType);
    }

    @Override
    public void update(SysDictType dictType) {
        SysDictType existing = dictTypeMapper.selectById(dictType.getId());
        if (existing == null) {
            throw new BusinessException("字典类型不存在");
        }
        // 校验字典类型唯一（排除自身）
        LambdaQueryWrapper<SysDictType> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysDictType::getDictType, dictType.getDictType()).ne(SysDictType::getId, dictType.getId());
        Long count = dictTypeMapper.selectCount(wrapper);
        if (count > 0) {
            throw new BusinessException("字典类型'" + dictType.getDictType() + "'已存在");
        }
        dictTypeMapper.updateById(dictType);
    }

    @Override
    public void delete(List<Long> ids) {
        dictTypeMapper.deleteBatchIds(ids);
    }
}
