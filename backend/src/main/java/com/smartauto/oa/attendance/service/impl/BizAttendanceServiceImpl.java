package com.smartauto.oa.attendance.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.smartauto.oa.attendance.entity.BizAttendance;
import com.smartauto.oa.attendance.mapper.BizAttendanceMapper;
import com.smartauto.oa.attendance.service.IBizAttendanceService;
import com.smartauto.oa.common.BusinessException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 考勤服务实现
 */
@Service
public class BizAttendanceServiceImpl implements IBizAttendanceService {

    private final BizAttendanceMapper attendanceMapper;

    /** 上班时间 09:00 */
    private static final LocalTime WORK_START = LocalTime.of(9, 0);
    /** 下班时间 18:00 */
    private static final LocalTime WORK_END = LocalTime.of(18, 0);

    public BizAttendanceServiceImpl(BizAttendanceMapper attendanceMapper) {
        this.attendanceMapper = attendanceMapper;
    }

    @Override
    public BizAttendance getTodayRecord(Long userId) {
        LambdaQueryWrapper<BizAttendance> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BizAttendance::getUserId, userId).eq(BizAttendance::getCheckDate, LocalDate.now());
        return attendanceMapper.selectOne(wrapper);
    }

    @Override
    public void checkIn(Long userId, String userName) {
        LocalDate today = LocalDate.now();
        BizAttendance existing = getTodayRecord(userId);
        if (existing != null && existing.getCheckIn() != null) {
            throw new BusinessException("今日已打卡");
        }

        LocalDateTime now = LocalDateTime.now();
        String status = now.toLocalTime().isAfter(WORK_START) ? "late" : "normal";

        if (existing == null) {
            BizAttendance attendance = new BizAttendance();
            attendance.setUserId(userId);
            attendance.setUserName(userName);
            attendance.setCheckDate(today);
            attendance.setCheckIn(now);
            attendance.setStatus(status);
            attendanceMapper.insert(attendance);
        } else {
            existing.setCheckIn(now);
            existing.setStatus(status);
            attendanceMapper.updateById(existing);
        }
    }

    @Override
    public void checkOut(Long userId, String userName) {
        BizAttendance record = getTodayRecord(userId);
        if (record == null || record.getCheckIn() == null) {
            throw new BusinessException("请先完成上班打卡");
        }
        if (record.getCheckOut() != null) {
            throw new BusinessException("今日已打下班卡");
        }

        LocalDateTime now = LocalDateTime.now();
        record.setCheckOut(now);

        // 判断早退
        if (now.toLocalTime().isBefore(WORK_END) && "normal".equals(record.getStatus())) {
            record.setStatus("early_leave");
        }

        // 计算工时
        Duration duration = Duration.between(record.getCheckIn(), now);
        BigDecimal hours = BigDecimal.valueOf(duration.toMinutes()).divide(BigDecimal.valueOf(60), 1, RoundingMode.HALF_UP);
        record.setWorkHours(hours);

        attendanceMapper.updateById(record);
    }

    @Override
    public Map<String, Object> getMonthlyStats(Long userId, int year, int month) {
        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.plusMonths(1).minusDays(1);

        LambdaQueryWrapper<BizAttendance> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BizAttendance::getUserId, userId)
               .between(BizAttendance::getCheckDate, start, end);
        long total = attendanceMapper.selectCount(wrapper);

        // 统计各状态数量
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalDays", total);

        LambdaQueryWrapper<BizAttendance> normalWrapper = new LambdaQueryWrapper<>();
        normalWrapper.eq(BizAttendance::getUserId, userId)
                     .between(BizAttendance::getCheckDate, start, end)
                     .eq(BizAttendance::getStatus, "normal");
        stats.put("normalDays", attendanceMapper.selectCount(normalWrapper));

        LambdaQueryWrapper<BizAttendance> lateWrapper = new LambdaQueryWrapper<>();
        lateWrapper.eq(BizAttendance::getUserId, userId)
                   .between(BizAttendance::getCheckDate, start, end)
                   .eq(BizAttendance::getStatus, "late");
        stats.put("lateCount", attendanceMapper.selectCount(lateWrapper));

        LambdaQueryWrapper<BizAttendance> earlyWrapper = new LambdaQueryWrapper<>();
        earlyWrapper.eq(BizAttendance::getUserId, userId)
                    .between(BizAttendance::getCheckDate, start, end)
                    .eq(BizAttendance::getStatus, "early_leave");
        stats.put("earlyLeaveCount", attendanceMapper.selectCount(earlyWrapper));

        return stats;
    }
}
