package com.by.open.ontology.ontologyservice.commentmodule.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection = "comments")
public class Comment {
    
    @Id
    private String id;
    
    private String content;
    
    private String authorId;
    
    private String authorName;
    
    private String projectId;
    
    private String entityId;
    
    private String entityType;
    
    private Date createdDate;
    
    private Date lastModifiedDate;
    
    public Comment() {
        this.createdDate = new Date();
        this.lastModifiedDate = new Date();
    }
    
    public Comment(String content, String authorId, String authorName, String projectId, String entityId, String entityType) {
        this.content = content;
        this.authorId = authorId;
        this.authorName = authorName;
        this.projectId = projectId;
        this.entityId = entityId;
        this.entityType = entityType;
        this.createdDate = new Date();
        this.lastModifiedDate = new Date();
    }
    
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
        this.lastModifiedDate = new Date();
    }
    
    public String getAuthorId() {
        return authorId;
    }
    
    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }
    
    public String getAuthorName() {
        return authorName;
    }
    
    public void setAuthorName(String authorName) {
        this.authorName = authorName;
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
    
    public Date getCreatedDate() {
        return createdDate;
    }
    
    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }
    
    public Date getLastModifiedDate() {
        return lastModifiedDate;
    }
    
    public void setLastModifiedDate(Date lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }
}