package com.by.open.ontology.ontologyservice.classmodule.controller;

public class MoveClassRequest {
    private String classId;
    private String newParentId;

    public MoveClassRequest() {
    }

    public String getClassId() {
        return classId;
    }

    public void setClassId(String classId) {
        this.classId = classId;
    }

    public String getNewParentId() {
        return newParentId;
    }

    public void setNewParentId(String newParentId) {
        this.newParentId = newParentId;
    }
}
