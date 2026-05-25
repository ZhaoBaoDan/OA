# SmartAuto OA API 接口规范

## 通用规范

### 请求格式
- Content-Type: application/json
- 字符编码: UTF-8
- 时间格式: yyyy-MM-dd HH:mm:ss

### 认证方式
- 使用 JWT Bearer Token
- Header: `Authorization: Bearer <token>`

### 统一响应格式
```json
{
  "code": 200,
  "msg": "操作成功",
  "data": {}
}
```

### 分页响应格式
```json
{
  "code": 200,
  "msg": "操作成功",
  "data": {
    "records": [],
    "total": 100,
    "pageNum": 1,
    "pageSize": 10
  }
}
```

### 错误码
| code | 说明 |
|------|------|
| 200 | 成功 |
| 400 | 请求参数错误 |
| 401 | 未认证 |
| 403 | 无权限 |
| 404 | 资源不存在 |
| 500 | 服务器内部错误 |
| 1001 | 用户名或密码错误 |
| 1002 | 账号已被停用 |
| 1003 | Token 已过期 |

---

## 系统管理模块 API

### 认证模块 `/api/auth`

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /api/auth/login | 用户登录 |
| POST | /api/auth/logout | 用户登出 |
| GET | /api/auth/info | 获取当前用户信息 |
| POST | /api/auth/refresh | 刷新 Token |

### 用户管理 `/api/system/user`

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /api/system/user | 用户列表 (分页) |
| GET | /api/system/user/{id} | 用户详情 |
| POST | /api/system/user | 新增用户 |
| PUT | /api/system/user | 修改用户 |
| DELETE | /api/system/user/{id} | 删除用户 |
| PUT | /api/system/user/status | 修改用户状态 |
| PUT | /api/system/user/resetPwd | 重置密码 |
| GET | /api/system/user/export | 导出用户 |
| POST | /api/system/user/import | 导入用户 |

### 角色管理 `/api/system/role`

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /api/system/role | 角色列表 |
| GET | /api/system/role/{id} | 角色详情 |
| POST | /api/system/role | 新增角色 |
| PUT | /api/system/role | 修改角色 |
| DELETE | /api/system/role/{id} | 删除角色 |
| PUT | /api/system/role/dataScope | 修改数据权限 |
| PUT | /api/system/role/status | 修改角色状态 |

### 菜单管理 `/api/system/menu`

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /api/system/menu | 菜单列表 |
| GET | /api/system/menu/{id} | 菜单详情 |
| POST | /api/system/menu | 新增菜单 |
| PUT | /api/system/menu | 修改菜单 |
| DELETE | /api/system/menu/{id} | 删除菜单 |
| GET | /api/system/menu/tree | 菜单树 |
| GET | /api/system/menu/roleMenuTree/{roleId} | 角色菜单树 |

### 部门管理 `/api/system/dept`

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /api/system/dept | 部门列表 |
| GET | /api/system/dept/{id} | 部门详情 |
| POST | /api/system/dept | 新增部门 |
| PUT | /api/system/dept | 修改部门 |
| DELETE | /api/system/dept/{id} | 删除部门 |
| GET | /api/system/dept/tree | 部门树 |

### 岗位管理 `/api/system/post`

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /api/system/post | 岗位列表 |
| POST | /api/system/post | 新增岗位 |
| PUT | /api/system/post | 修改岗位 |
| DELETE | /api/system/post/{id} | 删除岗位 |

### 字典管理 `/api/system/dict`

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /api/system/dict/type | 字典类型列表 |
| POST | /api/system/dict/type | 新增字典类型 |
| PUT | /api/system/dict/type | 修改字典类型 |
| DELETE | /api/system/dict/type/{id} | 删除字典类型 |
| GET | /api/system/dict/data | 字典数据列表 |
| GET | /api/system/dict/data/type/{dictType} | 按类型查字典数据 |
| POST | /api/system/dict/data | 新增字典数据 |
| PUT | /api/system/dict/data | 修改字典数据 |
| DELETE | /api/system/dict/data/{id} | 删除字典数据 |

### 操作日志 `/api/system/log`

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /api/system/log | 日志列表 |
| DELETE | /api/system/log/{id} | 删除日志 |
| DELETE | /api/system/log/clean | 清空日志 |
