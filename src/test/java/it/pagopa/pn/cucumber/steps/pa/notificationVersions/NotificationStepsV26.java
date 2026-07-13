package it.pagopa.pn.cucumber.steps.pa.notificationVersions;

import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.FullSentNotificationV29;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.NewNotificationRequestStatusResponseV26;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.NewNotificationRequestV26;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.NewNotificationResponse;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.NotificationAttachmentBodyRef;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.NotificationDigitalAddress;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.NotificationDocument;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.NotificationFeePolicy;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.NotificationPaymentAttachment;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.NotificationPaymentItem;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.NotificationPriceResponse;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.NotificationRecipientV24;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.NotificationRequestRefusedProblemError;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.RequestStatus;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementCategoryV28;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementV28;
import it.pagopa.pn.client.b2b.pa.polling.dto.PnPollingResponseV29;
import it.pagopa.pn.client.b2b.pa.service.IPnPaB2bClient;
import it.pagopa.pn.cucumber.steps.SharedSteps;
import it.pagopa.pn.cucumber.steps.pa.utilityVersions.NotificationUtilsV26;
import it.pagopa.pn.client.b2b.pa.domain.Costanti;
import it.pagopa.pn.client.b2b.pa.domain.Destinatario;
import it.pagopa.pn.client.b2b.pa.utils.FiscalCodeGenerator;
import it.pagopa.pn.cucumber.utils.NotificationValue;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.opentest4j.AssertionFailedError;
import org.springframework.util.Base64Utils;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static it.pagopa.pn.cucumber.steps.SharedSteps.threadWait;
import static it.pagopa.pn.cucumber.steps.pa.utilityVersions.B2bUtils.APPLICATION_PDF;
import static it.pagopa.pn.cucumber.steps.pa.utilityVersions.B2bUtils.NEW_NOTIFICATION_IUN;
import static it.pagopa.pn.cucumber.steps.pa.utilityVersions.B2bUtils.NEW_NOTIFICATION_RESPONSE;
import static it.pagopa.pn.client.b2b.pa.domain.Costanti.NOTIFICATION_INJECTION_ALLEGATO;
import static it.pagopa.pn.client.b2b.pa.domain.Costanti.NOTIFICATION_STATUS_ACCEPTED;
import static it.pagopa.pn.client.b2b.pa.domain.Costanti.NOTIFICATION_STATUS_CANCELLED;
import static it.pagopa.pn.client.b2b.pa.domain.Costanti.NOTIFICATION_STATUS_REFUSED;
import static it.pagopa.pn.client.b2b.pa.domain.Costanti.OVERSIZE_ALLEGATO;
import static it.pagopa.pn.client.b2b.pa.domain.Costanti.OVER_15_ALLEGATO;
import static it.pagopa.pn.client.b2b.pa.domain.Costanti.VALIDATION_STATUS;
import static it.pagopa.pn.client.b2b.pa.domain.Costanti.WRONG_EXTENSION;
import static it.pagopa.pn.cucumber.utils.NotificationValue.DOCUMENT;
import static it.pagopa.pn.cucumber.utils.NotificationValue.PAYMENT_PAGOPA_FORM;
import static it.pagopa.pn.cucumber.utils.NotificationValue.getDefaultValue;
import static it.pagopa.pn.cucumber.utils.NotificationValue.getValue;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

@Data
@Slf4j
public class NotificationStepsV26 implements NotificationStepsInterface {

    private NewNotificationRequestV26 notificationRequest;
    private NewNotificationResponse notificationResponse;
    private final SharedSteps sharedSteps;
    private final IPnPaB2bClient b2bClient;
    private final NotificationVersion version;
    private final NotificationUtilsV26 utils;

    public NotificationStepsV26(SharedSteps sharedSteps) {
        this.sharedSteps = sharedSteps;
        b2bClient = sharedSteps.getB2bClient();
        version = NotificationVersion.V26;
        utils = new NotificationUtilsV26(sharedSteps.getContext(), b2bClient, sharedSteps.getPollingFactory());
    }

    @Override
    public Object getFullSentNotification() {
        return b2bClient.getSentNotificationV29(sharedSteps.getNotificationIun());
    }

    private FullSentNotificationV29 getFullSentNotificationVersioned() {
        return (FullSentNotificationV29) getFullSentNotification();
    }

    @Override
    public void prepareNotificationRequest(Map<String, String> data) {
        notificationRequest = utils.convertNotificationRequest(data);
        sharedSteps.setVersionUsed(version);
    }

    @Override
    public void prepareNotificationRequestSimileAllaPrecedente(boolean isCreditorTaxIdUguale, boolean isCodiceAvvisoUguale, boolean isPaProtocolNumberUguale, String idempotenceToken) {
        NewNotificationRequestV26 newNotificationRequest = utils.convertNotificationRequest(new HashMap<>());
        NotificationRecipientV24 newRecipient = utils.convertNotificationRecipient(new HashMap<>());

        NotificationRecipientV24 oldRecipient = notificationRequest.getRecipients().get(0);
        newRecipient.setDenomination(oldRecipient.getDenomination());
        newRecipient.setTaxId(oldRecipient.getTaxId());
        newRecipient.setRecipientType(oldRecipient.getRecipientType());

        if (isCreditorTaxIdUguale) {
            Assertions.assertNotNull(notificationRequest.getRecipients().get(0).getPayments());
            String creditorTaxId = Objects.requireNonNull(Objects.requireNonNull(oldRecipient.getPayments()).get(0).getPagoPa()).getCreditorTaxId();
            newRecipient.getPayments().get(0).getPagoPa().setCreditorTaxId(creditorTaxId);
        }
        if (isCodiceAvvisoUguale) {
            Assertions.assertNotNull(notificationRequest.getRecipients().get(0).getPayments());
            String noticeCode = Objects.requireNonNull(Objects.requireNonNull(oldRecipient.getPayments()).get(0).getPagoPa()).getNoticeCode();
            newRecipient.getPayments().get(0).getPagoPa().setNoticeCode(noticeCode);
        }
        if (isPaProtocolNumberUguale) {
            newNotificationRequest.setPaProtocolNumber(notificationRequest.getPaProtocolNumber());
        }
        if (idempotenceToken != null) {
            newNotificationRequest.setIdempotenceToken(idempotenceToken);
        }

        newNotificationRequest.setSubject(notificationRequest.getSubject());
        newNotificationRequest.setSenderDenomination(notificationRequest.getSenderDenomination());
        newNotificationRequest.addRecipientsItem(newRecipient);

        notificationRequest = newNotificationRequest;
    }

    @Override
    public void resetNotificationRequest() {
        notificationRequest.setRecipients(new ArrayList<>());
        NotificationAttachmentBodyRef ref = new NotificationAttachmentBodyRef()
                .key("classpath:/sample.pdf");
        NotificationDocument document = new NotificationDocument()
                .contentType("application/pdf")
                .ref(ref);
        notificationRequest.setDocuments(List.of(document));
    }

    @Override
    public void addRecipientToNotification(Destinatario destinatario, Map<String, String> data) {
        if (destinatario != null && destinatario.isNessuno()) return;
        NotificationRecipientV24 notificationRecipient = utils.convertNotificationRecipient(data);
        if (notificationRequest.getNotificationFeePolicy() == NotificationFeePolicy.DELIVERY_MODE
                && NotificationValue.getValue(data, NotificationValue.PAYMENT.key) != null) {
            String pagopaFormValue = getValue(data, PAYMENT_PAGOPA_FORM.key);
            if (pagopaFormValue != null && !pagopaFormValue.equalsIgnoreCase("NO")) {
                for (NotificationPaymentItem payments : Objects.requireNonNull(notificationRecipient.getPayments())) {
                    Objects.requireNonNull(payments.getPagoPa()).setApplyCost(true);
                }
            }
        }
        if (destinatario != null) {
            notificationRecipient.setDenomination(destinatario.getDenomination());
            notificationRecipient.setTaxId(destinatario.isSignorCasuale() ?
                    FiscalCodeGenerator.generateCF(System.nanoTime()) : destinatario.getTaxId());
            notificationRecipient.setRecipientType(NotificationRecipientV24.RecipientTypeEnum.valueOf(destinatario.getRecipientType()));
            /* Nei vecchi metodi @And("Destinatario xxx") denomination e taxId venivano sempre settati
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
    public void addRecipientToNotificationSpecialCondition(Destinatario destinatario, Map<String, String> data, String condition, Integer otherRecipientIndex) {
        switch (condition.toUpperCase()) {
            case "SAME_IUV_AS_RECIPIENT_INDEX" -> {
                Assertions.assertDoesNotThrow(() -> Objects.requireNonNull(notificationRequest.getRecipients().get(otherRecipientIndex - 1).getPayments()).get(0));
                String previousIUV = notificationRequest.getRecipients().get(otherRecipientIndex - 1).getPayments().get(0).getPagoPa().getNoticeCode();
                int currentRecipientNumber = notificationRequest.getRecipients().size();
                addRecipientToNotification(destinatario, data);
                NotificationRecipientV24 recipientAdded = notificationRequest.getRecipients().get(currentRecipientNumber);
                recipientAdded.getPayments().get(0).getPagoPa().setNoticeCode(previousIUV);
            }
        }
    }

    @Override
    public void setSenderTaxId(String senderTaxId) {
        notificationRequest.setSenderTaxId(senderTaxId);
    }

    @Override
    public String getSenderTaxId() {
        return notificationRequest.getSenderTaxId();
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
    public void sendNotification(int wait, String status, String pollingStrategy) {
        try {
            Assertions.assertDoesNotThrow(() -> {
                uploadNotification(null);
                if (status.equalsIgnoreCase(NOTIFICATION_STATUS_ACCEPTED)) {
                    threadWait(wait);
                    PnPollingResponseV29 pollingResponse = utils.waitForEvent(notificationResponse, pollingStrategy, NOTIFICATION_STATUS_ACCEPTED);
                    threadWait(wait);
                    assertThat(pollingResponse.getNotification())
                            .as("La fullSentNotification della notifica appena creata non dev'essere null")
                            .isNotNull();
                } else if (status.equalsIgnoreCase(NOTIFICATION_STATUS_REFUSED)) {
                    log.info("Request status for " + sharedSteps.getNotificationIun());
                    long startTime = System.currentTimeMillis();
                    PnPollingResponseV29 pollingResponse = utils.waitForEvent(notificationResponse, pollingStrategy, NOTIFICATION_STATUS_REFUSED);
                    long endTime = System.currentTimeMillis();
                    log.info("Execution time {}ms", (endTime - startTime));
                    StringBuilder error = new StringBuilder();
                    if (pollingResponse.getStatusResponse() != null
                            && pollingResponse.getStatusResponse().getErrors() != null
                            && !pollingResponse.getStatusResponse().getErrors().isEmpty()) {
                        for (NotificationRequestRefusedProblemError err : pollingResponse.getStatusResponse().getErrors()) {
                            error.append(" ").append(err.getDetail());
                        }
                    }
                    log.info("Detail status {}", error);
                    String errorCode = error.toString();
                    sharedSteps.setErrorCode(errorCode);
                    threadWait(wait);
                    assertThat(errorCode).as("Il codice di errore non dev'essere vuoto").isNotEmpty();
                } else if (status.equalsIgnoreCase(NOTIFICATION_STATUS_CANCELLED)) {
                    RequestStatus response = b2bClient.notificationCancellation(sharedSteps.getNotificationIun());
                    assertThat(response).as("La response della chiamata di cancellazione non dev'essere null").isNotNull();
                    assertThat(response.getDetails()).as("I details della response della chiamata di cancellazione non devono essere null").isNotNull();
                    assertThat(response.getDetails()).as("I details della response della chiamata di cancellazione non devono essere vuoti").isNotEmpty();
                    assertThat(response.getDetails().get(0).getCode()).isEqualToIgnoringCase("NOTIFICATION_CANCELLATION_ACCEPTED");
                    PnPollingResponseV29 pollingResponse = utils.waitForEvent(notificationResponse, pollingStrategy, NOTIFICATION_STATUS_REFUSED);
                    threadWait(wait);
                    assertThat(pollingResponse.getResult())
                            .as("La notifica dovrebbe essere stata annullata prima di andare in REFUSED")
                            .isFalse();
                }
            });
        } catch (AssertionError assertionError) {
            sharedSteps.throwAssertionErrorWithIUN(assertionError);
        }
    }

    public Object uploadNotification(String errorType) throws IOException {
        sharedSteps.setNotificationCreationDate(OffsetDateTime.now(java.time.ZoneOffset.UTC));
        notificationResponse = utils.uploadNotification(notificationRequest, errorType);
        log.info(NEW_NOTIFICATION_RESPONSE, notificationResponse);
        String iun = new String(Base64Utils.decodeFromString(notificationResponse.getNotificationRequestId()));
        assertThat(iun).as("Lo IUN generato in fase di invio notifica non può essere null").isNotNull();
        log.info(NEW_NOTIFICATION_IUN, iun);
        sharedSteps.setNotificationIun(iun);
        return notificationResponse;
    }

    @Override
    public void setIuvToRecipient(Integer posizione, String iuvGPD) {
        Objects.requireNonNull(Objects.requireNonNull(
                this.notificationRequest.getRecipients().get(0).getPayments()).get(posizione).getPagoPa()).setNoticeCode(iuvGPD);
    }

    @Override
    public void addDocumentItems(int numAllegati) {
        for (int i = 0; i < numAllegati; i++) {
            notificationRequest.addDocumentsItem(
                    new NotificationDocument().contentType(APPLICATION_PDF).ref(new NotificationAttachmentBodyRef().key(getDefaultValue(DOCUMENT.key))));
        }
    }

    @Override
    public void performPriceVerification(String price, String date, Integer destinatario) {
        FullSentNotificationV29 fullSentNotification = getFullSentNotificationVersioned();
        List<NotificationPaymentItem> listNotificationPaymentItem = fullSentNotification.getRecipients().get(destinatario).getPayments();
        if (listNotificationPaymentItem != null) {
            for (NotificationPaymentItem notificationPaymentItem : listNotificationPaymentItem) {
                NotificationPriceResponse notificationPrice = b2bClient.getNotificationPrice(
                        notificationPaymentItem.getPagoPa().getCreditorTaxId(),
                        notificationPaymentItem.getPagoPa().getNoticeCode());
                try {
                    Assertions.assertEquals(notificationPrice.getIun(), sharedSteps.getNotificationIun());
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

    @Override
    public void uploadNotificationAllegatiUgualiPagamento() throws IOException {
        List<NotificationDocument> newDocs = new ArrayList<>();
        for (NotificationDocument doc : notificationRequest.getDocuments()) {
            newDocs.add(utils.preloadDocument(doc));
        }
        notificationRequest.setDocuments(newDocs);

        for (NotificationRecipientV24 recipient : notificationRequest.getRecipients()) {
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
                        paymentInfo.getF24().setMetadataAttachment(utils.preloadMetadataAttachment(paymentInfo.getF24().getMetadataAttachment()));
                    }
                }

            }
        }
        b2bClient.sendNewNotificationV26(notificationRequest);
    }

    @Override
    public void addIuvGpdToDestinatario(String denominazione, String iuvGpd, Integer paymentIndex) {
        for (NotificationRecipientV24 recipient : notificationRequest.getRecipients()) {
            if (recipient.getDenomination().equalsIgnoreCase(denominazione)) {
                Objects.requireNonNull(Objects.requireNonNull(recipient.getPayments()).get(paymentIndex).getPagoPa()).setNoticeCode(iuvGpd);
            }
        }
    }

    @Override
    public void addIuvGpdToDestinatario(Integer recIndex, String iuvGpd, Integer recipientPaymentIndex) {
        NotificationRecipientV24 recipient = notificationRequest.getRecipients().get(recIndex);
        Objects.requireNonNull(Objects.requireNonNull(recipient.getPayments()).get(recipientPaymentIndex).getPagoPa()).setNoticeCode(iuvGpd);
    }

    @Override
    public List<String> getDatiPagamento(Integer destinatario, Integer pagamento) {
        FullSentNotificationV29 fullSentNotification = getFullSentNotificationVersioned();
        return Arrays.asList(
                Objects.requireNonNull(Objects.requireNonNull(fullSentNotification.getRecipients().get(destinatario).getPayments()).get(pagamento).getPagoPa()).getCreditorTaxId(),
                Objects.requireNonNull(Objects.requireNonNull(fullSentNotification.getRecipients().get(destinatario).getPayments()).get(pagamento).getPagoPa()).getNoticeCode());
    }

    @Override
    public void waitForTimelineElement(String timelineElementCategory, Integer attempts) {
        TimelineElementV28 timelineElement = null;
        for (int i = 0; i < attempts; i++) {
            FullSentNotificationV29 fsn = getFullSentNotificationVersioned();
            log.info("NOTIFICATION_TIMELINE: " + fsn.getTimeline());
            timelineElement = fsn.getTimeline()
                    .stream().filter(elem -> Objects.requireNonNull(elem.getCategory().getValue())
                            .equals(TimelineElementCategoryV28.valueOf(timelineElementCategory).getValue()))
                    .findAny().orElse(null);
            if (timelineElement != null) {
                break;
            } else {
                threadWait(sharedSteps.getWorkFlowWait());
            }
        }
        assertThat(timelineElement).as("Il timeline element restituito non dev'essere null").isNotNull();
    }

    @Override
    public void getNotificationRequestStatus(String requestId) {
        try {
            Assertions.assertDoesNotThrow(() -> b2bClient.getNotificationRequestStatusV26(requestId));
        } catch (AssertionFailedError assertionFailedError) {
            String message = assertionFailedError.getMessage() +
                    "{RequestID: " + (notificationResponse == null ? "NULL" : notificationResponse.getNotificationRequestId()) + " }";
            throw new AssertionFailedError(message, assertionFailedError.getExpected(), assertionFailedError.getActual(), assertionFailedError.getCause());
        }
    }

    @Override
    public void checkTaxonomyCode() {
        FullSentNotificationV29 fullSentNotification = getFullSentNotificationVersioned();
        assertThat(fullSentNotification.getTaxonomyCode())
                .as("Il taxonomyCode nella notifica inviata non dovrebbe essere null")
                .isNotNull();

        if (notificationRequest.getTaxonomyCode() != null) {
            assertThat(notificationRequest.getTaxonomyCode())
                    .as("Il taxonomyCode nella richiesta di notifica dovrebbe essere uguale al taxonomyCode nella notifica inviata")
                    .isEqualTo(fullSentNotification.getTaxonomyCode());
        }
    }

    @Override
    public int getRecipientsSize() {
        return notificationRequest.getRecipients().size();
    }

    @Override
    public String getRecipientNoticeCode(int recipientIndex, int paymentIndex) {
        return notificationRequest.getRecipients().get(recipientIndex).getPayments().get(paymentIndex).getPagoPa().getNoticeCode();
    }

    @Override
    public String getRecipientCreditorTaxId(int recipientIndex, int paymentIndex) {
        return notificationRequest.getRecipients().get(recipientIndex).getPayments().get(paymentIndex).getPagoPa().getCreditorTaxId();
    }

    @Override
    public void produceEvidence() {
        assertThat(notificationResponse)
                .as("La risposta della nuova notifica non dovrebbe essere null")
                .isNotNull();
        log.info("METADATI: " + '\n' + notificationResponse);
        log.info("REQUEST-ID: " + '\n' + notificationResponse.getNotificationRequestId());
    }

    @Override
    public void verifyCorrectAcquisition() {
        assertSoftly(softly -> {
            softly.assertThat(notificationResponse)
                    .as("La risposta della nuova notifica non dovrebbe essere null")
                    .isNotNull();

            softly.assertThat(notificationResponse)
                    .as("L'ID della richiesta di notifica non dovrebbe essere null")
                    .isNotNull();

            softly.assertThat(b2bClient.getNotificationRequestStatusV26(notificationResponse.getNotificationRequestId()))
                    .as("Lo stato della richiesta di notifica non dovrebbe essere null.",
                            notificationResponse.getNotificationRequestId())
                    .isNotNull();
        });
    }

    @Override
    public void verifyStatus(boolean withNotificationRequestId, boolean withPaProtocolNumber, boolean withIdempotenceToken) {
        String notificationRequestId = withNotificationRequestId ? notificationResponse.getNotificationRequestId() : null;
        String paProtocolNumber = withPaProtocolNumber ? notificationResponse.getPaProtocolNumber() : null;
        String idempotenceToken = withIdempotenceToken ? notificationResponse.getIdempotenceToken() : null;

        NewNotificationRequestStatusResponseV26 newNotificationRequestStatusResponse = Assertions.assertDoesNotThrow(() ->
                b2bClient.getNotificationRequestStatusAllParamV26(notificationRequestId, paProtocolNumber, idempotenceToken));
        assertThat(newNotificationRequestStatusResponse.getNotificationRequestStatus())
                .as("Lo stato della richiesta di notifica non dovrebbe essere null")
                .isNotNull();
        log.debug(newNotificationRequestStatusResponse.getNotificationRequestStatus());
    }

    @Override
    public void verifyNotification(String notificationIun) {
        utils.verifyNotification(notificationIun);
    }

    @Override
    public void createAndSendNotificationRequestWithError(String errorType) {
        try {
            switch (errorType) {
                case WRONG_EXTENSION -> {
                    NotificationDocument notificationDocument = notificationRequest.getDocuments().get(0);
                    notificationDocument.getRef().setKey("classpath:/sample.txt");
                }
                case OVERSIZE_ALLEGATO -> {
                    NotificationDocument notificationDocument = notificationRequest.getDocuments().get(0);
                    notificationDocument.getRef().setKey("classpath:/200MB_PDF.pdf");
                }
                case NOTIFICATION_INJECTION_ALLEGATO -> {
                    NotificationDocument injectionDocument = utils.newDocument("classpath:/sample_injection.xml.pdf");
                    notificationRequest.setDocuments(List.of(injectionDocument));
                }
                case OVER_15_ALLEGATO -> {
                    NotificationDocument notificationDocument = utils.newDocument("classpath:/sample.pdf");
                    List<NotificationDocument> newDocs = new ArrayList<>();
                    for (int i = 0; i < 20; i++) {
                        newDocs.add(notificationDocument);
                    }
                    notificationRequest.setDocuments(newDocs);
                }
            }
            uploadNotification(errorType);
        } catch (IOException ioException) {
            throw new RuntimeException(errorType + " - Errore imprevisto in fase di creazione request: " + ioException.getMessage());
        }

        long startTime = System.currentTimeMillis();
        PnPollingResponseV29 pollingResponse = utils.waitForEvent(notificationResponse, VALIDATION_STATUS, NOTIFICATION_STATUS_REFUSED);
        long endTime = System.currentTimeMillis();
        log.info("Execution time {}ms", (endTime - startTime));

        StringBuilder error = new StringBuilder();
        if (pollingResponse.getStatusResponse() != null && pollingResponse.getStatusResponse().getErrors() != null && !pollingResponse.getStatusResponse().getErrors().isEmpty()) {
            for (NotificationRequestRefusedProblemError err : pollingResponse.getStatusResponse().getErrors()) {
                error.append(" ").append(err.getDetail());
            }
        }
        log.info("Detail status {}", error);
        sharedSteps.setErrorCode(error.toString());
    }

    @Override
    public String getCreditorTaxId(int recipientIndex) {
        return notificationRequest.getRecipients().get(recipientIndex).getPayments().get(0).getPagoPa().getCreditorTaxId();
    }

    @Override
    public String getNoticeCode(int recipientIndex) {
        return notificationRequest.getRecipients().get(recipientIndex).getPayments().get(0).getPagoPa().getNoticeCode();
    }

    @Override
    public void setApplyCostFalse(int recipientIndex, int paymentIndex) {
        notificationRequest.getRecipients().get(recipientIndex).getPayments().get(paymentIndex).getPagoPa().setApplyCost(false);
    }
}
