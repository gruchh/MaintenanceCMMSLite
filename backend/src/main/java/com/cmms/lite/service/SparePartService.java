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

    private static final String NOT_FOUND_MESSAGE = "SparePart not found with id: ";

    @Transactional(readOnly = true)
    public Page<SparePartDTOs.Response> findAll(Pageable pageable) {
        Page<SparePart> sparePartsPage = sparePartRepository.findAll(pageable);
        return sparePartsPage.map(sparePartMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public SparePartDTOs.Response findById(Long id) {
        return sparePartRepository.findById(id)
                .map(sparePartMapper::toResponse)
                .orElseThrow(() -> new EntityNotFoundException(NOT_FOUND_MESSAGE + id));
    }

    @Transactional
    public SparePartDTOs.Response save(SparePartDTOs.CreateRequest createRequest) {
        SparePart sparePart = sparePartMapper.toEntity(createRequest);
        SparePart savedPart = sparePartRepository.save(sparePart);
        return sparePartMapper.toResponse(savedPart);
    }

    @Transactional
    public SparePartDTOs.Response update(Long id, SparePartDTOs.UpdateRequest updateRequest) {
        SparePart existingPart = sparePartRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(NOT_FOUND_MESSAGE + id));

        sparePartMapper.updateEntityFromRequest(updateRequest, existingPart);
        SparePart updatedPart = sparePartRepository.save(existingPart);

        return sparePartMapper.toResponse(updatedPart);
    }

    @Transactional
    public void delete(Long id) {
        if (!sparePartRepository.existsById(id)) {
            throw new EntityNotFoundException(NOT_FOUND_MESSAGE + id);
        }
        sparePartRepository.deleteById(id);
    }
}