package com.smartauto.oa.attendance.controller;

import com.smartauto.oa.attendance.entity.AttendanceSchedule;
import com.smartauto.oa.attendance.service.IScheduleService;
import com.smartauto.oa.common.PageResult;
import com.smartauto.oa.common.Result;
import com.smartauto.oa.common.SecurityUtils;
import com.smartauto.oa.system.annotation.OperLog;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 考勤班次管理
 */
@Tag(name = "考勤班次管理")
@RestController
@RequestMapping("/attendance/schedule")
public class ScheduleController {

    private final IScheduleService scheduleService;

    public ScheduleController(IScheduleService scheduleService) {
        this.scheduleService = scheduleService;
    }

    @Operation(summary = "班次列表（分页）")
    @GetMapping
    public Result<PageResult<AttendanceSchedule>> list(
            AttendanceSchedule query,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        return Result.success(scheduleService.page(query, pageNum, pageSize));
    }

    @Operation(summary = "我的班次列表")
    @GetMapping("/mine")
    public Result<List<AttendanceSchedule>> mySchedules() {
        Long userId = SecurityUtils.getUserId();
        return Result.success(scheduleService.listByUserId(userId));
    }

    @Operation(summary = "获取当前有效班次")
    @GetMapping("/effective")
    public Result<AttendanceSchedule> getEffective() {
        Long userId = SecurityUtils.getUserId();
        return Result.success(scheduleService.getEffectiveSchedule(userId));
    }

    @Operation(summary = "班次详情")
    @GetMapping("/{id}")
    public Result<AttendanceSchedule> getById(@PathVariable Long id) {
        return Result.success(scheduleService.getById(id));
    }

    @OperLog(title = "新增班次", businessType = "INSERT")
    @Operation(summary = "新增班次")
    @PostMapping
    public Result<Void> create(@RequestBody AttendanceSchedule schedule) {
        scheduleService.create(schedule);
        return Result.success("新增成功");
    }

    @OperLog(title = "修改班次", businessType = "UPDATE")
    @Operation(summary = "修改班次")
    @PutMapping
    public Result<Void> update(@RequestBody AttendanceSchedule schedule) {
        scheduleService.update(schedule);
        return Result.success("修改成功");
    }

    @OperLog(title = "删除班次", businessType = "DELETE")
    @Operation(summary = "删除班次")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        scheduleService.delete(id);
        return Result.success("删除成功");
    }
}
