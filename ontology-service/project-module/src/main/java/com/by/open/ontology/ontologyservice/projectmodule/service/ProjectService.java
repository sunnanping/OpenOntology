package com.by.open.ontology.ontologyservice.projectmodule.service;

import com.by.open.ontology.ontologyservice.projectmodule.entity.Project;
import com.by.open.ontology.ontologyservice.projectmodule.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class ProjectService {

    @Autowired
    private ProjectRepository projectRepository;

    public Project create(Project project) {
        project.setCreatedDate(new Date());
        project.setLastModifiedDate(new Date());
        return projectRepository.save(project);
    }

    public Project findById(String id) {
        return projectRepository.findById(id).orElse(null);
    }

    public Project findByName(String name) {
        return projectRepository.findByName(name);
    }

    public List<Project> findByCreatorId(String creatorId) {
        return projectRepository.findByCreatorId(creatorId);
    }

    public List<Project> findByStatus(String status) {
        return projectRepository.findByStatus(status);
    }

    public List<Project> findAll() {
        return projectRepository.findAll();
    }

    public Project update(String id, Project project) {
        Project existingProject = projectRepository.findById(id).orElse(null);
        if (existingProject != null) {
            existingProject.setName(project.getName());
            existingProject.setDescription(project.getDescription());
            existingProject.setNamespace(project.getNamespace());
            existingProject.setContributors(project.getContributors());
            existingProject.setStatus(project.getStatus());
            existingProject.setFormat(project.getFormat());
            existingProject.setContent(project.getContent());
            existingProject.setLastModifiedDate(new Date());
            return projectRepository.save(existingProject);
        }
        return null;
    }

    public void delete(String id) {
        projectRepository.deleteById(id);
    }

    public Project updateLastOpened(String id) {
        Project project = projectRepository.findById(id).orElse(null);
        if (project != null) {
            project.setLastOpened(new Date());
            return projectRepository.save(project);
        }
        return null;
    }
}
