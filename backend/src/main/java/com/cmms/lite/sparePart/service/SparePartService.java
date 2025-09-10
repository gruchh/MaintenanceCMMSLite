package com.cmms.lite.sparePart.service;

import com.cmms.lite.CmmsLiteApplication;
import com.cmms.lite.sparePart.dto.CreateSparePartDTO;
import com.cmms.lite.sparePart.dto.SparePartResponseDTO;
import com.cmms.lite.sparePart.dto.UpdateSparePartDTO;
import com.cmms.lite.sparePart.entity.SparePart;
import com.cmms.lite.sparePart.exception.SparePartNotFoundException;
import com.cmms.lite.sparePart.mapper.SparePartMapper;
import com.cmms.lite.sparePart.repository.SparePartRepository;
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

    private static final String NOT_FOUND = "Część zamienna o ID %d nie została znaleziona.";

    @Transactional(readOnly = true)
    public Page<SparePartResponseDTO> getAllSpareParts(Pageable pageable) {
        return sparePartRepository.findAll(pageable)
                .map(sparePartMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public SparePartResponseDTO getSparePartById(Long id) {
        SparePart sparePart = getSparePartByIdOrThrow(id);
        return sparePartMapper.toResponse(sparePart);
    }

    @Transactional
    public SparePartResponseDTO createSparePart(CreateSparePartDTO request) {
        SparePart sparePart = sparePartMapper.toEntity(request);
        SparePart saved = sparePartRepository.save(sparePart);
        return sparePartMapper.toResponse(saved);
    }

    @Transactional
    public SparePartResponseDTO updateSparePart(Long id, UpdateSparePartDTO request) {
        SparePart existing = getSparePartByIdOrThrow(id);
        sparePartMapper.updateEntityFromRequest(request, existing);
        SparePart updated = sparePartRepository.save(existing);
        return sparePartMapper.toResponse(updated);
    }

    @Transactional
    public void deleteSparePart(Long id) {
        if (!sparePartRepository.existsById(id)) {
            throw new SparePartNotFoundException(String.format(NOT_FOUND, id));
        }
        sparePartRepository.deleteById(id);
    }

    private SparePart getSparePartByIdOrThrow(Long id) {
        return sparePartRepository.findById(id)
                .orElseThrow(() -> new SparePartNotFoundException(String.format(NOT_FOUND, id)));
    }
}