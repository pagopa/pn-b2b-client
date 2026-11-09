package it.pagopa.pn.cucumber.steps.fatturazioneComBo.model;

import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Elemento della timeline interna di SEND (pn-Timelines), con i soli dati che servono alla fatturazione.
 * Il costo di notifica non è esposto dalle API: lo si legge solo da qui.
 *
 * @param recIndex         indice del destinatario; assente per gli elementi riferiti all'intera notifica (es. REQUEST_REFUSED)
 * @param notificationCost costo associato all'elemento; null se non valorizzato
 * @param refusalCodes     codici dei motivi di rifiuto, valorizzati solo per REQUEST_REFUSED
 */
public record InternalTimelineElement(String timelineElementId,
                                      String category,
                                      Instant timestamp,
                                      String paId,
                                      Integer recIndex,
                                      Integer notificationCost,
                                      List<String> refusalCodes) {

    private static final Pattern REC_INDEX_IN_ID = Pattern.compile("RECINDEX_(\\d+)");

    public static InternalTimelineElement from(Map<String, AttributeValue> item) {
        Map<String, AttributeValue> details = Optional.ofNullable(item.get("details"))
                .filter(AttributeValue::hasM)
                .map(AttributeValue::m)
                .orElse(Map.of());
        String timelineElementId = item.get("timelineElementId").s();
        return new InternalTimelineElement(
                timelineElementId,
                item.get("category").s(),
                Instant.parse(Optional.ofNullable(item.get("timestamp")).orElse(item.get("businessTimestamp")).s()),
                Optional.ofNullable(item.get("paId")).map(AttributeValue::s).orElse(null),
                Optional.ofNullable(DynamoValues.integer(details.get("recIndex")))
                        .orElseGet(() -> recIndexFromId(timelineElementId)),
                DynamoValues.integer(details.get("notificationCost")),
                refusalCodes(details));
    }

    public boolean hasCost() {
        return notificationCost != null;
    }

    private static Integer recIndexFromId(String timelineElementId) {
        Matcher matcher = REC_INDEX_IN_ID.matcher(timelineElementId);
        return matcher.find() ? Integer.valueOf(matcher.group(1)) : null;
    }

    private static List<String> refusalCodes(Map<String, AttributeValue> details) {
        AttributeValue reasons = details.get("refusalReasons");
        if (reasons == null || !reasons.hasL()) {
            return List.of();
        }
        return reasons.l().stream()
                .filter(AttributeValue::hasM)
                .map(reason -> reason.m().get("errorCode"))
                .filter(code -> code != null && code.s() != null)
                .map(AttributeValue::s)
                .toList();
    }
}
