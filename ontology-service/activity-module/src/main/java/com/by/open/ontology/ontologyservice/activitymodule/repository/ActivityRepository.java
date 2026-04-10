package com.by.open.ontology.ontologyservice.activitymodule.repository;

import com.by.open.ontology.ontologyservice.activitymodule.entity.Activity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ActivityRepository extends MongoRepository<Activity, String> {
    
    List<Activity> findByProjectId(String projectId);
    
    List<Activity> findByUserId(String userId);
    
    List<Activity> findByEntityId(String entityId);
    
    List<Activity> findByEntityType(String entityType);
    
    List<Activity> findByProjectIdOrderByTimestampDesc(String projectId);
    
    List<Activity> findByUserIdOrderByTimestampDesc(String userId);
    
    List<Activity> findByEntityIdOrderByTimestampDesc(String entityId);
    
    List<Activity> findByType(String type);
    
    List<Activity> findByAction(String action);
}
