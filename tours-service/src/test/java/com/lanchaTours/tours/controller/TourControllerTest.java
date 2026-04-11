package com.lanchaTours.tours.controller;

import com.lanchaTours.tours.dto.CreateTourRequest;
import com.lanchaTours.tours.dto.TourResponse;
import io.micronaut.core.type.Argument;
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
        CreateTourRequest req = req("Isla Tortuga", "Puntarenas", new BigDecimal("45.00"));
        HttpResponse<TourResponse> res = client.toBlocking()
            .exchange(HttpRequest.POST("/tours", req), TourResponse.class);
        assertEquals(HttpStatus.CREATED, res.getStatus());
        assertNotNull(res.body().getId());
        assertEquals("Isla Tortuga", res.body().getName());
    }

    @Test
    void testListTours_returnsOk() {
        client.toBlocking().exchange(
            HttpRequest.POST("/tours", req("Laguna Arenal", "Alajuela", new BigDecimal("60.00"))),
            TourResponse.class);
        HttpResponse<List<TourResponse>> res = client.toBlocking()
            .exchange(HttpRequest.GET("/tours"), Argument.listOf(TourResponse.class));
        assertEquals(HttpStatus.OK, res.getStatus());
        assertFalse(res.body().isEmpty());
    }

    @Test
    void testListTours_filterByLocation() {
        client.toBlocking().exchange(
            HttpRequest.POST("/tours", req("Manglares Quepos", "Quepos", new BigDecimal("35.00"))),
            TourResponse.class);
        HttpResponse<List<TourResponse>> res = client.toBlocking()
            .exchange(HttpRequest.GET("/tours?location=quepos"), Argument.listOf(TourResponse.class));
        assertEquals(HttpStatus.OK, res.getStatus());
        assertFalse(res.body().isEmpty());
    }

    private CreateTourRequest req(String name, String location, BigDecimal price) {
        CreateTourRequest r = new CreateTourRequest();
        r.setName(name);
        r.setLocation(location);
        r.setPrice(price);
        r.setGuideName("Guía Local");
        r.setAvailableSpots(10);
        return r;
    }
}
