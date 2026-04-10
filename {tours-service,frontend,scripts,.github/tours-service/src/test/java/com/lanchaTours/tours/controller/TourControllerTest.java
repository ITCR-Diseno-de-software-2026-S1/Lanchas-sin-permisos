package com.lanchaTours.tours.controller;

import com.lanchaTours.tours.dto.CreateTourRequest;
import com.lanchaTours.tours.dto.TourResponse;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@MicronautTest
class TourControllerTest {

    @Inject
    @Client("/")
    HttpClient client;

    @Test
    void testCreateTour_returnsCreated() {
        CreateTourRequest request = buildRequest("Tour Isla Tortuga", "Puntarenas", new BigDecimal("45.00"));

        HttpResponse<TourResponse> response = client.toBlocking()
            .exchange(HttpRequest.POST("/tours", request), TourResponse.class);

        assertEquals(HttpStatus.CREATED, response.getStatus());
        TourResponse body = response.body();
        assertNotNull(body);
        assertNotNull(body.getId());
        assertEquals("Tour Isla Tortuga", body.getName());
        assertEquals("Puntarenas", body.getLocation());
        assertEquals(new BigDecimal("45.00"), body.getPrice());
        assertTrue(body.getActive());
    }

    @Test
    void testListTours_returnsOk() {
        // create one first
        client.toBlocking()
            .exchange(HttpRequest.POST("/tours", buildRequest("Laguna Arenal", "Alajuela", new BigDecimal("60.00"))), TourResponse.class);

        HttpResponse<List> response = client.toBlocking()
            .exchange(HttpRequest.GET("/tours"), List.class);

        assertEquals(HttpStatus.OK, response.getStatus());
        assertNotNull(response.body());
        assertFalse(response.body().isEmpty());
    }

    @Test
    void testListTours_filterByLocation() {
        client.toBlocking()
            .exchange(HttpRequest.POST("/tours", buildRequest("Manglares del Sur", "Quepos", new BigDecimal("35.00"))), TourResponse.class);

        HttpResponse<List> response = client.toBlocking()
            .exchange(HttpRequest.GET("/tours?location=quepos"), List.class);

        assertEquals(HttpStatus.OK, response.getStatus());
        assertFalse(response.body().isEmpty());
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private CreateTourRequest buildRequest(String name, String location, BigDecimal price) {
        CreateTourRequest r = new CreateTourRequest();
        r.setName(name);
        r.setLocation(location);
        r.setPrice(price);
        r.setDescription("Tour local en lancha");
        r.setGuideName("Don Carlos");
        r.setAvailableSpots(10);
        return r;
    }
}
