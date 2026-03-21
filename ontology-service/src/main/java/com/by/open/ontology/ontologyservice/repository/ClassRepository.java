package com.by.open.ontology.ontologyservice.repository;

import com.by.open.ontology.ontologyservice.entity.Class;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClassRepository extends MongoRepository<Class, String> {
    Class findByName(String name);
    Class findByIri(String iri);
    List<Class> findByOntologyId(String ontologyId);
}
