package com.pdf.backend.repository;

import com.pdf.backend.entities.SavedUserJourney;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;

@Repository
public interface SavedUserJourneyRepository extends JpaRepository<SavedUserJourney, Long> {
    SavedUserJourney findBySelectedRoutes(String signature);

    List<SavedUserJourney> findByDateStartGreaterThan(String dateStart);
}