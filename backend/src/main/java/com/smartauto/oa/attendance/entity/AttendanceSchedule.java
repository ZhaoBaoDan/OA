package com.smartauto.oa.attendance.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * 考勤班次实体
 */
@Data
@TableName("attendance_schedule")
@Schema(description = "考勤班次实体")
public class AttendanceSchedule implements Serializable {

    @TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "班次类型（1固定 2弹性 3轮班）")
    private Integer scheduleType;

    @Schema(description = "上班时间")
    @JsonFormat(pattern = "HH:mm")
    private LocalTime workStartTime;

    @Schema(description = "下班时间")
    @JsonFormat(pattern = "HH:mm")
    private LocalTime workEndTime;

    @Schema(description = "休息开始时间")
    @JsonFormat(pattern = "HH:mm")
    private LocalTime restStartTime;

    @Schema(description = "休息结束时间")
    @JsonFormat(pattern = "HH:mm")
    private LocalTime restEndTime;

    @Schema(description = "生效日期")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate effectiveDate;

    @Schema(description = "状态（1正常 0停用）")
    private Integer status;

    @Schema(description = "删除标志")
    private Integer delFlag;

    @Schema(description = "创建者")
    private String createBy;

    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @Schema(description = "更新者")
    private String updateBy;

    @Schema(description = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}
