package com.logic.springjwt.controllers;

import com.logic.springjwt.models.GalleryItem;
import com.logic.springjwt.repository.GalleryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GalleryControllerTest {

    @Mock
    private GalleryRepository galleryRepository;

    @InjectMocks
    private GalleryController galleryController;

    private GalleryItem sampleItem;

    @BeforeEach
    void setUp() {
        sampleItem = new GalleryItem(
                1L,
                "Policy Workshop",
                "Workshops",
                "2026-10-15",
                "/uploads/Gallery/test.jpg"
        );
    }

    @Test
    void testGetAllGalleryItems() {
        when(galleryRepository.findAllByOrderByIdDesc()).thenReturn(List.of(sampleItem));

        ResponseEntity<List<GalleryItem>> response = galleryController.getAllGalleryItems(null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals("Policy Workshop", response.getBody().get(0).getTitle());
        verify(galleryRepository).findAllByOrderByIdDesc();
    }

    @Test
    void testGetAllGalleryItemsWithCategoryFilter() {
        when(galleryRepository.findByCategoryOrderByIdDesc("Workshops")).thenReturn(List.of(sampleItem));

        ResponseEntity<List<GalleryItem>> response = galleryController.getAllGalleryItems("Workshops");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        verify(galleryRepository).findByCategoryOrderByIdDesc("Workshops");
    }

    @Test
    void testGetGalleryItemByIdSuccess() {
        when(galleryRepository.findById(1L)).thenReturn(Optional.of(sampleItem));

        ResponseEntity<GalleryItem> response = galleryController.getGalleryItemById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getId());
    }

    @Test
    void testGetGalleryItemByIdNotFound() {
        when(galleryRepository.findById(99L)).thenReturn(Optional.empty());

        ResponseEntity<GalleryItem> response = galleryController.getGalleryItemById(99L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testCreateGalleryItemWithoutImage() {
        when(galleryRepository.save(any(GalleryItem.class))).thenAnswer(inv -> {
            GalleryItem item = inv.getArgument(0);
            item.setId(2L);
            return item;
        });

        ResponseEntity<?> response = galleryController.createGalleryItem(
                "Annual AGM",
                "Institutional",
                "2026-10-18",
                null,
                null
        );

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        GalleryItem created = (GalleryItem) response.getBody();
        assertEquals("Annual AGM", created.getTitle());
        assertEquals("Institutional", created.getCategory());
        verify(galleryRepository).save(any(GalleryItem.class));
    }

    @Test
    void testCreateGalleryItemWithImage() {
        when(galleryRepository.save(any(GalleryItem.class))).thenAnswer(inv -> {
            GalleryItem item = inv.getArgument(0);
            item.setId(3L);
            return item;
        });

        MockMultipartFile file = new MockMultipartFile(
                "image",
                "test.jpg",
                "image/jpeg",
                "fake-image-bytes".getBytes()
        );

        ResponseEntity<?> response = galleryController.createGalleryItem(
                "Field Visit to Kandy",
                "Field Visits",
                "2026-10-19",
                file,
                null
        );

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        GalleryItem created = (GalleryItem) response.getBody();
        assertEquals("Field Visit to Kandy", created.getTitle());
        assertTrue(created.getImageUrl().startsWith("/uploads/Gallery/"));
        verify(galleryRepository).save(any(GalleryItem.class));
    }

    @Test
    void testUpdateGalleryItemJsonSuccess() {
        when(galleryRepository.findById(1L)).thenReturn(Optional.of(sampleItem));
        when(galleryRepository.save(any(GalleryItem.class))).thenAnswer(inv -> inv.getArgument(0));

        GalleryItem updateData = new GalleryItem();
        updateData.setTitle("Updated Title");
        updateData.setCategory("Field Visits");
        updateData.setEventDate("2026-11-01");

        ResponseEntity<?> response = galleryController.updateGalleryItemJson(1L, updateData);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        GalleryItem updated = (GalleryItem) response.getBody();
        assertEquals("Updated Title", updated.getTitle());
        assertEquals("Field Visits", updated.getCategory());
        assertEquals("2026-11-01", updated.getEventDate());
        verify(galleryRepository).save(sampleItem);
    }

    @Test
    void testCreateGalleryItemWithMultipleImages() {
        when(galleryRepository.save(any(GalleryItem.class))).thenAnswer(inv -> {
            GalleryItem item = inv.getArgument(0);
            item.setId(4L);
            return item;
        });

        MockMultipartFile file1 = new MockMultipartFile("images", "img1.jpg", "image/jpeg", "fake-img1".getBytes());
        MockMultipartFile file2 = new MockMultipartFile("images", "img2.jpg", "image/jpeg", "fake-img2".getBytes());

        ResponseEntity<?> response = galleryController.createGalleryItem(
                "Multi-Photo Event",
                "Workshops",
                "2026-10-20",
                List.of(file1, file2),
                null,
                null,
                null
        );

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        GalleryItem created = (GalleryItem) response.getBody();
        assertEquals("Multi-Photo Event", created.getTitle());
        assertEquals(2, created.getImages().size());
        assertEquals(created.getImageUrl(), created.getImages().get(0));
        verify(galleryRepository).save(any(GalleryItem.class));
    }

    @Test
    void testDeleteGalleryItemSuccess() {
        when(galleryRepository.findById(1L)).thenReturn(Optional.of(sampleItem));

        ResponseEntity<?> response = galleryController.deleteGalleryItem(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(galleryRepository).delete(sampleItem);
    }

    @Test
    void testDeleteGalleryItemNotFound() {
        when(galleryRepository.findById(99L)).thenReturn(Optional.empty());

        ResponseEntity<?> response = galleryController.deleteGalleryItem(99L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(galleryRepository, never()).delete(any(GalleryItem.class));
    }
}
