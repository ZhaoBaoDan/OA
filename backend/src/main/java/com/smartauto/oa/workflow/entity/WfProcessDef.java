package com.smartauto.oa.workflow.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.smartauto.oa.common.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 流程定义实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("wf_process_def")
@Schema(description = "流程定义实体")
public class WfProcessDef extends BaseEntity {

    @NotBlank(message = "流程标识不能为空")
    @Schema(description = "流程标识")
    private String processKey;

    @NotBlank(message = "流程名称不能为空")
    @Schema(description = "流程名称")
    private String processName;

    @Schema(description = "版本号")
    private Integer version;

    @Schema(description = "流程分类")
    private String category;

    @Schema(description = "BPMN XML")
    private String bpmnXml;

    @Schema(description = "描述")
    private String description;

    @Schema(description = "状态（1已发布 0未发布 2已挂起）")
    private Integer status;
}
