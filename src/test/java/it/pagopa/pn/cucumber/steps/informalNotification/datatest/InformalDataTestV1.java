package it.pagopa.pn.cucumber.steps.informalNotification.datatest;


import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.internalb2bpainformal.model.DigitalAddressSource;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.internalb2bpainformal.model.InformalTimelineElementDetailsV1;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.internalb2bpainformal.model.InformalTimelineElementV1;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.internalb2bpainformal.model.ResponseStatus;
import lombok.Data;

import java.util.Map;

@Data
public class InformalDataTestV1 {

    private InformalTimelineElementV1 timelineElement;

    public static InformalDataTestV1 convertMap(Map<String, String> data) {

        if (data == null || data.isEmpty()) {
            return null;
        }
        if (data.size() == 1 && data.get("NULL") != null) {
            return null;
        }

        try {

            String recIndex = data.get("details_recIndex");
            String responseStatus = data.get("details_responseStatus");
            String digitalAddressSource = data.get("details_digitalAddressSource");
            String sentAttemptMade = data.get("details_sentAttemptMade");
            String sourceElementId = data.get("details_sourceElementId");

            InformalDataTestV1 result = new InformalDataTestV1();

            InformalTimelineElementDetailsV1 details = new InformalTimelineElementDetailsV1()
                    .recIndex(recIndex != null ? Integer.valueOf(recIndex) : null)
                    .responseStatus(responseStatus != null ? ResponseStatus.valueOf(responseStatus) : null)
                    .digitalAddressSource(digitalAddressSource != null ? DigitalAddressSource.valueOf(digitalAddressSource) : null)
                    .sentAttemptMade(sentAttemptMade != null ? Integer.valueOf(sentAttemptMade) : null)
                    .sourceElementId(sourceElementId != null ? sourceElementId : null);

            InformalTimelineElementV1 element = new InformalTimelineElementV1().details(details);

            result.setTimelineElement(element);

            return result;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}