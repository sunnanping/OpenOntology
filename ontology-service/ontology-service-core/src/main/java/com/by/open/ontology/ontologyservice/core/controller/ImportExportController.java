package com.by.open.ontology.ontologyservice.core.controller;

import com.by.open.ontology.ontologyservice.core.entity.Ontology;
import com.by.open.ontology.ontologyservice.core.repository.OntologyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;

@RestController
@RequestMapping("/api/ontology/import-export")
@CrossOrigin(origins = "*")
public class ImportExportController {

    @Autowired
    private OntologyRepository ontologyRepository;

    @PostMapping("/import")
    public Ontology importOntology(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "format", defaultValue = "OWL") String format,
            @RequestParam("ontologyName") String ontologyName,
            @RequestParam(value = "namespace", required = false) String namespace,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam("creatorId") String creatorId) throws IOException {
        
        Ontology ontology = new Ontology();
        ontology.setName(ontologyName);
        ontology.setNamespace(namespace != null ? namespace : "http://example.org/" + ontologyName.toLowerCase().replace(" ", "-"));
        ontology.setDescription(description);
        ontology.setFormat(format);
        ontology.setCreatorId(creatorId);
        ontology.setCreatedDate(new Date());
        ontology.setLastModifiedDate(new Date());
        ontology.setStatus("ACTIVE");
        ontology.setContent(file.getBytes());
        
        return ontologyRepository.save(ontology);
    }

    @GetMapping("/export/{id}")
    public ResponseEntity<byte[]> exportOntology(@PathVariable String id) {
        return ontologyRepository.findById(id)
                .map(ontology -> {
                    String filename = ontology.getName() + getFileExtension(ontology.getFormat());
                    return ResponseEntity.ok()
                            .header("Content-Disposition", "attachment; filename=\"" + filename + "\"")
                            .header("Content-Type", getContentType(ontology.getFormat()))
                            .body(ontology.getContent());
                })
                .orElse(ResponseEntity.notFound().build());
    }

    private String getFileExtension(String format) {
        if (format == null) return ".owl";
        switch (format.toUpperCase()) {
            case "OWL":
                return ".owl";
            case "RDF":
            case "RDFXML":
                return ".rdf";
            case "TURTLE":
                return ".ttl";
            case "JSONLD":
                return ".jsonld";
            default:
                return ".owl";
        }
    }

    private String getContentType(String format) {
        if (format == null) return "application/rdf+xml";
        switch (format.toUpperCase()) {
            case "OWL":
            case "RDF":
            case "RDFXML":
                return "application/rdf+xml";
            case "TURTLE":
                return "text/turtle";
            case "JSONLD":
                return "application/ld+json";
            default:
                return "application/rdf+xml";
        }
    }
}
