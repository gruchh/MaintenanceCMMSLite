package com.cmms.lite.config;

import com.cmms.lite.core.entity.*;
import com.cmms.lite.core.repository.BreakdownRepository;
import com.cmms.lite.core.repository.BreakdownUsedPartsRepository;
import com.cmms.lite.core.repository.MachineRepository;
import com.cmms.lite.core.repository.SparePartRepository;
import com.cmms.lite.security.entity.Role;
import com.cmms.lite.security.entity.User;
import com.cmms.lite.security.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer {

    private final MachineRepository machineRepository;
    private final SparePartRepository sparePartRepository;
    private final BreakdownRepository breakdownRepository;
    private final BreakdownUsedPartsRepository breakdownUsedPartsRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @EventListener(ApplicationReadyEvent.class)
    public void initializeData() {
        if (userRepository.count() > 0) {
            log.info("Dane już istnieją w bazie. Inicjalizacja pominięta.");
            return;
        }

        log.info("Rozpoczynanie inicjalizacji bazy danych przykładowymi danymi...");

        createUsers();
        List<Machine> machines = createMachines();
        machineRepository.saveAll(machines);
        List<SparePart> spareParts = createSpareParts();
        sparePartRepository.saveAll(spareParts);
        createBreakdowns(machines, spareParts);

        log.info("Inicjalizacja bazy danych przykładowymi danymi została zakończona pomyślnie.");
    }

    private void createUsers() {
        log.info("Tworzenie użytkowników z hierarchią ról...");
        User admin = User.builder()
                .username("admin")
                .email("admin@cmms.com")
                .password(passwordEncoder.encode("admin12345"))
                .roles(Set.of(Role.ADMIN))
                .build();

        User technician = User.builder()
                .username("technik")
                .email("technik@cmms.com")
                .password(passwordEncoder.encode("technik123"))
                .roles(Set.of(Role.TECHNICAN))
                .build();

        User subcontractor = User.builder()
                .username("podwykonawca")
                .email("podwykonawca@cmms.com")
                .password(passwordEncoder.encode("podwykonawca123"))
                .roles(Set.of(Role.SUBCONTRACTOR))
                .build();

        userRepository.saveAll(List.of(admin, technician, subcontractor));
        log.info("Utworzono {} użytkowników.", userRepository.count());
    }

    private List<Machine> createMachines() {
        List<Machine> machines = new ArrayList<>();

        Object[][] machineData = {
                {"TKR", "Tokarka CNC T-500", "DMG Mori", "Precyzyjna tokarka do produkcji seryjnej."},
                {"VOO", "Frezarka 5-osiowa V-90", "Haas", "Centrum obróbcze do skomplikowanych detali."},
                {"LCC", "Maszyna do cięcia laserowego L-3030", "Trumpf", "Wysokowydajny laser do cięcia blach stalowych."},
                {"RER", "Robot spawalniczy Fanuc R-2000iC", "Fanuc", "Zrobotyzowane stanowisko do spawania MIG/MAG."},
                {"PPP", "Piec hartowniczy P-1200", "Seco/Warwick", "Piec z kontrolowaną atmosferą do obróbki cieplnej."},
                {"UEW", "Zgrzewarka ultradźwiękowa U-50", "Branson", "Urządzenie do precyzyjnego łączenia tworzyw sztucznych."},
                {"CSD", "Maszyna inspekcyjna 3D Zeiss Contura", "Zeiss", "Współrzędnościowa maszyna pomiarowa o wysokiej dokładności."},
                {"PKK", "Prasa krawędziowa Amada HFE", "Amada", "Hydrauliczna prasa do gięcia blach o nacisku 150 ton."},
                {"SWA", "Szlifierka do wałków Jotes SWA-10", "Jotes", "Szlifierka do precyzyjnego wykańczania powierzchni zewnętrznych."},
                {"WMN", "Wiertarka wielowrzecionowa Wotan B-75", "Wotan", "Maszyna do wykonywania otworów w produkcji wielkoseryjnej."}
        };

        for (int i = 0; i < machineData.length; i++) {
            Machine machine = new Machine();
            machine.setCode((String) machineData[i][0]);
            machine.setFullName((String) machineData[i][1]);
            machine.setSerialNumber("SN-" + (String) machineData[i][0] + "-" + (2020 + i));
            machine.setManufacturer((String) machineData[i][2]);
            machine.setProductionDate(LocalDate.now().minusYears(machineData.length - i));
            machine.setDescription((String) machineData[i][3]);
            machines.add(machine);
        }

        log.info("Przygotowano {} maszyn do zapisu.", machines.size());
        return machines;
    }

    private List<SparePart> createSpareParts() {
        List<SparePart> spareParts = new ArrayList<>();
        String[] partNames = {"Bezpiecznik 2A", "Czujnik indukcyjny", "Filtr oleju hydraulicznego", "Przekaźnik bezpieczeństwa", "Stycznik mocy", "Zawór pneumatyczny", "Wężyk chłodziwa", "Uszczelka siłownika"};
        for (int i = 1; i <= 15; i++) {
            SparePart part = new SparePart();
            part.setName(partNames[i % partNames.length] + " typ " + i);
            part.setPrice(new BigDecimal(15 + i * 12).setScale(2));
            part.setProducer(i % 2 == 0 ? "Siemens" : "Festo");
            spareParts.add(part);
        }
        log.info("Przygotowano {} części zamiennych do zapisu.", spareParts.size());
        return spareParts;
    }

    private void createBreakdowns(List<Machine> machines, List<SparePart> spareParts) {
        List<Breakdown> breakdowns = new ArrayList<>();
        Random random = new Random();
        LocalDateTime now = LocalDateTime.now();

        String[] problemTypes = {"Nagły zanik", "Problem z", "Błąd", "Głośna praca", "Wyciek", "Niska wydajność", "Przegrzewanie się", "Brak komunikacji z"};
        String[] components = {"napędu osi Z", "systemu smarowania", "układu chłodzenia", "panelu sterowania HMI", "głównego wrzeciona", "magazynu narzędzi", "pompy hydraulicznej", "falownika"};
        String[] specialistActions = {"Wymieniono", "Naprawiono", "Wyczyszczono i skalibrowano", "Dokręcono połączenia i sprawdzono", "Zaktualizowano oprogramowanie", "Uzupełniono stan"};
        String[] repairedComponents = {"uszkodzony przekaźnik", "zapchany filtr", "przewód sygnałowy", "czujnik ciśnienia", "oprogramowanie sterownika", "oleju hydraulicznego"};

        for (int i = 0; i < 25; i++) {
            Breakdown breakdown = new Breakdown();
            String description = problemTypes[random.nextInt(problemTypes.length)] + " " + components[random.nextInt(components.length)] + ".";
            breakdown.setDescription(description);

            boolean isOldBreakdown = i < 20;
            if (isOldBreakdown) {
                LocalDateTime reported = now.minusDays(random.nextInt(30) + 2).withHour(random.nextInt(12) + 8);
                breakdown.setReportedAt(reported);
                breakdown.setStartedAt(reported.minusMinutes(random.nextInt(120)));
                if (random.nextInt(10) != 0) {
                    breakdown.setOpened(false);
                    breakdown.setFinishedAt(reported.plusHours(random.nextInt(10) + 1));
                    String comment = specialistActions[random.nextInt(specialistActions.length)] + " " + repairedComponents[random.nextInt(repairedComponents.length)] + ". Maszyna przetestowana, działa poprawnie.";
                    breakdown.setSpecialistComment(comment);
                } else {
                    breakdown.setOpened(true);
                    breakdown.setSpecialistComment("Oczekuje na dostawę części specjalistycznej.");
                }
            } else {
                LocalDateTime reported = now.minusHours(random.nextInt(48)).minusMinutes(random.nextInt(60));
                breakdown.setReportedAt(reported);
                breakdown.setStartedAt(reported.minusMinutes(random.nextInt(30)));
                if (random.nextInt(4) == 0) {
                    breakdown.setOpened(false);
                    breakdown.setFinishedAt(reported.plusMinutes(random.nextInt(120) + 30));
                    breakdown.setSpecialistComment("Reset sterownika rozwiązał problem. Obserwować.");
                } else {
                    breakdown.setOpened(true);
                }
            }
            breakdown.setMachine(machines.get(random.nextInt(machines.size())));
            breakdown.setTotalCost(BigDecimal.ZERO);
            breakdown.setType(BreakdownType.values()[random.nextInt(BreakdownType.values().length)]);
            breakdowns.add(breakdown);
        }
        breakdownRepository.saveAll(breakdowns);

        List<BreakdownUsedParts> usedPartsList = new ArrayList<>();
        for (Breakdown breakdown : breakdownRepository.findAll()) {
            if (!breakdown.getOpened() && breakdown.getFinishedAt() != null) {
                int numberOfParts = random.nextInt(4);
                BigDecimal totalCost = BigDecimal.ZERO;
                for (int j = 0; j < numberOfParts; j++) {
                    BreakdownUsedParts usedPart = new BreakdownUsedParts();
                    usedPart.setBreakdown(breakdown);
                    SparePart randomPart = spareParts.get(random.nextInt(spareParts.size()));
                    usedPart.setSparePart(randomPart);
                    usedPart.setQuantity(random.nextInt(2) + 1);
                    usedPartsList.add(usedPart);
                    totalCost = totalCost.add(randomPart.getPrice().multiply(new BigDecimal(usedPart.getQuantity())));
                }
                if (totalCost.compareTo(BigDecimal.ZERO) > 0) {
                    breakdown.setTotalCost(totalCost);
                    breakdownRepository.save(breakdown);
                }
            }
        }
        breakdownUsedPartsRepository.saveAll(usedPartsList);
        log.info("Utworzono {} awarii i przypisano do nich części.", breakdowns.size());
    }
}