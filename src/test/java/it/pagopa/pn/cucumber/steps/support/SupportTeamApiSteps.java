package it.pagopa.pn.cucumber.steps.support;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.model.external.bff.apikey.manager.pa.BffRequestApiKeyStatus;
import it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.model.external.bff.apikey.manager.pa.BffRequestNewApiKey;
import it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.model.external.bff.pa.recipient.BffDocumentType;
import it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.model.external.bff.pa.recipient.BffFullNotificationV1;
import it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.model.external.bff.pa.recipient.BffNewNotificationRequest;
import it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.model.external.bff.pa.recipient.BffNotificationsResponse;
import it.pagopa.pn.client.b2b.pa.service.impl.PnApiKeyManagerExternalClientImpl;
import it.pagopa.pn.client.b2b.pa.service.impl.PnBffPaClientImpl;
import it.pagopa.pn.client.b2b.pa.service.utils.SettableApiKey;
import it.pagopa.pn.client.b2b.pa.service.utils.SettableBearerToken;
import lombok.extern.slf4j.Slf4j;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;

import static java.time.OffsetDateTime.now;

@Slf4j
public class SupportTeamApiSteps {
    private final PnApiKeyManagerExternalClientImpl bffApiKeyClient;
    private final PnBffPaClientImpl bffPaClient;
    private final Map<String, BiConsumer<PnApiKeyManagerExternalClientImpl, PnBffPaClientImpl>> strategies = new HashMap<>();
    private Exception exception;

    public SupportTeamApiSteps(PnApiKeyManagerExternalClientImpl bffApiKeyClient, PnBffPaClientImpl bffPaClient) {
        this.bffApiKeyClient = bffApiKeyClient;
        this.bffPaClient = bffPaClient;
        populateMapStrategy();
    }

    private void populateMapStrategy() {
        strategies.put("newSentNotification", (apiKeyClient, paClient) -> paClient.newSentNotificationV1(new BffNewNotificationRequest()));
        strategies.put("changeAdditionalLanguage", (apiKeyClient, paClient) -> paClient.changeAdditionalLang(null));
        strategies.put("notificationCancellation", (apiKeyClient, paClient) -> paClient.notificationCancellationV1("iun"));
        strategies.put("getApiKeys", (apiKeyClient, paClient) -> apiKeyClient.getApiKeys(0, null, null, true));
        strategies.put("newApiKey", (apiKeyClient, paClient) -> apiKeyClient.newApiKey(new BffRequestNewApiKey()));
        strategies.put("changeStatusApiKey", (apiKeyClient, paClient) -> apiKeyClient.changeStatusApiKey("id", new BffRequestApiKeyStatus()));
        strategies.put("deleteApiKeys", (apiKeyClient, paClient) -> apiKeyClient.deleteApiKeys("id"));
        strategies.put("searchSentNotification", (apiKeyClient, paClient) -> searchSentNotification());
        strategies.put("getSentNotification", (apiKeyClient, paClient) -> getSentNotification());
        strategies.put("getSentNotificationDocument", (apiKeyClient, paClient) -> getSentNotificationDocument());
        strategies.put("getSentNotificationPayment", (apiKeyClient, paClient) -> paClient.getSentNotificationPaymentV1("iun", null, null, null));
        strategies.put("getDashboardData", (apiKeyClient, paClient) -> paClient.getDashboardDataV1(null, null, null, null));
    }

    @When("viene invocata la seguente API: {string} dal team supporto")
    public void invokeApiAsSupportTeam(String api) {
        setUserRole();
        BiConsumer<PnApiKeyManagerExternalClientImpl, PnBffPaClientImpl> strategy = strategies.get(api);
        if (strategy != null) {
            try {
                strategy.accept(bffApiKeyClient, bffPaClient);
            } catch (Exception e) {
                this.exception = e;
            }
        } else {
            log.warn("Nessuna strategy trovata per la API: {}", api);
        }
    }

    @Then("il ruolo supporto non ha accesso all'API e riceve un errore di autorizzazione")
    public void verifySupportRoleAccessDenied() {
        if (exception != null) {
            String message = exception.getMessage();
            log.info("Eccezione catturata: {}", message);
            assert message.contains("403") || message.contains("Forbidden") || message.contains("Unauthorized");
        } else {
            throw new AssertionError("Nessuna eccezione catturata, ci si aspettava un errore di autorizzazione");
        }
    }

    @Then("il ruolo supporto ha accesso all'API e riceve una risposta valida")
    public void verifySupportRoleAccessGranted() {
        if (exception != null) {
            throw new AssertionError("Eccezione catturata: " + exception.getMessage());
        }
    }

    private void setUserRole() {
        bffPaClient.setBearerToken(SettableBearerToken.BearerTokenType.SUPPORT_1);
        bffApiKeyClient.setApiKeys(SettableApiKey.ApiKeyType.SUPPORT_1);
    }

    private BffNotificationsResponse searchSentNotification() {
        OffsetDateTime startDate = now().minusYears(1).atZoneSameInstant(ZoneId.of("UTC")).toOffsetDateTime();
        OffsetDateTime endDate = now().atZoneSameInstant(ZoneId.of("UTC")).toOffsetDateTime();
        return bffPaClient.searchSentNotification(startDate, endDate,
                null, null, null, null, 10, null);
    }

    private void getSentNotificationDocument() {
        BffFullNotificationV1 bffFullNotificationV1 = getSentNotification();
        String documentId = Optional.ofNullable(bffFullNotificationV1.getOtherDocuments())
                        .map(docs -> docs.get(0))
                        .map(doc -> doc.getDocumentId())
                        .orElseThrow(() -> new RuntimeException("Nessun documento trovato nella notifica"));

        bffPaClient.getSentNotificationDocumentV1(retrieveFirstIunFromSearch(), BffDocumentType.AAR, null, documentId);
    }

    private void getSentNotificationPayment() {
        bffPaClient.getSentNotificationPaymentV1("iun", null, null, null);
    }

    private BffFullNotificationV1 getSentNotification() {
        return bffPaClient.getSentNotificationV1(retrieveFirstIunFromSearch());
    }

    private String retrieveFirstIunFromSearch() {
        BffNotificationsResponse response = searchSentNotification();
        assert response.getResultsPage() != null;
        if (response.getResultsPage().isEmpty()) {
            throw new RuntimeException("Nessuna notifica trovata per eseguire il test");
        }
        return response.getResultsPage().get(0).getIun();
    }
}
