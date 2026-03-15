package com.by.open.ontology.instanceservice.repository;

import com.by.open.ontology.instanceservice.entity.Instance;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InstanceRepository extends MongoRepository<Instance, String> {
    List<Instance> findByOntologyId(String ontologyId);
    List<Instance> findByClassId(String classId);
    Instance findByName(String name);
    Instance findByIri(String iri);
}