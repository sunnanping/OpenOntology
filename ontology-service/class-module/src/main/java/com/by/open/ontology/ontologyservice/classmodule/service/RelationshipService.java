package com.by.open.ontology.ontologyservice.classmodule.service;

import com.by.open.ontology.ontologyservice.classmodule.entity.Relationship;
import com.by.open.ontology.ontologyservice.classmodule.repository.RelationshipRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class RelationshipService {

    @Autowired
    private RelationshipRepository relationshipRepository;

    public Relationship create(Relationship relationship) {
        relationship.setCreatedDate(new Date());
        relationship.setLastModifiedDate(new Date());
        return relationshipRepository.save(relationship);
    }

    public List<Relationship> findByEntityIdAndEntityType(String entityId, String entityType) {
        return relationshipRepository.findByEntityIdAndEntityType(entityId, entityType);
    }

    public List<Relationship> findByEntityId(String entityId) {
        return relationshipRepository.findByEntityIdAndEntityType(entityId, "CLASS");
    }

    public Relationship update(String id, Relationship relationship) {
        Relationship existingRelationship = relationshipRepository.findById(id).orElse(null);
        if (existingRelationship != null) {
            existingRelationship.setProperty(relationship.getProperty());
            existingRelationship.setValue(relationship.getValue());
            existingRelationship.setLanguage(relationship.getLanguage());
            existingRelationship.setLastModifiedDate(new Date());
            return relationshipRepository.save(existingRelationship);
        }
        return null;
    }

    public void delete(String id) {
        relationshipRepository.deleteById(id);
    }

    public void deleteByEntityIdAndEntityType(String entityId, String entityType) {
        relationshipRepository.deleteByEntityIdAndEntityType(entityId, entityType);
    }

    public void deleteByEntityIdAndEntityTypeAndPropertyAndValue(String entityId, String entityType, String property, String value) {
        List<Relationship> relationships = relationshipRepository.findByEntityIdAndEntityType(entityId, entityType);
        for (Relationship relationship : relationships) {
            if (relationship.getProperty().equals(property) &&
                relationship.getValue().equals(value)) {
                relationshipRepository.deleteById(relationship.getId());
            }
        }
    }

    public Relationship setRelationship(Relationship relationship) {
        // Delete existing relationship with same property and value (regardless of language)
        List<Relationship> existingRelationships = relationshipRepository.findByEntityIdAndEntityType(
            relationship.getEntityId(), relationship.getEntityType());
        
        for (Relationship existing : existingRelationships) {
            if (existing.getProperty().equals(relationship.getProperty()) &&
                existing.getValue().equals(relationship.getValue())) {
                relationshipRepository.deleteById(existing.getId());
            }
        }
        
        // Create new relationship
        relationship.setCreatedDate(new Date());
        relationship.setLastModifiedDate(new Date());
        return relationshipRepository.save(relationship);
    }

    public List<Relationship> batchSaveRelationships(String entityId, String entityType, List<Relationship> relationships) {
        // Delete existing relationships for this entity
        relationshipRepository.deleteByEntityIdAndEntityType(entityId, entityType);
        
        // Save new relationships
        Date now = new Date();
        for (Relationship relationship : relationships) {
            relationship.setEntityId(entityId);
            relationship.setEntityType(entityType);
            relationship.setCreatedDate(now);
            relationship.setLastModifiedDate(now);
        }
        
        return relationshipRepository.saveAll(relationships);
    }
}
