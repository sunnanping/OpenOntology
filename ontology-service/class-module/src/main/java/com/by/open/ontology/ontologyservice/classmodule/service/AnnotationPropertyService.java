package com.by.open.ontology.ontologyservice.classmodule.service;

import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AnnotationPropertyService {

    // 常用的Annotation属性列表
    private static final List<String> COMMON_ANNOTATION_PROPERTIES = Arrays.asList(
        // RDFS标准属性
        "rdfs:label",
        "rdfs:comment",
        "rdfs:seeAlso",
        "rdfs:isDefinedBy",
        
        // SKOS标准属性
        "skos:prefLabel",
        "skos:altLabel",
        "skos:hiddenLabel",
        "skos:definition",
        "skos:example",
        "skos:note",
        "skos:changeNote",
        "skos:editorialNote",
        "skos:historyNote",
        "skos:scopeNote",
        "skos:comment",
        "skos:CollectableProperty",
        
        // DC标准属性
        "dc:title",
        "dc:description",
        "dc:creator",
        "dc:date",
        "dc:source",
        "dc:subject",
        "dc:rights",
        "dc:relation",
        
        // OWL标准属性
        "owl:incompatibleWith",
        "owl:backwardCompatibleWith",
        "owl:deprecated",
        "owl:priorVersion",
        "owl:versionInfo"
    );

    /**
     * 根据搜索关键字获取匹配的Annotation属性列表
     * @param keyword 搜索关键字
     * @return 匹配的属性列表
     */
    public List<String> searchAnnotationProperties(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return COMMON_ANNOTATION_PROPERTIES;
        }
        
        String lowerKeyword = keyword.toLowerCase();
        return COMMON_ANNOTATION_PROPERTIES.stream()
            .filter(prop -> prop.toLowerCase().contains(lowerKeyword))
            .collect(Collectors.toList());
    }

    /**
     * 获取所有常用的Annotation属性
     * @return 所有常用属性列表
     */
    public List<String> getAllAnnotationProperties() {
        return COMMON_ANNOTATION_PROPERTIES;
    }
}
