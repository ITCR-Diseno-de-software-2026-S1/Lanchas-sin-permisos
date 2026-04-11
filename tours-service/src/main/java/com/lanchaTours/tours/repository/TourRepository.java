package com.lanchaTours.tours.repository;

import com.lanchaTours.tours.model.Tour;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.jpa.repository.JpaRepository;
import java.util.List;

@Repository
public interface TourRepository extends JpaRepository<Tour, Long> {

    List<Tour> findByActiveTrueOrderByCreatedAtDesc();

    List<Tour> findByLocationContainsIgnoreCaseAndActiveTrue(String location);
}
