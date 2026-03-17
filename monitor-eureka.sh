#!/bin/bash

# 监控Eureka Server状态的脚本

EUREKA_SERVER_URL="http://localhost:8762/actuator/health"
EUREKA_SERVER_DIR="eureka-server"
MAX_RETRIES=3

check_eureka_status() {
    # 使用curl检查Eureka Server的健康状态
    response=$(curl -s -o /dev/null -w "%{http_code}" $EUREKA_SERVER_URL)
    
    if [ "$response" -eq 200 ]; then
        # 检查返回的JSON是否包含"UP"状态
        health_status=$(curl -s $EUREKA_SERVER_URL | grep -o '"status":"[^"]*"' | cut -d'"' -f4)
        if [ "$health_status" == "UP" ]; then
            return 0  # Eureka Server正常
        fi
    fi
    return 1  # Eureka Server异常
}

restart_eureka() {
    echo "Eureka Server 异常，准备重启..."
    
    # 查找并关闭Eureka Server进程
    EUREKA_PID=$(ps aux | grep 'eureka-server' | grep -v grep | awk '{print $2}')
    if [ ! -z "$EUREKA_PID" ]; then
        kill -9 $EUREKA_PID
        echo "已关闭Eureka Server进程 $EUREKA_PID"
    fi
    
    # 等待3秒
    echo "等待3秒..."
    sleep 3
    
    # 启动Eureka Server
    echo "启动 Eureka Server..."
    cd "$EUREKA_SERVER_DIR" && mvn spring-boot:run &
    EUREKA_PID=$!
    echo $EUREKA_PID > ../eureka-server.pid
    
    # 等待Eureka Server启动
    echo "等待Eureka Server启动..."
    sleep 15
    
    # 检查重启是否成功
    retry_count=0
    while [ $retry_count -lt $MAX_RETRIES ]; do
        if check_eureka_status; then
            echo "Eureka Server 重启成功！"
            return 0
        fi
        retry_count=$((retry_count + 1))
        echo "重启检查失败，3秒后重试..."
        sleep 3
    done
    
    echo "重启失败，$MAX_RETRIES次尝试后放弃"
    return 1
}

# 主循环
echo "开始监控 Eureka Server..."
while true; do
    echo "检查 Eureka Server 状态..."
    if check_eureka_status; then
        echo "Eureka Server 状态正常"
    else
        restart_eureka
    fi
    
    # 等待30秒后再次检查
    echo "等待30秒后再次检查..."
    sleep 30
done
