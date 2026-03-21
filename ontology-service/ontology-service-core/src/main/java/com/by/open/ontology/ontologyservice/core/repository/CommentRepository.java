package com.by.open.ontology.ontologyservice.core.repository;

import com.by.open.ontology.ontologyservice.core.entity.Comment;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends MongoRepository<Comment, String> {
    List<Comment> findByProjectId(String projectId);
    List<Comment> findByEntityId(String entityId);
    List<Comment> findByProjectIdAndEntityId(String projectId, String entityId);
}
