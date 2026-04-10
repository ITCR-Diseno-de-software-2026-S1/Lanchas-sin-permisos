package com.lanchaTours.tours.controller;

import com.lanchaTours.tours.dto.CreateTourRequest;
import com.lanchaTours.tours.dto.TourResponse;
import com.lanchaTours.tours.service.TourService;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.*;
import io.micronaut.validation.Validated;
import jakarta.validation.Valid;

import java.util.List;

/**
 * REST controller for the Tours microservice.
 *
 * Responsibilities:
 *  - HTTP binding only (routes, status codes, response wrapping)
 *  - Delegates ALL business logic to TourService
 *  - Never accesses the repository directly
 */
@Controller("/tours")
@Validated
public class TourController {

    private final TourService tourService;

    public TourController(TourService tourService) {
        this.tourService = tourService;
    }

    /**
     * POST /tours
     * Creates a new boat tour.
     * Returns HTTP 201 Created with the persisted tour in the body.
     */
    @Post
    public HttpResponse<TourResponse> createTour(@Body @Valid CreateTourRequest request) {
        TourResponse created = tourService.createTour(request);
        return HttpResponse.created(created);
    }

    /**
     * GET /tours
     * Lists all active tours. Optionally filtered by ?location=<keyword>.
     */
    @Get
    public HttpResponse<List<TourResponse>> listTours(
            @QueryValue(defaultValue = "") String location) {

        List<TourResponse> tours = location.isBlank()
            ? tourService.listActiveTours()
            : tourService.findByLocation(location);

        return HttpResponse.ok(tours);
    }
}
