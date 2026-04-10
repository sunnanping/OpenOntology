package com.by.open.ontology.ontologyservice.activitymodule.controller;

import com.by.open.ontology.ontologyservice.activitymodule.entity.Activity;
import com.by.open.ontology.ontologyservice.activitymodule.service.ActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/activity")
public class ActivityController {
    
    @Autowired
    private ActivityService activityService;
    
    @PostMapping("/create")
    public ResponseEntity<Activity> create(@RequestBody Map<String, Object> request) {
        Activity activity = new Activity();
        
        activity.setType((String) request.get("type"));
        activity.setAction((String) request.get("action"));
        activity.setDescription((String) request.get("description"));
        activity.setUserId((String) request.get("userId"));
        activity.setUserName((String) request.get("userName"));
        activity.setProjectId((String) request.get("projectId"));
        activity.setEntityId((String) request.get("entityId"));
        activity.setEntityType((String) request.get("entityType"));
        activity.setEntityName((String) request.get("entityName"));
        
        Activity createdActivity = activityService.create(activity);
        return ResponseEntity.ok(createdActivity);
    }
    
    @GetMapping("/findById/{id}")
    public ResponseEntity<Activity> findById(@PathVariable String id) {
        Activity activity = activityService.findById(id);
        if (activity != null) {
            return ResponseEntity.ok(activity);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/findByProjectId/{projectId}")
    public ResponseEntity<List<Activity>> findByProjectId(@PathVariable String projectId) {
        List<Activity> activities = activityService.findByProjectId(projectId);
        return ResponseEntity.ok(activities);
    }
    
    @GetMapping("/findByUserId/{userId}")
    public ResponseEntity<List<Activity>> findByUserId(@PathVariable String userId) {
        List<Activity> activities = activityService.findByUserId(userId);
        return ResponseEntity.ok(activities);
    }
    
    @GetMapping("/findByEntityId/{entityId}")
    public ResponseEntity<List<Activity>> findByEntityId(@PathVariable String entityId) {
        List<Activity> activities = activityService.findByEntityId(entityId);
        return ResponseEntity.ok(activities);
    }
    
    @GetMapping("/findByEntityType/{entityType}")
    public ResponseEntity<List<Activity>> findByEntityType(@PathVariable String entityType) {
        List<Activity> activities = activityService.findByEntityType(entityType);
        return ResponseEntity.ok(activities);
    }
    
    @GetMapping("/findByType/{type}")
    public ResponseEntity<List<Activity>> findByType(@PathVariable String type) {
        List<Activity> activities = activityService.findByType(type);
        return ResponseEntity.ok(activities);
    }
    
    @GetMapping("/findByAction/{action}")
    public ResponseEntity<List<Activity>> findByAction(@PathVariable String action) {
        List<Activity> activities = activityService.findByAction(action);
        return ResponseEntity.ok(activities);
    }
    
    @GetMapping("/findAll")
    public ResponseEntity<List<Activity>> findAll() {
        List<Activity> activities = activityService.findAll();
        return ResponseEntity.ok(activities);
    }
    
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        activityService.delete(id);
        return ResponseEntity.ok().build();
    }
    
    @DeleteMapping("/deleteByProjectId/{projectId}")
    public ResponseEntity<Void> deleteByProjectId(@PathVariable String projectId) {
        activityService.deleteByProjectId(projectId);
        return ResponseEntity.ok().build();
    }
    
    @DeleteMapping("/deleteByEntityId/{entityId}")
    public ResponseEntity<Void> deleteByEntityId(@PathVariable String entityId) {
        activityService.deleteByEntityId(entityId);
        return ResponseEntity.ok().build();
    }
    
    @DeleteMapping("/deleteByUserId/{userId}")
    public ResponseEntity<Void> deleteByUserId(@PathVariable String userId) {
        activityService.deleteByUserId(userId);
        return ResponseEntity.ok().build();
    }
}