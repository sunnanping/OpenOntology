@echo off

rem 监控Eureka Server状态的脚本
setlocal

set EUREKA_SERVER_URL=http://localhost:8762/actuator/health
set EUREKA_SERVER_DIR=eureka-server
set MAX_RETRIES=3

:CHECK_LOOP
echo 检查 Eureka Server 状态...

rem 使用curl检查Eureka Server的健康状态
curl -s %EUREKA_SERVER_URL% > health-check.json

rem 检查是否返回200状态码
if %errorlevel% neq 0 goto RESTART_EUREKA

rem 检查返回的JSON是否包含"UP"状态
findstr /i "\"status\":\"UP\"" health-check.json > nul
if %errorlevel% neq 0 goto RESTART_EUREKA

echo Eureka Server 状态正常
rem 清理临时文件
del health-check.json 2> nul

rem 等待30秒后再次检查
echo 等待30秒后再次检查...
ping 127.0.0.1 -n 31 > nul
goto CHECK_LOOP

:RESTART_EUREKA
echo Eureka Server 异常，准备重启...

rem 清理临时文件
del health-check.json 2> nul

rem 查找并关闭Eureka Server进程
for /f "tokens=2" %%a in ('tasklist /fi "imagename eq java.exe" /fo list ^| findstr /i "eureka-server"') do (
    taskkill /F /PID %%a
    echo 已关闭Eureka Server进程 %%a
)

rem 等待3秒
echo 等待3秒...
ping 127.0.0.1 -n 4 > nul

rem 启动Eureka Server
echo 启动 Eureka Server...
start "Eureka Server" cmd /k "cd %EUREKA_SERVER_DIR% && mvn spring-boot:run"

rem 等待Eureka Server启动
echo 等待Eureka Server启动...
ping 127.0.0.1 -n 15 > nul

rem 检查重启是否成功
set /a RETRY_COUNT=0
:CHECK_RESTART
set /a RETRY_COUNT+=1

curl -s %EUREKA_SERVER_URL% > health-check.json
if %errorlevel% neq 0 goto CHECK_RESTART_FAILED

findstr /i "\"status\":\"UP\"" health-check.json > nul
if %errorlevel% neq 0 goto CHECK_RESTART_FAILED

echo Eureka Server 重启成功！
del health-check.json 2> nul

rem 等待30秒后继续监控
echo 等待30秒后继续监控...
ping 127.0.0.1 -n 31 > nul
goto CHECK_LOOP

:CHECK_RESTART_FAILED
del health-check.json 2> nul
if %RETRY_COUNT% lss %MAX_RETRIES% (
    echo 重启检查失败，%MAX_RETRIES%秒后重试...
    ping 127.0.0.1 -n %MAX_RETRIES% > nul
    goto CHECK_RESTART
) else (
    echo 重启失败，%MAX_RETRIES%次尝试后放弃
    pause
    exit /b 1
)
