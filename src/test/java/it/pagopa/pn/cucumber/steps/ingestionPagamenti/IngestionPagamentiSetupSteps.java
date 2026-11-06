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
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

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

    // Destinatario e recapito delle comunicazioni bonarie usati in tutta la suite notificheBonarie
    private static final String INFORMAL_RECIPIENT_TAX_ID = "FRMTTR76M06B715E";
    private static final String INFORMAL_RECIPIENT_DENOMINATION = "Ettore Fieramosca";
    private static final String INFORMAL_RECIPIENT_EMAIL = "tullio.test@virgilio.it";
    // PEC con esito positivo del mock di external-channel, come in MonitoraggioCampagneNotificheBonarie
    private static final String INFORMAL_RECIPIENT_PEC = "example@OK-pecSuccess.it";

    private static final String INFORMAL_SUBJECT = "Comunicazione bonaria con avviso pagoPA";
    // Il router di pn-io-connector-mock instrada sul mock i messaggi IO il cui oggetto contiene @io:<sequenza>.
    // Con OK_READ_THEN_PAID il mock risponde READ a 60-90 s e PAID dopo 90 s al polling di pn-io-connector
    private static final String IO_MOCK_SEQUENCE_TAG = " @io:OK_READ_THEN_PAID";

    private static final String PAYMENT_NOTICE_PAID = "PAYMENT_NOTICE_PAID";
    private static final String NOTIFICATION_PAID = "NOTIFICATION_PAID";
    private static final String STOP_ON_PAID = "stopOnPaid";
    private static final String COMMUNICATION_TYPE = "communicationType";
    private static final String NOTIFICATION_STATUS_CANCELLED = "CANCELLED";
    private static final String LEGAL_STATUS_COMPLETED = "EFFECTIVE_DATE";
    private static final String INFORMAL_STATUS_COMPLETED = "COMPLETED_REACHED";

    private final IngestionPagamentiScenarioContext context;
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
     * EFFECTIVE_DATE è uno stato stabile raggiungibile senza azioni del destinatario: un cambio di stato dopo il
     * pagamento sarebbe attribuibile solo al pagamento.
     */
    @Given("la notifica legale ha concluso il proprio workflow")
    public void legalWorkflowCompleted() {
        legalNotificationSteps.readingEventUpToTheStatusOfNotification(LEGAL_STATUS_COMPLETED);
    }

    @Given("il {string} ha annullato la notifica legale")
    public void cancelLegalNotification(String paName) {
        sharedSteps.notificationCanBeCanceledWithIunByComune("può", paName);
        legalNotificationSteps.readingEventUpToTheStatusOfNotification(NOTIFICATION_STATUS_CANCELLED);
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
        return Stream.of(PAYMENT_NOTICE_PAID, NOTIFICATION_PAID).allMatch(category -> dynamoDbService.call(DynamoTableName.TIMELINE, Map.of(
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
    public void informalNotificationWithPagoPaNotice(String paName, String campaignId) throws IOException {
        sendInformalNotificationWithPagoPaNotice(paName, campaignId, Map.of());
    }

    /**
     * Destinatario raggiungibile su più canali digitali, così che il workflow possa proseguire oltre il primo canale.
     */
    @Given("il {string} ha inviato una comunicazione bonaria con avviso pagoPA e recapiti EMAIL e PEC nella campagna {string}")
    public void informalNotificationWithPagoPaNoticeAndPec(String paName, String campaignId) throws IOException {
        sendInformalNotificationWithPagoPaNotice(paName, campaignId, Map.of("digitalDomicile", INFORMAL_RECIPIENT_PEC));
    }

    private void sendInformalNotificationWithPagoPaNotice(String paName, String campaignId, Map<String, String> additionalData) throws IOException {
        Map<String, String> data = new HashMap<>(additionalData);
        data.put("campaignId", campaignId);
        data.put("messageId", context.isPaidAlsoOnAppIo() ? messagePaidOnAppIo(paName) : "${NEW-IT}");
        data.put("subject", INFORMAL_SUBJECT);
        data.put("recipientType", "PF");
        data.put("taxId", INFORMAL_RECIPIENT_TAX_ID);
        data.put("denomination", INFORMAL_RECIPIENT_DENOMINATION);
        data.put("email", INFORMAL_RECIPIENT_EMAIL);
        data.put("payment_multy_number", "1");
        data.put("payment_amount", String.valueOf(INFORMAL_PAYMENT_AMOUNT));

        informalNotificationSteps.createInformalNotification(paName, data);
        informalNotificationSteps.sendInformalAndWaitStatus("ACCEPTED");

        InformalNotificationContext.InformalRecipientContext recipient = sendSharedContext.getInformalNotificationContext().getRecipient();
        context.setNotice(new PaymentNotice(recipient.getPaymentCreditorTaxId(), recipient.getPaymentNoticeCode(), INFORMAL_PAYMENT_AMOUNT));
        log.info("Comunicazione bonaria {} con avviso {}", sharedSteps.getNotificationIun(), context.getNotice());
    }

    /**
     * pn-workflow-manager usa come oggetto del messaggio App IO quello del messaggio del destinatario, non l'oggetto
     * della comunicazione bonaria: il tag della sequenza del mock va quindi nel messaggio.
     */
    private String messagePaidOnAppIo(String paName) {
        String senderId = sharedSteps.getSenderInfoProvider().getPaInfo(paName).getSenderId();
        String subject = InformalMessageTemplates.RISCUOTI_IT.getContent().getSubject() + IO_MOCK_SEQUENCE_TAG;
        return informalMessageProvider.createMessageITWithSubject(senderId, subject);
    }

    @Given("la comunicazione bonaria ha concluso il proprio workflow")
    public void informalWorkflowCompleted() {
        informalNotificationSteps.verifyFinalNotificationStatus(INFORMAL_STATUS_COMPLETED);
    }

    // =========================================================================
    // CAMPAGNE
    // Le campagne con stopOnPaid vengono create dal team SEND: i test non possono crearle né modificarle, quindi
    // gli step verificano soltanto che la configurazione sia quella attesa dallo scenario.
    // =========================================================================

    @Given("la campagna {string} del {string} conclude il workflow al pagamento")
    public void campaignStopsOnPaid(String campaignId, String paName) {
        assertThat(campaignStopOnPaid(campaignId, paName))
                .as("La campagna %s deve avere %s valorizzato a true", campaignId, STOP_ON_PAID)
                .isNotNull()
                .extracting(AttributeValue::bool)
                .isEqualTo(true);
    }

    @Given("la campagna {string} del {string} non conclude il workflow al pagamento")
    public void campaignDoesNotStopOnPaid(String campaignId, String paName) {
        assertThat(campaignStopOnPaid(campaignId, paName))
                .as("La campagna %s deve avere %s valorizzato a false", campaignId, STOP_ON_PAID)
                .isNotNull()
                .extracting(AttributeValue::bool)
                .isEqualTo(false);
    }

    @Given("la campagna {string} del {string} non specifica se concludere il workflow al pagamento")
    public void campaignWithoutStopOnPaid(String campaignId, String paName) {
        assertThat(campaignStopOnPaid(campaignId, paName))
                .as("La campagna %s non deve avere l'attributo %s", campaignId, STOP_ON_PAID)
                .isNull();
    }

    private AttributeValue campaignStopOnPaid(String campaignId, String paName) {
        String senderId = sharedSteps.getSenderInfoProvider().getPaInfo(paName).getSenderId();
        QueryResponse response = dynamoDbService.call(DynamoTableName.CAMPAIGNS, Map.of(
                ":v_senderId", AttributeValue.builder().s(senderId).build(),
                ":v_campaignId", AttributeValue.builder().s(campaignId).build()));
        assertThat(response.items())
                .as("Campagna %s del mittente %s non trovata su %s", campaignId, paName, DynamoTableName.CAMPAIGNS.getValue())
                .hasSize(1);
        return response.items().get(0).get(STOP_ON_PAID);
    }
}
