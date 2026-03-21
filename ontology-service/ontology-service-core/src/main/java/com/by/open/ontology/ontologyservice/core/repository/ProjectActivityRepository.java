package com.by.open.ontology.ontologyservice.core.repository;

import com.by.open.ontology.ontologyservice.core.entity.ProjectActivity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectActivityRepository extends MongoRepository<ProjectActivity, String> {
    List<ProjectActivity> findByProjectId(String projectId);
    List<ProjectActivity> findByUserId(String userId);
    List<ProjectActivity> findByProjectIdOrderByTimestampDesc(String projectId);
}
