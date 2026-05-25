package com.smartauto.oa.meeting.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.smartauto.oa.common.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 会议室实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("biz_meeting_room")
@Schema(description = "会议室实体")
public class BizMeetingRoom extends BaseEntity {

    @NotBlank(message = "会议室名称不能为空")
    @Schema(description = "会议室名称")
    private String name;

    @Schema(description = "容纳人数")
    private Integer capacity;

    @Schema(description = "设备（逗号分隔）")
    private String equipment;

    @Schema(description = "位置")
    private String location;

    @Schema(description = "状态（1可用 0维护中）")
    private Integer status;
}
