package com.smartauto.oa.meeting.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.smartauto.oa.common.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * 会议室预约实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("biz_meeting_booking")
@Schema(description = "会议室预约实体")
public class BizMeetingBooking extends BaseEntity {

    @NotNull(message = "会议室不能为空")
    @Schema(description = "会议室ID")
    private Long roomId;

    @NotNull(message = "预约日期不能为空")
    @Schema(description = "预约日期")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate bookingDate;

    @NotBlank(message = "开始时间不能为空")
    @Schema(description = "开始时间（HH:mm）")
    private String startTime;

    @NotBlank(message = "结束时间不能为空")
    @Schema(description = "结束时间（HH:mm）")
    private String endTime;

    @NotBlank(message = "会议主题不能为空")
    @Schema(description = "会议主题")
    private String title;

    @Schema(description = "组织者ID")
    private Long organizerId;

    @Schema(description = "组织者名称")
    private String organizer;

    @Schema(description = "参会人")
    private String attendees;

    @Schema(description = "备注")
    private String remark;
}
