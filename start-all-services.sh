#!/bin/bash

# 启动顺序：eureka-server -> config-server -> 其他服务

echo "启动 Eureka Server..."
cd eureka-server && mvn spring-boot:run &
EUREKA_PID=$!

# 等待Eureka Server启动
echo "等待Eureka Server启动..."
sleep 10

echo "Eureka Server 已启动"

# 启动 Config Server
echo "启动 Config Server..."
cd ../config-server && mvn spring-boot:run &
CONFIG_PID=$!

# 等待Config Server启动
echo "等待Config Server启动..."
sleep 8

echo "Config Server 已启动"

# 启动其他服务
echo "启动 User Service..."
cd ../user-service && mvn spring-boot:run &

 echo "启动 Gateway..."
cd ../gateway && mvn spring-boot:run &

 echo "启动 Ontology Service..."
cd ../ontology-service/ontology-service-app && mvn spring-boot:run &

 echo "启动 Collaboration Service..."
cd ../collaboration-service && mvn spring-boot:run &

 echo "启动 I18n Service..."
cd ../i18n-service && mvn spring-boot:run &

 echo "启动 Admin Service..."
cd ../admin-service && mvn spring-boot:run &

echo "所有服务启动命令已执行，请检查各服务状态"

# 保存PID到文件
echo $EUREKA_PID > eureka-server.pid
echo $CONFIG_PID > config-server.pid
