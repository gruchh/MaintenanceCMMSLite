package com.cmms.lite.service;

import com.cmms.lite.api.dto.SparePartDTOs;
import com.cmms.lite.core.entity.SparePart;
import com.cmms.lite.core.mapper.SparePartMapper;
import com.cmms.lite.core.repository.SparePartRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SparePartService {

    private final SparePartRepository sparePartRepository;
    private final SparePartMapper sparePartMapper;

    private static final String NOT_FOUND = "SparePart not found with id: ";

    @Transactional(readOnly = true)
    public Page<SparePartDTOs.Response> getAllSpareParts(Pageable pageable) {
        return sparePartRepository.findAll(pageable)
                .map(sparePartMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public SparePartDTOs.Response getSparePartById(Long id) {
        SparePart sparePart = getSparePartByIdOrThrow(id);
        return sparePartMapper.toResponse(sparePart);
    }

    @Transactional
    public SparePartDTOs.Response createSparePart(SparePartDTOs.CreateRequest request) {
        SparePart sparePart = sparePartMapper.toEntity(request);
        SparePart saved = sparePartRepository.save(sparePart);
        return sparePartMapper.toResponse(saved);
    }

    @Transactional
    public SparePartDTOs.Response updateSparePart(Long id, SparePartDTOs.UpdateRequest request) {
        SparePart existing = getSparePartByIdOrThrow(id);
        sparePartMapper.updateEntityFromRequest(request, existing);
        SparePart updated = sparePartRepository.save(existing);
        return sparePartMapper.toResponse(updated);
    }

    @Transactional
    public void deleteSparePart(Long id) {
        if (!sparePartRepository.existsById(id)) {
            throw new EntityNotFoundException(NOT_FOUND + id);
        }
        sparePartRepository.deleteById(id);
    }

    private SparePart getSparePartByIdOrThrow(Long id) {
        return sparePartRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(NOT_FOUND + id));
    }
}