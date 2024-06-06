package it.pagopa.pn.cucumber.steps.pa;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import io.cucumber.java.After;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.NotificationStatusHistoryElement;
import it.pagopa.pn.client.b2b.pa.polling.design.PnPollingFactory;
import it.pagopa.pn.client.b2b.pa.polling.design.PnPollingStrategy;
import it.pagopa.pn.client.b2b.pa.polling.dto.PnPollingParameter;
import it.pagopa.pn.client.b2b.pa.polling.dto.PnPollingResponseV20;
import it.pagopa.pn.client.b2b.pa.polling.dto.PnPollingResponseV23;
import it.pagopa.pn.client.b2b.pa.polling.dto.PnPollingWebhook;
import it.pagopa.pn.client.b2b.pa.polling.impl.PnPollingServiceWebhookV20;
import it.pagopa.pn.client.b2b.pa.polling.impl.PnPollingServiceWebhookV23;
import it.pagopa.pn.client.b2b.pa.service.IPnPaB2bClient;
import it.pagopa.pn.client.b2b.pa.service.IPnWebRecipientClient;
import it.pagopa.pn.client.b2b.pa.service.IPnWebhookB2bClient;
import it.pagopa.pn.client.b2b.pa.service.utils.SettableApiKey;
import it.pagopa.pn.client.b2b.pa.utils.TimingForPolling;
import it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model_v2.ProgressResponseElement;
import it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model_v2.StreamCreationRequest;
import it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model_v2.StreamMetadataResponse;
import it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model_v2.TimelineElementCategoryV20;
import it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model_v2_3.*;
import it.pagopa.pn.cucumber.steps.SharedSteps;
import it.pagopa.pn.cucumber.utils.GroupPosition;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.opentest4j.AssertionFailedError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpStatusCodeException;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import static it.pagopa.pn.cucumber.steps.pa.AvanzamentoNotificheWebhookB2bSteps.StreamVersion.V10;
import static it.pagopa.pn.cucumber.steps.pa.AvanzamentoNotificheWebhookB2bSteps.StreamVersion.V23;

@Slf4j
public class AvanzamentoNotificheWebhookB2bSteps {

    private final IPnWebhookB2bClient webhookB2bClient;
    private final IPnPaB2bClient b2bClient;
    private final IPnWebRecipientClient webRecipientClient;
    private final SharedSteps sharedSteps;
    private List<StreamCreationRequest> streamCreationRequestList;
    private List<StreamMetadataResponse> eventStreamList;
    private List<StreamCreationRequestV23> streamCreationRequestListV23;
    private List<StreamMetadataResponseV23> eventStreamListV23;
    private StreamRequestV23 streamRequestV23;
    private StreamCreationRequest streamCreationRequest;
    private Integer requestNumber;
    private HttpStatusCodeException notificationError;
    private final PnPollingFactory pollingFactory;
    private final TimingForPolling timingForPolling;

    @And("viene verificato che il campo legalfactIds sia valorizzato nel EventStream")
    public void vieneVerificatoCheIlCampoLegalfactIdsSiaValorizzato() {
        Assertions.assertNotNull(sharedSteps.getProgressResponseElement());
        Assertions.assertNotNull(sharedSteps.getProgressResponseElement().getLegalfactIds());
        Assertions.assertFalse(sharedSteps.getProgressResponseElement().getLegalfactIds().isEmpty());
    }

    //private final WebhookSynchronizer webhookSynchronizer;

    public enum StreamVersion {V23,V10,V10_V23}

    private final Set<String> paStreamOwner = new HashSet<>();

    //TODO: rimuovere
    private final LinkedList<ProgressResponseElement> progressResponseElementList = new LinkedList<>();
    private final LinkedList<ProgressResponseElementV23> progressResponseElementListV23 = new LinkedList<>();
    private ProgressResponseElementV23 progressResponseElementResultV23;
    private static IPnWebhookB2bClient webhookClientForClean;
    private static boolean webhookTestLaunch;
    private static final Map<String,SettableApiKey.ApiKeyType> paForStream =
            Map.of(
            "Comune_1",SettableApiKey.ApiKeyType.MVP_1,
            "Comune_2", SettableApiKey.ApiKeyType.MVP_2,
            "Comune_Multi", SettableApiKey.ApiKeyType.GA);


    @Autowired
    public AvanzamentoNotificheWebhookB2bSteps(IPnWebhookB2bClient webhookB2bClient, SharedSteps sharedSteps,
                                               TimingForPolling timingForPolling, PnPollingFactory pollingFactory) {
        this.sharedSteps = sharedSteps;
        this.webhookB2bClient = webhookB2bClient;
        this.webRecipientClient = sharedSteps.getWebRecipientClient();
        this.timingForPolling = timingForPolling;
        this.b2bClient = sharedSteps.getB2bClient();
        this.pollingFactory = pollingFactory;
        webhookTestLaunch = true;
        AvanzamentoNotificheWebhookB2bSteps.webhookClientForClean = webhookB2bClient;
    }

    //@AfterAll -> problema con esecuzione concorrente
    public static void afterAll() {
        log.info("Start clean Webhook!!!");
        log.info("webhookClientForClean state: "+webhookClientForClean);
        log.info("webhook eseguito: "+webhookTestLaunch);
        if(webhookTestLaunch){
            log.info("Starting cleaning");
            for(SettableApiKey.ApiKeyType pa: paForStream.values()){
                //TODO: MODIFICARE
                webhookClientForClean.setApiKeys(pa);

                //DELETE V1
                List<it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model_v2.StreamListElement> streamListElements = webhookClientForClean.listEventStreams();
                for(it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model_v2.StreamListElement elem: streamListElements){
                    try{
                        webhookClientForClean.deleteEventStream(elem.getStreamId());
                    }catch (HttpStatusCodeException statusCodeException){
                        log.error("HTTP Error: statusCode {} message {}",statusCodeException.getStatusCode(),statusCodeException.getMessage());
                    }
                }

                //DELETE V2.3
                List<it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model_v2_3.StreamListElement> streamListElementsV23 = webhookClientForClean.listEventStreamsV23();
                for(it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model_v2_3.StreamListElement elem: streamListElementsV23){
                    try{
                        webhookClientForClean.deleteEventStreamV23(elem.getStreamId());
                    }catch (HttpStatusCodeException statusCodeException){
                        log.error("HTTP Error: statusCode {} message {}",statusCodeException.getStatusCode(),statusCodeException.getMessage());
                    }
                }
            }
        }
    }


    @After("@cleanWebhook")
    public void afterStreamTestRun(){
        log.info("Starting cleaning");
        for(String pa: paStreamOwner){
            if(paForStream.containsKey(pa)){ deleteAllPaStreamForAllVersion(paForStream.get(pa)); };
        }

        //TODO: Da ripristinare con concorrenza
//        log.info("After StreamTest started");
//        //removeStream
//        //si può solo cancellare
//        try{
//            Iterator<UUID> iteratorStreamIdForPaAndVersion = streamIdForPaAndVersion.keySet().iterator();
//            while(iteratorStreamIdForPaAndVersion.hasNext()){
//                UUID streamId = iteratorStreamIdForPaAndVersion.next();
//                log.info("removeStream phase start for id {}",streamId);
//                PnPaB2bUtils.Pair<String, StreamVersion> paAndVersion = streamIdForPaAndVersion.get(streamId);
//                log.info("removeStream id {} for pa {} with version {}",streamId,paAndVersion.getValue1(),paAndVersion.getValue2());
//                setPaWebhook(paAndVersion.getValue1());
//                deleteStreamWrapper(paAndVersion.getValue2(),paAndVersion.getValue1(),streamId);
//            }
//        }catch(Exception e){
//            log.info("Exception in delete after: {}",e.getMessage());
//        }
//
//        //releaseStreamSlot
//        Iterator<String> iteratorNumberOfStreamSlotAcquiredForPa = numberOfStreamSlotAcquiredForPa.keySet().iterator();
//        while(iteratorNumberOfStreamSlotAcquiredForPa.hasNext()){
//            String pa = iteratorNumberOfStreamSlotAcquiredForPa.next();
//            log.info("releaseStreamCreationSlot phase start for pa {}",pa);
//            PnPaB2bUtils.Pair<Boolean, Integer> isAcquireNumberOfStramSlot = numberOfStreamSlotAcquiredForPa.get(pa);
//            if(isAcquireNumberOfStramSlot.getValue1() && isAcquireNumberOfStramSlot.getValue2() > 0){
//                log.info("release n.{} of streamCreationSlot for pa {}",isAcquireNumberOfStramSlot.getValue2(),pa);
//                WEBHOOKSYNCHRONIZER.releaseStreamCreationSlot(isAcquireNumberOfStramSlot.getValue2(),pa);
//            }
//        }
    }



    private void deleteAllPaStreamForAllVersion(SettableApiKey.ApiKeyType pa){
        //TODO: MODIFICARE
        webhookClientForClean.setApiKeys(pa);

        //DELETE V1
        List<it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model_v2.StreamListElement> streamListElements = webhookClientForClean.listEventStreams();
        for(it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model_v2.StreamListElement elem: streamListElements){
            try{
                webhookClientForClean.deleteEventStream(elem.getStreamId());
            }catch (HttpStatusCodeException statusCodeException){
                log.error("HTTP Error: statusCode {} message {}",statusCodeException.getStatusCode(),statusCodeException.getMessage());
            }
        }

        //DELETE V2.3
        List<it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model_v2_3.StreamListElement> streamListElementsV23 = webhookClientForClean.listEventStreamsV23();
        for(it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model_v2_3.StreamListElement elem: streamListElementsV23){
            try{
                webhookClientForClean.deleteEventStreamV23(elem.getStreamId());
            }catch (HttpStatusCodeException statusCodeException){
                log.error("HTTP Error: statusCode {} message {}",statusCodeException.getStatusCode(),statusCodeException.getMessage());
            }
        }
    }

    @Given("si predispo(ngono)(ne) {int} nuov(i)(o) stream denominat(i)(o) {string} con eventType {string} con versione {string}")
    public void setUpStreamsWithEventType(int number, String title, String eventType, String version) {
        this.requestNumber = number;
        createStreamRequest(StreamVersion.valueOf(version.trim().toUpperCase()),new LinkedList<>(),number,title,eventType);
    }

    @Given("si predispo(ngono)(ne) {int} nuov(i)(o) stream V2 denominat(i)(o) {string} con eventType {string}")
    public void setUpStreamsWithEventTypeV2(int number, String title, String eventType) {
        this.requestNumber = number;
        List<String> filteredValues = eventType.equalsIgnoreCase("STATUS") ?
                Arrays.stream(NotificationStatus.values()).map(Enum::toString).toList() :
                Arrays.stream(TimelineElementCategoryV20.values()).map(Enum::toString).toList();

        createStreamRequest(V10,filteredValues,number,title,eventType);
    }

    @When("si crea(no) i(l) nuov(o)(i) stream per il {string} con versione {string} e filtro di timeline {string}")
    public void createdStreamByFilterValue(String pa,String version,String filter) {
        setPaWebhook(pa);
        createStream(pa,StreamVersion.valueOf(version.trim().toUpperCase()),null,false, List.of(filter),false);
    }

    @When("si crea(no) i(l) nuov(o)(i) stream per il {string} con versione {string}")
    public void createdStream(String pa,String version) {
        setPaWebhook(pa);
        updateApiKeyForStream();
        createStream(pa,StreamVersion.valueOf(version.trim().toUpperCase()),null,false, null,false);
    }

    @And("si crea il nuovo stream per il {string} con versione {string} \\(caso errato)")
    public void siCreaIlNuovoStreamPerIlConVersioneFORZATOSoloPerCasoErrato(String pa, String version) {
        setPaWebhook(pa);
        createStream(pa,StreamVersion.valueOf(version.trim().toUpperCase()),null,false, null,true);
    }

    @When("si crea(no) i(l) nuov(o)(i) stream V23 per il {string} con un gruppo disponibile {string}")
    public void createdStreamByGroups(String pa, String position) {
        setPaWebhook(pa);
        updateApiKeyForStream();
        createStream(pa,StreamVersion.V23,getGruopForStream(position,pa),false, null,false);
    }

    @When("si crea(no) i(l) nuov(o)(i) stream V23 per il {string} con un gruppo disponibile {string} \\(caso errato)")
    public void createdStreamByGroupsForced(String pa, String position) {
        setPaWebhook(pa);
        updateApiKeyForStream();
        createStream(pa,StreamVersion.V23,getGruopForStream(position,pa),false, null,true);
    }

    @When("si crea(no) i(l) nuov(o)(i) stream V23 per il {string} con replaceId con un gruppo disponibile {string} \\(caso errato)")
    public void createdStreamByGroupsForcedWithReplace(String pa, String position) {
        setPaWebhook(pa);
        updateApiKeyForStream();
        createStream(pa,StreamVersion.V23,getGruopForStream(position,pa),true, null,true);
    }

    @When("si crea(no) i(l) nuov(o)(i) stream V23 per il {string} con replaceId con un gruppo disponibile {string}")
    public void createdStreamByGroupsWithReplaceId(String pa, String position) {
        setPaWebhook(pa);
        updateApiKeyForStream();
        createStream(pa,StreamVersion.V23,getGruopForStream(position,pa),true, null,false);
    }

    //TODO MODIFICARE...
    @When("si crea(no) i(l) nuov(o)(i) stream V10_V23 per il {string} con replaceId con un gruppo disponibile {string}")
    public void createdStreamByGroupsWithReplaceIdV10V23(String pa, String position) {
        setPaWebhook(pa);
        updateApiKeyForStream();
        createStream(pa, StreamVersion.V10_V23, getGruopForStream(position, pa), true, null, false);
    }

    @When("viene aggiornata la apiKey utilizzata per gli stream")
    public void updateApiKeyForStream() {
        if(sharedSteps.getResponseNewApiKey()!= null){
            webhookB2bClient.setApiKey(sharedSteps.getResponseNewApiKey().getApiKey());
        }
    }

    @And("si cancella(no) (lo)(gli) stream creat(o)(i) per il {string} con versione {string}")
    public void deleteStream(String pa, String versione) {
        switch (StreamVersion.valueOf(versione.trim().toUpperCase())) {
            case V10 -> {
                for (StreamMetadataResponse eventStream : eventStreamList) {
                    deleteStreamWrapper(V10,pa,eventStream.getStreamId());
                }
            }
            case V23 -> {
                for(StreamMetadataResponseV23 eventStream: eventStreamListV23){
                    deleteStreamWrapper(V23,pa,eventStream.getStreamId());
                }
            }
            default -> throw new IllegalArgumentException();
        }
    }

    @And("si aggiorna(no) (lo)(gli) stream creat(o)(i) con versione {string} e apiKey aggiornata")
    public void updateStreamUpadateApiKey(String versione) {
        updateApiKeyForStream();
        updateStream(versione);
    }

    @And("si {string} un gruppo allo stream creat(o)(i) con versione {string} per il comune {string} e apiKey aggiornata")
    public void updateGroupsStreamUpadateApiKey(String action, String versione, String pa) {
        updateApiKeyForStream();
        if(sharedSteps.getRequestNewApiKey()!= null){
            streamRequestV23 = new StreamRequestV23();
            if ("rimuove".equalsIgnoreCase(action) && sharedSteps.getRequestNewApiKey()!= null && sharedSteps.getRequestNewApiKey().getGroups().size()>=2) {
                streamRequestV23.setGroups(sharedSteps.getRequestNewApiKey().getGroups().subList(0, 0));
            } else if ("aggiunge".equalsIgnoreCase(action)) {
                streamRequestV23.setGroups(sharedSteps.getGroupAllActiveByPa(pa));
            }else if ("stesso".equalsIgnoreCase(action)) {
                for (StreamMetadataResponseV23 eventStream : eventStreamListV23) {
                    streamRequestV23.setGroups(eventStream.getGroups());
                }

            }
        }
        updateStream(versione);
    }


    @And("si aggiorna(no) (lo)(gli) stream creat(o)(i) con versione {string} con un gruppo che non appartiene al comune {string}")
    public void updateStreamByGroupsNoPA(String versione,String pa) {
        updateStreamByGroupsPA(versione,pa,false);
    }

    @And("si aggiorna(no) (lo)(gli) stream creat(o)(i) con versione {string} con un gruppo che appartiene al comune {string}")
    public void updateStreamByGroupsPA(String versione, String pa) {
        updateStreamByGroupsPA(versione,pa,true);
    }

    private void updateStreamByGroupsPA(String version, String pa, boolean groupOfPa){
        String groupToUse = switch (pa){
            case "Comune_Multi" -> groupOfPa ? "Comune_Multi" : "Comune_1";
            case "Comune_1" -> groupOfPa ? "Comune_1" : "Comune_Multi";
            default -> "Comune_Multi";
        };
        try {
            streamRequestV23 = new StreamRequestV23();
            streamRequestV23.setGroups(List.of(sharedSteps.getGroupIdByPa(groupToUse, GroupPosition.FIRST)));
        } catch (HttpStatusCodeException e) {
            this.notificationError = e;
            sharedSteps.setNotificationError(e);
        }
        updateStream(version);
    }

    @And("si aggiorna(no) (lo)(gli) stream creat(o)(i) con versione {string} -Cross Versioning")
    public void updateStreamVersioning (String versione) {

        if(sharedSteps.getResponseNewApiKey()!= null){
            webhookB2bClient.setApiKey(sharedSteps.getResponseNewApiKey().getApiKey());
        }
        UUID idStream = null;
        if (eventStreamList!= null && !eventStreamList.isEmpty()){
            idStream = eventStreamList.get(0).getStreamId();
        } else if (eventStreamListV23 != null && !eventStreamListV23.isEmpty()) {
            idStream = eventStreamListV23.get(0).getStreamId();
        }
        switch (versione) {
            case "V10":
                try {
                    streamCreationRequest = new StreamCreationRequest();
                    streamCreationRequest.setTitle("Update Stream V10");
                    streamCreationRequest.setEventType(StreamCreationRequest.EventTypeEnum.TIMELINE);
                    webhookB2bClient.updateEventStream(idStream, streamCreationRequest);
                } catch (HttpStatusCodeException e) {
                    this.notificationError = e;
                    sharedSteps.setNotificationError(e);
                }
            case "V23":
                try {
                    streamRequestV23 = new StreamRequestV23();
                    streamRequestV23.setTitle("Update Stream V23");
                    streamRequestV23.setEventType(StreamRequestV23.EventTypeEnum.TIMELINE);
                    webhookB2bClient.updateEventStreamV23(idStream, streamRequestV23);
                } catch (HttpStatusCodeException e) {
                    this.notificationError = e;
                    sharedSteps.setNotificationError(e);
                }
                break;
            default:
                throw new IllegalArgumentException();
        }
    }

    @And("si aggiorna(no) (lo)(gli) stream creat(o)(i) con versione {string}")
    public void updateStream (String versione) {
        switch (versione) {
            case "V10":
                for(StreamMetadataResponse eventStream: eventStreamList){
                    StreamCreationRequest streamCreationRequest = new StreamCreationRequest();
                    streamCreationRequest.setTitle("Stream Update V10");
                    streamCreationRequest.setEventType(StreamCreationRequest.EventTypeEnum.TIMELINE);
                    webhookB2bClient.updateEventStream(eventStream.getStreamId(),streamCreationRequest);
                }
                break;
            case "V23":
                try{
                    if (streamRequestV23 == null) {
                        streamRequestV23 = new StreamRequestV23();
                        streamRequestV23.setGroups(sharedSteps.getRequestNewApiKey().getGroups());
                    }
                    streamRequestV23.setTitle("Stream Update");

                    streamRequestV23.setEventType(StreamRequestV23.EventTypeEnum.TIMELINE);
                    for (StreamMetadataResponseV23 eventStream : eventStreamListV23) {
                        StreamMetadataResponseV23 result = webhookB2bClient.updateEventStreamV23(eventStream.getStreamId(), streamRequestV23);
                        Assertions.assertNotNull(result);
                        Assertions.assertTrue(streamRequestV23.getTitle().equalsIgnoreCase(result.getTitle()));
                        log.info("EVENTSTREAM update : {}", result);
                    }
                }catch (HttpStatusCodeException e) {
                    this.notificationError = e;
                    sharedSteps.setNotificationError(e);
                }

                break;
            default:
                throw new IllegalArgumentException();
        }
    }

    @And("si disabilita(no) (lo)(gli) stream creat(o)(i) per il comune {string} con versione V23 e apiKey aggiornata")
    public void disableStreamUpdateApiKey(String pa) {
        updateApiKeyForStream();
        disableStreamInternal(pa);
    }

    @And("si disabilita(no) (lo)(gli) stream V23 creat(o)(i) per il comune {string}")
    public void disableStream(String pa) {
        disableStreamInternal(pa);
    }

    @And("si disabilita(no) (lo)(gli) stream che non esiste e apiKey aggiornata")
    public void disableStreamNotexist() {
        updateApiKeyForStream();
        try{
            webhookB2bClient.disableEventStreamV23(UUID.randomUUID());
        }catch (HttpStatusCodeException e) {
            this.notificationError = e;
            sharedSteps.setNotificationError(e);
        }
    }


    @And("si cancella(no) (lo)(gli) stream che non esiste e apiKey aggiornata")
    public void deleteStreamNotexist() {
        updateApiKeyForStream();
        try{
            webhookB2bClient.deleteEventStreamV23(UUID.randomUUID());
        }catch (HttpStatusCodeException e) {
            this.notificationError = e;
            sharedSteps.setNotificationError(e);
        }
    }

    @And("si consuma(no) (lo)(gli) stream che non esiste e apiKey aggiornata")
    public void consumeStreamNotexist() {
        updateApiKeyForStream();
        try{
            webhookB2bClient.consumeEventStreamHttpV23(UUID.randomUUID(), null);
        }catch (HttpStatusCodeException e) {
            this.notificationError = e;
            sharedSteps.setNotificationError(e);
        }
    }

    @And("si legge(no) (lo)(gli) stream che non esiste e apiKey aggiornata")
    public void readStreamNotexist() {
        updateApiKeyForStream();
        try{
            webhookB2bClient.getEventStream(this.eventStreamListV23.get(0).getStreamId());
        }catch (HttpStatusCodeException e) {
            this.notificationError = e;
            sharedSteps.setNotificationError(e);
        }
    }

    @And("si aggiorna(no) (lo)(gli) stream che non esiste e apiKey aggiornata")
    public void updateStreamNotexist() {
        updateApiKeyForStream();
        try {
            if (streamRequestV23 == null) {
                streamRequestV23 = new StreamRequestV23();
            }
            streamRequestV23.setTitle("Stream Update");
            //TODO Verificare il corretto comportamento
            if (streamRequestV23.getGroups() == null) {
                streamRequestV23.setGroups(sharedSteps.getRequestNewApiKey().getGroups());
            }
            streamRequestV23.setEventType(StreamRequestV23.EventTypeEnum.TIMELINE);
            webhookB2bClient.updateEventStreamV23(this.eventStreamListV23.get(0).getStreamId(), streamRequestV23);
        } catch (HttpStatusCodeException e) {
            this.notificationError = e;
            sharedSteps.setNotificationError(e);
        }
    }


    @And("viene verificata la corretta cancellazione con versione {string}")
    public void verifiedTheCorrectDeletion(String versione) {
        switch (versione) {
            case "V10":
                List<it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model_v2.StreamListElement> streamListElements = webhookB2bClient.listEventStreams();
                for(StreamMetadataResponse eventStream: eventStreamList){
                    it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model_v2.StreamListElement streamListElement = streamListElements.stream().filter(elem -> elem.getStreamId() == eventStream.getStreamId()).findAny().orElse(null);
                    Assertions.assertNull(streamListElement);
                }
                break;
            case "V23":
                List<it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model_v2_3.StreamListElement> streamListElementsV23 = webhookB2bClient.listEventStreamsV23();
                for(StreamMetadataResponseV23 eventStream: eventStreamListV23){
                    it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model_v2_3.StreamListElement streamListElementV23 = streamListElementsV23.stream().filter(elem -> elem.getStreamId() == eventStream.getStreamId()).findAny().orElse(null);
                    Assertions.assertNull(streamListElementV23);
                }
                break;
            default:
                throw new IllegalArgumentException();
        }
    }

    @Then("lo stream è stato creato e viene correttamente recuperato dal sistema tramite stream id con versione {string} e apiKey aggiornata")
    public void streamBeenCreatedAndCorrectlyRetrievedByStreamIdUpdateApiKey(String versione) {
        updateApiKeyForStream();
        streamBeenCreatedAndCorrectlyRetrievedByStreamId(versione);
    }

    @Then("lo stream è stato creato e viene correttamente recuperato dal sistema tramite stream id con versione {string}")
    public void streamBeenCreatedAndCorrectlyRetrievedByStreamId(String versione) {
        StreamVersion streamVersion = StreamVersion.valueOf(versione.trim().toUpperCase());
        switch (streamVersion) {
            case V10-> {
                StreamMetadataResponse eventStream = Assertions.assertDoesNotThrow(() -> webhookB2bClient.getEventStream(this.eventStreamList.get(0).getStreamId()));
                sharedSteps.setEventStream(eventStream);
            }
            case V23 ->{
                StreamMetadataResponseV23 eventStreamV23 = Assertions.assertDoesNotThrow(() ->
                        webhookB2bClient.getEventStreamV23(this.eventStreamListV23.get(0).getStreamId()));
                sharedSteps.setEventStreamV23(eventStreamV23);
                Assertions.assertNotNull(eventStreamV23);
                Assertions.assertNotNull(eventStreamV23.getStreamId());
                log.info("EVENTSTREAM: {}", eventStreamV23);
            }
        }
    }

    @And("lo stream viene recuperato dal sistema tramite stream id con versione {string} e apiKey aggiornata")
    public void streamBeenRetrievedByStreamIdUpdateApiKey(String versione) {
        updateApiKeyForStream();
        streamBeenRetrievedByStreamId(versione);
    }

    @Then("lo stream viene recuperato dal sistema tramite stream id con versione {string}")
    public void streamBeenRetrievedByStreamId(String versione) {
        StreamVersion streamVersion = StreamVersion.valueOf(versione.trim().toUpperCase());
        try{
            switch (streamVersion) {
                case V10 -> webhookB2bClient.getEventStream(this.eventStreamList.get(0).getStreamId());
                case V23 -> webhookB2bClient.getEventStreamV23(this.eventStreamListV23.get(0).getStreamId());
            }
        }catch (HttpStatusCodeException e) {
            this.notificationError = e;
            sharedSteps.setNotificationError(e);
        }
    }

    @And("vengono letti gli eventi dello stream versione V23")
    public void readStreamEventsV23() {
        updateApiKeyForStream();
        try{
            List<ProgressResponseElementV23> progressResponseElements = webhookB2bClient.consumeEventStreamV23(this.eventStreamListV23.get(0).getStreamId(), null);
            log.info("EventProgress: " + progressResponseElements);
        }catch (HttpStatusCodeException e) {
            this.notificationError = e;
            sharedSteps.setNotificationError(e);
        }
    }


    @And("vengono letti gli eventi dello stream del {string} fino allo stato {string}")
    public void readStreamEventsState(String pa,String status) {
        setPaWebhook(pa);

        StatusElementSearchResult<it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model_v2.NotificationStatus>
                statusEventForStream = getStatusEventForStream(V10, status);

        it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model_v2.NotificationStatus
                notificationStatus = statusEventForStream.getNotificationStatus();

        it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.NotificationStatus notificationInternalStatus =
                it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.NotificationStatus.valueOf(statusEventForStream.notificationStatus.name());

        int numCheck = statusEventForStream.getNumCheck();
        int waiting = statusEventForStream.getWaiting();

        ProgressResponseElement progressResponseElement = null;
        boolean finded = false;
        for (int i = 0; i < numCheck; i++) {

            sleepTest(waiting);

            sharedSteps.setSentNotification(b2bClient.getSentNotification(sharedSteps.getSentNotification().getIun()));
            NotificationStatusHistoryElement notificationStatusHistoryElement = sharedSteps.getSentNotification().getNotificationStatusHistory().stream().filter(elem -> elem.getStatus().equals(notificationInternalStatus)).findAny().orElse(null);

            if (notificationStatusHistoryElement != null) {
                finded = true;
                break;
            }
        }

        Assertions.assertTrue(finded);
        for (int i = 0; i < 4; i++) {
            progressResponseElement = searchInWebhookV20(notificationStatus,null,0);
            log.debug("PROGRESS-ELEMENT: "+progressResponseElement);

            if (progressResponseElement != null) {
                break;
            }

            sleepTest();
        }

        try{
            Assertions.assertNotNull(progressResponseElement);
            log.info("EventProgress: " + progressResponseElement);
        }catch(AssertionFailedError assertionFailedError){
            String message = assertionFailedError.getMessage()+
                    " {IUN: "+sharedSteps.getSentNotification().getIun()+" -WEBHOOK: "+this.eventStreamList.get(0).getStreamId()+" }";
            throw new AssertionFailedError(message,assertionFailedError.getExpected(),assertionFailedError.getActual(),assertionFailedError.getCause());
        }

    }

    @And("vengono letti gli eventi dello stream del {string} fino allo stato {string} con la versione V23")
    public void readStreamEventsStateV23(String pa,String status) {

        setPaWebhook(pa);
        StatusElementSearchResult<NotificationStatus> statusEventForStream = getStatusEventForStream(StreamVersion.V23, status);
        NotificationStatus notificationStatus = statusEventForStream.getNotificationStatus();

        int numCheck = statusEventForStream.getNumCheck();
        int waiting = statusEventForStream.getWaiting();

        it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.NotificationStatus notificationInternalStatus =
                it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.NotificationStatus.valueOf(notificationStatus.name());

        ProgressResponseElementV23 progressResponseElement = null;

        boolean finded = false;
        for (int i = 0; i < numCheck; i++) {
            sleepTest(waiting);

            sharedSteps.setSentNotification(b2bClient.getSentNotification(sharedSteps.getSentNotification().getIun()));
            NotificationStatusHistoryElement notificationStatusHistoryElement = sharedSteps.getSentNotification().getNotificationStatusHistory().stream().filter(elem -> elem.getStatus().equals(notificationInternalStatus)).findAny().orElse(null);

            if (notificationStatusHistoryElement != null) {
                finded = true;
                break;
            }
        }

        Assertions.assertTrue(finded);
        for (int i = 0; i < 4; i++) {
            progressResponseElement = searchInWebhookV23(notificationStatus,null,0,0);
            log.debug("PROGRESS-ELEMENT: "+progressResponseElement);

            if (progressResponseElement != null) {
                break;
            }
            sleepTest();
        }

        try{
            Assertions.assertNotNull(progressResponseElement);
            log.info("EventProgress: " + progressResponseElement);
        }catch(AssertionFailedError assertionFailedError){
            String message = assertionFailedError.getMessage()+
                    " {IUN: "+sharedSteps.getSentNotification().getIun()+" -WEBHOOK: "+this.eventStreamListV23.get(0).getStreamId()+" }";
            throw new AssertionFailedError(message,assertionFailedError.getExpected(),assertionFailedError.getActual(),assertionFailedError.getCause());
        }

    }



    @And("vengono letti gli eventi dello stream del {string} fino allo stato {string} con versione V23 e apiKey aggiornata con position {int}")
    public void readStreamEventsStateV23(String pa,String status, Integer position) {
        updateApiKeyForStream();
        setPaWebhook(pa);

        StatusElementSearchResult<NotificationStatus> statusEventForStream = getStatusEventForStream(StreamVersion.V23, status);
        NotificationStatus notificationStatus = statusEventForStream.getNotificationStatus();

        ProgressResponseElementV23 progressResponseElement = null;
        for (int i = 0; i < eventStreamListV23.size(); i++) {
            progressResponseElement = searchInWebhookV23(notificationStatus,null,0,position);
            log.debug("PROGRESS-ELEMENT: "+progressResponseElement);
            if (progressResponseElement != null) {
                break;
            }
            sleepTest();
        }

        try{
            Assertions.assertNotNull(progressResponseElement);
            log.info("EventProgress: " + progressResponseElement);
        }catch(AssertionFailedError assertionFailedError){
            String message = assertionFailedError.getMessage()+
                    " {IUN: "+sharedSteps.getSentNotification().getIun()+" -WEBHOOK: "+this.eventStreamListV23.get(0).getStreamId()+" }";
            throw new AssertionFailedError(message,assertionFailedError.getExpected(),assertionFailedError.getActual(),assertionFailedError.getCause());
        }

    }


    @And("vengono letti gli eventi dello stream del {string} del validatore fino allo stato {string}")
    public void readStreamEventsStateValidatore(String pa,String status) {
        setPaWebhook(pa);

        StatusElementSearchResult<it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model_v2.NotificationStatus>
                statusEventForStream = getStatusEventForStream(V10, status);
        it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model_v2.NotificationStatus
                notificationStatus = statusEventForStream.getNotificationStatus();

        it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.NotificationStatus notificationInternalStatus =
                it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.NotificationStatus.valueOf(statusEventForStream.notificationStatus.name());

        ProgressResponseElement progressResponseElement = null;
        int wait = 48;
        boolean finded = false;
        for (int i = 0; i < wait; i++) {
            progressResponseElement = searchInWebhookV20(notificationStatus,null,0);
            log.debug("PROGRESS-ELEMENT: "+progressResponseElement);

            sharedSteps.setSentNotification(b2bClient.getSentNotification(sharedSteps.getSentNotification().getIun()));
            NotificationStatusHistoryElement notificationStatusHistoryElement = sharedSteps.getSentNotification().getNotificationStatusHistory().stream().filter(elem -> elem.getStatus().equals(notificationInternalStatus)).findAny().orElse(null);
            if (notificationStatusHistoryElement != null && !finded) {
                wait = i + 4;
                finded = true;
            }
            if (progressResponseElement != null) {
                break;
            }
            sleepTest(10 * 1000);
        }
        try{
            Assertions.assertNotNull(progressResponseElement);
            log.info("EventProgress: " + progressResponseElement);
        }catch(AssertionFailedError assertionFailedError){
            String message = assertionFailedError.getMessage()+
                    " {IUN: "+sharedSteps.getSentNotification().getIun()+" -WEBHOOK: "+this.eventStreamList.get(0).getStreamId()+" }";
            throw new AssertionFailedError(message,assertionFailedError.getExpected(),assertionFailedError.getActual(),assertionFailedError.getCause());
        }

    }

    @And("vengono letti gli eventi dello stream del {string} con la verifica di Allegato non trovato con la versione V23")
    public void readStreamEventsStateRefusedV23(String pa) {

        setPaWebhook(pa);
        NotificationStatus notificationStatus;
        notificationStatus = NotificationStatus.REFUSED;
        ProgressResponseElementV23 progressResponseElement = null;

        for (int i = 0; i < 4; i++) {
            progressResponseElement = searchInWebhookFileNotFoundV23(notificationStatus,null,0);
            log.debug("PROGRESS-ELEMENT: "+progressResponseElement);

            if (progressResponseElement != null) {
                break;
            }
            sleepTest();
        }

        try{
            Assertions.assertNotNull(progressResponseElement);
            log.info("EventProgress: " + progressResponseElement);
        }catch(AssertionFailedError assertionFailedError){
            String message = assertionFailedError.getMessage()+
                    " {IUN: "+sharedSteps.getSentNotification().getIun()+" -WEBHOOK: "+this.eventStreamList.get(0).getStreamId()+" }";
            throw new AssertionFailedError(message,assertionFailedError.getExpected(),assertionFailedError.getActual(),assertionFailedError.getCause());
        }
    }

    @And("vengono letti gli eventi dello stream del {string} con la verifica di Allegato non trovato")
    public void readStreamEventsStateRefused(String pa) {

        setPaWebhook(pa);
        NotificationStatus notificationStatus;
        notificationStatus = NotificationStatus.REFUSED;
        ProgressResponseElement progressResponseElement = null;

        for (int i = 0; i < 4; i++) {
            progressResponseElement = searchInWebhookFileNotFound(notificationStatus,null,0);
            log.debug("PROGRESS-ELEMENT: "+progressResponseElement);

            if (progressResponseElement != null) {
                break;
            }
            sleepTest();
        }

        try{
            Assertions.assertNotNull(progressResponseElement);
            log.info("EventProgress: " + progressResponseElement);
        }catch(AssertionFailedError assertionFailedError){
            String message = assertionFailedError.getMessage()+
                    " {IUN: "+sharedSteps.getSentNotification().getIun()+" -WEBHOOK: "+this.eventStreamList.get(0).getStreamId()+" }";
            throw new AssertionFailedError(message,assertionFailedError.getExpected(),assertionFailedError.getActual(),assertionFailedError.getCause());
        }
    }


    @Then("vengono letti gli eventi dello stream del {string} fino all'elemento di timeline {string}")
    public void readStreamTimelineElement(String pa,String timelineEventCategory) {
        setPaWebhook(pa);

        TimelineElementSearchResult<TimelineElementCategoryV20> timelineForStream = getTimelineEventForStream(V10, timelineEventCategory);
        TimelineElementCategoryV20 timelineElementCategory = timelineForStream.getTimelineElementCategory();
        int numCheck = timelineForStream.getNumCheck();
        int waiting = timelineForStream.getWaiting();

        ProgressResponseElement progressResponseElement = null;

        it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementCategoryV23 timelineElementInternalCategory =
                it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementCategoryV23.valueOf(timelineElementCategory.name());

        boolean finish = checkInternalTimeline(timelineElementCategory.name(),numCheck,waiting);
        Assertions.assertTrue(finish);

        for (int i = 0; i < 4; i++) {
            progressResponseElement = searchInWebhookV20(timelineElementCategory,null,0);
            log.debug("PROGRESS-ELEMENT: "+progressResponseElement);

            if (progressResponseElement != null) {
                break;
            }
            sleepTest();
        }
        try{
            Assertions.assertNotNull(progressResponseElement);
            ProgressResponseElement finalProgressResponseElement = progressResponseElement;
            Assertions.assertFalse(sharedSteps.getSentNotification()
                    .getTimeline()
                    .stream()
                    .filter(elem -> elem.getCategory().equals(timelineElementInternalCategory)
                            && elem.getTimestamp().truncatedTo(ChronoUnit.SECONDS).equals(finalProgressResponseElement.getTimestamp().truncatedTo(ChronoUnit.SECONDS)))
                    .findAny()
                    .isEmpty());
            log.info("EventProgress: " + progressResponseElement);
            sharedSteps.setProgressResponseElement(progressResponseElement);
        }catch(AssertionFailedError assertionFailedError){
            String message = assertionFailedError.getMessage()+
                    "{IUN: "+sharedSteps.getSentNotification().getIun()+" -WEBHOOK: "+this.eventStreamList.get(0).getStreamId()+" }";
            throw new AssertionFailedError(message,assertionFailedError.getExpected(),assertionFailedError.getActual(),assertionFailedError.getCause());
        }
    }


    @Then("vengono letti gli eventi dello stream del {string} fino all'elemento di timeline {string} con la versione V23")
    public void readStreamTimelineElementV23(String pa,String timelineEventCategory) {
        setPaWebhook(pa);

        TimelineElementSearchResult<TimelineElementCategoryV23> timelineForStream = getTimelineEventForStream(StreamVersion.V23, timelineEventCategory);
        TimelineElementCategoryV23 timelineElementCategory = timelineForStream.getTimelineElementCategory();
        int numCheck = timelineForStream.getNumCheck();
        int waiting = timelineForStream.getWaiting();

        it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementCategoryV23 timelineElementInternalCategory =
                it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementCategoryV23.valueOf(timelineElementCategory.name());

        ProgressResponseElementV23 progressResponseElement = null;
        boolean finish = checkInternalTimeline(timelineElementCategory.name(),numCheck,waiting);
        Assertions.assertTrue(finish);

        for (int i = 0; i < 4; i++) {
            progressResponseElement = searchInWebhookV23(timelineElementCategory,null,0,0);
            log.debug("PROGRESS-ELEMENT: "+progressResponseElement);

            if (progressResponseElement != null) {
                break;
            }
            sleepTest();
        }
        try{
            Assertions.assertNotNull(progressResponseElement);
            Assertions.assertEquals(progressResponseElement.getElement().getTimestamp().truncatedTo(ChronoUnit.SECONDS),
                    sharedSteps.getSentNotification().getTimeline().stream()
                            .filter(elem -> elem.getCategory().equals(timelineElementInternalCategory)).findAny().get().getTimestamp().truncatedTo(ChronoUnit.SECONDS));
            log.info("EventProgress: " + progressResponseElement);

        }catch(AssertionFailedError assertionFailedError){
            String message = assertionFailedError.getMessage()+
                    "{IUN: "+sharedSteps.getSentNotification().getIun()+" -WEBHOOK: "+this.eventStreamListV23.get(0).getStreamId()+" }";
            throw new AssertionFailedError(message,assertionFailedError.getExpected(),assertionFailedError.getActual(),assertionFailedError.getCause());
        }
    }

    @Then("verifica non presenza di eventi nello stream del {string}")
    public void readStreamTimelineElementNotPresent(String pa) {
        verifyNotEventInStream(pa, V10);
    }

    @Then("si verifica che non siano presenti eventi nello stream v23 del {string}")
    public void readStreamTimelineElementNotPresentV23(String pa) {
        verifyNotEventInStream(pa, V23);
    }

    private void verifyNotEventInStream(String pa, StreamVersion streamVersion){
        setPaWebhook(pa);
        updateApiKeyForStream();
                switch (streamVersion){
                    case V10 -> Assertions.assertTrue(webhookB2bClient.consumeEventStream(this.eventStreamList.get(0).getStreamId(), null).isEmpty());
                    case V23,V10_V23 -> Assertions.assertTrue(webhookB2bClient.consumeEventStreamV23(this.eventStreamListV23.get(0).getStreamId(),null).isEmpty());

                }
    }

    @Then("vengono letti gli eventi dello stream del {string} fino all'elemento di timeline {string} con versione V23 e apiKey aggiornata con position {int}")
    public void readStreamTimelineElementV23(String pa,String timelineEventCategory,Integer position) {
        updateApiKeyForStream();
        setPaWebhook(pa);

        TimelineElementSearchResult<TimelineElementCategoryV23> timelineForStream = getTimelineEventForStream(StreamVersion.V23, timelineEventCategory);
        TimelineElementCategoryV23 timelineElementCategory = timelineForStream.getTimelineElementCategory();

        it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementCategoryV23 timelineElementInternalCategory =
                it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementCategoryV23.valueOf(timelineElementCategory.name());

        ProgressResponseElementV23 progressResponseElement = null;

        for (int i = 0; i < 4; i++) {
            progressResponseElement = searchInWebhookV23(timelineElementCategory,null,0, position);
            log.debug("PROGRESS-ELEMENT: "+progressResponseElement);

            if (progressResponseElement != null) {
                break;
            }
            sleepTest();
        }
        try{
            Assertions.assertNotNull(progressResponseElement);
            progressResponseElementResultV23 = progressResponseElement;
            //TODO Verificare...
            Assertions.assertEquals(progressResponseElement.getElement().getTimestamp().truncatedTo(ChronoUnit.SECONDS),
                    sharedSteps.getSentNotification().getTimeline().stream()
                            .filter(elem -> elem.getCategory().equals(timelineElementInternalCategory)).findAny().get().getTimestamp().truncatedTo(ChronoUnit.SECONDS));
            log.info("EventProgress: " + progressResponseElement);
            sharedSteps.setProgressResponseElementV23(progressResponseElement);
            //sharedSteps.getTimelineElementV23().getDetails();
        }catch(AssertionFailedError assertionFailedError){
            String message = assertionFailedError.getMessage()+
                    "{IUN: "+sharedSteps.getSentNotification().getIun()+" -WEBHOOK: "+this.eventStreamListV23.get(0).getStreamId()+" }";
            throw new AssertionFailedError(message,assertionFailedError.getExpected(),assertionFailedError.getActual(),assertionFailedError.getCause());
        }
    }

    @Then("non ci sono nuovi eventi nello stream")
    public void noyReadStreamTimelineElementV23() {
        Assertions.assertNull(progressResponseElementResultV23);
    }



    @Then("Si verifica che l'elemento di timeline REFINEMENT abbia il timestamp uguale a quella presente nel webhook")
    public void readStreamTimelineElementAndVerifyDate() {
        OffsetDateTime EventTimestamp=null;
        OffsetDateTime NotificationTimestamp=null;
        try{
            Assertions.assertNotNull(progressResponseElementList);

            EventTimestamp = progressResponseElementList.stream().filter(elem -> elem.getTimelineEventCategory().equals( it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model_v2.TimelineElementCategoryV20.REFINEMENT)).findAny().get().getTimestamp();
            NotificationTimestamp =sharedSteps.getSentNotification().getTimeline().stream().filter(elem -> elem.getCategory().equals( it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementCategoryV23.SCHEDULE_REFINEMENT)).findAny().get().getDetails().getSchedulingDate();
            log.info("event timestamp : {}",EventTimestamp);
            log.info("notification timestamp : {}",NotificationTimestamp);

            Assertions.assertEquals(EventTimestamp,NotificationTimestamp);

        }catch(AssertionFailedError assertionFailedError){
            String message = assertionFailedError.getMessage()+
                    "{IUN: "+sharedSteps.getSentNotification().getIun()+" -WEBHOOK: "+this.eventStreamList.get(0).getStreamId()+" }";
            throw new AssertionFailedError(message,assertionFailedError.getExpected(),assertionFailedError.getActual(),assertionFailedError.getCause());
        }
    }

    @Then("Si verifica che l'elemento di timeline REFINEMENT abbia il timestamp uguale a quella presente nel webhook con la versione V23")
    public void readStreamTimelineElementAndVerifyDateV23() {
        OffsetDateTime EventTimestamp=null;
        OffsetDateTime NotificationTimestamp=null;
        try{
            Assertions.assertNotNull(progressResponseElementListV23);
            //TODO Verificare...
            EventTimestamp = progressResponseElementListV23.stream().filter(elem -> elem.getElement().getCategory().equals(TimelineElementCategoryV23.REFINEMENT)).findAny().get().getElement().getTimestamp();
            //EventTimestamp = progressResponseElementListV23.stream().filter(elem -> elem.getTimelineEventCategory().equals(TimelineElementCategoryV23.REFINEMENT)).findAny().get().getTimestamp();
            NotificationTimestamp =sharedSteps.getSentNotification().getTimeline().stream().filter(elem -> elem.getCategory().equals( it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementCategoryV23.SCHEDULE_REFINEMENT)).findAny().get().getDetails().getSchedulingDate();
            log.info("event timestamp : {}",EventTimestamp);
            log.info("notification timestamp : {}",NotificationTimestamp);

            Assertions.assertEquals(EventTimestamp,NotificationTimestamp);

        }catch(AssertionFailedError assertionFailedError){
            String message = assertionFailedError.getMessage()+
                    "{IUN: "+sharedSteps.getSentNotification().getIun()+" -WEBHOOK: "+this.eventStreamListV23.get(0).getStreamId()+" }";
            throw new AssertionFailedError(message,assertionFailedError.getExpected(),assertionFailedError.getActual(),assertionFailedError.getCause());
        }
    }


    @Then("Si verifica che l'elemento di timeline {string} dello stream di {string} non abbia il timestamp uguale a quella della notifica")
    public void readStreamTimelineElementAndVerify(String timelineEventCategory,String pa) {
        //il controllo viene già fatto però ATTENZIONE era fatto in maniera errata
        readStreamTimelineElement(pa,timelineEventCategory);
    }

    @Then("Si verifica che l'elemento di timeline {string} dello stream di {string} non abbia il timestamp uguale a quella della notifica con la versione V23")
    public void readStreamTimelineElementAndVerifyV23(String timelineEventCategory,String pa) {
        //Il controllo viene effettuato
        readStreamTimelineElementV23(pa,timelineEventCategory);
    }

    private <T> PnPollingWebhook getPnPollingWebhook(T timeLineOrStatus) {
        PnPollingWebhook pnPollingWebhook = new PnPollingWebhook();
        if(timeLineOrStatus instanceof TimelineElementCategoryV20) {
            pnPollingWebhook.setTimelineElementCategoryV20((TimelineElementCategoryV20) timeLineOrStatus);
            progressResponseElementList.clear();
            pnPollingWebhook.setProgressResponseElementListV20(progressResponseElementList);
        }else if(timeLineOrStatus instanceof it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model_v2.NotificationStatus) {
            pnPollingWebhook.setNotificationStatusV20((it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model_v2.NotificationStatus) timeLineOrStatus);
            progressResponseElementList.clear();
            pnPollingWebhook.setProgressResponseElementListV20(progressResponseElementList);
        } else if(timeLineOrStatus instanceof TimelineElementCategoryV23) {
            pnPollingWebhook.setTimelineElementCategoryV23((TimelineElementCategoryV23) timeLineOrStatus);
            progressResponseElementListV23.clear();
            pnPollingWebhook.setProgressResponseElementListV23(progressResponseElementListV23);
        }else if(timeLineOrStatus instanceof it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model_v2_3.NotificationStatus){
            pnPollingWebhook.setNotificationStatusV23((it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model_v2_3.NotificationStatus) timeLineOrStatus);
            progressResponseElementListV23.clear();
            pnPollingWebhook.setProgressResponseElementListV23(progressResponseElementListV23);
        } else{
            throw new IllegalArgumentException();
        }
        return pnPollingWebhook;
    }

    private <T> ProgressResponseElement searchInWebhookV20(T timeLineOrStatus, String lastEventId, int deepCount) {
        PnPollingWebhook pnPollingWebhook = getPnPollingWebhook(timeLineOrStatus);
        PnPollingParameter pnPollingParameter = PnPollingParameter.builder()
                .value("WEBHOOK")
                .pnPollingWebhook(pnPollingWebhook)
                .deepCount(deepCount)
                .lastEventId(lastEventId)
                .streamId(eventStreamList.get(0).getStreamId())
                .build();
        PnPollingServiceWebhookV20 webhookV20 = (PnPollingServiceWebhookV20) sharedSteps.getPollingFactory().getPollingService(PnPollingStrategy.WEBHOOK_V20);
        PnPollingResponseV20 pnPollingResponseV20 = webhookV20.waitForEvent(sharedSteps.getSentNotification().getIun(), pnPollingParameter);

        log.info("WEBHOOK_PROGRESS_RESPONSE_ELEMENT_V20: " + pnPollingResponseV20.getProgressResponseElementV20());
        if(pnPollingResponseV20.getProgressResponseElementV20() != null) {
            sharedSteps.setProgressResponseElements(pnPollingResponseV20.getProgressResponseElementListV20());
            return pnPollingResponseV20.getProgressResponseElementV20();
        }
        return null;
    }

    private <T> ProgressResponseElementV23 searchInWebhookV23(T timeLineOrStatus,String lastEventId, int deepCount, int position) {
        PnPollingWebhook pnPollingWebhook = getPnPollingWebhook(timeLineOrStatus);
        PnPollingServiceWebhookV23 webhookV23 = (PnPollingServiceWebhookV23) sharedSteps.getPollingFactory().getPollingService(PnPollingStrategy.WEBHOOK_V23);
        PnPollingResponseV23 pnPollingResponseV23 = webhookV23.waitForEvent(sharedSteps.getSentNotification().getIun(),
                PnPollingParameter.builder()
                        .value("WEBHOOK")
                        .pnPollingWebhook(pnPollingWebhook)
                        .deepCount(deepCount)
                        .lastEventId(lastEventId)
                        .streamId(eventStreamListV23.get(position).getStreamId())
                        .build());

        log.info("WEBHOOK_PROGRESS_RESPONSE_ELEMENT_V23: " + pnPollingResponseV23.getProgressResponseElementV23());
        if(pnPollingResponseV23.getProgressResponseElementListV23() != null) {
            sharedSteps.setProgressResponseElementsV23(pnPollingResponseV23.getProgressResponseElementListV23());
            return pnPollingResponseV23.getProgressResponseElementV23();
        }
        return null;
    }

    private <T> ProgressResponseElement searchInWebhookFileNotFound(T timeLineOrStatus,String lastEventId, int deepCount){

        TimelineElementCategoryV23 timelineElementCategory = null;
        NotificationStatus notificationStatus = null;
        if(timeLineOrStatus instanceof TimelineElementCategoryV23){
            timelineElementCategory = (TimelineElementCategoryV23)timeLineOrStatus;
        }else if(timeLineOrStatus instanceof NotificationStatus){
            notificationStatus = (NotificationStatus)timeLineOrStatus;
        }else{
            throw new IllegalArgumentException();
        }
        ProgressResponseElement progressResponseElement = null;
        ResponseEntity<List<ProgressResponseElement>> listResponseEntity = webhookB2bClient.consumeEventStreamHttp(this.eventStreamList.get(0).getStreamId(), lastEventId);
        int retryAfter = Integer.parseInt(listResponseEntity.getHeaders().get("retry-after").get(0));
        List<ProgressResponseElement> progressResponseElements = listResponseEntity.getBody();
        if(deepCount >= 200){
            throw new IllegalStateException("LOP: PROGRESS-ELEMENTS: "+progressResponseElements
                    +" WEBHOOK: "+this.eventStreamList.get(0).getStreamId()+" IUN: "+sharedSteps.getSentNotification().getIun()+" DEEP: "+deepCount);
        }
        ProgressResponseElement lastProgress = null;
        for(ProgressResponseElement elem: progressResponseElements){
            if("REFUSED".equalsIgnoreCase(elem.getNewStatus().getValue()) && elem.getValidationErrors() != null && elem.getValidationErrors().size()>0){
                if (elem.getValidationErrors().get(0).getErrorCode()!= null && "FILE_NOTFOUND".equalsIgnoreCase(elem.getValidationErrors().get(0).getErrorCode()) )
                    progressResponseElement = elem;
                break;
            }
        }//for
        return progressResponseElement;
    }//searchInWebhookTimelineElement

    private <T> ProgressResponseElementV23 searchInWebhookFileNotFoundV23(T timeLineOrStatus,String lastEventId, int deepCount){

        TimelineElementCategoryV23 timelineElementCategory = null;
        NotificationStatus notificationStatus = null;
        if(timeLineOrStatus instanceof TimelineElementCategoryV23){
            timelineElementCategory = (TimelineElementCategoryV23)timeLineOrStatus;
        }else if(timeLineOrStatus instanceof NotificationStatus){
            notificationStatus = (NotificationStatus)timeLineOrStatus;
        }else{
            throw new IllegalArgumentException();
        }
        ProgressResponseElementV23 progressResponseElement = null;
        ResponseEntity<List<ProgressResponseElementV23>> listResponseEntity = webhookB2bClient.consumeEventStreamHttpV23(this.eventStreamListV23.get(0).getStreamId(), lastEventId);
        int retryAfter = Integer.parseInt(listResponseEntity.getHeaders().get("retry-after").get(0));
        List<ProgressResponseElementV23> progressResponseElements = listResponseEntity.getBody();
        if(deepCount >= 200){
            throw new IllegalStateException("LOP: PROGRESS-ELEMENTS: "+progressResponseElements
                    +" WEBHOOK: "+this.eventStreamListV23.get(0).getStreamId()+" IUN: "+sharedSteps.getSentNotification().getIun()+" DEEP: "+deepCount);
        }
        ProgressResponseElementV23 lastProgress = null;
        for(ProgressResponseElementV23 elem: progressResponseElements){
            if("REFUSED".equalsIgnoreCase(elem.getNewStatus().getValue()) ){
                //TODO Verificare se Corretto
                break;
            }
        }//for

        //TODO Verificare il corretto comportamento...
        /**
            ProgressResponseElementV23 lastProgress = null;
            for(ProgressResponseElementV23 elem: progressResponseElements){
                if("REFUSED".equalsIgnoreCase(elem.getNewStatus().getValue()) && elem.getValidationErrors() != null && elem.getValidationErrors().size()>0){
                    if (elem.getValidationErrors().get(0).getErrorCode()!= null && "FILE_NOTFOUND".equalsIgnoreCase(elem.getValidationErrors().get(0).getErrorCode()) )
                        progressResponseElement = elem;
                        break;
                }
            }//for
            **/
        return progressResponseElement;
    }//searchInWebhookTimelineElement


    @And("{string} legge la notifica")
    public void userReadNotification(String recipient) {
        sharedSteps.selectUser(recipient);
        Assertions.assertDoesNotThrow(() -> webRecipientClient.getReceivedNotification(sharedSteps.getSentNotification().getIun(), null));
        sleepTest(sharedSteps.getWorkFlowWait());
    }

    @And("{string} legge la notifica dopo i 10 giorni")
    public void userReadNotificationAfterTot(String recipient) {
        sharedSteps.selectUser(recipient);
        sleepTest(sharedSteps.getSchedulingDaysSuccessAnalogRefinement().toMillis());
        Assertions.assertDoesNotThrow(() -> {
            webRecipientClient.getReceivedNotification(sharedSteps.getSentNotification().getIun(), null);
        });
        sleepTest(sharedSteps.getWorkFlowWait());
    }

    @Then("si verifica nello stream del {string} che la notifica abbia lo stato VIEWED con versione {string}")
    public void checkViewedStateV23(String pa, String versione) {
        sleepTest(sharedSteps.getWait()*2);
        setPaWebhook(pa);
        switch (versione) {
            case "V10":
                ProgressResponseElement progressResponseElement = searchInWebhookV20(NotificationStatus.VIEWED, null, 0);
                Assertions.assertNotNull(progressResponseElement);
                break;
            case "V23":
                ProgressResponseElementV23 progressResponseElementV23 = searchInWebhookV23(NotificationStatus.VIEWED, null, 0,0);
                Assertions.assertNotNull(progressResponseElementV23);
                break;
            default:
                throw new IllegalArgumentException();
        }

    }

    @Then("si verifica nello stream del {string} che la notifica abbia lo stato VIEWED")
    public void checkViewedState(String pa) {
        sleepTest((sharedSteps.getWait()*2));

        setPaWebhook(pa);
        ProgressResponseElement progressResponseElement = searchInWebhookV20(it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model_v2.NotificationStatus.VIEWED, null, 0);
        Assertions.assertNotNull(progressResponseElement);
    }


    //TODO: old version
    @Then("l'ultima creazione ha prodotto un errore con status code {string}")
    public void lastCreationProducedAnErrorWithStatusCode(String statusCode) {
        List<it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model_v2.StreamListElement> streamListElements = webhookB2bClient.listEventStreams();
        System.out.println("streamListElements: "+streamListElements.size());
        System.out.println("eventStreamList: "+eventStreamList.size());
        System.out.println("requestNumber: "+requestNumber);
        Assertions.assertTrue((this.notificationError != null) &&
                (this.notificationError.getStatusCode().toString().substring(0,3).equals(statusCode)) && (eventStreamList.size() == (requestNumber-1)));
    }

    @Given("vengono cancellati tutti gli stream presenti del {string} con versione {string}")
    public void deleteAll(String pa,String versione) {
        setPaWebhook(pa);
        switch (versione) {
            case "V10":
                List<it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model_v2.StreamListElement> streamListElements = webhookB2bClient.listEventStreams();
                for(it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model_v2.StreamListElement elem: streamListElements){
                    System.out.println(elem);
                    deleteStreamWrapper(V10,pa,elem.getStreamId());
                }
                break;
            case "V23":
                List<it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model_v2_3.StreamListElement> streamListElementsV23 = webhookB2bClient.listEventStreamsV23();
                for(it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model_v2_3.StreamListElement elem: streamListElementsV23){
                    System.out.println(elem);
                    deleteStreamWrapper(V23,pa,elem.getStreamId());
                }
                break;
            default:
                throw new IllegalArgumentException();
        }
    }


    @And("vengono prodotte le evidenze: metadati, requestID, IUN e stati")
    public void evidenceProducedIunRequestIdAndState() {
        log.info("METADATI: "+'\n'+sharedSteps.getNewNotificationResponse());
        log.info("REQUEST-ID: "+'\n'+sharedSteps.getNewNotificationResponse().getNotificationRequestId());
        log.info("IUN: "+'\n'+sharedSteps.getSentNotification().getIun());
        for(ProgressResponseElement element: progressResponseElementList){
            log.info("EVENT: "+'\n'+element.getTimelineEventCategory()+" "+element.getTimestamp());
        }

    }

    @Then("viene verificato che il ProgressResponseElement del webhook abbia un EventId incrementale e senza duplicati V23")
    public void vieneVerificatoCheIlProgressResponseElementIdDelWebhookSiaIncrementaleESenzaDuplicatiV23() {
        List<ProgressResponseElementV23> progressResponseElements = sharedSteps.getProgressResponseElementsV23();
        Assertions.assertNotNull(progressResponseElements);
        boolean counterIncrement = true ;
        int lastEventID = SharedSteps.lastEventID;
        //logger.info("ELEMENTI NEL WEBHOOK LAST EVENT ID1: "+lastEventID);
        for(ProgressResponseElementV23 elem: progressResponseElements){
            if (lastEventID==0){
                lastEventID = Integer.parseInt(elem.getEventId());
                continue;
            }
            if (Integer.parseInt(elem.getEventId())<=lastEventID){
                counterIncrement = false;
                break;
            }else {
                lastEventID = Integer.parseInt(elem.getEventId());
            }
        }//for
        try{
            Assertions.assertTrue(counterIncrement);
        }catch (AssertionFailedError assertionFailedError){
            throw new AssertionFailedError(assertionFailedError.getMessage()+" PROGRESS-ELEMENT: \n"+progressResponseElements);
        }

        SharedSteps.lastEventID=lastEventID;
        //logger.info("ELEMENTI NEL WEBHOOK LAST EVENT ID2: "+lastEventID);
    }


    @Then("viene verificato che il ProgressResponseElement del webhook abbia un EventId incrementale e senza duplicati")
    public void vieneVerificatoCheIlProgressResponseElementIdDelWebhookSiaIncrementaleESenzaDuplicati() {

        List<ProgressResponseElement> progressResponseElements = sharedSteps.getProgressResponseElements();
        Assertions.assertNotNull(progressResponseElements);
        boolean counterIncrement = true ;
        int lastEventID = SharedSteps.lastEventID;
        for(ProgressResponseElement elem: progressResponseElements){
            if (lastEventID==0){
                lastEventID = Integer.parseInt(elem.getEventId());
                continue;
            }
            if (Integer.parseInt(elem.getEventId())<=lastEventID){
                counterIncrement = false;
                break;
            }else {
                lastEventID = Integer.parseInt(elem.getEventId());
            }
        }//for
        try{
            Assertions.assertTrue(counterIncrement);
        }catch (AssertionFailedError assertionFailedError){
            throw new AssertionFailedError(assertionFailedError.getMessage()+" PROGRESS-ELEMENT: \n"+progressResponseElements);
        }

        SharedSteps.lastEventID = lastEventID;
    }

    @And("vengono letti gli eventi dello stream che contenga {int} eventi con la versione {string}")
    public void readStreamNumberEventsV23(Integer numEventi, String versione) {
        switch (versione) {
            case "V10":
                Assertions.assertDoesNotThrow(() -> {
                    List<ProgressResponseElement> progressResponseElements = webhookB2bClient.consumeEventStream(this.eventStreamList.get(0).getStreamId(), null);
                    log.info("EventProgress: " + progressResponseElements);

                    Assertions.assertEquals(progressResponseElements.size() , numEventi);
                    System.out.println("ELEMENTI NEL WEBHOOK: "+progressResponseElements.size());
                });
                break;
            case "V23":
                Assertions.assertDoesNotThrow(() -> {
                    List<ProgressResponseElementV23> progressResponseElements = webhookB2bClient.consumeEventStreamV23(this.eventStreamListV23.get(0).getStreamId(), null);
                    log.info("EventProgress: " + progressResponseElements);

                    Assertions.assertEquals(progressResponseElements.size() , numEventi);
                    System.out.println("ELEMENTI NEL WEBHOOK: "+progressResponseElements.size());
                });
                break;
            default:
                throw new IllegalArgumentException();
        }
    }


    @And("vengono letti gli eventi dello stream che contenga {int} eventi")
    public void readStreamNumberEvents(Integer numEventi) {
        Assertions.assertDoesNotThrow(() -> {
            List<ProgressResponseElement> progressResponseElements = webhookB2bClient.consumeEventStream(this.eventStreamList.get(0).getStreamId(), null);
            log.info("EventProgress: " + progressResponseElements);

            Assertions.assertEquals(progressResponseElements.size() , numEventi);
            System.out.println("ELEMENTI NEL WEBHOOK: "+progressResponseElements.size());
        });

    }



    @And("verifica corrispondenza tra i detail del webhook e quelli della timeline")
    public void verificaCorrispondenzaTraIDetailDelWebhookEQuelliDellaTimeline() throws IllegalAccessException, JsonProcessingException {

        it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementDetailsV23 timelineElementDetails = sharedSteps.getTimelineElementV23().getDetails();//PERCHè NON TENERLO NELLA CLASSE ?!

        it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model_v2_3.TimelineElementDetailsV23 timelineElementWebhookDetails = sharedSteps.getProgressResponseElementV23().getElement().getDetails();
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String json = ow.writeValueAsString(timelineElementDetails);
        System.out.println(json);

        ObjectWriter ow1 = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String json1 = ow1.writeValueAsString(timelineElementDetails);
        System.out.println(json1);

        ObjectMapper mapper = new ObjectMapper();
        Assertions.assertEquals(mapper.readTree(json), mapper.readTree(json1));

    }

    @Then("verifica deanonimizzazione degli eventi di timeline con delega {string} analogico")
    public void verificaDeanonimizzazioneDegliEventiDiTimelineAnalogico(String delega) {
        TimelineElementDetailsV23 timelineElementWebhookDetails = sharedSteps.getProgressResponseElementV23().getElement().getDetails();
        Assertions.assertNotNull(timelineElementWebhookDetails);
        Assertions.assertNotNull(timelineElementWebhookDetails.getPhysicalAddress().getAddress());
        Assertions.assertNotNull(timelineElementWebhookDetails.getPhysicalAddress().getMunicipality());
        Assertions.assertNotNull(timelineElementWebhookDetails.getPhysicalAddress().getProvince());
        Assertions.assertNotNull(timelineElementWebhookDetails.getPhysicalAddress().getZip());

        verificaDeanonimizzazioneDegliEventiDiTimelinePresenzaDelega(timelineElementWebhookDetails,delega);
    }

    @Then("verifica deanonimizzazione degli eventi di timeline con delega {string} digitale")
    public void verificaDeanonimizzazioneDegliEventiDiTimelineDigitale(String delega) {
        TimelineElementDetailsV23 timelineElementWebhookDetails = sharedSteps.getProgressResponseElementV23().getElement().getDetails();
        Assertions.assertNotNull(timelineElementWebhookDetails.getDigitalAddress());
        verificaDeanonimizzazioneDegliEventiDiTimelinePresenzaDelega(timelineElementWebhookDetails,delega);
    }

    public void verificaDeanonimizzazioneDegliEventiDiTimelinePresenzaDelega (TimelineElementDetailsV23 timelineElementWebhookDetails,String delega) {
        if ("SI".equalsIgnoreCase(delega)) {
            Assertions.assertNotNull(timelineElementWebhookDetails.getDelegateInfo());
            Assertions.assertNotNull(timelineElementWebhookDetails.getDelegateInfo().getTaxId());
            Assertions.assertNotNull(timelineElementWebhookDetails.getDelegateInfo().getDenomination());
        }
    }


    @When("vengono letti gli eventi di timeline dello stream con versione {string} -Cross Versioning")
    public void vengonoLettiGliEventiDiTimelineDelloStreamDel(String versione) {
        updateApiKeyForStream();
        try{
            switch (versione) {
                case "V10":
                    webhookB2bClient.consumeEventStreamHttp(this.eventStreamListV23.get(0).getStreamId(), null);
                    break;
                case "V23":
                    webhookB2bClient.consumeEventStreamHttpV23(this.eventStreamList.get(0).getStreamId(), null);
                    break;
                default:
                    throw new IllegalArgumentException();
            }
        }catch (HttpStatusCodeException e) {
            this.notificationError = e;
            sharedSteps.setNotificationError( e);
        }
    }

    /*********************************************************************
     * BUSINESS CODE TODO: Externalise
     *********************************************************************/
    @Data
    private static class  TimelineElementSearchResult <T>{
        public T timelineElementCategory;
        int numCheck;
        int waiting;
    }

    @Data
    private static class  StatusElementSearchResult <T>{
        public T notificationStatus;
        int numCheck;
        int waiting;
    }

    @SuppressWarnings("unchecked")
    private <T> TimelineElementSearchResult<T> getTimelineEventForStream(StreamVersion streamVersion,String timelineEventCategory){
        timelineEventCategory = timelineEventCategory.trim().toUpperCase();
        TimingForPolling.TimingResult timingForElement = timingForPolling.getTimingForElement(timelineEventCategory);
        try{
            switch (streamVersion){
                case V10 -> {
                    TimelineElementSearchResult<TimelineElementCategoryV20> result = new TimelineElementSearchResult<>();

                    result.setTimelineElementCategory(TimelineElementCategoryV20.valueOf(timelineEventCategory));
                    result.setWaiting(timingForElement.waiting());
                    result.setNumCheck(timingForElement.numCheck());
                    return (TimelineElementSearchResult<T>) result;
                }
                case V23 -> {
                    TimelineElementSearchResult<TimelineElementCategoryV23> result = new TimelineElementSearchResult<>();

                    result.setTimelineElementCategory(TimelineElementCategoryV23.valueOf(timelineEventCategory));
                    result.setWaiting(timingForElement.waiting());
                    result.setNumCheck(timingForElement.numCheck());
                    return (TimelineElementSearchResult<T>) result;
                }
            }
        }catch (ClassCastException classCastException){
            log.error("Wrong type t for streamVersion {}, error in cast {}", streamVersion, classCastException.getMessage());
        }

        throw new IllegalArgumentException();
    }

    @SuppressWarnings("unchecked")
    private  <T> StatusElementSearchResult<T> getStatusEventForStream(StreamVersion streamVersion,String notificationStatusName){
        notificationStatusName = notificationStatusName.trim().toUpperCase();
        TimingForPolling.TimingResult timingForElement = timingForPolling.getTimingForElement(notificationStatusName);
        try{
            switch (streamVersion){
                case V10 -> {
                    StatusElementSearchResult<it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model_v2.NotificationStatus>
                            result = new StatusElementSearchResult<>();

                    result.setNotificationStatus(
                            it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model_v2.NotificationStatus
                                    .valueOf(notificationStatusName));
                    result.setWaiting(timingForElement.waiting());
                    result.setNumCheck(timingForElement.numCheck());
                    return (StatusElementSearchResult<T>) result;

                }
                case V23 -> {
                    StatusElementSearchResult<NotificationStatus> result = new StatusElementSearchResult<>();

                    result.setNotificationStatus(NotificationStatus.valueOf(notificationStatusName));
                    result.setWaiting(timingForElement.waiting());
                    result.setNumCheck(timingForElement.numCheck());
                    return (StatusElementSearchResult<T>) result;

                }
            }
        }catch (ClassCastException classCastException){
            log.error("Wrong type t for streamVersion {}, error in cast {}", streamVersion, classCastException.getMessage());
        }

        throw new IllegalArgumentException();
    }

    private void sleepTest(){
        sleepTest(sharedSteps.getWait());
    }

    private void sleepTest(long wait){
        try {
            Thread.sleep(wait);
        } catch (InterruptedException exc) {
            throw new RuntimeException(exc);
        }
    }

    private boolean checkInternalTimeline(String timelineElementName, int numCheck,int waiting){
        it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementCategoryV23 timelineElementInternalCategory =
                it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementCategoryV23.valueOf(timelineElementName);

        boolean finish = false;
        for (int i = 0; i < numCheck; i++) {
            try {
                Thread.sleep(waiting);
            } catch (InterruptedException exc) {
                throw new RuntimeException(exc);
            }

            sharedSteps.setSentNotification(b2bClient.getSentNotification(sharedSteps.getSentNotification().getIun()));
            it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementV23 timelineElement =
                    sharedSteps.getSentNotification().getTimeline().stream().filter(elem -> elem.getCategory().equals(timelineElementInternalCategory)).findAny().orElse(null);
            if (timelineElement != null) {
                finish = true;
                break;
            }
        }
        return finish;
    }

    private void createStreamRequest(StreamVersion streamVersion, List<String> filterValues, int number, String title, String eventType){
        switch(streamVersion){
            case V10 -> {
                this.streamCreationRequestList = new LinkedList<>();
                for(int i = 0; i<number; i++){
                    StreamCreationRequest streamRequest = new StreamCreationRequest();
                    streamRequest.setTitle(title+"_"+i);
                    streamRequest.setEventType(eventType.equalsIgnoreCase("STATUS") ?
                            StreamCreationRequest.EventTypeEnum.STATUS : StreamCreationRequest.EventTypeEnum.TIMELINE);
                    streamRequest.setFilterValues(filterValues);
                    streamCreationRequestList.add(streamRequest);
                }
            }
            case V23 -> {
                this.streamCreationRequestListV23 = new LinkedList<>();
                for(int i = 0; i<number; i++){
                    StreamCreationRequestV23 streamRequest = new StreamCreationRequestV23();
                    streamRequest.setTitle(title+"_"+i);
                    streamRequest.setEventType(eventType.equalsIgnoreCase("STATUS") ?
                            StreamCreationRequestV23.EventTypeEnum.STATUS : StreamCreationRequestV23.EventTypeEnum.TIMELINE);
                    streamRequest.setFilterValues(filterValues);
                    streamCreationRequestListV23.add(streamRequest);
                }
            }
        }
    }

    private void createStream(String pa,StreamVersion streamVersion,List<String> listGroups, boolean replaceId,List<String> filteredValues, boolean forced){
        try{
            switch (streamVersion){
                case V10 -> {
                    if(this.eventStreamList == null)this.eventStreamList = new LinkedList<>();
                    //if(!forced)acquireStreamCreationSlotInternal(pa,streamCreationRequestList.size());

                    for(StreamCreationRequest request: streamCreationRequestList){
                        if (filteredValues!= null && !filteredValues.isEmpty()){
                            request.setFilterValues(filteredValues);
                        }
                        StreamMetadataResponse eventStream = webhookB2bClient.createEventStream(request);
                        this.eventStreamList.add(eventStream);
                        addStreamId(pa,eventStream.getStreamId(),streamVersion);
                    }

                }
                case V23 -> {
                    if(this.eventStreamListV23 == null)this.eventStreamListV23 = new LinkedList<>();
                    //if(!forced)acquireStreamCreationSlotInternal(pa,streamCreationRequestListV23.size());

                    for(StreamCreationRequestV23 request: streamCreationRequestListV23){
                        if (filteredValues!= null && !filteredValues.isEmpty()){
                            request.setFilterValues(filteredValues);
                        }
                        if(listGroups != null){
                            request.setGroups(listGroups);
                        }
                        if (replaceId){
                            request.setReplacedStreamId(sharedSteps.getEventStreamV23().getStreamId());
                        }


                        try {
                            StreamMetadataResponseV23 eventStream = webhookB2bClient.createEventStreamV23(request);
                            if (replaceId) {
                                StreamMetadataResponseV23 eventStreamV23 =
                                        webhookB2bClient.getEventStreamV23(this.eventStreamListV23.get(0).getStreamId());
                                sharedSteps.setEventStreamV23(eventStreamV23);
                                Assertions.assertNotNull(eventStreamV23);
                                Assertions.assertNotNull(eventStreamV23.getStreamId());
                                Assertions.assertNotNull(eventStreamV23.getDisabledDate());
                                log.info("EVENTSTREAM REPLACED: {}", eventStreamV23);

                                this.eventStreamListV23 = new LinkedList<>();
                            }
                            this.eventStreamListV23.add(eventStream);
                            addStreamId(pa, eventStream.getStreamId(), streamVersion);

                        } catch (HttpStatusCodeException e) {
                            this.notificationError = e;
                            sharedSteps.setNotificationError(e);
                        }

                    }
                }
                case V10_V23 -> {
                    if (this.eventStreamListV23 == null) this.eventStreamListV23 = new LinkedList<>();

                    StreamCreationRequestV23 request = new StreamCreationRequestV23();
                    if (filteredValues != null && !filteredValues.isEmpty()) {
                        request.setFilterValues(filteredValues);
                    }
                    if (listGroups != null) {
                        request.setGroups(listGroups);
                    }

                    if (replaceId) {
                        request.setReplacedStreamId(sharedSteps.getEventStream().getStreamId());
                    }
                    try {
                        StreamMetadataResponseV23 eventStream = webhookB2bClient.createEventStreamV23(request);

                        if (replaceId) {
                            StreamMetadataResponseV23 eventStreamV23 = Assertions.assertDoesNotThrow(() ->
                                    webhookB2bClient.getEventStreamV23(this.eventStreamList.get(0).getStreamId()));
                            sharedSteps.setEventStreamV23(eventStreamV23);
                            Assertions.assertNotNull(eventStreamV23);
                            Assertions.assertNotNull(eventStreamV23.getStreamId());
                            Assertions.assertNotNull(eventStreamV23.getDisabledDate());
                            log.info("EVENTSTREAM REPLACED: {}", eventStreamV23);
                        }
                        this.eventStreamListV23.add(eventStream);
                        addStreamId(pa, eventStream.getStreamId(), streamVersion);
                    } catch (HttpStatusCodeException e) {
                        this.notificationError = e;
                        sharedSteps.setNotificationError(e);
                    }
                }
            }
        }catch (HttpStatusCodeException e) {
            log.error("Error {} in create Stream version {}, group {}, replaceID {}, filteredValues {}",
                    e.getStatusCode(),streamVersion,listGroups,replaceId,filteredValues);
            this.notificationError = e;
            sharedSteps.setNotificationError(e);
            if(!forced)throw e;
        }
        if(!webhookTestLaunch)webhookTestLaunch = true;
    }

    private void addStreamId(String pa, UUID streamId, StreamVersion version) {
        //streamIdForPaAndVersion.put(streamId,new PnPaB2bUtils.Pair<>(pa,version));
        paStreamOwner.add(pa);
    }


    private void disableStreamInternal(String pa){
        try{
            for (StreamMetadataResponseV23 eventStream : eventStreamListV23) {
                StreamMetadataResponseV23 result = webhookB2bClient.disableEventStreamV23(eventStream.getStreamId());
                Assertions.assertNotNull(result);
                Assertions.assertNotNull(result.getDisabledDate());
                //streamIdForPaAndVersion.remove(eventStream.getStreamId());
                //releaseStreamCreationSlotInternal(pa);
            }
        }catch (HttpStatusCodeException e) {
            this.notificationError = e;
            sharedSteps.setNotificationError(e);
            log.error("ERROR IN DISABLE STREAM ");
        }
    }

    private void deleteStreamWrapper(StreamVersion streamVersion, String pa, UUID streamID){
        deleteStreamWrapperWithoutReleaseStreamCreationSlot(streamVersion,pa,streamID);
//        if(deleteStreamWrapperWithoutReleaseStreamCreationSlot(streamVersion,pa,streamID)){
//            //releaseStreamCreationSlotInternal(pa);
//            //streamIdForPaAndVersion.remove(streamID);
//        }
    }

    private boolean deleteStreamWrapperWithoutReleaseStreamCreationSlot(StreamVersion streamVersion, String pa, UUID streamID){
        try{
            switch (streamVersion){
                case V10 -> webhookB2bClient.deleteEventStream(streamID);
                case V23 ->  webhookB2bClient.deleteEventStreamV23(streamID);
            }
            return true;
        }catch (HttpStatusCodeException e){
            this.notificationError = e;
            sharedSteps.setNotificationError(e);
            log.error("ERROR IN DELETE STREAM id {} streamVersion{} pa {}",streamID,streamVersion.name(),pa);
            return false;
        }
    }

//    private void releaseStreamCreationSlotInternal(String pa){ //(String pa,int numberOfStream)
//        if(numberOfStreamSlotAcquiredForPa.containsKey(pa)){
//            if(numberOfStreamSlotAcquiredForPa.get(pa).getValue2() > 0){
//                WEBHOOKSYNCHRONIZER.releaseStreamCreationSlot(1,pa);
//            }
//            Integer numberOfStreamSlot = this.numberOfStreamSlotAcquiredForPa.get(pa).getValue2();
//            this.numberOfStreamSlotAcquiredForPa.get(pa).setValue2(numberOfStreamSlot-1);
//        }
//    }

//    private void acquireStreamCreationSlotInternal(String pa,int numberOfStream){
//        try{
//            if(!numberOfStreamSlotAcquiredForPa.containsKey(pa)){
//                this.numberOfStreamSlotAcquiredForPa.put(pa,new PnPaB2bUtils.Pair<>(
//                        WEBHOOKSYNCHRONIZER.acquireStreamCreationSlot(numberOfStream, pa),numberOfStream));
//            }else{
//                if(WEBHOOKSYNCHRONIZER.acquireStreamCreationSlot(numberOfStream, pa)){
//                    PnPaB2bUtils.Pair<Boolean, Integer> numberOfStreamPa = numberOfStreamSlotAcquiredForPa.get(pa);
//                    numberOfStreamPa.setValue2(numberOfStreamPa.getValue2()+numberOfStream);
//                    numberOfStreamSlotAcquiredForPa.put(pa,numberOfStreamPa);
//                }
//            }
//            sleepTest(800);
//        }catch (InterruptedException e) {
//            log.error("Error in acquire stream slot!!");
//            throw new RuntimeException(e);
//        }
//    }

    private List<String> getGruopForStream(String position, String pa){
        List<String> groupList = null;
        position = position.trim().toUpperCase();
        switch (position) {
            case "FIRST", "LAST" -> groupList = List.of(sharedSteps.getGroupIdByPa(pa, GroupPosition.valueOf(position)));
            case "ALL" ->  groupList = sharedSteps.getGroupAllActiveByPa(pa);
            case "NO_GROUPS" -> groupList = null;
            case "UGUALI" -> {
                Assertions.assertNotNull(sharedSteps.getRequestNewApiKey());
                groupList = sharedSteps.getRequestNewApiKey().getGroups();
            }
            case "ALTRA_PA" -> {
                if ("Comune_1".equalsIgnoreCase(pa)) {
                    Assertions.assertNotNull(sharedSteps.getGroupIdByPa("Comune_Multi", GroupPosition.FIRST));
                    groupList = List.of(sharedSteps.getGroupIdByPa("Comune_Multi", GroupPosition.FIRST));
                } else if("Comune_Multi".equalsIgnoreCase(pa) ) {
                    Assertions.assertNotNull(sharedSteps.getGroupIdByPa("Comune_1", GroupPosition.FIRST));
                    groupList = List.of(sharedSteps.getGroupIdByPa("Comune_1", GroupPosition.FIRST));
                }else{
                    Assertions.assertNotNull(sharedSteps.getGroupIdByPa("Comune_1", GroupPosition.FIRST));
                    groupList = List.of(sharedSteps.getGroupIdByPa("Comune_1", GroupPosition.FIRST));
                }
            }
            default -> throw new IllegalArgumentException();
        }
        return groupList;
    }


    private void setPaWebhook(String pa){
        switch (pa){
            case "Comune_1":
                webhookB2bClient.setApiKeys(SettableApiKey.ApiKeyType.MVP_1);
                pollingFactory.setApiKeys(SettableApiKey.ApiKeyType.MVP_1);
                sharedSteps.selectPA(pa);
                break;
            case "Comune_2":
                webhookB2bClient.setApiKeys(SettableApiKey.ApiKeyType.MVP_2);
                pollingFactory.setApiKeys(SettableApiKey.ApiKeyType.MVP_2);
                sharedSteps.selectPA(pa);
                break;
            case "Comune_Multi":
                webhookB2bClient.setApiKeys(SettableApiKey.ApiKeyType.GA);
                pollingFactory.setApiKeys(SettableApiKey.ApiKeyType.GA);
                sharedSteps.selectPA(pa);
                break;
            default:
                throw new IllegalArgumentException();
        }
    }

}
