package com.by.open.ontology.adminservice.repository;

import com.by.open.ontology.adminservice.entity.Admin;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminRepository extends MongoRepository<Admin, String> {
    Admin findByUsername(String username);
    Admin findByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}