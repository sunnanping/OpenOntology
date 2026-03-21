package com.by.open.ontology.ontologyservice.repository;

import com.by.open.ontology.ontologyservice.entity.Property;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PropertyRepository extends MongoRepository<Property, String> {
    Property findByName(String name);
    Property findByIri(String iri);
    List<Property> findByOntologyId(String ontologyId);
    List<Property> findByPropertyType(String propertyType);
}
