package com.by.open.ontology.searchservice.repository;

import com.by.open.ontology.searchservice.entity.SearchIndex;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SearchIndexRepository extends MongoRepository<SearchIndex, String> {
    List<SearchIndex> findByOntologyId(String ontologyId);
    List<SearchIndex> findByEntityType(String entityType);
    Optional<SearchIndex> findByEntityId(String entityId);
    List<SearchIndex> findByNameContainingIgnoreCase(String name);
    List<SearchIndex> findByIriContainingIgnoreCase(String iri);
    List<SearchIndex> findByContentContainingIgnoreCase(String content);
    void deleteByEntityId(String entityId);
    void deleteByOntologyId(String ontologyId);
}