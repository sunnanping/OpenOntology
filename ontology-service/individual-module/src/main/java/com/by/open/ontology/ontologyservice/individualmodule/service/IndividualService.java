package com.by.open.ontology.ontologyservice.individualmodule.service;

import com.by.open.ontology.ontologyservice.individualmodule.entity.Individual;
import com.by.open.ontology.ontologyservice.individualmodule.repository.IndividualRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class IndividualService {

    @Autowired
    private IndividualRepository individualRepository;

    public Individual create(Individual individual) {
        individual.setCreatedDate(new Date());
        individual.setLastModifiedDate(new Date());
        return individualRepository.save(individual);
    }

    public Individual findById(String id) {
        return individualRepository.findById(id).orElse(null);
    }

    public Individual findByName(String name) {
        return individualRepository.findByName(name);
    }

    public Individual findByIri(String iri) {
        return individualRepository.findByIri(iri);
    }

    public List<Individual> findByOntologyId(String ontologyId) {
        return individualRepository.findByOntologyId(ontologyId);
    }

    public List<Individual> findByClassIds(String classId) {
        return individualRepository.findByClassIds(classId);
    }

    public List<Individual> findAll() {
        return individualRepository.findAll();
    }

    public Individual update(String id, Individual individual) {
        Individual existingIndividual = individualRepository.findById(id).orElse(null);
        if (existingIndividual != null) {
            existingIndividual.setName(individual.getName());
            existingIndividual.setIri(individual.getIri());
            existingIndividual.setOntologyId(individual.getOntologyId());
            existingIndividual.setDescription(individual.getDescription());
            existingIndividual.setClassIds(individual.getClassIds());
            existingIndividual.setPropertyValues(individual.getPropertyValues());
            existingIndividual.setLastModifiedDate(new Date());
            return individualRepository.save(existingIndividual);
        }
        return null;
    }

    public void delete(String id) {
        individualRepository.deleteById(id);
    }

    public void addClass(String individualId, String classId) {
        Individual individual = individualRepository.findById(individualId).orElse(null);
        if (individual != null) {
            individual.getClassIds().add(classId);
            individualRepository.save(individual);
        }
    }

    public void addPropertyValue(String individualId, String propertyValue) {
        Individual individual = individualRepository.findById(individualId).orElse(null);
        if (individual != null) {
            individual.getPropertyValues().add(propertyValue);
            individualRepository.save(individual);
        }
    }
}
