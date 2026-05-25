package com.smartauto.oa.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.smartauto.oa.common.PageResult;
import com.smartauto.oa.system.entity.SysOperLog;
import com.smartauto.oa.system.mapper.SysOperLogMapper;
import com.smartauto.oa.system.service.ISysOperLogService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 操作日志服务实现
 */
@Service
public class SysOperLogServiceImpl implements ISysOperLogService {

    private final SysOperLogMapper operLogMapper;

    public SysOperLogServiceImpl(SysOperLogMapper operLogMapper) {
        this.operLogMapper = operLogMapper;
    }

    @Override
    public PageResult<SysOperLog> page(SysOperLog query, int pageNum, int pageSize) {
        Page<SysOperLog> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<SysOperLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(query.getTitle()), SysOperLog::getTitle, query.getTitle())
               .like(StringUtils.hasText(query.getOperName()), SysOperLog::getOperName, query.getOperName())
               .eq(query.getStatus() != null, SysOperLog::getStatus, query.getStatus())
               .orderByDesc(SysOperLog::getOperTime);
        Page<SysOperLog> result = operLogMapper.selectPage(page, wrapper);
        return new PageResult<>(result.getTotal(), result.getRecords(), pageNum, pageSize);
    }

    @Override
    @Async
    public void insert(SysOperLog operLog) {
        operLogMapper.insert(operLog);
    }

    @Override
    public void delete(List<Long> ids) {
        operLogMapper.deleteBatchIds(ids);
    }

    @Override
    public void clean() {
        operLogMapper.delete(new LambdaQueryWrapper<>());
    }
}
