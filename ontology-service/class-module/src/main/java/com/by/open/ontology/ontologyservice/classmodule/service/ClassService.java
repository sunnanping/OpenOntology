package com.by.open.ontology.ontologyservice.classmodule.service;

import com.by.open.ontology.ontologyservice.classmodule.entity.Class;
import com.by.open.ontology.ontologyservice.classmodule.repository.ClassRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class ClassService {

    @Autowired
    private ClassRepository classRepository;
    
    private static final String OWL_THING_ID = "owl:Thing";

    public Class create(Class classEntity) {
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
        // 如果没有指定父类，默认父类为owl:Thing（虚拟根节点）
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
        
        // 更新父类的 subClasses 列表（owl:Thing是虚拟节点，不在数据库中）
        for (String parentId : savedClass.getSuperClasses()) {
            // 跳过owl:Thing虚拟根节点
            if (OWL_THING_ID.equals(parentId)) {
                continue;
            }
            
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

    public void removeSuperClass(String classId, String superClassId) {
        Class classEntity = classRepository.findById(classId).orElse(null);
        if (classEntity != null) {
            classEntity.getSuperClasses().remove(superClassId);
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

    public Map<String, Object> importRDFS(String script, String ontologyId, String projectId) {
        Map<String, Object> result = new HashMap<>();
        List<Class> importedClasses = new ArrayList<>();
        
        try {
            // 解析Turtle/RDFS脚本
            Map<String, ParsedClass> parsedClasses = parseTurtleScript(script);
            
            // 创建类并保存
            for (ParsedClass parsedClass : parsedClasses.values()) {
                Class classEntity = new Class();
                classEntity.setId(UUID.randomUUID().toString());
                classEntity.setName(parsedClass.getName());
                classEntity.setIri(parsedClass.getIri());
                classEntity.setOntologyId(ontologyId);
                classEntity.setLanguageTag(parsedClass.getLanguageTag());
                classEntity.setSuperClasses(parsedClass.getSuperClasses());
                classEntity.setCreatedDate(new Date());
                classEntity.setLastModifiedDate(new Date());
                
                // 处理注解
                List<Class.Annotation> annotations = new ArrayList<>();
                for (ParsedAnnotation annotation : parsedClass.getAnnotations()) {
                    annotations.add(new Class.Annotation(
                        annotation.getPredicate(),
                        annotation.getValue(),
                        annotation.getLanguageTag()
                    ));
                }
                classEntity.setAnnotations(annotations);
                
                // 保存类
                Class savedClass = classRepository.save(classEntity);
                importedClasses.add(savedClass);
            }
            
            // 更新子类关系
            for (Class importedClass : importedClasses) {
                if (importedClass.getSuperClasses() != null) {
                    for (String parentId : importedClass.getSuperClasses()) {
                        if (!OWL_THING_ID.equals(parentId)) {
                            Class parentClass = classRepository.findById(parentId).orElse(null);
                            if (parentClass != null) {
                                if (parentClass.getSubClasses() == null) {
                                    parentClass.setSubClasses(new ArrayList<>());
                                }
                                if (!parentClass.getSubClasses().contains(importedClass.getId())) {
                                    parentClass.getSubClasses().add(importedClass.getId());
                                    parentClass.setLastModifiedDate(new Date());
                                    classRepository.save(parentClass);
                                }
                            }
                        }
                    }
                }
            }
            
            result.put("importedCount", importedClasses.size());
            result.put("importedClasses", importedClasses);
            result.put("success", true);
            
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", e.getMessage());
        }
        
        return result;
    }
    
    private Map<String, ParsedClass> parseTurtleScript(String script) {
        Map<String, ParsedClass> classes = new HashMap<>();
        Map<String, String> prefixes = new HashMap<>();
        
        String[] lines = script.split("\\n");
        String currentSubject = null;
        ParsedClass currentClass = null;
        
        for (String line : lines) {
            line = line.trim();
            
            // 跳过空行和注释
            if (line.isEmpty() || line.startsWith("#")) {
                continue;
            }
            
            // 解析前缀定义
            if (line.startsWith("@prefix")) {
                parsePrefix(line, prefixes);
                continue;
            }
            
            // 解析类定义
            if (line.contains("a rdfs:Class") || line.contains("a owl:Class")) {
                currentSubject = extractSubject(line);
                if (currentSubject != null) {
                    String fullUri = expandPrefix(currentSubject, prefixes);
                    currentClass = new ParsedClass();
                    currentClass.setUri(fullUri);
                    currentClass.setName(extractLocalName(currentSubject));
                    currentClass.setIri(fullUri);
                    currentClass.setLanguageTag("zh");
                    classes.put(fullUri, currentClass);
                }
            }
            
            // 解析rdfs:subClassOf
            if (line.contains("rdfs:subClassOf") && currentClass != null) {
                String parentUri = extractObject(line, prefixes);
                if (parentUri != null) {
                    currentClass.getSuperClasses().add(parentUri);
                }
            }
            
            // 解析rdfs:label
            if (line.contains("rdfs:label") && currentClass != null) {
                ParsedAnnotation annotation = parseLabel(line);
                if (annotation != null) {
                    currentClass.getAnnotations().add(annotation);
                    if (annotation.getLanguageTag() != null) {
                        currentClass.setLanguageTag(annotation.getLanguageTag());
                    }
                }
            }
            
            // 解析rdfs:comment
            if (line.contains("rdfs:comment") && currentClass != null) {
                ParsedAnnotation annotation = parseComment(line);
                if (annotation != null) {
                    currentClass.getAnnotations().add(annotation);
                }
            }
        }
        
        return classes;
    }
    
    private void parsePrefix(String line, Map<String, String> prefixes) {
        // 解析 @prefix : <http://example.org/device#> .
        Pattern pattern = Pattern.compile("@prefix\\s+(\\w*):\\s*<(.*?)>\\s*\\.");
        Matcher matcher = pattern.matcher(line);
        if (matcher.find()) {
            String prefix = matcher.group(1);
            String uri = matcher.group(2);
            prefixes.put(prefix.isEmpty() ? ":" : prefix + ":", uri);
        }
    }
    
    private String extractSubject(String line) {
        // 提取主语，如 :办公室 或 <http://example.org/device#办公室>
        Pattern pattern = Pattern.compile("^(?::(\\w+)|<(.*?)>)\\s+a\\s+");
        Matcher matcher = pattern.matcher(line);
        if (matcher.find()) {
            if (matcher.group(1) != null) {
                return ":" + matcher.group(1);
            } else if (matcher.group(2) != null) {
                return "<" + matcher.group(2) + ">";
            }
        }
        return null;
    }
    
    private String extractObject(String line, Map<String, String> prefixes) {
        // 提取宾语
        Pattern pattern = Pattern.compile("rdfs:subClassOf\\s+(?::(\\w+)|<(.*?)>)");
        Matcher matcher = pattern.matcher(line);
        if (matcher.find()) {
            if (matcher.group(1) != null) {
                return expandPrefix(":" + matcher.group(1), prefixes);
            } else if (matcher.group(2) != null) {
                return matcher.group(2);
            }
        }
        return null;
    }
    
    private String expandPrefix(String prefixedUri, Map<String, String> prefixes) {
        for (Map.Entry<String, String> entry : prefixes.entrySet()) {
            if (prefixedUri.startsWith(entry.getKey())) {
                return entry.getValue() + prefixedUri.substring(entry.getKey().length());
            }
        }
        return prefixedUri;
    }
    
    private String extractLocalName(String uri) {
        // 提取本地名称
        if (uri.startsWith("<") && uri.endsWith(">")) {
            uri = uri.substring(1, uri.length() - 1);
        }
        int lastHash = uri.lastIndexOf('#');
        int lastSlash = uri.lastIndexOf('/');
        int lastColon = uri.lastIndexOf(':');
        int lastIndex = Math.max(lastHash, Math.max(lastSlash, lastColon));
        if (lastIndex > 0) {
            return uri.substring(lastIndex + 1);
        }
        return uri;
    }
    
    private ParsedAnnotation parseLabel(String line) {
        // 解析 rdfs:label "办公室"@zh
        Pattern pattern = Pattern.compile("rdfs:label\\s+\"(.*?)\"(?:@([a-zA-Z-]+))?");
        Matcher matcher = pattern.matcher(line);
        if (matcher.find()) {
            ParsedAnnotation annotation = new ParsedAnnotation();
            annotation.setPredicate("rdfs:label");
            annotation.setValue(matcher.group(1));
            annotation.setLanguageTag(matcher.group(2) != null ? matcher.group(2) : "zh");
            return annotation;
        }
        return null;
    }
    
    private ParsedAnnotation parseComment(String line) {
        // 解析 rdfs:comment "用于办公的物理空间"@zh
        Pattern pattern = Pattern.compile("rdfs:comment\\s+\"(.*?)\"(?:@([a-zA-Z-]+))?");
        Matcher matcher = pattern.matcher(line);
        if (matcher.find()) {
            ParsedAnnotation annotation = new ParsedAnnotation();
            annotation.setPredicate("rdfs:comment");
            annotation.setValue(matcher.group(1));
            annotation.setLanguageTag(matcher.group(2) != null ? matcher.group(2) : "zh");
            return annotation;
        }
        return null;
    }
    
    // 辅助类
    private static class ParsedClass {
        private String uri;
        private String name;
        private String iri;
        private String languageTag = "zh";
        private List<String> superClasses = new ArrayList<>();
        private List<ParsedAnnotation> annotations = new ArrayList<>();
        
        // Getters and Setters
        public String getUri() { return uri; }
        public void setUri(String uri) { this.uri = uri; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getIri() { return iri; }
        public void setIri(String iri) { this.iri = iri; }
        public String getLanguageTag() { return languageTag; }
        public void setLanguageTag(String languageTag) { this.languageTag = languageTag; }
        public List<String> getSuperClasses() { return superClasses; }
        public void setSuperClasses(List<String> superClasses) { this.superClasses = superClasses; }
        public List<ParsedAnnotation> getAnnotations() { return annotations; }
        public void setAnnotations(List<ParsedAnnotation> annotations) { this.annotations = annotations; }
    }
    
    private static class ParsedAnnotation {
        private String predicate;
        private String value;
        private String languageTag;
        
        // Getters and Setters
        public String getPredicate() { return predicate; }
        public void setPredicate(String predicate) { this.predicate = predicate; }
        public String getValue() { return value; }
        public void setValue(String value) { this.value = value; }
        public String getLanguageTag() { return languageTag; }
        public void setLanguageTag(String languageTag) { this.languageTag = languageTag; }
    }
}
