package com.smartauto.oa.job;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.smartauto.oa.attendance.entity.BizAttendance;
import com.smartauto.oa.attendance.mapper.BizAttendanceMapper;
import com.smartauto.oa.notification.entity.BizNotification;
import com.smartauto.oa.notification.service.IBizNotificationService;
import com.smartauto.oa.system.entity.SysUser;
import com.smartauto.oa.system.mapper.SysUserMapper;
import com.xxl.job.core.handler.annotation.XxlJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

/**
 * 考勤提醒定时任务
 */
@Component
public class AttendanceReminderJob {

    private static final Logger logger = LoggerFactory.getLogger(AttendanceReminderJob.class);

    private final SysUserMapper userMapper;
    private final BizAttendanceMapper attendanceMapper;
    private final IBizNotificationService notificationService;

    public AttendanceReminderJob(SysUserMapper userMapper,
                                 BizAttendanceMapper attendanceMapper,
                                 IBizNotificationService notificationService) {
        this.userMapper = userMapper;
        this.attendanceMapper = attendanceMapper;
        this.notificationService = notificationService;
    }

    /**
     * 每日 18:30 检查未打卡人员并发送提醒
     */
    @XxlJob("attendanceReminderJobHandler")
    public void execute() {
        logger.info("开始执行考勤提醒任务...");
        LocalDate today = LocalDate.now();

        // 查询所有正常用户
        List<SysUser> users = userMapper.selectList(
                new LambdaQueryWrapper<SysUser>()
                        .eq(SysUser::getStatus, 1)
                        .eq(SysUser::getDelFlag, 0));

        int reminderCount = 0;
        for (SysUser user : users) {
            // 检查今日是否有打卡记录
            Long count = attendanceMapper.selectCount(
                    new LambdaQueryWrapper<BizAttendance>()
                            .eq(BizAttendance::getUserId, user.getId())
                            .eq(BizAttendance::getCheckDate, today));
            if (count == 0) {
                // 发送提醒通知
                BizNotification notification = new BizNotification();
                notification.setTitle("考勤打卡提醒");
                notification.setContent("您今日尚未完成下班打卡，请及时打卡。");
                notification.setType("attendance");
                notification.setUserId(user.getId());
                notification.setIsRead(0);
                notificationService.create(notification);
                reminderCount++;
            }
        }
        logger.info("考勤提醒任务完成，共提醒 {} 人", reminderCount);
    }
}
