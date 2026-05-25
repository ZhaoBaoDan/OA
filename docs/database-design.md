# SmartAuto OA 数据库设计文档

## 数据库规范

- 数据库名: `smartauto_oa`
- 字符集: `utf8mb4`
- 排序规则: `utf8mb4_general_ci`
- 存储引擎: InnoDB
- 表前缀: `sys_` (系统模块), `wf_` (工作流), `att_` (考勤), `doc_` (文档), `task_` (任务), `mt_` (会议), `asset_` (资产), `rpt_` (报表), `ntf_` (通知)

## 通用字段规范

所有业务表继承 BaseEntity，包含以下公共字段：
- `id` BIGINT 主键 (雪花算法)
- `create_by` VARCHAR(64) 创建者
- `create_time` DATETIME 创建时间
- `update_by` VARCHAR(64) 更新者
- `update_time` DATETIME 更新时间
- `del_flag` TINYINT 逻辑删除 (0=正常, 1=删除)

## ER 关系图

```
sys_user ──┬── sys_user_role ──── sys_role ──── sys_role_menu ──── sys_menu
            ├── sys_user_post ──── sys_post
            └── sys_dept (dept_id)

sys_dict_type ──── sys_dict_data
```

## 表详细设计

### sys_user 用户表
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT PK | 用户ID |
| username | VARCHAR(64) UNIQUE | 用户账号 |
| password | VARCHAR(128) | 密码 (BCrypt) |
| nickname | VARCHAR(64) | 用户昵称 |
| email | VARCHAR(128) | 邮箱 |
| phone | VARCHAR(20) | 手机号 |
| avatar | VARCHAR(256) | 头像地址 |
| gender | TINYINT | 性别 (0=未知,1=男,2=女) |
| status | TINYINT | 状态 (0=正常,1=停用) |
| dept_id | BIGINT | 部门ID |
| remark | VARCHAR(500) | 备注 |
| login_ip | VARCHAR(64) | 最后登录IP |
| login_time | DATETIME | 最后登录时间 |

### sys_role 角色表
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT PK | 角色ID |
| role_name | VARCHAR(64) | 角色名称 |
| role_key | VARCHAR(64) UNIQUE | 角色标识 |
| sort | INT | 显示顺序 |
| data_scope | TINYINT | 数据范围 (1=全部,2=自定义,3=本部门,4=本部门及以下,5=仅本人) |
| status | TINYINT | 状态 |
| remark | VARCHAR(500) | 备注 |

### sys_menu 菜单表
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT PK | 菜单ID |
| parent_id | BIGINT | 父菜单ID |
| menu_name | VARCHAR(64) | 菜单名称 |
| path | VARCHAR(200) | 路由路径 |
| component | VARCHAR(200) | 组件路径 |
| menu_type | CHAR(1) | 类型 (M=目录,C=菜单,B=按钮) |
| perms | VARCHAR(100) | 权限标识 |
| icon | VARCHAR(100) | 图标 |
| sort | INT | 显示顺序 |
| visible | TINYINT | 是否可见 |
| status | TINYINT | 状态 |

### sys_dept 部门表
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT PK | 部门ID |
| parent_id | BIGINT | 父部门ID |
| dept_name | VARCHAR(64) | 部门名称 |
| sort | INT | 显示顺序 |
| leader | VARCHAR(64) | 负责人 |
| phone | VARCHAR(20) | 联系电话 |
| email | VARCHAR(128) | 邮箱 |
| status | TINYINT | 状态 |

### sys_post 岗位表
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT PK | 岗位ID |
| post_code | VARCHAR(64) UNIQUE | 岗位编码 |
| post_name | VARCHAR(64) | 岗位名称 |
| sort | INT | 显示顺序 |
| status | TINYINT | 状态 |
| remark | VARCHAR(500) | 备注 |

### sys_dict_type 字典类型表
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT PK | 字典类型ID |
| dict_name | VARCHAR(100) | 字典名称 |
| dict_type | VARCHAR(100) UNIQUE | 字典类型 |
| status | TINYINT | 状态 |
| remark | VARCHAR(500) | 备注 |

### sys_dict_data 字典数据表
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT PK | 字典数据ID |
| dict_type | VARCHAR(100) | 字典类型 |
| dict_label | VARCHAR(100) | 字典标签 |
| dict_value | VARCHAR(100) | 字典值 |
| sort | INT | 显示顺序 |
| css_class | VARCHAR(100) | 样式属性 |
| status | TINYINT | 状态 |
| remark | VARCHAR(500) | 备注 |

### sys_oper_log 操作日志表
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT PK | 日志ID |
| title | VARCHAR(64) | 模块标题 |
| method | VARCHAR(200) | 方法名称 |
| request_url | VARCHAR(256) | 请求URL |
| request_param | TEXT | 请求参数 |
| response_result | TEXT | 返回结果 |
| status | TINYINT | 操作状态 (0=正常,1=异常) |
| error_msg | TEXT | 错误消息 |
| oper_ip | VARCHAR(64) | 操作IP |
| oper_name | VARCHAR(64) | 操作人 |
| oper_time | DATETIME | 操作时间 |

### 关联表

**sys_user_role** (用户-角色关联)
| user_id | BIGINT | 用户ID |
| role_id | BIGINT | 角色ID |

**sys_role_menu** (角色-菜单关联)
| role_id | BIGINT | 角色ID |
| menu_id | BIGINT | 菜单ID |

**sys_user_post** (用户-岗位关联)
| user_id | BIGINT | 用户ID |
| post_id | BIGINT | 岗位ID |
