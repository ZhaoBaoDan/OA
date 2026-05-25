package com.smartauto.oa.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 统一返回结果
 */
@Data
@Schema(description = "统一返回结果")
public class Result<T> implements Serializable {

    @Schema(description = "状态码")
    private int code;

    @Schema(description = "消息")
    private String msg;

    @Schema(description = "数据")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T data;

    public static final int SUCCESS_CODE = 200;
    public static final int FAIL_CODE = 500;
    public static final int UNAUTHORIZED_CODE = 401;
    public static final int FORBIDDEN_CODE = 403;

    public static <T> Result<T> success() {
        return build(SUCCESS_CODE, "操作成功", null);
    }

    public static <T> Result<T> success(T data) {
        return build(SUCCESS_CODE, "操作成功", data);
    }

    public static Result<Void> success(String msg) {
        return build(SUCCESS_CODE, msg, null);
    }

    public static <T> Result<T> success(String msg, T data) {
        return build(SUCCESS_CODE, msg, data);
    }

    public static <T> Result<T> fail() {
        return build(FAIL_CODE, "操作失败", null);
    }

    public static <T> Result<T> fail(String msg) {
        return build(FAIL_CODE, msg, null);
    }

    public static <T> Result<T> fail(int code, String msg) {
        return build(code, msg, null);
    }

    public static <T> Result<T> unauthorized(String msg) {
        return build(UNAUTHORIZED_CODE, msg, null);
    }

    public static <T> Result<T> forbidden(String msg) {
        return build(FORBIDDEN_CODE, msg, null);
    }

    private static <T> Result<T> build(int code, String msg, T data) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setMsg(msg);
        result.setData(data);
        return result;
    }
}
