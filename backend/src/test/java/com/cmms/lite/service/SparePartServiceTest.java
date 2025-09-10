package com.cmms.lite.service;

import com.cmms.lite.sparePart.dto.CreateSparePartDTO;
import com.cmms.lite.sparePart.dto.SparePartResponseDTO;
import com.cmms.lite.sparePart.dto.UpdateSparePartDTO;
import com.cmms.lite.sparePart.entity.SparePart;
import com.cmms.lite.sparePart.exception.SparePartNotFoundException;
import com.cmms.lite.sparePart.mapper.SparePartMapper;
import com.cmms.lite.sparePart.repository.SparePartRepository;
import com.cmms.lite.sparePart.service.SparePartService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SparePartServiceTest {

    @Mock
    private SparePartRepository sparePartRepository;

    @Mock
    private SparePartMapper sparePartMapper;

    @InjectMocks
    private SparePartService sparePartService;

    private SparePart testSparePart;
    private SparePartResponseDTO responseDTO;
    private CreateSparePartDTO createRequest;
    private UpdateSparePartDTO updateRequest;

    @BeforeEach
    void setUp() {
        testSparePart = new SparePart();
        testSparePart.setId(1L);
        testSparePart.setName("Test Part");
        testSparePart.setPrice(BigDecimal.TEN);

        responseDTO = new SparePartResponseDTO(1L, "Test Part", BigDecimal.TEN, "Producer");


        createRequest = new CreateSparePartDTO("Test Part", BigDecimal.TEN, "Producer");
        updateRequest = new UpdateSparePartDTO("Updated Part", BigDecimal.ONE, "New Producer");
    }

    @Test
    void getSparePartById_shouldReturnSparePart_whenItExists() {
        when(sparePartRepository.findById(1L)).thenReturn(Optional.of(testSparePart));
        when(sparePartMapper.toResponse(testSparePart)).thenReturn(responseDTO);

        SparePartResponseDTO result = sparePartService.getSparePartById(1L);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(1L);
    }

    @Test
    void getSparePartById_shouldThrowSparePartNotFoundException_whenItDoesNotExist() {
        when(sparePartRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(SparePartNotFoundException.class, () -> sparePartService.getSparePartById(1L));
    }

    @Test
    void createSparePart_shouldCreateAndReturnSparePart() {
        when(sparePartMapper.toEntity(createRequest)).thenReturn(testSparePart);
        when(sparePartRepository.save(testSparePart)).thenReturn(testSparePart);
        when(sparePartMapper.toResponse(testSparePart)).thenReturn(responseDTO);

        SparePartResponseDTO result = sparePartService.createSparePart(createRequest);

        assertThat(result).isNotNull();
        assertThat(result.name()).isEqualTo(createRequest.getName());
        verify(sparePartRepository, times(1)).save(testSparePart);
    }

    @Test
    void updateSparePart_shouldUpdateSuccessfully_whenSparePartExists() {
        SparePart updatedPart = new SparePart();

        when(sparePartRepository.findById(1L)).thenReturn(Optional.of(testSparePart));
        doNothing().when(sparePartMapper).updateEntityFromRequest(updateRequest, testSparePart);
        when(sparePartRepository.save(testSparePart)).thenReturn(updatedPart);
        when(sparePartMapper.toResponse(updatedPart)).thenReturn(responseDTO);

        SparePartResponseDTO result = sparePartService.updateSparePart(1L, updateRequest);

        assertThat(result).isNotNull();
        verify(sparePartMapper, times(1)).updateEntityFromRequest(updateRequest, testSparePart);
        verify(sparePartRepository, times(1)).save(testSparePart);
    }

    @Test
    void updateSparePart_shouldThrowSparePartNotFoundException_whenItDoesNotExist() {
        when(sparePartRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(SparePartNotFoundException.class, () -> sparePartService.updateSparePart(1L, updateRequest));
    }

    @Test
    void deleteSparePart_shouldDeleteSuccessfully_whenSparePartExists() {
        when(sparePartRepository.existsById(1L)).thenReturn(true);

        assertDoesNotThrow(() -> sparePartService.deleteSparePart(1L));

        verify(sparePartRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteSparePart_shouldThrowSparePartNotFoundException_whenItDoesNotExist() {
        when(sparePartRepository.existsById(1L)).thenReturn(false);

        assertThrows(SparePartNotFoundException.class, () -> sparePartService.deleteSparePart(1L));
    }
}