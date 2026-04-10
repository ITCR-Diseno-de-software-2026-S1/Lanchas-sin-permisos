package com.lanchaTours.tours.service;

import com.lanchaTours.tours.dto.CreateTourRequest;
import com.lanchaTours.tours.dto.TourResponse;
import com.lanchaTours.tours.model.Tour;
import com.lanchaTours.tours.repository.TourRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TourServiceTest {

    @Mock
    TourRepository tourRepository;

    @InjectMocks
    TourService tourService;

    private Tour sampleTour;

    @BeforeEach
    void setUp() {
        sampleTour = new Tour("Isla del Coco", "Puntarenas", new BigDecimal("120.00"),
            "Expedición submarina", "Don Jorge", 8);
        sampleTour.setId(1L);
        sampleTour.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void createTour_persistsAndReturnsDto() {
        when(tourRepository.save(any(Tour.class))).thenReturn(sampleTour);

        CreateTourRequest req = new CreateTourRequest();
        req.setName("Isla del Coco");
        req.setLocation("Puntarenas");
        req.setPrice(new BigDecimal("120.00"));
        req.setGuideName("Don Jorge");
        req.setAvailableSpots(8);

        TourResponse result = tourService.createTour(req);

        assertNotNull(result);
        assertEquals("Isla del Coco", result.getName());
        assertEquals("Puntarenas", result.getLocation());
        verify(tourRepository, times(1)).save(any(Tour.class));
    }

    @Test
    void listActiveTours_returnsAllActive() {
        when(tourRepository.findByActiveTrueOrderByCreatedAtDesc())
            .thenReturn(List.of(sampleTour));

        List<TourResponse> results = tourService.listActiveTours();

        assertEquals(1, results.size());
        assertEquals("Isla del Coco", results.get(0).getName());
    }

    @Test
    void findByLocation_filtersCorrectly() {
        when(tourRepository.findByLocationContainsIgnoreCaseAndActiveTrue("Puntarenas"))
            .thenReturn(List.of(sampleTour));

        List<TourResponse> results = tourService.findByLocation("Puntarenas");

        assertEquals(1, results.size());
        assertEquals("Puntarenas", results.get(0).getLocation());
    }

    @Test
    void createTour_priceIsRoundedToTwoDecimals() {
        Tour roundedTour = new Tour("Tour", "Loc", new BigDecimal("45.00"), "", "Guia", 5);
        roundedTour.setId(2L);
        roundedTour.setCreatedAt(LocalDateTime.now());
        when(tourRepository.save(any(Tour.class))).thenReturn(roundedTour);

        CreateTourRequest req = new CreateTourRequest();
        req.setName("Tour");
        req.setLocation("Loc");
        req.setPrice(new BigDecimal("44.999"));
        req.setGuideName("Guia");

        TourResponse result = tourService.createTour(req);
        assertNotNull(result);
        verify(tourRepository).save(argThat(t ->
            t.getPrice().scale() == 2
        ));
    }
}
