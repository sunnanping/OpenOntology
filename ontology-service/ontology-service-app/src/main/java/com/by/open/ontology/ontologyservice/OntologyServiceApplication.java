package com.by.open.ontology.ontologyservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication(scanBasePackages = {
    "com.by.open.ontology.ontologyservice.core",
    "com.by.open.ontology.ontologyservice.projectmodule",
    "com.by.open.ontology.ontologyservice.classmodule",
    "com.by.open.ontology.ontologyservice.propertymodule",
    "com.by.open.ontology.ontologyservice.individualmodule"
})
@EnableEurekaClient
public class OntologyServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(OntologyServiceApplication.class, args);
    }
}
