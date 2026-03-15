package com.by.open.ontology.ontologyservice.i18n;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LanguageMapRepository extends MongoRepository<LanguageMap, String> {
    List<LanguageMap> findByEntityRef(String entityRef);
    List<LanguageMap> findByEntityType(String entityType);
    Optional<LanguageMap> findByEntityRefAndEntityType(String entityRef, String entityType);
    void deleteByEntityRef(String entityRef);
}
