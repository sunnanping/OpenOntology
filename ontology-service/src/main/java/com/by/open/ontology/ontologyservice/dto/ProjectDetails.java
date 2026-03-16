package com.by.open.ontology.ontologyservice.dto;

import java.util.Date;

public class ProjectDetails {
    private String id;
    private String name;
    private String description;
    private String owner;
    private Date lastOpened;
    private Date lastModified;
    private Date createdAt;
    private String status;
    private String[] sharedWith;

    public ProjectDetails() {
    }

    public ProjectDetails(String id, String name, String description, String owner,
                         Date lastOpened, Date lastModified, Date createdAt,
                         String status, String[] sharedWith) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.owner = owner;
        this.lastOpened = lastOpened;
        this.lastModified = lastModified;
        this.createdAt = createdAt;
        this.status = status;
        this.sharedWith = sharedWith;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public Date getLastOpened() {
        return lastOpened;
    }

    public void setLastOpened(Date lastOpened) {
        this.lastOpened = lastOpened;
    }

    public Date getLastModified() {
        return lastModified;
    }

    public void setLastModified(Date lastModified) {
        this.lastModified = lastModified;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String[] getSharedWith() {
        return sharedWith;
    }

    public void setSharedWith(String[] sharedWith) {
        this.sharedWith = sharedWith;
    }
}
