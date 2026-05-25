package com.smartauto.oa.meeting.service;

import com.smartauto.oa.common.PageResult;
import com.smartauto.oa.meeting.entity.BizMeetingRoom;

import java.util.List;

/**
 * 会议室服务接口
 */
public interface IBizMeetingRoomService {

    PageResult<BizMeetingRoom> page(BizMeetingRoom query, int pageNum, int pageSize);

    List<BizMeetingRoom> listAll();

    BizMeetingRoom getById(Long id);

    void create(BizMeetingRoom room);

    void update(BizMeetingRoom room);

    void delete(List<Long> ids);
}
