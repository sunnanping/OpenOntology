package com.by.open.ontology.i18nservice.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;

import java.util.Date;

@Document(collection = "translation_history")
public class TranslationHistory {
    @Id
    private String id;
    
    @Indexed
    private String entityRef;
    
    @Indexed
    private String entityType;
    
    @Indexed
    private String langTag;
    
    private String oldValue;
    
    private String newValue;
    
    private String action;
    
    private String proposedBy;
    
    private Date proposedAt;
    
    private String confirmedBy;
    
    private Date confirmedAt;
    
    private String approvalStatus;
    
    private String comment;
    
    private String source;

    public TranslationHistory() {
        this.source = "MANUAL";
        this.approvalStatus = "PENDING";
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEntityRef() {
        return entityRef;
    }

    public void setEntityRef(String entityRef) {
        this.entityRef = entityRef;
    }

    public String getEntityType() {
        return entityType;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    public String getLangTag() {
        return langTag;
    }

    public void setLangTag(String langTag) {
        this.langTag = langTag;
    }

    public String getOldValue() {
        return oldValue;
    }

    public void setOldValue(String oldValue) {
        this.oldValue = oldValue;
    }

    public String getNewValue() {
        return newValue;
    }

    public void setNewValue(String newValue) {
        this.newValue = newValue;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getProposedBy() {
        return proposedBy;
    }

    public void setProposedBy(String proposedBy) {
        this.proposedBy = proposedBy;
    }

    public Date getProposedAt() {
        return proposedAt;
    }

    public void setProposedAt(Date proposedAt) {
        this.proposedAt = proposedAt;
    }

    public String getConfirmedBy() {
        return confirmedBy;
    }

    public void setConfirmedBy(String confirmedBy) {
        this.confirmedBy = confirmedBy;
    }

    public Date getConfirmedAt() {
        return confirmedAt;
    }

    public void setConfirmedAt(Date confirmedAt) {
        this.confirmedAt = confirmedAt;
    }

    public String getApprovalStatus() {
        return approvalStatus;
    }

    public void setApprovalStatus(String approvalStatus) {
        this.approvalStatus = approvalStatus;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    @Override
    public String toString() {
        return "TranslationHistory{" +
                "id='" + id + '\'' +
                ", entityRef='" + entityRef + '\'' +
                ", entityType='" + entityType + '\'' +
                ", langTag='" + langTag + '\'' +
                ", action='" + action + '\'' +
                ", approvalStatus='" + approvalStatus + '\'' +
                ", source='" + source + '\'' +
                '}';
    }
}
