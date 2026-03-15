package com.by.open.ontology.i18nservice.repository;

import com.by.open.ontology.i18nservice.entity.LanguageMap;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LanguageMapRepository extends MongoRepository<LanguageMap, String> {
    Optional<LanguageMap> findByEntityRefAndEntityType(String entityRef, String entityType);
    void deleteByEntityRef(String entityRef);
}
