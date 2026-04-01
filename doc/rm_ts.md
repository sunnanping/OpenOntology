# 需求管理以及技术方案文档

## 文档信息

| 项目名称 | OpenOntology |
|---------|-------------|
| 项目版本 | v0.1.15 |
| 文档版本 | v0.1.15 |
| 创建日期 | 2026-03-28 |
| 最后更新 | 2026-03-31 |
| 文档作者 | sunnanping |

---

## 1. 业务背景

### 1.1 项目背景

OpenOntology 是一个基于 Spring Cloud 微服务架构的协作式本体编辑器，参考 WebProtégé 设计，支持多语言、实时协作和多种本体格式导入导出。项目旨在提供一个开源的、可扩展的本体编辑平台，支持团队协作和知识管理。

### 1.2 业务目标

- 提供完整的本体编辑和管理功能
- 支持多用户实时协作
- 支持多种本体格式导入导出
- 提供多语言支持
- 提供可视化的本体编辑界面

### 1.3 目标用户

- 本体工程师
- 知识图谱研究人员
- 语义网开发者
- 企业知识管理团队

---

## 2. 需求分析

### 2.1 业务需求

| 需求编号 | 需求名称 | 需求描述 | 优先级 | 状态 |
|---------|---------|---------|-------|------|
| REQ-001 | 项目架构重构 | 采取前后端分离的原则重构WebProtégé项目 | 高 | ✅ 已完成 |
| REQ-002 | 多语言支持 | 支持12种语言的界面和内容 | 高 | ✅ 已完成 |
| REQ-003 | 本体管理 | 本体的创建、编辑、删除、导入导出 | 高 | ✅ 已完成 |
| REQ-004 | 类管理 | 类的创建、编辑、删除、层次结构管理 | 高 | ✅ 已完成 |
| REQ-005 | 属性管理 | 对象属性、数据属性、注释属性管理 | 高 | ✅ 已完成 |
| REQ-006 | 实例管理 | 实例的创建、编辑、删除 | 高 | ✅ 已完成 |
| REQ-007 | 推理功能 | 一致性检查、分类推理 | 中 | ✅ 已完成 |
| REQ-008 | 协作功能 | 多用户协作、评论系统 | 中 | ✅ 已完成 |
| REQ-009 | 管理员功能 | 管理员管理、系统设置 | 高 | ✅ 已完成 |
| REQ-010 | 项目编辑器 | Class Hierarchy、类编辑、立体视图 | 高 | ✅ 已完成 |
| REQ-011 | 面板响应式加载 | 优化面板数据加载逻辑 | 中 | ✅ 已完成 |
| REQ-012 | Annotations优化 | 支持默认annotation显示和多annotation输入 | 高 | ✅ 已完成 |

### 2.2 已实现功能清单

#### 2.2.1 核心功能

| 功能模块 | 功能描述 | 完成版本 | 验证状态 |
|---------|---------|---------|---------|
| 项目架构重构 | Spring Cloud微服务架构 + Vue3前端 | v0.1.0 | ✅ 已验证 |
| 多语言支持 | 12种语言界面支持 | v0.1.2 | ✅ 已验证 |
| 翻译API集成 | 8种翻译API集成 | v0.1.4 | ✅ 已验证 |
| 本体导入导出 | 支持OWL、RDF/XML、Turtle等格式 | v0.1.5 | ✅ 已验证 |
| 管理员模块 | 管理员CRUD、系统设置 | v0.1.6 | ✅ 已验证 |
| 项目操作功能 | 项目创建、打开、下载、删除 | v0.1.6 | ✅ 已验证 |
| 项目编辑器 | Class Hierarchy、类编辑、立体视图 | v0.1.7 | ✅ 已验证 |
| DraggableModal | 可移动模态框组件 | v0.1.8 | ✅ 已验证 |
| AlertModal | 警告模态框组件（支持8方向缩放） | v0.1.9 | ✅ 已验证 |
| Merge Entities | 合并实体功能 | v0.1.10 | ✅ 已验证 |
| Tree拖拽 | Class Hierarchy树形拖拽功能 | v0.1.11 | ✅ 已验证 |
| Class Details | 类详情页面优化 | v0.1.12 | ✅ 已验证 |
| Annotation搜索 | Annotation属性搜索功能 | v0.1.13 | ✅ 已验证 |
| 代理配置修复 | 前端代理配置修复 | v0.1.14 | ✅ 已验证 |
| 面板响应式加载 | 4个面板响应式数据加载 | v0.1.15 | ✅ 已验证 |
| Annotations优化 | 支持默认annotation显示和多annotation输入格式 | v0.1.15 | ✅ 已验证 |

#### 2.2.2 通用组件

| 组件名称 | 文件路径 | 功能描述 |
|---------|---------|---------|
| AlertModal | frontend/src/components/AlertModal.vue | 警告/提示模态框，支持拖拽移动、8方向缩放、复制消息、截图 |
| DraggableModal | frontend/src/components/DraggableModal.vue | 可移动模态框，支持标题栏拖拽 |
| LanguageSwitcher | frontend/src/components/LanguageSwitcher.vue | 语言切换器，支持12种语言 |
| TranslationEditor | frontend/src/components/TranslationEditor.vue | 翻译编辑器 |
| TreeNode | frontend/src/components/TreeNode.vue | 递归树形组件 |
| ClassEditorPanel | frontend/src/components/modules/ClassEditorPanel.vue | 类编辑器面板，支持响应式数据加载 |

---

## 3. 技术方案

### 3.1 技术选型

| 层级 | 技术栈 | 版本 | 说明 |
|-----|-------|-----|------|
| 前端框架 | Vue 3 | 3.x | 前端框架 |
| UI组件库 | Element Plus + Bootstrap 5 | - | UI组件 |
| 路由 | Vue Router | 4.x | 路由管理 |
| 状态管理 | Vuex | 4.x | 状态管理 |
| 国际化 | Vue I18n | 9.x | 多语言支持 |
| HTTP客户端 | Axios | - | HTTP请求 |
| 后端框架 | Spring Boot | 2.7.x | 后端框架 |
| 微服务 | Spring Cloud | 2021.x | 微服务架构 |
| 服务注册 | Eureka | - | 服务注册中心 |
| 配置中心 | Config Server | - | 配置管理 |
| API网关 | Gateway | - | API网关 |
| 数据库 | MongoDB | 5.x | 数据存储 |
| 本体处理 | OWL API | 5.1.20 | 本体操作 |
| 构建工具 | Maven | 3.6.3 | 项目构建 |

### 3.2 系统架构

```
┌─────────────────────────────────────────────────────────────┐
│                        前端应用 (Vue3)                        │
│  ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌──────────┐       │
│  │  Admin   │ │ Projects │ │  Editor  │ │   i18n   │       │
│  └──────────┘ └──────────┘ └──────────┘ └──────────┘       │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                      API Gateway (8080)                      │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                    Eureka Server (8762)                      │
│                    Config Server (8888)                      │
└─────────────────────────────────────────────────────────────┘
                              │
        ┌─────────────────────┼─────────────────────┐
        ▼                     ▼                     ▼
┌──────────────┐    ┌──────────────┐    ┌──────────────┐
│ user-service │    │admin-service │    │ontology-svc  │
│    (8081)    │    │    (8089)    │    │    (8082)    │
└──────────────┘    └──────────────┘    └──────────────┘
        │                     │                     │
        └─────────────────────┼─────────────────────┘
                              ▼
                    ┌──────────────┐
                    │   MongoDB    │
                    └──────────────┘
```

### 3.3 微服务模块

| 模块名称 | 主要功能 | 端口 | 依赖服务 |
|---------|---------|-----|---------|
| eureka-server | 服务注册中心 | 8762 | 无 |
| config-server | 配置中心 | 8888 | eureka-server |
| gateway | API网关 | 8080 | eureka-server |
| user-service | 用户管理服务 | 8081 | eureka-server |
| admin-service | 管理员管理服务 | 8089 | eureka-server |
| ontology-service | 本体管理服务 | 8082 | eureka-server |
| collaboration-service | 协作服务 | 8087 | eureka-server |
| i18n-service | 国际化服务 | 8086 | eureka-server |

### 3.4 Annotations优化实现

#### 3.4.1 后端实现

1. **AnnotationParser工具类**
   - 位置：`ontology-service/class-module/src/main/java/com/by/open/ontology/ontologyservice/classmodule/service/AnnotationParser.java`
   - 功能：解析多种格式的annotation value，支持：
     - 带引号的格式：`"办公室"@zh, "Office"@en`
     - 不带引号的格式：`办公室@zh, Office@en`
     - @后为空的格式：`办公室@, Office@en`
     - 带分号结尾的格式：`办公室@zh, Office@en;`
     - 带引号的lang：`办公室@"zh", Office@"en"`

2. **AnnotationService增强**
   - 位置：`ontology-service/class-module/src/main/java/com/by/open/ontology/ontologyservice/classmodule/service/AnnotationService.java`
   - 新增方法：`processAnnotations`
   - 功能：
     - 从单个value字段解析多个annotation
     - 检查annotation的property+language组合是否存在
     - 存在则更新，不存在则新增
     - 更新Class实体中的annotations字段

3. **AnnotationController增强**
   - 位置：`ontology-service/class-module/src/main/java/com/by/open/ontology/ontologyservice/classmodule/controller/AnnotationController.java`
   - 新增端点：`/api/annotation/process`
   - 功能：处理批量annotation的创建和更新请求

#### 3.4.2 前端实现

1. **ClassEditorPanel.vue优化**
   - 位置：`frontend/src/components/modules/ClassEditorPanel.vue`
   - 功能：
     - 当Class实体的annotations为空时，自动添加默认的rdfs:label annotation
     - 为默认annotation显示打勾按钮，用于提交系统默认annotation数据
     - 支持在value输入框中输入多条annotation数据
     - 优化按钮样式和提示信息

2. **前端提示优化**
   - 修改value输入框的placeholder，添加示例格式：
     - `Enter value, for example: value-1@lang, value-2@lang`

#### 3.4.3 技术流程

1. **默认Annotation显示流程**
   - 前端加载Class详情时，检查annotations是否为空
   - 若为空，自动添加默认的rdfs:label annotation（value为Class名称）
   - 显示打勾按钮，用于提交默认annotation

2. **多Annotation输入流程**
   - 用户在value输入框中输入多条annotation，格式如：`办公室@zh, Office@en`
   - 前端检测到value包含逗号时，调用新的process端点
   - 后端解析value字段，创建或更新多个annotation
   - 前端重新加载Class详情，显示更新后的annotations

3. **Class节点显示名称更新流程**
   - 当用户提交或删除rdfs:label annotation时，前端检测是否为项目语言或语言为空
   - 如果符合条件，调用loadClassHierarchy()重新加载类层次结构
   - 重新加载时，buildTreeData函数会调用getNodeDisplayName函数重新计算每个节点的displayName
   - 节点显示名称按照优先级规则显示：匹配项目语言的rdfs:label → 语言为空的rdfs:label → 类名

### 3.5 Class节点显示名称优化实现

#### 3.5.1 前端实现

1. **getNodeDisplayName函数**
   - 位置：`frontend/src/components/modules/ClassEditorPanel.vue`
   - 功能：根据优先级规则获取节点的显示名称
     - 首先查找匹配项目语言的rdfs:label annotation
     - 其次查找语言为空的rdfs:label annotation
     - 最后使用类名

2. **buildTreeData函数优化**
   - 位置：`frontend/src/components/modules/ClassEditorPanel.vue`
   - 功能：在构建树形数据时，使用getNodeDisplayName函数获取每个节点的displayName

3. **Annotation提交后更新逻辑**
   - 位置：`frontend/src/components/modules/ClassEditorPanel.vue`
   - 功能：修改submitAnnotation、handleAnnotationLangBlur和handleNewAnnotationLangBlur函数
   - 当提交的是rdfs:label annotation且语言符合条件时，调用loadClassHierarchy()重新加载类层次结构

4. **Annotation删除后更新逻辑**
   - 位置：`frontend/src/components/modules/ClassEditorPanel.vue`
   - 功能：修改removeAnnotation函数
   - 当删除的是rdfs:label annotation且语言符合条件时，调用loadClassHierarchy()重新加载类层次结构

---

## 4. 项目模块依赖关系

### 4.1 核心模块依赖

| 模块名称 | 主要功能 | 依赖服务 | 配置文件 |
|---------|---------|---------|----------|
| eureka-server | 服务注册中心 | 无 | application.yml |
| config-server | 配置中心 | eureka-server | application.yml |
| gateway | API网关 | eureka-server, 所有业务服务 | application.yml |
| user-service | 用户管理服务 | eureka-server | application.yml |
| admin-service | 管理员管理服务 | eureka-server | application.yml |
| ontology-service | 本体管理服务 | eureka-server | application.yml |
| collaboration-service | 协作服务 | eureka-server | application.yml |
| version-service | 版本管理服务 | eureka-server | application.yml |
| search-service | 搜索服务 | eureka-server | application.yml |
| i18n-service | 国际化服务 | eureka-server | application.yml |

### 4.2 启动顺序

1. **eureka-server** (服务注册中心)
2. **config-server** (配置中心)
3. **业务服务** (可并行启动)
4. **gateway** (API网关)

### 4.3 启动命令

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

# Debug方式启动
mvn spring-boot:run -Dspring-boot.run.debug
```

---

## 5. 微服务URL

### 5.1 服务基础URL

| 服务名称 | 基础URL | 访问路径 |
|---------|---------|----------|
| eureka-server | http://localhost:8762 | 服务注册中心控制台，默认账号和口令为admin/admin123 |
| config-server | http://localhost:8888 | 配置中心 |
| gateway | http://localhost:8080 | API网关 |

### 5.2 业务服务访问路径（通过网关）

| 服务名称 | 访问路径 | 说明 |
|---------|---------|------|
| user-service | http://localhost:8080/api/user/** | 用户管理相关接口 |
| admin-service | http://localhost:8080/api/admin/** | 管理员管理相关接口 |
| ontology-service | http://localhost:8080/api/ontology/** | 本体管理相关接口 |
| class-module | http://localhost:8082/api/class/** | 类管理相关接口 |
| property-module | http://localhost:8082/api/property/** | 属性管理相关接口 |
| instance-module | http://localhost:8082/api/instance/** | 实例管理相关接口 |
| reasoning-module | http://localhost:8082/api/reasoning/** | 推理相关接口 |
| collaboration-service | http://localhost:8087/api/collaboration/** | 协作相关接口 |
| version-module | http://localhost:8082/api/version/** | 版本管理相关接口 |
| search-module | http://localhost:8082/api/search/** | 搜索相关接口 |

### 5.3 后端服务端口清单

| 服务名称 | 端口 | 说明 |
|---------|-----|------|
| eureka-server | 8762 | 服务注册中心 |
| config-server | 8888 | 配置中心 |
| gateway | 8080 | API网关 |
| user-service | 8081 | 用户服务 |
| ontology-service | 8082 | 本体服务 |
| collaboration-service | 8087 | 协作服务 |
| i18n-service | 8086 | 国际化服务 |
| admin-service | 8089 | 管理服务 |

---

## 6. UAT测试步骤

### 6.1 服务启动验证

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

### 6.2 功能测试

#### 6.2.1 用户服务测试
- 测试用户注册：POST /api/user/register
- 测试用户登录：POST /api/user/login
- 测试用户信息查询：GET /api/user/{id}

#### 6.2.2 管理员服务测试
- 测试创建管理员：POST /api/admin/create
- 测试管理员登录：POST /api/admin/login
- 测试查询所有管理员：GET /api/admin/findAll
- 测试查询管理员详情：GET /api/admin/findById/{id}
- 测试更新管理员：PUT /api/admin/update
- 测试删除管理员：DELETE /api/admin/delete/{id}
- 测试获取系统设置：GET /api/admin/settings
- 测试更新系统设置：PUT /api/admin/settings

#### 6.2.3 本体服务测试
- 测试创建本体：POST /api/ontology
- 测试查询本体列表：GET /api/ontology
- 测试查询本体详情：GET /api/ontology/{id}
- 测试导出本体：GET /api/ontology/{id}/export
- 测试导入本体：POST /api/ontology/import

#### 6.2.4 类服务测试
- 测试创建类：POST /api/class
- 测试查询类列表：GET /api/class
- 测试查询类详情：GET /api/class/{id}

#### 6.2.5 属性服务测试
- 测试创建属性：POST /api/property
- 测试查询属性列表：GET /api/property
- 测试查询属性详情：GET /api/property/{id}

#### 6.2.6 实例服务测试
- 测试创建实例：POST /api/instance
- 测试查询实例列表：GET /api/instance
- 测试查询实例详情：GET /api/instance/{id}

#### 6.2.7 推理服务测试
- 测试执行推理：POST /api/reasoning/execute
- 测试查询推理结果：GET /api/reasoning/result/{id}

#### 6.2.8 协作服务测试
- 测试创建协作：POST /api/collaboration
- 测试查询协作列表：GET /api/collaboration
- 测试查询协作详情：GET /api/collaboration/{id}

#### 6.2.9 版本服务测试
- 测试创建版本：POST /api/version
- 测试查询版本列表：GET /api/version
- 测试查询版本详情：GET /api/version/{id}

#### 6.2.10 搜索服务测试
- 测试搜索本体：GET /api/search/ontology
- 测试搜索类：GET /api/search/class
- 测试搜索属性：GET /api/search/property
- 测试搜索实例：GET /api/search/instance

#### 6.2.11 Annotation服务测试

- **基础CRUD测试**：
  - 测试创建Annotation：POST /api/annotation/create
    - 请求体示例：
      ```json
      {
        "entityId": "class-id-123",
        "entityType": "CLASS",
        "property": "rdfs:label",
        "language": "en",
        "value": "Office"
      }
      ```
  - 测试查询Annotation列表：GET /api/annotation/findByEntityIdAndEntityType/{entityId}/{entityType}
  - 测试查询Annotation详情：GET /api/annotation/findById/{id}
  - 测试更新Annotation：PUT /api/annotation/update
  - 测试删除Annotation：DELETE /api/annotation/delete/{id}

- **多Annotation输入测试**：
  - 测试批量处理Annotation：POST /api/annotation/process
    - 请求体示例（不带引号）：
      ```json
      {
        "entityId": "class-id-123",
        "entityType": "CLASS",
        "property": "rdfs:label",
        "value": "办公室@zh, Office@en"
      }
      ```
    - 请求体示例（带引号）：
      ```json
      {
        "entityId": "class-id-123",
        "entityType": "CLASS",
        "property": "rdfs:label",
        "value": "\"办公室\"@zh, \"Office\"@en"
      }
      ```
    - 请求体示例（带分号结尾）：
      ```json
      {
        "entityId": "class-id-123",
        "entityType": "CLASS",
        "property": "rdfs:label",
        "value": "办公室@zh, Office@en;"
      }
      ```
    - 请求体示例（语言为空）：
      ```json
      {
        "entityId": "class-id-123",
        "entityType": "CLASS",
        "property": "rdfs:label",
        "value": "办公室@, Office@en"
      }
      ```

- **Annotation属性搜索测试**：
  - 测试搜索Annotation属性：GET /api/annotation/searchProperties?keyword=rdfs
    - 返回匹配的Annotation属性列表
  - 测试搜索所有Annotation属性：GET /api/annotation/searchProperties（不传keyword参数）

- **测试案例**：
  1. **创建单个Annotation**：
     - 创建一个rdfs:label annotation，语言为en
     - 验证创建成功，返回annotation id
     - 查询该annotation，验证数据正确
  
  2. **创建多个Annotation**：
     - 使用process端点创建多个annotation（不同语言）
     - 验证所有annotation都创建成功
     - 查询该实体的所有annotation，验证数量和内容正确
  
  3. **更新已存在的Annotation**：
     - 创建一个rdfs:label annotation，语言为en，value为"Office"
     - 再次使用process端点，相同property和language，value改为"Workplace"
     - 验证annotation被更新而不是重复创建
     - 查询该实体的annotation，验证value已更新
  
  4. **删除Annotation**：
     - 创建一个annotation
     - 删除该annotation
     - 查询该annotation，验证已删除
  
  5. **Class节点显示名称测试**：
     - 创建一个Class，不添加任何annotation
     - 验证节点显示名称为类名
     - 添加rdfs:label annotation，语言为项目默认语言
     - 验证节点显示名称更新为annotation的value
     - 删除该annotation
     - 验证节点显示名称恢复为类名

#### 6.2.12 前端Admin模块测试

- **管理员登录页面**：访问 http://localhost:5173/admin/login
  - 输入用户名和密码
  - 测试登录功能
  - 登录成功后跳转到管理员列表页面

- **创建管理员页面**：访问 http://localhost:5173/admin/create
  - 填写管理员信息（用户名、密码、邮箱等）
  - 测试创建功能
  - 创建成功后显示成功提示

- **管理员列表页面**：访问 http://localhost:5173/admin/list
  - 查看管理员列表
  - 测试编辑功能
  - 测试删除功能

- **更新管理员页面**：访问 http://localhost:5173/admin/update?id={adminId}
  - 编辑管理员信息
  - 测试更新功能
  - 更新成功后跳转到管理员列表页面

- **系统设置页面**：访问 http://localhost:5173/admin/settings
  - 修改系统设置（如应用URL、权限设置、上传限制等）
  - 测试保存功能
  - 保存成功后显示成功提示

#### 6.2.13 前端Projects模块测试

- **项目清单页面**：访问 http://localhost:5173/projects/list
  - 查看项目列表（显示项目名称、所有者、最后打开时间、最后修改时间）
  - 测试筛选功能（Owned by Me、Shared with Me、Trash）
  - 测试排序功能（Last Opened、Last Modified、Name、Owner）
  - 管理员角色显示"Create New Project"按钮

- **项目操作菜单测试**：
  - **Open**：点击操作菜单中的"Open"
    - 更新项目最后打开时间
    - 跳转到项目编辑页面
  
  - **Open in new window**：点击操作菜单中的"Open in new window"
    - 更新项目最后打开时间
    - 在新窗口打开项目编辑页面
  
  - **Download**：点击操作菜单中的"Download"
    - 下载项目文件（支持OWL、RDF/XML、Turtle等多种格式）
    - 验证下载文件内容正确
  
  - **Move to trash**：点击操作菜单中的"Move to trash"
    - 将项目移动到回收站
    - 更新项目状态为TRASH
    - 项目从列表中消失（Trash筛选时可见）

- **创建项目模态框**：
  - 点击"Create New Project"按钮打开模态框
  - **创建新项目**：
    - 输入项目名称
    - 选择项目语言（支持16种语言）
    - 点击"Create"按钮创建项目
    - 新项目添加到列表中
  
  - **从现有资源创建**：
    - 点击"Create from existing sources"展开文件上传区域
    - 选择文件格式（OWL、RDF/XML、Turtle等）
    - 上传本体文件
    - 点击"Create"按钮导入项目
    - 新项目添加到列表中
  
  - **模态框操作**：
    - 测试拖拽移动模态框
    - 点击"×"按钮关闭模态框

#### 6.2.14 项目编辑器测试

- **项目编辑器页面**：访问 http://localhost:5173/projects/edit/{projectId}
  - 页面布局验证：
    - 左侧：Class Hierarchy面板
    - 中间：内容编辑区
    - 右侧：Comments和Project Feed面板

- **Class Hierarchy面板测试**：
  - **树形结构展示**：
    - 验证任意层级树形结构正确显示
    - 点击展开/折叠节点
    - 选择类节点，中间区域显示类详情
  
  - **添加类功能**：
    - 点击"+"按钮打开Add Class弹窗
    - 输入类名称（支持多行，每行一个类名）
    - 设置语言标签
    - 点击Create创建类
    - 验证新类添加到选中节点下
  
  - **删除类功能**：
    - 选中类节点
    - 点击"✕"按钮打开删除确认弹窗
    - 确认删除
    - 验证类从树中移除
  
  - **搜索类功能**：
    - 点击"🔍"按钮打开Search弹窗
    - 输入搜索关键词
    - 验证搜索结果正确显示
    - 点击搜索结果跳转到对应类
  
  - **拖拽调整层级**：
    - 拖拽类节点到目标父节点
    - 验证类移动到新位置
    - 验证循环引用检测（不能将父节点拖入子节点）
  
  - **右键菜单**:
    - 右键点击类节点
    - 验证菜单选项（Add Subclass、Delete、Rename、Move、Merge Into...、Show Direct Link、Watch...）
    - 测试各菜单项功能
  
  - **Merge Entities功能**:
    - 右键点击类节点，选择"Merge Into..."
    - 验证Merge Entities对话框显示
    - 测试搜索类功能
    - 测试类树选择功能
    - 验证无数据时显示默认的owl:Thing根节点

- **类编辑视图测试**：
  - 选择类后显示编辑视图
  - **面板响应式加载测试**：
    - 点击Class Hierarchy中的类节点
    - 验证4个面板（class、Comments、Project Feed、Description）先显示加载状态
    - 验证数据加载完成后显示正确内容
    - 验证面板结构保持不变，避免重新加载时的闪烁
    - 验证并行加载提高响应速度
  
  - **Annotations编辑**：
    - 查看现有注释
    - 添加新注释（属性、值、语言标签）
    - 删除注释
  
  - **Parents编辑**：
    - 查看当前父类
    - 搜索并添加新父类
    - 删除父类
  
  - **Relationships编辑**：
    - 添加新关系
    - 删除关系
  
  - **保存功能**：
    - 点击Save保存修改
    - 点击Cancel取消修改

- **立体视图测试**：
  - 点击"🌐"按钮切换到立体视图
  - **图形展示**：
    - 验证类层次结构图形正确显示
    - 父类显示在上方
    - 子类显示在下方
  
  - **缩放功能**：
    - 点击"+"放大
    - 点击"-"缩小
    - 点击"⟲"重置缩放
  
  - **下载功能**：
    - 点击PNG下载PNG格式图片
    - 点击JPG下载JPG格式图片
    - 点击DrawIO下载DrawIO格式文件
    - 点击Mermaid下载Mermaid脚本文件

- **数据全生命周期测试**：
  - 点击"📜"按钮切换到历史视图
  - **历史记录展示**：
    - 验证变更历史列表正确显示
    - 显示变更日期、版本号、作者、变更内容
  
  - **分页功能**：
    - 验证分页控件显示
    - 点击Prev/Next切换页面
    - 验证页码信息正确

- **右侧面板测试**：
  - **Comments面板**：
    - 查看评论列表
    - 添加新评论
    - 验证评论显示正确
  
  - **Project Feed面板**：
    - 查看项目动态列表
    - 验证动态内容正确
  
  - **面板大小调整**：
    - 拖拽中间分隔条
    - 验证上下面板大小可调整
    - 验证最小高度限制

- **面板拖拽测试**：
  - 拖拽Class Hierarchy面板标题栏
  - 验证面板位置可移动
  - 拖拽右侧面板标题栏
  - 验证面板位置可移动

### 6.3 集成测试

1. **完整流程测试**
   - 创建管理员 → 管理员登录 → 创建用户 → 用户登录 → 创建本体 → 创建类 → 创建属性 → 创建实例 → 执行推理 → 搜索

2. **权限测试**
   - 测试不同角色的访问权限
   - 测试未登录用户的访问限制

3. **性能测试**
   - 测试并发请求处理能力
   - 测试大数据量下的响应时间

4. **容错测试**
   - 测试服务下线后的自动恢复
   - 测试网络异常时的处理

---

## 7. 配置要求

### 7.1 环境要求

- JDK 11+
- Maven 3.6+
- MongoDB（用于存储本体数据）
- Git（用于配置中心）

### 7.2 配置文件修改

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

---

## 8. 故障排查

### 8.1 常见问题

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

### 8.2 日志查看

- 服务日志：查看各服务的控制台输出
- 系统日志：查看 `logs` 目录下的日志文件
- Eureka日志：访问eureka-server控制台查看服务状态

---

## 9. 部署建议

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

---

## 10. 版本历史

| 版本 | 日期 | 说明 |
|-----|------|------|
| v0.1.0 | 2026-03-15 | 初始版本，项目架构重构 |
| v0.1.1 | 2026-03-15 | 功能完整性修复 |
| v0.1.2 | 2026-03-15 | 多语言支持实现 |
| v0.1.3 | 2026-03-15 | 项目重命名和配置更新 |
| v0.1.4 | 2026-03-15 | 多语言支持优化，集成翻译API |
| v0.1.5 | 2026-03-15 | 本体导入导出功能实现 |
| v0.1.6 | 2026-03-16 | 管理员模块、项目操作功能、系统设置实现 |
| v0.1.7 | 2026-03-16 | 项目编辑器实现（Class Hierarchy、类编辑、立体视图、数据全生命周期、评论和动态） |
| v0.1.8 | 2026-03-18 | DraggableModal组件移动功能实现 |
| v0.1.9 | 2026-03-19 | AlertModal和DraggableModal组件移动功能Bug修复 |
| v0.1.10 | 2026-03-23 | 实现Merge Entities功能，添加Tree选择和无数据时显示根节点 |
| v0.1.11 | 2026-03-24 | Class Hierarchy Tree组件拖拽功能实现 |
| v0.1.12 | 2026-03-26 | Class Details页面优化 |
| v0.1.13 | 2026-03-27 | ClassEditorPanel.vue代码错误修复，Annotation属性搜索功能实现 |
| v0.1.14 | 2026-03-29 | 项目编译和前端代理配置修复 |
| v0.1.15 | 2026-03-31 | Annotations优化，支持默认annotation显示和多annotation输入格式，优化Class节点显示名称逻辑 |

---

## 11. 待优化项

- [ ] 推理性能优化
- [ ] 协作功能的实时性提升
- [ ] 版本控制的冲突处理
- [ ] 翻译质量的评估机制
- [ ] 大型本体的导入导出性能优化
- [ ] 前端性能优化
- [ ] 单元测试和集成测试完善

---

## 12. 附录

### 12.1 术语表

| 术语 | 说明 |
|-----|------|
| 本体 | 对领域知识的正式描述 |
| OWL | Web本体语言 |
| RDF | 资源描述框架 |
| IRI | 国际化资源标识符 |
| Annotation | 注解，用于添加元数据 |
| Class | 类，本体中的概念 |
| Property | 属性，类之间的关系或特征 |
| Instance | 实例，类的具体对象 |

### 12.2 参考文档

- [WebProtégé](https://github.com/protegeproject/webprotege)
- [OWL API](https://github.com/owlcs/owlapi)
- [Spring Cloud](https://spring.io/projects/spring-cloud)
- [Vue 3](https://vuejs.org/)

---

## 修订历史

| 版本 | 日期 | 修订人 | 修订内容 |
|-----|------|-------|---------|
| v1.0 | 2026-03-30 | sunnanping | 初始版本，整合ProjectTest.md和已实现功能 |
