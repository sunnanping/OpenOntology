package com.by.open.ontology.reasoningservice.controller;

import com.by.open.ontology.reasoningservice.entity.ReasoningTask;
import com.by.open.ontology.reasoningservice.service.ReasoningService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/reasoning")
@CrossOrigin(origins = "*")
public class ReasoningController {

    @Autowired
    private ReasoningService reasoningService;

    @GetMapping("/findAll")
    public List<ReasoningTask> findAll() {
        return reasoningService.findAll();
    }

    @GetMapping("/findById/{id}")
    public ResponseEntity<ReasoningTask> findById(@PathVariable String id) {
        Optional<ReasoningTask> task = reasoningService.findById(id);
        return task.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/findByOntologyId/{ontologyId}")
    public List<ReasoningTask> findByOntologyId(@PathVariable String ontologyId) {
        return reasoningService.findByOntologyId(ontologyId);
    }

    @PostMapping("/create")
    public ReasoningTask createTask(@RequestBody ReasoningTask task) {
        return reasoningService.createTask(task);
    }

    @PostMapping("/execute/{taskId}")
    public ResponseEntity<ReasoningTask> executeTask(@PathVariable String taskId) {
        ReasoningTask task = reasoningService.executeTask(taskId);
        if (task != null) {
            return ResponseEntity.ok(task);
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<ReasoningTask> updateTask(@PathVariable String id, @RequestBody ReasoningTask task) {
        ReasoningTask updatedTask = reasoningService.updateTask(id, task);
        if (updatedTask != null) {
            return ResponseEntity.ok(updatedTask);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable String id) {
        reasoningService.deleteTask(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/pending")
    public List<ReasoningTask> getPendingTasks() {
        return reasoningService.getPendingTasks();
    }

    @GetMapping("/running")
    public List<ReasoningTask> getRunningTasks() {
        return reasoningService.getRunningTasks();
    }
}