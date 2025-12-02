package it.pagopa.pn.cucumber.steps.messaggiCortesiaBanche;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.pn.client.b2b.pa.service.impl.EmdIntegrationApiImpl;
import it.pagopa.pn.client.b2b.radd.generated.openapi.clients.emd.model.SendMessageRequestBody;
import it.pagopa.pn.client.b2b.radd.generated.openapi.clients.emd.model.SendMessageResponse;
import it.pagopa.pn.cucumber.steps.messaggiCortesiaBanche.domain.EmdCheckTppEndpoint;
import org.junit.jupiter.api.Assertions;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpStatusCodeException;

import java.util.List;
import java.util.Map;

public class MessaggiCortesiaBancheSteps {
    private final EmdIntegrationApiImpl emdIntegrationApi;
    private ResponseEntity<?> emdResponseEntity;

    public MessaggiCortesiaBancheSteps(EmdIntegrationApiImpl emdIntegrationApi) {
        this.emdIntegrationApi = emdIntegrationApi;
    }

    @When("viene invocato l'endpoint sendMessage con i seguenti parametri")
    public void callEmdSendMessage(List<SendMessageRequestBody> requestBodyList) {
        try {
            emdResponseEntity = emdIntegrationApi.sendMessage(requestBodyList.get(0));
        } catch (HttpStatusCodeException e) {
            emdResponseEntity = new ResponseEntity<>(e.getStatusCode());
        }
    }

    @When("viene invocato l'endpoint {emdCheckTppEndpoint} con retrievalId: {string}")
    public void callEmdCheckTPP(EmdCheckTppEndpoint emdCheckTppEndpoint, String retrievalId) {
        try {
            if (emdCheckTppEndpoint == EmdCheckTppEndpoint.TOKEN_CHECK_TPP)
                emdResponseEntity = emdIntegrationApi.tokenCheckTPP(retrievalId);
            else
                emdResponseEntity = emdIntegrationApi.emdCheckTPP(retrievalId);
        } catch (HttpStatusCodeException e) {
            emdResponseEntity = new ResponseEntity<>(e.getStatusCode());
        }
    }

    @When("viene invocato l'endpoint paymentUrl con i seguenti parametri")
    public void callEmdPaymentUrl(Map<String, String> row) {
        String amountString = row.get("amount");
        Integer amount = amountString == null || amountString.isEmpty() ? null : Integer.valueOf(amountString);
        try {
            emdResponseEntity = emdIntegrationApi.getPaymentUrl(row.get("retrievalId"), row.get("noticeCode"), row.get("paTaxId"), amount);
        } catch (HttpStatusCodeException e) {
            emdResponseEntity = new ResponseEntity<>(e.getStatusCode());
        }
    }

    @Then("si ottiene status code {int}")
    public void verifyStatusCode(int statusCode) {
        Assertions.assertEquals(statusCode, emdResponseEntity.getStatusCode().value());
    }

    @And("la risposta contiene outcome uguale a {string}")
    public void verifyOutcomeResponse(String outcome) {
        Assertions.assertEquals(SendMessageResponse.OutcomeEnum.valueOf(outcome), ((SendMessageResponse) emdResponseEntity.getBody()).getOutcome());
    }
}
