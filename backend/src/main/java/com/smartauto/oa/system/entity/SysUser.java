package com.smartauto.oa.system.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.smartauto.oa.common.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_user")
@Schema(description = "用户实体")
public class SysUser extends BaseEntity {

    @NotBlank(message = "用户名不能为空")
    @Size(min = 2, max = 50, message = "用户名长度2-50个字符")
    @Schema(description = "用户账号")
    private String username;

    @JsonIgnore
    @Schema(description = "密码")
    private String password;

    @Schema(description = "用户昵称")
    private String nickname;

    @Email(message = "邮箱格式不正确")
    @Schema(description = "邮箱")
    private String email;

    @Schema(description = "手机号")
    private String phone;

    @Schema(description = "头像地址")
    private String avatar;

    @Schema(description = "性别（0未知 1男 2女）")
    private Integer sex;

    @Schema(description = "状态（1正常 0停用）")
    private Integer status;

    @Schema(description = "部门ID")
    private Long deptId;

    @Schema(description = "最后登录IP")
    private String loginIp;

    @Schema(description = "最后登录时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField("login_time")
    private LocalDateTime loginDate;

    /** 非数据库字段 */
    @TableField(exist = false)
    @Schema(description = "部门名称")
    private String deptName;

    @TableField(exist = false)
    @Schema(description = "角色列表")
    private List<SysRole> roles;

    @TableField(exist = false)
    @Schema(description = "岗位列表")
    private List<SysPost> posts;

    @TableField(exist = false)
    @Schema(description = "角色ID列表")
    private List<Long> roleIds;

    @TableField(exist = false)
    @Schema(description = "岗位ID列表")
    private List<Long> postIds;
}
