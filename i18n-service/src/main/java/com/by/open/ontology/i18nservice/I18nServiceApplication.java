package com.by.open.ontology.i18nservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
@EnableCaching
public class I18nServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(I18nServiceApplication.class, args);
    }
}
