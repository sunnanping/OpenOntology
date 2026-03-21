package com.by.open.ontology.ontologyservice.projectmodule.repository;

import com.by.open.ontology.ontologyservice.projectmodule.entity.Project;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectRepository extends MongoRepository<Project, String> {
    Project findByName(String name);
    List<Project> findByCreatorId(String creatorId);
    List<Project> findByStatus(String status);
}
