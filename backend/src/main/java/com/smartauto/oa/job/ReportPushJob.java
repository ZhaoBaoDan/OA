package com.smartauto.oa.job;

import com.smartauto.oa.notification.entity.BizNotification;
import com.smartauto.oa.notification.service.IBizNotificationService;
import com.smartauto.oa.report.service.impl.ReportServiceImpl;
import com.smartauto.oa.system.entity.SysUser;
import com.smartauto.oa.system.mapper.SysUserMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xxl.job.core.handler.annotation.XxlJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 报表定时推送任务
 */
@Component
public class ReportPushJob {

    private static final Logger logger = LoggerFactory.getLogger(ReportPushJob.class);

    private final ReportServiceImpl reportService;
    private final SysUserMapper userMapper;
    private final IBizNotificationService notificationService;

    public ReportPushJob(ReportServiceImpl reportService,
                         SysUserMapper userMapper,
                         IBizNotificationService notificationService) {
        this.reportService = reportService;
        this.userMapper = userMapper;
        this.notificationService = notificationService;
    }

    /**
     * 每周一 09:00 推送周报统计给管理员
     */
    @XxlJob("weeklyReportPushJobHandler")
    public void execute() {
        logger.info("开始执行周报推送任务...");
        LocalDate today = LocalDate.now();
        int year = today.getYear();
        int month = today.getMonthValue();

        // 生成报表摘要
        Map<String, Object> taskReport = reportService.getTaskReport(null);
        Map<String, Object> attendanceReport = reportService.getAttendanceReport(year, month);

        String summary = String.format(
                "本周工作简报：\n" +
                "• 待办任务: %s 件\n" +
                "• 进行中: %s 件\n" +
                "• 已完成: %s 件\n" +
                "• 逾期: %s 件\n" +
                "• 本月出勤记录: %s 条",
                taskReport.getOrDefault("todo", 0),
                taskReport.getOrDefault("progress", 0),
                taskReport.getOrDefault("done", 0),
                taskReport.getOrDefault("overdue", 0),
                attendanceReport.getOrDefault("totalRecords", 0));

        // 通知所有管理员
        List<SysUser> admins = userMapper.selectList(
                new LambdaQueryWrapper<SysUser>().eq(SysUser::getId, 1L)); // 管理员
        for (SysUser admin : admins) {
            BizNotification notification = new BizNotification();
            notification.setTitle("每周工作简报");
            notification.setContent(summary);
            notification.setType("system");
            notification.setUserId(admin.getId());
            notification.setIsRead(0);
            notificationService.create(notification);
        }
        logger.info("周报推送完成");
    }
}
