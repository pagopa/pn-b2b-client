package it.pagopa.pn.cucumber.steps.delayer.utils;

import it.pagopa.pn.cucumber.steps.delayer.DelayerStepsOld;
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

    public static Map<String, List<DelayerPaperDelivery>> groupByDriver(List<DelayerPaperDelivery> notifications) {
        Map<String, List<DelayerPaperDelivery>> byDriverKey = new HashMap<>();
        for (DelayerPaperDelivery notification : notifications) {
            String driverKey = getDriverKey(notification);
            byDriverKey.computeIfAbsent(driverKey, k -> new ArrayList<>()).add(notification);
        }
        return byDriverKey;
    }

    public static String getDriverKey(DelayerPaperDelivery n) {
        return String.join("~", n.getUnifiedDeliveryDriver(), n.getProvince());
    }

    public Integer getDriverCapacity(String driverId) {
        if (driverId == null || driverId.split("~")[0].equalsIgnoreCase("null")) 
            throw new RuntimeException("Driver id not found");

        Integer capacity = context.driverCapacityMap.get(driverId);
        if (capacity == null) throw new RuntimeException("DriverId missing: " + driverId);

        return capacity;
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

    public Integer getSenderLimit(Map.Entry<String, List<DelayerPaperDelivery>> entry) {
        Integer senderLimit = context.senderLimitMap.get(entry.getKey());
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

    public String calculateNotificationPk(WorkflowSteps workflowStep, String expectedDeliveryDate) {
        if (workflowStep == null || expectedDeliveryDate == null || expectedDeliveryDate.isEmpty())
            throw new RuntimeException("Errore nel calcolo della pk della notifica");

        return String.join("~", expectedDeliveryDate, workflowStep.name());
    }

    public String calculateNotificationSk(WorkflowSteps workflowStep, DelayerPaperDelivery n) {
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

            case EVALUATE_PRINT_CAPACITY -> {
                String priority = calculatePriority(n);
                String date = context.expectedDeliveryDate;

                return String.join("~", priority, date, requestId);
            }

            case SENT_TO_PREPARE_PHASE_2 -> {
                String date = context.expectedDeliveryDate;

                return String.join("~", date, requestId);
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

}
