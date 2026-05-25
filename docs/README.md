# SmartAuto OA 智能自动化办公平台

## 项目概述

SmartAuto OA 以"流程自动化、事件驱动、数据反哺"为核心理念，构建新一代企业办公平台。

## 技术栈

### 后端
| 技术 | 版本 | 用途 |
|------|------|------|
| Spring Boot | 3.2.x | 基础框架 |
| Spring MVC | 6.x | RESTful API |
| MyBatis-Plus | 3.5.x | ORM |
| Spring Security + JWT | - | 无状态认证 |
| Flowable | 7.x | BPMN 2.0 工作流 |
| Redis | 7.x | 缓存、分布式锁 |
| RabbitMQ | 3.13 | 消息队列、事件驱动 |
| MinIO | - | S3 兼容文件存储 |
| Elasticsearch | 8.x | 全文检索 |
| XXL-JOB | 2.4.x | 分布式定时任务 |
| Knife4j | 4.x | OpenAPI 3.0 接口文档 |

### 前端
| 技术 | 版本 | 用途 |
|------|------|------|
| Vue | 3 | 前端框架 |
| TypeScript | - | 类型安全 |
| Vite | 5 | 构建工具 |
| Element Plus | - | UI 组件库 |
| Pinia | - | 状态管理 |
| ECharts | 5 | 数据图表 |
| bpmn.js / LogicFlow | - | 流程设计器 |

## 模块清单

### 系统基础模块 (8个)
1. **认证模块** - 登录/登出、Token 刷新
2. **用户管理** - CRUD、批量导入导出、头像上传
3. **角色管理** - CRUD、菜单权限分配、数据范围控制
4. **菜单管理** - 目录/菜单/按钮三级资源树
5. **部门管理** - 无限层级树形组织结构
6. **岗位管理** - CRUD、状态启用/禁用
7. **数据字典** - 字典类型 + 字典数据两级管理
8. **操作日志** - AOP 自动记录

### 业务功能模块 (7个)
1. **工作流引擎** - Flowable BPMN 2.0、可视化流程设计
2. **考勤管理** - 打卡、排班、假期管理
3. **文档管理** - 在线预览编辑、版本控制、全文检索
4. **任务管理** - 看板、甘特图、自动提醒
5. **会议管理** - 会议室预约、冲突检测、纪要管理
6. **资产管理** - 全生命周期、折旧计算、标签盘点
7. **报表中心** - 多源数据聚合、ECharts 展示

### 通用能力
- **通知中心** - 站内信、邮件、WebSocket 实时推送
- **权限控制** - RBAC 模型、菜单/按钮/数据行级权限

## 项目结构

```
smartauto-oa/
├── backend/          # Spring Boot 后端
│   ├── src/main/java/com/smartauto/oa/
│   │   ├── config/       # 配置类
│   │   ├── common/       # 公共工具、异常处理
│   │   ├── system/       # 系统管理模块
│   │   ├── workflow/     # 工作流模块
│   │   ├── attendance/   # 考勤模块
│   │   ├── document/     # 文档模块
│   │   ├── task/         # 任务模块
│   │   ├── meeting/      # 会议模块
│   │   ├── asset/        # 资产模块
│   │   ├── report/       # 报表模块
│   │   └── notification/ # 通知模块
│   └── src/main/resources/
│       ├── mapper/       # MyBatis XML
│       └── db/           # 数据库脚本
├── frontend/         # Vue 3 前端
│   └── src/
│       ├── api/          # API 封装
│       ├── components/   # 公共组件
│       ├── layout/       # 布局框架
│       ├── router/       # 路由配置
│       ├── stores/       # Pinia 状态
│       ├── types/        # TypeScript 类型
│       ├── utils/        # 工具函数
│       └── views/        # 页面视图
├── docs/             # 项目文档
└── deploy/           # 部署配置
```

## 快速开始

### 环境要求
- JDK 17+
- Node.js 18+
- MySQL 8.0+
- Redis 7.x
- RabbitMQ 3.13 (可选)
- MinIO (可选)

### 后端启动
```bash
cd backend
mvn spring-boot:run
```

### 前端启动
```bash
cd frontend
npm install
npm run dev
```

### 访问
- 前端: http://localhost:5173
- 后端 API: http://localhost:8080
- 接口文档: http://localhost:8080/doc.html
