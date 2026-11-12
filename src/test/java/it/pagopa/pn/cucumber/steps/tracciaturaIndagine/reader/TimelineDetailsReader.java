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
 * Legge da pn-Timelines perché receiverAddressSourceType e hasOnSiteInvestigation non sono esposti da B2B né dallo stream.
 */
@Component
@RequiredArgsConstructor
public class TimelineDetailsReader {
    private static final String DETAILS = "details";
    private static final String REC_INDEX = "recIndex";
    private static final String SENT_ATTEMPT_MADE = "sentAttemptMade";

    private final DynamoDbService dynamoDbService;

    /**
     * Con sentAttemptMade null non filtra sul tentativo.
     */
    public List<Map<String, String>> readDetails(String iun, String category, int recIndex, Integer sentAttemptMade) {
        List<Map<String, AttributeValue>> items = dynamoDbService.callAllPages(DynamoTableName.TIMELINE, Map.of(
                ":v_iun", AttributeValue.builder().s(iun).build(),
                ":v_category", AttributeValue.builder().s(category).build()
        ));
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
