package com.by.open.ontology.versionservice.service;

import com.by.open.ontology.versionservice.entity.OntologyVersion;
import com.by.open.ontology.versionservice.repository.OntologyVersionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Service
public class VersionService {

    @Autowired
    private OntologyVersionRepository versionRepository;

    public List<OntologyVersion> findAll() {
        return versionRepository.findAll();
    }

    public Optional<OntologyVersion> findById(String id) {
        return versionRepository.findById(id);
    }

    public List<OntologyVersion> findByOntologyId(String ontologyId) {
        return versionRepository.findByOntologyIdOrderByCreatedAtDesc(ontologyId);
    }

    public Optional<OntologyVersion> getLatestVersion(String ontologyId) {
        return versionRepository.findFirstByOntologyIdOrderByCreatedAtDesc(ontologyId);
    }

    public OntologyVersion createVersion(OntologyVersion version) {
        version.setCreatedAt(LocalDateTime.now());
        version.setStatus("ACTIVE");
        
        if (version.getVersionNumber() == null || version.getVersionNumber().isEmpty()) {
            Optional<OntologyVersion> latestVersion = getLatestVersion(version.getOntologyId());
            if (latestVersion.isPresent()) {
                String latestVersionNumber = latestVersion.get().getVersionNumber();
                version.setVersionNumber(incrementVersion(latestVersionNumber));
                version.setParentVersionId(latestVersion.get().getId());
            } else {
                version.setVersionNumber("1.0.0");
            }
        }
        
        if (version.getSnapshot() == null) {
            version.setSnapshot(new HashMap<>());
        }
        
        return versionRepository.save(version);
    }

    public OntologyVersion updateVersion(String id, OntologyVersion version) {
        Optional<OntologyVersion> optionalVersion = versionRepository.findById(id);
        if (optionalVersion.isPresent()) {
            OntologyVersion existingVersion = optionalVersion.get();
            existingVersion.setDescription(version.getDescription());
            return versionRepository.save(existingVersion);
        }
        return null;
    }

    public void deleteVersion(String id) {
        versionRepository.deleteById(id);
    }

    public OntologyVersion rollback(String ontologyId, String versionId) {
        Optional<OntologyVersion> targetVersion = versionRepository.findById(versionId);
        if (targetVersion.isPresent()) {
            OntologyVersion newVersion = new OntologyVersion();
            newVersion.setOntologyId(ontologyId);
            newVersion.setSnapshot(targetVersion.get().getSnapshot());
            newVersion.setDescription("Rollback to version " + targetVersion.get().getVersionNumber());
            newVersion.setCreatedBy(targetVersion.get().getCreatedBy());
            newVersion.setCreatedByName(targetVersion.get().getCreatedByName());
            return createVersion(newVersion);
        }
        return null;
    }

    public List<OntologyVersion> getVersionHistory(String ontologyId) {
        return versionRepository.findByOntologyIdOrderByCreatedAtDesc(ontologyId);
    }

    private String incrementVersion(String versionNumber) {
        try {
            String[] parts = versionNumber.split("\\.");
            if (parts.length == 3) {
                int major = Integer.parseInt(parts[0]);
                int minor = Integer.parseInt(parts[1]);
                int patch = Integer.parseInt(parts[2]);
                patch++;
                return major + "." + minor + "." + patch;
            }
        } catch (Exception e) {
            return versionNumber + ".1";
        }
        return versionNumber + ".1";
    }
}