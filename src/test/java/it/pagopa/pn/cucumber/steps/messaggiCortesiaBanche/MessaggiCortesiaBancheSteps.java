package it.pagopa.pn.cucumber.steps.messaggiCortesiaBanche;

import io.cucumber.java.DataTableType;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.common.http.HttpCallExecutor;
import it.pagopa.pn.client.b2b.pa.service.impl.EmdIntegrationApiImpl;
import it.pagopa.pn.client.b2b.radd.generated.openapi.clients.emd.model.SendMessageRequestBody;
import it.pagopa.pn.client.b2b.radd.generated.openapi.clients.emd.model.SendMessageResponse;
import it.pagopa.pn.cucumber.steps.messaggiCortesiaBanche.domain.EmdCheckTppEndpoint;
import org.apache.commons.lang3.RandomStringUtils;
import org.joda.time.DateTime;
import org.junit.jupiter.api.Assertions;

import java.util.List;
import java.util.Map;

public class MessaggiCortesiaBancheSteps {
    private final EmdIntegrationApiImpl emdIntegrationApi;
    private final HttpCallExecutor httpCallExecutor;

    public MessaggiCortesiaBancheSteps(EmdIntegrationApiImpl emdIntegrationApi, HttpCallExecutor httpCallExecutor) {
        this.emdIntegrationApi = emdIntegrationApi;
        this.httpCallExecutor = httpCallExecutor;
    }

    @When("viene invocato l'endpoint sendMessage con i seguenti parametri")
    public void callEmdSendMessage(List<SendMessageRequestBody> requestBodyList) {
        httpCallExecutor.callForEntity(() -> emdIntegrationApi.sendMessage(requestBodyList.get(0)));
    }

    @When("viene invocato l'endpoint {emdCheckTppEndpoint} con retrievalId: {string}")
    public void callEmdCheckTPP(EmdCheckTppEndpoint emdCheckTppEndpoint, String retrievalId) {
        httpCallExecutor.callForEntity(() -> emdCheckTppEndpoint == EmdCheckTppEndpoint.TOKEN_CHECK_TPP
                ? emdIntegrationApi.tokenCheckTPP(retrievalId)
                : emdIntegrationApi.emdCheckTPP(retrievalId));
    }

    @When("viene invocato l'endpoint paymentUrl con i seguenti parametri")
    public void callEmdPaymentUrl(Map<String, String> row) {
        String amountString = row.get("amount");
        httpCallExecutor.callForEntity(() -> emdIntegrationApi.getPaymentUrl(
                row.get("retrievalId"), row.get("noticeCode"), row.get("paTaxId"),
                amountString == null || amountString.isEmpty() ? null : Integer.valueOf(amountString)));
    }

    @Then("si ottiene status code {int}")
    public void verifyStatusCode(int statusCode) {
        Assertions.assertTrue(httpCallExecutor.hasResult(), "La chiamata EMD non ha prodotto un esito HTTP");
        Assertions.assertEquals(statusCode, httpCallExecutor.getStatusCode().intValue(),
                "Lo status HTTP della chiamata EMD non corrisponde a quello atteso");
    }

    @And("la risposta contiene outcome uguale a {string}")
    public void verifyOutcomeResponse(String outcome) {
        Assertions.assertTrue(httpCallExecutor.isSuccessful(), "La chiamata EMD non ha prodotto una risposta di successo");
        Assertions.assertNotNull(httpCallExecutor.getResponse(), "La risposta EMD non contiene un body");
        SendMessageResponse body = httpCallExecutor.getResponseBody(SendMessageResponse.class);
        Assertions.assertEquals(SendMessageResponse.OutcomeEnum.valueOf(outcome), body.getOutcome());
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
            case "TEXT_98":
                return RandomStringUtils.randomAlphabetic(98);
            case "TEXT_UTF8":
                return "Messaggio con caratteri UTF-8 àèìòù € 漢字 😊UTF-8: à è ì ò ù, é ç ñ, €, ©, ™ e lettere non latine come α β γ.";
            default:
                return value;
        }
    }
}
