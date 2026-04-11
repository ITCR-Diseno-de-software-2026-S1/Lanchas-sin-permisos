package com.lanchaTours.tours.dto;

import io.micronaut.serde.annotation.Serdeable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

@Serdeable
public class CreateTourRequest {

    @NotBlank
    private String name;

    @NotBlank
    private String location;

    @NotNull
    @Positive
    private BigDecimal price;

    private String guideName;
    private String description;
    private Integer availableSpots;

    public CreateTourRequest() {}

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
}
