package com.by.open.ontology.ontologyservice.service;

import com.by.open.ontology.ontologyservice.entity.Ontology;
import com.by.open.ontology.ontologyservice.repository.OntologyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class OntologyService {
    @Autowired
    private OntologyRepository ontologyRepository;

    public Ontology create(Ontology ontology) {
        if (ontologyRepository.findByName(ontology.getName()) != null) {
            throw new RuntimeException("Ontology name already exists");
        }

        if (ontologyRepository.findByNamespace(ontology.getNamespace()) != null) {
            throw new RuntimeException("Namespace already exists");
        }

        Date now = new Date();
        ontology.setCreatedDate(now);
        ontology.setLastModifiedDate(now);

        if (ontology.getStatus() == null) {
            ontology.setStatus("ACTIVE");
        }

        return ontologyRepository.save(ontology);
    }

    public Ontology findById(String id) {
        return ontologyRepository.findById(id).orElseThrow(() -> new RuntimeException("Ontology not found"));
    }

    public Ontology findByName(String name) {
        return ontologyRepository.findByName(name);
    }

    public Ontology findByNamespace(String namespace) {
        return ontologyRepository.findByNamespace(namespace);
    }

    public List<Ontology> findAll() {
        return ontologyRepository.findAll();
    }

    public Ontology update(Ontology ontology) {
        Ontology existingOntology = ontologyRepository.findById(ontology.getId()).orElseThrow(() -> new RuntimeException("Ontology not found"));

        if (ontology.getName() != null) {
            existingOntology.setName(ontology.getName());
        }
        if (ontology.getDescription() != null) {
            existingOntology.setDescription(ontology.getDescription());
        }
        if (ontology.getNamespace() != null) {
            existingOntology.setNamespace(ontology.getNamespace());
        }
        if (ontology.getContributors() != null) {
            existingOntology.setContributors(ontology.getContributors());
        }
        if (ontology.getStatus() != null) {
            existingOntology.setStatus(ontology.getStatus());
        }
        if (ontology.getFormat() != null) {
            existingOntology.setFormat(ontology.getFormat());
        }
        if (ontology.getContent() != null) {
            existingOntology.setContent(ontology.getContent());
        }
        existingOntology.setLastModifiedDate(new Date());

        return ontologyRepository.save(existingOntology);
    }

    public void delete(String id) {
        ontologyRepository.deleteById(id);
    }
}