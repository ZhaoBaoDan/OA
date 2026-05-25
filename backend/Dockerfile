# ========== 构建阶段 ==========
FROM maven:3.9-eclipse-temurin-17 AS builder

WORKDIR /build
COPY pom.xml .
# 先下载依赖（利用 Docker 缓存层）
RUN mvn dependency:go-offline -B

COPY src ./src
RUN mvn package -DskipTests -B

# ========== 运行阶段 ==========
FROM eclipse-temurin:17-jre-alpine

LABEL maintainer="SmartAuto OA Team"
LABEL description="SmartAuto OA Backend Service"

WORKDIR /app

# 安装字体（验证码图片需要）
RUN apk add --no-cache fontconfig ttf-dejavu

# 从构建阶段复制 JAR
COPY --from=builder /build/target/*.jar app.jar

# 暴露端口
EXPOSE 8080

# JVM 参数
ENV JAVA_OPTS="-Xms256m -Xmx512m -XX:+UseG1GC -XX:MaxGCPauseMillis=200"

# 健康检查
HEALTHCHECK --interval=30s --timeout=10s --retries=3 \
  CMD wget -qO- http://localhost:8080/api/auth/captcha || exit 1

# 启动应用
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar --spring.profiles.active=${SPRING_PROFILES_ACTIVE:-docker}"]
