package com.by.open.ontology.ontologyservice.classmodule.controller;

import com.by.open.ontology.ontologyservice.classmodule.entity.Annotation;
import com.by.open.ontology.ontologyservice.classmodule.service.AnnotationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/annotation")
public class AnnotationController {

    @Autowired
    private AnnotationService annotationService;

    @PostMapping("/create")
    public ResponseEntity<Annotation> create(@RequestBody Annotation annotation) {
        Annotation createdAnnotation = annotationService.create(annotation);
        return ResponseEntity.ok(createdAnnotation);
    }

    @GetMapping("/findByEntityIdAndEntityType/{entityId}/{entityType}")
    public ResponseEntity<List<Annotation>> findByEntityIdAndEntityType(
            @PathVariable String entityId, 
            @PathVariable String entityType) {
        List<Annotation> annotations = annotationService.findByEntityIdAndEntityType(entityId, entityType);
        return ResponseEntity.ok(annotations);
    }

    @GetMapping("/findByEntityId/{entityId}")
    public ResponseEntity<List<Annotation>> findByEntityId(@PathVariable String entityId) {
        List<Annotation> annotations = annotationService.findByEntityId(entityId);
        return ResponseEntity.ok(annotations);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Annotation> update(@PathVariable String id, @RequestBody Annotation annotation) {
        Annotation updatedAnnotation = annotationService.update(id, annotation);
        if (updatedAnnotation != null) {
            return ResponseEntity.ok(updatedAnnotation);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        annotationService.delete(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/set")
    public ResponseEntity<Annotation> setAnnotation(@RequestBody Annotation annotation) {
        Annotation setAnnotation = annotationService.setAnnotation(annotation);
        return ResponseEntity.ok(setAnnotation);
    }

    @PostMapping("/batch")
    public ResponseEntity<List<Annotation>> batchSaveAnnotations(@RequestBody BatchAnnotationsRequest request) {
        List<Annotation> savedAnnotations = annotationService.batchSaveAnnotations(
            request.getEntityId(), 
            request.getEntityType(), 
            request.getAnnotations()
        );
        return ResponseEntity.ok(savedAnnotations);
    }
}
