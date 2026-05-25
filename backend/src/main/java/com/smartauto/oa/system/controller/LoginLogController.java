package com.smartauto.oa.system.controller;

import com.smartauto.oa.common.PageResult;
import com.smartauto.oa.common.Result;
import com.smartauto.oa.system.entity.SysLoginLog;
import com.smartauto.oa.system.service.ISysLoginLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

/**
 * 登录日志管理
 */
@Tag(name = "登录日志管理")
@RestController
@RequestMapping("/auth")
public class LoginLogController {

    private final ISysLoginLogService loginLogService;

    public LoginLogController(ISysLoginLogService loginLogService) {
        this.loginLogService = loginLogService;
    }

    @Operation(summary = "分页查询登录日志")
    @GetMapping("/loginLog")
    public Result<PageResult<SysLoginLog>> page(SysLoginLog query,
                                                  @Parameter(description = "页码") @RequestParam(defaultValue = "1") int pageNum,
                                                  @Parameter(description = "每页条数") @RequestParam(defaultValue = "10") int pageSize) {
        PageResult<SysLoginLog> result = loginLogService.page(query, pageNum, pageSize);
        return Result.success(result);
    }
}
