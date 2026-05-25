# SmartAuto OA Docker 部署指南

## 从零到一完整部署步骤

### 一、环境准备

#### 1. 安装 Docker + Docker Compose

```bash
# Ubuntu/Debian
curl -fsSL https://get.docker.com | sh
sudo usermod -aG docker $USER
# 重新登录使 docker 组生效

# 验证安装
docker --version          # 需要 20.10+
docker compose version    # 需要 2.0+
```

#### 2. 硬件要求

| 资源 | 最低 | 推荐 |
|------|------|------|
| CPU | 2 核 | 4 核 |
| 内存 | 4 GB | 8 GB |
| 磁盘 | 20 GB | 50 GB |

#### 3. 端口检查

确保以下端口未被占用：

```bash
# 检查端口占用
lsof -i :80      # 前端 Nginx
lsof -i :8080    # 后端 API
lsof -i :3306    # MySQL
lsof -i :6379    # Redis
lsof -i :9000    # MinIO API
lsof -i :9001    # MinIO 控制台
lsof -i :5672    # RabbitMQ
lsof -i :15672   # RabbitMQ 管理界面
lsof -i :9200    # Elasticsearch
```

---

### 二、获取代码

```bash
# 克隆项目
git clone https://github.com/ZhaoBaoDan/OA.git
cd OA/smartauto-oa
```

---

### 三、一键启动

```bash
# 构建并启动所有服务（首次较慢，约 5-10 分钟）
docker compose up -d --build

# 查看启动进度
docker compose logs -f
```

#### 启动顺序

```
MySQL + Redis + MinIO + RabbitMQ + Elasticsearch
        ↓ (健康检查通过)
      Backend (Spring Boot)
        ↓
      Frontend (Nginx)
```

#### 等待就绪

```bash
# 检查所有容器状态
docker compose ps

# 所有服务应显示 "healthy" 或 "running"
# NAME                STATUS
# smartauto-mysql     running (healthy)
# smartauto-redis     running (healthy)
# smartauto-minio     running
# smartauto-rabbitmq  running
# smartauto-es        running
# smartauto-backend   running
# smartauto-frontend  running
```

---

### 四、访问系统

| 服务 | 地址 | 说明 |
|------|------|------|
| **前端页面** | http://localhost | Vue 3 主界面 |
| **后端 API** | http://localhost:8080/api | Spring Boot |
| **接口文档** | http://localhost:8080/api/doc.html | Knife4j |
| **MinIO 控制台** | http://localhost:9001 | 文件管理 |
| **RabbitMQ 管理** | http://localhost:15672 | 消息队列 |

#### 默认账号

| 系统 | 用户名 | 密码 |
|------|--------|------|
| OA 系统 | admin | admin123 |
| MinIO | minioadmin | minioadmin123 |
| RabbitMQ | guest | guest |

---

### 五、常用运维命令

```bash
# ========== 服务管理 ==========

# 查看所有服务状态
docker compose ps

# 查看某个服务日志
docker compose logs -f backend
docker compose logs -f frontend
docker compose logs -f mysql

# 重启某个服务
docker compose restart backend

# 停止所有服务
docker compose down

# 停止并清除数据（慎用！会清空数据库）
docker compose down -v

# ========== 更新部署 ==========

# 拉取最新代码
git pull

# 重新构建并部署
docker compose up -d --build

# ========== 数据库 ==========

# 进入 MySQL 命令行
docker exec -it smartauto-mysql mysql -uroot -psmartauto123 smartauto_oa

# 备份数据库
docker exec smartauto-mysql mysqldump -uroot -psmartauto123 smartauto_oa > backup_$(date +%Y%m%d).sql

# 恢复数据库
docker exec -i smartauto-mysql mysql -uroot -psmartauto123 smartauto_oa < backup.sql

# ========== MinIO ==========

# 创建 Bucket（首次部署后需要）
docker exec smartauto-minio mc alias set local http://localhost:9000 minioadmin minioadmin123
docker exec smartauto-minio mc mb local/smartauto-oa --ignore-existing
```

---

### 六、配置说明

#### 修改端口

编辑 `docker-compose.yml` 中的 `ports` 映射：

```yaml
frontend:
  ports:
    - "8080:80"    # 前端改为 8080

backend:
  ports:
    - "9090:8080"  # 后端改为 9090
```

#### 修改密码

编辑 `docker-compose.yml` 中的 `environment` 变量：

```yaml
mysql:
  environment:
    MYSQL_ROOT_PASSWORD: your_new_password

backend:
  environment:
    MYSQL_PASS: your_new_password  # 保持一致
```

---

### 七、故障排查

| 问题 | 排查命令 | 解决方案 |
|------|----------|----------|
| 后端启动失败 | `docker compose logs backend` | 检查 MySQL/Redis 是否就绪 |
| 前端 502 | `docker compose logs frontend` | 等待后端完全启动（约 30s） |
| 数据库连接失败 | `docker compose logs mysql` | 检查密码是否一致 |
| MinIO 上传失败 | `docker compose logs minio` | 确认 Bucket 已创建 |
| 内存不足 | `docker stats` | 增加内存或减少 ES JVM 堆 |

#### ES 内存不足

如果服务器内存 < 4GB，可以禁用 Elasticsearch：

```yaml
# docker-compose.yml 中注释掉 elasticsearch 服务
# backend 环境变量中移除 ES 依赖
```

---

### 八、生产环境建议

1. **修改所有默认密码**
2. **配置 HTTPS**（Nginx 反向代理 + Let's Encrypt）
3. **数据定期备份**（MySQL + MinIO）
4. **日志收集**（接入 ELK 或 Loki）
5. **监控告警**（Prometheus + Grafana）
6. **使用外部 MySQL/Redis**（云数据库更稳定）
