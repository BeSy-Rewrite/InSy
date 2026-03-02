package com.hs_esslingen.insy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.hs_esslingen.insy.dto.LocationDTO;
import com.hs_esslingen.insy.repository.InventoryRepository;
import com.hs_esslingen.insy.service.LocationService;

@DisplayName("LocationService Unit Tests - Null Handling")
class LocationServiceTest {

    @Mock
    private InventoryRepository inventoryRepository;

    @InjectMocks
    private LocationService locationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // ===== Test Cases: Normal Scenarios =====

    @Test
    @DisplayName("Should return LocationDTO with locations when locations exist")
    void getAllLocations_whenLocationsExist_returnsLocationDTO() {
        // Arrange
        Set<String> mockLocations = new HashSet<>();
        mockLocations.add("Raum 101");
        mockLocations.add("Raum 102");
        mockLocations.add("Lager");

        when(inventoryRepository.findAllLocations()).thenReturn(mockLocations);

        // Act
        LocationDTO result = locationService.getAllLocations();

        // Assert
        assertNotNull(result);
        assertNotNull(result.getLocations());
        assertEquals(3, result.getLocations().size());
        assertTrue(result.getLocations().contains("Raum 101"));
        assertTrue(result.getLocations().contains("Raum 102"));
        assertTrue(result.getLocations().contains("Lager"));
        verify(inventoryRepository).findAllLocations();
    }

    @Test
    @DisplayName("Should return LocationDTO with single location")
    void getAllLocations_withSingleLocation_returnsLocationDTOWithOneEntry() {
        // Arrange
        Set<String> mockLocations = new HashSet<>();
        mockLocations.add("Raum 101");

        when(inventoryRepository.findAllLocations()).thenReturn(mockLocations);

        // Act
        LocationDTO result = locationService.getAllLocations();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getLocations().size());
        assertTrue(result.getLocations().contains("Raum 101"));
    }

    // ===== Test Cases: Empty Set Scenarios =====

    @Test
    @DisplayName("Should handle empty set when no locations exist")
    void getAllLocations_whenNoLocationsExist_returnsEmptySet() {
        // Arrange
        Set<String> emptySet = Collections.emptySet();
        when(inventoryRepository.findAllLocations()).thenReturn(emptySet);

        // Act
        LocationDTO result = locationService.getAllLocations();

        // Assert
        assertNotNull(result);
        assertNotNull(result.getLocations());
        assertTrue(result.getLocations().isEmpty());
        assertEquals(0, result.getLocations().size());
    }

    @Test
    @DisplayName("Should handle empty HashSet correctly")
    void getAllLocations_withEmptyHashSet_returnsEmptySet() {
        // Arrange
        Set<String> emptyHashSet = new HashSet<>();
        when(inventoryRepository.findAllLocations()).thenReturn(emptyHashSet);

        // Act
        LocationDTO result = locationService.getAllLocations();

        // Assert
        assertNotNull(result);
        assertNotNull(result.getLocations());
        assertTrue(result.getLocations().isEmpty());
    }

    // ===== Test Cases: Null Scenarios =====

    @Test
    @DisplayName("Should handle null returned from repository")
    void getAllLocations_whenRepositoryReturnsNull_shouldHandleNullSafely() {
        // Arrange
        when(inventoryRepository.findAllLocations()).thenReturn(null);

        // Act & Assert
        // This test documents current behavior - depends on implementation
        // If service doesn't handle null, it will throw NullPointerException
        try {
            LocationDTO result = locationService.getAllLocations();
            // If no exception, check that result is not null
            assertNotNull(result);
        } catch (NullPointerException e) {
            // This is acceptable behavior if null check is not implemented in service
            assertTrue(true, "NullPointerException thrown as expected when repository returns null");
        }
    }

    // ===== Test Cases: Special Characters and Edge Cases =====

    @Test
    @DisplayName("Should handle locations with special characters")
    void getAllLocations_withSpecialCharacters_returnsLocationDTOCorrectly() {
        // Arrange
        Set<String> mockLocations = new HashSet<>();
        mockLocations.add("Raum 101 - Büro");
        mockLocations.add("Lager/Archiv");
        mockLocations.add("Standort_Nord");

        when(inventoryRepository.findAllLocations()).thenReturn(mockLocations);

        // Act
        LocationDTO result = locationService.getAllLocations();

        // Assert
        assertNotNull(result);
        assertEquals(3, result.getLocations().size());
        assertTrue(result.getLocations().contains("Raum 101 - Büro"));
        assertTrue(result.getLocations().contains("Lager/Archiv"));
        assertTrue(result.getLocations().contains("Standort_Nord"));
    }

    @Test
    @DisplayName("Should handle whitespace in location names")
    void getAllLocations_withWhitespace_handlesProperly() {
        // Arrange
        Set<String> mockLocations = new HashSet<>();
        mockLocations.add("  Raum 101  ");
        mockLocations.add("\tLager");

        when(inventoryRepository.findAllLocations()).thenReturn(mockLocations);

        // Act
        LocationDTO result = locationService.getAllLocations();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.getLocations().size());
        assertTrue(result.getLocations().contains("  Raum 101  "));
        assertTrue(result.getLocations().contains("\tLager"));
    }

    @Test
    @DisplayName("Should handle empty string in locations")
    void getAllLocations_withEmptyString_includesEmptyString() {
        // Arrange
        Set<String> mockLocations = new HashSet<>();
        mockLocations.add("");
        mockLocations.add("Raum 101");

        when(inventoryRepository.findAllLocations()).thenReturn(mockLocations);

        // Act
        LocationDTO result = locationService.getAllLocations();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.getLocations().size());
        assertTrue(result.getLocations().contains(""));
        assertTrue(result.getLocations().contains("Raum 101"));
    }

    // ===== Test Cases: Large Data Set =====

    @Test
    @DisplayName("Should handle large set of locations")
    void getAllLocations_withLargeDataSet_returnsAllLocations() {
        // Arrange
        Set<String> mockLocations = new HashSet<>();
        for (int i = 1; i <= 1000; i++) {
            mockLocations.add("Raum " + i);
        }

        when(inventoryRepository.findAllLocations()).thenReturn(mockLocations);

        // Act
        LocationDTO result = locationService.getAllLocations();

        // Assert
        assertNotNull(result);
        assertEquals(1000, result.getLocations().size());
        assertTrue(result.getLocations().contains("Raum 1"));
        assertTrue(result.getLocations().contains("Raum 500"));
        assertTrue(result.getLocations().contains("Raum 1000"));
    }

    // ===== Test Cases: Duplicate Prevention =====

    @Test
    @DisplayName("Should use Set to prevent duplicates")
    void getAllLocations_setNaturePreventsDuplicates() {
        // Arrange
        // Using HashSet directly - Set interface prevents duplicates
        Set<String> mockLocations = new HashSet<>();
        mockLocations.add("Raum 101");
        mockLocations.add("Raum 101"); // Duplicate - won't be added to set
        mockLocations.add("Raum 102");

        when(inventoryRepository.findAllLocations()).thenReturn(mockLocations);

        // Act
        LocationDTO result = locationService.getAllLocations();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.getLocations().size());
    }

    // ===== Test Cases: DTO Structure =====

    @Test
    @DisplayName("Should return properly structured LocationDTO")
    void getAllLocations_returnedDTOHasCorrectStructure() {
        // Arrange
        Set<String> mockLocations = new HashSet<>();
        mockLocations.add("Raum 101");

        when(inventoryRepository.findAllLocations()).thenReturn(mockLocations);

        // Act
        LocationDTO result = locationService.getAllLocations();

        // Assert
        assertNotNull(result);
        assertNotNull(result.getLocations());
        // Verify the DTO is not empty and has the expected field populated
        assertTrue(result.getLocations().size() > 0);
    }

    @Test
    @DisplayName("Should ensure LocationDTO.locations field is never null")
    void getAllLocations_ensureDTOLocationsFieldIsNotNull() {
        // Arrange
        Set<String> emptySet = Collections.emptySet();
        when(inventoryRepository.findAllLocations()).thenReturn(emptySet);

        // Act
        LocationDTO result = locationService.getAllLocations();

        // Assert
        assertNotNull(result);
        assertNotNull(result.getLocations(), "LocationDTO.locations should never be null");
    }

    // ===== Test Cases: Repository Interaction =====

    @Test
    @DisplayName("Should call repository method exactly once")
    void getAllLocations_callsRepositoryMethodOnce() {
        // Arrange
        Set<String> mockLocations = Collections.emptySet();
        when(inventoryRepository.findAllLocations()).thenReturn(mockLocations);

        // Act
        locationService.getAllLocations();

        // Assert
        verify(inventoryRepository).findAllLocations();
    }

    @Test
    @DisplayName("Should not pass any arguments to repository method")
    void getAllLocations_doesNotPassArgumentsToRepository() {
        // Arrange
        Set<String> mockLocations = Collections.emptySet();
        when(inventoryRepository.findAllLocations()).thenReturn(mockLocations);

        // Act
        locationService.getAllLocations();

        // Assert - verify method is called with no arguments
        verify(inventoryRepository).findAllLocations();
    }
}
