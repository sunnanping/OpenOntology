package com.by.open.ontology.reasoningservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class ReasoningServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(ReasoningServiceApplication.class, args);
    }
}