package com.smartauto.oa.attendance.service;

import com.smartauto.oa.attendance.entity.AttendanceSchedule;
import com.smartauto.oa.common.PageResult;

import java.util.List;

/**
 * 考勤班次服务接口
 */
public interface IScheduleService {

    PageResult<AttendanceSchedule> page(AttendanceSchedule query, int pageNum, int pageSize);

    List<AttendanceSchedule> listByUserId(Long userId);

    AttendanceSchedule getById(Long id);

    AttendanceSchedule getEffectiveSchedule(Long userId);

    void create(AttendanceSchedule schedule);

    void update(AttendanceSchedule schedule);

    void delete(Long id);
}
