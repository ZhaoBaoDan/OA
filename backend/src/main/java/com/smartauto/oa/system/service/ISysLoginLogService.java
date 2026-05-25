package com.smartauto.oa.system.service;

import com.smartauto.oa.common.PageResult;
import com.smartauto.oa.system.entity.SysLoginLog;

/**
 * 登录日志服务接口
 */
public interface ISysLoginLogService {

    /**
     * 记录登录日志
     */
    void recordLoginLog(SysLoginLog loginLog);

    /**
     * 分页查询登录日志
     */
    PageResult<SysLoginLog> page(SysLoginLog query, int pageNum, int pageSize);
}
