# SmartAuto OA v4.0 变更记录

## 版本信息
- **版本号**: 4.0
- **发布日期**: 2026-05-26
- **修复目标**: Docker 一键部署 `docker compose up -d --build` 全流程通过

---

## 一、已修复问题清单（13项）

### 前端（4项）

| # | 文件 | 问题 | 修复方式 |
|---|------|------|----------|
| 1 | `tsconfig.json` | `include` 含 `uno.config.ts`，vue-tsc 无法生成 `.d.ts` 导致 TS6305 编译中断 | 从 `include` 数组中移除 `uno.config.ts` |
| 2 | `tsconfig.node.json` | `include` 含 `uno.config.ts` + `composite:true`，vue-tsc 解析 project references 时报错 | 从 `include` 数组中移除 `uno.config.ts`，仅保留 `vite.config.ts` |
| 3 | `frontend/Dockerfile` | `node:18-alpine` 不满足 chokidar@5 / sass@1.100 等依赖要求的 `>=20.19.0` | 基础镜像改为 `node:20-alpine` |
| 4 | `src/api/*.ts`（8个文件） | `attendance/document/workflow/meeting/flowable/asset/schedule/report` 的 API 路径带 `/api` 前缀，与 `baseURL=/api` 叠加导致请求 `/api/api/...` | 批量移除前端 API 路径中的 `/api` 前缀 |

### 后端（8项）

| # | 文件 | 问题 | 修复方式 |
|---|------|------|----------|
| 5 | `pom.xml` | 缺少 `spring-boot-starter-websocket` 依赖，`@EnableWebSocketMessageBroker` 等注解编译失败 | 在 `<dependencies>` 中添加该依赖 |
| 6 | `Result.java` | 缺少 `success(String msg)` 方法，30+ 处 `Result<Void>` 类型方法调用 `Result.success("xxx")` 时类型不匹配 | 新增 `public static Result<Void> success(String msg)` 重载方法 |
| 7 | `SysRoleMapper.java` | 缺少 `selectRoleIdsByMenuId()` 方法，`SysMenuServiceImpl.delete()` 编译失败 | 添加 `@Select` 注解的 Mapper 方法 |
| 8 | `FlowableEngineService.java` | `getResourceAsStreamByDeploymentId()` 在 Flowable 7.0.1 不存在 | 改用 `repositoryService.getResourceAsStream(deploymentId, resourceName)` |
| 9 | `FlowableEngineService.java` | `orderByProcessInstanceStartTime()` 在 Flowable 7.0.1 不存在 | 改用 `orderByProcessInstanceId().desc()` |
| 10 | **全部31个Controller** | `@RequestMapping("/api/...")` 与 `application.yml` 的 `context-path:/api` 叠加，实际路径变成 `/api/api/...` | 批量移除 Controller `@RequestMapping` 中的 `/api` 前缀 |
| 11 | `SysUser.java` | `loginDate` 字段经驼峰转下划线映射为 `login_date`，但 schema.sql 中列名是 `login_time` | 添加 `@TableField("login_time")` 注解 |
| 12 | `schema.sql` | 字符集 `utf8mb4_unicode_ci` 与 docker-compose.yml 的 `utf8mb4_general_ci` 不一致 | 统一为 `utf8mb4_general_ci` |

### Docker Compose（1项）

| # | 文件 | 问题 | 修复方式 |
|---|------|------|----------|
| 13 | `docker-compose.yml` | `version: '3.8'` 属性在 Docker Compose V2 中已废弃 | 删除 `version` 行 |

### 前端 API 路径对齐（额外修复）

| # | 文件 | 问题 | 修复方式 |
|---|------|------|----------|
| 14 | `frontend/src/api/workflow.ts` | 前端路径 `/workflow/definition` 与后端 `/workflow/def` 不匹配 | 重写文件，对齐后端实际端点 |
| 15 | `frontend/src/api/attendance.ts` | 前端 `clockIn/clockOut` 与后端 `checkin/checkout` 不匹配 | 重写文件，对齐后端实际端点 |
| 16 | `frontend/src/api/system/*.ts`（7个文件） | 前端列表接口路径带 `/list` 后缀，后端无此后缀 | 批量移除 `/list` 后缀 |

---

## 二、后续待完善项（非阻塞，不影响部署）

### 2.1 后端功能补全

| 优先级 | 项目 | 说明 |
|--------|------|------|
| P1 | `MetaObjectHandler` | `BaseEntity` 的 `createBy/updateBy/createTime/updateTime` 使用了 `FieldFill.INSERT` 注解，但未实现 `MetaObjectHandler`，导致审计字段不会自动填充。建议添加 `AutoFillMetaObjectHandler` |
| P1 | 文档下载/预览接口 | `BizDocumentController` 缺少 `/{id}/download` 和 `/{id}/preview` 端点，前端调用会 404 |
| P2 | 考勤统计接口 | 前端原有 `/attendance/month`、`/attendance/statistics`、`/attendance/calendar` 端点，后端仅有 `/attendance/stats`，建议补全 |
| P2 | 流程定义激活接口 | 前端原有 `activateDefinition` 功能，后端 `WfProcessDefController` 仅有 `suspend` 无 `activate`，建议补全 |
| P3 | `init-data.sql` 冲突 | `deploy/init-data.sql` 与 `schema.sql` 的初始数据有主键冲突，两者不应同时执行。建议合并或删除其中一个 |

### 2.2 前端功能补全

| 优先级 | 项目 | 说明 |
|--------|------|------|
| P2 | `flowable.ts` 集成 | 前端原有 `flowable.ts`（Flowable 专用 API），已保留但部分视图组件可能引用旧路径，需逐页面验证 |
| P3 | Vue SFC 类型检查 | 部分 `.vue` 文件可能存在未使用的变量或类型导入，可在后续版本逐步清理 |

### 2.3 Docker 运维优化

| 优先级 | 项目 | 说明 |
|--------|------|------|
| P2 | ES 健康检查 | Elasticsearch 的 healthcheck 使用 `curl`，但 `elasticsearch:8.13.0` 镜像可能未预装 curl，建议改为 `wget` 或使用 ES API |
| P3 | 资源限制 | 建议为各容器添加 `deploy.resources.limits` 防止 OOM |
| P3 | 日志轮转 | 建议添加 `logging` 配置限制容器日志大小 |

---

## 三、部署验证步骤

### 3.1 一键部署

```bash
cd smartauto-oa
docker compose up -d --build
```

### 3.2 检查服务状态

```bash
# 查看所有容器状态
docker compose ps

# 查看后端启动日志
docker compose logs -f backend

# 查看前端构建日志
docker compose logs -f frontend
```

### 3.3 验证访问

| 服务 | 地址 | 预期结果 |
|------|------|----------|
| 前端页面 | http://localhost | 显示登录页 |
| 后端 API | http://localhost:8080/api/auth/captcha | 返回验证码 JSON |
| 接口文档 | http://localhost:8080/api/doc.html | Knife4j 文档页 |
| 默认账号 | admin / admin123 | 登录成功进入仪表盘 |

### 3.4 常见问题排查

```bash
# 后端启动失败（数据库连接）
docker compose logs backend | grep -i "connection refused"
# → 检查 MySQL healthcheck 是否通过

# 前端 502
# → 后端可能还没启动完成，等待 30 秒后刷新

# 容器重启循环
docker compose logs backend | tail -50
# → 查看具体异常堆栈
```

---

## 四、文件变更总览

```
smartauto-oa/
├── docker-compose.yml                    # 删除 version 属性
├── CHANGELOG-v4.0.md                     # 本文件（新增）
├── backend/
│   ├── pom.xml                           # 添加 websocket 依赖
│   └── src/main/java/com/smartauto/oa/
│   │   ├── common/Result.java            # 新增 success(String) 方法
│   │   ├── system/entity/SysUser.java    # 添加 @TableField("login_time")
│   │   ├── system/mapper/SysRoleMapper.java  # 新增 selectRoleIdsByMenuId
│   │   ├── workflow/service/FlowableEngineService.java  # 修复 Flowable API
│   │   └── */controller/*.java           # 全部31个Controller移除/api前缀
│   └── src/main/resources/db/schema.sql  # 统一字符集
├── frontend/
│   ├── Dockerfile                        # Node 20
│   ├── tsconfig.json                     # 移除 uno.config.ts
│   ├── tsconfig.node.json                # 移除 uno.config.ts
│   └── src/api/
│       ├── workflow.ts                   # 重写，对齐后端端点
│       ├── attendance.ts                 # 重写，对齐后端端点
│       ├── system/*.ts                   # 移除 /list 后缀
│       └── *.ts                          # 移除 /api 前缀（8个文件）
```
