package com.smartauto.oa.meeting.controller;

import com.smartauto.oa.common.Result;
import com.smartauto.oa.common.SecurityUtils;
import com.smartauto.oa.meeting.entity.BizMeetingBooking;
import com.smartauto.oa.meeting.service.IBizMeetingBookingService;
import com.smartauto.oa.system.annotation.OperLog;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * 会议室预约管理
 */
@Tag(name = "会议室预约管理")
@RestController
@RequestMapping("/meeting/booking")
public class BizMeetingBookingController {

    private final IBizMeetingBookingService bookingService;

    public BizMeetingBookingController(IBizMeetingBookingService bookingService) {
        this.bookingService = bookingService;
    }

    @Operation(summary = "查询指定会议室指定日期的预约")
    @GetMapping
    public Result<List<BizMeetingBooking>> list(
            @Parameter(description = "会议室ID", required = true) @RequestParam Long roomId,
            @Parameter(description = "日期", required = true) @RequestParam LocalDate date) {
        return Result.success(bookingService.listByRoomAndDate(roomId, date));
    }

    @Operation(summary = "查询指定会议室指定月份的预约")
    @GetMapping("/month")
    public Result<List<BizMeetingBooking>> listByMonth(
            @Parameter(description = "会议室ID", required = true) @RequestParam Long roomId,
            @Parameter(description = "年份", required = true) @RequestParam int year,
            @Parameter(description = "月份", required = true) @RequestParam int month) {
        return Result.success(bookingService.listByRoomAndMonth(roomId, year, month));
    }

    @OperLog(title = "新增会议室预约", businessType = "INSERT")
    @Operation(summary = "新增预约")
    @PostMapping
    public Result<Void> create(@Valid @RequestBody BizMeetingBooking booking) {
        Long userId = SecurityUtils.getUserId();
        String userName = SecurityUtils.getUsername();
        booking.setOrganizerId(userId);
        booking.setOrganizer(userName);
        bookingService.create(booking);
        return Result.success();
    }

    @OperLog(title = "取消会议室预约", businessType = "DELETE")
    @Operation(summary = "取消预约")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        bookingService.delete(id);
        return Result.success();
    }
}
