package it.pagopa.pn.cucumber.steps.pa.notificationVersions;

import it.pagopa.pn.client.b2b.pa.PnPaB2bUtils;
import it.pagopa.pn.client.b2b.pa.exception.PnB2bException;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.*;
import it.pagopa.pn.client.b2b.pa.polling.IPnPollingService;
import it.pagopa.pn.client.b2b.pa.polling.design.PnPollingStrategy;
import it.pagopa.pn.client.b2b.pa.polling.dto.PnPollingParameter;
import it.pagopa.pn.client.b2b.pa.polling.dto.PnPollingResponseV26;
import it.pagopa.pn.cucumber.steps.SharedSteps;
import it.pagopa.pn.cucumber.utils.NotificationValue;
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
import java.util.Objects;

import static it.pagopa.pn.client.b2b.pa.PnPaB2bUtils.*;
import static it.pagopa.pn.cucumber.steps.SharedSteps.threadWait;
import static it.pagopa.pn.cucumber.steps.pa.notificationVersions.Costanti.*;
import static it.pagopa.pn.cucumber.utils.NotificationValue.*;

@Data
@Slf4j
public class NotificationStepsV24 implements NotificationStepsInterface {

    private NewNotificationRequestV24 notificationRequest;
    private NewNotificationResponse notificationResponse;
    private FullSentNotificationV26 fullSentNotification;
    private OffsetDateTime notificationCreationDate;
    private final SharedSteps.NotificationVersion version;
    private SharedSteps sharedSteps;

    public NotificationStepsV24(SharedSteps sharedSteps) {
        version = SharedSteps.NotificationVersion.V24;
        this.sharedSteps = sharedSteps;
    }

    @Override
    public String getNotificationSentIun() {
        return fullSentNotification.getIun();
    }

    @Override
    public void setNotificationRequest(Map<String, String> data) {
        notificationRequest = sharedSteps.getDataTableTypeUtil().convertNotificationRequestV24(data);
        sharedSteps.setNotificationRequest(notificationRequest);//TODO MATTEO: SOLO QUA, CHE E' L'ULTIMA VERSIONE
        sharedSteps.setVersionUsed(version);
    }

    @Override
    public void addRecipitentToNotification(String recipientName, Map<String, String> data) {
        NotificationRecipientV23 notificationRecipient = sharedSteps.getDataTableTypeUtil().convertNotificationRecipient(data);
        if (notificationRequest.getNotificationFeePolicy() == NotificationFeePolicy.DELIVERY_MODE
                && NotificationValue.getValue(data, PAYMENT.key) != null) {
            String pagopaFormValue = getValue(data, PAYMENT_PAGOPA_FORM.key);
            if (pagopaFormValue != null && !pagopaFormValue.equalsIgnoreCase("NO")) {
                for (NotificationPaymentItem payments : Objects.requireNonNull(notificationRecipient.getPayments())) {
                    Objects.requireNonNull(payments.getPagoPa()).setApplyCost(true);
                }
            }
        }
        if (recipientName != null) {
            Destinatario destinatario = Destinatario.getByName(recipientName);
            notificationRecipient.setDenomination(destinatario.getDenomination());
            notificationRecipient.setTaxId(destinatario.getTaxId());
            notificationRecipient.setRecipientType(NotificationRecipientV23.RecipientTypeEnum.valueOf(destinatario.getRecipientType()));
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
        notificationRequest.setSenderTaxId(senderTaxId);
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
        fullSentNotification = sharedSteps.getB2bClient().getSentNotification(iun);
    }

    @Override
    public Object retrieveNotificationRequest() {
        return notificationRequest;
    }

    @Override
    public Object retrieveNotificationResponse() {
        return notificationResponse;
    }

    //TODO MATTEO TEST
    @Override
    public void sendNotification(int wait, String status, String pollingStrategy) {
        try {
            Assertions.assertDoesNotThrow(() -> {
                notificationCreationDate = OffsetDateTime.now();
                notificationResponse = (NewNotificationResponse) uploadNotification();
                sharedSteps.setNewNotificationResponse(notificationResponse);//TODO MATTEO: SOLO QUA, CHE E' L'ULTIMA VERSIONE
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
                    RequestStatus response = sharedSteps.getB2bUtils().getClient().notificationCancellation(
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
        } catch (AssertionFailedError assertionFailedError) {
            String message = assertionFailedError.getMessage() +
                    "{RequestID: " + (notificationResponse == null ? "NULL" : notificationResponse.getNotificationRequestId()) + " }";
            throw new AssertionFailedError(message, assertionFailedError.getExpected(), assertionFailedError.getActual(), assertionFailedError.getCause());
        }
    }

    //TODO MATTEO TEST (rendere private?)
    @Override
    public Object uploadNotification() throws IOException {
        //PRELOAD DOCUMENTI NOTIFICA
        List<NotificationDocument> documents = new ArrayList<>();
        for (NotificationDocument doc : notificationRequest.getDocuments()) {
            try {
                Thread.sleep(sharedSteps.getB2bUtils().getRandom().nextInt(350));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new PnB2bException(e.getMessage());
            }
            if (doc != null) {
                documents.add(this.preloadDocument(doc));
            }
        }
        notificationRequest.setDocuments(documents);
        //PRELOAD DOCUMENTI DI PAGAMENTO
        preloadPayDocument(notificationRequest);
        return getAndCheckSendNewNotification(notificationRequest);
    }

    @Override
    public void setIuvToRecipient(Integer posizione, String iuvGPD) {
        Objects.requireNonNull(Objects.requireNonNull(
                this.notificationRequest.getRecipients().get(0).getPayments()).get(posizione).getPagoPa()).setNoticeCode(iuvGPD);
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
        List<NotificationPaymentItem> listNotificationPaymentItem = sharedSteps.getFullSentNotificationV26().getRecipients().get(destinatario).getPayments();
        if (listNotificationPaymentItem != null) {
            for (NotificationPaymentItem notificationPaymentItem : listNotificationPaymentItem) {
                it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v21.NotificationPriceResponse notificationPrice =
                        sharedSteps.getB2bClient().getNotificationPrice(notificationPaymentItem.getPagoPa().getCreditorTaxId(), notificationPaymentItem.getPagoPa().getNoticeCode());
                try {
                    Assertions.assertEquals(notificationPrice.getIun(), sharedSteps.getIunVersionamento());
                    if (price != null) {
                        log.info("Costo notifica: {} destinatario: {}", notificationPrice.getAmount(), destinatario);
                        Assertions.assertEquals(Integer.parseInt(price), notificationPrice.getAmount());
                    }
                    if (notificationPrice.getRefinementDate() != null) {
                        Assertions.assertEquals(OffsetDateTime.now().toLocalDate(), notificationPrice.getRefinementDate().toLocalDate());
                    }
                } catch (AssertionFailedError assertionFailedError) {
                    sharedSteps.throwAssertionErrorWithIUN(assertionFailedError);
                }
            }
        }
    }

    private FullSentNotificationV26 waitForRequestAccepted(NewNotificationResponse response, String pollingStrategy) {
        IPnPollingService pollingService = sharedSteps.getB2bUtils().getPollingFactory().getPollingService(getPollingStrategy(pollingStrategy));
        PnPollingResponseV26 pollingResponse = (PnPollingResponseV26) pollingService.waitForEvent(response.getNotificationRequestId(), PnPollingParameter.builder().value(ACCEPTED).build());
        FullSentNotificationV26 result = pollingResponse.getNotification() == null ? null : pollingResponse.getNotification();
        sharedSteps.setFullSentNotificationV26(result);
        return result;
    }

    private String waitForRequestRefused(NewNotificationResponse response, String pollingStrategy) {
        log.info("Request status for " + response.getNotificationRequestId());
        long startTime = System.currentTimeMillis();

        IPnPollingService pollingService = sharedSteps.getB2bUtils().getPollingFactory().getPollingService(getPollingStrategy(pollingStrategy));
        PnPollingResponseV26 pollingResponse = (PnPollingResponseV26) pollingService.waitForEvent(response.getNotificationRequestId(), PnPollingParameter.builder().value(REFUSED).build());

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
        PnPollingResponseV26 pollingResponse = (PnPollingResponseV26) pollingService.waitForEvent(response.getNotificationRequestId(), PnPollingParameter.builder().value(REFUSED).build());
        return pollingResponse.getResult();
    }

    private String getPollingStrategy(String pollingStrategy) {
        return switch (pollingStrategy) {
            case TIMELINE_RAPID -> PnPollingStrategy.TIMELINE_RAPID_V26;
            case TIMELINE_SLOW -> PnPollingStrategy.TIMELINE_SLOW_V26;
            case STATUS_RAPID -> PnPollingStrategy.STATUS_RAPID_V26;
            case STATUS_SLOW -> PnPollingStrategy.STATUS_SLOW_V26;
            case TIMELINE_SLOW_E2E -> PnPollingStrategy.TIMELINE_SLOW_E2E_V26;
            case TIMELINE_EXTRA_RAPID -> PnPollingStrategy.TIMELINE_EXTRA_RAPID_V26;
            case STATUS_EXTRA_RAPID -> PnPollingStrategy.STATUS_EXTRA_RAPID_V26;
            case VALIDATION_STATUS -> PnPollingStrategy.VALIDATION_STATUS_V26;
            case VALIDATION_STATUS_ACCEPTATION_SHORT -> PnPollingStrategy.VALIDATION_STATUS_ACCEPTATION_SHORT_V26;
            case VALIDATION_STATUS_EXTRA_RAPID -> PnPollingStrategy.VALIDATION_STATUS_ACCEPTATION_EXTRA_RAPID_V26;
            case VALIDATION_STATUS_NO_ACCEPTATION -> PnPollingStrategy.VALIDATION_STATUS_NO_ACCEPTATION_V26;
            case WEBHOOK -> PnPollingStrategy.WEBHOOK_V26;
            default ->
                    throw new RuntimeException("PnPollingStrategy non riconosciuta per la versione V24: " + pollingStrategy);
        };
    }

    private NotificationDocument preloadDocument(NotificationDocument document) throws IOException {
        PnPaB2bUtils.Pair<String, String> preloadDocument = sharedSteps.getB2bUtils().preloadGeneric(document.getRef().getKey(), LOAD_TO_PRESIGNED);
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

    public void documentSetKey(NotificationDocument notificationDocument, String key) {
        notificationDocument.getRef().setKey(key);
    }

    public void documentSetVersionToken(NotificationDocument notificationDocument, String version) {
        notificationDocument.getRef().setVersionToken(version);
    }

    public void documentSetDigests(NotificationDocument notificationDocument, String sha256) {
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

    private NewNotificationResponse getAndCheckSendNewNotification(NewNotificationRequestV24 request) {
        log.info(NEW_NOTIFICATION_REQUEST, request);
        NewNotificationResponse response = sharedSteps.getB2bUtils().getClient().sendNewNotificationV24(request);
        log.info(NEW_NOTIFICATION_REQUEST_RESPONSE, response);
        if (response != null) {
            try {
                log.info(NEW_NOTIFICATION_IUN, new String(Base64Utils.decodeFromString(response.getNotificationRequestId())));
            } catch (Exception e) {
                throw new PnB2bException(e.getMessage());
            }
        }
        return response;
    }

    private void preloadPayDocument(NewNotificationRequestV24 request) throws IOException {
        for (NotificationRecipientV23 recipient : request.getRecipients()) {
            List<NotificationPaymentItem> paymentList = recipient.getPayments();
            if (paymentList != null) {
                setAttachmentWithSleep(paymentList);
            }
        }
    }

    private void setAttachmentWithSleep(List<NotificationPaymentItem> paymentList) throws IOException {
        for (NotificationPaymentItem paymentInfo : paymentList) {
            try {
                Thread.sleep(sharedSteps.getB2bUtils().getRandom().nextInt(350));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new PnB2bException(e.getMessage());
            }
            if (paymentInfo.getPagoPa() != null) {
                paymentInfo.getPagoPa().setAttachment(preloadAttachment(paymentInfo.getPagoPa().getAttachment()));
            }
            if (paymentInfo.getF24() != null) {
                paymentInfo.getF24().setMetadataAttachment(preloadMetadataAttachment(paymentInfo.getF24().getMetadataAttachment()));
            }
        }
    }

    private NotificationMetadataAttachment preloadMetadataAttachment(NotificationMetadataAttachment attachment) throws IOException {
        if (attachment != null) {
            Pair<String, String> preloadAttachment = sharedSteps.getB2bUtils().preloadGeneric(attachment.getRef().getKey(), LOAD_TO_PRESIGNED_METADATI);
            metadataAttachmentSetKey(attachment, preloadAttachment.getValue1());
            metadataAttachmentSetVersionToken(attachment, "v1");
            metadataAttachmentSetDigests(attachment, preloadAttachment.getValue2());
            return attachment;
        }
        return null;
    }

    private void metadataAttachmentSetKey(NotificationMetadataAttachment notificationMetadataAttachment, String key) {
        notificationMetadataAttachment.getRef().setKey(key);
    }

    private void metadataAttachmentSetVersionToken(NotificationMetadataAttachment notificationMetadataAttachment, String version) {
        notificationMetadataAttachment.getRef().setVersionToken(version);
    }

    private void metadataAttachmentSetDigests(NotificationMetadataAttachment notificationMetadataAttachment, String sha256) {
        notificationMetadataAttachment.digests(new NotificationAttachmentDigests().sha256(sha256));
    }
}
