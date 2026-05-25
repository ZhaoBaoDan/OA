package com.smartauto.oa.notification.controller;

import com.smartauto.oa.common.PageResult;
import com.smartauto.oa.common.Result;
import com.smartauto.oa.common.SecurityUtils;
import com.smartauto.oa.notification.entity.BizNotification;
import com.smartauto.oa.notification.service.IBizNotificationService;
import com.smartauto.oa.system.annotation.OperLog;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

/**
 * 通知管理
 */
@Tag(name = "通知管理")
@RestController
@RequestMapping("/notification")
public class BizNotificationController {

    private final IBizNotificationService notificationService;

    public BizNotificationController(IBizNotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @Operation(summary = "分页查询通知")
    @GetMapping
    public Result<PageResult<BizNotification>> page(
            @Parameter(description = "过滤条件（all/unread/read）") @RequestParam(defaultValue = "all") String filter,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int pageNum,
            @Parameter(description = "每页条数") @RequestParam(defaultValue = "10") int pageSize) {
        Long userId = SecurityUtils.getUserId();
        return Result.success(notificationService.page(userId, filter, pageNum, pageSize));
    }

    @Operation(summary = "获取未读通知数量")
    @GetMapping("/unread-count")
    public Result<Long> unreadCount() {
        Long userId = SecurityUtils.getUserId();
        return Result.success(notificationService.unreadCount(userId));
    }

    @Operation(summary = "标记单条通知已读")
    @PutMapping("/{id}/read")
    public Result<Void> markRead(@PathVariable Long id) {
        notificationService.markRead(id);
        return Result.success();
    }

    @Operation(summary = "全部标记已读")
    @PutMapping("/read-all")
    public Result<Void> markAllRead() {
        Long userId = SecurityUtils.getUserId();
        notificationService.markAllRead(userId);
        return Result.success();
    }

    @OperLog(title = "删除通知", businessType = "DELETE")
    @Operation(summary = "删除通知")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        notificationService.delete(id);
        return Result.success();
    }
}
