package com.by.open.ontology.ontologyservice.individualmodule.controller;

import com.by.open.ontology.ontologyservice.individualmodule.entity.Individual;
import com.by.open.ontology.ontologyservice.individualmodule.service.IndividualService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/individual")
public class IndividualController {

    @Autowired
    private IndividualService individualService;

    @PostMapping("/create")
    public ResponseEntity<Individual> create(@RequestBody Individual individual) {
        Individual createdIndividual = individualService.create(individual);
        return ResponseEntity.ok(createdIndividual);
    }

    @GetMapping("/findById/{id}")
    public ResponseEntity<Individual> findById(@PathVariable String id) {
        Individual individual = individualService.findById(id);
        if (individual != null) {
            return ResponseEntity.ok(individual);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/findByName/{name}")
    public ResponseEntity<Individual> findByName(@PathVariable String name) {
        Individual individual = individualService.findByName(name);
        if (individual != null) {
            return ResponseEntity.ok(individual);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/findByIri/{iri}")
    public ResponseEntity<Individual> findByIri(@PathVariable String iri) {
        Individual individual = individualService.findByIri(iri);
        if (individual != null) {
            return ResponseEntity.ok(individual);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/findByOntologyId/{ontologyId}")
    public ResponseEntity<List<Individual>> findByOntologyId(@PathVariable String ontologyId) {
        List<Individual> individuals = individualService.findByOntologyId(ontologyId);
        return ResponseEntity.ok(individuals);
    }

    @GetMapping("/findByClassId/{classId}")
    public ResponseEntity<List<Individual>> findByClassId(@PathVariable String classId) {
        List<Individual> individuals = individualService.findByClassIds(classId);
        return ResponseEntity.ok(individuals);
    }

    @GetMapping("/findAll")
    public ResponseEntity<List<Individual>> findAll() {
        List<Individual> individuals = individualService.findAll();
        return ResponseEntity.ok(individuals);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Individual> update(@PathVariable String id, @RequestBody Individual individual) {
        Individual updatedIndividual = individualService.update(id, individual);
        if (updatedIndividual != null) {
            return ResponseEntity.ok(updatedIndividual);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        individualService.delete(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/addClass/{individualId}/{classId}")
    public ResponseEntity<Void> addClass(@PathVariable String individualId, @PathVariable String classId) {
        individualService.addClass(individualId, classId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/addPropertyValue/{individualId}/{propertyValue}")
    public ResponseEntity<Void> addPropertyValue(@PathVariable String individualId, @PathVariable String propertyValue) {
        individualService.addPropertyValue(individualId, propertyValue);
        return ResponseEntity.ok().build();
    }
}
