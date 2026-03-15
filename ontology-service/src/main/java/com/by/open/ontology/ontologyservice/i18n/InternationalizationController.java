package com.by.open.ontology.ontologyservice.i18n;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/i18n")
@CrossOrigin(origins = "*")
public class InternationalizationController {

    @Autowired
    private InternationalizationService i18nService;

    @GetMapping("/translations/{entityType}/{entityRef}")
    public ResponseEntity<LanguageMap> getTranslations(
            @PathVariable String entityType,
            @PathVariable String entityRef) {
        LanguageMap translations = i18nService.getTranslations(entityRef, entityType);
        return ResponseEntity.ok(translations);
    }

    @PostMapping("/translations/{entityType}/{entityRef}")
    public ResponseEntity<LanguageMap> saveTranslations(
            @PathVariable String entityType,
            @PathVariable String entityRef,
            @RequestBody Map<String, String> translations) {
        LanguageMap result = i18nService.saveTranslations(entityRef, entityType, translations);
        return ResponseEntity.ok(result);
    }

    @PutMapping("/translations/{entityType}/{entityRef}/{langTag}")
    public ResponseEntity<LanguageMap> addTranslation(
            @PathVariable String entityType,
            @PathVariable String entityRef,
            @PathVariable String langTag,
            @RequestBody TranslationRequest request) {
        LanguageMap result = i18nService.addTranslation(entityRef, entityType, langTag, request.getValue());
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/translations/{entityType}/{entityRef}/{langTag}")
    public ResponseEntity<Void> removeTranslation(
            @PathVariable String entityType,
            @PathVariable String entityRef,
            @PathVariable String langTag) {
        i18nService.removeTranslation(entityRef, entityType, langTag);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/translations/{entityType}/{entityRef}")
    public ResponseEntity<Void> deleteAllTranslations(
            @PathVariable String entityType,
            @PathVariable String entityRef) {
        i18nService.deleteAllTranslations(entityRef);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/available-languages")
    public ResponseEntity<List<String>> getAvailableLanguages(
            @RequestParam(required = false) String entityType,
            @RequestParam(required = false) String entityRef) {
        if (entityRef != null && entityType != null) {
            List<String> languages = i18nService.getAvailableLanguages(entityRef, entityType);
            return ResponseEntity.ok(languages);
        }
        return ResponseEntity.ok(List.of());
    }

    @GetMapping("/language-options")
    public ResponseEntity<Map<String, String>> getLanguageOptions(
            @RequestParam(defaultValue = "en") String displayLanguage) {
        Map<String, String> options = i18nService.getLanguageOptions(displayLanguage);
        return ResponseEntity.ok(options);
    }

    @GetMapping("/localized-value")
    public ResponseEntity<String> getLocalizedValue(
            @RequestParam String entityRef,
            @RequestParam String entityType,
            @RequestParam String langTag,
            @RequestParam(required = false) String defaultValue) {
        String value = i18nService.getLocalizedValue(entityRef, entityType, langTag, defaultValue);
        return ResponseEntity.ok(value != null ? value : "");
    }

    public static class TranslationRequest {
        private String value;

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }
}
