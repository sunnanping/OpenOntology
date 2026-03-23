# OpenOntology 功能文档

## 目录
1. [系统架构](#系统架构)
2. [微服务模块](#微服务模块)
3. [通用组件](#通用组件)
4. [API接口汇总](#api接口汇总)

---

## 系统架构

### 整体架构图

```
┌─────────────────────────────────────────────────────────────┐
│                        前端层 (Vue3)                         │
│  ┌─────────────┐ ┌─────────────┐ ┌─────────────────────┐   │
│  │  项目列表    │ │  项目编辑器  │ │     管理后台         │   │
│  │  ProjectList│ │ProjectEditor│ │   (Admin Module)    │   │
│  └─────────────┘ └─────────────┘ └─────────────────────┘   │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                      网关层 (Gateway)                        │
│              路由转发 / 负载均衡 / 统一认证                    │
└─────────────────────────────────────────────────────────────┘
                              │
              ┌───────────────┼───────────────┐
              ▼               ▼               ▼
┌─────────────────────────────────────────────────────────────┐
│                     微服务层 (Spring Cloud)                   │
│  ┌─────────┐ ┌─────────┐ ┌─────────┐ ┌─────────┐ ┌────────┐ │
│  │ontology │ │  class  │ │ property│ │ instance│ │reasoning│ │
│  │-service │ │-service │ │-service │ │-service │ │-service │ │
│  └─────────┘ └─────────┘ └─────────┘ └─────────┘ └────────┘ │
│  ┌─────────┐ ┌─────────┐ ┌─────────┐ ┌─────────┐ ┌────────┐ │
│  │collabor-│ │ version │ │ search  │ │  user   │ │ admin  │ │
│  │-ation   │ │-service │ │-service │ │-service │ │-service│ │
│  │-service │ │         │ │         │ │         │ │        │ │
│  └─────────┘ └─────────┘ └─────────┘ └─────────┘ └────────┘ │
│  ┌─────────┐ ┌─────────┐                                     │
│  │  i18n   │ │  config │                                     │
│  │-service │ │-server  │                                     │
│  └─────────┘ └─────────┘                                     │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                      基础设施层                              │
│  ┌─────────┐ ┌─────────┐ ┌─────────┐ ┌─────────────────┐   │
│  │  Eureka │ │ MongoDB │ │  Redis  │ │  翻译API服务     │   │
│  │ (注册中心)│ │(数据存储)│ │ (缓存)  │ │ (Google/百度等) │   │
│  └─────────┘ └─────────┘ └─────────┘ └─────────────────┘   │
└─────────────────────────────────────────────────────────────┘
```

### 技术栈

| 层级 | 技术 |
|------|------|
| 前端 | Vue 3 + Bootstrap 5 + Element Plus |
| 网关 | Spring Cloud Gateway |
| 微服务 | Spring Boot + Spring Cloud |
| 注册中心 | Eureka |
| 配置中心 | Spring Cloud Config |
| 数据存储 | MongoDB |
| 缓存 | Redis |
| 本体处理 | OWL API 5.1.20 |

---

## 微服务模块

### 1. eureka-server (服务注册中心)

**功能描述**：
- 微服务注册与发现
- 服务健康检查
- 自我保护模式

**配置参数**：
```yaml
server:
  port: 8762

eureka:
  instance:
    hostname: localhost
  client:
    registerWithEureka: false
    fetchRegistry: false
    serviceUrl:
      defaultZone: http://localhost:8762/eureka/
  server:
    enableSelfPreservation: true
    evictionIntervalTimerInMs: 60000
```

---

### 2. config-server (配置中心)

**功能描述**：
- 集中管理微服务配置
- 支持动态配置刷新
- 配置版本管理

**配置参数**：
```yaml
server:
  port: 8888

spring:
  cloud:
    config:
      server:
        git:
          uri: https://github.com/config-repo
          searchPaths: '{application}'
```

---

### 3. gateway-service (API网关)

**功能描述**：
- 统一入口路由
- 负载均衡
- 请求过滤
- 统一认证

**配置参数**：
```yaml
server:
  port: 9000

spring:
  cloud:
    gateway:
      routes:
        - id: ontology-service
          uri: lb://ontology-service
          predicates:
            - Path=/api/ontology/**
          filters:
            - StripPrefix=1
```

---

### 4. ontology-service (本体管理服务)

**功能描述**：
- 本体的CRUD操作
- 本体导入导出
- 命名空间管理
- 多格式支持(OWL, RDF/XML, Turtle等)

**实体类**：
```java
@Entity
public class Ontology {
    @Id
    private String id;
    private String name;
    private String namespace;
    private String description;
    private String language;
    private String ownerId;
    private Date createTime;
    private Date updateTime;
    private Date lastOpened;
    private List<String> sharedWith;
    private boolean deleted;
}
```

**API接口**：

| 接口 | 方法 | 路径 | 参数 | 返回值 |
|------|------|------|------|--------|
| 创建本体 | POST | /api/ontology/create | OntologyDTO | Ontology |
| 根据ID查询 | GET | /api/ontology/findById/{id} | id | Ontology |
| 根据名称查询 | GET | /api/ontology/findByName | name | Ontology |
| 查询所有 | GET | /api/ontology/findAll | - | List<Ontology> |
| 更新本体 | PUT | /api/ontology/update | OntologyDTO | Ontology |
| 删除本体 | DELETE | /api/ontology/delete/{id} | id | void |
| 导出本体 | GET | /api/ontology/import-export/export/{ontologyId} | ontologyId, format | ResponseEntity<byte[]> |
| 导入本体 | POST | /api/ontology/import-export/import | file, format | Ontology |

---

### 5. class-service (类管理服务)

**功能描述**：
- 类的CRUD操作
- 类层次结构管理
- 父类/子类关系维护
- 多语言标签支持

**实体类**：
```java
@Entity
public class OntologyClass {
    @Id
    private String id;
    private String ontologyId;
    private String name;
    private String iri;
    private List<String> parentIds;
    private List<String> childIds;
    private Map<String, String> labels;  // 多语言标签
    private Map<String, String> comments; // 多语言注释
    private List<Annotation> annotations;
    private List<Relationship> relationships;
}
```

**API接口**：

| 接口 | 方法 | 路径 | 参数 | 返回值 |
|------|------|------|------|--------|
| 创建类 | POST | /api/class/create | ClassDTO | OntologyClass |
| 根据ID查询 | GET | /api/class/findById/{id} | id | OntologyClass |
| 根据本体查询 | GET | /api/class/findByOntology/{ontologyId} | ontologyId | List<OntologyClass> |
| 查询所有 | GET | /api/class/findAll | - | List<OntologyClass> |
| 更新类 | PUT | /api/class/update | ClassDTO | OntologyClass |
| 删除类 | DELETE | /api/class/delete/{id} | id | void |
| 获取子类 | GET | /api/class/children/{id} | id | List<OntologyClass> |
| 获取父类 | GET | /api/class/parents/{id} | id | List<OntologyClass> |
| 搜索类 | GET | /api/class/search | query, projectId | List<OntologyClass> |

---

### 6. property-service (属性管理服务)

**功能描述**：
- 对象属性管理
- 数据属性管理
- 注释属性管理
- 属性约束管理

**实体类**：
```java
@Entity
public class OntologyProperty {
    @Id
    private String id;
    private String ontologyId;
    private String name;
    private String iri;
    private PropertyType type;  // OBJECT, DATA, ANNOTATION
    private List<String> domain;  // 定义域
    private List<String> range;   // 值域
    private boolean functional;
    private boolean transitive;
    private boolean symmetric;
    private boolean asymmetric;
    private boolean reflexive;
    private boolean irreflexive;
}
```

**API接口**：

| 接口 | 方法 | 路径 | 参数 | 返回值 |
|------|------|------|------|--------|
| 创建属性 | POST | /api/property/create | PropertyDTO | OntologyProperty |
| 根据ID查询 | GET | /api/property/findById/{id} | id | OntologyProperty |
| 根据本体查询 | GET | /api/property/findByOntology/{ontologyId} | ontologyId | List<OntologyProperty> |
| 根据类型查询 | GET | /api/property/findByType | type, ontologyId | List<OntologyProperty> |
| 更新属性 | PUT | /api/property/update | PropertyDTO | OntologyProperty |
| 删除属性 | DELETE | /api/property/delete/{id} | id | void |

---

### 7. instance-service (实例管理服务)

**功能描述**：
- 实例的CRUD操作
- 实例分类
- 属性值管理
- 实例与类的关系维护

**实体类**：
```java
@Entity
public class Instance {
    @Id
    private String id;
    private String ontologyId;
    private String name;
    private String iri;
    private List<String> classIds;  // 所属类
    private Map<String, List<PropertyValue>> propertyValues;
    private Map<String, String> labels;
    private Map<String, String> comments;
}

public class PropertyValue {
    private String propertyId;
    private String propertyName;
    private String value;
    private String type;  // 数据类型
}
```

**API接口**：

| 接口 | 方法 | 路径 | 参数 | 返回值 |
|------|------|------|------|--------|
| 创建实例 | POST | /api/instance/create | InstanceDTO | Instance |
| 根据ID查询 | GET | /api/instance/findById/{id} | id | Instance |
| 根据类查询 | GET | /api/instance/findByClass/{classId} | classId | List<Instance> |
| 根据本体查询 | GET | /api/instance/findByOntology/{ontologyId} | ontologyId | List<Instance> |
| 更新实例 | PUT | /api/instance/update | InstanceDTO | Instance |
| 删除实例 | DELETE | /api/instance/delete/{id} | id | void |
| 添加属性值 | POST | /api/instance/addPropertyValue | PropertyValueDTO | Instance |
| 删除属性值 | DELETE | /api/instance/removePropertyValue | instanceId, propertyId | Instance |

---

### 8. reasoning-service (本体推理服务)

**功能描述**：
- 一致性检查
- 分类推理
- 实例实现
- 支持多种推理器(OWL-API, Pellet, HermiT)

**API接口**：

| 接口 | 方法 | 路径 | 参数 | 返回值 |
|------|------|------|------|--------|
| 一致性检查 | POST | /api/reasoning/consistency | ontologyId | ConsistencyResult |
| 获取推断的类层次 | GET | /api/reasoning/inferredHierarchy | ontologyId | ClassHierarchy |
| 获取实例类型 | GET | /api/reasoning/instanceTypes | instanceId | List<String> |
| 执行推理 | POST | /api/reasoning/reason | ontologyId, reasonerType | ReasoningResult |

---

### 9. collaboration-service (协作服务)

**功能描述**：
- 多用户实时协作
- 评论系统
- 项目活动记录
- WebSocket实时通信

**实体类**：
```java
@Entity
public class Comment {
    @Id
    private String id;
    private String ontologyId;
    private String entityId;  // 评论的目标实体
    private String entityType; // CLASS, PROPERTY, INSTANCE
    private String userId;
    private String username;
    private String content;
    private Date createTime;
    private Date updateTime;
    private String parentId;  // 回复的评论ID
}

@Entity
public class ProjectActivity {
    @Id
    private String id;
    private String ontologyId;
    private String userId;
    private String username;
    private ActivityType type;  // CREATE, UPDATE, DELETE
    private String entityType;  // ONTOLOGY, CLASS, PROPERTY, INSTANCE
    private String entityId;
    private String entityName;
    private String description;
    private Date timestamp;
}
```

**API接口**：

| 接口 | 方法 | 路径 | 参数 | 返回值 |
|------|------|------|------|--------|
| 添加评论 | POST | /api/collaboration/comments | CommentDTO | Comment |
| 获取评论 | GET | /api/collaboration/comments/{entityId} | entityId | List<Comment> |
| 更新评论 | PUT | /api/collaboration/comments/{id} | id, content | Comment |
| 删除评论 | DELETE | /api/collaboration/comments/{id} | id | void |
| 获取项目活动 | GET | /api/collaboration/activities/{ontologyId} | ontologyId, page, size | Page<ProjectActivity> |

---

### 10. version-service (版本控制服务)

**功能描述**：
- 本体版本管理
- 版本对比
- 版本回滚
- 变更历史记录

**实体类**：
```java
@Entity
public class OntologyVersion {
    @Id
    private String id;
    private String ontologyId;
    private String versionNumber;
    private String description;
    private String userId;
    private String username;
    private Date createTime;
    private String snapshot;  // 本体快照
}

@Entity
public class ChangeRecord {
    @Id
    private String id;
    private String ontologyId;
    private String versionId;
    private ChangeType type;  // ADD, UPDATE, DELETE
    private String entityType;
    private String entityId;
    private String entityName;
    private String oldValue;
    private String newValue;
    private Date timestamp;
    private String userId;
}
```

**API接口**：

| 接口 | 方法 | 路径 | 参数 | 返回值 |
|------|------|------|------|--------|
| 创建版本 | POST | /api/version/create | VersionDTO | OntologyVersion |
| 获取版本列表 | GET | /api/version/list/{ontologyId} | ontologyId | List<OntologyVersion> |
| 获取版本详情 | GET | /api/version/detail/{id} | id | OntologyVersion |
| 版本对比 | GET | /api/version/compare | versionId1, versionId2 | VersionDiff |
| 版本回滚 | POST | /api/version/rollback | versionId | Ontology |
| 获取变更历史 | GET | /api/version/changes/{ontologyId} | ontologyId, page, size | Page<ChangeRecord> |

---

### 11. search-service (全文搜索服务)

**功能描述**：
- 跨本体全文搜索
- 实体类型过滤
- 高级搜索选项
- 搜索结果高亮

**API接口**：

| 接口 | 方法 | 路径 | 参数 | 返回值 |
|------|------|------|------|--------|
| 全文搜索 | GET | /api/search/fulltext | keyword, ontologyId, entityTypes, page, size | SearchResult |
| 高级搜索 | POST | /api/search/advanced | SearchCriteria | SearchResult |
| 搜索建议 | GET | /api/search/suggestions | keyword, ontologyId | List<String> |
| 搜索历史 | GET | /api/search/history/{userId} | userId | List<SearchHistory> |

---

### 12. user-service (用户管理服务)

**功能描述**：
- 用户注册/登录
- 用户信息管理
- 用户权限管理
- JWT认证

**实体类**：
```java
@Entity
public class User {
    @Id
    private String id;
    private String username;
    private String email;
    private String password;
    private List<String> roles;
    private Date createTime;
    private Date updateTime;
    private boolean enabled;
}
```

**API接口**：

| 接口 | 方法 | 路径 | 参数 | 返回值 |
|------|------|------|------|--------|
| 用户注册 | POST | /api/user/register | RegisterDTO | User |
| 用户登录 | POST | /api/user/login | LoginDTO | TokenResponse |
| 获取用户信息 | GET | /api/user/info/{id} | id | User |
| 更新用户信息 | PUT | /api/user/update | UserDTO | User |
| 修改密码 | PUT | /api/user/changePassword | ChangePasswordDTO | void |
| 删除用户 | DELETE | /api/user/delete/{id} | id | void |

---

### 13. admin-service (管理员管理服务)

**功能描述**：
- 管理员CRUD操作
- 系统设置管理
- 用户管理
- 系统监控

**实体类**：
```java
@Entity
public class Admin {
    @Id
    private String id;
    private String username;
    private String email;
    private String password;
    private List<String> permissions;
    private Date createTime;
    private Date lastLoginTime;
    private boolean enabled;
}

@Entity
public class SystemSettings {
    @Id
    private String id;
    private String siteName;
    private String siteDescription;
    private String logoUrl;
    private boolean allowRegistration;
    private boolean allowPublicAccess;
    private Map<String, Object> features;
}
```

**API接口**：

| 接口 | 方法 | 路径 | 参数 | 返回值 |
|------|------|------|------|--------|
| 创建管理员 | POST | /api/admin/create | AdminDTO | Admin |
| 管理员登录 | POST | /api/admin/login | LoginDTO | TokenResponse |
| 获取所有管理员 | GET | /api/admin/findAll | - | List<Admin> |
| 根据ID查询 | GET | /api/admin/findById/{id} | id | Admin |
| 更新管理员 | PUT | /api/admin/update | AdminDTO | Admin |
| 删除管理员 | DELETE | /api/admin/delete/{id} | id | void |
| 获取系统设置 | GET | /api/admin/settings | - | SystemSettings |
| 更新系统设置 | PUT | /api/admin/settings | SystemSettings | SystemSettings |

---

### 14. i18n-service (国际化翻译服务)

**功能描述**：
- 多语言翻译
- 翻译确认工作流
- 翻译历史记录
- 集成多种翻译API

**支持的翻译API**：
- Google Translate
- DeepL
- Microsoft Translator
- Yandex
- 百度翻译
- 阿里云机器翻译
- 有道智云
- 腾讯云机器翻译

**实体类**：
```java
@Entity
public class Translation {
    @Id
    private String id;
    private String key;
    private String sourceLanguage;
    private String targetLanguage;
    private String sourceText;
    private String translatedText;
    private TranslationStatus status;  // PENDING, CONFIRMED, REJECTED
    private String translator;  // API名称或用户ID
    private Date createTime;
    private Date confirmTime;
    private String confirmedBy;
}
```

**API接口**：

| 接口 | 方法 | 路径 | 参数 | 返回值 |
|------|------|------|------|--------|
| 翻译文本 | GET | /api/i18n/translate | text, sourceLang, targetLang, provider | Translation |
| 获取支持的语言 | GET | /api/i18n/languages | - | List<Language> |
| 确认翻译 | POST | /api/i18n/confirm | translationId, confirmedBy | Translation |
| 获取翻译历史 | GET | /api/i18n/history | key, page, size | Page<Translation> |
| 批量翻译 | POST | /api/i18n/translate/batch | BatchTranslationRequest | List<Translation> |

---

## 通用组件

### 1. AlertModal (警告/提示模态框)

**文件路径**：`frontend/src/components/AlertModal.vue`

**功能描述**：
基于 Bootstrap 5 的警告/提示模态框组件，支持拖拽移动、缩放、复制消息、截图等功能。

**特性**：
- 支持多种提示类型（success、error、warning、info）
- 支持通过标题栏拖拽移动对话框
- 支持8个方向缩放对话框（默认禁用，需通过resizable属性启用）
- 支持复制消息内容到剪贴板
- 支持截图功能（复制到内存或下载文件）
- 消息内容自动将冒号前内容加粗显示
- 支持水平和垂直对齐方式自定义
- 支持 ESC 键关闭
- 操作成功提示（蓝底白字）

**Props**：

| 属性 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| title | String | '提示' | 对话框标题 |
| message | String | required | 提示消息内容 |
| type | String | 'info' | 提示类型（success/error/warning/info） |
| confirmText | String | '确定' | 确认按钮文本 |
| width | String | '400px' | 对话框宽度 |
| minWidth | Number | 300 | 最小宽度 |
| minHeight | Number | 150 | 最小高度 |
| textAlign | String | 'left' | 文本水平对齐（left/center/right） |
| verticalAlign | String | 'center' | 文本垂直对齐（top/center/bottom） |
| captureType | String | 'copy' | 截图方式（copy/download） |
| tipMaxLength | Number | 10 | 提示文本最大长度 |
| tipDuration | Number | 500 | 提示显示时长（毫秒） |
| resizable | Boolean | false | 是否启用缩放功能 |

**Events**：

| 事件 | 说明 |
|------|------|
| close | 关闭对话框时触发 |
| confirm | 点击确认按钮时触发 |

**方法**：

| 方法 | 说明 |
|------|------|
| copyMessage | 复制消息内容到剪贴板 |
| captureScreenshot | 截图功能（支持复制到内存或下载） |
| showTipMessage | 显示操作成功提示 |

**使用示例**：

**基本使用（默认禁用缩放）**：
```vue
<template>
  <AlertModal
    title="错误提示"
    message="来源: 创建项目失败\n错误信息: 项目名称已存在"
    type="error"
    :width="'500px'"
    textAlign="left"
    verticalAlign="center"
    captureType="copy"
    @close="handleClose"
    @confirm="handleConfirm"
  />
</template>

<script>
import AlertModal from '@/components/AlertModal.vue'

export default {
  components: { AlertModal },
  methods: {
    handleClose() {
      this.showAlert = false
    },
    handleConfirm() {
      // 确认处理逻辑
    }
  }
}
</script>
```

**启用缩放功能**：
```vue
<template>
  <AlertModal
    title="可缩放提示"
    message="此对话框支持调整大小\n可通过边角拖拽调整"
    type="info"
    :width="'600px'"
    :resizable="true"
    textAlign="center"
    @close="handleClose"
  />
</template>

<script>
import AlertModal from '@/components/AlertModal.vue'

export default {
  components: { AlertModal },
  methods: {
    handleClose() {
      this.showAlert = false
    }
  }
}
</script>
```

**消息格式说明**：
- 消息内容支持换行符（\n）
- 冒号（:或：）前的内容会自动加粗显示
- 示例：`"来源: 系统错误\n详情: 连接超时"` 会显示为：
  - **来源:** 系统错误
  - **详情:** 连接超时

---

### 2. DraggableModal (可移动模态框)

**文件路径**：`frontend/src/components/DraggableModal.vue`

**功能描述**：
基于 Bootstrap 5 的模态框组件，支持通过标题栏拖拽移动。

**特性**：
- 支持通过标题栏拖拽移动对话框
- 第一次移动时自动获取当前位置，无漂移现象
- 关闭按钮阻止拖拽事件冒泡
- 支持 ESC 键关闭
- 响应式宽度设置

**Props**：

| 属性 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| title | String | '' | 对话框标题 |
| width | String | '500px' | 对话框宽度 |
| icon | String | '' | 自定义图标路径 |
| showDefaultIcon | Boolean | true | 是否显示默认图标 |

**Events**：

| 事件 | 说明 |
|------|------|
| close | 关闭对话框时触发 |

**使用示例**：
```vue
<template>
  <DraggableModal
    title="创建新项目"
    :width="'500px'"
    @close="handleClose"
  >
    <form>
      <div class="form-group">
        <label>项目名称</label>
        <input type="text" class="form-control" />
      </div>
    </form>
    <template #footer>
      <button class="btn btn-primary" @click="handleSubmit">确认</button>
      <button class="btn btn-secondary" @click="handleClose">取消</button>
    </template>
  </DraggableModal>
</template>

<script>
import DraggableModal from '@/components/DraggableModal.vue'

export default {
  components: { DraggableModal },
  methods: {
    handleClose() {
      this.$emit('close')
    },
    handleSubmit() {
      // 提交逻辑
    }
  }
}
</script>
```

---

### 3. LanguageSwitcher (语言切换器)

**文件路径**：`frontend/src/components/LanguageSwitcher.vue`

**功能描述**：
提供多语言切换功能，支持12种语言。

**特性**：
- 下拉菜单选择语言
- 自动保存用户语言偏好
- 实时切换界面语言

**Props**：

| 属性 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| size | String | 'default' | 按钮大小(small/default/large) |
| type | String | 'primary' | 按钮类型 |

**使用示例**：
```vue
<template>
  <LanguageSwitcher size="small" type="text" />
</template>
```

---

### 4. TranslationEditor (翻译编辑器)

**文件路径**：`frontend/src/components/TranslationEditor.vue`

**功能描述**：
用于编辑和管理多语言翻译内容。

**特性**：
- 多语言输入框
- 翻译建议
- 翻译确认
- 翻译历史查看

**Props**：

| 属性 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| translationKey | String | required | 翻译键 |
| sourceText | String | '' | 源文本 |
| languages | Array | [] | 支持的语言列表 |

---

### 5. TreeNode (递归树形组件)

**文件路径**：`frontend/src/components/TreeNode.vue`

**功能描述**：
支持任意层级的递归树形结构展示，用于类层次结构。

**特性**：
- 递归渲染子节点
- 支持展开/折叠
- 支持选中状态
- 支持右键菜单
- 支持拖拽排序

**Props**：

| 属性 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| node | Object | required | 节点数据 |
| level | Number | 0 | 节点层级 |
| selectedId | String | '' | 当前选中节点ID |

**Events**：

| 事件 | 说明 |
|------|------|
| select | 选中节点时触发 |
| expand | 展开节点时触发 |
| collapse | 折叠节点时触发 |
| contextmenu | 右键点击时触发 |

---

## API接口汇总

### 接口前缀说明

| 服务 | 接口前缀 |
|------|----------|
| ontology-service | /api/ontology |
| class-service | /api/class |
| property-service | /api/property |
| instance-service | /api/instance |
| reasoning-service | /api/reasoning |
| collaboration-service | /api/collaboration |
| version-service | /api/version |
| search-service | /api/search |
| user-service | /api/user |
| admin-service | /api/admin |
| i18n-service | /api/i18n |

### 通用响应格式

**成功响应**：
```json
{
  "code": 200,
  "message": "success",
  "data": { ... }
}
```

**错误响应**：
```json
{
  "code": 500,
  "message": "错误信息",
  "data": null
}
```

### 分页参数

| 参数 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| page | Integer | 0 | 页码(从0开始) |
| size | Integer | 10 | 每页大小 |
| sort | String | id,desc | 排序字段和方向 |

### 分页响应

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "content": [ ... ],
    "totalElements": 100,
    "totalPages": 10,
    "size": 10,
    "number": 0,
    "first": true,
    "last": false
  }
}
```

---

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

---

## 文档更新记录

| 日期 | 版本 | 更新内容 |
|------|------|----------|
| 2026-03-19 | v1.1 | 新增配置说明章节 |
| 2026-03-18 | v1.0 | 初始版本，系统架构和微服务模块功能说明 |
| 2026-03-22 | v1.1 | 添加前端组件架构，三层布局组件，模块化设计 |

---

## 前端组件架构

### 整体架构设计

前端采用组件化架构设计，参考 WebProtege 的 UI/UX 模式，实现了三层布局结构：

```
┌─────────────────────────────────────────────────────────────┐
│                    第一层：系统菜单 (SystemMenuBar)           │
│  ┌─────────┐ ┌─────────┐ ┌──────────────┐ ┌────────────┐  │
│  │ 项目Logo │ │  Home   │ │  系统菜单     │ │ 用户/帮助   │  │
│  └─────────┘ └─────────┘ └──────────────┘ └────────────┘  │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│              第二层：实体类型标签栏 (EntityTabsBar)           │
│  ┌──────┐ ┌──────┐ ┌──────┐ ┌──────┐ ┌──────┐ ┌──────┐  │
│  │Class │ │Prop  │ │Ind   │ │Cmt   │ │Chg   │ │His   │  │
│  └──────┘ └──────┘ └──────┘ └──────┘ └──────┘ └──────┘  │
│  ┌──────────────────────────────────────────────────────┐  │
│  │                   Add tab                            │  │
│  └──────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│              第三层：动态模块组件 (Modules)                   │
│  ┌──────────────┐ ┌──────────────┐ ┌──────────────────┐   │
│  │ Class        │ │ Class        │ │ Comments         │   │
│  │ Hierarchy    │ │ Details      │ │                  │   │
│  │              │ │              │ │                  │   │
│  │              │ │              │ ├──────────────────┤   │
│  │              │ │              │ │ Project Feed     │   │
│  └──────────────┘ └──────────────┘ └──────────────────┘   │
└─────────────────────────────────────────────────────────────┘
```

### 技术栈

| 技术 | 版本 | 用途 |
|------|------|------|
| Vue | 3.x | 前端框架 |
| Bootstrap | 5.x | UI 组件库 |
| Element Plus | Latest | UI 组件库（Tree、Scrollbar等） |
| ECharts | 5.x | 数据可视化（Entity Graph） |
| Vue Router | 4.x | 路由管理 |
| Axios | Latest | HTTP 请求 |
| Vite | 4.x | 构建工具 |

---

### 核心组件

#### 1. LoadOperatePage (容器页面)

**文件路径**：`frontend/src/views/LoadOperatePage.vue`

**功能描述**：
容器页面，整合三层布局组件，负责动态加载不同的功能模块。根据传入的项目ID和模块类型，加载对应的编辑器组件。

**Props**：
```javascript
{
  projectId: String,      // 项目ID（从路由参数获取）
  projectInfo: Object,    // 项目信息
  userInfo: Object,       // 用户信息
  module: String          // 模块类型（classes、properties、individuals等）
}
```

**事件**：
- `class-selected`: 类被选中
- `class-created`: 类被创建
- `class-deleted`: 类被删除

**路由配置**：
```javascript
{
  path: '/operate/:projectId',
  name: 'LoadOperatePage',
  component: () => import('../views/LoadOperatePage.vue'),
  props: true,
  meta: { requiresAuth: true }
}
```

**可用模块**：
- `ClassEditorPanel`: 类编辑器
- `PropertyEditorPanel`: 属性编辑器
- `IndividualEditorPanel`: 实例编辑器
- `CommentManagerPanel`: 评论管理器
- `ChangesViewerPanel`: 变更查看器
- `HistoryViewerPanel`: 历史查看器

---

#### 2. SystemMenuBar (系统菜单)

**文件路径**：`frontend/src/components/layout/SystemMenuBar.vue`

**功能描述**：
第一层系统菜单组件，提供项目导航、系统功能、用户管理等功能。

**Props**：
```javascript
{
  projectName: String,    // 项目名称（动态传入，替换硬编码的xyz）
  currentUser: Object,    // 当前用户信息
  projectInfo: Object     // 项目信息
}
```

**主要功能**：
- 项目Logo和名称显示
- Home 导航
- Display 菜单（Theme、Language）
- Project 菜单（Settings、Share）
- Share 功能
- 用户信息下拉菜单（Profile、Logout）
- Help 菜单（Documentation、About）
- 移动端响应式菜单

**响应式设计**：
- 桌面端（≥992px）：完整菜单显示
- 平板端（768px-992px）：简化菜单显示
- 移动端（<768px）：汉堡菜单

---

#### 3. EntityTabsBar (实体类型标签栏)

**文件路径**：`frontend/src/components/layout/EntityTabsBar.vue`

**功能描述**：
第二层实体类型标签栏组件，提供不同类型实体的快速切换。

**Props**：
```javascript
{
  tabs: Array,           // 标签列表
  activeTab: String      // 当前激活的标签ID
}
```

**事件**：
- `tab-change`: 标签切换
- `add-tab`: 添加新标签

**默认标签**：
```javascript
[
  { id: 'classes', label: 'Classes', shortLabel: 'Cls', icon: 'bi bi-diagram-3', module: 'ClassEditorPanel' },
  { id: 'properties', label: 'Properties', shortLabel: 'Prop', icon: 'bi bi-link-45deg', module: 'PropertyEditorPanel' },
  { id: 'individuals', label: 'Individuals', shortLabel: 'Ind', icon: 'bi bi-people', module: 'IndividualEditorPanel' },
  { id: 'comments', label: 'Comments', shortLabel: 'Cmt', icon: 'bi bi-chat-left-text', module: 'CommentManagerPanel' },
  { id: 'changes', label: 'Changes by Entity', shortLabel: 'Chg', icon: 'bi bi-clock-history', module: 'ChangesViewerPanel' },
  { id: 'history', label: 'History', shortLabel: 'His', icon: 'bi bi-journal-text', module: 'HistoryViewerPanel' }
]
```

**特性**：
- 使用 ElementUI Scrollbar 实现横向滚动
- 响应式标签显示（桌面端显示完整标签，移动端显示简短标签）
- 动态标签高亮
- 支持添加自定义标签

---

#### 4. ClassEditorPanel (类编辑器面板)

**文件路径**：`frontend/src/components/modules/ClassEditorPanel.vue`

**功能描述**：
第三层类编辑器面板，包含四个可调整大小的子面板：Class Hierarchy、Class Details、Comments、Project Feed。

**Props**：
```javascript
{
  projectId: String,      // 项目ID
  projectInfo: Object,    // 项目信息
  userInfo: Object        // 用户信息
}
```

**事件**：
- `class-selected`: 类被选中
- `class-created`: 类被创建
- `class-deleted`: 类被删除

**子面板**：

1. **Class Hierarchy 面板**
   - 显示类层次结构树
   - 支持创建、删除、搜索类
   - 使用 ElementUI Tree 组件
   - 支持过滤功能
   - 可调整宽度（左右拖拽，无限制）

2. **Class Details 面板**
   - 显示选中类的详细信息
   - 包含三个标签页：Details、Entity Graph、Changes
   - Details 标签：显示类的注释、父类、子类、属性、关系等
   - Entity Graph 标签：使用 ECharts 显示实体关系图
   - Changes 标签：显示类的变更历史
   - 可调整宽度（左右拖拽，无限制）

3. **Comments 面板**
   - 显示实体评论
   - 支持添加新评论
   - 支持过滤功能
   - 可调整高度（上下拖拽，无限制）

4. **Project Feed 面板**
   - 显示项目活动记录
   - 实时更新项目变更
   - 自动填充剩余空间

**面板调整功能**：
- 左右拖拽调整 Class Hierarchy 和 Class Details 面板宽度（无最小/最大限制）
- 左右拖拽调整 Class Details 和右侧面板宽度（无最小/最大限制）
- 上下拖拽调整 Comments 和 Project Feed 面板高度（无最小/最大限制）

**移动端适配**：
- 小于 992px 时切换为移动端视图
- 显示移动端面板切换标签
- 每次只显示一个面板
- 支持面板间快速切换

**主要功能**：
- 创建类（Create Class）
- 删除类（Delete Class）
- 搜索类（Search Class）
- 过滤类（Filter Classes）
- 查看类详情（View Class Details）
- 查看实体图（View Entity Graph）
- 查看变更历史（View Changes）
- 添加评论（Add Comment）
- 添加注释（Add Annotation）
- 添加父类（Add Parent）
- 添加关系（Add Relationship）

---

#### 5. PropertyEditorPanel (属性编辑器面板)

**文件路径**：`frontend/src/components/modules/PropertyEditorPanel.vue`

**功能描述**：
属性编辑器面板，用于管理对象属性、数据属性和注释属性。

**状态**：占位组件，待实现

---

#### 6. IndividualEditorPanel (实例编辑器面板)

**文件路径**：`frontend/src/components/modules/IndividualEditorPanel.vue`

**功能描述**：
实例编辑器面板，用于管理本体实例。

**状态**：占位组件，待实现

---

#### 7. CommentManagerPanel (评论管理面板)

**文件路径**：`frontend/src/components/modules/CommentManagerPanel.vue`

**功能描述**：
评论管理面板，用于管理和查看所有评论。

**状态**：占位组件，待实现

---

#### 8. ChangesViewerPanel (变更查看面板)

**文件路径**：`frontend/src/components/modules/ChangesViewerPanel.vue`

**功能描述**：
变更查看面板，用于查看按实体分组的变更历史。

**状态**：占位组件，待实现

---

#### 9. HistoryViewerPanel (历史查看面板)

**文件路径**：`frontend/src/components/modules/HistoryViewerPanel.vue`

**功能描述**：
历史查看面板，用于查看项目完整历史记录。

**状态**：占位组件，待实现

---

### 路由配置

**文件路径**：`frontend/src/router/index.js`

**主要路由**：

| 路径 | 组件 | 说明 |
|------|------|------|
| `/` | Home | 首页 |
| `/login` | Login | 登录页 |
| `/projects/list` | ProjectList | 项目列表 |
| `/operate/:projectId` | LoadOperatePage | 操作页面（容器） |
| `/class-editor` | ClassEditor | 类编辑器（旧版，已弃用） |

**路由守卫**：
- `requiresAuth`: 需要登录认证
- `requiresAdmin`: 需要管理员权限

---

### API 集成

**HTTP 客户端**：
- 使用 Axios 进行 HTTP 请求
- 全局配置：`window.$http`
- 依赖注入：`inject('$http')`

**主要 API 调用**：

| 功能 | 方法 | 路径 |
|------|------|------|
| 获取项目信息 | GET | `/api/ontology/findById/{projectId}` |
| 更新最后打开时间 | PUT | `/api/ontology/update-last-opened/{projectId}` |
| 获取类层次结构 | GET | `/api/class/hierarchy/{ontologyId}` |
| 创建类 | POST | `/api/class/create` |
| 更新类 | PUT | `/api/class/update` |
| 删除类 | DELETE | `/api/class/delete/{id}` |
| 获取评论 | GET | `/api/collaboration/comments/{entityId}` |
| 添加评论 | POST | `/api/collaboration/comments` |
| 获取项目活动 | GET | `/api/collaboration/activities/{ontologyId}` |

---

### 响应式设计

**断点**：
- 超大屏（≥1400px）：xl
- 大屏（≥1200px）：lg
- 中屏（≥992px）：md
- 小屏（≥768px）：sm
- 超小屏（<768px）：xs

**适配策略**：
1. **系统菜单**：
   - 桌面端：完整菜单显示
   - 平板端：简化菜单，隐藏部分文字
   - 移动端：汉堡菜单，点击展开

2. **实体标签栏**：
   - 桌面端：显示完整标签名
   - 平板端：显示完整标签名，支持横向滚动
   - 移动端：显示简短标签名（3个字符），支持横向滚动

3. **编辑器面板**：
   - 桌面端：四面板并排显示，支持拖拽调整
   - 平板端：四面板并排显示，支持拖拽调整
   - 移动端：单面板显示，顶部标签切换

---

### 样式规范

**颜色主题**：
- 主色：`#4a90d9`（蓝色）
- 成功：`#28a745`（绿色）
- 警告：`#ffc107`（黄色）
- 危险：`#dc3545`（红色）
- 信息：`#17a2b8`（青色）
- 背景：`#f8f9fa`（浅灰）
- 边框：`#dee2e6`（灰色）
- 文字：`#212529`（深灰）

**字体**：
- 系统字体：`-apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, "Helvetica Neue", Arial, sans-serif`
- 代码字体：`"SFMono-Regular", Consolas, "Liberation Mono", Menlo, Courier, monospace`

**间距**：
- 基础间距：8px
- 小间距：4px
- 中间距：16px
- 大间距：24px

**圆角**：
- 小圆角：4px
- 中圆角：6px
- 大圆角：8px

---

### 性能优化

**代码分割**：
- 使用 Vue Router 的懒加载
- 使用 `defineAsyncComponent` 动态加载模块组件

**缓存策略**：
- 使用 Vue 的 `shallowRef` 避免深层响应式
- 使用 `computed` 缓存计算结果

**渲染优化**：
- 使用 `v-show` 替代 `v-if` 减少销毁重建
- 使用 `key` 优化列表渲染
- 使用 `v-once` 静态内容

**资源优化**：
- 按需引入 Element Plus 组件
- 使用 Vite 的 Tree Shaking
- 图片懒加载

---

### 开发指南

**组件开发流程**：
1. 在 `frontend/src/components/modules/` 下创建新组件
2. 在 `LoadOperatePage.vue` 中注册组件
3. 在 `availableTabs` 中添加标签配置
4. 在 `moduleComponents` 中添加组件映射
5. 实现组件的 Props 和 Events
6. 添加响应式设计
7. 编写单元测试

**样式开发规范**：
1. 使用 Scoped CSS 避免样式污染
2. 使用 Bootstrap 5 的工具类
3. 遵循 BEM 命名规范
4. 使用 CSS 变量管理主题色
5. 添加响应式断点

**调试技巧**：
1. 使用 Vue DevTools 调试组件
2. 使用 Chrome DevTools 调试样式
3. 使用 Network 面板调试 API
4. 使用 Console 查看错误日志

---

### 已知问题

1. **占位组件未实现**：
   - PropertyEditorPanel
   - IndividualEditorPanel
   - CommentManagerPanel
   - ChangesViewerPanel
   - HistoryViewerPanel

2. **动态模块加载**：
   - 目前使用静态加载，需要改为从后端获取可用模块列表
   - 需要实现基于权限的模块加载

3. **实时协作**：
   - WebSocket 连接未实现
   - 实时评论更新未实现
   - 实时项目活动更新未实现

4. **Entity Graph**：
   - ECharts 图表初始化需要优化
   - 大数据量下的性能需要优化

---

### 未来规划

1. **功能完善**：
   - 实现所有占位组件
   - 实现动态模块加载
   - 实现实时协作功能

2. **性能优化**：
   - 优化大数据量下的渲染性能
   - 实现虚拟滚动
   - 优化 ECharts 图表性能

3. **用户体验**：
   - 添加快捷键支持
   - 添加拖拽排序
   - 添加撤销/重做功能

4. **国际化**：
   - 实现多语言支持
   - 集成翻译服务

5. **测试**：
   - 添加单元测试
   - 添加集成测试
   - 添加 E2E 测试

---

## 更新历史

| 日期 | 版本 | 说明 |
|------|------|------|
| 2026-03-18 | v1.0 | 初始版本，系统架构和微服务模块功能说明 |
| 2026-03-22 | v1.1 | 添加前端组件架构，三层布局组件，模块化设计 |
