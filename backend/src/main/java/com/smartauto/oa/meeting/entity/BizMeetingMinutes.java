package com.smartauto.oa.meeting.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.smartauto.oa.common.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * 会议纪要实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("biz_meeting_minutes")
@Schema(description = "会议纪要实体")
public class BizMeetingMinutes extends BaseEntity {

    @Schema(description = "关联预约ID")
    private Long bookingId;

    @NotBlank(message = "会议主题不能为空")
    @Schema(description = "会议主题")
    private String title;

    @Schema(description = "会议室ID")
    private Long roomId;

    @Schema(description = "会议室名称")
    private String roomName;

    @Schema(description = "会议日期")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate meetingDate;

    @Schema(description = "开始时间（HH:mm）")
    private String startTime;

    @Schema(description = "结束时间（HH:mm）")
    private String endTime;

    @Schema(description = "组织者ID")
    private Long organizerId;

    @Schema(description = "组织者")
    private String organizer;

    @Schema(description = "参会人")
    private String attendees;

    @Schema(description = "纪要内容")
    private String content;

    @Schema(description = "待办事项JSON")
    private String actionItems;

    @Schema(description = "记录人ID")
    private Long recorderId;

    @Schema(description = "记录人")
    private String recorderName;
}
