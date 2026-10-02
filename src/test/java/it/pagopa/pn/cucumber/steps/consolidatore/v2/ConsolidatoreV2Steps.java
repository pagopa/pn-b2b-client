package it.pagopa.pn.cucumber.steps.consolidatore.v2;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.pn.client.b2b.generated.openapi.clients.address_manager.model.*;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.FullSentNotificationV29;

import it.pagopa.pn.client.b2b.pa.service.AddressManagerService;
import it.pagopa.pn.cucumber.steps.SharedSteps;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;

import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@RequiredArgsConstructor
public class ConsolidatoreV2Steps {
    private NormalizeSyncRequest request;
    private Integer statusCode;
    private final AddressManagerService addressManagerService;
    private final SharedSteps sharedSteps;

    @Given("preparo una request di normalizzazione con:")
    public void prepareNormalizeRequest(DataTable dataTable) {

        Map<String, String> dati = dataTable.asMap(String.class, String.class);
        String randomCorrelationId = "correlation-" + UUID.randomUUID();

        AnalogAddress address = new AnalogAddress()
                .addressRow(nullableValue(dati, "addressRow"))
                .addressRow2(nullableValue(dati, "addressRow2"))
                .cap(nullableValue(dati, "cap"))
                .city(nullableValue(dati, "city"))
                .city2(nullableValue(dati, "city2"))
                .pr(nullableValue(dati, "pr"))
                .country(nullableValue(dati, "country"))
                .nameRow2(nullableValue(dati, "nameRow2"));


        request = new NormalizeSyncRequest()
                .correlationId(randomCorrelationId)
                .address(address);
    }

    private String nullableValue(Map<String, String> dati, String key) {
        String value = dati.get(key);
        if (value == null || value.isBlank() || "null".equalsIgnoreCase(value))
            return null;
        return value;
    }

    @When("invoco la normalizzazione sincrona")
    public void invokeSyncNormalize() {
        statusCode = null;
        ResponseEntity<?> result = addressManagerService.normalizzazioneSyncWithHttpInfo(request);
        statusCode = result.getStatusCodeValue();
    }

    @Then("lo status code della response è {int}")
    public void checkStatusCode(Integer expectedStatusCode) {
        assertEquals(expectedStatusCode, statusCode);
    }

//    @Then("l'error code è {int}")
//    public void getErrorCode(Integer expectedErrorCode) {
//        assertEquals(
//                expectedErrorCode,
//                problem == null ? 0 : problem.getStatus()
//        );
//    }

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
