package com.by.open.ontology.ontologyservice.classmodule.service;

import com.by.open.ontology.ontologyservice.classmodule.entity.Annotation;
import com.by.open.ontology.ontologyservice.classmodule.repository.AnnotationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class AnnotationService {

    @Autowired
    private AnnotationRepository annotationRepository;

    public Annotation create(Annotation annotation) {
        annotation.setCreatedDate(new Date());
        annotation.setLastModifiedDate(new Date());
        return annotationRepository.save(annotation);
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
            return annotationRepository.save(existingAnnotation);
        }
        return null;
    }

    public void delete(String id) {
        annotationRepository.deleteById(id);
    }

    public void deleteByEntityIdAndEntityType(String entityId, String entityType) {
        annotationRepository.deleteByEntityIdAndEntityType(entityId, entityType);
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
        return annotationRepository.save(annotation);
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
        
        return annotationRepository.saveAll(annotations);
    }
}
