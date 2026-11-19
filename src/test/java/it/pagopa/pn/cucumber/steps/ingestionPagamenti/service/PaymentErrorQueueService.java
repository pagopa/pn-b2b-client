package it.pagopa.pn.cucumber.steps.ingestionPagamenti.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.pagopa.pn.client.b2b.pa.service.SqsService;
import it.pagopa.pn.cucumber.steps.ingestionPagamenti.dto.PnErrorEventPayload;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.sqs.model.Message;

import java.util.Objects;
import java.util.Optional;

/**
 * Accesso in sola lettura alla DLQ degli eventi di pagamento malformati. La DLQ non ha consumer, quindi ogni ricerca
 * la scorre per intero; il visibility timeout resta inferiore alle finestre di attesa degli step, così un evento
 * nascosto dalla ricerca di uno scenario precedente torna visibile prima che la verifica scada.
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
                                    @Value("${pn.kafka-bridge.payments-dlq-name:pn-kafka-bridge-payments-DLQ}") String dlqName) {
        this.sqsService = sqsService;
        this.objectMapper = objectMapper;
        this.dlqName = dlqName;
    }

    /**
     * Primo evento della DLQ il cui messaggio originale contiene {@code marker}.
     */
    public Optional<PnErrorEventPayload> findEventContaining(String marker) {
        return sqsService.receiveAllMessages(dlqName, VISIBILITY_TIMEOUT_SECONDS).stream()
                .map(Message::body)
                .map(this::parse)
                .filter(Objects::nonNull)
                .filter(event -> event.originalMessage() != null && event.originalMessage().contains(marker))
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
