package com.smartauto.oa.report.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.smartauto.oa.attendance.entity.BizAttendance;
import com.smartauto.oa.attendance.mapper.BizAttendanceMapper;
import com.smartauto.oa.notification.mapper.BizNotificationMapper;
import com.smartauto.oa.notification.entity.BizNotification;
import com.smartauto.oa.report.service.ReportService;
import com.smartauto.oa.system.entity.SysDept;
import com.smartauto.oa.system.entity.SysUser;
import com.smartauto.oa.system.mapper.SysDeptMapper;
import com.smartauto.oa.system.mapper.SysUserMapper;
import com.smartauto.oa.task.entity.BizTask;
import com.smartauto.oa.task.mapper.BizTaskMapper;
import com.smartauto.oa.workflow.entity.WfProcessInst;
import com.smartauto.oa.workflow.mapper.WfProcessInstMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 报表服务实现
 */
@Service
public class ReportServiceImpl implements ReportService {

    private final BizAttendanceMapper attendanceMapper;
    private final BizTaskMapper taskMapper;
    private final BizNotificationMapper notificationMapper;
    private final SysUserMapper userMapper;
    private final SysDeptMapper deptMapper;
    private final WfProcessInstMapper processInstMapper;

    public ReportServiceImpl(BizAttendanceMapper attendanceMapper,
                             BizTaskMapper taskMapper,
                             BizNotificationMapper notificationMapper,
                             SysUserMapper userMapper,
                             SysDeptMapper deptMapper,
                             WfProcessInstMapper processInstMapper) {
        this.attendanceMapper = attendanceMapper;
        this.taskMapper = taskMapper;
        this.notificationMapper = notificationMapper;
        this.userMapper = userMapper;
        this.deptMapper = deptMapper;
        this.processInstMapper = processInstMapper;
    }

    @Override
    public Map<String, Object> getOverview() {
        Map<String, Object> overview = new HashMap<>();
        overview.put("totalUsers", userMapper.selectCount(
                new LambdaQueryWrapper<SysUser>().eq(SysUser::getDelFlag, 0)));
        overview.put("totalTasks", taskMapper.selectCount(null));
        overview.put("totalNotifications", notificationMapper.selectCount(null));
        overview.put("totalProcesses", processInstMapper.selectCount(null));
        return overview;
    }

    @Override
    public Map<String, Object> getAttendanceReport(int year, int month) {
        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.plusMonths(1).minusDays(1);

        // 本月每日出勤统计
        List<BizAttendance> records = attendanceMapper.selectList(
                new LambdaQueryWrapper<BizAttendance>()
                        .between(BizAttendance::getCheckDate, start, end));

        Map<LocalDate, Map<String, Long>> dailyStats = records.stream()
                .collect(Collectors.groupingBy(
                        BizAttendance::getCheckDate,
                        Collectors.groupingBy(r -> r.getStatus() != null ? r.getStatus() : "normal", Collectors.counting())));

        // 汇总统计
        long normalCount = records.stream().filter(r -> "normal".equals(r.getStatus())).count();
        long lateCount = records.stream().filter(r -> "late".equals(r.getStatus())).count();
        long earlyCount = records.stream().filter(r -> "early_leave".equals(r.getStatus())).count();
        long absentCount = records.stream().filter(r -> "absent".equals(r.getStatus())).count();

        Map<String, Object> report = new HashMap<>();
        report.put("totalRecords", records.size());
        report.put("normalCount", normalCount);
        report.put("lateCount", lateCount);
        report.put("earlyLeaveCount", earlyCount);
        report.put("absentCount", absentCount);
        report.put("dailyStats", dailyStats);
        return report;
    }

    @Override
    public Map<String, Object> getTaskReport(Long userId) {
        LambdaQueryWrapper<BizTask> baseWrapper = new LambdaQueryWrapper<>();
        if (userId != null) {
            baseWrapper.eq(BizTask::getAssigneeId, userId);
        }

        long todoCount = taskMapper.selectCount(
                new LambdaQueryWrapper<BizTask>().eq(BizTask::getStatus, "todo"));
        long progressCount = taskMapper.selectCount(
                new LambdaQueryWrapper<BizTask>().eq(BizTask::getStatus, "progress"));
        long doneCount = taskMapper.selectCount(
                new LambdaQueryWrapper<BizTask>().eq(BizTask::getStatus, "done"));
        long archiveCount = taskMapper.selectCount(
                new LambdaQueryWrapper<BizTask>().eq(BizTask::getStatus, "archive"));

        // 逾期任务数
        long overdueCount = taskMapper.selectCount(
                new LambdaQueryWrapper<BizTask>()
                        .ne(BizTask::getStatus, "done")
                        .ne(BizTask::getStatus, "archive")
                        .lt(BizTask::getDueDate, LocalDate.now()));

        Map<String, Object> report = new HashMap<>();
        report.put("todo", todoCount);
        report.put("progress", progressCount);
        report.put("done", doneCount);
        report.put("archive", archiveCount);
        report.put("overdue", overdueCount);
        report.put("total", todoCount + progressCount + doneCount + archiveCount);
        return report;
    }

    @Override
    public Map<String, Object> getAssetReport() {
        // Placeholder - asset module is separate
        return new HashMap<>();
    }

    /**
     * 部门人数分布（报表用）
     */
    public Map<String, Object> getDeptDistribution() {
        List<SysDept> depts = deptMapper.selectList(
                new LambdaQueryWrapper<SysDept>().eq(SysDept::getDelFlag, 0));
        List<SysUser> users = userMapper.selectList(
                new LambdaQueryWrapper<SysUser>().eq(SysUser::getDelFlag, 0));

        Map<Long, Long> deptUserCount = users.stream()
                .filter(u -> u.getDeptId() != null)
                .collect(Collectors.groupingBy(SysUser::getDeptId, Collectors.counting()));

        List<Map<String, Object>> distribution = new ArrayList<>();
        for (SysDept dept : depts) {
            Map<String, Object> item = new HashMap<>();
            item.put("deptName", dept.getDeptName());
            item.put("count", deptUserCount.getOrDefault(dept.getId(), 0L));
            distribution.add(item);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("departments", distribution);
        return result;
    }

    /**
     * 每日待办趋势（最近7天）
     */
    public Map<String, Object> getTodoTrend() {
        LocalDate today = LocalDate.now();
        List<Map<String, Object>> trend = new ArrayList<>();

        for (int i = 6; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            // 该日创建的待办数
            long created = taskMapper.selectCount(
                    new LambdaQueryWrapper<BizTask>()
                            .ge(BizTask::getCreateTime, date.atStartOfDay())
                            .lt(BizTask::getCreateTime, date.plusDays(1).atStartOfDay()));
            // 该日完成的待办数（通过 status=done 且 updateTime 在该日）
            long completed = taskMapper.selectCount(
                    new LambdaQueryWrapper<BizTask>()
                            .eq(BizTask::getStatus, "done")
                            .ge(BizTask::getUpdateTime, date.atStartOfDay())
                            .lt(BizTask::getUpdateTime, date.plusDays(1).atStartOfDay()));

            Map<String, Object> day = new HashMap<>();
            day.put("date", date.toString());
            day.put("dayOfWeek", date.getDayOfWeek().getValue());
            day.put("created", created);
            day.put("completed", completed);
            trend.add(day);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("trend", trend);
        return result;
    }

    /**
     * 月度考勤趋势（12个月）
     */
    public Map<String, Object> getAttendanceYearTrend(int year) {
        List<Map<String, Object>> trend = new ArrayList<>();
        for (int month = 1; month <= 12; month++) {
            LocalDate start = LocalDate.of(year, month, 1);
            LocalDate end = start.plusMonths(1).minusDays(1);

            long normal = attendanceMapper.selectCount(
                    new LambdaQueryWrapper<BizAttendance>()
                            .between(BizAttendance::getCheckDate, start, end)
                            .eq(BizAttendance::getStatus, "normal"));
            long late = attendanceMapper.selectCount(
                    new LambdaQueryWrapper<BizAttendance>()
                            .between(BizAttendance::getCheckDate, start, end)
                            .eq(BizAttendance::getStatus, "late"));
            long absent = attendanceMapper.selectCount(
                    new LambdaQueryWrapper<BizAttendance>()
                            .between(BizAttendance::getCheckDate, start, end)
                            .eq(BizAttendance::getStatus, "absent"));

            Map<String, Object> monthData = new HashMap<>();
            monthData.put("month", month);
            monthData.put("normal", normal);
            monthData.put("late", late);
            monthData.put("absent", absent);
            trend.add(monthData);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("year", year);
        result.put("trend", trend);
        return result;
    }
}
