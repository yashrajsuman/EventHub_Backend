package com.eventhub.event;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import java.time.LocalDateTime;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String description;
    private String venue;
    private LocalDateTime startsAt;
    private Integer capacity;
    private Integer numberOfDays;
    private BigDecimal dailyPay;
    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<Registration> registrations = new ArrayList<>();

    protected Event() { }

    public Event(String title, String description, String venue, LocalDateTime startsAt, Integer capacity, Integer numberOfDays, BigDecimal dailyPay) {
        this.title = title; this.description = description; this.venue = venue;
        this.startsAt = startsAt; this.capacity = capacity; this.numberOfDays = numberOfDays; this.dailyPay = dailyPay;
    }
    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getVenue() { return venue; }
    public LocalDateTime getStartsAt() { return startsAt; }
    public Integer getCapacity() { return capacity; }
    public Integer getNumberOfDays() { return numberOfDays; }
    public BigDecimal getDailyPay() { return dailyPay; }
    public List<Registration> getRegistrations() { return registrations; }
    public void addRegistration(Registration registration) {
        registration.setEvent(this);
        registrations.add(registration);
    }
    public void update(String title, String description, String venue, LocalDateTime startsAt, Integer capacity, Integer numberOfDays, BigDecimal dailyPay) {
        this.title = title; this.description = description; this.venue = venue; this.startsAt = startsAt;
        this.capacity = capacity; this.numberOfDays = numberOfDays; this.dailyPay = dailyPay;
    }
}
