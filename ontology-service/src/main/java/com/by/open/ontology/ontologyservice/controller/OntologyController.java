package com.by.open.ontology.ontologyservice.controller;

import com.by.open.ontology.ontologyservice.entity.Ontology;
import com.by.open.ontology.ontologyservice.service.OntologyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ontology")
public class OntologyController {
    @Autowired
    private OntologyService ontologyService;

    @PostMapping("/create")
    public Ontology create(@RequestBody Ontology ontology) {
        return ontologyService.create(ontology);
    }

    @GetMapping("/findById/{id}")
    public Ontology findById(@PathVariable String id) {
        return ontologyService.findById(id);
    }

    @GetMapping("/findByName")
    public Ontology findByName(@RequestParam String name) {
        return ontologyService.findByName(name);
    }

    @GetMapping("/findByNamespace")
    public Ontology findByNamespace(@RequestParam String namespace) {
        return ontologyService.findByNamespace(namespace);
    }

    @GetMapping("/findAll")
    public List<Ontology> findAll() {
        return ontologyService.findAll();
    }

    @PutMapping("/update")
    public Ontology update(@RequestBody Ontology ontology) {
        return ontologyService.update(ontology);
    }

    @DeleteMapping("/delete/{id}")
    public void delete(@PathVariable String id) {
        ontologyService.delete(id);
    }
}
