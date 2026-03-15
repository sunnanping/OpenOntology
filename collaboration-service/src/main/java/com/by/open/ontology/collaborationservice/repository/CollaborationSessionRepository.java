package com.by.open.ontology.collaborationservice.repository;

import com.by.open.ontology.collaborationservice.entity.CollaborationSession;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CollaborationSessionRepository extends MongoRepository<CollaborationSession, String> {
    List<CollaborationSession> findByOntologyId(String ontologyId);
    List<CollaborationSession> findByStatus(String status);
    List<CollaborationSession> findByCreatedBy(String createdBy);
}