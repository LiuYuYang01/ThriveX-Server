# 基于官方JDK 1.8镜像作为构建环境
FROM maven:3.6.3-openjdk-8 AS builder

# 设置工作目录
WORKDIR /app

# 复制Maven配置文件
COPY . .

# 构建应用，跳过测试以加快构建速度
RUN mvn install -Dmaven.test.skip=true -P pro

# 运行阶段使用精简的JRE 1.8镜像
FROM openjdk:8-jre-alpine

# 维护者信息
LABEL maintainer="1141306760@qq.com"

# 设置时区为Asia/Shanghai
ENV TZ=Asia/Shanghai
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

# 设置工作目录
WORKDIR /app

# 从构建阶段复制打包好的jar文件
COPY --from=builder /app/blog/target/*.jar app.jar
# 添加环境变量
ENV JAVA_OPTS="-Xmx512m -Xms256m"
# 暴露应用端口
EXPOSE 9003

# 启动命令，使用exec形式以确保应用接收信号
# 修改启动命令
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]