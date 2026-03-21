package com.by.open.ontology.ontologyservice.versionmodule.repository;

import com.by.open.ontology.ontologyservice.versionmodule.entity.OntologyVersion;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OntologyVersionRepository extends MongoRepository<OntologyVersion, String> {
    List<OntologyVersion> findByOntologyId(String ontologyId);
    List<OntologyVersion> findByOntologyIdOrderByCreatedAtDesc(String ontologyId);
    Optional<OntologyVersion> findFirstByOntologyIdOrderByCreatedAtDesc(String ontologyId);
    List<OntologyVersion> findByCreatedBy(String createdBy);
    List<OntologyVersion> findByParentVersionId(String parentVersionId);
}
