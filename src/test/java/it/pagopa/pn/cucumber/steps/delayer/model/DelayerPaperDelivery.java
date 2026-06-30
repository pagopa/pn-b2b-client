package it.pagopa.pn.cucumber.steps.delayer.model;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@EqualsAndHashCode(of = {"pk", "requestId"})
@NoArgsConstructor
public class DelayerPaperDelivery {

    private String pk;
    private String sk;
    private String attempt;
    private String cap;
    private String createdAt;
    private String deliveryDate;
    private String iun;
    private String notificationSentAt;
    private String prepareRequestDate;
    private String priority;
    private String productType;
    private String province;
    private String recipientId;
    private String requestId;
    private String senderPaId;
    private String tenderId;
    private String unifiedDeliveryDriver;
    private String senderPriority;
    private String virtualNotificationSentAt;

    public DelayerPaperDelivery(List<String> header, List<String> csvLine) {
        if (header == null || csvLine == null || header.size() != csvLine.size()) {
            throw new IllegalArgumentException("CSV non valido: header e riga devono avere la stessa lunghezza");
        }

        Map<String, String> rowMap = new HashMap<>();
        for (int i = 0; i < header.size(); i++) {
            rowMap.put(header.get(i).trim(), csvLine.get(i).trim());
        }

        this.requestId = requireField(rowMap, "requestId");
        this.notificationSentAt = requireField(rowMap, "notificationSentAt");
        this.prepareRequestDate = requireField(rowMap, "prepareRequestDate");
        this.productType = requireField(rowMap, "productType");
        this.senderPaId = requireField(rowMap, "senderPaId");
        this.province = requireField(rowMap, "province");
        this.cap = requireField(rowMap, "cap");
        this.attempt = requireField(rowMap, "attempt");
        this.iun = requireField(rowMap, "iun");
        this.unifiedDeliveryDriver = getField(rowMap, "unifiedDeliveryDriver");
        this.senderPriority = getField(rowMap, "senderPriority");
        if (this.senderPriority == null) {
            this.senderPriority = "0";
        }
        this.virtualNotificationSentAt = getField(rowMap, "virtualNotificationSentAt");
    }

    public DelayerPaperDelivery(JsonNode tableRecord) {
        this.pk = getField(tableRecord, "pk");
        this.sk = getField(tableRecord, "sk");
        this.requestId = requireField(tableRecord, "requestId", false);
        this.notificationSentAt = requireField(tableRecord, "notificationSentAt", false);
        this.prepareRequestDate = requireField(tableRecord, "prepareRequestDate", false);
        this.productType = requireField(tableRecord, "productType", false);
        this.senderPaId = requireField(tableRecord, "senderPaId", false);
        this.province = requireField(tableRecord, "province", false);
        this.cap = requireField(tableRecord, "cap", false);
        this.attempt = requireField(tableRecord, "attempt", false);
        this.iun = requireField(tableRecord, "iun", false);
        this.unifiedDeliveryDriver = requireField(tableRecord, "unifiedDeliveryDriver", true);
        this.priority = requireField(tableRecord, "priority", true);
        this.senderPriority = requireField(tableRecord, "senderPriority", true);
        if (this.senderPriority == null) {
            this.senderPriority = "0";
        }
        this.virtualNotificationSentAt = requireField(tableRecord, "virtualNotificationSentAt", true);
    }

    public DelayerPaperDelivery(DelayerPaperDelivery source) {
        this.pk = source.pk;
        this.sk = source.sk;
        this.requestId = source.requestId;
        this.notificationSentAt = source.notificationSentAt;
        this.prepareRequestDate = source.prepareRequestDate;
        this.productType = source.productType;
        this.senderPaId = source.senderPaId;
        this.province = source.province;
        this.cap = source.cap;
        this.attempt = source.attempt;
        this.iun = source.iun;
        this.unifiedDeliveryDriver = source.unifiedDeliveryDriver;
        this.priority = source.priority;
        this.senderPriority = source.senderPriority;
        this.virtualNotificationSentAt = source.virtualNotificationSentAt;
    }

    private String requireField(JsonNode node, String fieldName, boolean nullable) {
        JsonNode field = node.get(fieldName);
        if (field == null || field.isNull() || field.asText().isBlank()) {
            if (nullable) return null;
            else throw new IllegalArgumentException("Campo obbligatorio mancante o vuoto: " + fieldName);
        }
        return field.asText();
    }

    private String requireField(Map<String, String> rowMap, String fieldName) {
        String value = rowMap.get(fieldName);
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Campo CSV mancante o vuoto: " + fieldName);
        }
        return value;
    }

    private String getField(JsonNode node, String fieldName) {
        JsonNode field = node.get(fieldName);
        return (field != null && !field.isNull()) ? field.asText() : null;
    }

    private String getField(Map<String, String> rowMap, String fieldName) {
        String value = rowMap.get(fieldName);
        if (value == null || value.isBlank()) {
            return null;
        }
        return value;
    }

    public boolean isRS() {
        return this.getProductType().equalsIgnoreCase("RS");
    }

    public boolean isSecondAttempt() {
        return Integer.parseInt(this.getAttempt()) == 1;
    }
    public int getSenderPriorityValue() {
        if (this.senderPriority == null || this.senderPriority.isBlank()) {
            return 0;
        }
        return Integer.parseInt(this.senderPriority);
    }

    public String getEffectiveNotificationSentAt() {
        return this.virtualNotificationSentAt != null && !this.virtualNotificationSentAt.isBlank()
                ? this.virtualNotificationSentAt
                : this.notificationSentAt;
    }
}
