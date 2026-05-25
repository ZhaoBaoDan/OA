package com.smartauto.oa.task.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.smartauto.oa.common.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * 任务实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("biz_task")
@Schema(description = "任务实体")
public class BizTask extends BaseEntity {

    @NotBlank(message = "任务标题不能为空")
    @Schema(description = "任务标题")
    private String title;

    @Schema(description = "任务描述")
    private String description;

    @Schema(description = "优先级（low/medium/high/urgent）")
    private String priority;

    @Schema(description = "状态（todo/progress/done/archive）")
    private String status;

    @Schema(description = "负责人ID")
    private Long assigneeId;

    @Schema(description = "负责人名称")
    private String assignee;

    @Schema(description = "截止日期")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dueDate;

    @Schema(description = "标签（逗号分隔）")
    private String tags;

    @Schema(description = "排序")
    private Integer sort;
}
