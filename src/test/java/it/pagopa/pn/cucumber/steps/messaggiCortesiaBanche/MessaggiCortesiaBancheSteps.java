package it.pagopa.pn.cucumber.steps.messaggiCortesiaBanche;

import io.cucumber.java.DataTableType;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.pn.client.b2b.pa.service.impl.EmdMessageApiImpl;
import it.pagopa.pn.client.b2b.radd.generated.openapi.clients.emd.model.SendMessageRequestBody;

import java.util.List;
import java.util.Map;

public class MessaggiCortesiaBancheSteps {
    private final EmdMessageApiImpl messageApi;
    private SendMessageRequestBody sendMessageRequest;

    public MessaggiCortesiaBancheSteps(EmdMessageApiImpl messageApi) {
        this.messageApi = messageApi;
    }

    @Given("viene settato il token")
    public void setToken() {

    }

    @When("viene invocato l'endpoint con i seguenti parametri")
    public void callEmd(List<SendMessageRequestBody> requestBodyList) {
        messageApi.sendMessage(requestBodyList.get(0));

    }

    @Then("si ottiene status code {int}")
    public void verifyStatusCode(int statusCode) {

    }

    @DataTableType
    public SendMessageRequestBody convert(Map<String, String> row) {
        return new SendMessageRequestBody()
                .internalRecipientId(row.get("internalRecipientId"))
                .recipientId(row.get("recipientId"))
                .senderDescription(row.get("senderDescription"))
                .originId(row.get("originId"))
                .associatedPayment(Boolean.parseBoolean(row.get("associatedPayment")));
    }
}
