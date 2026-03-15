package com.by.open.ontology.collaborationservice.service;

import com.by.open.ontology.collaborationservice.entity.Comment;
import com.by.open.ontology.collaborationservice.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    public List<Comment> findAllComments() {
        return commentRepository.findAll();
    }

    public Optional<Comment> findCommentById(String id) {
        return commentRepository.findById(id);
    }

    public List<Comment> findCommentsBySessionId(String sessionId) {
        return commentRepository.findBySessionId(sessionId);
    }

    public List<Comment> findCommentsByOntologyId(String ontologyId) {
        return commentRepository.findByOntologyId(ontologyId);
    }

    public List<Comment> findCommentsByEntityId(String entityId) {
        return commentRepository.findByEntityId(entityId);
    }

    public Comment createComment(Comment comment) {
        comment.setCreatedAt(LocalDateTime.now());
        comment.setUpdatedAt(LocalDateTime.now());
        return commentRepository.save(comment);
    }

    public Comment updateComment(String id, Comment comment) {
        Optional<Comment> optionalComment = commentRepository.findById(id);
        if (optionalComment.isPresent()) {
            Comment existingComment = optionalComment.get();
            existingComment.setContent(comment.getContent());
            existingComment.setUpdatedAt(LocalDateTime.now());
            return commentRepository.save(existingComment);
        }
        return null;
    }

    public void deleteComment(String id) {
        commentRepository.deleteById(id);
    }
}