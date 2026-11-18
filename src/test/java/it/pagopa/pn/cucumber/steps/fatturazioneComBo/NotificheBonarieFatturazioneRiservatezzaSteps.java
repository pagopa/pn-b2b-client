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
import it.pagopa.common.polling.Poller;
import it.pagopa.common.polling.PollingConfig;
import it.pagopa.common.polling.PollingTimeoutException;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.internalb2bpainformal.model.FullSentInformalNotificationV1;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.internalb2bpainformal.model.NewInformalNotificationRequestStatusResponseV1;
import it.pagopa.pn.client.b2b.pa.provider.SenderInfoProvider;
import it.pagopa.pn.client.b2b.pa.service.impl.PnPaB2bInternalInformalClientImpl;
import it.pagopa.pn.client.b2b.pa.service.impl.PnWebhookB2bExternalClientImpl;
import it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model.CommunicationType;
import it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model.ProgressResponseElementV30;
import it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model.StreamCreationRequestV30;
import it.pagopa.pn.cucumber.steps.SendSharedContext;
import it.pagopa.pn.cucumber.steps.fatturazioneComBo.context.NotificheBonarieFatturazioneScenarioContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.client.RestClientException;

import java.io.UncheckedIOException;
import java.time.Duration;
import java.util.List;
import java.util.UUID;
import java.util.stream.StreamSupport;

import static it.pagopa.pn.cucumber.steps.fatturazioneComBo.model.CommunicationDomain.REQUEST_REFUSED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

/**
 * Riservatezza dei costi: ciò che l'ente vede di una comunicazione bonaria, cioè la comunicazione letta dall'API di
 * consultazione (timeline, o stato della richiesta se rifiutata) e gli aggiornamenti di stato ricevuti sullo stream
 * (pn-stream, API v3.0).
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
public class NotificheBonarieFatturazioneRiservatezzaSteps {

    private static final String NOTIFICATION_COST = "notificationCost";
    private static final Duration POLL_INTERVAL = Duration.ofSeconds(20);
    private static final Duration STREAM_TIMEOUT = Duration.ofMinutes(10);
    private static final ParameterizedTypeReference<String> RAW_JSON = new ParameterizedTypeReference<>() {
    };

    /** Lettura permissiva, per ispezionare le risposte così come arrivano. */
    private final ObjectMapper objectMapper = new ObjectMapper();
    /** Lettura secondo il contratto pubblicato: fallisce su campi o valori non previsti dai modelli generati. */
    private final ObjectMapper contractMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .enable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

    private final NotificheBonarieFatturazioneScenarioContext context;
    private final SendSharedContext sendSharedContext;
    private final PnPaB2bInternalInformalClientImpl informalClient;
    private final PnWebhookB2bExternalClientImpl webhookClient;
    private final SenderInfoProvider senderInfoProvider;

    @Given("il {string} riceve gli aggiornamenti di stato delle comunicazioni bonarie")
    public void informalStreamCreated(String sender) {
        webhookClient.setApiKeys(senderInfoProvider.getApiKeyType(sender));
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
     * Legge ciò che l'ente può consultare della comunicazione e consuma lo stream finché non arriva l'evento di
     * chiusura (conclusione o rifiuto) della comunicazione dello scenario.
     * <p>
     * Una comunicazione accettata si consulta con la sua timeline ({@code GET /delivery/v1/notifications/sent/{iun}}).
     * Una comunicazione rifiutata non è consultabile (404 PN_DELIVERY_NOTIFICATIONNOTFOUND): l'ente ne vede lo stato
     * della richiesta ({@code GET /delivery/v1/requests}), REFUSED con gli errori.
     */
    @When("il {string} consulta la comunicazione bonaria e ne riceve gli aggiornamenti di stato")
    public void senderReadsCommunicationAndStream(String sender) throws JsonProcessingException {
        assertThat(context.getStreamId()).as("Lo scenario deve creare lo stream prima dell'invio").isNotNull();
        String senderId = sendSharedContext.getInformalNotificationContext().getSenderId();
        String exposed = isRefused()
                ? informalClient.getInformalNotificationRequestStatus(senderId, context.getNotificationRequestId(), RAW_JSON).getBody()
                : informalClient.getSentInformalNotificationSender(senderId, context.getIun(), null, RAW_JSON).getBody();
        context.setExposedNotification(objectMapper.readTree(exposed));

        webhookClient.setApiKeys(senderInfoProvider.getApiKeyType(sender));
        String closureCategory = closureCategory();
        String[] lastEventId = {null};
        // Poller interroga nel thread dello scenario: il contesto è un bean con scope di scenario.
        try {
            Poller.pollUntil(() -> readNewEvents(lastEventId, context.getExposedStreamEvents()),
                    events -> events.stream().anyMatch(e -> closureCategory.equals(e.path("informalElement").path("category").asText())),
                    PollingConfig.of(STREAM_TIMEOUT, POLL_INTERVAL));
        } catch (PollingTimeoutException e) {
            throw new AssertionError("Lo stream %s deve ricevere la chiusura %s della comunicazione %s. Eventi ricevuti: %s"
                    .formatted(context.getStreamId(), closureCategory, context.getIun(), context.getExposedStreamEvents()), e);
        }
        log.info("Comunicazione {}: {} eventi ricevuti sullo stream", context.getIun(), context.getExposedStreamEvents().size());
    }

    @Then("gli eventi esposti all'ente non riportano alcun costo")
    public void costNotExposed() {
        JsonNode exposed = context.getExposedNotification();
        assertSoftly(softly -> {
            if (isRefused()) {
                softly.assertThat(exposed.path("notificationRequestStatus").asText())
                        .as("Lo stato della richiesta letto dall'ente deve essere REFUSED, altrimenti la verifica non è significativa")
                        .isEqualTo("REFUSED");
            } else {
                List<String> timelineCategories = StreamSupport.stream(exposed.path("timeline").spliterator(), false)
                        .map(element -> element.path("category").asText())
                        .toList();
                softly.assertThat(timelineCategories)
                        .as("La timeline esposta all'ente deve contenere la chiusura %s, altrimenti la verifica non è significativa", closureCategory())
                        .contains(closureCategory());
            }
            softly.assertThat(exposed.findValues(NOTIFICATION_COST))
                    .as("Quanto l'ente legge della comunicazione %s non deve riportare costi", context.getIun())
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
            Class<?> contract = isRefused() ? NewInformalNotificationRequestStatusResponseV1.class : FullSentInformalNotificationV1.class;
            softly.assertThatCode(() -> contractMapper.treeToValue(context.getExposedNotification(), contract))
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
        } catch (RestClientException e) {
            log.warn("Cancellazione dello stream {} non riuscita: {}", context.getStreamId(), e.getMessage());
        }
    }

    private String closureCategory() {
        return context.getFirstClosures().get(0).category();
    }

    private boolean isRefused() {
        return REQUEST_REFUSED.equals(closureCategory());
    }

    /**
     * Consuma lo stream fino all'ultimo evento disponibile e tiene quelli della comunicazione dello scenario. L'evento
     * di rifiuto non riporta lo IUN, solo l'id della richiesta.
     */
    private List<JsonNode> readNewEvents(String[] lastEventId, List<JsonNode> events) {
        JsonNode page;
        do {
            page = readJson(webhookClient.consumeEventStreamV30(context.getStreamId(), lastEventId[0], RAW_JSON).getBody());
            for (JsonNode event : page) {
                lastEventId[0] = event.path("eventId").asText();
                if (context.getIun().equals(event.path("iun").asText())
                        || context.getNotificationRequestId().equals(event.path("notificationRequestId").asText())) {
                    events.add(event);
                }
            }
        } while (page.size() > 0);
        return events;
    }

    private JsonNode readJson(String json) {
        try {
            return objectMapper.readTree(json);
        } catch (JsonProcessingException e) {
            throw new UncheckedIOException(e);
        }
    }
}
