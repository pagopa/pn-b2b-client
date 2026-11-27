package it.pagopa.pn.cucumber.utils;

import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.LegalFactDownloadMetadataResponse;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementV28;
import it.pagopa.pn.cucumber.steps.SharedSteps;
import org.junit.jupiter.api.Assertions;

import java.util.Objects;

import static it.pagopa.pn.client.b2b.pa.domain.Costanti.SEND_ANALOG_PROGRESS;

public class LegalFactDownloadUtility {

    private final SharedSteps sharedSteps;

    public LegalFactDownloadUtility(SharedSteps sharedSteps) {
        this.sharedSteps = Objects.requireNonNull(sharedSteps, "sharedSteps non deve essere null");
    }

    public LegalFactDownloadMetadataResponse getLegalFactIdAAR(String aarType, Integer recIndex) {
        TimelineElementV28 timelineElement = sharedSteps.getSentNotificationLastVersion().getTimeline()
                .stream()
                .filter(elem -> Objects.equals(elem.getCategory().getValue(), SEND_ANALOG_PROGRESS))
                .filter(elem -> elem.getDetails() != null)
                .filter(elem -> Objects.equals(elem.getDetails().getRecIndex(), recIndex))
                .filter(elem -> elem.getDetails().getAttachments() != null && !elem.getDetails().getAttachments().isEmpty())
                .filter(elem -> Objects.equals(elem.getDetails().getDeliveryDetailCode(), "CON020"))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Nessun SEND_ANALOG_PROGRESS per recIndex=" + recIndex));

        assert timelineElement.getDetails() != null;
        String generatedAarUrl = timelineElement.getDetails().getAttachments().get(0).getUrl();

        Assertions.assertNotNull(generatedAarUrl);
        Assertions.assertFalse(generatedAarUrl.isBlank());

        String keySearch = resolveKeySearch(generatedAarUrl, aarType);

        Assertions.assertNotNull(keySearch);

        return Assertions.assertDoesNotThrow(() ->
                sharedSteps.getB2bClient().getDownloadLegalFact(sharedSteps.getNotificationIun(), keySearch));
    }

    private String resolveKeySearch(String generatedAarUrl, String aarType) {
        if (generatedAarUrl.contains(aarType)) {
            return generatedAarUrl.substring(generatedAarUrl.indexOf(aarType));
        }
        if (generatedAarUrl.contains("PN_PRINTED")) {
            return generatedAarUrl.substring(generatedAarUrl.indexOf("PN_PRINTED"));
        }
        return null;
    }
}