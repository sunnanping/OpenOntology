package com.by.open.ontology.ontologyservice.datatypemodule.controller;

import com.by.open.ontology.ontologyservice.datatypemodule.entity.Datatype;
import com.by.open.ontology.ontologyservice.datatypemodule.service.DatatypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/datatype")
public class DatatypeController {
    
    @Autowired
    private DatatypeService datatypeService;
    
    @PostMapping("/create")
    public ResponseEntity<Datatype> create(@RequestBody Map<String, Object> request) {
        Datatype datatype = new Datatype();
        
        datatype.setName((String) request.get("name"));
        datatype.setIri((String) request.get("iri"));
        datatype.setOntologyId((String) request.get("ontologyId"));
        datatype.setProjectId((String) request.get("projectId"));
        datatype.setDescription((String) request.get("description"));
        datatype.setStandard(false);
        
        Datatype createdDatatype = datatypeService.create(datatype);
        return ResponseEntity.ok(createdDatatype);
    }
    
    @GetMapping("/findById/{id}")
    public ResponseEntity<Datatype> findById(@PathVariable String id) {
        Datatype datatype = datatypeService.findById(id);
        if (datatype != null) {
            return ResponseEntity.ok(datatype);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/findByName/{name}")
    public ResponseEntity<Datatype> findByName(@PathVariable String name) {
        Datatype datatype = datatypeService.findByName(name);
        if (datatype != null) {
            return ResponseEntity.ok(datatype);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/findByIri/{iri}")
    public ResponseEntity<Datatype> findByIri(@PathVariable String iri) {
        Datatype datatype = datatypeService.findByIri(iri);
        if (datatype != null) {
            return ResponseEntity.ok(datatype);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/findByOntologyId/{ontologyId}")
    public ResponseEntity<List<Datatype>> findByOntologyId(@PathVariable String ontologyId) {
        List<Datatype> datatypes = datatypeService.findByOntologyId(ontologyId);
        return ResponseEntity.ok(datatypes);
    }
    
    @GetMapping("/findByProjectId/{projectId}")
    public ResponseEntity<List<Datatype>> findByProjectId(@PathVariable String projectId) {
        List<Datatype> datatypes = datatypeService.findByProjectId(projectId);
        return ResponseEntity.ok(datatypes);
    }
    
    @GetMapping("/findAll")
    public ResponseEntity<List<Datatype>> findAll() {
        List<Datatype> datatypes = datatypeService.findAll();
        return ResponseEntity.ok(datatypes);
    }
    
    @GetMapping("/findCustom/{ontologyId}")
    public ResponseEntity<List<Datatype>> findCustomDatatypes(@PathVariable String ontologyId) {
        List<Datatype> datatypes = datatypeService.findCustomDatatypes(ontologyId);
        return ResponseEntity.ok(datatypes);
    }
    
    @PutMapping("/update/{id}")
    public ResponseEntity<Datatype> update(@PathVariable String id, @RequestBody Datatype datatype) {
        Datatype updatedDatatype = datatypeService.update(id, datatype);
        if (updatedDatatype != null) {
            return ResponseEntity.ok(updatedDatatype);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        datatypeService.delete(id);
        return ResponseEntity.ok().build();
    }
    
    @DeleteMapping("/deleteByOntologyId/{ontologyId}")
    public ResponseEntity<Void> deleteByOntologyId(@PathVariable String ontologyId) {
        datatypeService.deleteByOntologyId(ontologyId);
        return ResponseEntity.ok().build();
    }
}