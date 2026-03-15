package com.by.open.ontology.collaborationservice.repository;

import com.by.open.ontology.collaborationservice.entity.Comment;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends MongoRepository<Comment, String> {
    List<Comment> findBySessionId(String sessionId);
    List<Comment> findByOntologyId(String ontologyId);
    List<Comment> findByEntityId(String entityId);
    List<Comment> findByUserId(String userId);
}