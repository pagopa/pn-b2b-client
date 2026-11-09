package it.pagopa.pn.cucumber.steps.fatturazioneComBo.service;

import it.pagopa.pn.client.b2b.pa.domain.DynamoTableName;
import it.pagopa.pn.client.b2b.pa.service.DynamoDbService;
import it.pagopa.pn.cucumber.steps.fatturazioneComBo.model.CommunicationDomain;
import it.pagopa.pn.cucumber.steps.fatturazioneComBo.model.InternalTimelineElement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.time.Duration;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;

import static org.awaitility.Awaitility.await;

/**
 * Lettura della timeline interna (pn-Timelines), l'unica fonte del costo di notifica: le API e gli stream non lo espongono.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class InternalTimelineService {

    private static final Duration POLL_INTERVAL = Duration.ofSeconds(15);
    private static final Comparator<InternalTimelineElement> CHRONOLOGICAL =
            Comparator.comparing(InternalTimelineElement::timestamp).thenComparing(InternalTimelineElement::timelineElementId);

    private final DynamoDbService dynamoDbService;

    public List<InternalTimelineElement> timeline(String iun) {
        return dynamoDbService.callAllPages(DynamoTableName.TIMELINE_FULL, Map.of(
                        ":v_iun", AttributeValue.builder().s(iun).build()))
                .stream()
                .map(InternalTimelineElement::from)
                .sorted(CHRONOLOGICAL)
                .toList();
    }

    /**
     * Eventi di chiusura del percorso, in ordine cronologico.
     *
     * @param recIndex destinatario; null per considerare tutti i destinatari
     */
    public List<InternalTimelineElement> closures(String iun, CommunicationDomain domain, Integer recIndex) {
        return timeline(iun).stream()
                .filter(domain::isClosure)
                .filter(element -> recIndex == null || recIndex.equals(element.recIndex()))
                .toList();
    }

    /**
     * Prima chiusura del percorso del destinatario: è quella che rende fatturabile la comunicazione.
     * Il rifiuto, riferito all'intera comunicazione, vale per ogni destinatario.
     */
    public Optional<InternalTimelineElement> firstClosure(String iun, CommunicationDomain domain, int recIndex) {
        return timeline(iun).stream()
                .filter(domain::isClosure)
                .filter(element -> element.recIndex() == null || element.recIndex() == recIndex)
                .findFirst();
    }

    public InternalTimelineElement awaitFirstClosure(String iun, CommunicationDomain domain, int recIndex, Duration timeout) {
        return awaitElement(iun, timeout,
                "la chiusura del percorso del destinatario " + recIndex,
                timeline -> timeline.stream()
                        .filter(domain::isClosure)
                        .filter(element -> element.recIndex() == null || element.recIndex() == recIndex)
                        .findFirst());
    }

    /**
     * Attende una chiusura del destinatario successiva a quella indicata (cambio di esito dopo la conclusione).
     */
    public InternalTimelineElement awaitLaterClosure(String iun, CommunicationDomain domain, InternalTimelineElement firstClosure, Duration timeout) {
        return awaitElement(iun, timeout,
                "una nuova chiusura del percorso del destinatario " + firstClosure.recIndex(),
                timeline -> timeline.stream()
                        .filter(domain::isClosure)
                        .filter(element -> firstClosure.recIndex().equals(element.recIndex()))
                        .filter(element -> !element.timelineElementId().equals(firstClosure.timelineElementId()))
                        .findFirst());
    }

    public InternalTimelineElement awaitCategory(String iun, String category, Duration timeout) {
        return awaitElement(iun, timeout, "l'elemento " + category,
                timeline -> timeline.stream().filter(element -> category.equals(element.category())).findFirst());
    }

    public void awaitTimeline(String iun, Predicate<List<InternalTimelineElement>> condition, String description, Duration timeout) {
        awaitElement(iun, timeout, description, timeline -> condition.test(timeline) ? timeline.stream().findFirst() : Optional.empty());
    }

    private InternalTimelineElement awaitElement(String iun, Duration timeout, String description,
                                                 java.util.function.Function<List<InternalTimelineElement>, Optional<InternalTimelineElement>> finder) {
        AtomicReference<InternalTimelineElement> found = new AtomicReference<>();
        try {
            await().atMost(timeout).pollInterval(POLL_INTERVAL).ignoreExceptions().until(() -> {
                Optional<InternalTimelineElement> element = finder.apply(timeline(iun));
                element.ifPresent(found::set);
                return element.isPresent();
            });
        } catch (org.awaitility.core.ConditionTimeoutException e) {
            throw new AssertionError("Entro %s non è comparso nella timeline interna della notifica %s %s"
                    .formatted(timeout, iun, description), e);
        }
        log.info("Timeline interna {}: {}", iun, found.get());
        return found.get();
    }
}
