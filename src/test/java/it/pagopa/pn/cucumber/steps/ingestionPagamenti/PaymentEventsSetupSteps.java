package it.pagopa.pn.cucumber.steps.ingestionPagamenti;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import it.pagopa.common.polling.Poller;
import it.pagopa.common.polling.PollingConfig;
import it.pagopa.pn.client.b2b.pa.domain.Destinatario;
import it.pagopa.pn.client.b2b.pa.domain.DynamoTableName;
import it.pagopa.pn.client.b2b.pa.provider.SenderInfoProvider;
import it.pagopa.pn.client.b2b.pa.service.DynamoDbService;
import it.pagopa.pn.cucumber.steps.SendSharedContext;
import it.pagopa.pn.cucumber.steps.SharedSteps;
import it.pagopa.pn.cucumber.steps.common.InformalNotificationContext;
import it.pagopa.pn.cucumber.steps.informalNotification.InformalTemplateMessage.InformalMessageTemplates;
import it.pagopa.pn.cucumber.steps.informalNotification.provider.InformalMessageProvider;
import it.pagopa.pn.cucumber.steps.ingestionPagamenti.config.PaymentEventsSettings;
import it.pagopa.pn.cucumber.steps.ingestionPagamenti.context.PaymentEventsScenarioContext;
import it.pagopa.pn.cucumber.steps.ingestionPagamenti.dto.PaymentNotice;
import it.pagopa.pn.cucumber.steps.pa.AvanzamentoNotificheB2bSteps;
import it.pagopa.pn.cucumber.steps.pa.PresaInCaricoNoticaBonariaSteps;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.web.client.RestClientResponseException;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.QueryResponse;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Preparazione delle notifiche legali, delle comunicazioni bonarie e delle campagne su cui vengono simulati i pagamenti.
 */
@Slf4j
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@RequiredArgsConstructor
public class PaymentEventsSetupSteps {

    // SEND correla il pagamento solo per creditorTaxId e noticeCode: l'importo non viene confrontato con l'avviso
    private static final int LEGAL_PAYMENT_AMOUNT = 10000;
    private static final int INFORMAL_PAYMENT_AMOUNT = 5000;

    private static final String INFORMAL_SUBJECT = "Comunicazione bonaria con avviso pagoPA";
    // Il mock di App IO gestisce i messaggi il cui oggetto contiene @io:<sequenza>
    private static final String IO_MOCK_SEQUENCE_PREFIX = " @io:";

    private static final String PAYMENT_NOTICE_PAID = "PAYMENT_NOTICE_PAID";
    // NOTIFICATION_PAID nell'API, PAYMENT su pn-Timelines
    private static final String SENDER_PAYMENT_CATEGORY = "PAYMENT";
    private static final String STOP_ON_PAID = "stopOnPaid";
    private static final String CAMPAIGN_WORKFLOW = "workflow";
    private static final String CAMPAIGN_CHANNEL_TIMEOUT = "timeout";
    private static final String CAMPAIGN_CHANNEL_RECIPIENT_TYPES = "recipientType";
    private static final String INFORMAL_RECIPIENT_TYPE = "PF";
    private static final String COMMUNICATION_TYPE = "communicationType";
    private static final String NOTIFICATION_STATUS_CANCELLED = "CANCELLED";
    private static final String LEGAL_WORKFLOW_COMPLETED_CATEGORY = "REFINEMENT";
    private static final String INFORMAL_STATUS_COMPLETED = "COMPLETED_REACHED";
    private static final Duration POLL_INTERVAL = Duration.ofSeconds(5);
    private static final Duration LEGAL_WORKFLOW_POLL_INTERVAL = Duration.ofSeconds(15);

    private final PaymentEventsScenarioContext context;
    private final PaymentEventsSettings settings;
    private final SharedSteps sharedSteps;
    private final SendSharedContext sendSharedContext;
    private final PresaInCaricoNoticaBonariaSteps informalNotificationSteps;
    private final AvanzamentoNotificheB2bSteps legalNotificationSteps;
    private final DynamoDbService dynamoDbService;
    private final InformalMessageProvider informalMessageProvider;

    // =========================================================================
    // NOTIFICHE LEGALI
    // =========================================================================

    @Given("il {string} ha notificato a {destinatario} una notifica legale con avviso pagoPA")
    public void legalNotificationWithPagoPaNotice(String paName, Destinatario destinatario) {
        legalNotificationWithPagoPaNotice(paName, destinatario, Map.of());
    }

    /**
     * Le bonarie generano codici avviso che terminano con l'indice del pagamento (00): così l'avviso è riusabile in una bonaria.
     */
    @Given("il {string} ha notificato a {destinatario} una notifica legale con avviso pagoPA il cui codice termina con 00")
    public void legalNotificationWithPagoPaNoticeEndingWith00(String paName, Destinatario destinatario) {
        legalNotificationWithPagoPaNotice(paName, destinatario, Map.of("payment_noticeCode", PaymentNotice.randomNoticeCodeEndingWith("00")));
    }

    private void legalNotificationWithPagoPaNotice(String paName, Destinatario destinatario, Map<String, String> additionalPaymentData) {
        sharedSteps.prepareNotificationRequest(new HashMap<>(Map.of(
                "subject", "Notifica legale con avviso pagoPA",
                "senderDenomination", "Comune di Milano",
                "physicalCommunication", "REGISTERED_LETTER_890",
                "feePolicy", "FLAT_RATE")));
        Map<String, String> paymentData = new HashMap<>(Map.of(
                "payment_pagoPaForm", "SI",
                "payment_f24", "NULL"));
        paymentData.putAll(additionalPaymentData);
        sharedSteps.addDestinatarioWithParams(destinatario, paymentData);
        sharedSteps.sendNotification(paName, "ACCEPTED");

        context.setNotice(new PaymentNotice(
                sharedSteps.getRecipientCreditorTaxId(0, 0),
                sharedSteps.getRecipientNoticeCode(0, 0),
                LEGAL_PAYMENT_AMOUNT));
        log.info("Notifica legale {} con avviso {}", sharedSteps.getNotificationIun(), context.getNotice());
    }

    /**
     * Si aspetta ACCEPTED, così un rifiuto emerge come errore dello step con il motivo restituito da SEND.
     */
    @Given("il {string} notifica a {destinatario} una seconda notifica legale con lo stesso avviso pagoPA")
    public void secondLegalNotificationWithSameNotice(String paName, Destinatario destinatario) {
        PaymentNotice notice = context.getNotice();
        assertThat(notice).as("Nessun avviso pagoPA preparato nello scenario").isNotNull();
        String firstIun = sharedSteps.getNotificationIun();
        context.setPreviousNotificationIun(firstIun);
        sharedSteps.prepareNotificationRequest(new HashMap<>(Map.of(
                "subject", "Seconda notifica legale con lo stesso avviso pagoPA",
                "senderDenomination", "Comune di Milano",
                "physicalCommunication", "REGISTERED_LETTER_890",
                "feePolicy", "FLAT_RATE")));
        sharedSteps.addDestinatarioWithParams(destinatario, new HashMap<>(Map.of(
                "payment_pagoPaForm", "SI",
                "payment_f24", "NULL",
                "payment_creditorTaxId", notice.creditorTaxId(),
                "payment_noticeCode", notice.noticeCode())));
        sharedSteps.sendNotification(paName, "ACCEPTED");
        log.info("IUV SEND {}: prima notifica {}, seconda notifica legale {}", notice, firstIun, sharedSteps.getNotificationIun());
    }

    @Given("il {string} invia una seconda comunicazione bonaria con lo stesso avviso pagoPA nella campagna {string}")
    public void secondInformalNotificationWithSameNotice(String paName, String campaignName) throws IOException {
        PaymentNotice notice = context.getNotice();
        assertThat(notice).as("Nessun avviso pagoPA preparato nello scenario").isNotNull();
        String firstIun = sharedSteps.getNotificationIun();
        sendInformalNotificationWithPagoPaNotice(paName, campaignName, Map.of(
                "payment_creditorTaxId", notice.creditorTaxId(),
                "payment_noticeCode", notice.noticeCode()));
        log.info("IUV SEND {}: prima notifica {}, seconda comunicazione bonaria {} con avviso {}", notice, firstIun, sharedSteps.getNotificationIun(), context.getNotice());
    }

    /**
     * Registra lo stato HTTP del rifiuto per la verifica; un errore diverso da un rifiuto HTTP fa fallire lo step.
     */
    @When("il {string} tenta di notificare a {destinatario} una seconda notifica legale con lo stesso avviso pagoPA")
    public void attemptSecondLegalNotificationWithSameNotice(String paName, Destinatario destinatario) {
        recordRejectionOf(() -> secondLegalNotificationWithSameNotice(paName, destinatario));
    }

    @When("il {string} tenta di inviare una seconda comunicazione bonaria con lo stesso avviso pagoPA nella campagna {string}")
    public void attemptSecondInformalNotificationWithSameNotice(String paName, String campaignName) {
        recordRejectionOf(() -> {
            try {
                secondInformalNotificationWithSameNotice(paName, campaignName);
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        });
    }

    private void recordRejectionOf(Runnable attempt) {
        context.setSecondNotificationRejectionStatus(null);
        try {
            attempt.run();
        } catch (RuntimeException | AssertionError e) {
            for (Throwable cause = e; cause != null; cause = cause.getCause()) {
                if (cause instanceof RestClientResponseException httpError) {
                    context.setSecondNotificationRejectionStatus(httpError.getRawStatusCode());
                    log.info("Seconda notifica con lo stesso avviso rifiutata da SEND con stato {}", httpError.getRawStatusCode());
                    return;
                }
            }
            throw e;
        }
    }

    /**
     * Dopo il perfezionamento lo stato resta stabile, quindi un cambio di stato è attribuibile solo al pagamento.
     * Lo stato raggiunto dipende dalle tempistiche dell'ambiente e viene registrato per il confronto successivo.
     */
    @Given("la notifica legale ha concluso il proprio workflow")
    public void legalWorkflowCompleted() {
        String iun = sharedSteps.getNotificationIun();
        Consumer<List<Map<String, AttributeValue>>> verify = items -> assertThat(items)
                .as("La notifica legale %s non ha ancora l'elemento %s", iun, LEGAL_WORKFLOW_COMPLETED_CATEGORY)
                .isNotEmpty();
        Poller.pollUntil(() -> timelineItems(iun, LEGAL_WORKFLOW_COMPLETED_CATEGORY), verify,
                PollingConfig.of(settings.getLegalWorkflowTimeout(), LEGAL_WORKFLOW_POLL_INTERVAL));
        context.setLegalStatusBeforePayment(sharedSteps.getSentNotificationLastVersion().getNotificationStatus().getValue());
        log.info("Notifica legale {} perfezionata nello stato {}", iun, context.getLegalStatusBeforePayment());
    }

    @Given("il {string} ha annullato la notifica legale")
    public void cancelLegalNotification(String paName) {
        sharedSteps.notificationCanBeCanceledWithIunByComune("può", paName);
        legalNotificationSteps.readingEventUpToTheStatusOfNotification(NOTIFICATION_STATUS_CANCELLED);
        awaitNoticeRemovedFromPaymentInfo();
    }

    /**
     * La rimozione è asincrona: un pagamento pubblicato prima verrebbe ancora correlato alla notifica annullata.
     */
    private void awaitNoticeRemovedFromPaymentInfo() {
        String pk = context.getNotice().paymentInfoPk();
        Consumer<List<Map<String, AttributeValue>>> verify = items -> assertThat(items)
                .as("L'avviso %s è ancora censito su pn-PaymentInfo dopo l'annullamento della notifica", pk)
                .isEmpty();
        Poller.pollUntil(() -> dynamoDbService.call(DynamoTableName.PAYMENT_INFO, Map.of(
                        ":v_pk", AttributeValue.builder().s(pk).build())).items(), verify,
                PollingConfig.of(settings.getPaymentInfoRemovalTimeout(), POLL_INTERVAL));
    }

    /**
     * Su una notifica già pagata il nuovo pagamento verrebbe scartato come duplicato: si sceglie un record storico
     * la cui notifica non risulta pagata.
     */
    @Given("una notifica legale il cui avviso è stato censito prima dell'introduzione del tipo comunicazione")
    public void legacyLegalNotification() {
        Map<String, AttributeValue> paymentInfo = dynamoDbService
                .findFirstItemWithoutAttribute(DynamoTableName.PAYMENT_INFO, COMMUNICATION_TYPE, item -> isNotPaid(item.get("iun").s()))
                .orElseThrow(() -> new AssertionError("Nessun record di pn-PaymentInfo privo di " + COMMUNICATION_TYPE + " relativo a una notifica non pagata"));

        context.setNotice(PaymentNotice.fromPaymentInfoPk(paymentInfo.get("pk").s(), LEGAL_PAYMENT_AMOUNT));
        sharedSteps.setNotificationIun(paymentInfo.get("iun").s());
        log.info("Notifica legale storica {} con avviso {}", sharedSteps.getNotificationIun(), context.getNotice());
    }

    private boolean isNotPaid(String iun) {
        return Stream.of(PAYMENT_NOTICE_PAID, SENDER_PAYMENT_CATEGORY).allMatch(category -> timelineItems(iun, category).isEmpty());
    }

    private List<Map<String, AttributeValue>> timelineItems(String iun, String category) {
        return dynamoDbService.call(DynamoTableName.TIMELINE, Map.of(
                ":v_iun", AttributeValue.builder().s(iun).build(),
                ":v_category", AttributeValue.builder().s(category).build())).items();
    }

    // =========================================================================
    // COMUNICAZIONI BONARIE
    // =========================================================================

    @Given("il destinatario paga gli avvisi anche tramite App IO")
    public void recipientPaysAlsoOnAppIo() {
        context.setPaidAlsoOnAppIo(true);
    }

    @Given("il destinatario è raggiungibile su App IO ma non paga gli avvisi tramite App IO")
    public void recipientReachableOnAppIoWithoutPayment() {
        context.setReachableOnAppIoWithoutPayment(true);
    }

    @Given("il {string} ha inviato una comunicazione bonaria con avviso pagoPA nella campagna {string}")
    public void informalNotificationWithPagoPaNotice(String paName, String campaignName) throws IOException {
        sendInformalNotificationWithPagoPaNotice(paName, campaignName, Map.of());
    }

    @Given("il {string} ha inviato una comunicazione bonaria con avviso pagoPA e recapiti EMAIL e PEC nella campagna {string}")
    public void informalNotificationWithPagoPaNoticeAndPec(String paName, String campaignName) throws IOException {
        sendInformalNotificationWithPagoPaNotice(paName, campaignName, Map.of("digitalDomicile", settings.getInformalRecipientPec()));
    }

    /**
     * La durata del primo canale della campagna determina quando il workflow passerebbe al canale successivo.
     */
    private void sendInformalNotificationWithPagoPaNotice(String paName, String campaignName, Map<String, String> additionalData) throws IOException {
        String campaignId = settings.campaignId(campaignName);
        Map<String, String> data = new HashMap<>(additionalData);
        data.put("campaignId", campaignId);
        data.put("messageId", messageId(paName));
        data.put("subject", INFORMAL_SUBJECT);
        data.put("recipientType", INFORMAL_RECIPIENT_TYPE);
        data.put("taxId", settings.getInformalRecipientTaxId());
        data.put("denomination", settings.getInformalRecipientDenomination());
        data.put("email", settings.getInformalRecipientEmail());
        data.put("payment_multy_number", "1");
        data.put("payment_amount", String.valueOf(INFORMAL_PAYMENT_AMOUNT));

        context.setFirstChannelTimeout(firstChannelTimeout(campaignItem(campaignId, paName)));
        informalNotificationSteps.createInformalNotification(paName, data);
        informalNotificationSteps.sendInformalAndWaitStatus("ACCEPTED");

        InformalNotificationContext.InformalRecipientContext recipient = sendSharedContext.getInformalNotificationContext().getRecipient();
        context.setNotice(new PaymentNotice(recipient.getPaymentCreditorTaxId(), recipient.getPaymentNoticeCode(), INFORMAL_PAYMENT_AMOUNT));
        log.info("Comunicazione bonaria {} nella campagna {} con avviso {}", sharedSteps.getNotificationIun(), campaignId, context.getNotice());
    }

    /**
     * Su App IO l'oggetto è quello del messaggio, non della comunicazione: la sequenza del mock va nel messaggio.
     */
    private String messageId(String paName) {
        if (context.isPaidAlsoOnAppIo()) {
            return messageOnAppIo(paName, settings.getIoMockPaymentSequence());
        }
        if (context.isReachableOnAppIoWithoutPayment()) {
            return messageOnAppIo(paName, settings.getIoMockNoPaymentSequence());
        }
        return "${NEW-IT}";
    }

    private String messageOnAppIo(String paName, String ioMockSequence) {
        SenderInfoProvider.PaInfo paInfo = sharedSteps.getSenderInfoProvider().getPaInfo(paName);
        String subject = InformalMessageTemplates.RISCUOTI_IT.getContent().getSubject() + IO_MOCK_SEQUENCE_PREFIX + ioMockSequence;
        return informalMessageProvider.createMessageITWithSubject(subject, paInfo.getSenderId(), paInfo.getApiKeyType());
    }

    /**
     * Durata del primo canale della campagna per le persone fisiche; null se il canale non ne ha una (es. ANALOG).
     */
    private static Duration firstChannelTimeout(Map<String, AttributeValue> campaign) {
        AttributeValue workflow = campaign.get(CAMPAIGN_WORKFLOW);
        if (workflow == null || !workflow.hasL()) {
            return null;
        }
        return workflow.l().stream()
                .filter(AttributeValue::hasM)
                .map(AttributeValue::m)
                .filter(channel -> !channel.containsKey(CAMPAIGN_CHANNEL_RECIPIENT_TYPES)
                        || channel.get(CAMPAIGN_CHANNEL_RECIPIENT_TYPES).l().stream().anyMatch(type -> INFORMAL_RECIPIENT_TYPE.equals(type.s())))
                .findFirst()
                .map(channel -> channel.get(CAMPAIGN_CHANNEL_TIMEOUT))
                .map(timeout -> Duration.parse(timeout.s()))
                .orElse(null);
    }

    @Given("la comunicazione bonaria ha concluso il proprio workflow")
    public void informalWorkflowCompleted() {
        informalNotificationSteps.verifyFinalNotificationStatus(INFORMAL_STATUS_COMPLETED);
    }

    // =========================================================================
    // CAMPAGNE (in sola lettura: gli step verificano che la configurazione sia quella attesa dallo scenario)
    // =========================================================================

    @Given("la campagna {string} del {string} conclude il workflow al pagamento")
    public void campaignStopsOnPaid(String campaignName, String paName) {
        String campaignId = settings.campaignId(campaignName);
        assertThat(campaignItem(campaignId, paName).get(STOP_ON_PAID))
                .as("La campagna %s deve avere %s valorizzato a true", campaignId, STOP_ON_PAID)
                .isNotNull()
                .extracting(AttributeValue::bool)
                .isEqualTo(true);
    }

    @Given("la campagna {string} del {string} non conclude il workflow al pagamento")
    public void campaignDoesNotStopOnPaid(String campaignName, String paName) {
        String campaignId = settings.campaignId(campaignName);
        assertThat(campaignItem(campaignId, paName).get(STOP_ON_PAID))
                .as("La campagna %s deve avere %s valorizzato a false", campaignId, STOP_ON_PAID)
                .isNotNull()
                .extracting(AttributeValue::bool)
                .isEqualTo(false);
    }

    @Given("la campagna {string} del {string} non specifica se concludere il workflow al pagamento")
    public void campaignWithoutStopOnPaid(String campaignName, String paName) {
        String campaignId = settings.campaignId(campaignName);
        assertThat(campaignItem(campaignId, paName).get(STOP_ON_PAID))
                .as("La campagna %s non deve avere l'attributo %s", campaignId, STOP_ON_PAID)
                .isNull();
    }

    private Map<String, AttributeValue> campaignItem(String campaignId, String paName) {
        String senderId = sharedSteps.getSenderInfoProvider().getPaInfo(paName).getSenderId();
        QueryResponse response = dynamoDbService.call(DynamoTableName.CAMPAIGNS, Map.of(
                ":v_senderId", AttributeValue.builder().s(senderId).build(),
                ":v_campaignId", AttributeValue.builder().s(campaignId).build()));
        assertThat(response.items())
                .as("Campagna %s del mittente %s non trovata su %s", campaignId, paName, DynamoTableName.CAMPAIGNS.getValue())
                .hasSize(1);
        return response.items().get(0);
    }
}
