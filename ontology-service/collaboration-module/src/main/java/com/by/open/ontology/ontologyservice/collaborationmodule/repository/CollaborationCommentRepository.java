package com.by.open.ontology.ontologyservice.collaborationmodule.repository;

import com.by.open.ontology.ontologyservice.collaborationmodule.entity.CollaborationComment;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CollaborationCommentRepository extends MongoRepository<CollaborationComment, String> {
    
    List<CollaborationComment> findByProjectId(String projectId);
    
    List<CollaborationComment> findByEntityId(String entityId);
    
    List<CollaborationComment> findByEntityIdAndEntityType(String entityId, String entityType);
    
    List<CollaborationComment> findByAuthorId(String authorId);
    
    List<CollaborationComment> findByProjectIdOrderByCreatedDateDesc(String projectId);
    
    List<CollaborationComment> findByEntityIdOrderByCreatedDateDesc(String entityId);
}