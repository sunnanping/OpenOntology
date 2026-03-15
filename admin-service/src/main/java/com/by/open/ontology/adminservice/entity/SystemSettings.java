package com.by.open.ontology.adminservice.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection = "system_settings")
public class SystemSettings {
    @Id
    private String id;
    private String scheme;
    private String host;
    private String path;
    private String port;
    private boolean accountCreationEnabled;
    private boolean projectCreationEnabled;
    private boolean projectUploadEnabled;
    private int maxUploadSize;
    private Date createdDate;
    private Date lastModifiedDate;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getScheme() {
        return scheme;
    }

    public void setScheme(String scheme) {
        this.scheme = scheme;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public boolean isAccountCreationEnabled() {
        return accountCreationEnabled;
    }

    public void setAccountCreationEnabled(boolean accountCreationEnabled) {
        this.accountCreationEnabled = accountCreationEnabled;
    }

    public boolean isProjectCreationEnabled() {
        return projectCreationEnabled;
    }

    public void setProjectCreationEnabled(boolean projectCreationEnabled) {
        this.projectCreationEnabled = projectCreationEnabled;
    }

    public boolean isProjectUploadEnabled() {
        return projectUploadEnabled;
    }

    public void setProjectUploadEnabled(boolean projectUploadEnabled) {
        this.projectUploadEnabled = projectUploadEnabled;
    }

    public int getMaxUploadSize() {
        return maxUploadSize;
    }

    public void setMaxUploadSize(int maxUploadSize) {
        this.maxUploadSize = maxUploadSize;
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
}