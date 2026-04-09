package com.by.open.ontology.ontologyservice.metadatamodule.controller;

import com.by.open.ontology.ontologyservice.metadatamodule.service.MetadataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/metadata")
public class MetadataController {

    @Autowired
    private MetadataService metadataService;

    /**
     * 获取标准的OWL属性
     */
    @GetMapping("/owl/properties")
    public ResponseEntity<List<String>> getStandardOWLProperties() {
        List<String> properties = metadataService.getStandardOWLProperties();
        return ResponseEntity.ok(properties);
    }

    /**
     * 获取标准的数据类型
     */
    @GetMapping("/datatypes")
    public ResponseEntity<List<String>> getStandardDatatypes() {
        List<String> datatypes = metadataService.getStandardDatatypes();
        return ResponseEntity.ok(datatypes);
    }

    /**
     * 获取标准的RDFS属性
     */
    @GetMapping("/rdfs/properties")
    public ResponseEntity<List<String>> getStandardRDFSProperties() {
        List<String> properties = metadataService.getStandardRDFSProperties();
        return ResponseEntity.ok(properties);
    }

    /**
     * 获取标准的RDF属性
     */
    @GetMapping("/rdf/properties")
    public ResponseEntity<List<String>> getStandardRDFProperties() {
        List<String> properties = metadataService.getStandardRDFProperties();
        return ResponseEntity.ok(properties);
    }
}
