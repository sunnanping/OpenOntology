package com.by.open.ontology.ontologyservice.instancemodule.controller;

import com.by.open.ontology.ontologyservice.instancemodule.entity.Instance;
import com.by.open.ontology.ontologyservice.instancemodule.service.InstanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/instance")
public class InstanceController {

    @Autowired
    private InstanceService instanceService;

    @PostMapping("/create")
    public ResponseEntity<Instance> create(@RequestBody Instance instance) {
        Instance createdInstance = instanceService.create(instance);
        return ResponseEntity.ok(createdInstance);
    }

    @GetMapping("/findById/{id}")
    public ResponseEntity<Instance> findById(@PathVariable String id) {
        Instance instance = instanceService.findById(id);
        if (instance != null) {
            return ResponseEntity.ok(instance);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/findByName/{name}")
    public ResponseEntity<Instance> findByName(@PathVariable String name) {
        Instance instance = instanceService.findByName(name);
        if (instance != null) {
            return ResponseEntity.ok(instance);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/findByIri/{iri}")
    public ResponseEntity<Instance> findByIri(@PathVariable String iri) {
        Instance instance = instanceService.findByIri(iri);
        if (instance != null) {
            return ResponseEntity.ok(instance);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/findByOntologyId/{ontologyId}")
    public ResponseEntity<List<Instance>> findByOntologyId(@PathVariable String ontologyId) {
        List<Instance> instances = instanceService.findByOntologyId(ontologyId);
        return ResponseEntity.ok(instances);
    }

    @GetMapping("/findByClassId/{classId}")
    public ResponseEntity<List<Instance>> findByClassId(@PathVariable String classId) {
        List<Instance> instances = instanceService.findByClassId(classId);
        return ResponseEntity.ok(instances);
    }

    @GetMapping("/findAll")
    public ResponseEntity<List<Instance>> findAll() {
        List<Instance> instances = instanceService.findAll();
        return ResponseEntity.ok(instances);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Instance> update(@PathVariable String id, @RequestBody Instance instance) {
        Instance updatedInstance = instanceService.update(id, instance);
        if (updatedInstance != null) {
            return ResponseEntity.ok(updatedInstance);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        instanceService.delete(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/addPropertyValue/{instanceId}")
    public ResponseEntity<Void> addPropertyValue(@PathVariable String instanceId, @RequestBody Map<String, Object> propertyData) {
        String propertyName = (String) propertyData.get("propertyName");
        Object propertyValue = propertyData.get("propertyValue");
        instanceService.addPropertyValue(instanceId, propertyName, propertyValue);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/removePropertyValue/{instanceId}/{propertyName}")
    public ResponseEntity<Void> removePropertyValue(@PathVariable String instanceId, @PathVariable String propertyName) {
        instanceService.removePropertyValue(instanceId, propertyName);
        return ResponseEntity.ok().build();
    }

}
