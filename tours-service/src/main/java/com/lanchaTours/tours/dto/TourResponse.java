package com.lanchaTours.tours.dto;

import com.lanchaTours.tours.model.Tour;
import io.micronaut.serde.annotation.Serdeable;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO for outbound tour responses.
 * Exposes only what consumers need — never the raw entity.
 */
@Serdeable
public class TourResponse {

    private Long id;
    private String name;
    private String location;
    private BigDecimal price;
    private String description;
    private String guideName;
    private Integer availableSpots;
    private LocalDateTime createdAt;
    private Boolean active;

    public TourResponse() {}

    /** Factory method — maps entity → DTO. */
    public static TourResponse from(Tour tour) {
        TourResponse dto = new TourResponse();
        dto.id             = tour.getId();
        dto.name           = tour.getName();
        dto.location       = tour.getLocation();
        dto.price          = tour.getPrice();
        dto.description    = tour.getDescription();
        dto.guideName      = tour.getGuideName();
        dto.availableSpots = tour.getAvailableSpots();
        dto.createdAt      = tour.getCreatedAt();
        dto.active         = tour.getActive();
        return dto;
    }

    // ── Getters & Setters ─────────────────────────────────────────────────────

    public Long getId()                           { return id; }
    public void setId(Long id)                    { this.id = id; }

    public String getName()                       { return name; }
    public void setName(String name)              { this.name = name; }

    public String getLocation()                   { return location; }
    public void setLocation(String location)      { this.location = location; }

    public BigDecimal getPrice()                  { return price; }
    public void setPrice(BigDecimal price)        { this.price = price; }

    public String getDescription()                { return description; }
    public void setDescription(String description){ this.description = description; }

    public String getGuideName()                  { return guideName; }
    public void setGuideName(String guideName)    { this.guideName = guideName; }

    public Integer getAvailableSpots()            { return availableSpots; }
    public void setAvailableSpots(Integer spots)  { this.availableSpots = spots; }

    public LocalDateTime getCreatedAt()           { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public Boolean getActive()                    { return active; }
    public void setActive(Boolean active)         { this.active = active; }
}
