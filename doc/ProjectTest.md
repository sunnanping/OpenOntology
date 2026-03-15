# OpenOntology 项目测试文档

## 1. 项目模块依赖关系

### 核心模块依赖

| 模块名称 | 主要功能 | 依赖服务 | 配置文件 |
|---------|---------|---------|----------|
| eureka-server | 服务注册中心 | 无 | application.yml |
| config-server | 配置中心 | eureka-server | application.yml |
| gateway | API网关 | eureka-server, 所有业务服务 | application.yml |
| user-service | 用户管理服务 | eureka-server | application.yml |
| ontology-service | 本体管理服务 | eureka-server | application.yml |
| class-service | 类管理服务 | eureka-server | application.yml |
| property-service | 属性管理服务 | eureka-server | application.yml |
| instance-service | 实例管理服务 | eureka-server | application.yml |
| reasoning-service | 推理服务 | eureka-server | application.yml |
| collaboration-service | 协作服务 | eureka-server | application.yml |
| version-service | 版本管理服务 | eureka-server | application.yml |
| search-service | 搜索服务 | eureka-server | application.yml |
| i18n-service | 国际化服务 | eureka-server | application.yml |

## 2. 启动顺序

### 推荐启动顺序

1. **eureka-server** (服务注册中心)
2. **config-server** (配置中心)
3. **业务服务** (可并行启动)：
   - user-service
   - ontology-service
   - class-service
   - property-service
   - instance-service
   - reasoning-service
   - collaboration-service
   - version-service
   - search-service
   - i18n-service
4. **gateway** (API网关)

### 启动命令

```bash
# 启动eureka-server
cd eureka-server
mvn spring-boot:run

# 启动config-server
cd config-server
mvn spring-boot:run

# 启动业务服务（示例）
cd user-service
mvn spring-boot:run

# 启动gateway
cd gateway
mvn spring-boot:run
```

## 3. 微服务URL

### 服务基础URL

| 服务名称 | 基础URL | 访问路径 |
|---------|---------|----------|
| eureka-server | http://localhost:8762 | 服务注册中心控制台 |
| config-server | http://localhost:8888 | 配置中心 |
| gateway | http://localhost:8080 | API网关 |

### 业务服务访问路径（通过网关）

| 服务名称 | 访问路径 | 说明 |
|---------|---------|------|
| user-service | http://localhost:8080/api/user/** | 用户管理相关接口 |
| ontology-service | http://localhost:8080/api/ontology/** | 本体管理相关接口 |
| class-service | http://localhost:8080/api/class/** | 类管理相关接口 |
| property-service | http://localhost:8080/api/property/** | 属性管理相关接口 |
| instance-service | http://localhost:8080/api/instance/** | 实例管理相关接口 |
| reasoning-service | http://localhost:8080/api/reasoning/** | 推理相关接口 |
| collaboration-service | http://localhost:8080/api/collaboration/** | 协作相关接口 |
| version-service | http://localhost:8080/api/version/** | 版本管理相关接口 |
| search-service | http://localhost:8080/api/search/** | 搜索相关接口 |

## 4. UAT测试步骤

### 4.1 服务启动验证

1. **启动eureka-server**
   - 访问 http://localhost:8762
   - 验证服务注册中心正常运行

2. **启动config-server**
   - 访问 http://localhost:8888
   - 验证配置中心正常运行

3. **启动所有业务服务**
   - 等待服务注册到eureka-server
   - 访问eureka-server控制台，确认所有服务已注册

4. **启动gateway**
   - 访问 http://localhost:8080
   - 验证网关正常运行

### 4.2 功能测试

#### 4.2.1 用户服务测试
- 测试用户注册：POST /api/user/register
- 测试用户登录：POST /api/user/login
- 测试用户信息查询：GET /api/user/{id}

#### 4.2.2 本体服务测试
- 测试创建本体：POST /api/ontology
- 测试查询本体列表：GET /api/ontology
- 测试查询本体详情：GET /api/ontology/{id}
- 测试导出本体：GET /api/ontology/{id}/export
- 测试导入本体：POST /api/ontology/import

#### 4.2.3 类服务测试
- 测试创建类：POST /api/class
- 测试查询类列表：GET /api/class
- 测试查询类详情：GET /api/class/{id}

#### 4.2.4 属性服务测试
- 测试创建属性：POST /api/property
- 测试查询属性列表：GET /api/property
- 测试查询属性详情：GET /api/property/{id}

#### 4.2.5 实例服务测试
- 测试创建实例：POST /api/instance
- 测试查询实例列表：GET /api/instance
- 测试查询实例详情：GET /api/instance/{id}

#### 4.2.6 推理服务测试
- 测试执行推理：POST /api/reasoning/execute
- 测试查询推理结果：GET /api/reasoning/result/{id}

#### 4.2.7 协作服务测试
- 测试创建协作：POST /api/collaboration
- 测试查询协作列表：GET /api/collaboration
- 测试查询协作详情：GET /api/collaboration/{id}

#### 4.2.8 版本服务测试
- 测试创建版本：POST /api/version
- 测试查询版本列表：GET /api/version
- 测试查询版本详情：GET /api/version/{id}

#### 4.2.9 搜索服务测试
- 测试搜索本体：GET /api/search/ontology
- 测试搜索类：GET /api/search/class
- 测试搜索属性：GET /api/search/property
- 测试搜索实例：GET /api/search/instance

### 4.3 集成测试

1. **完整流程测试**
   - 创建用户 → 登录 → 创建本体 → 创建类 → 创建属性 → 创建实例 → 执行推理 → 搜索

2. **权限测试**
   - 测试不同角色的访问权限
   - 测试未登录用户的访问限制

3. **性能测试**
   - 测试并发请求处理能力
   - 测试大数据量下的响应时间

4. **容错测试**
   - 测试服务下线后的自动恢复
   - 测试网络异常时的处理

## 5. 配置要求

### 5.1 环境要求

- JDK 11+
- Maven 3.6+
- MongoDB（用于存储本体数据）
- Git（用于配置中心）

### 5.2 配置文件修改

1. **eureka-server**
   - 可修改 `application.yml` 中的端口和安全配置

2. **config-server**
   - 需要修改 `application.yml` 中的Git仓库配置
   - 替换 `username` 和 `password` 为实际的GitHub凭证

3. **业务服务**
   - 可根据需要修改 `application.yml` 中的服务配置
   - 确保eureka服务地址正确

4. **gateway**
   - 可根据需要修改路由规则
   - 确保eureka服务地址正确

## 6. 故障排查

### 6.1 常见问题

1. **服务注册失败**
   - 检查eureka-server是否正常运行
   - 检查服务配置中的eureka地址是否正确
   - 检查网络连接是否正常

2. **网关路由失败**
   - 检查服务是否已注册到eureka
   - 检查路由规则是否正确
   - 检查服务是否正常运行

3. **数据库连接失败**
   - 检查MongoDB服务是否正常运行
   - 检查连接配置是否正确

4. **配置中心连接失败**
   - 检查Git仓库配置是否正确
   - 检查网络连接是否正常

### 6.2 日志查看

- 服务日志：查看各服务的控制台输出
- 系统日志：查看 `logs` 目录下的日志文件
- Eureka日志：访问eureka-server控制台查看服务状态

## 7. 部署建议

1. **开发环境**
   - 本地启动所有服务
   - 使用默认配置

2. **测试环境**
   - 部署到测试服务器
   - 配置独立的数据库
   - 启用监控

3. **生产环境**
   - 集群部署eureka-server
   - 配置负载均衡
   - 启用HTTPS
   - 配置详细的监控和告警
