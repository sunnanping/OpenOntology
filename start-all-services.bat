@echo off

rem 启动顺序：eureka-server -> config-server -> 其他服务

rem 启动Eureka Server
echo 启动 Eureka Server...
start "Eureka Server" cmd /k "cd eureka-server && mvn spring-boot:run"

rem 等待Eureka Server启动
ping 127.0.0.1 -n 10 > nul

echo Eureka Server 已启动

rem 启动 Config Server
echo 启动 Config Server...
start "Config Server" cmd /k "cd config-server && mvn spring-boot:run"

rem 等待Config Server启动
ping 127.0.0.1 -n 8 > nul

echo Config Server 已启动

rem 启动其他服务
echo 启动 User Service...
start "User Service" cmd /k "cd user-service && mvn spring-boot:run"

echo 启动 Gateway...
start "Gateway" cmd /k "cd gateway && mvn spring-boot:run"

echo 启动 Ontology Service...
start "Ontology Service" cmd /k "cd ontology-service/ontology-service-app && mvn spring-boot:run"

echo 启动 Collaboration Service...
start "Collaboration Service" cmd /k "cd collaboration-service && mvn spring-boot:run"

echo 启动 I18n Service...
start "I18n Service" cmd /k "cd i18n-service && mvn spring-boot:run"

echo 启动 Admin Service...
start "Admin Service" cmd /k "cd admin-service && mvn spring-boot:run"

echo 所有服务启动命令已执行，请检查各服务状态
pause
