package com.by.open.ontology.ontologyservice.classmodule.service;

import com.by.open.ontology.ontologyservice.classmodule.entity.Annotation;
import com.by.open.ontology.ontologyservice.classmodule.entity.Class;
import com.by.open.ontology.ontologyservice.classmodule.repository.AnnotationRepository;
import com.by.open.ontology.ontologyservice.classmodule.repository.ClassRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
