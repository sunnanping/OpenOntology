package com.by.open.ontology.i18nservice.service;

import com.by.open.ontology.i18nservice.entity.LanguageMap;
import com.by.open.ontology.i18nservice.entity.TranslationHistory;
import com.by.open.ontology.i18nservice.repository.LanguageMapRepository;
import com.by.open.ontology.i18nservice.repository.TranslationHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.*;

@Service
public class I18nService {

    @Autowired
    private LanguageMapRepository languageMapRepository;

    @Autowired
    private TranslationHistoryRepository translationHistoryRepository;

    @Autowired
    private ExternalTranslationService externalTranslationService;

    @Cacheable(value = "translations", key = "#entityRef + ':' + #entityType")
    public LanguageMap getTranslations(String entityRef, String entityType) {
        return languageMapRepository.findByEntityRefAndEntityType(entityRef, entityType)
                .orElse(LanguageMap.empty());
    }

    public LanguageMap saveTranslations(String entityRef, String entityType, Map<String, String> translations, String username) {
        Optional<LanguageMap> existing = languageMapRepository.findByEntityRefAndEntityType(entityRef, entityType);
        
        LanguageMap languageMap;
        if (existing.isPresent()) {
            languageMap = existing.get();
            Map<String, String> oldTranslations = new HashMap<>(languageMap.getTranslations());
            languageMap.setTranslations(translations);
            
            for (Map.Entry<String, String> entry : translations.entrySet()) {
                if (!Objects.equals(oldTranslations.get(entry.getKey()), entry.getValue())) {
                    recordTranslationHistory(entityRef, entityType, entry.getKey(), 
                            oldTranslations.get(entry.getKey()), entry.getValue(), "UPDATE", username);
                }
            }
        } else {
            languageMap = new LanguageMap(translations);
            languageMap.setEntityRef(entityRef);
            languageMap.setEntityType(entityType);
            
            for (Map.Entry<String, String> entry : translations.entrySet()) {
                recordTranslationHistory(entityRef, entityType, entry.getKey(), 
                        null, entry.getValue(), "CREATE", username);
            }
        }
        
        languageMap.setLastProposedBy(username);
        languageMap.setLastProposedAt(new Date());
        languageMap.setSource("MANUAL");
        languageMap.setConfirmed(false);
        
        return languageMapRepository.save(languageMap);
    }

    public LanguageMap addTranslation(String entityRef, String entityType, String langTag, String value, String username) {
        Optional<LanguageMap> existing = languageMapRepository.findByEntityRefAndEntityType(entityRef, entityType);
        
        LanguageMap languageMap;
        String oldValue = null;
        
        if (existing.isPresent()) {
            languageMap = existing.get();
            oldValue = languageMap.getTranslations().get(langTag);
        } else {
            languageMap = new LanguageMap();
            languageMap.setEntityRef(entityRef);
            languageMap.setEntityType(entityType);
        }
        
        languageMap.put(langTag, value);
        languageMap.setLastProposedBy(username);
        languageMap.setLastProposedAt(new Date());
        languageMap.setSource("MANUAL");
        languageMap.setConfirmed(false);
        
        recordTranslationHistory(entityRef, entityType, langTag, oldValue, value, "ADD", username);
        
        return languageMapRepository.save(languageMap);
    }

    public void removeTranslation(String entityRef, String entityType, String langTag, String username) {
        Optional<LanguageMap> existing = languageMapRepository.findByEntityRefAndEntityType(entityRef, entityType);
        
        if (existing.isPresent()) {
            LanguageMap languageMap = existing.get();
            String oldValue = languageMap.getTranslations().get(langTag);
            languageMap.remove(langTag);
            
            recordTranslationHistory(entityRef, entityType, langTag, oldValue, null, "DELETE", username);
            
            if (languageMap.isEmpty()) {
                languageMapRepository.delete(languageMap);
            } else {
                languageMapRepository.save(languageMap);
            }
        }
    }

    public void deleteAllTranslations(String entityRef) {
        languageMapRepository.deleteByEntityRef(entityRef);
    }

    public List<String> getAvailableLanguages(String entityRef, String entityType) {
        LanguageMap map = getTranslations(entityRef, entityType);
        return new ArrayList<>(map.getTranslations().keySet());
    }

    public String getLocalizedValue(String entityRef, String entityType, String langTag, String defaultValue) {
        LanguageMap map = getTranslations(entityRef, entityType);
        return map.getOrDefault(langTag, defaultValue);
    }

    public String getLocalizedValueWithFallback(String entityRef, String entityType, String preferredLangTag, String fallbackLangTag) {
        LanguageMap map = getTranslations(entityRef, entityType);
        
        Optional<String> value = map.get(preferredLangTag);
        if (value.isPresent()) {
            return value.get();
        }
        
        value = map.get(fallbackLangTag);
        if (value.isPresent()) {
            return value.get();
        }
        
        if (!map.isEmpty()) {
            return map.getTranslations().values().iterator().next();
        }
        
        return null;
    }

    public Map<String, String> getLanguageOptions(String displayLanguage) {
        Map<String, String> options = new LinkedHashMap<>();
        
        String[] commonLanguages = {
            "en", "zh-CN", "zh-TW", "de", "fr", "es", "it", "ja", "ko", "ru", "pt", "ar"
        };
        
        Map<String, String> languageNames = new HashMap<>();
        languageNames.put("en", "English");
        languageNames.put("zh-CN", "简体中文");
        languageNames.put("zh-TW", "繁體中文");
        languageNames.put("de", "Deutsch");
        languageNames.put("fr", "Français");
        languageNames.put("es", "Español");
        languageNames.put("it", "Italiano");
        languageNames.put("ja", "日本語");
        languageNames.put("ko", "한국어");
        languageNames.put("ru", "Русский");
        languageNames.put("pt", "Português");
        languageNames.put("ar", "العربية");
        
        for (String lang : commonLanguages) {
            options.put(lang, languageNames.get(lang));
        }
        
        return options;
    }

    public Mono<LanguageMap> getTranslationWithFallback(String entityRef, String entityType, String langTag, 
            String fallbackLangTag, String username) {
        LanguageMap existingMap = getTranslations(entityRef, entityType);
        
        if (existingMap.containsKey(langTag)) {
            return Mono.just(existingMap);
        }
        
        String sourceLang = "en";
        if (!existingMap.isEmpty()) {
            sourceLang = existingMap.getTranslations().keySet().iterator().next();
        }
        
        String sourceText = existingMap.getOrDefault(sourceLang, "");
        if (sourceText.isEmpty()) {
            return Mono.just(existingMap);
        }
        
        return externalTranslationService.translate(sourceText, sourceLang, langTag, "MYMEMORY")
                .map(translatedText -> {
                    existingMap.put(langTag, translatedText);
                    existingMap.setLastProposedBy(username);
                    existingMap.setLastProposedAt(new Date());
                    existingMap.setSource("EXTERNAL");
                    existingMap.setConfirmed(false);
                    
                    recordTranslationHistory(entityRef, entityType, langTag, null, translatedText, "AUTO_TRANSLATE", username);
                    
                    return languageMapRepository.save(existingMap);
                })
                .onErrorResume(e -> Mono.just(existingMap));
    }

    public LanguageMap confirmTranslation(String entityRef, String entityType, String langTag, String username) {
        Optional<LanguageMap> existing = languageMapRepository.findByEntityRefAndEntityType(entityRef, entityType);
        
        if (existing.isPresent()) {
            LanguageMap languageMap = existing.get();
            languageMap.setConfirmed(true);
            languageMap.setLastConfirmedBy(username);
            languageMap.setLastConfirmedAt(new Date());
            
            List<TranslationHistory> histories = translationHistoryRepository
                    .findByEntityRefAndEntityTypeAndLangTag(entityRef, entityType, langTag);
            
            for (TranslationHistory history : histories) {
                if ("PENDING".equals(history.getApprovalStatus())) {
                    history.setConfirmedBy(username);
                    history.setConfirmedAt(new Date());
                    history.setApprovalStatus("APPROVED");
                    translationHistoryRepository.save(history);
                }
            }
            
            return languageMapRepository.save(languageMap);
        }
        
        throw new RuntimeException("Translation not found");
    }

    public LanguageMap rejectTranslation(String entityRef, String entityType, String langTag, String username, String comment) {
        Optional<LanguageMap> existing = languageMapRepository.findByEntityRefAndEntityType(entityRef, entityType);
        
        if (existing.isPresent()) {
            LanguageMap languageMap = existing.get();
            languageMap.remove(langTag);
            
            List<TranslationHistory> histories = translationHistoryRepository
                    .findByEntityRefAndEntityTypeAndLangTag(entityRef, entityType, langTag);
            
            for (TranslationHistory history : histories) {
                if ("PENDING".equals(history.getApprovalStatus())) {
                    history.setConfirmedBy(username);
                    history.setConfirmedAt(new Date());
                    history.setApprovalStatus("REJECTED");
                    history.setComment(comment);
                    translationHistoryRepository.save(history);
                }
            }
            
            return languageMapRepository.save(languageMap);
        }
        
        throw new RuntimeException("Translation not found");
    }

    private void recordTranslationHistory(String entityRef, String entityType, String langTag, 
            String oldValue, String newValue, String action, String username) {
        TranslationHistory history = new TranslationHistory();
        history.setEntityRef(entityRef);
        history.setEntityType(entityType);
        history.setLangTag(langTag);
        history.setOldValue(oldValue);
        history.setNewValue(newValue);
        history.setAction(action);
        history.setProposedBy(username);
        history.setProposedAt(new Date());
        history.setApprovalStatus("PENDING");
        
        translationHistoryRepository.save(history);
    }

    public List<TranslationHistory> getTranslationHistory(String entityRef, String entityType) {
        return translationHistoryRepository.findByEntityRefAndEntityType(entityRef, entityType);
    }

    public List<TranslationHistory> getTranslationHistoryByUser(String username) {
        return translationHistoryRepository.findByProposedBy(username);
    }

    public List<TranslationHistory> getTranslationHistoryByStatus(String status) {
        return translationHistoryRepository.findByApprovalStatus(status);
    }

    public List<TranslationHistory> getTranslationHistoryByDateRange(Date startDate, Date endDate) {
        return translationHistoryRepository.findByProposedAtBetween(startDate, endDate);
    }

    public List<TranslationHistory> getTranslationHistoryByFilters(String entityRef, String entityType, 
            Date startDate, Date endDate, String status, String username) {
        List<TranslationHistory> result = translationHistoryRepository
                .findByEntityRefAndEntityTypeAndProposedAtBetween(entityRef, entityType, startDate, endDate);
        
        if (status != null && !status.isEmpty()) {
            result = result.stream()
                    .filter(h -> status.equals(h.getApprovalStatus()))
                    .collect(java.util.stream.Collectors.toList());
        }
        
        if (username != null && !username.isEmpty()) {
            result = result.stream()
                    .filter(h -> username.equals(h.getProposedBy()))
                    .collect(java.util.stream.Collectors.toList());
        }
        
        return result;
    }
}
