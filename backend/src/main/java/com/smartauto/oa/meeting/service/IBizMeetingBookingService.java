package com.smartauto.oa.meeting.service;

import com.smartauto.oa.meeting.entity.BizMeetingBooking;

import java.time.LocalDate;
import java.util.List;

/**
 * 会议室预约服务接口
 */
public interface IBizMeetingBookingService {

    /**
     * 查询指定会议室指定日期的预约列表
     */
    List<BizMeetingBooking> listByRoomAndDate(Long roomId, LocalDate date);

    /**
     * 查询指定会议室指定月份的预约列表
     */
    List<BizMeetingBooking> listByRoomAndMonth(Long roomId, int year, int month);

    void create(BizMeetingBooking booking);

    void delete(Long id);
}
