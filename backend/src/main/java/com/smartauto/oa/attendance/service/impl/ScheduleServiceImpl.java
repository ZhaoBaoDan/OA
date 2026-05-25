package com.smartauto.oa.attendance.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.smartauto.oa.attendance.entity.AttendanceSchedule;
import com.smartauto.oa.attendance.mapper.AttendanceScheduleMapper;
import com.smartauto.oa.attendance.service.IScheduleService;
import com.smartauto.oa.common.BusinessException;
import com.smartauto.oa.common.PageResult;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

/**
 * 考勤班次服务实现
 */
@Service
public class ScheduleServiceImpl implements IScheduleService {

    private final AttendanceScheduleMapper scheduleMapper;

    public ScheduleServiceImpl(AttendanceScheduleMapper scheduleMapper) {
        this.scheduleMapper = scheduleMapper;
    }

    @Override
    public PageResult<AttendanceSchedule> page(AttendanceSchedule query, int pageNum, int pageSize) {
        Page<AttendanceSchedule> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<AttendanceSchedule> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(query.getUserId() != null, AttendanceSchedule::getUserId, query.getUserId())
               .eq(query.getScheduleType() != null, AttendanceSchedule::getScheduleType, query.getScheduleType())
               .eq(AttendanceSchedule::getDelFlag, 0)
               .orderByDesc(AttendanceSchedule::getEffectiveDate);
        Page<AttendanceSchedule> result = scheduleMapper.selectPage(page, wrapper);
        return new PageResult<>(result.getTotal(), result.getRecords(), pageNum, pageSize);
    }

    @Override
    public List<AttendanceSchedule> listByUserId(Long userId) {
        return scheduleMapper.selectList(
                new LambdaQueryWrapper<AttendanceSchedule>()
                        .eq(AttendanceSchedule::getUserId, userId)
                        .eq(AttendanceSchedule::getDelFlag, 0)
                        .orderByDesc(AttendanceSchedule::getEffectiveDate));
    }

    @Override
    public AttendanceSchedule getById(Long id) {
        AttendanceSchedule schedule = scheduleMapper.selectById(id);
        if (schedule == null) throw new BusinessException("班次不存在");
        return schedule;
    }

    @Override
    public AttendanceSchedule getEffectiveSchedule(Long userId) {
        LocalDate today = LocalDate.now();
        return scheduleMapper.selectOne(
                new LambdaQueryWrapper<AttendanceSchedule>()
                        .eq(AttendanceSchedule::getUserId, userId)
                        .eq(AttendanceSchedule::getStatus, 1)
                        .eq(AttendanceSchedule::getDelFlag, 0)
                        .le(AttendanceSchedule::getEffectiveDate, today)
                        .orderByDesc(AttendanceSchedule::getEffectiveDate)
                        .last("LIMIT 1"));
    }

    @Override
    public void create(AttendanceSchedule schedule) {
        schedule.setDelFlag(0);
        scheduleMapper.insert(schedule);
    }

    @Override
    public void update(AttendanceSchedule schedule) {
        if (scheduleMapper.selectById(schedule.getId()) == null) {
            throw new BusinessException("班次不存在");
        }
        scheduleMapper.updateById(schedule);
    }

    @Override
    public void delete(Long id) {
        scheduleMapper.deleteById(id);
    }
}
