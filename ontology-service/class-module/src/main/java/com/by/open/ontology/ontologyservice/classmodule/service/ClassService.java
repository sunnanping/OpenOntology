package com.by.open.ontology.ontologyservice.classmodule.service;

import com.by.open.ontology.ontologyservice.classmodule.entity.Class;
import com.by.open.ontology.ontologyservice.classmodule.repository.ClassRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class ClassService {

    @Autowired
    private ClassRepository classRepository;
    
    private static final String OWL_THING_ID = "owl:Thing";
    private static final String OWL_THING_IRI = "http://www.w3.org/2002/07/owl#Thing";
    
    // 确保 owl:Thing 根节点存在
    private void ensureOwlThingExists(String ontologyId) {
        Class owlThing = classRepository.findById(OWL_THING_ID).orElse(null);
        if (owlThing == null) {
            // 创建 owl:Thing 根节点
            owlThing = new Class();
            owlThing.setId(OWL_THING_ID);
            owlThing.setName("owl:Thing");
            owlThing.setIri(OWL_THING_IRI);
            owlThing.setOntologyId(ontologyId);
            owlThing.setLanguageTag("en");
            owlThing.setCreatedDate(new Date());
            owlThing.setLastModifiedDate(new Date());
            owlThing.setSuperClasses(new ArrayList<>());
            owlThing.setSubClasses(new ArrayList<>());
            owlThing.setProperties(new ArrayList<>());
            owlThing.setIndividuals(new ArrayList<>());
            owlThing.setAbstractClass(true);
            
            // 创建 rdfs:label 注解
            List<Class.Annotation> annotations = new ArrayList<>();
            annotations.add(new Class.Annotation("rdfs:label", "Thing", "en"));
            owlThing.setAnnotations(annotations);
            
            classRepository.save(owlThing);
        }
    }

    public Class create(Class classEntity) {
        // 确保 owl:Thing 根节点存在
        String ontologyId = classEntity.getOntologyId();
        if (ontologyId != null && !ontologyId.isEmpty()) {
            ensureOwlThingExists(ontologyId);
        }
        
        // 设置创建时间和更新时间
        classEntity.setCreatedDate(new Date());
        classEntity.setLastModifiedDate(new Date());
        
        // 如果没有设置ID，生成一个
        if (classEntity.getId() == null || classEntity.getId().isEmpty()) {
            classEntity.setId(UUID.randomUUID().toString());
        }
        
        // 如果没有设置语言标签，使用默认值
        if (classEntity.getLanguageTag() == null || classEntity.getLanguageTag().isEmpty()) {
            classEntity.setLanguageTag("en");
        }
        
        // 创建rdfs:label注解
        List<Class.Annotation> annotations = new ArrayList<>();
        if (classEntity.getName() != null && !classEntity.getName().isEmpty()) {
            Class.Annotation labelAnnotation = new Class.Annotation(
                "rdfs:label",
                classEntity.getName(),
                classEntity.getLanguageTag()
            );
            annotations.add(labelAnnotation);
        }
        classEntity.setAnnotations(annotations);
        
        // 处理父类关系
        if (classEntity.getSuperClasses() == null) {
            classEntity.setSuperClasses(new ArrayList<>());
        }
        if (classEntity.getSuperClasses().isEmpty()) {
            classEntity.getSuperClasses().add(OWL_THING_ID);
        }
        
        // 初始化其他列表
        if (classEntity.getSubClasses() == null) {
            classEntity.setSubClasses(new ArrayList<>());
        }
        if (classEntity.getProperties() == null) {
            classEntity.setProperties(new ArrayList<>());
        }
        if (classEntity.getIndividuals() == null) {
            classEntity.setIndividuals(new ArrayList<>());
        }
        
        // 保存新创建的类
        Class savedClass = classRepository.save(classEntity);
        
        // 更新父类的 subClasses 列表
        for (String parentId : savedClass.getSuperClasses()) {
            Class parentClass = classRepository.findById(parentId).orElse(null);
            if (parentClass != null) {
                if (parentClass.getSubClasses() == null) {
                    parentClass.setSubClasses(new ArrayList<>());
                }
                if (!parentClass.getSubClasses().contains(savedClass.getId())) {
                    parentClass.getSubClasses().add(savedClass.getId());
                    parentClass.setLastModifiedDate(new Date());
                    classRepository.save(parentClass);
                }
            }
        }
        
        return savedClass;
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

    public void moveClass(String classId, String newParentId) {
        Class classEntity = classRepository.findById(classId).orElse(null);
        if (classEntity != null) {
            // Remove from old parent
            List<String> superClasses = classEntity.getSuperClasses();
            if (superClasses != null && !superClasses.isEmpty()) {
                String oldParentId = superClasses.get(0);
                Class oldParent = classRepository.findById(oldParentId).orElse(null);
                if (oldParent != null && oldParent.getSubClasses() != null) {
                    oldParent.getSubClasses().remove(classId);
                    classRepository.save(oldParent);
                }
            }
            
            // Add to new parent
            Class newParent = classRepository.findById(newParentId).orElse(null);
            if (newParent != null) {
                if (newParent.getSubClasses() == null) {
                    newParent.setSubClasses(new ArrayList<>());
                }
                newParent.getSubClasses().add(classId);
                classRepository.save(newParent);
            }
            
            // Update class superClasses
            if (classEntity.getSuperClasses() == null) {
                classEntity.setSuperClasses(new ArrayList<>());
            } else {
                classEntity.getSuperClasses().clear();
            }
            classEntity.getSuperClasses().add(newParentId);
            classEntity.setLastModifiedDate(new Date());
            classRepository.save(classEntity);
        }
    }

    public void mergeClasses(String sourceId, String targetId) {
        Class sourceClass = classRepository.findById(sourceId).orElse(null);
        Class targetClass = classRepository.findById(targetId).orElse(null);
        
        if (sourceClass != null && targetClass != null) {
            // Merge properties
            if (sourceClass.getProperties() != null) {
                if (targetClass.getProperties() == null) {
                    targetClass.setProperties(new ArrayList<>());
                }
                targetClass.getProperties().addAll(sourceClass.getProperties());
            }
            
            // Merge individuals
            if (sourceClass.getIndividuals() != null) {
                if (targetClass.getIndividuals() == null) {
                    targetClass.setIndividuals(new ArrayList<>());
                }
                targetClass.getIndividuals().addAll(sourceClass.getIndividuals());
            }
            
            // Merge subClasses
            if (sourceClass.getSubClasses() != null) {
                if (targetClass.getSubClasses() == null) {
                    targetClass.setSubClasses(new ArrayList<>());
                }
                targetClass.getSubClasses().addAll(sourceClass.getSubClasses());
                
                // Update subClasses' superClass reference
                for (String subClassId : sourceClass.getSubClasses()) {
                    Class subClass = classRepository.findById(subClassId).orElse(null);
                    if (subClass != null && subClass.getSuperClasses() != null) {
                        subClass.getSuperClasses().remove(sourceId);
                        subClass.getSuperClasses().add(targetId);
                        classRepository.save(subClass);
                    }
                }
            }
            
            targetClass.setLastModifiedDate(new Date());
            classRepository.save(targetClass);
            
            // Remove source class from its parent's subClasses
            if (sourceClass.getSuperClasses() != null && !sourceClass.getSuperClasses().isEmpty()) {
                String parentId = sourceClass.getSuperClasses().get(0);
                Class parent = classRepository.findById(parentId).orElse(null);
                if (parent != null && parent.getSubClasses() != null) {
                    parent.getSubClasses().remove(sourceId);
                    classRepository.save(parent);
                }
            }
            
            // Delete source class
            classRepository.deleteById(sourceId);
        }
    }

    public List<Class> searchClasses(String query, String projectId) {
        List<Class> classes = classRepository.findByOntologyId(projectId);
        return classes.stream()
                .filter(cls -> cls.getName().toLowerCase().contains(query.toLowerCase()))
                .collect(java.util.stream.Collectors.toList());
    }
}
