package com.smartauto.oa.notification.service;

import com.smartauto.oa.common.PageResult;
import com.smartauto.oa.notification.entity.BizNotification;

/**
 * 通知服务接口
 */
public interface IBizNotificationService {

    /**
     * 分页查询通知（全部/未读/已读）
     */
    PageResult<BizNotification> page(Long userId, String filter, int pageNum, int pageSize);

    /**
     * 获取未读数量
     */
    long unreadCount(Long userId);

    /**
     * 标记单条已读
     */
    void markRead(Long id);

    /**
     * 全部标记已读
     */
    void markAllRead(Long userId);

    /**
     * 创建通知
     */
    void create(BizNotification notification);

    /**
     * 删除通知
     */
    void delete(Long id);
}
