package com.by.open.ontology.ontologyservice.classmodule.repository;

import com.by.open.ontology.ontologyservice.classmodule.entity.Annotation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnnotationRepository extends MongoRepository<Annotation, String> {
    List<Annotation> findByEntityIdAndEntityType(String entityId, String entityType);
    List<Annotation> findByEntityId(String entityId);
    void deleteByEntityIdAndEntityType(String entityId, String entityType);
}
