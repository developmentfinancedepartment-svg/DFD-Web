package com.logic.springjwt.controllers;

import com.logic.springjwt.models.Scheme;
import com.logic.springjwt.repository.SchemeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SchemeControllerTest {

    @Mock
    private SchemeRepository schemeRepository;

    @InjectMocks
    private SchemeController schemeController;

    private Scheme sampleScheme;

    @BeforeEach
    void setUp() {
        sampleScheme = new Scheme(
                1L,
                "Re-MSME Plus Loan Scheme",
                "MSME Revitalization",
                "Up to Rs. 25M",
                "5% p.a.",
                "Financial assistance and concessionary loans for MSMEs.",
                "fas fa-store",
                true
        );
    }

    @Test
    void testGetActiveSchemes() {
        when(schemeRepository.findPublicSchemes()).thenReturn(List.of(sampleScheme));

        ResponseEntity<List<Scheme>> response = schemeController.getActiveSchemes();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals("Re-MSME Plus Loan Scheme", response.getBody().get(0).getTitle());
        verify(schemeRepository).findPublicSchemes();
    }

    @Test
    void testGetSchemeByIdSuccess() {
        when(schemeRepository.findById(1L)).thenReturn(Optional.of(sampleScheme));

        ResponseEntity<Scheme> response = schemeController.getSchemeById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Re-MSME Plus Loan Scheme", response.getBody().getTitle());
        verify(schemeRepository).findById(1L);
    }

    @Test
    void testGetSchemeByIdNotFound() {
        when(schemeRepository.findById(99L)).thenReturn(Optional.empty());

        ResponseEntity<Scheme> response = schemeController.getSchemeById(99L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(schemeRepository).findById(99L);
    }

    @Test
    void testGetAllSchemes() {
        when(schemeRepository.findAll()).thenReturn(List.of(sampleScheme));

        ResponseEntity<List<Scheme>> response = schemeController.getAllSchemes();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        verify(schemeRepository).findAll();
    }

    @Test
    void testCreateScheme() {
        when(schemeRepository.save(any(Scheme.class))).thenReturn(sampleScheme);

        ResponseEntity<Scheme> response = schemeController.createScheme(sampleScheme);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Re-MSME Plus Loan Scheme", response.getBody().getTitle());
        verify(schemeRepository).save(any(Scheme.class));
    }

    @Test
    void testUpdateSchemeSuccess() {
        Scheme updatedDetails = new Scheme(
                1L,
                "Updated Scheme",
                "Green Energy",
                "Up to Rs. 50M",
                "4% p.a.",
                "Updated description",
                "fas fa-solar-panel",
                false
        );

        updatedDetails.setCenterLocationsTableRaw("[{\"district\":\"Polonnaruwa\",\"warehouseName\":\"Gal-Oya Central Silo\"}]");
        updatedDetails.setOperationalArrangements("Cost-recovery fees apply for commercial drying.");
        updatedDetails.setBeneficiaries("Farmers cultivating Paddy, Maize, Soybean, Sesame, Black Gram, Pepper, Peanuts, and other grains\nSmall and medium millers");
        updatedDetails.setShowOtherBadge(true);

        when(schemeRepository.findById(1L)).thenReturn(Optional.of(sampleScheme));
        when(schemeRepository.save(any(Scheme.class))).thenAnswer(inv -> inv.getArgument(0));

        ResponseEntity<Scheme> response = schemeController.updateScheme(1L, updatedDetails);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Updated Scheme", response.getBody().getTitle());
        assertEquals("Green Energy", response.getBody().getCategory());
        assertEquals("Cost-recovery fees apply for commercial drying.", response.getBody().getOperationalArrangements());
        assertEquals("[{\"district\":\"Polonnaruwa\",\"warehouseName\":\"Gal-Oya Central Silo\"}]", response.getBody().getCenterLocationsTableRaw());
        assertEquals("Farmers cultivating Paddy, Maize, Soybean, Sesame, Black Gram, Pepper, Peanuts, and other grains\nSmall and medium millers", response.getBody().getBeneficiaries());
        assertTrue(response.getBody().getShowOtherBadge());
        assertFalse(response.getBody().isActive());
    }

    @Test
    void testUpdateSchemeNotFound() {
        when(schemeRepository.findById(99L)).thenReturn(Optional.empty());

        ResponseEntity<Scheme> response = schemeController.updateScheme(99L, sampleScheme);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testDeleteSchemeSuccess() {
        when(schemeRepository.existsById(1L)).thenReturn(true);

        ResponseEntity<Void> response = schemeController.deleteScheme(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(schemeRepository).deleteById(1L);
    }

    @Test
    void testDeleteSchemeNotFound() {
        when(schemeRepository.existsById(99L)).thenReturn(false);

        ResponseEntity<Void> response = schemeController.deleteScheme(99L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(schemeRepository, never()).deleteById(anyLong());
    }

    @Test
    @SuppressWarnings("unchecked")
    void testSchemeDataInitializerSeeds4DefaultSchemesWhenEmpty() {
        when(schemeRepository.count()).thenReturn(0L);
        SchemeDataInitializer initializer = new SchemeDataInitializer(schemeRepository);

        initializer.run();

        ArgumentCaptor<List<Scheme>> captor = ArgumentCaptor.forClass(List.class);
        verify(schemeRepository).saveAll(captor.capture());
        List<Scheme> seeded = captor.getValue();
        assertEquals(4, seeded.size());
        assertEquals("Re-MSME Plus Loan Scheme", seeded.get(0).getTitle());
        assertEquals("NPA Borrower Relief Scheme", seeded.get(1).getTitle());
        assertEquals("Food Security Project", seeded.get(2).getTitle());
        assertEquals("Working Capital Loan Scheme", seeded.get(3).getTitle());
    }
}
