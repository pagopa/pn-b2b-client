package it.pagopa.pn.cucumber.steps.consolidatore.v2;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.normalizzatoresync.model.AddressIn;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.normalizzatoresync.model.NormalizzazioneSyncRequest;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.normalizzatoresync.model.NormalizzazioneSyncResponse;
import it.pagopa.pn.client.b2b.pa.service.NormalizzatoreService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
@RequiredArgsConstructor
public class ConsolidatoreV2Steps {
    private ResponseEntity<NormalizzazioneSyncResponse> response;
    private NormalizzazioneSyncRequest request;
    private final NormalizzatoreService normalizzatoreService;

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
        response = normalizzatoreService.normalizzazioneSyncWithHttpInfo(
                pnAddressManagerCxId,
                xApiKey,
                request
        );
    }

    @Then("lo status code della response è {int}")
    public void verificoStatusCode(Integer expectedStatusCode) {
        assertEquals(
                expectedStatusCode.intValue(),
                response.getStatusCodeValue()
        );
    }
}
