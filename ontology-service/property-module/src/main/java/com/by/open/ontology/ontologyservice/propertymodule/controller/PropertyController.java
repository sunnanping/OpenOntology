package com.by.open.ontology.ontologyservice.propertymodule.controller;

import com.by.open.ontology.ontologyservice.propertymodule.entity.Property;
import com.by.open.ontology.ontologyservice.propertymodule.service.PropertyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/property")
public class PropertyController {

    @Autowired
    private PropertyService propertyService;

    @PostMapping("/create")
    public ResponseEntity<Property> create(@RequestBody Property property) {
        Property createdProperty = propertyService.create(property);
        return ResponseEntity.ok(createdProperty);
    }

    @GetMapping("/findById/{id}")
    public ResponseEntity<Property> findById(@PathVariable String id) {
        Property property = propertyService.findById(id);
        if (property != null) {
            return ResponseEntity.ok(property);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/findByName/{name}")
    public ResponseEntity<Property> findByName(@PathVariable String name) {
        Property property = propertyService.findByName(name);
        if (property != null) {
            return ResponseEntity.ok(property);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/findByIri/{iri}")
    public ResponseEntity<Property> findByIri(@PathVariable String iri) {
        Property property = propertyService.findByIri(iri);
        if (property != null) {
            return ResponseEntity.ok(property);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/findByOntologyId/{ontologyId}")
    public ResponseEntity<List<Property>> findByOntologyId(@PathVariable String ontologyId) {
        List<Property> properties = propertyService.findByOntologyId(ontologyId);
        return ResponseEntity.ok(properties);
    }

    @GetMapping("/findByPropertyType/{propertyType}")
    public ResponseEntity<List<Property>> findByPropertyType(@PathVariable String propertyType) {
        List<Property> properties = propertyService.findByPropertyType(propertyType);
        return ResponseEntity.ok(properties);
    }

    @GetMapping("/findAll")
    public ResponseEntity<List<Property>> findAll() {
        List<Property> properties = propertyService.findAll();
        return ResponseEntity.ok(properties);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Property> update(@PathVariable String id, @RequestBody Property property) {
        Property updatedProperty = propertyService.update(id, property);
        if (updatedProperty != null) {
            return ResponseEntity.ok(updatedProperty);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        propertyService.delete(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/addDomain/{propertyId}/{domainId}")
    public ResponseEntity<Void> addDomain(@PathVariable String propertyId, @PathVariable String domainId) {
        propertyService.addDomain(propertyId, domainId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/addRange/{propertyId}/{rangeId}")
    public ResponseEntity<Void> addRange(@PathVariable String propertyId, @PathVariable String rangeId) {
        propertyService.addRange(propertyId, rangeId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/addSubProperty/{propertyId}/{subPropertyId}")
    public ResponseEntity<Void> addSubProperty(@PathVariable String propertyId, @PathVariable String subPropertyId) {
        propertyService.addSubProperty(propertyId, subPropertyId);
        return ResponseEntity.ok().build();
    }
}
