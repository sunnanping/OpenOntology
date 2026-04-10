package com.by.open.ontology.ontologyservice.activitymodule.service;

import com.by.open.ontology.ontologyservice.activitymodule.entity.Activity;
import com.by.open.ontology.ontologyservice.activitymodule.repository.ActivityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ActivityService {
    
    @Autowired
    private ActivityRepository activityRepository;
    
    public Activity create(Activity activity) {
        if (activity.getId() == null || activity.getId().isEmpty()) {
            activity.setId(UUID.randomUUID().toString());
        }
        return activityRepository.save(activity);
    }
    
    public Activity findById(String id) {
        return activityRepository.findById(id).orElse(null);
    }
    
    public List<Activity> findByProjectId(String projectId) {
        return activityRepository.findByProjectIdOrderByTimestampDesc(projectId);
    }
    
    public List<Activity> findByUserId(String userId) {
        return activityRepository.findByUserIdOrderByTimestampDesc(userId);
    }
    
    public List<Activity> findByEntityId(String entityId) {
        return activityRepository.findByEntityIdOrderByTimestampDesc(entityId);
    }
    
    public List<Activity> findByEntityType(String entityType) {
        return activityRepository.findByEntityType(entityType);
    }
    
    public List<Activity> findByType(String type) {
        return activityRepository.findByType(type);
    }
    
    public List<Activity> findByAction(String action) {
        return activityRepository.findByAction(action);
    }
    
    public List<Activity> findAll() {
        return activityRepository.findAll();
    }
    
    public void delete(String id) {
        activityRepository.deleteById(id);
    }
    
    public void deleteByProjectId(String projectId) {
        List<Activity> activities = activityRepository.findByProjectId(projectId);
        activityRepository.deleteAll(activities);
    }
    
    public void deleteByEntityId(String entityId) {
        List<Activity> activities = activityRepository.findByEntityId(entityId);
        activityRepository.deleteAll(activities);
    }
    
    public void deleteByUserId(String userId) {
        List<Activity> activities = activityRepository.findByUserId(userId);
        activityRepository.deleteAll(activities);
    }
}
