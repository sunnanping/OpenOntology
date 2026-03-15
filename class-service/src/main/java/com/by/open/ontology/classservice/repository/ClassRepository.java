package com.by.open.ontology.classservice.repository;

import com.by.open.ontology.classservice.entity.Class;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClassRepository extends MongoRepository<Class, String> {
    List<Class> findByOntologyId(String ontologyId);
    List<Class> findBySuperClassesContaining(String superClassId);
    List<Class> findBySubClassesContaining(String subClassId);
    Class findByName(String name);
    Class findByIri(String iri);
}