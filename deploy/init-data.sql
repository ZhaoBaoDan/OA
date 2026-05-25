-- ========================================
-- SmartAuto OA 初始化数据
-- ========================================

-- 初始化管理员用户 (密码: admin123 BCrypt加密)
INSERT INTO sys_user (id, username, password, nickname, email, phone, status, dept_id, create_time) VALUES
(1, 'admin', '$2a$10$VQEDZbLR4kBjmHO/GQp5l.VPLuJfvqDG2Bg/vBmMBfUMRj5z5qvya', '系统管理员', 'admin@smartauto.com', '13800000001', 0, 1, NOW()),
(2, 'zhangsan', '$2a$10$VQEDZbLR4kBjmHO/GQp5l.VPLuJfvqDG2Bg/vBmMBfUMRj5z5qvya', '张三', 'zhangsan@smartauto.com', '13800000002', 0, 2, NOW()),
(3, 'lisi', '$2a$10$VQEDZbLR4kBjmHO/GQp5l.VPLuJfvqDG2Bg/vBmMBfUMRj5z5qvya', '李四', 'lisi@smartauto.com', '13800000003', 0, 3, NOW());

-- 初始化部门
INSERT INTO sys_dept (id, parent_id, ancestors, dept_name, sort, leader, phone, email, status, create_time) VALUES
(1, 0, '0', 'SmartAuto科技', 0, '管理员', '13800000001', 'admin@smartauto.com', 0, NOW()),
(2, 1, '0,1', '技术部', 1, '张三', '13800000002', 'tech@smartauto.com', 0, NOW()),
(3, 1, '0,1', '产品部', 2, '李四', '13800000003', 'product@smartauto.com', 0, NOW()),
(4, 1, '0,1', '市场部', 3, NULL, NULL, NULL, 0, NOW()),
(5, 1, '0,1', '人事部', 4, NULL, NULL, NULL, 0, NOW()),
(6, 1, '0,1', '财务部', 5, NULL, NULL, NULL, 0, NOW());

-- 初始化角色
INSERT INTO sys_role (id, role_name, role_key, sort, data_scope, status, create_time) VALUES
(1, '超级管理员', 'admin', 1, 1, 0, NOW()),
(2, '普通用户', 'user', 2, 5, 0, NOW()),
(3, '部门经理', 'dept_manager', 3, 4, 0, NOW());

-- 用户角色关联
INSERT INTO sys_user_role (user_id, role_id) VALUES (1, 1), (2, 2), (3, 2);

-- 初始化岗位
INSERT INTO sys_post (id, post_code, post_name, sort, status, create_time) VALUES
(1, 'CEO', 'CEO', 1, 0, NOW()),
(2, 'CTO', '技术总监', 2, 0, NOW()),
(3, 'DEV_ENGINEER', '开发工程师', 3, 0, NOW()),
(4, 'PRODUCT_MANAGER', '产品经理', 4, 0, NOW()),
(5, 'UI_DESIGNER', 'UI设计师', 5, 0, NOW());

-- 用户岗位关联
INSERT INTO sys_user_post (user_id, post_id) VALUES (1, 1), (2, 3), (3, 4);

-- 初始化菜单
INSERT INTO sys_menu (id, parent_id, menu_name, path, component, menu_type, perms, icon, sort, visible, status, create_time) VALUES
-- 一级目录
(1, 0, '系统管理', '/system', NULL, 'M', NULL, 'Setting', 1, 0, 0, NOW()),
(2, 0, '工作流', '/workflow', NULL, 'M', NULL, 'Connection', 2, 0, 0, NOW()),
(3, 0, '办公管理', '/office', NULL, 'M', NULL, 'OfficeBuilding', 3, 0, 0, NOW()),
(4, 0, '数据中心', '/data', NULL, 'M', NULL, 'DataLine', 4, 0, 0, NOW()),
-- 系统管理子菜单
(10, 1, '用户管理', 'user', 'system/user/index', 'C', 'system:user:list', 'User', 1, 0, 0, NOW()),
(11, 1, '角色管理', 'role', 'system/role/index', 'C', 'system:role:list', 'UserFilled', 2, 0, 0, NOW()),
(12, 1, '菜单管理', 'menu', 'system/menu/index', 'C', 'system:menu:list', 'Menu', 3, 0, 0, NOW()),
(13, 1, '部门管理', 'dept', 'system/dept/index', 'C', 'system:dept:list', 'Share', 4, 0, 0, NOW()),
(14, 1, '岗位管理', 'post', 'system/post/index', 'C', 'system:post:list', 'Postcard', 5, 0, 0, NOW()),
(15, 1, '字典管理', 'dict', 'system/dict/index', 'C', 'system:dict:list', 'Collection', 6, 0, 0, NOW()),
(16, 1, '操作日志', 'log', 'system/log/index', 'C', 'system:log:list', 'Document', 7, 0, 0, NOW()),
-- 工作流子菜单
(20, 2, '流程设计', 'design', 'workflow/design/index', 'C', 'workflow:design:list', 'Edit', 1, 0, 0, NOW()),
(21, 2, '流程实例', 'instance', 'workflow/instance/index', 'C', 'workflow:instance:list', 'VideoPlay', 2, 0, 0, NOW()),
-- 办公管理子菜单
(30, 3, '考勤打卡', 'checkin', 'attendance/checkin/index', 'C', 'attendance:checkin', 'AlarmClock', 1, 0, 0, NOW()),
(31, 3, '任务看板', 'board', 'task/board/index', 'C', 'task:board:list', 'Finished', 2, 0, 0, NOW()),
(32, 3, '文档管理', 'document', 'document/list/index', 'C', 'document:list', 'Folder', 3, 0, 0, NOW()),
(33, 3, '会议预约', 'meeting', 'meeting/room/index', 'C', 'meeting:room:list', 'Calendar', 4, 0, 0, NOW()),
(34, 3, '资产管理', 'asset', 'asset/list/index', 'C', 'asset:list', 'Box', 5, 0, 0, NOW()),
(35, 3, '通知中心', 'notification', 'notification/list/index', 'C', 'notification:list', 'Bell', 6, 0, 0, NOW()),
-- 数据中心子菜单
(40, 4, '报表中心', 'report', 'report/center/index', 'C', 'report:center', 'DataAnalysis', 1, 0, 0, NOW()),
-- 用户管理按钮权限
(101, 10, '用户查询', '', '', 'B', 'system:user:query', '', 1, 0, 0, NOW()),
(102, 10, '用户新增', '', '', 'B', 'system:user:add', '', 2, 0, 0, NOW()),
(103, 10, '用户修改', '', '', 'B', 'system:user:edit', '', 3, 0, 0, NOW()),
(104, 10, '用户删除', '', '', 'B', 'system:user:remove', '', 4, 0, 0, NOW()),
(105, 10, '重置密码', '', '', 'B', 'system:user:resetPwd', '', 5, 0, 0, NOW());

-- 角色菜单关联（管理员拥有所有菜单）
INSERT INTO sys_role_menu (role_id, menu_id)
SELECT 1, id FROM sys_menu;

-- 普通用户只有基本权限
INSERT INTO sys_role_menu (role_id, menu_id) VALUES
(2, 3), (2, 30), (2, 31), (2, 32), (2, 33), (2, 35), (2, 40), (2, 4);

-- 初始化字典类型
INSERT INTO sys_dict_type (id, dict_name, dict_type, status, create_time) VALUES
(1, '用户性别', 'sys_user_sex', 0, NOW()),
(2, '系统状态', 'sys_normal_disable', 0, NOW()),
(3, '菜单类型', 'sys_menu_type', 0, NOW()),
(4, '操作类型', 'sys_oper_type', 0, NOW()),
(5, '任务优先级', 'task_priority', 0, NOW()),
(6, '任务状态', 'task_status', 0, NOW()),
(7, '资产状态', 'asset_status', 0, NOW());

-- 初始化字典数据
INSERT INTO sys_dict_data (id, dict_type, dict_label, dict_value, sort, status, create_time) VALUES
-- 用户性别
(1, 'sys_user_sex', '未知', '0', 1, 0, NOW()),
(2, 'sys_user_sex', '男', '1', 2, 0, NOW()),
(3, 'sys_user_sex', '女', '2', 3, 0, NOW()),
-- 系统状态
(4, 'sys_normal_disable', '正常', '0', 1, 0, NOW()),
(5, 'sys_normal_disable', '停用', '1', 2, 0, NOW()),
-- 菜单类型
(6, 'sys_menu_type', '目录', 'M', 1, 0, NOW()),
(7, 'sys_menu_type', '菜单', 'C', 2, 0, NOW()),
(8, 'sys_menu_type', '按钮', 'B', 3, 0, NOW()),
-- 任务优先级
(9, 'task_priority', '低', '0', 1, 0, NOW()),
(10, 'task_priority', '中', '1', 2, 0, NOW()),
(11, 'task_priority', '高', '2', 3, 0, NOW()),
(12, 'task_priority', '紧急', '3', 4, 0, NOW()),
-- 任务状态
(13, 'task_status', '待办', 'todo', 1, 0, NOW()),
(14, 'task_status', '进行中', 'doing', 2, 0, NOW()),
(15, 'task_status', '已完成', 'done', 3, 0, NOW()),
(16, 'task_status', '已归档', 'archived', 4, 0, NOW()),
-- 资产状态
(17, 'asset_status', '闲置', '0', 1, 0, NOW()),
(18, 'asset_status', '在用', '1', 2, 0, NOW()),
(19, 'asset_status', '维修', '2', 3, 0, NOW()),
(20, 'asset_status', '报废', '3', 4, 0, NOW());
