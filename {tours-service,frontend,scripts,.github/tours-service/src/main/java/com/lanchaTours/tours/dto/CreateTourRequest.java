package com.lanchaTours.tours.dto;

import io.micronaut.serde.annotation.Serdeable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

/**
 * DTO for POST /tours request body.
 * Decouples the HTTP layer from the domain model.
 */
@Serdeable
public class CreateTourRequest {

    @NotBlank(message = "El nombre del tour es obligatorio")
    @Size(max = 150, message = "El nombre no puede superar 150 caracteres")
    private String name;

    @NotBlank(message = "La ubicación es obligatoria")
    @Size(max = 200, message = "La ubicación no puede superar 200 caracteres")
    private String location;

    @NotNull(message = "El precio es obligatorio")
    @Positive(message = "El precio debe ser un valor positivo")
    private BigDecimal price;

    @Size(max = 500, message = "La descripción no puede superar 500 caracteres")
    private String description;

    @NotBlank(message = "El nombre del guía es obligatorio")
    @Size(max = 100, message = "El nombre del guía no puede superar 100 caracteres")
    private String guideName;

    @Positive(message = "Los cupos disponibles deben ser un valor positivo")
    private Integer availableSpots;

    // ── Getters & Setters ─────────────────────────────────────────────────────

    public String getName()                   { return name; }
    public void setName(String name)          { this.name = name; }

    public String getLocation()               { return location; }
    public void setLocation(String location)  { this.location = location; }

    public BigDecimal getPrice()              { return price; }
    public void setPrice(BigDecimal price)    { this.price = price; }

    public String getDescription()                 { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getGuideName()                   { return guideName; }
    public void setGuideName(String guideName)      { this.guideName = guideName; }

    public Integer getAvailableSpots()             { return availableSpots; }
    public void setAvailableSpots(Integer spots)   { this.availableSpots = spots; }
}
