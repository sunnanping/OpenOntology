package com.by.open.ontology.ontologyservice.datatypemodule.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "datatypes")
public class Datatype {
    
    @Id
    private String id;
    
    private String name;
    
    private String iri;
    
    private String ontologyId;
    
    private String projectId;
    
    private String description;
    
    private boolean isStandard;
    
    public Datatype() {
    }
    
    public Datatype(String name, String iri, String ontologyId, String projectId) {
        this.name = name;
        this.iri = iri;
        this.ontologyId = ontologyId;
        this.projectId = projectId;
        this.isStandard = false;
    }
    
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getIri() {
        return iri;
    }
    
    public void setIri(String iri) {
        this.iri = iri;
    }
    
    public String getOntologyId() {
        return ontologyId;
    }
    
    public void setOntologyId(String ontologyId) {
        this.ontologyId = ontologyId;
    }
    
    public String getProjectId() {
        return projectId;
    }
    
    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public boolean isStandard() {
        return isStandard;
    }
    
    public void setStandard(boolean standard) {
        isStandard = standard;
    }
}