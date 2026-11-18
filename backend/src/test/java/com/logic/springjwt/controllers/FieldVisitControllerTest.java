package com.logic.springjwt.controllers;

import com.logic.springjwt.models.FieldVisit;
import com.logic.springjwt.repository.FieldVisitRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FieldVisitControllerTest {

    @Mock
    private FieldVisitRepository fieldVisitRepository;

    @InjectMocks
    private FieldVisitController fieldVisitController;

    private FieldVisit sampleVisit;

    @BeforeEach
    void setUp() {
        sampleVisit = new FieldVisit(
                1L,
                "Dairy Farming Cooperative Inspection",
                "Central",
                "SME Loan",
                "Inspection description here",
                "Takeaway 1\nTakeaway 2"
        );
    }

    @Test
    void testGetAllFieldVisits() {
        when(fieldVisitRepository.findAllByOrderByIdDesc()).thenReturn(List.of(sampleVisit));
        when(fieldVisitRepository.count()).thenReturn(1L);
        when(fieldVisitRepository.countDistinctRegions()).thenReturn(1L);

        ResponseEntity<Map<String, Object>> response = fieldVisitController.getAllFieldVisits(null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().containsKey("visits"));
        assertTrue(response.getBody().containsKey("totalCount"));
        assertTrue(response.getBody().containsKey("distinctProvincesCount"));

        @SuppressWarnings("unchecked")
        List<FieldVisit> visits = (List<FieldVisit>) response.getBody().get("visits");
        assertEquals(1, visits.size());
        assertEquals("Central", visits.get(0).getRegion());
        assertEquals(1L, response.getBody().get("totalCount"));
        assertEquals(1L, response.getBody().get("distinctProvincesCount"));
        verify(fieldVisitRepository).findAllByOrderByIdDesc();
    }

    @Test
    void testGetAllFieldVisitsWithRegionFilter() {
        when(fieldVisitRepository.findByRegionOrderByIdDesc("Central")).thenReturn(List.of(sampleVisit));
        when(fieldVisitRepository.count()).thenReturn(1L);
        when(fieldVisitRepository.countDistinctRegions()).thenReturn(1L);

        ResponseEntity<Map<String, Object>> response = fieldVisitController.getAllFieldVisits("Central");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(fieldVisitRepository).findByRegionOrderByIdDesc("Central");
    }

    @Test
    void testGetFieldVisitByIdSuccess() {
        when(fieldVisitRepository.findById(1L)).thenReturn(Optional.of(sampleVisit));

        ResponseEntity<FieldVisit> response = fieldVisitController.getFieldVisitById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Dairy Farming Cooperative Inspection", response.getBody().getTitle());
    }

    @Test
    void testGetFieldVisitByIdNotFound() {
        when(fieldVisitRepository.findById(99L)).thenReturn(Optional.empty());

        ResponseEntity<FieldVisit> response = fieldVisitController.getFieldVisitById(99L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testCreateFieldVisit() {
        when(fieldVisitRepository.save(any(FieldVisit.class))).thenAnswer(inv -> {
            FieldVisit v = inv.getArgument(0);
            v.setId(2L);
            return v;
        });

        FieldVisit toSave = new FieldVisit("New Inspection", "Eastern", "Microfinance", "Description", "Point A\nPoint B");
        ResponseEntity<FieldVisit> response = fieldVisitController.createFieldVisit(toSave);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2L, response.getBody().getId());
        assertEquals("Eastern", response.getBody().getRegion());
        verify(fieldVisitRepository).save(any(FieldVisit.class));
    }

    @Test
    void testDeleteFieldVisitSuccess() {
        when(fieldVisitRepository.existsById(1L)).thenReturn(true);

        ResponseEntity<Void> response = fieldVisitController.deleteFieldVisit(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(fieldVisitRepository).deleteById(1L);
    }

    @Test
    void testDeleteFieldVisitNotFound() {
        when(fieldVisitRepository.existsById(99L)).thenReturn(false);

        ResponseEntity<Void> response = fieldVisitController.deleteFieldVisit(99L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(fieldVisitRepository, never()).deleteById(anyLong());
    }
}
