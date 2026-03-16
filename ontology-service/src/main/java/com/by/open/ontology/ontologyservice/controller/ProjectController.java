package com.by.open.ontology.ontologyservice.controller;

import com.by.open.ontology.ontologyservice.dto.ProjectDetails;
import com.by.open.ontology.ontologyservice.entity.Ontology;
import com.by.open.ontology.ontologyservice.service.OntologyService;
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
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    @Autowired
    private OntologyService ontologyService;

    @GetMapping("/available")
    public List<ProjectDetails> getAvailableProjects(@RequestParam String username) {
        List<Ontology> ontologies = ontologyService.findAll();
        List<ProjectDetails> projects = new ArrayList<>();
        
        for (Ontology ontology : ontologies) {
            if (!"TRASH".equals(ontology.getStatus())) {
                ProjectDetails details = convertToProjectDetails(ontology);
                projects.add(details);
            }
        }
        
        return projects;
    }

    @GetMapping("/owned")
    public List<ProjectDetails> getOwnedProjects(@RequestParam String username) {
        List<Ontology> ontologies = ontologyService.findAll();
        List<ProjectDetails> projects = new ArrayList<>();
        
        for (Ontology ontology : ontologies) {
            if (username.equals(ontology.getCreatorId()) && !"TRASH".equals(ontology.getStatus())) {
                ProjectDetails details = convertToProjectDetails(ontology);
                projects.add(details);
            }
        }
        
        return projects;
    }

    @GetMapping("/shared")
    public List<ProjectDetails> getSharedProjects(@RequestParam String username) {
        List<Ontology> ontologies = ontologyService.findAll();
        List<ProjectDetails> projects = new ArrayList<>();
        
        for (Ontology ontology : ontologies) {
            if (!username.equals(ontology.getCreatorId()) && 
                ontology.getContributors() != null && 
                containsUser(ontology.getContributors(), username) &&
                !"TRASH".equals(ontology.getStatus())) {
                ProjectDetails details = convertToProjectDetails(ontology);
                projects.add(details);
            }
        }
        
        return projects;
    }

    @GetMapping("/trash")
    public List<ProjectDetails> getTrashedProjects(@RequestParam String username) {
        List<Ontology> ontologies = ontologyService.findAll();
        List<ProjectDetails> projects = new ArrayList<>();
        
        for (Ontology ontology : ontologies) {
            if ("TRASH".equals(ontology.getStatus()) && 
                (username.equals(ontology.getCreatorId()) || 
                 (ontology.getContributors() != null && containsUser(ontology.getContributors(), username)))) {
                ProjectDetails details = convertToProjectDetails(ontology);
                projects.add(details);
            }
        }
        
        return projects;
    }

    @PutMapping("/move-to-trash/{id}")
    public ProjectDetails moveToTrash(@PathVariable String id) {
        Ontology ontology = ontologyService.findById(id);
        if (ontology != null) {
            ontology.setStatus("TRASH");
            ontology.setLastModifiedDate(new Date());
            Ontology updated = ontologyService.update(ontology);
            return convertToProjectDetails(updated);
        }
        return null;
    }

    @PutMapping("/restore/{id}")
    public ProjectDetails restoreProject(@PathVariable String id) {
        Ontology ontology = ontologyService.findById(id);
        if (ontology != null) {
            ontology.setStatus("ACTIVE");
            ontology.setLastModifiedDate(new Date());
            Ontology updated = ontologyService.update(ontology);
            return convertToProjectDetails(updated);
        }
        return null;
    }

    @DeleteMapping("/delete/{id}")
    public void deleteProject(@PathVariable String id) {
        ontologyService.delete(id);
    }

    @GetMapping("/download/{id}")
    public void downloadProject(@PathVariable String id, 
                                @RequestParam(defaultValue = "OWL") String format,
                                HttpServletResponse response) throws IOException {
        Ontology ontology = ontologyService.findById(id);
        if (ontology != null && ontology.getContent() != null) {
            String filename = ontology.getName() + getFileExtension(format);
            response.setContentType(getContentType(format));
            response.setHeader(HttpHeaders.CONTENT_DISPOSITION, 
                             "attachment; filename=\"" + filename + "\"");
            response.getOutputStream().write(ontology.getContent());
            response.getOutputStream().flush();
        }
    }

    @PutMapping("/update-last-opened/{id}")
    public ProjectDetails updateLastOpened(@PathVariable String id) {
        Ontology ontology = ontologyService.updateLastOpened(id);
        return convertToProjectDetails(ontology);
    }

    private ProjectDetails convertToProjectDetails(Ontology ontology) {
        ProjectDetails details = new ProjectDetails();
        details.setId(ontology.getId());
        details.setName(ontology.getName());
        details.setDescription(ontology.getDescription());
        details.setOwner(ontology.getCreatorId());
        details.setLastOpened(ontology.getLastOpened());
        details.setLastModified(ontology.getLastModifiedDate());
        details.setCreatedAt(ontology.getCreatedDate());
        details.setStatus(ontology.getStatus());
        details.setSharedWith(ontology.getContributors());
        return details;
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
