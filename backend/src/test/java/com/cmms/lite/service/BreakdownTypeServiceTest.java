package com.cmms.lite.service;

import com.cmms.lite.api.dto.BreakdownTypeDTOs;
import com.cmms.lite.core.entity.BreakdownType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class BreakdownTypeServiceTest {

    @InjectMocks
    private BreakdownTypeService breakdownTypeService;

    @Test
    void getAllBreakdownTypesAsDto_shouldReturnListOfAllEnumValues() {
        List<BreakdownTypeDTOs.Response> result = breakdownTypeService.getAllBreakdownTypesAsDto();

        assertThat(result).isNotNull();
        assertEquals(BreakdownType.values().length, result.size());

        BreakdownTypeDTOs.Response mechanicalType = result.stream()
                .filter(dto -> dto.value().equals(BreakdownType.MECHANICAL.name()))
                .findFirst()
                .orElse(null);

        assertThat(mechanicalType).isNotNull();
        assertEquals(BreakdownType.MECHANICAL.getDisplayName(), mechanicalType.displayName());
    }
}