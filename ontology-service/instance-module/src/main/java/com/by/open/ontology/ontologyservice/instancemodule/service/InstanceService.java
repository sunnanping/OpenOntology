package com.by.open.ontology.ontologyservice.instancemodule.service;

import com.by.open.ontology.ontologyservice.instancemodule.entity.Instance;
import com.by.open.ontology.ontologyservice.instancemodule.repository.InstanceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class InstanceService {

    @Autowired
    private InstanceRepository instanceRepository;

    public Instance create(Instance instance) {
        instance.setCreatedDate(new Date());
        instance.setLastModifiedDate(new Date());
        return instanceRepository.save(instance);
    }

    public Instance findById(String id) {
        return instanceRepository.findById(id).orElse(null);
    }

    public Instance findByName(String name) {
        return instanceRepository.findByName(name);
    }

    public Instance findByIri(String iri) {
        return instanceRepository.findByIri(iri);
    }

    public List<Instance> findByOntologyId(String ontologyId) {
        return instanceRepository.findByOntologyId(ontologyId);
    }

    public List<Instance> findByClassId(String classId) {
        return instanceRepository.findByClassId(classId);
    }

    public List<Instance> findAll() {
        return instanceRepository.findAll();
    }

    public Instance update(String id, Instance instance) {
        Instance existingInstance = instanceRepository.findById(id).orElse(null);
        if (existingInstance != null) {
            existingInstance.setName(instance.getName());
            existingInstance.setIri(instance.getIri());
            existingInstance.setOntologyId(instance.getOntologyId());
            existingInstance.setClassId(instance.getClassId());
            existingInstance.setPropertyValues(instance.getPropertyValues());
            existingInstance.setLastModifiedDate(new Date());
            return instanceRepository.save(existingInstance);
        }
        return null;
    }

    public void delete(String id) {
        instanceRepository.deleteById(id);
    }

    public void addPropertyValue(String instanceId, String propertyName, Object propertyValue) {
        Instance instance = instanceRepository.findById(instanceId).orElse(null);
        if (instance != null) {
            instance.getPropertyValues().put(propertyName, propertyValue);
            instanceRepository.save(instance);
        }
    }

    public void removePropertyValue(String instanceId, String propertyName) {
        Instance instance = instanceRepository.findById(instanceId).orElse(null);
        if (instance != null) {
            instance.getPropertyValues().remove(propertyName);
            instanceRepository.save(instance);
        }
    }

}
