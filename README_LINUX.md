# Linux环境下Eureka Server异常处理方案

## 1. 脚本说明

### 1.1 启动脚本

- **start-all-services.sh**：启动所有服务的脚本，按顺序启动Eureka Server、Config Server和其他微服务
- **monitor-eureka.sh**：监控Eureka Server状态的脚本，异常时自动重启

### 1.2 系统服务配置

- **eureka-server.service**：Eureka Server的系统服务配置文件
- **eureka-monitor.service**：Eureka监控服务的系统服务配置文件

## 2. 使用方法

### 2.1 直接使用脚本

1. **赋予脚本执行权限**：
   ```bash
   chmod +x start-all-services.sh monitor-eureka.sh
   ```

2. **启动所有服务**：
   ```bash
   ./start-all-services.sh
   ```

3. **启动监控服务**：
   ```bash
   ./monitor-eureka.sh
   ```

### 2.2 配置系统服务（推荐）

1. **复制服务配置文件**：
   ```bash
   sudo cp eureka-server.service /etc/systemd/system/
   sudo cp eureka-monitor.service /etc/systemd/system/
   ```

2. **修改服务配置**（根据实际路径调整）：
   ```bash
   sudo nano /etc/systemd/system/eureka-server.service
   sudo nano /etc/systemd/system/eureka-monitor.service
   ```

3. **重新加载系统服务**：
   ```bash
   sudo systemctl daemon-reload
   ```

4. **启动服务**：
   ```bash
   sudo systemctl start eureka-server
   sudo systemctl start eureka-monitor
   ```

5. **设置开机自启**：
   ```bash
   sudo systemctl enable eureka-server
   sudo systemctl enable eureka-monitor
   ```

## 3. 配置优化

### 3.1 Eureka Server配置

- **自我保护模式**：已启用，防止网络波动导致服务被误删
- **心跳间隔**：30秒
- **服务过期时间**：90秒
- **Actuator端点**：已配置健康检查端点

### 3.2 客户端配置

- **健康检查**：已启用
- **注册间隔**：30秒
- **重试机制**：已配置Ribbon重试
- **断路器**：已配置Resilience4j

## 4. 监控机制

- **健康检查**：每30秒检查一次Eureka Server状态
- **自动重启**：异常时自动重启Eureka Server
- **重启验证**：检查重启是否成功
- **系统服务**：通过systemd管理，确保服务持续运行

## 5. 故障处理

### 5.1 常见问题

1. **Eureka Server启动失败**：
   - 检查端口是否被占用：`sudo netstat -tulpn | grep 8762`
   - 检查Maven配置：`mvn -v`
   - 检查Java环境：`java -version`

2. **服务注册失败**：
   - 检查Eureka Server是否运行：`curl http://localhost:8762/actuator/health`
   - 检查网络连接：`ping localhost`
   - 检查服务配置：`cat application.yml`

3. **监控脚本不工作**：
   - 检查curl是否安装：`sudo apt-get install curl`
   - 检查脚本权限：`ls -la monitor-eureka.sh`
   - 检查日志输出：`./monitor-eureka.sh`

### 5.2 日志查看

- **Eureka Server日志**：`sudo journalctl -u eureka-server`
- **监控服务日志**：`sudo journalctl -u eureka-monitor`
- **服务状态**：`sudo systemctl status eureka-server`

## 6. 注意事项

- 确保Linux环境安装了Java和Maven
- 确保防火墙开放了必要的端口（8762、8080等）
- 定期检查服务状态和日志
- 根据实际环境调整服务配置文件中的路径和用户

## 7. 故障恢复流程

1. 系统启动时，eureka-server.service自动启动
2. eureka-monitor.service在eureka-server启动后启动
3. 监控服务定期检查Eureka Server状态
4. 当Eureka Server异常时，自动重启
5. 重启后验证Eureka Server状态
6. 所有微服务通过健康检查和重试机制自动重新注册
