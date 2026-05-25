package com.smartauto.oa.workflow.service;

import com.smartauto.oa.common.PageResult;
import com.smartauto.oa.workflow.entity.WfApprovalRecord;
import com.smartauto.oa.workflow.entity.WfProcessInst;

import java.util.List;

/**
 * 流程实例服务接口
 */
public interface IWfProcessInstService {

    PageResult<WfProcessInst> page(WfProcessInst query, int pageNum, int pageSize);

    WfProcessInst getById(Long id);

    List<WfApprovalRecord> getApprovalRecords(Long processInstId);

    void terminate(Long id);

    void delete(List<Long> ids);
}
