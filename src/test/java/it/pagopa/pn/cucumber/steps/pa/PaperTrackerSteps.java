package it.pagopa.pn.cucumber.steps.pa;

import io.cucumber.java.ParameterType;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import it.pagopa.pn.client.b2b.generated.openapi.clients.papertracker.model.*;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.AttachmentDetails;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.FullSentNotificationV27;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementV27;
import it.pagopa.pn.client.b2b.pa.service.IPnPaperTrackerClient;
import it.pagopa.pn.cucumber.steps.SharedSteps;
import it.pagopa.pn.cucumber.steps.utilitySteps.PaperTrackerErrorCategory;
import it.pagopa.pn.cucumber.steps.utilitySteps.PaperTrackerTrackingSequence;
import it.pagopa.pn.cucumber.steps.utilitySteps.TimelineSequence;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static it.pagopa.pn.cucumber.steps.utilitySteps.Costanti.*;
import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
public class PaperTrackerSteps {
    private static final String TRACKINGS_ELEMENT_NOT_FOUND = "La risposta di /trackings non contiene tutti gli elementi presenti in timeline!";
    private static final String OUTPUTS_RESPONSE_ELEMENT_NOT_FOUND = "La risposta di /outputs non contiene tutti gli elementi previsti che sono presenti in timeline!";
    private final AvanzamentoNotificheB2bSteps b2bSteps;
    private final SharedSteps sharedSteps;
    private final IPnPaperTrackerClient paperTrackerClient;

    @Autowired
    public PaperTrackerSteps(AvanzamentoNotificheB2bSteps b2bSteps, IPnPaperTrackerClient paperTrackerClient) {
        this.b2bSteps = b2bSteps;
        this.sharedSteps = b2bSteps.getSharedSteps();
        this.paperTrackerClient = paperTrackerClient;
    }

    private List<NotificationEvent> provideAnalogProgressAndFeedbackElement(FullSentNotificationV27 fullSentNotification, int attempt) {
         return fullSentNotification.getTimeline().stream()
                 .filter(te -> te.getElementId().contains("ATTEMPT_" + attempt) && (te.getElementId().contains("SEND_ANALOG_PROGRESS") || te.getElementId().contains("SEND_ANALOG_FEEDBACK")))
                 .map(te -> te.getDetails())
                 .map(td -> new NotificationEvent(td.getDeliveryDetailCode(), createAttachmentUrl(td.getAttachments())))
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



    @Then("si verifica che gli elementi di timeline coincidono con quelli su PnPaperTracker, PnPaperTrackerDryRunOutputs con PCRETRY 0 e 1")
    public void verify() {
        FullSentNotificationV27 fullSentNotification = sharedSteps.getSentNotificationLastVersion();
        List<String> stringaTracking = fullSentNotification.getTimeline().stream()
                .map(TimelineElementV27::getElementId)
                .filter(id -> id.contains(PREPARE_ANALOG_DOMICILE))
                .findFirst()
                .map(id -> List.of(id + ".PCRETRY_0", id + ".PCRETRY_1"))
                .orElseThrow(() -> new IllegalStateException("No elementId containing " + PREPARE_ANALOG_DOMICILE));

        TrackingsRequest request = new TrackingsRequest().trackingIds(stringaTracking);
        TrackingsResponse responseTracking = paperTrackerClient.retrieveTrackerEvents(request);
        PaperTrackerOutputsResponse responseOutput = paperTrackerClient.retrieveTrackerOutputs(request);

        List<NotificationEvent> timelineItems = provideAnalogProgressAndFeedbackElement(fullSentNotification, 0);
        List<NotificationEvent> trackingItems = responseTracking.getTrackings().stream().flatMap(item -> item.getEvents().stream())
                .map(te -> new NotificationEvent(te.getStatusCode(), createAttachmentUrlTracking(te.getAttachments())))
                .collect(Collectors.toCollection(ArrayList::new));
        List<NotificationEvent> outputsItems = responseOutput.getResults().stream().flatMap(item -> item.getOutputs().stream())
                .map(te -> new NotificationEvent(te.getStatusDetail(), createAttachmentUrlTracking(te.getAttachments())))
                .collect(Collectors.toCollection(ArrayList::new));

        assertSameElements(sanitizeList(timelineItems, List.of("PNRN012")), trackingItems, TRACKINGS_ELEMENT_NOT_FOUND);
        assertSameElements(sanitizeList(timelineItems, List.of("CON018")), outputsItems, OUTPUTS_RESPONSE_ELEMENT_NOT_FOUND);
    }


    @Then("si verifica il corretto salvataggio degli eventi su PnPaperTracker, PnPaperTrackerDryRunOutputs e timeline")
    public void checkPaperTrackerEvents() {
        FullSentNotificationV27 fullSentNotification = sharedSteps.getSentNotificationLastVersion();
        List<String> stringaTracking = fullSentNotification.getTimeline().stream().filter(e ->
                e.getElementId().contains(PREPARE_ANALOG_DOMICILE)).map(e -> e.getElementId() + ".PCRETRY_0").toList();

        List<String> analogEventIds = fullSentNotification.getTimeline().stream().filter(e ->
                e.getElementId().contains(PREPARE_ANALOG_DOMICILE)).map(TimelineElementV27::getElementId).toList();

        TrackingsRequest request = new TrackingsRequest();
        request.setTrackingIds(stringaTracking);
        //TrackingsResponse responseTracking = paperTrackerClient.retrieveTrackerEvents(request);
        PaperTrackerOutputsResponse responseOutput = paperTrackerClient.retrieveTrackerOutputs(request);

        Map<Integer, List<NotificationEvent>> mapTimeline = new HashMap<>();
        for (int i=0; i < analogEventIds.size(); i++) {
            mapTimeline.put(i, provideAnalogProgressAndFeedbackElement(fullSentNotification, i));
        }
/*
        Map<Integer, List<NotificationEvent>> mapTracking = new HashMap<>();
        responseTracking.getTrackings().sort(Comparator.comparing(Tracking::getAttemptId));
        for (int j=0; j < responseTracking.getTrackings().size(); j++) {
            List<NotificationEvent> result2 = responseTracking.getTrackings().get(j).getEvents().stream()
                    .map(te -> new NotificationEvent(te.getStatusCode(), createAttachmentUrlTracking(te.getAttachments())))
                    .collect(Collectors.toCollection(ArrayList::new));
            mapTracking.put(j, result2);
        }
 */

/*     La parte relativa a RIR è stata riprogrammata per future release
       Map<Integer, List<UtilityObject>> mapTrackingRir = new HashMap<>();
        if (responseTracking.getTrackings().stream().anyMatch(ev -> ev.getProductType().getValue().equals("RIR"))) {
            for (int i=0; i < analogEventIds0.size(); i++) {
                TrackingsResponse rirTrackingResponseAttempt = paperTrackerClient.retrieveTrackingsByAttemptId(analogEventIds0.get(i), null);
                List<UtilityObject> result3 = rirTrackingResponseAttempt.getTrackings().get(i).getEvents().stream()
                        .map(tr -> new UtilityObject(tr.getStatusCode(), createAttachmentUrlTracking(tr.getAttachments())))
                        .toList();
                mapTrackingRir.put(i, result3);
            }
        }*/

        Map<Integer, List<NotificationEvent>> mapOutput = new HashMap<>();
        for (int j=0; j < responseOutput.getResults().size(); j++) {
            List<NotificationEvent> result2 = responseOutput.getResults().get(j).getOutputs().stream()
                    .map(te -> new NotificationEvent(te.getStatusDetail(), createAttachmentUrlTracking(te.getAttachments())))
                    .collect(Collectors.toCollection(ArrayList::new));
            mapOutput.put(j, result2);
        }

        for (Integer attempt : mapTimeline.keySet()) {
            //assertSameElements(sanitizeList(groupByDeliveryDetailCode(mapTimeline.get(attempt)), List.of("PNRN012")), mapTracking.get(attempt), TRACKINGS_ELEMENT_NOT_FOUND);
            assertSameElements(sanitizeList(mapTimeline.get(attempt), List.of("CON018")), mapOutput.get(attempt), OUTPUTS_RESPONSE_ELEMENT_NOT_FOUND);
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
            List<NotificationEvent> result2 = responseTracking.getTrackings().get(j).getEvents().stream()
                    .map(te -> new NotificationEvent(te.getStatusCode(), createAttachmentUrlTracking(te.getAttachments())))
                    .collect(Collectors.toCollection(ArrayList::new));
            mapTracking.put(j, result2);
        }
        Map<Integer, List<NotificationEvent>> expectedEvents = parse(PaperTrackerTrackingSequence. getByName(sequenceName).getEvents());
        for (Integer attempt : mapTracking.keySet()) {
            assertRelaxedSameElements(mapTracking.get(attempt), expectedEvents.get(attempt), TRACKINGS_ELEMENT_NOT_FOUND);
        }
    }




    private Map<Integer, List<NotificationEvent>> parse(List<String> rawList) {
        Map<Integer, List<NotificationEvent>> result = new HashMap<>();

        Pattern countPattern = Pattern.compile("(.+?)_COUNT_(\\d+)$");
        Pattern attemptPattern = Pattern.compile("(.+?)_ATTEMPT_(\\d+)$");
        Pattern optionalPattern = Pattern.compile("\\[(.*?)]");

        for (String raw : rawList) {
            List<String> tags = new ArrayList<>();

            // 1. estrai info opzionali tra []
            Matcher optionalMatcher = optionalPattern.matcher(raw);
            if (optionalMatcher.find()) {
                String options = optionalMatcher.group(1);
                for (String part : options.split(";")) {
                    if (part.startsWith("DOC:")) {
                        String value = part.substring(4).trim();
                        if (value.equalsIgnoreCase("7ZIP")) tags.add("safestorage://PN_PRINTED");
                        else if (value.equalsIgnoreCase("Plico") || value.equalsIgnoreCase("Indagine") || value.equalsIgnoreCase("AR"))
                            tags.add("safestorage://PN_EXTERNAL_LEGAL_FACTS-");
                    }
                }
            }

            String base = raw.replaceAll("\\[.*?\\]", "");

            // 3. estrai COUNT
            int count = 1;
            Matcher countMatcher = countPattern.matcher(base);
            if (countMatcher.find()) {
                base = countMatcher.group(1);
                count = Integer.parseInt(countMatcher.group(2));
            }

            // 4. estrai ATTEMPT
            int attempt = 0;
            Matcher attemptMatcher = attemptPattern.matcher(base);
            if (attemptMatcher.find()) {
                base = attemptMatcher.group(1);
                attempt = Integer.parseInt(attemptMatcher.group(2));
            }

            // 5. crea NotificationEvent
            NotificationEvent event = new NotificationEvent(base, tags);

            // 6. aggiungi duplicati nella mappa
            result.computeIfAbsent(attempt, k -> new ArrayList<>())
                    .addAll(Collections.nCopies(count, event));
        }

        return result;
    }







    private List<NotificationEvent> groupByDeliveryDetailCode(List<NotificationEvent> list) {
        return list.stream()
                .collect(Collectors.toMap(
                        NotificationEvent::getDeliveryDetailCode,
                        b -> new ArrayList(b.getAttachmentUrlName()),
                        (list1, list2) -> {
                            list1.addAll(list2);
                            return list1;
                        }
                ))
                .entrySet()
                .stream()
                .map(e -> new NotificationEvent(e.getKey(), e.getValue()))
                .sorted()
                .toList();

    }

    private List<NotificationEvent> sanitizeList(List<NotificationEvent> list, List<String> deliveryDetailsList) {
        return list.stream().filter(item -> !deliveryDetailsList.contains(item.getDeliveryDetailCode())).collect(Collectors.toCollection(ArrayList::new));
    }


    private List<String> createAttachmentUrlTracking(List<Attachment> attachmentList) {
        return Optional.ofNullable(attachmentList).orElse(List.of()).stream()
                .map(att -> att.getUrl())
                .toList();
    }

    private List<String> createAttachmentUrl(List<AttachmentDetails> attachmentList) {
        return Optional.ofNullable(attachmentList).orElse(List.of()).stream()
                .map(att -> att.getUrl())
                .toList();
    }


    @AllArgsConstructor
    @ToString
    @Getter
    private static class NotificationEvent implements Comparable<NotificationEvent> {
        private String deliveryDetailCode;
        private List<String> attachmentUrlName;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof NotificationEvent)) return false;
            NotificationEvent that = (NotificationEvent) o;

            if (!Objects.equals(deliveryDetailCode, that.deliveryDetailCode)) {
                return false;
            }

            List<String> thisList = new ArrayList<>(attachmentUrlName);
            List<String> thatList = new ArrayList<>(that.attachmentUrlName);

            Collections.sort(thisList);
            Collections.sort(thatList);

            return thisList.equals(thatList);
        }

        public boolean equalsRelaxed(NotificationEvent other) {
            if (other == null) return false;

            if (!this.deliveryDetailCode.equals(other.deliveryDetailCode)) {
                return false;
            }

            if (this.attachmentUrlName.size() != other.attachmentUrlName.size()) {
                return false;
            }

            for (String attachmentUrlNameOther : other.attachmentUrlName) {
                boolean found = this.attachmentUrlName.stream()
                        .anyMatch(tagThis -> tagThis.startsWith(attachmentUrlNameOther));
                if (!found) {
                    return false;
                }
            }

            return true;
        }

        @Override
        public int hashCode() {
            List<String> sortedList = new ArrayList<>(attachmentUrlName);
            Collections.sort(sortedList);
            return Objects.hash(deliveryDetailCode, sortedList);
        }

        @Override
        public int compareTo(NotificationEvent other) {
            if (this.deliveryDetailCode == null && other.deliveryDetailCode == null) return 0;
            if (this.deliveryDetailCode == null) return -1;
            if (other.deliveryDetailCode == null) return 1;
            return this.deliveryDetailCode.compareTo(other.deliveryDetailCode);
        }
    }

    @ParameterType("TRACKING_ID_NOT_FOUND|RENDICONTAZIONE_SCARTATA|DATE_ERROR|STATUS_CODE_ERROR|LAST_EVENT_EXTRACTION_ERROR|EMPTY_STRING" +
            "|REGISTERED_LETTER_CODE_ERROR|DELIVERY_FAILURE_CAUSE_ERROR|ATTACHMENTS_ERROR|MAX_RETRY_REACHED_ERROR|OCR_VALIDATION|DUPLICATED_EVENT|NOT_RETRYABLE_EVENT_ERROR")
    public static PaperTrackerErrorCategory paperTrackerErrorCategory(String errorCategory) {
        return PaperTrackerErrorCategory.valueOf(errorCategory.toUpperCase());
    }

    @Then("si verifica il corretto salvataggio dell'errore su PnPaperTrackingsError con category: {paperTrackerErrorCategory} e flowThrow: {string}")
    public void checkTrackingErrors(PaperTrackerErrorCategory category, String flowThrow) {
        FullSentNotificationV27 fullSentNotification = sharedSteps.getSentNotificationLastVersion();
        assertThat(fullSentNotification).as("La full sent notification non dev'essere null").isNotNull();

        List<String> analogEventIds = fullSentNotification.getTimeline().stream().filter(e ->
                e.getElementId().contains(PREPARE_ANALOG_DOMICILE)).map(e -> e.getElementId() + ".PCRETRY_0").toList();

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
                .map(err -> err.getCategory())
                .toList();

        List<String> flowThrows = errorsResponse.getResults().stream()
                .flatMap(res -> res.getErrors().stream())
                .map(err -> err.getFlowThrow().getValue())
                .toList();

        Assertions.assertTrue(categories.contains(category.getValue()), String.format("Categoria non trovata:\n%s\nCategorie presenti:\n%s", category.getValue(), categories));
        Assertions.assertTrue(flowThrows.contains(flowThrow), String.format("FlowThrow non trovato:\n%s\nFlowThrow presenti:\n%s", flowThrow, flowThrows));
    }



    /**
     * L'idea alla base di questo step è:
     * 1) ogni notifica inviata ha una sequence associata
     * 2) recupero tale sequence dal nome
     * 3) per ciascuno degli eventi della sequence:
     * 3.1) se non ha proprietà particolari associate (failureCause, documentType) prendo l'evento così com'è
     * 3.2) in caso contrario splitto la stringa dell'evento per recuperare il valore di tali proprietà
     * 4) costruisco la mappa dei dati che mi aspetto di trovare in timeline
     * 5) per ciascuno di essi invoco il metodo di b2bSteps "checkIfTimelineElementExists"
     */

    @Then("si controlla che siano presenti tutti gli eventi relativi alla sequence {string} e iun {string}")
    public void checkSequenceEventsOnPaperTracker(String sequenceName, String iun) {
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
//        if (docType != null) {
//            data.put("details_attachments", "[{\"documentType\": \"" + docType + "\"}]");
//        }
        return data;
    }

}