package com.by.open.ontology.ontologyservice.dto;

import java.util.List;

public class ClassHierarchyNode {
    private String id;
    private String name;
    private String iri;
    private String description;
    private List<ClassHierarchyNode> children;
    private boolean expanded;
    private String parentId;

    public ClassHierarchyNode() {
    }

    public ClassHierarchyNode(String id, String name, String iri, String description) {
        this.id = id;
        this.name = name;
        this.iri = iri;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<ClassHierarchyNode> getChildren() {
        return children;
    }

    public void setChildren(List<ClassHierarchyNode> children) {
        this.children = children;
    }

    public boolean isExpanded() {
        return expanded;
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }
}
