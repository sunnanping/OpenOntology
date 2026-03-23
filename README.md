# OpenOntology

OpenOntology 是一个基于 Spring Cloud 微服务架构的协作式本体编辑器，参考 WebProtégé 设计，支持多语言、实时协作和多种本体格式导入导出。

## 项目信息

- **项目名称**: OpenOntology
- **开发工具**: TRAE CN V3.3.38
```
TRAE CN
版本: 3.3.38
VSCode 版本: 1.107.1
提交: 4de6fa3dee64450584b96e0200bedcdccbb410a8
日期: 2026-03-14T04:18:06.511Z
Electron: 39.2.7-release.1.26.3 (aha)
Node.js: 22.21.1
V8: 14.2.231.23-electron.0
OS: Windows_NT x64 10.0.26100
构建版本: 2.3.13959
设备ID: 539c83fc871098474035cc36c35fa33a0d0f0dad13ec95cba5e56a8b05e47889
Device Id: 1804719102368745
```
- **当前版本**: v0.1.9
- **启动时间**: 2026-03-14
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
13. **admin-service**: 管理员管理服务
14. **i18n-service**: 国际化翻译服务

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

### 11. 项目编辑器

- **类层次结构管理**
  - 支持任意层级的树形结构
  - 拖拽调整类层级关系
  - 添加、删除、搜索类功能
  - 可移动、响应式的操作弹窗
  - 右键菜单支持快捷操作

- **类编辑功能**
  - 编辑类属性（Annotations、Parents、Relationships）
  - 多语言标签支持
  - 父类搜索和添加

- **立体视图**
  - 类层次结构图形化展示
  - 支持放大、缩小、重置
  - 支持多种格式下载（PNG、JPG、DrawIO、Mermaid）

- **数据全生命周期**
  - 变更历史记录
  - 分页浏览历史记录
  - 变更详情展示

- **评论和动态**
  - 类相关评论功能
  - 项目动态实时展示
  - 面板大小可拖拽调整

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
├── admin-service/              # 管理员管理服务
│   ├── src/main/java/com/by/open/ontology/adminservice/
│   │   ├── controller/
│   │   ├── service/
│   │   ├── repository/
│   │   ├── entity/
│   │   └── config/
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
│   │   │   ├── Register.vue
│   │   │   ├── Projects/
│   │   │   │   ├── ProjectList.vue
│   │   │   │   ├── CreateProject.vue
│   │   │   │   └── ProjectEditor.vue
│   │   │   └── Admin/
│   │   │       ├── CreateAdmin.vue
│   │   │       ├── LoginAdmin.vue
│   │   │       ├── ListAdmin.vue
│   │   │       └── UpdateAdmin.vue
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

## 版本历史

| 版本     | 日期         | 说明               |
| ------ | ---------- | ---------------- |
| v0.1.0 | 2026-03-15 | 初始版本，项目架构重构      |
| v0.1.1 | 2026-03-15 | 功能完整性修复          |
| v0.1.2 | 2026-03-15 | 多语言支持实现          |
| v0.1.3 | 2026-03-15 | 项目重命名和配置更新       |
| v0.1.4 | 2026-03-15 | 多语言支持优化，集成翻译 API |
| v0.1.5 | 2026-03-15 | 本体导入导出功能实现       |
| v0.1.6 | 2026-03-16 | 管理员模块、项目操作功能、系统设置实现 |
| v0.1.7 | 2026-03-16 | 项目编辑器实现（Class Hierarchy、类编辑、立体视图、数据全生命周期、评论和动态） |
| v0.1.8 | 2026-03-18 | DraggableModal组件移动功能实现 |
| v0.1.9 | 2026-03-19 | AlertModal和DraggableModal组件移动功能Bug修复 |
| v0.1.10 | 2026-03-23 | 实现Merge Entities功能，添加Tree选择和无数据时显示根节点 |

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

## 前端组件

### DraggableModal 可移动模态框

基于 Bootstrap 5 的模态框组件，支持拖拽移动功能。

**特性**:
- 支持通过标题栏拖拽移动对话框
- 第一次移动时自动获取当前位置，无漂移现象
- 关闭按钮阻止拖拽事件冒泡
- 支持 ESC 键关闭
- 响应式宽度设置

**使用示例**:
```vue
<DraggableModal
  title="对话框标题"
  :width="'500px'"
  @close="handleClose"
>
  <p>对话框内容</p>
  <template #footer>
    <button class="btn btn-primary">确认</button>
    <button class="btn btn-secondary" @click="handleClose">取消</button>
  </template>
</DraggableModal>
```

**Props**:
| 属性 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| title | String | '' | 对话框标题 |
| width | String | '500px' | 对话框宽度 |
| icon | String | '' | 自定义图标路径 |
| showDefaultIcon | Boolean | true | 是否显示默认图标 |

**Events**:
| 事件 | 说明 |
|------|------|
| close | 关闭对话框时触发 |

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
