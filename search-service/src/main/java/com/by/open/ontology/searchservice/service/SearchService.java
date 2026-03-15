package com.by.open.ontology.searchservice.service;

import com.by.open.ontology.searchservice.entity.SearchIndex;
import com.by.open.ontology.searchservice.entity.SearchResult;
import com.by.open.ontology.searchservice.repository.SearchIndexRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SearchService {

    @Autowired
    private SearchIndexRepository indexRepository;

    public List<SearchResult> search(String query, String ontologyId, String entityType) {
        List<SearchIndex> indices = new ArrayList<>();
        
        if (ontologyId != null && !ontologyId.isEmpty()) {
            indices = indexRepository.findByOntologyId(ontologyId);
            if (entityType != null && !entityType.isEmpty()) {
                indices = indices.stream()
                    .filter(idx -> entityType.equals(idx.getEntityType()))
                    .collect(Collectors.toList());
            }
        } else if (entityType != null && !entityType.isEmpty()) {
            indices = indexRepository.findByEntityType(entityType);
        } else {
            indices = indexRepository.findAll();
        }
        
        return indices.stream()
            .filter(idx -> matchesQuery(idx, query))
            .map(this::convertToSearchResult)
            .collect(Collectors.toList());
    }

    public List<SearchResult> searchByName(String name) {
        List<SearchIndex> indices = indexRepository.findByNameContainingIgnoreCase(name);
        return indices.stream()
            .map(this::convertToSearchResult)
            .collect(Collectors.toList());
    }

    public List<SearchResult> searchByIri(String iri) {
        List<SearchIndex> indices = indexRepository.findByIriContainingIgnoreCase(iri);
        return indices.stream()
            .map(this::convertToSearchResult)
            .collect(Collectors.toList());
    }

    public SearchIndex indexEntity(SearchIndex index) {
        index.setIndexedAt(LocalDateTime.now());
        return indexRepository.save(index);
    }

    public SearchIndex updateIndex(String entityId, SearchIndex index) {
        Optional<SearchIndex> existingIndex = indexRepository.findByEntityId(entityId);
        if (existingIndex.isPresent()) {
            SearchIndex toUpdate = existingIndex.get();
            toUpdate.setName(index.getName());
            toUpdate.setIri(index.getIri());
            toUpdate.setDescription(index.getDescription());
            toUpdate.setContent(index.getContent());
            toUpdate.setIndexedAt(LocalDateTime.now());
            return indexRepository.save(toUpdate);
        }
        return indexEntity(index);
    }

    public void deleteIndex(String entityId) {
        indexRepository.deleteByEntityId(entityId);
    }

    public void deleteIndexesByOntologyId(String ontologyId) {
        indexRepository.deleteByOntologyId(ontologyId);
    }

    public List<SearchIndex> getIndexesByOntologyId(String ontologyId) {
        return indexRepository.findByOntologyId(ontologyId);
    }

    private boolean matchesQuery(SearchIndex index, String query) {
        if (query == null || query.isEmpty()) {
            return true;
        }
        String lowerQuery = query.toLowerCase();
        return (index.getName() != null && index.getName().toLowerCase().contains(lowerQuery)) ||
               (index.getIri() != null && index.getIri().toLowerCase().contains(lowerQuery)) ||
               (index.getDescription() != null && index.getDescription().toLowerCase().contains(lowerQuery)) ||
               (index.getContent() != null && index.getContent().toLowerCase().contains(lowerQuery));
    }

    private SearchResult convertToSearchResult(SearchIndex index) {
        SearchResult result = new SearchResult();
        result.setEntityId(index.getEntityId());
        result.setEntityType(index.getEntityType());
        result.setOntologyId(index.getOntologyId());
        result.setName(index.getName());
        result.setIri(index.getIri());
        result.setDescription(index.getDescription());
        result.setScore(1.0);
        result.setHighlights(new ArrayList<>());
        return result;
    }
}