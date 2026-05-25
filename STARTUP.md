# SmartAuto OA 启动指南

## 🚀 快速启动（Docker 一键部署）

### 前置要求
- Docker 20.10+
- Docker Compose 2.0+

### 一键启动

```bash
# 克隆项目后进入目录
cd smartauto-oa

# 启动所有服务
docker-compose up -d

# 查看启动日志
docker-compose logs -f

# 等待所有服务就绪（约2-3分钟）
```

### 访问地址

| 服务 | 地址 | 说明 |
|------|------|------|
| **前端页面** | http://localhost | Vue 3 前端 |
| **后端 API** | http://localhost:8080/api | Spring Boot 后端 |
| **接口文档** | http://localhost:8080/api/doc.html | Knife4j API 文档 |
| **MySQL** | localhost:3306 | 数据库 |
| **Redis** | localhost:6379 | 缓存 |

### 默认账号

| 用户名 | 密码 | 角色 |
|--------|------|------|
| admin | admin123 | 超级管理员 |
| zhangsan | admin123 | 普通用户 |
| lisi | admin123 | 普通用户 |

### 常用命令

```bash
# 停止服务
docker-compose down

# 停止并删除数据（慎用！会清除数据库）
docker-compose down -v

# 重新构建并启动
docker-compose up -d --build

# 查看某个服务的日志
docker-compose logs -f backend
docker-compose logs -f frontend

# 进入容器
docker exec -it smartauto-backend sh
docker exec -it smartauto-mysql mysql -uroot -psmartauto123
```

---

## 💻 本地开发启动

### 环境要求

| 工具 | 版本 | 说明 |
|------|------|------|
| JDK | 17+ | Java 运行环境 |
| Maven | 3.8+ | 构建工具 |
| Node.js | 18+ | 前端运行环境 |
| MySQL | 8.0+ | 数据库 |
| Redis | 7.x | 缓存 |

### 1. 准备数据库

```bash
# 登录 MySQL
mysql -u root -p

# 创建数据库
CREATE DATABASE smartauto_oa DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

# 导入表结构
mysql -u root -p smartauto_oa < backend/src/main/resources/db/schema.sql

# 导入初始数据
mysql -u root -p smartauto_oa < deploy/init-data.sql
```

### 2. 启动后端

```bash
cd backend

# 修改数据库连接（如果需要）
# 编辑 src/main/resources/application.yml
# 修改 spring.datasource.url, username, password
# 修改 spring.data.redis.host, password

# 编译运行
mvn spring-boot:run

# 或者打包后运行
mvn clean package -DskipTests
java -jar target/smartauto-oa-1.0.0-SNAPSHOT.jar
```

后端启动成功后访问: http://localhost:8080/api/doc.html

### 3. 启动前端

```bash
cd frontend

# 安装依赖
npm install

# 启动开发服务器
npm run dev
```

前端启动成功后访问: http://localhost:5173

### 4. 本地开发配置

如果不需要 RabbitMQ、Elasticsearch、MinIO 等服务，可以在 `application.yml` 中注释掉相关配置。项目核心功能（用户管理、权限、考勤、任务等）只需要 MySQL + Redis 即可运行。

---

## 📦 项目结构

```
smartauto-oa/
├── backend/                    # Spring Boot 后端
│   ├── Dockerfile             # 后端 Docker 镜像
│   ├── pom.xml                # Maven 依赖
│   └── src/
│       ├── main/
│       │   ├── java/com/smartauto/oa/
│       │   │   ├── config/       # 配置类
│       │   │   ├── common/       # 公共工具
│       │   │   ├── system/       # 系统管理模块
│       │   │   ├── workflow/     # 工作流模块
│       │   │   ├── attendance/   # 考勤模块
│       │   │   ├── document/     # 文档模块
│       │   │   ├── task/         # 任务模块
│       │   │   ├── meeting/      # 会议模块
│       │   │   ├── asset/        # 资产模块
│       │   │   ├── report/       # 报表模块
│       │   │   └── notification/ # 通知模块
│       │   └── resources/
│       │       ├── application.yml
│       │       ├── application-docker.yml
│       │       └── db/schema.sql
│       └── test/
├── frontend/                   # Vue 3 前端
│   ├── Dockerfile             # 前端 Docker 镜像
│   ├── nginx.conf             # Nginx 配置
│   ├── package.json
│   └── src/
├── deploy/
│   └── init-data.sql          # 初始数据
├── docs/                      # 项目文档
├── docker-compose.yml         # Docker Compose 编排
└── STARTUP.md                 # 本文件
```

---

## 🔧 常见问题

### Q: 后端启动报错 "Connection refused"
A: 确保 MySQL 和 Redis 已启动，检查 `application.yml` 中的连接配置。

### Q: 前端启动报错 "Module not found"
A: 执行 `rm -rf node_modules && npm install` 重新安装依赖。

### Q: Docker 启动后前端显示 502
A: 后端可能还没启动完成，等待 30 秒后刷新页面。

### Q: 忘记管理员密码
A: 连接数据库执行：
```sql
UPDATE sys_user SET password = '$2a$10$VQEDZbLR4kBjmHO/GQp5l.VPLuJfvqDG2Bg/vBmMBfUMRj5z5qvya' WHERE username = 'admin';
```
密码重置为 `admin123`。

### Q: 如何修改端口
A: 
- 后端端口：修改 `application.yml` 中的 `server.port`
- 前端端口：修改 `frontend/nginx.conf` 中的 `listen`
- Docker 端口映射：修改 `docker-compose.yml` 中的 `ports`
