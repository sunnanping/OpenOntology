# OpenOntology 项目测试文档

## 1. 项目模块依赖关系

### 核心模块依赖

| 模块名称 | 主要功能 | 依赖服务 | 配置文件 |
|---------|---------|---------|----------|
| eureka-server | 服务注册中心 | 无 | application.yml |
| config-server | 配置中心 | eureka-server | application.yml |
| gateway | API网关 | eureka-server, 所有业务服务 | application.yml |
| user-service | 用户管理服务 | eureka-server | application.yml |
| admin-service | 管理员管理服务 | eureka-server | application.yml |
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
   - admin-service
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
| admin-service | http://localhost:8080/api/admin/** | 管理员管理相关接口 |
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

#### 4.2.2 管理员服务测试
- 测试创建管理员：POST /api/admin/create
- 测试管理员登录：POST /api/admin/login
- 测试查询所有管理员：GET /api/admin/findAll
- 测试查询管理员详情：GET /api/admin/findById/{id}
- 测试更新管理员：PUT /api/admin/update
- 测试删除管理员：DELETE /api/admin/delete/{id}
- 测试获取系统设置：GET /api/admin/settings
- 测试更新系统设置：PUT /api/admin/settings

#### 4.2.3 本体服务测试
- 测试创建本体：POST /api/ontology
- 测试查询本体列表：GET /api/ontology
- 测试查询本体详情：GET /api/ontology/{id}
- 测试导出本体：GET /api/ontology/{id}/export
- 测试导入本体：POST /api/ontology/import

#### 4.2.4 类服务测试
- 测试创建类：POST /api/class
- 测试查询类列表：GET /api/class
- 测试查询类详情：GET /api/class/{id}

#### 4.2.5 属性服务测试
- 测试创建属性：POST /api/property
- 测试查询属性列表：GET /api/property
- 测试查询属性详情：GET /api/property/{id}

#### 4.2.6 实例服务测试
- 测试创建实例：POST /api/instance
- 测试查询实例列表：GET /api/instance
- 测试查询实例详情：GET /api/instance/{id}

#### 4.2.7 推理服务测试
- 测试执行推理：POST /api/reasoning/execute
- 测试查询推理结果：GET /api/reasoning/result/{id}

#### 4.2.8 协作服务测试
- 测试创建协作：POST /api/collaboration
- 测试查询协作列表：GET /api/collaboration
- 测试查询协作详情：GET /api/collaboration/{id}

#### 4.2.9 版本服务测试
- 测试创建版本：POST /api/version
- 测试查询版本列表：GET /api/version
- 测试查询版本详情：GET /api/version/{id}

#### 4.2.10 搜索服务测试
- 测试搜索本体：GET /api/search/ontology
- 测试搜索类：GET /api/search/class
- 测试搜索属性：GET /api/search/property
- 测试搜索实例：GET /api/search/instance

#### 4.2.11 前端Admin模块测试
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

#### 4.2.12 前端Projects模块测试
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

#### 4.2.13 项目编辑器测试
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
  
  - **右键菜单**：
    - 右键点击类节点
    - 验证菜单选项（Add Subclass、Delete、Rename）
    - 测试各菜单项功能

- **类编辑视图测试**：
  - 选择类后显示编辑视图
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

### 4.3 集成测试

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
