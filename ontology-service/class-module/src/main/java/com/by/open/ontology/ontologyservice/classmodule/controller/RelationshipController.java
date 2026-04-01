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

    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteByEntityIdAndEntityTypeAndPropertyAndValue(
            @RequestParam String entityId,
            @RequestParam String entityType,
            @RequestParam String property,
            @RequestParam String value) {
        relationshipService.deleteByEntityIdAndEntityTypeAndPropertyAndValue(entityId, entityType, property, value);
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
