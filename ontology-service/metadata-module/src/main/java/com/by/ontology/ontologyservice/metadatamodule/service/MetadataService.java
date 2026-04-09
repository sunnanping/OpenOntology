package com.by.ontology.ontologyservice.metadatamodule.service;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.vocab.OWL2Datatype;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class MetadataService {

    private final OWLOntologyManager manager;
    private final OWLDataFactory factory;

    public MetadataService() {
        this.manager = OWLManager.createOWLOntologyManager();
        this.factory = manager.getOWLDataFactory();
    }

    /**
     * 获取标准的OWL属性
     */
    @Cacheable(value = "owlProperties", key = "'standard'", unless = "#result == null or #result.isEmpty()")
    public List<String> getStandardOWLProperties() {
        List<String> properties = new ArrayList<>();
        
        // 添加基础的OWL属性
        properties.add(factory.getOWLBottomDataProperty().getIRI().getFragment());
        properties.add(factory.getOWLBottomObjectProperty().getIRI().getFragment());
        properties.add(factory.getOWLTopDataProperty().getIRI().getFragment());
        properties.add(factory.getOWLTopObjectProperty().getIRI().getFragment());
        
        return properties;
    }

    /**
     * 获取标准的数据类型
     */
    @Cacheable(value = "datatypes", key = "'standard'", unless = "#result == null or #result.isEmpty()")
    public List<String> getStandardDatatypes() {
        List<String> datatypes = new ArrayList<>();
        
        // 添加XSD数据类型 - 使用OWL2Datatype
        datatypes.add(OWL2Datatype.XSD_STRING.getIRI().getFragment());
        datatypes.add(OWL2Datatype.XSD_INT.getIRI().getFragment());
        datatypes.add(OWL2Datatype.XSD_INTEGER.getIRI().getFragment());
        datatypes.add(OWL2Datatype.XSD_BOOLEAN.getIRI().getFragment());
        datatypes.add(OWL2Datatype.XSD_FLOAT.getIRI().getFragment());
        datatypes.add(OWL2Datatype.XSD_DOUBLE.getIRI().getFragment());
        datatypes.add(OWL2Datatype.XSD_DATE_TIME.getIRI().getFragment());
        datatypes.add(OWL2Datatype.XSD_LONG.getIRI().getFragment());
        datatypes.add(OWL2Datatype.XSD_SHORT.getIRI().getFragment());
        datatypes.add(OWL2Datatype.XSD_BYTE.getIRI().getFragment());
        datatypes.add(OWL2Datatype.XSD_DECIMAL.getIRI().getFragment());
        datatypes.add(OWL2Datatype.XSD_NEGATIVE_INTEGER.getIRI().getFragment());
        datatypes.add(OWL2Datatype.XSD_NON_NEGATIVE_INTEGER.getIRI().getFragment());
        datatypes.add(OWL2Datatype.XSD_NON_POSITIVE_INTEGER.getIRI().getFragment());
        datatypes.add(OWL2Datatype.XSD_POSITIVE_INTEGER.getIRI().getFragment());
        
        // 添加OWL数据类型
        datatypes.add(OWL2Datatype.RDF_XML_LITERAL.getIRI().getFragment());
        datatypes.add(OWL2Datatype.OWL_REAL.getIRI().getFragment());
        datatypes.add(OWL2Datatype.OWL_RATIONAL.getIRI().getFragment());
        
        return datatypes;
    }

    /**
     * 获取标准的RDFS属性
     */
    @Cacheable(value = "rdfsProperties", key = "'standard'", unless = "#result == null or #result.isEmpty()")
    public List<String> getStandardRDFSProperties() {
        List<String> properties = new ArrayList<>();
        
        // 添加RDFS属性 - 使用IRI字符串
        properties.add("label");
        properties.add("comment");
        properties.add("domain");
        properties.add("range");
        properties.add("subClassOf");
        properties.add("subPropertyOf");
        properties.add("isDefinedBy");
        properties.add("seeAlso");
        
        return properties;
    }

    /**
     * 获取标准的RDF属性
     */
    @Cacheable(value = "rdfProperties", key = "'standard'", unless = "#result == null or #result.isEmpty()")
    public List<String> getStandardRDFProperties() {
        List<String> properties = new ArrayList<>();
        
        // 添加RDF属性 - 使用IRI字符串
        properties.add("type");
        properties.add("subject");
        properties.add("predicate");
        properties.add("object");
        properties.add("first");
        properties.add("rest");
        properties.add("nil");
        
        return properties;
    }
}
