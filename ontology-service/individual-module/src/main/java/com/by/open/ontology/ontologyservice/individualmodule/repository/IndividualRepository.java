package com.by.open.ontology.ontologyservice.individualmodule.repository;

import com.by.open.ontology.ontologyservice.individualmodule.entity.Individual;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IndividualRepository extends MongoRepository<Individual, String> {
    Individual findByName(String name);
    Individual findByIri(String iri);
    List<Individual> findByOntologyId(String ontologyId);
    List<Individual> findByClassIds(String classId);
}
