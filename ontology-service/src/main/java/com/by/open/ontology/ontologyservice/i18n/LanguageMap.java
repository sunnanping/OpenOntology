package com.by.open.ontology.ontologyservice.i18n;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Document(collection = "language_maps")
public class LanguageMap {
    @Id
    private String id;
    
    private Map<String, String> translations;
    
    @Indexed
    private String entityRef;
    
    private String entityType;

    public LanguageMap() {
        this.translations = new HashMap<>();
    }

    public LanguageMap(Map<String, String> translations) {
        this.translations = translations != null ? new HashMap<>(translations) : new HashMap<>();
    }

    public static LanguageMap empty() {
        return new LanguageMap();
    }

    public static LanguageMap of(String langTag, String value) {
        LanguageMap map = new LanguageMap();
        map.put(langTag, value);
        return map;
    }

    public static LanguageMap of(String langTag1, String value1, String langTag2, String value2) {
        LanguageMap map = new LanguageMap();
        map.put(langTag1, value1);
        map.put(langTag2, value2);
        return map;
    }

    public Optional<String> get(String langTag) {
        if (langTag == null || translations == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(translations.get(langTag));
    }

    public String getOrDefault(String langTag, String defaultValue) {
        return get(langTag).orElse(defaultValue);
    }

    public void put(String langTag, String value) {
        if (translations == null) {
            translations = new HashMap<>();
        }
        if (langTag != null && value != null) {
            translations.put(langTag, value);
        }
    }

    public void remove(String langTag) {
        if (translations != null && langTag != null) {
            translations.remove(langTag);
        }
    }

    public boolean containsKey(String langTag) {
        return translations != null && langTag != null && translations.containsKey(langTag);
    }

    public boolean isEmpty() {
        return translations == null || translations.isEmpty();
    }

    public int size() {
        return translations == null ? 0 : translations.size();
    }

    public Map<String, String> getTranslations() {
        return translations;
    }

    public void setTranslations(Map<String, String> translations) {
        this.translations = translations;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEntityRef() {
        return entityRef;
    }

    public void setEntityRef(String entityRef) {
        this.entityRef = entityRef;
    }

    public String getEntityType() {
        return entityType;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    @Override
    public String toString() {
        return "LanguageMap{" +
                "id='" + id + '\'' +
                ", translations=" + translations +
                ", entityRef='" + entityRef + '\'' +
                ", entityType='" + entityType + '\'' +
                '}';
    }
}
