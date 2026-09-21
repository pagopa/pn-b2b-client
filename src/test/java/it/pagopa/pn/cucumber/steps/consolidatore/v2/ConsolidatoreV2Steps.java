package it.pagopa.pn.cucumber.steps.consolidatore.v2;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.FullSentNotificationV29;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.normalizzatoresync.model.AddressIn;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.normalizzatoresync.model.NormalizzazioneSyncRequest;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.normalizzatoresync.model.NormalizzazioneSyncResponse;
import it.pagopa.pn.client.b2b.pa.service.NormalizzatoreService;
import it.pagopa.pn.cucumber.steps.SharedSteps;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@RequiredArgsConstructor
public class ConsolidatoreV2Steps {
    private ResponseEntity<NormalizzazioneSyncResponse> response;
    private NormalizzazioneSyncRequest request;
    private final NormalizzatoreService normalizzatoreService;
    private final SharedSteps sharedSteps;

    @Given("preparo una request di normalizzazione con:")
    public void prepareNormalizeRequest(DataTable dataTable) {

        Map<String, String> dati = dataTable.asMap(String.class, String.class);

        AddressIn addressIn = new AddressIn()
                .id(dati.get("id"))
                .provincia(dati.get("provincia"))
                .cap(dati.get("cap"))
                .localita(dati.get("localita"))
                .localitaAggiuntiva(dati.get("localitaAggiuntiva"))
                .indirizzo(dati.get("indirizzo"))
                .indirizzoAggiuntivo(dati.get("indirizzoAggiuntivo"))
                .stato(dati.get("stato"));

        request = new NormalizzazioneSyncRequest()
                .addressIn(addressIn);
    }

    @When("invoco la normalizzazione sincrona")
    public void invokeSyncNormalize() {
        response = normalizzatoreService.normalizzazioneSyncWithHttpInfo(request);
    }

    @Then("lo status code della response è {int}")
    public void verificoStatusCode(Integer expectedStatusCode) {
        assertEquals(
                expectedStatusCode.intValue(),
                response.getStatusCodeValue()
        );
    }

    @Then("il codice errore di normalizzazione è {string}")
    public void checkErroreNorm(String expectedNErroreNorm) {
        assertNotNull(response);
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getAddressOut());

        Integer actualNErroreNorm =
                response.getBody()
                        .getAddressOut()
                        .getnErroreNorm();

        if ("null".equalsIgnoreCase(expectedNErroreNorm)) {
            assertNull(actualNErroreNorm);
        } else {
            assertEquals(
                    Integer.valueOf(expectedNErroreNorm),
                    actualNErroreNorm
            );
        }
    }

    @Then("si verifica che il deliveryDetailCode {string} {is} presente in timeline")
    public void checkDeliveryDetailCode(String deliveryDetailCode, Boolean is ) {
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
