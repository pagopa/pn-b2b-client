package it.pagopa.pn.client.b2b.pa.service;

import it.pagopa.pn.client.b2b.pa.model.kafka.PaymentEventRequest;
import org.springframework.web.client.RestClientException;

/**
 * Client di pn-kafka-mock, che pubblica sul cluster Kafka mock di SEND eventi conformi al tracciato pagoPA.
 */
public interface IPnKafkaMockClient {

    /**
     * Pubblica un evento di pagamento tramite POST /kafka-mock-private/notice-payment.
     */
    void publishPaymentEvent(PaymentEventRequest request) throws RestClientException;

    /**
     * Pubblica il contenuto così com'è, senza serializzazione: serve a simulare eventi malformati o oltre 1MB.
     * L'API notice-payment lascia passare i dati malformati e li pubblica su Kafka così come sono.
     */
    void publishRawPaymentEvent(String rawPayload) throws RestClientException;
}
