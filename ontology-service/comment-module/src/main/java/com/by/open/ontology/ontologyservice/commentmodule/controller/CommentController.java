package com.by.open.ontology.ontologyservice.commentmodule.controller;

import com.by.open.ontology.ontologyservice.commentmodule.entity.Comment;
import com.by.open.ontology.ontologyservice.commentmodule.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/comment")
public class CommentController {
    
    @Autowired
    private CommentService commentService;
    
    @PostMapping("/create")
    public ResponseEntity<Comment> create(@RequestBody Map<String, Object> request) {
        Comment comment = new Comment();
        
        comment.setContent((String) request.get("content"));
        comment.setAuthorId((String) request.get("authorId"));
        comment.setAuthorName((String) request.get("authorName"));
        comment.setProjectId((String) request.get("projectId"));
        comment.setEntityId((String) request.get("entityId"));
        comment.setEntityType((String) request.get("entityType"));
        
        Comment createdComment = commentService.create(comment);
        return ResponseEntity.ok(createdComment);
    }
    
    @GetMapping("/findById/{id}")
    public ResponseEntity<Comment> findById(@PathVariable String id) {
        Comment comment = commentService.findById(id);
        if (comment != null) {
            return ResponseEntity.ok(comment);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/findByProjectId/{projectId}")
    public ResponseEntity<List<Comment>> findByProjectId(@PathVariable String projectId) {
        List<Comment> comments = commentService.findByProjectId(projectId);
        return ResponseEntity.ok(comments);
    }
    
    @GetMapping("/findByEntityId/{entityId}")
    public ResponseEntity<List<Comment>> findByEntityId(@PathVariable String entityId) {
        List<Comment> comments = commentService.findByEntityId(entityId);
        return ResponseEntity.ok(comments);
    }
    
    @GetMapping("/findByEntityIdAndEntityType/{entityId}/{entityType}")
    public ResponseEntity<List<Comment>> findByEntityIdAndEntityType(@PathVariable String entityId, @PathVariable String entityType) {
        List<Comment> comments = commentService.findByEntityIdAndEntityType(entityId, entityType);
        return ResponseEntity.ok(comments);
    }
    
    @GetMapping("/findByAuthorId/{authorId}")
    public ResponseEntity<List<Comment>> findByAuthorId(@PathVariable String authorId) {
        List<Comment> comments = commentService.findByAuthorId(authorId);
        return ResponseEntity.ok(comments);
    }
    
    @GetMapping("/findAll")
    public ResponseEntity<List<Comment>> findAll() {
        List<Comment> comments = commentService.findAll();
        return ResponseEntity.ok(comments);
    }
    
    @PutMapping("/update/{id}")
    public ResponseEntity<Comment> update(@PathVariable String id, @RequestBody Comment comment) {
        Comment updatedComment = commentService.update(id, comment);
        if (updatedComment != null) {
            return ResponseEntity.ok(updatedComment);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        commentService.delete(id);
        return ResponseEntity.ok().build();
    }
    
    @DeleteMapping("/deleteByProjectId/{projectId}")
    public ResponseEntity<Void> deleteByProjectId(@PathVariable String projectId) {
        commentService.deleteByProjectId(projectId);
        return ResponseEntity.ok().build();
    }
    
    @DeleteMapping("/deleteByEntityId/{entityId}")
    public ResponseEntity<Void> deleteByEntityId(@PathVariable String entityId) {
        commentService.deleteByEntityId(entityId);
        return ResponseEntity.ok().build();
    }
}