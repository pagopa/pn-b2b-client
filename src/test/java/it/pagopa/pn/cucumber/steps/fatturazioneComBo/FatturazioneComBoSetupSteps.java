package it.pagopa.pn.cucumber.steps.fatturazioneComBo;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import it.pagopa.pn.client.b2b.pa.domain.Destinatario;
import it.pagopa.pn.client.b2b.pa.provider.DestinatarioRegistry;
import it.pagopa.pn.cucumber.steps.SendSharedContext;
import it.pagopa.pn.cucumber.steps.SharedSteps;
import it.pagopa.pn.cucumber.steps.fatturazioneComBo.context.FatturazioneComBoScenarioContext;
import it.pagopa.pn.cucumber.steps.fatturazioneComBo.model.CommunicationDomain;
import it.pagopa.pn.cucumber.steps.fatturazioneComBo.model.InternalTimelineElement;
import it.pagopa.pn.cucumber.steps.fatturazioneComBo.service.InternalTimelineService;
import it.pagopa.pn.cucumber.steps.pa.PresaInCaricoNoticaBonariaSteps;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static it.pagopa.pn.cucumber.steps.fatturazioneComBo.model.CommunicationDomain.INFORMAL;
import static it.pagopa.pn.cucumber.steps.fatturazioneComBo.model.CommunicationDomain.LEGAL;
import static it.pagopa.pn.cucumber.steps.fatturazioneComBo.model.CommunicationDomain.REQUEST_REFUSED;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Invio delle comunicazioni bonarie e delle notifiche legali e avanzamento del loro percorso fino alla chiusura.
 * <p>
 * I dati di invio (campagne, recapiti dei mock) sono quelli già usati dalle suite notificheBonarie e
 * workflowNotifica, validi su tutti gli ambienti; i destinatari vengono risolti per ambiente tramite
 * {@link DestinatarioRegistry} (config/taxids-{env}.properties).
 */
@Slf4j
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@RequiredArgsConstructor
public class FatturazioneComBoSetupSteps {

    // Campagne delle comunicazioni bonarie
    /** Primo canale EMAIL: si chiude al feedback desiderato o al fallimento del canale. */
    private static final String CAMPAIGN_DIGITAL = "FattOrd";
    /** Prevede la raccomandata semplice dopo i canali digitali. */
    private static final String CAMPAIGN_ANALOG = "MessaMora";

    // Recapiti con esito noto dei mock di external-channels e del simulatore SES
    private static final String EMAIL_DELIVERED = "tullio.test@virgilio.it";
    private static final String EMAIL_BOUNCED = "bounce@simulator.amazonses.com";
    private static final String EMAIL_SUPPRESSED = "suppressionlist@simulator.amazonses.com";
    private static final String INFORMAL_PAPER_DELIVERED = "Via@OK_RS";

    // Valori speciali interpretati dai builder delle richieste
    private static final String NULL = "NULL";
    private static final String NO_PHYSICAL_ADDRESS = "${PHYSICAL_ADDRESS_NULL}";
    private static final String NEW_ITALIAN_MESSAGE = "${NEW-IT}";
    /** Messaggio in italiano e francese: chiedere il tedesco rende la comunicazione non conforme. */
    private static final String NEW_ITALIAN_FRENCH_MESSAGE = "${NEW-IT-FR}";
    private static final String LANGUAGE_NOT_IN_MESSAGE = "DE";

    private static final String ACCEPTED = "ACCEPTED";
    private static final String REFUSED = "REFUSED";
    /**
     * Unico motivo di rifiuto tecnico (pn-delivery-push-validator, NotificationRefusedErrorCodeInt): la ricerca
     * dell'indirizzo sui registri restituisce un errore. NOT_VALID_ADDRESS e ADDRESS_NOT_FOUND sono rifiuti ordinari.
     */
    private static final String ADDRESS_SEARCH_FAILED = "ADDRESS_SEARCH_FAILED";

    private static final Duration INFORMAL_CLOSURE_TIMEOUT = Duration.ofMinutes(20);
    private static final Duration LEGAL_CLOSURE_TIMEOUT = Duration.ofMinutes(30);
    private static final Duration REFUSAL_TIMEOUT = Duration.ofMinutes(5);

    private final FatturazioneComBoScenarioContext context;
    private final SharedSteps sharedSteps;
    private final SendSharedContext sendSharedContext;
    private final DestinatarioRegistry destinatarioRegistry;
    private final PresaInCaricoNoticaBonariaSteps informalNotificationSteps;
    private final InternalTimelineService timelineService;

    /*
     * Destinatario la cui ricerca dell'indirizzo sui registri va in errore (rifiuto tecnico). Il default è il destinatario
     * del mock dei registri per cui ANPR risponde con errore 500, come nella suite RicercaIndirizzoSpezioneVas
     * (RICERCA_INDIRIZZO_MULTI_COSTI_3): vale dove SEND usa il mock dei registri. Altri destinatari del mock che vanno
     * in errore 500 (pagina "Mock - National Registries"): PF RCCRCC80A01H501B, CIACIA80A01H501X; PG 01634580029
     * (Registro Imprese). Si cambiano con -D o con le variabili d'ambiente PN_COMBO_INVOICING_ADDRESS_SEARCH_ERROR_*.
     * Per una PG senza PEC serve una campagna il cui flusso non preveda la PEC per le PG, altrimenti la richiesta è
     * rifiutata con DIGITAL_ADDRESS_MISSING prima della ricerca dell'indirizzo.
     */
    @Value("${pn.combo-invoicing.address-search-error.denomination:PF Errore 500}")
    private String addressSearchErrorDenomination;
    @Value("${pn.combo-invoicing.address-search-error.tax-id:SRFBRD80A01E256Z}")
    private String addressSearchErrorTaxId;
    @Value("${pn.combo-invoicing.address-search-error.recipient-type:PF}")
    private String addressSearchErrorRecipientType;
    @Value("${pn.combo-invoicing.address-search-error.campaign-id:" + CAMPAIGN_ANALOG + "}")
    private String addressSearchErrorCampaignId;

    // =========================================================================
    // COMUNICAZIONI BONARIE ACCETTATE
    // =========================================================================

    @Given("il {string} ha inviato una comunicazione bonaria a un destinatario raggiungibile via email")
    public void informalReachableByEmail(String paName) throws IOException {
        sendInformal(paName, ACCEPTED, informalRecipient(CAMPAIGN_DIGITAL, destinatarioRegistry.DESTINATARIO_ETTORE_FIERAMOSCA,
                Map.of("email", EMAIL_DELIVERED)));
    }

    /**
     * L'email rimbalza: il percorso si chiude senza raggiungere il destinatario, che può ancora leggere la comunicazione.
     */
    @Given("il {string} ha inviato una comunicazione bonaria a un destinatario non raggiungibile via email")
    public void informalNotReachableByEmail(String paName) throws IOException {
        sendInformal(paName, ACCEPTED, informalRecipient(CAMPAIGN_DIGITAL, destinatarioRegistry.DESTINATARIO_ETTORE_FIERAMOSCA,
                Map.of("email", EMAIL_BOUNCED)));
    }

    /**
     * L'email è in suppression list: la campagna passa alla raccomandata semplice, che il mock consegna.
     */
    @Given("il {string} ha inviato una comunicazione bonaria che prevede l'invio cartaceo")
    public void informalWithPaperDelivery(String paName) throws IOException {
        sendInformal(paName, ACCEPTED, informalRecipient(CAMPAIGN_ANALOG, destinatarioRegistry.DESTINATARIO_ETTORE_FIERAMOSCA,
                Map.of("email", EMAIL_SUPPRESSED, "physical_address_address", INFORMAL_PAPER_DELIVERED)));
    }

    @When("il percorso della comunicazione bonaria si conclude")
    @Given("il percorso della comunicazione bonaria si è concluso")
    public void informalPathCompleted() {
        awaitFirstClosures(INFORMAL_CLOSURE_TIMEOUT);
    }

    /**
     * La lettura dopo la chiusura cambia l'esito del destinatario e produce una nuova chiusura.
     */
    @When("il destinatario legge la comunicazione bonaria e l'esito cambia")
    public void informalReadAfterClosure() {
        Destinatario recipient = sendSharedContext.getInformalNotificationContext().getRecipient().getDestinatario();
        informalNotificationSteps.recipientReadsInformalNotification(recipient);
        context.setLaterClosure(timelineService.awaitLaterClosure(context.getIun(), INFORMAL, context.getFirstClosures().get(0), INFORMAL_CLOSURE_TIMEOUT));
    }

    // =========================================================================
    // COMUNICAZIONI BONARIE RIFIUTATE
    // =========================================================================

    /**
     * Il messaggio non è disponibile nella lingua richiesta: la comunicazione viene rifiutata in validazione,
     * senza errori tecnici.
     */
    @When("il {string} invia una comunicazione bonaria non conforme a {int} destinatari/destinatario")
    @Given("il {string} ha inviato una comunicazione bonaria non conforme a {int} destinatari/destinatario")
    public void informalNotCompliant(String paName, int recipients) throws IOException {
        List<Destinatario> candidates = List.of(destinatarioRegistry.DESTINATARIO_CUCUMBER_SPA, destinatarioRegistry.DESTINATARIO_ETTORE_FIERAMOSCA);
        assertThat(recipients).as("Destinatari disponibili per lo scenario").isBetween(1, candidates.size());
        Map<String, String> notCompliant = Map.of("messageId", NEW_ITALIAN_FRENCH_MESSAGE, "additionalLanguages", LANGUAGE_NOT_IN_MESSAGE);
        sendInformal(paName, REFUSED, candidates.subList(0, recipients).stream()
                .map(recipient -> informalRecipient(CAMPAIGN_DIGITAL, recipient, notCompliant))
                .toArray(Map[]::new));
        awaitRefusal();
    }

    /**
     * Destinatario senza indirizzo fisico: SEND lo cerca sui registri e la ricerca va in errore (rifiuto tecnico).
     */
    @When("il {string} invia una comunicazione bonaria senza indirizzo fisico a un destinatario per cui la ricerca dell'indirizzo da parte di SEND non va a buon fine")
    public void informalWithAddressSearchFailed(String paName) throws IOException {
        sendInformal(paName, REFUSED, informalRecipient(addressSearchErrorCampaignId, addressSearchErrorRecipient(),
                Map.of("physical_address", NO_PHYSICAL_ADDRESS)));
        informalNotificationSteps.verifyNotificationStatusError(ADDRESS_SEARCH_FAILED);
        awaitTechnicalRefusal();
    }

    // =========================================================================
    // NOTIFICHE LEGALI
    // =========================================================================

    @Given("il {string} ha inviato una notifica legale a un destinatario raggiungibile via PEC")
    public void legalReachableByPec(String paName) {
        prepareLegal(Map.of(), destinatarioRegistry.DESTINATARIO_MARIO_GHERKIN, Map.of());
        sharedSteps.sendNotification(paName, ACCEPTED);
        rememberLegal();
    }

    @When("il {string} annulla la notifica legale")
    public void legalCancelled(String paName) {
        sharedSteps.notificationCanBeCanceledWithIunByComune("può", paName);
        awaitFirstClosures(LEGAL_CLOSURE_TIMEOUT);
    }

    /**
     * Il documento della notifica non viene caricato: la notifica viene rifiutata in validazione, senza errori tecnici.
     */
    @When("il {string} invia una notifica legale con un documento non disponibile")
    public void legalWithMissingDocument(String paName) {
        prepareLegal(Map.of(), destinatarioRegistry.DESTINATARIO_MARIO_GHERKIN, Map.of());
        sharedSteps.laNotificaVieneInviataSenzaPreloadAllegato(paName);
        rememberLegal();
        awaitRefusal();
    }

    /**
     * Destinatario senza domicilio digitale né indirizzo fisico: SEND lo cerca sui registri e la ricerca va in
     * errore (rifiuto tecnico).
     */
    @When("il {string} invia una notifica legale senza indirizzo fisico a un destinatario per cui la ricerca dell'indirizzo da parte di SEND non va a buon fine")
    public void legalWithAddressSearchFailed(String paName) throws IOException {
        prepareLegal(Map.of(), addressSearchErrorRecipient(), Map.of("digitalDomicile", NULL, "physicalAddress", NULL));
        // Il polling sullo stato REFUSED degli step legali restituisce il controllo dopo molti minuti: il rifiuto
        // si attende direttamente sulla timeline interna, dove compare in circa un minuto.
        sharedSteps.sendNotificationWithoutWaitingForAccepted(paName);
        rememberLegal();
        awaitTechnicalRefusal();
    }

    // =========================================================================
    // SUPPORTO
    // =========================================================================

    private Destinatario addressSearchErrorRecipient() {
        return new Destinatario(addressSearchErrorDenomination, addressSearchErrorTaxId, null, addressSearchErrorRecipientType, "PEC");
    }

    private Map<String, String> informalRecipient(String campaignId, Destinatario recipient, Map<String, String> overrides) {
        Map<String, String> data = new HashMap<>();
        data.put("campaignId", campaignId);
        data.put("messageId", NEW_ITALIAN_MESSAGE);
        data.put("subject", "Fatturazione comunicazioni bonarie");
        data.put("recipientType", recipient.getRecipientType());
        data.put("taxId", recipient.getTaxId());
        data.put("denomination", recipient.getDenomination());
        data.put("email", NULL);
        data.put("digitalDomicile", NULL);
        data.putAll(overrides);
        return data;
    }

    @SafeVarargs
    private void sendInformal(String paName, String expectedStatus, Map<String, String>... recipients) throws IOException {
        informalNotificationSteps.createInformalNotification(paName, recipients[0]);
        for (int i = 1; i < recipients.length; i++) {
            informalNotificationSteps.addInformalRecipientLight(recipients[i]);
        }
        informalNotificationSteps.sendInformalAndWaitStatus(expectedStatus);
        context.setDomain(INFORMAL);
        context.setNotificationRequestId(informalNotificationSteps.getSavedNotificationRequestId());
        if (sendSharedContext.getInformalNotificationContext().getIun() == null) {
            // Lo stato di una richiesta rifiutata non riporta lo IUN: pn-delivery assegna come id della richiesta
            // lo IUN in base64.
            sendSharedContext.getInformalNotificationContext().setIun(
                    new String(Base64.getDecoder().decode(context.getNotificationRequestId()), StandardCharsets.UTF_8));
        }
        context.setIun(sendSharedContext.getInformalNotificationContext().getIun());
        context.setRecipientsCount(recipients.length);
        assertThat(context.getIun()).as("La comunicazione bonaria inviata deve avere uno IUN").isNotBlank();
        log.info("Comunicazione bonaria {} con {} destinatari in stato {}", context.getIun(), recipients.length, expectedStatus);
    }

    private void prepareLegal(Map<String, String> notificationOverrides, Destinatario recipient, Map<String, String> recipientOverrides) {
        Map<String, String> notification = new HashMap<>(Map.of(
                "subject", "Fatturazione notifiche legali",
                "senderDenomination", "Comune di Milano"));
        notification.putAll(notificationOverrides);
        sharedSteps.prepareNotificationRequest(notification);
        sharedSteps.addDestinatarioWithParams(recipient, new HashMap<>(recipientOverrides));
    }

    private void rememberLegal() {
        context.setDomain(LEGAL);
        context.setIun(sharedSteps.getNotificationIun());
        context.setRecipientsCount(1);
        log.info("Notifica legale {}", context.getIun());
    }

    private void awaitFirstClosures(Duration timeout) {
        CommunicationDomain domain = context.getDomain();
        for (int recIndex = 0; recIndex < context.getRecipientsCount(); recIndex++) {
            InternalTimelineElement closure = timelineService.awaitFirstClosure(context.getIun(), domain, recIndex, timeout);
            context.getFirstClosures().put(recIndex, closure);
            context.setPaId(closure.paId());
        }
    }

    private void awaitTechnicalRefusal() {
        assertThat(awaitRefusal().refusalCodes())
                .as("La notifica %s deve essere rifiutata per un errore tecnico della ricerca dell'indirizzo", context.getIun())
                .contains(ADDRESS_SEARCH_FAILED);
    }

    private InternalTimelineElement awaitRefusal() {
        InternalTimelineElement refusal = timelineService.awaitCategory(context.getIun(), REQUEST_REFUSED, REFUSAL_TIMEOUT);
        context.getFirstClosures().put(0, refusal);
        context.setPaId(refusal.paId());
        return refusal;
    }
}
