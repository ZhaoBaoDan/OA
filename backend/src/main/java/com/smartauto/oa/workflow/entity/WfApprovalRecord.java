package com.smartauto.oa.workflow.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 流程审批记录实体
 */
@Data
@TableName("wf_approval_record")
@Schema(description = "流程审批记录实体")
public class WfApprovalRecord implements Serializable {

    @TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "记录ID")
    private Long id;

    @Schema(description = "流程实例ID")
    private Long processInstId;

    @Schema(description = "节点ID")
    private String nodeId;

    @Schema(description = "节点名称")
    private String nodeName;

    @Schema(description = "节点类型")
    private String nodeType;

    @Schema(description = "办理人ID")
    private Long assigneeId;

    @Schema(description = "办理人名称")
    private String assigneeName;

    @Schema(description = "操作（agree/reject/delegate/submit）")
    private String action;

    @Schema(description = "审批意见")
    private String comment;

    @Schema(description = "状态（pending/approved/rejected）")
    private String status;

    @Schema(description = "开始时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;

    @Schema(description = "结束时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;

    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}
