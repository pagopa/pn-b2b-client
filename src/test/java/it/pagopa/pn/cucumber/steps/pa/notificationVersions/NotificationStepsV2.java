package it.pagopa.pn.cucumber.steps.pa.notificationVersions;

import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.*;
import it.pagopa.pn.client.b2b.pa.polling.dto.PnPollingResponseV20;
import it.pagopa.pn.client.b2b.pa.service.IPnPaB2bClient;
import it.pagopa.pn.cucumber.steps.SharedSteps;
import it.pagopa.pn.cucumber.steps.pa.utilityVersions.NotificationUtilsV2;
import it.pagopa.pn.cucumber.steps.utilitySteps.Costanti;
import it.pagopa.pn.cucumber.steps.utilitySteps.Destinatario;
import it.pagopa.pn.cucumber.utils.FiscalCodeGenerator;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.opentest4j.AssertionFailedError;
import org.springframework.util.Base64Utils;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.*;

import static it.pagopa.pn.cucumber.steps.SharedSteps.threadWait;
import static it.pagopa.pn.cucumber.steps.pa.utilityVersions.B2bUtils.*;
import static it.pagopa.pn.cucumber.steps.utilitySteps.Costanti.*;
import static it.pagopa.pn.cucumber.utils.NotificationValue.DOCUMENT;
import static it.pagopa.pn.cucumber.utils.NotificationValue.getDefaultValue;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

@Data
@Slf4j
public class NotificationStepsV2 implements NotificationStepsInterface {

    private NewNotificationRequest notificationRequest;
    private NewNotificationResponse notificationResponse;
    private final SharedSteps sharedSteps;
    private final IPnPaB2bClient b2bClient;
    private final NotificationVersion version;
    private final NotificationUtilsV2 utils;

    public NotificationStepsV2(SharedSteps sharedSteps) {
        this.sharedSteps = sharedSteps;
        b2bClient = sharedSteps.getB2bClient();
        version = NotificationVersion.V2;
        utils = new NotificationUtilsV2(sharedSteps.getContext(), b2bClient, sharedSteps.getPollingFactory());
    }

    @Override
    public Object getFullSentNotification() {
        return b2bClient.getSentNotificationV2(sharedSteps.getNotificationIun());
    }

    private FullSentNotificationV20 getFullSentNotificationVersioned() {
        return (FullSentNotificationV20) getFullSentNotification();
    }

    @Override
    public void prepareNotificationRequest(Map<String, String> data) {
        notificationRequest = utils.convertNotificationRequest(data);
        sharedSteps.setVersionUsed(version);
    }

    @Override
    public void prepareNotificationRequestSimileAllaPrecedente(boolean isCreditorTaxIdUguale, boolean isCodiceAvvisoUguale, boolean isPaProtocolNumberUguale, String idempotenceToken) {
        NewNotificationRequest newNotificationRequest = utils.convertNotificationRequest(new HashMap<>());
        NotificationRecipient newRecipient = utils.convertNotificationRecipient(new HashMap<>());

        NotificationRecipient oldRecipient = notificationRequest.getRecipients().get(0);
        newRecipient.setDenomination(oldRecipient.getDenomination());
        newRecipient.setTaxId(oldRecipient.getTaxId());
        newRecipient.setRecipientType(oldRecipient.getRecipientType());

        if (isCreditorTaxIdUguale) {
            Assertions.assertNotNull(notificationRequest.getRecipients().get(0).getPayment());
            String creditorTaxId = Objects.requireNonNull(Objects.requireNonNull(oldRecipient.getPayment())).getCreditorTaxId();
            newRecipient.getPayment().setCreditorTaxId(creditorTaxId);
        }
        if (isCodiceAvvisoUguale) {
            Assertions.assertNotNull(notificationRequest.getRecipients().get(0).getPayment());
            String noticeCode = Objects.requireNonNull(Objects.requireNonNull(oldRecipient.getPayment())).getNoticeCode();
            newRecipient.getPayment().setNoticeCode(noticeCode);
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
        NotificationRecipient notificationRecipient = utils.convertNotificationRecipient(data);
        if (destinatario != null) {
            notificationRecipient.setDenomination(destinatario.getDenomination());
            notificationRecipient.setTaxId(destinatario.isSignorCasuale() ?
                    FiscalCodeGenerator.generateCF(System.nanoTime()) : destinatario.getTaxId());
            notificationRecipient.setRecipientType(NotificationRecipient.RecipientTypeEnum.valueOf(destinatario.getRecipientType()));
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
                Assertions.assertDoesNotThrow(() -> Objects.requireNonNull(notificationRequest.getRecipients().get(otherRecipientIndex - 1).getPayment()));
                String previousIUV = notificationRequest.getRecipients().get(otherRecipientIndex).getPayment().getNoticeCode();
                int currentRecipientNumber = notificationRequest.getRecipients().size();
                addRecipientToNotification(destinatario, data);
                NotificationRecipient recipientAdded = notificationRequest.getRecipients().get(currentRecipientNumber + 1);
                recipientAdded.getPayment().setNoticeCode(previousIUV);
            }
        }
    }

    @Override
    public void setSenderTaxId(String senderTaxId) {
        this.notificationRequest.setSenderTaxId(senderTaxId);
    }

    @Override
    public String getSenderTaxId() { return notificationRequest.getSenderTaxId(); }

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
                    PnPollingResponseV20 pollingResponse = utils.waitForEvent(notificationResponse, pollingStrategy, NOTIFICATION_STATUS_ACCEPTED);
                    threadWait(wait);
                    assertThat(pollingResponse.getNotification())
                            .as("La fullSentNotification della notifica appena creata non dev'essere null")
                            .isNotNull();
                } else if (status.equalsIgnoreCase(NOTIFICATION_STATUS_REFUSED)) {
                    log.info("Request status for " + sharedSteps.getNotificationIun());
                    long startTime = System.currentTimeMillis();
                    PnPollingResponseV20 pollingResponse = utils.waitForEvent(notificationResponse, pollingStrategy, NOTIFICATION_STATUS_REFUSED);
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
                    String errorCode = error.toString();
                    sharedSteps.setErrorCode(errorCode);
                    threadWait(wait);
                    assertThat(errorCode).as("Il codice di errore non dev'essere vuoto").isNotEmpty();
                } else if (status.equalsIgnoreCase(NOTIFICATION_STATUS_CANCELLED)) {
                    it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.RequestStatus response = b2bClient.notificationCancellation(sharedSteps.getNotificationIun());
                    assertThat(response).as("La response della chiamata di cancellazione non dev'essere null").isNotNull();
                    assertThat(response.getDetails()).as("I details della response della chiamata di cancellazione non devono essere null").isNotNull();
                    assertThat(response.getDetails()).as("I details della response della chiamata di cancellazione non devono essere vuoti").isNotEmpty();
                    assertThat(response.getDetails().get(0).getCode()).isEqualToIgnoringCase("NOTIFICATION_CANCELLATION_ACCEPTED");
                    PnPollingResponseV20 pollingResponse = utils.waitForEvent(notificationResponse, pollingStrategy, NOTIFICATION_STATUS_REFUSED);
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

    @Override
    public Object uploadNotification(String errorType) throws IOException {
        sharedSteps.setNotificationCreationDate(OffsetDateTime.now(java.time.ZoneOffset.UTC));
        log.info(NEW_NOTIFICATION_REQUEST, notificationRequest);
        notificationResponse = utils.uploadNotification(notificationRequest);
        log.info(NEW_NOTIFICATION_RESPONSE, notificationResponse);
        String iun = new String(Base64Utils.decodeFromString(notificationResponse.getNotificationRequestId()));
        assertThat(iun).as("Lo IUN generato in fase di invio notifica non può essere null").isNotNull();
        log.info(NEW_NOTIFICATION_IUN, iun);
        sharedSteps.setNotificationIun(iun);
        return notificationResponse;
    }

    @Override
    public void setIuvToRecipient(Integer posizione, String iuvGPD) {
        throw new RuntimeException("Metodo non previsto per la versione V2");
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
        List<String> datiPagamento = sharedSteps.getDatiPagamentoVersionamento(destinatario, 0);
        NotificationPriceResponse notificationPrice =
                b2bClient.getNotificationPrice(datiPagamento.get(0), datiPagamento.get(1));
        try {
            Assertions.assertEquals(notificationPrice.getIun(), sharedSteps.getNotificationIun());
            if (price != null) {
                log.info("Costo notifica: {} destinatario: {}", notificationPrice.getAmount(), destinatario);
                Assertions.assertEquals(notificationPrice.getAmount(), Integer.parseInt(price));
            }
            if (date != null) {
                Assertions.assertNotNull(notificationPrice.getRefinementDate());
            }
        } catch (AssertionError assertionError) {
            sharedSteps.throwAssertionErrorWithIUN(assertionError);
        }
    }

    @Override
    public void uploadNotificationAllegatiUgualiPagamento() {
        throw new RuntimeException("Metodo non previsto per la V2: uploadNotificationAllegatiUgualiPagamento");
    }

    @Override
    public void addIuvGpdToDestinatario(String denominazione, String iuvGpd, Integer paymentIndex) {
        for (NotificationRecipient recipient : notificationRequest.getRecipients()) {
            if (recipient.getDenomination().equalsIgnoreCase(denominazione)) {
                Objects.requireNonNull(recipient.getPayment()).setNoticeCode(iuvGpd);
            }
        }
    }

    @Override
    public void addIuvGpdToDestinatario(Integer recIndex, String iuvGpd, Integer recipientPaymentIndex) {
        NotificationRecipient recipient = notificationRequest.getRecipients().get(recIndex);
        Objects.requireNonNull(recipient.getPayment()).setNoticeCode(iuvGpd);
    }

    @Override
    public List<String> getDatiPagamento(Integer destinatario, Integer pagamento) {
        FullSentNotificationV20 fullSentNotification = getFullSentNotificationVersioned();
        return Arrays.asList(
                Objects.requireNonNull(fullSentNotification.getRecipients().get(destinatario).getPayment()).getCreditorTaxId(),
                Objects.requireNonNull(fullSentNotification.getRecipients().get(destinatario).getPayment()).getNoticeCode());
    }

    @Override
    public void waitForTimelineElement(String timelineElementCategory, Integer attempts) {
        TimelineElementV20 timelineElement = null;
        for (int i = 0; i < attempts; i++) {
            FullSentNotificationV20 fsn = getFullSentNotificationVersioned();
            log.info("NOTIFICATION_TIMELINE: " + fsn.getTimeline());
            timelineElement = fsn.getTimeline()
                    .stream().filter(elem -> Objects.requireNonNull(elem.getCategory().getValue())
                            .equals(TimelineElementCategoryV20.valueOf(timelineElementCategory).getValue()))
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
            Assertions.assertDoesNotThrow(() -> b2bClient.getNotificationRequestStatusV2(requestId));
        } catch (AssertionFailedError assertionFailedError) {
            String message = assertionFailedError.getMessage() +
                    "{RequestID: " + (notificationResponse == null ? "NULL" : notificationResponse.getNotificationRequestId()) + " }";
            throw new AssertionFailedError(message, assertionFailedError.getExpected(), assertionFailedError.getActual(), assertionFailedError.getCause());
        }
    }

    @Override
    public void checkTaxonomyCode() {
        FullSentNotificationV20 fullSentNotification = getFullSentNotificationVersioned();
        assertThat(fullSentNotification.getTaxonomyCode())
                .as("Il taxonomyCode nella notifica inviata non dovrebbe essere nullo")
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
        return notificationRequest.getRecipients().get(recipientIndex).getPayment().getNoticeCode();
    }

    @Override
    public String getRecipientCreditorTaxId(int recipientIndex, int paymentIndex) {
        return notificationRequest.getRecipients().get(recipientIndex).getPayment().getCreditorTaxId();
    }

    @Override
    public void produceEvidence() {
        assertThat(notificationResponse)
                .as("La risposta della nuova notifica non dovrebbe essere nulla")
                .isNotNull();
        log.info("METADATI: " + '\n' + notificationResponse);
        log.info("REQUEST-ID: " + '\n' + notificationResponse.getNotificationRequestId());
    }

    @Override
    public void verifyCorrectAcquisition() {
        assertSoftly(softly -> {
            softly.assertThat(notificationResponse)
                    .as("La risposta della nuova notifica non dovrebbe essere nulla")
                    .isNotNull();

            softly.assertThat(notificationResponse)
                    .as("L'ID della richiesta di notifica non dovrebbe essere nullo")
                    .isNotNull();

            softly.assertThat(b2bClient.getNotificationRequestStatusV2(notificationResponse.getNotificationRequestId()))
                    .as("Lo stato della richiesta di notifica non dovrebbe essere nullo.",
                            notificationResponse.getNotificationRequestId())
                    .isNotNull();
        });
    }

    @Override
    public void verifyStatus(boolean withNotificationRequestId, boolean withPaProtocolNumber, boolean withIdempotenceToken) {
        String notificationRequestId = withNotificationRequestId ? notificationResponse.getNotificationRequestId() : null;
        String paProtocolNumber = withPaProtocolNumber ? notificationResponse.getPaProtocolNumber() : null;
        String idempotenceToken = withIdempotenceToken ? notificationResponse.getIdempotenceToken() : null;

        NewNotificationRequestStatusResponse newNotificationRequestStatusResponse = Assertions.assertDoesNotThrow(() ->
                b2bClient.getNotificationRequestStatusAllParamV2(notificationRequestId, paProtocolNumber, idempotenceToken));
        assertThat(newNotificationRequestStatusResponse.getNotificationRequestStatus())
                .as("Lo stato della richiesta di notifica non dovrebbe essere nullo")
                .isNotNull();
        log.debug(newNotificationRequestStatusResponse.getNotificationRequestStatus());
    }

    @Override
    public void verifyNotification(String notificationIun) {
        utils.verifyNotification(notificationIun);
    }

    @Override
    public void createAndSendNotificationRequestWithError(String errorType) {
        //metodo non previsto per la versione 2
    }

    @Override
    public String getCreditorTaxId(int recipientIndex) {
        return notificationRequest.getRecipients().get(recipientIndex).getPayment().getCreditorTaxId();
    }

    @Override
    public String getNoticeCode(int recipientIndex) {
        return notificationRequest.getRecipients().get(recipientIndex).getPayment().getNoticeCode();
    }
}
