package com.by.open.ontology.ontologyservice.classmodule.controller;

import com.by.open.ontology.ontologyservice.classmodule.entity.Relationship;
import com.by.open.ontology.ontologyservice.classmodule.service.RelationshipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/relationship")
public class RelationshipController {

    @Autowired
    private RelationshipService relationshipService;
    
    // 删除请求参数类
    private static class RelationshipDeleteRequest {
        private String entityId;
        private String entityType;
        private String property;
        private String value;
        
        // getters and setters
        public String getEntityId() {
            return entityId;
        }
        
        public void setEntityId(String entityId) {
            this.entityId = entityId;
        }
        
        public String getEntityType() {
            return entityType;
        }
        
        public void setEntityType(String entityType) {
            this.entityType = entityType;
        }
        
        public String getProperty() {
            return property;
        }
        
        public void setProperty(String property) {
            this.property = property;
        }
        
        public String getValue() {
            return value;
        }
        
        public void setValue(String value) {
            this.value = value;
        }
    }

    @PostMapping("/create")
    public ResponseEntity<Relationship> create(@RequestBody Relationship relationship) {
        Relationship createdRelationship = relationshipService.create(relationship);
        return ResponseEntity.ok(createdRelationship);
    }

    @GetMapping("/findByEntityIdAndEntityType/{entityId}/{entityType}")
    public ResponseEntity<List<Relationship>> findByEntityIdAndEntityType(
            @PathVariable String entityId, 
            @PathVariable String entityType) {
        List<Relationship> relationships = relationshipService.findByEntityIdAndEntityType(entityId, entityType);
        return ResponseEntity.ok(relationships);
    }

    @GetMapping("/findByEntityId/{entityId}")
    public ResponseEntity<List<Relationship>> findByEntityId(@PathVariable String entityId) {
        List<Relationship> relationships = relationshipService.findByEntityId(entityId);
        return ResponseEntity.ok(relationships);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Relationship> update(@PathVariable String id, @RequestBody Relationship relationship) {
        Relationship updatedRelationship = relationshipService.update(id, relationship);
        if (updatedRelationship != null) {
            return ResponseEntity.ok(updatedRelationship);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        relationshipService.delete(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/delete")
    public ResponseEntity<Void> deleteByEntityIdAndEntityTypeAndPropertyAndValue(
            @RequestBody RelationshipDeleteRequest request) {
        relationshipService.deleteByEntityIdAndEntityTypeAndPropertyAndValue(
                request.getEntityId(), 
                request.getEntityType(), 
                request.getProperty(), 
                request.getValue());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/set")
    public ResponseEntity<Relationship> setRelationship(@RequestBody Relationship relationship) {
        Relationship setRelationship = relationshipService.setRelationship(relationship);
        return ResponseEntity.ok(setRelationship);
    }

    @PostMapping("/batch")
    public ResponseEntity<List<Relationship>> batchSaveRelationships(@RequestBody BatchRelationshipsRequest request) {
        List<Relationship> savedRelationships = relationshipService.batchSaveRelationships(
            request.getEntityId(), 
            request.getEntityType(), 
            request.getRelationships()
        );
        return ResponseEntity.ok(savedRelationships);
    }
}
