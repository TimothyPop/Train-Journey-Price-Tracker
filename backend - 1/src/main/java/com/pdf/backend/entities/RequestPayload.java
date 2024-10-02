package com.pdf.backend.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Collections;

@Data
public class RequestPayload {

    private Location origin;
    private Location destination;
    private OutwardTime outwardTime;

    @JsonProperty("fareRequestDetails")
    private FareRequestDetails fareRequestDetails;

    @Data
    public static class Location {
        private String crs;
        private boolean group;
    }

    @Data
    public static class OutwardTime {
        private String travelTime;
        private String type; // 'DEPART' or 'ARRIVE'
    }

    @Data
    public static class FareRequestDetails {
        private Passengers passengers;
        private String fareClass;
        private Object railcards = Collections.emptyList(); // Assuming it's an empty list
    }

    @Data
    public static class Passengers {
        private int adult;
        private int child;
    }
}
