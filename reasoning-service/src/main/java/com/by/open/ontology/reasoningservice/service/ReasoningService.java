package com.by.open.ontology.reasoningservice.service;

import com.by.open.ontology.reasoningservice.entity.ReasoningTask;
import com.by.open.ontology.reasoningservice.repository.ReasoningTaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ReasoningService {

    @Autowired
    private ReasoningTaskRepository reasoningTaskRepository;

    public List<ReasoningTask> findAll() {
        return reasoningTaskRepository.findAll();
    }

    public Optional<ReasoningTask> findById(String id) {
        return reasoningTaskRepository.findById(id);
    }

    public List<ReasoningTask> findByOntologyId(String ontologyId) {
        return reasoningTaskRepository.findByOntologyId(ontologyId);
    }

    public ReasoningTask createTask(ReasoningTask task) {
        task.setStatus("PENDING");
        task.setCreatedAt(LocalDateTime.now());
        return reasoningTaskRepository.save(task);
    }

    public ReasoningTask executeTask(String taskId) {
        Optional<ReasoningTask> optionalTask = reasoningTaskRepository.findById(taskId);
        if (optionalTask.isPresent()) {
            ReasoningTask task = optionalTask.get();
            task.setStatus("RUNNING");
            reasoningTaskRepository.save(task);

            try {
                Map<String, Object> results = performReasoning(task);
                task.setResults(results);
                task.setStatus("COMPLETED");
                task.setCompletedAt(LocalDateTime.now());
            } catch (Exception e) {
                task.setStatus("FAILED");
                task.setErrorMessage(e.getMessage());
            }

            return reasoningTaskRepository.save(task);
        }
        return null;
    }

    public ReasoningTask updateTask(String id, ReasoningTask task) {
        Optional<ReasoningTask> optionalTask = reasoningTaskRepository.findById(id);
        if (optionalTask.isPresent()) {
            ReasoningTask existingTask = optionalTask.get();
            existingTask.setName(task.getName());
            existingTask.setDescription(task.getDescription());
            existingTask.setReasonerType(task.getReasonerType());
            existingTask.setRules(task.getRules());
            return reasoningTaskRepository.save(existingTask);
        }
        return null;
    }

    public void deleteTask(String id) {
        reasoningTaskRepository.deleteById(id);
    }

    private Map<String, Object> performReasoning(ReasoningTask task) {
        Map<String, Object> results = new HashMap<>();
        results.put("inferredClasses", 0);
        results.put("inferredProperties", 0);
        results.put("inferredInstances", 0);
        results.put("inconsistencies", 0);
        results.put("reasonerType", task.getReasonerType());
        results.put("executionTime", System.currentTimeMillis());
        return results;
    }

    public List<ReasoningTask> getPendingTasks() {
        return reasoningTaskRepository.findByStatus("PENDING");
    }

    public List<ReasoningTask> getRunningTasks() {
        return reasoningTaskRepository.findByStatus("RUNNING");
    }
}