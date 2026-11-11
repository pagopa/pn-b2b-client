package it.pagopa.pn.cucumber.steps.ingestionPagamenti;

import io.cucumber.java.en.Given;
import it.pagopa.pn.client.b2b.pa.domain.Destinatario;
import it.pagopa.pn.client.b2b.pa.domain.DynamoTableName;
import it.pagopa.pn.client.b2b.pa.service.DynamoDbService;
import it.pagopa.pn.cucumber.steps.SendSharedContext;
import it.pagopa.pn.cucumber.steps.SharedSteps;
import it.pagopa.pn.cucumber.steps.common.InformalNotificationContext;
import it.pagopa.pn.cucumber.steps.informalNotification.InformalTemplateMessage.InformalMessageTemplates;
import it.pagopa.pn.cucumber.steps.informalNotification.provider.InformalMessageProvider;
import it.pagopa.pn.cucumber.steps.ingestionPagamenti.config.PaymentEventsSettings;
import it.pagopa.pn.cucumber.steps.ingestionPagamenti.context.IngestionPagamentiScenarioContext;
import it.pagopa.pn.cucumber.steps.ingestionPagamenti.dto.PaymentNotice;
import it.pagopa.pn.cucumber.steps.pa.AvanzamentoNotificheB2bSteps;
import it.pagopa.pn.cucumber.steps.pa.PresaInCaricoNoticaBonariaSteps;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.QueryResponse;

import java.io.IOException;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

/**
 * Preparazione delle notifiche legali, delle comunicazioni bonarie e delle campagne su cui vengono simulati i pagamenti.
 */
@Slf4j
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@RequiredArgsConstructor
public class IngestionPagamentiSetupSteps {

    /**
     * L'importo dell'evento pagoPA non viene confrontato con l'avviso: SEND correla il pagamento solo per creditorTaxId e noticeCode.
     */
    private static final int LEGAL_PAYMENT_AMOUNT = 10000;
    private static final int INFORMAL_PAYMENT_AMOUNT = 5000;

    private static final String INFORMAL_SUBJECT = "Comunicazione bonaria con avviso pagoPA";
    // Il router di pn-io-connector-mock instrada sul mock i messaggi IO il cui oggetto contiene @io:<sequenza>.
    // Con OK_READ_THEN_PAID (default) il mock risponde READ a 60-90 s e PAID dopo 90 s al polling di pn-io-connector
    private static final String IO_MOCK_SEQUENCE_PREFIX = " @io:";

    private static final String PAYMENT_NOTICE_PAID = "PAYMENT_NOTICE_PAID";
    // Su pn-Timelines il pagamento comunicato dall'ente (NOTIFICATION_PAID nell'API) ha categoria PAYMENT
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

    private final IngestionPagamentiScenarioContext context;
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
        sharedSteps.prepareNotificationRequest(new HashMap<>(Map.of(
                "subject", "Notifica legale con avviso pagoPA",
                "senderDenomination", "Comune di Milano",
                "physicalCommunication", "REGISTERED_LETTER_890",
                "feePolicy", "FLAT_RATE")));
        sharedSteps.addDestinatarioWithParams(destinatario, new HashMap<>(Map.of(
                "payment_pagoPaForm", "SI",
                "payment_f24", "NULL")));
        sharedSteps.sendNotification(paName, "ACCEPTED");

        context.setNotice(new PaymentNotice(
                sharedSteps.getRecipientCreditorTaxId(0, 0),
                sharedSteps.getRecipientNoticeCode(0, 0),
                LEGAL_PAYMENT_AMOUNT));
        log.info("Notifica legale {} con avviso {}", sharedSteps.getNotificationIun(), context.getNotice());
    }

    /**
     * Il workflow si conclude con il perfezionamento (REFINEMENT): da lì lo stato resta stabile senza azioni del
     * destinatario, quindi un cambio di stato dopo il pagamento sarebbe attribuibile solo al pagamento. Lo stato
     * raggiunto dipende dalle tempistiche dell'ambiente (EFFECTIVE_DATE, oppure DELIVERED quando il perfezionamento
     * ha un timestamp precedente alla consegna) e viene registrato per il confronto successivo.
     */
    @Given("la notifica legale ha concluso il proprio workflow")
    public void legalWorkflowCompleted() {
        String iun = sharedSteps.getNotificationIun();
        await().atMost(settings.getLegalWorkflowTimeout()).pollInterval(Duration.ofSeconds(15)).untilAsserted(() ->
                assertThat(dynamoDbService.call(DynamoTableName.TIMELINE, Map.of(
                        ":v_iun", AttributeValue.builder().s(iun).build(),
                        ":v_category", AttributeValue.builder().s(LEGAL_WORKFLOW_COMPLETED_CATEGORY).build())).items())
                        .as("La notifica legale %s non ha ancora l'elemento %s", iun, LEGAL_WORKFLOW_COMPLETED_CATEGORY)
                        .isNotEmpty());
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
     * pn-notification-cost-service rimuove gli avvisi da pn-PaymentInfo in modo asincrono dopo NOTIFICATION_CANCELLED:
     * un pagamento pubblicato prima della rimozione verrebbe ancora correlato alla notifica annullata.
     */
    private void awaitNoticeRemovedFromPaymentInfo() {
        String pk = context.getNotice().paymentInfoPk();
        await().atMost(settings.getPaymentInfoRemovalTimeout()).pollInterval(Duration.ofSeconds(5)).untilAsserted(() ->
                assertThat(dynamoDbService.call(DynamoTableName.PAYMENT_INFO, Map.of(
                        ":v_pk", AttributeValue.builder().s(pk).build())).items())
                        .as("L'avviso %s è ancora censito su pn-PaymentInfo dopo l'annullamento della notifica", pk)
                        .isEmpty());
    }

    /**
     * L'id di PAYMENT_NOTICE_PAID dipende solo da IUN e avviso (SRS §6.5): su una notifica già pagata un nuovo
     * pagamento verrebbe scartato come duplicato e il test troverebbe l'elemento di un'esecuzione precedente.
     * Si sceglie quindi il primo record storico la cui notifica non risulta pagata né da pagoPA né dall'ente.
     * Le notifiche annullate non compaiono: con lo stato CANCELLED i loro record vengono rimossi da pn-PaymentInfo.
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
        return Stream.of(PAYMENT_NOTICE_PAID, SENDER_PAYMENT_CATEGORY).allMatch(category -> dynamoDbService.call(DynamoTableName.TIMELINE, Map.of(
                ":v_iun", AttributeValue.builder().s(iun).build(),
                ":v_category", AttributeValue.builder().s(category).build())).items().isEmpty());
    }

    // =========================================================================
    // COMUNICAZIONI BONARIE
    // =========================================================================

    @Given("il destinatario paga gli avvisi anche tramite App IO")
    public void recipientPaysAlsoOnAppIo() {
        context.setPaidAlsoOnAppIo(true);
    }

    @Given("il {string} ha inviato una comunicazione bonaria con avviso pagoPA nella campagna {string}")
    public void informalNotificationWithPagoPaNotice(String paName, String campaignName) throws IOException {
        sendInformalNotificationWithPagoPaNotice(paName, campaignName, Map.of());
    }

    /**
     * Destinatario raggiungibile su più canali digitali, così che il workflow possa proseguire oltre il primo canale.
     */
    @Given("il {string} ha inviato una comunicazione bonaria con avviso pagoPA e recapiti EMAIL e PEC nella campagna {string}")
    public void informalNotificationWithPagoPaNoticeAndPec(String paName, String campaignName) throws IOException {
        sendInformalNotificationWithPagoPaNotice(paName, campaignName, Map.of("digitalDomicile", settings.getInformalRecipientPec()));
    }

    /**
     * La campagna effettiva può essere diversa da quella nominata nello scenario (vedi {@link PaymentEventsSettings}).
     * La durata del primo canale della campagna viene registrata: determina quando il workflow passerebbe al canale successivo.
     */
    private void sendInformalNotificationWithPagoPaNotice(String paName, String campaignName, Map<String, String> additionalData) throws IOException {
        String campaignId = settings.campaignId(campaignName);
        Map<String, String> data = new HashMap<>(additionalData);
        data.put("campaignId", campaignId);
        data.put("messageId", context.isPaidAlsoOnAppIo() ? messagePaidOnAppIo(paName) : "${NEW-IT}");
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
     * pn-workflow-manager usa come oggetto del messaggio App IO quello del messaggio del destinatario, non l'oggetto
     * della comunicazione bonaria: il tag della sequenza del mock va quindi nel messaggio.
     */
    private String messagePaidOnAppIo(String paName) {
        String senderId = sharedSteps.getSenderInfoProvider().getPaInfo(paName).getSenderId();
        String subject = InformalMessageTemplates.RISCUOTI_IT.getContent().getSubject() + IO_MOCK_SEQUENCE_PREFIX + settings.getIoMockPaymentSequence();
        return informalMessageProvider.createMessageITWithSubject(senderId, subject);
    }

    /**
     * Durata del primo canale del workflow della campagna previsto per i destinatari persona fisica; null se il
     * canale non ha una durata (es. ANALOG).
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
    // CAMPAGNE
    // Le campagne vengono create dal team SEND: i test non possono crearle né modificarle, quindi gli step verificano
    // soltanto che la configurazione sia quella attesa dallo scenario. La campagna effettiva si può cambiare per
    // ambiente con pn.payment-events.campaign.<nome> (vedi PaymentEventsSettings).
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
