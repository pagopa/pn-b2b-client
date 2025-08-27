package it.pagopa.pn.cucumber.steps.pa;

import io.cucumber.java.en.Then;
import it.pagopa.pn.client.b2b.generated.openapi.clients.papertracker.model.*;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.AttachmentDetails;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.FullSentNotificationV27;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementDetailsV27;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementV27;
import it.pagopa.pn.client.b2b.pa.service.IPnPaperTrackerClient;
import it.pagopa.pn.cucumber.steps.SharedSteps;
import it.pagopa.pn.cucumber.steps.utilitySteps.Sequence;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
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

    @Then("si verifica il corretto salvataggio degli eventi su PnPaperTracker, PnPaperTrackerDryRunOutputs e timeline per la sequence: {string}")
    public void checkEventsOnPaperTrackerAndDryOutput(String sequenceName) {
        try {
            FullSentNotificationV27 fullSentNotification = sharedSteps.getSentNotificationLastVersion();
            assertThat(fullSentNotification).as("La full sent notification non dev'essere null").isNotNull();

            //TODO: quel PCRETRY_0 mi lascia qualche perplessità: il numero se ho ben capito potrebbe variare,
            // in base al numero di retry effettuati (la maggior parte delle sequence ne ha uno, ma alcune effettuano secondi tentativi)

            String analogEventIds = fullSentNotification.getTimeline().stream().filter(e ->
                    e.getElementId().contains(PREPARE_ANALOG_DOMICILE)).map(e -> e.getElementId() + ".PCRETRY_"+ Sequence.getByName(sequenceName).getPcRetry()).toList().get(0);

            TrackingsRequest request = new TrackingsRequest();
            request.setTrackingIds(List.of(analogEventIds));

            //Poiché la request ha un solo eventId, la response conterrà un solo Tracking
            TrackingsResponse responseTracking = paperTrackerClient.retrieveTrackerEvents(request);
            assertThat(responseTracking).as("La response di paperTracker non dev'essere null").isNotNull();
            assertThat(responseTracking.getTrackings()).as("La response di paperTracker non dev'essere vuota").isNotEmpty();
            Tracking tracking = responseTracking.getTrackings().get(0);

            //Anche qua, la request ha un solo eventId, dunque la response conterrà un solo PaperTrackerOutputsResponseResultInner
            PaperTrackerOutputsResponse responseOutput = paperTrackerClient.retrieveTrackerOutputs(request);
            assertThat(responseOutput).as("La response di paperTrackerOutput non dev'essere null").isNotNull();
            List<PaperTrackerOutputsResponseResultInner> innerOutputList = responseOutput.getResult();
            assertThat(innerOutputList).as("La innerOutputList non dev'essere vuota").isNotEmpty();
            PaperTrackerOutputsResponseResultInner innerOutput = innerOutputList.get(0);
            assertThat(innerOutput).as("L'innerOutput non dev'essere null").isNotNull();
            List<PaperTrackerOutput> outputList = innerOutput.getOutputs();

            if (tracking.getProductType().getValue().equals("RIR")) {
                //TODO: extra steps for RIR (andranno chiamate API aggiuntive per fare dei controlli addizionali. Al momento non esistono ancora)
            }

            // a questo punto, verifico che ogni elemento presente nei tracking risulti anche nei dry output
            /*
            for (PaperEvent event : tracking.getEvents()) {

                String statusCode = event.getStatusCode();
                //TODO: è possibile che lo statusCode di Tracking debba matchare non con lo statusCode di dryOutput bensì con lo statusDetail
                List<PaperTrackerOutput> sameCodeOutputs = outputList.stream().filter(out -> out.getStatusCode().equals(statusCode)).toList();

                boolean hasDryOutputEquivalent = false;
                for (PaperTrackerOutput output : sameCodeOutputs) {
                    if (Objects.equals(output.getAttachments(), event.getAttachments()) &&
                            Objects.equals(output.getDiscoveredAddress(), event.getDiscoveredAddress()) &&
                            Objects.equals(output.getStatusDateTime(), event.getStatusTimestamp()) &&
                            Objects.equals(output.getClientRequestTimeStamp(), event.getRequestTimestamp())
                    ) {
                        hasDryOutputEquivalent = true;
                        break;
                    }
                }



                assertThat(hasDryOutputEquivalent).as(logFailedMatch(event, outputList)).isTrue();
                //appurato che ha un corrispettivo su dryOutput, andiamo a verificare che sia anche presente sulla timeline
                //TODO: non avendo potuto fare prove concrete con dati, non mi è ben chiaro che tipo di elemento di timeline
                // dovrei andare a controllare sulla fullSentNotification? Sarà quello con deliveryDetailCode uguale?
                TimelineElementV27 timelineElement = fullSentNotification.getTimeline().stream().filter(
                        te -> te.getDetails() != null && te.getDetails().getDeliveryDetailCode().equals(statusCode)).findFirst().orElse(null);

                assertThat(timelineElement).as("L'evento tracking non ha un corrispettivo nella timeline").isNotNull();

                //bisogna controllare che tutti gli elementi di progress e feedback che sono nella timeline siano anche nella sameCodeOutputs
                // e che il deliverydetailCode e attacchments sia uguale

            }
             */
            //NEL CASO IN CUI LA RISPOSTA DI TRACKING DEVE CONTENERE TUTTI I DELIVERYDETAILCODE
            Set<String> delideryDetailCode = fullSentNotification.getTimeline().stream()
                    .filter(te -> te.getElementId().contains("SEND_ANALOG_PROGRESS") || te.getElementId().contains("SEND_ANALOG_FEEDBACK"))
                    .map(te -> te.getDetails())
                    .map(te -> te.getDeliveryDetailCode())
                    .collect(Collectors.toSet());

            Set<String> responseTrackingDeliveryDetailCode = responseTracking.getTrackings().stream().flatMap(tr -> tr.getEvents().stream()).map(e -> e.getStatusCode()).collect(Collectors.toSet());
            assertThat(delideryDetailCode)
                    .as("Gli statusCode presenti nella chiamata a /trackings non coincidono con quelli presenti nella timeline!")
                    .isEqualTo(responseTrackingDeliveryDetailCode);

            //-----------------------


            List<TimelineElementDetailsV27> filteredList = fullSentNotification.getTimeline().stream()
                    .filter(te -> te.getElementId().contains(".ATTEMPT_" + Sequence.getByName(sequenceName).getPcRetry())
                            && (te.getElementId().contains("SEND_ANALOG_PROGRESS") || te.getElementId().contains("SEND_ANALOG_FEEDBACK")))
                    .map(te -> te.getDetails())
                    .toList();
/*
            List<UtilityObject> timelineAttachmentsUrl = fullSentNotification.getTimeline().stream()
                    .filter(te -> te.getElementId().contains("SEND_ANALOG_PROGRESS") || te.getElementId().contains("SEND_ANALOG_FEEDBACK"))
                    .map(te -> te.getDetails())
                    .filter(te -> te.getAttachments() != null)
                    .map(td -> new UtilityObject(td.getDeliveryDetailCode(), td.getAttachments().stream().map(att -> att.getUrl()).toList()))
                    .toList();

            List<UtilityObject> paperTrackerOutputAttachmentUrl = outputList.stream().filter(ol -> ol.getAttachments() != null)
                    .map(er -> new UtilityObject(er.getStatusCode(), er.getAttachments().stream().map(att -> att.getUrl()).toList()))
                    .toList();*/





            for (TimelineElementDetailsV27 elem : filteredList) {
                boolean b = outputList.stream().anyMatch(e -> e.getStatusDetail().equals(elem.getDeliveryDetailCode()))
                        &&
                        outputList.stream().map(PaperTrackerOutput::getAttachments).anyMatch(paperAtt -> timelineDetailsContainsAttachment(elem.getAttachments(), paperAtt));
                if (!b) throw new IllegalArgumentException("Elemento non trovato!");
            }
        } catch (AssertionError ae) {
            sharedSteps.throwAssertionErrorWithIUN(ae);
        }
    }


    @Then("aaa si verifica il corretto salvataggio degli eventi su PnPaperTracker, PnPaperTrackerDryRunOutputs e timeline per la sequence: {string}")
    public void yyy(String sequenceName) {
        FullSentNotificationV27 fullSentNotification = sharedSteps.getSentNotificationLastVersionByIun("ZYQD-XAKX-EVZY-202508-R-1");
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
                    .map(td -> new UtilityObject(td.getDeliveryDetailCode(), createAttachmentUrl(td.getAttachments()), finalI))
                    .toList();
            mapTimeline.put(i, result);
        }

        Map<Integer, List<UtilityObject>> mapTracking = new HashMap<>();
        for (int j=0; j < responseTracking.getTrackings().size(); j++) {
            int finalJ = j;
            List<UtilityObject> result2 = responseTracking.getTrackings().get(j).getEvents().stream()
                    .map(te -> new UtilityObject(te.getStatusCode(), createAttachmentUrlTracking(te.getAttachments()), finalJ))
                    .toList();
            mapTracking.put(j, result2);
        }

        Map<Integer, List<UtilityObject>> mapTrackingRir;
        if (responseTracking.getTrackings().stream().flatMap(tr -> tr.getEvents().stream()).anyMatch(ev -> ev.getProductType().equals("RIR"))) {
            for (int i=0; i < analogEventIds0.size(); i++) {
                int finalI = i;
//                List<UtilityObject> result = paperTrackerClient.retrieveTrackingsByAttemptId(analogEventIds0.get(i)).getTrackings()
//                mapTimeline.put(i, result);
            }
        }




        PaperTrackerOutputsResponse responseOutput = paperTrackerClient.retrieveTrackerOutputs(request);
        assertThat(responseOutput).as("La response di paperTrackerOutput non dev'essere null").isNotNull();
        List<PaperTrackerOutputsResponseResultInner> innerOutputList = responseOutput.getResult();
        assertThat(innerOutputList).as("La innerOutputList non dev'essere vuota").isNotEmpty();
        Map<Integer, List<UtilityObject>> mapOutput = new HashMap<>();
        for (int j=0; j < responseOutput.getResult().size(); j++) {
            int finalJ = j;
            List<UtilityObject> result2 = responseOutput.getResult().get(j).getOutputs().stream()
                    .map(te -> new UtilityObject(te.getStatusDetail(), createAttachmentUrlTracking(te.getAttachments()), finalJ))
                    .toList();
            mapOutput.put(j, result2);
        }

        for (Integer attempt : mapTimeline.keySet()) {
            List<UtilityObject> sortedTimeline = mapTimeline.get(attempt).stream().sorted().toList();
            List<UtilityObject> sortedTracking = mapTracking.get(attempt).stream().sorted().toList();
            List<UtilityObject> sortedOutputs =mapOutput.get(attempt).stream().sorted().toList();
            Assertions.assertEquals(sortedTimeline, sortedTracking);
            // BUG: https://pagopa.atlassian.net/browse/PN-16147?atlOrigin=eyJpIjoiYzFlN2RiMzRjZDk5NGU5Zjk1MmNmZjA3MTY1MGM4NTAiLCJwIjoiamlyYS1zbGFjay1pbnQifQ
            // LA RISPOSTA DI OUTPUTS NON CONTIENE TUTTI I DATI PRESENTI SULLA TIMELINE
            Assertions.assertEquals(sortedTimeline, sortedOutputs);
        }























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
    private class UtilityObject implements Comparable<UtilityObject> {
        private String deliveryDetailCode;
        private List<String> attachmentUrlName;
        private int attempts;

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

    @Then("si verifica il corretto salvataggio dell'errore su PnPaperTrackingsError con category: {string} e flowThrow: {string}")
    public void checkTrackingErrors(String category, String flowThrow) {
        try {
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

            Assertions.assertTrue(errorsResponse.getResults().stream().flatMap(res -> res.getErrors().stream()).anyMatch(err -> err.getCategory().equals(category) && err.getEventThrow().equals(flowThrow)));
        } catch (AssertionError ae) {
            sharedSteps.throwAssertionErrorWithIUN(ae);
        }
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

    @Then("si controlla che siano presenti tutti gli eventi relativi alla sequence {string}")
    public void checkSequenceEventsOnPaperTracker(String sequenceName) {
        log.info("Creata notifica con sequence: " + sequenceName + " e IUN: " + sharedSteps.getNotificationIun());
//        Sequence sequence = Sequence.getByName(sequenceName);
//        assertThat(sequence).as("Sequence inesistente: " + sequenceName).isNotNull();
//
//        for (String eventString : sequence.getEvents()) {
//            Map<String, String> data = buildDataMap(eventString);
//            b2bSteps.checkIfTimelineElementExists(data.get("eventCategory"), true, data);
//        }
    }

    private Map<String, String> buildDataMap(String eventString) {
        Map<String, String> data = new HashMap<>();
        Matcher m = Pattern.compile("_ATTEMPT_(\\d+)").matcher(eventString);
        if (m.find()) data.put("details_sentAttemptMade", m.group(1));

        int openBracketIndex = eventString.indexOf("[");

        String deliveryDetailCode = eventString.replaceAll("_ATTEMPT_\\d+", "");
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