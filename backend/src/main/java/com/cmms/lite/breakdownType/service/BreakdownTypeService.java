package com.cmms.lite.service;

import com.cmms.lite.breakdownType.BreakdownTypeDTOs;
import com.cmms.lite.breakdownType.BreakdownType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BreakdownTypeService {

    public List<BreakdownTypeDTOs.Response> getAllBreakdownTypesAsDto() {
        return Arrays.stream(BreakdownType.values())
                .map(type -> new BreakdownTypeDTOs.Response(type.name(), type.getDisplayName()))
                .collect(Collectors.toList());
    }
}
