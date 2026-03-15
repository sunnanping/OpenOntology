# 翻译API配置说明

本文档说明如何配置和使用i18n-service中的各种翻译API。

## 支持的翻译API

### 国际翻译服务
- **Google Translate** - Google翻译API
- **DeepL** - DeepL翻译API
- **Microsoft Translator** - 微软翻译API
- **Yandex Translate** - Yandex翻译API
- **MyMemory Translation** - 免费备用翻译API

### 国内翻译服务
- **百度翻译 API** - 百度翻译API
- **阿里云机器翻译 API** - 阿里云机器翻译API
- **有道智云文本翻译 API** - 有道智云翻译API
- **腾讯云机器翻译 API** - 腾讯云翻译API

## 配置方式

### 1. 环境变量配置（推荐）

在启动i18n-service时，通过环境变量设置API密钥：

#### 国际翻译服务
```bash
export GOOGLE_TRANSLATE_API_KEY="your-google-api-key"
export DEEPL_API_KEY="your-deepl-api-key"
export MICROSOFT_TRANSLATE_API_KEY="your-microsoft-api-key"
export YANDEX_TRANSLATE_API_KEY="your-yandex-api-key"
```

#### 国内翻译服务
```bash
# 百度翻译
export BAIDU_TRANSLATE_APP_ID="your-baidu-app-id"
export BAIDU_TRANSLATE_SECRET_KEY="your-baidu-secret-key"

# 阿里云机器翻译
export ALIYUN_ACCESS_KEY_ID="your-aliyun-access-key-id"
export ALIYUN_ACCESS_KEY_SECRET="your-aliyun-access-key-secret"

# 有道智云翻译
export YOUDAO_APP_ID="your-youdao-app-id"
export YOUDAO_APP_SECRET="your-youdao-app-secret"

# 腾讯云机器翻译
export TENCENT_SECRET_ID="your-tencent-secret-id"
export TENCENT_SECRET_KEY="your-tencent-secret-key"
export TENCENT_REGION="ap-beijing"
```

### 2. application.yml配置

直接在`application.yml`文件中配置：

```yaml
translation:
  # 国际翻译服务
  google:
    api-key: "your-google-api-key"
  deepl:
    api-key: "your-deepl-api-key"
  microsoft:
    api-key: "your-microsoft-api-key"
  yandex:
    api-key: "your-yandex-api-key"
  
  # 国内翻译服务
  baidu:
    app-id: "your-baidu-app-id"
    secret-key: "your-baidu-secret-key"
  aliyun:
    access-key-id: "your-aliyun-access-key-id"
    access-key-secret: "your-aliyun-access-key-secret"
  youdao:
    app-id: "your-youdao-app-id"
    app-secret: "your-youdao-app-secret"
  tencent:
    secret-id: "your-tencent-secret-id"
    secret-key: "your-tencent-secret-key"
    region: "ap-beijing"
```

## API密钥获取方式

### 百度翻译 API
1. 访问 [百度翻译开放平台](https://fanyi-api.baidu.com/)
2. 注册并登录账号
3. 进入管理控制台，创建应用
4. 获取APP ID和密钥

### 阿里云机器翻译 API
1. 访问 [阿里云官网](https://www.aliyun.com/)
2. 注册并登录账号
3. 开通机器翻译服务
4. 在AccessKey管理中创建AccessKey
5. 获取AccessKey ID和AccessKey Secret

### 有道智云文本翻译 API
1. 访问 [有道智云](https://ai.youdao.com/)
2. 注册并登录账号
3. 创建应用，选择文本翻译服务
4. 获取应用ID和应用密钥

### 腾讯云机器翻译 API
1. 访问 [腾讯云官网](https://cloud.tencent.com/)
2. 注册并登录账号
3. 开通机器翻译服务
4. 在API密钥管理中创建密钥
5. 获取Secret ID和Secret Key

### Google Translate API
1. 访问 [Google Cloud Console](https://console.cloud.google.com/)
2. 创建项目并启用Cloud Translation API
3. 创建API密钥

### DeepL API
1. 访问 [DeepL官网](https://www.deepl.com/)
2. 注册并登录账号
3. 在账户设置中获取API密钥

### Microsoft Translator API
1. 访问 [Azure Portal](https://portal.azure.com/)
2. 创建Translator资源
3. 获取API密钥

### Yandex Translate API
1. 访问 [Yandex Cloud](https://cloud.yandex.com/)
2. 创建项目并启用Translate API
3. 创建API密钥

## 测试API

### 1. 使用测试控制器

启动i18n-service后，可以使用以下HTTP接口测试：

#### 获取支持的翻译服务
```bash
curl http://localhost:8086/api/i18n/test/providers
```

#### 测试单个翻译服务
```bash
curl -X POST "http://localhost:8086/api/i18n/test/translate" \
  -d "text=Hello, World!" \
  -d "sourceLang=en" \
  -d "targetLang=zh-CN" \
  -d "provider=BAIDU"
```

#### 测试所有翻译服务
```bash
curl -X POST "http://localhost:8086/api/i18n/test/test-all" \
  -d "text=Hello, World!" \
  -d "sourceLang=en" \
  -d "targetLang=zh-CN"
```

#### 测试单个服务并查看响应时间
```bash
curl -X POST "http://localhost:8086/api/i18n/test/test-single" \
  -d "provider=BAIDU" \
  -d "text=Hello, World!" \
  -d "sourceLang=en" \
  -d "targetLang=zh-CN"
```

### 2. 使用JUnit测试

运行测试类：
```bash
mvn test -Dtest=TranslationApiTest
```

## 支持的语言

所有翻译服务都支持以下语言：

| 语言代码 | 语言名称 | 百度 | 阿里云 | 有道 | 腾讯云 |
|---------|---------|------|--------|------|--------|
| en      | English | ✅    | ✅      | ✅    | ✅      |
| zh-CN   | 简体中文 | ✅    | ✅      | ✅    | ✅      |
| zh-TW   | 繁體中文 | ✅    | ✅      | ✅    | ✅      |
| de      | Deutsch | ✅    | ✅      | ✅    | ✅      |
| fr      | Français | ✅    | ✅      | ✅    | ✅      |
| es      | Español | ✅    | ✅      | ✅    | ✅      |
| it      | Italiano | ✅    | ✅      | ✅    | ✅      |
| ja      | 日本語 | ✅    | ✅      | ✅    | ✅      |
| ko      | 한국어 | ✅    | ✅      | ✅    | ✅      |
| ru      | Русский | ✅    | ✅      | ✅    | ✅      |
| pt      | Português | ✅    | ✅      | ✅    | ✅      |
| ar      | العربية | ✅    | ✅      | ✅    | ✅      |

## 降级机制

当配置的翻译API密钥为空或API调用失败时，系统会自动降级到MyMemory免费翻译API，确保翻译功能的可用性。

## 使用建议

1. **优先使用国内翻译服务**：对于中文相关的翻译，建议优先使用百度、阿里云、有道或腾讯云的翻译服务，这些服务对中文的处理更加准确。

2. **配置多个翻译服务**：建议配置多个翻译服务作为备用，当某个服务不可用时可以自动切换。

3. **监控API调用**：定期监控各翻译服务的调用成功率和响应时间，选择最适合的服务。

4. **注意API配额**：各翻译服务都有免费配额限制，请注意监控使用量，避免超出配额。

## 故障排查

### API调用失败
1. 检查API密钥是否正确配置
2. 检查网络连接是否正常
3. 检查API配额是否已用尽
4. 查看日志文件获取详细错误信息

### 翻译结果不准确
1. 尝试使用不同的翻译服务
2. 检查语言代码是否正确
3. 对于专业术语，考虑使用专业翻译服务

## 联系支持

如有问题，请联系各翻译服务的官方技术支持：
- 百度翻译：https://fanyi-api.baidu.com/
- 阿里云：https://help.aliyun.com/
- 有道智云：https://ai.youdao.com/
- 腾讯云：https://cloud.tencent.com/document/product/551
