package com.by.open.ontology.ontologyservice.classmodule.service;

import com.by.open.ontology.ontologyservice.classmodule.entity.Annotation;

import java.util.ArrayList;
import java.util.List;

public class AnnotationParser {

    /**
     * 解析Annotation的value字段，支持多种格式
     * 支持的格式：
     * 1. "办公室"@zh, "Office"@en
     * 2. 办公室@zh,Office@en
     * 3. 办公室@,Office@en（@后为空表示lang为空）
     * 
     * @param value 包含多个annotation的value字符串
     * @param property 注解属性
     * @param entityId 实体ID
     * @param entityType 实体类型
     * @return 解析后的Annotation列表
     */
    public static List<Annotation> parseAnnotations(String value, String property, String entityId, String entityType) {
        List<Annotation> annotations = new ArrayList<>();
        
        if (value == null || value.trim().isEmpty()) {
            return annotations;
        }
        
        // 去掉字符串后的分号
        value = value.trim();
        if (value.endsWith(";")) {
            value = value.substring(0, value.length() - 1).trim();
        }
        
        // 按逗号分割多个annotation
        String[] parts = value.split(",");
        
        for (String part : parts) {
            part = part.trim();
            if (part.isEmpty()) {
                continue;
            }
            
            Annotation annotation = parseSingleAnnotation(part, property, entityId, entityType);
            if (annotation != null) {
                annotations.add(annotation);
            }
        }
        
        return annotations;
    }
    
    /**
     * 解析单个annotation
     * 
     * @param part 单个annotation字符串
     * @param property 注解属性
     * @param entityId 实体ID
     * @param entityType 实体类型
     * @return 解析后的Annotation对象
     */
    private static Annotation parseSingleAnnotation(String part, String property, String entityId, String entityType) {
        Annotation annotation = new Annotation();
        annotation.setEntityId(entityId);
        annotation.setEntityType(entityType);
        annotation.setProperty(property);
        
        // 查找@符号的位置
        int atIndex = part.lastIndexOf('@');
        
        if (atIndex != -1) {
            // 提取value部分
            String valuePart = part.substring(0, atIndex).trim();
            // 提取language部分
            String langPart = part.substring(atIndex + 1).trim();
            
            // 处理带引号的value
            if (valuePart.startsWith("\"")) {
                int endQuoteIndex = valuePart.lastIndexOf('\"');
                if (endQuoteIndex != -1) {
                    valuePart = valuePart.substring(1, endQuoteIndex).trim();
                }
            }
            
            // 处理带引号的language
            if (langPart.startsWith("\"")) {
                int endQuoteIndex = langPart.lastIndexOf('\"');
                if (endQuoteIndex != -1) {
                    langPart = langPart.substring(1, endQuoteIndex).trim();
                }
            }
            
            // 处理空language
            if (langPart.isEmpty()) {
                annotation.setLanguage(null);
            } else {
                annotation.setLanguage(langPart);
            }
            
            annotation.setValue(valuePart);
        } else {
            // 没有@符号，整个部分作为value，language为null
            // 处理带引号的value
            if (part.startsWith("\"")) {
                int endQuoteIndex = part.lastIndexOf('\"');
                if (endQuoteIndex != -1) {
                    part = part.substring(1, endQuoteIndex).trim();
                }
            }
            annotation.setValue(part.trim());
            annotation.setLanguage(null);
        }
        
        return annotation;
    }
}
