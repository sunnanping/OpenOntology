package com.by.open.ontology.ontologyservice.service;

import com.by.open.ontology.ontologyservice.entity.Ontology;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.formats.*;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.util.ShortFormProvider;
import org.semanticweb.owlapi.util.SimpleShortFormProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
public class ImportExportService {

    @Autowired
    private OntologyService ontologyService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public byte[] exportOntology(String ontologyId, String format) throws Exception {
        Ontology ontology = ontologyService.findById(ontologyId);
        if (ontology == null) {
            throw new RuntimeException("Ontology not found: " + ontologyId);
        }

        switch (format.toUpperCase()) {
            case "OWL":
                return exportAsOWL(ontology);
            case "RDFXML":
                return exportAsRDFXML(ontology);
            case "TURTLE":
                return exportAsTurtle(ontology);
            case "NTRIPLES":
                return exportAsNTriples(ontology);
            case "JSONLD":
                return exportAsJSONLD(ontology);
            case "MANCHESTER":
                return exportAsManchesterSyntax(ontology);
            case "FUNCTIONAL":
                return exportAsFunctionalSyntax(ontology);
            case "XML":
                return exportAsXML(ontology);
            case "CSV":
                return exportAsCSV(ontology);
            default:
                return exportAsOWL(ontology);
        }
    }

    public String importOntology(MultipartFile file, String format, String ontologyName, String namespace) throws Exception {
        String content = new String(file.getBytes(), StandardCharsets.UTF_8);
        
        switch (format.toUpperCase()) {
            case "OWL":
            case "RDFXML":
            case "TURTLE":
            case "NTRIPLES":
            case "JSONLD":
            case "MANCHESTER":
            case "FUNCTIONAL":
                return importFromOWLFormat(content, format, ontologyName, namespace);
            case "XML":
                return importFromXML(content, ontologyName, namespace);
            case "CSV":
                return importFromCSV(content, ontologyName, namespace);
            case "JSON":
                return importFromJSON(content, ontologyName, namespace);
            default:
                return importFromOWLFormat(content, format, ontologyName, namespace);
        }
    }

    private byte[] exportAsOWL(Ontology ontology) throws Exception {
        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        OWLDataFactory factory = manager.getOWLDataFactory();
        
        IRI iri = IRI.create(ontology.getNamespace());
        OWLOntology owlOntology = manager.createOntology(iri);
        
        addOntologyMetadata(owlOntology, ontology, factory);
        
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        manager.saveOntology(owlOntology, new OWLXMLDocumentFormat(), outputStream);
        
        return outputStream.toByteArray();
    }

    private byte[] exportAsRDFXML(Ontology ontology) throws Exception {
        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        OWLDataFactory factory = manager.getOWLDataFactory();
        
        IRI iri = IRI.create(ontology.getNamespace());
        OWLOntology owlOntology = manager.createOntology(iri);
        
        addOntologyMetadata(owlOntology, ontology, factory);
        
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        manager.saveOntology(owlOntology, new RDFXMLDocumentFormat(), outputStream);
        
        return outputStream.toByteArray();
    }

    private byte[] exportAsTurtle(Ontology ontology) throws Exception {
        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        OWLDataFactory factory = manager.getOWLDataFactory();
        
        IRI iri = IRI.create(ontology.getNamespace());
        OWLOntology owlOntology = manager.createOntology(iri);
        
        addOntologyMetadata(owlOntology, ontology, factory);
        
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        manager.saveOntology(owlOntology, new TurtleDocumentFormat(), outputStream);
        
        return outputStream.toByteArray();
    }

    private byte[] exportAsNTriples(Ontology ontology) throws Exception {
        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        OWLDataFactory factory = manager.getOWLDataFactory();
        
        IRI iri = IRI.create(ontology.getNamespace());
        OWLOntology owlOntology = manager.createOntology(iri);
        
        addOntologyMetadata(owlOntology, ontology, factory);
        
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        manager.saveOntology(owlOntology, new NTriplesDocumentFormat(), outputStream);
        
        return outputStream.toByteArray();
    }

    private byte[] exportAsJSONLD(Ontology ontology) throws Exception {
        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        OWLDataFactory factory = manager.getOWLDataFactory();
        
        IRI iri = IRI.create(ontology.getNamespace());
        OWLOntology owlOntology = manager.createOntology(iri);
        
        addOntologyMetadata(owlOntology, ontology, factory);
        
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        RDFDocumentFormat rdfFormat = new RDFXMLDocumentFormat();
        rdfFormat.setParameter(RDFDocumentFormat.COMMENT, "Exported as RDF/XML for JSON-LD compatibility");
        manager.saveOntology(owlOntology, rdfFormat, outputStream);
        
        return outputStream.toByteArray();
    }

    private byte[] exportAsManchesterSyntax(Ontology ontology) throws Exception {
        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        OWLDataFactory factory = manager.getOWLDataFactory();
        
        IRI iri = IRI.create(ontology.getNamespace());
        OWLOntology owlOntology = manager.createOntology(iri);
        
        addOntologyMetadata(owlOntology, ontology, factory);
        
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        manager.saveOntology(owlOntology, new ManchesterSyntaxDocumentFormat(), outputStream);
        
        return outputStream.toByteArray();
    }

    private byte[] exportAsFunctionalSyntax(Ontology ontology) throws Exception {
        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        OWLDataFactory factory = manager.getOWLDataFactory();
        
        IRI iri = IRI.create(ontology.getNamespace());
        OWLOntology owlOntology = manager.createOntology(iri);
        
        addOntologyMetadata(owlOntology, ontology, factory);
        
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        manager.saveOntology(owlOntology, new FunctionalSyntaxDocumentFormat(), outputStream);
        
        return outputStream.toByteArray();
    }

    private byte[] exportAsXML(Ontology ontology) throws Exception {
        Map<String, Object> ontologyMap = new LinkedHashMap<>();
        ontologyMap.put("id", ontology.getId());
        ontologyMap.put("name", ontology.getName());
        ontologyMap.put("namespace", ontology.getNamespace());
        ontologyMap.put("description", ontology.getDescription());
        ontologyMap.put("status", ontology.getStatus());
        ontologyMap.put("format", ontology.getFormat());
        ontologyMap.put("createdDate", ontology.getCreatedDate());
        ontologyMap.put("lastModifiedDate", ontology.getLastModifiedDate());
        ontologyMap.put("creatorId", ontology.getCreatorId());
        
        Map<String, Object> root = new LinkedHashMap<>();
        root.put("ontology", ontologyMap);
        
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        String xml = objectMapper.writeValueAsString(root);
        
        return xml.getBytes(StandardCharsets.UTF_8);
    }

    private byte[] exportAsCSV(Ontology ontology) throws Exception {
        StringBuilder csv = new StringBuilder();
        csv.append("ID,Name,Namespace,Description,Status,Format,CreatedDate,LastModifiedDate,CreatorId\n");
        
        csv.append(escapeCsv(ontology.getId())).append(",");
        csv.append(escapeCsv(ontology.getName())).append(",");
        csv.append(escapeCsv(ontology.getNamespace())).append(",");
        csv.append(escapeCsv(ontology.getDescription())).append(",");
        csv.append(escapeCsv(ontology.getStatus())).append(",");
        csv.append(escapeCsv(ontology.getFormat())).append(",");
        csv.append(ontology.getCreatedDate()).append(",");
        csv.append(ontology.getLastModifiedDate()).append(",");
        csv.append(escapeCsv(ontology.getCreatorId())).append("\n");
        
        return csv.toString().getBytes(StandardCharsets.UTF_8);
    }

    private String importFromOWLFormat(String content, String format, String ontologyName, String namespace) throws Exception {
        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        
        OWLDocumentFormat owlFormat;
        switch (format.toUpperCase()) {
            case "RDFXML":
                owlFormat = new RDFXMLDocumentFormat();
                break;
            case "TURTLE":
                owlFormat = new TurtleDocumentFormat();
                break;
            case "NTRIPLES":
                owlFormat = new NTriplesDocumentFormat();
                break;
            case "MANCHESTER":
                owlFormat = new ManchesterSyntaxDocumentFormat();
                break;
            case "FUNCTIONAL":
                owlFormat = new FunctionalSyntaxDocumentFormat();
                break;
            default:
                owlFormat = new OWLXMLDocumentFormat();
        }
        
        File tempFile = File.createTempFile("ontology", ".owl");
        try {
            java.nio.file.Files.write(tempFile.toPath(), content.getBytes(StandardCharsets.UTF_8));
            OWLOntology owlOntology = manager.loadOntologyFromOntologyDocument(tempFile.toURI(), owlFormat);
            
            String actualNamespace = owlOntology.getOntologyID().getOntologyIRI().map(IRI::toString).orElse(namespace);
            String actualName = ontologyName != null && !ontologyName.isEmpty() ? ontologyName : "Imported Ontology";
            
            Ontology ontology = new Ontology();
            ontology.setName(actualName);
            ontology.setNamespace(actualNamespace);
            ontology.setDescription("Imported from " + format);
            ontology.setStatus("ACTIVE");
            ontology.setFormat(format);
            
            Ontology savedOntology = ontologyService.create(ontology);
            
            return savedOntology.getId();
        } finally {
            tempFile.delete();
        }
    }

    private String importFromXML(String content, String ontologyName, String namespace) throws Exception {
        Map<String, Object> root = objectMapper.readValue(content, Map.class);
        Map<String, Object> ontologyMap = (Map<String, Object>) root.get("ontology");
        
        Ontology ontology = new Ontology();
        ontology.setName(ontologyName != null && !ontologyName.isEmpty() ? ontologyName : (String) ontologyMap.get("name"));
        ontology.setNamespace(namespace != null && !namespace.isEmpty() ? namespace : (String) ontologyMap.get("namespace"));
        ontology.setDescription((String) ontologyMap.get("description"));
        ontology.setStatus((String) ontologyMap.get("status"));
        ontology.setFormat((String) ontologyMap.get("format"));
        
        Ontology savedOntology = ontologyService.create(ontology);
        
        return savedOntology.getId();
    }

    private String importFromCSV(String content, String ontologyName, String namespace) throws Exception {
        String[] lines = content.split("\n");
        if (lines.length < 2) {
            throw new RuntimeException("Invalid CSV format");
        }
        
        String[] headers = lines[0].split(",");
        String[] values = lines[1].split(",");
        
        Ontology ontology = new Ontology();
        ontology.setName(ontologyName != null && !ontologyName.isEmpty() ? ontologyName : getValue(headers, values, "Name"));
        ontology.setNamespace(namespace != null && !namespace.isEmpty() ? namespace : getValue(headers, values, "Namespace"));
        ontology.setDescription(getValue(headers, values, "Description"));
        ontology.setStatus(getValue(headers, values, "Status"));
        ontology.setFormat(getValue(headers, values, "Format"));
        
        Ontology savedOntology = ontologyService.create(ontology);
        
        return savedOntology.getId();
    }

    private String importFromJSON(String content, String ontologyName, String namespace) throws Exception {
        Map<String, Object> ontologyMap = objectMapper.readValue(content, Map.class);
        
        Ontology ontology = new Ontology();
        ontology.setName(ontologyName != null && !ontologyName.isEmpty() ? ontologyName : (String) ontologyMap.get("name"));
        ontology.setNamespace(namespace != null && !namespace.isEmpty() ? namespace : (String) ontologyMap.get("namespace"));
        ontology.setDescription((String) ontologyMap.get("description"));
        ontology.setStatus((String) ontologyMap.get("status"));
        ontology.setFormat((String) ontologyMap.get("format"));
        
        Ontology savedOntology = ontologyService.create(ontology);
        
        return savedOntology.getId();
    }

    private void addOntologyMetadata(OWLOntology owlOntology, Ontology ontology, OWLDataFactory factory) {
        OWLOntologyManager manager = owlOntology.getOWLOntologyManager();
        IRI ontologyIRI = owlOntology.getOntologyID().getOntologyIRI().orElse(IRI.create(ontology.getNamespace()));
        
        OWLAnnotationProperty labelProperty = factory.getRDFSLabel();
        OWLAnnotationProperty commentProperty = factory.getRDFSComment();
        OWLAnnotationProperty statusProperty = factory.getOWLAnnotationProperty(IRI.create("http://purl.org/dc/elements/1.1/status"));
        
        OWLLiteral nameLiteral = factory.getOWLLiteral(ontology.getName());
        OWLAnnotation nameAnnotation = factory.getOWLAnnotation(labelProperty, nameLiteral);
        manager.applyChange(new AddOntologyAnnotation(owlOntology, nameAnnotation));
        
        if (ontology.getDescription() != null && !ontology.getDescription().isEmpty()) {
            OWLLiteral descriptionLiteral = factory.getOWLLiteral(ontology.getDescription());
            OWLAnnotation descriptionAnnotation = factory.getOWLAnnotation(commentProperty, descriptionLiteral);
            manager.applyChange(new AddOntologyAnnotation(owlOntology, descriptionAnnotation));
        }
        
        if (ontology.getStatus() != null && !ontology.getStatus().isEmpty()) {
            OWLLiteral statusLiteral = factory.getOWLLiteral(ontology.getStatus());
            OWLAnnotation statusAnnotation = factory.getOWLAnnotation(statusProperty, statusLiteral);
            manager.applyChange(new AddOntologyAnnotation(owlOntology, statusAnnotation));
        }
    }

    private String escapeCsv(String value) {
        if (value == null) {
            return "";
        }
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }

    private String getValue(String[] headers, String[] values, String key) {
        for (int i = 0; i < headers.length; i++) {
            if (headers[i].trim().equalsIgnoreCase(key) && i < values.length) {
                return values[i].trim().replace("\"", "");
            }
        }
        return "";
    }
}
