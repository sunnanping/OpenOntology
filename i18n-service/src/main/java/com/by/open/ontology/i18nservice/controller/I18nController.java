package com.by.open.ontology.i18nservice.controller;

import com.by.open.ontology.i18nservice.entity.LanguageMap;
import com.by.open.ontology.i18nservice.entity.TranslationHistory;
import com.by.open.ontology.i18nservice.service.I18nService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/i18n")
@CrossOrigin(origins = "*")
public class I18nController {

    @Autowired
    private I18nService i18nService;

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
            @RequestBody Map<String, Object> request,
            HttpServletRequest httpRequest) {
        String username = httpRequest.getHeader("X-User-Name");
        @SuppressWarnings("unchecked")
        Map<String, String> translations = (Map<String, String>) request.get("translations");
        LanguageMap result = i18nService.saveTranslations(entityRef, entityType, translations, username);
        return ResponseEntity.ok(result);
    }

    @PutMapping("/translation/{entityType}/{entityRef}/{langTag}")
    public ResponseEntity<LanguageMap> updateTranslation(
            @PathVariable String entityType,
            @PathVariable String entityRef,
            @PathVariable String langTag,
            @RequestBody TranslationRequest request,
            HttpServletRequest httpRequest) {
        String username = httpRequest.getHeader("X-User-Name");
        LanguageMap result = i18nService.addTranslation(entityRef, entityType, langTag, request.getValue(), username);
        return ResponseEntity.ok(result);
    }

    @PutMapping("/translations/{entityType}/{entityRef}/{langTag}")
    public ResponseEntity<LanguageMap> addTranslation(
            @PathVariable String entityType,
            @PathVariable String entityRef,
            @PathVariable String langTag,
            @RequestBody TranslationRequest request,
            HttpServletRequest httpRequest) {
        String username = httpRequest.getHeader("X-User-Name");
        LanguageMap result = i18nService.addTranslation(entityRef, entityType, langTag, request.getValue(), username);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/translation/{entityType}/{entityRef}/{langTag}")
    public ResponseEntity<Void> removeTranslation(
            @PathVariable String entityType,
            @PathVariable String entityRef,
            @PathVariable String langTag,
            HttpServletRequest request) {
        String username = request.getHeader("X-User-Name");
        i18nService.removeTranslation(entityRef, entityType, langTag, username);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/translations/{entityType}/{entityRef}/{langTag}")
    public ResponseEntity<Void> removeTranslationOld(
            @PathVariable String entityType,
            @PathVariable String entityRef,
            @PathVariable String langTag,
            HttpServletRequest request) {
        String username = request.getHeader("X-User-Name");
        i18nService.removeTranslation(entityRef, entityType, langTag, username);
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

    @GetMapping("/translation-with-fallback")
    public Mono<LanguageMap> getTranslationWithFallback(
            @RequestParam String entityRef,
            @RequestParam String entityType,
            @RequestParam String langTag,
            @RequestParam(required = false) String fallbackLangTag,
            HttpServletRequest request) {
        String username = request.getHeader("X-User-Name");
        String fallback = fallbackLangTag != null ? fallbackLangTag : "en";
        return i18nService.getTranslationWithFallback(entityRef, entityType, langTag, fallback, username);
    }

    @PostMapping("/confirm-translation/{entityType}/{entityRef}/{langTag}")
    public ResponseEntity<LanguageMap> confirmTranslation(
            @PathVariable String entityType,
            @PathVariable String entityRef,
            @PathVariable String langTag,
            HttpServletRequest request) {
        String username = request.getHeader("X-User-Name");
        LanguageMap result = i18nService.confirmTranslation(entityRef, entityType, langTag, username);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/reject-translation/{entityType}/{entityRef}/{langTag}")
    public ResponseEntity<LanguageMap> rejectTranslation(
            @PathVariable String entityType,
            @PathVariable String entityRef,
            @PathVariable String langTag,
            @RequestBody RejectionRequest request,
            HttpServletRequest httpRequest) {
        String username = httpRequest.getHeader("X-User-Name");
        LanguageMap result = i18nService.rejectTranslation(entityRef, entityType, langTag, username, request.getComment());
        return ResponseEntity.ok(result);
    }

    @GetMapping("/history/{entityType}/{entityRef}")
    public ResponseEntity<List<TranslationHistory>> getTranslationHistory(
            @PathVariable String entityType,
            @PathVariable String entityRef) {
        List<TranslationHistory> history = i18nService.getTranslationHistory(entityRef, entityType);
        return ResponseEntity.ok(history);
    }

    @GetMapping("/history/user/{username}")
    public ResponseEntity<List<TranslationHistory>> getTranslationHistoryByUser(
            @PathVariable String username) {
        List<TranslationHistory> history = i18nService.getTranslationHistoryByUser(username);
        return ResponseEntity.ok(history);
    }

    @GetMapping("/history/status/{status}")
    public ResponseEntity<List<TranslationHistory>> getTranslationHistoryByStatus(
            @PathVariable String status) {
        List<TranslationHistory> history = i18nService.getTranslationHistoryByStatus(status);
        return ResponseEntity.ok(history);
    }

    @GetMapping("/history")
    public ResponseEntity<Page<TranslationHistory>> getTranslationHistory(
            @RequestParam(required = false) String entityRef,
            @RequestParam(required = false) String entityType,
            @RequestParam(required = false) String langTag,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Date start = parseDate(startDate);
        Date end = parseDate(endDate);
        
        List<TranslationHistory> allHistory = i18nService.getTranslationHistoryByFilters(
                entityRef, entityType, start, end, status, username);
        
        List<TranslationHistory> filteredHistory = allHistory;
        if (langTag != null && !langTag.isEmpty()) {
            filteredHistory = allHistory.stream()
                    .filter(h -> langTag.equals(h.getLangTag()))
                    .collect(Collectors.toList());
        }
        
        Pageable pageable = PageRequest.of(page, size);
        int startIdx = (int) pageable.getOffset();
        int endIdx = Math.min(startIdx + pageable.getPageSize(), filteredHistory.size());
        
        List<TranslationHistory> pageContent = filteredHistory.subList(
                startIdx, Math.max(startIdx, endIdx));
        
        Page<TranslationHistory> result = new PageImpl<>(pageContent, pageable, filteredHistory.size());
        return ResponseEntity.ok(result);
    }

    @GetMapping("/pending-translations")
    public ResponseEntity<Page<PendingTranslation>> getPendingTranslations(
            @RequestParam(required = false) String entityType,
            @RequestParam(required = false) String language,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        List<TranslationHistory> pendingHistory = i18nService.getTranslationHistoryByStatus("PENDING");
        
        List<PendingTranslation> pendingTranslations = pendingHistory.stream()
                .filter(h -> (entityType == null || entityType.isEmpty() || entityType.equals(h.getEntityType())) &&
                            (language == null || language.isEmpty() || language.equals(h.getLangTag())))
                .map(h -> new PendingTranslation(
                        h.getEntityRef(),
                        h.getEntityType(),
                        h.getLangTag(),
                        h.getNewValue(),
                        h.getProposedBy(),
                        h.getProposedAt(),
                        "AUTO_TRANSLATE".equals(h.getAction()) ? "EXTERNAL" : "MANUAL"
                ))
                .collect(Collectors.toList());
        
        Pageable pageable = PageRequest.of(page, size);
        int startIdx = (int) pageable.getOffset();
        int endIdx = Math.min(startIdx + pageable.getPageSize(), pendingTranslations.size());
        
        List<PendingTranslation> pageContent = pendingTranslations.subList(
                startIdx, Math.max(startIdx, endIdx));
        
        Page<PendingTranslation> result = new PageImpl<>(pageContent, pageable, pendingTranslations.size());
        return ResponseEntity.ok(result);
    }

    @GetMapping("/history/export")
    public ResponseEntity<String> exportTranslationHistory(
            @RequestParam(required = false) String entityRef,
            @RequestParam(required = false) String entityType,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String username) {
        Date start = parseDate(startDate);
        Date end = parseDate(endDate);
        
        List<TranslationHistory> history = i18nService.getTranslationHistoryByFilters(
                entityRef, entityType, start, end, status, username);
        
        StringBuilder csv = new StringBuilder();
        csv.append("EntityRef,EntityType,LangTag,OldValue,NewValue,Action,ProposedBy,ProposedAt,ConfirmedBy,ConfirmedAt,ApprovalStatus,Comment,Source\n");
        
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        for (TranslationHistory h : history) {
            csv.append(escapeCsv(h.getEntityRef())).append(",");
            csv.append(escapeCsv(h.getEntityType())).append(",");
            csv.append(escapeCsv(h.getLangTag())).append(",");
            csv.append(escapeCsv(h.getOldValue())).append(",");
            csv.append(escapeCsv(h.getNewValue())).append(",");
            csv.append(escapeCsv(h.getAction())).append(",");
            csv.append(escapeCsv(h.getProposedBy())).append(",");
            csv.append(h.getProposedAt() != null ? sdf.format(h.getProposedAt()) : "").append(",");
            csv.append(escapeCsv(h.getConfirmedBy())).append(",");
            csv.append(h.getConfirmedAt() != null ? sdf.format(h.getConfirmedAt()) : "").append(",");
            csv.append(escapeCsv(h.getApprovalStatus())).append(",");
            csv.append(escapeCsv(h.getComment())).append(",");
            csv.append(escapeCsv(h.getSource())).append("\n");
        }
        
        return ResponseEntity.ok()
                .header("Content-Type", "text/csv")
                .header("Content-Disposition", "attachment; filename=translation_history.csv")
                .body(csv.toString());
    }

    private Date parseDate(String dateStr) {
        if (dateStr == null || dateStr.isEmpty()) {
            return null;
        }
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            return sdf.parse(dateStr);
        } catch (Exception e) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                return sdf.parse(dateStr);
            } catch (Exception ex) {
                return null;
            }
        }
    }

    private String escapeCsv(String value) {
        if (value == null) {
            return "";
        }
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
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

    public static class RejectionRequest {
        private String comment;

        public String getComment() {
            return comment;
        }

        public void setComment(String comment) {
            this.comment = comment;
        }
    }

    public static class PendingTranslation {
        private String entityRef;
        private String entityType;
        private String language;
        private String value;
        private String lastProposedBy;
        private Date lastProposedAt;
        private String source;

        public PendingTranslation(String entityRef, String entityType, String language, String value,
                                String lastProposedBy, Date lastProposedAt, String source) {
            this.entityRef = entityRef;
            this.entityType = entityType;
            this.language = language;
            this.value = value;
            this.lastProposedBy = lastProposedBy;
            this.lastProposedAt = lastProposedAt;
            this.source = source;
        }

        public String getEntityRef() {
            return entityRef;
        }

        public String getEntityType() {
            return entityType;
        }

        public String getLanguage() {
            return language;
        }

        public String getValue() {
            return value;
        }

        public String getLastProposedBy() {
            return lastProposedBy;
        }

        public Date getLastProposedAt() {
            return lastProposedAt;
        }

        public String getSource() {
            return source;
        }
    }
}
