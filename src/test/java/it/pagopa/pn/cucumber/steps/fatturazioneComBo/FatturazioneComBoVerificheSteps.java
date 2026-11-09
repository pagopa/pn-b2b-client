package it.pagopa.pn.cucumber.steps.fatturazioneComBo;

import io.cucumber.java.en.Then;
import it.pagopa.pn.cucumber.steps.fatturazioneComBo.context.FatturazioneComBoScenarioContext;
import it.pagopa.pn.cucumber.steps.fatturazioneComBo.model.CommunicationDomain;
import it.pagopa.pn.cucumber.steps.fatturazioneComBo.model.InternalTimelineElement;
import it.pagopa.pn.cucumber.steps.fatturazioneComBo.model.InvoicingRecord;
import it.pagopa.pn.cucumber.steps.fatturazioneComBo.service.InternalTimelineService;
import it.pagopa.pn.cucumber.steps.fatturazioneComBo.service.InvoicingRecordService;
import it.pagopa.pn.cucumber.steps.fatturazioneComBo.service.NotificationCostExpectations;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.OptionalInt;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import static it.pagopa.pn.cucumber.steps.fatturazioneComBo.model.CommunicationDomain.INFORMAL;
import static it.pagopa.pn.cucumber.steps.fatturazioneComBo.model.CommunicationDomain.LEGAL;
import static it.pagopa.pn.cucumber.steps.fatturazioneComBo.model.CommunicationDomain.REQUEST_REFUSED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.awaitility.Awaitility.await;

/**
 * Verifiche sui costi registrati nella timeline interna e sui record delle tabelle di fatturazione.
 * <p>
 * Gli importi attesi sono opzionali ({@link NotificationCostExpectations}): senza configurazione si verifica che il
 * costo sia valorizzato, positivo e identico fra timeline e fatturazione.
 */
@Slf4j
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@RequiredArgsConstructor
public class FatturazioneComBoVerificheSteps {

    /** Invio cartaceo delle bonarie: sempre raccomandata semplice, un solo tentativo (SRS WI-1.1). */
    private static final String INFORMAL_PAPER_PREFIX = "SEND_ANALOG_MESSAGE.IUN_%s.RECINDEX_%d.ATTEMPT_0.DELIVERYTYPE_RS";
    private static final Set<String> INFORMAL_PAPER_CATEGORIES = Set.of("SEND_ANALOG_MESSAGE");
    private static final Set<String> LEGAL_PAPER_CATEGORIES = Set.of("SEND_ANALOG_DOMICILE", "SEND_SIMPLE_REGISTERED_LETTER");
    private static final Duration POLL_INTERVAL = Duration.ofSeconds(30);

    private final FatturazioneComBoScenarioContext context;
    private final InternalTimelineService timelineService;
    private final InvoicingRecordService invoicingService;
    private final NotificationCostExpectations expectations;

    // =========================================================================
    // COSTO IN TIMELINE
    // =========================================================================

    @Then("la conclusione è registrata in timeline con il costo previsto per le comunicazioni bonarie")
    public void closureHasInformalCost() {
        context.getFirstClosures().forEach((recIndex, closure) ->
                assertCost(closure, expectations.informalBaseCost(), "la prima chiusura del destinatario " + recIndex));
    }

    @Then("il rifiuto è registrato in timeline con un costo pari al costo della comunicazione bonaria per il numero di destinatari")
    public void informalRefusalCost() {
        InternalTimelineElement refusal = refusal();
        assertCost(refusal, multiply(expectations.informalBaseCost(), context.getRecipientsCount()), "il rifiuto");
        assertThat(refusal.notificationCost() % context.getRecipientsCount())
                .as("Il costo del rifiuto (%s) deve essere proporzionale ai %s destinatari", refusal.notificationCost(), context.getRecipientsCount())
                .isZero();
    }

    @Then("il rifiuto è registrato in timeline con il costo previsto per i rifiuti tecnici delle comunicazioni bonarie")
    public void informalTechnicalRefusalCost() {
        assertCost(refusal(), expectations.informalTechnicalRefusalCost(), "il rifiuto tecnico");
    }

    @Then("il nuovo esito è registrato in timeline senza costo")
    public void laterClosureWithoutCost() {
        InternalTimelineElement laterClosure = context.getLaterClosure();
        assertThat(laterClosure.notificationCost())
                .as("La nuova chiusura %s non deve avere un costo: il costo appartiene solo alla prima chiusura del destinatario", laterClosure.timelineElementId())
                .isNull();
    }

    @Then("gli eventi del percorso non riportano alcun costo")
    public void pathEventsWithoutCost() {
        List<InternalTimelineElement> pathWithCost = timelineService.timeline(context.getIun()).stream()
                .filter(element -> !context.getDomain().isClosure(element))
                .filter(InternalTimelineElement::hasCost)
                .toList();
        assertThat(pathWithCost)
                .as("Solo gli eventi di chiusura possono avere un costo. Comunicazione %s", context.getIun())
                .isEmpty();
    }

    // =========================================================================
    // FATTURAZIONE
    // =========================================================================

    @Then("la comunicazione bonaria risulta fra quelle da fatturare all'ente nel giorno della conclusione")
    @Then("la comunicazione rifiutata risulta fra quelle da fatturare all'ente nel giorno del rifiuto")
    public void informalInvoiced() {
        assertInvoiced(INFORMAL);
    }

    /**
     * La prima conclusione è fatturata con il suo costo e il suo giorno, ed è l'unica conclusione fatturata per
     * ciascun destinatario.
     */
    @Then("fra le comunicazioni da fatturare resta solo la prima conclusione, con il suo costo e il suo giorno")
    public void onlyFirstClosureInvoiced() {
        assertInvoiced(INFORMAL);
        context.getFirstClosures().forEach((recIndex, closure) -> {
            List<InvoicingRecord> closureRecords = invoicingService.records(INFORMAL, closure.paId(), day(closure), context.getIun()).stream()
                    .filter(invoicingRecord -> INFORMAL.getClosureCategories().contains(invoicingRecord.category()))
                    .filter(invoicingRecord -> invoicingRecord.timelineElementId().contains("RECINDEX_" + recIndex))
                    .toList();
            assertThat(closureRecords)
                    .as("Il destinatario %s della comunicazione %s deve essere fatturato una sola volta", recIndex, context.getIun())
                    .hasSize(1);
        });
    }

    /**
     * La nuova chiusura non deve comparire fra le comunicazioni da fatturare per tutta la finestra di assestamento.
     */
    @Then("il nuovo esito non genera ulteriori addebiti")
    public void laterClosureNotInvoiced() {
        InternalTimelineElement laterClosure = context.getLaterClosure();
        invoicingService.assertNeverRecorded(INFORMAL, laterClosure.paId(), day(laterClosure), context.getIun(),
                invoicingRecord -> laterClosure.timelineElementId().equals(invoicingRecord.timelineElementId()),
                "la nuova chiusura " + laterClosure.timelineElementId());
    }

    @Then("l'invio cartaceo risulta da fatturare insieme alla comunicazione, nel giorno della conclusione")
    public void informalPaperInvoiced() {
        assertPaperInvoicedWithClosure(INFORMAL, INFORMAL_PAPER_CATEGORIES);
        context.getFirstClosures().forEach((recIndex, closure) -> {
            String expectedPrefix = INFORMAL_PAPER_PREFIX.formatted(context.getIun(), recIndex);
            assertThat(invoicingService.records(INFORMAL, closure.paId(), day(closure), context.getIun()))
                    .as("Dell'invio cartaceo si fattura solo la raccomandata semplice al primo tentativo (%s)", expectedPrefix)
                    .filteredOn(invoicingRecord -> INFORMAL_PAPER_CATEGORIES.contains(invoicingRecord.category()))
                    .allMatch(invoicingRecord -> invoicingRecord.timelineElementId().startsWith(expectedPrefix));
        });
    }

    @Then("l'invio cartaceo risulta fra le notifiche legali da fatturare insieme alla notifica")
    public void legalPaperInvoiced() {
        assertPaperInvoicedWithClosure(LEGAL, LEGAL_PAPER_CATEGORIES);
    }

    /**
     * L'invio cartaceo viene scritto insieme alla chiusura che lo rende fatturabile: una volta presente la chiusura,
     * un eventuale invio cartaceo sarebbe già presente.
     */
    @Then("non risulta da fatturare alcun invio cartaceo")
    public void noPaperInvoiced() {
        CommunicationDomain domain = context.getDomain();
        assertInvoiced(domain);
        Set<String> paperCategories = domain == INFORMAL ? INFORMAL_PAPER_CATEGORIES : LEGAL_PAPER_CATEGORIES;
        InternalTimelineElement closure = context.getFirstClosures().get(0);
        assertThat(invoicingService.records(domain, closure.paId(), day(closure), context.getIun()))
                .as("Per la notifica %s non deve risultare da fatturare alcun invio cartaceo", context.getIun())
                .noneMatch(invoicingRecord -> paperCategories.contains(invoicingRecord.category()));
    }

    /**
     * Finché il percorso è aperto la comunicazione non deve comparire fra quelle da fatturare. Se nel frattempo il
     * percorso si conclude, la verifica si ferma: da quel momento la fatturazione è attesa.
     */
    @Then("la comunicazione bonaria non risulta fra quelle da fatturare finché il percorso non si conclude")
    public void informalNotInvoicedWhileOpen() {
        List<InternalTimelineElement> timeline = timelineService.timeline(context.getIun());
        assertThat(timeline.stream().filter(INFORMAL::isClosure).toList())
                .as("Precondizione: il percorso della comunicazione %s non deve essere ancora concluso", context.getIun())
                .isEmpty();
        String paId = timeline.stream().map(InternalTimelineElement::paId).filter(id -> id != null).findFirst()
                .orElseThrow(() -> new AssertionError("Nessun elemento della timeline di " + context.getIun() + " riporta l'ente"));
        AtomicReference<List<InvoicingRecord>> lastRead = new AtomicReference<>(List.of());
        await().during(Duration.ofMinutes(3)).atMost(Duration.ofMinutes(4)).pollInterval(POLL_INTERVAL).until(() -> {
            if (!timelineService.closures(context.getIun(), INFORMAL, null).isEmpty()) {
                return true;
            }
            lastRead.set(invoicingService.records(INFORMAL, paId, LocalDate.now(ZoneId.of("Europe/Rome")), context.getIun()));
            return lastRead.get().isEmpty();
        });
        log.info("Comunicazione {} non fatturata durante il percorso. Ultima lettura: {}", context.getIun(), lastRead.get());
    }

    @Then("la notifica legale risulta fra le notifiche legali da fatturare con il costo previsto per le notifiche legali")
    public void legalInvoicedWithLegalCost() {
        assertLegalInvoiced(expectations.legalBaseCost());
    }

    @Then("la notifica legale risulta fra le notifiche legali da fatturare con il costo previsto per i rifiuti tecnici delle notifiche legali")
    public void legalInvoicedWithTechnicalRefusalCost() {
        refusal();
        assertLegalInvoiced(expectations.legalTechnicalRefusalCost());
    }

    @Then("la notifica legale non risulta fra le comunicazioni bonarie da fatturare")
    public void legalNotInInformalInvoicing() {
        assertNotInvoicedIn(INFORMAL);
    }

    @Then("la comunicazione bonaria non risulta fra le notifiche legali da fatturare")
    public void informalNotInLegalInvoicing() {
        assertInvoiced(INFORMAL);
        assertNotInvoicedIn(LEGAL);
    }

    // =========================================================================
    // SUPPORTO
    // =========================================================================

    private InternalTimelineElement refusal() {
        InternalTimelineElement refusal = context.getFirstClosures().get(0);
        assertThat(refusal.category()).as("La comunicazione %s deve essere stata rifiutata", context.getIun()).isEqualTo(REQUEST_REFUSED);
        return refusal;
    }

    private void assertCost(InternalTimelineElement element, OptionalInt expected, String description) {
        assertThat(element.notificationCost())
                .as("%s (%s) della notifica %s deve riportare un costo positivo", description, element.timelineElementId(), context.getIun())
                .isNotNull()
                .isPositive();
        expected.ifPresent(value -> assertThat(element.notificationCost())
                .as("%s (%s) della notifica %s deve riportare il costo configurato", description, element.timelineElementId(), context.getIun())
                .isEqualTo(value));
    }

    private static OptionalInt multiply(OptionalInt value, int factor) {
        return value.isPresent() ? OptionalInt.of(value.getAsInt() * factor) : OptionalInt.empty();
    }

    /**
     * Ogni prima chiusura deve avere un record nella tabella del dominio, nel giorno della chiusura, con lo stesso costo.
     */
    private void assertInvoiced(CommunicationDomain domain) {
        context.getFirstClosures().forEach((recIndex, closure) -> {
            InvoicingRecord invoicingRecord = invoicingService.awaitRecordOf(domain, context.getIun(), closure);
            assertSoftly(softly -> {
                softly.assertThat(invoicingRecord.invoicingDay())
                        .as("Giorno di fatturazione di %s", closure.timelineElementId())
                        .isEqualTo(day(closure).toString());
                // la lambda di fatturazione lavora al millisecondo
                softly.assertThat(Instant.parse(invoicingRecord.invoicingTimestamp()))
                        .as("Istante di fatturazione di %s", closure.timelineElementId())
                        .isEqualTo(closure.timestamp().truncatedTo(ChronoUnit.MILLIS));
                softly.assertThat(invoicingRecord.notificationCost())
                        .as("Il costo fatturato di %s deve coincidere con quello della timeline", closure.timelineElementId())
                        .isEqualTo(closure.notificationCost());
                softly.assertThat(invoicingRecord.ttl())
                        .as("Il record di %s deve avere una scadenza successiva all'istante di fatturazione", closure.timelineElementId())
                        .isNotNull()
                        .isGreaterThan(closure.timestamp().getEpochSecond());
                expectations.invoicingTtlDays().ifPresent(days -> softly.assertThat(invoicingRecord.ttl())
                        .as("Scadenza del record di %s", closure.timelineElementId())
                        .isEqualTo(closure.timestamp().plus(Duration.ofDays(days)).getEpochSecond()));
            });
        });
    }

    private void assertPaperInvoicedWithClosure(CommunicationDomain domain, Set<String> paperCategories) {
        assertInvoiced(domain);
        context.getFirstClosures().forEach((recIndex, closure) -> {
            InvoicingRecord closureRecord = invoicingService.awaitRecordOf(domain, context.getIun(), closure);
            List<InvoicingRecord> paperRecords = invoicingService.records(domain, closure.paId(), day(closure), context.getIun()).stream()
                    .filter(invoicingRecord -> paperCategories.contains(invoicingRecord.category()))
                    .filter(invoicingRecord -> invoicingRecord.timelineElementId().contains("RECINDEX_" + recIndex))
                    .toList();
            assertThat(paperRecords)
                    .as("L'invio cartaceo del destinatario %s della notifica %s deve risultare da fatturare", recIndex, context.getIun())
                    .isNotEmpty()
                    .allSatisfy(paper -> assertThat(paper.invoicingTimestamp())
                            .as("L'invio cartaceo %s si fattura con l'istante della chiusura %s", paper.timelineElementId(), closure.timelineElementId())
                            .isEqualTo(closureRecord.invoicingTimestamp()));
        });
    }

    private void assertLegalInvoiced(OptionalInt expectedCost) {
        InternalTimelineElement closure = context.getFirstClosures().get(0);
        assertCost(closure, expectedCost, "la chiusura " + closure.category());
        assertInvoiced(LEGAL);
    }

    private void assertNotInvoicedIn(CommunicationDomain domain) {
        InternalTimelineElement closure = context.getFirstClosures().get(0);
        invoicingService.assertNeverRecorded(domain, closure.paId(), day(closure), context.getIun(),
                invoicingRecord -> true, "alcun record");
    }

    private static LocalDate day(InternalTimelineElement element) {
        return InvoicingRecordService.invoicingDay(element.timestamp());
    }
}
