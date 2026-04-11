package com.lanchaTours.tours.service;

import com.lanchaTours.tours.dto.CreateTourRequest;
import com.lanchaTours.tours.dto.TourResponse;
import com.lanchaTours.tours.model.Tour;
import com.lanchaTours.tours.repository.TourRepository;
import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

@Singleton
public class TourService {

    private final TourRepository repo;

    public TourService(TourRepository repo) {
        this.repo = repo;
    }

    @Transactional
    public TourResponse create(CreateTourRequest req) {
        Tour tour = new Tour(
            req.getName(),
            req.getLocation(),
            req.getPrice().setScale(2, RoundingMode.HALF_UP),
            req.getGuideName() != null ? req.getGuideName() : "",
            req.getDescription() != null ? req.getDescription() : "",
            req.getAvailableSpots()
        );
        return TourResponse.from(repo.save(tour));
    }

    @Transactional
    public List<TourResponse> listAll() {
        return repo.findByActiveTrueOrderByCreatedAtDesc()
            .stream().map(TourResponse::from).collect(Collectors.toList());
    }

    @Transactional
    public List<TourResponse> findByLocation(String location) {
        return repo.findByLocationContainsIgnoreCaseAndActiveTrue(location)
            .stream().map(TourResponse::from).collect(Collectors.toList());
    }
}
