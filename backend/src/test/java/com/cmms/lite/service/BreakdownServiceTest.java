package com.cmms.lite.service;

import com.cmms.lite.breakdown.BreakdownDTOs;
import com.cmms.lite.breakdown.entity.Breakdown;
import com.cmms.lite.breakdown.entity.BreakdownUsedParts;
import com.cmms.lite.breakdown.exception.BreakdownNotFoundException;
import com.cmms.lite.breakdown.mapper.BreakdownMapper;
import com.cmms.lite.breakdown.repository.BreakdownRepository;
import com.cmms.lite.breakdown.service.BreakdownService;
import com.cmms.lite.breakdownType.dto.BreakdownType;
import com.cmms.lite.machine.*;
import com.cmms.lite.sparePart.entity.SparePart;
import com.cmms.lite.sparePart.exception.UsedPartNotFoundException;
import com.cmms.lite.sparePart.repository.SparePartRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BreakdownServiceTest {

    @Mock
    private BreakdownRepository breakdownRepository;
    @Mock
    private SparePartRepository sparePartRepository;
    @Mock
    private MachineRepository machineRepository;
    @Mock
    private BreakdownMapper breakdownMapper;

    @InjectMocks
    private BreakdownService breakdownService;

    private Machine testMachine;
    private Breakdown testBreakdown;
    private SparePart testSparePart;
    private BreakdownDTOs.Response responseDTO;

    @BeforeEach
    void setUp() {
        testMachine = new Machine();
        testMachine.setId(1L);
        testMachine.setCode("M-001");
        testMachine.setFullName("Test Machine");

        testSparePart = new SparePart();
        testSparePart.setId(1L);
        testSparePart.setName("Test Spare Part");
        testSparePart.setPrice(BigDecimal.TEN);

        testBreakdown = new Breakdown();
        testBreakdown.setId(1L);
        testBreakdown.setDescription("Test Breakdown");
        testBreakdown.setMachine(testMachine);
        testBreakdown.setOpened(true);
        testBreakdown.setReportedAt(LocalDateTime.now());
        testBreakdown.setUsedPartsList(new ArrayList<>());
        testBreakdown.setTotalCost(BigDecimal.ZERO);
        testBreakdown.setType(BreakdownType.MECHANICAL);

        responseDTO = new BreakdownDTOs.Response(
                1L,
                "Test Description",
                LocalDateTime.now(),
                null,
                null,
                true,
                null,
                BreakdownType.MECHANICAL,
                BigDecimal.ZERO,
                new MachineDTOs.SummaryResponse(1L, "M-001", "Test Machine"),
                List.of()
        );
    }

    @Test
    void createBreakdown_shouldCreateBreakdownSuccessfully() {
        BreakdownDTOs.CreateRequest createRequest = new BreakdownDTOs.CreateRequest("Test Description", 1L, BreakdownType.MECHANICAL);
        when(machineRepository.findById(1L)).thenReturn(Optional.of(testMachine));
        when(breakdownRepository.save(any(Breakdown.class))).thenReturn(testBreakdown);
        when(breakdownMapper.toResponse(any(Breakdown.class))).thenReturn(responseDTO);

        BreakdownDTOs.Response response = breakdownService.createBreakdown(createRequest);

        assertNotNull(response);
        assertEquals("Test Description", response.description());
        verify(breakdownRepository, times(1)).save(any(Breakdown.class));
    }

    @Test
    void createBreakdown_shouldThrowExceptionWhenMachineNotFound() {
        BreakdownDTOs.CreateRequest createRequest = new BreakdownDTOs.CreateRequest("Test Description", 1L, BreakdownType.MECHANICAL);
        when(machineRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(MachineNotFoundException.class, () -> breakdownService.createBreakdown(createRequest));
    }

    @Test
    void getBreakdownById_shouldReturnBreakdownSuccessfully() {
        when(breakdownRepository.findById(1L)).thenReturn(Optional.of(testBreakdown));
        when(breakdownMapper.toResponse(any(Breakdown.class))).thenReturn(responseDTO);

        BreakdownDTOs.Response response = breakdownService.getBreakdownById(1L);

        assertNotNull(response);
        assertEquals("Test Description", response.description());
    }

    @Test
    void getBreakdownById_shouldThrowExceptionWhenBreakdownNotFound() {
        when(breakdownRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(BreakdownNotFoundException.class, () -> breakdownService.getBreakdownById(1L));
    }

    @Test
    void getAllBreakdowns_shouldReturnBreakdownPageSuccessfully() {
        Pageable pageable = Pageable.unpaged();
        List<Breakdown> breakdowns = List.of(testBreakdown);
        Page<Breakdown> breakdownPage = new PageImpl<>(breakdowns, pageable, breakdowns.size());

        when(breakdownRepository.findAll(pageable)).thenReturn(breakdownPage);
        when(breakdownMapper.toResponse(any(Breakdown.class))).thenReturn(responseDTO);

        Page<BreakdownDTOs.Response> responsePage = breakdownService.getAllBreakdowns(pageable);

        assertNotNull(responsePage);
        assertEquals(1, responsePage.getContent().size());
        assertEquals("Test Description", responsePage.getContent().get(0).description());
    }

    @Test
    void addPartToBreakdown_shouldAddPartAndRecalculateCost() {
        BreakdownDTOs.AddPartRequest addPartRequest = new BreakdownDTOs.AddPartRequest(1L, 2);
        when(breakdownRepository.findById(1L)).thenReturn(Optional.of(testBreakdown));
        when(sparePartRepository.findById(1L)).thenReturn(Optional.of(testSparePart));
        when(breakdownRepository.save(any(Breakdown.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(breakdownMapper.toResponse(any(Breakdown.class))).thenAnswer(invocation -> {
            Breakdown savedBreakdown = invocation.getArgument(0);
            Machine machine = savedBreakdown.getMachine();
            MachineDTOs.SummaryResponse machineSummary = new MachineDTOs.SummaryResponse(machine.getId(), machine.getCode(), machine.getFullName());
            return new BreakdownDTOs.Response(
                    savedBreakdown.getId(), savedBreakdown.getDescription(), savedBreakdown.getReportedAt(), null, null,
                    savedBreakdown.getOpened(), null, savedBreakdown.getType(), savedBreakdown.getTotalCost(), machineSummary, List.of());
        });

        BreakdownDTOs.Response response = breakdownService.addPartToBreakdown(1L, addPartRequest);

        assertEquals(1, testBreakdown.getUsedPartsList().size());
        assertEquals(new BigDecimal("20"), response.totalCost());
        verify(breakdownRepository, times(1)).save(testBreakdown);
    }

    @Test
    void removePartFromBreakdown_shouldRemovePartAndRecalculateCost() {
        BreakdownUsedParts usedPart = new BreakdownUsedParts();
        usedPart.setId(10L);
        usedPart.setSparePart(testSparePart);
        usedPart.setQuantity(2);
        testBreakdown.getUsedPartsList().add(usedPart);
        testBreakdown.setTotalCost(new BigDecimal("20"));

        when(breakdownRepository.findById(1L)).thenReturn(Optional.of(testBreakdown));
        when(breakdownRepository.save(any(Breakdown.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(breakdownMapper.toResponse(any(Breakdown.class))).thenAnswer(invocation -> {
            Breakdown savedBreakdown = invocation.getArgument(0);
            Machine machine = savedBreakdown.getMachine();
            MachineDTOs.SummaryResponse machineSummary = new MachineDTOs.SummaryResponse(machine.getId(), machine.getCode(), machine.getFullName());
            return new BreakdownDTOs.Response(
                    savedBreakdown.getId(), savedBreakdown.getDescription(), savedBreakdown.getReportedAt(), null, null,
                    savedBreakdown.getOpened(), null, savedBreakdown.getType(), savedBreakdown.getTotalCost(), machineSummary, List.of());
        });

        BreakdownDTOs.Response response = breakdownService.removePartFromBreakdown(1L, 10L);

        assertTrue(testBreakdown.getUsedPartsList().isEmpty());
        assertEquals(BigDecimal.ZERO, response.totalCost());
        verify(breakdownRepository, times(1)).save(testBreakdown);
    }

    @Test
    void removePartFromBreakdown_shouldThrowExceptionWhenUsedPartNotFound() {
        when(breakdownRepository.findById(1L)).thenReturn(Optional.of(testBreakdown));
        assertThrows(UsedPartNotFoundException.class, () -> breakdownService.removePartFromBreakdown(1L, 99L));
    }


    @Test
    void closeBreakdown_shouldCloseBreakdownSuccessfully() {
        BreakdownDTOs.CloseRequest closeRequest = new BreakdownDTOs.CloseRequest("Test Comment");
        when(breakdownRepository.findById(1L)).thenReturn(Optional.of(testBreakdown));
        when(breakdownRepository.save(any(Breakdown.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(breakdownMapper.toResponse(any(Breakdown.class))).thenAnswer(invocation -> {
            Breakdown savedBreakdown = invocation.getArgument(0);
            Machine machine = savedBreakdown.getMachine();
            MachineDTOs.SummaryResponse machineSummary = new MachineDTOs.SummaryResponse(machine.getId(), machine.getCode(), machine.getFullName());
            return new BreakdownDTOs.Response(
                    savedBreakdown.getId(), savedBreakdown.getDescription(), savedBreakdown.getReportedAt(),
                    savedBreakdown.getStartedAt(), savedBreakdown.getFinishedAt(), savedBreakdown.getOpened(),
                    savedBreakdown.getSpecialistComment(), savedBreakdown.getType(), savedBreakdown.getTotalCost(), machineSummary, List.of());
        });


        BreakdownDTOs.Response response = breakdownService.closeBreakdown(1L, closeRequest);

        assertFalse(response.opened());
        assertNotNull(response.finishedAt());
        assertEquals("Test Comment", response.specialistComment());
        verify(breakdownRepository, times(1)).save(testBreakdown);
    }

    @Test
    void closeBreakdown_shouldThrowExceptionWhenBreakdownAlreadyClosed() {
        testBreakdown.setOpened(false);
        BreakdownDTOs.CloseRequest closeRequest = new BreakdownDTOs.CloseRequest("Test Comment");

        when(breakdownRepository.findById(1L)).thenReturn(Optional.of(testBreakdown));

        assertThrows(IllegalOperationException.class, () -> breakdownService.closeBreakdown(1L, closeRequest));
    }

    @Test
    void getLatestBreakdown_shouldReturnLatestBreakdown() {
        when(breakdownRepository.findTopByOrderByReportedAtDesc()).thenReturn(Optional.of(testBreakdown));
        when(breakdownMapper.toResponse(testBreakdown)).thenReturn(responseDTO);

        BreakdownDTOs.Response response = breakdownService.getLatestBreakdown();

        assertNotNull(response);
        assertEquals(testBreakdown.getId(), response.id());
    }

    @Test
    void getLatestBreakdown_shouldThrowExceptionWhenNoBreakdownsFound() {
        when(breakdownRepository.findTopByOrderByReportedAtDesc()).thenReturn(Optional.empty());

        assertThrows(BreakdownNotFoundException.class, () -> breakdownService.getLatestBreakdown());
    }

    @Test
    void getBreakdownStats_shouldReturnCorrectStats() {
        LocalDateTime now = LocalDateTime.now();
        Breakdown testBreakdown = new Breakdown();
        testBreakdown.setFinishedAt(now.minusDays(5));

        when(breakdownRepository.findTopByOrderByFinishedAtDesc()).thenReturn(Optional.of(testBreakdown));

        when(breakdownRepository.countByFinishedAtBetween(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(1L)
                .thenReturn(5L)
                .thenReturn(10L);

        when(breakdownRepository.getAverageBreakdownDurationInMinutes()).thenReturn(120.5);

        BreakdownDTOs.BreakdownStatsDTO stats = breakdownService.getBreakdownStats();

        assertEquals(5L, stats.daysSinceLastBreakdown());
        assertEquals(1L, stats.breakdownsLastWeek());
        assertEquals(5L, stats.breakdownsLastMonth());
        assertEquals(10L, stats.breakdownsCurrentYear());
        assertEquals(120.5, stats.averageBreakdownDurationMinutes());
    }
}