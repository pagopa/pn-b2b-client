package it.pagopa.pn.cucumber.steps.fatturazioneComBo.model;

import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.Map;
import java.util.Optional;

/**
 * Record di una tabella di fatturazione (pn-TimelinesForInvoicing o pn-InformalTimelinesForInvoicing).
 * Il record contiene tutti i campi dell'elemento di timeline fatturato più le chiavi di fatturazione.
 * I nomi "invoincingTimestamp..." riproducono un refuso voluto della struttura della tabella.
 *
 * @param invoicingTimestamp istante di fatturazione: è quello dell'elemento che ha reso fatturabile la notifica,
 *                           anche per gli invii cartacei collegati
 * @param ttl                scadenza del record in secondi epoch
 */
public record InvoicingRecord(String paIdInvoicingDay,
                              String timelineElementId,
                              String category,
                              String invoicingDay,
                              String invoicingTimestamp,
                              Integer notificationCost,
                              Long ttl) {

    public static InvoicingRecord from(Map<String, AttributeValue> item) {
        Map<String, AttributeValue> details = Optional.ofNullable(item.get("details"))
                .filter(AttributeValue::hasM)
                .map(AttributeValue::m)
                .orElse(Map.of());
        return new InvoicingRecord(
                item.get("paId_invoicingDay").s(),
                string(item, "timelineElementId"),
                string(item, "category"),
                string(item, "invoicingDay"),
                string(item, "invoincingTimestamp"),
                DynamoValues.integer(details.get("notificationCost")),
                DynamoValues.longValue(item.get("ttl")));
    }

    private static String string(Map<String, AttributeValue> item, String attribute) {
        return Optional.ofNullable(item.get(attribute)).map(AttributeValue::s).orElse(null);
    }
}
