package com.by.open.ontology.classservice.service;

import com.by.open.ontology.classservice.entity.Class;
import com.by.open.ontology.classservice.repository.ClassRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class ClassService {

    @Autowired
    private ClassRepository classRepository;

    public Class create(Class classEntity) {
        classEntity.setCreatedDate(new Date());
        classEntity.setLastModifiedDate(new Date());
        return classRepository.save(classEntity);
    }

    public Class findById(String id) {
        return classRepository.findById(id).orElse(null);
    }

    public Class findByName(String name) {
        return classRepository.findByName(name);
    }

    public Class findByIri(String iri) {
        return classRepository.findByIri(iri);
    }

    public List<Class> findByOntologyId(String ontologyId) {
        return classRepository.findByOntologyId(ontologyId);
    }

    public List<Class> findAll() {
        return classRepository.findAll();
    }

    public Class update(String id, Class classEntity) {
        Class existingClass = classRepository.findById(id).orElse(null);
        if (existingClass != null) {
            existingClass.setName(classEntity.getName());
            existingClass.setIri(classEntity.getIri());
            existingClass.setOntologyId(classEntity.getOntologyId());
            existingClass.setDescription(classEntity.getDescription());
            existingClass.setSuperClasses(classEntity.getSuperClasses());
            existingClass.setSubClasses(classEntity.getSubClasses());
            existingClass.setProperties(classEntity.getProperties());
            existingClass.setIndividuals(classEntity.getIndividuals());
            existingClass.setAbstractClass(classEntity.isAbstractClass());
            existingClass.setLastModifiedDate(new Date());
            return classRepository.save(existingClass);
        }
        return null;
    }

    public void delete(String id) {
        classRepository.deleteById(id);
    }

    public void addSubClass(String classId, String subClassId) {
        Class classEntity = classRepository.findById(classId).orElse(null);
        if (classEntity != null) {
            classEntity.getSubClasses().add(subClassId);
            classRepository.save(classEntity);
        }
    }

    public void addSuperClass(String classId, String superClassId) {
        Class classEntity = classRepository.findById(classId).orElse(null);
        if (classEntity != null) {
            classEntity.getSuperClasses().add(superClassId);
            classRepository.save(classEntity);
        }
    }

    public void addProperty(String classId, String propertyId) {
        Class classEntity = classRepository.findById(classId).orElse(null);
        if (classEntity != null) {
            classEntity.getProperties().add(propertyId);
            classRepository.save(classEntity);
        }
    }

    public void addIndividual(String classId, String individualId) {
        Class classEntity = classRepository.findById(classId).orElse(null);
        if (classEntity != null) {
            classEntity.getIndividuals().add(individualId);
            classRepository.save(classEntity);
        }
    }

}