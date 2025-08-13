package it.pagopa.pn.cucumber.steps.pa;

import io.cucumber.java.en.Then;
import it.pagopa.pn.client.b2b.generated.openapi.clients.papertracker.model.*;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.FullSentNotificationV27;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementV27;
import it.pagopa.pn.client.b2b.pa.service.IPnPaperTrackerClient;
import it.pagopa.pn.cucumber.steps.SharedSteps;
import it.pagopa.pn.cucumber.steps.utilitySteps.Sequence;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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

    @Then("si verifica il corretto salvataggio degli eventi su PnPaperTracker, PnPaperTrackerDryRunOutputs e timeline")
    public void checkEventsOnPaperTrackerAndDryOutput() {
        try {
            FullSentNotificationV27 fullSentNotification = sharedSteps.getSentNotificationLastVersion();
            assertThat(fullSentNotification).as("La full sent notification non dev'essere null").isNotNull();

            //TODO: quel PCRETRY_0 mi lascia qualche perplessità: il numero se ho ben capito potrebbe variare,
            // in base al numero di retry effettuati (la maggior parte delle sequence ne ha uno, ma alcune effettuano secondi tentativi)
            List<String> analogEventIds = fullSentNotification.getTimeline().stream().filter(e ->
                    e.getCategory().getValue().equals(PREPARE_ANALOG_DOMICILE)).map(e -> e.getElementId() + ".PCRETRY_0").toList();

            TrackingsRequest request = new TrackingsRequest();
            request.setTrackingIds(analogEventIds);

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
            }
        } catch (AssertionError ae) {
            sharedSteps.throwAssertionErrorWithIUN(ae);
        }
    }

    @Then("si verifica il corretto salvataggio dell'errore su PnPaperTrackingsError")
    public void checkTrackingErrors() {
        try {
            FullSentNotificationV27 fullSentNotification = sharedSteps.getSentNotificationLastVersion();
            assertThat(fullSentNotification).as("La full sent notification non dev'essere null").isNotNull();

            //TODO: quel PCRETRY_0 mi lascia qualche perplessità: il numero se ho ben capito potrebbe variare,
            // in base al numero di retry effettuati (la maggior parte delle sequence ne ha uno, ma alcune effettuano secondi tentativi)
            List<String> analogEventIds = fullSentNotification.getTimeline().stream().filter(e ->
                    e.getCategory().getValue().equals(PREPARE_ANALOG_DOMICILE)).map(e -> e.getElementId() + ".PCRETRY_0").toList();

            TrackingsRequest request = new TrackingsRequest();
            request.setTrackingIds(analogEventIds);

            TrackingErrorsResponse errorsResponse = paperTrackerClient.retrieveTrackerErrors(request);
            assertThat(errorsResponse).as("La TrackingErrorsResponse non dev'essere null").isNotNull();
            List<TrackingErrorsResponseResultsInner> innerErrorList = errorsResponse.getResults();
            assertThat(innerErrorList).as("La TrackingErrorsResponseResultsInner non dev'essere vuota").isNotEmpty();
            TrackingErrorsResponseResultsInner innerError = innerErrorList.get(0);
            assertThat(innerError).as("L'innerError non dev'essere null").isNotNull();
            List<TrackingError> outputList = innerError.getErrors();
            //TODO: questo metodo dovrebbe andare a controllare che gli errori generati dalle sequence siano stati salvati
            // Non essendo tuttavia ancora state create le sequence di errore, non so bene che tipo di controllo debba fare
            // sui dati recuperati tramite chiamata API
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
        Sequence sequence = Sequence.getByName(sequenceName);
        assertThat(sequence).as("Sequence inesistente: " + sequenceName).isNotNull();

        for (String eventString : sequence.getEvents()) {
            Map<String, String> data = buildDataMap(eventString);
            b2bSteps.checkIfTimelineElementExists(data.get("eventCategory"), true, data);
        }
    }

    private Map<String, String> buildDataMap(String eventString) {
        Map<String, String> data = new HashMap<>();

        int openBracketIndex = eventString.indexOf("[");

        String deliveryDetailCode = eventString;
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

        data.put("loadTimeline", "true");
        data.put("details", "NOT_NULL");
        data.put("details_deliveryDetailCode", deliveryDetailCode);
        data.put("eventCategory", Sequence.isFeedback(deliveryDetailCode) ? SEND_ANALOG_FEEDBACK : SEND_ANALOG_PROGRESS);
        if (failureCause != null) {
            data.put("details_failureCause", failureCause);
        }
        if (docType != null) {
            data.put("details_attachments", "[{\"documentType\": \"" + docType + "\"}]");
        }
        return data;
    }

    private String logFailedMatch(PaperEvent event, List<PaperTrackerOutput> outputs) {
        StringBuilder sb = new StringBuilder();
        sb.append("L'evento\n").append(event).append("\nnon ha un corrispettivo in dry output: ");
        outputs.forEach(out -> {
            sb.append(out);
            sb.append("\n");
        });
        return sb.toString();
    }
}