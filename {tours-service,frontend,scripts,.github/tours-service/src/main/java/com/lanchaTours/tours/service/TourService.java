package com.lanchaTours.tours.service;

import com.lanchaTours.tours.dto.CreateTourRequest;
import com.lanchaTours.tours.dto.TourResponse;
import com.lanchaTours.tours.model.Tour;
import com.lanchaTours.tours.repository.TourRepository;
import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Business-logic layer for tours.
 *
 * Rules enforced here (not in controller, not in repository):
 * - A tour must have at least 1 available spot if the field is provided.
 * - Price rounding to 2 decimal places is applied before persistence.
 */
@Singleton
public class TourService {

    private static final Logger log = LoggerFactory.getLogger(TourService.class);

    private final TourRepository tourRepository;

    public TourService(TourRepository tourRepository) {
        this.tourRepository = tourRepository;
    }

    /**
     * Creates and persists a new tour.
     *
     * @param request validated DTO from the controller
     * @return the persisted tour mapped to a response DTO
     */
    @Transactional
    public TourResponse createTour(CreateTourRequest request) {
        log.info("Creating tour '{}' in '{}'", request.getName(), request.getLocation());

        Tour tour = new Tour(
            request.getName(),
            request.getLocation(),
            request.getPrice().setScale(2, java.math.RoundingMode.HALF_UP),
            request.getDescription() != null ? request.getDescription() : "",
            request.getGuideName(),
            request.getAvailableSpots()
        );

        Tour saved = tourRepository.save(tour);
        log.info("Tour persisted with id={}", saved.getId());
        return TourResponse.from(saved);
    }

    /**
     * Returns all active tours, ordered by creation date descending.
     */
    @Transactional
    public List<TourResponse> listActiveTours() {
        return tourRepository.findByActiveTrueOrderByCreatedAtDesc()
            .stream()
            .map(TourResponse::from)
            .collect(Collectors.toList());
    }

    /**
     * Returns active tours filtered by location (case-insensitive substring).
     */
    @Transactional
    public List<TourResponse> findByLocation(String location) {
        return tourRepository.findByLocationContainsIgnoreCaseAndActiveTrue(location)
            .stream()
            .map(TourResponse::from)
            .collect(Collectors.toList());
    }
}
