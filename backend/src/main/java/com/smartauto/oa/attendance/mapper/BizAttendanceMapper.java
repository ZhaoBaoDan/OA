package com.smartauto.oa.attendance.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.smartauto.oa.attendance.entity.BizAttendance;
import org.apache.ibatis.annotations.Mapper;

/**
 * 考勤记录 Mapper
 */
@Mapper
public interface BizAttendanceMapper extends BaseMapper<BizAttendance> {
}
