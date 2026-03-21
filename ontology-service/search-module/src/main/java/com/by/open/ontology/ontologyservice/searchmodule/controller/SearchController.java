package com.by.open.ontology.ontologyservice.searchmodule.controller;

import com.by.open.ontology.ontologyservice.searchmodule.entity.SearchIndex;
import com.by.open.ontology.ontologyservice.searchmodule.entity.SearchResult;
import com.by.open.ontology.ontologyservice.searchmodule.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/search")
@CrossOrigin(origins = "*")
public class SearchController {

    @Autowired
    private SearchService searchService;

    @GetMapping("/query")
    public List<SearchResult> search(
            @RequestParam String query,
            @RequestParam(required = false) String ontologyId,
            @RequestParam(required = false) String entityType) {
        return searchService.search(query, ontologyId, entityType);
    }

    @GetMapping("/byname")
    public List<SearchResult> searchByName(@RequestParam String name) {
        return searchService.searchByName(name);
    }

    @GetMapping("/byiri")
    public List<SearchResult> searchByIri(@RequestParam String iri) {
        return searchService.searchByIri(iri);
    }

    @PostMapping("/index")
    public SearchIndex indexEntity(@RequestBody SearchIndex index) {
        return searchService.indexEntity(index);
    }

    @PutMapping("/index/{entityId}")
    public ResponseEntity<SearchIndex> updateIndex(@PathVariable String entityId, @RequestBody SearchIndex index) {
        SearchIndex updatedIndex = searchService.updateIndex(entityId, index);
        return ResponseEntity.ok(updatedIndex);
    }

    @DeleteMapping("/index/{entityId}")
    public ResponseEntity<Void> deleteIndex(@PathVariable String entityId) {
        searchService.deleteIndex(entityId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/index/ontology/{ontologyId}")
    public ResponseEntity<Void> deleteIndexesByOntologyId(@PathVariable String ontologyId) {
        searchService.deleteIndexesByOntologyId(ontologyId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/index/ontology/{ontologyId}")
    public List<SearchIndex> getIndexesByOntologyId(@PathVariable String ontologyId) {
        return searchService.getIndexesByOntologyId(ontologyId);
    }
}
