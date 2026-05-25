package com.smartauto.oa.workflow.service;

import com.smartauto.oa.common.PageResult;
import com.smartauto.oa.workflow.entity.WfProcessDef;

import java.util.List;

/**
 * 流程定义服务接口
 */
public interface IWfProcessDefService {

    PageResult<WfProcessDef> page(WfProcessDef query, int pageNum, int pageSize);

    WfProcessDef getById(Long id);

    List<WfProcessDef> listAll();

    void create(WfProcessDef processDef);

    void update(WfProcessDef processDef);

    void delete(List<Long> ids);

    void deploy(Long id);

    void suspend(Long id);
}
