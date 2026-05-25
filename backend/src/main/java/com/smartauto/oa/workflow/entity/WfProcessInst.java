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
 * 流程实例实体
 */
@Data
@TableName("wf_process_inst")
@Schema(description = "流程实例实体")
public class WfProcessInst implements Serializable {

    @TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "流程实例ID")
    private Long id;

    @Schema(description = "流程定义ID")
    private Long processDefId;

    @Schema(description = "流程标识")
    private String processKey;

    @Schema(description = "流程名称")
    private String processName;

    @Schema(description = "业务标识")
    private String businessKey;

    @Schema(description = "发起人ID")
    private Long startUserId;

    @Schema(description = "发起人名称")
    private String startUserName;

    @Schema(description = "状态（running/completed/terminated/suspended）")
    private String status;

    @Schema(description = "开始时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;

    @Schema(description = "结束时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;

    @Schema(description = "耗时(ms)")
    private Long duration;

    @Schema(description = "流程变量JSON")
    private String variables;
}
