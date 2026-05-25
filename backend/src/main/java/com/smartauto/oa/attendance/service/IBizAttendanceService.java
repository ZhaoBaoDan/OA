package com.smartauto.oa.attendance.service;

import com.smartauto.oa.attendance.entity.BizAttendance;

import java.util.Map;

/**
 * 考勤服务接口
 */
public interface IBizAttendanceService {

    /**
     * 获取用户今日打卡记录
     */
    BizAttendance getTodayRecord(Long userId);

    /**
     * 上班打卡
     */
    void checkIn(Long userId, String userName);

    /**
     * 下班打卡
     */
    void checkOut(Long userId, String userName);

    /**
     * 获取月度考勤统计
     */
    Map<String, Object> getMonthlyStats(Long userId, int year, int month);
}
