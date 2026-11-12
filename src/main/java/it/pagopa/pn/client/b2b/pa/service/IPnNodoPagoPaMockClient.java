package it.pagopa.pn.client.b2b.pa.service;

import it.pagopa.pn.client.b2b.generated.openapi.clients.externalchannels.nodopagopa.model.NoticePaymentRequest;
import it.pagopa.pn.client.b2b.generated.openapi.clients.externalchannels.nodopagopa.model.NoticePaymentResponse;
import org.springframework.web.client.RestClientException;

/**
 * Client del mock del Nodo pagoPA esposto da pn-external-channels, che pubblica sul cluster Kafka mock di SEND
 * eventi di pagamento nel tracciato pagoPA.
 */
public interface IPnNodoPagoPaMockClient {

    /**
     * Pubblica un evento di pagamento tramite POST /external-channels/nodo-pagopa/v1/payment. I campi non valorizzati
     * vengono omessi dalla richiesta: il mock li pubblica assenti (tranne amount, che valorizza a "1000").
     */
    NoticePaymentResponse publishPaymentEvent(NoticePaymentRequest request) throws RestClientException;
}
