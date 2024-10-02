package com.pdf.backend.controllers;

import com.pdf.backend.entities.TrainStation;
import com.pdf.backend.service.CSVService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/csv")
public class CSVController {

    @Autowired
    private CSVService csvService;

    @Autowired
    private RestTemplate restTemplate;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Please upload a CSV file.");
        }

        try {
            csvService.save(file);
            return ResponseEntity.status(HttpStatus.OK).body("File uploaded and data saved successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload and process the file: " + e.getMessage());
        }
    }

    @GetMapping("/stationPicker/{partialName}")
    public List<TrainStation> getStationsByPartialName(@PathVariable String partialName) {
        return csvService.findStationsByPartialName(partialName);
    }

    @PostMapping("/journey-planner")
    @CrossOrigin(origins = "*") // Adjust CORS settings as needed
    public ResponseEntity<String> proxyPost(
            @RequestBody String requestBody) {

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");

        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                "https://jpservices.nationalrail.co.uk/journey-planner",
                HttpMethod.POST,
                entity,
                String.class
        );

        return ResponseEntity
                .status(response.getStatusCode())
                .body(response.getBody());
    }
}
