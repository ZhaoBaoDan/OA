package com.smartauto.oa.attendance.service;

import com.smartauto.oa.attendance.entity.LeaveBalance;

import java.util.List;

/**
 * 假期余额服务接口
 */
public interface ILeaveBalanceService {

    List<LeaveBalance> listByUserId(Long userId, Integer year);

    List<LeaveBalance> listByUserId(Long userId);

    LeaveBalance getById(Long id);

    void create(LeaveBalance balance);

    void update(LeaveBalance balance);

    void deduct(Long userId, String leaveType, int year, java.math.BigDecimal days);
}
