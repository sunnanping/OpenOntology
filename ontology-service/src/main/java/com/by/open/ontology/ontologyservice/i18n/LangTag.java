package com.by.open.ontology.ontologyservice.i18n;

public class LangTag {
    private String languageCode;
    private String scriptCode;
    private String regionCode;
    private String variantCode;
    private String privateUse;

    public LangTag() {
    }

    public LangTag(String languageCode) {
        this.languageCode = languageCode;
    }

    public LangTag(String languageCode, String scriptCode, String regionCode) {
        this.languageCode = languageCode;
        this.scriptCode = scriptCode;
        this.regionCode = regionCode;
    }

    public static LangTag parse(String tag) {
        if (tag == null || tag.isEmpty()) {
            return null;
        }
        
        LangTag langTag = new LangTag();
        String[] parts = tag.split("-");
        
        if (parts.length > 0 && !parts[0].isEmpty()) {
            langTag.setLanguageCode(parts[0].toLowerCase());
        }
        
        if (parts.length > 1) {
            if (parts[1].length() == 4) {
                langTag.setScriptCode(parts[1]);
            } else if (parts[1].length() == 2) {
                langTag.setRegionCode(parts[1].toUpperCase());
            }
        }
        
        if (parts.length > 2) {
            if (parts[2].length() == 2) {
                langTag.setRegionCode(parts[2].toUpperCase());
            } else {
                langTag.setVariantCode(parts[2]);
            }
        }
        
        return langTag;
    }

    public static LangTag fromBCP47(String bcp47Tag) {
        return parse(bcp47Tag);
    }

    public String toBCP47() {
        StringBuilder sb = new StringBuilder();
        
        if (languageCode != null) {
            sb.append(languageCode);
        }
        
        if (scriptCode != null && !scriptCode.isEmpty()) {
            sb.append("-").append(scriptCode);
        }
        
        if (regionCode != null && !regionCode.isEmpty()) {
            sb.append("-").append(regionCode);
        }
        
        if (variantCode != null && !variantCode.isEmpty()) {
            sb.append("-").append(variantCode);
        }
        
        if (privateUse != null && !privateUse.isEmpty()) {
            sb.append("-").append(privateUse);
        }
        
        return sb.toString();
    }

    public String getLanguageCode() {
        return languageCode;
    }

    public void setLanguageCode(String languageCode) {
        this.languageCode = languageCode;
    }

    public String getScriptCode() {
        return scriptCode;
    }

    public void setScriptCode(String scriptCode) {
        this.scriptCode = scriptCode;
    }

    public String getRegionCode() {
        return regionCode;
    }

    public void setRegionCode(String regionCode) {
        this.regionCode = regionCode;
    }

    public String getVariantCode() {
        return variantCode;
    }

    public void setVariantCode(String variantCode) {
        this.variantCode = variantCode;
    }

    public String getPrivateUse() {
        return privateUse;
    }

    public void setPrivateUse(String privateUse) {
        this.privateUse = privateUse;
    }

    public String getDisplayName(String language) {
        return getLanguageDisplayName(language);
    }

    public String getLanguageDisplayName(String displayLanguage) {
        if (displayLanguage == null || displayLanguage.isEmpty()) {
            return toBCP47();
        }
        
        return LanguageCode.getDisplayName(languageCode, displayLanguage);
    }

    @Override
    public String toString() {
        return toBCP47();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LangTag langTag = (LangTag) o;
        return toBCP47().equals(langTag.toBCP47());
    }

    @Override
    public int hashCode() {
        return toBCP47().hashCode();
    }
}
