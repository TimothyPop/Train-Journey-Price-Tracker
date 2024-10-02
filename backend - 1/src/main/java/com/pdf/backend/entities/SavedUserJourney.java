package com.pdf.backend.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.Set;

@Data
@Entity
@Table(name = "saved_user_journey")
public class SavedUserJourney {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String origin;

    @Column(nullable = false)
    private String destination;

    @Column(nullable = false, length = 3)
    private String originCrs;

    @Column(nullable = false, length = 3)
    private String desCrs;

    @Column(nullable = false)
    private String type;

    @Column(nullable = false)
    private int child;

    @Column(nullable = false)
    private int adult;

    @Column(nullable = false)
    private String dateStart;

    @Column(nullable = false)
    private String dateTime;

    @Column(nullable = false)
    private String searchTime;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String selectedRoutes;

    @Column(nullable = false)
    private double alertPrice;

    @Column(nullable = false)
    private double totalLowestPrice;

    @Column(nullable = false)
    private String duration;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @ManyToOne()
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @JsonIgnore
    @OneToMany(mappedBy = "savedUserJourney", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<SelectedJourneyPriceTracker> selectedJourneyPriceTrackers;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public SavedUserJourney() {
    }

}
