package com.cmms.lite.breakdownType.service;

import com.cmms.lite.breakdownType.dto.BreakdownTypeResponseDTO;
import com.cmms.lite.breakdownType.entity.BreakdownType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BreakdownTypeService {

    public List<BreakdownTypeResponseDTO> getAllBreakdownTypesAsDto() {
        return Arrays.stream(BreakdownType.values())
                .map(type -> new BreakdownTypeResponseDTO(type.name(), type.getDisplayName()))
                .collect(Collectors.toList());
    }
}
