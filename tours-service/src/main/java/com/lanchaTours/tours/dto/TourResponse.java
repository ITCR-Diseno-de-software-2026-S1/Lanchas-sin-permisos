package com.lanchaTours.tours.dto;

import com.lanchaTours.tours.model.Tour;
import io.micronaut.serde.annotation.Serdeable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Serdeable
public class TourResponse {

    private Long id;
    private String name;
    private String location;
    private BigDecimal price;
    private String guideName;
    private String description;
    private Integer availableSpots;
    private LocalDateTime createdAt;
    private Boolean active;

    public TourResponse() {}

    public static TourResponse from(Tour t) {
        TourResponse r = new TourResponse();
        r.id = t.getId();
        r.name = t.getName();
        r.location = t.getLocation();
        r.price = t.getPrice();
        r.guideName = t.getGuideName();
        r.description = t.getDescription();
        r.availableSpots = t.getAvailableSpots();
        r.createdAt = t.getCreatedAt();
        r.active = t.getActive();
        return r;
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
    public void setAvailableSpots(Integer s) { this.availableSpots = s; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }
}
