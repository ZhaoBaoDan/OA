package com.smartauto.oa.task.service;

import com.smartauto.oa.common.PageResult;
import com.smartauto.oa.task.entity.BizTask;

import java.util.List;
import java.util.Map;

/**
 * 任务服务接口
 */
public interface IBizTaskService {

    PageResult<BizTask> page(BizTask query, int pageNum, int pageSize);

    BizTask getById(Long id);

    List<BizTask> listByStatus(String status);

    Map<String, List<BizTask>> board();

    void create(BizTask task);

    void update(BizTask task);

    void updateStatus(Long id, String status);

    void delete(List<Long> ids);
}
