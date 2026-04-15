package it.pagopa.pn.cucumber.steps.comunicazioniBonarie;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.pn.client.b2b.generated.openapi.clients.privatepaperchannel.model.InformalPrepareRequest;
import it.pagopa.pn.client.b2b.generated.openapi.clients.privatepaperchannel.model.InformalPrepareResponse;
import it.pagopa.pn.client.b2b.generated.openapi.clients.privatepaperchannel.model.InformalProposalProductTypeEnum;
import it.pagopa.pn.client.b2b.generated.openapi.clients.privatepaperchannel.model.ShipmentCalculateRequest;
import it.pagopa.pn.client.b2b.pa.service.IPnPaperChannelClientImpl;
import it.pagopa.pn.client.b2b.radd.generated.openapi.clients.externalb2braddcoverage.model.CreateCoverageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.util.AssertionErrors.assertNotNull;

public class PaperChannelSteps {


    private final IPnPaperChannelClientImpl paperChannelClient;

    private InformalPrepareRequest informalPrepareRequest;

    private HttpStatus httpStatusCode;

    private InformalPrepareResponse informalPrepareResponse;
    private Exception encounteredException;
    public PaperChannelSteps(IPnPaperChannelClientImpl paperChannelClient) {
        this.paperChannelClient = paperChannelClient;
    }

    @Given("inizializzata una comunicazione bonaria con i parametri:")
    public void newPaperChannelInformalRequest(DataTable dataTable) {
        Map<String, String> data = dataTable.asMaps().get(0);
        informalPrepareRequest = new InformalPrepareRequest()
                .iun(data.get("iun"))
                .requestId(data.get("requestId"))
                .receiverType(data.get("receiverType"))
                .printType(data.get("printType"))
                //.attachmentUrls(null)
                .proposalProductType(InformalProposalProductTypeEnum.RS);
    }

    @Given("inizializzata una comunicazione bonaria con parametro required mancante:")
    public void newPaperChannelInformalRequestRequiredMissing(DataTable dataTable) {
        Map<String, String> data = dataTable.asMaps().get(0);
        informalPrepareRequest = new InformalPrepareRequest()
                .iun(data.get("iun"))
                .requestId(data.get("requestId"))
                .receiverType(data.get("receiverType"))
                .printType(data.get("printType"))
                //.attachmentUrls(null)
                .proposalProductType(InformalProposalProductTypeEnum.RS);
    }

    @When("si richiede la prepare della comunicazione bonaria")
    public void callPaperChannelInformal(String xClientId) {
        try {
            this.informalPrepareResponse = paperChannelClient.sendInformalPrepareRequest(informalPrepareRequest, xClientId);
            this.httpStatusCode = HttpStatus.OK; // O un valore che indichi "Successo 2xx"

        } catch (HttpStatusCodeException ex) {
            httpStatusCode = ex.getStatusCode();
        } catch (RestClientException e) {
            // Invece di fare il throw, salviamo l'errore per controllarlo dopo
            this.encounteredException = e;
        }
    }

    @Then("si riceve un errore con codice di stato {int}")
    public void verifyResponseCode(int expectedCode) {
        // Verifichiamo che lo stato salvato non sia null e corrisponda a quello atteso
        assertNotNull("Il codice di stato non è stato valorizzato (nessun errore HTTP intercettato)", httpStatusCode);
        assertEquals(expectedCode, httpStatusCode.value());

        //assertNotNull("Mi aspettavo un errore di comunicazione", encounteredException);
        //assertTrue(encounteredException instanceof RestClientException);
    }

    @Then("si riceve un codice di stato di successo")
    public void verificaStatoSuccesso() {
        // Verifica che non ci siano eccezioni generiche
        assertNull(encounteredException, "Errore durante la chiamata: " + encounteredException);

        // Verifica che lo stato sia 200 o 201
        assertTrue(httpStatusCode.is2xxSuccessful(),
                "Atteso 200 o 201, ma ricevuto: " + httpStatusCode);

        // Verifica che l'oggetto risposta sia popolato
        assertNotNull("La risposta InformalPrepareResponse è nulla", this.informalPrepareResponse);
    }




}
