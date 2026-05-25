package com.smartauto.oa.common.event;

import lombok.Data;

import java.io.Serializable;

/**
 * 系统通知事件
 */
@Data
public class SystemNoticeEvent implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 通知类型（system/workflow/task/meeting/attendance） */
    private String type;

    /** 标题 */
    private String title;

    /** 内容 */
    private String content;

    /** 接收人 ID */
    private Long userId;

    public SystemNoticeEvent() {}

    public SystemNoticeEvent(String type, String title, String content, Long userId) {
        this.type = type;
        this.title = title;
        this.content = content;
        this.userId = userId;
    }
}
