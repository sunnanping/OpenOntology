package com.by.open.ontology.collaborationservice.service;

import com.by.open.ontology.collaborationservice.entity.CollaborationSession;
import com.by.open.ontology.collaborationservice.repository.CollaborationSessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CollaborationService {

    @Autowired
    private CollaborationSessionRepository sessionRepository;

    public List<CollaborationSession> findAllSessions() {
        return sessionRepository.findAll();
    }

    public Optional<CollaborationSession> findSessionById(String id) {
        return sessionRepository.findById(id);
    }

    public List<CollaborationSession> findSessionsByOntologyId(String ontologyId) {
        return sessionRepository.findByOntologyId(ontologyId);
    }

    public CollaborationSession createSession(CollaborationSession session) {
        session.setCreatedAt(LocalDateTime.now());
        session.setUpdatedAt(LocalDateTime.now());
        session.setStatus("ACTIVE");
        if (session.getParticipants() == null) {
            session.setParticipants(new ArrayList<>());
        }
        return sessionRepository.save(session);
    }

    public CollaborationSession updateSession(String id, CollaborationSession session) {
        Optional<CollaborationSession> optionalSession = sessionRepository.findById(id);
        if (optionalSession.isPresent()) {
            CollaborationSession existingSession = optionalSession.get();
            existingSession.setName(session.getName());
            existingSession.setUpdatedAt(LocalDateTime.now());
            return sessionRepository.save(existingSession);
        }
        return null;
    }

    public CollaborationSession addParticipant(String sessionId, String userId) {
        Optional<CollaborationSession> optionalSession = sessionRepository.findById(sessionId);
        if (optionalSession.isPresent()) {
            CollaborationSession session = optionalSession.get();
            if (session.getParticipants() == null) {
                session.setParticipants(new ArrayList<>());
            }
            if (!session.getParticipants().contains(userId)) {
                session.getParticipants().add(userId);
                session.setUpdatedAt(LocalDateTime.now());
                return sessionRepository.save(session);
            }
            return session;
        }
        return null;
    }

    public CollaborationSession removeParticipant(String sessionId, String userId) {
        Optional<CollaborationSession> optionalSession = sessionRepository.findById(sessionId);
        if (optionalSession.isPresent()) {
            CollaborationSession session = optionalSession.get();
            if (session.getParticipants() != null) {
                session.getParticipants().remove(userId);
                session.setUpdatedAt(LocalDateTime.now());
                return sessionRepository.save(session);
            }
            return session;
        }
        return null;
    }

    public void deleteSession(String id) {
        sessionRepository.deleteById(id);
    }

    public CollaborationSession endSession(String id) {
        Optional<CollaborationSession> optionalSession = sessionRepository.findById(id);
        if (optionalSession.isPresent()) {
            CollaborationSession session = optionalSession.get();
            session.setStatus("ENDED");
            session.setUpdatedAt(LocalDateTime.now());
            return sessionRepository.save(session);
        }
        return null;
    }
}