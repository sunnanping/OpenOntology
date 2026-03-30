package com.by.open.ontology.ontologyservice.classmodule.service;

import com.by.open.ontology.ontologyservice.classmodule.entity.Annotation;
import com.by.open.ontology.ontologyservice.classmodule.entity.Class;
import com.by.open.ontology.ontologyservice.classmodule.repository.AnnotationRepository;
import com.by.open.ontology.ontologyservice.classmodule.repository.ClassRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class AnnotationService {

    @Autowired
    private AnnotationRepository annotationRepository;
    
    @Autowired
    private ClassRepository classRepository;

    public Annotation create(Annotation annotation) {
        annotation.setCreatedDate(new Date());
        annotation.setLastModifiedDate(new Date());
        Annotation savedAnnotation = annotationRepository.save(annotation);
        
        // Update Class entity annotations
        updateClassAnnotations(annotation.getEntityId());
        
        return savedAnnotation;
    }

    public List<Annotation> findByEntityIdAndEntityType(String entityId, String entityType) {
        return annotationRepository.findByEntityIdAndEntityType(entityId, entityType);
    }

    public List<Annotation> findByEntityId(String entityId) {
        return annotationRepository.findByEntityId(entityId);
    }

    public Annotation update(String id, Annotation annotation) {
        Annotation existingAnnotation = annotationRepository.findById(id).orElse(null);
        if (existingAnnotation != null) {
            existingAnnotation.setProperty(annotation.getProperty());
            existingAnnotation.setLanguage(annotation.getLanguage());
            existingAnnotation.setValue(annotation.getValue());
            existingAnnotation.setLastModifiedDate(new Date());
            Annotation updatedAnnotation = annotationRepository.save(existingAnnotation);
            
            // Update Class entity annotations
            updateClassAnnotations(existingAnnotation.getEntityId());
            
            return updatedAnnotation;
        }
        return null;
    }

    public void delete(String id) {
        Annotation annotation = annotationRepository.findById(id).orElse(null);
        if (annotation != null) {
            String entityId = annotation.getEntityId();
            annotationRepository.deleteById(id);
            
            // Update Class entity annotations
            updateClassAnnotations(entityId);
        }
    }

    public void deleteByEntityIdAndEntityType(String entityId, String entityType) {
        annotationRepository.deleteByEntityIdAndEntityType(entityId, entityType);
        
        // Update Class entity annotations
        updateClassAnnotations(entityId);
    }

    public void deleteByEntityIdAndEntityTypeAndPropertyAndLanguage(String entityId, String entityType, String property, String language) {
        List<Annotation> annotations = annotationRepository.findByEntityIdAndEntityType(entityId, entityType);
        for (Annotation annotation : annotations) {
            if (annotation.getProperty().equals(property) &&
                (language == null ? annotation.getLanguage() == null : 
                 language.equals(annotation.getLanguage()))) {
                annotationRepository.deleteById(annotation.getId());
            }
        }
        
        // Update Class entity annotations
        updateClassAnnotations(entityId);
    }

    public Annotation setAnnotation(Annotation annotation) {
        // Delete existing annotation with same property and language
        List<Annotation> existingAnnotations = annotationRepository.findByEntityIdAndEntityType(
            annotation.getEntityId(), annotation.getEntityType());
        
        for (Annotation existing : existingAnnotations) {
            if (existing.getProperty().equals(annotation.getProperty()) &&
                (existing.getLanguage() == null ? annotation.getLanguage() == null : 
                 existing.getLanguage().equals(annotation.getLanguage()))) {
                annotationRepository.deleteById(existing.getId());
            }
        }
        
        // Create new annotation
        annotation.setCreatedDate(new Date());
        annotation.setLastModifiedDate(new Date());
        Annotation savedAnnotation = annotationRepository.save(annotation);
        
        // Update Class entity annotations
        updateClassAnnotations(annotation.getEntityId());
        
        return savedAnnotation;
    }

    public List<Annotation> batchSaveAnnotations(String entityId, String entityType, List<Annotation> annotations) {
        // Delete existing annotations for this entity
        annotationRepository.deleteByEntityIdAndEntityType(entityId, entityType);
        
        // Save new annotations
        Date now = new Date();
        for (Annotation annotation : annotations) {
            annotation.setEntityId(entityId);
            annotation.setEntityType(entityType);
            annotation.setCreatedDate(now);
            annotation.setLastModifiedDate(now);
        }
        
        List<Annotation> savedAnnotations = annotationRepository.saveAll(annotations);
        
        // Update Class entity annotations
        updateClassAnnotations(entityId);
        
        return savedAnnotations;
    }
    
    /**
     * 处理批量Annotation的创建和更新
     * 支持从单个value字段解析多个annotation
     * 
     * @param entityId 实体ID
     * @param entityType 实体类型
     * @param property 注解属性
     * @param value 包含多个annotation的value字符串,数据示例：
     * @return 处理后的Annotation列表
     */
    public List<Annotation> processAnnotations(String entityId, String entityType, String property, String value) {
        // 解析value字段为多个Annotation对象
        List<Annotation> parsedAnnotations = AnnotationParser.parseAnnotations(value, property, entityId, entityType);
        
        List<Annotation> result = new ArrayList<>();
        Date now = new Date();
        
        // 检查现有注解
        List<Annotation> existingAnnotations = annotationRepository.findByEntityIdAndEntityType(entityId, entityType);
        
        for (Annotation parsedAnnotation : parsedAnnotations) {
            // 检查是否已存在相同的property和language
            boolean found = false;
            for (Annotation existing : existingAnnotations) {
                if (existing.getProperty().equals(parsedAnnotation.getProperty()) &&
                    (existing.getLanguage() == null ? parsedAnnotation.getLanguage() == null : 
                     existing.getLanguage().equals(parsedAnnotation.getLanguage()))) {
                    // 更新现有注解
                    existing.setValue(parsedAnnotation.getValue());
                    existing.setLastModifiedDate(now);
                    result.add(annotationRepository.save(existing));
                    found = true;
                    break;
                }
            }
            
            if (!found) {
                // 创建新注解
                parsedAnnotation.setCreatedDate(now);
                parsedAnnotation.setLastModifiedDate(now);
                result.add(annotationRepository.save(parsedAnnotation));
            }
        }
        
        // Update Class entity annotations
        updateClassAnnotations(entityId);
        
        return result;
    }
    
    /**
     * 更新Class实体的annotations字段
     * @param entityId Class实体ID
     */
    private void updateClassAnnotations(String entityId) {
        Class classEntity = classRepository.findById(entityId).orElse(null);
        if (classEntity != null) {
            List<Annotation> annotations = annotationRepository.findByEntityIdAndEntityType(entityId, "CLASS");
            List<Class.Annotation> classAnnotations = new java.util.ArrayList<>();
            for (Annotation annotation : annotations) {
                Class.Annotation classAnnotation = new Class.Annotation(
                    annotation.getProperty(),
                    annotation.getValue(),
                    annotation.getLanguage()
                );
                classAnnotations.add(classAnnotation);
            }
            classEntity.setAnnotations(classAnnotations);
            classRepository.save(classEntity);
        }
    }
}
