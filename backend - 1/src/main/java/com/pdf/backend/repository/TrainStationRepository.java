package com.pdf.backend.repository;


import com.pdf.backend.entities.TrainStation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TrainStationRepository extends JpaRepository<TrainStation, Long> {
    List<TrainStation> findByStationNameStartingWithIgnoreCase(String stationName);
}