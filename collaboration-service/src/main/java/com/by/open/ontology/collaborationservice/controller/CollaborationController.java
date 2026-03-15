package com.by.open.ontology.collaborationservice.controller;

import com.by.open.ontology.collaborationservice.entity.CollaborationSession;
import com.by.open.ontology.collaborationservice.entity.Comment;
import com.by.open.ontology.collaborationservice.service.CollaborationService;
import com.by.open.ontology.collaborationservice.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/collaboration")
@CrossOrigin(origins = "*")
public class CollaborationController {

    @Autowired
    private CollaborationService collaborationService;

    @Autowired
    private CommentService commentService;

    @GetMapping("/session/findAll")
    public List<CollaborationSession> findAllSessions() {
        return collaborationService.findAllSessions();
    }

    @GetMapping("/session/findById/{id}")
    public ResponseEntity<CollaborationSession> findSessionById(@PathVariable String id) {
        Optional<CollaborationSession> session = collaborationService.findSessionById(id);
        return session.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/session/findByOntologyId/{ontologyId}")
    public List<CollaborationSession> findSessionsByOntologyId(@PathVariable String ontologyId) {
        return collaborationService.findSessionsByOntologyId(ontologyId);
    }

    @PostMapping("/session/create")
    public CollaborationSession createSession(@RequestBody CollaborationSession session) {
        return collaborationService.createSession(session);
    }

    @PutMapping("/session/update/{id}")
    public ResponseEntity<CollaborationSession> updateSession(@PathVariable String id, @RequestBody CollaborationSession session) {
        CollaborationSession updatedSession = collaborationService.updateSession(id, session);
        if (updatedSession != null) {
            return ResponseEntity.ok(updatedSession);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/session/{sessionId}/addParticipant/{userId}")
    public ResponseEntity<CollaborationSession> addParticipant(@PathVariable String sessionId, @PathVariable String userId) {
        CollaborationSession session = collaborationService.addParticipant(sessionId, userId);
        if (session != null) {
            return ResponseEntity.ok(session);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/session/{sessionId}/removeParticipant/{userId}")
    public ResponseEntity<CollaborationSession> removeParticipant(@PathVariable String sessionId, @PathVariable String userId) {
        CollaborationSession session = collaborationService.removeParticipant(sessionId, userId);
        if (session != null) {
            return ResponseEntity.ok(session);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/session/delete/{id}")
    public ResponseEntity<Void> deleteSession(@PathVariable String id) {
        collaborationService.deleteSession(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/session/end/{id}")
    public ResponseEntity<CollaborationSession> endSession(@PathVariable String id) {
        CollaborationSession session = collaborationService.endSession(id);
        if (session != null) {
            return ResponseEntity.ok(session);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/comment/findAll")
    public List<Comment> findAllComments() {
        return commentService.findAllComments();
    }

    @GetMapping("/comment/findById/{id}")
    public ResponseEntity<Comment> findCommentById(@PathVariable String id) {
        Optional<Comment> comment = commentService.findCommentById(id);
        return comment.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/comment/findBySessionId/{sessionId}")
    public List<Comment> findCommentsBySessionId(@PathVariable String sessionId) {
        return commentService.findCommentsBySessionId(sessionId);
    }

    @GetMapping("/comment/findByOntologyId/{ontologyId}")
    public List<Comment> findCommentsByOntologyId(@PathVariable String ontologyId) {
        return commentService.findCommentsByOntologyId(ontologyId);
    }

    @GetMapping("/comment/findByEntityId/{entityId}")
    public List<Comment> findCommentsByEntityId(@PathVariable String entityId) {
        return commentService.findCommentsByEntityId(entityId);
    }

    @PostMapping("/comment/create")
    public Comment createComment(@RequestBody Comment comment) {
        return commentService.createComment(comment);
    }

    @PutMapping("/comment/update/{id}")
    public ResponseEntity<Comment> updateComment(@PathVariable String id, @RequestBody Comment comment) {
        Comment updatedComment = commentService.updateComment(id, comment);
        if (updatedComment != null) {
            return ResponseEntity.ok(updatedComment);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/comment/delete/{id}")
    public ResponseEntity<Void> deleteComment(@PathVariable String id) {
        commentService.deleteComment(id);
        return ResponseEntity.ok().build();
    }
}