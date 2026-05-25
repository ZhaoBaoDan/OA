package com.smartauto.oa.system.service;

import com.smartauto.oa.common.PageResult;
import com.smartauto.oa.system.entity.SysOperLog;

import java.util.List;

/**
 * 操作日志服务接口
 */
public interface ISysOperLogService {

    /**
     * 分页查询操作日志
     */
    PageResult<SysOperLog> page(SysOperLog query, int pageNum, int pageSize);

    /**
     * 新增操作日志
     */
    void insert(SysOperLog operLog);

    /**
     * 删除操作日志
     */
    void delete(List<Long> ids);

    /**
     * 清空操作日志
     */
    void clean();
}
