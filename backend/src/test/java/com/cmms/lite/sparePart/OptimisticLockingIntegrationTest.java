package com.cmms.lite.sparePart;

import com.cmms.lite.breakdown.repository.BreakdownRepository;
import com.cmms.lite.machine.entity.Machine;
import com.cmms.lite.machine.repository.MachineRepository;
import com.cmms.lite.sparePart.entity.SparePart;
import com.cmms.lite.sparePart.repository.SparePartRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
class OptimisticLockingIntegrationTest {

    @Autowired
    private SparePartRepository sparePartRepository;
    @Autowired
    private BreakdownRepository breakdownRepository;
    @Autowired
    private MachineRepository machineRepository;

    private Long sparePartId;

    @BeforeEach
    void setUp() {
        Machine machine = machineRepository.save(Machine.builder()
                .code("TST")
                .fullName("Maszyna testowa")
                .serialNumber("SN-TEST-001")
                .manufacturer("TestProducer")
                .productionDate(LocalDate.now().minusYears(2))
                .build());

        SparePart sparePart = sparePartRepository.save(SparePart.builder()
                .name("Część testowa")
                .price(new BigDecimal("49.99"))
                .producer("TestProducer")
                .stockQuantity(10)
                .build());

        sparePartId = sparePart.getId();
    }

    @AfterEach
    void tearDown() {
        sparePartRepository.deleteAll();
        breakdownRepository.deleteAll();
        machineRepository.deleteAll();
    }

    @Test
    void shouldThrowOptimisticLockException_onConcurrentUpdate() {
        SparePart instanceA = sparePartRepository.findById(sparePartId).orElseThrow();
        SparePart instanceB = sparePartRepository.findById(sparePartId).orElseThrow();

        instanceA.setStockQuantity(9);
        SparePart savedA = sparePartRepository.saveAndFlush(instanceA);
        assertThat(savedA.getVersion()).isGreaterThan(instanceB.getVersion());

        instanceB.setStockQuantity(8);
        assertThatThrownBy(() -> sparePartRepository.saveAndFlush(instanceB))
                .isInstanceOf(ObjectOptimisticLockingFailureException.class);
    }
}