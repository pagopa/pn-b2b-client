package it.pagopa.pn.client.b2b.pa.service;

import it.pagopa.pn.client.b2b.generated.openapi.clients.externalchannels.nodopagopa.model.KafkaProducerResponse;
import it.pagopa.pn.client.b2b.generated.openapi.clients.externalchannels.nodopagopa.model.NoticePaymentRequest;
import it.pagopa.pn.client.b2b.generated.openapi.clients.externalchannels.nodopagopa.model.OnboardingRequest;
import org.springframework.web.client.RestClientException;

/**
 * Client del mock Kafka esposto da pn-external-channels: pubblica sul cluster Kafka mock di SEND eventi di pagamento
 * nel tracciato pagoPA e adesioni degli enti nel tracciato SelfCare.
 */
public interface IPnKafkaMockClient {

    /**
     * Pubblica un evento di pagamento; i campi non valorizzati vengono pubblicati assenti, tranne l'importo.
     */
    KafkaProducerResponse publishPaymentEvent(NoticePaymentRequest request) throws RestClientException;

    /**
     * Pubblica un'adesione; i campi non valorizzati vengono generati dal mock.
     */
    KafkaProducerResponse publishOnboardingEvent(OnboardingRequest request) throws RestClientException;
}
