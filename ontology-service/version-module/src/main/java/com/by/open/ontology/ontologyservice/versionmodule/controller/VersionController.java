package com.by.open.ontology.ontologyservice.versionmodule.controller;

import com.by.open.ontology.ontologyservice.versionmodule.entity.OntologyVersion;
import com.by.open.ontology.ontologyservice.versionmodule.service.VersionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/version")
@CrossOrigin(origins = "*")
public class VersionController {

    @Autowired
    private VersionService versionService;

    @GetMapping("/findAll")
    public List<OntologyVersion> findAll() {
        return versionService.findAll();
    }

    @GetMapping("/findById/{id}")
    public ResponseEntity<OntologyVersion> findById(@PathVariable String id) {
        Optional<OntologyVersion> version = versionService.findById(id);
        return version.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/findByOntologyId/{ontologyId}")
    public List<OntologyVersion> findByOntologyId(@PathVariable String ontologyId) {
        return versionService.findByOntologyId(ontologyId);
    }

    @GetMapping("/latest/{ontologyId}")
    public ResponseEntity<OntologyVersion> getLatestVersion(@PathVariable String ontologyId) {
        Optional<OntologyVersion> version = versionService.getLatestVersion(ontologyId);
        return version.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/create")
    public OntologyVersion createVersion(@RequestBody OntologyVersion version) {
        return versionService.createVersion(version);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<OntologyVersion> updateVersion(@PathVariable String id, @RequestBody OntologyVersion version) {
        OntologyVersion updatedVersion = versionService.updateVersion(id, version);
        if (updatedVersion != null) {
            return ResponseEntity.ok(updatedVersion);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteVersion(@PathVariable String id) {
        versionService.deleteVersion(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/rollback/{ontologyId}/{versionId}")
    public ResponseEntity<OntologyVersion> rollback(@PathVariable String ontologyId, @PathVariable String versionId) {
        OntologyVersion newVersion = versionService.rollback(ontologyId, versionId);
        if (newVersion != null) {
            return ResponseEntity.ok(newVersion);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/history/{ontologyId}")
    public List<OntologyVersion> getVersionHistory(@PathVariable String ontologyId) {
        return versionService.getVersionHistory(ontologyId);
    }
}
