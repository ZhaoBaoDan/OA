package com.smartauto.oa.meeting.controller;

import com.smartauto.oa.common.PageResult;
import com.smartauto.oa.common.Result;
import com.smartauto.oa.common.SecurityUtils;
import com.smartauto.oa.meeting.entity.BizMeetingMinutes;
import com.smartauto.oa.meeting.service.IBizMeetingMinutesService;
import com.smartauto.oa.system.annotation.OperLog;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

/**
 * 会议纪要管理
 */
@Tag(name = "会议纪要管理")
@RestController
@RequestMapping("/meeting/minutes")
public class BizMeetingMinutesController {

    private final IBizMeetingMinutesService minutesService;

    public BizMeetingMinutesController(IBizMeetingMinutesService minutesService) {
        this.minutesService = minutesService;
    }

    @Operation(summary = "纪要列表（分页）")
    @GetMapping
    public Result<PageResult<BizMeetingMinutes>> list(
            BizMeetingMinutes query,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        return Result.success(minutesService.page(query, pageNum, pageSize));
    }

    @Operation(summary = "纪要详情")
    @GetMapping("/{id}")
    public Result<BizMeetingMinutes> getById(@PathVariable Long id) {
        return Result.success(minutesService.getById(id));
    }

    @OperLog(title = "新增会议纪要", businessType = "INSERT")
    @Operation(summary = "新增纪要")
    @PostMapping
    public Result<Void> create(@RequestBody BizMeetingMinutes minutes) {
        Long userId = SecurityUtils.getUserId();
        String userName = SecurityUtils.getUsername();
        minutes.setRecorderId(userId);
        minutes.setRecorderName(userName);
        minutesService.create(minutes);
        return Result.success("新增成功");
    }

    @OperLog(title = "修改会议纪要", businessType = "UPDATE")
    @Operation(summary = "修改纪要")
    @PutMapping
    public Result<Void> update(@RequestBody BizMeetingMinutes minutes) {
        minutesService.update(minutes);
        return Result.success("修改成功");
    }

    @OperLog(title = "删除会议纪要", businessType = "DELETE")
    @Operation(summary = "删除纪要")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        minutesService.delete(id);
        return Result.success("删除成功");
    }
}
