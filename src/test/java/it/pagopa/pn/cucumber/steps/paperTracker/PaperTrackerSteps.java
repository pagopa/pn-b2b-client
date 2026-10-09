package it.pagopa.pn.cucumber.steps.paperTracker;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import it.pagopa.pn.client.b2b.generated.openapi.clients.papertracker.model.Attachment;
import it.pagopa.pn.client.b2b.generated.openapi.clients.papertracker.model.PaperEvent;
import it.pagopa.pn.client.b2b.generated.openapi.clients.papertracker.model.PaperTrackerOutputsResponse;
import it.pagopa.pn.client.b2b.generated.openapi.clients.papertracker.model.Tracking;
import it.pagopa.pn.client.b2b.generated.openapi.clients.papertracker.model.TrackingError;
import it.pagopa.pn.client.b2b.generated.openapi.clients.papertracker.model.TrackingErrorsResponse;
import it.pagopa.pn.client.b2b.generated.openapi.clients.papertracker.model.TrackingsRequest;
import it.pagopa.pn.client.b2b.generated.openapi.clients.papertracker.model.TrackingsResponse;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.AttachmentDetails;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.FullSentNotificationV29;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.NotificationStatusV26;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementDetailsV28;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementV28;
import it.pagopa.pn.client.b2b.pa.service.IPnPaperTrackerClient;
import it.pagopa.pn.cucumber.steps.SharedSteps;
import it.pagopa.pn.cucumber.steps.pa.AvanzamentoNotificheB2bSteps;
import it.pagopa.pn.cucumber.steps.paperTracker.domain.NotificationEvent;
import it.pagopa.pn.cucumber.steps.paperTracker.parser.EventTimelineParser;
import it.pagopa.pn.cucumber.steps.paperTracker.proxy.PaperTrackerSchemaValidatorProxy;
import it.pagopa.pn.cucumber.steps.paperTracker.validator.additionalDetails.AdditionalDetailsValidator;
import it.pagopa.pn.cucumber.steps.paperTracker.validator.additionalDetails.AffectedEventsValidator;
import it.pagopa.pn.cucumber.steps.paperTracker.validator.additionalDetails.FlatAdditionalDetailsValidator;
import it.pagopa.pn.cucumber.steps.paperTracker.validator.additionalDetails.MissingAttachmentsValidator;
import it.pagopa.pn.cucumber.steps.paperTracker.validator.additionalDetails.MissingStatusCodeValidator;
import it.pagopa.pn.cucumber.steps.paperTracker.validator.additionalDetails.OcrDataResultPayloadValidator;
import it.pagopa.pn.cucumber.steps.utilitySteps.PaperTrackerTrackingSequence;
import it.pagopa.pn.cucumber.steps.utilitySteps.TimelineSequence;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static it.pagopa.pn.client.b2b.pa.domain.Costanti.PREPARE_ANALOG_DOMICILE;
import static it.pagopa.pn.client.b2b.pa.domain.Costanti.PREPARE_SIMPLE_REGISTERED_LETTER;
import static it.pagopa.pn.client.b2b.pa.domain.Costanti.REFINEMENT;
import static it.pagopa.pn.client.b2b.pa.domain.Costanti.SEND_ANALOG_FEEDBACK;
import static it.pagopa.pn.client.b2b.pa.domain.Costanti.SEND_ANALOG_PROGRESS;
import static it.pagopa.pn.client.b2b.pa.domain.Costanti.SEND_SIMPLE_REGISTERED_LETTER_PROGRESS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
public class PaperTrackerSteps {
    private static final String TRACKINGS_ELEMENT_NOT_FOUND = "La risposta di /trackings non contiene tutti gli elementi presenti in timeline!";
    private static final String OUTPUTS_RESPONSE_ELEMENT_NOT_FOUND = "La risposta di /outputs non contiene tutti gli elementi previsti che sono presenti in timeline!";
    private static final Map<String, AdditionalDetailsValidator> VALIDATORS = Map.of(
            "ocrDataResultPayload", new OcrDataResultPayloadValidator(),
            "affectedEvents", new AffectedEventsValidator(),
            "missingStatusCodes", new MissingStatusCodeValidator(),
            "missingAttachments", new MissingAttachmentsValidator(),
            "flatAdditionalDetails", new FlatAdditionalDetailsValidator()
    );

    private final EventTimelineParser eventTimelineParser;
    private final AvanzamentoNotificheB2bSteps b2bSteps;
    private final SharedSteps sharedSteps;
    private final IPnPaperTrackerClient paperTrackerClient;
    private final PaperTrackerSchemaValidatorProxy paperTrackerSchemaValidatorProxy;
    private TrackingsResponse responseTracking;
    private List<String> trackingKeys;

    public PaperTrackerSteps(EventTimelineParser eventTimelineParser,
                             AvanzamentoNotificheB2bSteps b2bSteps,
                             IPnPaperTrackerClient paperTrackerClient,
                             PaperTrackerSchemaValidatorProxy paperTrackerSchemaValidatorProxy) {
        this.eventTimelineParser = eventTimelineParser;
        this.b2bSteps = b2bSteps;
        this.sharedSteps = b2bSteps.getSharedSteps();
        this.paperTrackerClient = paperTrackerClient;
        this.paperTrackerSchemaValidatorProxy = paperTrackerSchemaValidatorProxy;
    }

    private List<NotificationEvent> provideAnalogProgressAndFeedbackElement(FullSentNotificationV29 fullSentNotification, int attempt) {
        Predicate<TimelineElementV28> predicate;
        if (trackingKeys.get(0).contains(PREPARE_SIMPLE_REGISTERED_LETTER)) {
            predicate = te -> te.getElementId().contains(SEND_SIMPLE_REGISTERED_LETTER_PROGRESS);
        } else {
            predicate = te -> te.getElementId().contains("ATTEMPT_" + attempt) &&
                    (te.getElementId().contains("SEND_ANALOG_PROGRESS") ||
                            te.getElementId().contains("SEND_ANALOG_FEEDBACK"));
        }
        return fullSentNotification.getTimeline().stream()
                .filter(predicate)
                .map(te -> te.getDetails())
                .map(td -> new NotificationEvent(td.getDeliveryDetailCode(), createAttachmentUrls(td.getAttachments(), AttachmentDetails::getUrl), td.getDeliveryFailureCause()))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    private void assertSameElements(List<NotificationEvent> list1, List<NotificationEvent> list2, String errorMessage) {
        if (list1 == null || list2 == null) {
            Assertions.assertEquals(list1, list2, errorMessage);
            return;
        }
        list1.sort(Comparator.comparing(NotificationEvent::getDeliveryDetailCode).thenComparing(attach -> String.join(",", attach.getAttachmentUrlName())));
        list2.sort(Comparator.comparing(NotificationEvent::getDeliveryDetailCode).thenComparing(attach -> String.join(",", attach.getAttachmentUrlName())));
        Assertions.assertEquals(list1, list2, errorMessage);
    }

    private void assertRelaxedSameElements(List<NotificationEvent> list1, List<NotificationEvent> list2, String errorMessage) {
        if (list1 == null || list2 == null) {
            Assertions.assertEquals(list1, list2, errorMessage);
            return;
        }
        list1.sort(Comparator.comparing(NotificationEvent::getDeliveryDetailCode).thenComparing(attach -> String.join(",", attach.getAttachmentUrlName())));
        list2.sort(Comparator.comparing(NotificationEvent::getDeliveryDetailCode).thenComparing(attach -> String.join(",", attach.getAttachmentUrlName())));
        assertTrue(() -> {
            if (list1.size() != list2.size()) {
                return false;
            }
            for (int i = 0; i < list1.size(); i++) {
                if (!list1.get(i).equalsRelaxed(list2.get(i))) {
                    return false;
                }
            }
            return true;
        }, errorMessage + " - expected: " + list2 + " but was: " + list1);
    }

    @Then("si verifica che la risposta tracking per la sequence {string} contenga tutti gli elementi attesi e che sia strutturalmente valida")
    public void verifyTrackingEventsForSequenceWithPCRetryNew(String sequenceName) {
        TrackingsRequest request = new TrackingsRequest().trackingIds(trackingKeys);
        Map<Integer, List<NotificationEvent>> expectedEvents = eventTimelineParser.parse(PaperTrackerTrackingSequence.getByName(sequenceName).getEvents());

        await().atMost(Duration.ofMinutes(5))
                .pollInterval(Duration.ofSeconds(5))
                .untilAsserted(() -> {
                    responseTracking = paperTrackerClient.retrieveTrackerEvents(request);

                    Map<Integer, List<NotificationEvent>> groupedTrackingByAttempt = responseTracking.getTrackings().stream()
                            .filter(t -> t.getEvents() != null)
                            .collect(Collectors.toMap(
                                    att -> {
                                        String attId = att.getAttemptId();
                                        int index = attId != null ? attId.lastIndexOf("_") : -1;
                                        if (index != -1) {
                                            try {
                                                return Integer.parseInt(attId.substring(index + 1));
                                            } catch (NumberFormatException ignored) {
                                            }
                                        }
                                        return 0;
                                    },
                                    t -> t.getEvents().stream()
                                            .map(pe -> new NotificationEvent(pe.getStatusCode(), createAttachmentUrls(pe.getAttachments(), Attachment::getUri), pe.getDeliveryFailureCause()))
                                            .collect(Collectors.toList()),
                                    (existing, newList) -> {
                                        existing.addAll(newList);
                                        return existing;
                                    }
                            ));

                    Set<Integer> allAttempts = new HashSet<>();
                    allAttempts.addAll(groupedTrackingByAttempt.keySet());
                    allAttempts.addAll(expectedEvents.keySet());

                    if (expectedEvents.keySet().size() <= 1 && expectedEvents.containsKey(0) && groupedTrackingByAttempt.keySet().size() > 1) {
                        List<NotificationEvent> allActualEvents = groupedTrackingByAttempt.values().stream()
                                .flatMap(List::stream)
                                .collect(Collectors.toList());
                        List<NotificationEvent> allExpectedEvents = expectedEvents.getOrDefault(0, Collections.emptyList());
                        assertRelaxedSameElements(allActualEvents, allExpectedEvents, TRACKINGS_ELEMENT_NOT_FOUND);
                    } else {
                        for (Integer attemptIndex : allAttempts) {
                            List<NotificationEvent> actualList = groupedTrackingByAttempt.getOrDefault(attemptIndex, Collections.emptyList());
                            List<NotificationEvent> expectedList = expectedEvents.getOrDefault(attemptIndex, Collections.emptyList());
                            if (!actualList.isEmpty() || !expectedList.isEmpty()) {
                                assertRelaxedSameElements(actualList, expectedList, TRACKINGS_ELEMENT_NOT_FOUND);
                            }
                        }
                    }
                });

        TrackingsResponse validTrackingsResponse = new TrackingsResponse();
        validTrackingsResponse.setTrackings(responseTracking.getTrackings().stream()
                .filter(t -> t.getEvents() != null && !t.getEvents().isEmpty())
                .collect(Collectors.toList()));
        verifyTrackingResponseStructure(validTrackingsResponse, "it/pagopa/pn/cucumber/paperTracker/schemaValidators/tracking-response-schema.json", sequenceName);
    }

    @And("genera la key da utilizzare per invocare l'API per il prodotto: {string}")
    public void generateTrackingIdForProduct(String productType) {
        String key = productType.equals("RS") ? PREPARE_SIMPLE_REGISTERED_LETTER : PREPARE_ANALOG_DOMICILE;
        FullSentNotificationV29 fullSentNotification = sharedSteps.getSentNotificationLastVersionByIun(sharedSteps.getNotificationIun());
        List<String> keys = fullSentNotification.getTimeline().stream()
                .map(TimelineElementV28::getElementId)
                .filter(e -> e.contains(key) && (e.contains(".ATTEMPT_0") || !e.contains(".ATTEMPT_")))
                .flatMap(prepare -> Stream.of(prepare + ".PCRETRY_0", prepare + ".PCRETRY_1", prepare + ".PCRETRY_2", prepare + ".PCRETRY_3", prepare + ".PCRETRY_4"))
                .toList();
        if (keys.isEmpty()) {
            String iun = sharedSteps.getNotificationIun();
            trackingKeys = Stream.of(
                    key + ".IUN_" + iun + ".RECINDEX_0.ATTEMPT_0.PCRETRY_0",
                    key + ".IUN_" + iun + ".RECINDEX_0.ATTEMPT_0.PCRETRY_1",
                    key + ".IUN_" + iun + ".RECINDEX_0.ATTEMPT_0.PCRETRY_2",
                    key + ".IUN_" + iun + ".RECINDEX_0.ATTEMPT_0.PCRETRY_3",
                    key + ".IUN_" + iun + ".RECINDEX_0.ATTEMPT_0.PCRETRY_4"
            ).toList();
        } else {
            trackingKeys = keys;
        }
    }

    @Then("si verifica che gli eventi presenti in PaperTrackerDryRunOutputs coincidano con la timeline per la sequence: {string}")
    public void checkPaperTrackerEventsNew(String sequenceName) {
        TrackingsRequest request = new TrackingsRequest();
        request.setTrackingIds(trackingKeys);
        PaperTrackerOutputsResponse responseOutput = paperTrackerClient.retrieveTrackerOutputs(request);

        FullSentNotificationV29 fullSentNotification = sharedSteps.getSentNotificationLastVersion();
        assertThat(fullSentNotification).isNotNull();

        //recupera tutte gli elementi prepare che sono presenti in timeline
        List<String> analogEventIds = fullSentNotification.getTimeline().stream()
                .filter(e -> e.getElementId().contains(trackingKeys.get(0).split("\\.IUN")[0]))
                .map(TimelineElementV28::getElementId)
                .toList();

        assertThat(analogEventIds).isNotEmpty();
        assertThat(responseOutput.getResults()).isNotEmpty();

        Map<Integer, List<NotificationEvent>> expectedTimelineEvents = buildTimelineEventsMap(fullSentNotification, analogEventIds);
        Map<Integer, List<NotificationEvent>> actualOutputEvents = buildOutputEventsMap(responseOutput);

        validateEventsMaps(expectedTimelineEvents, actualOutputEvents, sequenceName);
    }

    /**
     * Crea una mappa con chiave l'indice dei vari elementi di prepare presenti in timeline
     * e come valore aggiunge la lista di tutti gli elementi relativi a quel attempt
     *
     * @param notification
     * @param analogEventIds
     * @return
     */
    private Map<Integer, List<NotificationEvent>> buildTimelineEventsMap(FullSentNotificationV29 notification, List<String> analogEventIds) {
        Map<Integer, List<NotificationEvent>> map = new HashMap<>();
        for (int i = 0; i < analogEventIds.size(); i++) {
            map.put(i, provideAnalogProgressAndFeedbackElement(notification, i));
        }
        return map;
    }

    /**
     * Crea una mappa con chiave i vari PCRETRY_0 presenti nella risposta della API /outputs
     * e come valore aggiunge una lista di oggetti che corrispondono a quelli ritornati in risposta
     * e convertiti nell'oggetto di utility NotificationEvent
     *
     * @param responseOutput
     * @return
     */
    private Map<Integer, List<NotificationEvent>> buildOutputEventsMap(PaperTrackerOutputsResponse responseOutput) {
        Map<Integer, List<NotificationEvent>> map = responseOutput.getResults().stream().collect(Collectors.groupingBy(
                r -> {
                    Pattern pattern = Pattern.compile("ATTEMPT_(\\d+)");
                    Matcher matcher = pattern.matcher(r.getTrackingId());
                    return (matcher.find()) ? Integer.parseInt(matcher.group(1)) : 0;
                }, Collectors.flatMapping(
                        r -> r.getOutputs().stream()
                                .map(output -> new NotificationEvent(
                                        output.getStatusDetail(),
                                        createAttachmentUrls(output.getAttachments(), Attachment::getUri),
                                        output.getDeliveryFailureCause()
                                )), Collectors.toList()
                )
        ));
        return map;
    }

    private void validateEventsMaps(Map<Integer, List<NotificationEvent>> expectedEvents,
                                    Map<Integer, List<NotificationEvent>> actualEvents,
                                    String sequenceName) {
        List<String> excludedCodes = getExcludedCodesForSequence(sequenceName);

        for (Integer attempt : expectedEvents.keySet()) {
            List<NotificationEvent> filteredExpected = sanitizeList(expectedEvents.get(attempt), excludedCodes);
            List<NotificationEvent> actualForAttempt = actualEvents.get(attempt);

            assertThat(actualForAttempt).isNotNull();
            assertSameElements(filteredExpected, actualForAttempt, OUTPUTS_RESPONSE_ELEMENT_NOT_FOUND);
        }
    }

    private List<String> getExcludedCodesForSequence(String sequenceName) {
        if (List.of("OK_AR_INVALID_DATETIME", "OK_AR_NO_EVENT_B", "OK_AR_TIMESTAMP_ERR",
                "OK_RIR_TIMESTAMP_ERR", "OK_RIR_INVALID_DATETIME").contains(sequenceName)) {
            return List.of("RECRN001C", "RECRI003C");
        }
        return List.of("CON018");
    }

    private <T> void verifyTrackingResponseStructure(T response, String schemaPath, String sequenceName) {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.valueToTree(response);
        paperTrackerSchemaValidatorProxy.provide(sequenceName).validate(jsonNode, schemaPath);
    }

    private List<NotificationEvent> sanitizeList(List<NotificationEvent> list, List<String> deliveryDetailsList) {
        return list.stream().filter(item -> !deliveryDetailsList.contains(item.getDeliveryDetailCode())).collect(Collectors.toCollection(ArrayList::new));
    }

    private <T> List<String> createAttachmentUrls(List<T> attachments, Function<T, String> extractor) {
        return Optional.ofNullable(attachments)
                .orElse(List.of())
                .stream()
                .map(extractor)
                .toList();
    }

    @And("si verifica che non ci siano {word} per i trackingId richiesti")
    public void checkNoDataGeneric(String type) {
        TrackingsRequest request = new TrackingsRequest();
        request.setTrackingIds(trackingKeys);
        switch (type.toLowerCase()) {
            case "errori" -> {
                TrackingErrorsResponse errorsResponse = paperTrackerClient.retrieveTrackerErrors(request);
                assertNoData(errorsResponse.getResults(), res -> res.getErrors(),
                        "Non ci devono essere errori per i trackingId richiesti");
            }
            case "outputs" -> {
                PaperTrackerOutputsResponse outputsResponse = paperTrackerClient.retrieveTrackerOutputs(request);
                assertNoData(outputsResponse.getResults(), res -> res.getOutputs(),
                        "Non ci devono essere dati per i trackingId richiesti");
            }
            default -> throw new IllegalArgumentException("Tipo sconosciuto: " + type);
        }
    }

    private <T, R> void assertNoData(List<T> results, Function<T, List<R>> extractor, String message) {
        boolean hasData = results.stream()
                .map(extractor)
                .filter(Objects::nonNull)
                .anyMatch(list -> !list.isEmpty());

        assertThat(hasData).as(message).isFalse();
    }

    @Then("si verifica che su PaperTrackingsError ci sia un errore del seguente tipo: {string}")
    public void checkTrackingErrorsNew(String expectedError) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode expectedResult = (ObjectNode) objectMapper.readTree(expectedError);
        String trackingId = expectedResult.get("trackingId").asText().replace("<iun>", sharedSteps.getNotificationIun());
        String expectedErrorCategory = expectedResult.get("errorCategory").asText();

        TrackingsRequest request = new TrackingsRequest();
        request.setTrackingIds(trackingKeys);
        AtomicReference<TrackingError> atomicReference = new AtomicReference<>();
        await().atMost(Duration.ofMinutes(25))
                .pollInterval(Duration.ofSeconds(5))
                .untilAsserted(() -> {
                    // recupera la lista di errori e cerca quello che ha category e flowThrow uguali a quelli attesi, se lo trova lo setta nell'atomic reference
                    // altrimenti, l'atomic reference rimane null e l'assert fallisce, facendo riprovare fino a quando non viene trovato o non scade il timeout
                    TrackingErrorsResponse errorsResponse = paperTrackerClient.retrieveTrackerErrors(request);
                    errorsResponse.getResults().stream()
                            .flatMap(r -> r.getErrors().stream())
                            .filter(e -> e.getTrackingId().equals(trackingId))
                            .filter(e -> e.getErrorCategory().equals(expectedErrorCategory))
                            .findFirst()
                            .ifPresent(e -> atomicReference.set(e));
                    assertThat(atomicReference.get()).as("Non è stato trovato nessun errore per trackingId " + trackingId).isNotNull();
                });
        TrackingError trackingError = atomicReference.get();
        ObjectNode actualResult = objectMapper.valueToTree(trackingError);

        assertErrorResponseFields(expectedResult, actualResult);
    }

    private void assertErrorResponseFields(ObjectNode expected, ObjectNode actual) {
        assertThat(actual.get("created").asText()).isNotNull().satisfies(OffsetDateTime::parse);
        assertThat(expected.get("errorCategory").asText()).isEqualTo(actual.get("errorCategory").asText());

        //details
        assertThat(expected.at("/details/cause")).isEqualTo(actual.at("/details/cause"));
        JsonNode expectedMessageNode = expected.at("/details/message");
        if (!expectedMessageNode.isMissingNode()) {
            String expectedMessage = expectedMessageNode.asText().replaceAll("<iun>", sharedSteps.getNotificationIun());
            String actualMessage = actual.at("/details/message").asText().replaceAll("\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}Z", "<date>");
            assertThat(expectedMessage).isEqualTo(actualMessage);
        }

        JsonNode expectedAdditionalDetails = expected.at("/details/additionalDetails");
        if (!expectedAdditionalDetails.isEmpty()) {
            JsonNode actualAdditionalDetails = actual.at("/details/additionalDetails");
            if (isAdditionalDetailsNested(expectedAdditionalDetails)) {
                // Se additionalDetails è nested, prendi il primo field name e usa il validatore corrispondente
                String validatorKey = expectedAdditionalDetails.fieldNames().next();
                AdditionalDetailsValidator validator = VALIDATORS.get(validatorKey);
                assertThat(validator).as("Non è stato definito nessun validatore per: " + validatorKey).isNotNull();
                validator.validate(actualAdditionalDetails, expectedAdditionalDetails);
            } else {
                // Se additionalDetails è flat, usa il validatore flat per confrontare tutti i campi
                AdditionalDetailsValidator flatValidator = VALIDATORS.get("flatAdditionalDetails");
                assertThat(flatValidator).as("Validatore flat non trovato").isNotNull();
                flatValidator.validate(actualAdditionalDetails, expectedAdditionalDetails);
            }
        }

        if (expected.get("flowThrow") == null) assertThat(actual.get("flowThrow") == null).isTrue();
        else assertThat(expected.get("flowThrow").asText()).isEqualTo(actual.get("flowThrow").asText());
        assertThat(expected.get("eventThrow").asText()).isEqualTo(actual.get("eventThrow").asText());
        assertThat(actual.get("eventIdThrow").asText()).isNotNull();
        assertThat(expected.get("productType").asText()).isEqualTo(actual.get("productType").asText());
        assertThat(expected.get("type").asText()).isEqualTo(actual.get("type").asText());
    }

    private boolean isAdditionalDetailsNested(JsonNode additionalDetails) {
        if (additionalDetails.isEmpty()) {
            return false;
        }
        String firstFieldName = additionalDetails.fieldNames().next();
        return VALIDATORS.containsKey(firstFieldName) && !firstFieldName.equals("flatAdditionalDetails");
    }

    @Then("si controlla che non ci siano eventi duplicati")
    public void verifyNotDuplicatedEventArePresent() {
        FullSentNotificationV29 fullSentNotification = sharedSteps.getSentNotificationLastVersion();
        List<TimelineElementV28> timelineElements = fullSentNotification.getTimeline();
        long result = 0;
        for (TimelineElementV28 timelineElement : timelineElements) {
            result = timelineElements.stream()
                    .filter(te -> Objects.equals(te.getElementId(), timelineElement.getElementId()))
                    .filter(te -> Objects.equals(te.getTimestamp(), timelineElement.getTimestamp()))
                    .filter(te -> Objects.equals(te.getCategory().getValue(), timelineElement.getCategory().getValue()))
                    .filter(te -> Objects.equals(
                            Optional.ofNullable(te.getDetails()).map(TimelineElementDetailsV28::getDeliveryDetailCode).orElse(null),
                            Optional.ofNullable(timelineElement.getDetails()).map(TimelineElementDetailsV28::getDeliveryDetailCode).orElse(null))
                    )
                    .filter(te -> Objects.equals(
                            Optional.ofNullable(te.getDetails()).map(TimelineElementDetailsV28::getAttachments).map(List::size).orElse(null),
                            Optional.ofNullable(timelineElement.getDetails()).map(TimelineElementDetailsV28::getAttachments).map(List::size).orElse(null))
                    )
                    .filter(te -> Objects.equals(
                            Optional.ofNullable(te.getDetails()).map(TimelineElementDetailsV28::getAttachments).map(List::size).orElse(null),
                            Optional.ofNullable(timelineElement.getDetails()).map(TimelineElementDetailsV28::getAttachments).map(List::size).orElse(null))
                    )
                    .filter(te -> verifySameAttachments(
                            Optional.ofNullable(te.getDetails()).map(TimelineElementDetailsV28::getAttachments).orElse(null),
                            Optional.ofNullable(timelineElement.getDetails()).map(TimelineElementDetailsV28::getAttachments).orElse(null))
                    )
                    .filter(te -> Objects.equals(
                            Optional.ofNullable(te.getDetails()).map(TimelineElementDetailsV28::getDeliveryFailureCause).orElse(null),
                            Optional.ofNullable(timelineElement.getDetails()).map(TimelineElementDetailsV28::getDeliveryFailureCause).orElse(null))
                    )
                    .count();
            assertThat(result).as("Nella timeline sono stati riscontrati dei duplicati per l'elemento " + timelineElement.getElementId())
                    .isLessThanOrEqualTo(1);
        }
    }

    private boolean verifySameAttachments(List<AttachmentDetails> list1, List<AttachmentDetails> list2) {
        if (list1 == null && list2 == null) return true;
        if (list1 == null || list2 == null) return false;
        Comparator<AttachmentDetails> comparator = Comparator.comparing(AttachmentDetails::getId);
        list1.sort(comparator);
        list2.sort(comparator);
        return list1.equals(list2);
    }

    @Then("si controlla che siano presenti tutti gli eventi relativi alla sequence {string}")
    public void checkSequenceEventsOnPaperTracker(String sequenceName) {
        log.info("Creata notifica con " + sequenceName + "e IUN: " + sharedSteps.getNotificationIun());
        TimelineSequence timelineSequence = TimelineSequence.getByName(sequenceName);
        assertThat(timelineSequence).as("Sequence inesistente: " + sequenceName).isNotNull();
        for (String eventString : timelineSequence.getEvents()) {
            Map<String, String> data = buildDataMap(eventString);
            Matcher m = Pattern.compile("_COUNT_(\\d+)").matcher(eventString);
            if (m.find()) {
                b2bSteps.checkNumberOfTimelineElementsWithCategoryFromMap(data.get("eventCategory"), Integer.parseInt(m.group(1)), data);
            } else {
                b2bSteps.checkIfTimelineElementExists(data.get("eventCategory"), true, data);
            }
        }
    }

    private Map<String, String> buildDataMap(String eventString) {
        Map<String, String> data = new HashMap<>();
        Matcher m = Pattern.compile("_ATTEMPT_(\\d+)").matcher(eventString);
        if (m.find()) data.put("details_sentAttemptMade", m.group(1));
        int openBracketIndex = eventString.indexOf("[");
        String deliveryDetailCode = eventString.replaceAll("_ATTEMPT_\\d+", "").replaceAll("_COUNT_\\d+", "");
        String failureCause = null;
        String docType = null;
        if (openBracketIndex > -1) {
            int closeBracketIndex = eventString.indexOf("]");
            String propertySection = eventString.substring(openBracketIndex + 1, closeBracketIndex);
            deliveryDetailCode = eventString.substring(0, openBracketIndex);
            String[] keyValueProperties = propertySection.split(";");
            for (String property : keyValueProperties) {
                String[] pair = property.split(":");
                String key = pair[0];
                String value = pair[1];
                if (key.equalsIgnoreCase("FAILCAUSE")) {
                    failureCause = value;
                } else if (key.equalsIgnoreCase("DOC")) {
                    docType = value;
                }
            }
        }
        data.put("loadTimeline", "false");
        data.put("details_recIndex", "0");
        data.put("details", "NOT_NULL");
        data.put("details_deliveryDetailCode", deliveryDetailCode);
        String eventCategory = trackingKeys.get(0).contains(PREPARE_SIMPLE_REGISTERED_LETTER) ? SEND_SIMPLE_REGISTERED_LETTER_PROGRESS
                : TimelineSequence.isFeedback(deliveryDetailCode) ? SEND_ANALOG_FEEDBACK : SEND_ANALOG_PROGRESS;
        data.put("eventCategory", eventCategory);
        if (failureCause != null) {
            data.put("details_failureCause", failureCause);
        }
        return data;
    }

    @And("si verifica che la risposta dell'API attempts contenga finalDematFound e paperDeliveryTimestamp")
    public void verifyAttemptsResponse() {
        String attemptId = sharedSteps.getSentNotificationLastVersion().getTimeline().stream().filter(e ->
                e.getElementId().contains(PREPARE_ANALOG_DOMICILE)).map(TimelineElementV28::getElementId).findAny().orElseThrow();
        TrackingsResponse trackingsResponse = paperTrackerClient.retrieveTrackingsByAttemptId(attemptId, null);
        trackingsResponse.getTrackings().sort(Comparator.comparing(Tracking::getTrackingId));
        assertNotNull(trackingsResponse.getTrackings());
        assertNotNull(trackingsResponse.getTrackings().get(0));
        assertNotNull(trackingsResponse.getTrackings().get(1));
        assertNotNull(trackingsResponse.getTrackings().get(0).getTrackingId().contains("PCRETRY_0"));
        assertNotNull(trackingsResponse.getTrackings().get(1).getTrackingId().contains("PCRETRY_1"));
        if (trackingsResponse.getTrackings().size() == 3) {
            assertNotNull(trackingsResponse.getTrackings().get(2).getTrackingId().contains("PCRETRY_2"));
        }
        int lastPcRetryIndex = trackingsResponse.getTrackings().size() - 1;
        assertNotNull(trackingsResponse.getTrackings().get(lastPcRetryIndex).getPaperStatus());
        assertNotNull(trackingsResponse.getTrackings().get(lastPcRetryIndex).getPaperStatus().getFinalDematFound());

        String consolidatorHandlingTimestamp = trackingsResponse.getTrackings().get(lastPcRetryIndex).getEvents().stream().filter(e -> e.getStatusCode().equals("P000")).map(PaperEvent::getStatusTimestamp).findFirst().orElse(null);
        Assertions.assertEquals(consolidatorHandlingTimestamp, trackingsResponse.getTrackings().get(lastPcRetryIndex).getPaperStatus().getPaperDeliveryTimestamp());
    }

    @And("si verifica che non sia presente nessun retry per il tracking")
    public void verifyNoRetryTriggered() {
        TrackingsRequest request = new TrackingsRequest().trackingIds(trackingKeys);
        TrackingsResponse response = paperTrackerClient.retrieveTrackerEvents(request);
        boolean hasRetry = response != null && response.getTrackings() != null && response.getTrackings().stream()
                .anyMatch(t -> t.getTrackingId() != null && !t.getTrackingId().endsWith(".PCRETRY_0"));
        assertThat(hasRetry).as("Non deve essere generato alcun retry per la notifica").isFalse();
    }

    @Then("si verifica che la notifica rimanga bloccata in stato DELIVERING al superamento del max retry")
    public void verifyNotificationBlockedInDeliveringOnMaxRetry() {
        await().atMost(Duration.ofMinutes(25))
                .pollInterval(Duration.ofSeconds(15))
                .ignoreExceptionsMatching(e -> e instanceof org.springframework.web.client.RestClientException)
                .untilAsserted(() -> {
                    FullSentNotificationV29 fullSentNotification = sharedSteps.getSentNotificationLastVersion();
                    assertThat(fullSentNotification).isNotNull();
                    assertThat(fullSentNotification.getNotificationStatus())
                            .as("Lo stato della notifica deve essere DELIVERING")
                            .isEqualTo(NotificationStatusV26.DELIVERING);

                    List<TimelineElementV28> timeline = fullSentNotification.getTimeline();
                    assertThat(timeline).as("Timeline non deve essere nulla").isNotNull();

                    // 1. Verificare che non sia presente REFINEMENT né SEND_ANALOG_FEEDBACK
                    boolean hasRefinementOrFeedback = timeline.stream().anyMatch(te ->
                            REFINEMENT.equals(te.getCategory().getValue()) ||
                            SEND_ANALOG_FEEDBACK.equals(te.getCategory().getValue())
                    );
                    assertThat(hasRefinementOrFeedback)
                            .as("La notifica non deve contenere eventi di FEEDBACK o REFINEMENT")
                            .isFalse();

                    // 2. Verificare che su pn-Timelines siano presenti tutti gli eventi di failure dei 5 tentativi:
                    // Attempt 0: RECRN002C (M10)
                    // Retry 1: RECRN006 (Furto/Smarrimento F01)
                    // Retry 2: RECRN013 (Non rendicontabile)
                    // Retry 3: RECRN006 (Furto/Smarrimento F01)
                    // Retry 4: RECRN002C (M10 - 5° tentativo finale che esaurisce i retry)
                    List<String> progressDeliveryCodes = timeline.stream()
                            .filter(te -> SEND_ANALOG_PROGRESS.equals(te.getCategory().getValue()))
                            .map(te -> te.getDetails() != null ? te.getDetails().getDeliveryDetailCode() : null)
                            .filter(Objects::nonNull)
                            .toList();

                    long recrn002cCount = progressDeliveryCodes.stream().filter("RECRN002C"::equals).count();
                    long recrn006Count = progressDeliveryCodes.stream().filter("RECRN006"::equals).count();
                    long recrn0013Count = progressDeliveryCodes.stream().filter("RECRN013"::equals).count();

                    assertThat(recrn002cCount)
                            .as("Devono essere presenti 2 eventi RECRN002C (Attempt 0 e Retry 4)")
                            .isEqualTo(2);
                    assertThat(recrn006Count)
                            .as("Devono essere presenti 2 eventi RECRN006 (Retry 1 e Retry 3)")
                            .isEqualTo(2);
                    assertThat(recrn0013Count)
                            .as("Deve essere presente 1 evento RECRN013 (Retry 2)")
                            .isEqualTo(1);
                });

        // 3. Verificare che l'ultimo tracking (PCRETRY_4) su PaperTracker sia in stato KO
        TrackingsRequest request = new TrackingsRequest().trackingIds(trackingKeys);
        TrackingsResponse response = paperTrackerClient.retrieveTrackerEvents(request);
        assertThat(response).as("La risposta di PaperTracker non deve essere nulla").isNotNull();
        assertThat(response.getTrackings()).as("I trackings non devono essere vuoti").isNotEmpty();

        Tracking lastTracking = response.getTrackings().stream()
                .filter(t -> t.getTrackingId() != null && t.getTrackingId().endsWith(".PCRETRY_4"))
                .findFirst()
                .orElse(null);

        assertThat(lastTracking)
                .as("Il tracking relativo a PCRETRY_4 deve essere presente")
                .isNotNull();

        assertThat(lastTracking.getState())
                .as("L'ultimo tracking al raggiungimento del max retry deve terminare in stato KO (attualmente risulta %s)", lastTracking.getState())
                .isEqualTo(Tracking.StateEnum.KO);
    }

    @io.cucumber.java.en.Given("viene riutilizzata la notifica con iun {string} per il prodotto: {string}")
    public void reuseNotificationByIun(String iun, String productType) {
        sharedSteps.setNotificationIun(iun);
        generateTrackingIdForProduct(productType);
    }
}