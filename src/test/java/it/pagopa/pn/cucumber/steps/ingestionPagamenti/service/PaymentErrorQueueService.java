package it.pagopa.pn.cucumber.steps.ingestionPagamenti.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.pagopa.pn.client.b2b.pa.service.SqsService;
import it.pagopa.pn.cucumber.steps.ingestionPagamenti.dto.PnErrorEventPayload;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Optional;

/**
 * Accesso in sola lettura alla DLQ in cui pn-kafka-bridge scarta gli eventi di pagamento malformati.
 * La DLQ non ha consumer (SRS §5.2) e conserva i messaggi per 14 giorni, quindi ogni ricerca la scorre per intero.
 * I messaggi letti restano nascosti per VISIBILITY_TIMEOUT_SECONDS: una ricerca successiva vede solo quelli arrivati
 * nel frattempo. Il timeout resta inferiore alle finestre di attesa degli step: se l'evento atteso viene nascosto
 * dalla ricerca di uno scenario precedente, torna visibile prima che la verifica scada.
 */
@Slf4j
@Component
public class PaymentErrorQueueService {
    private static final int VISIBILITY_TIMEOUT_SECONDS = 30;

    private final SqsService sqsService;
    private final ObjectMapper objectMapper;
    private final String dlqName;

    public PaymentErrorQueueService(SqsService sqsService,
                                    ObjectMapper objectMapper,
                                    @Value("${pn.kafka-bridge.payments-dlq-name}") String dlqName) {
        this.sqsService = sqsService;
        this.objectMapper = objectMapper;
        this.dlqName = dlqName;
    }

    public Optional<PnErrorEventPayload> findEventContaining(String marker) {
        return sqsService.readAllMessageBodies(dlqName, VISIBILITY_TIMEOUT_SECONDS).stream()
                .map(this::parse)
                .filter(Objects::nonNull)
                .filter(event -> event.getOriginalMessage() != null && event.getOriginalMessage().contains(marker))
                .findFirst();
    }

    private PnErrorEventPayload parse(String body) {
        try {
            return objectMapper.readValue(body, PnErrorEventPayload.class);
        } catch (JsonProcessingException e) {
            log.warn("Messaggio della DLQ {} non leggibile come PnErrorEvent: {}", dlqName, body);
            return null;
        }
    }
}
