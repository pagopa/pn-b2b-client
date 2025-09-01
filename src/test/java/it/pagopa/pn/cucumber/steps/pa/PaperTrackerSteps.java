package it.pagopa.pn.cucumber.steps.pa;

import io.cucumber.java.ParameterType;
import io.cucumber.java.en.Then;
import it.pagopa.pn.client.b2b.generated.openapi.clients.papertracker.model.*;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.AttachmentDetails;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.FullSentNotificationV27;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementV27;
import it.pagopa.pn.client.b2b.pa.service.IPnPaperTrackerClient;
import it.pagopa.pn.cucumber.steps.SharedSteps;
import it.pagopa.pn.cucumber.steps.utilitySteps.PaperTrackerErrorCategory;
import it.pagopa.pn.cucumber.steps.utilitySteps.Sequence;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Collections;
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
    private final AvanzamentoNotificheB2bSteps b2bSteps;
    private final SharedSteps sharedSteps;
    private final IPnPaperTrackerClient paperTrackerClient;

    @Autowired
    public PaperTrackerSteps(AvanzamentoNotificheB2bSteps b2bSteps, IPnPaperTrackerClient paperTrackerClient) {
        this.b2bSteps = b2bSteps;
        this.sharedSteps = b2bSteps.getSharedSteps();
        this.paperTrackerClient = paperTrackerClient;
    }


    @Then("si verifica il corretto salvataggio degli eventi su PnPaperTracker, PnPaperTrackerDryRunOutputs e timeline per lo iun: {string}")
    public void checkPaperTrackerEvents(String iun) {
        FullSentNotificationV27 fullSentNotification = sharedSteps.getSentNotificationLastVersion();
        List<String> stringaTracking = fullSentNotification.getTimeline().stream().filter(e ->
                e.getElementId().contains(PREPARE_ANALOG_DOMICILE)).map(e -> e.getElementId() + ".PCRETRY_0").toList();

        List<String> analogEventIds0 = fullSentNotification.getTimeline().stream().filter(e ->
                e.getElementId().contains(PREPARE_ANALOG_DOMICILE)).map(TimelineElementV27::getElementId).toList();

        TrackingsRequest request = new TrackingsRequest();
        request.setTrackingIds(stringaTracking);

        // BUG: /tracking NON ACCETTA PIù DI UN PARAMETRO NELLA RICHIESTA
        TrackingsResponse responseTracking = paperTrackerClient.retrieveTrackerEvents(request);
        assertThat(responseTracking).as("La response di paperTracker non dev'essere null").isNotNull();
        assertThat(responseTracking.getTrackings()).as("La response di paperTracker non dev'essere vuota").isNotEmpty();

        Map<Integer, List<UtilityObject>> mapTimeline = new HashMap<>();
        for (int i=0; i < analogEventIds0.size(); i++) {
            int finalI = i;
            List<UtilityObject> result = fullSentNotification.getTimeline().stream()
                    .filter(te -> te.getElementId().contains("ATTEMPT_" + finalI) && (te.getElementId().contains("SEND_ANALOG_PROGRESS") || te.getElementId().contains("SEND_ANALOG_FEEDBACK")))
                    .map(te -> te.getDetails())
                    .map(td -> new UtilityObject(td.getDeliveryDetailCode(), createAttachmentUrl(td.getAttachments())))
                    .toList();
            mapTimeline.put(i, result);
        }

        Map<Integer, List<UtilityObject>> mapTracking = new HashMap<>();
        for (int j=0; j < responseTracking.getTrackings().size(); j++) {
            List<UtilityObject> result2 = responseTracking.getTrackings().get(j).getEvents().stream()
                    .map(te -> new UtilityObject(te.getStatusCode(), createAttachmentUrlTracking(te.getAttachments())))
                    .toList();
            mapTracking.put(j, result2);
        }

        Map<Integer, List<UtilityObject>> mapTrackingRir = new HashMap<>();
        if (responseTracking.getTrackings().stream().anyMatch(ev -> ev.getProductType().getValue().equals("RIR"))) {
            for (int i=0; i < analogEventIds0.size(); i++) {
                TrackingsResponse rirTrackingResponseAttempt = paperTrackerClient.retrieveTrackingsByAttemptId(analogEventIds0.get(i), null);
                List<UtilityObject> result3 = rirTrackingResponseAttempt.getTrackings().get(i).getEvents().stream()
                        .map(tr -> new UtilityObject(tr.getStatusCode(), createAttachmentUrlTracking(tr.getAttachments())))
                        .toList();
                mapTrackingRir.put(i, result3);
            }
        }

        PaperTrackerOutputsResponse responseOutput = paperTrackerClient.retrieveTrackerOutputs(request);
        assertThat(responseOutput).as("La response di paperTrackerOutput non dev'essere null").isNotNull();
        List<PaperTrackerOutputsResponseResultsInner> innerOutputList = responseOutput.getResults();
        assertThat(innerOutputList).as("La innerOutputList non dev'essere vuota").isNotEmpty();
        Map<Integer, List<UtilityObject>> mapOutput = new HashMap<>();
        for (int j=0; j < responseOutput.getResults().size(); j++) {
            List<UtilityObject> result2 = responseOutput.getResults().get(j).getOutputs().stream()
                    .map(te -> new UtilityObject(te.getStatusDetail(), createAttachmentUrlTracking(te.getAttachments())))
                    .toList();
            mapOutput.put(j, result2);
        }

        for (Integer attempt : mapTimeline.keySet()) {
            List<UtilityObject> sortedTimeline = mapTimeline.get(attempt).stream().sorted().collect(Collectors.toCollection(ArrayList::new));
            List<UtilityObject> sortedTracking = mapTracking.get(attempt).stream().sorted().toList();
            List<UtilityObject> sortedOutputs = mapOutput.get(attempt).stream().sorted().toList();
            if (!mapTrackingRir.isEmpty()) {
                List<UtilityObject> sortedRirTrackingAttempts = mapTrackingRir.get(attempt).stream().sorted().toList();
                Assertions.assertEquals(sortedRirTrackingAttempts, sortedTracking, "La risposta di /attempts differisce da quella di /trackings!");
            }
            Assertions.assertEquals(sortedTimeline, sortedTracking, "La risposta di /trackings non contiene tutti gli elementi presenti in timeline!");
            // BUG: https://pagopa.atlassian.net/browse/PN-16147?atlOrigin=eyJpIjoiYzFlN2RiMzRjZDk5NGU5Zjk1MmNmZjA3MTY1MGM4NTAiLCJwIjoiamlyYS1zbGFjay1pbnQifQ
            // LA RISPOSTA DI OUTPUTS NON CONTIENE TUTTI I DATI PRESENTI SULLA TIMELINE
            // SI RIMUOVE CON018 IN QUANTO NON è PREVISTO CHE SIA RITORNATO NELLA TABELLA OUTPUTS
            Assertions.assertEquals(sanitizeList(sortedTimeline, List.of("CON018")), sortedOutputs,
                    "La risposta di /outputs non contiene tutti gli elementi previsti che sono presenti in timeline!");
        }

    }

    private List<UtilityObject> sanitizeList(List<UtilityObject> list, List<String> deliveryDetailsList) {
        return list.stream().filter(item -> !deliveryDetailsList.contains(item.getDeliveryDetailCode())).toList();
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
    private class UtilityObject implements Comparable<UtilityObject> {
        private String deliveryDetailCode;
        private List<String> attachmentUrlName;
//        private int attempts;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof UtilityObject)) return false;
            UtilityObject that = (UtilityObject) o;

            if (!Objects.equals(deliveryDetailCode, that.deliveryDetailCode)) {
                return false;
            }

            // confronto ignorando l’ordine, ma rispettando i duplicati
            List<String> thisList = new ArrayList<>(attachmentUrlName);
            List<String> thatList = new ArrayList<>(that.attachmentUrlName);

            Collections.sort(thisList);
            Collections.sort(thatList);

            return thisList.equals(thatList);
        }

        @Override
        public int hashCode() {
            List<String> sortedList = new ArrayList<>(attachmentUrlName);
            Collections.sort(sortedList);
            return Objects.hash(deliveryDetailCode, sortedList);
        }

        @Override
        public int compareTo(UtilityObject other) {
            if (this.deliveryDetailCode == null && other.deliveryDetailCode == null) return 0;
            if (this.deliveryDetailCode == null) return -1;
            if (other.deliveryDetailCode == null) return 1;
            return this.deliveryDetailCode.compareTo(other.deliveryDetailCode);
        }
    }


    private boolean timelineDetailsContainsAttachment(List<AttachmentDetails> attachments, List<Attachment> paperTrackerOutputs) {
        boolean allAttachementsMatches = false;
        for (AttachmentDetails attachmentDetail : attachments) {
            allAttachementsMatches = paperTrackerOutputs.stream().anyMatch(att -> att.getUrl().equals(attachmentDetail.getUrl()));
        }
        return allAttachementsMatches;
    }

    @ParameterType("TRACKING_ID_NOT_FOUND|RENDICONTAZIONE_SCARTATA|DATE_ERROR|STATUS_CODE_ERROR|LAST_EVENT_EXTRACTION_ERROR" +
            "|REGISTERED_LETTER_CODE_ERROR|DELIVERY_FAILURE_CAUSE_ERROR|ATTACHMENTS_ERROR|MAX_RETRY_REACHED_ERROR|OCR_VALIDATION|DUPLICATED_EVENT")
    public static PaperTrackerErrorCategory paperTrackerErrorCategory(String errorCategory) {
        return PaperTrackerErrorCategory.valueOf(errorCategory.toUpperCase());
    }

    @Then("si verifica il corretto salvataggio dell'errore su PnPaperTrackingsError con category: {paperTrackerErrorCategory} e flowThrow: {string} e iun: {string}")
    public void checkTrackingErrors(PaperTrackerErrorCategory category, String flowThrow, String iun) {
        FullSentNotificationV27 fullSentNotification = sharedSteps.getSentNotificationLastVersionByIun(iun);
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
        log.info("Creata notifica con sequence: " + sequenceName + " e IUN: " + sharedSteps.getNotificationIun());

//        Sequence sequence = Sequence.getByName(sequenceName);
//        sharedSteps.setNotificationIun(iun);
//        assertThat(sequence).as("Sequence inesistente: " + sequenceName).isNotNull();
//
//        for (String eventString : sequence.getEvents()) {
//            Map<String, String> data = buildDataMap(eventString);
//            Matcher m = Pattern.compile("_COUNT_(\\d+)").matcher(eventString);
//            if (m.find()) {
//                b2bSteps.checkNumberOfTimelineElementsWithCategoryFromMap(data.get("eventCategory"), Integer.parseInt(m.group(1)), data);
//            }
//            else {
//                b2bSteps.checkIfTimelineElementExists(data.get("eventCategory"), true, data);
//            }
//        }
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
        data.put("eventCategory", Sequence.isFeedback(deliveryDetailCode) ? SEND_ANALOG_FEEDBACK : SEND_ANALOG_PROGRESS);
        if (failureCause != null) {
            data.put("details_failureCause", failureCause);
        }
        if (docType != null) {
//            data.put("details_attachments", "[{\"documentType\": \"" + docType + "\"}]");
        }
        return data;
    }

    private String logFailedMatch(PaperEvent event, List<PaperTrackerOutput> outputs) {
        StringBuilder sb = new StringBuilder();
        sb.append("L'evento\n").append(event).append("\nnon ha un corrispettivo in dry output:\n");
        outputs.forEach(out -> {
            sb.append(out);
            sb.append("\n");
        });
        return sb.toString();
    }
}