package com.by.open.ontology.ontologyservice.i18n;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class InternationalizationService {

    @Autowired
    private LanguageMapRepository languageMapRepository;

    public LanguageMap getTranslations(String entityRef, String entityType) {
        return languageMapRepository.findByEntityRefAndEntityType(entityRef, entityType)
                .orElse(LanguageMap.empty());
    }

    public LanguageMap saveTranslations(String entityRef, String entityType, Map<String, String> translations) {
        Optional<LanguageMap> existing = languageMapRepository.findByEntityRefAndEntityType(entityRef, entityType);
        
        LanguageMap languageMap;
        if (existing.isPresent()) {
            languageMap = existing.get();
            languageMap.setTranslations(translations);
        } else {
            languageMap = new LanguageMap(translations);
            languageMap.setEntityRef(entityRef);
            languageMap.setEntityType(entityType);
        }
        
        return languageMapRepository.save(languageMap);
    }

    public LanguageMap addTranslation(String entityRef, String entityType, String langTag, String value) {
        Optional<LanguageMap> existing = languageMapRepository.findByEntityRefAndEntityType(entityRef, entityType);
        
        LanguageMap languageMap;
        if (existing.isPresent()) {
            languageMap = existing.get();
        } else {
            languageMap = new LanguageMap();
            languageMap.setEntityRef(entityRef);
            languageMap.setEntityType(entityType);
        }
        
        languageMap.put(langTag, value);
        return languageMapRepository.save(languageMap);
    }

    public void removeTranslation(String entityRef, String entityType, String langTag) {
        Optional<LanguageMap> existing = languageMapRepository.findByEntityRefAndEntityType(entityRef, entityType);
        
        if (existing.isPresent()) {
            LanguageMap languageMap = existing.get();
            languageMap.remove(langTag);
            
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

    public List<LanguageMap> getTranslationsByEntityType(String entityType) {
        return languageMapRepository.findByEntityType(entityType);
    }

    public String getLocalizedValue(String entityRef, String entityType, String langTag, String defaultValue) {
        LanguageMap map = getTranslations(entityRef, entityType);
        return map.getOrDefault(langTag, defaultValue);
    }

    public String getLocalizedValue(String entityRef, String entityType, String langTag) {
        return getLocalizedValue(entityRef, entityType, langTag, null);
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

    public List<String> getAvailableLanguages(String entityRef, String entityType) {
        LanguageMap map = getTranslations(entityRef, entityType);
        return new ArrayList<>(map.getTranslations().keySet());
    }

    public Map<String, String> getLanguageOptions(String displayLanguage) {
        Map<String, String> options = new LinkedHashMap<>();
        
        String[] commonLanguages = {
            LanguageCode.ENGLISH,
            LanguageCode.CHINESE_SIMPLIFIED,
            LanguageCode.CHINESE_TRADITIONAL,
            LanguageCode.GERMAN,
            LanguageCode.FRENCH,
            LanguageCode.SPANISH,
            LanguageCode.ITALIAN,
            LanguageCode.JAPANESE,
            LanguageCode.KOREAN,
            LanguageCode.RUSSIAN,
            LanguageCode.PORTUGUESE,
            LanguageCode.ARABIC
        };
        
        for (String lang : commonLanguages) {
            options.put(lang, LanguageCode.getDisplayName(lang, displayLanguage));
        }
        
        return options;
    }
}
