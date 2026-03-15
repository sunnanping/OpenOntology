package com.by.open.ontology.i18nservice;

import com.by.open.ontology.i18nservice.service.ExternalTranslationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Mono;

import java.time.Duration;

@SpringBootTest
public class TranslationApiTest {

    @Autowired
    private ExternalTranslationService externalTranslationService;

    @Test
    public void testMyMemoryTranslation() {
        System.out.println("Testing MyMemory Translation API...");
        Mono<String> result = externalTranslationService.translate(
                "Hello, World!", "en", "zh-CN", "MYMEMORY");
        
        String translation = result.block(Duration.ofSeconds(10));
        System.out.println("MyMemory Translation Result: " + translation);
        assert translation != null && !translation.isEmpty();
    }

    @Test
    public void testBaiduTranslation() {
        System.out.println("Testing Baidu Translation API...");
        Mono<String> result = externalTranslationService.translate(
                "Hello, World!", "en", "zh-CN", "BAIDU");
        
        String translation = result.block(Duration.ofSeconds(10));
        System.out.println("Baidu Translation Result: " + translation);
        assert translation != null && !translation.isEmpty();
    }

    @Test
    public void testAliyunTranslation() {
        System.out.println("Testing Aliyun Machine Translation API...");
        Mono<String> result = externalTranslationService.translate(
                "Hello, World!", "en", "zh-CN", "ALIYUN");
        
        String translation = result.block(Duration.ofSeconds(10));
        System.out.println("Aliyun Translation Result: " + translation);
        assert translation != null && !translation.isEmpty();
    }

    @Test
    public void testYoudaoTranslation() {
        System.out.println("Testing Youdao Translation API...");
        Mono<String> result = externalTranslationService.translate(
                "Hello, World!", "en", "zh-CN", "YOUDAO");
        
        String translation = result.block(Duration.ofSeconds(10));
        System.out.println("Youdao Translation Result: " + translation);
        assert translation != null && !translation.isEmpty();
    }

    @Test
    public void testTencentTranslation() {
        System.out.println("Testing Tencent Cloud Translation API...");
        Mono<String> result = externalTranslationService.translate(
                "Hello, World!", "en", "zh-CN", "TENCENT");
        
        String translation = result.block(Duration.ofSeconds(10));
        System.out.println("Tencent Translation Result: " + translation);
        assert translation != null && !translation.isEmpty();
    }

    @Test
    public void testGoogleTranslation() {
        System.out.println("Testing Google Translation API...");
        Mono<String> result = externalTranslationService.translate(
                "Hello, World!", "en", "zh-CN", "GOOGLE");
        
        String translation = result.block(Duration.ofSeconds(10));
        System.out.println("Google Translation Result: " + translation);
        assert translation != null && !translation.isEmpty();
    }

    @Test
    public void testDeepLTranslation() {
        System.out.println("Testing DeepL Translation API...");
        Mono<String> result = externalTranslationService.translate(
                "Hello, World!", "en", "zh-CN", "DEEPL");
        
        String translation = result.block(Duration.ofSeconds(10));
        System.out.println("DeepL Translation Result: " + translation);
        assert translation != null && !translation.isEmpty();
    }

    @Test
    public void testMicrosoftTranslation() {
        System.out.println("Testing Microsoft Translator API...");
        Mono<String> result = externalTranslationService.translate(
                "Hello, World!", "en", "zh-CN", "MICROSOFT");
        
        String translation = result.block(Duration.ofSeconds(10));
        System.out.println("Microsoft Translation Result: " + translation);
        assert translation != null && !translation.isEmpty();
    }

    @Test
    public void testYandexTranslation() {
        System.out.println("Testing Yandex Translate API...");
        Mono<String> result = externalTranslationService.translate(
                "Hello, World!", "en", "zh-CN", "YANDEX");
        
        String translation = result.block(Duration.ofSeconds(10));
        System.out.println("Yandex Translation Result: " + translation);
        assert translation != null && !translation.isEmpty();
    }

    @Test
    public void testAllProviders() {
        System.out.println("Testing all translation providers...");
        System.out.println("Supported providers: " + externalTranslationService.getSupportedProviders());
        
        String[] providers = {"MYMEMORY", "BAIDU", "ALIYUN", "YOUDAO", "TENCENT", 
                              "GOOGLE", "DEEPL", "MICROSOFT", "YANDEX"};
        
        for (String provider : providers) {
            try {
                System.out.println("\n--- Testing " + provider + " ---");
                Mono<String> result = externalTranslationService.translate(
                        "Hello, World!", "en", "zh-CN", provider);
                
                String translation = result.block(Duration.ofSeconds(10));
                System.out.println(provider + " Result: " + translation);
                
                if (translation != null && !translation.isEmpty()) {
                    System.out.println(provider + " API is working!");
                } else {
                    System.out.println(provider + " API returned empty result");
                }
            } catch (Exception e) {
                System.out.println(provider + " API test failed: " + e.getMessage());
            }
        }
    }

    @Test
    public void testMultipleLanguages() {
        System.out.println("Testing multiple languages with MyMemory...");
        
        String[] languages = {"zh-CN", "zh-TW", "de", "fr", "es", "it", "ja", "ko", "ru", "pt"};
        
        for (String lang : languages) {
            try {
                Mono<String> result = externalTranslationService.translate(
                        "Hello, World!", "en", lang, "MYMEMORY");
                
                String translation = result.block(Duration.ofSeconds(10));
                System.out.println("Translation to " + lang + ": " + translation);
            } catch (Exception e) {
                System.out.println("Translation to " + lang + " failed: " + e.getMessage());
            }
        }
    }
}
