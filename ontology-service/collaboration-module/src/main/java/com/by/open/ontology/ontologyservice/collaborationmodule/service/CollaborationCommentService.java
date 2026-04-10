package com.by.open.ontology.ontologyservice.collaborationmodule.service;

import com.by.open.ontology.ontologyservice.collaborationmodule.entity.CollaborationComment;
import com.by.open.ontology.ontologyservice.collaborationmodule.repository.CollaborationCommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class CollaborationCommentService {
    
    @Autowired
    private CollaborationCommentRepository collaborationCommentRepository;
    
    public CollaborationComment create(CollaborationComment comment) {
        if (comment.getId() == null || comment.getId().isEmpty()) {
            comment.setId(UUID.randomUUID().toString());
        }
        return collaborationCommentRepository.save(comment);
    }
    
    public CollaborationComment findById(String id) {
        return collaborationCommentRepository.findById(id).orElse(null);
    }
    
    public List<CollaborationComment> findByProjectId(String projectId) {
        return collaborationCommentRepository.findByProjectIdOrderByCreatedDateDesc(projectId);
    }
    
    public List<CollaborationComment> findByEntityId(String entityId) {
        return collaborationCommentRepository.findByEntityIdOrderByCreatedDateDesc(entityId);
    }
    
    public List<CollaborationComment> findByEntityIdAndEntityType(String entityId, String entityType) {
        return collaborationCommentRepository.findByEntityIdAndEntityType(entityId, entityType);
    }
    
    public List<CollaborationComment> findByAuthorId(String authorId) {
        return collaborationCommentRepository.findByAuthorId(authorId);
    }
    
    public List<CollaborationComment> findAll() {
        return collaborationCommentRepository.findAll();
    }
    
    public CollaborationComment update(String id, CollaborationComment comment) {
        CollaborationComment existingComment = collaborationCommentRepository.findById(id).orElse(null);
        if (existingComment != null) {
            existingComment.setContent(comment.getContent());
            existingComment.setLastModifiedDate(comment.getLastModifiedDate());
            return collaborationCommentRepository.save(existingComment);
        }
        return null;
    }
    
    public void delete(String id) {
        collaborationCommentRepository.deleteById(id);
    }
    
    public void deleteByProjectId(String projectId) {
        List<CollaborationComment> comments = collaborationCommentRepository.findByProjectId(projectId);
        collaborationCommentRepository.deleteAll(comments);
    }
    
    public void deleteByEntityId(String entityId) {
        List<CollaborationComment> comments = collaborationCommentRepository.findByEntityId(entityId);
        collaborationCommentRepository.deleteAll(comments);
    }
}