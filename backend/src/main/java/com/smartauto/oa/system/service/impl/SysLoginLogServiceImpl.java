package com.smartauto.oa.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.smartauto.oa.common.PageResult;
import com.smartauto.oa.system.entity.SysLoginLog;
import com.smartauto.oa.system.mapper.SysLoginLogMapper;
import com.smartauto.oa.system.service.ISysLoginLogService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * 登录日志服务实现
 */
@Service
public class SysLoginLogServiceImpl implements ISysLoginLogService {

    private final SysLoginLogMapper loginLogMapper;

    public SysLoginLogServiceImpl(SysLoginLogMapper loginLogMapper) {
        this.loginLogMapper = loginLogMapper;
    }

    @Override
    public void recordLoginLog(SysLoginLog loginLog) {
        loginLogMapper.insert(loginLog);
    }

    @Override
    public PageResult<SysLoginLog> page(SysLoginLog query, int pageNum, int pageSize) {
        Page<SysLoginLog> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<SysLoginLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(query.getUsername()), SysLoginLog::getUsername, query.getUsername())
               .eq(query.getStatus() != null, SysLoginLog::getStatus, query.getStatus())
               .orderByDesc(SysLoginLog::getLoginTime);
        Page<SysLoginLog> result = loginLogMapper.selectPage(page, wrapper);
        return new PageResult<>(result.getTotal(), result.getRecords(), pageNum, pageSize);
    }
}
