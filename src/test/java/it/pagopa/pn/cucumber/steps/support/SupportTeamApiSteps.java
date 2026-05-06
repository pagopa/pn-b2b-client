package it.pagopa.pn.cucumber.steps.support;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.model.external.bff.apikey.manager.pa.BffRequestApiKeyStatus;
import it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.model.external.bff.apikey.manager.pa.BffRequestNewApiKey;
import it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.model.external.bff.pa.info.BffAdditionalLanguages;
import it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.model.external.bff.pa.recipient.BffDocumentDownloadMetadataResponse;
import it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.model.external.bff.pa.recipient.BffDocumentType;
import it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.model.external.bff.pa.recipient.BffFullNotificationV1;
import it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.model.external.bff.pa.recipient.BffNewNotificationRequest;
import it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.model.external.bff.pa.recipient.BffNotificationsResponse;
import it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.model.external.bff.pa.recipient.NotificationSearchRow;
import it.pagopa.pn.client.b2b.pa.service.impl.PnApiKeyManagerExternalClientImpl;
import it.pagopa.pn.client.b2b.pa.service.impl.PnBffPaClientImpl;
import it.pagopa.pn.client.b2b.pa.service.utils.SettableApiKey;
import it.pagopa.pn.client.b2b.pa.service.utils.SettableBearerToken;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.web.client.HttpStatusCodeException;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.model.external.bff.pa.recipient.NotificationStatusV26.EFFECTIVE_DATE;
import static java.time.OffsetDateTime.now;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Slf4j
public class SupportTeamApiSteps {
    private final PnApiKeyManagerExternalClientImpl bffApiKeyClient;
    private final PnBffPaClientImpl bffPaClient;
    private final Map<String, Runnable> strategies = new HashMap<>();
    private Exception exception;

    public SupportTeamApiSteps(PnApiKeyManagerExternalClientImpl bffApiKeyClient, PnBffPaClientImpl bffPaClient) {
        this.bffApiKeyClient = bffApiKeyClient;
        this.bffPaClient = bffPaClient;
        populateMapStrategy();
    }

    private void populateMapStrategy() {
        strategies.put("INVIO_NUOVA_NOTIFICA", () -> bffPaClient.newSentNotificationV1(new BffNewNotificationRequest()));
        strategies.put("CAMBIO_LINGUA", () -> bffPaClient.changeAdditionalLang(
                new BffAdditionalLanguages().addAdditionalLanguagesItem("italiano")));
        strategies.put("CANCELLAZIONE_NOTIFICA", () -> bffPaClient.notificationCancellationV1("iun"));
        strategies.put("RECUPERA_API_KEYS", () -> bffApiKeyClient.getApiKeys(null, null, null, true));
        strategies.put("CREA_API_KEY", () -> bffApiKeyClient.newApiKey(new BffRequestNewApiKey()));
        strategies.put("CAMBIA_STATO_API_KEY", () -> bffApiKeyClient.changeStatusApiKey("id", new BffRequestApiKeyStatus()));
        strategies.put("CANCELLA_API_KEY", () -> bffApiKeyClient.deleteApiKeys("id"));
        strategies.put("RICERCA_TUTTE_LE_NOTIFICHE", this::searchSentNotification);
        strategies.put("DETTAGLIO_NOTIFICA", this::getSentNotification);
        strategies.put("RECUPERO_DOCUMENTI_NOTIFICA", this::getSentNotificationDocument);
        strategies.put("RECUPERO_ALLEGATI_PAGAMENTO", this::getSentNotificationPayment);
        strategies.put("VISUALIZZA_DASHBOARD", this::getDashboardDataV1);
    }

    @When("Il team di supporto effettua l'operazione di: {string}")
    public void invokeApiAsSupportTeam(String api) {
        setUserRole();
        Runnable strategy = strategies.get(api);
        if (strategy != null) {
            try {
                strategy.run();
                exception = null;
            } catch (Exception e) {
                this.exception = e;
            }
        } else {
            throw new IllegalArgumentException("Nessuna strategy configurata per la API: " + api);
        }
    }

    @Then("il ruolo supporto non ha accesso all'API e riceve un errore di autorizzazione")
    public void verifySupportRoleAccessDenied() {
        if (exception != null) {
            String message = exception.getMessage();
            log.info("Eccezione catturata: {}", message);
            assertEquals(403, ((HttpStatusCodeException) exception).getStatusCode().value(), "Ci si aspettava un errore 403 Forbidden");
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
                null, EFFECTIVE_DATE, null, null, 50, null);
    }

    private void getSentNotificationDocument() {
        Pair<BffFullNotificationV1, String> bffFullNotificationWithIUN = getSentNotification();
        String documentId = Optional.ofNullable(bffFullNotificationWithIUN.getKey())
                .map(BffFullNotificationV1::getOtherDocuments)
                .filter(docs -> !docs.isEmpty())
                .map(docs -> docs.get(0))
                .filter(Objects::nonNull)
                .map(doc -> doc.getDocumentId())
                .filter(id -> id != null && !id.isEmpty())
                .orElseThrow(() -> new RuntimeException("Nessun documento trovato nella notifica"));

        bffPaClient.getSentNotificationDocumentV1(bffFullNotificationWithIUN.getValue(), BffDocumentType.AAR, null, documentId);
    }

    private void getSentNotificationPayment() {
        BffNotificationsResponse bffNotificationsResponse = searchSentNotification();
        assertNotNull(bffNotificationsResponse.getResultsPage(), "La ricerca delle notifiche non ha restituito alcun risultato");
        for (NotificationSearchRow notificationSearchRow : bffNotificationsResponse.getResultsPage()) {
            BffFullNotificationV1 bffFullNotificationV1 = getSentNotification(notificationSearchRow.getIun());
            if (!bffFullNotificationV1.getDocuments().isEmpty()) {
                BffDocumentDownloadMetadataResponse response = bffPaClient.getSentNotificationPaymentV1(notificationSearchRow.getIun(), 0, "PAGOPA", 0);
                // Verifica che la risposta contenga almeno un documento di pagamento
                if (response != null) {
                    return;
                } else {
                    log.info("Nessun documento di pagamento trovato per la notifica con IUN: {}", notificationSearchRow.getIun());
                }
                // La notifica ha documenti, ma nessuno di tipo pagamento, quindi si continua a cercare nelle altre notifiche
            } else {
                log.info("Nessun documento trovato per la notifica con IUN: {}", notificationSearchRow.getIun());
            }
        }
        throw new RuntimeException("Nessun documento di pagamento trovato nelle notifiche recuperate");
    }

    private BffFullNotificationV1 getSentNotification(String iun) {
        return bffPaClient.getSentNotificationV1(iun);
    }

    private Pair<BffFullNotificationV1, String> getSentNotification() {
        String iun = retrieveFirstIunFromSearch();
        return Pair.of(bffPaClient.getSentNotificationV1(iun), iun);
    }

    private String retrieveFirstIunFromSearch() {
        BffNotificationsResponse response = searchSentNotification();
        assertNotNull(response.getResultsPage(), "La ricerca delle notifiche non ha restituito alcun risultato");
        if (response.getResultsPage().isEmpty()) {
            throw new RuntimeException("Nessuna notifica trovata per eseguire il test");
        }
        return response.getResultsPage().get(0).getIun();
    }

    private void getDashboardDataV1() {
        bffPaClient.getDashboardDataV1("BS", LocalDate.now().minusYears(1), LocalDate.now());
    }
}
