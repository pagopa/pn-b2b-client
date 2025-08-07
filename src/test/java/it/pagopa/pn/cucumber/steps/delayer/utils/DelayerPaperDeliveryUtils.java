package it.pagopa.pn.cucumber.steps.delayer.utils;

import it.pagopa.pn.cucumber.steps.delayer.model.DelayerContext;
import it.pagopa.pn.cucumber.steps.delayer.model.DelayerPaperDelivery;
import it.pagopa.pn.cucumber.steps.delayer.model.enums.WorkflowSteps;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

public class DelayerPaperDeliveryUtils {
    
    private final DelayerContext context;

    public DelayerPaperDeliveryUtils(DelayerContext context) {
        this.context = context;
    }

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

    public int getDriverCapacity(String driverId) {
        return findCapacityOrMap(driverId, false);
    }

    public void setDriverCapacity(String driverId, int capacity) {
        Map<String, Integer> capMap = findCapacityOrMap(driverId, true);
        capMap.put(driverId, capacity);
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

        Map<String, Map<String, Integer>> driverMap = context.driverCapCapacityMap;

        if (isProvince(location)) {
            return driverMap.containsKey(driverId);
        } else if (isValidCap(location) || isValidTestCap(location)) {
            return driverMap.values().stream()
                    .anyMatch(capMap -> capMap.containsKey(location));
        }

        return false;
    }

    public static String getSenderKey(DelayerPaperDelivery n) {
        return String.join("~", n.getSenderPaId(), n.getProductType(), n.getProvince());
    }

    public static Map<String, List<DelayerPaperDelivery>> groupBySender(List<DelayerPaperDelivery> notifications) {
        Map<String, List<DelayerPaperDelivery>> bySenderKey = new HashMap<>();
        for (DelayerPaperDelivery notification : notifications) {
            String senderKey = getSenderKey(notification);
            bySenderKey.computeIfAbsent(senderKey, k -> new ArrayList<>()).add(notification);
        }
        return bySenderKey;
    }

    public Integer getSenderLimit(String senderKey) {
        Integer senderLimit = context.senderLimitMap.get(senderKey);
        if (senderLimit == null)
            throw new RuntimeException("Sender limit not found");

        return senderLimit;
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
        
        return context.senderLimitMap.containsKey(senderKey);
    }

    public static List<DelayerPaperDelivery> sortByPriority(List<DelayerPaperDelivery> notifiche) {
        List<DelayerPaperDelivery> rs = new ArrayList<>();
        List<DelayerPaperDelivery> secondi = new ArrayList<>();
        List<DelayerPaperDelivery> altri = new ArrayList<>();

        for (DelayerPaperDelivery n : notifiche) {
            String tipo = n.getProductType();
            int att = Integer.parseInt(n.getAttempt());
            if ("RS".equalsIgnoreCase(tipo)) {
                rs.add(n);
            } else if (att == 1) {
                secondi.add(n);
            } else {
                altri.add(n);
            }
        }

        Comparator<DelayerPaperDelivery> byPrepare = Comparator.comparing(d -> parseDate(d.getPrepareRequestDate()));
        Comparator<DelayerPaperDelivery> byNotification = Comparator.comparing(d -> parseDate(d.getNotificationSentAt()));

        rs.sort(byPrepare);
        secondi.sort(byPrepare);
        altri.sort(byNotification);

        List<DelayerPaperDelivery> ordinati = new ArrayList<>();
        ordinati.addAll(rs);
        ordinati.addAll(secondi);
        ordinati.addAll(altri);
        return ordinati;
    }

    private static LocalDateTime parseDate(String dateStr) {
        try {
            return LocalDateTime.parse(dateStr, DateTimeFormatter.ISO_DATE_TIME);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Data non valida: " + dateStr, e);
        }
    }

    public static String getNextMonday() {
        LocalDate nextMonday = LocalDate.now().with(DayOfWeek.MONDAY).plusWeeks(1);
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
                // Usa notificationSentAt se RS o secondo tentativo, altrimenti prepareRequestDate
                boolean isRsOrSecondAttempt = n.isRS() || n.isSecondAttempt();

                String date = isRsOrSecondAttempt
                        ? n.getNotificationSentAt()
                        : n.getPrepareRequestDate();

                String province = n.getProvince();

                return String.join("~", province, date, requestId);
            }

            case EVALUATE_DRIVER_CAPACITY, EVALUATE_RESIDUAL_CAPACITY -> {
                String driver = n.getUnifiedDeliveryDriver();
                String date = context.expectedDeliveryDate;
                String province = n.getProvince();
                String priority = calculatePriority(n);

                return String.join("~", driver, province, priority, date, requestId);
            }

            case EVALUATE_PRINT_CAPACITY, SENT_TO_PREPARE_PHASE_2 -> {
                String priority = calculatePriority(n);
                String date = context.expectedDeliveryDate;

                return String.join("~", priority, date, requestId);
            }

            default -> throw new IllegalArgumentException("Unsupported workflowStep: " + workflowStep);
        }
    }

    public String calculatePriority(DelayerPaperDelivery n) {
        String key = String.format("PRODUCT_%s.ATTEMPT_%d", n.getProductType(), Integer.parseInt(n.getAttempt()));

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

    private boolean isValidCap(String cap) {
        return cap != null && cap.matches("^\\d{5}$");
    }

    private boolean isValidTestCap(String cap) {
        return cap != null && cap.matches("^CAP\\d+_P\\d+$");
    }

    private boolean isProvince(String value) {
        return value != null && value.matches("^[A-Z]{2}$");
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

    @SuppressWarnings("unchecked")
    private <T> T findCapacityOrMap(String driverId, boolean returnMap) {
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
        if (isProvince(location)) {
            Map<String, Integer> capMap = context.driverCapCapacityMap.get(driverId);
            if (capMap == null) {
                throw new IllegalStateException("Nessuna mappa trovata per driver/provincia: " + driverId);
            }

            return (T) (returnMap ? capMap :
                    Integer.valueOf(capMap.values().stream()
                            .mapToInt(Integer::intValue)
                            .sum()));
        }

        // Caso: CAP o CAP di test
        if (isValidCap(location) || isValidTestCap(location)) {
            String fullKey = driver + "~" + location;

            return context.driverCapCapacityMap.values().stream()
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
