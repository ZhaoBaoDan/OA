package com.smartauto.oa.meeting.controller;

import com.smartauto.oa.common.PageResult;
import com.smartauto.oa.common.Result;
import com.smartauto.oa.meeting.entity.BizMeetingRoom;
import com.smartauto.oa.meeting.service.IBizMeetingRoomService;
import com.smartauto.oa.system.annotation.OperLog;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 会议室管理
 */
@Tag(name = "会议室管理")
@RestController
@RequestMapping("/meeting/room")
public class BizMeetingRoomController {

    private final IBizMeetingRoomService roomService;

    public BizMeetingRoomController(IBizMeetingRoomService roomService) {
        this.roomService = roomService;
    }

    @Operation(summary = "分页查询会议室列表")
    @GetMapping
    public Result<PageResult<BizMeetingRoom>> page(BizMeetingRoom query,
                                                     @Parameter(description = "页码") @RequestParam(defaultValue = "1") int pageNum,
                                                     @Parameter(description = "每页条数") @RequestParam(defaultValue = "10") int pageSize) {
        return Result.success(roomService.page(query, pageNum, pageSize));
    }

    @Operation(summary = "查询所有可用会议室")
    @GetMapping("/list")
    public Result<List<BizMeetingRoom>> listAll() {
        return Result.success(roomService.listAll());
    }

    @Operation(summary = "根据ID查询会议室")
    @GetMapping("/{id}")
    public Result<BizMeetingRoom> getById(@PathVariable Long id) {
        return Result.success(roomService.getById(id));
    }

    @OperLog(title = "新增会议室", businessType = "INSERT")
    @Operation(summary = "新增会议室")
    @PostMapping
    public Result<Void> create(@Valid @RequestBody BizMeetingRoom room) {
        roomService.create(room);
        return Result.success();
    }

    @OperLog(title = "修改会议室", businessType = "UPDATE")
    @Operation(summary = "修改会议室")
    @PutMapping
    public Result<Void> update(@Valid @RequestBody BizMeetingRoom room) {
        roomService.update(room);
        return Result.success();
    }

    @OperLog(title = "删除会议室", businessType = "DELETE")
    @Operation(summary = "删除会议室")
    @DeleteMapping("/{ids}")
    public Result<Void> delete(@PathVariable List<Long> ids) {
        roomService.delete(ids);
        return Result.success();
    }
}
