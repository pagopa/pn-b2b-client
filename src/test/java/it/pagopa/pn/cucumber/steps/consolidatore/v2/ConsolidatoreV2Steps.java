package it.pagopa.pn.cucumber.steps.consolidatore.v2;
import io.cucumber.java.en.Then;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.FullSentNotificationV29;
import it.pagopa.pn.cucumber.steps.SharedSteps;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import java.util.Map;
import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@RequiredArgsConstructor
public class ConsolidatoreV2Steps {
    private Integer statusCode;
    private final SharedSteps sharedSteps;

    private String nullableValue(Map<String, String> dati, String key) {
        String value = dati.get(key);
        if (value == null || value.isBlank() || "null".equalsIgnoreCase(value))
            return null;
        return value;
    }

    @Then("si verifica che il deliveryDetailCode {string} {is} presente in timeline")
    public void checkDeliveryDetailCode(String deliveryDetailCode, Boolean is) {
        try {
            FullSentNotificationV29 fullSentNotification = sharedSteps.getSentNotificationLastVersion();
            boolean isPresent = fullSentNotification.getTimeline().stream().anyMatch(elem ->
                    elem.getDetails() != null
                            && elem.getDetails().getDeliveryDetailCode() != null
                            && elem.getDetails().getDeliveryDetailCode().equals(deliveryDetailCode)
            );
            String result = (isPresent ? "Trovato elemento con deliveryDetailCode "
                    : "Non trovato elemento con deliveryDetailCode ") + deliveryDetailCode;
            assertThat(isPresent).as(result).isEqualTo(is);
        } catch (AssertionError assertionError) {
            sharedSteps.throwAssertionErrorWithIUN(assertionError);
        }
    }
}
