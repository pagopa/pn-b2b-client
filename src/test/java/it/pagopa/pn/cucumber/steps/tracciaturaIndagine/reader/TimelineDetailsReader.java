package it.pagopa.pn.cucumber.steps.tracciaturaIndagine.reader;

import it.pagopa.pn.client.b2b.pa.domain.DynamoTableName;
import it.pagopa.pn.client.b2b.pa.service.DynamoDbService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Legge i details degli elementi di timeline direttamente da pn-Timelines.
 * <p>
 * I campi receiverAddressSourceType e hasOnSiteInvestigation non sono esposti dalle API B2B di pn-delivery-push
 * (GET della notifica inviata): quando lo saranno, basta sostituire questa classe con una lettura via API,
 * senza toccare step e feature.
 */
@Component
@RequiredArgsConstructor
public class TimelineDetailsReader {
    private static final String DETAILS = "details";
    private static final String REC_INDEX = "recIndex";
    private static final String SENT_ATTEMPT_MADE = "sentAttemptMade";

    private final DynamoDbService dynamoDbService;

    /**
     * Restituisce i details degli elementi della categoria indicata per il destinatario, con i valori convertiti in stringa.
     * Se sentAttemptMade è null non viene applicato alcun filtro sul tentativo.
     */
    public List<Map<String, String>> readDetails(String iun, String category, int recIndex, Integer sentAttemptMade) {
        List<Map<String, AttributeValue>> items = dynamoDbService.call(DynamoTableName.TIMELINE, Map.of(
                ":v_iun", AttributeValue.builder().s(iun).build(),
                ":v_category", AttributeValue.builder().s(category).build()
        )).items();
        return items.stream()
                .map(item -> item.get(DETAILS))
                .filter(details -> details != null && details.hasM())
                .map(details -> toStringMap(details.m()))
                .filter(details -> String.valueOf(recIndex).equals(details.get(REC_INDEX)))
                .filter(details -> sentAttemptMade == null || String.valueOf(sentAttemptMade).equals(details.get(SENT_ATTEMPT_MADE)))
                .toList();
    }

    private static Map<String, String> toStringMap(Map<String, AttributeValue> details) {
        Map<String, String> values = new HashMap<>();
        details.forEach((field, value) -> values.put(field, readValue(value)));
        return values;
    }

    private static String readValue(AttributeValue value) {
        if (value == null || Boolean.TRUE.equals(value.nul())) {
            return null;
        }
        if (value.s() != null) {
            return value.s();
        }
        if (value.n() != null) {
            return value.n();
        }
        if (value.bool() != null) {
            return String.valueOf(value.bool());
        }
        return value.toString();
    }
}
