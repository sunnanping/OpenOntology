package com.by.open.ontology.i18nservice.controller;

import com.by.open.ontology.i18nservice.service.ExternalTranslationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/i18n/test")
@CrossOrigin(origins = "*")
public class TranslationTestController {

    @Autowired
    private ExternalTranslationService externalTranslationService;

    @GetMapping("/providers")
    public ResponseEntity<Map<String, String>> getSupportedProviders() {
        return ResponseEntity.ok(externalTranslationService.getSupportedProviders());
    }

    @PostMapping("/translate")
    public ResponseEntity<Map<String, Object>> testTranslation(
            @RequestParam String text,
            @RequestParam String sourceLang,
            @RequestParam String targetLang,
            @RequestParam(defaultValue = "MYMEMORY") String provider) {
        
        Map<String, Object> result = new HashMap<>();
        result.put("provider", provider);
        result.put("text", text);
        result.put("sourceLang", sourceLang);
        result.put("targetLang", targetLang);
        
        try {
            Mono<String> translationMono = externalTranslationService.translate(
                    text, sourceLang, targetLang, provider);
            
            String translation = translationMono.block();
            result.put("success", true);
            result.put("translation", translation);
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", e.getMessage());
        }
        
        return ResponseEntity.ok(result);
    }

    @PostMapping("/test-all")
    public ResponseEntity<Map<String, Object>> testAllProviders(
            @RequestParam(defaultValue = "Hello, World!") String text,
            @RequestParam(defaultValue = "en") String sourceLang,
            @RequestParam(defaultValue = "zh-CN") String targetLang) {
        
        Map<String, Object> result = new HashMap<>();
        result.put("text", text);
        result.put("sourceLang", sourceLang);
        result.put("targetLang", targetLang);
        
        Map<String, Object> providerResults = new HashMap<>();
        
        String[] providers = {"MYMEMORY", "BAIDU", "ALIYUN", "YOUDAO", "TENCENT", 
                              "GOOGLE", "DEEPL", "MICROSOFT", "YANDEX"};
        
        for (String provider : providers) {
            try {
                Map<String, Object> providerResult = new HashMap<>();
                Mono<String> translationMono = externalTranslationService.translate(
                        text, sourceLang, targetLang, provider);
                
                String translation = translationMono.block();
                providerResult.put("success", true);
                providerResult.put("translation", translation);
                providerResults.put(provider, providerResult);
            } catch (Exception e) {
                Map<String, Object> providerResult = new HashMap<>();
                providerResult.put("success", false);
                providerResult.put("error", e.getMessage());
                providerResults.put(provider, providerResult);
            }
        }
        
        result.put("results", providerResults);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/test-single")
    public ResponseEntity<Map<String, Object>> testSingleProvider(
            @RequestParam String provider,
            @RequestParam(defaultValue = "Hello, World!") String text,
            @RequestParam(defaultValue = "en") String sourceLang,
            @RequestParam(defaultValue = "zh-CN") String targetLang) {
        
        Map<String, Object> result = new HashMap<>();
        result.put("provider", provider);
        result.put("text", text);
        result.put("sourceLang", sourceLang);
        result.put("targetLang", targetLang);
        
        try {
            long startTime = System.currentTimeMillis();
            Mono<String> translationMono = externalTranslationService.translate(
                    text, sourceLang, targetLang, provider);
            
            String translation = translationMono.block();
            long endTime = System.currentTimeMillis();
            
            result.put("success", true);
            result.put("translation", translation);
            result.put("responseTime", endTime - startTime + "ms");
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", e.getMessage());
        }
        
        return ResponseEntity.ok(result);
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("service", "i18n-service");
        health.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.ok(health);
    }
}
