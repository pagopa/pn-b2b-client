package it.pagopa.pn.cucumber.steps.pa.notificationVersions;

import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v2.*;
import it.pagopa.pn.client.b2b.pa.polling.IPnPollingService;
import it.pagopa.pn.client.b2b.pa.polling.design.PnPollingStrategy;
import it.pagopa.pn.client.b2b.pa.polling.dto.PnPollingParameter;
import it.pagopa.pn.client.b2b.pa.polling.dto.PnPollingResponseV20;
import it.pagopa.pn.cucumber.steps.SharedSteps;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.opentest4j.AssertionFailedError;
import org.springframework.util.Base64Utils;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static it.pagopa.pn.client.b2b.pa.PnPaB2bUtils.*;
import static it.pagopa.pn.cucumber.steps.SharedSteps.threadWait;
import static it.pagopa.pn.cucumber.steps.pa.notificationVersions.Costanti.*;
import static it.pagopa.pn.cucumber.utils.NotificationValue.DOCUMENT;
import static it.pagopa.pn.cucumber.utils.NotificationValue.getDefaultValue;

@Data
@Slf4j
public class NotificationStepsV2 implements NotificationStepsInterface {

    private NewNotificationRequest notificationRequest;
    private NewNotificationResponse notificationResponse;
    private FullSentNotificationV20 fullSentNotification;
    private OffsetDateTime notificationCreationDate;
    private final SharedSteps.NotificationVersion version;
    private SharedSteps sharedSteps;

    public NotificationStepsV2(SharedSteps sharedSteps) {
        version = SharedSteps.NotificationVersion.V2;
        this.sharedSteps = sharedSteps;
    }

    @Override
    public Object getSentNotificationAnyVersion() {
        return fullSentNotification;
    }

    @Override
    public String getVersionString() {
        return version.toString();
    }

    @Override
    public String getNotificationSentIun() {
        return fullSentNotification != null ? fullSentNotification.getIun() : null;
    }

    @Override
    public void prepareNotificationRequest(Map<String, String> data) {
        notificationRequest = sharedSteps.getDataTableTypeUtil().convertNotificationRequestV2(data);
        sharedSteps.setVersionUsed(version);
    }

    @Override
    public void addRecipientToNotification(String recipientName, Map<String, String> data) {
        Destinatario destinatario = Destinatario.getByName(recipientName);
        if (destinatario != null && destinatario.equals(Destinatario.DESTINATARIO_NESSUNO)) return;
        NotificationRecipient notificationRecipient = sharedSteps.getDataTableTypeUtil().convertNotificationRecipientV2(data);
        if (destinatario != null) {
            notificationRecipient.setDenomination(destinatario.getDenomination());
            notificationRecipient.setTaxId(destinatario.getTaxId());
            notificationRecipient.setRecipientType(NotificationRecipient.RecipientTypeEnum.valueOf(destinatario.getRecipientType()));
            /** Nei vecchi metodi @And("Destinatario xxx") denomination e taxId venivano sempre settati
             * (recipientType veniva spesso passato null, ma in quei casi subentrava il valore di default PG)
             * e data veniva passata sempre come mappa vuota.
             * Al contrario nei vecchi metodi @And("Destinatario xxx e:"), data veniva passata come mappa con valori
             * e al contempo digitalDomicile era sempre null, in modo da non sovrascrivere eventuali valori passati.
             * Pertanto il seguente codice segue il vecchio comportamento, ma in maniera più chiara e coincisa */
            if (data.isEmpty()) {
                notificationRecipient.setDigitalDomicile(
                        new NotificationDigitalAddress()
                                .type(NotificationDigitalAddress.TypeEnum.valueOf(destinatario.getDigitalDomicileType()))
                                .address(Costanti.getDigitalAddressValue()));
            }
        }
        notificationRequest.addRecipientsItem(notificationRecipient);
    }

    @Override
    public void setSenderTaxId(String senderTaxId) {
        this.notificationRequest.setSenderTaxId(senderTaxId);
    }

    @Override
    public String getNotificationRequestGroup() {
        return notificationRequest.getGroup();
    }

    @Override
    public void setNotificationRequestGroup(String group) {
        notificationRequest.setGroup(group);
    }

    @Override
    public void retrieveFullSentNotification(String iun) {
        fullSentNotification = sharedSteps.getB2bClient().getSentNotificationV2(iun);
    }

    @Override
    public Object retrieveNotificationRequest() {
        return notificationRequest;
    }

    @Override
    public Object retrieveNotificationResponse() {
        return notificationResponse;
    }

    @Override
    public void sendNotification(int wait, String status, String pollingStrategy) {
        try {
            Assertions.assertDoesNotThrow(() -> {
                notificationCreationDate = OffsetDateTime.now();
                notificationResponse = (NewNotificationResponse) uploadNotification();
                if (status.equalsIgnoreCase(NOTIFICATION_STATUS_ACCEPTED)) {
                    threadWait(wait);
                    fullSentNotification = waitForRequestAccepted(notificationResponse, pollingStrategy);
                    threadWait(wait);
                    Assertions.assertNotNull(fullSentNotification);
                } else if (status.equalsIgnoreCase(NOTIFICATION_STATUS_REFUSED)) {
                    String errorCode = waitForRequestRefused(notificationResponse, pollingStrategy);
                    sharedSteps.setErrorCode(errorCode);
                    threadWait(wait);
                    Assertions.assertFalse(errorCode.isEmpty());
                }
                //TODO MATTEO: TUTTO DA VERIFICARE COME CASO
                else if (status.equalsIgnoreCase(NOTIFICATION_STATUS_NOT_REFUSED)) {
                    it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.RequestStatus response = sharedSteps.getB2bUtils().getClient().notificationCancellation(
                            new String(Base64Utils.decodeFromString(notificationResponse.getNotificationRequestId())));
                    Assertions.assertNotNull(response);
                    Assertions.assertNotNull(response.getDetails());
                    Assertions.assertFalse(response.getDetails().isEmpty());
                    Assertions.assertTrue("NOTIFICATION_CANCELLATION_ACCEPTED".equalsIgnoreCase(response.getDetails().get(0).getCode()));
                    boolean refused = waitForRequestNotRefused(notificationResponse, pollingStrategy);
                    threadWait(wait);
                    Assertions.assertFalse(refused);
                }
            });
            threadWait(wait);
            Assertions.assertNotNull(fullSentNotification);
        } catch (AssertionFailedError assertionFailedError) {
            String message = assertionFailedError.getMessage() +
                    "{RequestID: " + (notificationResponse == null ? "NULL" : notificationResponse.getNotificationRequestId()) + " }";
            throw new AssertionFailedError(message, assertionFailedError.getExpected(), assertionFailedError.getActual(), assertionFailedError.getCause());
        }
    }

    @Override
    public Object uploadNotification() throws IOException {
        List<NotificationDocument> documents = new ArrayList<>();
        for (NotificationDocument doc : notificationRequest.getDocuments()) {
            documents.add(this.preloadDocument(doc));
        }
        notificationRequest.setDocuments(documents);
        for (NotificationRecipient recipient : notificationRequest.getRecipients()) {
            NotificationPaymentInfo paymentInfo = recipient.getPayment();
            if (paymentInfo != null) {
                paymentInfo.setPagoPaForm(preloadAttachment(paymentInfo.getPagoPaForm()));
            }
        }
        log.info(NEW_NOTIFICATION_REQUEST, notificationRequest);
        NewNotificationResponse response = sharedSteps.getB2bUtils().getClient().sendNewNotificationV2(notificationRequest);
        log.info(NEW_NOTIFICATION_REQUEST_RESPONSE, response);
        return response;
    }

    @Override
    public void setIuvToRecipient(Integer posizione, String iuvGPD) {
        throw new RuntimeException("Metodo non previsto per la versione V2");
    }

    @Override
    public void addDocumentItems(int numAllegati) {
        int i = 0;
        while (i < numAllegati) {
            notificationRequest.addDocumentsItem(
                    new NotificationDocument()
                            .contentType(APPLICATION_PDF)
                            .ref(new NotificationAttachmentBodyRef().key(getDefaultValue(DOCUMENT.key))));
            i++;
        }
    }

    @Override
    public void performPriceVerification(String price, String date, Integer destinatario) {
        List<String> datiPagamento = sharedSteps.getDatiPagamentoVersionamento(destinatario, 0);
        it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v21.NotificationPriceResponse notificationPrice =
                sharedSteps.getB2bClient().getNotificationPrice(datiPagamento.get(0), datiPagamento.get(1));
        try {
            Assertions.assertEquals(notificationPrice.getIun(), sharedSteps.getIunVersionamento());
            if (price != null) {
                log.info("Costo notifica: {} destinatario: {}", notificationPrice.getAmount(), destinatario);
                Assertions.assertEquals(notificationPrice.getAmount(), Integer.parseInt(price));
            }
            if (date != null) {
                Assertions.assertNotNull(notificationPrice.getRefinementDate());
            }
        } catch (AssertionFailedError assertionFailedError) {
            sharedSteps.throwAssertionErrorWithIUN(assertionFailedError);
        }
    }

    private FullSentNotificationV20 waitForRequestAccepted(NewNotificationResponse response, String pollingStrategy) {
        IPnPollingService pollingService = sharedSteps.getB2bUtils().getPollingFactory().getPollingService(getPollingStrategy(pollingStrategy));
        PnPollingResponseV20 pollingResponse = (PnPollingResponseV20) pollingService.waitForEvent(response.getNotificationRequestId(), PnPollingParameter.builder().value(ACCEPTED).build());
        FullSentNotificationV20 result = pollingResponse.getNotification() == null ? null : pollingResponse.getNotification();
        sharedSteps.setFullSentNotificationV20(result);
        return result;
    }

    private String waitForRequestRefused(NewNotificationResponse response, String pollingStrategy) {
        log.info("Request status for " + response.getNotificationRequestId());
        long startTime = System.currentTimeMillis();

        IPnPollingService pollingService = sharedSteps.getB2bUtils().getPollingFactory().getPollingService(getPollingStrategy(pollingStrategy));
        PnPollingResponseV20 pollingResponse = (PnPollingResponseV20) pollingService.waitForEvent(response.getNotificationRequestId(), PnPollingParameter.builder().value(REFUSED).build());

        long endTime = System.currentTimeMillis();
        log.info("Execution time {}ms", (endTime - startTime));

        StringBuilder error = new StringBuilder();
        if (pollingResponse.getStatusResponse() != null
                && pollingResponse.getStatusResponse().getErrors() != null
                && !pollingResponse.getStatusResponse().getErrors().isEmpty()) {
            for (ProblemError err : pollingResponse.getStatusResponse().getErrors()) {
                error.append(" ").append(err.getDetail());
            }
        }
        log.info("Detail status {}", error);
        return error.toString();
    }

    private boolean waitForRequestNotRefused(NewNotificationResponse response, String pollingStrategy) {
        IPnPollingService pollingService = sharedSteps.getB2bUtils().getPollingFactory().getPollingService(getPollingStrategy(pollingStrategy));
        PnPollingResponseV20 pollingResponse = (PnPollingResponseV20) pollingService.waitForEvent(response.getNotificationRequestId(), PnPollingParameter.builder().value(REFUSED).build());
        return pollingResponse.getResult();
    }

    private String getPollingStrategy(String pollingStrategy) {
        return switch (pollingStrategy) {
            case TIMELINE_RAPID -> PnPollingStrategy.TIMELINE_RAPID_V20;
            case STATUS_RAPID -> PnPollingStrategy.STATUS_RAPID_V20;
            case TIMELINE_SLOW -> PnPollingStrategy.TIMELINE_SLOW_V20;
            case STATUS_SLOW -> PnPollingStrategy.STATUS_SLOW_V20;
            case VALIDATION_STATUS -> PnPollingStrategy.VALIDATION_STATUS_V20;
            case WEBHOOK -> PnPollingStrategy.WEBHOOK_V20;
            default ->
                    throw new RuntimeException("PnPollingStrategy non riconosciuta per la versione V20 : " + pollingStrategy);
        };
    }

    public NotificationDocument preloadDocument(NotificationDocument document) throws IOException {
        Pair<String, String> preloadDocument = sharedSteps.getB2bUtils().preloadGeneric(document.getRef().getKey(), LOAD_TO_PRESIGNED);
        documentSetKey(document, preloadDocument.getValue1());
        documentSetVersionToken(document, "v1");
        documentSetDigests(document, preloadDocument.getValue2());
        return document;
    }

    public NotificationPaymentAttachment preloadAttachment(NotificationPaymentAttachment attachment) throws IOException {
        if (attachment != null) {
            Pair<String, String> preloadAttachment = sharedSteps.getB2bUtils().preloadGeneric(attachment.getRef().getKey(), LOAD_TO_PRESIGNED);
            attachmentSetKey(attachment, preloadAttachment.getValue1());
            attachmentSetVersionToken(attachment, "v1");
            attachmentSetDigests(attachment, preloadAttachment.getValue2());
            return attachment;
        }
        return null;
    }

    private void documentSetKey(NotificationDocument notificationDocument, String key) {
        notificationDocument.getRef().setKey(key);
    }

    private void documentSetVersionToken(NotificationDocument notificationDocument, String version) {
        notificationDocument.getRef().setVersionToken(version);
    }

    private void documentSetDigests(NotificationDocument notificationDocument, String sha256) {
        notificationDocument.digests(new NotificationAttachmentDigests().sha256(sha256));
    }

    private void attachmentSetKey(NotificationPaymentAttachment notificationPaymentAttachment, String key) {
        notificationPaymentAttachment.getRef().setKey(key);
    }

    private void attachmentSetVersionToken(NotificationPaymentAttachment notificationPaymentAttachment, String version) {
        notificationPaymentAttachment.getRef().setVersionToken(version);
    }

    private void attachmentSetDigests(NotificationPaymentAttachment notificationPaymentAttachment, String sha256) {
        notificationPaymentAttachment.digests(new NotificationAttachmentDigests().sha256(sha256));
    }
}
