package it.pagopa.pn.cucumber.steps.delayer.utils;

import it.pagopa.pn.cucumber.steps.delayer.model.DelayerContext;
import it.pagopa.pn.cucumber.steps.delayer.model.DelayerPaperDelivery;
import it.pagopa.pn.cucumber.steps.delayer.model.enums.WorkflowSteps;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@RequiredArgsConstructor
public class DelayerPaperDeliveryUtils {

    private static final String UNIFIED_DRIVER_SCONOSCIUTO = "driverSconosciuto";
    private final DelayerContext context;

    public static String getUnifiedDeliveryDriverKey(DelayerPaperDelivery n) {
        String driver = n.getUnifiedDeliveryDriver();
        String province = n.getProvince();

        if (driver == null || driver.isBlank()) {
            throw new IllegalArgumentException("UnifiedDeliveryDriver mancante o vuoto");
        }
        if (province == null || province.isBlank()) {
            throw new IllegalArgumentException("Provincia mancante o vuota");
        }

        return driver + "~" + province;
    }

    public static String getCapDeliveryDriverKey(DelayerPaperDelivery n) {
        String driver = n.getUnifiedDeliveryDriver();
        String cap = n.getCap();

        if (driver == null || driver.isBlank()) {
            throw new IllegalArgumentException("UnifiedDeliveryDriver mancante o vuoto");
        }
        if (cap == null || cap.isBlank()) {
            throw new IllegalArgumentException("CAP mancante o vuoto");
        }

        return driver + "~" + cap;
    }

    public int getAvailableDriverCapacity(String driverId) {
        return findCapacityOrMap(driverId, false, false);
    }

    public int getInitalDriverCapacity(String driverId) {
        return findCapacityOrMap(driverId, false, true);
    }

    // Qui deve scalare la capacità provinciale invece di quella di cap
    public void setAvailableDriverCapacity(String driverId, int capacity) {
        String[] parts = splitDriverId(driverId);
        String location = parts[1];

        if (capacity < 0) {
            throw new IllegalArgumentException("La capacità non può essere negativa");
        }

        if (!isValidProvince(location) && !isValidCap(location)) {
            throw new IllegalArgumentException("Il secondo token non è una provincia o un cap valido o di test: " + location);
        }

        Map<String, Integer> capMap = findCapacityOrMap(driverId, true, false);
        capMap.put(driverId, capacity);
    }

    public void setInitialDriverCapacity(String driverId, int capacity) {
        String[] parts = splitDriverId(driverId);
        String location = parts[1];

        if (capacity < 0) {
            throw new IllegalArgumentException("La capacità non può essere negativa");
        }

        if (!isValidProvince(location) && !isValidCap(location)) {
            throw new IllegalArgumentException("Il secondo token non è una provincia o un cap valido o di test: " + location);
        }

        Map<String, Integer> capMap = findCapacityOrMap(driverId, true, true);
        capMap.put(driverId, capacity);
    }

    public List<DelayerPaperDelivery> deepCopyAndUpdateKeys(List<DelayerPaperDelivery> source, WorkflowSteps step, String deliveryDate) {
        return source.stream()
                .map(DelayerPaperDelivery::new)
                .peek(n -> {
                    n.setPk(calculatePk(step, deliveryDate));
                    n.setSk(calculateSk(step, n));
                    n.setPriority(calculatePriority(n));
                })
                .toList();
    }

    public boolean hasDriver(String driverId) {
        if (driverId == null || !driverId.contains("~")) {
            return false;
        }

        String[] parts = driverId.split("~");
        if (parts.length != 2) {
            return false;
        }

        String driverKey = parts[0];
        String location = parts[1]; // può essere provincia o cap

        Map<String, Map<String, Integer>> driverMap = context.driverCapacityMap;

        if (isValidProvince(location)) {
            return driverMap.containsKey(driverId);
        } else if (isValidCap(location)) {
            return driverMap.values().stream()
                    .anyMatch(capMap -> capMap.containsKey(location));
        }

        return false;
    }

    public static boolean hasSeedInRequestId(String seed, DelayerPaperDelivery n) {
        Pattern pattern = Pattern.compile("^" + Pattern.quote(seed) + "\\d+$");
        return pattern.matcher(n.getRequestId()).matches();
    }

    public static String extractSeed(DelayerPaperDelivery n) {
        if (n == null || n.getRequestId() == null || n.getRequestId().isBlank()) {
            throw new IllegalArgumentException("Notifica o requestId nullo/vuoto");
        }

        Pattern pattern = Pattern.compile("^(.+?)\\d+$"); // Gruppo 1: tutto prima delle cifre finali
        Matcher matcher = pattern.matcher(n.getRequestId());

        if (!matcher.matches()) {
            throw new IllegalArgumentException("RequestId non rispetta il formato atteso: " + n.getRequestId());
        }

        return matcher.group(1); // Il seed
    }

    public static WorkflowSteps extractWorkflowStep(DelayerPaperDelivery n) {
        if (n.getPk() == null || n.getPk().isBlank()) {
            throw new IllegalArgumentException("PK mancante: impossibile estrarre il workflow step");
        }

        // Divido PK per "~"
        String[] tokens = n.getPk().split("~");
        String token = tokens[1];

        try {
            return WorkflowSteps.valueOf(token);
        } catch (IllegalArgumentException e) {
            throw new IllegalStateException("Workflow step non riconosciuto dal PK: " + n.getPk(), e);
        }
    }

    public List<String> compare(List<DelayerPaperDelivery> expected, List<DelayerPaperDelivery> actual) {
        List<Map<String, String>> expectedList = toComparableMapList(expected == null ? List.of() : expected);
        List<Map<String, String>> actualList = toComparableMapList(actual == null ? List.of() : actual);

        List<String> problems = new ArrayList<>();

        // Raggruppa per requestId per gestire eventuali duplicati senza perdere informazione
        Map<String, List<Map<String, String>>> expectedByReqId = new LinkedHashMap<>();
        for (Map<String, String> m : expectedList) {
            String id = m.get("requestId");
            if (id == null || id.isBlank()) {
                problems.add("Expected senza requestId: " + m);
                continue;
            }
            expectedByReqId.computeIfAbsent(id, k -> new ArrayList<>()).add(m);
        }

        Map<String, List<Map<String, String>>> actualByReqId = new LinkedHashMap<>();
        for (Map<String, String> m : actualList) {
            String id = m.get("requestId");
            if (id == null || id.isBlank()) {
                problems.add("Actual senza requestId: " + m);
                continue;
            }
            actualByReqId.computeIfAbsent(id, k -> new ArrayList<>()).add(m);
        }

        // Segnala duplicati "veri": stesso requestId con contenuti diversi
        expectedByReqId.forEach((reqId, items) -> {
            if (items.size() > 1) {
                Set<Map<String, String>> distinct = new LinkedHashSet<>(items);
                if (distinct.size() > 1) {
                    problems.add("Duplicato in expected per requestId=" + reqId + " con payload differenti");
                }
            }
        });

        actualByReqId.forEach((reqId, items) -> {
            if (items.size() > 1) {
                Set<Map<String, String>> distinct = new LinkedHashSet<>(items);
                if (distinct.size() > 1) {
                    problems.add("Duplicato in actual per requestId=" + reqId + " con payload differenti");
                }
            }
        });

        // Confronto actual vs expected per requestId
        for (Map.Entry<String, List<Map<String, String>>> e : actualByReqId.entrySet()) {
            String reqId = e.getKey();
            List<Map<String, String>> aCandidates = e.getValue();
            List<Map<String, String>> eCandidates = expectedByReqId.get(reqId);

            if (eCandidates == null || eCandidates.isEmpty()) {
                problems.add("Actual non atteso (requestId=" + reqId + ")");
                continue;
            }

            // Se esiste almeno una coppia identica, quel requestId è ok
            boolean anyExactMatch = aCandidates.stream().anyMatch(eCandidates::contains);
            if (anyExactMatch) {
                continue;
            }

            // Altrimenti dettaglia differenze usando il primo elemento come riferimento
            Map<String, String> aMap = aCandidates.get(0);
            Map<String, String> eMap = eCandidates.get(0);

            Set<String> keys = new LinkedHashSet<>();
            keys.addAll(eMap.keySet());
            keys.addAll(aMap.keySet());

            List<String> diffs = new ArrayList<>();
            for (String k : keys) {
                String ev = eMap.get(k);
                String av = aMap.get(k);
                if (!Objects.equals(ev, av)) {
                    diffs.add(k + " [expected=" + ev + ", actual=" + av + "]");
                }
            }

            if (!diffs.isEmpty()) {
                problems.add("Differenze per requestId=" + reqId + " -> " + diffs);
            }
        }

        // Missing in actual
        for (String reqId : expectedByReqId.keySet()) {
            if (!actualByReqId.containsKey(reqId)) {
                problems.add("Atteso ma non presente in actual (requestId=" + reqId + ")");
            }
        }

        return problems;
    }

    public static String getSenderKey(DelayerPaperDelivery n) {
        return String.join("~", n.getSenderPaId(), n.getProductType(), n.getProvince());
    }

    public static String extractDeliveryDate(DelayerPaperDelivery n) {
        String[] parts = n.getPk().split("~");
        return (parts.length > 0) ? parts[0] : "";
    }

    public static Map<String, List<DelayerPaperDelivery>> groupBySender(List<DelayerPaperDelivery> notifications) {
        Map<String, List<DelayerPaperDelivery>> bySenderKey = new HashMap<>();
        for (DelayerPaperDelivery notification : notifications) {
            String senderKey = getSenderKey(notification);
            bySenderKey.computeIfAbsent(senderKey, k -> new ArrayList<>()).add(notification);
        }
        return bySenderKey;
    }

    public Map<String, List<DelayerPaperDelivery>> groupByUnifiedDeliveryDriver(List<DelayerPaperDelivery> notifications) {
        Map<String, List<DelayerPaperDelivery>> byDriverKey = new HashMap<>();
        if (notifications == null || notifications.isEmpty()) {
            return byDriverKey;
        }

        for (DelayerPaperDelivery n : notifications) {
            String senderKey = getUnifiedDeliveryDriverKey(n);
            WorkflowSteps step = extractWorkflowStep(n);
            String deliveryDate = extractDeliveryDate(n);

            // Calcola il blocco da aggiungere (può anche essere immutabile, non è un problema)
            List<DelayerPaperDelivery> chunk =
                    deepCopyAndUpdateKeys(Collections.singletonList(n), step, deliveryDate);

            // Assicura una lista MUTABILE nel map value e poi aggiunge
            byDriverKey.computeIfAbsent(senderKey, k -> new ArrayList<>()).addAll(chunk);
        }

        return byDriverKey;
    }

    public String[] splitDriverKey(String driverKey) {
        String[] parts = driverKey.split("~");
        if (parts.length != 2) {
            throw new IllegalArgumentException("Formato driverId non valido. Atteso 'driver~location': " + driverKey);
        }

        String driver = parts[0];
        String location = parts[1];

        if (!isValidProvince(location) && !isValidCap(location)) {
            throw new IllegalArgumentException("Il secondo token non è una provincia o un cap valido o di test: " + location);
        }

        return parts;
    }

    public Integer getSenderLimit(String senderKey) {
        Integer senderLimit = context.senderLimitMap.get(senderKey);
        if (senderLimit == null)
            throw new RuntimeException("Sender limit not found");

        return senderLimit;
    }

    public void setSenderLimit(String senderKey, int limit) {
        if (senderKey == null || senderKey.isBlank()) {
            throw new IllegalArgumentException("Sender key non può essere nulla o vuota");
        }

        if (limit < 0) {
            throw new IllegalArgumentException("Il limite del mittente non può essere negativo");
        }

        context.senderLimitMap.put(senderKey, limit);

        Integer stored = context.senderLimitMap.get(senderKey);
        if (stored == null || stored != limit) {
            throw new IllegalStateException("Errore nell'impostazione del limite per il sender: " + senderKey);
        }
    }

    public boolean isMittenteCensito(String senderKey) {

        if (senderKey == null || senderKey.isBlank()) {
            throw new IllegalStateException("SenderKey mancante o vuoto: " + senderKey);
        }

        String[] parts = senderKey.split("~");
        if (parts.length == 0) {
            throw new IllegalStateException("Formato senderKey non valido: " + senderKey);
        }

        String paId = parts[0];

        if (paId == null || paId.isBlank()) {
            throw new AssertionError(String.format("La SenderKey (%s) ha una paId vuota: %s", senderKey, paId));
        }

        return !paId.equalsIgnoreCase("unknow");
    }

    public boolean isDriverCensito(String driverKey) {

        if (driverKey == null || driverKey.isBlank()) {
            throw new IllegalStateException("SenderKey mancante o vuoto: " + driverKey);
        }

        String[] parts = driverKey.split("~");
        if (parts.length == 0) {
            throw new IllegalStateException("Formato driverKey non valido: " + driverKey);
        }

        String driver = parts[0];

        if (driver == null || driver.isBlank()) {
            throw new AssertionError(String.format("La driverKey (%s) ha una identificativo vuoto: %s", driverKey, driver));
        }

        return !driver.equalsIgnoreCase("unknow");
    }

    public static List<DelayerPaperDelivery> sortByPriority(List<DelayerPaperDelivery> notifiche) {
        List<DelayerPaperDelivery> rs = new ArrayList<>();
        List<DelayerPaperDelivery> secondi = new ArrayList<>();
        List<DelayerPaperDelivery> altri = new ArrayList<>();
        List<DelayerPaperDelivery> comunicazioniBonarie = new ArrayList<>();

        for (DelayerPaperDelivery n : notifiche) {
            String tipo = n.getProductType();
            int att = Integer.parseInt(n.getAttempt());
            if ("RS".equalsIgnoreCase(tipo) && !n.isInformalCommunication()) {
                rs.add(n);
            } else if (n.isInformalCommunication()) {
                comunicazioniBonarie.add(n);
            } else if (att == 1) {
                secondi.add(n);
            } else {
                altri.add(n);
            }
        }

        Comparator<DelayerPaperDelivery> byPrepare = Comparator.comparing(d -> parseDate(d.getPrepareRequestDate()));
        Comparator<DelayerPaperDelivery> bySenderPriorityAndNotification =
                Comparator.comparingInt((DelayerPaperDelivery d) -> Integer.parseInt(d.getSenderPriority()))
                        .reversed()
                        .thenComparing(d -> parseDate(d.getNotificationSentAt()));

        rs.sort(byPrepare);
        secondi.sort(byPrepare);
        altri.sort(bySenderPriorityAndNotification);
        comunicazioniBonarie.sort(byPrepare);

        List<DelayerPaperDelivery> ordinati = new ArrayList<>();
        ordinati.addAll(rs);
        ordinati.addAll(secondi);
        ordinati.addAll(altri);
        ordinati.addAll(comunicazioniBonarie);
        return ordinati;
    }

    private static LocalDateTime parseDate(String dateStr) {
        try {
            return LocalDateTime.parse(dateStr, DateTimeFormatter.ISO_DATE_TIME);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Data non valida: " + dateStr, e);
        }
    }

    public static String getNextMonday(int weeksToAdd) {
        LocalDate nextMonday = LocalDate.now().with(DayOfWeek.MONDAY).plusWeeks(weeksToAdd);
        return nextMonday.format(DateTimeFormatter.ISO_LOCAL_DATE);
    }

    public static String getNextMondayFromDate(String date, int weeksToAdd) {
        LocalDate localDate = LocalDate.parse(date, DateTimeFormatter.ISO_LOCAL_DATE).plusWeeks(weeksToAdd);
        return localDate.format(DateTimeFormatter.ISO_LOCAL_DATE);
    }

    public static String getPreviousMondayFromDate(String date, int weeksToRemove) {
        LocalDate localDate = LocalDate.parse(date, DateTimeFormatter.ISO_LOCAL_DATE).minusWeeks(weeksToRemove);
        return localDate.format(DateTimeFormatter.ISO_LOCAL_DATE);
    }

    public static String getCurrentMonday() {
        LocalDate nextMonday = LocalDate.now().with(DayOfWeek.MONDAY);
        return nextMonday.format(DateTimeFormatter.ISO_LOCAL_DATE);
    }

    public String calculatePk(WorkflowSteps workflowStep, String expectedDeliveryDate) {
        if (workflowStep == null || expectedDeliveryDate == null || expectedDeliveryDate.isEmpty())
            throw new RuntimeException("Errore nel calcolo della pk della notifica");

        return String.join("~", expectedDeliveryDate, workflowStep.name());
    }

    public String calculateSk(WorkflowSteps workflowStep, DelayerPaperDelivery n) {
        String requestId = n.getRequestId();

        switch (workflowStep) {

            case EVALUATE_SENDER_LIMIT -> {
                String date = resolveReferenceDate(n);
                String province = n.getProvince();
                return String.join("~", province, date, requestId);
            }

            case EVALUATE_SENDER_PRIORITY, EVALUATE_DRIVER_CAPACITY, EVALUATE_RESIDUAL_CAPACITY -> {
                String driver = n.getUnifiedDeliveryDriver();
                String province = n.getProvince();
                String priority = calculatePriority(n);
                String refIso = resolveReferenceDate(n);
                return String.join("~", driver, province, priority, refIso, requestId);
            }

            case EVALUATE_PRINT_CAPACITY -> {
                String priority = calculatePriority(n);
                String date = n.getVirtualNotificationSentAt() != null ? n.getVirtualNotificationSentAt() : n.getPrepareRequestDate();
                return String.join("~", priority, date, requestId);
            }

            case  SENT_TO_PREPARE_PHASE_2 -> {
                String priority = calculatePriority(n);
                String date = n.getPrepareRequestDate();
                return String.join("~", priority, date, requestId);
            }

            default -> throw new IllegalArgumentException("Unsupported workflowStep: " + workflowStep);
        }
    }

    public String calculatePriority(DelayerPaperDelivery n) {
        String key = String.format("PRODUCT_%s.ATTEMPT_%d.%s", n.getProductType(), Integer.parseInt(n.getAttempt()),
                n.isInformalCommunication() ? "INFORMAL" : "LEGAL");

        for (Map.Entry<String, List<String>> entry : context.priorityConfigMap.entrySet()) {
            if (entry.getValue().contains(key)) {
                return entry.getKey();
            }
        }

        throw new IllegalStateException(String.format(
                "Priorità non trovata per la chiave: %s. Controlla la configurazione dei parametri.", key));
    }

    public static int calculateLimitByComparativo(String compare, int limit) {
        return switch (compare) {
            case "almeno", "esattamente" -> limit;
            case "inferiore" -> limit - 1;
            default -> throw new IllegalArgumentException("Il comparativo non è valido: " + compare);
        };
    }

    public boolean isValidCap(String cap) {
        return (cap != null && cap.matches("^\\d{5}$")) || isValidTestCap(cap);
    }

    private boolean isValidTestCap(String cap) {
        return cap != null && cap.matches("^CAP\\d+_P\\d+$");
    }

    public boolean isValidProvince(String value) {
        return (value != null && value.matches("^[A-Z]{2}$")) || isValidTestProvince(value);
    }

    private boolean isValidTestProvince(String value) {
        return value != null && value.matches("^P\\d+$");
    }

    private String[] splitDriverId(String driverId) {
        if (driverId == null || driverId.isBlank() || !driverId.contains("~")) {
            throw new IllegalArgumentException("Driver ID non valido o mancante: " + driverId);
        }

        String[] parts = driverId.split("~");
        if (parts.length != 2) {
            throw new IllegalArgumentException("Formato driverId non valido (atteso 'driver~location'): " + driverId);
        }

        return parts;
    }

    private List<Map<String, String>> toComparableMapList(List<DelayerPaperDelivery> list) {
        return list.stream()
                .map(n -> {
                    Map<String, String> map = new LinkedHashMap<>();
                    WorkflowSteps step = extractWorkflowStep(n);

                    map.put("pk", n.getPk());
                    map.put("sk", n.getSk());
                    map.put("requestId", n.getRequestId());
                    map.put("notificationSentAt", n.getNotificationSentAt());
                    map.put("prepareRequestDate", n.getPrepareRequestDate());
                    map.put("productType", n.getProductType());
                    map.put("senderPaId", n.getSenderPaId());
                    map.put("province", n.getProvince());
                    map.put("cap", n.getCap());
                    map.put("attempt", n.getAttempt());
                    map.put("iun", n.getIun());
                    if (step.getIndex() > 0 && !n.getUnifiedDeliveryDriver().equals(UNIFIED_DRIVER_SCONOSCIUTO))
                        map.put("unifiedDeliveryDriver", n.getUnifiedDeliveryDriver());

                    return map;
                }).toList();
    }

    private String resolveReferenceDate(DelayerPaperDelivery n) {
        if (!Objects.isNull(n.getVirtualNotificationSentAt()) && !n.getVirtualNotificationSentAt().isBlank()) {
            return n.getVirtualNotificationSentAt();
        }

        boolean isRsOrSecondAttempt = n.isRS() || n.isSecondAttempt();
        return isRsOrSecondAttempt ? n.getNotificationSentAt() : n.getPrepareRequestDate();
    }

    @SuppressWarnings("unchecked")
    private <T> T findCapacityOrMap(String driverId, boolean returnMap, boolean initial) {
        if (driverId == null || driverId.isBlank() || !driverId.contains("~")) {
            throw new IllegalArgumentException("Driver ID non valido o mancante: " + driverId);
        }

        String[] parts = driverId.split("~");
        if (parts.length != 2) {
            throw new IllegalArgumentException("Formato driverId non valido. Atteso 'driver~location': " + driverId);
        }

        String driver = parts[0];
        String location = parts[1];

        // Caso: provincia
        if (isValidProvince(location)) {
            Map<String, Integer> capMap = initial ? context.driverCapacityMap.get(driverId) : context.usedDriverCapacityMap.get(driverId);
            if (capMap == null) {
                throw new IllegalStateException("Nessuna mappa trovata per driver/provincia: " + driverId);
            }

            return (T) (returnMap ? capMap : capMap.get(driverId));
        }

        // Caso: CAP o CAP di test
        if (isValidCap(location)) {
            String fullKey = driver + "~" + location;
            var targetMap = initial ? context.driverCapacityMap : context.usedDriverCapacityMap;

            return targetMap.values().stream()
                    .filter(capMap -> capMap.containsKey(fullKey))
                    .findFirst()
                    .map(capMap -> {
                        if (returnMap) {
                            return (T) capMap;
                        } else {
                            Integer capacity = capMap.get(fullKey);
                            if (capacity == null) {
                                throw new IllegalStateException("Capacità non trovata per chiave '%s'".formatted(fullKey));
                            }
                            return (T) capacity;
                        }
                    })
                    .orElseThrow(() -> new IllegalStateException(
                            "Nessuna mappa trovata per driver '%s' con CAP '%s'".formatted(driver, location)));
        }

        throw new IllegalArgumentException("Il secondo token non è una provincia o CAP valido: " + location);
    }
}
