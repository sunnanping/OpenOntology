package com.by.open.ontology.ontologyservice.propertymodule.service;

import com.by.open.ontology.ontologyservice.propertymodule.entity.Property;
import com.by.open.ontology.ontologyservice.propertymodule.repository.PropertyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class PropertyService {

    @Autowired
    private PropertyRepository propertyRepository;

    public Property create(Property property) {
        property.setCreatedDate(new Date());
        property.setLastModifiedDate(new Date());
        return propertyRepository.save(property);
    }

    public Property findById(String id) {
        return propertyRepository.findById(id).orElse(null);
    }

    public Property findByName(String name) {
        return propertyRepository.findByName(name);
    }

    public Property findByIri(String iri) {
        return propertyRepository.findByIri(iri);
    }

    public List<Property> findByOntologyId(String ontologyId) {
        return propertyRepository.findByOntologyId(ontologyId);
    }

    public List<Property> findByPropertyType(String propertyType) {
        return propertyRepository.findByPropertyType(propertyType);
    }

    public List<Property> findAll() {
        return propertyRepository.findAll();
    }

    public Property update(String id, Property property) {
        Property existingProperty = propertyRepository.findById(id).orElse(null);
        if (existingProperty != null) {
            existingProperty.setName(property.getName());
            existingProperty.setIri(property.getIri());
            existingProperty.setOntologyId(property.getOntologyId());
            existingProperty.setDescription(property.getDescription());
            existingProperty.setPropertyType(property.getPropertyType());
            existingProperty.setDomains(property.getDomains());
            existingProperty.setRanges(property.getRanges());
            existingProperty.setFunctional(property.isFunctional());
            existingProperty.setInverseFunctional(property.isInverseFunctional());
            existingProperty.setTransitive(property.isTransitive());
            existingProperty.setSymmetric(property.isSymmetric());
            existingProperty.setInverseProperty(property.getInverseProperty());
            existingProperty.setSuperProperty(property.getSuperProperty());
            existingProperty.setSubProperties(property.getSubProperties());
            existingProperty.setLastModifiedDate(new Date());
            return propertyRepository.save(existingProperty);
        }
        return null;
    }

    public void delete(String id) {
        propertyRepository.deleteById(id);
    }

    public void addDomain(String propertyId, String domainId) {
        Property property = propertyRepository.findById(propertyId).orElse(null);
        if (property != null) {
            property.getDomains().add(domainId);
            propertyRepository.save(property);
        }
    }

    public void addRange(String propertyId, String rangeId) {
        Property property = propertyRepository.findById(propertyId).orElse(null);
        if (property != null) {
            property.getRanges().add(rangeId);
            propertyRepository.save(property);
        }
    }

    public void addSubProperty(String propertyId, String subPropertyId) {
        Property property = propertyRepository.findById(propertyId).orElse(null);
        if (property != null) {
            property.getSubProperties().add(subPropertyId);
            propertyRepository.save(property);
        }
    }
}
