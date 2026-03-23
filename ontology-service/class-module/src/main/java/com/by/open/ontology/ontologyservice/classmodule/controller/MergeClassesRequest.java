package com.by.open.ontology.ontologyservice.classmodule.controller;

public class MergeClassesRequest {
    private String sourceId;
    private String targetId;

    public MergeClassesRequest() {
    }

    public String getSourceId() {
        return sourceId;
    }

    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }

    public String getTargetId() {
        return targetId;
    }

    public void setTargetId(String targetId) {
        this.targetId = targetId;
    }
}
