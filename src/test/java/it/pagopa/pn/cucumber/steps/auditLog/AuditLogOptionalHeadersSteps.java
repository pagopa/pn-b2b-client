package it.pagopa.pn.cucumber.steps.auditLog;

import io.cucumber.java.en.When;
import it.pagopa.common.util.StringUtils;
import it.pagopa.pn.client.b2b.audit.AuditLogPrivateApiClient;
import it.pagopa.pn.client.b2b.pa.domain.Destinatario;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.FullSentNotificationV29;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementV28;
import it.pagopa.pn.cucumber.steps.SendSharedContext;
import it.pagopa.pn.cucumber.steps.SharedSteps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import static it.pagopa.pn.client.b2b.pa.domain.Costanti.AAR_GENERATION;

/**
 * Step per QA-19752: invocano (tramite il client ad-hoc {@link AuditLogPrivateApiClient}) le 7 API private di
 * pn-delivery/pn-delivery-push indicate nel ticket passando le varie combinazioni degli header opzionali
 * {@code x-pagopa-pn-cx-id}, {@code x-pagopa-pn-cx-type}, {@code x-pagopa-pn-uid}. La verifica che l'audit log
 * prodotto riporti esattamente gli header passati e' delegata allo step generico
 * {@code AwsServiceSteps#checkAuditLogFromAws}, invocato direttamente dal file feature con una tabella che include
 * come termini di ricerca i valori degli header passati e come {@code exclude} i nomi di quelli assenti. Le
 * chiamate business possono anche fallire (es. 404 su un documento non esistente): l'eccezione viene ignorata
 * perche' l'oggetto di verifica e' l'audit log, non l'esito della chiamata.
 */
@Slf4j
public class AuditLogOptionalHeadersSteps {

    private final SharedSteps sharedSteps;
    private final SendSharedContext sendSharedContext;
    private final AuditLogPrivateApiClient auditLogPrivateApiClient;

    @Autowired
    public AuditLogOptionalHeadersSteps(SharedSteps sharedSteps, SendSharedContext sendSharedContext,
                                        AuditLogPrivateApiClient auditLogPrivateApiClient) {
        this.sharedSteps = sharedSteps;
        this.sendSharedContext = sendSharedContext;
        this.auditLogPrivateApiClient = auditLogPrivateApiClient;
    }

    @When("viene invocata l'API privata searchNotificationsPrivate con i seguenti header opzionali")
    public void invocaSearchNotificationsPrivate(Map<String, String> headerTable) {
        OptionalHeaders headers = OptionalHeaders.from(headerTable);
        try {
            auditLogPrivateApiClient.searchNotificationsPrivate(
                    OffsetDateTime.now(ZoneOffset.UTC).minusDays(1), OffsetDateTime.now(ZoneOffset.UTC).plusDays(1),
                    "RSSMRA85T10A562S", 10,
                    headers.uid, headers.cxId, headers.cxType);
        } catch (Exception e) {
            log.info("searchNotificationsPrivate: chiamata terminata con errore (ignorato ai fini della verifica audit log): {}", e.getMessage());
        }
    }

    @When("viene invocata l'API privata checkAarQrCode con i seguenti header opzionali")
    public void invocaCheckAarQrCode(Map<String, String> headerTable) {
        OptionalHeaders headers = OptionalHeaders.from(headerTable);
        Destinatario destinatario = currentRecipient();
        try {
            auditLogPrivateApiClient.checkAarQrCode(destinatario.getRecipientType(), currentRecipientInternalId(),
                    "QRCODE-" + sharedSteps.getNotificationIun(),
                    headers.uid, headers.cxId, headers.cxType);
        } catch (Exception e) {
            log.info("checkAarQrCode: chiamata terminata con errore (ignorato ai fini della verifica audit log): {}", e.getMessage());
        }
    }

    @When("viene invocata l'API privata getReceivedNotificationDocumentPrivate con i seguenti header opzionali")
    public void invocaGetReceivedNotificationDocumentPrivate(Map<String, String> headerTable) {
        OptionalHeaders headers = OptionalHeaders.from(headerTable);
        try {
            auditLogPrivateApiClient.getReceivedNotificationDocumentPrivate(
                    sharedSteps.getNotificationIun(), currentDocIdx(), currentRecipientInternalId(),
                    headers.uid, headers.cxId, headers.cxType);
        } catch (Exception e) {
            log.info("getReceivedNotificationDocumentPrivate: chiamata terminata con errore (ignorato ai fini della verifica audit log): {}", e.getMessage());
        }
    }

    @When("viene invocata l'API privata getReceivedNotificationAttachmentPrivate con i seguenti header opzionali")
    public void invocaGetReceivedNotificationAttachmentPrivate(Map<String, String> headerTable) {
        OptionalHeaders headers = OptionalHeaders.from(headerTable);
        try {
            auditLogPrivateApiClient.getReceivedNotificationAttachmentPrivate(
                    sharedSteps.getNotificationIun(), "F24", currentRecipientInternalId(),
                    headers.uid, headers.cxId, headers.cxType);
        } catch (Exception e) {
            log.info("getReceivedNotificationAttachmentPrivate: chiamata terminata con errore (ignorato ai fini della verifica audit log): {}", e.getMessage());
        }
    }

    @When("viene invocata l'API privata getLegalFactByIdPrivate con i seguenti header opzionali")
    public void invocaGetLegalFactByIdPrivate(Map<String, String> headerTable) {
        OptionalHeaders headers = OptionalHeaders.from(headerTable);
        try {
            auditLogPrivateApiClient.getLegalFactByIdPrivate(
                    currentRecipientInternalId(), sharedSteps.getNotificationIun(), currentAarLegalFactId(),
                    headers.uid, headers.cxId, headers.cxType);
        } catch (Exception e) {
            log.info("getLegalFactByIdPrivate: chiamata terminata con errore (ignorato ai fini della verifica audit log): {}", e.getMessage());
        }
    }

    @When("viene invocata l'API privata getNotificationLegalFactsPrivate con i seguenti header opzionali")
    public void invocaGetNotificationLegalFactsPrivate(Map<String, String> headerTable) {
        OptionalHeaders headers = OptionalHeaders.from(headerTable);
        try {
            auditLogPrivateApiClient.getNotificationLegalFactsPrivate(
                    currentRecipientInternalId(), sharedSteps.getNotificationIun(),
                    headers.uid, headers.cxId, headers.cxType);
        } catch (Exception e) {
            log.info("getNotificationLegalFactsPrivate: chiamata terminata con errore (ignorato ai fini della verifica audit log): {}", e.getMessage());
        }
    }

    @When("viene invocata l'API privata notifyNotificationRaddRetrieved con i seguenti header opzionali")
    public void invocaNotifyNotificationRaddRetrieved(Map<String, String> headerTable) {
        OptionalHeaders headers = OptionalHeaders.from(headerTable);
        Destinatario destinatario = currentRecipient();
        try {
            auditLogPrivateApiClient.notifyNotificationRaddRetrieved(
                    sharedSteps.getNotificationIun(), destinatario.getRecipientType(), currentRecipientInternalId(),
                    "ALT", "CUCUMBER-" + UUID.randomUUID(), OffsetDateTime.now(),
                    headers.uid, headers.cxId, headers.cxType);
        } catch (Exception e) {
            log.info("notifyNotificationRaddRetrieved: chiamata terminata con errore (ignorato ai fini della verifica audit log): {}", e.getMessage());
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
