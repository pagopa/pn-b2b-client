package it.pagopa.pn.cucumber.steps.fatturazioneComBo.context;

import com.fasterxml.jackson.databind.JsonNode;
import io.cucumber.spring.ScenarioScope;
import it.pagopa.pn.cucumber.steps.fatturazioneComBo.model.CommunicationDomain;
import it.pagopa.pn.cucumber.steps.fatturazioneComBo.model.InternalTimelineElement;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
@Getter
@Setter
@ScenarioScope
public class FatturazioneComBoScenarioContext {

    /**
     * Dominio della comunicazione oggetto dello scenario.
     */
    private CommunicationDomain domain;

    private String iun;

    /**
     * Id della richiesta di invio, l'unico riferimento che l'ente ha per una comunicazione rifiutata.
     */
    private String notificationRequestId;

    /**
     * Ente mittente come lo identificano la timeline e le tabelle di fatturazione.
     */
    private String paId;

    private int recipientsCount;

    /**
     * Prima chiusura del percorso di ciascun destinatario, per indice del destinatario.
     */
    private final Map<Integer, InternalTimelineElement> firstClosures = new HashMap<>();

    /**
     * Chiusura successiva alla prima, prodotta da un cambio di esito dopo la conclusione.
     */
    private InternalTimelineElement laterClosure;

    /**
     * Stream degli eventi delle comunicazioni bonarie creato per lo scenario, cancellato a fine scenario.
     */
    private UUID streamId;

    /**
     * Comunicazione, con la sua timeline, come la legge l'ente (body JSON della risposta).
     */
    private JsonNode exposedNotification;

    /**
     * Eventi dello stream ricevuti dall'ente per la comunicazione dello scenario (body JSON di ciascun evento).
     */
    private final List<JsonNode> exposedStreamEvents = new ArrayList<>();
}
