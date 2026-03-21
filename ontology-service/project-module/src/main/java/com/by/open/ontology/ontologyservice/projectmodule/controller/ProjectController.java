package com.by.open.ontology.ontologyservice.projectmodule.controller;

import com.by.open.ontology.ontologyservice.projectmodule.entity.Project;
import com.by.open.ontology.ontologyservice.projectmodule.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    @Autowired
    private ProjectService projectService;

    @GetMapping("/available")
    public List<Project> getAvailableProjects(@RequestParam String username) {
        List<Project> projects = projectService.findAll();
        List<Project> availableProjects = new ArrayList<>();
        
        for (Project project : projects) {
            if (!"TRASH".equals(project.getStatus())) {
                availableProjects.add(project);
            }
        }
        
        return availableProjects;
    }

    @GetMapping("/owned")
    public List<Project> getOwnedProjects(@RequestParam String username) {
        List<Project> projects = projectService.findByCreatorId(username);
        List<Project> ownedProjects = new ArrayList<>();
        
        for (Project project : projects) {
            if (!"TRASH".equals(project.getStatus())) {
                ownedProjects.add(project);
            }
        }
        
        return ownedProjects;
    }

    @GetMapping("/shared")
    public List<Project> getSharedProjects(@RequestParam String username) {
        List<Project> projects = projectService.findAll();
        List<Project> sharedProjects = new ArrayList<>();
        
        for (Project project : projects) {
            if (!username.equals(project.getCreatorId()) && 
                project.getContributors() != null && 
                containsUser(project.getContributors(), username) &&
                !"TRASH".equals(project.getStatus())) {
                sharedProjects.add(project);
            }
        }
        
        return sharedProjects;
    }

    @GetMapping("/trash")
    public List<Project> getTrashedProjects(@RequestParam String username) {
        List<Project> projects = projectService.findAll();
        List<Project> trashedProjects = new ArrayList<>();
        
        for (Project project : projects) {
            if ("TRASH".equals(project.getStatus()) && 
                (username.equals(project.getCreatorId()) || 
                 (project.getContributors() != null && containsUser(project.getContributors(), username)))) {
                trashedProjects.add(project);
            }
        }
        
        return trashedProjects;
    }

    @PutMapping("/move-to-trash/{id}")
    public Project moveToTrash(@PathVariable String id) {
        Project project = projectService.findById(id);
        if (project != null) {
            project.setStatus("TRASH");
            project.setLastModifiedDate(new Date());
            Project updated = projectService.update(id, project);
            return updated;
        }
        return null;
    }

    @PutMapping("/restore/{id}")
    public Project restoreProject(@PathVariable String id) {
        Project project = projectService.findById(id);
        if (project != null) {
            project.setStatus("ACTIVE");
            project.setLastModifiedDate(new Date());
            Project updated = projectService.update(id, project);
            return updated;
        }
        return null;
    }

    @DeleteMapping("/delete/{id}")
    public void deleteProject(@PathVariable String id) {
        projectService.delete(id);
    }

    @GetMapping("/download/{id}")
    public void downloadProject(@PathVariable String id, 
                                @RequestParam(defaultValue = "OWL") String format,
                                HttpServletResponse response) throws IOException {
        Project project = projectService.findById(id);
        if (project != null && project.getContent() != null) {
            String filename = project.getName() + getFileExtension(format);
            response.setContentType(getContentType(format));
            response.setHeader(HttpHeaders.CONTENT_DISPOSITION, 
                             "attachment; filename=\"" + filename + "\"");
            response.getOutputStream().write(project.getContent());
            response.getOutputStream().flush();
        }
    }

    @PutMapping("/update-last-opened/{id}")
    public Project updateLastOpened(@PathVariable String id) {
        Project project = projectService.updateLastOpened(id);
        return project;
    }

    private boolean containsUser(String[] users, String username) {
        if (users == null) return false;
        for (String user : users) {
            if (username.equals(user)) {
                return true;
            }
        }
        return false;
    }

    private String getFileExtension(String format) {
        switch (format.toUpperCase()) {
            case "OWL":
                return ".owl";
            case "RDF":
                return ".rdf";
            case "TURTLE":
                return ".ttl";
            case "JSONLD":
                return ".jsonld";
            default:
                return ".owl";
        }
    }

    private String getContentType(String format) {
        switch (format.toUpperCase()) {
            case "OWL":
                return "application/rdf+xml";
            case "RDF":
                return "application/rdf+xml";
            case "TURTLE":
                return "text/turtle";
            case "JSONLD":
                return "application/ld+json";
            default:
                return "application/rdf+xml";
        }
    }
}
