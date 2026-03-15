package com.by.open.ontology.versionservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class VersionServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(VersionServiceApplication.class, args);
    }
}