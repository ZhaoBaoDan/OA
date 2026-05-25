package com.smartauto.oa.report.controller;

import com.smartauto.oa.attendance.service.IBizAttendanceService;
import com.smartauto.oa.common.Result;
import com.smartauto.oa.common.SecurityUtils;
import com.smartauto.oa.notification.service.IBizNotificationService;
import com.smartauto.oa.report.service.impl.ReportServiceImpl;
import com.smartauto.oa.task.service.IBizTaskService;
import com.smartauto.oa.workflow.mapper.WfProcessInstMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.smartauto.oa.workflow.entity.WfProcessInst;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

/**
 * 报表中心
 */
@Tag(name = "报表中心")
@RestController
@RequestMapping("/report")
public class ReportController {

    private final IBizAttendanceService attendanceService;
    private final IBizTaskService taskService;
    private final IBizNotificationService notificationService;
    private final WfProcessInstMapper processInstMapper;
    private final ReportServiceImpl reportService;

    public ReportController(IBizAttendanceService attendanceService,
                            IBizTaskService taskService,
                            IBizNotificationService notificationService,
                            WfProcessInstMapper processInstMapper,
                            ReportServiceImpl reportService) {
        this.attendanceService = attendanceService;
        this.taskService = taskService;
        this.notificationService = notificationService;
        this.processInstMapper = processInstMapper;
        this.reportService = reportService;
    }

    @Operation(summary = "仪表盘统计数据")
    @GetMapping("/dashboard")
    public Result<Map<String, Object>> dashboard() {
        Long userId = SecurityUtils.getUserId();
        LocalDate today = LocalDate.now();
        Map<String, Object> data = new HashMap<>();

        // 今日考勤状态
        data.put("todayAttendance", attendanceService.getTodayRecord(userId));

        // 月度考勤统计
        data.put("monthlyAttendance", attendanceService.getMonthlyStats(
                userId, today.getYear(), today.getMonthValue()));

        // 任务看板统计（按数量）
        Map<String, Object> taskStats = new HashMap<>();
        taskStats.put("board", taskService.board());
        taskStats.put("report", reportService.getTaskReport(userId));
        data.put("tasks", taskStats);

        // 未读通知数
        data.put("unreadNotifications", notificationService.unreadCount(userId));

        // 进行中流程数
        long runningProcesses = processInstMapper.selectCount(
                new LambdaQueryWrapper<WfProcessInst>().eq(WfProcessInst::getStatus, "running"));
        data.put("runningProcesses", runningProcesses);

        return Result.success(data);
    }

    @Operation(summary = "考勤趋势（12个月）")
    @GetMapping("/attendance/trend")
    public Result<Map<String, Object>> attendanceTrend(
            @Parameter(description = "年份") @RequestParam(defaultValue = "#{T(java.time.LocalDate).now().getYear()}") int year) {
        return Result.success(reportService.getAttendanceYearTrend(year));
    }

    @Operation(summary = "任务统计")
    @GetMapping("/task/stats")
    public Result<Map<String, Object>> taskStats() {
        Long userId = SecurityUtils.getUserId();
        return Result.success(reportService.getTaskReport(userId));
    }

    @Operation(summary = "部门人数分布")
    @GetMapping("/dept/distribution")
    public Result<Map<String, Object>> deptDistribution() {
        return Result.success(reportService.getDeptDistribution());
    }

    @Operation(summary = "每日待办趋势（最近7天）")
    @GetMapping("/todo/trend")
    public Result<Map<String, Object>> todoTrend() {
        return Result.success(reportService.getTodoTrend());
    }
}
