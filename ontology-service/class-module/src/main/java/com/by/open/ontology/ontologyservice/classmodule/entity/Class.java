package com.by.open.ontology.ontologyservice.classmodule.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Document(collection = "classes")
public class Class {
    @Id
    private String id;
    private String name;
    private String iri;
    private String ontologyId;
    private String description;
    private String languageTag;
    private List<String> superClasses;
    private List<String> subClasses;
    private List<String> properties;
    private List<String> individuals;
    private List<Annotation> annotations;
    private List<Relationship> relationships;
    private boolean abstractClass;
    private Date createdDate;
    private Date lastModifiedDate;
    private String creatorId;
    
    // 内部类：注解
    public static class Annotation {
        private String property;
        private String value;
        private String language;
        
        public Annotation() {}
        
        public Annotation(String property, String value, String language) {
            this.property = property;
            this.value = value;
            this.language = language;
        }
        
        public String getProperty() { return property; }
        public void setProperty(String property) { this.property = property; }
        public String getValue() { return value; }
        public void setValue(String value) { this.value = value; }
        public String getLanguage() { return language; }
        public void setLanguage(String language) { this.language = language; }
    }
    
    // 内部类：关系
    public static class Relationship {
        private String property;
        private String target;
        private String language;
        
        public Relationship() {}
        
        public Relationship(String property, String target, String language) {
            this.property = property;
            this.target = target;
            this.language = language;
        }
        
        public String getProperty() { return property; }
        public void setProperty(String property) { this.property = property; }
        public String getTarget() { return target; }
        public void setTarget(String target) { this.target = target; }
        public String getLanguage() { return language; }
        public void setLanguage(String language) { this.language = language; }
    }

    public Class() {
    }

    public Class(String name, String iri, String ontologyId, String description) {
        this.name = name;
        this.iri = iri;
        this.ontologyId = ontologyId;
        this.description = description;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getSuperClasses() {
        return superClasses;
    }

    public void setSuperClasses(List<String> superClasses) {
        this.superClasses = superClasses;
    }

    public List<String> getSubClasses() {
        return subClasses;
    }

    public void setSubClasses(List<String> subClasses) {
        this.subClasses = subClasses;
    }

    public List<String> getProperties() {
        return properties;
    }

    public void setProperties(List<String> properties) {
        this.properties = properties;
    }

    public List<String> getIndividuals() {
        return individuals;
    }

    public void setIndividuals(List<String> individuals) {
        this.individuals = individuals;
    }

    public boolean isAbstractClass() {
        return abstractClass;
    }

    public void setAbstractClass(boolean abstractClass) {
        this.abstractClass = abstractClass;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Date getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(Date lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public String getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(String creatorId) {
        this.creatorId = creatorId;
    }
    
    public String getLanguageTag() {
        return languageTag;
    }

    public void setLanguageTag(String languageTag) {
        this.languageTag = languageTag;
    }
    
    public List<Annotation> getAnnotations() {
        return annotations;
    }

    public void setAnnotations(List<Annotation> annotations) {
        this.annotations = annotations;
    }

    public List<Relationship> getRelationships() {
        return relationships;
    }

    public void setRelationships(List<Relationship> relationships) {
        this.relationships = relationships;
    }
}
