package com.by.open.ontology.i18nservice.repository;

import com.by.open.ontology.i18nservice.entity.TranslationHistory;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface TranslationHistoryRepository extends MongoRepository<TranslationHistory, String> {
    List<TranslationHistory> findByEntityRefAndEntityType(String entityRef, String entityType);
    List<TranslationHistory> findByEntityRefAndEntityTypeAndLangTag(String entityRef, String entityType, String langTag);
    List<TranslationHistory> findByApprovalStatus(String approvalStatus);
    List<TranslationHistory> findByProposedBy(String proposedBy);
    List<TranslationHistory> findByConfirmedBy(String confirmedBy);
    List<TranslationHistory> findByProposedAtBetween(Date startDate, Date endDate);
    List<TranslationHistory> findByEntityRefAndEntityTypeAndProposedAtBetween(String entityRef, String entityType, Date startDate, Date endDate);
}
