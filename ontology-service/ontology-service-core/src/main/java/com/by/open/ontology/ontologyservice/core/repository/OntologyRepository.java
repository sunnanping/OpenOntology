package com.by.open.ontology.ontologyservice.core.repository;

import com.by.open.ontology.ontologyservice.core.entity.Ontology;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OntologyRepository extends MongoRepository<Ontology, String> {
    Ontology findByName(String name);
    List<Ontology> findByCreatorId(String creatorId);
    List<Ontology> findByStatus(String status);
}
