package com.smartauto.oa.notification.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.smartauto.oa.common.PageResult;
import com.smartauto.oa.notification.controller.WebSocketNotificationController;
import com.smartauto.oa.notification.entity.BizNotification;
import com.smartauto.oa.notification.mapper.BizNotificationMapper;
import com.smartauto.oa.notification.service.IBizNotificationService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * 通知服务实现
 */
@Service
public class BizNotificationServiceImpl implements IBizNotificationService {

    private final BizNotificationMapper notificationMapper;
    private final WebSocketNotificationController wsController;

    public BizNotificationServiceImpl(BizNotificationMapper notificationMapper,
                                       WebSocketNotificationController wsController) {
        this.notificationMapper = notificationMapper;
        this.wsController = wsController;
    }

    @Override
    public PageResult<BizNotification> page(Long userId, String filter, int pageNum, int pageSize) {
        Page<BizNotification> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<BizNotification> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BizNotification::getUserId, userId);
        if ("unread".equals(filter)) {
            wrapper.eq(BizNotification::getIsRead, 0);
        } else if ("read".equals(filter)) {
            wrapper.eq(BizNotification::getIsRead, 1);
        }
        wrapper.orderByDesc(BizNotification::getCreateTime);
        Page<BizNotification> result = notificationMapper.selectPage(page, wrapper);
        return new PageResult<>(result.getTotal(), result.getRecords(), pageNum, pageSize);
    }

    @Override
    public long unreadCount(Long userId) {
        LambdaQueryWrapper<BizNotification> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BizNotification::getUserId, userId).eq(BizNotification::getIsRead, 0);
        return notificationMapper.selectCount(wrapper);
    }

    @Override
    public void markRead(Long id) {
        BizNotification notification = notificationMapper.selectById(id);
        if (notification != null && notification.getIsRead() == 0) {
            notification.setIsRead(1);
            notification.setReadTime(LocalDateTime.now());
            notificationMapper.updateById(notification);
        }
    }

    @Override
    public void markAllRead(Long userId) {
        LambdaUpdateWrapper<BizNotification> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(BizNotification::getUserId, userId)
               .eq(BizNotification::getIsRead, 0)
               .set(BizNotification::getIsRead, 1)
               .set(BizNotification::getReadTime, LocalDateTime.now());
        notificationMapper.update(null, wrapper);
    }

    @Override
    public void create(BizNotification notification) {
        if (notification.getIsRead() == null) {
            notification.setIsRead(0);
        }
        notificationMapper.insert(notification);

        // 同时通过 WebSocket 实时推送
        try {
            wsController.sendToUser(
                    notification.getUserId(),
                    notification.getType(),
                    notification.getTitle(),
                    notification.getContent());
        } catch (Exception ignored) {
            // WebSocket 推送失败不阻断主流程
        }
    }

    @Override
    public void delete(Long id) {
        notificationMapper.deleteById(id);
    }
}
