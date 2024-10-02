package com.pdf.backend.repository;

import com.pdf.backend.entities.SavedUserJourney;
import com.pdf.backend.entities.SelectedJourneyPriceTracker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SavedUserJourneyPriceTrackerRepository extends JpaRepository<SelectedJourneyPriceTracker, Long> {
}