package com.by.open.ontology.adminservice.repository;

import com.by.open.ontology.adminservice.entity.SystemSettings;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SystemSettingsRepository extends MongoRepository<SystemSettings, String> {
    SystemSettings findFirstByOrderByCreatedDateDesc();
}