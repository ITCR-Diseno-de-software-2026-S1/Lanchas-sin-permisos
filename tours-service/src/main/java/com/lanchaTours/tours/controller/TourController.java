package com.lanchaTours.tours.controller;

import com.lanchaTours.tours.dto.CreateTourRequest;
import com.lanchaTours.tours.dto.TourResponse;
import com.lanchaTours.tours.service.TourService;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.*;
import io.micronaut.validation.Validated;
import jakarta.validation.Valid;
import java.util.List;

@Controller("/tours")
@Validated
public class TourController {

    private final TourService service;

    public TourController(TourService service) {
        this.service = service;
    }

    /** POST /tours — crea un tour nuevo */
    @Post
    public HttpResponse<TourResponse> create(@Body @Valid CreateTourRequest req) {
        return HttpResponse.created(service.create(req));
    }

    /** GET /tours[?location=x] — lista tours activos */
    @Get
    public HttpResponse<List<TourResponse>> list(
            @QueryValue(defaultValue = "") String location) {
        List<TourResponse> result = location.isBlank()
            ? service.listAll()
            : service.findByLocation(location);
        return HttpResponse.ok(result);
    }
}
