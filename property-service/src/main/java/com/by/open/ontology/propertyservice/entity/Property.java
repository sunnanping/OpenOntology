package com.by.open.ontology.propertyservice.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

@Document(collection = "properties")
public class Property {

    @Id
    private String id;
    private String name;
    private String iri;
    private String ontologyId;
    private String description;
    private String propertyType;
    private List<String> domains;
    private List<String> ranges;
    private boolean functional;
    private boolean inverseFunctional;
    private boolean transitive;
    private boolean symmetric;
    private String inverseProperty;
    private String superProperty;
    private List<String> subProperties;
    private Date createdDate;
    private Date lastModifiedDate;
    private String creatorId;

    public Property() {
    }

    public Property(String name, String iri, String ontologyId, String propertyType) {
        this.name = name;
        this.iri = iri;
        this.ontologyId = ontologyId;
        this.propertyType = propertyType;
        this.createdDate = new Date();
        this.lastModifiedDate = new Date();
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

    public String getPropertyType() {
        return propertyType;
    }

    public void setPropertyType(String propertyType) {
        this.propertyType = propertyType;
    }

    public List<String> getDomains() {
        return domains;
    }

    public void setDomains(List<String> domains) {
        this.domains = domains;
    }

    public List<String> getRanges() {
        return ranges;
    }

    public void setRanges(List<String> ranges) {
        this.ranges = ranges;
    }

    public boolean isFunctional() {
        return functional;
    }

    public void setFunctional(boolean functional) {
        this.functional = functional;
    }

    public boolean isInverseFunctional() {
        return inverseFunctional;
    }

    public void setInverseFunctional(boolean inverseFunctional) {
        this.inverseFunctional = inverseFunctional;
    }

    public boolean isTransitive() {
        return transitive;
    }

    public void setTransitive(boolean transitive) {
        this.transitive = transitive;
    }

    public boolean isSymmetric() {
        return symmetric;
    }

    public void setSymmetric(boolean symmetric) {
        this.symmetric = symmetric;
    }

    public String getInverseProperty() {
        return inverseProperty;
    }

    public void setInverseProperty(String inverseProperty) {
        this.inverseProperty = inverseProperty;
    }

    public String getSuperProperty() {
        return superProperty;
    }

    public void setSuperProperty(String superProperty) {
        this.superProperty = superProperty;
    }

    public List<String> getSubProperties() {
        return subProperties;
    }

    public void setSubProperties(List<String> subProperties) {
        this.subProperties = subProperties;
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

}