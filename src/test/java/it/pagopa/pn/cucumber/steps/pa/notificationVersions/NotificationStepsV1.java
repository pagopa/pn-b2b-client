package it.pagopa.pn.cucumber.steps.pa.notificationVersions;

import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.RequestStatus;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.*;
import it.pagopa.pn.client.b2b.pa.polling.IPnPollingService;
import it.pagopa.pn.client.b2b.pa.polling.design.PnPollingStrategy;
import it.pagopa.pn.client.b2b.pa.polling.dto.PnPollingParameter;
import it.pagopa.pn.client.b2b.pa.polling.dto.PnPollingResponseV1;
import it.pagopa.pn.cucumber.steps.SharedSteps;
import it.pagopa.pn.cucumber.steps.pa.utilityVersions.NotificationUtilsV1;
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
import java.util.concurrent.atomic.AtomicReference;

import static it.pagopa.pn.client.b2b.pa.PnPaB2bUtils.*;
import static it.pagopa.pn.cucumber.steps.SharedSteps.threadWait;
import static it.pagopa.pn.cucumber.steps.utilitySteps.Costanti.*;
import static it.pagopa.pn.cucumber.steps.utilitySteps.Destinatario.DESTINATARIO_NESSUNO;
import static it.pagopa.pn.cucumber.steps.utilitySteps.Destinatario.DESTINATARIO_SIGNOR_CASUALE;
import static it.pagopa.pn.cucumber.utils.NotificationValue.DOCUMENT;
import static it.pagopa.pn.cucumber.utils.NotificationValue.getDefaultValue;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

@Data
@Slf4j
public class NotificationStepsV1 implements NotificationStepsInterface {

    private NewNotificationRequest notificationRequest;
    private NewNotificationResponse notificationResponse;
    private final SharedSteps sharedSteps;
    private final NotificationVersion version;
    private final NotificationUtilsV1 utils;

    public NotificationStepsV1(SharedSteps sharedSteps) {
        this.sharedSteps = sharedSteps;
        version = NotificationVersion.V1;
        utils = new NotificationUtilsV1(this);
    }

    @Override
    public String getVersionString() {
        return version.toString();
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
        if (destinatario != null && destinatario.equals(DESTINATARIO_NESSUNO)) return;
        NotificationRecipient notificationRecipient = utils.convertNotificationRecipient(data);
        if (destinatario != null) {
            notificationRecipient.setDenomination(destinatario.getDenomination());
            notificationRecipient.setTaxId(destinatario.equals(DESTINATARIO_SIGNOR_CASUALE) ?
                    FiscalCodeGenerator.generateCF(System.nanoTime()) : destinatario.getTaxId());
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
    public String getNotificationRequestGroup() {
        return notificationRequest.getGroup();
    }

    @Override
    public void setNotificationRequestGroup(String group) {
        notificationRequest.setGroup(group);
    }

    @Override
    public String sendNotification(int wait, String status, String pollingStrategy) {
        AtomicReference<String> newNotificationIun = new AtomicReference<>(null);
        try {
            Assertions.assertDoesNotThrow(() -> {
                notificationResponse = (NewNotificationResponse) uploadNotification();
                if (status.equalsIgnoreCase(NOTIFICATION_STATUS_ACCEPTED)) {
                    threadWait(wait);
                    FullSentNotification fullSentNotification = waitForRequestAccepted(notificationResponse, pollingStrategy);
                    threadWait(wait);
                    Assertions.assertNotNull(fullSentNotification);
                    newNotificationIun.set(fullSentNotification.getIun());
                } else if (status.equalsIgnoreCase(NOTIFICATION_STATUS_REFUSED)) {
                    String errorCode = waitForRequestRefused(notificationResponse, pollingStrategy);
                    sharedSteps.setErrorCode(errorCode);
                    threadWait(wait);
                    Assertions.assertFalse(errorCode.isEmpty());
                    newNotificationIun.set(new String(Base64Utils.decodeFromString(notificationResponse.getNotificationRequestId())));
                } else if (status.equalsIgnoreCase(NOTIFICATION_STATUS_CANCELLED)) {
                    newNotificationIun.set(new String(Base64Utils.decodeFromString(notificationResponse.getNotificationRequestId())));
                    RequestStatus response = sharedSteps.getB2bClient().notificationCancellation(newNotificationIun.get());
                    Assertions.assertNotNull(response);
                    Assertions.assertNotNull(response.getDetails());
                    Assertions.assertFalse(response.getDetails().isEmpty());
                    Assertions.assertTrue("NOTIFICATION_CANCELLATION_ACCEPTED".equalsIgnoreCase(response.getDetails().get(0).getCode()));
                    boolean refused = waitForRequestNotRefused(notificationResponse, pollingStrategy);
                    threadWait(wait);
                    Assertions.assertFalse(refused);
                }
            });
            assertThat(newNotificationIun.get()).as("Lo IUN generato in fase di invio notifica non può essere nullo").isNotNull();
            sharedSteps.setNotificationIun(newNotificationIun.get());
            return newNotificationIun.get();
        } catch (AssertionFailedError assertionFailedError) {
            String message = assertionFailedError.getMessage() +
                    "{RequestID: " + (notificationResponse == null ? "NULL" : notificationResponse.getNotificationRequestId()) + " }";
            throw new AssertionFailedError(message, assertionFailedError.getExpected(), assertionFailedError.getActual(), assertionFailedError.getCause());
        }
    }

    @Override
    public Object uploadNotification() throws IOException {
        sharedSteps.setNotificationCreationDate(OffsetDateTime.now());
        List<NotificationDocument> documents = new ArrayList<>();
        for (NotificationDocument doc : notificationRequest.getDocuments()) {
            documents.add(utils.preloadDocument(doc));
        }
        notificationRequest.setDocuments(documents);
        for (NotificationRecipient recipient : notificationRequest.getRecipients()) {
            NotificationPaymentInfo paymentInfo = recipient.getPayment();
            if (paymentInfo != null) {
                paymentInfo.setPagoPaForm(utils.preloadAttachment(paymentInfo.getPagoPaForm()));
            }
        }
        log.info(NEW_NOTIFICATION_REQUEST, notificationRequest);
        NewNotificationResponse response = sharedSteps.getB2bClient().sendNewNotificationV1(notificationRequest);
        log.info(NEW_NOTIFICATION_REQUEST_RESPONSE, response);
        notificationResponse = response;
        return response;
    }

    @Override
    public void setIuvToRecipient(Integer posizione, String iuvGPD) {
        throw new RuntimeException("Metodo non previsto per la versione V1");
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
        it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v21.NotificationPriceResponse notificationPrice =
                sharedSteps.getB2bClient().getNotificationPrice(datiPagamento.get(0), datiPagamento.get(1));
        try {
            Assertions.assertEquals(notificationPrice.getIun(), sharedSteps.getNotificationIun());
            if (price != null) {
                log.info("Costo notifica: {} destinatario: {}", notificationPrice.getAmount(), destinatario);
                Assertions.assertEquals(Integer.parseInt(price), notificationPrice.getAmount());
            }
            if (date != null) {
                Assertions.assertNotNull(notificationPrice.getRefinementDate());
            }
        } catch (AssertionFailedError assertionFailedError) {
            sharedSteps.throwAssertionErrorWithIUN(assertionFailedError);
        }
    }

    @Override
    public void uploadNotificationAllegatiUgualiPagamento() {
        throwUnsupportedMethodException("uploadNotificationAllegatiUgualiPagamento");
    }

    private FullSentNotification waitForRequestAccepted(NewNotificationResponse response, String pollingStrategy) {
        IPnPollingService pollingService = sharedSteps.getB2bUtils().getPollingFactory().getPollingService(getPollingStrategy(pollingStrategy));
        PnPollingResponseV1 pollingResponse = (PnPollingResponseV1) pollingService.waitForEvent(response.getNotificationRequestId(), PnPollingParameter.builder().value(ACCEPTED).build());
        return pollingResponse.getNotification() == null ? null : pollingResponse.getNotification();
    }

    private String waitForRequestRefused(NewNotificationResponse response, String pollingStrategy) {
        log.info("Request status for " + response.getNotificationRequestId());
        long startTime = System.currentTimeMillis();

        IPnPollingService pollingService = sharedSteps.getB2bUtils().getPollingFactory().getPollingService(getPollingStrategy(pollingStrategy));
        PnPollingResponseV1 pollingResponse = (PnPollingResponseV1) pollingService.waitForEvent(response.getNotificationRequestId(), PnPollingParameter.builder().value(REFUSED).build());

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
        PnPollingResponseV1 pollingResponse = (PnPollingResponseV1) pollingService.waitForEvent(response.getNotificationRequestId(), PnPollingParameter.builder().value(REFUSED).build());
        return pollingResponse.getResult();
    }

    public static String getPollingStrategy(String pollingStrategy) {
        return switch (pollingStrategy) {
            case TIMELINE_RAPID -> PnPollingStrategy.TIMELINE_RAPID_V1;
            case STATUS_RAPID -> PnPollingStrategy.STATUS_RAPID_V1;
            case TIMELINE_SLOW -> PnPollingStrategy.TIMELINE_SLOW_V1;
            case STATUS_SLOW -> PnPollingStrategy.STATUS_SLOW_V1;
            case VALIDATION_STATUS -> PnPollingStrategy.VALIDATION_STATUS_V1;
            default ->
                    throw new RuntimeException("PnPollingStrategy non riconosciuta per la versione 1: " + pollingStrategy);
        };
    }

    @Override
    public void addIuvGdpToDestinatario(String denominazione, String iuvGdp, Integer paymentIndex) {
        for (NotificationRecipient recipient : this.notificationRequest.getRecipients()) {
            if (recipient.getDenomination().equalsIgnoreCase(denominazione)) {
                Objects.requireNonNull(Objects.requireNonNull(recipient.getPayment())).setNoticeCode(iuvGdp);
            }
        }
    }

    @Override
    public List<String> getDatiPagamento(String iun, Integer destinatario, Integer pagamento) {
        FullSentNotification fullSentNotification = sharedSteps.getB2bClient().getSentNotificationV1(iun);
        return Arrays.asList(
                Objects.requireNonNull(fullSentNotification.getRecipients().get(destinatario).getPayment()).getCreditorTaxId(),
                Objects.requireNonNull(fullSentNotification.getRecipients().get(destinatario).getPayment()).getNoticeCode());
    }

    @Override
    public void waitForTimelineElement(String iun, String timelineElementCategory, Integer attempts) {
        TimelineElement timelineElement = null;
        for (int i = 0; i < attempts; i++) {
            threadWait(sharedSteps.getWorkFlowWait());
            FullSentNotification fsn = sharedSteps.getB2bClient().getSentNotificationV1(iun);
            log.info("NOTIFICATION_TIMELINE: " + fsn.getTimeline());
            timelineElement = fsn.getTimeline()
                    .stream().filter(elem -> Objects.requireNonNull(elem.getCategory().getValue())
                            .equals(TimelineElementCategory.valueOf(timelineElementCategory).getValue()))
                    .findAny().orElse(null);
            if (timelineElement != null) {
                break;
            }
        }
        assertThat(timelineElement).as("Il timeline element restituito non dev'essere null").isNotNull();
    }

    @Override
    public void getNotificationRequestStatus(String requestId) {
        try {
            Assertions.assertDoesNotThrow(() -> sharedSteps.getB2bClient().getNotificationRequestStatusV1(requestId));
        } catch (AssertionFailedError assertionFailedError) {
            String message = assertionFailedError.getMessage() +
                    "{RequestID: " + (notificationResponse == null ? "NULL" : notificationResponse.getNotificationRequestId()) + " }";
            throw new AssertionFailedError(message, assertionFailedError.getExpected(), assertionFailedError.getActual(), assertionFailedError.getCause());
        }
    }

    @Override
    public void checkTaxonomyCode() {
        String iun = sharedSteps.getNotificationIun();
        FullSentNotification fullSentNotification = sharedSteps.getB2bClient().getSentNotificationV1(iun);
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

            softly.assertThat(sharedSteps.getB2bClient().getNotificationRequestStatusV1(notificationResponse.getNotificationRequestId()))
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
                sharedSteps.getB2bClient().getNotificationRequestStatusAllParamV1(notificationRequestId, paProtocolNumber, idempotenceToken));
        assertThat(newNotificationRequestStatusResponse.getNotificationRequestStatus())
                .as("Lo stato della richiesta di notifica non dovrebbe essere nullo")
                .isNotNull();
        log.debug(newNotificationRequestStatusResponse.getNotificationRequestStatus());
    }
}
