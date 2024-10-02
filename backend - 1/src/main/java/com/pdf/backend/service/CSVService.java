package com.pdf.backend.service;

import com.pdf.backend.entities.TrainStation;
import com.pdf.backend.repository.TrainStationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
public class CSVService {

    @Autowired
    private TrainStationRepository repository;

    public void save(MultipartFile file) {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            List<TrainStation> stations = new ArrayList<>();

            // Skip the header
            br.readLine();

            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");

                TrainStation station = new TrainStation();
                station.setStationName(values[0]);
                station.setLatitude(Double.parseDouble(values[1]));
                station.setLongitude(Double.parseDouble(values[2]));
                station.setCrsCode(values[3]);
                stations.add(station);
            }

            repository.saveAll(stations);
        } catch (Exception e) {
            throw new RuntimeException("Failed to store CSV data: " + e.getMessage());
        }
    }

    public List<TrainStation> findStationsByPartialName(String partialName) {
        return repository.findByStationNameStartingWithIgnoreCase(partialName);
    }
}
