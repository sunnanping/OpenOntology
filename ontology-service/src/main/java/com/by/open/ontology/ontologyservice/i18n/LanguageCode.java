package com.by.open.ontology.ontologyservice.i18n;

import java.util.HashMap;
import java.util.Map;
import java.util.Locale;

public class LanguageCode {
    public static final String ENGLISH = "en";
    public static final String CHINESE = "zh";
    public static final String CHINESE_SIMPLIFIED = "zh-CN";
    public static final String CHINESE_TRADITIONAL = "zh-TW";
    public static final String GERMAN = "de";
    public static final String FRENCH = "fr";
    public static final String SPANISH = "es";
    public static final String ITALIAN = "it";
    public static final String JAPANESE = "ja";
    public static final String KOREAN = "ko";
    public static final String RUSSIAN = "ru";
    public static final String PORTUGUESE = "pt";
    public static final String ARABIC = "ar";
    
    private static final Map<String, Map<String, String>> LANGUAGE_NAMES = new HashMap<>();
    
    static {
        Map<String, String> englishNames = new HashMap<>();
        englishNames.put(ENGLISH, "English");
        englishNames.put(CHINESE, "Chinese");
        englishNames.put(CHINESE_SIMPLIFIED, "Chinese (Simplified)");
        englishNames.put(CHINESE_TRADITIONAL, "Chinese (Traditional)");
        englishNames.put(GERMAN, "German");
        englishNames.put(FRENCH, "French");
        englishNames.put(SPANISH, "Spanish");
        englishNames.put(ITALIAN, "Italian");
        englishNames.put(JAPANESE, "Japanese");
        englishNames.put(KOREAN, "Korean");
        englishNames.put(RUSSIAN, "Russian");
        englishNames.put(PORTUGUESE, "Portuguese");
        englishNames.put(ARABIC, "Arabic");
        LANGUAGE_NAMES.put(ENGLISH, englishNames);
        
        Map<String, String> chineseNames = new HashMap<>();
        chineseNames.put(ENGLISH, "英语");
        chineseNames.put(CHINESE, "中文");
        chineseNames.put(CHINESE_SIMPLIFIED, "简体中文");
        chineseNames.put(CHINESE_TRADITIONAL, "繁体中文");
        chineseNames.put(GERMAN, "德语");
        chineseNames.put(FRENCH, "法语");
        chineseNames.put(SPANISH, "西班牙语");
        chineseNames.put(ITALIAN, "意大利语");
        chineseNames.put(JAPANESE, "日语");
        chineseNames.put(KOREAN, "韩语");
        chineseNames.put(RUSSIAN, "俄语");
        chineseNames.put(PORTUGUESE, "葡萄牙语");
        chineseNames.put(ARABIC, "阿拉伯语");
        LANGUAGE_NAMES.put(CHINESE, chineseNames);
        LANGUAGE_NAMES.put(CHINESE_SIMPLIFIED, chineseNames);
    }
    
    public static String getDisplayName(String languageCode, String displayLanguage) {
        if (languageCode == null || languageCode.isEmpty()) {
            return "";
        }
        
        Map<String, String> names = LANGUAGE_NAMES.get(displayLanguage);
        if (names == null) {
            names = LANGUAGE_NAMES.get(ENGLISH);
        }
        
        if (names != null) {
            String name = names.get(languageCode);
            if (name != null) {
                return name;
            }
        }
        
        Locale locale = Locale.forLanguageTag(languageCode);
        return locale.getDisplayName(Locale.forLanguageTag(displayLanguage));
    }
    
    public static String getNativeName(String languageCode) {
        if (languageCode == null || languageCode.isEmpty()) {
            return "";
        }
        
        Locale locale = Locale.forLanguageTag(languageCode);
        return locale.getDisplayName(locale);
    }
    
    public static boolean isValid(String languageCode) {
        if (languageCode == null || languageCode.isEmpty()) {
            return false;
        }
        
        try {
            Locale locale = Locale.forLanguageTag(languageCode);
            return locale != null && !locale.getLanguage().isEmpty();
        } catch (Exception e) {
            return false;
        }
    }
    
    public static String getDefaultLanguage() {
        return ENGLISH;
    }
}