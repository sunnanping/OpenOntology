package com.by.open.ontology.ontologyservice.controller;

import com.by.open.ontology.ontologyservice.entity.Class;
import com.by.open.ontology.ontologyservice.service.ClassService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/class")
public class ClassController {

    @Autowired
    private ClassService classService;

    @PostMapping("/create")
    public ResponseEntity<Class> create(@RequestBody Class classEntity) {
        Class createdClass = classService.create(classEntity);
        return ResponseEntity.ok(createdClass);
    }

    @GetMapping("/findById/{id}")
    public ResponseEntity<Class> findById(@PathVariable String id) {
        Class classEntity = classService.findById(id);
        if (classEntity != null) {
            return ResponseEntity.ok(classEntity);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/findByName/{name}")
    public ResponseEntity<Class> findByName(@PathVariable String name) {
        Class classEntity = classService.findByName(name);
        if (classEntity != null) {
            return ResponseEntity.ok(classEntity);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/findByIri/{iri}")
    public ResponseEntity<Class> findByIri(@PathVariable String iri) {
        Class classEntity = classService.findByIri(iri);
        if (classEntity != null) {
            return ResponseEntity.ok(classEntity);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/findByOntologyId/{ontologyId}")
    public ResponseEntity<List<Class>> findByOntologyId(@PathVariable String ontologyId) {
        List<Class> classes = classService.findByOntologyId(ontologyId);
        return ResponseEntity.ok(classes);
    }

    @GetMapping("/findAll")
    public ResponseEntity<List<Class>> findAll() {
        List<Class> classes = classService.findAll();
        return ResponseEntity.ok(classes);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Class> update(@PathVariable String id, @RequestBody Class classEntity) {
        Class updatedClass = classService.update(id, classEntity);
        if (updatedClass != null) {
            return ResponseEntity.ok(updatedClass);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        classService.delete(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/addSubClass/{classId}/{subClassId}")
    public ResponseEntity<Void> addSubClass(@PathVariable String classId, @PathVariable String subClassId) {
        classService.addSubClass(classId, subClassId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/addSuperClass/{classId}/{superClassId}")
    public ResponseEntity<Void> addSuperClass(@PathVariable String classId, @PathVariable String superClassId) {
        classService.addSuperClass(classId, superClassId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/addProperty/{classId}/{propertyId}")
    public ResponseEntity<Void> addProperty(@PathVariable String classId, @PathVariable String propertyId) {
        classService.addProperty(classId, propertyId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/addIndividual/{classId}/{individualId}")
    public ResponseEntity<Void> addIndividual(@PathVariable String classId, @PathVariable String individualId) {
        classService.addIndividual(classId, individualId);
        return ResponseEntity.ok().build();
    }
}
