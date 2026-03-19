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

## 文档更新记录

| 日期 | 版本 | 更新内容 |
|------|------|----------|
| 2026-03-18 | v1.0 | 初始版本，系统架构和微服务模块功能说明 |
