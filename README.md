# OpenOntology

OpenOntology 是一个基于 Spring Cloud 微服务架构的协作式本体编辑器，参考 WebProtégé 设计，支持多语言、实时协作和多种本体格式导入导出。

## 项目信息

- **项目名称**: OpenOntology
- **当前版本**: v0.1.5
- **开发时间**: 2026-03-15
- **项目路径**: `d:\project\trae\java\OpenOntology`

## 技术架构

### 前端技术栈

- **框架**: Vue 3
- **UI 组件库**: Element Plus + Bootstrap 5
- **路由**: Vue Router
- **状态管理**: Vuex
- **国际化**: Vue I18n
- **HTTP 客户端**: Axios

### 后端技术栈

- **框架**: Spring Boot + Spring Cloud
- **微服务组件**: Eureka (服务注册)、Config Server (配置中心)、Gateway (网关)
- **数据存储**: MongoDB
- **本体处理**: OWL API 5.1.20
- **构建工具**: Maven 3.6.3

### 微服务模块

1. **eureka-server**: 服务注册中心
2. **config-server**: 配置中心
3. **gateway-service**: API 网关
4. **ontology-service**: 本体管理服务
5. **class-service**: 类管理服务
6. **property-service**: 属性管理服务
7. **instance-service**: 实例管理服务
8. **reasoning-service**: 本体推理服务
9. **collaboration-service**: 协作服务
10. **version-service**: 版本控制服务
11. **search-service**: 全文搜索服务
12. **user-service**: 用户管理服务
13. **i18n-service**: 国际化翻译服务

## 核心功能

### 1. 本体管理

- 创建、编辑、删除本体
- 支持多种本体格式：OWL、RDF/XML、Turtle、N-Triples
- 本体导入导出（支持 OWL、RDF/XML、Turtle、Manchester Syntax、Functional Syntax、XML、CSV、JSON 等格式）
- 命名空间管理
- 版本控制

### 2. 类管理

- 类的创建、编辑、删除
- 类层次结构可视化
- 支持父子类关系
- 多语言标签和注释

### 3. 属性管理

- 对象属性、数据属性、注释属性
- 属性约束（定义域、值域）
- 属性特征（函数型、传递性、对称性等）

### 4. 实例管理

- 实例的创建、编辑、删除
- 属性值管理
- 实例分类

### 5. 本体推理

- 一致性检查
- 分类推理
- 实例实现
- 支持多种推理器

### 6. 实时协作

- 多用户同时编辑
- 评论系统
- 协作会话管理
- WebSocket 实时通信

### 7. 多语言支持

- 12 种语言支持（中文、英文、德文、法文、西班牙文、意大利文、日文、韩文、俄文、葡萄牙文、阿拉伯文）
- 翻译确认工作流
- 翻译历史记录
- 管理员翻译管理界面

### 8. 翻译服务

集成多种翻译 API：

- **国际**: Google Translate、DeepL、Microsoft Translator、Yandex
- **国内**: 百度翻译、阿里云机器翻译、有道智云、腾讯云机器翻译

### 9. 全文搜索

- 跨本体搜索
- 实体类型过滤
- 高级搜索选项

### 10. 可视化编辑

- 图形化本体编辑器
- 类层次结构树形展示
- 多种布局选项

## 项目结构

```
OpenOntology/
├── eureka-server/              # Eureka 服务注册中心
│   ├── src/main/java/          # Java 源代码
│   ├── src/main/resources/     # 配置文件
│   └── pom.xml                 # 模块 POM
├── config-server/              # 配置中心
│   ├── src/main/java/
│   ├── src/main/resources/
│   └── pom.xml
├── gateway/                    # API 网关
│   ├── src/main/java/
│   ├── src/main/resources/
│   └── pom.xml
├── ontology-service/           # 本体管理服务
│   ├── src/main/java/com/by/open/ontology/ontologyservice/
│   │   ├── controller/         # 控制器层
│   │   ├── service/            # 业务逻辑层
│   │   ├── repository/         # 数据访问层
│   │   ├── entity/             # 实体类
│   │   └── config/             # 配置类
│   ├── src/main/resources/
│   └── pom.xml
├── class-service/              # 类管理服务
│   ├── src/main/java/com/by/open/ontology/classservice/
│   │   ├── controller/
│   │   ├── service/
│   │   ├── repository/
│   │   └── entity/
│   ├── src/main/resources/
│   └── pom.xml
├── property-service/           # 属性管理服务
│   ├── src/main/java/com/by/open/ontology/propertyservice/
│   │   ├── controller/
│   │   ├── service/
│   │   ├── repository/
│   │   └── entity/
│   ├── src/main/resources/
│   └── pom.xml
├── instance-service/           # 实例管理服务
│   ├── src/main/java/com/by/open/ontology/instanceservice/
│   │   ├── controller/
│   │   ├── service/
│   │   ├── repository/
│   │   └── entity/
│   ├── src/main/resources/
│   └── pom.xml
├── reasoning-service/          # 本体推理服务
│   ├── src/main/java/com/by/open/ontology/reasoningservice/
│   │   ├── controller/
│   │   ├── service/
│   │   ├── repository/
│   │   └── entity/
│   ├── src/main/resources/
│   └── pom.xml
├── collaboration-service/      # 协作服务
│   ├── src/main/java/com/by/open/ontology/collaborationservice/
│   │   ├── controller/
│   │   ├── service/
│   │   ├── repository/
│   │   └── entity/
│   ├── src/main/resources/
│   └── pom.xml
├── version-service/            # 版本控制服务
│   ├── src/main/java/com/by/open/ontology/versionservice/
│   │   ├── controller/
│   │   ├── service/
│   │   ├── repository/
│   │   └── entity/
│   ├── src/main/resources/
│   └── pom.xml
├── search-service/             # 全文搜索服务
│   ├── src/main/java/com/by/open/ontology/searchservice/
│   │   ├── controller/
│   │   ├── service/
│   │   ├── repository/
│   │   └── entity/
│   ├── src/main/resources/
│   └── pom.xml
├── user-service/               # 用户管理服务
│   ├── src/main/java/com/by/open/ontology/userservice/
│   │   ├── controller/
│   │   ├── service/
│   │   ├── repository/
│   │   └── entity/
│   ├── src/main/resources/
│   └── pom.xml
├── i18n-service/               # 国际化翻译服务
│   ├── src/main/java/com/by/open/ontology/i18nservice/
│   │   ├── controller/
│   │   ├── service/
│   │   ├── repository/
│   │   ├── entity/
│   │   └── config/
│   ├── src/main/resources/
│   └── pom.xml
├── frontend/                   # Vue3 前端应用
│   ├── src/
│   │   ├── views/              # 页面组件
│   │   │   ├── Home.vue
│   │   │   ├── Ontology.vue
│   │   │   ├── Class.vue
│   │   │   ├── Property.vue
│   │   │   ├── Instance.vue
│   │   │   ├── Editor.vue
│   │   │   ├── Hierarchy.vue
│   │   │   ├── Reasoning.vue
│   │   │   ├── Collaboration.vue
│   │   │   ├── Version.vue
│   │   │   ├── Search.vue
│   │   │   ├── ImportExport.vue
│   │   │   ├── TranslationAdmin.vue
│   │   │   ├── Login.vue
│   │   │   └── Register.vue
│   │   ├── components/         # 公共组件
│   │   │   ├── LanguageSwitcher.vue
│   │   │   ├── TranslationEditor.vue
│   │   │   └── TranslationTooltip.vue
│   │   ├── router/             # 路由配置
│   │   │   └── index.js
│   │   ├── locales/            # 多语言文件
│   │   │   ├── en.json
│   │   │   └── zh-CN.json
│   │   ├── main.js             # 入口文件
│   │   └── App.vue             # 根组件
│   ├── index.html
│   ├── package.json
│   ├── package-lock.json
│   └── vite.config.js
├── doc/                        # 文档目录
│   ├── ProjectTest.md          # 项目测试文档
│   └── hci-log.md              # 人机交互日志
├── pom.xml                     # 父 POM（聚合所有模块）
├── LICENSE                     # 许可证文件
└── README.md                   # 项目说明文档
```

## 快速开始

### 环境要求

- JDK 11+
- Maven 3.6.3
- Node.js 16+
- MongoDB 4.4+

### 后端启动

1. 启动 Eureka 服务注册中心

```bash
cd eureka-server
mvn spring-boot:run
```

1. 启动 Config Server

```bash
cd config-server
mvn spring-boot:run
```

1. 启动其他微服务（按依赖顺序）

```bash
# 启动网关
cd gateway
mvn spring-boot:run

# 启动本体服务
cd ontology-service
mvn spring-boot:run

# 启动其他服务...
```

### 前端启动

```bash
cd frontend
npm install
npm run dev
```

前端默认运行在 `http://localhost:5173`

### 访问应用

- 前端界面: <http://localhost:5173>
- Eureka 控制台: <http://localhost:8762>
- API 网关: <http://localhost:9000>

## 项目测试

### 后端测试

#### 单元测试

```bash
# 运行所有单元测试
cd <service-name>
mvn test

# 运行指定类的测试
mvn test -Dtest=ClassName

# 运行指定方法的测试
mvn test -Dtest=ClassName#methodName
```

#### 集成测试

```bash
# 运行集成测试
mvn verify

# 跳过测试编译
mvn clean install -DskipTests
```

#### 测试覆盖率

```bash
# 生成测试覆盖率报告
mvn jacoco:report

# 查看覆盖率报告
# 位于 target/site/jacoco/index.html
```

### 前端测试

#### 单元测试

```bash
cd frontend

# 运行单元测试
npm run test:unit

# 运行测试并生成覆盖率报告
npm run test:unit -- --coverage

# 运行测试并监视文件变化
npm run test:unit -- --watch
```

#### E2E 测试

```bash
# 运行端到端测试
npm run test:e2e

# 运行特定测试文件
npm run test:e2e -- --spec tests/e2e/specs/test.js
```

#### 代码质量检查

```bash
# ESLint 代码检查
npm run lint

# 自动修复 ESLint 问题
npm run lint -- --fix
```

### API 测试

#### 使用 curl 测试

```bash
# 测试本体服务健康状态
curl http://localhost:8080/actuator/health

# 测试获取所有本体
curl http://localhost:8080/api/ontology/findAll

# 测试创建本体
curl -X POST http://localhost:8080/api/ontology/create \
  -H "Content-Type: application/json" \
  -d '{"name":"Test Ontology","namespace":"http://example.org/test","description":"Test description"}'
```

#### 使用 Postman 测试

1. 导入 API 集合文件（位于 `docs/postman/` 目录）
2. 配置环境变量（baseUrl、token 等）
3. 运行测试集合

### 性能测试

#### JMeter 测试

```bash
# 运行 JMeter 性能测试
jmeter -n -t docs/jmeter/ontology-service.jmx -l results.jtl

# 生成 HTML 报告
jmeter -g results.jtl -o report/
```

#### 负载测试

```bash
# 使用 Apache Bench 进行简单负载测试
ab -n 1000 -c 10 http://localhost:8080/api/ontology/findAll
```

### 测试数据

#### 初始化测试数据

```bash
# 运行数据初始化脚本
mongo localhost:27017/openontology scripts/init-test-data.js
```

#### 清理测试数据

```bash
# 清理测试数据库
mongo localhost:27017/openontology scripts/cleanup-test-data.js
```

### 持续集成测试

项目支持 GitHub Actions 持续集成，自动运行以下测试：

1. **代码质量检查**: ESLint、SonarQube
2. **单元测试**: JUnit、Jest
3. **集成测试**: Spring Boot Test、Cypress
4. **安全扫描**: OWASP Dependency Check
5. **性能测试**: JMeter 基准测试

查看 `.github/workflows/ci.yml` 了解详细的 CI 配置。

## 配置说明

### 翻译 API 配置

在 `i18n-service/src/main/resources/application.yml` 中配置翻译 API 密钥：

```yaml
translation:
  google:
    api-key: ${GOOGLE_TRANSLATE_API_KEY:}
  deepl:
    api-key: ${DEEPL_API_KEY:}
  microsoft:
    api-key: ${MICROSOFT_TRANSLATE_API_KEY:}
  baidu:
    app-id: ${BAIDU_TRANSLATE_APP_ID:}
    secret-key: ${BAIDU_TRANSLATE_SECRET_KEY:}
  aliyun:
    access-key-id: ${ALIYUN_ACCESS_KEY_ID:}
    access-key-secret: ${ALIYUN_ACCESS_KEY_SECRET:}
  youdao:
    app-id: ${YOUDAO_APP_ID:}
    app-secret: ${YOUDAO_APP_SECRET:}
  tencent:
    secret-id: ${TENCENT_SECRET_ID:}
    secret-key: ${TENCENT_SECRET_KEY:}
    region: ${TENCENT_REGION:ap-beijing}
```

### MongoDB 配置

在各服务的 `application.yml` 中配置 MongoDB 连接：

```yaml
spring:
  data:
    mongodb:
      uri: mongodb://localhost:27017/openontology
```

## API 文档

### 本体管理 API

- `POST /api/ontology/create` - 创建本体
- `GET /api/ontology/findById/{id}` - 根据 ID 查询本体
- `GET /api/ontology/findByName?name={name}` - 根据名称查询本体
- `GET /api/ontology/findAll` - 查询所有本体
- `PUT /api/ontology/update` - 更新本体
- `DELETE /api/ontology/delete/{id}` - 删除本体

### 导入导出 API

- `GET /api/ontology/import-export/export/{ontologyId}?format={format}` - 导出本体
- `POST /api/ontology/import-export/import` - 导入本体
- `GET /api/ontology/import-export/formats` - 获取支持的格式列表

### 国际化 API

- `GET /api/i18n/translate` - 翻译文本
- `GET /api/i18n/languages` - 获取支持的语言列表
- `POST /api/i18n/confirm` - 确认翻译
- `GET /api/i18n/history` - 获取翻译历史

## 版本历史

| 版本     | 日期         | 说明               |
| ------ | ---------- | ---------------- |
| v0.1.0 | 2026-03-15 | 初始版本，项目架构重构      |
| v0.1.1 | 2026-03-15 | 功能完整性修复          |
| v0.1.2 | 2026-03-15 | 多语言支持实现          |
| v0.1.3 | 2026-03-15 | 项目重命名和配置更新       |
| v0.1.4 | 2026-03-15 | 多语言支持优化，集成翻译 API |
| v0.1.5 | 2026-03-15 | 本体导入导出功能实现       |

## 开发日志

详细的人机交互开发日志请查看 [hci-log.md](hci-log.md)。

## 待优化项

- [ ] 推理性能优化
- [ ] 协作功能的实时性提升
- [ ] 版本控制的冲突处理
- [ ] 翻译质量的评估机制
- [ ] 大型本体的导入导出性能优化
- [ ] 前端性能优化
- [ ] 单元测试和集成测试完善

## 参考项目

- [WebProtégé](https://github.com/protegeproject/webprotege) - 斯坦福大学开源本体编辑器

## 许可证

Apache-2.0 license

## 联系方式

如有问题或建议，请通过以下方式联系：

- 提交 Issue
- 发送邮件

***

**OpenOntology** - 让本体编辑更简单、更协作、更智能
