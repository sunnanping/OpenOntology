package com.by.open.ontology.ontologyservice.classmodule.repository;

import com.by.open.ontology.ontologyservice.classmodule.entity.Relationship;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface RelationshipRepository extends MongoRepository<Relationship, String> {
    List<Relationship> findByEntityIdAndEntityType(String entityId, String entityType);
    void deleteByEntityIdAndEntityType(String entityId, String entityType);
}
