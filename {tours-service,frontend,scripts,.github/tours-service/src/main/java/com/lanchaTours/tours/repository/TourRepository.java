package com.lanchaTours.tours.repository;

import com.lanchaTours.tours.model.Tour;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Repository layer — pure data-access, no business logic.
 *
 * Micronaut Data generates the implementation at compile time via
 * annotation processing (no reflection, no runtime proxies).
 */
@Repository
public interface TourRepository extends JpaRepository<Tour, Long> {

    /**
     * Returns only active (non-deleted) tours, ordered newest first.
     */
    List<Tour> findByActiveTrueOrderByCreatedAtDesc();

    /**
     * Case-insensitive location filter.
     */
    List<Tour> findByLocationContainsIgnoreCaseAndActiveTrue(String location);
}
