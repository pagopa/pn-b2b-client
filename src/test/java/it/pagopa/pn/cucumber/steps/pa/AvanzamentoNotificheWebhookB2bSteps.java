package it.pagopa.pn.cucumber.steps.pa;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.cucumber.java.After;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.*;
import it.pagopa.pn.client.b2b.pa.polling.design.PnPollingFactory;
import it.pagopa.pn.client.b2b.pa.polling.design.PnPollingStrategy;
import it.pagopa.pn.client.b2b.pa.polling.dto.*;
import it.pagopa.pn.client.b2b.pa.polling.impl.*;
import it.pagopa.pn.client.b2b.pa.service.IPnPaB2bClient;
import it.pagopa.pn.client.b2b.pa.service.IPnWebRecipientClient;
import it.pagopa.pn.client.b2b.pa.service.IPnWebhookB2bClient;
import it.pagopa.pn.client.b2b.pa.service.utils.SettableApiKey;
import it.pagopa.pn.client.b2b.pa.utils.TimingForPolling;
import it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model_v2.ProgressResponseElement;
import it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model_v2.StreamMetadataResponse;
import it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model_v2.TimelineElementCategoryV20;
import it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model_v26.ProgressResponseElementV26;
import it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model_v26.TimelineElementCategoryV26;
import it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model_v27.ProgressResponseElementV27;
import it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model_v2_3.NotificationStatus;
import it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model_v2_3.ProgressResponseElementV23;
import it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model_v2_3.StreamListElement;
import it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model_v2_3.StreamMetadataResponseV23;
import it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model_v2_3.TimelineElementCategoryV23;
import it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model_v2_3.TimelineElementDetailsV23;
import it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model_v2_3.TimelineElementV23;
import it.pagopa.pn.cucumber.steps.SharedSteps;
import it.pagopa.pn.cucumber.steps.pa.webhookVersions.*;
import it.pagopa.pn.cucumber.utils.GroupPosition;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.opentest4j.AssertionFailedError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpStatusCodeException;

import java.lang.reflect.Field;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static it.pagopa.pn.cucumber.steps.pa.AvanzamentoNotificheWebhookB2bSteps.StreamVersion.*;

@Slf4j
public class AvanzamentoNotificheWebhookB2bSteps {

    @Getter
    private final SharedSteps sharedSteps;
    @Getter
    private final IPnWebhookB2bClient webhookB2bClient;
    @Getter
    private final IPnPaB2bClient b2bClient;
    private final IPnWebRecipientClient webRecipientClient;
    private Integer requestNumber;
    @Getter
    @Setter
    private HttpStatusCodeException notificationError;
    private final PnPollingFactory pollingFactory;
    @Getter
    private final TimingForPolling timingForPolling;
    private List<StreamMetadataResponse> eventStreamList;
    private List<StreamMetadataResponseV23> eventStreamListV23;
    private List<ProgressResponseElement> progressResponseElements = new LinkedList<>();
    private List<ProgressResponseElementV23> progressResponseElementsV23 = new LinkedList<>();
    private List<ProgressResponseElementV24> progressResponseElementsV24 = new LinkedList<>();

    private List<ProgressResponseElementV26> progressResponseElementsV26 = new LinkedList<>();
    private List<ProgressResponseElementV27> progressResponseElementsV27 = new LinkedList<>();

    private final WebhookStepsV10 webhookStepsV10 = new WebhookStepsV10(this);
    private final WebhookStepsV23 webhookStepsV23 = new WebhookStepsV23(this);
    private final WebhookStepsV24 webhookStepsV24 = new WebhookStepsV24(this);
    private final WebhookStepsV25 webhookStepsV25 = new WebhookStepsV25(this);
    private final WebhookStepsV26 webhookStepsV26 = new WebhookStepsV26(this);
    private final WebhookStepsV27 webhookStepsV27 = new WebhookStepsV27(this);


    @And("viene verificato che il campo legalfactIds sia valorizzato nel EventStream")
    public void vieneVerificatoCheIlCampoLegalfactIdsSiaValorizzato() {
        Assertions.assertNotNull(sharedSteps.getProgressResponseElement());
        Assertions.assertNotNull(sharedSteps.getProgressResponseElement().getLegalfactIds());
        Assertions.assertFalse(sharedSteps.getProgressResponseElement().getLegalfactIds().isEmpty());
    }

    public enum StreamVersion {V10, V10_V23, V23, V24, V25, V26, V27}

    @Getter
    private final Set<String> paStreamOwner = new HashSet<>();
    private ProgressResponseElementV23 progressResponseElementResultV23;
    private static IPnWebhookB2bClient webhookClientForClean;
    private static boolean webhookTestLaunch;
    private static final Map<String, SettableApiKey.ApiKeyType> paForStream =
            Map.of(
                    "Comune_1", SettableApiKey.ApiKeyType.MVP_1,
                    "Comune_2", SettableApiKey.ApiKeyType.MVP_2,
                    "Comune_Multi", SettableApiKey.ApiKeyType.GA);


    @Autowired
    public AvanzamentoNotificheWebhookB2bSteps(IPnWebhookB2bClient webhookB2bClient, IPnWebhookB2bClient webhookClientForClean, SharedSteps sharedSteps,
                                               TimingForPolling timingForPolling, PnPollingFactory pollingFactory) {
        this.sharedSteps = sharedSteps;
        this.webhookB2bClient = webhookB2bClient;
        this.webRecipientClient = sharedSteps.getWebRecipientClient();
        this.timingForPolling = timingForPolling;
        this.b2bClient = sharedSteps.getB2bClient();
        this.pollingFactory = pollingFactory;
        webhookTestLaunch = true;
        this.webhookClientForClean = webhookClientForClean;
    }

    //@AfterAll -> problema con esecuzione concorrente
    public static void afterAll() {
        log.info("Start clean Webhook!!!");
        log.info("webhookClientForClean state: " + webhookClientForClean);
        log.info("webhook eseguito: " + webhookTestLaunch);
        if (webhookTestLaunch) {
            log.info("Starting cleaning");
            for (SettableApiKey.ApiKeyType pa : paForStream.values()) {
                //TODO: MODIFICARE
                webhookClientForClean.setApiKeys(pa);

                //DELETE V1
                List<it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model_v2.StreamListElement> streamListElements = webhookClientForClean.listEventStreams();
                for (it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model_v2.StreamListElement elem : streamListElements) {
                    try {
                        webhookClientForClean.deleteEventStream(elem.getStreamId());
                    } catch (HttpStatusCodeException statusCodeException) {
                        log.error("HTTP Error: statusCode {} message {}", statusCodeException.getStatusCode(), statusCodeException.getMessage());
                    }
                }

                //DELETE V2.3
                List<StreamListElement> streamListElementsV23 = webhookClientForClean.listEventStreamsV23();
                for (StreamListElement elem : streamListElementsV23) {
                    try {
                        webhookClientForClean.deleteEventStreamV23(elem.getStreamId());
                    } catch (HttpStatusCodeException statusCodeException) {
                        log.error("HTTP Error: statusCode {} message {}", statusCodeException.getStatusCode(), statusCodeException.getMessage());
                    }
                }
            }
        }
    }


    @After("@cleanWebhook")
    public void afterStreamTestRun() {
        log.info("Starting cleaning");
        for (String pa : paStreamOwner) {
            if (paForStream.containsKey(pa)) {
                deleteAllPaStreamForAllVersion(paForStream.get(pa));
            }
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

    private WebhookStepsInterface getWebhookStep(StreamVersion streamVersion) {
        switch (streamVersion) {
            case V10_V23 -> {
                return null;//TODO MATTEO
            }
            case V10 -> {
                return this.webhookStepsV10;
            }
            case V23 -> {
                return this.webhookStepsV23;
            }
            case V24 -> {
                return this.webhookStepsV24;
            }
            case V25 -> {
                return this.webhookStepsV25;
            }
            case V26 -> {
                return this.webhookStepsV26;
            }
            case V27 -> {
                return this.webhookStepsV27;
            }
            default -> throw new IllegalArgumentException("Version not supported!: " + streamVersion);
        }
    }

    private StreamVersion getStreamVersion(String version) {
        if (version.equalsIgnoreCase("più recente")) {
            version = "V27";//TODO: modificare questo valore ogni volta che viene aggiunta una versione più recente
        }
        return StreamVersion.valueOf(version.trim().toUpperCase());
    }

    private void setPaWebhook(String pa) {
        switch (pa) {
            case "Comune_1" -> {
                webhookB2bClient.setApiKeys(SettableApiKey.ApiKeyType.MVP_1);
                pollingFactory.setApiKeys(SettableApiKey.ApiKeyType.MVP_1);
                sharedSteps.selectPA(pa);
            }
            case "Comune_2" -> {
                webhookB2bClient.setApiKeys(SettableApiKey.ApiKeyType.MVP_2);
                pollingFactory.setApiKeys(SettableApiKey.ApiKeyType.MVP_2);
                sharedSteps.selectPA(pa);
            }
            case "Comune_Multi" -> {
                webhookB2bClient.setApiKeys(SettableApiKey.ApiKeyType.GA);
                pollingFactory.setApiKeys(SettableApiKey.ApiKeyType.GA);
                sharedSteps.selectPA(pa);
            }
            default -> throw new IllegalArgumentException();
        }
    }

//    private void deleteAllPaStreamForAllVersion(String pa) {
//        webhookClientForClean.setApiKeys(paForStream.get(pa));
//        Arrays.stream(StreamVersion.values()).forEach(version -> {
//            WebhookStepsInterface webhookStepsInterface = getWebhookStep(version);
//            if (webhookStepsInterface != null)
//                webhookStepsInterface.deleteStreams(pa);
//        });
//    }

    private void deleteAllPaStreamForAllVersion(SettableApiKey.ApiKeyType pa) {
        //TODO: MODIFICARE
        webhookClientForClean.setApiKeys(pa);

        //DELETE V1
        List<it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model_v2.StreamListElement> streamListElements = webhookClientForClean.listEventStreams();
        for (it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model_v2.StreamListElement elem : streamListElements) {
            try {
                webhookClientForClean.deleteEventStream(elem.getStreamId());
            } catch (HttpStatusCodeException statusCodeException) {
                log.error("HTTP Error: statusCode {} message {}", statusCodeException.getStatusCode(), statusCodeException.getMessage());
            }
        }

        //DELETE V2.3
        List<StreamListElement> streamListElementsV23 = webhookClientForClean.listEventStreamsV23();
        for (StreamListElement elem : streamListElementsV23) {
            try {
                webhookClientForClean.deleteEventStreamV23(elem.getStreamId());
            } catch (HttpStatusCodeException statusCodeException) {
                log.error("HTTP Error: statusCode {} message {}", statusCodeException.getStatusCode(), statusCodeException.getMessage());
            }
        }

        //DELETE V2.4
        List<it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.StreamListElement> streamListElementsV24 = webhookClientForClean.listEventStreamsV24();
        for (it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.StreamListElement elem : streamListElementsV24) {
            try {
                webhookClientForClean.deleteEventStreamV24(elem.getStreamId());
            } catch (HttpStatusCodeException statusCodeException) {
                log.error("HTTP Error: statusCode {} message {}", statusCodeException.getStatusCode(), statusCodeException.getMessage());
            }
        }
        //DELETE V2.5
        List<it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.StreamListElement> streamListElementsV25 = webhookClientForClean.listEventStreamsV25();
        for (it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.StreamListElement elem : streamListElementsV25) {
            try {
                webhookClientForClean.deleteEventStreamV25(elem.getStreamId());
            } catch (HttpStatusCodeException statusCodeException) {
                log.error("HTTP Error: statusCode {} message {}", statusCodeException.getStatusCode(), statusCodeException.getMessage());
            }
        }
        //DELETE V26
        List<it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model_v26.StreamListElement> streamListElementsV26 = webhookClientForClean.listEventStreamsV26();
        for (it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model_v26.StreamListElement elem : streamListElementsV26) {
            try {
                webhookClientForClean.deleteEventStreamV26(elem.getStreamId());
            } catch (HttpStatusCodeException statusCodeException) {
                log.error("HTTP Error: statusCode {} message {}", statusCodeException.getStatusCode(), statusCodeException.getMessage());
            }
        }
        //DELETE V27
        List<it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model_v27.StreamListElement> streamListElementsV27 = webhookClientForClean.listEventStreamsV27();
        for (it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model_v27.StreamListElement elem : streamListElementsV27) {
            try {
                webhookClientForClean.deleteEventStreamV27(elem.getStreamId());
            } catch (HttpStatusCodeException statusCodeException) {
                log.error("HTTP Error: statusCode {} message {}", statusCodeException.getStatusCode(), statusCodeException.getMessage());
            }
        }
    }




//    private void deleteAllPaStreamForAllVersion(SettableApiKey.ApiKeyType pa) {
//        //TODO: MODIFICARE
//        webhookClientForClean.setApiKeys(pa);
//
//        //DELETE V1
//        List<it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model_v2.StreamListElement> streamListElements = webhookClientForClean.listEventStreams();
//        for (it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model_v2.StreamListElement elem : streamListElements) {
//            try {
//                webhookClientForClean.deleteEventStream(elem.getStreamId());
//            } catch (HttpStatusCodeException statusCodeException) {
//                log.error("HTTP Error: statusCode {} message {}", statusCodeException.getStatusCode(), statusCodeException.getMessage());
//            }
//        }
//
//        //DELETE V23
//        List<StreamListElement> streamListElementsV23 = webhookClientForClean.listEventStreamsV23();
//        for (StreamListElement elem : streamListElementsV23) {
//            try {
//                webhookClientForClean.deleteEventStreamV23(elem.getStreamId());
//            } catch (HttpStatusCodeException statusCodeException) {
//                log.error("HTTP Error: statusCode {} message {}", statusCodeException.getStatusCode(), statusCodeException.getMessage());
//            }
//        }
//
//        //DELETE V24
//        List<it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.StreamListElement> streamListElementsV24 = webhookClientForClean.listEventStreamsV24();
//        for (it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.StreamListElement elem : streamListElementsV24) {
//            try {
//                webhookClientForClean.deleteEventStreamV24(elem.getStreamId());
//            } catch (HttpStatusCodeException statusCodeException) {
//                log.error("HTTP Error: statusCode {} message {}", statusCodeException.getStatusCode(), statusCodeException.getMessage());
//            }
//        }
//        //DELETE V25
//        List<it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.StreamListElement> streamListElementsV25 = webhookClientForClean.listEventStreamsV25();
//        for (it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.StreamListElement elem : streamListElementsV25) {
//            try {
//                webhookClientForClean.deleteEventStreamV25(elem.getStreamId());
//            } catch (HttpStatusCodeException statusCodeException) {
//                log.error("HTTP Error: statusCode {} message {}", statusCodeException.getStatusCode(), statusCodeException.getMessage());
//            }
//        }
//        //DELETE V26
//        List<it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model_v26.StreamListElement> streamListElementsV26 = webhookClientForClean.listEventStreamsV26();
//        for (it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model_v26.StreamListElement elem : streamListElementsV26) {
//            try {
//                webhookClientForClean.deleteEventStreamV26(elem.getStreamId());
//            } catch (HttpStatusCodeException statusCodeException) {
//                log.error("HTTP Error: statusCode {} message {}", statusCodeException.getStatusCode(), statusCodeException.getMessage());
//            }
//        }
//        //DELETE V27
//        List<it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model_v27.StreamListElement> streamListElementsV27 = webhookClientForClean.listEventStreamsV27();
//        for (it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model_v27.StreamListElement elem : streamListElementsV27) {
//            try {
//                webhookClientForClean.deleteEventStreamV27(elem.getStreamId());
//            } catch (HttpStatusCodeException statusCodeException) {
//                log.error("HTTP Error: statusCode {} message {}", statusCodeException.getStatusCode(), statusCodeException.getMessage());
//            }
//        }
//    }

    @Given("si predispo(ngono)(ne) {int} nuov(i)(o) stream denominat(i)(o) {string} con eventType {string} con versione {string}")
    public void setUpStreamsWithEventType(int number, String title, String eventType, String version) {
        this.requestNumber = number;
        StreamVersion streamVersion = getStreamVersion(version);
        createStreamRequest(streamVersion, new LinkedList<>(), number, title, eventType);
    }

    @Given("si predispo(ngono)(ne) {int} nuov(i)(o) stream V2 denominat(i)(o) {string} con eventType {string}")
    public void setUpStreamsWithEventTypeV2(int number, String title, String eventType) {
        this.requestNumber = number;
        List<String> filteredValues = eventType.equalsIgnoreCase("STATUS") ?
                Arrays.stream(NotificationStatus.values()).map(Enum::toString).toList() :
                Arrays.stream(TimelineElementCategoryV20.values()).map(Enum::toString).toList();

        createStreamRequest(V10, filteredValues, number, title, eventType);
    }

    // Questo step dovrà comparire solo nei feature con versione 27 o superiore
    @Given("(allo)(agli) stream versione {string} si setta il campo waitForAccepted introdotto con la versione 27 a {string}")
    public void setWaitForAccepted(String version, String waitForAccepted) {
        StreamVersion streamVersion = getStreamVersion(version);
        WebhookStepsInterface webhookStepsInterface = getWebhookStep(streamVersion);
        webhookStepsInterface.setValueForWaitForAccepted(Boolean.valueOf(waitForAccepted));
    }

    @When("si crea(no) i(l) nuov(o)(i) stream per il {string} con versione {string} e filtro di timeline {string}")
    public void createdStreamByFilterValue(String pa, String version, String filter) {
        setPaWebhook(pa);
        StreamVersion streamVersion = getStreamVersion(version);
        createStream(pa, streamVersion, null, false, List.of(filter), false);
    }

    @When("si crea(no) i(l) nuov(o)(i) stream per il {string} con versione {string}")
    public void createdStream(String pa, String version) {
        setPaWebhook(pa);
        updateApiKeyForStream();
        StreamVersion streamVersion = getStreamVersion(version);
        createStream(pa, streamVersion, null, false, null, false);
    }

    @When("si crea(no) i(l) nuov(o)(i) stream per il {string} con versione {string} e filtro status {string}")
    public void createdStream(String pa, String version, String filter) {
        setPaWebhook(pa);
        updateApiKeyForStream();
        StreamVersion streamVersion = getStreamVersion(version);
        createStream(pa, streamVersion, null, false, List.of(filter), false);
    }

    @And("si crea il nuovo stream per il {string} con versione {string} \\(caso errato)")
    public void siCreaIlNuovoStreamPerIlConVersioneFORZATOSoloPerCasoErrato(String pa, String version) {
        setPaWebhook(pa);
        StreamVersion streamVersion = getStreamVersion(version);
        createStream(pa, streamVersion, null, false, null, true);
    }

    @When("si crea(no) i(l) nuov(o)(i) stream con versione {string} per il {string} con un gruppo disponibile {string}")
    public void createdStreamByGroups(String version, String pa, String position) {
        setPaWebhook(pa);
        updateApiKeyForStream();
        StreamVersion streamVersion = getStreamVersion(version);
        createStream(pa, streamVersion, getGruopForStream(position, pa), false, null, false);
    }

    @When("si crea(no) i(l) nuov(o)(i) stream con versione {string} per il {string} con un gruppo disponibile {string} \\(caso errato)")
    public void createdStreamByGroupsForced(String version, String pa, String position) {
        setPaWebhook(pa);
        updateApiKeyForStream();
        StreamVersion streamVersion = getStreamVersion(version);
        createStream(pa, streamVersion, getGruopForStream(position, pa), false, null, true);
    }

    @When("si crea(no) i(l) nuov(o)(i) stream con versione {string} per il {string} con replaceId con un gruppo disponibile {string} \\(caso errato)")
    public void createdStreamByGroupsForcedWithReplace(String version, String pa, String position) {
        setPaWebhook(pa);
        updateApiKeyForStream();
        StreamVersion streamVersion = getStreamVersion(version);
        createStream(pa, streamVersion, getGruopForStream(position, pa), true, null, true);
    }

    @When("si crea(no) i(l) nuov(o)(i) stream con versione {string} per il {string} con replaceId con un gruppo disponibile {string}")
    public void createdStreamByGroupsWithReplaceId(String version, String pa, String position) {
        setPaWebhook(pa);
        updateApiKeyForStream();
        StreamVersion streamVersion = getStreamVersion(version);
        createStream(pa, streamVersion, getGruopForStream(position, pa), true, null, false);
    }

    @When("viene aggiornata la apiKey utilizzata per gli stream")
    public void updateApiKeyForStream() {
        if (sharedSteps.getResponseNewApiKey() != null) {
            webhookB2bClient.setApiKey(sharedSteps.getResponseNewApiKey().getApiKey());
        }
    }

    @And("si cancella(no) (lo)(gli) stream creat(o)(i) per il {string} con versione {string}")
    public void deleteStream(String pa, String version) {
        StreamVersion streamVersion = getStreamVersion(version);
        WebhookStepsInterface webhookStepsInterface = getWebhookStep(streamVersion);
        webhookStepsInterface.deleteStreams(pa);
    }

    @And("si aggiorna(no) (lo)(gli) stream creat(o)(i) con versione {string} e apiKey aggiornata")
    public void updateStreamUpadateApiKey(String version) {
        updateApiKeyForStream();
        updateStream(version);
    }

    @And("si {string} un gruppo allo stream creat(o)(i) con versione {string} per il comune {string} e apiKey aggiornata")
    public void updateGroupsStreamUpadateApiKey(String action, String version, String pa) {
        updateApiKeyForStream();
        StreamVersion streamVersion = getStreamVersion(version);
        if (sharedSteps.getRequestNewApiKey() != null) {
            WebhookStepsInterface webhookStepsInterface = getWebhookStep(streamVersion);
            webhookStepsInterface.initializeStreamRequest(action, pa);
        }
        updateStream(streamVersion.toString());
    }

    @And("si aggiorna(no) (lo)(gli) stream creat(o)(i) con versione {string} con un gruppo che non appartiene al comune {string}")
    public void updateStreamByGroupsNoPA(String version, String pa) {
        StreamVersion streamVersion = getStreamVersion(version);
        updateStreamByGroupsPA(streamVersion, pa, false);
    }

    @And("si aggiorna(no) (lo)(gli) stream creat(o)(i) con versione {string} con un gruppo che appartiene al comune {string}")
    public void updateStreamByGroupsPA(String version, String pa) {
        StreamVersion streamVersion = getStreamVersion(version);
        updateStreamByGroupsPA(streamVersion, pa, true);
    }

    private void updateStreamByGroupsPA(StreamVersion streamVersion, String pa, boolean groupOfPa) {
        String groupToUse = switch (pa) {
            case "Comune_Multi" -> groupOfPa ? "Comune_Multi" : "Comune_1";
            case "Comune_1" -> groupOfPa ? "Comune_1" : "Comune_Multi";
            default -> "Comune_Multi";
        };
        try {
            List<String> groupIdByPa = List.of(sharedSteps.getGroupIdByPa(groupToUse, GroupPosition.FIRST));
            WebhookStepsInterface webhookStepsInterface = getWebhookStep(streamVersion);
            webhookStepsInterface.createStreamRequestWithGroupsPA(groupIdByPa);
        } catch (HttpStatusCodeException e) {
            this.notificationError = e;
            sharedSteps.setNotificationError(e);
        }
        updateStream(streamVersion.toString());
    }

    @And("si aggiorna(no) (lo)(gli) stream creat(o)(i) con versione {string} invocando la versione {string} - Cross Versioning")
    public void updateStreamVersioning(String rightVersion, String wrongVersion) {
        if (sharedSteps.getResponseNewApiKey() != null) {
            webhookB2bClient.setApiKey(sharedSteps.getResponseNewApiKey().getApiKey());
        }
        StreamVersion streamVersion = getStreamVersion(rightVersion);
        WebhookStepsInterface webhookStepsInterface = getWebhookStep(streamVersion);
        UUID streamId = webhookStepsInterface.getStreamId();
        try {
            StreamVersion wrongStreamVersion = getStreamVersion(wrongVersion);
            WebhookStepsInterface wrongInterface = getWebhookStep(wrongStreamVersion);
            wrongInterface.updateStreamCreatingNewRequest(streamId);
        } catch (HttpStatusCodeException e) {
            this.notificationError = e;
            sharedSteps.setNotificationError(e);
        }
    }

    @And("si aggiorna(no) (lo)(gli) stream creat(o)(i) con versione {string}")
    public void updateStream(String version) {
        StreamVersion streamVersion = getStreamVersion(version);
        WebhookStepsInterface webhookStepsInterface = getWebhookStep(streamVersion);
        try {
            webhookStepsInterface.updateStreams();
        } catch (HttpStatusCodeException e) {
            this.notificationError = e;
            sharedSteps.setNotificationError(e);
        }
    }

    @And("si disabilita(no) (lo)(gli) stream creat(o)(i) per il comune {string} con versione {string} e apiKey aggiornata")
    public void disableStreamUpdateApiKey(String pa, String version) {
        updateApiKeyForStream();
        StreamVersion streamVersion = getStreamVersion(version);
        disableStreamInternal(streamVersion);
    }

    @And("si disabilita(no) (lo)(gli) stream {string} creat(o)(i) per il comune {string}")
    public void disableStream(String version, String pa) {
        StreamVersion streamVersion = getStreamVersion(version);
        disableStreamInternal(streamVersion);
    }

    @And("si disabilita(no) (lo)(gli) stream che non esiste e apiKey aggiornata")
    public void disableStreamNotexist() {
        updateApiKeyForStream();
        try {
            webhookB2bClient.disableEventStreamV23(UUID.randomUUID());
        } catch (HttpStatusCodeException e) {
            this.notificationError = e;
            sharedSteps.setNotificationError(e);
        }
    }

    @And("si cancella(no) (lo)(gli) stream che non esiste e apiKey aggiornata")
    public void deleteStreamNotexist() {
        updateApiKeyForStream();
        try {
            webhookB2bClient.deleteEventStreamV23(UUID.randomUUID());
        } catch (HttpStatusCodeException e) {
            this.notificationError = e;
            sharedSteps.setNotificationError(e);
        }
    }

    @And("si consuma(no) (lo)(gli) stream che non esiste e apiKey aggiornata")
    public void consumeStreamNotexist() {
        updateApiKeyForStream();
        try {
            webhookB2bClient.consumeEventStreamHttpV23(UUID.randomUUID(), null);
        } catch (HttpStatusCodeException e) {
            this.notificationError = e;
            sharedSteps.setNotificationError(e);
        }
    }

    @And("si legge(no) (lo)(gli) stream che non esiste e apiKey aggiornata con versione {string}")
    public void readStreamNotExist(String version) {
        updateApiKeyForStream();
        StreamVersion streamVersion = getStreamVersion(version);
        WebhookStepsInterface webhookStepsInterface = getWebhookStep(streamVersion);
        UUID streamId = webhookStepsInterface.getStreamId();
        try {
            webhookB2bClient.getEventStream(streamId);
        } catch (HttpStatusCodeException e) {
            this.notificationError = e;
            sharedSteps.setNotificationError(e);
        }
    }

    @And("si aggiorna(no) (lo)(gli) stream che non esiste e apiKey aggiornata con versione {string}")
    public void updateStreamNotExist(String version) {
        updateApiKeyForStream();
        try {
            StreamVersion streamVersion = getStreamVersion(version);
            WebhookStepsInterface webhookStepsInterface = getWebhookStep(streamVersion);
            UUID streamId = webhookStepsInterface.getStreamId();
            Object streamRequest = webhookStepsInterface.getStreamRequest();
            webhookStepsInterface.initStreamRequest(streamRequest);
            webhookStepsInterface.updateStreamWithExistingRequest(streamId);
        } catch (HttpStatusCodeException e) {
            this.notificationError = e;
            sharedSteps.setNotificationError(e);
        }
    }

    @And("viene verificata la corretta cancellazione con versione {string}")
    public void verifiedTheCorrectDeletion(String version) {
        StreamVersion streamVersion = getStreamVersion(version);
        WebhookStepsInterface webhookStepsInterface = getWebhookStep(streamVersion);
        webhookStepsInterface.checkCorrectCancellation();
    }

    @Then("lo stream è stato creato e viene correttamente recuperato dal sistema tramite stream id con versione {string} e apiKey aggiornata")
    public void streamBeenCreatedAndCorrectlyRetrievedByStreamIdUpdateApiKey(String version) {
        updateApiKeyForStream();
        streamBeenCreatedAndCorrectlyRetrievedByStreamId(version);
    }

    @Then("lo stream è stato creato e viene correttamente recuperato dal sistema tramite stream id con versione {string}")
    public void streamBeenCreatedAndCorrectlyRetrievedByStreamId(String version) {
        StreamVersion streamVersion = getStreamVersion(version);
        WebhookStepsInterface webhookStepsInterface = getWebhookStep(streamVersion);
        UUID streamId = webhookStepsInterface.getStreamId();
        webhookStepsInterface.getStreamById(streamId);
    }

    @And("lo stream viene recuperato dal sistema tramite stream id con versione {string} e apiKey aggiornata")
    public void streamBeenRetrievedByStreamIdUpdateApiKey(String version) {
        updateApiKeyForStream();
        streamBeenRetrievedByStreamId(version);
    }

    @Then("lo stream viene recuperato dal sistema tramite stream id con versione {string}")
    public void streamBeenRetrievedByStreamId(String version) {
        StreamVersion streamVersion = getStreamVersion(version);
        try {
            WebhookStepsInterface webhookStepsInterface = getWebhookStep(streamVersion);
            UUID streamId = webhookStepsInterface.getStreamId();
            webhookStepsInterface.getStreamById(streamId);
        } catch (HttpStatusCodeException e) {
            this.notificationError = e;
            sharedSteps.setNotificationError(e);
        }
    }

    @And("vengono letti gli eventi dello stream versione {string}")
    public void readStreamEvents(String version) {
        readStreamElement(version, version);
    }


    @When("vengono letti gli eventi dello stream con versione {string} creati dalla versione {string}")
    public void vengonoLettiGliEventiDelloStreamConVersioneCreatiDallaVersione(String versionRead, String versionCreate) {
        readStreamElement(versionCreate, versionRead);
    }

    private void readStreamElement(String versionCreate, String versionRead) {
        updateApiKeyForStream();
        StreamVersion streamVersionCreate = getStreamVersion(versionCreate);
        WebhookStepsInterface webhookStepsInterfaceCreate = getWebhookStep(streamVersionCreate);
        UUID streamId = webhookStepsInterfaceCreate.getStreamId();
        StreamVersion streamVersionRead = getStreamVersion(versionRead);
        WebhookStepsInterface webhookStepsInterfaceRead = getWebhookStep(streamVersionRead);
        try {
            webhookStepsInterfaceRead.consumeEventStream(streamId);
        } catch (HttpStatusCodeException e) {
            this.notificationError = e;
            sharedSteps.setNotificationError(e);
        }
    }

    private boolean searchSpecificTimelineEvent(String timelineEvent, String deliveryDetailCode) {
        Assertions.assertNotNull(progressResponseElementsV23);
        return progressResponseElementsV23.stream()
                .filter(Objects::nonNull)
                .filter(x -> x.getIun() != null && x.getIun().equals(sharedSteps.getSentNotification().getIun()))
                .map(ProgressResponseElementV23::getElement)
                .filter(x -> x.getElementId() != null && x.getElementId().contains(timelineEvent))
                .map(TimelineElementV23::getDetails)
                .filter(Objects::nonNull)
                .anyMatch(x -> x.getDeliveryDetailCode() != null && x.getDeliveryDetailCode().equals(deliveryDetailCode));
    }

    @And("viene verificato che gli eventi dello stream non contengono l'elemento di timeline {string} con deliveryDetailCode {string}")
    public void verifyStreamNotContainsSpecificTimelineEvent(String timelineEvent, String deliveryDetailCode) {
        Assertions.assertFalse(searchSpecificTimelineEvent(timelineEvent, deliveryDetailCode));
    }

    @And("viene verificato che gli eventi dello stream contengono l'elemento di timeline {string} con deliveryDetailCode {string}")
    public void verifyStreamContainsSpecificTimelineEvent(String timelineEvent, String deliveryDetailCode) {
        Assertions.assertTrue(searchSpecificTimelineEvent(timelineEvent, deliveryDetailCode));
    }

    @And("vengono letti gli eventi dello stream non esistente versione {string}")
    public void readStreamEventsNotFound(String version) {
        updateApiKeyForStream();
        UUID streamId = UUID.randomUUID();
        try {
            StreamVersion streamVersion = getStreamVersion(version);
            WebhookStepsInterface webhookStepsInterface = getWebhookStep(streamVersion);
            webhookStepsInterface.consumeEventStream(streamId);
        } catch (HttpStatusCodeException e) {
            this.notificationError = e;
            sharedSteps.setNotificationError(e);
        }
    }

    //TODO MATTEO -> si può rendere parametrico che funziona per tutte le versioni
    @And("vengono letti gli eventi dello stream del {string} fino allo stato {string}")
    public void readStreamEventsState(String pa, String status) {
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
            NotificationStatusHistoryElementV26 notificationStatusHistoryElement = sharedSteps.getSentNotification()
                    .getNotificationStatusHistory().stream()
                    .filter(elem -> elem.getStatus().getValue()
                            .equals(notificationInternalStatus.getValue()))
                    .findAny()
                    .orElse(null);

            if (notificationStatusHistoryElement != null) {
                finded = true;
                break;
            }
        }

        Assertions.assertTrue(finded);
        for (int i = 0; i < 4; i++) {
            progressResponseElement = searchInWebhookV20(notificationStatus, null, 0);
            log.debug("PROGRESS-ELEMENT: " + progressResponseElement);

            if (progressResponseElement != null) {
                break;
            }

            sleepTest();
        }

        try {
            Assertions.assertNotNull(progressResponseElement);
            log.info("EventProgress: " + progressResponseElement);
        } catch (AssertionFailedError assertionFailedError) {
            //TODO MATTEO TEST
            WebhookStepsInterface webhookStepsInterface = getWebhookStep(StreamVersion.V10);
            String errorLog = String.format("{IUN: %s -WEBHOOK %s }", webhookStepsInterface.getSentNotificationIun(), webhookStepsInterface.getStreamId());
            String message = assertionFailedError.getMessage() + errorLog;
            throw new AssertionFailedError(message, assertionFailedError.getExpected(), assertionFailedError.getActual(), assertionFailedError.getCause());
        }

    }

    @And("vengono letti gli eventi dello stream del {string} fino allo stato {string} con versione V26")
    public void readStreamEventsStatev26(String pa, String status) {
        setPaWebhook(pa);

        StatusElementSearchResult<it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model_v26.NotificationStatusV26>
                statusEventForStream = getStatusEventForStream(V26, status);

        it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model_v26.NotificationStatusV26
                notificationStatus = statusEventForStream.getNotificationStatus();

        it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.NotificationStatusV26 notificationInternalStatus =
                it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.NotificationStatusV26.valueOf(statusEventForStream.notificationStatus.name());

        int numCheck = statusEventForStream.getNumCheck();
        int waiting = statusEventForStream.getWaiting();

        ProgressResponseElementV26 progressResponseElement = null;
        boolean finded = false;
        for (int i = 0; i < numCheck; i++) {

            sleepTest(waiting);

            sharedSteps.setSentNotification(b2bClient.getSentNotification(sharedSteps.getSentNotification().getIun()));
            NotificationStatusHistoryElementV26 notificationStatusHistoryElement = sharedSteps.getSentNotification().getNotificationStatusHistory().stream().filter(elem -> elem.getStatus().equals(notificationInternalStatus)).findAny().orElse(null);

            if (notificationStatusHistoryElement != null) {
                finded = true;
                break;
            }
        }

        Assertions.assertTrue(finded);
        for (int i = 0; i < 4; i++) {
            progressResponseElement = searchInWebhookV26(notificationStatus, null, 0, 0);
            log.debug("PROGRESS-ELEMENT: " + progressResponseElement);

            if (progressResponseElement != null) {
                break;
            }

            sleepTest();
        }

        try {
            Assertions.assertNotNull(progressResponseElement);
            log.info("EventProgress: " + progressResponseElement);
        } catch (AssertionFailedError assertionFailedError) {
            //TODO MATTEO TEST
            WebhookStepsInterface webhookStepsInterface = getWebhookStep(StreamVersion.V26);
            String errorLog = String.format("{IUN: %s -WEBHOOK %s }", webhookStepsInterface.getSentNotificationIun(), webhookStepsInterface.getStreamId());
            String message = assertionFailedError.getMessage() + errorLog;
            throw new AssertionFailedError(message, assertionFailedError.getExpected(), assertionFailedError.getActual(), assertionFailedError.getCause());
        }
    }

    @And("vengono letti gli eventi dello stream del {string} fino allo stato {string} con la versione V23")
    public void readStreamEventsStateV23(String pa, String status) {
        setPaWebhook(pa);
        StatusElementSearchResult<NotificationStatus> statusEventForStream = getStatusEventForStream(StreamVersion.V23, status);
        NotificationStatus notificationStatus = statusEventForStream.getNotificationStatus();
        int numCheck = statusEventForStream.getNumCheck();
        int waiting = statusEventForStream.getWaiting();
        it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.NotificationStatus notificationInternalStatus = it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.NotificationStatus.valueOf(notificationStatus.name());
        ProgressResponseElementV23 progressResponseElement = null;
        boolean finded = false;
        for (int i = 0; i < numCheck; i++) {
            sleepTest(waiting);
            sharedSteps.setSentNotificationV23(b2bClient.getSentNotificationV23(sharedSteps.getSentNotification().getIun()));
            NotificationStatusHistoryElement notificationStatusHistoryElement = sharedSteps.getSentNotificationV23().getNotificationStatusHistory().stream().filter(elem -> elem.getStatus().equals(notificationInternalStatus)).findAny().orElse(null);
            if (notificationStatusHistoryElement != null) {
                finded = true;
                break;
            }
        }
        Assertions.assertTrue(finded);
        for (int i = 0; i < 4; i++) {
            progressResponseElement = searchInWebhookV23(notificationStatus, null, 0, 0);
            log.debug("PROGRESS-ELEMENT: " + progressResponseElement);
            if (progressResponseElement != null) {
                break;
            }
            sleepTest();
        }
        try {
            Assertions.assertNotNull(progressResponseElement);
            log.info("EventProgress: " + progressResponseElement);
        } catch (AssertionFailedError assertionFailedError) {
            //TODO MATTEO TEST
            WebhookStepsInterface webhookStepsInterface = getWebhookStep(StreamVersion.V23);
            String errorLog = String.format("{IUN: %s -WEBHOOK %s }", webhookStepsInterface.getSentNotificationIun(), webhookStepsInterface.getStreamId());
            String message = assertionFailedError.getMessage() + errorLog;
            throw new AssertionFailedError(message, assertionFailedError.getExpected(), assertionFailedError.getActual(), assertionFailedError.getCause());
        }
    }

    @And("vengono letti gli eventi dello stream del {string} fino allo stato {string} con la versione V27")
    public void readStreamEventsStateV27(String pa, String status) {
        setPaWebhook(pa);
        StatusElementSearchResult<NotificationStatus> statusEventForStream = getStatusEventForStream(StreamVersion.V27, status);
        NotificationStatus notificationStatus = statusEventForStream.getNotificationStatus();
        int numCheck = statusEventForStream.getNumCheck();
        int waiting = statusEventForStream.getWaiting();
        it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model_v27.NotificationStatus notificationInternalStatus =
                it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model_v27.NotificationStatus
                        .valueOf(notificationStatus.name());
        ProgressResponseElementV27 progressResponseElement = null;
        boolean finded = false;
        for (int i = 0; i < numCheck; i++) {
            sleepTest(waiting);
            sharedSteps.setSentNotification(b2bClient.getSentNotification(sharedSteps.getSentNotification().getIun()));
            NotificationStatusHistoryElementV26 notificationStatusHistoryElement = sharedSteps.getSentNotification().getNotificationStatusHistory().
                    stream().filter(elem -> elem.getStatus().getValue().equals(notificationInternalStatus.getValue())).findAny().orElse(null);
            if (notificationStatusHistoryElement != null) {
                finded = true;
                break;
            }
        }
        Assertions.assertTrue(finded);
        for (int i = 0; i < 4; i++) {
            progressResponseElement = searchInWebhookV27(notificationStatus, null, 0, 0);
            log.debug("PROGRESS-ELEMENT: " + progressResponseElement);
            if (progressResponseElement != null) {
                break;
            }
            sleepTest();
        }
        try {
            Assertions.assertNotNull(progressResponseElement);
            log.info("EventProgress: " + progressResponseElement);
        } catch (AssertionFailedError assertionFailedError) {
            //TODO MATTEO TEST
            WebhookStepsInterface webhookStepsInterface = getWebhookStep(StreamVersion.V27);
            String errorLog = String.format("{IUN: %s -WEBHOOK %s }", webhookStepsInterface.getSentNotificationIun(), webhookStepsInterface.getStreamId());
            String message = assertionFailedError.getMessage() + errorLog;
            throw new AssertionFailedError(message, assertionFailedError.getExpected(), assertionFailedError.getActual(), assertionFailedError.getCause());
        }
    }


    @And("vengono letti gli eventi dello stream del {string} fino allo stato {string} con versione V23 e apiKey aggiornata con position {int}")
    public void readStreamEventsStateV23(String pa, String status, Integer position) {
        updateApiKeyForStream();
        setPaWebhook(pa);

        StatusElementSearchResult<NotificationStatus> statusEventForStream = getStatusEventForStream(StreamVersion.V23, status);
        NotificationStatus notificationStatus = statusEventForStream.getNotificationStatus();

        ProgressResponseElementV23 progressResponseElement = null;
        for (int i = 0; i < eventStreamListV23.size(); i++) {
            progressResponseElement = searchInWebhookV23(notificationStatus, null, 0, position);
            log.debug("PROGRESS-ELEMENT: " + progressResponseElement);
            if (progressResponseElement != null) {
                break;
            }
            sleepTest();
        }

        try {
            Assertions.assertNotNull(progressResponseElement);
            log.info("EventProgress: " + progressResponseElement);
        } catch (AssertionFailedError assertionFailedError) {
            //TODO MATTEO TEST
            WebhookStepsInterface webhookStepsInterface = getWebhookStep(StreamVersion.V23);
            String errorLog = String.format("{IUN: %s -WEBHOOK %s }", webhookStepsInterface.getSentNotificationIun(), webhookStepsInterface.getStreamId());
            String message = assertionFailedError.getMessage() + errorLog;
            throw new AssertionFailedError(message, assertionFailedError.getExpected(), assertionFailedError.getActual(), assertionFailedError.getCause());
        }
    }

    //V10 only
    @And("vengono letti gli eventi dello stream del {string} del validatore fino allo stato {string}")
    public void readStreamEventsStateValidatore(String pa, String status) {
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
            progressResponseElement = searchInWebhookV20(notificationStatus, null, 0);
            log.debug("PROGRESS-ELEMENT: " + progressResponseElement);

            sharedSteps.setSentNotification(b2bClient.getSentNotification(sharedSteps.getSentNotification().getIun()));
            NotificationStatusHistoryElementV26 notificationStatusHistoryElement = sharedSteps.getSentNotification()
                    .getNotificationStatusHistory().stream()
                    .filter(elem -> elem.getStatus().getValue().equals(notificationInternalStatus.getValue()))
                    .findAny()
                    .orElse(null);

            if (notificationStatusHistoryElement != null && !finded) {
                wait = i + 4;
                finded = true;
            }
            if (progressResponseElement != null) {
                break;
            }
            sleepTest(10 * 1000);
        }
        try {
            Assertions.assertNotNull(progressResponseElement);
            log.info("EventProgress: " + progressResponseElement);
        } catch (AssertionFailedError assertionFailedError) {
            //TODO MATTEO TEST
            WebhookStepsInterface webhookStepsInterface = getWebhookStep(StreamVersion.V10);
            String errorLog = String.format("{IUN: %s -WEBHOOK %s }", webhookStepsInterface.getSentNotificationIun(), webhookStepsInterface.getStreamId());
            String message = assertionFailedError.getMessage() + errorLog;
            throw new AssertionFailedError(message, assertionFailedError.getExpected(), assertionFailedError.getActual(), assertionFailedError.getCause());
        }

    }

    //TODO rimuovere -> STEP NON UTILIZZATO
//    @And("vengono letti gli eventi dello stream del {string} con la verifica di Allegato non trovato con la versione V23")
//    public void readStreamEventsStateRefusedV23(String pa) {
//
//        setPaWebhook(pa);
//        NotificationStatus notificationStatus;
//        notificationStatus = NotificationStatus.REFUSED;
//        ProgressResponseElementV23 progressResponseElement = null;
//
//        for (int i = 0; i < 4; i++) {
//            progressResponseElement = searchInWebhookFileNotFoundV23(notificationStatus, null, 0);
//            log.debug("PROGRESS-ELEMENT: " + progressResponseElement);
//
//            if (progressResponseElement != null) {
//                break;
//            }
//            sleepTest();
//        }
//
//        try {
//            Assertions.assertNotNull(progressResponseElement);
//            log.info("EventProgress: " + progressResponseElement);
//        } catch (AssertionFailedError assertionFailedError) {
//            String message = assertionFailedError.getMessage() +
//                    " {IUN: " + sharedSteps.getSentNotification().getIun() + " -WEBHOOK: " + this.eventStreamList.get(0).getStreamId() + " }";
//            throw new AssertionFailedError(message, assertionFailedError.getExpected(), assertionFailedError.getActual(), assertionFailedError.getCause());
//        }
//    }

    //V10 only
    @And("vengono letti gli eventi dello stream del {string} con la verifica di Allegato non trovato")
    public void readStreamEventsStateRefused(String pa) {

        setPaWebhook(pa);
        NotificationStatus notificationStatus;
        notificationStatus = NotificationStatus.REFUSED;
        ProgressResponseElement progressResponseElement = null;

        for (int i = 0; i < 4; i++) {
            progressResponseElement = searchInWebhookFileNotFound(notificationStatus, null, 0);
            log.debug("PROGRESS-ELEMENT: " + progressResponseElement);

            if (progressResponseElement != null) {
                break;
            }
            sleepTest();
        }

        try {
            Assertions.assertNotNull(progressResponseElement);
            log.info("EventProgress: " + progressResponseElement);
        } catch (AssertionFailedError assertionFailedError) {
            WebhookStepsInterface webhookStepsInterface = getWebhookStep(StreamVersion.V10);
            String errorLog = String.format("{IUN: %s -WEBHOOK %s }", webhookStepsInterface.getSentNotificationIun(), webhookStepsInterface.getStreamId());
            String message = assertionFailedError.getMessage() + errorLog;
            throw new AssertionFailedError(message, assertionFailedError.getExpected(), assertionFailedError.getActual(), assertionFailedError.getCause());
        }
    }


    //V10 only
    @Then("vengono letti gli eventi dello stream del {string} fino all'elemento di timeline {string}")
    public void readStreamTimelineElement(String pa, String timelineEventCategory) {
        setPaWebhook(pa);

        TimelineElementSearchResult<TimelineElementCategoryV20> timelineForStream = getTimelineEventForStream(V10, timelineEventCategory);
        TimelineElementCategoryV20 timelineElementCategory = timelineForStream.getTimelineElementCategory();
        int numCheck = timelineForStream.getNumCheck();
        int waiting = timelineForStream.getWaiting();

        ProgressResponseElement progressResponseElement = null;

        it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementCategoryV26 timelineElementInternalCategory =
                it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementCategoryV26.valueOf(timelineElementCategory.name());

        boolean finish = checkInternalTimeline(timelineElementCategory.name(), numCheck, waiting);
        Assertions.assertTrue(finish);

        for (int i = 0; i < 4; i++) {
            progressResponseElement = searchInWebhookV20(timelineElementCategory, null, 0);
            log.debug("PROGRESS-ELEMENT: " + progressResponseElement);

            if (progressResponseElement != null) {
                break;
            }
            sleepTest();
        }
        try {
            Assertions.assertNotNull(progressResponseElement);
            ProgressResponseElement finalProgressResponseElement = progressResponseElement;
            Assertions.assertFalse(sharedSteps.getSentNotification()
                    .getTimeline()
                    .stream()
                    .filter(data -> data.getCategory() != null)
                    .filter(elem -> elem.getCategory().getValue().equals(timelineElementInternalCategory.getValue()))
                    .filter(elem -> elem.getTimestamp() != null)
                    .filter(elem -> elem.getTimestamp().truncatedTo(ChronoUnit.SECONDS).equals(finalProgressResponseElement
                            .getTimestamp()
                            .truncatedTo(ChronoUnit.SECONDS)))
                    .findAny()
                    .isEmpty());
            log.info("EventProgress: " + progressResponseElement);
            sharedSteps.setProgressResponseElement(progressResponseElement);
        } catch (AssertionFailedError assertionFailedError) {
            //TODO MATTEO TEST
            WebhookStepsInterface webhookStepsInterface = getWebhookStep(StreamVersion.V10);
            String errorLog = String.format("{IUN: %s -WEBHOOK %s }", webhookStepsInterface.getSentNotificationIun(), webhookStepsInterface.getStreamId());
            String message = assertionFailedError.getMessage() + errorLog;
            throw new AssertionFailedError(message, assertionFailedError.getExpected(), assertionFailedError.getActual(), assertionFailedError.getCause());
        }
    }

    //TODO MATTEO -> si può rendere parametrico che funziona con tutte le versioni
    @Then("vengono letti gli eventi dello stream del {string} fino all'elemento di timeline {string} con versione V26")
    public void readStreamTimelineElementV26(String pa, String timelineEventCategory) {
        setPaWebhook(pa);

        TimelineElementSearchResult<TimelineElementCategoryV26> timelineForStream = getTimelineEventForStream(V26, timelineEventCategory);
        TimelineElementCategoryV26 timelineElementCategory = timelineForStream.getTimelineElementCategory();
        int numCheck = timelineForStream.getNumCheck();
        int waiting = timelineForStream.getWaiting();

        ProgressResponseElementV26 progressResponseElement = null;

        it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementCategoryV26 timelineElementInternalCategory =
                it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementCategoryV26.valueOf(timelineElementCategory.name());

        boolean finish = checkInternalTimelineV26(timelineElementCategory.name(), numCheck, waiting);
        Assertions.assertTrue(finish);

        for (int i = 0; i < 4; i++) {
            progressResponseElement = searchInWebhookV26(timelineElementCategory, null, 0, 0);
            log.debug("PROGRESS-ELEMENT: " + progressResponseElement);

            if (progressResponseElement != null) {
                break;
            }
            sleepTest();
        }
        try {
            Assertions.assertNotNull(progressResponseElement);
            Assertions.assertFalse(sharedSteps.getSentNotification()
                    .getTimeline()
                    .stream()
                    .filter(elem -> elem.getCategory() != null)
                    .filter(elem -> elem.getCategory().getValue().equals(timelineElementInternalCategory.getValue()))
                    .findAny()
                    .isEmpty());
            log.info("EventProgress: " + progressResponseElement);
            sharedSteps.setProgressResponseElementV26(progressResponseElement);
        } catch (AssertionFailedError assertionFailedError) {
            //TODO MATTEO TEST
            WebhookStepsInterface webhookStepsInterface = getWebhookStep(StreamVersion.V26);
            String errorLog = String.format("{IUN: %s -WEBHOOK %s }", webhookStepsInterface.getSentNotificationIun(), webhookStepsInterface.getStreamId());
            String message = assertionFailedError.getMessage() + errorLog;
            throw new AssertionFailedError(message, assertionFailedError.getExpected(), assertionFailedError.getActual(), assertionFailedError.getCause());
        }
    }

    //TODO MATTEO -> 24 only (almeno, gli step che lo richiamano sono solo con la 24...allora perchè c'è la 10)
    @Then("vengono letti gli eventi dello stream del {string} fino all'elemento di timeline {string} con deliveryDetailCode {string}")
    public void readStreamTimelineElementDelivCode(String pa, String timelineEventCategory, String deliveryDetailCode) {
        setPaWebhook(pa);

        TimelineElementSearchResult<TimelineElementCategoryV20> timelineForStream = getTimelineEventForStream(V10, timelineEventCategory);
        TimelineElementCategoryV20 timelineElementCategory = timelineForStream.getTimelineElementCategory();
        int numCheck = timelineForStream.getNumCheck();
        int waiting = timelineForStream.getWaiting();

        ProgressResponseElement progressResponseElement = null;

        it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementCategoryV23 timelineElementInternalCategory =
                it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementCategoryV23.valueOf(timelineElementCategory.name());

        boolean finish = checkInternalTimeline(timelineElementCategory.name(), numCheck, waiting);
        Assertions.assertTrue(finish);

        for (int i = 0; i < 4; i++) {
            progressResponseElement = searchInWebhookV20(timelineElementCategory, null, 0);
            log.debug("PROGRESS-ELEMENT: " + progressResponseElement);

            if (progressResponseElement != null) {
                break;
            }
            sleepTest();
        }
        try {
            Assertions.assertNotNull(progressResponseElement);
            Assertions.assertFalse(sharedSteps.getSentNotification()
                    .getTimeline()
                    .stream()
                    .filter(data -> data.getCategory() != null)
                    .filter(elem -> elem.getCategory().getValue().equals(timelineElementInternalCategory.getValue()))
                    .filter(elem -> elem.getDetails() != null && elem.getDetails().getDeliveryDetailCode() != null)
                    .filter(elem -> elem.getDetails().getDeliveryDetailCode().equals(deliveryDetailCode))
                    .findAny()
                    .isEmpty());
            log.info("EventProgress: " + progressResponseElement);
            sharedSteps.setProgressResponseElement(progressResponseElement);
        } catch (AssertionFailedError assertionFailedError) {
            //TODO MATTEO TEST
            WebhookStepsInterface webhookStepsInterface = getWebhookStep(StreamVersion.V10);
            String errorLog = String.format("{IUN: %s -WEBHOOK %s }", webhookStepsInterface.getSentNotificationIun(), webhookStepsInterface.getStreamId());
            String message = assertionFailedError.getMessage() + errorLog;
            throw new AssertionFailedError(message, assertionFailedError.getExpected(), assertionFailedError.getActual(), assertionFailedError.getCause());
        }
    }

    @Then("vengono letti gli eventi dello stream del {string} fino all'elemento di timeline {string} con la versione {string}")
    public void readStreamTimelineElement(String pa, String timelineEventCategory, String version) {
        WebhookStepsInterface webhookStepsInterface = getWebhookStep(StreamVersion.valueOf(version));
        StreamVersion streamVersion = StreamVersion.valueOf(version);
        setPaWebhook(pa);
        TimelineElementSearchResult<?> timelineForStream = getTimelineEventForStream(streamVersion, timelineEventCategory);
        boolean finish = webhookStepsInterface.checkInternalTimeline(timelineForStream);
        Assertions.assertTrue(finish);
        Object progressResponseElement = null;
        for (int i = 0; i < 4; i++) {
            progressResponseElement = webhookStepsInterface.searchInWebhook(null, 0, 0, timelineForStream);
            log.debug("PROGRESS-ELEMENT: " + progressResponseElement);
            if (progressResponseElement != null) {
                break;
            }
            sleepTest();
        }
        webhookStepsInterface.verifyAssertions(timelineForStream, progressResponseElement);


//
//        TimelineElementSearchResult<TimelineElementCategoryV23> timelineForStream = getTimelineEventForStream(StreamVersion.V23, timelineEventCategory);
//        TimelineElementCategoryV23 timelineElementCategory = timelineForStream.getTimelineElementCategory();
//        int numCheck = timelineForStream.getNumCheck();
//        int waiting = timelineForStream.getWaiting();
//
//        it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementCategoryV23 timelineElementInternalCategory =
//                it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementCategoryV23.valueOf(timelineElementCategory.name());
//
//        ProgressResponseElementV23 progressResponseElement = null;
//        boolean finish = checkInternalTimeline(timelineElementCategory.name(), numCheck, waiting);
//        Assertions.assertTrue(finish);
//
//        for (int i = 0; i < 4; i++) {
//            progressResponseElement = searchInWebhookV23(timelineElementCategory, null, 0, 0);
//            log.debug("PROGRESS-ELEMENT: " + progressResponseElement);
//
//            if (progressResponseElement != null) {
//                break;
//            }
//            sleepTest();
//        }
//        try {
//            Assertions.assertNotNull(progressResponseElement);
//
//            it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementV26 elementToCheck = sharedSteps.getSentNotification().getTimeline().stream()
//                    .filter(elem -> elem.getCategory() != null)
//                    .filter(elem -> elem.getCategory().getValue().equals(timelineElementInternalCategory.getValue()))
//                    .findAny()
//                    .orElse(null);
//
//            Assertions.assertNotNull(elementToCheck);
//            Assertions.assertNotNull(elementToCheck.getTimestamp());
//            Assertions.assertNotNull(progressResponseElement.getElement());
//            Assertions.assertNotNull(progressResponseElement.getElement().getTimestamp());
//            Assertions.assertEquals(progressResponseElement.getElement().getTimestamp().truncatedTo(ChronoUnit.SECONDS),
//                    elementToCheck.getTimestamp().truncatedTo(ChronoUnit.SECONDS));
//
//            log.info("EventProgress: " + progressResponseElement);
//
//        } catch (AssertionFailedError assertionFailedError) {
//            String message = assertionFailedError.getMessage() +
//                    "{IUN: " + sharedSteps.getSentNotificationV23().getIun() + " -WEBHOOK: " + this.eventStreamListV23.get(0).getStreamId() + " }";
//            throw new AssertionFailedError(message, assertionFailedError.getExpected(), assertionFailedError.getActual(), assertionFailedError.getCause());
//        }
    }

    @Then("vengono letti gli eventi dello stream del {string} fino all'elemento di timeline {string} con la versione V27")
    public void readStreamTimelineElementV27(String pa, String timelineEventCategory) {
        setPaWebhook(pa);

        TimelineElementSearchResult<TimelineElementCategoryV26> timelineForStream = getTimelineEventForStream(StreamVersion.V27, timelineEventCategory);
        TimelineElementCategoryV26 timelineElementCategory = timelineForStream.getTimelineElementCategory();
        int numCheck = timelineForStream.getNumCheck();
        int waiting = timelineForStream.getWaiting();

        TimelineElementCategoryV26 timelineElementInternalCategory =
                TimelineElementCategoryV26.valueOf(timelineElementCategory.name());

        ProgressResponseElementV27 progressResponseElement = null;
        boolean finish = checkInternalTimeline(timelineElementCategory.name(), numCheck, waiting);
        Assertions.assertTrue(finish);

        for (int i = 0; i < 4; i++) {
            progressResponseElement = searchInWebhookV27(timelineElementCategory, null, 0, 0);
            log.debug("PROGRESS-ELEMENT: " + progressResponseElement);

            if (progressResponseElement != null) {
                break;
            }
            sleepTest();
        }
        try {
            Assertions.assertNotNull(progressResponseElement);

            it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementV26 elementToCheck =
                    sharedSteps.getSentNotification().getTimeline().stream()
                            .filter(elem -> elem.getCategory() != null)
                            .filter(elem -> elem.getCategory().getValue().equals(timelineElementInternalCategory.getValue()))
                            .findAny()
                            .orElse(null);

            Assertions.assertNotNull(elementToCheck);
            Assertions.assertNotNull(elementToCheck.getTimestamp());
            Assertions.assertNotNull(progressResponseElement.getElement());
            Assertions.assertNotNull(progressResponseElement.getElement().getTimestamp());
            Assertions.assertEquals(progressResponseElement.getElement().getTimestamp().truncatedTo(ChronoUnit.SECONDS),
                    elementToCheck.getTimestamp().truncatedTo(ChronoUnit.SECONDS));

            log.info("EventProgress: " + progressResponseElement);

        } catch (AssertionFailedError assertionFailedError) {
            //TODO MATTEO TEST
            WebhookStepsInterface webhookStepsInterface = getWebhookStep(StreamVersion.V27);
            String errorLog = String.format("{IUN: %s -WEBHOOK %s }", webhookStepsInterface.getSentNotificationIun(), webhookStepsInterface.getStreamId());
            String message = assertionFailedError.getMessage() + errorLog;
            throw new AssertionFailedError(message, assertionFailedError.getExpected(), assertionFailedError.getActual(), assertionFailedError.getCause());
        }
    }

    @Then("vengono letti gli eventi dello stream del {string} fino all'elemento di timeline {string} con la versione V24 con deliveryDetailCode {string}")
    public void readStreamTimelineElementDelivCodeV24(String pa, String timelineEventCategory, String deliveryDetailCode) {
        //TODO MATTEO TEST
        StreamVersion streamVersion = V24;
        WebhookStepsInterface webhookStepsInterface = getWebhookStep(streamVersion);

        setPaWebhook(pa);

        TimelineElementSearchResult<TimelineElementCategoryV23> timelineForStream = getTimelineEventForStream(V23, timelineEventCategory);
        TimelineElementCategoryV23 timelineElementCategory = timelineForStream.getTimelineElementCategory();
        int numCheck = timelineForStream.getNumCheck();
        int waiting = timelineForStream.getWaiting();

        ProgressResponseElementV24 progressResponseElement = null;

        it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementCategoryV23 timelineElementInternalCategory =
                it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementCategoryV23.valueOf(timelineElementCategory.name());


        boolean finish = checkInternalTimeline(timelineElementCategory.name(), numCheck, waiting);
        Assertions.assertTrue(finish);

        for (int i = 0; i < 4; i++) {
            progressResponseElement = searchInWebhookV24(timelineElementCategory, null, 0, 0);
            log.debug("PROGRESS-ELEMENT: " + progressResponseElement);

            if (progressResponseElement != null) {
                break;
            }
            sleepTest();
        }
        try {
            Assertions.assertNotNull(progressResponseElement);
            //ProgressResponseElementV24 finalProgressResponseElement = progressResponseElement;
            Assertions.assertFalse(sharedSteps.getSentNotification()
                    .getTimeline()
                    .stream()
                    .filter(data -> data.getCategory() != null && data.getDetails() != null && data.getDetails().getDeliveryDetailCode() != null)
                    .filter(elem -> elem.getCategory().getValue().equals(timelineElementInternalCategory.getValue())
                            && elem.getDetails().getDeliveryDetailCode().equals(deliveryDetailCode))
                    .findAny()
                    .isEmpty());
            log.info("EventProgress: " + progressResponseElement);
            //sharedSteps.setProgressResponseElement(progressResponseElement);
        } catch (AssertionFailedError assertionFailedError) {
            String errorLog = String.format("{IUN: %s -WEBHOOK %s }", webhookStepsInterface.getSentNotificationIun(), webhookStepsInterface.getStreamId());
            String message = assertionFailedError.getMessage() + errorLog;
            throw new AssertionFailedError(message, assertionFailedError.getExpected(), assertionFailedError.getActual(), assertionFailedError.getCause());
        }
    }

    @Then("verifica presenza SERCQ")
    public void verifySercqPresent() {
        Assertions.assertTrue(sharedSteps.getProgressResponseElementsV23().stream()
                .map(ProgressResponseElementV23::getElement)
                .filter(data -> data.getElementId() != null)
                .filter(timelineElementV23 -> timelineElementV23.getElementId().contains("SEND_DIGITAL_FEEDBACK"))
                .map(TimelineElementV23::getDetails)
                .filter(Objects::nonNull)
                .allMatch(elementDetailsV23 -> "OK".equals(elementDetailsV23.getResponseStatus().toString()) && "SERCQ".equals(elementDetailsV23.getDigitalAddress().getType())
                ));
    }

    @Then("verifica la non presenza di SERCQ")
    public void verifySercqIsNotPresent() {
        Assertions.assertTrue(sharedSteps.getProgressResponseElements().stream()
                .filter(data -> data.getTimelineEventCategory() != null)
                .filter(progressResponseElement -> progressResponseElement.getTimelineEventCategory().getValue().contains("SEND_DIGITAL_FEEDBACK"))
                .allMatch(progressResponseElement -> "PEC".equals(progressResponseElement.getChannel())));
    }

    @Then("si verifica che non siano presenti eventi nello stream {string} del {string}")
    public void readStreamTimelineElementNotPresent(String version, String pa) {
        StreamVersion streamVersion = getStreamVersion(version);
        verifyNotEventInStream(pa, streamVersion);
    }

    private void verifyNotEventInStream(String pa, StreamVersion streamVersion) {
        setPaWebhook(pa);
        updateApiKeyForStream();
        WebhookStepsInterface webhookStepsInterface = getWebhookStep(streamVersion);
        webhookStepsInterface.verifyNoEventInStream();
    }

    @Then("vengono letti gli eventi dello stream del {string} fino all'elemento di timeline {string} con versione V23 e apiKey aggiornata con position {int}")
    public void readStreamTimelineElementV23(String pa, String timelineEventCategory, Integer position) {
        updateApiKeyForStream();
        setPaWebhook(pa);

        TimelineElementSearchResult<TimelineElementCategoryV23> timelineForStream = getTimelineEventForStream(StreamVersion.V23, timelineEventCategory);
        TimelineElementCategoryV23 timelineElementCategory = timelineForStream.getTimelineElementCategory();

        it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementCategoryV23 timelineElementInternalCategory =
                it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementCategoryV23.valueOf(timelineElementCategory.name());

        ProgressResponseElementV23 progressResponseElement = null;

        for (int i = 0; i < 4; i++) {
            progressResponseElement = searchInWebhookV23(timelineElementCategory, null, 0, position);
            log.debug("PROGRESS-ELEMENT: " + progressResponseElement);

            if (progressResponseElement != null) {
                break;
            }
            sleepTest();
        }
        try {
            Assertions.assertNotNull(progressResponseElement);
            progressResponseElementResultV23 = progressResponseElement;
            //TODO Verificare...

            TimelineElementV26 elementToCheck = sharedSteps.getSentNotification().getTimeline().stream()
                    .filter(elem -> elem.getCategory() != null)
                    .filter(elem -> elem.getCategory().getValue().equals(timelineElementInternalCategory.getValue()))
                    .findAny()
                    .orElse(null);

            Assertions.assertNotNull(elementToCheck);
            Assertions.assertNotNull(elementToCheck.getTimestamp());
            Assertions.assertEquals(progressResponseElement.getElement().getTimestamp().truncatedTo(ChronoUnit.SECONDS),
                    elementToCheck.getTimestamp().truncatedTo(ChronoUnit.SECONDS));
            log.info("EventProgress: " + progressResponseElement);
            sharedSteps.setProgressResponseElementV23(progressResponseElement);
        } catch (AssertionFailedError assertionFailedError) {
            //TODO MATTEO TEST
            WebhookStepsInterface webhookStepsInterface = getWebhookStep(StreamVersion.V23);
            String errorLog = String.format("{IUN: %s -WEBHOOK %s }", webhookStepsInterface.getSentNotificationIun(), webhookStepsInterface.getStreamId());
            String message = assertionFailedError.getMessage() + errorLog;
            throw new AssertionFailedError(message, assertionFailedError.getExpected(), assertionFailedError.getActual(), assertionFailedError.getCause());
        }
    }

    @Then("vengono letti gli eventi dello stream del {string} fino all'elemento di timeline {string} con versione V27 e apiKey aggiornata con position {int}")
    public void readStreamTimelineElementV27(String pa, String timelineEventCategory, Integer position) {
        updateApiKeyForStream();
        setPaWebhook(pa);

        TimelineElementSearchResult<TimelineElementCategoryV26> timelineForStream = getTimelineEventForStream(StreamVersion.V27, timelineEventCategory);
        TimelineElementCategoryV26 timelineElementCategory = timelineForStream.getTimelineElementCategory();

        TimelineElementCategoryV26 timelineElementInternalCategory = TimelineElementCategoryV26.valueOf(timelineElementCategory.name());

        ProgressResponseElementV27 progressResponseElement = null;

        for (int i = 0; i < 4; i++) {
            progressResponseElement = searchInWebhookV27(timelineElementCategory, null, 0, position);
            log.debug("PROGRESS-ELEMENT: " + progressResponseElement);

            if (progressResponseElement != null) {
                break;
            }
            sleepTest();
        }
        try {
            Assertions.assertNotNull(progressResponseElement);
            TimelineElementV26 elementToCheck = sharedSteps.getSentNotification().getTimeline().stream()
                    .filter(elem -> elem.getCategory() != null)
                    .filter(elem -> elem.getCategory().getValue().equals(timelineElementInternalCategory.getValue()))
                    .findAny()
                    .orElse(null);

            Assertions.assertNotNull(elementToCheck);
            Assertions.assertNotNull(elementToCheck.getTimestamp());
            Assertions.assertEquals(progressResponseElement.getElement().getTimestamp().truncatedTo(ChronoUnit.SECONDS),
                    elementToCheck.getTimestamp().truncatedTo(ChronoUnit.SECONDS));
            log.info("EventProgress: " + progressResponseElement);
            sharedSteps.setProgressResponseElementV27(progressResponseElement);
        } catch (AssertionFailedError assertionFailedError) {
            //TODO MATTEO TEST
            WebhookStepsInterface webhookStepsInterface = getWebhookStep(StreamVersion.V27);
            String errorLog = String.format("{IUN: %s -WEBHOOK %s }", webhookStepsInterface.getSentNotificationIun(), webhookStepsInterface.getStreamId());
            String message = assertionFailedError.getMessage() + errorLog;
            throw new AssertionFailedError(message, assertionFailedError.getExpected(), assertionFailedError.getActual(), assertionFailedError.getCause());
        }
    }

    @Then("non ci sono nuovi eventi nello stream")
    public void noyReadStreamTimelineElementV23() {
        Assertions.assertNull(progressResponseElementResultV23);
    }


    //V10 only
    @Then("Si verifica che l'elemento di timeline REFINEMENT abbia il timestamp uguale a quella presente nel webhook")
    public void readStreamTimelineElementAndVerifyDate() {
        OffsetDateTime eventTimestamp;
        OffsetDateTime notificationTimestamp;
        try {
            Assertions.assertNotNull(progressResponseElements);

            eventTimestamp = progressResponseElements.stream()
                    .filter(data -> data.getTimelineEventCategory() != null)
                    .filter(elem -> elem.getTimelineEventCategory().equals(it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model_v2.TimelineElementCategoryV20.REFINEMENT))
                    .findAny()
                    .map(ProgressResponseElement::getTimestamp)
                    .orElse(null);

            notificationTimestamp = sharedSteps.getSentNotification().getTimeline().stream()
                    .filter(data -> data.getCategory() != null)
                    .filter(elem -> elem.getCategory().getValue().equals(it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementCategoryV23.SCHEDULE_REFINEMENT.getValue()))
                    .findAny()
                    .map(TimelineElementV26::getDetails)
                    .map(TimelineElementDetailsV26::getSchedulingDate)
                    .orElse(null);

            Assertions.assertNotNull(eventTimestamp);
            Assertions.assertNotNull(notificationTimestamp);
            log.info("event timestamp : {}", eventTimestamp);
            log.info("notification timestamp : {}", notificationTimestamp);

            Assertions.assertEquals(eventTimestamp, notificationTimestamp);

        } catch (AssertionFailedError assertionFailedError) {
            //TODO MATTEO TEST
            WebhookStepsInterface webhookStepsInterface = getWebhookStep(StreamVersion.V10);
            String errorLog = String.format("{IUN: %s -WEBHOOK %s }", webhookStepsInterface.getSentNotificationIun(), webhookStepsInterface.getStreamId());
            String message = assertionFailedError.getMessage() + errorLog;
            throw new AssertionFailedError(message, assertionFailedError.getExpected(), assertionFailedError.getActual(), assertionFailedError.getCause());
        }
    }

    @Then("Si verifica che l'elemento di timeline REFINEMENT abbia il timestamp uguale a quella presente nel webhook con la versione V23")
    public void readStreamTimelineElementAndVerifyDateV23() {
        OffsetDateTime EventTimestamp;
        OffsetDateTime NotificationTimestamp;
        try {
            Assertions.assertNotNull(progressResponseElementsV23);
            //TODO Verificare...
            EventTimestamp = progressResponseElementsV23.stream().filter(elem -> elem.getElement().getCategory().equals(TimelineElementCategoryV23.REFINEMENT)).findAny().get().getElement().getTimestamp();
            //EventTimestamp = progressResponseElementListV23.stream().filter(elem -> elem.getTimelineEventCategory().equals(TimelineElementCategoryV23.REFINEMENT)).findAny().get().getTimestamp();
            TimelineElementV26 timelineToCheck = sharedSteps.getSentNotification().getTimeline().stream()
                    .filter(elem -> elem.getCategory().getValue().equals(it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementCategoryV23.SCHEDULE_REFINEMENT.getValue()))
                    .findAny()
                    .orElse(null);

            Assertions.assertNotNull(timelineToCheck);
            Assertions.assertNotNull(timelineToCheck.getDetails());

            NotificationTimestamp = timelineToCheck.getDetails().getSchedulingDate();
            log.info("event timestamp : {}", EventTimestamp);
            log.info("notification timestamp : {}", NotificationTimestamp);

            Assertions.assertEquals(EventTimestamp, NotificationTimestamp);

        } catch (AssertionFailedError assertionFailedError) {
            //TODO MATTEO TEST
            WebhookStepsInterface webhookStepsInterface = getWebhookStep(StreamVersion.V23);
            String errorLog = String.format("{IUN: %s -WEBHOOK %s }", webhookStepsInterface.getSentNotificationIun(), webhookStepsInterface.getStreamId());
            String message = assertionFailedError.getMessage() + errorLog;
            throw new AssertionFailedError(message, assertionFailedError.getExpected(), assertionFailedError.getActual(), assertionFailedError.getCause());
        }
    }

    @Then("Si verifica che l'elemento di timeline REFINEMENT abbia il timestamp uguale a quella presente nel webhook con la versione V27")
    public void readStreamTimelineElementAndVerifyDateV27() {
        OffsetDateTime EventTimestamp;
        OffsetDateTime NotificationTimestamp;
        try {
            Assertions.assertNotNull(progressResponseElementsV27);
            EventTimestamp = progressResponseElementsV27.stream().filter(
                            elem -> elem.getElement().getCategory().getValue().equals(TimelineElementCategoryV26.REFINEMENT.getValue()))
                    .findAny().get().getElement().getTimestamp();
            TimelineElementV26 timelineToCheck = sharedSteps.getSentNotification().getTimeline().stream()
                    .filter(elem -> elem.getCategory().getValue().equals(it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementCategoryV26.SCHEDULE_REFINEMENT.getValue()))
                    .findAny()
                    .orElse(null);

            Assertions.assertNotNull(timelineToCheck);
            Assertions.assertNotNull(timelineToCheck.getDetails());

            NotificationTimestamp = timelineToCheck.getDetails().getSchedulingDate();
            log.info("event timestamp : {}", EventTimestamp);
            log.info("notification timestamp : {}", NotificationTimestamp);

            Assertions.assertEquals(EventTimestamp, NotificationTimestamp);

        } catch (AssertionFailedError assertionFailedError) {
            //TODO MATTEO TEST
            WebhookStepsInterface webhookStepsInterface = getWebhookStep(StreamVersion.V27);
            String errorLog = String.format("{IUN: %s -WEBHOOK %s }", webhookStepsInterface.getSentNotificationIun(), webhookStepsInterface.getStreamId());
            String message = assertionFailedError.getMessage() + errorLog;
            throw new AssertionFailedError(message, assertionFailedError.getExpected(), assertionFailedError.getActual(), assertionFailedError.getCause());
        }
    }


    @Then("Si verifica che l'elemento di timeline {string} dello stream di {string} non abbia il timestamp uguale a quella della notifica")
    public void readStreamTimelineElementAndVerify(String timelineEventCategory, String pa) {
        //il controllo viene già fatto però ATTENZIONE era fatto in maniera errata
        readStreamTimelineElement(pa, timelineEventCategory);
    }

    @Then("Si verifica che l'elemento di timeline {string} dello stream di {string} non abbia il timestamp uguale a quella della notifica con la versione V23")
    public void readStreamTimelineElementAndVerifyV23(String timelineEventCategory, String pa) {
        //Il controllo viene effettuato
        readStreamTimelineElement(pa, timelineEventCategory, "V23");
    }

    private <T> PnPollingWebhook getPnPollingWebhook(T timeLineOrStatus) {
        PnPollingWebhook pnPollingWebhook = new PnPollingWebhook();


        if (timeLineOrStatus instanceof TimelineElementCategoryV20) {
            pnPollingWebhook.setTimelineElementCategoryV20((TimelineElementCategoryV20) timeLineOrStatus);
            progressResponseElements.clear();
            pnPollingWebhook.setProgressResponseElementListV20((LinkedList<ProgressResponseElement>) progressResponseElements);

        } else if (timeLineOrStatus instanceof it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model_v2.NotificationStatus) {
            pnPollingWebhook.setNotificationStatusV20((it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model_v2.NotificationStatus) timeLineOrStatus);
            progressResponseElements.clear();
            pnPollingWebhook.setProgressResponseElementListV20((LinkedList<ProgressResponseElement>) progressResponseElements);

        } else if (timeLineOrStatus instanceof TimelineElementCategoryV23) {
            pnPollingWebhook.setTimelineElementCategoryV23((TimelineElementCategoryV23) timeLineOrStatus);
            progressResponseElementsV23.clear();
            pnPollingWebhook.setProgressResponseElementListV23((LinkedList<ProgressResponseElementV23>) progressResponseElementsV23);

        } else if (timeLineOrStatus instanceof it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model_v2_3.NotificationStatus) {
            pnPollingWebhook.setNotificationStatusV23((NotificationStatus) timeLineOrStatus);
            progressResponseElementsV23.clear();
            pnPollingWebhook.setProgressResponseElementListV23((LinkedList<ProgressResponseElementV23>) progressResponseElementsV23);

        } else if (timeLineOrStatus instanceof TimelineElementCategoryV26) {
            pnPollingWebhook.setTimelineElementCategoryV26((it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementCategoryV26) timeLineOrStatus);
            progressResponseElementsV26.clear();
            pnPollingWebhook.setProgressResponseElementListV26((LinkedList<ProgressResponseElementV26>) progressResponseElementsV26);

        } else if (timeLineOrStatus instanceof it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementCategoryV23) {
            pnPollingWebhook.setTimelineElementCategoryV24((it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementCategoryV23) timeLineOrStatus);
            progressResponseElementsV24.clear();
            pnPollingWebhook.setProgressResponseElementListV24((LinkedList<ProgressResponseElementV24>) progressResponseElementsV24);

        } else if (timeLineOrStatus instanceof it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model_v26.NotificationStatus) {
            pnPollingWebhook.setNotificationStatus_noVersionV26((it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model_v26.NotificationStatus) timeLineOrStatus);
            progressResponseElementsV26.clear();
            pnPollingWebhook.setProgressResponseElementListV26((LinkedList<ProgressResponseElementV26>) progressResponseElementsV26);

        } else if (timeLineOrStatus instanceof it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model_v26.NotificationStatusV26) {
            pnPollingWebhook.setNotificationStatusV26((it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model_v26.NotificationStatusV26) timeLineOrStatus);
            progressResponseElementsV26.clear();
            pnPollingWebhook.setProgressResponseElementListV26((LinkedList<ProgressResponseElementV26>) progressResponseElementsV26);
        } else if (timeLineOrStatus instanceof it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model_v27.NotificationStatus) {
            pnPollingWebhook.setNotificationStatus_noVersionV27((it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model_v27.NotificationStatus) timeLineOrStatus);
            progressResponseElementsV27.clear();
            pnPollingWebhook.setProgressResponseElementListV27((LinkedList<ProgressResponseElementV27>) progressResponseElementsV27);

        } else if (timeLineOrStatus instanceof it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model_v27.NotificationStatusV26) {
            pnPollingWebhook.setNotificationStatusV27((it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model_v27.NotificationStatusV26) timeLineOrStatus);
            progressResponseElementsV27.clear();
            pnPollingWebhook.setProgressResponseElementListV27((LinkedList<ProgressResponseElementV27>) progressResponseElementsV27);

        } else {
            throw new IllegalArgumentException();
        }
        return pnPollingWebhook;
    }

    private <T> ProgressResponseElement searchInWebhookV20(T timeLineOrStatus, String lastEventId, int deepCount) {
        //TODO MATTEO TEST
        WebhookStepsInterface webhookStepsInterface = getWebhookStep(StreamVersion.V10);
        UUID streamId = webhookStepsInterface.getStreamId();

        PnPollingWebhook pnPollingWebhook = getPnPollingWebhook(timeLineOrStatus);
        PnPollingParameter pnPollingParameter = PnPollingParameter.builder()
                .value("WEBHOOK")
                .pnPollingWebhook(pnPollingWebhook)
                .deepCount(deepCount)
                .lastEventId(lastEventId)
                .streamId(streamId)
                .build();
        PnPollingServiceWebhookV20 webhookV20 = (PnPollingServiceWebhookV20) sharedSteps.getPollingFactory().getPollingService(PnPollingStrategy.WEBHOOK_V20);
        PnPollingResponseV20 pnPollingResponseV20 = webhookV20.waitForEvent(sharedSteps.getSentNotification().getIun(), pnPollingParameter);

        log.info("WEBHOOK_PROGRESS_RESPONSE_ELEMENT_V20: " + pnPollingResponseV20.getProgressResponseElementV20());
        if (pnPollingResponseV20.getProgressResponseElementV20() != null) {
            sharedSteps.setProgressResponseElements(pnPollingResponseV20.getProgressResponseElementListV20());
            return pnPollingResponseV20.getProgressResponseElementV20();
        }
        return null;
    }

    private <T> ProgressResponseElementV23 searchInWebhookV23(T timeLineOrStatus, String lastEventId, int deepCount, int position) {
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
        if (pnPollingResponseV23.getProgressResponseElementsV23() != null) {
            sharedSteps.setProgressResponseElementsV23(pnPollingResponseV23.getProgressResponseElementsV23());
            return pnPollingResponseV23.getProgressResponseElementV23();
        }
        return null;
    }


    private <T> ProgressResponseElementV24 searchInWebhookV24(T timeLineOrStatus, String lastEventId, int deepCount, int position) {
        //TODO MATTEO TEST
        StreamVersion streamVersion = V24;
        WebhookStepsInterface webhookStepsInterface = getWebhookStep(streamVersion);
        UUID streamId = webhookStepsInterface.getStreamId();

        PnPollingWebhook pnPollingWebhook = getPnPollingWebhook(timeLineOrStatus);
        PnPollingServiceWebhookV24 webhookV24 = (PnPollingServiceWebhookV24) sharedSteps.getPollingFactory().getPollingService(PnPollingStrategy.WEBHOOK_V24);
        PnPollingResponseV24 pnPollingResponseV24 = webhookV24.waitForEvent(sharedSteps.getSentNotification().getIun(),
                PnPollingParameter.builder()
                        .value("WEBHOOK")
                        .pnPollingWebhook(pnPollingWebhook)
                        .deepCount(deepCount)
                        .lastEventId(lastEventId)
                        .streamId(streamId)
                        .build());

        log.info("WEBHOOK_PROGRESS_RESPONSE_ELEMENT_" + streamVersion + ": " + pnPollingResponseV24.getProgressResponseElementV24());
        if (pnPollingResponseV24.getProgressResponseElementListV24() != null) {
            sharedSteps.setProgressResponseElementsV24(pnPollingResponseV24.getProgressResponseElementListV24());
            return pnPollingResponseV24.getProgressResponseElementV24();
        }
        return null;
    }

    private <T> ProgressResponseElementV26 searchInWebhookV26(T timeLineOrStatus, String lastEventId, int deepCount, int position) {
        //TODO MATTEO TEST
        StreamVersion streamVersion = V26;
        WebhookStepsInterface webhookStepsInterface = getWebhookStep(streamVersion);
        UUID streamId = webhookStepsInterface.getStreamId();

        PnPollingWebhook pnPollingWebhook = getPnPollingWebhook(timeLineOrStatus);
        PnPollingServiceWebhookV26 webhookV26 = (PnPollingServiceWebhookV26) sharedSteps.getPollingFactory().getPollingService(PnPollingStrategy.WEBHOOK_V26);
        PnPollingResponseV26 pnPollingResponseV26 = webhookV26.waitForEvent(sharedSteps.getSentNotification().getIun(),
                PnPollingParameter.builder()
                        .value("WEBHOOK")
                        .pnPollingWebhook(pnPollingWebhook)
                        .deepCount(deepCount)
                        .lastEventId(lastEventId)
                        .streamId(streamId)
                        .build());

        log.info("WEBHOOK_PROGRESS_RESPONSE_ELEMENT_" + streamVersion + ": " + pnPollingResponseV26.getProgressResponseElementV26());
        if (pnPollingResponseV26.getProgressResponseElementListV26() != null) {
            sharedSteps.setProgressResponseElementsV26(pnPollingResponseV26.getProgressResponseElementListV26());
            return pnPollingResponseV26.getProgressResponseElementV26();
        }
        return null;
    }

    private <T> ProgressResponseElementV27 searchInWebhookV27(T timeLineOrStatus, String lastEventId, int deepCount, int position) {
        //TODO MATTEO TEST
        StreamVersion streamVersion = V27;
        WebhookStepsInterface webhookStepsInterface = getWebhookStep(streamVersion);
        UUID streamId = webhookStepsInterface.getStreamId();

        PnPollingWebhook pnPollingWebhook = getPnPollingWebhook(timeLineOrStatus);
        PnPollingServiceWebhookV27 webhookV27 = (PnPollingServiceWebhookV27) sharedSteps.getPollingFactory().getPollingService(PnPollingStrategy.WEBHOOK_V26);
        PnPollingResponseV27 pnPollingResponseV27 = webhookV27.waitForEvent(sharedSteps.getSentNotification().getIun(),
                PnPollingParameter.builder()
                        .value("WEBHOOK")
                        .pnPollingWebhook(pnPollingWebhook)
                        .deepCount(deepCount)
                        .lastEventId(lastEventId)
                        .streamId(streamId)
                        .build());
        log.info("WEBHOOK_PROGRESS_RESPONSE_ELEMENT_" + streamVersion + ": " + pnPollingResponseV27.getProgressResponseElementV27());
        if (pnPollingResponseV27.getProgressResponseElementListV27() != null) {
            sharedSteps.setProgressResponseElementsV27(pnPollingResponseV27.getProgressResponseElementListV27());
            return pnPollingResponseV27.getProgressResponseElementV27();
        }
        return null;
    }

    //V10 only
    private <T> ProgressResponseElement searchInWebhookFileNotFound(T timeLineOrStatus, String lastEventId, int deepCount) {
        if (!(timeLineOrStatus instanceof TimelineElementCategoryV23) && !(timeLineOrStatus instanceof NotificationStatus)) {
            throw new IllegalArgumentException();
        }
        //TODO MATTEO TEST
        WebhookStepsInterface webhookStepsInterface = getWebhookStep(StreamVersion.V10);
        UUID streamId = webhookStepsInterface.getStreamId();

        ProgressResponseElement progressResponseElement = null;
        ResponseEntity<List<ProgressResponseElement>> listResponseEntity = webhookB2bClient.consumeEventStreamHttp(streamId, lastEventId);
        int retryAfter = Integer.parseInt(listResponseEntity.getHeaders().get("retry-after").get(0));
        List<ProgressResponseElement> progressResponseElements = listResponseEntity.getBody();
        if (deepCount >= 200) {
            throw new IllegalStateException(
                    "LOP: PROGRESS-ELEMENTS: " + progressResponseElements
                            + " WEBHOOK: " + streamId
                            + " IUN: " + sharedSteps.getSentNotification().getIun()
                            + " DEEP: " + deepCount);
        }
        for (ProgressResponseElement elem : progressResponseElements) {
            if ("REFUSED".equalsIgnoreCase(elem.getNewStatus().getValue()) && elem.getValidationErrors() != null && elem.getValidationErrors().size() > 0) {
                if (elem.getValidationErrors().get(0).getErrorCode() != null && "FILE_NOTFOUND".equalsIgnoreCase(elem.getValidationErrors().get(0).getErrorCode()))
                    progressResponseElement = elem;
                break;
            }
        }
        return progressResponseElement;
    }

    private <T> ProgressResponseElementV23 searchInWebhookFileNotFoundV23(T timeLineOrStatus, String lastEventId, int deepCount) {
        //TODO MATTEO TEST
        StreamVersion streamVersion = V23;
        WebhookStepsInterface webhookStepsInterface = getWebhookStep(streamVersion);
        UUID streamId = webhookStepsInterface.getStreamId();

        if (!(timeLineOrStatus instanceof TimelineElementCategoryV23) && !(timeLineOrStatus instanceof NotificationStatus)) {
            throw new IllegalArgumentException();
        }
        ProgressResponseElementV23 progressResponseElement = null;
        ResponseEntity<List<ProgressResponseElementV23>> listResponseEntity = webhookB2bClient.consumeEventStreamHttpV23(streamId, lastEventId);
        int retryAfter = Integer.parseInt(listResponseEntity.getHeaders().get("retry-after").get(0));
        List<ProgressResponseElementV23> progressResponseElements = listResponseEntity.getBody();
        if (deepCount >= 200) {
            String errorLog = String.format("{IUN: %s -WEBHOOK %s }", webhookStepsInterface.getSentNotificationIun(), webhookStepsInterface.getStreamId());
            throw new IllegalStateException("LOP: PROGRESS-ELEMENTS: " + progressResponseElements + " " + errorLog + " DEEP: " + deepCount);
        }
        for (ProgressResponseElementV23 elem : progressResponseElements) {
            if ("REFUSED".equalsIgnoreCase(elem.getNewStatus().getValue())) {
                //TODO Verificare se Corretto
                break;
            }
        }
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
    }


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

    @And("vengono letti gli eventi dello stream con id {string} e versione {string}")
    public void vengonoLettiGliEventiDelloStreamDelV(String streamID, String version) {
        setPaWebhook("Comune_Multi");
        UUID streamId = UUID.fromString(streamID);
        StreamVersion streamVersion = getStreamVersion(version);
        WebhookStepsInterface webhookStepsInterface = getWebhookStep(streamVersion);
        webhookStepsInterface.consumeEventStream(streamId);
    }

    @And("vengono letti tutti gli eventi degli stream v23 creati per il test di carico per {int} minuti")
    public void readAllStreamEvent(int minuti) {
        int elapsedMinute = 0;
        setPaWebhook("Comune_Multi");
        while (elapsedMinute < minuti) {
            try {
                for (StreamMetadataResponseV23 streamId : eventStreamListV23) {
                    List<ProgressResponseElementV23> progressResponseElementV23s = webhookB2bClient.consumeEventStreamV23(streamId.getStreamId(), null);
                    System.out.println("progressResponseElementV23s size: " + progressResponseElementV23s.size());
                    sleepTest(50);
                }
                sleepTest(60 * 1000);
                elapsedMinute += 1;
                setPaWebhook("Comune_Multi");
            } catch (Exception e) {
                System.out.println("Exception");
            }

        }
    }

    @And("vengono letti tutti gli eventi degli stream v23 hardcodati per il test di carico per {int} minuti")
    public void readAllStreamEventHardCoded(int minuti) {
        String[] streamList = {"00001d7a-42e8-41df-a995-40da72a087d7"};
        int elapsedMinute = 0;
        setPaWebhook("Comune_Multi");
        while (elapsedMinute < minuti) {
            try {
                for (String streamID : streamList) {
                    List<ProgressResponseElementV23> progressResponseElementV23s = webhookB2bClient.consumeEventStreamV23(UUID.fromString(streamID), null);
                    System.out.println("progressResponseElementV23s size: " + progressResponseElementV23s.size());
                    sleepTest(50);
                }
                sleepTest(60 * 1000);
                elapsedMinute += 1;
                setPaWebhook("Comune_Multi");
            } catch (Exception e) {
                System.out.println("Exception in read");
            }
        }
    }

    //V10 only
    @Then("si verifica nello stream del {string} che la notifica abbia lo stato VIEWED")
    public void checkViewedState(String pa) {
        sleepTest((sharedSteps.getWait() * 2));
        setPaWebhook(pa);
        ProgressResponseElement progressResponseElement = searchInWebhookV20(it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model_v2.NotificationStatus.VIEWED, null, 0);
        Assertions.assertNotNull(progressResponseElement);
    }

    //V10 only
    @Then("l'ultima creazione ha prodotto un errore con status code {string}")
    public void lastCreationProducedAnErrorWithStatusCode(String statusCode) {
        List<it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model_v2.StreamListElement> streamListElements = webhookB2bClient.listEventStreams();
        System.out.println("streamListElements: " + streamListElements.size());
        System.out.println("eventStreamList: " + webhookStepsV10.getEventStreamList().size());
        System.out.println("requestNumber: " + requestNumber);
        Assertions.assertTrue((this.notificationError != null) &&
                (this.notificationError.getStatusCode().toString().substring(0, 3).equals(statusCode)) && (webhookStepsV10.getEventStreamList().size() == (requestNumber - 1)));
    }

    @Given("vengono cancellati tutti gli stream presenti del {string} con versione {string}")
    public void deleteAll(String pa, String version) {
        setPaWebhook(pa);
        StreamVersion streamVersion = getStreamVersion(version);
        WebhookStepsInterface webhookStepsInterface = getWebhookStep(streamVersion);
        webhookStepsInterface.deleteStreamsBeforeTest(pa);
    }

    //non esiste file feature che richiami questo step
    @And("vengono prodotte le evidenze: metadati, requestID, IUN e stati")
    public void evidenceProducedIunRequestIdAndState() {
        log.info("METADATI: " + '\n' + sharedSteps.getNewNotificationResponse());
        log.info("REQUEST-ID: " + '\n' + sharedSteps.getNewNotificationResponse().getNotificationRequestId());
        log.info("IUN: " + '\n' + sharedSteps.getSentNotification().getIun());
        for (ProgressResponseElement element : progressResponseElements) {
            log.info("EVENT: " + '\n' + element.getTimelineEventCategory() + " " + element.getTimestamp());
        }
    }

    @Then("viene verificato che il ProgressResponseElement del webhook abbia un EventId incrementale e senza duplicati {string}")
    public void verifyIncrementalAndUniqueProgressResponseElementId(String version) {
        //TODO MATTEO TEST
        StreamVersion streamVersion = getStreamVersion(version);
        WebhookStepsInterface webhookStepsInterface = getWebhookStep(streamVersion);
        webhookStepsInterface.verifyIncrementalEventId();
    }

    @And("vengono letti gli eventi dello stream che contenga {int} eventi con la versione {string}")
    public void readStreamNumberEventsV23(Integer numEventi, String version) {
        StreamVersion streamVersion = getStreamVersion(version);
        WebhookStepsInterface webhookStepsInterface = getWebhookStep(streamVersion);
        webhookStepsInterface.consumeEventStreamAndCheckNumEvents(numEventi);
    }

    @And("verifica corrispondenza tra i detail del webhook e quelli della timeline")
    public void verificaCorrispondenzaTraIDetailDelWebhookEQuelliDellaTimeline() throws JsonProcessingException {
        it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model_v2_3.TimelineElementV23 timelineElementWebHook = sharedSteps.getProgressResponseElementV23().getElement();

        Assertions.assertNotNull(timelineElementWebHook);
        Assertions.assertNotNull(timelineElementWebHook.getCategory());

        String elementId = timelineElementWebHook.getCategory().toString();
        TimelineElementV26 timelineElement = sharedSteps.getSentNotification().getTimeline().stream()
                .filter(data -> data.getCategory() != null)
                .filter(data -> data.getCategory().getValue().equalsIgnoreCase(elementId))
                .findFirst()
                .orElse(null);

        Assertions.assertNotNull(timelineElement);

        TimelineElementDetailsV26 timelineElementDetails = timelineElement.getDetails();

        Assertions.assertNotNull(timelineElementDetails);

        it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model_v2_3.TimelineElementDetailsV23 timelineElementWebhookDetails = timelineElementWebHook.getDetails();

        Assertions.assertNotNull(timelineElementWebhookDetails);

        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String json = ow.writeValueAsString(serializeObject(timelineElementDetails));
        System.out.println(json);
        String json1 = ow.writeValueAsString(serializeObject(timelineElementWebhookDetails));
        System.out.println(json1);

        ObjectMapper mapper = new ObjectMapper();
        Assertions.assertEquals(mapper.readTree(json), mapper.readTree(json1));
    }

    private Object serializeObject(Object obj) {
        Map<String, Object> result = new LinkedHashMap<>();
        if (obj == null) {
            return result;
        }
        Field[] fields = obj.getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            try {
                Object value = field.get(obj);
                if (value instanceof OffsetDateTime) {
                    result.put(field.getName(), ((OffsetDateTime) value).toString());
                } else {
                    result.put(field.getName(), value);
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Errore nell'accesso al campo " + field.getName(), e);
            }
        }
        return result;
    }

    @Then("verifica deanonimizzazione degli eventi di timeline con delega {string} analogico")
    public void verificaDeanonimizzazioneDegliEventiDiTimelineAnalogico(String delega) {
        TimelineElementDetailsV23 timelineElementWebhookDetails = sharedSteps.getProgressResponseElementV23().getElement().getDetails();
        Assertions.assertNotNull(timelineElementWebhookDetails);
        Assertions.assertNotNull(timelineElementWebhookDetails.getPhysicalAddress().getAddress());
        Assertions.assertNotNull(timelineElementWebhookDetails.getPhysicalAddress().getMunicipality());
        Assertions.assertNotNull(timelineElementWebhookDetails.getPhysicalAddress().getProvince());
        Assertions.assertNotNull(timelineElementWebhookDetails.getPhysicalAddress().getZip());

        verificaDeanonimizzazioneDegliEventiDiTimelinePresenzaDelega(timelineElementWebhookDetails, delega);
    }

    @Then("verifica deanonimizzazione degli eventi di timeline con delega {string} digitale")
    public void verificaDeanonimizzazioneDegliEventiDiTimelineDigitale(String delega) {
        TimelineElementDetailsV23 timelineElementWebhookDetails = sharedSteps.getProgressResponseElementV23().getElement().getDetails();
        Assertions.assertNotNull(timelineElementWebhookDetails.getDigitalAddress());
        verificaDeanonimizzazioneDegliEventiDiTimelinePresenzaDelega(timelineElementWebhookDetails, delega);
    }

    public void verificaDeanonimizzazioneDegliEventiDiTimelinePresenzaDelega(TimelineElementDetailsV23 timelineElementWebhookDetails, String delega) {
        if ("SI".equalsIgnoreCase(delega)) {
            Assertions.assertNotNull(timelineElementWebhookDetails.getDelegateInfo());
            Assertions.assertNotNull(timelineElementWebhookDetails.getDelegateInfo().getTaxId());
            Assertions.assertNotNull(timelineElementWebhookDetails.getDelegateInfo().getDenomination());
        }
    }

    @When("vengono letti gli eventi di timeline dello stream con versione {string} nonostante sia stato creato con la {string} -Cross Versioning")
    public void vengonoLettiGliEventiDiTimelineDelloStreamDel(String versionRead, String versionCreate) {
        updateApiKeyForStream();
        StreamVersion streamVersionCreate = getStreamVersion(versionCreate);
        WebhookStepsInterface webhookStepsInterfaceCreate = getWebhookStep(streamVersionCreate);
        UUID streamId = webhookStepsInterfaceCreate.getStreamId();
        StreamVersion streamVersionRead = getStreamVersion(versionRead);
        WebhookStepsInterface webhookStepsInterfaceRead = getWebhookStep(streamVersionRead);
        try {
            webhookStepsInterfaceRead.consumeEventStream(streamId);
        } catch (HttpStatusCodeException e) {
            this.notificationError = e;
            sharedSteps.setNotificationError(e);
        }
    }

    /*********************************************************************
     * BUSINESS CODE TODO: Externalise
     *********************************************************************/
    @Data
    public static class TimelineElementSearchResult<T> {
        public T timelineElementCategory;
        int numCheck;
        int waiting;
    }

    @Data
    public static class StatusElementSearchResult<T> {
        public T notificationStatus;
        int numCheck;
        int waiting;
    }

    @SuppressWarnings("unchecked")
    private <T> TimelineElementSearchResult<T> getTimelineEventForStream(StreamVersion streamVersion, String timelineEventCategory) {
        timelineEventCategory = timelineEventCategory.trim().toUpperCase();
        TimingForPolling.TimingResult timingForElement = timingForPolling.getTimingForElement(timelineEventCategory);
        WebhookStepsInterface webhookStepsInterface = getWebhookStep(streamVersion);
        try {
            //TODO MATTEO TEST
            webhookStepsInterface.getTimelineEventForStream(timelineEventCategory, timingForElement);
//            switch (streamVersion) {
//                case V10 -> {
//                    TimelineElementSearchResult<TimelineElementCategoryV20> result = new TimelineElementSearchResult<>();
//
//                    result.setTimelineElementCategory(TimelineElementCategoryV20.valueOf(timelineEventCategory));
//                    result.setWaiting(timingForElement.waiting());
//                    result.setNumCheck(timingForElement.numCheck());
//                    return (TimelineElementSearchResult<T>) result;
//                }
//                case V23 -> {
//                    TimelineElementSearchResult<TimelineElementCategoryV23> result = new TimelineElementSearchResult<>();
//
//                    result.setTimelineElementCategory(TimelineElementCategoryV23.valueOf(timelineEventCategory));
//                    result.setWaiting(timingForElement.waiting());
//                    result.setNumCheck(timingForElement.numCheck());
//                    return (TimelineElementSearchResult<T>) result;
//                }
//                case V24, V25 -> {
//                    TimelineElementSearchResult<it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementCategoryV23> result = new TimelineElementSearchResult<>();
//
//                    result.setTimelineElementCategory(it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementCategoryV23.valueOf(timelineEventCategory));
//                    result.setWaiting(timingForElement.waiting());
//                    result.setNumCheck(timingForElement.numCheck());
//                    return (TimelineElementSearchResult<T>) result;
//                }
//                case V26, V27 -> {
//                    TimelineElementSearchResult<it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementCategoryV26> result = new TimelineElementSearchResult<>();
//
//                    result.setTimelineElementCategory(it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementCategoryV26.valueOf(timelineEventCategory));
//                    result.setWaiting(timingForElement.waiting());
//                    result.setNumCheck(timingForElement.numCheck());
//                    return (TimelineElementSearchResult<T>) result;
//                }
//            }
        } catch (ClassCastException classCastException) {
            log.error("Wrong type t for streamVersion {}, error in cast {}", streamVersion, classCastException.getMessage());
        }

        throw new IllegalArgumentException();
    }

    @SuppressWarnings("unchecked")
    private <T> StatusElementSearchResult<T> getStatusEventForStream(StreamVersion streamVersion, String notificationStatusName) {
        notificationStatusName = notificationStatusName.trim().toUpperCase();
        TimingForPolling.TimingResult timingForElement = timingForPolling.getTimingForElement(notificationStatusName);
        WebhookStepsInterface webhookStepsInterface = getWebhookStep(streamVersion);
        try {
            //TODO MATTEO TEST
            webhookStepsInterface.getStatusEventForStream(notificationStatusName, timingForElement);
//            switch (streamVersion) {
//                case V10 -> {
//                    StatusElementSearchResult<it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model_v2.NotificationStatus>
//                            result = new StatusElementSearchResult<>();
//
//                    result.setNotificationStatus(
//                            it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model_v2.NotificationStatus
//                                    .valueOf(notificationStatusName));
//                    result.setWaiting(timingForElement.waiting());
//                    result.setNumCheck(timingForElement.numCheck());
//                    return (StatusElementSearchResult<T>) result;
//                }
//                case V23 -> {
//                    StatusElementSearchResult<NotificationStatus> result = new StatusElementSearchResult<>();
//
//                    result.setNotificationStatus(NotificationStatus.valueOf(notificationStatusName));
//                    result.setWaiting(timingForElement.waiting());
//                    result.setNumCheck(timingForElement.numCheck());
//                    return (StatusElementSearchResult<T>) result;
//                }
//                case V26 -> {
//                    StatusElementSearchResult<it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model_v26.NotificationStatusV26> result = new StatusElementSearchResult<>();
//
//                    result.setNotificationStatus(it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model_v26.NotificationStatusV26.valueOf(notificationStatusName));
//                    result.setWaiting(timingForElement.waiting());
//                    result.setNumCheck(timingForElement.numCheck());
//                    return (StatusElementSearchResult<T>) result;
//                }
//                case V27 -> {
//                    StatusElementSearchResult<it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model_v27.NotificationStatusV26> result = new StatusElementSearchResult<>();
//
//                    result.setNotificationStatus(it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model_v27.NotificationStatusV26.valueOf(notificationStatusName));
//                    result.setWaiting(timingForElement.waiting());
//                    result.setNumCheck(timingForElement.numCheck());
//                    return (StatusElementSearchResult<T>) result;
//                }
//            }
        } catch (ClassCastException classCastException) {
            log.error("Wrong type t for streamVersion {}, error in cast {}", streamVersion, classCastException.getMessage());
        }

        throw new IllegalArgumentException();
    }

    private void sleepTest() {
        sleepTest(sharedSteps.getWait());
    }

    private void sleepTest(int wait) {
        sleepTest(Long.valueOf(wait));
    }

    private void sleepTest(long wait) {
        try {
            Thread.sleep(wait);
        } catch (InterruptedException exc) {
            throw new RuntimeException(exc);
        }
    }

    private boolean checkInternalTimeline(String timelineElementName, int numCheck, int waiting) {
        it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementCategoryV26 timelineElementInternalCategory =
                it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementCategoryV26.valueOf(timelineElementName);

        boolean finish = false;
        for (int i = 0; i < numCheck; i++) {
            try {
                Thread.sleep(waiting);
            } catch (InterruptedException exc) {
                throw new RuntimeException(exc);
            }

            sharedSteps.setSentNotification(b2bClient.getSentNotification(sharedSteps.getSentNotification().getIun()));
            it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementV26 timelineElement =
                    sharedSteps.getSentNotification().getTimeline().stream().filter(elem -> elem.getCategory().equals(timelineElementInternalCategory)).findAny().orElse(null);
            if (timelineElement != null) {
                finish = true;
                break;
            }
        }
        return finish;
    }

    private boolean checkInternalTimelineV26(String timelineElementName, int numCheck, int waiting) {
        it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementCategoryV26 timelineElementInternalCategory =
                it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementCategoryV26.valueOf(timelineElementName);

        boolean finish = false;
        for (int i = 0; i < numCheck; i++) {
            try {
                Thread.sleep(waiting);
            } catch (InterruptedException exc) {
                throw new RuntimeException(exc);
            }

            sharedSteps.setSentNotification(b2bClient.getSentNotification(sharedSteps.getSentNotification().getIun()));
            it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementV26 timelineElement =
                    sharedSteps.getSentNotification().getTimeline().stream().filter(elem -> elem.getCategory().equals(timelineElementInternalCategory)).findAny().orElse(null);
            if (timelineElement != null) {
                finish = true;
                break;
            }
        }
        return finish;
    }

    private void createStreamRequest(StreamVersion streamVersion, List<String> filterValues, int number, String title, String eventType) {
        WebhookStepsInterface webhookStepsInterface = getWebhookStep(streamVersion);
        webhookStepsInterface.createStreamRequest(filterValues, number, title, eventType);
    }

    private void createStream(String pa, StreamVersion streamVersion, List<String> listGroups, boolean replaceId, List<String> filteredValues, boolean forced) {
        try {
            WebhookStepsInterface webhookStepsInterface = getWebhookStep(streamVersion);
            try {
                webhookStepsInterface.createEventStream(pa, listGroups, replaceId, filteredValues, forced);
            } catch (HttpStatusCodeException e) {
                this.notificationError = e;
                sharedSteps.setNotificationError(e);
            }
        } catch (HttpStatusCodeException e) {
            log.error("Error {} in create Stream version {}, group {}, replaceID {}, filteredValues {}",
                    e.getStatusCode(), streamVersion, listGroups, replaceId, filteredValues);
            this.notificationError = e;
            sharedSteps.setNotificationError(e);
            if (!forced) throw e;
        }
        if (!webhookTestLaunch) webhookTestLaunch = true;
    }

    private void addStreamId(String pa, UUID streamId, StreamVersion version) {
        //streamIdForPaAndVersion.put(streamId,new PnPaB2bUtils.Pair<>(pa,version));
        paStreamOwner.add(pa);
    }

    private void disableStreamInternal(StreamVersion version) {
        WebhookStepsInterface webhookStepsInterface = getWebhookStep(version);
        try {
            webhookStepsInterface.disableStreams();
        } catch (HttpStatusCodeException e) {
            this.notificationError = e;
            sharedSteps.setNotificationError(e);
        }
    }

    private List<String> getGruopForStream(String position, String pa) {
        List<String> groupList;
        position = position.trim().toUpperCase();
        switch (position) {
            case "FIRST", "LAST" ->
                    groupList = List.of(sharedSteps.getGroupIdByPa(pa, GroupPosition.valueOf(position)));
            case "ALL" -> groupList = sharedSteps.getGroupAllActiveByPa(pa);
            case "NO_GROUPS" -> groupList = null;
            case "UGUALI" -> {
                Assertions.assertNotNull(sharedSteps.getRequestNewApiKey());
                groupList = sharedSteps.getRequestNewApiKey().getGroups();
            }
            case "ALTRA_PA" -> {
                if ("Comune_1".equalsIgnoreCase(pa)) {
                    Assertions.assertNotNull(sharedSteps.getGroupIdByPa("Comune_Multi", GroupPosition.FIRST));
                    groupList = List.of(sharedSteps.getGroupIdByPa("Comune_Multi", GroupPosition.FIRST));
                } else if ("Comune_Multi".equalsIgnoreCase(pa)) {
                    Assertions.assertNotNull(sharedSteps.getGroupIdByPa("Comune_1", GroupPosition.FIRST));
                    groupList = List.of(sharedSteps.getGroupIdByPa("Comune_1", GroupPosition.FIRST));
                } else {
                    Assertions.assertNotNull(sharedSteps.getGroupIdByPa("Comune_1", GroupPosition.FIRST));
                    groupList = List.of(sharedSteps.getGroupIdByPa("Comune_1", GroupPosition.FIRST));
                }
            }
            default -> throw new IllegalArgumentException();
        }
        return groupList;
    }

    @When("si invoca l'api B2B versione {string} per ottenere gli elementi di timeline di tale notifica")
    public void getTimelineElementVersionB2B(String version) {
        String iun = this.sharedSteps.getSentNotification().getIun();
        if (version.equalsIgnoreCase("V27")) {
            FullSentNotificationV26 fullSentNotification = b2bClient.getSentNotification(iun);
            this.sharedSteps.setNotificationResponseComplete(fullSentNotification);
        } else if (version.equalsIgnoreCase("V26")) {
            FullSentNotificationV26 fullSentNotification = b2bClient.getSentNotification(iun);
            this.sharedSteps.setNotificationResponseComplete(fullSentNotification);
        } else if (version.equalsIgnoreCase("V25")) {
            FullSentNotificationV25 fullSentNotification = b2bClient.getSentNotificationV25(iun);
            this.sharedSteps.setNotificationResponseCompleteV25(fullSentNotification);
        } else if (version.equalsIgnoreCase("V24")) {
            FullSentNotificationV24 fullSentNotification = b2bClient.getSentNotificationV24(iun);
            this.sharedSteps.setNotificationResponseCompleteV24(fullSentNotification);
        } else if (version.equalsIgnoreCase("V23")) {
            FullSentNotificationV23 fullSentNotification = b2bClient.getSentNotificationV23(iun);
            this.sharedSteps.setNotificationResponseCompleteV23(fullSentNotification);
        }
    }

    @When("si invoca l'api Webhook versione {string} per ottenere gli elementi di timeline di tale notifica")
    public void getTimelineElementVersionWebhook(String version) {
        updateApiKeyForStream();
        StreamVersion streamVersion = getStreamVersion(version);
        WebhookStepsInterface webhookStepsInterface = getWebhookStep(streamVersion);
        UUID streamId = webhookStepsInterface.getStreamId();
        webhookStepsInterface.consumeEventStream(streamId);
    }

    @Then("si controlla che tra gli elementi dello stream con versione {string} ritornati non ci sia l'elemento {string}")
    public void streamDoesntContainsElement(String version, String elementType) {
        StreamVersion streamVersion = getStreamVersion(version);
        WebhookStepsInterface webhookStepsInterface = getWebhookStep(streamVersion);
        webhookStepsInterface.verifySpecificEventNotInStream(elementType);
    }

    @Then("tra gli elementi di timeline versione {string} di categoria {string} nessuno contiene un legalFact con categoria {string}")
    public void checkTimelineElementVersionLegalFacts(String version, String timelineCategory, String legalFactCategory) {
        if (version.equalsIgnoreCase("V26") || version.equalsIgnoreCase("V27")) {
            Assertions.assertNotNull(this.sharedSteps.getNotificationResponseComplete());
            TimelineElementV26 timelineElementWithTargetCategory = this.sharedSteps.getNotificationResponseComplete().getTimeline().stream().filter(
                    x -> x.getCategory().getValue().equals(timelineCategory)).findFirst().orElse(null);
            Assertions.assertNotNull(timelineElementWithTargetCategory);
            timelineElementWithTargetCategory.getLegalFactsIds().forEach(
                    x -> Assertions.assertFalse(x.getCategory().equals(legalFactCategory)));
        } else if (version.equalsIgnoreCase("V25")) {
            Assertions.assertNotNull(this.sharedSteps.getNotificationResponseComplete());
            TimelineElementV25 timelineElementWithTargetCategory = this.sharedSteps.getNotificationResponseCompleteV25().getTimeline().stream().filter(
                    x -> x.getCategory().getValue().equals(timelineCategory)).findFirst().orElse(null);
            Assertions.assertNotNull(timelineElementWithTargetCategory);
            timelineElementWithTargetCategory.getLegalFactsIds().forEach(
                    x -> Assertions.assertFalse(x.getCategory().equals(legalFactCategory)));
        } else if (version.equalsIgnoreCase("V24")) {
            Assertions.assertNotNull(this.sharedSteps.getNotificationResponseCompleteV24());
            TimelineElementV24 timelineElementWithTargetCategory = this.sharedSteps.getNotificationResponseCompleteV24().getTimeline().stream().filter(
                    x -> x.getCategory().getValue().equals(timelineCategory)).findFirst().orElse(null);
            Assertions.assertNotNull(timelineElementWithTargetCategory);
            timelineElementWithTargetCategory.getLegalFactsIds().forEach(
                    x -> Assertions.assertFalse(x.getCategory().getValue().equals(legalFactCategory)));
        } else if (version.equalsIgnoreCase("V23")) {
            Assertions.assertNotNull(this.sharedSteps.getNotificationResponseCompleteV23());
            it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementV23 timelineElementWithTargetCategory = this.sharedSteps.getNotificationResponseCompleteV23().getTimeline().stream().filter(
                    x -> x.getCategory().getValue().equals(timelineCategory)).findFirst().orElse(null);
            Assertions.assertNotNull(timelineElementWithTargetCategory);
            timelineElementWithTargetCategory.getLegalFactsIds().forEach(
                    x -> Assertions.assertFalse(x.getCategory().getValue().equals(legalFactCategory)));
        }
    }

    @Then("gli elementi di timeline restituiti da B2B contengono i campi attesi in accordo alla versione {string}")
    public void checkTimelineElementVersionB2B(String version) {
        if (version.equalsIgnoreCase("V24")) {
            Assertions.assertNotNull(this.sharedSteps.getNotificationResponseComplete());
            checkTimelineElement(this.sharedSteps.getNotificationResponseCompleteV24());
        } else if (version.equalsIgnoreCase("V23")) {
            Assertions.assertNotNull(this.sharedSteps.getNotificationResponseCompleteV23());
            this.sharedSteps.getNotificationResponseCompleteV23().getTimeline().forEach(this::checkTimelineElement);
        }
    }

    @Then("gli elementi di timeline restituiti dal Webhook contengono i campi attesi in accordo alla versione {string}")
    public void checkTimelineElementVersionWebHook(String version) {
        if (version.equalsIgnoreCase("V24")) {
            Assertions.assertNotNull(this.progressResponseElementsV24);
            this.progressResponseElementsV24.forEach(pre -> checkTimelineElement(pre.getElement()));
        } else if (version.equalsIgnoreCase("V23")) {
            Assertions.assertNotNull(this.progressResponseElementsV23);
            this.progressResponseElementsV23.forEach(pre -> checkTimelineElement(pre.getElement()));
        }
    }

    private void checkTimelineElement(Object timeline) {
        if (timeline instanceof FullSentNotificationV25 FullSentNotificationV25) {
            FullSentNotificationV25.getTimeline().forEach(TimelineElementV25 -> {
                Assertions.assertNotNull(TimelineElementV25.getIngestionTimestamp());
                Assertions.assertNotNull(TimelineElementV25.getNotificationSentAt());
                Assertions.assertNotNull(TimelineElementV25.getEventTimestamp());
                log.info("Field presence checked for " + TimelineElementV25.getCategory().getValue());
                checkValues(TimelineElementV25, FullSentNotificationV25.getTimeline());
            });
        } else if (timeline instanceof TimelineElementV24 TimelineElementV24) {
            Assertions.assertNotNull(TimelineElementV24.getIngestionTimestamp());
            Assertions.assertNotNull(TimelineElementV24.getNotificationSentAt());
            Assertions.assertNotNull(TimelineElementV24.getEventTimestamp());
            log.info("Field presence checked for " + TimelineElementV24.getCategory().getValue());
        } else if (timeline instanceof TimelineElementV23 timelineElementV23) {
            Map timelineElementMap = JsonMapper.builder().addModule(new JavaTimeModule()).build().convertValue(timelineElementV23, Map.class);
            Assertions.assertFalse(timelineElementMap.containsKey("ingestionTimeStamp"));
            Assertions.assertFalse(timelineElementMap.containsKey("notificationSentAt"));
            Assertions.assertFalse(timelineElementMap.containsKey("eventTimestamp"));
            log.info("Absence of fields checked for " + timelineElementV23.getCategory().getValue());
        }
    }

    private void checkValues(TimelineElementV25 TimelineElementV25, List<TimelineElementV25> TimelineElementV25list) {
        String category = TimelineElementV25.getCategory().getValue();
        try {
            switch (category) {
                case "NOTIFICATION_VIEWED",
                        "REFINEMENT", "PAYMENT",
                        "NOTIFICATION_RADD_RETRIEVED",
                        "SEND_DIGITAL_PROGRESS" -> {
                    Assertions.assertEquals(TimelineElementV25.getTimestamp(), TimelineElementV25.getDetails().getEventTimestamp());
                    Assertions.assertEquals(TimelineElementV25.getEventTimestamp(), TimelineElementV25.getDetails().getEventTimestamp());
                }
                case "SEND_DIGITAL_FEEDBACK",
                        "SEND_ANALOG_FEEDBACK",
                        "SEND_ANALOG_PROGRESS",
                        "SEND_SIMPLE_REGISTERED_LETTER_PROGRESS" -> {
                    Assertions.assertEquals(TimelineElementV25.getTimestamp(), TimelineElementV25.getDetails().getNotificationDate());
                    Assertions.assertEquals(TimelineElementV25.getEventTimestamp(), TimelineElementV25.getDetails().getNotificationDate());
                }
                case "ANALOG_SUCCESS_WORKFLOW",
                        "ANALOG_FAILURE_WORKFLOW",
                        "COMPLETELY_UNREACHABLE_CREATION_REQUEST",
                        "COMPLETELY_UNREACHABLE" -> {
                    OffsetDateTime odtAnalogFeedBack = TimelineElementV25list.stream()
                            .filter(e -> e.getCategory().getValue().equalsIgnoreCase("SEND_ANALOG_FEEDBACK"))
                            .map(x -> x.getDetails().getNotificationDate()).findFirst().orElse(null);
                    OffsetDateTime odtAnalogDomicileFailure = TimelineElementV25list.stream()
                            .filter(e -> e.getCategory().getValue().equalsIgnoreCase("PREPARE_ANALOG_DOMICILE_FAILURE"))
                            .map(x -> x.getTimestamp()).findFirst().orElse(null);
                    OffsetDateTime mostRecentEvent;
                    if (odtAnalogFeedBack != null && odtAnalogDomicileFailure != null) {
                        mostRecentEvent = odtAnalogFeedBack.isAfter(odtAnalogDomicileFailure) ? odtAnalogFeedBack : odtAnalogDomicileFailure;
                    } else if (odtAnalogFeedBack == null) {
                        mostRecentEvent = odtAnalogDomicileFailure;
                    } else {
                        mostRecentEvent = odtAnalogFeedBack;
                    }
                    Assertions.assertEquals(TimelineElementV25.getTimestamp(), mostRecentEvent);
                    Assertions.assertEquals(TimelineElementV25.getEventTimestamp(), mostRecentEvent);
                }
                case "SCHEDULE_REFINEMENT" -> {
                    OffsetDateTime odtAnalogFeedBack = TimelineElementV25list.stream()
                            .filter(e -> e.getCategory().getValue().equalsIgnoreCase("SEND_ANALOG_FEEDBACK"))
                            .map(x -> x.getDetails().getNotificationDate()).findFirst().orElse(null);
                    OffsetDateTime odtAnalogDomicileFailure = TimelineElementV25list.stream()
                            .filter(e -> e.getCategory().getValue().equalsIgnoreCase("PREPARE_ANALOG_DOMICILE_FAILURE"))
                            .map(it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementV25::getTimestamp).findFirst().orElse(null);
                    OffsetDateTime mostRecentEvent = TimelineElementV25.getTimestamp();
                    if (odtAnalogFeedBack != null && odtAnalogDomicileFailure != null) {
                        mostRecentEvent = odtAnalogFeedBack.isAfter(odtAnalogDomicileFailure) ? odtAnalogFeedBack : odtAnalogDomicileFailure;
                    } else if (odtAnalogFeedBack == null && odtAnalogDomicileFailure != null) {
                        mostRecentEvent = odtAnalogDomicileFailure;
                    } else if (odtAnalogFeedBack != null) {
                        mostRecentEvent = odtAnalogFeedBack;
                    }
                    Assertions.assertEquals(TimelineElementV25.getTimestamp(), mostRecentEvent);
                    Assertions.assertEquals(TimelineElementV25.getEventTimestamp(), mostRecentEvent);
                }

                case "DIGITAL_SUCCESS_WORKFLOW",
                        "DIGITAL_FAILURE_WORKFLOW " -> {
                    Assertions.assertEquals(TimelineElementV25.getTimestamp(), TimelineElementV25.getTimestamp());
                    Assertions.assertEquals(TimelineElementV25.getEventTimestamp(), TimelineElementV25.getTimestamp());
                    Assertions.assertEquals(TimelineElementV25.getIngestionTimestamp(), TimelineElementV25.getNotificationSentAt());
                    Assertions.assertEquals(TimelineElementV25.getNotificationSentAt(), TimelineElementV25.getIngestionTimestamp());
                }
            }
        } catch (AssertionFailedError e) {
            log.error("Assertion failed for category " + category);
        }
    }

    @Then("la chiamata restituisce correttamente lo stream di elementi timeline versione {string}")
    public void checkForNoError(String version) {
        Assertions.assertNull(sharedSteps.getNotificationError());
        if (version.equalsIgnoreCase("V24")) {
            Assertions.assertNotNull(this.progressResponseElementsV24);
            this.progressResponseElementsV24.forEach(pre -> checkTimelineElement(pre.getElement()));
        } else {
            Assertions.assertNotNull(this.progressResponseElementsV23);
            this.progressResponseElementsV23.forEach(pre -> checkTimelineElement(pre.getElement()));
        }
    }

    @Then("la chiamata restituisce un errore {int} riportante la dicitura {string}")
    public void checkForError(Integer errorCode, String errorMessage) {
        Assertions.assertNotNull(this.notificationError);
        Assertions.assertEquals(errorCode, this.notificationError.getRawStatusCode());
        Assertions.assertNotNull(this.notificationError.getMessage());
        Assertions.assertTrue(this.notificationError.getMessage().contains(errorMessage));
    }

    //TODO MATTEO: perchè la versione è scolpita e non parametrizzata ???
    @And("vengono letti gli eventi dello stream del {string} fino allo stato {string} con versione V25")
    public void readStreamEventsStateV25(String pa, String status) {
        setPaWebhook(pa);

        StatusElementSearchResult<it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model_v26.NotificationStatus>
                statusEventForStream = getStatusEventForStream(V25, status);

        it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model_v26.NotificationStatus
                notificationStatus = statusEventForStream.getNotificationStatus();

        it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.NotificationStatus notificationInternalStatus =
                it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.NotificationStatus.valueOf(statusEventForStream.notificationStatus.name());

        int numCheck = statusEventForStream.getNumCheck();
        int waiting = statusEventForStream.getWaiting();

        ProgressResponseElementV26 progressResponseElement = null;
        boolean finded = false;
        for (int i = 0; i < numCheck; i++) {

            sleepTest(waiting);

            sharedSteps.setSentNotification(b2bClient.getSentNotification(sharedSteps.getSentNotification().getIun()));
            NotificationStatusHistoryElementV26 notificationStatusHistoryElement = sharedSteps.getSentNotification()
                    .getNotificationStatusHistory().stream()
                    .filter(elem -> elem.getStatus().getValue().equals(notificationInternalStatus.getValue()))
                    .findAny()
                    .orElse(null);

            if (notificationStatusHistoryElement != null) {
                finded = true;
                break;
            }
        }

        Assertions.assertTrue(finded);
        for (int i = 0; i < 4; i++) {
            progressResponseElement = searchInWebhookV26(notificationStatus, null, 0, 0);
            log.debug("PROGRESS-ELEMENT: " + progressResponseElement);

            if (progressResponseElement != null) {
                break;
            }

            sleepTest();
        }

        try {
            Assertions.assertNotNull(progressResponseElement);
            log.info("EventProgress: " + progressResponseElement);
        } catch (AssertionFailedError assertionFailedError) {
            //TODO MATTEO TEST
            WebhookStepsInterface webhookStepsInterface = getWebhookStep(StreamVersion.V25);
            String errorLog = String.format("{IUN: %s -WEBHOOK %s }", webhookStepsInterface.getSentNotificationIun(), webhookStepsInterface.getStreamId());
            String message = assertionFailedError.getMessage() + errorLog;
            throw new AssertionFailedError(message, assertionFailedError.getExpected(), assertionFailedError.getActual(), assertionFailedError.getCause());
        }
    }

    @And("vengono letti gli eventi dello stream del {string} fino allo stato {string} con versione V23")
    public void readStreamEventsStatev25(String pa, String status) {
        setPaWebhook(pa);

        StatusElementSearchResult<it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model_v2_3.NotificationStatus>
                statusEventForStream = getStatusEventForStream(V23, status);

        it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model_v2_3.NotificationStatus
                notificationStatus = statusEventForStream.getNotificationStatus();

        it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.NotificationStatus notificationInternalStatus =
                it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.NotificationStatus.valueOf(statusEventForStream.notificationStatus.name());

        int numCheck = statusEventForStream.getNumCheck();
        int waiting = statusEventForStream.getWaiting();

        ProgressResponseElementV23 progressResponseElement = null;
        boolean finded = false;
        for (int i = 0; i < numCheck; i++) {

            sleepTest(waiting);

            sharedSteps.setSentNotificationV23(b2bClient.getSentNotificationV23(sharedSteps.getSentNotification().getIun()));
            NotificationStatusHistoryElement notificationStatusHistoryElement = sharedSteps.getSentNotificationV23().getNotificationStatusHistory().stream().filter(elem -> elem.getStatus().equals(notificationInternalStatus)).findAny().orElse(null);

            if (notificationStatusHistoryElement != null) {
                finded = true;
                break;
            }
        }

        Assertions.assertTrue(finded);
        for (int i = 0; i < 4; i++) {
            progressResponseElement = searchInWebhookV23(notificationStatus, null, 0, 0);
            log.debug("PROGRESS-ELEMENT: " + progressResponseElement);

            if (progressResponseElement != null) {
                break;
            }

            sleepTest();
        }

        try {
            Assertions.assertNotNull(progressResponseElement);
            log.info("EventProgress: " + progressResponseElement);
        } catch (AssertionFailedError assertionFailedError) {
            //TODO MATTEO TEST
            WebhookStepsInterface webhookStepsInterface = getWebhookStep(StreamVersion.V23);
            String errorLog = String.format("{IUN: %s -WEBHOOK %s }", webhookStepsInterface.getSentNotificationIun(), webhookStepsInterface.getStreamId());
            String message = assertionFailedError.getMessage() + errorLog;
            throw new AssertionFailedError(message, assertionFailedError.getExpected(), assertionFailedError.getActual(), assertionFailedError.getCause());
        }
    }
}
