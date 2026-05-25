package com.smartauto.oa.attendance.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.smartauto.oa.attendance.entity.LeaveBalance;
import com.smartauto.oa.attendance.mapper.LeaveBalanceMapper;
import com.smartauto.oa.attendance.service.ILeaveBalanceService;
import com.smartauto.oa.common.BusinessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * 假期余额服务实现
 */
@Service
public class LeaveBalanceServiceImpl implements ILeaveBalanceService {

    private final LeaveBalanceMapper leaveBalanceMapper;

    public LeaveBalanceServiceImpl(LeaveBalanceMapper leaveBalanceMapper) {
        this.leaveBalanceMapper = leaveBalanceMapper;
    }

    @Override
    public List<LeaveBalance> listByUserId(Long userId, Integer year) {
        return leaveBalanceMapper.selectList(
                new LambdaQueryWrapper<LeaveBalance>()
                        .eq(LeaveBalance::getUserId, userId)
                        .eq(LeaveBalance::getYear, year)
                        .eq(LeaveBalance::getDelFlag, 0));
    }

    @Override
    public List<LeaveBalance> listByUserId(Long userId) {
        return listByUserId(userId, LocalDate.now().getYear());
    }

    @Override
    public LeaveBalance getById(Long id) {
        LeaveBalance balance = leaveBalanceMapper.selectById(id);
        if (balance == null) throw new BusinessException("假期余额记录不存在");
        return balance;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void create(LeaveBalance balance) {
        // 检查是否已存在同年同类型记录
        Long count = leaveBalanceMapper.selectCount(
                new LambdaQueryWrapper<LeaveBalance>()
                        .eq(LeaveBalance::getUserId, balance.getUserId())
                        .eq(LeaveBalance::getLeaveType, balance.getLeaveType())
                        .eq(LeaveBalance::getYear, balance.getYear())
                        .eq(LeaveBalance::getDelFlag, 0));
        if (count > 0) {
            throw new BusinessException("该用户同年同类型假期余额已存在");
        }
        balance.setRemainDays(balance.getTotalDays().subtract(
                balance.getUsedDays() != null ? balance.getUsedDays() : BigDecimal.ZERO));
        balance.setDelFlag(0);
        leaveBalanceMapper.insert(balance);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(LeaveBalance balance) {
        if (leaveBalanceMapper.selectById(balance.getId()) == null) {
            throw new BusinessException("假期余额记录不存在");
        }
        if (balance.getTotalDays() != null && balance.getUsedDays() != null) {
            balance.setRemainDays(balance.getTotalDays().subtract(balance.getUsedDays()));
        }
        leaveBalanceMapper.updateById(balance);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deduct(Long userId, String leaveType, int year, BigDecimal days) {
        LeaveBalance balance = leaveBalanceMapper.selectOne(
                new LambdaQueryWrapper<LeaveBalance>()
                        .eq(LeaveBalance::getUserId, userId)
                        .eq(LeaveBalance::getLeaveType, leaveType)
                        .eq(LeaveBalance::getYear, year)
                        .eq(LeaveBalance::getDelFlag, 0));
        if (balance == null) {
            throw new BusinessException("未找到对应假期余额");
        }
        if (balance.getRemainDays().compareTo(days) < 0) {
            throw new BusinessException("假期余额不足，剩余" + balance.getRemainDays() + "天");
        }
        balance.setUsedDays(balance.getUsedDays().add(days));
        balance.setRemainDays(balance.getRemainDays().subtract(days));
        leaveBalanceMapper.updateById(balance);
    }
}
