package com.by.open.ontology.propertyservice.repository;

import com.by.open.ontology.propertyservice.entity.Property;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PropertyRepository extends MongoRepository<Property, String> {
    List<Property> findByOntologyId(String ontologyId);
    List<Property> findByPropertyType(String propertyType);
    List<Property> findByDomainsContaining(String domainId);
    List<Property> findByRangesContaining(String rangeId);
    Property findByName(String name);
    Property findByIri(String iri);
}