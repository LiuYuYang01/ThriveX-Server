# 使用国内代理 Maven 镜像
FROM docker.m.daocloud.io/library/maven:3.9.9-eclipse-temurin-21 AS builder
# 设置应用目录
WORKDIR /app
# 复制 Maven 配置
COPY pom.xml ./
# 复制子模块配置
COPY model/pom.xml model/pom.xml
# 复制业务模块配置
COPY blog/pom.xml blog/pom.xml
# 复制项目源码
COPY . .
# 打包后端服务
RUN mvn clean package -DskipTests
# 使用国内代理 Java21 镜像
FROM docker.m.daocloud.io/library/eclipse-temurin:21-jre-alpine AS runner
# 设置应用目录
WORKDIR /app
# 设置运行端口
ENV PORT=9003
# 复制服务包
COPY --from=builder /app/blog/target/*.jar app.jar
# 暴露服务端口
EXPOSE 9003
# 启动后端服务
CMD ["java", "-jar", "app.jar"]
