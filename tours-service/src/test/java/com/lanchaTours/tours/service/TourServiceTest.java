package com.lanchaTours.tours.service;

import com.lanchaTours.tours.dto.CreateTourRequest;
import com.lanchaTours.tours.dto.TourResponse;
import com.lanchaTours.tours.model.Tour;
import com.lanchaTours.tours.repository.TourRepository;
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

    @Mock TourRepository repo;
    @InjectMocks TourService service;

    private Tour sampleTour() {
        Tour t = new Tour("Tour Coco", "Puntarenas", new BigDecimal("120.00"), "Don Jorge", "Tour submarino", 8);
        t.setId(1L);
        t.setCreatedAt(LocalDateTime.now());
        return t;
    }

    @Test
    void create_persistsAndReturnsDto() {
        when(repo.save(any(Tour.class))).thenReturn(sampleTour());
        CreateTourRequest req = new CreateTourRequest();
        req.setName("Tour Coco");
        req.setLocation("Puntarenas");
        req.setPrice(new BigDecimal("120.00"));
        req.setGuideName("Don Jorge");

        TourResponse result = service.create(req);

        assertNotNull(result);
        assertEquals("Tour Coco", result.getName());
        verify(repo).save(any(Tour.class));
    }

    @Test
    void listAll_returnsActiveTours() {
        when(repo.findByActiveTrueOrderByCreatedAtDesc()).thenReturn(List.of(sampleTour()));
        List<TourResponse> result = service.listAll();
        assertEquals(1, result.size());
    }

    @Test
    void findByLocation_filtersCorrectly() {
        when(repo.findByLocationContainsIgnoreCaseAndActiveTrue("Puntarenas"))
            .thenReturn(List.of(sampleTour()));
        List<TourResponse> result = service.findByLocation("Puntarenas");
        assertEquals(1, result.size());
        assertEquals("Puntarenas", result.get(0).getLocation());
    }
}
