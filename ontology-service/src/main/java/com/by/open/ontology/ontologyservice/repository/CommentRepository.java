package com.by.open.ontology.ontologyservice.repository;

import com.by.open.ontology.ontologyservice.entity.Comment;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends MongoRepository<Comment, String> {
    List<Comment> findByProjectId(String projectId);
    List<Comment> findByProjectIdAndEntityId(String projectId, String entityId);
    List<Comment> findByProjectIdOrderByCreatedAtDesc(String projectId);
}
