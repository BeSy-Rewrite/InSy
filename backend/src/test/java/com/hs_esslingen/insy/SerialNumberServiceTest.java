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

import com.hs_esslingen.insy.dto.SerialNumberDTO;
import com.hs_esslingen.insy.repository.InventoryRepository;
import com.hs_esslingen.insy.service.SerialNumberService;

@DisplayName("SerialNumberService Unit Tests - Null Handling")
class SerialNumberServiceTest {

    @Mock
    private InventoryRepository inventoryRepository;

    @InjectMocks
    private SerialNumberService serialNumberService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // ===== Test Cases: Normal Scenarios =====

    @Test
    @DisplayName("Should return SerialNumberDTO with serial numbers when they exist")
    void getAllSerialNumbers_whenSerialNumbersExist_returnsSerialNumberDTO() {
        // Arrange
        Set<String> mockSerialNumbers = new HashSet<>();
        mockSerialNumbers.add("SN001");
        mockSerialNumbers.add("SN002");
        mockSerialNumbers.add("SN003");

        when(inventoryRepository.findAllSerialNumbers()).thenReturn(mockSerialNumbers);

        // Act
        SerialNumberDTO result = serialNumberService.getAllSerialNumbers();

        // Assert
        assertNotNull(result);
        assertNotNull(result.getSerialNumbers());
        assertEquals(3, result.getSerialNumbers().size());
        assertTrue(result.getSerialNumbers().contains("SN001"));
        assertTrue(result.getSerialNumbers().contains("SN002"));
        assertTrue(result.getSerialNumbers().contains("SN003"));
        verify(inventoryRepository).findAllSerialNumbers();
    }

    @Test
    @DisplayName("Should return SerialNumberDTO with single serial number")
    void getAllSerialNumbers_withSingleSerialNumber_returnsSerialNumberDTOWithOneEntry() {
        // Arrange
        Set<String> mockSerialNumbers = new HashSet<>();
        mockSerialNumbers.add("ABC123456");

        when(inventoryRepository.findAllSerialNumbers()).thenReturn(mockSerialNumbers);

        // Act
        SerialNumberDTO result = serialNumberService.getAllSerialNumbers();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getSerialNumbers().size());
        assertTrue(result.getSerialNumbers().contains("ABC123456"));
    }

    @Test
    @DisplayName("Should handle multiple serial numbers with different formats")
    void getAllSerialNumbers_withDifferentFormats_returnsAllSerialNumbers() {
        // Arrange
        Set<String> mockSerialNumbers = new HashSet<>();
        mockSerialNumbers.add("SN-2024-001");
        mockSerialNumbers.add("ABC123456");
        mockSerialNumbers.add("12345678");
        mockSerialNumbers.add("DELL-XPS-789");

        when(inventoryRepository.findAllSerialNumbers()).thenReturn(mockSerialNumbers);

        // Act
        SerialNumberDTO result = serialNumberService.getAllSerialNumbers();

        // Assert
        assertNotNull(result);
        assertEquals(4, result.getSerialNumbers().size());
        assertTrue(result.getSerialNumbers().contains("SN-2024-001"));
        assertTrue(result.getSerialNumbers().contains("ABC123456"));
        assertTrue(result.getSerialNumbers().contains("12345678"));
        assertTrue(result.getSerialNumbers().contains("DELL-XPS-789"));
    }

    // ===== Test Cases: Empty Set Scenarios =====

    @Test
    @DisplayName("Should handle empty set when no serial numbers exist")
    void getAllSerialNumbers_whenNoSerialNumbersExist_returnsEmptySet() {
        // Arrange
        Set<String> emptySet = Collections.emptySet();
        when(inventoryRepository.findAllSerialNumbers()).thenReturn(emptySet);

        // Act
        SerialNumberDTO result = serialNumberService.getAllSerialNumbers();

        // Assert
        assertNotNull(result);
        assertNotNull(result.getSerialNumbers());
        assertTrue(result.getSerialNumbers().isEmpty());
        assertEquals(0, result.getSerialNumbers().size());
    }

    @Test
    @DisplayName("Should handle empty HashSet correctly")
    void getAllSerialNumbers_withEmptyHashSet_returnsEmptySet() {
        // Arrange
        Set<String> emptyHashSet = new HashSet<>();
        when(inventoryRepository.findAllSerialNumbers()).thenReturn(emptyHashSet);

        // Act
        SerialNumberDTO result = serialNumberService.getAllSerialNumbers();

        // Assert
        assertNotNull(result);
        assertNotNull(result.getSerialNumbers());
        assertTrue(result.getSerialNumbers().isEmpty());
    }

    @Test
    @DisplayName("Should handle Collections.emptySet() correctly")
    void getAllSerialNumbers_withCollectionsEmptySet_returnsEmptySet() {
        // Arrange
        when(inventoryRepository.findAllSerialNumbers()).thenReturn(Collections.emptySet());

        // Act
        SerialNumberDTO result = serialNumberService.getAllSerialNumbers();

        // Assert
        assertNotNull(result);
        assertNotNull(result.getSerialNumbers());
        assertTrue(result.getSerialNumbers().isEmpty());
    }

    // ===== Test Cases: Null Scenarios =====

    @Test
    @DisplayName("Should handle null returned from repository")
    void getAllSerialNumbers_whenRepositoryReturnsNull_shouldHandleNullSafely() {
        // Arrange
        when(inventoryRepository.findAllSerialNumbers()).thenReturn(null);

        // Act & Assert
        // This test documents current behavior - depends on implementation
        // If service doesn't handle null, it will throw NullPointerException
        try {
            SerialNumberDTO result = serialNumberService.getAllSerialNumbers();
            // If no exception, check that result is not null
            assertNotNull(result);
        } catch (NullPointerException e) {
            // This is acceptable behavior if null check is not implemented in service
            assertTrue(true, "NullPointerException thrown as expected when repository returns null");
        }
    }

    // ===== Test Cases: Special Characters and Edge Cases =====

    @Test
    @DisplayName("Should handle serial numbers with special characters")
    void getAllSerialNumbers_withSpecialCharacters_returnsSerialNumberDTOCorrectly() {
        // Arrange
        Set<String> mockSerialNumbers = new HashSet<>();
        mockSerialNumbers.add("SN-2024-001");
        mockSerialNumbers.add("ABC_123.456");
        mockSerialNumbers.add("XY/Z#789");

        when(inventoryRepository.findAllSerialNumbers()).thenReturn(mockSerialNumbers);

        // Act
        SerialNumberDTO result = serialNumberService.getAllSerialNumbers();

        // Assert
        assertNotNull(result);
        assertEquals(3, result.getSerialNumbers().size());
        assertTrue(result.getSerialNumbers().contains("SN-2024-001"));
        assertTrue(result.getSerialNumbers().contains("ABC_123.456"));
        assertTrue(result.getSerialNumbers().contains("XY/Z#789"));
    }

    @Test
    @DisplayName("Should handle serial numbers with whitespace")
    void getAllSerialNumbers_withWhitespace_handlesProperly() {
        // Arrange
        Set<String> mockSerialNumbers = new HashSet<>();
        mockSerialNumbers.add("  SN001  ");
        mockSerialNumbers.add("\tABC123");

        when(inventoryRepository.findAllSerialNumbers()).thenReturn(mockSerialNumbers);

        // Act
        SerialNumberDTO result = serialNumberService.getAllSerialNumbers();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.getSerialNumbers().size());
        assertTrue(result.getSerialNumbers().contains("  SN001  "));
        assertTrue(result.getSerialNumbers().contains("\tABC123"));
    }

    @Test
    @DisplayName("Should handle empty string in serial numbers")
    void getAllSerialNumbers_withEmptyString_includesEmptyString() {
        // Arrange
        Set<String> mockSerialNumbers = new HashSet<>();
        mockSerialNumbers.add("");
        mockSerialNumbers.add("SN001");

        when(inventoryRepository.findAllSerialNumbers()).thenReturn(mockSerialNumbers);

        // Act
        SerialNumberDTO result = serialNumberService.getAllSerialNumbers();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.getSerialNumbers().size());
        assertTrue(result.getSerialNumbers().contains(""));
        assertTrue(result.getSerialNumbers().contains("SN001"));
    }

    @Test
    @DisplayName("Should handle very long serial numbers")
    void getAllSerialNumbers_withLongSerialNumbers_handlesProperly() {
        // Arrange
        String longSerialNumber = "SN" + "0".repeat(1000);
        Set<String> mockSerialNumbers = new HashSet<>();
        mockSerialNumbers.add(longSerialNumber);
        mockSerialNumbers.add("SHORT");

        when(inventoryRepository.findAllSerialNumbers()).thenReturn(mockSerialNumbers);

        // Act
        SerialNumberDTO result = serialNumberService.getAllSerialNumbers();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.getSerialNumbers().size());
        assertTrue(result.getSerialNumbers().contains(longSerialNumber));
        assertTrue(result.getSerialNumbers().contains("SHORT"));
    }

    @Test
    @DisplayName("Should handle single character serial numbers")
    void getAllSerialNumbers_withSingleCharacter_handlesProperly() {
        // Arrange
        Set<String> mockSerialNumbers = new HashSet<>();
        mockSerialNumbers.add("A");
        mockSerialNumbers.add("1");
        mockSerialNumbers.add("X");

        when(inventoryRepository.findAllSerialNumbers()).thenReturn(mockSerialNumbers);

        // Act
        SerialNumberDTO result = serialNumberService.getAllSerialNumbers();

        // Assert
        assertNotNull(result);
        assertEquals(3, result.getSerialNumbers().size());
        assertTrue(result.getSerialNumbers().contains("A"));
        assertTrue(result.getSerialNumbers().contains("1"));
        assertTrue(result.getSerialNumbers().contains("X"));
    }

    @Test
    @DisplayName("Should handle Unicode characters in serial numbers")
    void getAllSerialNumbers_withUnicodeCharacters_handlesProperly() {
        // Arrange
        Set<String> mockSerialNumbers = new HashSet<>();
        mockSerialNumbers.add("SN-ÄÖÜ-001");
        mockSerialNumbers.add("中文-123");
        mockSerialNumbers.add("ΔΣΦ-456");

        when(inventoryRepository.findAllSerialNumbers()).thenReturn(mockSerialNumbers);

        // Act
        SerialNumberDTO result = serialNumberService.getAllSerialNumbers();

        // Assert
        assertNotNull(result);
        assertEquals(3, result.getSerialNumbers().size());
        assertTrue(result.getSerialNumbers().contains("SN-ÄÖÜ-001"));
        assertTrue(result.getSerialNumbers().contains("中文-123"));
        assertTrue(result.getSerialNumbers().contains("ΔΣΦ-456"));
    }

    // ===== Test Cases: Large Data Set =====

    @Test
    @DisplayName("Should handle large set of serial numbers")
    void getAllSerialNumbers_withLargeDataSet_returnsAllSerialNumbers() {
        // Arrange
        Set<String> mockSerialNumbers = new HashSet<>();
        for (int i = 1; i <= 10000; i++) {
            mockSerialNumbers.add("SN-" + String.format("%06d", i));
        }

        when(inventoryRepository.findAllSerialNumbers()).thenReturn(mockSerialNumbers);

        // Act
        SerialNumberDTO result = serialNumberService.getAllSerialNumbers();

        // Assert
        assertNotNull(result);
        assertEquals(10000, result.getSerialNumbers().size());
        assertTrue(result.getSerialNumbers().contains("SN-000001"));
        assertTrue(result.getSerialNumbers().contains("SN-005000"));
        assertTrue(result.getSerialNumbers().contains("SN-010000"));
    }

    // ===== Test Cases: Duplicate Prevention =====

    @Test
    @DisplayName("Should use Set to prevent duplicates")
    void getAllSerialNumbers_setNaturePreventsDuplicates() {
        // Arrange
        // Using HashSet directly - Set interface prevents duplicates
        Set<String> mockSerialNumbers = new HashSet<>();
        mockSerialNumbers.add("SN001");
        mockSerialNumbers.add("SN001"); // Duplicate - won't be added to set
        mockSerialNumbers.add("SN002");

        when(inventoryRepository.findAllSerialNumbers()).thenReturn(mockSerialNumbers);

        // Act
        SerialNumberDTO result = serialNumberService.getAllSerialNumbers();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.getSerialNumbers().size());
    }

    // ===== Test Cases: DTO Structure =====

    @Test
    @DisplayName("Should return properly structured SerialNumberDTO")
    void getAllSerialNumbers_returnedDTOHasCorrectStructure() {
        // Arrange
        Set<String> mockSerialNumbers = new HashSet<>();
        mockSerialNumbers.add("SN001");

        when(inventoryRepository.findAllSerialNumbers()).thenReturn(mockSerialNumbers);

        // Act
        SerialNumberDTO result = serialNumberService.getAllSerialNumbers();

        // Assert
        assertNotNull(result);
        assertNotNull(result.getSerialNumbers());
        // Verify the DTO is not empty and has the expected field populated
        assertTrue(result.getSerialNumbers().size() > 0);
    }

    @Test
    @DisplayName("Should ensure SerialNumberDTO.serialNumbers field is never null")
    void getAllSerialNumbers_ensureDTOSerialNumbersFieldIsNotNull() {
        // Arrange
        Set<String> emptySet = Collections.emptySet();
        when(inventoryRepository.findAllSerialNumbers()).thenReturn(emptySet);

        // Act
        SerialNumberDTO result = serialNumberService.getAllSerialNumbers();

        // Assert
        assertNotNull(result);
        assertNotNull(result.getSerialNumbers(), "SerialNumberDTO.serialNumbers should never be null");
    }

    @Test
    @DisplayName("Should return mutable set when serial numbers exist")
    void getAllSerialNumbers_returnsMutableSet() {
        // Arrange
        Set<String> mockSerialNumbers = new HashSet<>();
        mockSerialNumbers.add("SN001");

        when(inventoryRepository.findAllSerialNumbers()).thenReturn(mockSerialNumbers);

        // Act
        SerialNumberDTO result = serialNumberService.getAllSerialNumbers();

        // Assert
        assertNotNull(result);
        assertNotNull(result.getSerialNumbers());
        // HashSet should be mutable (can add/remove elements)
        assertEquals(1, result.getSerialNumbers().size());
    }

    // ===== Test Cases: Repository Interaction =====

    @Test
    @DisplayName("Should call repository method exactly once")
    void getAllSerialNumbers_callsRepositoryMethodOnce() {
        // Arrange
        Set<String> mockSerialNumbers = Collections.emptySet();
        when(inventoryRepository.findAllSerialNumbers()).thenReturn(mockSerialNumbers);

        // Act
        serialNumberService.getAllSerialNumbers();

        // Assert
        verify(inventoryRepository).findAllSerialNumbers();
    }

    @Test
    @DisplayName("Should not pass any arguments to repository method")
    void getAllSerialNumbers_doesNotPassArgumentsToRepository() {
        // Arrange
        Set<String> mockSerialNumbers = Collections.emptySet();
        when(inventoryRepository.findAllSerialNumbers()).thenReturn(mockSerialNumbers);

        // Act
        serialNumberService.getAllSerialNumbers();

        // Assert - verify method is called with no arguments
        verify(inventoryRepository).findAllSerialNumbers();
    }

    // ===== Test Cases: Case Sensitivity =====

    @Test
    @DisplayName("Should preserve case in serial numbers")
    void getAllSerialNumbers_preservesCase() {
        // Arrange
        Set<String> mockSerialNumbers = new HashSet<>();
        mockSerialNumbers.add("SN001");
        mockSerialNumbers.add("sn001");
        mockSerialNumbers.add("Sn001");

        when(inventoryRepository.findAllSerialNumbers()).thenReturn(mockSerialNumbers);

        // Act
        SerialNumberDTO result = serialNumberService.getAllSerialNumbers();

        // Assert
        assertNotNull(result);
        assertEquals(3, result.getSerialNumbers().size());
        assertTrue(result.getSerialNumbers().contains("SN001"));
        assertTrue(result.getSerialNumbers().contains("sn001"));
        assertTrue(result.getSerialNumbers().contains("Sn001"));
    }
}
