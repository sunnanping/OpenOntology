package com.by.ontology.ontologyservice.metadatamodule.service;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;
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
        properties.add(OWLVocabulary.OWL_BOTTOM_DATA_PROPERTY.getIRI().getFragment());
        properties.add(OWLVocabulary.OWL_BOTTOM_OBJECT_PROPERTY.getIRI().getFragment());
        properties.add(OWLVocabulary.OWL_TOP_DATA_PROPERTY.getIRI().getFragment());
        properties.add(OWLVocabulary.OWL_TOP_OBJECT_PROPERTY.getIRI().getFragment());
        
        return properties;
    }

    /**
     * 获取标准的数据类型
     */
    @Cacheable(value = "datatypes", key = "'standard'", unless = "#result == null or #result.isEmpty()")
    public List<String> getStandardDatatypes() {
        List<String> datatypes = new ArrayList<>();
        
        // 添加XSD数据类型
        datatypes.add(XSDVocabulary.STRING.getIRI().getFragment());
        datatypes.add(XSDVocabulary.INT.getIRI().getFragment());
        datatypes.add(XSDVocabulary.INTEGER.getIRI().getFragment());
        datatypes.add(XSDVocabulary.BOOLEAN.getIRI().getFragment());
        datatypes.add(XSDVocabulary.FLOAT.getIRI().getFragment());
        datatypes.add(XSDVocabulary.DOUBLE.getIRI().getFragment());
        datatypes.add(XSDVocabulary.DATE.getIRI().getFragment());
        datatypes.add(XSDVocabulary.DATETIME.getIRI().getFragment());
        datatypes.add(XSDVocabulary.LONG.getIRI().getFragment());
        datatypes.add(XSDVocabulary.SHORT.getIRI().getFragment());
        datatypes.add(XSDVocabulary.BYTE.getIRI().getFragment());
        datatypes.add(XSDVocabulary.UNSIGNED_INT.getIRI().getFragment());
        datatypes.add(XSDVocabulary.UNSIGNED_LONG.getIRI().getFragment());
        datatypes.add(XSDVocabulary.UNSIGNED_SHORT.getIRI().getFragment());
        datatypes.add(XSDVocabulary.UNSIGNED_BYTE.getIRI().getFragment());
        datatypes.add(XSDVocabulary.DECIMAL.getIRI().getFragment());
        datatypes.add(XSDVocabulary.NEGATIVE_INTEGER.getIRI().getFragment());
        datatypes.add(XSDVocabulary.NON_NEGATIVE_INTEGER.getIRI().getFragment());
        datatypes.add(XSDVocabulary.NON_POSITIVE_INTEGER.getIRI().getFragment());
        datatypes.add(XSDVocabulary.POSITIVE_INTEGER.getIRI().getFragment());
        
        // 添加OWL数据类型
        datatypes.add(OWLVocabulary.OWL_REAL.getIRI().getFragment());
        datatypes.add(OWLVocabulary.OWL_RATIONAL.getIRI().getFragment());
        datatypes.add(OWLVocabulary.OWL_LITERAL.getIRI().getFragment());
        datatypes.add(OWLVocabulary.OWL_THING.getIRI().getFragment());
        
        return datatypes;
    }

    /**
     * 获取标准的RDFS属性
     */
    @Cacheable(value = "rdfsProperties", key = "'standard'", unless = "#result == null or #result.isEmpty()")
    public List<String> getStandardRDFSProperties() {
        List<String> properties = new ArrayList<>();
        
        // 添加RDFS属性
        properties.add(RDFSVocabulary.RDFS_LABEL.getIRI().getFragment());
        properties.add(RDFSVocabulary.RDFS_COMMENT.getIRI().getFragment());
        properties.add(RDFSVocabulary.RDFS_DOMAIN.getIRI().getFragment());
        properties.add(RDFSVocabulary.RDFS_RANGE.getIRI().getFragment());
        properties.add(RDFSVocabulary.RDFS_SUB_CLASS_OF.getIRI().getFragment());
        properties.add(RDFSVocabulary.RDFS_SUB_PROPERTY_OF.getIRI().getFragment());
        properties.add(RDFSVocabulary.RDFS_IS_DEFINED_BY.getIRI().getFragment());
        properties.add(RDFSVocabulary.RDFS_SEE_ALSO.getIRI().getFragment());
        
        return properties;
    }

    /**
     * 获取标准的RDF属性
     */
    @Cacheable(value = "rdfProperties", key = "'standard'", unless = "#result == null or #result.isEmpty()")
    public List<String> getStandardRDFProperties() {
        List<String> properties = new ArrayList<>();
        
        // 添加RDF属性
        properties.add(RDFVocabulary.RDF_TYPE.getIRI().getFragment());
        properties.add(RDFVocabulary.RDF_SUBJECT.getIRI().getFragment());
        properties.add(RDFVocabulary.RDF_PREDICATE.getIRI().getFragment());
        properties.add(RDFVocabulary.RDF_OBJECT.getIRI().getFragment());
        properties.add(RDFVocabulary.RDF_FIRST.getIRI().getFragment());
        properties.add(RDFVocabulary.RDF_REST.getIRI().getFragment());
        properties.add(RDFVocabulary.RDF_NIL.getIRI().getFragment());
        
        return properties;
    }
}
