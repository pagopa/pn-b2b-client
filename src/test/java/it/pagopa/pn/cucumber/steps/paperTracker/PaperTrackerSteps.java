package it.pagopa.pn.cucumber.steps.paperTracker;

import io.cucumber.java.ParameterType;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import it.pagopa.pn.client.b2b.generated.openapi.clients.papertracker.model.*;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.AttachmentDetails;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.FullSentNotificationV27;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementDetailsV27;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementV27;
import it.pagopa.pn.client.b2b.pa.service.IPnPaperTrackerClient;
import it.pagopa.pn.cucumber.steps.SharedSteps;
import it.pagopa.pn.cucumber.steps.pa.AvanzamentoNotificheB2bSteps;
import it.pagopa.pn.cucumber.steps.paperTracker.domain.NotificationEvent;
import it.pagopa.pn.cucumber.steps.paperTracker.parser.EventTimelineParser;
import it.pagopa.pn.cucumber.steps.utilitySteps.PaperTrackerErrorCategory;
import it.pagopa.pn.cucumber.steps.utilitySteps.PaperTrackerTrackingSequence;
import it.pagopa.pn.cucumber.steps.utilitySteps.TimelineSequence;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static it.pagopa.pn.cucumber.steps.utilitySteps.Costanti.*;
import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
public class PaperTrackerSteps {
    private static final String TRACKINGS_ELEMENT_NOT_FOUND = "La risposta di /trackings non contiene tutti gli elementi presenti in timeline!";
    private static final String OUTPUTS_RESPONSE_ELEMENT_NOT_FOUND = "La risposta di /outputs non contiene tutti gli elementi previsti che sono presenti in timeline!";

    private final EventTimelineParser eventTimelineParser;
    private final AvanzamentoNotificheB2bSteps b2bSteps;
    private final SharedSteps sharedSteps;
    private final IPnPaperTrackerClient paperTrackerClient;

    @Autowired
    public PaperTrackerSteps(EventTimelineParser eventTimelineParser,
                             AvanzamentoNotificheB2bSteps b2bSteps,
                             IPnPaperTrackerClient paperTrackerClient) {
        this.eventTimelineParser = eventTimelineParser;
        this.b2bSteps = b2bSteps;
        this.sharedSteps = b2bSteps.getSharedSteps();
        this.paperTrackerClient = paperTrackerClient;
    }

    private List<NotificationEvent> provideAnalogProgressAndFeedbackElement(FullSentNotificationV27 fullSentNotification, int attempt) {
         return fullSentNotification.getTimeline().stream()
                 .filter(te -> te.getElementId().contains("ATTEMPT_" + attempt) && (te.getElementId().contains("SEND_ANALOG_PROGRESS") || te.getElementId().contains("SEND_ANALOG_FEEDBACK")))
                 .map(te -> te.getDetails())
                 .map(td -> new NotificationEvent(td.getDeliveryDetailCode(), createAttachmentUrl(td.getAttachments()), td.getDeliveryFailureCause()))
                 .collect(Collectors.toCollection(ArrayList::new));
    }

    private void assertSameElements(List<NotificationEvent> list1, List<NotificationEvent> list2, String errorMessage) {
        list1.sort(Comparator.comparing(NotificationEvent::getDeliveryDetailCode).thenComparing(attach -> String.join(",", attach.getAttachmentUrlName())));
        list2.sort(Comparator.comparing(NotificationEvent::getDeliveryDetailCode).thenComparing(attach -> String.join(",", attach.getAttachmentUrlName())));
        Assertions.assertEquals(list1, list2, errorMessage);
    }

    private void assertRelaxedSameElements(List<NotificationEvent> list1, List<NotificationEvent> list2, String errorMessage) {
        list1.sort(Comparator.comparing(NotificationEvent::getDeliveryDetailCode).thenComparing(attach -> String.join(",", attach.getAttachmentUrlName())));
        list2.sort(Comparator.comparing(NotificationEvent::getDeliveryDetailCode).thenComparing(attach -> String.join(",", attach.getAttachmentUrlName())));
        Assertions.assertTrue(() -> {
            for (int i = 0; i < list1.size(); i++) {
                if (!list1.get(i).equalsRelaxed(list2.get(i))) {
                    return false;
                }
            }
            return true;
        });
    }


    @Then("si verifica che gli elementi di timeline per la sequence {string} coincidono con quelli su PnPaperTracker, PnPaperTrackerDryRunOutputs con PCRETRY 0, 1, 2")
    public void verifyTrackingEventsForSequenceWithPCRetry(String sequenceName) {
        log.info("Creata notifica con sequence " + sequenceName + "e iun: " + sharedSteps.getNotificationIun());
        FullSentNotificationV27 fullSentNotification = sharedSteps.getSentNotificationLastVersion();
        List<String> stringaTracking = fullSentNotification.getTimeline().stream()
                .map(TimelineElementV27::getElementId)
                .filter(id -> id.contains(PREPARE_ANALOG_DOMICILE))
                .flatMap(prepare -> Stream.of(prepare + ".PCRETRY_0", prepare + ".PCRETRY_1", prepare + ".PCRETRY_2"))
                .collect(Collectors.toList());

        TrackingsRequest request = new TrackingsRequest().trackingIds(stringaTracking);
        TrackingsResponse responseTracking = paperTrackerClient.retrieveTrackerEvents(request);
        PaperTrackerOutputsResponse responseOutput = paperTrackerClient.retrieveTrackerOutputs(request);


        Map<Integer, List<NotificationEvent>> groupedTrackingByAttempt = responseTracking.getTrackings().stream()
                .collect(Collectors.toMap(
                        att -> {
                            int index = att.getAttemptId().lastIndexOf("_");
                            return Integer.parseInt(att.getAttemptId().substring(index + 1));
                        },
                        t -> t.getEvents().stream()
                                .map(pe -> new NotificationEvent(pe.getStatusCode(), createAttachmentUrlTracking(pe.getAttachments()), pe.getDeliveryFailureCause()))
                                .collect(Collectors.toList()),
                        (existing, newList) -> {
                            existing.addAll(newList);
                            return existing;
                        }
                ));

        Map<Integer, List<NotificationEvent>> groupedOutputsByAttempt = responseOutput.getResults().stream()
                .collect(Collectors.toMap(att -> {
                            String trackingId = att.getTrackingId();
                            int attemptIndex = trackingId.lastIndexOf(".ATTEMPT_");
                            int pcRetryIndex = trackingId.lastIndexOf(".PCRETRY_");

                            String numberStr = trackingId.substring(attemptIndex + ".ATTEMPT_".length(), pcRetryIndex);
                            return Integer.parseInt(numberStr);
                        },
                        t -> t.getOutputs().stream()
                                .map(pe -> new NotificationEvent(pe.getStatusDetail(), createAttachmentUrlTracking(pe.getAttachments()), pe.getDeliveryFailureCause()))
                                .collect(Collectors.toList()),
                        (existing, newList) -> {
                            existing.addAll(newList);
                            return existing;
                        }
                ));

        Map<Integer, List<NotificationEvent>> expectedEvents = eventTimelineParser.parse(PaperTrackerTrackingSequence. getByName(sequenceName).getEvents());
        for (int i = 0; i < groupedTrackingByAttempt.keySet().size(); i++ ) {
            assertRelaxedSameElements(groupedTrackingByAttempt.get(i), expectedEvents.get(i), TRACKINGS_ELEMENT_NOT_FOUND);
            List<NotificationEvent> timelineItems = provideAnalogProgressAndFeedbackElement(fullSentNotification, i);
            assertSameElements(sanitizeList(timelineItems, List.of("CON018")), groupedOutputsByAttempt.get(i), OUTPUTS_RESPONSE_ELEMENT_NOT_FOUND);
        }

    }


    @Then("si verifica che gli eventi presenti in PaperTrackerDryRunOutputs coincidano con la timeline per la sequence: {string}")
    public void checkPaperTrackerEvents(String sequenceName) {
        FullSentNotificationV27 fullSentNotification = sharedSteps.getSentNotificationLastVersion();
        List<String> stringaTracking = fullSentNotification.getTimeline().stream().filter(e ->
                e.getElementId().contains(PREPARE_ANALOG_DOMICILE)).map(e -> e.getElementId() + ".PCRETRY_0").toList();

        List<String> analogEventIds = fullSentNotification.getTimeline().stream().filter(e ->
                e.getElementId().contains(PREPARE_ANALOG_DOMICILE)).map(TimelineElementV27::getElementId).toList();

        TrackingsRequest request = new TrackingsRequest();
        request.setTrackingIds(stringaTracking);
        PaperTrackerOutputsResponse responseOutput = paperTrackerClient.retrieveTrackerOutputs(request);

        Map<Integer, List<NotificationEvent>> mapTimeline = new HashMap<>();
        for (int i=0; i < analogEventIds.size(); i++) {
            mapTimeline.put(i, provideAnalogProgressAndFeedbackElement(fullSentNotification, i));
        }

        Map<Integer, List<NotificationEvent>> mapOutput = new HashMap<>();
        responseOutput.getResults().sort(Comparator.comparing(PaperTrackerOutputsResponseResultsInner::getTrackingId));
        for (int j=0; j < responseOutput.getResults().size(); j++) {
            List<NotificationEvent> result2 = responseOutput.getResults().get(j).getOutputs().stream()
                    .map(te -> new NotificationEvent(te.getStatusDetail(), createAttachmentUrlTracking(te.getAttachments()), te.getDeliveryFailureCause()))
                    .collect(Collectors.toCollection(ArrayList::new));
            mapOutput.put(j, result2);
        }

        for (Integer attempt : mapTimeline.keySet()) {
            List<NotificationEvent> filteredOutputs = mapTimeline.get(attempt);
            if (List.of("OK_AR_INVALID_DATETIME", "OK_AR_NO_EVENT_B", "OK_AR_TIMESTAMP_ERR", "OK_RIR_TIMESTAMP_ERR", "OK_RIR_INVALID_DATETIME").contains(sequenceName)) {
                filteredOutputs = sanitizeList(filteredOutputs, List.of("RECRN001C", "RECRI003C"));
            }
            if (List.of("OK_AR_ALL_CON", "OK_GIACENZA_AR_4").contains(sequenceName)) {
                assertSameElements(sanitizeList(filteredOutputs, List.of("CON018")), mapOutput.get(attempt), OUTPUTS_RESPONSE_ELEMENT_NOT_FOUND);
            }
            else {
                assertSameElements(sanitizeList(filteredOutputs, List.of("CON018")), mapOutput.get(attempt), OUTPUTS_RESPONSE_ELEMENT_NOT_FOUND);
            }
        }
    }


    @And("si verifica che la risposta trackings sia uguale a quella attesa {string}")
    public void verifyTrackingResponse(String sequenceName) {
        log.info("Creata notifica con sequence " + sequenceName + "e iun: " + sharedSteps.getNotificationIun());
        FullSentNotificationV27 fullSentNotification = sharedSteps.getSentNotificationLastVersion();
        List<String> stringaTracking = fullSentNotification.getTimeline().stream().filter(e ->
                e.getElementId().contains(PREPARE_ANALOG_DOMICILE)).map(e -> e.getElementId() + ".PCRETRY_0").toList();
        TrackingsRequest request = new TrackingsRequest();
        request.setTrackingIds(stringaTracking);
        TrackingsResponse responseTracking = paperTrackerClient.retrieveTrackerEvents(request);
        Map<Integer, List<NotificationEvent>> mapTracking = new HashMap<>();
        responseTracking.getTrackings().sort(Comparator.comparing(Tracking::getAttemptId));
        for (int j=0; j < responseTracking.getTrackings().size(); j++) {
            Tracking tracking = responseTracking.getTrackings().get(j);
            //REMOVE DUPLICATED EVENTS
            List<PaperEvent> paperEventList = new ArrayList<>(tracking.getEvents().stream()
                    .collect(Collectors.toMap(
                            PaperEvent::getRequestTimestamp,
                            pe -> pe,
                            (pe1, pe2) -> pe1
                    )).values());
            List<NotificationEvent> notificationEventList = paperEventList.stream()
                    .map(te -> new NotificationEvent(te.getStatusCode(), createAttachmentUrlTracking(te.getAttachments()), te.getDeliveryFailureCause()))
                    .collect(Collectors.toCollection(ArrayList::new));
            mapTracking.put(j, notificationEventList);
        }
        Map<Integer, List<NotificationEvent>> expectedEvents = eventTimelineParser.parse(PaperTrackerTrackingSequence. getByName(sequenceName).getEvents());
        for (Integer attempt : mapTracking.keySet()) {
            assertRelaxedSameElements(mapTracking.get(attempt), expectedEvents.get(attempt), TRACKINGS_ELEMENT_NOT_FOUND);
        }
    }

    private List<NotificationEvent> sanitizeList(List<NotificationEvent> list, List<String> deliveryDetailsList) {
        return list.stream().filter(item -> !deliveryDetailsList.contains(item.getDeliveryDetailCode())).collect(Collectors.toCollection(ArrayList::new));
    }

    private List<String> createAttachmentUrlTracking(List<Attachment> attachmentList) {
        return Optional.ofNullable(attachmentList).orElse(List.of()).stream()
                .map(Attachment::getUri)
                .toList();
    }

    private List<String> createAttachmentUrl(List<AttachmentDetails> attachmentList) {
        return Optional.ofNullable(attachmentList).orElse(List.of()).stream()
                .map(AttachmentDetails::getUrl)
                .toList();
    }

    @Then("si verifica che su PaperTrackingsError ci sia un errore con category: {paperTrackerErrorCategory}, flowThrow: {string} per la sequence: {string} e pcRetry: {string}")
    public void checkTrackingErrors(PaperTrackerErrorCategory category, String flowThrow, String sequenceName, String pcRetry) {
        log.info("Creata notifica con sequence " + sequenceName + "e iun: " + sharedSteps.getNotificationIun());
        FullSentNotificationV27 fullSentNotification = sharedSteps.getSentNotificationLastVersion();
        assertThat(fullSentNotification).as("La full sent notification non dev'essere null").isNotNull();

        List<String> analogEventIds = fullSentNotification.getTimeline().stream().filter(e ->
                e.getElementId().contains(PREPARE_ANALOG_DOMICILE)).map(e -> e.getElementId() + ".PCRETRY_" + pcRetry).toList();

        TrackingsRequest request = new TrackingsRequest();
        request.setTrackingIds(analogEventIds);

        TrackingErrorsResponse errorsResponse = paperTrackerClient.retrieveTrackerErrors(request);
        assertThat(errorsResponse).as("La TrackingErrorsResponse non dev'essere null").isNotNull();
        List<TrackingErrorsResponseResultsInner> innerErrorList = errorsResponse.getResults();
        assertThat(innerErrorList).as("La TrackingErrorsResponseResultsInner non dev'essere vuota").isNotEmpty();
        TrackingErrorsResponseResultsInner innerError = innerErrorList.get(0);
        assertThat(innerError).as("L'innerError non dev'essere null").isNotNull();

        List<String> categories = errorsResponse.getResults().stream()
                .flatMap(res -> res.getErrors().stream())
                .map(TrackingError::getErrorCategory)
                .toList();

        List<String> flowThrows = errorsResponse.getResults().stream()
                .flatMap(res -> res.getErrors().stream())
                .map(err -> err.getFlowThrow().getValue())
                .toList();

        Assertions.assertTrue(categories.contains(category.getValue()), String.format("Categoria non trovata:\n%s\nCategorie presenti:\n%s", category.getValue(), categories));
        Assertions.assertTrue(flowThrows.contains(flowThrow), String.format("FlowThrow non trovato:\n%s\nFlowThrow presenti:\n%s", flowThrow, flowThrows));
    }

    @Then("si controlla che non ci siano eventi duplicati")
    public void verifyNotDuplicatedEventArePresent() {
        FullSentNotificationV27 fullSentNotificationV27 = sharedSteps.getSentNotificationLastVersion();
        List<TimelineElementV27> timelineElementV27 = fullSentNotificationV27.getTimeline();
        long result = 0;
        for (TimelineElementV27 timelineElement : timelineElementV27) {
            result = timelineElementV27.stream()
                    .filter(te -> Objects.equals(te.getElementId(), timelineElement.getElementId()))
                    .filter(te -> Objects.equals(te.getTimestamp(), timelineElement.getTimestamp()))
                    .filter(te -> Objects.equals(te.getCategory().getValue(), timelineElement.getCategory().getValue()))
                    .filter(te -> Objects.equals(
                            Optional.ofNullable(te.getDetails()).map(TimelineElementDetailsV27::getDeliveryDetailCode).orElse(null),
                            Optional.ofNullable(timelineElement.getDetails()).map(TimelineElementDetailsV27::getDeliveryDetailCode).orElse(null))
                    )
                    .filter(te -> Objects.equals(
                            Optional.ofNullable(te.getDetails()).map(TimelineElementDetailsV27::getAttachments).map(List::size).orElse(null),
                            Optional.ofNullable(timelineElement.getDetails()).map(TimelineElementDetailsV27::getAttachments).map(List::size).orElse(null))
                    )
                    .filter(te -> Objects.equals(
                            Optional.ofNullable(te.getDetails()).map(TimelineElementDetailsV27::getAttachments).map(List::size).orElse(null),
                            Optional.ofNullable(timelineElement.getDetails()).map(TimelineElementDetailsV27::getAttachments).map(List::size).orElse(null))
                    )
                    .filter(te -> verifySameAttachments(
                            Optional.ofNullable(te.getDetails()).map(TimelineElementDetailsV27::getAttachments).orElse(null),
                            Optional.ofNullable(timelineElement.getDetails()).map(TimelineElementDetailsV27::getAttachments).orElse(null))
                    )
                    .filter(te -> Objects.equals(
                            Optional.ofNullable(te.getDetails()).map(TimelineElementDetailsV27::getDeliveryFailureCause).orElse(null),
                            Optional.ofNullable(timelineElement.getDetails()).map(TimelineElementDetailsV27::getDeliveryFailureCause).orElse(null))
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
            }
            else {
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
        data.put("eventCategory", TimelineSequence.isFeedback(deliveryDetailCode) ? SEND_ANALOG_FEEDBACK : SEND_ANALOG_PROGRESS);
        if (failureCause != null) {
            data.put("details_failureCause", failureCause);
        }
        return data;
    }

    @And("si verifica che la risposta dell'API attempts contenga finalDematFound e paperDeliveryTimestamp")
    public void verifyAttemptsResponse() {
        String attemptId = sharedSteps.getSentNotificationLastVersion().getTimeline().stream().filter(e ->
                e.getElementId().contains(PREPARE_ANALOG_DOMICILE)).map(TimelineElementV27::getElementId).findAny().orElseThrow();
        TrackingsResponse trackingsResponse = paperTrackerClient.retrieveTrackingsByAttemptId(attemptId, null);
        trackingsResponse.getTrackings().sort(Comparator.comparing(Tracking::getTrackingId));
        Assertions.assertNotNull(trackingsResponse.getTrackings());
        Assertions.assertNotNull(trackingsResponse.getTrackings().get(0));
        Assertions.assertNotNull(trackingsResponse.getTrackings().get(1));
        Assertions.assertNotNull(trackingsResponse.getTrackings().get(0).getTrackingId().contains("PCRETRY_0"));
        Assertions.assertNotNull(trackingsResponse.getTrackings().get(1).getTrackingId().contains("PCRETRY_1"));
        if (trackingsResponse.getTrackings().size() == 3) {
            Assertions.assertNotNull(trackingsResponse.getTrackings().get(2).getTrackingId().contains("PCRETRY_2"));
        }
        int lastPcRetryIndex = trackingsResponse.getTrackings().size() - 1;
        Assertions.assertNotNull(trackingsResponse.getTrackings().get(lastPcRetryIndex).getPaperStatus());
        Assertions.assertNotNull(trackingsResponse.getTrackings().get(lastPcRetryIndex).getPaperStatus().getFinalDematFound());

        String consolidatorHandlingTimestamp = trackingsResponse.getTrackings().get(lastPcRetryIndex).getEvents().stream().filter(e -> e.getStatusCode().equals("P000")).map(PaperEvent::getStatusTimestamp).findFirst().orElse(null);
        Assertions.assertEquals(consolidatorHandlingTimestamp, trackingsResponse.getTrackings().get(lastPcRetryIndex).getPaperStatus().getPaperDeliveryTimestamp());
    }

}