package it.pagopa.pn.cucumber.steps.messaggiCortesiaBanche;

import io.cucumber.java.DataTableType;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.pn.client.b2b.pa.service.impl.EmdIntegrationApiImpl;
import it.pagopa.pn.client.b2b.radd.generated.openapi.clients.emd.model.SendMessageRequestBody;
import it.pagopa.pn.client.b2b.radd.generated.openapi.clients.emd.model.SendMessageResponse;
import it.pagopa.pn.cucumber.steps.messaggiCortesiaBanche.domain.EmdCheckTppEndpoint;
import org.apache.commons.lang3.RandomStringUtils;
import org.joda.time.DateTime;
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

    @DataTableType
    public SendMessageRequestBody sendMessageRequestBodyMapper(Map<String, String> row) {
        return new SendMessageRequestBody()
                .internalRecipientId(resolveText(row.get("internalRecipientId")))
                .recipientId(resolveText(row.get("recipientId")))
                .senderDescription(resolveText(row.get("senderDescription")))
                .originId(resolveText(row.get("originId")))
                .associatedPayment(row.get("associatedPayment") != null && !row.get("associatedPayment").isEmpty() ? Boolean.valueOf(row.get("associatedPayment")) : null)
                .deliveryMode(row.get("deliveryMode") != null && !row.get("deliveryMode").isEmpty() ? SendMessageRequestBody.DeliveryModeEnum.valueOf(row.get("deliveryMode")) : null)
                .schedulingAnalogDate(row.get("schedulingAnalogDate") != null ? DateTime.now().toString() : null);
    }

    private String resolveText(String value) {
        if (value == null) return null;

        switch (value) {
            case "TEXT_250":
                return RandomStringUtils.randomAlphabetic(250);
            case "TEXT_251":
                return RandomStringUtils.randomAlphabetic(251);
            case "TEXT_100":
                return RandomStringUtils.randomAlphabetic(100);
            case "TEXT_101":
                return RandomStringUtils.randomAlphabetic(101);
            case "TEXT_UTF8":
                return "Messaggio con caratteri UTF-8 àèìòù € 漢字 😊UTF-8: à è ì ò ù, é ç ñ, €, ©, ™ e lettere non latine come α β γ.";
            default:
                return value;
        }
    }
}
