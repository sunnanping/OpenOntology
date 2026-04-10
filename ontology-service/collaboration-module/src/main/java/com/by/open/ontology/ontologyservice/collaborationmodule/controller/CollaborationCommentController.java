package com.by.open.ontology.ontologyservice.collaborationmodule.controller;

import com.by.open.ontology.ontologyservice.collaborationmodule.entity.CollaborationComment;
import com.by.open.ontology.ontologyservice.collaborationmodule.service.CollaborationCommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/collaboration/comment")
public class CollaborationCommentController {
    
    @Autowired
    private CollaborationCommentService collaborationCommentService;
    
    @PostMapping("/create")
    public ResponseEntity<CollaborationComment> create(@RequestBody Map<String, Object> request) {
        CollaborationComment comment = new CollaborationComment();
        
        comment.setContent((String) request.get("content"));
        comment.setAuthorId((String) request.get("authorId"));
        comment.setAuthorName((String) request.get("authorName"));
        comment.setProjectId((String) request.get("projectId"));
        comment.setEntityId((String) request.get("entityId"));
        comment.setEntityType((String) request.get("entityType"));
        
        CollaborationComment createdComment = collaborationCommentService.create(comment);
        return ResponseEntity.ok(createdComment);
    }
    
    @GetMapping("/findById/{id}")
    public ResponseEntity<CollaborationComment> findById(@PathVariable String id) {
        CollaborationComment comment = collaborationCommentService.findById(id);
        if (comment != null) {
            return ResponseEntity.ok(comment);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/findByProjectId/{projectId}")
    public ResponseEntity<List<CollaborationComment>> findByProjectId(@PathVariable String projectId) {
        List<CollaborationComment> comments = collaborationCommentService.findByProjectId(projectId);
        return ResponseEntity.ok(comments);
    }
    
    @GetMapping("/findByEntityId/{entityId}")
    public ResponseEntity<List<CollaborationComment>> findByEntityId(@PathVariable String entityId) {
        List<CollaborationComment> comments = collaborationCommentService.findByEntityId(entityId);
        return ResponseEntity.ok(comments);
    }
    
    @GetMapping("/findByEntityIdAndEntityType/{entityId}/{entityType}")
    public ResponseEntity<List<CollaborationComment>> findByEntityIdAndEntityType(@PathVariable String entityId, @PathVariable String entityType) {
        List<CollaborationComment> comments = collaborationCommentService.findByEntityIdAndEntityType(entityId, entityType);
        return ResponseEntity.ok(comments);
    }
    
    @GetMapping("/findByAuthorId/{authorId}")
    public ResponseEntity<List<CollaborationComment>> findByAuthorId(@PathVariable String authorId) {
        List<CollaborationComment> comments = collaborationCommentService.findByAuthorId(authorId);
        return ResponseEntity.ok(comments);
    }
    
    @GetMapping("/findAll")
    public ResponseEntity<List<CollaborationComment>> findAll() {
        List<CollaborationComment> comments = collaborationCommentService.findAll();
        return ResponseEntity.ok(comments);
    }
    
    @PutMapping("/update/{id}")
    public ResponseEntity<CollaborationComment> update(@PathVariable String id, @RequestBody CollaborationComment comment) {
        CollaborationComment updatedComment = collaborationCommentService.update(id, comment);
        if (updatedComment != null) {
            return ResponseEntity.ok(updatedComment);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        collaborationCommentService.delete(id);
        return ResponseEntity.ok().build();
    }
    
    @DeleteMapping("/deleteByProjectId/{projectId}")
    public ResponseEntity<Void> deleteByProjectId(@PathVariable String projectId) {
        collaborationCommentService.deleteByProjectId(projectId);
        return ResponseEntity.ok().build();
    }
    
    @DeleteMapping("/deleteByEntityId/{entityId}")
    public ResponseEntity<Void> deleteByEntityId(@PathVariable String entityId) {
        collaborationCommentService.deleteByEntityId(entityId);
        return ResponseEntity.ok().build();
    }
}