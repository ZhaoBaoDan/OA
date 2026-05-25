package com.smartauto.oa.workflow.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.smartauto.oa.workflow.entity.WfApprovalRecord;
import org.apache.ibatis.annotations.Mapper;

/**
 * 流程审批记录 Mapper
 */
@Mapper
public interface WfApprovalRecordMapper extends BaseMapper<WfApprovalRecord> {
}
