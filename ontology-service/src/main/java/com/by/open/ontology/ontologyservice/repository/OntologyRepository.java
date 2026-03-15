package com.by.open.ontology.ontologyservice.repository;

import com.by.open.ontology.ontologyservice.entity.Ontology;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OntologyRepository extends MongoRepository<Ontology, String> {
    Ontology findByName(String name);
    Ontology findByNamespace(String namespace);
}
