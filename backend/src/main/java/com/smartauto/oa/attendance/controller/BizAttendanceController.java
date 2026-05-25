package com.smartauto.oa.attendance.controller;

import com.smartauto.oa.attendance.entity.BizAttendance;
import com.smartauto.oa.attendance.service.IBizAttendanceService;
import com.smartauto.oa.common.Result;
import com.smartauto.oa.common.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 考勤管理
 */
@Tag(name = "考勤管理")
@RestController
@RequestMapping("/attendance")
public class BizAttendanceController {

    private final IBizAttendanceService attendanceService;

    public BizAttendanceController(IBizAttendanceService attendanceService) {
        this.attendanceService = attendanceService;
    }

    @Operation(summary = "获取今日打卡记录")
    @GetMapping("/today")
    public Result<BizAttendance> getToday() {
        Long userId = SecurityUtils.getUserId();
        return Result.success(attendanceService.getTodayRecord(userId));
    }

    @Operation(summary = "上班打卡")
    @PostMapping("/checkin")
    public Result<Void> checkIn() {
        Long userId = SecurityUtils.getUserId();
        String userName = SecurityUtils.getUsername();
        attendanceService.checkIn(userId, userName);
        return Result.success();
    }

    @Operation(summary = "下班打卡")
    @PostMapping("/checkout")
    public Result<Void> checkOut() {
        Long userId = SecurityUtils.getUserId();
        String userName = SecurityUtils.getUsername();
        attendanceService.checkOut(userId, userName);
        return Result.success();
    }

    @Operation(summary = "获取月度考勤统计")
    @GetMapping("/stats")
    public Result<Map<String, Object>> getMonthlyStats(
            @Parameter(description = "年份") @RequestParam int year,
            @Parameter(description = "月份") @RequestParam int month) {
        Long userId = SecurityUtils.getUserId();
        return Result.success(attendanceService.getMonthlyStats(userId, year, month));
    }
}
