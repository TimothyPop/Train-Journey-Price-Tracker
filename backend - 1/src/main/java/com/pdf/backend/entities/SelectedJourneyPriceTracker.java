package com.pdf.backend.entities;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDate;

@Data
@Entity
public class SelectedJourneyPriceTracker {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate date;
    private Double price;

    @ManyToOne()
    @JoinColumn(name = "saved_user_journey", nullable = false)
    private SavedUserJourney savedUserJourney;

}
