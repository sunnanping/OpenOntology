package com.by.open.ontology.ontologyservice.commentmodule.repository;

import com.by.open.ontology.ontologyservice.commentmodule.entity.Comment;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentModuleRepository extends MongoRepository<Comment, String> {
    
    List<Comment> findByProjectId(String projectId);
    
    List<Comment> findByEntityId(String entityId);
    
    List<Comment> findByEntityIdAndEntityType(String entityId, String entityType);
    
    List<Comment> findByAuthorId(String authorId);
    
    List<Comment> findByProjectIdOrderByCreatedDateDesc(String projectId);
    
    List<Comment> findByEntityIdOrderByCreatedDateDesc(String entityId);
}