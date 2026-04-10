package com.by.open.ontology.ontologyservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication(scanBasePackages = {
    "com.by.open.ontology.ontologyservice",
    "com.by.open.ontology.ontologyservice.core",
    "com.by.open.ontology.ontologyservice.projectmodule",
    "com.by.open.ontology.ontologyservice.classmodule",
    "com.by.open.ontology.ontologyservice.propertymodule",
    "com.by.open.ontology.ontologyservice.individualmodule",
    "com.by.open.ontology.ontologyservice.instancemodule",
    "com.by.open.ontology.ontologyservice.versionmodule",
    "com.by.open.ontology.ontologyservice.reasoningmodule",
    "com.by.open.ontology.ontologyservice.searchmodule",
    "com.by.open.ontology.ontologyservice.metadatamodule",
    "com.by.open.ontology.ontologyservice.datatypemodule",
    "com.by.open.ontology.ontologyservice.commentmodule",
    "com.by.open.ontology.ontologyservice.collaborationmodule",
    "com.by.open.ontology.ontologyservice.activitymodule"
})
@EnableEurekaClient
public class OntologyServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(OntologyServiceApplication.class, args);
    }
}
