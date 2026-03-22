package com.by.open.ontology.ontologyservice.core.controller;

import com.by.open.ontology.ontologyservice.core.entity.Ontology;
import com.by.open.ontology.ontologyservice.core.repository.OntologyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/ontology")
@CrossOrigin(origins = "*")
public class OntologyController {

    @Autowired
    private OntologyRepository ontologyRepository;

    @GetMapping("/findAll")
    public List<Ontology> findAll() {
        return ontologyRepository.findAll();
    }

    @GetMapping("/findById/{id}")
    public ResponseEntity<Ontology> findById(@PathVariable String id) {
        Optional<Ontology> ontology = ontologyRepository.findById(id);
        return ontology.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/findByCreatorId/{creatorId}")
    public List<Ontology> findByCreatorId(@PathVariable String creatorId) {
        return ontologyRepository.findByCreatorId(creatorId);
    }

    @PostMapping("/create")
    public Ontology create(@RequestBody Ontology ontology) {
        ontology.setCreatedDate(new Date());
        ontology.setLastModifiedDate(new Date());
        ontology.setStatus("ACTIVE");
        if (ontology.getFormat() == null) {
            ontology.setFormat("OWL");
        }
        return ontologyRepository.save(ontology);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Ontology> update(@PathVariable String id, @RequestBody Ontology ontology) {
        Optional<Ontology> existingOntology = ontologyRepository.findById(id);
        if (existingOntology.isPresent()) {
            Ontology updated = existingOntology.get();
            updated.setName(ontology.getName());
            updated.setDescription(ontology.getDescription());
            updated.setNamespace(ontology.getNamespace());
            updated.setLastModifiedDate(new Date());
            if (ontology.getContributors() != null) {
                updated.setContributors(ontology.getContributors());
            }
            return ResponseEntity.ok(ontologyRepository.save(updated));
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        ontologyRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/update-last-opened/{id}")
    public ResponseEntity<Ontology> updateLastOpened(@PathVariable String id) {
        Optional<Ontology> existingOntology = ontologyRepository.findById(id);
        if (existingOntology.isPresent()) {
            Ontology updated = existingOntology.get();
            updated.setLastOpened(new Date());
            return ResponseEntity.ok(ontologyRepository.save(updated));
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/move-to-trash/{id}")
    public ResponseEntity<Ontology> moveToTrash(@PathVariable String id) {
        Optional<Ontology> existingOntology = ontologyRepository.findById(id);
        if (existingOntology.isPresent()) {
            Ontology updated = existingOntology.get();
            updated.setStatus("TRASH");
            updated.setLastModifiedDate(new Date());
            return ResponseEntity.ok(ontologyRepository.save(updated));
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/restore/{id}")
    public ResponseEntity<Ontology> restore(@PathVariable String id) {
        Optional<Ontology> existingOntology = ontologyRepository.findById(id);
        if (existingOntology.isPresent()) {
            Ontology updated = existingOntology.get();
            updated.setStatus("ACTIVE");
            updated.setLastModifiedDate(new Date());
            return ResponseEntity.ok(ontologyRepository.save(updated));
        }
        return ResponseEntity.notFound().build();
    }
}
