package com.by.open.ontology.ontologyservice.commentmodule.service;

import com.by.open.ontology.ontologyservice.commentmodule.entity.Comment;
import com.by.open.ontology.ontologyservice.commentmodule.repository.CommentModuleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class CommentService {
    
    @Autowired
    private CommentModuleRepository commentRepository;
    
    public Comment create(Comment comment) {
        if (comment.getId() == null || comment.getId().isEmpty()) {
            comment.setId(UUID.randomUUID().toString());
        }
        return commentRepository.save(comment);
    }
    
    public Comment findById(String id) {
        return commentRepository.findById(id).orElse(null);
    }
    
    public List<Comment> findByProjectId(String projectId) {
        return commentRepository.findByProjectIdOrderByCreatedDateDesc(projectId);
    }
    
    public List<Comment> findByEntityId(String entityId) {
        return commentRepository.findByEntityIdOrderByCreatedDateDesc(entityId);
    }
    
    public List<Comment> findByEntityIdAndEntityType(String entityId, String entityType) {
        return commentRepository.findByEntityIdAndEntityType(entityId, entityType);
    }
    
    public List<Comment> findByAuthorId(String authorId) {
        return commentRepository.findByAuthorId(authorId);
    }
    
    public List<Comment> findAll() {
        return commentRepository.findAll();
    }
    
    public Comment update(String id, Comment comment) {
        Comment existingComment = commentRepository.findById(id).orElse(null);
        if (existingComment != null) {
            existingComment.setContent(comment.getContent());
            existingComment.setLastModifiedDate(comment.getLastModifiedDate());
            return commentRepository.save(existingComment);
        }
        return null;
    }
    
    public void delete(String id) {
        commentRepository.deleteById(id);
    }
    
    public void deleteByProjectId(String projectId) {
        List<Comment> comments = commentRepository.findByProjectId(projectId);
        commentRepository.deleteAll(comments);
    }
    
    public void deleteByEntityId(String entityId) {
        List<Comment> comments = commentRepository.findByEntityId(entityId);
        commentRepository.deleteAll(comments);
    }
}