package it.pagopa.pn.cucumber.steps.auditLog;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.common.polling.PollingConfig;
import it.pagopa.common.polling.Poller;
import it.pagopa.common.util.CloudWatchLogsService;
import it.pagopa.common.util.StringUtils;
import it.pagopa.pn.client.b2b.audit.AuditLogPrivateApiClient;
import it.pagopa.pn.client.b2b.pa.domain.Destinatario;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.FullSentNotificationV29;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementV28;
import it.pagopa.pn.cucumber.steps.SendSharedContext;
import it.pagopa.pn.cucumber.steps.SharedSteps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import static it.pagopa.pn.client.b2b.pa.domain.Costanti.AAR_GENERATION;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Step per QA-19752: invocano (tramite il client ad-hoc {@link AuditLogPrivateApiClient}) le 7 API private di
 * pn-delivery/pn-delivery-push indicate nel ticket passando le varie combinazioni degli header opzionali
 * {@code x-pagopa-pn-cx-id}, {@code x-pagopa-pn-cx-type}, {@code x-pagopa-pn-uid}, e verificano che l'audit log
 * prodotto riporti, come campi JSON di primo livello {@code cx_id}/{@code cx_type}/{@code uid}, esattamente gli
 * header passati (con il relativo valore) e non quelli assenti. Il confronto sui campi JSON e' fatto lato client
 * tramite {@link CloudWatchLogsService#hasMatchingLog}: i filter pattern di CloudWatch non ammettono caratteri
 * {@code "} letterali nei termini, quindi non si puo' esprimere "il campo X e' assente/uguale a Y" componendo un
 * filter pattern piu' articolato (a differenza del semplice controllo di presenza di {@code AwsServiceSteps}, che
 * resta lo step da usare quando basta cercare una sottostringa). Le chiamate business possono anche fallire (es.
 * 404 su un documento non esistente): l'eccezione viene ignorata perche' l'oggetto di verifica e' l'audit log, non
 * l'esito della chiamata.
 */
@Slf4j
public class AuditLogOptionalHeadersSteps {

    private final SharedSteps sharedSteps;
    private final SendSharedContext sendSharedContext;
    private final AuditLogPrivateApiClient auditLogPrivateApiClient;
    private final CloudWatchLogsService cloudWatchLogsService;
    private OptionalHeaders lastHeaders = OptionalHeaders.from(Map.of());

    @Autowired
    public AuditLogOptionalHeadersSteps(SharedSteps sharedSteps, SendSharedContext sendSharedContext,
                                        AuditLogPrivateApiClient auditLogPrivateApiClient, CloudWatchLogsService cloudWatchLogsService) {
        this.sharedSteps = sharedSteps;
        this.sendSharedContext = sendSharedContext;
        this.auditLogPrivateApiClient = auditLogPrivateApiClient;
        this.cloudWatchLogsService = cloudWatchLogsService;
    }

    @When("viene invocata l'API privata searchNotificationsPrivate con i seguenti header opzionali")
    public void invocaSearchNotificationsPrivate(Map<String, String> headerTable) {
        lastHeaders = OptionalHeaders.from(headerTable);
        try {
            auditLogPrivateApiClient.searchNotificationsPrivate(
                    OffsetDateTime.now(ZoneOffset.UTC).minusDays(1), OffsetDateTime.now(ZoneOffset.UTC).plusDays(1),
                    "RSSMRA85T10A562S", 10,
                    lastHeaders.uid, lastHeaders.cxId, lastHeaders.cxType);
        } catch (Exception e) {
            log.info("searchNotificationsPrivate: chiamata terminata con errore (ignorato ai fini della verifica audit log): {}", e.getMessage());
        }
    }

    @When("viene invocata l'API privata checkAarQrCode con i seguenti header opzionali")
    public void invocaCheckAarQrCode(Map<String, String> headerTable) {
        lastHeaders = OptionalHeaders.from(headerTable);
        Destinatario destinatario = currentRecipient();
        try {
            auditLogPrivateApiClient.checkAarQrCode(destinatario.getRecipientType(), currentRecipientInternalId(),
                    "QRCODE-" + sharedSteps.getNotificationIun(),
                    lastHeaders.uid, lastHeaders.cxId, lastHeaders.cxType);
        } catch (Exception e) {
            log.info("checkAarQrCode: chiamata terminata con errore (ignorato ai fini della verifica audit log): {}", e.getMessage());
        }
    }

    @When("viene invocata l'API privata getReceivedNotificationDocumentPrivate con i seguenti header opzionali")
    public void invocaGetReceivedNotificationDocumentPrivate(Map<String, String> headerTable) {
        lastHeaders = OptionalHeaders.from(headerTable);
        try {
            auditLogPrivateApiClient.getReceivedNotificationDocumentPrivate(
                    sharedSteps.getNotificationIun(), currentDocIdx(), currentRecipientInternalId(),
                    lastHeaders.uid, lastHeaders.cxId, lastHeaders.cxType);
        } catch (Exception e) {
            log.info("getReceivedNotificationDocumentPrivate: chiamata terminata con errore (ignorato ai fini della verifica audit log): {}", e.getMessage());
        }
    }

    @When("viene invocata l'API privata getReceivedNotificationAttachmentPrivate con i seguenti header opzionali")
    public void invocaGetReceivedNotificationAttachmentPrivate(Map<String, String> headerTable) {
        lastHeaders = OptionalHeaders.from(headerTable);
        try {
            auditLogPrivateApiClient.getReceivedNotificationAttachmentPrivate(
                    sharedSteps.getNotificationIun(), "F24", currentRecipientInternalId(),
                    lastHeaders.uid, lastHeaders.cxId, lastHeaders.cxType);
        } catch (Exception e) {
            log.info("getReceivedNotificationAttachmentPrivate: chiamata terminata con errore (ignorato ai fini della verifica audit log): {}", e.getMessage());
        }
    }

    @When("viene invocata l'API privata getLegalFactByIdPrivate con i seguenti header opzionali")
    public void invocaGetLegalFactByIdPrivate(Map<String, String> headerTable) {
        lastHeaders = OptionalHeaders.from(headerTable);
        try {
            auditLogPrivateApiClient.getLegalFactByIdPrivate(
                    currentRecipientInternalId(), sharedSteps.getNotificationIun(), currentAarLegalFactId(),
                    lastHeaders.uid, lastHeaders.cxId, lastHeaders.cxType);
        } catch (Exception e) {
            log.info("getLegalFactByIdPrivate: chiamata terminata con errore (ignorato ai fini della verifica audit log): {}", e.getMessage());
        }
    }

    @When("viene invocata l'API privata getNotificationLegalFactsPrivate con i seguenti header opzionali")
    public void invocaGetNotificationLegalFactsPrivate(Map<String, String> headerTable) {
        lastHeaders = OptionalHeaders.from(headerTable);
        try {
            auditLogPrivateApiClient.getNotificationLegalFactsPrivate(
                    currentRecipientInternalId(), sharedSteps.getNotificationIun(),
                    lastHeaders.uid, lastHeaders.cxId, lastHeaders.cxType);
        } catch (Exception e) {
            log.info("getNotificationLegalFactsPrivate: chiamata terminata con errore (ignorato ai fini della verifica audit log): {}", e.getMessage());
        }
    }

    @When("viene invocata l'API privata notifyNotificationRaddRetrieved con i seguenti header opzionali")
    public void invocaNotifyNotificationRaddRetrieved(Map<String, String> headerTable) {
        lastHeaders = OptionalHeaders.from(headerTable);
        Destinatario destinatario = currentRecipient();
        try {
            auditLogPrivateApiClient.notifyNotificationRaddRetrieved(
                    sharedSteps.getNotificationIun(), destinatario.getRecipientType(), currentRecipientInternalId(),
                    "ALT", "CUCUMBER-" + UUID.randomUUID(), OffsetDateTime.now(),
                    lastHeaders.uid, lastHeaders.cxId, lastHeaders.cxType);
        } catch (Exception e) {
            log.info("notifyNotificationRaddRetrieved: chiamata terminata con errore (ignorato ai fini della verifica audit log): {}", e.getMessage());
        }
    }

    /**
     * Cerca su CloudWatch i log con il {@code tag} indicato (filtro semplice, nessun carattere speciale) e verifica
     * lato client che almeno uno di essi riporti i campi JSON {@code cx_id}/{@code cx_type}/{@code uid} coerenti con
     * l'ultima combinazione di header opzionali passata dalla {@code When} precedente: presenti con il relativo
     * valore se passati, assenti se non passati.
     */
    @Then("verifico che l'audit log con tag {string} su {string} riporti solo gli header opzionali effettivamente passati")
    public void verificaAuditLogHeaderOpzionali(String tag, String logGroup) {
        Map<String, String> expectedFields = new LinkedHashMap<>();
        expectedFields.put("cx_id", lastHeaders.cxId);
        expectedFields.put("cx_type", lastHeaders.cxType);
        expectedFields.put("uid", lastHeaders.uid);
        try {
            Poller.pollUntil(
                    () -> hasMatchingLogIgnoringTransientErrors(logGroup, tag, expectedFields),
                    found -> {
                        assertThat(found).as("Nessun audit log %s su %s riporta esattamente cx_id=%s, cx_type=%s, uid=%s",
                                tag, logGroup, lastHeaders.cxId, lastHeaders.cxType, lastHeaders.uid).isTrue();
                    },
                    PollingConfig.of(Duration.ofMinutes(2), Duration.ofSeconds(10)));
        } catch (AssertionError assertionError) {
            sharedSteps.throwAssertionErrorWithIUN(assertionError);
        }
    }

    /** Ignora errori transitori di CloudWatch (es. throttling) riprovando al giro di polling successivo, invece di far fallire subito il check. */
    private boolean hasMatchingLogIgnoringTransientErrors(String logGroup, String tag, Map<String, String> expectedFields) {
        try {
            return cloudWatchLogsService.hasMatchingLog(logGroup, "\"" + tag + "\"", 20, expectedFields);
        } catch (RuntimeException e) {
            log.warn("Errore transitorio durante la ricerca su CloudWatch, nuovo tentativo al prossimo giro di polling", e);
            return false;
        }
    }

    private Destinatario currentRecipient() {
        return sendSharedContext.getLegalNotificationContext().getRecipient().getDestinatario();
    }

    private String currentRecipientInternalId() {
        Destinatario destinatario = currentRecipient();
        return destinatario.getRecipientType() + "-" + destinatario.getUid();
    }

    private Integer currentDocIdx() {
        return Integer.parseInt(sharedSteps.getSentNotificationLastVersion().getDocuments().get(0).getDocIdx());
    }

    /** Legge dalla timeline la chiave del legal fact generato per l'AAR (stesso meccanismo di LegalFactContentVerifySteps). */
    private String currentAarLegalFactId() {
        FullSentNotificationV29 notification = sharedSteps.getSentNotificationLastVersion();
        TimelineElementV28 aarElement = notification.getTimeline().stream()
                .filter(element -> Objects.requireNonNull(element.getCategory().getValue()).equals(AAR_GENERATION))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Nessun elemento di timeline AAR_GENERATION trovato per lo IUN " + notification.getIun()));
        String generatedAarUrl = Objects.requireNonNull(aarElement.getDetails()).getGeneratedAarUrl();
        return generatedAarUrl.substring(generatedAarUrl.indexOf("PN_AAR"));
    }

    private static final class OptionalHeaders {
        private final String cxId;
        private final String cxType;
        private final String uid;

        private OptionalHeaders(String cxId, String cxType, String uid) {
            this.cxId = cxId;
            this.cxType = cxType;
            this.uid = uid;
        }

        private static OptionalHeaders from(Map<String, String> table) {
            return new OptionalHeaders(
                    StringUtils.resolveValue(table.get("cxId")),
                    StringUtils.resolveValue(table.get("cxType")),
                    StringUtils.resolveValue(table.get("uid")));
        }
    }
}
