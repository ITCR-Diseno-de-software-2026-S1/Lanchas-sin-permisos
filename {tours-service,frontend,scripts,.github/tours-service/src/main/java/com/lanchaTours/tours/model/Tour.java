package com.lanchaTours.tours.model;

import io.micronaut.serde.annotation.Serdeable;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Tour entity — represents a boat tour published by a local guide.
 */
@Entity
@Table(name = "tours")
@Serdeable
public class Tour {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false, length = 150)
    private String name;

    @NotBlank
    @Column(nullable = false, length = 200)
    private String location;

    @NotNull
    @Positive
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(nullable = false, length = 500)
    private String description;

    @Column(name = "guide_name", nullable = false, length = 100)
    private String guideName;

    @Column(name = "available_spots")
    private Integer availableSpots;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "active", nullable = false)
    private Boolean active = true;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    // ── Constructors ──────────────────────────────────────────────────────────

    public Tour() {}

    public Tour(String name, String location, BigDecimal price,
                String description, String guideName, Integer availableSpots) {
        this.name           = name;
        this.location       = location;
        this.price          = price;
        this.description    = description;
        this.guideName      = guideName;
        this.availableSpots = availableSpots;
        this.active         = true;
    }

    // ── Getters & Setters ─────────────────────────────────────────────────────

    public Long getId()                       { return id; }
    public void setId(Long id)                { this.id = id; }

    public String getName()                   { return name; }
    public void setName(String name)          { this.name = name; }

    public String getLocation()               { return location; }
    public void setLocation(String location)  { this.location = location; }

    public BigDecimal getPrice()              { return price; }
    public void setPrice(BigDecimal price)    { this.price = price; }

    public String getDescription()                    { return description; }
    public void setDescription(String description)    { this.description = description; }

    public String getGuideName()                      { return guideName; }
    public void setGuideName(String guideName)        { this.guideName = guideName; }

    public Integer getAvailableSpots()                { return availableSpots; }
    public void setAvailableSpots(Integer spots)      { this.availableSpots = spots; }

    public LocalDateTime getCreatedAt()               { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public Boolean getActive()                { return active; }
    public void setActive(Boolean active)     { this.active = active; }
}
