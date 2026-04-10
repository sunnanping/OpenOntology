package com.by.open.ontology.ontologyservice.datatypemodule.repository;

import com.by.open.ontology.ontologyservice.datatypemodule.entity.Datatype;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DatatypeRepository extends MongoRepository<Datatype, String> {
    
    List<Datatype> findByOntologyId(String ontologyId);
    
    List<Datatype> findByProjectId(String projectId);
    
    Datatype findByName(String name);
    
    Datatype findByIri(String iri);
    
    List<Datatype> findByIsStandard(boolean isStandard);
    
    List<Datatype> findByOntologyIdAndIsStandard(String ontologyId, boolean isStandard);
}