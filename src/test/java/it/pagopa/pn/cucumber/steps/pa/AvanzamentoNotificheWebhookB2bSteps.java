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
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.NotificationStatusHistoryElementV26;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementV24;
import it.pagopa.pn.client.b2b.pa.polling.design.PnPollingFactory;
import it.pagopa.pn.client.b2b.pa.service.IPnPaB2bClient;
import it.pagopa.pn.client.b2b.pa.service.IPnWebRecipientClient;
import it.pagopa.pn.client.b2b.pa.service.IPnWebhookB2bClient;
import it.pagopa.pn.client.b2b.pa.service.utils.SettableApiKey;
import it.pagopa.pn.client.b2b.pa.utils.TimingForPolling;
import it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model_v2.ProgressResponseElement;
import it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model_v2_3.NotificationStatus;
import it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model_v2_3.ProgressResponseElementV23;
import it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model_v2_3.TimelineElementCategoryV23;
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
import java.util.*;

import static it.pagopa.pn.cucumber.steps.pa.notificationVersions.Costanti.*;

@Slf4j
public class AvanzamentoNotificheWebhookB2bSteps {

    public enum StreamVersion {
        V10(10), V23(23), V24(24), V25(25), V26(26), V27(27);

        /**
         * Scopo di questo campo è quello di poter comparare le versioni con < o >
         * In questo modo si possono aggiungere controlli nel codice per verificare
         * se un dato Stream Version è antecedente o successivo a un'altra versione
         */
        @Getter
        private final int value;

        StreamVersion(int value) {
            this.value = value;
        }
    }

    @Getter
    private final IPnWebhookB2bClient webhookB2bClient;
    @Getter
    private static IPnWebhookB2bClient webhookClientForClean;//TODO: Perché statico?
    @Getter
    private final SharedSteps sharedSteps;
    private final PnPollingFactory pollingFactory;
    @Getter
    private final TimingForPolling timingForPolling;
    private static boolean webhookTestLaunch = true;
    @Getter
    private final Set<String> paStreamOwner = new HashSet<>();
    @Getter
    @Setter
    private HttpStatusCodeException notificationError;
    private Integer requestNumber;
    private final WebhookStepsV10 webhookStepsV10 = new WebhookStepsV10(this);
    private final WebhookStepsV23 webhookStepsV23 = new WebhookStepsV23(this);
    private final WebhookStepsV24 webhookStepsV24 = new WebhookStepsV24(this);
    private final WebhookStepsV25 webhookStepsV25 = new WebhookStepsV25(this);
    private final WebhookStepsV26 webhookStepsV26 = new WebhookStepsV26(this);
    private final WebhookStepsV27 webhookStepsV27 = new WebhookStepsV27(this);
    private static final Map<String, SettableApiKey.ApiKeyType> paForStream =
            Map.of(
                    COMUNE_1, SettableApiKey.ApiKeyType.MVP_1,
                    COMUNE_2, SettableApiKey.ApiKeyType.MVP_2,
                    COMUNE_MULTI, SettableApiKey.ApiKeyType.GA);

    @Autowired
    public AvanzamentoNotificheWebhookB2bSteps(
            IPnWebhookB2bClient webhookB2bClient,
            IPnWebhookB2bClient webhookClientForClean,
            SharedSteps sharedSteps,
            TimingForPolling timingForPolling,
            PnPollingFactory pollingFactory) {

        this.webhookB2bClient = webhookB2bClient;
        this.webhookClientForClean = webhookClientForClean;
        this.sharedSteps = sharedSteps;
        this.timingForPolling = timingForPolling;
        this.pollingFactory = pollingFactory;
    }

    public IPnPaB2bClient getB2bClient() {
        return sharedSteps.getB2bClient();
    }

    public IPnWebRecipientClient getWebRecipientClient() {
        return sharedSteps.getWebRecipientClient();
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

    private StreamVersion getStreamVersion(String version) {
        if (version.trim().equalsIgnoreCase(MOST_RECENT)) {
            return StreamVersion.V27;//TODO: modificare questo valore ogni volta che viene aggiunta una versione più recente
        }
        return StreamVersion.valueOf(version.trim().toUpperCase());
    }

    private WebhookStepsInterface getWebhookStep(String version) {
        return getWebhookStep(getStreamVersion(version));
    }

    private WebhookStepsInterface getWebhookStep(StreamVersion streamVersion) {
        switch (streamVersion) {
            case V10 -> {
                return webhookStepsV10;
            }
            case V23 -> {
                return webhookStepsV23;
            }
            case V24 -> {
                return webhookStepsV24;
            }
            case V25 -> {
                return webhookStepsV25;
            }
            case V26 -> {
                return webhookStepsV26;
            }
            case V27 -> {
                return webhookStepsV27;
            }
            default -> throw new IllegalArgumentException("Version not supported!: " + streamVersion);
        }
    }

    private void logError(AssertionFailedError assertionFailedError, String iun, UUID streamId) {
        String errorLog = String.format("{IUN: %s -WEBHOOK %s }", iun, streamId);
        String message = assertionFailedError.getMessage() + errorLog;
        throw new AssertionFailedError(message, assertionFailedError.getExpected(), assertionFailedError.getActual(), assertionFailedError.getCause());
    }

    private void setPaWebhook(String paName) {
        switch (paName) {
            case COMUNE_1 -> {
                webhookB2bClient.setApiKeys(SettableApiKey.ApiKeyType.MVP_1);
                pollingFactory.setApiKeys(SettableApiKey.ApiKeyType.MVP_1);
                sharedSteps.setPA(paName);
            }
            case COMUNE_2 -> {
                webhookB2bClient.setApiKeys(SettableApiKey.ApiKeyType.MVP_2);
                pollingFactory.setApiKeys(SettableApiKey.ApiKeyType.MVP_2);
                sharedSteps.setPA(paName);
            }
            case COMUNE_MULTI -> {
                webhookB2bClient.setApiKeys(SettableApiKey.ApiKeyType.GA);
                pollingFactory.setApiKeys(SettableApiKey.ApiKeyType.GA);
                sharedSteps.setPA(paName);
            }
            default -> throw new IllegalArgumentException();
        }
    }

    private void deleteAllPaStreamForAllVersion(SettableApiKey.ApiKeyType pa) {
        webhookClientForClean.setApiKeys(pa);
        Arrays.stream(StreamVersion.values()).forEach(version -> {
            WebhookStepsInterface webhookStepsInterface = getWebhookStep(version);
            try {
                webhookStepsInterface.cleanWebHookDelete();
            } catch (HttpStatusCodeException statusCodeException) {
                log.error("HTTP Error: statusCode {} message {}", statusCodeException.getStatusCode(), statusCodeException.getMessage());
            }
        });
    }

    //versioni 23 e 27 only?
    @And("viene verificato che il campo legalFactIds sia valorizzato nel EventStream con la versione {string}")
    public void vieneVerificatoCheIlCampoLegalfactIdsSiaValorizzato(String version) {
        getWebhookStep(version).checkLegalFactId();
    }

    @Given("si predispo(ngono)(ne) {int} nuov(i)(o) stream denominat(i)(o) {string} con eventType {string} con versione {string}")
    public void setUpStreamsWithEventType(int number, String title, String eventType, String version) {
        requestNumber = number;
        StreamVersion streamVersion = getStreamVersion(version);
        createStreamRequest(streamVersion, new LinkedList<>(), number, title, eventType);
    }

    @When("si crea(no) i(l) nuov(o)(i) stream per il {string} con versione {string}")
    public void createStream(String pa, String version) {
        setPaWebhook(pa);
        updateApiKeyForStream();
        StreamVersion streamVersion = getStreamVersion(version);
        createStream(pa, streamVersion, null, false, null, false, null);
    }

    @When("si crea(no) i(l) nuov(o)(i) stream per il {string} con versione {string} e filtro di timeline {string}")
    public void createStreamWithFilteredTimeline(String pa, String version, String filter) {
        setPaWebhook(pa);
        StreamVersion streamVersion = getStreamVersion(version);
        createStream(pa, streamVersion, null, false, List.of(filter), false, null);
    }

    @When("si crea(no) i(l) nuov(o)(i) stream per il {string} con versione {string} e filtro status {string}")
    public void createStreamWithFilteredStatus(String pa, String version, String filter) {
        setPaWebhook(pa);
        updateApiKeyForStream();
        StreamVersion streamVersion = getStreamVersion(version);
        createStream(pa, streamVersion, null, false, List.of(filter), false, null);
    }

    @When("si crea(no) i(l) nuov(o)(i) stream con versione {string} per il {string} con un gruppo disponibile {string}")
    public void createStreamWithGroups(String version, String pa, String position) {
        setPaWebhook(pa);
        updateApiKeyForStream();
        StreamVersion streamVersion = getStreamVersion(version);
        createStream(pa, streamVersion, getGruopForStream(position, pa), false, null, false, null);
    }

    @And("si crea il nuovo stream con versione {string} per il {string} \\(caso errato)")
    public void createStreamForced(String version, String pa) {
        setPaWebhook(pa);
        StreamVersion streamVersion = getStreamVersion(version);
        createStream(pa, streamVersion, null, false, null, true, null);
    }

    @When("si crea(no) i(l) nuov(o)(i) stream con versione {string} per il {string} con un gruppo disponibile {string} \\(caso errato)")
    public void createStreamWithGroupsForced(String version, String pa, String position) {
        setPaWebhook(pa);
        updateApiKeyForStream();
        StreamVersion streamVersion = getStreamVersion(version);
        createStream(pa, streamVersion, getGruopForStream(position, pa), false, null, true, null);
    }

    @When("si crea(no) i(l) nuov(o)(i) stream con versione {string} per il {string} con replaceId con un gruppo disponibile {string} \\(caso errato)")
    public void createStreamWithGroupsForcedWithReplaceId(String version, String pa, String position) {
        setPaWebhook(pa);
        updateApiKeyForStream();
        StreamVersion streamVersion = getStreamVersion(version);
        createStream(pa, streamVersion, getGruopForStream(position, pa), true, null, true, null);
    }

    @When("si crea(no) i(l) nuov(o)(i) stream con versione {string} per il {string} con replaceId con un gruppo disponibile {string}")
    public void createStreamWithGroupsAndReplaceId(String version, String pa, String position) {
        setPaWebhook(pa);
        updateApiKeyForStream();
        StreamVersion streamVersion = getStreamVersion(version);
        createStream(pa, streamVersion, getGruopForStream(position, pa), true, null, false, null);
    }

    @When("si crea il nuovo stream con versione {string} per il {string} con un gruppo disponibile {string} con replaceId dello stream creato con la versione {string} - Cross Versioning")
    public void createStreamWithGroupsAndReplaceIdCrossVersion(String version, String pa, String position, String crossVersion) {
        setPaWebhook(pa);
        updateApiKeyForStream();
        StreamVersion streamVersion = getStreamVersion(version);
        StreamVersion streamCrossVersion = getStreamVersion(crossVersion);
        createStream(pa, streamVersion, getGruopForStream(position, pa), true, null, false, streamCrossVersion);
    }

    @When("viene aggiornata la apiKey utilizzata per gli stream")
    public void updateApiKeyForStream() {
        if (sharedSteps.getResponseNewApiKey() != null) {
            webhookB2bClient.setApiKey(sharedSteps.getResponseNewApiKey().getApiKey());
        }
    }

    @And("si cancella(no) (lo)(gli) stream creat(o)(i) per il {string} con versione {string}")
    public void deleteStream(String pa, String version) {
        getWebhookStep(version).deleteStreams(pa);
    }

    @And("si aggiorna(no) (lo)(gli) stream creat(o)(i) con versione {string} e apiKey aggiornata")
    public void updateStreamUpdateApiKey(String version) {
        updateApiKeyForStream();
        updateStream(version);
    }

    @And("si {string} un gruppo allo stream creat(o)(i) con versione {string} per il comune {string} e apiKey aggiornata")
    public void updateGroupsStreamUpdateApiKey(String action, String version, String pa) {
        updateApiKeyForStream();
        StreamVersion streamVersion = getStreamVersion(version);
        if (sharedSteps.getRequestNewApiKey() != null) {
            WebhookStepsInterface webhookStepsInterface = getWebhookStep(streamVersion);
            webhookStepsInterface.initializeStreamRequest(action, pa);
        }
        updateStream(streamVersion.toString());
    }

    @And("si aggiorna(no) (lo)(gli) stream creat(o)(i) con versione {string} con un gruppo che non appartiene al comune {string}")
    public void updateStreamWithGroupsNoPA(String version, String pa) {
        StreamVersion streamVersion = getStreamVersion(version);
        updateStreamByGroupsPA(streamVersion, pa, false);
    }

    @And("si aggiorna(no) (lo)(gli) stream creat(o)(i) con versione {string} con un gruppo che appartiene al comune {string}")
    public void updateStreamWithGroupsPA(String version, String pa) {
        StreamVersion streamVersion = getStreamVersion(version);
        updateStreamByGroupsPA(streamVersion, pa, true);
    }

    @Given("(allo)(agli) stream versione {string} si setta il campo waitForAccepted introdotto con la versione {int} a {string}")
    public void setWaitForAccepted(String version, int introducingVersion, String waitForAccepted) {
        StreamVersion streamVersion = getStreamVersion(version);
        if (streamVersion.getValue() < introducingVersion) {
            throw new IllegalArgumentException(
                    "Questo step deve comparire solo nei file feature dalla versione " + introducingVersion + "in poi");
        }
        WebhookStepsInterface webhookStepsInterface = getWebhookStep(streamVersion);
        webhookStepsInterface.setValueForWaitForAccepted(Boolean.parseBoolean(waitForAccepted));
    }

    private void updateStreamByGroupsPA(StreamVersion streamVersion, String pa, boolean groupOfPa) {
        String groupToUse = switch (pa) {
            case COMUNE_MULTI -> groupOfPa ? COMUNE_MULTI : COMUNE_1;
            case COMUNE_1 -> groupOfPa ? COMUNE_1 : COMUNE_MULTI;
            default -> COMUNE_MULTI;
        };
        try {
            List<String> groupIdByPa = List.of(sharedSteps.getGroupIdByPa(groupToUse, GroupPosition.FIRST));
            WebhookStepsInterface webhookStepsInterface = getWebhookStep(streamVersion);
            webhookStepsInterface.createStreamRequestWithGroupsPA(groupIdByPa);
        } catch (HttpStatusCodeException e) {
            notificationError = e;
            sharedSteps.setNotificationError(e);
        }
        updateStream(streamVersion.toString());
    }

    @And("si aggiorna(no) (lo)(gli) stream creat(o)(i) con versione {string} invocando la versione {string} - Cross Versioning")
    public void updateStreamCrossVersioning(String rightVersion, String wrongVersion) {
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
            notificationError = e;
            sharedSteps.setNotificationError(e);
        }
    }

    @And("si aggiorna(no) (lo)(gli) stream creat(o)(i) con versione {string}")
    public void updateStream(String version) {
        try {
            getWebhookStep(version).updateStreams();
        } catch (HttpStatusCodeException e) {
            notificationError = e;
            sharedSteps.setNotificationError(e);
        }
    }

    //Usato da tutte le versioni
    @And("si disabilita(no) (lo)(gli) stream creat(o)(i) per il comune {string} con versione {string} e apiKey aggiornata")
    public void disableAllStreamsUpdateApiKey(String pa, String version) {
        updateApiKeyForStream();
        StreamVersion streamVersion = getStreamVersion(version);
        if (streamVersion.value < 23) {
            throw new IllegalArgumentException(
                    "Gli stream si possono disabilitare solo dalla versione 23 in poi");
        }
        disableStreams(streamVersion);
    }

    @And("si disabilita(no) (lo)(gli) stream {string} creat(o)(i) per il comune {string}")
    public void disableAllStreams(String version, String pa) {
        StreamVersion streamVersion = getStreamVersion(version);
        if (streamVersion.value < 23) {
            throw new IllegalArgumentException(
                    "Gli stream si possono disabilitare solo dalla versione 23 in poi");
        }
        disableStreams(streamVersion);
    }

    @And("si disabilita(no) (lo)(gli) stream che non esist(e)(ono) con la versione {string} e apiKey aggiornata")
    public void disableStreamThatNotExist(String version) {
        updateApiKeyForStream();
        UUID notExistingStreamId = UUID.randomUUID();
        StreamVersion streamVersion = getStreamVersion(version);
        if (streamVersion.value < 23) {
            throw new IllegalArgumentException(
                    "Gli stream si possono disabilitare solo dalla versione 23 in poi");
        }
        WebhookStepsInterface webhookStepsInterface = getWebhookStep(streamVersion);
        try {
            webhookStepsInterface.disableStream(notExistingStreamId);
        } catch (HttpStatusCodeException e) {
            notificationError = e;
            sharedSteps.setNotificationError(e);
        }
    }

    //Usato da tutte le versioni
    @And("si cancella(no) (lo)(gli) stream che non esist(e)(ono) con la versione {string} e apiKey aggiornata")
    public void deleteStreamThatNotExist(String version) {
        updateApiKeyForStream();
        UUID notExistingStreamId = UUID.randomUUID();
        try {
            getWebhookStep(version).deleteStream(notExistingStreamId);
        } catch (HttpStatusCodeException e) {
            notificationError = e;
            sharedSteps.setNotificationError(e);
        }
    }

    //Usato da tutte le versioni
    @And("si consuma(no) (lo)(gli) stream che non esist(e)(ono) con la versione {string} e apiKey aggiornata")
    public void consumeStreamThatNotExist(String version) {
        updateApiKeyForStream();
        UUID notExistingStreamId = UUID.randomUUID();
        try {
            getWebhookStep(version).consumeEventStream(notExistingStreamId);
        } catch (HttpStatusCodeException e) {
            notificationError = e;
            sharedSteps.setNotificationError(e);
        }
    }

    //Usato da tutte le versioni, tranne V26
    @And("si legg(e)(ono) (lo)(gli) stream che non esist(e)(ono) e apiKey aggiornata con versione {string}")
    public void readStreamThatNotExist(String version) {
        updateApiKeyForStream();
        StreamVersion streamVersion = getStreamVersion(version);
        WebhookStepsInterface webhookStepsInterface = getWebhookStep(streamVersion);
        //TODO MATTEO: qua lo stream risulta inesistente perchè è stato cancellato in precedenza in un altro step
        // Per una maggiore consistenza sarebbe meglio -> UUID notExistingStreamId = UUID.randomUUID();
        UUID notExistingStreamId = webhookStepsInterface.getStreamId();
        try {
            webhookStepsInterface.retrieveStreamEvent(notExistingStreamId);
        } catch (HttpStatusCodeException e) {
            notificationError = e;
            sharedSteps.setNotificationError(e);
        }
    }

    //Usato da tutte le versioni
    @And("si aggiorna(no) (lo)(gli) stream che non esist(e)(ono) e apiKey aggiornata con versione {string}")
    public void updateStreamNotExist(String version) {
        updateApiKeyForStream();
        try {
            StreamVersion streamVersion = getStreamVersion(version);
            WebhookStepsInterface webhookStepsInterface = getWebhookStep(streamVersion);
            //TODO MATTEO: qua lo stream risulta inesistente perchè è stato cancellato in precedenza in un altro step
            // Per una maggiore consistenza sarebbe meglio -> UUID notExistingStreamId = UUID.randomUUID();
            UUID notExistingStreamId = webhookStepsInterface.getStreamId();
            Object streamRequest = webhookStepsInterface.getStreamRequest();
            webhookStepsInterface.initStreamRequest(streamRequest);
            webhookStepsInterface.updateStreamWithExistingRequest(notExistingStreamId);
        } catch (HttpStatusCodeException e) {
            notificationError = e;
            sharedSteps.setNotificationError(e);
        }
    }

    @And("viene verificata la corretta cancellazione con versione {string}")
    public void verifiedTheCorrectDeletion(String version) {
        getWebhookStep(version).checkCorrectCancellation();
    }

    //Usato da tutte le versioni, tranne V26
    @Then("lo stream è stato creato e viene correttamente recuperato dal sistema tramite stream id con versione {string} e apiKey aggiornata")
    public void streamBeenCreatedAndCorrectlyRetrievedByStreamIdUpdateApiKey(String version) {
        updateApiKeyForStream();
        streamBeenCreatedAndCorrectlyRetrievedByStreamId(version);
    }

    //Usato prevalentemente con V23 e V10, e due casi con la V25. Molti potrebbero essere sostituiti da "più recente"
    @Then("lo stream è stato creato e viene correttamente recuperato dal sistema tramite stream id con versione {string}")
    public void streamBeenCreatedAndCorrectlyRetrievedByStreamId(String version) {
        WebhookStepsInterface webhookStepsInterface = getWebhookStep(version);
        UUID streamId = webhookStepsInterface.getStreamId();
        webhookStepsInterface.getStreamById(streamId);
    }

    //Usato da tutte le versioni, tranne V26
    @When("lo stream viene recuperato dal sistema tramite stream id con versione {string} e apiKey aggiornata")
    public void streamBeenRetrievedByStreamIdUpdateApiKey(String version) {
        updateApiKeyForStream();
        WebhookStepsInterface webhookStepsInterface = getWebhookStep(version);
        UUID streamId = webhookStepsInterface.getStreamId();
        try {
            webhookStepsInterface.getStreamById(streamId);
        } catch (HttpStatusCodeException e) {
            notificationError = e;
            sharedSteps.setNotificationError(e);
        }
    }

    @And("vengono letti gli eventi dello stream versione {string}")
    public void readStreamEvents(String version) {
        readStreamElement(version, version);
    }

    // 1 occorrenza, in VisualizzazioneTimestampTecniciSla
    @When("vengono letti gli eventi dello stream con versione {string} creati dalla versione {string}")
    public void vengonoLettiGliEventiDelloStreamConVersioneCreatiDallaVersione(String versionRead, String versionCreate) {
        readStreamElement(versionCreate, versionRead);
    }

    private void readStreamElement(String versionCreate, String versionRead) {
        updateApiKeyForStream();
        WebhookStepsInterface webhookStepsInterfaceCreate = getWebhookStep(versionCreate);
        UUID streamId = webhookStepsInterfaceCreate.getStreamId();
        WebhookStepsInterface webhookStepsInterfaceRead = getWebhookStep(versionRead);
        try {
            webhookStepsInterfaceRead.consumeEventStream(streamId);
        } catch (HttpStatusCodeException e) {
            notificationError = e;
            sharedSteps.setNotificationError(e);
        }
    }

    //23 only
    @And("viene verificato che gli eventi dello stream non contengono l'elemento di timeline {string} con deliveryDetailCode {string}")
    public void verifyStreamNotContainsSpecificTimelineEvent(String timelineEvent, String deliveryDetailCode) {
        Assertions.assertFalse(searchSpecificTimelineEvent(timelineEvent, deliveryDetailCode));
    }

    //23 only
    @And("viene verificato che gli eventi dello stream contengono l'elemento di timeline {string} con deliveryDetailCode {string}")
    public void verifyStreamContainsSpecificTimelineEvent(String timelineEvent, String deliveryDetailCode) {
        Assertions.assertTrue(searchSpecificTimelineEvent(timelineEvent, deliveryDetailCode));
    }

    private boolean searchSpecificTimelineEvent(String timelineEvent, String deliveryDetailCode) {
        Assertions.assertNotNull(webhookStepsV23.getProgressResponseElementList());
        return webhookStepsV23.getProgressResponseElementList().stream()
                .filter(Objects::nonNull)
                .filter(x -> x.getIun() != null && x.getIun().equals(sharedSteps.getFullSentNotificationV26().getIun()))
                .map(ProgressResponseElementV23::getElement)
                .filter(x -> x.getElementId() != null && x.getElementId().contains(timelineEvent))
                .map(TimelineElementV23::getDetails)
                .filter(Objects::nonNull)
                .anyMatch(x -> x.getDeliveryDetailCode() != null && x.getDeliveryDetailCode().equals(deliveryDetailCode));
    }

    //V10 only
    @And("vengono letti gli eventi dello stream del {string} del validatore fino allo stato {string}")
    public void readStreamEventsStateValidatore(String pa, String status) {
        setPaWebhook(pa);

        WebhookStepsInterface webhookStepsInterface = getWebhookStep(StreamVersion.V10);

        StatusElementSearchResult<it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model_v2.NotificationStatus> searchElementResult =
                webhookStepsInterface.getStatusEventForStream(status, timingForPolling.getTimingForElement(status));

        it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model_v2.NotificationStatus notificationStatus = searchElementResult.getNotificationStatus();

        it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.NotificationStatus notificationInternalStatus =
                it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.NotificationStatus.valueOf(searchElementResult.getNotificationStatus().name());

        Object progressResponseElement = null;
        int wait = 48;
        boolean finded = false;
        for (int i = 0; i < wait; i++) {
            progressResponseElement = webhookStepsInterface.searchStatusElementInWebhook(null, 0, 0, searchElementResult);
            log.debug("PROGRESS-ELEMENT: " + progressResponseElement);

            sharedSteps.setFullSentNotificationV26(getB2bClient().getSentNotification(sharedSteps.getFullSentNotificationV26().getIun()));
            NotificationStatusHistoryElementV26 notificationStatusHistoryElement = sharedSteps.getFullSentNotificationV26()
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
            String errorLog = String.format("{IUN: %s -WEBHOOK %s }", webhookStepsInterface.getSentNotificationIun(), webhookStepsInterface.getStreamId());
            String message = assertionFailedError.getMessage() + errorLog;
            throw new AssertionFailedError(message, assertionFailedError.getExpected(), assertionFailedError.getActual(), assertionFailedError.getCause());
        }
    }

    //V10 only, nondimeno reso parametrico per tutte le versioni
    @And("vengono letti gli eventi dello stream con versione {string} del {string} con la verifica di Allegato non trovato")
    public void readStreamEventsStateRefused(String version, String pa) {
        setPaWebhook(pa);
        WebhookStepsInterface webhookStepsInterface = getWebhookStep(version);
        NotificationStatus notificationStatus = NotificationStatus.REFUSED;
        ProgressResponseElement progressResponseElement = null;
        for (int i = 0; i < 4; i++) {
            progressResponseElement = searchInWebhookFileNotFound(notificationStatus, null, 0);

            log.debug("PROGRESS-ELEMENT: " + progressResponseElement);
            if (progressResponseElement != null) {
                break;
            }
            sleepTest(Long.valueOf(sharedSteps.getWait()));
        }
        try {
            Assertions.assertNotNull(progressResponseElement);
            log.info("EventProgress: " + progressResponseElement);
        } catch (AssertionFailedError assertionFailedError) {
            logError(assertionFailedError, webhookStepsInterface.getSentNotificationIun(), webhookStepsInterface.getStreamId());
        }
    }

    //V10 only
    private <T> ProgressResponseElement searchInWebhookFileNotFound(T timeLineOrStatus, String lastEventId, int deepCount) {
        if (!(timeLineOrStatus instanceof TimelineElementCategoryV23) && !(timeLineOrStatus instanceof NotificationStatus)) {
            throw new IllegalArgumentException();
        }
        WebhookStepsInterface webhookStepsInterface = getWebhookStep(StreamVersion.V10);
        UUID streamId = webhookStepsInterface.getStreamId();

        ProgressResponseElement progressResponseElement = null;
        ResponseEntity<List<ProgressResponseElement>> listResponseEntity = webhookB2bClient.consumeEventStreamHttp(streamId, lastEventId);
        List<ProgressResponseElement> progressResponseElements = listResponseEntity.getBody();
        if (deepCount >= 200) {
            throw new IllegalStateException(
                    "LOP: PROGRESS-ELEMENTS: " + progressResponseElements
                            + " WEBHOOK: " + streamId
                            + " IUN: " + sharedSteps.getFullSentNotificationV26().getIun()
                            + " DEEP: " + deepCount);
        }
        for (ProgressResponseElement elem : progressResponseElements) {
            if ("REFUSED".equalsIgnoreCase(elem.getNewStatus().getValue())
                    && elem.getValidationErrors() != null
                    && elem.getValidationErrors().size() > 0) {
                if (elem.getValidationErrors().get(0).getErrorCode() != null
                        && "FILE_NOTFOUND".equalsIgnoreCase(elem.getValidationErrors().get(0).getErrorCode()))
                    progressResponseElement = elem;
                break;
            }
        }
        return progressResponseElement;
    }

    @Then("vengono letti gli eventi dello stream del {string} fino all'elemento di timeline {string} con la versione {string}")
    public void readStreamTimelineElementBasic(String pa, String timelineEventCategory, String version) {
        readStreamTimeline(version, pa, timelineEventCategory, 0, null);
    }

    @Then("vengono letti gli eventi dello stream del {string} fino all'elemento di timeline {string} con la versione {string} e apiKey aggiornata con position {int}")
    public void readStreamTimelineElementWithUpdateApiKeyAndPosition(String pa, String timelineEventCategory, String version, int position) {
        updateApiKeyForStream();
        readStreamTimeline(version, pa, timelineEventCategory, position, null);
    }

    @Then("vengono letti gli eventi dello stream del {string} con la versione {string} fino all'elemento di timeline {string} con deliveryDetailCode {string}")
    public void readStreamTimelineElementWithDeliveryCode(String pa, String version, String timelineEventCategory, String deliveryDetailCode) {
        readStreamTimeline(version, pa, timelineEventCategory, 0, deliveryDetailCode);
    }

    //V10, 23 and V27 only
    @Then("Si verifica che l'elemento di timeline {string} {string} il timestamp uguale a quello di {string} presente nel webhook con la versione {string}")
    public void compareTimestampWebhook(String timelineElementCategory, String equal, String webhookElementCategory, String version) {
        boolean mustBeEqual = equal.trim().equalsIgnoreCase("ABBIA");
        WebhookStepsInterface webhookStepsInterface = getWebhookStep(version);
        try {
            webhookStepsInterface.compareTimestampWebhook(timelineElementCategory, webhookElementCategory, mustBeEqual);
        } catch (AssertionFailedError assertionFailedError) {
            logError(assertionFailedError, webhookStepsInterface.getSentNotificationIun(), webhookStepsInterface.getStreamId());
        }
    }

    //V10 only
    //TODO MATTEO: non sono convinto dell'implementazione di questo step, a me sembra che invece controlli proprio che sia uguale nel verifyAssertions
    @Then("Si verifica che l'elemento di timeline {string} dello stream con versione {string} di {string} non abbia il timestamp uguale a quella della notifica")
    public void readStreamTimelineElementAndVerify(String timelineEventCategory, String version, String pa) {
        //Il controllo viene effettuato
        readStreamTimeline(pa, timelineEventCategory, version, 0, null);
    }

    private void readStreamTimeline(String version, String pa, String timelineEventCategory, int position, String deliveryDetailCode) {
        StreamVersion streamVersion = getStreamVersion(version);
        WebhookStepsInterface webhookStepsInterface = getWebhookStep(streamVersion);
        setPaWebhook(pa);
        TimelineElementSearchResult<?> timelineForStream = getTimelineEventForStream(streamVersion, timelineEventCategory);
        boolean finish = webhookStepsInterface.checkTimeline(timelineForStream);
        Assertions.assertTrue(finish);
        Object progressResponseElement = null;
        for (int i = 0; i < 4; i++) {
            progressResponseElement = webhookStepsInterface.searchTimelineElementInWebhook(null, 0, position, timelineForStream);
            log.debug("PROGRESS-ELEMENT: " + progressResponseElement);
            if (progressResponseElement != null) {
                break;
            }
            sleepTest(Long.valueOf(sharedSteps.getWait()));
        }
        if (deliveryDetailCode == null) {
            webhookStepsInterface.verifyAssertionsTimeline(timelineForStream, progressResponseElement);
            webhookStepsInterface.setProgressResponseElement(progressResponseElement);
        } else {
            try {
                Assertions.assertNotNull(progressResponseElement);
                Assertions.assertFalse(sharedSteps.getFullSentNotificationV26()
                        .getTimeline()
                        .stream()
                        .filter(data -> data.getCategory() != null && data.getDetails() != null && data.getDetails().getDeliveryDetailCode() != null)
                        .filter(elem -> elem.getCategory().getValue().equals(timelineEventCategory)
                                && elem.getDetails().getDeliveryDetailCode().equals(deliveryDetailCode))
                        .findAny()
                        .isEmpty());
                log.info("EventProgress: " + progressResponseElement);
                webhookStepsInterface.setProgressResponseElement(progressResponseElement);
            } catch (AssertionFailedError assertionFailedError) {
                logError(assertionFailedError, webhookStepsInterface.getSentNotificationIun(), webhookStepsInterface.getStreamId());
            }
        }
    }

    @And("vengono letti gli eventi dello stream del {string} fino allo stato {string} con la versione {string}")
    public void readStreamStatusBasic(String pa, String status, String version) {
        readStreamStatus(pa, status, version, 0, false);
    }

    @And("vengono letti gli eventi dello stream del {string} fino allo stato {string} con la versione {string} e apiKey aggiornata con position {int}")
    public void readStreamStatusWithPositionAndUpdatedApiKey(String pa, String status, String version, Integer position) {
        readStreamStatus(pa, status, version, position, true);
    }

    private void readStreamStatus(String pa, String status, String version, int position, boolean isApiKeyUpdated) {
        if (isApiKeyUpdated) {
            updateApiKeyForStream();
        }
        WebhookStepsInterface webhookStepsInterface = getWebhookStep(StreamVersion.valueOf(version));
        StreamVersion streamVersion = StreamVersion.valueOf(version);
        setPaWebhook(pa);
        StatusElementSearchResult<?> statusEventForStream = getStatusEventForStream(streamVersion, status);
        boolean found = webhookStepsInterface.checkStatus(statusEventForStream);
        Assertions.assertTrue(found);
        Object progressResponseElement = null;
        for (int i = 0; i < 4; i++) {
            progressResponseElement = webhookStepsInterface.searchStatusElementInWebhook(null, 0, position, statusEventForStream);
            log.debug("PROGRESS-ELEMENT: " + progressResponseElement);
            if (progressResponseElement != null) {
                break;
            }
            sleepTest(Long.valueOf(sharedSteps.getWait()));
        }
        try {
            Assertions.assertNotNull(progressResponseElement);
            log.info("EventProgress: " + progressResponseElement);
        } catch (AssertionFailedError assertionFailedError) {
            logError(assertionFailedError, webhookStepsInterface.getSentNotificationIun(), webhookStepsInterface.getStreamId());
        }
    }

    @Then("si verifica la {string} di SERCQ con la versione {string}")
    public void verificaPresenzaSercQ(String filter, String version) {
        boolean isPresent = filter.equalsIgnoreCase("PRESENZA");
        getWebhookStep(version).verificaPresenzaSercQ(isPresent);
    }

    @Then("si verifica che non siano presenti eventi nello stream con versione {string} del {string}")
    public void readStreamTimelineElementNotPresent(String version, String pa) {
        setPaWebhook(pa);
        updateApiKeyForStream();
        getWebhookStep(version).verifyNoEventsInStream();
    }

    @And("{string} legge la notifica")
    public void userReadNotification(String recipient) {
        sharedSteps.selectUser(recipient);
        Assertions.assertDoesNotThrow(() -> getWebRecipientClient().getReceivedNotification(sharedSteps.getFullSentNotificationV26().getIun(), null));
        sleepTest(Long.valueOf(sharedSteps.getWorkFlowWait()));
    }

    @And("{string} legge la notifica dopo i 10 giorni")
    public void userReadNotificationAfterTot(String recipient) {
        sharedSteps.selectUser(recipient);
        sleepTest(sharedSteps.getSchedulingDaysSuccessAnalogRefinement().toMillis());
        Assertions.assertDoesNotThrow(() -> {
            getWebRecipientClient().getReceivedNotification(sharedSteps.getFullSentNotificationV26().getIun(), null);
        });
        sleepTest(Long.valueOf(sharedSteps.getWorkFlowWait()));
    }

    //usato solo da v23 per gli stress test
    @And("vengono letti gli eventi dello stream con id {string} e versione {string}")
    public void vengonoLettiGliEventiDelloStreamDelV(String streamID, String version) {
        setPaWebhook(COMUNE_MULTI);
        UUID streamId = UUID.fromString(streamID);
        getWebhookStep(version).consumeEventStream(streamId);
    }

    //usato solo da v23 per gli stress test
    @And("vengono letti tutti gli eventi degli stream con versione {string} creati per il test di carico per {int} minuti")
    public void readAllStreamEvents(String version, int minuti) {
        int elapsedMinute = 0;
        setPaWebhook(COMUNE_MULTI);
        while (elapsedMinute < minuti) {
            try {
                getWebhookStep(version).getStreamEventListForStressTest();
                sleepTest(60 * 1000);
                elapsedMinute += 1;
                setPaWebhook(COMUNE_MULTI);
            } catch (Exception e) {
                System.out.println("Exception");
            }
        }
    }

    //usato solo da V23 per gli stress test
    @And("vengono letti tutti gli eventi degli stream con versione {string} hardcodati per il test di carico per {int} minuti")
    public void readAllStreamEventsHardCoded(String version, int minuti) {
        String[] streamList = {"00001d7a-42e8-41df-a995-40da72a087d7"};
        int elapsedMinute = 0;
        setPaWebhook(COMUNE_MULTI);
        while (elapsedMinute < minuti) {
            try {
                for (String streamID : streamList) {
                    getWebhookStep(version).consumeEventStream(UUID.fromString(streamID));
                    sleepTest(50);
                }
                sleepTest(60 * 1000);
                elapsedMinute += 1;
                setPaWebhook(COMUNE_MULTI);
            } catch (Exception e) {
                System.out.println("Exception in read");
            }
        }
    }

    // usato solo in due casi con la versione 10, nondimeno parametrizzato (anche lo status ora è parametrizzato)
    @Then("si verifica nello stream del {string} con versione {string} che la notifica abbia lo stato {string}")
    public void checkNotificationState(String pa, String version, String status) {
        sleepTest((sharedSteps.getWait() * 2));
        setPaWebhook(pa);
        StreamVersion streamVersion = getStreamVersion(version);
        WebhookStepsInterface webhookStepsInterface = getWebhookStep(streamVersion);
        StatusElementSearchResult<?> statusEventForStream = getStatusEventForStream(streamVersion, status);
        Object progressResponseElement = webhookStepsInterface.searchStatusElementInWebhook(null, 0, 0, statusEventForStream);
        Assertions.assertNotNull(progressResponseElement);
    }

    //V10 only
    @Then("l'ultima creazione ha prodotto un errore con status code {string}")
    public void lastCreationProducedAnErrorWithStatusCode(String statusCode) {
        List<it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model_v2.StreamListElement> streamListElements = webhookB2bClient.listEventStreams();
        System.out.println("streamListElements: " + streamListElements.size());
        System.out.println("eventStreamList: " + webhookStepsV10.getEventStreamList().size());
        System.out.println("requestNumber: " + requestNumber);
        Assertions.assertTrue(
                (notificationError != null)
                        && (notificationError.getStatusCode().toString().substring(0, 3).equals(statusCode))
                        && (webhookStepsV10.getEventStreamList().size() == (requestNumber - 1)));
    }

    @Given("vengono cancellati tutti gli stream presenti del {string} con versione {string}")
    public void deleteAll(String pa, String version) {
        setPaWebhook(pa);
        getWebhookStep(version).deleteStreamsBeforeTest(pa);
    }

    //TODO cancellare? Non esiste file feature che richiami questo step
//    @And("vengono prodotte le evidenze: metadati, requestID, IUN e stati")
//    public void evidenceProducedIunRequestIdAndState() {
//        log.info("METADATI: " + '\n' + sharedSteps.getNewNotificationResponse());
//        log.info("REQUEST-ID: " + '\n' + sharedSteps.getNewNotificationResponse().getNotificationRequestId());
//        log.info("IUN: " + '\n' + sharedSteps.getSentNotification().getIun());
//        for (ProgressResponseElement element : progressResponseElements) {
//            log.info("EVENT: " + '\n' + element.getTimelineEventCategory() + " " + element.getTimestamp());
//        }
//    }

    @Then("viene verificato che il ProgressResponseElement del webhook abbia un EventId incrementale e senza duplicati {string}")
    public void verifyIncrementalAndUniqueProgressResponseElementId(String version) {
        getWebhookStep(version).verifyIncrementalEventId();
    }

    @And("vengono letti gli eventi dello stream che contenga {int} eventi con la versione {string}")
    public void readStreamNumberEventsV23(Integer numEventi, String version) {
        getWebhookStep(version).consumeEventStreamAndCheckNumEvents(numEventi);
    }

    @And("verifica corrispondenza tra i detail del webhook e quelli della timeline con la versione {string}")
    public void verificaCorrispondenzaTraIDetailDelWebhookEQuelliDellaTimeline(String version) throws JsonProcessingException {
        WebhookStepsInterface webhookStepsInterface = getWebhookStep(version);

        List<Object> objectsToCompare = webhookStepsInterface.verificaCorrispondenzaElementiTimelineWebhookAndB2B();
        Object b2bElement = objectsToCompare.get(0);
        Object webhookElement = objectsToCompare.get(1);

        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String jsonB2B = ow.writeValueAsString(serializeObject(b2bElement));
        System.out.println(jsonB2B);
        String jsonWebhook = ow.writeValueAsString(serializeObject(webhookElement));
        System.out.println(jsonWebhook);

        ObjectMapper mapper = new ObjectMapper();
        Assertions.assertEquals(mapper.readTree(jsonB2B), mapper.readTree(jsonWebhook));
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

    @Then("verifica deanonimizzazione degli eventi di timeline versione {string} con delega {string} analogico")
    public void verificaDeanonimizzazioneDegliEventiDiTimelineAnalogico(String version, String delega) {
        boolean withDelega = delega.trim().equalsIgnoreCase("SI");
        getWebhookStep(version).verificaDeanonimizzazioneEventiTimelineAnalogica(withDelega);
    }

    @Then("verifica deanonimizzazione degli eventi di timeline versione {string} con delega {string} digitale")
    public void verificaDeanonimizzazioneDegliEventiDiTimelineDigitale(String version, String delega) {
        boolean withDelega = delega.trim().equalsIgnoreCase("SI");
        getWebhookStep(version).verificaDeanonimizzazioneEventiTimelineDigitale(withDelega);
    }

    @When("vengono letti gli eventi di timeline dello stream con versione {string} nonostante sia stato creato con la {string} - Cross Versioning")
    public void vengonoLettiGliEventiDiTimelineDelloStreamDel(String versionRead, String versionCreate) {
        updateApiKeyForStream();
        WebhookStepsInterface webhookStepsInterfaceCreate = getWebhookStep(versionCreate);
        UUID streamId = webhookStepsInterfaceCreate.getStreamId();
        WebhookStepsInterface webhookStepsInterfaceRead = getWebhookStep(versionRead);
        try {
            webhookStepsInterfaceRead.consumeEventStream(streamId);
        } catch (HttpStatusCodeException e) {
            notificationError = e;
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

    private <T> TimelineElementSearchResult<T> getTimelineEventForStream(StreamVersion streamVersion, String timelineEventCategory) {
        timelineEventCategory = timelineEventCategory.trim().toUpperCase();
        TimingForPolling.TimingResult timingForElement = timingForPolling.getTimingForElement(timelineEventCategory);
        WebhookStepsInterface webhookStepsInterface = getWebhookStep(streamVersion);
        try {
            return webhookStepsInterface.getTimelineEventForStream(timelineEventCategory, timingForElement);
        } catch (ClassCastException classCastException) {
            log.error("Wrong type t for streamVersion {}, error in cast {}", streamVersion, classCastException.getMessage());
        }
        throw new IllegalArgumentException();
    }

    private <T> StatusElementSearchResult<T> getStatusEventForStream(StreamVersion streamVersion, String notificationStatusName) {
        notificationStatusName = notificationStatusName.trim().toUpperCase();
        TimingForPolling.TimingResult timingForElement = timingForPolling.getTimingForElement(notificationStatusName);
        WebhookStepsInterface webhookStepsInterface = getWebhookStep(streamVersion);
        try {
            return webhookStepsInterface.getStatusEventForStream(notificationStatusName, timingForElement);
        } catch (ClassCastException classCastException) {
            log.error("Wrong type t for streamVersion {}, error in cast {}", streamVersion, classCastException.getMessage());
        }
        throw new IllegalArgumentException();
    }

    public void sleepTest(long wait) {
        try {
            Thread.sleep(wait);
        } catch (InterruptedException exc) {
            throw new RuntimeException(exc);
        }
    }

    private void createStreamRequest(StreamVersion streamVersion, List<String> filterValues, int number, String title, String eventType) {
        WebhookStepsInterface webhookStepsInterface = getWebhookStep(streamVersion);
        webhookStepsInterface.createStreamRequest(filterValues, number, title, eventType);
    }

    //TODO, il parametro forced non viene mai usato in nessuna delle implementazioni.
    // Veniva usato in questo vecchio codice che è stato cancellato (prima ancora del refactor):
    // if (!forced) acquireStreamCreationSlotInternal(pa,streamCreationRequestList.size());
    // A che doveva servire? Rimuovere una volta appurata la sua inutilità
    private void createStream(String pa, StreamVersion streamVersion, List<String> listGroups, boolean replaceId, List<String> filteredValues, boolean forced, StreamVersion crossVersion) {
        try {
            UUID streamIdToReplace = null;
            WebhookStepsInterface webhookStepsInterface = getWebhookStep(streamVersion);
            if (replaceId) {
                streamIdToReplace = crossVersion == null ? webhookStepsInterface.getStreamId() : getWebhookStep(crossVersion).getStreamId();
            }
            try {
                webhookStepsInterface.createEventStream(pa, listGroups, streamIdToReplace, filteredValues, forced);
                if (replaceId) {
                    WebhookStepsInterface webhookStepsInterfaceForFinalChecks = crossVersion == null ? webhookStepsInterface : getWebhookStep(crossVersion);
                    webhookStepsInterfaceForFinalChecks.checkCorrectDisabling(streamIdToReplace);
                }
            } catch (HttpStatusCodeException e) {
                notificationError = e;
                sharedSteps.setNotificationError(e);
            }
        } catch (HttpStatusCodeException e) {
            log.error("Error {} in create Stream version {}, group {}, replaceID {}, filteredValues {}",
                    e.getStatusCode(), streamVersion, listGroups, replaceId, filteredValues);
            notificationError = e;
            sharedSteps.setNotificationError(e);
            if (!forced) throw e;
        }
        if (!webhookTestLaunch) webhookTestLaunch = true;
    }

    private void disableStreams(StreamVersion version) {
        WebhookStepsInterface webhookStepsInterface = getWebhookStep(version);
        try {
            webhookStepsInterface.disableStreams();
        } catch (HttpStatusCodeException e) {
            notificationError = e;
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
                if (COMUNE_1.equalsIgnoreCase(pa)) {
                    Assertions.assertNotNull(sharedSteps.getGroupIdByPa(COMUNE_MULTI, GroupPosition.FIRST));
                    groupList = List.of(sharedSteps.getGroupIdByPa(COMUNE_MULTI, GroupPosition.FIRST));
                } else if (COMUNE_MULTI.equalsIgnoreCase(pa)) {
                    Assertions.assertNotNull(sharedSteps.getGroupIdByPa(COMUNE_1, GroupPosition.FIRST));
                    groupList = List.of(sharedSteps.getGroupIdByPa(COMUNE_1, GroupPosition.FIRST));
                } else {
                    Assertions.assertNotNull(sharedSteps.getGroupIdByPa(COMUNE_1, GroupPosition.FIRST));
                    groupList = List.of(sharedSteps.getGroupIdByPa(COMUNE_1, GroupPosition.FIRST));
                }
            }
            default -> throw new IllegalArgumentException();
        }
        return groupList;
    }

    //Usato solo con la V25 (resa al mittente deceduto)
    @Then("si controlla che tra gli elementi dello stream con versione {string} ritornati non ci sia l'elemento {string}")
    public void streamDoesNotContainElement(String version, String elementType) {
        getWebhookStep(version).verifySpecificEventNotInStream(elementType);
    }

    //V23 and V24 only (VisualizzazioneTimeStampTecniciSLA.feature)
    @Then("la chiamata restituisce correttamente lo stream di elementi timeline versione {string}")
    public void checkForNoError(String version) {
        Assertions.assertNull(sharedSteps.getNotificationError());
        checkTimelineElementVersionWebHook(version);
    }

    //V23 and V24 only (VisualizzazioneTimeStampTecniciSLA.feature)
    @Then("gli elementi di timeline restituiti dal Webhook contengono i campi attesi in accordo alla versione {string}")
    public void checkTimelineElementVersionWebHook(String version) {
        if (version.equalsIgnoreCase("V24")) {
            Assertions.assertNotNull(webhookStepsV24.getProgressResponseElementList());
            webhookStepsV24.getProgressResponseElementList().forEach(pre -> checkTimelineElement(pre.getElement()));
        } else if (version.equalsIgnoreCase("V23")) {
            Assertions.assertNotNull(webhookStepsV23.getProgressResponseElementList());
            webhookStepsV23.getProgressResponseElementList().forEach(pre -> checkTimelineElement(pre.getElement()));
        }
    }

    //V23 and V24 only (VisualizzazioneTimeStampTecniciSLA.feature)
    @Then("gli elementi di timeline restituiti da B2B contengono i campi attesi in accordo alla versione {string}")
    public void checkTimelineElementVersionB2B(String version) {
        if (version.equalsIgnoreCase("V24")) {
            Assertions.assertNotNull(sharedSteps.getFullSentNotificationV24());
            sharedSteps.getFullSentNotificationV24().getTimeline().forEach(this::checkTimelineElement);
        } else if (version.equalsIgnoreCase("V23")) {
            Assertions.assertNotNull(sharedSteps.getFullSentNotificationV23());
            sharedSteps.getFullSentNotificationV23().getTimeline().forEach(this::checkTimelineElement);
        }
    }

    private void checkTimelineElement(Object timeline) {
        if (timeline instanceof TimelineElementV24 TimelineElementV24) {
            Assertions.assertNotNull(TimelineElementV24.getIngestionTimestamp());
            Assertions.assertNotNull(TimelineElementV24.getNotificationSentAt());
            Assertions.assertNotNull(TimelineElementV24.getEventTimestamp());
            log.info("Field presence checked for " + TimelineElementV24.getCategory().getValue());
        } else if (timeline instanceof TimelineElementV23 webhookTimelineElementV23) {
            Map timelineElementMap = JsonMapper.builder().addModule(new JavaTimeModule()).build().convertValue(webhookTimelineElementV23, Map.class);
            Assertions.assertFalse(timelineElementMap.containsKey("ingestionTimeStamp"));
            Assertions.assertFalse(timelineElementMap.containsKey("notificationSentAt"));
            Assertions.assertFalse(timelineElementMap.containsKey("eventTimestamp"));
            log.info("Absence of fields checked for " + webhookTimelineElementV23.getCategory().getValue());
        } else if (timeline instanceof it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementV23 b2bTimelineElementV23) {
            Map timelineElementMap = JsonMapper.builder().addModule(new JavaTimeModule()).build().convertValue(b2bTimelineElementV23, Map.class);
            Assertions.assertFalse(timelineElementMap.containsKey("ingestionTimeStamp"));
            Assertions.assertFalse(timelineElementMap.containsKey("notificationSentAt"));
            Assertions.assertFalse(timelineElementMap.containsKey("eventTimestamp"));
            log.info("Absence of fields checked for " + b2bTimelineElementV23.getCategory().getValue());
        }
    }

    //V23 and V24 only (VisualizzazioneTimeStampTecniciSLA.feature)
    @Then("la chiamata restituisce un errore {int} riportante la dicitura {string}")
    public void checkForError(Integer errorCode, String errorMessage) {
        Assertions.assertNotNull(notificationError);
        Assertions.assertEquals(errorCode, notificationError.getRawStatusCode());
        Assertions.assertNotNull(notificationError.getMessage());
        Assertions.assertTrue(notificationError.getMessage().contains(errorMessage));
    }

    @When("si invoca l'api Webhook versione {string} per ottenere gli elementi di timeline di tale notifica")
    public void getTimelineElementVersionWebhook(String version) {
        updateApiKeyForStream();
        WebhookStepsInterface webhookStepsInterface = getWebhookStep(version);
        UUID streamId = webhookStepsInterface.getStreamId();
        webhookStepsInterface.consumeEventStream(streamId);
    }

    @When("si invoca l'api B2B versione {string} per ottenere gli elementi di timeline di tale notifica")
    public void getTimelineElementVersionB2B(String version) {
        String iun = sharedSteps.getFullSentNotificationV26().getIun();
        getWebhookStep(version).getTimelineElementVersionB2B(iun);
    }
}