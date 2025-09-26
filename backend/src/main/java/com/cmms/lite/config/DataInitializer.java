package com.cmms.lite.config;

import com.cmms.lite.breakdown.entity.Breakdown;
import com.cmms.lite.breakdown.entity.BreakdownUsedParts;
import com.cmms.lite.breakdown.repository.BreakdownRepository;
import com.cmms.lite.breakdown.repository.BreakdownUsedPartsRepository;
import com.cmms.lite.breakdownType.entity.BreakdownType;
import com.cmms.lite.employee.entity.*;
import com.cmms.lite.employee.repository.EmployeeDetailsRepository;
import com.cmms.lite.employee.repository.EmployeeRepository;
import com.cmms.lite.employee.repository.EmployeeRoleRepository;
import com.cmms.lite.employeeRole.entity.EmployeeRole;
import com.cmms.lite.machine.entity.Machine;
import com.cmms.lite.machine.repository.MachineRepository;
import com.cmms.lite.security.entity.Role;
import com.cmms.lite.security.entity.User;
import com.cmms.lite.security.repository.UserRepository;
import com.cmms.lite.sparePart.entity.SparePart;
import com.cmms.lite.sparePart.repository.SparePartRepository;
import com.github.javafaker.Faker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Component
@Profile("dev")
@RequiredArgsConstructor
@Slf4j
public class DataInitializer {

    private final MachineRepository machineRepository;
    private final SparePartRepository sparePartRepository;
    private final BreakdownRepository breakdownRepository;
    private final BreakdownUsedPartsRepository breakdownUsedPartsRepository;
    private final UserRepository userRepository;
    private final EmployeeRepository employeeRepository;
    private final EmployeeRoleRepository employeeRoleRepository;
    private final EmployeeDetailsRepository employeeDetailsRepository;
    private final PasswordEncoder passwordEncoder;

    private static final Random RANDOM = new Random();
    private static final Faker FAKER = new Faker(new Locale("pl"));
    private static final List<String> ROLE_NAMES = List.of("Automatyk", "Mechanik", "Ślusarz", "Spawacz", "Elektronik", "Kierownik", "Manager");
    private static final List<String> PROBLEM_TYPES = List.of("Nagły zanik", "Problem z", "Błąd", "Głośna praca", "Wyciek", "Niska wydajność", "Przegrzewanie się", "Brak komunikacji z");
    private static final List<String> COMPONENTS = List.of("napędem osi Z", "systemem smarowania", "układem chłodzenia", "panelem sterowania HMI", "głównym wrzecionem", "magazynem narzędzi", "pompy hydraulicznej", "falownikiem");
    private static final List<String> SPECIALIST_ACTIONS = List.of("Wymieniono", "Naprawiono", "Wyczyszczono i skalibrowano", "Dokręcono połączenia i sprawdzono", "Zaktualizowano oprogramowanie", "Uzupełniono stan");
    private static final List<String> REPAIRED_COMPONENTS = List.of("uszkodzony przekaźnik", "zapchany filtr", "przewód sygnałowy", "czujnik ciśnienia", "oprogramowanie sterownika", "oleju hydraulicznego");
    private static final List<Brigade> BRIGADES = List.of(Brigade.A, Brigade.B, Brigade.C, Brigade.D, Brigade.K, Brigade.S);


    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void initializeData() {
        if (userRepository.count() > 0) {
            log.info("Dane już istnieją w bazie. Inicjalizacja pominięta.");
            return;
        }

        log.info("Rozpoczynanie inicjalizacji bazy danych przykładowymi danymi...");

        Map<String, EmployeeRole> employeeRoles = createEmployeeRoles();
        List<Employee> employees = createUsersAndEmployees(employeeRoles);
        createEmployeeDetails(employees);
        List<Machine> machines = createMachines();
        List<SparePart> spareParts = createSpareParts();
        createBreakdowns(machines, spareParts, employees);

        log.info("Inicjalizacja bazy danych przykładowymi danymi została zakończona pomyślnie.");
    }

    private Map<String, EmployeeRole> createEmployeeRoles() {
        log.info("Tworzenie ról pracowników...");
        List<EmployeeRole> roles = ROLE_NAMES.stream()
                .map(name -> EmployeeRole.builder().name(name).build())
                .collect(Collectors.toList());
        employeeRoleRepository.saveAll(roles);
        log.info("Utworzono {} ról pracowników.", roles.size());
        return roles.stream().collect(Collectors.toMap(EmployeeRole::getName, Function.identity()));
    }

    private record UserData(String firstName, String lastName, String username, String email, String password, Role role, String employeeRoleName, String avatarUrl) {
    }

    private List<Employee> createUsersAndEmployees(Map<String, EmployeeRole> roles) {
        log.info("Tworzenie użytkowników i pracowników...");

        List<UserData> usersData = List.of(
                new UserData("Adam", "Nowak", "admin", "admin@cmms.com", "admin12345", Role.ADMIN, "Manager", "https://robohash.org/admin.png?set=set4"),
                new UserData("Jan", "Kowalski", "kierownik", "kierownik@cmms.com", "kierownik123", Role.TECHNICAN, "Kierownik", "https://robohash.org/kierownik.png?set=set2"),
                new UserData("Tomasz", "Wiśniewski", "automatyk", "automatyk@cmms.com", "technik123", Role.TECHNICAN, "Automatyk", "https://robohash.org/automatyk.png"),
                new UserData("Marek", "Wójcik", "mechanik", "mechanik@cmms.com", "technik123", Role.TECHNICAN, "Mechanik", "https://robohash.org/mechanik.png"),
                new UserData("Piotr", "Kowalczyk", "elektronik", "elektronik@cmms.com", "technik123", Role.TECHNICAN, "Elektronik", "https://robohash.org/elektronik.png?set=set3"),
                new UserData("Krzysztof", "Zieliński", "slusarz", "slusarz@cmms.com", "podwykonawca123", Role.SUBCONTRACTOR, "Ślusarz", "https://robohash.org/slusarz.png?set=set5"),
                new UserData("Grzegorz", "Szymański", "spawacz", "spawacz@cmms.com", "podwykonawca123", Role.SUBCONTRACTOR, "Spawacz", "https://robohash.org/spawacz.png?set=set5")
        );

        List<User> savedUsers = new ArrayList<>();
        List<Employee> employeesToSave = new ArrayList<>();

        for (UserData data : usersData) {
            User user = User.builder()
                    .username(data.username())
                    .email(data.email())
                    .password(passwordEncoder.encode(data.password()))
                    .roles(Set.of(data.role()))
                    .build();

            User savedUser = userRepository.save(user);
            savedUsers.add(savedUser);

            employeesToSave.add(Employee.builder()
                    .user(savedUser)
                    .firstName(data.firstName())
                    .lastName(data.lastName())
                    .avatarUrl(data.avatarUrl())
                    .employeeRole(roles.get(data.employeeRoleName()))
                    .build());
        }

        List<Employee> savedEmployees = employeeRepository.saveAll(employeesToSave);
        log.info("Utworzono {} użytkowników i przypisano im role pracownicze.", savedUsers.size());
        return savedEmployees;
    }

    private void createEmployeeDetails(List<Employee> employees) {
        log.info("Tworzenie szczegółów pracowników...");
        List<EmployeeDetails> detailsToSave = new ArrayList<>();

        for (Employee employee : employees) {
            LocalDate dateOfBirth = FAKER.date().birthday(25, 60).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            LocalDate hireDate = FAKER.date().past(15 * 365, TimeUnit.DAYS).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

            Address address = new Address(
                    FAKER.address().streetAddress(),
                    FAKER.address().city(),
                    FAKER.address().zipCode(),
                    "Polska"
            );

            EmployeeDetails details = EmployeeDetails.builder()
                    .employee(employee)
                    .dateOfBirth(dateOfBirth)
                    .hireDate(hireDate)
                    .phoneNumber(FAKER.phoneNumber().cellPhone())
                    .address(address)
                    .contractEndDate(RANDOM.nextBoolean() ? hireDate.plusYears(RANDOM.nextInt(5) + 1) : null)
                    .salary(BigDecimal.valueOf(RANDOM.nextDouble(5000, 15000)).setScale(2, RoundingMode.HALF_UP))
                    .educationLevel(EducationLevel.values()[RANDOM.nextInt(EducationLevel.values().length)])
                    .fieldOfStudy(RANDOM.nextBoolean() ? FAKER.educator().course() : null)
                    .emergencyContactName(FAKER.name().fullName())
                    .emergencyContactPhone(FAKER.phoneNumber().cellPhone())
                    .brigade(getRandomElement(BRIGADES))
                    .build();
            detailsToSave.add(details);
        }

        employeeDetailsRepository.saveAll(detailsToSave);
        log.info("Utworzono szczegóły dla {} pracowników.", detailsToSave.size());
    }

    private record MachineData(String code, String fullName, String manufacturer, String description, int yearsOld) {
    }

    private List<Machine> createMachines() {
        log.info("Tworzenie maszyn...");
        List<MachineData> machinesData = List.of(
                new MachineData("TKR", "Tokarka CNC T-500", "DMG Mori", "Precyzyjna tokarka do produkcji seryjnej.", 10),
                new MachineData("VOO", "Frezarka 5-osiowa V-90", "Haas", "Centrum obróbcze do skomplikowanych detali.", 9),
                new MachineData("LCC", "Maszyna do cięcia laserowego L-3030", "Trumpf", "Wysokowydajny laser do cięcia blach stalowych.", 8),
                new MachineData("RER", "Robot spawalniczy Fanuc R-2000iC", "Fanuc", "Zrobotyzowane stanowisko do spawania MIG/MAG.", 7),
                new MachineData("PPP", "Piec hartowniczy P-1200", "Seco/Warwick", "Piec z kontrolowaną atmosferą do obróbki cieplnej.", 6),
                new MachineData("UEW", "Zgrzewarka ultradźwiękowa U-50", "Branson", "Urządzenie do precyzyjnego łączenia tworzyw sztucznych.", 5),
                new MachineData("CSD", "Maszyna inspekcyjna 3D Zeiss Contura", "Zeiss", "Współrzędnościowa maszyna pomiarowa o wysokiej dokładności.", 4),
                new MachineData("PKK", "Prasa krawędziowa Amada HFE", "Amada", "Hydrauliczna prasa do gięcia blach o nacisku 150 ton.", 3),
                new MachineData("SWA", "Szlifierka do wałków Jotes SWA-10", "Jotes", "Szlifierka do precyzyjnego wykańczania powierzchni zewnętrznych.", 2),
                new MachineData("WMN", "Wiertarka wielowrzecionowa Wotan B-75", "Wotan", "Maszyna do wykonywania otworów w produkcji wielkoseryjnej.", 1)
        );

        List<Machine> machines = machinesData.stream()
                .map(data -> Machine.builder()
                        .code(data.code())
                        .fullName(data.fullName())
                        .serialNumber("SN-" + data.code() + "-" + (LocalDate.now().getYear() - data.yearsOld()))
                        .manufacturer(data.manufacturer())
                        .productionDate(LocalDate.now().minusYears(data.yearsOld()))
                        .description(data.description())
                        .build())
                .collect(Collectors.toList());

        machineRepository.saveAll(machines);
        log.info("Utworzono {} maszyn.", machines.size());
        return machines;
    }

    private List<SparePart> createSpareParts() {
        log.info("Tworzenie części zamiennych...");
        String[] partNames = {"Bezpiecznik 2A", "Czujnik indukcyjny", "Filtr oleju hydraulicznego", "Przekaźnik bezpieczeństwa", "Stycznik mocy", "Zawór pneumatyczny", "Wężyk chłodziwa", "Uszczelka siłownika"};

        List<SparePart> spareParts = IntStream.rangeClosed(1, 15)
                .mapToObj(i -> SparePart.builder()
                        .name(partNames[i % partNames.length] + " typ " + i)
                        .price(new BigDecimal(15 + i * 12).setScale(2, RoundingMode.HALF_UP))
                        .producer(i % 2 == 0 ? "Siemens" : "Festo")
                        .build())
                .collect(Collectors.toList());

        sparePartRepository.saveAll(spareParts);
        log.info("Utworzono {} części zamiennych.", spareParts.size());
        return spareParts;
    }


    private void createBreakdowns(List<Machine> machines, List<SparePart> spareParts, List<Employee> employees) {
        log.info("Tworzenie awarii...");
        List<Breakdown> breakdownsToSave = new ArrayList<>();
        List<BreakdownUsedParts> allUsedPartsToSave = new ArrayList<>();

        List<Employee> technicians = employees.stream()
                .filter(e -> !List.of("Manager", "Kierownik").contains(e.getEmployeeRole().getName()))
                .collect(Collectors.toList());

        if (technicians.isEmpty()) {
            log.warn("Brak dostępnych techników do przypisania do awarii. Awariom nie zostaną przypisani pracownicy.");
        }

        for (int i = 0; i < 25; i++) {
            boolean isOldAndClosed = i < 20 && RANDOM.nextInt(10) != 0;
            boolean isOldAndOpen = i < 20 && !isOldAndClosed;
            boolean isOpen = !isOldAndClosed;

            LocalDateTime reportedAt = generateRandomPastDateTime(isOldAndClosed || isOldAndOpen);
            LocalDateTime startedAt = reportedAt.minusMinutes(RANDOM.nextInt(120));
            LocalDateTime finishedAt = isOpen ? null : startedAt.plusHours(RANDOM.nextInt(10) + 1);

            int teamSize = technicians.size() > 1 ? RANDOM.nextInt(2) + 1 : (technicians.isEmpty() ? 0 : 1);
            Set<Employee> assignedTeam = selectRandomTechnicians(technicians, teamSize);

            Breakdown breakdown = Breakdown.builder()
                    .description(generateRandomDescription())
                    .imageUrl(RANDOM.nextBoolean() ? "https://picsum.photos/seed/" + UUID.randomUUID() + "/800/600" : null)
                    .reportedAt(reportedAt)
                    .startedAt(startedAt)
                    .finishedAt(finishedAt)
                    .opened(isOpen)
                    .specialistComment(generateSpecialistComment(isOpen, isOldAndOpen))
                    .type(BreakdownType.values()[RANDOM.nextInt(BreakdownType.values().length)])
                    .machine(getRandomElement(machines))
                    .assignedEmployees(assignedTeam)
                    .build();

            if (!isOpen) {
                List<BreakdownUsedParts> usedPartsForThisBreakdown = createUsedPartsForBreakdown(breakdown, spareParts);
                allUsedPartsToSave.addAll(usedPartsForThisBreakdown);
                BigDecimal totalCost = usedPartsForThisBreakdown.stream()
                        .map(part -> part.getSparePart().getPrice().multiply(new BigDecimal(part.getQuantity())))
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                breakdown.setTotalCost(totalCost);
                breakdown.setUsedPartsList(usedPartsForThisBreakdown);
            } else {
                breakdown.setTotalCost(BigDecimal.ZERO);
            }
            breakdownsToSave.add(breakdown);
        }

        breakdownRepository.saveAll(breakdownsToSave);

        for (Breakdown breakdown : breakdownsToSave) {
            if (breakdown.getUsedPartsList() != null) {
                for (BreakdownUsedParts part : breakdown.getUsedPartsList()) {
                    part.setBreakdown(breakdown);
                }
                breakdownUsedPartsRepository.saveAll(breakdown.getUsedPartsList());
            }
        }
        log.info("Utworzono {} awarii i przypisano do nich części oraz pracowników.", breakdownsToSave.size());
    }

    private Set<Employee> selectRandomTechnicians(List<Employee> technicians, int count) {
        if (technicians == null || technicians.isEmpty() || count <= 0) {
            return new HashSet<>();
        }
        List<Employee> shuffledTechnicians = new ArrayList<>(technicians);
        Collections.shuffle(shuffledTechnicians);

        int actualCount = Math.min(count, shuffledTechnicians.size());

        return new HashSet<>(shuffledTechnicians.subList(0, actualCount));
    }


    private List<BreakdownUsedParts> createUsedPartsForBreakdown(Breakdown breakdown, List<SparePart> allSpareParts) {
        int numberOfParts = RANDOM.nextInt(4);
        List<BreakdownUsedParts> usedParts = new ArrayList<>();
        if (numberOfParts > 0) {
            Collections.shuffle(allSpareParts);
            for (int i = 0; i < numberOfParts; i++) {
                usedParts.add(BreakdownUsedParts.builder()
                        .breakdown(breakdown)
                        .sparePart(allSpareParts.get(i))
                        .quantity(RANDOM.nextInt(2) + 1)
                        .build());
            }
        }
        return usedParts;
    }

    private String generateRandomDescription() {
        return getRandomElement(PROBLEM_TYPES) + " " + getRandomElement(COMPONENTS) + ".";
    }

    private LocalDateTime generateRandomPastDateTime(boolean isOld) {
        int maxDaysAgo = isOld ? 30 : 2;
        int randomDays = isOld ? RANDOM.nextInt(maxDaysAgo) + 2 : RANDOM.nextInt(maxDaysAgo);
        int randomHours = RANDOM.nextInt(24);
        int randomMinutes = RANDOM.nextInt(60);
        return LocalDateTime.now().minusDays(randomDays).minusHours(randomHours).minusMinutes(randomMinutes);
    }

    private String generateSpecialistComment(boolean isOpen, boolean isWaitingForPart) {
        if (isOpen) {
            return isWaitingForPart ? "Oczekuje na dostawę części specjalistycznej." : "";
        }
        return getRandomElement(SPECIALIST_ACTIONS) + " " + getRandomElement(REPAIRED_COMPONENTS) + ". Maszyna przetestowana, działa poprawnie.";
    }

    private <T> T getRandomElement(List<T> list) {
        if (list == null || list.isEmpty()) {
            return null;
        }
        return list.get(RANDOM.nextInt(list.size()));
    }
}