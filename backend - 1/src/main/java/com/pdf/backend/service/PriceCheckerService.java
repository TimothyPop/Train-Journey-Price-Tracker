package com.pdf.backend.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.pdf.backend.entities.RequestPayload;
import com.pdf.backend.entities.SavedUserJourney;
import com.pdf.backend.entities.SelectedJourneyPriceTracker;
import com.pdf.backend.repository.SavedUserJourneyPriceTrackerRepository;
import com.pdf.backend.repository.SavedUserJourneyRepository;
import com.pdf.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class PriceCheckerService {

    @Autowired
    private SavedUserJourneyRepository savedUserJourneyRepository;

    @Autowired
    private SavedUserJourneyPriceTrackerRepository savedUserJourneyPriceTrackerRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JavaMailSender mailSender;

    //        @Scheduled(fixedRate = 60000) // Run every 60 seconds (1 minute)
    public void checkAndNotify() throws JsonProcessingException {
        //System.out.println("hello");
        OffsetDateTime now = OffsetDateTime.now();
        String nowAsString = now.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        List<SavedUserJourney> futureJourneys = savedUserJourneyRepository.findByDateStartGreaterThan(nowAsString);

        for (SavedUserJourney sr : futureJourneys) {
            System.out.println(sr.getSelectedRoutes());
            RequestPayload requestPayload = new RequestPayload();

            // Setting up the request payload as per your logic
            RequestPayload.Location origin = new RequestPayload.Location();
            origin.setCrs(sr.getOriginCrs());
            origin.setGroup(false);

            RequestPayload.Location destination = new RequestPayload.Location();
            destination.setCrs(sr.getDesCrs());
            destination.setGroup(false);

            RequestPayload.OutwardTime outwardTime = new RequestPayload.OutwardTime();
            outwardTime.setTravelTime(sr.getSearchTime());
            outwardTime.setType("DEPART");

            RequestPayload.Passengers passengers = new RequestPayload.Passengers();
            passengers.setAdult(sr.getAdult());
            passengers.setChild(sr.getChild());

            RequestPayload.FareRequestDetails fareRequestDetails = new RequestPayload.FareRequestDetails();
            fareRequestDetails.setPassengers(passengers);
            fareRequestDetails.setFareClass("ANY");

            requestPayload.setOrigin(origin);
            requestPayload.setDestination(destination);
            requestPayload.setOutwardTime(outwardTime);
            requestPayload.setFareRequestDetails(fareRequestDetails);

            HttpHeaders headers = new HttpHeaders();
            headers.set("Content-Type", "application/json");
            String requestBody = objectMapper.writeValueAsString(requestPayload);
            System.out.println(requestBody);

            HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    "https://jpservices.nationalrail.co.uk/journey-planner",
                    HttpMethod.POST,
                    entity,
                    String.class
            );

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                try {
                    JsonNode rootNode = objectMapper.readTree(response.getBody());
                    JsonNode outwardJourneysNode = rootNode.get("outwardJourneys");

                    if (outwardJourneysNode != null && outwardJourneysNode.isArray()) {
                        ArrayNode filteredJourneys = objectMapper.createArrayNode();

                        for (JsonNode journey : outwardJourneysNode) {
                            if (journey.get("signature") != null && journey.get("signature").asText().equals(sr.getSelectedRoutes())) {
                                filteredJourneys.add(journey);
                                break;
                            }
                        }

                        ((ObjectNode) rootNode).set("outwardJourneys", filteredJourneys);
                        String modifiedResponseBody = objectMapper.writeValueAsString(rootNode);
                        System.out.println(rootNode.get("outwardJourneys").get(0).get("signature"));

                        JsonNode faresNode = rootNode.get("outwardJourneys").get(0).get("fares");

                        if (faresNode != null && faresNode.isArray() && !faresNode.isEmpty()) {
                            JsonNode lowestFare = faresNode.get(0);
                            for (JsonNode fare : faresNode) {
                                if (fare.get("totalPrice").asDouble() < lowestFare.get("totalPrice").asDouble()) {
                                    lowestFare = fare;
                                    System.out.println(fare.get("totalPrice").asDouble());
                                }
                            }

                            double lowestFarePrice = lowestFare.get("totalPrice").asDouble() / 100;
                            System.out.println(lowestFare.get("totalPrice"));

                            SelectedJourneyPriceTracker selectedJourneyPriceTracker = new SelectedJourneyPriceTracker();
                            selectedJourneyPriceTracker.setSavedUserJourney(sr);
                            selectedJourneyPriceTracker.setPrice(lowestFare.get("totalPrice").asDouble() / 100);
                            selectedJourneyPriceTracker.setDate(LocalDate.now());
                            savedUserJourneyPriceTrackerRepository.save(selectedJourneyPriceTracker);

                            if (lowestFarePrice <= sr.getAlertPrice()) {
                                System.out.println("Sending mail to: " + sr.getUser().getEmail());
                                String body = "Your train Journey " + sr.getName() + " Price is reduced from " + sr.getTotalLowestPrice() + " to " + lowestFare.get("totalPrice").asDouble() / 100;
                                this.sendSimpleEmail(sr.getUser().getEmail(), "Price Reduced", body);
                                sr.setTotalLowestPrice(lowestFarePrice);
                                savedUserJourneyRepository.save(sr);
                            }


                            // Update the totalLowestPrice in SavedUserJourney
                        }
                    }
                } catch (JsonProcessingException e) {
                    e.printStackTrace(); // Handle JSON processing exception
                }
            }
        }
    }

    public void sendSimpleEmail(String toEmail, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("sonnetrd@gmail.com"); // or use your configured username
        message.setTo(toEmail);
        message.setSubject(subject);
        message.setText(body);

        mailSender.send(message);
    }
}
