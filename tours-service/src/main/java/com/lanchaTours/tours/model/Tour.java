package com.lanchaTours.tours.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "tours")
public class Tour {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String location;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "guide_name")
    private String guideName;

    @Column(length = 500)
    private String description;

    @Column(name = "available_spots")
    private Integer availableSpots;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private Boolean active = true;

    @PrePersist
    void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public Tour() {}

    public Tour(String name, String location, BigDecimal price,
                String guideName, String description, Integer availableSpots) {
        this.name = name;
        this.location = location;
        this.price = price;
        this.guideName = guideName;
        this.description = description;
        this.availableSpots = availableSpots;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public String getGuideName() { return guideName; }
    public void setGuideName(String guideName) { this.guideName = guideName; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Integer getAvailableSpots() { return availableSpots; }
    public void setAvailableSpots(Integer availableSpots) { this.availableSpots = availableSpots; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }
}
