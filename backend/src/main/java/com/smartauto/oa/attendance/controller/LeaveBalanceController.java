package com.smartauto.oa.attendance.controller;

import com.smartauto.oa.attendance.entity.LeaveBalance;
import com.smartauto.oa.attendance.service.ILeaveBalanceService;
import com.smartauto.oa.common.Result;
import com.smartauto.oa.common.SecurityUtils;
import com.smartauto.oa.system.annotation.OperLog;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 假期余额管理
 */
@Tag(name = "假期余额管理")
@RestController
@RequestMapping("/attendance/leave")
public class LeaveBalanceController {

    private final ILeaveBalanceService leaveBalanceService;

    public LeaveBalanceController(ILeaveBalanceService leaveBalanceService) {
        this.leaveBalanceService = leaveBalanceService;
    }

    @Operation(summary = "我的假期余额")
    @GetMapping("/mine")
    public Result<List<LeaveBalance>> myBalance(
            @Parameter(description = "年份") @RequestParam(required = false) Integer year) {
        Long userId = SecurityUtils.getUserId();
        if (year == null) {
            return Result.success(leaveBalanceService.listByUserId(userId));
        }
        return Result.success(leaveBalanceService.listByUserId(userId, year));
    }

    @Operation(summary = "指定用户假期余额")
    @GetMapping("/user/{userId}")
    public Result<List<LeaveBalance>> userBalance(
            @PathVariable Long userId,
            @Parameter(description = "年份") @RequestParam(required = false) Integer year) {
        if (year == null) {
            return Result.success(leaveBalanceService.listByUserId(userId));
        }
        return Result.success(leaveBalanceService.listByUserId(userId, year));
    }

    @Operation(summary = "假期余额详情")
    @GetMapping("/{id}")
    public Result<LeaveBalance> getById(@PathVariable Long id) {
        return Result.success(leaveBalanceService.getById(id));
    }

    @OperLog(title = "新增假期余额", businessType = "INSERT")
    @Operation(summary = "新增假期余额")
    @PostMapping
    public Result<Void> create(@RequestBody LeaveBalance balance) {
        leaveBalanceService.create(balance);
        return Result.success("新增成功");
    }

    @OperLog(title = "修改假期余额", businessType = "UPDATE")
    @Operation(summary = "修改假期余额")
    @PutMapping
    public Result<Void> update(@RequestBody LeaveBalance balance) {
        leaveBalanceService.update(balance);
        return Result.success("修改成功");
    }
}
