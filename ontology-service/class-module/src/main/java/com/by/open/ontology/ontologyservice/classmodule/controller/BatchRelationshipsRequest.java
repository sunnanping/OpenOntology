package com.by.open.ontology.ontologyservice.classmodule.controller;

import com.by.open.ontology.ontologyservice.classmodule.entity.Relationship;
import java.util.List;

public class BatchRelationshipsRequest {
    private String entityId;
    private String entityType;
    private List<Relationship> relationships;

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

    public List<Relationship> getRelationships() {
        return relationships;
    }

    public void setRelationships(List<Relationship> relationships) {
        this.relationships = relationships;
    }
}
