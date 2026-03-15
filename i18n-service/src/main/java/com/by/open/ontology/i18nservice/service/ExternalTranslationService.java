package com.by.open.ontology.i18nservice.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class ExternalTranslationService {

    private static final Logger logger = LoggerFactory.getLogger(ExternalTranslationService.class);

    private final WebClient webClient;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${translation.google.api-key:}")
    private String googleApiKey;

    @Value("${translation.deepl.api-key:}")
    private String deeplApiKey;

    @Value("${translation.microsoft.api-key:}")
    private String microsoftApiKey;

    @Value("${translation.yandex.api-key:}")
    private String yandexApiKey;

    @Value("${translation.baidu.app-id:}")
    private String baiduAppId;

    @Value("${translation.baidu.secret-key:}")
    private String baiduSecretKey;

    @Value("${translation.aliyun.access-key-id:}")
    private String aliyunAccessKeyId;

    @Value("${translation.aliyun.access-key-secret:}")
    private String aliyunAccessKeySecret;

    @Value("${translation.youdao.app-id:}")
    private String youdaoAppId;

    @Value("${translation.youdao.app-secret:}")
    private String youdaoAppSecret;

    @Value("${translation.tencent.secret-id:}")
    private String tencentSecretId;

    @Value("${translation.tencent.secret-key:}")
    private String tencentSecretKey;

    @Value("${translation.tencent.region:ap-beijing}")
    private String tencentRegion;

    private static final Map<String, String> SUPPORTED_TRANSLATION_APIS = new HashMap<>();

    static {
        SUPPORTED_TRANSLATION_APIS.put("GOOGLE", "Google Translate");
        SUPPORTED_TRANSLATION_APIS.put("DEEPL", "DeepL");
        SUPPORTED_TRANSLATION_APIS.put("MICROSOFT", "Microsoft Translator");
        SUPPORTED_TRANSLATION_APIS.put("YANDEX", "Yandex Translate");
        SUPPORTED_TRANSLATION_APIS.put("MYMEMORY", "MyMemory Translation");
        SUPPORTED_TRANSLATION_APIS.put("BAIDU", "Baidu Translate");
        SUPPORTED_TRANSLATION_APIS.put("ALIYUN", "Aliyun Machine Translation");
        SUPPORTED_TRANSLATION_APIS.put("YOUDAO", "Youdao Translation");
        SUPPORTED_TRANSLATION_APIS.put("TENCENT", "Tencent Cloud Translation");
    }

    public ExternalTranslationService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    public Mono<String> translate(String text, String sourceLang, String targetLang, String apiProvider) {
        if (text == null || text.trim().isEmpty()) {
            return Mono.just("");
        }

        switch (apiProvider.toUpperCase()) {
            case "GOOGLE":
                return translateWithGoogle(text, sourceLang, targetLang);
            case "DEEPL":
                return translateWithDeepL(text, sourceLang, targetLang);
            case "MICROSOFT":
                return translateWithMicrosoft(text, sourceLang, targetLang);
            case "YANDEX":
                return translateWithYandex(text, sourceLang, targetLang);
            case "BAIDU":
                return translateWithBaidu(text, sourceLang, targetLang);
            case "ALIYUN":
                return translateWithAliyun(text, sourceLang, targetLang);
            case "YOUDAO":
                return translateWithYoudao(text, sourceLang, targetLang);
            case "TENCENT":
                return translateWithTencent(text, sourceLang, targetLang);
            default:
                return translateWithMyMemory(text, sourceLang, targetLang);
        }
    }

    private Mono<String> translateWithMyMemory(String text, String sourceLang, String targetLang) {
        WebClient myMemoryClient = WebClient.builder()
                .baseUrl("https://api.mymemory.translated.net")
                .build();

        return myMemoryClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/get")
                        .queryParam("q", text)
                        .queryParam("langpair", sourceLang + "|" + targetLang)
                        .build())
                .retrieve()
                .bodyToMono(String.class)
                .map(response -> {
                    try {
                        JsonNode root = objectMapper.readTree(response);
                        JsonNode responseData = root.path("responseData");
                        if (responseData.has("translatedText")) {
                            return responseData.get("translatedText").asText();
                        }
                    } catch (Exception e) {
                        logger.error("Error parsing MyMemory response", e);
                    }
                    return text;
                })
                .onErrorResume(e -> {
                    logger.error("MyMemory translation error", e);
                    return Mono.just(text);
                });
    }

    private Mono<String> translateWithGoogle(String text, String sourceLang, String targetLang) {
        if (googleApiKey == null || googleApiKey.isEmpty()) {
            logger.warn("Google Translate API key not configured, falling back to MyMemory");
            return translateWithMyMemory(text, sourceLang, targetLang);
        }

        WebClient googleClient = WebClient.builder()
                .baseUrl("https://translation.googleapis.com/language/translate/v2")
                .build();

        return googleClient.post()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("key", googleApiKey)
                        .queryParam("q", text)
                        .queryParam("source", sourceLang)
                        .queryParam("target", targetLang)
                        .queryParam("format", "text")
                        .build())
                .retrieve()
                .bodyToMono(String.class)
                .map(response -> {
                    try {
                        JsonNode root = objectMapper.readTree(response);
                        JsonNode data = root.path("data");
                        JsonNode translations = data.path("translations");
                        if (translations.isArray() && translations.size() > 0) {
                            return translations.get(0).path("translatedText").asText();
                        }
                    } catch (Exception e) {
                        logger.error("Error parsing Google Translate response", e);
                    }
                    return text;
                })
                .onErrorResume(e -> {
                    logger.error("Google Translate error", e);
                    return translateWithMyMemory(text, sourceLang, targetLang);
                });
    }

    private Mono<String> translateWithDeepL(String text, String sourceLang, String targetLang) {
        if (deeplApiKey == null || deeplApiKey.isEmpty()) {
            logger.warn("DeepL API key not configured, falling back to MyMemory");
            return translateWithMyMemory(text, sourceLang, targetLang);
        }

        WebClient deeplClient = WebClient.builder()
                .baseUrl("https://api-free.deepl.com/v2/translate")
                .defaultHeader("Authorization", "DeepL-Auth-Key " + deeplApiKey)
                .defaultHeader("Content-Type", "application/json")
                .build();

        String requestBody = String.format("{\"text\":[\"%s\"],\"source_lang\":\"%s\",\"target_lang\":\"%s\"}",
                text.replace("\"", "\\\""), sourceLang.toUpperCase(), targetLang.toUpperCase());

        return deeplClient.post()
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .map(response -> {
                    try {
                        JsonNode root = objectMapper.readTree(response);
                        JsonNode translations = root.path("translations");
                        if (translations.isArray() && translations.size() > 0) {
                            return translations.get(0).path("text").asText();
                        }
                    } catch (Exception e) {
                        logger.error("Error parsing DeepL response", e);
                    }
                    return text;
                })
                .onErrorResume(e -> {
                    logger.error("DeepL translation error", e);
                    return translateWithMyMemory(text, sourceLang, targetLang);
                });
    }

    private Mono<String> translateWithMicrosoft(String text, String sourceLang, String targetLang) {
        if (microsoftApiKey == null || microsoftApiKey.isEmpty()) {
            logger.warn("Microsoft Translator API key not configured, falling back to MyMemory");
            return translateWithMyMemory(text, sourceLang, targetLang);
        }

        WebClient microsoftClient = WebClient.builder()
                .baseUrl("https://api.cognitive.microsofttranslator.com/translate")
                .defaultHeader("Ocp-Apim-Subscription-Key", microsoftApiKey)
                .defaultHeader("Ocp-Apim-Subscription-Region", "global")
                .defaultHeader("Content-Type", "application/json")
                .build();

        String requestBody = String.format("[{\"Text\":\"%s\"}]", text.replace("\"", "\\\""));

        return microsoftClient.post()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("api-version", "3.0")
                        .queryParam("from", sourceLang)
                        .queryParam("to", targetLang)
                        .build())
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .map(response -> {
                    try {
                        JsonNode root = objectMapper.readTree(response);
                        if (root.isArray() && root.size() > 0) {
                            JsonNode translations = root.get(0).path("translations");
                            if (translations.isArray() && translations.size() > 0) {
                                return translations.get(0).path("text").asText();
                            }
                        }
                    } catch (Exception e) {
                        logger.error("Error parsing Microsoft Translator response", e);
                    }
                    return text;
                })
                .onErrorResume(e -> {
                    logger.error("Microsoft Translator error", e);
                    return translateWithMyMemory(text, sourceLang, targetLang);
                });
    }

    private Mono<String> translateWithYandex(String text, String sourceLang, String targetLang) {
        if (yandexApiKey == null || yandexApiKey.isEmpty()) {
            logger.warn("Yandex Translate API key not configured, falling back to MyMemory");
            return translateWithMyMemory(text, sourceLang, targetLang);
        }

        WebClient yandexClient = WebClient.builder()
                .baseUrl("https://translate.api.cloud.yandex.net/translate/v2/translate")
                .defaultHeader("Authorization", "Api-Key " + yandexApiKey)
                .defaultHeader("Content-Type", "application/json")
                .build();

        String requestBody = String.format("{\"texts\":[\"%s\"],\"sourceLanguageCode\":\"%s\",\"targetLanguageCode\":\"%s\"}",
                text.replace("\"", "\\\""), sourceLang, targetLang);

        return yandexClient.post()
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .map(response -> {
                    try {
                        JsonNode root = objectMapper.readTree(response);
                        JsonNode translations = root.path("translations");
                        if (translations.isArray() && translations.size() > 0) {
                            return translations.get(0).path("text").asText();
                        }
                    } catch (Exception e) {
                        logger.error("Error parsing Yandex Translate response", e);
                    }
                    return text;
                })
                .onErrorResume(e -> {
                    logger.error("Yandex Translate error", e);
                    return translateWithMyMemory(text, sourceLang, targetLang);
                });
    }

    private Mono<String> translateWithBaidu(String text, String sourceLang, String targetLang) {
        if (baiduAppId == null || baiduAppId.isEmpty() || baiduSecretKey == null || baiduSecretKey.isEmpty()) {
            logger.warn("Baidu Translate API credentials not configured, falling back to MyMemory");
            return translateWithMyMemory(text, sourceLang, targetLang);
        }

        try {
            String salt = String.valueOf(System.currentTimeMillis());
            String sign = generateBaiduSign(baiduAppId, text, salt, baiduSecretKey);

            WebClient baiduClient = WebClient.builder()
                    .baseUrl("https://fanyi-api.baidu.com/api/trans/vip/translate")
                    .build();

            return baiduClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .queryParam("q", text)
                            .queryParam("from", convertLanguageCode(sourceLang, "baidu"))
                            .queryParam("to", convertLanguageCode(targetLang, "baidu"))
                            .queryParam("appid", baiduAppId)
                            .queryParam("salt", salt)
                            .queryParam("sign", sign)
                            .build())
                    .retrieve()
                    .bodyToMono(String.class)
                    .map(response -> {
                        try {
                            JsonNode root = objectMapper.readTree(response);
                            if (root.has("trans_result")) {
                                JsonNode transResult = root.path("trans_result");
                                if (transResult.isArray() && transResult.size() > 0) {
                                    return transResult.get(0).path("dst").asText();
                                }
                            } else if (root.has("error_code")) {
                                logger.error("Baidu Translate error code: {}, error msg: {}", 
                                    root.get("error_code").asText(), 
                                    root.has("error_msg") ? root.get("error_msg").asText() : "");
                            }
                        } catch (Exception e) {
                            logger.error("Error parsing Baidu Translate response", e);
                        }
                        return text;
                    })
                    .onErrorResume(e -> {
                        logger.error("Baidu Translate error", e);
                        return translateWithMyMemory(text, sourceLang, targetLang);
                    });
        } catch (Exception e) {
            logger.error("Error generating Baidu sign", e);
            return translateWithMyMemory(text, sourceLang, targetLang);
        }
    }

    private String generateBaiduSign(String appId, String query, String salt, String secretKey) {
        try {
            String str = appId + query + salt + secretKey;
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] bytes = md.digest(str.getBytes(StandardCharsets.UTF_8));
            StringBuilder sign = new StringBuilder();
            for (byte b : bytes) {
                String hex = Integer.toHexString(b & 0xFF);
                if (hex.length() == 1) {
                    sign.append('0');
                }
                sign.append(hex);
            }
            return sign.toString();
        } catch (Exception e) {
            throw new RuntimeException("Error generating Baidu sign", e);
        }
    }

    private Mono<String> translateWithAliyun(String text, String sourceLang, String targetLang) {
        if (aliyunAccessKeyId == null || aliyunAccessKeyId.isEmpty() || 
            aliyunAccessKeySecret == null || aliyunAccessKeySecret.isEmpty()) {
            logger.warn("Aliyun Machine Translation API credentials not configured, falling back to MyMemory");
            return translateWithMyMemory(text, sourceLang, targetLang);
        }

        try {
            String action = "TranslateGeneral";
            String version = "2018-10-12";
            String format = "JSON";
            String signatureNonce = UUID.randomUUID().toString();
            String timestamp = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").format(new Date());
            String signatureMethod = "HMAC-SHA1";

            Map<String, String> params = new LinkedHashMap<>();
            params.put("Action", action);
            params.put("Version", version);
            params.put("Format", format);
            params.put("AccessKeyId", aliyunAccessKeyId);
            params.put("SignatureMethod", signatureMethod);
            params.put("SignatureVersion", "1.0");
            params.put("SignatureNonce", signatureNonce);
            params.put("Timestamp", timestamp);
            params.put("SourceLanguage", convertLanguageCode(sourceLang, "aliyun"));
            params.put("TargetLanguage", convertLanguageCode(targetLang, "aliyun"));
            params.put("SourceText", text);
            params.put("FormatType", "text");

            String signature = generateAliyunSignature("GET", aliyunAccessKeySecret, params);

            WebClient aliyunClient = WebClient.builder()
                    .baseUrl("https://mt.aliyuncs.com")
                    .build();

            StringBuilder queryString = new StringBuilder();
            for (Map.Entry<String, String> entry : params.entrySet()) {
                if (queryString.length() > 0) {
                    queryString.append("&");
                }
                queryString.append(URLEncoder.encode(entry.getKey(), "UTF-8"))
                        .append("=")
                        .append(URLEncoder.encode(entry.getValue(), "UTF-8"));
            }
            queryString.append("&Signature=").append(URLEncoder.encode(signature, "UTF-8"));

            return aliyunClient.get()
                    .uri("/?" + queryString.toString())
                    .retrieve()
                    .bodyToMono(String.class)
                    .map(response -> {
                        try {
                            JsonNode root = objectMapper.readTree(response);
                            if (root.has("Data")) {
                                JsonNode data = root.path("Data");
                                if (data.has("Translated")) {
                                    return data.path("Translated").asText();
                                }
                            } else if (root.has("Code")) {
                                logger.error("Aliyun Translate error code: {}, message: {}", 
                                    root.get("Code").asText(), 
                                    root.has("Message") ? root.get("Message").asText() : "");
                            }
                        } catch (Exception e) {
                            logger.error("Error parsing Aliyun Translate response", e);
                        }
                        return text;
                    })
                    .onErrorResume(e -> {
                        logger.error("Aliyun Translate error", e);
                        return translateWithMyMemory(text, sourceLang, targetLang);
                    });
        } catch (Exception e) {
            logger.error("Error generating Aliyun signature", e);
            return translateWithMyMemory(text, sourceLang, targetLang);
        }
    }

    private String generateAliyunSignature(String method, String accessKeySecret, Map<String, String> params) {
        try {
            StringBuilder canonicalizedQueryString = new StringBuilder();
            for (Map.Entry<String, String> entry : params.entrySet()) {
                if (canonicalizedQueryString.length() > 0) {
                    canonicalizedQueryString.append("&");
                }
                canonicalizedQueryString.append(URLEncoder.encode(entry.getKey(), "UTF-8"))
                        .append("=")
                        .append(URLEncoder.encode(entry.getValue(), "UTF-8"));
            }

            String stringToSign = method + "&" + 
                               URLEncoder.encode("/", "UTF-8") + "&" + 
                               URLEncoder.encode(canonicalizedQueryString.toString(), "UTF-8");

            String key = accessKeySecret + "&";
            Mac mac = Mac.getInstance("HmacSHA1");
            mac.init(new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA1"));
            byte[] signatureBytes = mac.doFinal(stringToSign.getBytes(StandardCharsets.UTF_8));
            
            return Base64.getEncoder().encodeToString(signatureBytes);
        } catch (Exception e) {
            throw new RuntimeException("Error generating Aliyun signature", e);
        }
    }

    private Mono<String> translateWithYoudao(String text, String sourceLang, String targetLang) {
        if (youdaoAppId == null || youdaoAppId.isEmpty() || youdaoAppSecret == null || youdaoAppSecret.isEmpty()) {
            logger.warn("Youdao Translation API credentials not configured, falling back to MyMemory");
            return translateWithMyMemory(text, sourceLang, targetLang);
        }

        try {
            String salt = String.valueOf(System.currentTimeMillis());
            String curtime = String.valueOf(System.currentTimeMillis() / 1000);
            String sign = generateYoudaoSign(youdaoAppId, text, salt, curtime, youdaoAppSecret);

            WebClient youdaoClient = WebClient.builder()
                    .baseUrl("https://openapi.youdao.com/api")
                    .build();

            return youdaoClient.post()
                    .uri(uriBuilder -> uriBuilder
                            .path("/translate")
                            .build())
                    .bodyValue(new YoudaoRequest(
                            youdaoAppId,
                            sign,
                            convertLanguageCode(sourceLang, "youdao"),
                            convertLanguageCode(targetLang, "youdao"),
                            text,
                            "v2",
                            curtime,
                            salt
                    ))
                    .retrieve()
                    .bodyToMono(String.class)
                    .map(response -> {
                        try {
                            JsonNode root = objectMapper.readTree(response);
                            if (root.has("translation") && root.get("translation").isArray()) {
                                JsonNode translation = root.get("translation");
                                if (translation.size() > 0) {
                                    return translation.get(0).asText();
                                }
                            } else if (root.has("errorCode")) {
                                logger.error("Youdao Translate error code: {}, message: {}", 
                                    root.get("errorCode").asText(), 
                                    root.has("errorMsg") ? root.get("errorMsg").asText() : "");
                            }
                        } catch (Exception e) {
                            logger.error("Error parsing Youdao Translate response", e);
                        }
                        return text;
                    })
                    .onErrorResume(e -> {
                        logger.error("Youdao Translate error", e);
                        return translateWithMyMemory(text, sourceLang, targetLang);
                    });
        } catch (Exception e) {
            logger.error("Error generating Youdao sign", e);
            return translateWithMyMemory(text, sourceLang, targetLang);
        }
    }

    private String generateYoudaoSign(String appId, String query, String salt, String curtime, String appSecret) {
        try {
            String str = appId + truncate(query) + salt + curtime + appSecret;
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] bytes = md.digest(str.getBytes(StandardCharsets.UTF_8));
            StringBuilder sign = new StringBuilder();
            for (byte b : bytes) {
                String hex = Integer.toHexString(b & 0xFF);
                if (hex.length() == 1) {
                    sign.append('0');
                }
                sign.append(hex);
            }
            return sign.toString();
        } catch (Exception e) {
            throw new RuntimeException("Error generating Youdao sign", e);
        }
    }

    private String truncate(String q) {
        if (q == null) {
            return null;
        }
        int len = q.length();
        return len <= 20 ? q : (q.substring(0, 10) + len + q.substring(len - 10));
    }

    private Mono<String> translateWithTencent(String text, String sourceLang, String targetLang) {
        if (tencentSecretId == null || tencentSecretId.isEmpty() || 
            tencentSecretKey == null || tencentSecretKey.isEmpty()) {
            logger.warn("Tencent Cloud Translation API credentials not configured, falling back to MyMemory");
            return translateWithMyMemory(text, sourceLang, targetLang);
        }

        try {
            String action = "TextTranslate";
            String version = "2018-03-21";
            String region = tencentRegion;
            String timestamp = String.valueOf(System.currentTimeMillis() / 1000);
            String nonce = String.valueOf(System.currentTimeMillis());
            
            Map<String, String> params = new LinkedHashMap<>();
            params.put("Action", action);
            params.put("Version", version);
            params.put("Region", region);
            params.put("Timestamp", timestamp);
            params.put("Nonce", nonce);
            params.put("SecretId", tencentSecretId);
            params.put("SourceText", text);
            params.put("Source", convertLanguageCode(sourceLang, "tencent"));
            params.put("Target", convertLanguageCode(targetLang, "tencent"));
            params.put("ProjectId", "0");

            String signature = generateTencentSignature("GET", "tmt.tencentcloudapi.com", params, tencentSecretKey);

            WebClient tencentClient = WebClient.builder()
                    .baseUrl("https://tmt.tencentcloudapi.com")
                    .build();

            StringBuilder queryString = new StringBuilder();
            for (Map.Entry<String, String> entry : params.entrySet()) {
                if (queryString.length() > 0) {
                    queryString.append("&");
                }
                queryString.append(URLEncoder.encode(entry.getKey(), "UTF-8"))
                        .append("=")
                        .append(URLEncoder.encode(entry.getValue(), "UTF-8"));
            }
            queryString.append("&Signature=").append(URLEncoder.encode(signature, "UTF-8"));

            return tencentClient.get()
                    .uri("/?" + queryString.toString())
                    .retrieve()
                    .bodyToMono(String.class)
                    .map(response -> {
                        try {
                            JsonNode root = objectMapper.readTree(response);
                            if (root.has("Response")) {
                                JsonNode responseNode = root.path("Response");
                                if (responseNode.has("TargetText")) {
                                    return responseNode.path("TargetText").asText();
                                } else if (responseNode.has("Error")) {
                                    JsonNode error = responseNode.path("Error");
                                    logger.error("Tencent Translate error code: {}, message: {}", 
                                        error.get("Code").asText(), 
                                        error.get("Message").asText());
                                }
                            }
                        } catch (Exception e) {
                            logger.error("Error parsing Tencent Translate response", e);
                        }
                        return text;
                    })
                    .onErrorResume(e -> {
                        logger.error("Tencent Translate error", e);
                        return translateWithMyMemory(text, sourceLang, targetLang);
                    });
        } catch (Exception e) {
            logger.error("Error generating Tencent signature", e);
            return translateWithMyMemory(text, sourceLang, targetLang);
        }
    }

    private String generateTencentSignature(String method, String host, Map<String, String> params, String secretKey) {
        try {
            StringBuilder canonicalizedQueryString = new StringBuilder();
            List<String> sortedKeys = new ArrayList<>(params.keySet());
            Collections.sort(sortedKeys);
            
            for (String key : sortedKeys) {
                if (canonicalizedQueryString.length() > 0) {
                    canonicalizedQueryString.append("&");
                }
                canonicalizedQueryString.append(URLEncoder.encode(key, "UTF-8"))
                        .append("=")
                        .append(URLEncoder.encode(params.get(key), "UTF-8"));
            }

            String canonicalRequest = method.toLowerCase() + host + "/?" + canonicalizedQueryString.toString();
            String stringToSign = "HmacSHA1" + canonicalRequest;
            
            Mac mac = Mac.getInstance("HmacSHA1");
            mac.init(new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "HmacSHA1"));
            byte[] signatureBytes = mac.doFinal(stringToSign.getBytes(StandardCharsets.UTF_8));
            
            return Base64.getEncoder().encodeToString(signatureBytes);
        } catch (Exception e) {
            throw new RuntimeException("Error generating Tencent signature", e);
        }
    }

    private String convertLanguageCode(String langCode, String provider) {
        Map<String, Map<String, String>> languageMappings = new HashMap<>();
        
        Map<String, String> baiduMap = new HashMap<>();
        baiduMap.put("en", "en");
        baiduMap.put("zh-CN", "zh");
        baiduMap.put("zh-TW", "cht");
        baiduMap.put("de", "de");
        baiduMap.put("fr", "fra");
        baiduMap.put("es", "spa");
        baiduMap.put("it", "it");
        baiduMap.put("ja", "jp");
        baiduMap.put("ko", "kor");
        baiduMap.put("ru", "ru");
        baiduMap.put("pt", "pt");
        baiduMap.put("ar", "ara");
        languageMappings.put("baidu", baiduMap);

        Map<String, String> aliyunMap = new HashMap<>();
        aliyunMap.put("en", "en");
        aliyunMap.put("zh-CN", "zh");
        aliyunMap.put("zh-TW", "zh-tw");
        aliyunMap.put("de", "de");
        aliyunMap.put("fr", "fr");
        aliyunMap.put("es", "es");
        aliyunMap.put("it", "it");
        aliyunMap.put("ja", "ja");
        aliyunMap.put("ko", "ko");
        aliyunMap.put("ru", "ru");
        aliyunMap.put("pt", "pt");
        languageMappings.put("aliyun", aliyunMap);

        Map<String, String> youdaoMap = new HashMap<>();
        youdaoMap.put("en", "en");
        youdaoMap.put("zh-CN", "zh-CHS");
        youdaoMap.put("zh-TW", "zh-CHT");
        youdaoMap.put("de", "de");
        youdaoMap.put("fr", "fr");
        youdaoMap.put("es", "es");
        youdaoMap.put("it", "it");
        youdaoMap.put("ja", "ja");
        youdaoMap.put("ko", "ko");
        youdaoMap.put("ru", "ru");
        youdaoMap.put("pt", "pt");
        languageMappings.put("youdao", youdaoMap);

        Map<String, String> tencentMap = new HashMap<>();
        tencentMap.put("en", "en");
        tencentMap.put("zh-CN", "zh");
        tencentMap.put("zh-TW", "zh-TW");
        tencentMap.put("de", "de");
        tencentMap.put("fr", "fr");
        tencentMap.put("es", "es");
        tencentMap.put("it", "it");
        tencentMap.put("ja", "ja");
        tencentMap.put("ko", "ko");
        tencentMap.put("ru", "ru");
        tencentMap.put("pt", "pt");
        languageMappings.put("tencent", tencentMap);

        Map<String, String> providerMap = languageMappings.get(provider.toLowerCase());
        if (providerMap != null) {
            String converted = providerMap.get(langCode);
            if (converted != null) {
                return converted;
            }
        }
        
        return langCode;
    }

    public Map<String, String> getSupportedProviders() {
        return new HashMap<>(SUPPORTED_TRANSLATION_APIS);
    }

    private static class YoudaoRequest {
        private String appKey;
        private String sign;
        private String from;
        private String to;
        private String q;
        private String vocabId;
        private String curtime;
        private String salt;

        public YoudaoRequest(String appKey, String sign, String from, String to, String q, 
                          String vocabId, String curtime, String salt) {
            this.appKey = appKey;
            this.sign = sign;
            this.from = from;
            this.to = to;
            this.q = q;
            this.vocabId = vocabId;
            this.curtime = curtime;
            this.salt = salt;
        }

        public String getAppKey() {
            return appKey;
        }

        public String getSign() {
            return sign;
        }

        public String getFrom() {
            return from;
        }

        public String getTo() {
            return to;
        }

        public String getQ() {
            return q;
        }

        public String getVocabId() {
            return vocabId;
        }

        public String getCurtime() {
            return curtime;
        }

        public String getSalt() {
            return salt;
        }
    }
}
