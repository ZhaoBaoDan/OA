-- ========================================
-- SmartAuto OA 数据库建表脚本
-- MySQL 8.x
-- ========================================

CREATE DATABASE IF NOT EXISTS `smartauto_oa` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE `smartauto_oa`;

-- ----------------------------------------
-- 1. 部门表
-- ----------------------------------------
CREATE TABLE `sys_dept` (
    `id`          BIGINT       NOT NULL COMMENT '部门ID',
    `parent_id`   BIGINT       DEFAULT 0 COMMENT '父部门ID',
    `ancestors`   VARCHAR(500) DEFAULT '' COMMENT '祖级列表',
    `dept_name`   VARCHAR(50)  NOT NULL COMMENT '部门名称',
    `sort`        INT          DEFAULT 0 COMMENT '显示顺序',
    `leader`      VARCHAR(50)  DEFAULT NULL COMMENT '负责人',
    `phone`       VARCHAR(20)  DEFAULT NULL COMMENT '联系电话',
    `email`       VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
    `status`      TINYINT      DEFAULT 1 COMMENT '状态（1正常 0停用）',
    `del_flag`    TINYINT      DEFAULT 0 COMMENT '删除标志（0存在 1删除）',
    `create_by`   VARCHAR(64)  DEFAULT '' COMMENT '创建者',
    `create_time` DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`   VARCHAR(64)  DEFAULT '' COMMENT '更新者',
    `update_time` DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_parent_id` (`parent_id`)
) ENGINE=InnoDB COMMENT='部门表';

-- ----------------------------------------
-- 2. 用户表
-- ----------------------------------------
CREATE TABLE `sys_user` (
    `id`          BIGINT       NOT NULL COMMENT '用户ID',
    `username`    VARCHAR(50)  NOT NULL COMMENT '用户账号',
    `password`    VARCHAR(200) NOT NULL COMMENT '密码',
    `nickname`    VARCHAR(50)  DEFAULT '' COMMENT '用户昵称',
    `email`       VARCHAR(100) DEFAULT '' COMMENT '邮箱',
    `phone`       VARCHAR(20)  DEFAULT '' COMMENT '手机号',
    `avatar`      VARCHAR(500) DEFAULT '' COMMENT '头像地址',
    `sex`         TINYINT      DEFAULT 0 COMMENT '性别（0未知 1男 2女）',
    `status`      TINYINT      DEFAULT 1 COMMENT '状态（1正常 0停用）',
    `del_flag`    TINYINT      DEFAULT 0 COMMENT '删除标志（0存在 1删除）',
    `dept_id`     BIGINT       DEFAULT NULL COMMENT '部门ID',
    `create_by`   VARCHAR(64)  DEFAULT '' COMMENT '创建者',
    `create_time` DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`   VARCHAR(64)  DEFAULT '' COMMENT '更新者',
    `update_time` DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`),
    KEY `idx_dept_id` (`dept_id`)
) ENGINE=InnoDB COMMENT='用户表';

-- ----------------------------------------
-- 3. 角色表
-- ----------------------------------------
CREATE TABLE `sys_role` (
    `id`          BIGINT       NOT NULL COMMENT '角色ID',
    `role_name`   VARCHAR(50)  NOT NULL COMMENT '角色名称',
    `role_key`    VARCHAR(100) NOT NULL COMMENT '角色权限标识',
    `sort`        INT          DEFAULT 0 COMMENT '显示顺序',
    `status`      TINYINT      DEFAULT 1 COMMENT '状态（1正常 0停用）',
    `remark`      VARCHAR(500) DEFAULT NULL COMMENT '备注',
    `del_flag`    TINYINT      DEFAULT 0 COMMENT '删除标志（0存在 1删除）',
    `create_by`   VARCHAR(64)  DEFAULT '' COMMENT '创建者',
    `create_time` DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`   VARCHAR(64)  DEFAULT '' COMMENT '更新者',
    `update_time` DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_role_key` (`role_key`)
) ENGINE=InnoDB COMMENT='角色表';

-- ----------------------------------------
-- 4. 菜单表
-- ----------------------------------------
CREATE TABLE `sys_menu` (
    `id`          BIGINT       NOT NULL COMMENT '菜单ID',
    `parent_id`   BIGINT       DEFAULT 0 COMMENT '父菜单ID',
    `menu_name`   VARCHAR(50)  NOT NULL COMMENT '菜单名称',
    `path`        VARCHAR(200) DEFAULT '' COMMENT '路由地址',
    `component`   VARCHAR(255) DEFAULT NULL COMMENT '组件路径',
    `menu_type`   CHAR(1)      DEFAULT '' COMMENT '菜单类型（M目录 C菜单 B按钮）',
    `perms`       VARCHAR(100) DEFAULT NULL COMMENT '权限标识',
    `icon`        VARCHAR(100) DEFAULT '#' COMMENT '菜单图标',
    `sort`        INT          DEFAULT 0 COMMENT '显示顺序',
    `status`      TINYINT      DEFAULT 1 COMMENT '状态（1正常 0停用）',
    `visible`     TINYINT      DEFAULT 1 COMMENT '是否可见（1显示 0隐藏）',
    `del_flag`    TINYINT      DEFAULT 0 COMMENT '删除标志（0存在 1删除）',
    `create_by`   VARCHAR(64)  DEFAULT '' COMMENT '创建者',
    `create_time` DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`   VARCHAR(64)  DEFAULT '' COMMENT '更新者',
    `update_time` DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_parent_id` (`parent_id`)
) ENGINE=InnoDB COMMENT='菜单权限表';

-- ----------------------------------------
-- 5. 岗位表
-- ----------------------------------------
CREATE TABLE `sys_post` (
    `id`          BIGINT       NOT NULL COMMENT '岗位ID',
    `post_code`   VARCHAR(64)  NOT NULL COMMENT '岗位编码',
    `post_name`   VARCHAR(50)  NOT NULL COMMENT '岗位名称',
    `sort`        INT          DEFAULT 0 COMMENT '显示顺序',
    `status`      TINYINT      DEFAULT 1 COMMENT '状态（1正常 0停用）',
    `remark`      VARCHAR(500) DEFAULT NULL COMMENT '备注',
    `del_flag`    TINYINT      DEFAULT 0 COMMENT '删除标志（0存在 1删除）',
    `create_by`   VARCHAR(64)  DEFAULT '' COMMENT '创建者',
    `create_time` DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`   VARCHAR(64)  DEFAULT '' COMMENT '更新者',
    `update_time` DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_post_code` (`post_code`)
) ENGINE=InnoDB COMMENT='岗位表';

-- ----------------------------------------
-- 6. 字典类型表
-- ----------------------------------------
CREATE TABLE `sys_dict_type` (
    `id`          BIGINT       NOT NULL COMMENT '字典类型ID',
    `dict_name`   VARCHAR(100) NOT NULL COMMENT '字典名称',
    `dict_type`   VARCHAR(100) NOT NULL COMMENT '字典类型',
    `status`      TINYINT      DEFAULT 1 COMMENT '状态（1正常 0停用）',
    `remark`      VARCHAR(500) DEFAULT NULL COMMENT '备注',
    `del_flag`    TINYINT      DEFAULT 0 COMMENT '删除标志（0存在 1删除）',
    `create_by`   VARCHAR(64)  DEFAULT '' COMMENT '创建者',
    `create_time` DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`   VARCHAR(64)  DEFAULT '' COMMENT '更新者',
    `update_time` DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_dict_type` (`dict_type`)
) ENGINE=InnoDB COMMENT='字典类型表';

-- ----------------------------------------
-- 7. 字典数据表
-- ----------------------------------------
CREATE TABLE `sys_dict_data` (
    `id`          BIGINT       NOT NULL COMMENT '字典数据ID',
    `dict_type`   VARCHAR(100) NOT NULL COMMENT '字典类型',
    `dict_label`  VARCHAR(100) NOT NULL COMMENT '字典标签',
    `dict_value`  VARCHAR(100) NOT NULL COMMENT '字典值',
    `sort`        INT          DEFAULT 0 COMMENT '显示顺序',
    `status`      TINYINT      DEFAULT 1 COMMENT '状态（1正常 0停用）',
    `css_class`   VARCHAR(100) DEFAULT NULL COMMENT '样式属性',
    `remark`      VARCHAR(500) DEFAULT NULL COMMENT '备注',
    `del_flag`    TINYINT      DEFAULT 0 COMMENT '删除标志（0存在 1删除）',
    `create_by`   VARCHAR(64)  DEFAULT '' COMMENT '创建者',
    `create_time` DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`   VARCHAR(64)  DEFAULT '' COMMENT '更新者',
    `update_time` DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_dict_type` (`dict_type`)
) ENGINE=InnoDB COMMENT='字典数据表';

-- ----------------------------------------
-- 8. 用户角色关联表
-- ----------------------------------------
CREATE TABLE `sys_user_role` (
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `role_id` BIGINT NOT NULL COMMENT '角色ID',
    PRIMARY KEY (`user_id`, `role_id`),
    KEY `idx_role_id` (`role_id`)
) ENGINE=InnoDB COMMENT='用户和角色关联表';

-- ----------------------------------------
-- 9. 角色菜单关联表
-- ----------------------------------------
CREATE TABLE `sys_role_menu` (
    `role_id` BIGINT NOT NULL COMMENT '角色ID',
    `menu_id` BIGINT NOT NULL COMMENT '菜单ID',
    PRIMARY KEY (`role_id`, `menu_id`),
    KEY `idx_menu_id` (`menu_id`)
) ENGINE=InnoDB COMMENT='角色和菜单关联表';

-- ----------------------------------------
-- 10. 用户岗位关联表
-- ----------------------------------------
CREATE TABLE `sys_user_post` (
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `post_id` BIGINT NOT NULL COMMENT '岗位ID',
    PRIMARY KEY (`user_id`, `post_id`),
    KEY `idx_post_id` (`post_id`)
) ENGINE=InnoDB COMMENT='用户岗位关联表';

-- ----------------------------------------
-- 11. 操作日志表
-- ----------------------------------------
CREATE TABLE `sys_oper_log` (
    `id`              BIGINT        NOT NULL COMMENT '日志ID',
    `title`           VARCHAR(50)   DEFAULT '' COMMENT '模块标题',
    `method`          VARCHAR(200)  DEFAULT '' COMMENT '方法名称',
    `request_url`     VARCHAR(500)  DEFAULT '' COMMENT '请求URL',
    `request_param`   TEXT          DEFAULT NULL COMMENT '请求参数',
    `response_result` TEXT          DEFAULT NULL COMMENT '返回结果',
    `status`          TINYINT       DEFAULT 0 COMMENT '操作状态（1正常 0失败）',
    `error_msg`       VARCHAR(2000) DEFAULT NULL COMMENT '错误消息',
    `oper_ip`         VARCHAR(128)  DEFAULT '' COMMENT '操作IP',
    `oper_name`       VARCHAR(50)   DEFAULT '' COMMENT '操作人',
    `oper_time`       DATETIME      DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
    `cost_time`       BIGINT        DEFAULT 0 COMMENT '耗时(ms)',
    PRIMARY KEY (`id`),
    KEY `idx_oper_time` (`oper_time`),
    KEY `idx_oper_name` (`oper_name`)
) ENGINE=InnoDB COMMENT='操作日志记录';


-- ========================================
-- 业务模块表
-- ========================================

-- ----------------------------------------
-- 12. 流程定义表
-- ----------------------------------------
CREATE TABLE `wf_process_def` (
    `id`            BIGINT        NOT NULL COMMENT '流程定义ID',
    `process_key`   VARCHAR(100)  NOT NULL COMMENT '流程标识',
    `process_name`  VARCHAR(200)  NOT NULL COMMENT '流程名称',
    `version`       INT           DEFAULT 1 COMMENT '版本号',
    `category`      VARCHAR(100)  DEFAULT '' COMMENT '流程分类',
    `bpmn_xml`      MEDIUMTEXT    DEFAULT NULL COMMENT 'BPMN XML',
    `description`   VARCHAR(500)  DEFAULT '' COMMENT '描述',
    `status`        TINYINT       DEFAULT 1 COMMENT '状态（1已发布 0未发布 2已挂起）',
    `del_flag`      TINYINT       DEFAULT 0 COMMENT '删除标志',
    `create_by`     VARCHAR(64)   DEFAULT '' COMMENT '创建者',
    `create_time`   DATETIME      DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`     VARCHAR(64)   DEFAULT '' COMMENT '更新者',
    `update_time`   DATETIME      DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_process_key_version` (`process_key`, `version`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB COMMENT='流程定义表';

-- ----------------------------------------
-- 13. 流程实例表
-- ----------------------------------------
CREATE TABLE `wf_process_inst` (
    `id`              BIGINT        NOT NULL COMMENT '流程实例ID',
    `process_def_id`  BIGINT        NOT NULL COMMENT '流程定义ID',
    `process_key`     VARCHAR(100)  NOT NULL COMMENT '流程标识',
    `process_name`    VARCHAR(200)  NOT NULL COMMENT '流程名称',
    `business_key`    VARCHAR(200)  DEFAULT '' COMMENT '业务标识',
    `start_user_id`   BIGINT        NOT NULL COMMENT '发起人ID',
    `start_user_name` VARCHAR(50)   DEFAULT '' COMMENT '发起人名称',
    `status`          VARCHAR(20)   DEFAULT 'running' COMMENT '状态（running/completed/terminated/suspended）',
    `start_time`      DATETIME      DEFAULT CURRENT_TIMESTAMP COMMENT '开始时间',
    `end_time`        DATETIME      DEFAULT NULL COMMENT '结束时间',
    `duration`        BIGINT        DEFAULT NULL COMMENT '耗时(ms)',
    `variables`       TEXT          DEFAULT NULL COMMENT '流程变量JSON',
    `del_flag`        TINYINT       DEFAULT 0 COMMENT '删除标志',
    `create_time`     DATETIME      DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`     DATETIME      DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_process_key` (`process_key`),
    KEY `idx_start_user` (`start_user_id`),
    KEY `idx_status` (`status`),
    KEY `idx_business_key` (`business_key`)
) ENGINE=InnoDB COMMENT='流程实例表';

-- ----------------------------------------
-- 14. 流程节点审批记录表
-- ----------------------------------------
CREATE TABLE `wf_approval_record` (
    `id`                BIGINT        NOT NULL COMMENT '记录ID',
    `process_inst_id`   BIGINT        NOT NULL COMMENT '流程实例ID',
    `node_id`           VARCHAR(100)  NOT NULL COMMENT '节点ID',
    `node_name`         VARCHAR(200)  DEFAULT '' COMMENT '节点名称',
    `node_type`         VARCHAR(50)   DEFAULT '' COMMENT '节点类型（userTask/serviceTask/startEvent/endEvent）',
    `assignee_id`       BIGINT        DEFAULT NULL COMMENT '办理人ID',
    `assignee_name`     VARCHAR(50)   DEFAULT '' COMMENT '办理人名称',
    `action`            VARCHAR(20)   DEFAULT '' COMMENT '操作（agree/reject/delegate/submit）',
    `comment`           VARCHAR(500)  DEFAULT '' COMMENT '审批意见',
    `status`            VARCHAR(20)   DEFAULT 'pending' COMMENT '状态（pending/approved/rejected）',
    `start_time`        DATETIME      DEFAULT NULL COMMENT '开始时间',
    `end_time`          DATETIME      DEFAULT NULL COMMENT '结束时间',
    `create_time`       DATETIME      DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_process_inst` (`process_inst_id`),
    KEY `idx_assignee` (`assignee_id`)
) ENGINE=InnoDB COMMENT='流程审批记录表';

-- ----------------------------------------
-- 15. 任务表
-- ----------------------------------------
CREATE TABLE `biz_task` (
    `id`          BIGINT       NOT NULL COMMENT '任务ID',
    `title`       VARCHAR(200) NOT NULL COMMENT '任务标题',
    `description` VARCHAR(1000) DEFAULT '' COMMENT '任务描述',
    `priority`    VARCHAR(20)  DEFAULT 'medium' COMMENT '优先级（low/medium/high/urgent）',
    `status`      VARCHAR(20)  DEFAULT 'todo' COMMENT '状态（todo/progress/done/archive）',
    `assignee_id` BIGINT       DEFAULT NULL COMMENT '负责人ID',
    `assignee`    VARCHAR(50)  DEFAULT '' COMMENT '负责人名称',
    `due_date`    DATE         DEFAULT NULL COMMENT '截止日期',
    `tags`        VARCHAR(500) DEFAULT '' COMMENT '标签（逗号分隔）',
    `sort`        INT          DEFAULT 0 COMMENT '排序',
    `del_flag`    TINYINT      DEFAULT 0 COMMENT '删除标志',
    `create_by`   VARCHAR(64)  DEFAULT '' COMMENT '创建者',
    `create_time` DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`   VARCHAR(64)  DEFAULT '' COMMENT '更新者',
    `update_time` DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_status` (`status`),
    KEY `idx_assignee` (`assignee_id`),
    KEY `idx_due_date` (`due_date`)
) ENGINE=InnoDB COMMENT='任务表';

-- ----------------------------------------
-- 16. 考勤记录表
-- ----------------------------------------
CREATE TABLE `biz_attendance` (
    `id`          BIGINT       NOT NULL COMMENT '记录ID',
    `user_id`     BIGINT       NOT NULL COMMENT '用户ID',
    `user_name`   VARCHAR(50)  DEFAULT '' COMMENT '用户名',
    `check_date`  DATE         NOT NULL COMMENT '打卡日期',
    `check_in`    DATETIME     DEFAULT NULL COMMENT '上班打卡时间',
    `check_out`   DATETIME     DEFAULT NULL COMMENT '下班打卡时间',
    `status`      VARCHAR(20)  DEFAULT 'normal' COMMENT '状态（normal/late/early_leave/absent/leave）',
    `work_hours`  DECIMAL(4,1) DEFAULT NULL COMMENT '工时',
    `remark`      VARCHAR(500) DEFAULT '' COMMENT '备注',
    `create_time` DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_date` (`user_id`, `check_date`),
    KEY `idx_check_date` (`check_date`)
) ENGINE=InnoDB COMMENT='考勤记录表';

-- ----------------------------------------
-- 17. 文档表
-- ----------------------------------------
CREATE TABLE `biz_document` (
    `id`          BIGINT        NOT NULL COMMENT '文档ID',
    `file_name`   VARCHAR(300)  NOT NULL COMMENT '文件名',
    `file_type`   VARCHAR(20)   DEFAULT '' COMMENT '文件类型（image/document/spreadsheet/pdf/other）',
    `file_size`   BIGINT        DEFAULT 0 COMMENT '文件大小（字节）',
    `folder_id`   BIGINT        DEFAULT NULL COMMENT '文件夹ID',
    `file_url`    VARCHAR(500)  DEFAULT '' COMMENT '文件URL',
    `storage_path` VARCHAR(500) DEFAULT '' COMMENT '存储路径',
    `upload_by`   BIGINT        DEFAULT NULL COMMENT '上传人ID',
    `upload_name` VARCHAR(50)   DEFAULT '' COMMENT '上传人名称',
    `del_flag`    TINYINT       DEFAULT 0 COMMENT '删除标志',
    `create_time` DATETIME      DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME      DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_upload_by` (`upload_by`),
    KEY `idx_file_type` (`file_type`)
) ENGINE=InnoDB COMMENT='文档表';

-- ----------------------------------------
-- 18. 会议室表
-- ----------------------------------------
CREATE TABLE `biz_meeting_room` (
    `id`          BIGINT       NOT NULL COMMENT '会议室ID',
    `name`        VARCHAR(100) NOT NULL COMMENT '会议室名称',
    `capacity`    INT          DEFAULT 0 COMMENT '容纳人数',
    `equipment`   VARCHAR(500) DEFAULT '' COMMENT '设备（逗号分隔）',
    `location`    VARCHAR(200) DEFAULT '' COMMENT '位置',
    `status`      TINYINT      DEFAULT 1 COMMENT '状态（1可用 0维护中）',
    `del_flag`    TINYINT      DEFAULT 0 COMMENT '删除标志',
    `create_by`   VARCHAR(64)  DEFAULT '' COMMENT '创建者',
    `create_time` DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`   VARCHAR(64)  DEFAULT '' COMMENT '更新者',
    `update_time` DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB COMMENT='会议室表';

-- ----------------------------------------
-- 19. 会议室预约表
-- ----------------------------------------
CREATE TABLE `biz_meeting_booking` (
    `id`            BIGINT        NOT NULL COMMENT '预约ID',
    `room_id`       BIGINT        NOT NULL COMMENT '会议室ID',
    `booking_date`  DATE          NOT NULL COMMENT '预约日期',
    `start_time`    VARCHAR(5)    NOT NULL COMMENT '开始时间（HH:mm）',
    `end_time`      VARCHAR(5)    NOT NULL COMMENT '结束时间（HH:mm）',
    `title`         VARCHAR(200)  NOT NULL COMMENT '会议主题',
    `organizer_id`  BIGINT        DEFAULT NULL COMMENT '组织者ID',
    `organizer`     VARCHAR(50)   DEFAULT '' COMMENT '组织者名称',
    `attendees`     VARCHAR(500)  DEFAULT '' COMMENT '参会人',
    `remark`        VARCHAR(500)  DEFAULT '' COMMENT '备注',
    `del_flag`      TINYINT       DEFAULT 0 COMMENT '删除标志',
    `create_by`     VARCHAR(64)   DEFAULT '' COMMENT '创建者',
    `create_time`   DATETIME      DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`     VARCHAR(64)   DEFAULT '' COMMENT '更新者',
    `update_time`   DATETIME      DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_room_date` (`room_id`, `booking_date`),
    KEY `idx_booking_date` (`booking_date`)
) ENGINE=InnoDB COMMENT='会议室预约表';

-- ----------------------------------------
-- 20-bis. 会议纪要表
-- ----------------------------------------
CREATE TABLE `biz_meeting_minutes` (
    `id`            BIGINT        NOT NULL COMMENT '纪要ID',
    `booking_id`    BIGINT        DEFAULT NULL COMMENT '关联预约ID',
    `title`         VARCHAR(200)  NOT NULL COMMENT '会议主题',
    `room_id`       BIGINT        DEFAULT NULL COMMENT '会议室ID',
    `room_name`     VARCHAR(100)  DEFAULT '' COMMENT '会议室名称',
    `meeting_date`  DATE          NOT NULL COMMENT '会议日期',
    `start_time`    VARCHAR(5)    DEFAULT '' COMMENT '开始时间',
    `end_time`      VARCHAR(5)    DEFAULT '' COMMENT '结束时间',
    `organizer_id`  BIGINT        DEFAULT NULL COMMENT '组织者ID',
    `organizer`     VARCHAR(50)   DEFAULT '' COMMENT '组织者',
    `attendees`     VARCHAR(500)  DEFAULT '' COMMENT '参会人',
    `content`       TEXT          DEFAULT NULL COMMENT '纪要内容',
    `action_items`  TEXT          DEFAULT NULL COMMENT '待办事项JSON',
    `recorder_id`   BIGINT        DEFAULT NULL COMMENT '记录人ID',
    `recorder_name` VARCHAR(50)   DEFAULT '' COMMENT '记录人',
    `del_flag`      TINYINT       DEFAULT 0 COMMENT '删除标志',
    `create_by`     VARCHAR(64)   DEFAULT '' COMMENT '创建者',
    `create_time`   DATETIME      DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`     VARCHAR(64)   DEFAULT '' COMMENT '更新者',
    `update_time`   DATETIME      DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_booking_id` (`booking_id`),
    KEY `idx_meeting_date` (`meeting_date`)
) ENGINE=InnoDB COMMENT='会议纪要表';

-- ----------------------------------------
-- 20. 通知表
-- ----------------------------------------
CREATE TABLE `biz_notification` (
    `id`          BIGINT        NOT NULL COMMENT '通知ID',
    `title`       VARCHAR(200)  NOT NULL COMMENT '通知标题',
    `content`     VARCHAR(2000) DEFAULT '' COMMENT '通知内容',
    `type`        VARCHAR(20)   DEFAULT 'system' COMMENT '类型（system/workflow/task/meeting/attendance）',
    `user_id`     BIGINT        NOT NULL COMMENT '接收人ID',
    `is_read`     TINYINT       DEFAULT 0 COMMENT '是否已读（0未读 1已读）',
    `read_time`   DATETIME      DEFAULT NULL COMMENT '阅读时间',
    `del_flag`    TINYINT       DEFAULT 0 COMMENT '删除标志',
    `create_time` DATETIME      DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_read` (`user_id`, `is_read`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB COMMENT='通知表';


-- ----------------------------------------
-- 21. 流程定义表（增强）
-- ----------------------------------------
CREATE TABLE `wf_flow_definition` (
    `id`              BIGINT        NOT NULL COMMENT '主键ID',
    `name`            VARCHAR(200)  NOT NULL COMMENT '流程名称',
    `key`             VARCHAR(100)  NOT NULL COMMENT '流程标识',
    `version`         INT           DEFAULT 1 COMMENT '版本号',
    `category`        VARCHAR(100)  DEFAULT NULL COMMENT '流程分类',
    `description`     VARCHAR(1000) DEFAULT NULL COMMENT '流程描述',
    `xml_content`     MEDIUMTEXT    DEFAULT NULL COMMENT 'BPMN XML内容',
    `deployment_id`   VARCHAR(100)  DEFAULT NULL COMMENT 'Flowable部署ID',
    `process_def_id`  VARCHAR(200)  DEFAULT NULL COMMENT 'Flowable流程定义ID',
    `status`          TINYINT       DEFAULT 1 COMMENT '状态（1正常 0挂起）',
    `del_flag`        TINYINT       DEFAULT 0 COMMENT '删除标志（0存在 1删除）',
    `create_by`       VARCHAR(64)   DEFAULT '' COMMENT '创建者',
    `create_time`     DATETIME      DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`       VARCHAR(64)   DEFAULT '' COMMENT '更新者',
    `update_time`     DATETIME      DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_key` (`key`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB COMMENT='流程定义表';

-- ----------------------------------------
-- 22. 流程实例表（增强）
-- ----------------------------------------
CREATE TABLE `wf_flow_instance` (
    `id`                    BIGINT        NOT NULL COMMENT '主键ID',
    `process_definition_id` VARCHAR(200)  NOT NULL COMMENT '流程定义ID',
    `process_instance_id`   VARCHAR(200)  DEFAULT NULL COMMENT 'Flowable流程实例ID',
    `definition_id`         BIGINT        DEFAULT NULL COMMENT '本地流程定义ID',
    `business_key`          VARCHAR(200)  DEFAULT NULL COMMENT '业务主键',
    `title`                 VARCHAR(500)  DEFAULT NULL COMMENT '流程标题',
    `start_user_id`         BIGINT        NOT NULL COMMENT '发起人ID',
    `start_user_name`       VARCHAR(50)   DEFAULT '' COMMENT '发起人姓名',
    `start_time`            DATETIME      NOT NULL COMMENT '开始时间',
    `end_time`              DATETIME      DEFAULT NULL COMMENT '结束时间',
    `status`                TINYINT       DEFAULT 1 COMMENT '状态（1进行中 2已完成 3已终止 4已挂起）',
    `variables`             TEXT          DEFAULT NULL COMMENT '流程变量JSON',
    `current_node`          VARCHAR(200)  DEFAULT NULL COMMENT '当前节点名称',
    `del_flag`              TINYINT       DEFAULT 0 COMMENT '删除标志（0存在 1删除）',
    `create_by`             VARCHAR(64)   DEFAULT '' COMMENT '创建者',
    `create_time`           DATETIME      DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`             VARCHAR(64)   DEFAULT '' COMMENT '更新者',
    `update_time`           DATETIME      DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_process_instance_id` (`process_instance_id`),
    KEY `idx_start_user_id` (`start_user_id`),
    KEY `idx_status` (`status`),
    KEY `idx_definition_id` (`definition_id`)
) ENGINE=InnoDB COMMENT='流程实例表';

-- ----------------------------------------
-- 23. 流程任务表
-- ----------------------------------------
CREATE TABLE `wf_flow_task` (
    `id`                    BIGINT        NOT NULL COMMENT '主键ID',
    `task_id`               VARCHAR(200)  NOT NULL COMMENT 'Flowable任务ID',
    `process_instance_id`   VARCHAR(200)  NOT NULL COMMENT '流程实例ID',
    `task_name`             VARCHAR(200)  NOT NULL COMMENT '任务名称',
    `task_key`              VARCHAR(200)  DEFAULT NULL COMMENT '任务标识',
    `assignee`              BIGINT        DEFAULT NULL COMMENT '处理人ID',
    `assignee_name`         VARCHAR(50)   DEFAULT '' COMMENT '处理人姓名',
    `candidate_users`       VARCHAR(500)  DEFAULT NULL COMMENT '候选人ID列表',
    `candidate_groups`      VARCHAR(500)  DEFAULT NULL COMMENT '候选组列表',
    `create_time`           DATETIME      NOT NULL COMMENT '创建时间',
    `claim_time`            DATETIME      DEFAULT NULL COMMENT '签收时间',
    `due_time`              DATETIME      DEFAULT NULL COMMENT '截止时间',
    `complete_time`         DATETIME      DEFAULT NULL COMMENT '完成时间',
    `priority`              INT           DEFAULT 50 COMMENT '优先级（0-100）',
    `status`                TINYINT       DEFAULT 0 COMMENT '状态（0待签收 1处理中 2已完成 3已驳回 4已转办 5已委派）',
    `comment`               VARCHAR(1000) DEFAULT NULL COMMENT '审批意见',
    `del_flag`              TINYINT       DEFAULT 0 COMMENT '删除标志（0存在 1删除）',
    `create_by`             VARCHAR(64)   DEFAULT '' COMMENT '创建者',
    `update_time`           DATETIME      DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_task_id` (`task_id`),
    KEY `idx_process_instance_id` (`process_instance_id`),
    KEY `idx_assignee` (`assignee`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB COMMENT='流程任务表';

-- ----------------------------------------
-- 24. 考勤记录表（增强）
-- ----------------------------------------
CREATE TABLE `attendance_record` (
    `id`                BIGINT        NOT NULL COMMENT '主键ID',
    `user_id`           BIGINT        NOT NULL COMMENT '用户ID',
    `user_name`         VARCHAR(50)   DEFAULT '' COMMENT '用户姓名',
    `clock_in_time`     DATETIME      DEFAULT NULL COMMENT '上班打卡时间',
    `clock_out_time`    DATETIME      DEFAULT NULL COMMENT '下班打卡时间',
    `clock_in_ip`       VARCHAR(128)  DEFAULT NULL COMMENT '上班打卡IP',
    `clock_out_ip`      VARCHAR(128)  DEFAULT NULL COMMENT '下班打卡IP',
    `clock_in_location` VARCHAR(500)  DEFAULT NULL COMMENT '上班打卡地点',
    `clock_out_location`VARCHAR(500)  DEFAULT NULL COMMENT '下班打卡地点',
    `status`            TINYINT       DEFAULT 0 COMMENT '状态（0正常 1迟到 2早退 3缺卡 4异常）',
    `work_date`         DATE          NOT NULL COMMENT '工作日期',
    `work_hours`        DECIMAL(5,2)  DEFAULT 0 COMMENT '工作时长(小时)',
    `remark`            VARCHAR(500)  DEFAULT NULL COMMENT '备注',
    `del_flag`          TINYINT       DEFAULT 0 COMMENT '删除标志（0存在 1删除）',
    `create_by`         VARCHAR(64)   DEFAULT '' COMMENT '创建者',
    `create_time`       DATETIME      DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`         VARCHAR(64)   DEFAULT '' COMMENT '更新者',
    `update_time`       DATETIME      DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_work_date` (`work_date`),
    KEY `idx_user_date` (`user_id`, `work_date`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB COMMENT='考勤记录表';

-- ----------------------------------------
-- 25. 考勤班次表
-- ----------------------------------------
CREATE TABLE `attendance_schedule` (
    `id`                BIGINT        NOT NULL COMMENT '主键ID',
    `user_id`           BIGINT        NOT NULL COMMENT '用户ID',
    `schedule_type`     TINYINT       DEFAULT 1 COMMENT '班次类型（1固定 2弹性 3轮班）',
    `work_start_time`   TIME          NOT NULL COMMENT '上班时间',
    `work_end_time`     TIME          NOT NULL COMMENT '下班时间',
    `rest_start_time`   TIME          DEFAULT NULL COMMENT '休息开始时间',
    `rest_end_time`     TIME          DEFAULT NULL COMMENT '休息结束时间',
    `effective_date`    DATE          NOT NULL COMMENT '生效日期',
    `status`            TINYINT       DEFAULT 1 COMMENT '状态（1正常 0停用）',
    `del_flag`          TINYINT       DEFAULT 0 COMMENT '删除标志（0存在 1删除）',
    `create_by`         VARCHAR(64)   DEFAULT '' COMMENT '创建者',
    `create_time`       DATETIME      DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`         VARCHAR(64)   DEFAULT '' COMMENT '更新者',
    `update_time`       DATETIME      DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_effective_date` (`effective_date`)
) ENGINE=InnoDB COMMENT='考勤班次表';

-- ----------------------------------------
-- 26. 假期余额表
-- ----------------------------------------
CREATE TABLE `leave_balance` (
    `id`                BIGINT        NOT NULL COMMENT '主键ID',
    `user_id`           BIGINT        NOT NULL COMMENT '用户ID',
    `leave_type`        VARCHAR(20)   NOT NULL COMMENT '假期类型（annual年假 sick病假 personal事假 marriage婚假）',
    `total_days`        DECIMAL(5,1)  DEFAULT 0 COMMENT '总天数',
    `used_days`         DECIMAL(5,1)  DEFAULT 0 COMMENT '已用天数',
    `remain_days`       DECIMAL(5,1)  DEFAULT 0 COMMENT '剩余天数',
    `year`              INT           NOT NULL COMMENT '年份',
    `del_flag`          TINYINT       DEFAULT 0 COMMENT '删除标志（0存在 1删除）',
    `create_by`         VARCHAR(64)   DEFAULT '' COMMENT '创建者',
    `create_time`       DATETIME      DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`         VARCHAR(64)   DEFAULT '' COMMENT '更新者',
    `update_time`       DATETIME      DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_year` (`year`),
    KEY `idx_user_year_type` (`user_id`, `year`, `leave_type`)
) ENGINE=InnoDB COMMENT='假期余额表';

-- ----------------------------------------
-- 27. 文档文件夹表
-- ----------------------------------------
CREATE TABLE `document_folder` (
    `id`              BIGINT        NOT NULL COMMENT '主键ID',
    `name`            VARCHAR(200)  NOT NULL COMMENT '文件夹名称',
    `parent_id`       BIGINT        DEFAULT 0 COMMENT '父文件夹ID',
    `path`            VARCHAR(1000) DEFAULT '/' COMMENT '路径',
    `creator_id`      BIGINT        NOT NULL COMMENT '创建人ID',
    `del_flag`        TINYINT       DEFAULT 0 COMMENT '删除标志（0存在 1删除）',
    `create_by`       VARCHAR(64)   DEFAULT '' COMMENT '创建者',
    `create_time`     DATETIME      DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`       VARCHAR(64)   DEFAULT '' COMMENT '更新者',
    `update_time`     DATETIME      DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_parent_id` (`parent_id`),
    KEY `idx_creator_id` (`creator_id`)
) ENGINE=InnoDB COMMENT='文档文件夹表';

-- ----------------------------------------
-- 28. 文档版本表
-- ----------------------------------------
CREATE TABLE `document_version` (
    `id`              BIGINT        NOT NULL COMMENT '主键ID',
    `document_id`     BIGINT        NOT NULL COMMENT '文档ID',
    `version`         INT           NOT NULL COMMENT '版本号',
    `file_path`       VARCHAR(1000) NOT NULL COMMENT '存储路径',
    `file_size`       BIGINT        DEFAULT 0 COMMENT '文件大小(字节)',
    `uploader_id`     BIGINT        NOT NULL COMMENT '上传人ID',
    `uploader_name`   VARCHAR(50)   DEFAULT '' COMMENT '上传人姓名',
    `upload_time`     DATETIME      DEFAULT CURRENT_TIMESTAMP COMMENT '上传时间',
    `remark`          VARCHAR(500)  DEFAULT NULL COMMENT '版本说明',
    `del_flag`        TINYINT       DEFAULT 0 COMMENT '删除标志（0存在 1删除）',
    `create_by`       VARCHAR(64)   DEFAULT '' COMMENT '创建者',
    `create_time`     DATETIME      DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_document_id` (`document_id`),
    KEY `idx_version` (`document_id`, `version`)
) ENGINE=InnoDB COMMENT='文档版本表';

-- ----------------------------------------
-- 29. 资产分类表
-- ----------------------------------------
CREATE TABLE `biz_asset_category` (
    `id`          BIGINT       NOT NULL COMMENT '分类ID',
    `name`        VARCHAR(100) NOT NULL COMMENT '分类名称',
    `parent_id`   BIGINT       DEFAULT 0 COMMENT '父分类ID',
    `description` VARCHAR(500) DEFAULT '' COMMENT '描述',
    `status`      TINYINT      DEFAULT 1 COMMENT '状态（1正常 0停用）',
    `del_flag`    TINYINT      DEFAULT 0 COMMENT '删除标志（0存在 1删除）',
    `create_by`   VARCHAR(64)  DEFAULT '' COMMENT '创建者',
    `create_time` DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`   VARCHAR(64)  DEFAULT '' COMMENT '更新者',
    `update_time` DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_parent_id` (`parent_id`)
) ENGINE=InnoDB COMMENT='资产分类表';

-- ----------------------------------------
-- 30. 资产表
-- ----------------------------------------
CREATE TABLE `biz_asset` (
    `id`                 BIGINT        NOT NULL COMMENT '资产ID',
    `asset_code`         VARCHAR(50)   NOT NULL COMMENT '资产编码',
    `name`               VARCHAR(200)  NOT NULL COMMENT '资产名称',
    `category_id`        BIGINT        DEFAULT NULL COMMENT '分类ID',
    `category_name`      VARCHAR(100)  DEFAULT '' COMMENT '分类名称',
    `specification`      VARCHAR(200)  DEFAULT '' COMMENT '规格型号',
    `unit`               VARCHAR(20)   DEFAULT '' COMMENT '单位',
    `brand`              VARCHAR(100)  DEFAULT '' COMMENT '品牌',
    `purchase_date`      DATE          DEFAULT NULL COMMENT '采购日期',
    `purchase_price`     DECIMAL(12,2) DEFAULT 0 COMMENT '采购价格',
    `current_value`      DECIMAL(12,2) DEFAULT 0 COMMENT '当前价值',
    `depreciation_method` VARCHAR(20)  DEFAULT 'straight' COMMENT '折旧方法（straight直线/double双倍）',
    `depreciation_rate`  DECIMAL(5,2)  DEFAULT 0 COMMENT '年折旧率(%)',
    `location`           VARCHAR(200)  DEFAULT '' COMMENT '存放位置',
    `user_id`            BIGINT        DEFAULT NULL COMMENT '使用人ID',
    `user_name`          VARCHAR(50)   DEFAULT '' COMMENT '使用人姓名',
    `status`             TINYINT       DEFAULT 0 COMMENT '状态（0闲置 1在用 2维修 3报废）',
    `supplier`           VARCHAR(200)  DEFAULT '' COMMENT '供应商',
    `warranty_date`      DATE          DEFAULT NULL COMMENT '质保到期',
    `remark`             VARCHAR(500)  DEFAULT '' COMMENT '备注',
    `del_flag`           TINYINT       DEFAULT 0 COMMENT '删除标志（0存在 1删除）',
    `create_by`          VARCHAR(64)   DEFAULT '' COMMENT '创建者',
    `create_time`        DATETIME      DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`          VARCHAR(64)   DEFAULT '' COMMENT '更新者',
    `update_time`        DATETIME      DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_asset_code` (`asset_code`),
    KEY `idx_category_id` (`category_id`),
    KEY `idx_status` (`status`),
    KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB COMMENT='资产表';

-- ----------------------------------------
-- 31. 资产流转记录表
-- ----------------------------------------
CREATE TABLE `biz_asset_record` (
    `id`            BIGINT       NOT NULL COMMENT '记录ID',
    `asset_id`      BIGINT       NOT NULL COMMENT '资产ID',
    `type`          VARCHAR(20)  NOT NULL COMMENT '类型（IN入库/ASSIGN领用/RETURN归还/REPAIR维修/SCRAP报废）',
    `operator_id`   BIGINT       DEFAULT NULL COMMENT '操作人ID',
    `operator_name` VARCHAR(50)  DEFAULT '' COMMENT '操作人姓名',
    `from_user_id`  BIGINT       DEFAULT NULL COMMENT '原使用人ID',
    `to_user_id`    BIGINT       DEFAULT NULL COMMENT '新使用人ID',
    `to_user_name`  VARCHAR(50)  DEFAULT '' COMMENT '新使用人姓名',
    `remark`        VARCHAR(500) DEFAULT '' COMMENT '备注',
    `operate_time`  DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
    PRIMARY KEY (`id`),
    KEY `idx_asset_id` (`asset_id`),
    KEY `idx_operate_time` (`operate_time`)
) ENGINE=InnoDB COMMENT='资产流转记录表';

-- ========================================
-- 初始数据
-- ========================================

-- 管理员角色
INSERT INTO `sys_role` (`id`, `role_name`, `role_key`, `sort`, `status`, `remark`) VALUES
(1, '超级管理员', 'admin', 1, 1, '超级管理员角色'),
(2, '普通角色',   'common', 2, 1, '普通角色');

-- 管理员用户（密码: admin123，BCrypt加密）
INSERT INTO `sys_user` (`id`, `username`, `password`, `nickname`, `email`, `phone`, `status`) VALUES
(1, 'admin', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '超级管理员', 'admin@smartauto.com', '13800000000', 1);

-- 管理员角色关联
INSERT INTO `sys_user_role` (`user_id`, `role_id`) VALUES (1, 1);

-- 默认部门
INSERT INTO `sys_dept` (`id`, `parent_id`, `ancestors`, `dept_name`, `sort`, `leader`, `status`) VALUES
(100, 0,   '0',     'SmartAuto科技',  0, '管理员', 1),
(101, 100, '0,100', '研发部门',       1, '张三',  1),
(102, 100, '0,100', '市场部门',       2, '李四',  1);

-- 默认岗位
INSERT INTO `sys_post` (`id`, `post_code`, `post_name`, `sort`, `status`) VALUES
(1, 'ceo',  '董事长', 1, 1),
(2, 'cto',  '技术总监', 2, 1),
(3, 'hr',   '人力资源', 3, 1),
(4, 'user', '普通员工', 4, 1);

-- 用户岗位关联
INSERT INTO `sys_user_post` (`user_id`, `post_id`) VALUES (1, 1);

-- 默认菜单（含业务模块）
INSERT INTO `sys_menu` (`id`, `parent_id`, `menu_name`, `path`, `component`, `menu_type`, `perms`, `icon`, `sort`) VALUES
-- 系统管理
(1,   0,   '系统管理', '/system',     NULL,               'M', NULL,                  'system',      1),
(2,   1,   '用户管理', 'user',        'system/user/index', 'C', 'system:user:list',    'user',        1),
(3,   1,   '角色管理', 'role',        'system/role/index', 'C', 'system:role:list',    'peoples',     2),
(4,   1,   '菜单管理', 'menu',        'system/menu/index', 'C', 'system:menu:list',    'tree-table',  3),
(5,   1,   '部门管理', 'dept',        'system/dept/index', 'C', 'system:dept:list',    'tree',        4),
(6,   1,   '岗位管理', 'post',        'system/post/index', 'C', 'system:post:list',    'post',        5),
(7,   1,   '字典管理', 'dict',        'system/dict/index', 'C', 'system:dict:list',    'dict',        6),
(8,   1,   '操作日志', 'log',         'system/log/index',  'C', 'system:log:list',     'log',         7),
-- 流程管理
(20,  0,   '流程管理', '/workflow',    NULL,               'M', NULL,                  'workflow',    2),
(21,  20,  '流程设计', 'design',      'workflow/design/index',    'C', 'workflow:design:list',    'design',     1),
(22,  20,  '流程实例', 'instance',    'workflow/instance/index',  'C', 'workflow:instance:list',  'instance',   2),
(23,  20,  '我的待办', 'task',        'workflow/task/index',      'C', 'workflow:task:list',      'todo',       3),
-- 任务中心
(30,  0,   '任务中心', '/task',       NULL,               'M', NULL,                  'task',        3),
(31,  30,  '任务看板', 'board',       'task/board/index',        'C', 'task:board:list',         'board',      1),
-- 考勤管理
(40,  0,   '考勤管理', '/attendance', NULL,               'M', NULL,                  'attendance',  4),
(41,  40,  '考勤打卡', 'checkin',     'attendance/checkin/index', 'C', 'attendance:checkin:list', 'checkin',  1),
(42,  40,  '考勤记录', 'record',      'attendance/record/index',  'C', 'attendance:record:list',  'record',   2),
(43,  40,  '排班管理', 'schedule',    'attendance/record/index',  'C', 'attendance:schedule:list','date',     3),
(44,  40,  '假期余额', 'leave',       'attendance/record/index',  'C', 'attendance:leave:list',   'vacation', 4),
-- 文档管理
(50,  0,   '文档管理', '/document',   NULL,               'M', NULL,                  'document',    5),
(51,  50,  '文档列表', 'list',        'document/list/index',     'C', 'document:list:list',      'list',     1),
(52,  50,  '文件夹管理', 'folder',    'document/list/index',     'C', 'document:folder:list',    'folder',   2),
-- 会议管理
(60,  0,   '会议管理', '/meeting',    NULL,               'M', NULL,                  'meeting',     6),
(61,  60,  '会议室预约', 'room',      'meeting/room/index',      'C', 'meeting:room:list',       'room',     1),
(62,  60,  '会议记录', 'record',      'meeting/record/index',    'C', 'meeting:record:list',     'notebook', 2),
-- 报表中心
(70,  0,   '报表中心', '/report',     NULL,               'M', NULL,                  'report',      7),
(71,  70,  '报表中心', 'center',      'report/center/index',     'C', 'report:center:list',      'center',   1),
-- 通知中心
(80,  0,   '通知中心', '/notification', NULL,             'M', NULL,                  'notification', 8),
(81,  80,  '通知列表', 'list',        'notification/list/index', 'C', 'notification:list:list',  'bell',     1),
-- 资产管理
(90,  0,   '资产管理', '/asset',      NULL,               'M', NULL,                  'box',         9),
(91,  90,  '资产列表', 'list',        'asset/list/index',        'C', 'asset:list:list',         'goods',     1),
(92,  90,  '资产分类', 'category',    'asset/list/index',        'C', 'asset:category:list',     'tree',      2),
(93,  90,  '资产盘点', 'inventory',   'asset/inventory/index',   'C', 'asset:inventory:list',    'checked',   3);

-- 管理员角色菜单关联（全部权限）
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES
(1, 1), (1, 2), (1, 3), (1, 4), (1, 5), (1, 6), (1, 7), (1, 8),
(1, 20), (1, 21), (1, 22), (1, 23),
(1, 30), (1, 31),
(1, 40), (1, 41), (1, 42), (1, 43), (1, 44),
(1, 50), (1, 51), (1, 52),
(1, 60), (1, 61), (1, 62),
(1, 70), (1, 71),
(1, 80), (1, 81),
(1, 90), (1, 91), (1, 92), (1, 93);

-- 默认字典类型
INSERT INTO `sys_dict_type` (`id`, `dict_name`, `dict_type`, `status`) VALUES
(1, '用户性别', 'sys_user_sex',      1),
(2, '系统状态', 'sys_normal_disable', 1),
(3, '菜单类型', 'sys_menu_type',      1),
(4, '任务优先级', 'task_priority',    1),
(5, '任务状态',   'task_status',      1),
(6, '流程实例状态', 'wf_inst_status', 1);

-- 默认字典数据
INSERT INTO `sys_dict_data` (`id`, `dict_type`, `dict_label`, `dict_value`, `sort`) VALUES
-- 用户性别
(1,  'sys_user_sex',       '男',   '1', 1),
(2,  'sys_user_sex',       '女',   '2', 2),
(3,  'sys_user_sex',       '未知', '0', 3),
-- 系统状态
(4,  'sys_normal_disable', '正常', '1', 1),
(5,  'sys_normal_disable', '停用', '0', 2),
-- 菜单类型
(6,  'sys_menu_type',      '目录', 'M', 1),
(7,  'sys_menu_type',      '菜单', 'C', 2),
(8,  'sys_menu_type',      '按钮', 'B', 3),
-- 任务优先级
(9,  'task_priority',      '低',   'low',    1),
(10, 'task_priority',      '中',   'medium', 2),
(11, 'task_priority',      '高',   'high',   3),
(12, 'task_priority',      '紧急', 'urgent', 4),
-- 任务状态
(13, 'task_status',        '待办',   'todo',     1),
(14, 'task_status',        '进行中', 'progress', 2),
(15, 'task_status',        '已完成', 'done',     3),
(16, 'task_status',        '已归档', 'archive',  4),
-- 流程实例状态
(17, 'wf_inst_status',     '进行中', 'running',    1),
(18, 'wf_inst_status',     '已完成', 'completed',  2),
(19, 'wf_inst_status',     '已终止', 'terminated', 3),
(20, 'wf_inst_status',     '已挂起', 'suspended',  4);

-- 默认会议室
INSERT INTO `biz_meeting_room` (`id`, `name`, `capacity`, `equipment`, `location`, `status`) VALUES
(1, 'A101-大会议室', 20, '投影仪,白板,视频会议', 'A栋1楼', 1),
(2, 'A202-小会议室', 8,  '电视,白板',           'A栋2楼', 1),
(3, 'B301-培训室',   50, '投影仪,音响,麦克风',   'B栋3楼', 1),
(4, 'C105-VIP接待室', 10, '投影仪,视频会议,茶水', 'C栋1楼', 1);

-- 默认通知
INSERT INTO `biz_notification` (`id`, `title`, `content`, `type`, `user_id`, `is_read`) VALUES
(1, '系统升级通知',    '系统将于本周六凌晨2:00-6:00进行维护升级，届时将暂停服务，请提前做好准备。', 'system', 1, 0),
(2, '您有新的审批待处理', '张三提交的请假申请需要您审批，请及时处理。', 'workflow', 1, 0),
(3, '任务即将到期提醒', '您负责的「完成需求文档」任务将于明天到期，请注意安排时间。', 'task', 1, 0);

-- 资产状态字典
INSERT INTO `sys_dict_type` (`id`, `dict_name`, `dict_type`, `status`) VALUES
(7, '资产状态', 'asset_status', 1),
(8, '假期类型', 'leave_type', 1),
(9, '班次类型', 'schedule_type', 1);

INSERT INTO `sys_dict_data` (`id`, `dict_type`, `dict_label`, `dict_value`, `sort`) VALUES
-- 资产状态
(21, 'asset_status', '闲置',   '0', 1),
(22, 'asset_status', '在用',   '1', 2),
(23, 'asset_status', '维修',   '2', 3),
(24, 'asset_status', '报废',   '3', 4),
-- 假期类型
(25, 'leave_type',   '年假',   'annual',   1),
(26, 'leave_type',   '病假',   'sick',     2),
(27, 'leave_type',   '事假',   'personal', 3),
(28, 'leave_type',   '婚假',   'marriage', 4),
-- 班次类型
(29, 'schedule_type', '固定班次', '1', 1),
(30, 'schedule_type', '弹性班次', '2', 2),
(31, 'schedule_type', '轮班',    '3', 3);

-- 默认资产分类
INSERT INTO `biz_asset_category` (`id`, `name`, `parent_id`, `description`, `status`) VALUES
(1, '电子设备',   0, '电脑、打印机等电子设备', 1),
(2, '办公家具',   0, '桌椅、柜子等办公家具',   1),
(3, '笔记本电脑', 1, '便携式电脑',             1),
(4, '台式电脑',   1, '台式机主机+显示器',      1),
(5, '打印机',     1, '打印/复印/扫描一体机',   1);

-- 默认假期余额（管理员）
INSERT INTO `leave_balance` (`id`, `user_id`, `leave_type`, `total_days`, `used_days`, `remain_days`, `year`) VALUES
(1, 1, 'annual',   10.0, 2.0, 8.0, 2026),
(2, 1, 'sick',      5.0, 0.0, 5.0, 2026),
(3, 1, 'personal',  3.0, 1.0, 2.0, 2026);
