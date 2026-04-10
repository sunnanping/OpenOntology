package com.by.open.ontology.ontologyservice.activitymodule.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection = "activities")
public class Activity {
    
    @Id
    private String id;
    
    private String type;
    
    private String action;
    
    private String description;
    
    private String userId;
    
    private String userName;
    
    private String projectId;
    
    private String entityId;
    
    private String entityType;
    
    private String entityName;
    
    private Date timestamp;
    
    public Activity() {
        this.timestamp = new Date();
    }
    
    public Activity(String type, String action, String description, String userId, String userName, String projectId, String entityId, String entityType, String entityName) {
        this.type = type;
        this.action = action;
        this.description = description;
        this.userId = userId;
        this.userName = userName;
        this.projectId = projectId;
        this.entityId = entityId;
        this.entityType = entityType;
        this.entityName = entityName;
        this.timestamp = new Date();
    }
    
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public String getAction() {
        return action;
    }
    
    public void setAction(String action) {
        this.action = action;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getUserId() {
        return userId;
    }
    
    public void setUserId(String userId) {
        this.userId = userId;
    }
    
    public String getUserName() {
        return userName;
    }
    
    public void setUserName(String userName) {
        this.userName = userName;
    }
    
    public String getProjectId() {
        return projectId;
    }
    
    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }
    
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
    
    public String getEntityName() {
        return entityName;
    }
    
    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }
    
    public Date getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}