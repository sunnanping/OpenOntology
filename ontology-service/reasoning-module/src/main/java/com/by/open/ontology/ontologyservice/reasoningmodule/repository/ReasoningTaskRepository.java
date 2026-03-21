package com.by.open.ontology.ontologyservice.reasoningmodule.repository;

import com.by.open.ontology.ontologyservice.reasoningmodule.entity.ReasoningTask;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReasoningTaskRepository extends MongoRepository<ReasoningTask, String> {
    List<ReasoningTask> findByOntologyId(String ontologyId);
    List<ReasoningTask> findByStatus(String status);
}
