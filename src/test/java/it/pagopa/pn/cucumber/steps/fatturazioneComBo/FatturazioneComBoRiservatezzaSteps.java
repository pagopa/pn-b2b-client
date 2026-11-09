package it.pagopa.pn.cucumber.steps.fatturazioneComBo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.cucumber.java.After;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.internalb2bpainformal.model.FullSentInformalNotificationV1;
import it.pagopa.pn.client.b2b.pa.service.impl.PnPaB2bInternalInformalClientImpl;
import it.pagopa.pn.client.b2b.pa.service.impl.PnWebhookB2bExternalClientImpl;
import it.pagopa.pn.client.b2b.pa.service.utils.SettableApiKey;
import it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model.CommunicationType;
import it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model.ProgressResponseElementV30;
import it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model.StreamCreationRequestV30;
import it.pagopa.pn.cucumber.steps.SendSharedContext;
import it.pagopa.pn.cucumber.steps.fatturazioneComBo.context.FatturazioneComBoScenarioContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import java.time.Duration;
import java.util.List;
import java.util.UUID;
import java.util.stream.StreamSupport;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.awaitility.Awaitility.await;

/**
 * Riservatezza dei costi: ciò che l'ente vede di una comunicazione bonaria, cioè la timeline letta dall'API di
 * consultazione e gli aggiornamenti di stato ricevuti sullo stream (pn-stream, API v3.0).
 * <p>
 * Le risposte vengono lette come JSON grezzo: un campo non previsto dal contratto verrebbe scartato in silenzio dai
 * modelli generati e la verifica non lo vedrebbe.
 * <p>
 * Lo stream va creato prima dell'invio, perché riceve solo gli eventi successivi alla sua creazione. È dedicato alle
 * comunicazioni bonarie ({@code communicationType = INFORMAL}) e viene cancellato a fine scenario.
 */
@Slf4j
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@RequiredArgsConstructor
public class FatturazioneComBoRiservatezzaSteps {

    private static final String NOTIFICATION_COST = "notificationCost";
    private static final Duration POLL_INTERVAL = Duration.ofSeconds(20);
    private static final Duration STREAM_TIMEOUT = Duration.ofMinutes(10);

    /** Lettura permissiva, per ispezionare le risposte così come arrivano. */
    private final ObjectMapper objectMapper = new ObjectMapper();
    /** Lettura secondo il contratto pubblicato: fallisce su campi o valori non previsti dai modelli generati. */
    private final ObjectMapper contractMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .enable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

    private final FatturazioneComBoScenarioContext context;
    private final SendSharedContext sendSharedContext;
    private final PnPaB2bInternalInformalClientImpl informalClient;
    private final PnWebhookB2bExternalClientImpl webhookClient;

    @Given("il {string} riceve gli aggiornamenti di stato delle comunicazioni bonarie")
    public void informalStreamCreated(String sender) {
        webhookClient.setApiKeys(apiKeyOf(sender));
        StreamCreationRequestV30 request = new StreamCreationRequestV30()
                .title("fatturazione-combo-" + UUID.randomUUID())
                .eventType(StreamCreationRequestV30.EventTypeEnum.TIMELINE)
                // Le comunicazioni rifiutate non passano mai da ACCEPTED: lo stream deve ricevere anche i loro eventi.
                .waitForAccepted(false)
                .communicationType(CommunicationType.INFORMAL);
        UUID streamId = webhookClient.createEventStreamV30(request).getStreamId();
        context.setStreamId(streamId);
        log.info("Creato lo stream {} delle comunicazioni bonarie per {}", streamId, sender);
    }

    /**
     * Legge la comunicazione dall'API di consultazione dell'ente e consuma lo stream finché non arriva l'evento di
     * chiusura (conclusione o rifiuto) della comunicazione dello scenario.
     */
    @When("il {string} consulta la timeline della comunicazione bonaria e ne riceve gli aggiornamenti di stato")
    public void senderReadsTimelineAndStream(String sender) throws JsonProcessingException {
        assertThat(context.getStreamId()).as("Lo scenario deve creare lo stream prima dell'invio").isNotNull();
        String senderId = sendSharedContext.getInformalNotificationContext().getSenderId();
        context.setExposedNotification(objectMapper.readTree(informalClient.getSentInformalNotificationSenderAsJson(senderId, context.getIun())));

        webhookClient.setApiKeys(apiKeyOf(sender));
        String closureCategory = closureCategory();
        String[] lastEventId = {null};
        await().pollInterval(POLL_INTERVAL).atMost(STREAM_TIMEOUT).untilAsserted(() -> {
            readNewEvents(lastEventId, context.getExposedStreamEvents());
            assertThat(context.getExposedStreamEvents().stream().map(e -> e.path("informalElement").path("category").asText()))
                    .as("Lo stream %s deve ricevere la chiusura %s della comunicazione %s", context.getStreamId(), closureCategory, context.getIun())
                    .contains(closureCategory);
        });
        log.info("Comunicazione {}: {} eventi ricevuti sullo stream", context.getIun(), context.getExposedStreamEvents().size());
    }

    @Then("gli eventi esposti all'ente non riportano alcun costo")
    public void costNotExposed() {
        JsonNode timeline = context.getExposedNotification().path("timeline");
        List<String> timelineCategories = StreamSupport.stream(timeline.spliterator(), false)
                .map(element -> element.path("category").asText())
                .toList();
        assertSoftly(softly -> {
            softly.assertThat(timelineCategories)
                    .as("La timeline esposta all'ente deve contenere la chiusura %s, altrimenti la verifica non è significativa", closureCategory())
                    .contains(closureCategory());
            softly.assertThat(timeline.findValues(NOTIFICATION_COST))
                    .as("La timeline esposta all'ente per la comunicazione %s non deve riportare costi", context.getIun())
                    .isEmpty();
            context.getExposedStreamEvents().forEach(event -> softly.assertThat(event.findValues(NOTIFICATION_COST))
                    .as("L'evento %s dello stream non deve riportare costi", event.path("eventId").asText())
                    .isEmpty());
        });
    }

    /**
     * Il formato non cambia se la comunicazione e ogni evento dello stream si leggono con i modelli generati dal
     * contratto pubblicato, senza campi né valori in più. Se fallisce, verificare prima che i contratti
     * del client siano allineati alla versione rilasciata nell'ambiente.
     */
    @Then("il formato degli eventi esposti all'ente non cambia")
    public void exposedFormatUnchanged() {
        assertSoftly(softly -> {
            softly.assertThatCode(() -> contractMapper.treeToValue(context.getExposedNotification(), FullSentInformalNotificationV1.class))
                    .as("La comunicazione %s letta dall'ente deve rispettare il contratto pubblicato", context.getIun())
                    .doesNotThrowAnyException();
            context.getExposedStreamEvents().forEach(event -> softly
                    .assertThatCode(() -> contractMapper.treeToValue(event, ProgressResponseElementV30.class))
                    .as("L'evento %s dello stream deve rispettare il contratto pubblicato", event.path("eventId").asText())
                    .doesNotThrowAnyException());
        });
    }

    @After("@fatturazioneComBo")
    public void deleteStream() {
        if (context.getStreamId() == null) {
            return;
        }
        try {
            webhookClient.deleteEventStreamV30(context.getStreamId());
        } catch (Exception e) {
            log.warn("Cancellazione dello stream {} non riuscita: {}", context.getStreamId(), e.getMessage());
        }
    }

    private String closureCategory() {
        return context.getFirstClosures().get(0).category();
    }

    /** Consuma lo stream fino all'ultimo evento disponibile e tiene quelli della comunicazione dello scenario. */
    private void readNewEvents(String[] lastEventId, List<JsonNode> events) throws JsonProcessingException {
        JsonNode page;
        do {
            page = objectMapper.readTree(webhookClient.consumeEventStreamV30AsJson(context.getStreamId(), lastEventId[0]));
            for (JsonNode event : page) {
                lastEventId[0] = event.path("eventId").asText();
                if (context.getIun().equals(event.path("iun").asText())) {
                    events.add(event);
                }
            }
        } while (page.size() > 0);
    }

    private static SettableApiKey.ApiKeyType apiKeyOf(String sender) {
        return switch (sender) {
            case "Comune_1" -> SettableApiKey.ApiKeyType.MVP_1;
            case "Comune_2" -> SettableApiKey.ApiKeyType.MVP_2;
            case "Comune_Multi" -> SettableApiKey.ApiKeyType.GA;
            case "Comune_Son" -> SettableApiKey.ApiKeyType.SON;
            default -> throw new IllegalArgumentException("Ente non gestito per gli stream: " + sender);
        };
    }
}
