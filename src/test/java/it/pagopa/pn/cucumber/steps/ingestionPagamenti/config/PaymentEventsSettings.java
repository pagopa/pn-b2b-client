package it.pagopa.pn.cucumber.steps.ingestionPagamenti.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.time.Duration;

/**
 * Valori dei test sugli eventi di pagamento che possono cambiare tra ambienti o con il rilascio: nomi delle campagne,
 * tempi di attesa, log group e testo dei log, recapiti e sequenze dei mock. Ogni valore ha un default e si sovrascrive
 * con una system property (-D), senza modificare i file in config/.
 * <ul>
 *     <li>{@code pn.payment-events.campaign.<nome>}: campagna effettiva da usare al posto di quella nominata nello
 *     scenario (es. {@code -Dpn.payment-events.campaign.StopOnPaid-QA=FattOrdStop}); senza override si usa il nome dello scenario</li>
 *     <li>{@code pn.payment-events.timeout.*} e {@code pn.payment-events.window.*}: durate ISO-8601 (es. PT3M)</li>
 * </ul>
 */
@Getter
@Component
public class PaymentEventsSettings {
    private static final String CAMPAIGN_PROPERTY_PREFIX = "pn.payment-events.campaign.";

    private final Environment environment;

    /** Elaborazione asincrona di un evento (timeline, pn-PaymentInfo, log). */
    private final Duration asyncProcessingTimeout;
    /** Perfezionamento di una notifica legale digitale. */
    private final Duration legalWorkflowTimeout;
    /** Rimozione degli avvisi da pn-PaymentInfo dopo l'annullamento. */
    private final Duration paymentInfoRemovalTimeout;
    /** Arrivo di un evento malformato nella DLQ dei pagamenti. */
    private final Duration dlqTimeout;
    /** Osservazione degli effetti scritti dallo stesso consumer di un elemento già presente. */
    private final Duration sideEffectWindow;
    /** Osservazione di un pagamento che deve essere ignorato. */
    private final Duration ignoredPaymentWindow;
    /** Osservazione dell'assenza di un evento nella DLQ. */
    private final Duration dlqObservationWindow;
    /** Margine di schedulazione oltre la durata del primo canale della campagna. */
    private final Duration escalationMargin;
    /** Ritardo con cui pn-stream riceve gli elementi di timeline. */
    private final Duration streamPropagationDelay;

    private final String costServiceLogGroup;
    private final String paymentDiscardedLogMessage;

    private final String informalRecipientTaxId;
    private final String informalRecipientDenomination;
    private final String informalRecipientEmail;
    private final String informalRecipientPec;
    private final String ioMockPaymentSequence;

    public PaymentEventsSettings(
            Environment environment,
            @Value("${pn.payment-events.timeout.async-processing:PT3M}") String asyncProcessingTimeout,
            @Value("${pn.payment-events.timeout.legal-workflow:PT20M}") String legalWorkflowTimeout,
            @Value("${pn.payment-events.timeout.payment-info-removal:PT2M}") String paymentInfoRemovalTimeout,
            @Value("${pn.payment-events.timeout.dlq:PT2M}") String dlqTimeout,
            @Value("${pn.payment-events.window.side-effect:PT30S}") String sideEffectWindow,
            @Value("${pn.payment-events.window.ignored-payment:PT1M}") String ignoredPaymentWindow,
            @Value("${pn.payment-events.window.dlq-observation:PT1M}") String dlqObservationWindow,
            @Value("${pn.payment-events.window.escalation-margin:PT2M}") String escalationMargin,
            @Value("${pn.payment-events.window.stream-propagation:PT30S}") String streamPropagationDelay,
            @Value("${pn.payment-events.cost-service.log-group:/aws/ecs/pn-notification-cost-service}") String costServiceLogGroup,
            @Value("${pn.payment-events.cost-service.discarded-log-message:PaymentInfo not found}") String paymentDiscardedLogMessage,
            @Value("${pn.payment-events.informal-recipient.tax-id:FRMTTR76M06B715E}") String informalRecipientTaxId,
            @Value("${pn.payment-events.informal-recipient.denomination:Ettore Fieramosca}") String informalRecipientDenomination,
            @Value("${pn.payment-events.informal-recipient.email:tullio.test@virgilio.it}") String informalRecipientEmail,
            @Value("${pn.payment-events.informal-recipient.pec:example@OK-pecSuccess.it}") String informalRecipientPec,
            @Value("${pn.payment-events.io-mock.payment-sequence:OK_READ_THEN_PAID}") String ioMockPaymentSequence) {
        this.environment = environment;
        this.asyncProcessingTimeout = Duration.parse(asyncProcessingTimeout);
        this.legalWorkflowTimeout = Duration.parse(legalWorkflowTimeout);
        this.paymentInfoRemovalTimeout = Duration.parse(paymentInfoRemovalTimeout);
        this.dlqTimeout = Duration.parse(dlqTimeout);
        this.sideEffectWindow = Duration.parse(sideEffectWindow);
        this.ignoredPaymentWindow = Duration.parse(ignoredPaymentWindow);
        this.dlqObservationWindow = Duration.parse(dlqObservationWindow);
        this.escalationMargin = Duration.parse(escalationMargin);
        this.streamPropagationDelay = Duration.parse(streamPropagationDelay);
        this.costServiceLogGroup = costServiceLogGroup;
        this.paymentDiscardedLogMessage = paymentDiscardedLogMessage;
        this.informalRecipientTaxId = informalRecipientTaxId;
        this.informalRecipientDenomination = informalRecipientDenomination;
        this.informalRecipientEmail = informalRecipientEmail;
        this.informalRecipientPec = informalRecipientPec;
        this.ioMockPaymentSequence = ioMockPaymentSequence;
    }

    /**
     * Campagna effettiva per il nome usato nello scenario.
     */
    public String campaignId(String scenarioCampaignName) {
        return environment.getProperty(CAMPAIGN_PROPERTY_PREFIX + scenarioCampaignName, scenarioCampaignName);
    }
}
