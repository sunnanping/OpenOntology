package com.by.open.ontology.ontologyservice.datatypemodule.service;

import com.by.open.ontology.ontologyservice.datatypemodule.entity.Datatype;
import com.by.open.ontology.ontologyservice.datatypemodule.repository.DatatypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class DatatypeService {
    
    @Autowired
    private DatatypeRepository datatypeRepository;
    
    public Datatype create(Datatype datatype) {
        if (datatype.getId() == null || datatype.getId().isEmpty()) {
            datatype.setId(UUID.randomUUID().toString());
        }
        return datatypeRepository.save(datatype);
    }
    
    public Datatype findById(String id) {
        return datatypeRepository.findById(id).orElse(null);
    }
    
    public Datatype findByName(String name) {
        return datatypeRepository.findByName(name);
    }
    
    public Datatype findByIri(String iri) {
        return datatypeRepository.findByIri(iri);
    }
    
    public List<Datatype> findByOntologyId(String ontologyId) {
        return datatypeRepository.findByOntologyId(ontologyId);
    }
    
    public List<Datatype> findByProjectId(String projectId) {
        return datatypeRepository.findByProjectId(projectId);
    }
    
    public List<Datatype> findAll() {
        return datatypeRepository.findAll();
    }
    
    public List<Datatype> findStandardDatatypes() {
        return datatypeRepository.findByIsStandard(true);
    }
    
    public List<Datatype> findCustomDatatypes(String ontologyId) {
        return datatypeRepository.findByOntologyIdAndIsStandard(ontologyId, false);
    }
    
    public Datatype update(String id, Datatype datatype) {
        if (datatypeRepository.existsById(id)) {
            datatype.setId(id);
            return datatypeRepository.save(datatype);
        }
        return null;
    }
    
    public void delete(String id) {
        datatypeRepository.deleteById(id);
    }
    
    public void deleteByOntologyId(String ontologyId) {
        List<Datatype> datatypes = datatypeRepository.findByOntologyId(ontologyId);
        datatypeRepository.deleteAll(datatypes);
    }
}