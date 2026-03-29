package com.by.open.ontology.ontologyservice.classmodule.controller;

import com.by.open.ontology.ontologyservice.classmodule.entity.Class;
import com.by.open.ontology.ontologyservice.classmodule.service.ClassService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/class")
public class ClassController {

    @Autowired
    private ClassService classService;

    @PostMapping("/create")
    public ResponseEntity<Class> create(@RequestBody Map<String, Object> request) {
        Class classEntity = new Class();
        
        // 设置基本属性
        classEntity.setName((String) request.get("name"));
        classEntity.setIri((String) request.get("iri"));
        classEntity.setOntologyId((String) request.get("ontologyId"));
        classEntity.setLanguageTag((String) request.get("languageTag"));
        
        // 处理父类关系：前端传递的parentId就是父节点
        String parentId = (String) request.get("parentId");
        List<String> superClasses = new ArrayList<>();
        if (parentId != null && !parentId.isEmpty()) {
            superClasses.add(parentId);
        }
        classEntity.setSuperClasses(superClasses);
        
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

    @PostMapping("/removeSuperClass")
    public ResponseEntity<Void> removeSuperClass(@RequestBody Map<String, String> request) {
        String classId = request.get("classId");
        String superClassId = request.get("superClassId");
        classService.removeSuperClass(classId, superClassId);
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

    @PostMapping("/move")
    public ResponseEntity<Void> moveClass(@RequestBody MoveClassRequest request) {
        classService.moveClass(request.getClassId(), request.getNewParentId());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/merge")
    public ResponseEntity<Void> mergeClasses(@RequestBody MergeClassesRequest request) {
        classService.mergeClasses(request.getSourceId(), request.getTargetId());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<Class>> searchClasses(@RequestParam String query, @RequestParam String projectId) {
        List<Class> classes = classService.searchClasses(query, projectId);
        return ResponseEntity.ok(classes);
    }

    @PostMapping("/importRDFS")
    public ResponseEntity<Map<String, Object>> importRDFS(@RequestBody Map<String, Object> request) {
        String script = (String) request.get("script");
        String ontologyId = (String) request.get("ontologyId");
        String projectId = (String) request.get("projectId");
        
        Map<String, Object> result = classService.importRDFS(script, ontologyId, projectId);
        return ResponseEntity.ok(result);
    }
}
