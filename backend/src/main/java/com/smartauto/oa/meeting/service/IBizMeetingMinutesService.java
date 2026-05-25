package com.smartauto.oa.meeting.service;

import com.smartauto.oa.common.PageResult;
import com.smartauto.oa.meeting.entity.BizMeetingMinutes;

/**
 * 会议纪要服务接口
 */
public interface IBizMeetingMinutesService {

    PageResult<BizMeetingMinutes> page(BizMeetingMinutes query, int pageNum, int pageSize);

    BizMeetingMinutes getById(Long id);

    void create(BizMeetingMinutes minutes);

    void update(BizMeetingMinutes minutes);

    void delete(Long id);
}
