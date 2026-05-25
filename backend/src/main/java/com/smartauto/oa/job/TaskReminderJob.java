package com.smartauto.oa.job;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.smartauto.oa.notification.entity.BizNotification;
import com.smartauto.oa.notification.service.IBizNotificationService;
import com.smartauto.oa.task.entity.BizTask;
import com.smartauto.oa.task.mapper.BizTaskMapper;
import com.xxl.job.core.handler.annotation.XxlJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

/**
 * 任务到期提醒定时任务
 */
@Component
public class TaskReminderJob {

    private static final Logger logger = LoggerFactory.getLogger(TaskReminderJob.class);

    private final BizTaskMapper taskMapper;
    private final IBizNotificationService notificationService;

    public TaskReminderJob(BizTaskMapper taskMapper, IBizNotificationService notificationService) {
        this.taskMapper = taskMapper;
        this.notificationService = notificationService;
    }

    /**
     * 每日 09:00 检查即将到期的任务并提醒负责人
     */
    @XxlJob("taskReminderJobHandler")
    public void execute() {
        logger.info("开始执行任务到期提醒...");
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        LocalDate dayAfterTomorrow = LocalDate.now().plusDays(2);

        // 查询明天和后天到期的未完成任务
        List<BizTask> tasks = taskMapper.selectList(
                new LambdaQueryWrapper<BizTask>()
                        .in(BizTask::getStatus, "todo", "progress")
                        .ge(BizTask::getDueDate, tomorrow)
                        .le(BizTask::getDueDate, dayAfterTomorrow)
                        .isNotNull(BizTask::getAssigneeId));

        for (BizTask task : tasks) {
            long daysUntilDue = java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), task.getDueDate());
            String urgency = daysUntilDue <= 1 ? "明天" : "后天";

            BizNotification notification = new BizNotification();
            notification.setTitle("任务即将到期提醒");
            notification.setContent("您负责的「" + task.getTitle() + "」任务将于" + urgency + "到期，请注意安排时间。");
            notification.setType("task");
            notification.setUserId(task.getAssigneeId());
            notification.setIsRead(0);
            notificationService.create(notification);
        }
        logger.info("任务到期提醒完成，共提醒 {} 条", tasks.size());
    }
}
