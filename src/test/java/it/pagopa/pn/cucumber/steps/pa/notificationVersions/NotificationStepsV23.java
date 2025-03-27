package it.pagopa.pn.cucumber.steps.pa.notificationVersions;

import it.pagopa.pn.client.b2b.pa.exception.PnB2bException;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.*;
import it.pagopa.pn.client.b2b.pa.polling.IPnPollingService;
import it.pagopa.pn.client.b2b.pa.polling.design.PnPollingStrategy;
import it.pagopa.pn.client.b2b.pa.polling.dto.PnPollingParameter;
import it.pagopa.pn.client.b2b.pa.polling.dto.PnPollingResponseV23;
import it.pagopa.pn.cucumber.steps.SharedSteps;
import it.pagopa.pn.cucumber.utils.FiscalCodeGenerator;
import it.pagopa.pn.cucumber.utils.NotificationValue;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.opentest4j.AssertionFailedError;
import org.springframework.util.Base64Utils;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.*;

import static it.pagopa.pn.client.b2b.pa.PnPaB2bUtils.*;
import static it.pagopa.pn.cucumber.steps.SharedSteps.threadWait;
import static it.pagopa.pn.cucumber.steps.pa.notificationVersions.Costanti.*;
import static it.pagopa.pn.cucumber.steps.pa.notificationVersions.Destinatario.DESTINATARIO_NESSUNO;
import static it.pagopa.pn.cucumber.steps.pa.notificationVersions.Destinatario.DESTINATARIO_SIGNOR_CASUALE;
import static it.pagopa.pn.cucumber.utils.NotificationValue.*;

@Data
@Slf4j
public class NotificationStepsV23 implements NotificationStepsInterface {

    private NewNotificationRequestV23 notificationRequest;
    private NewNotificationResponse notificationResponse;
    private FullSentNotificationV23 fullSentNotification;
    private OffsetDateTime notificationCreationDate;
    private final SharedSteps.NotificationVersion version;
    private final SharedSteps sharedSteps;

    public NotificationStepsV23(SharedSteps sharedSteps) {
        version = SharedSteps.NotificationVersion.V23;
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
        notificationRequest = sharedSteps.getDataTableTypeUtil().convertNotificationRequestV23(data);
        sharedSteps.setVersionUsed(version);
    }

    @Override
    public void addRecipientToNotification(String recipientName, Map<String, String> data) {
        Destinatario destinatario = Destinatario.getByName(recipientName);
        if (destinatario != null && destinatario.equals(DESTINATARIO_NESSUNO)) return;
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
        if (destinatario != null) {
            notificationRecipient.setDenomination(destinatario.getDenomination());
            notificationRecipient.setTaxId(destinatario.equals(DESTINATARIO_SIGNOR_CASUALE) ?
                    FiscalCodeGenerator.generateCF(System.nanoTime()) : destinatario.getTaxId());
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
                    sharedSteps.setNotificationIun(fullSentNotification.getIun());
                } else if (status.equalsIgnoreCase(NOTIFICATION_STATUS_REFUSED)) {
                    String errorCode = waitForRequestRefused(notificationResponse, pollingStrategy);
                    sharedSteps.setErrorCode(errorCode);
                    threadWait(wait);
                    Assertions.assertFalse(errorCode.isEmpty());
                } else if (status.equalsIgnoreCase(NOTIFICATION_STATUS_NOT_REFUSED)) {
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
        //TODO MATTEO IMPLEMENTARE
    }

    private FullSentNotificationV23 waitForRequestAccepted(NewNotificationResponse response, String pollingStrategy) {
        IPnPollingService pollingService = sharedSteps.getB2bUtils().getPollingFactory().getPollingService(getPollingStrategy(pollingStrategy));
        PnPollingResponseV23 pollingResponse = (PnPollingResponseV23) pollingService.waitForEvent(response.getNotificationRequestId(), PnPollingParameter.builder().value(ACCEPTED).build());
        FullSentNotificationV23 result = pollingResponse.getNotification() == null ? null : pollingResponse.getNotification();
//        sharedSteps.setFullSentNotificationV23(result);//TODO MATTEO TEST PER RIMUOVERE FSN
        return result;
    }

    private String waitForRequestRefused(NewNotificationResponse response, String pollingStrategy) {
        log.info("Request status for " + response.getNotificationRequestId());
        long startTime = System.currentTimeMillis();

        IPnPollingService pollingService = sharedSteps.getB2bUtils().getPollingFactory().getPollingService(getPollingStrategy(pollingStrategy));
        PnPollingResponseV23 pollingResponse = (PnPollingResponseV23) pollingService.waitForEvent(response.getNotificationRequestId(), PnPollingParameter.builder().value(REFUSED).build());

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
        PnPollingResponseV23 pollingResponse = (PnPollingResponseV23) pollingService.waitForEvent(response.getNotificationRequestId(), PnPollingParameter.builder().value(REFUSED).build());
        return pollingResponse.getResult();
    }

    private String getPollingStrategy(String pollingStrategy) {
        return switch (pollingStrategy) {
            case TIMELINE_RAPID -> PnPollingStrategy.TIMELINE_RAPID_V23;
            case TIMELINE_SLOW -> PnPollingStrategy.TIMELINE_SLOW_V23;
            case STATUS_RAPID -> PnPollingStrategy.STATUS_RAPID_V23;
            case STATUS_SLOW -> PnPollingStrategy.STATUS_SLOW_V23;
            case TIMELINE_SLOW_E2E -> PnPollingStrategy.TIMELINE_SLOW_E2E_V23;
            case TIMELINE_EXTRA_RAPID -> PnPollingStrategy.TIMELINE_EXTRA_RAPID_V23;
            case STATUS_EXTRA_RAPID -> PnPollingStrategy.STATUS_EXTRA_RAPID_V23;
            case VALIDATION_STATUS -> PnPollingStrategy.VALIDATION_STATUS_V23;
            case VALIDATION_STATUS_ACCEPTATION_SHORT -> PnPollingStrategy.VALIDATION_STATUS_ACCEPTATION_SHORT_V23;
            case VALIDATION_STATUS_EXTRA_RAPID -> PnPollingStrategy.VALIDATION_STATUS_ACCEPTATION_EXTRA_RAPID_V23;
            case VALIDATION_STATUS_NO_ACCEPTATION -> PnPollingStrategy.VALIDATION_STATUS_NO_ACCEPTATION_V23;
            case WEBHOOK -> PnPollingStrategy.WEBHOOK_V23;
            default ->
                    throw new RuntimeException("PnPollingStrategy non riconosciuta per la versione V23: " + pollingStrategy);
        };
    }

    private NotificationDocument preloadDocument(NotificationDocument document) throws IOException {
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

    private NewNotificationResponse getAndCheckSendNewNotification(NewNotificationRequestV23 request) {
        log.info(NEW_NOTIFICATION_REQUEST, request);
        NewNotificationResponse response = sharedSteps.getB2bUtils().getClient().sendNewNotificationV23(request);
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

    private void preloadPayDocument(NewNotificationRequestV23 request) throws IOException {
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

    @Override
    public void uploadNotificationAllegatiUgualiPagamento() throws IOException {
        List<NotificationDocument> newDocs = new ArrayList<>();
        for (NotificationDocument doc : notificationRequest.getDocuments()) {
            newDocs.add(preloadDocument(doc));
        }
        notificationRequest.setDocuments(newDocs);

        for (NotificationRecipientV23 recipient : notificationRequest.getRecipients()) {
            List<NotificationPaymentItem> paymentList = recipient.getPayments();
            if (paymentList != null) {
                for (NotificationPaymentItem paymentInfo : paymentList) {
                    if (paymentInfo.getPagoPa() != null) {
                        paymentInfo.getPagoPa().setAttachment(new NotificationPaymentAttachment()
                                .ref(notificationRequest.getDocuments().get(0).getRef())
                                .digests(notificationRequest.getDocuments().get(0).getDigests())
                                .contentType(notificationRequest.getDocuments().get(0).getContentType()));
                    }
                    if (paymentInfo.getF24() != null) {
                        paymentInfo.getF24().setMetadataAttachment(preloadMetadataAttachment(paymentInfo.getF24().getMetadataAttachment()));
                    }
                }

            }
        }
        getAndCheckSendNewNotification(notificationRequest);
    }

    @Override
    public void addIuvGdpToDestinatario(String denominazione, String iuvGdp, Integer posizione) {
        notificationRequest.getRecipients().get(0).denomination(denominazione).getPayments().get(posizione).getPagoPa().setNoticeCode(iuvGdp);
    }

    @Override
    public List<String> getDatiPagamento(Integer destinatario, Integer pagamento) {
        return Arrays.asList(
                Objects.requireNonNull(Objects.requireNonNull(fullSentNotification.getRecipients().get(destinatario).getPayments()).get(pagamento).getPagoPa()).getCreditorTaxId(),
                Objects.requireNonNull(Objects.requireNonNull(fullSentNotification.getRecipients().get(destinatario).getPayments()).get(pagamento).getPagoPa()).getNoticeCode());
    }
}


