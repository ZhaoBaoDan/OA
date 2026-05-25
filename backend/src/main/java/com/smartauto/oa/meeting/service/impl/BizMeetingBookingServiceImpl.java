package com.smartauto.oa.meeting.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.smartauto.oa.common.BusinessException;
import com.smartauto.oa.meeting.entity.BizMeetingBooking;
import com.smartauto.oa.meeting.mapper.BizMeetingBookingMapper;
import com.smartauto.oa.meeting.service.IBizMeetingBookingService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

/**
 * 会议室预约服务实现
 */
@Service
public class BizMeetingBookingServiceImpl implements IBizMeetingBookingService {

    private final BizMeetingBookingMapper bookingMapper;

    public BizMeetingBookingServiceImpl(BizMeetingBookingMapper bookingMapper) {
        this.bookingMapper = bookingMapper;
    }

    @Override
    public List<BizMeetingBooking> listByRoomAndDate(Long roomId, LocalDate date) {
        LambdaQueryWrapper<BizMeetingBooking> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BizMeetingBooking::getRoomId, roomId)
               .eq(BizMeetingBooking::getBookingDate, date)
               .orderByAsc(BizMeetingBooking::getStartTime);
        return bookingMapper.selectList(wrapper);
    }

    @Override
    public List<BizMeetingBooking> listByRoomAndMonth(Long roomId, int year, int month) {
        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.plusMonths(1).minusDays(1);
        LambdaQueryWrapper<BizMeetingBooking> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BizMeetingBooking::getRoomId, roomId)
               .between(BizMeetingBooking::getBookingDate, start, end)
               .orderByAsc(BizMeetingBooking::getBookingDate)
               .orderByAsc(BizMeetingBooking::getStartTime);
        return bookingMapper.selectList(wrapper);
    }

    @Override
    public void create(BizMeetingBooking booking) {
        // 检查时间冲突
        List<BizMeetingBooking> existing = listByRoomAndDate(booking.getRoomId(), booking.getBookingDate());
        for (BizMeetingBooking b : existing) {
            if (booking.getStartTime().compareTo(b.getEndTime()) < 0
                    && booking.getEndTime().compareTo(b.getStartTime()) > 0) {
                throw new BusinessException("该时间段已被预约，请选择其他时间");
            }
        }
        bookingMapper.insert(booking);
    }

    @Override
    public void delete(Long id) {
        bookingMapper.deleteById(id);
    }
}
