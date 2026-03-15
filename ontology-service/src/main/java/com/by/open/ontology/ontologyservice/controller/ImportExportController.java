package com.by.open.ontology.ontologyservice.controller;

import com.by.open.ontology.ontologyservice.service.ImportExportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/api/ontology/import-export")
@CrossOrigin(origins = "*")
public class ImportExportController {

    @Autowired
    private ImportExportService importExportService;

    @GetMapping("/export/{ontologyId}")
    public ResponseEntity<byte[]> exportOntology(
            @PathVariable String ontologyId,
            @RequestParam(defaultValue = "OWL") String format) {
        try {
            byte[] content = importExportService.exportOntology(ontologyId, format);
            
            String filename = ontologyId + "." + getExtension(format);
            String contentType = getContentType(format);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(contentType));
            headers.setContentDispositionFormData("attachment", filename);
            headers.setContentLength(content.length);
            
            return new ResponseEntity<>(content, headers, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage().getBytes(StandardCharsets.UTF_8), 
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/import")
    public ResponseEntity<ImportResult> importOntology(
            @RequestParam("file") MultipartFile file,
            @RequestParam(defaultValue = "OWL") String format,
            @RequestParam(required = false) String ontologyName,
            @RequestParam(required = false) String namespace) {
        try {
            String ontologyId = importExportService.importOntology(file, format, ontologyName, namespace);
            
            ImportResult result = new ImportResult();
            result.setSuccess(true);
            result.setOntologyId(ontologyId);
            result.setMessage("Ontology imported successfully");
            
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            ImportResult result = new ImportResult();
            result.setSuccess(false);
            result.setMessage("Import failed: " + e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
        }
    }

    @GetMapping("/formats")
    public ResponseEntity<FormatInfo[]> getSupportedFormats() {
        FormatInfo[] formats = {
            new FormatInfo("OWL", "OWL/XML", "application/owl+xml", ".owl"),
            new FormatInfo("RDFXML", "RDF/XML", "application/rdf+xml", ".rdf"),
            new FormatInfo("TURTLE", "Turtle", "text/turtle", ".ttl"),
            new FormatInfo("NTRIPLES", "N-Triples", "application/n-triples", ".nt"),
            new FormatInfo("JSONLD", "JSON-LD", "application/ld+json", ".jsonld"),
            new FormatInfo("MANCHESTER", "Manchester Syntax", "text/plain", ".man"),
            new FormatInfo("FUNCTIONAL", "Functional Syntax", "text/plain", ".ofn"),
            new FormatInfo("XML", "XML", "application/xml", ".xml"),
            new FormatInfo("CSV", "CSV", "text/csv", ".csv"),
            new FormatInfo("JSON", "JSON", "application/json", ".json")
        };
        
        return ResponseEntity.ok(formats);
    }

    private String getExtension(String format) {
        switch (format.toUpperCase()) {
            case "RDFXML":
                return "rdf";
            case "TURTLE":
                return "ttl";
            case "NTRIPLES":
                return "nt";
            case "JSONLD":
                return "jsonld";
            case "MANCHESTER":
                return "man";
            case "FUNCTIONAL":
                return "ofn";
            case "XML":
                return "xml";
            case "CSV":
                return "csv";
            case "JSON":
                return "json";
            default:
                return "owl";
        }
    }

    private String getContentType(String format) {
        switch (format.toUpperCase()) {
            case "RDFXML":
                return "application/rdf+xml";
            case "TURTLE":
                return "text/turtle";
            case "NTRIPLES":
                return "application/n-triples";
            case "JSONLD":
                return "application/ld+json";
            case "MANCHESTER":
            case "FUNCTIONAL":
                return "text/plain";
            case "XML":
                return "application/xml";
            case "CSV":
                return "text/csv";
            case "JSON":
                return "application/json";
            default:
                return "application/owl+xml";
        }
    }

    public static class ImportResult {
        private boolean success;
        private String ontologyId;
        private String message;

        public boolean isSuccess() {
            return success;
        }

        public void setSuccess(boolean success) {
            this.success = success;
        }

        public String getOntologyId() {
            return ontologyId;
        }

        public void setOntologyId(String ontologyId) {
            this.ontologyId = ontologyId;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }

    public static class FormatInfo {
        private String code;
        private String name;
        private String contentType;
        private String extension;

        public FormatInfo(String code, String name, String contentType, String extension) {
            this.code = code;
            this.name = name;
            this.contentType = contentType;
            this.extension = extension;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getContentType() {
            return contentType;
        }

        public void setContentType(String contentType) {
            this.contentType = contentType;
        }

        public String getExtension() {
            return extension;
        }

        public void setExtension(String extension) {
            this.extension = extension;
        }
    }
}
