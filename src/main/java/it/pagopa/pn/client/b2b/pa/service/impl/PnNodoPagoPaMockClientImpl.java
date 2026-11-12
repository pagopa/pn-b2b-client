package it.pagopa.pn.client.b2b.pa.service.impl;

import it.pagopa.pn.client.b2b.generated.openapi.clients.externalchannels.nodopagopa.ApiClient;
import it.pagopa.pn.client.b2b.generated.openapi.clients.externalchannels.nodopagopa.api.ConnectionApi;
import it.pagopa.pn.client.b2b.generated.openapi.clients.externalchannels.nodopagopa.api.PaymentApi;
import it.pagopa.pn.client.b2b.generated.openapi.clients.externalchannels.nodopagopa.model.KafkaConnection;
import it.pagopa.pn.client.b2b.generated.openapi.clients.externalchannels.nodopagopa.model.NoticePaymentRequest;
import it.pagopa.pn.client.b2b.generated.openapi.clients.externalchannels.nodopagopa.model.KafkaProducerResponse;
import it.pagopa.pn.client.b2b.pa.service.IPnNodoPagoPaMockClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class PnNodoPagoPaMockClientImpl implements IPnNodoPagoPaMockClient {

    private final PaymentApi paymentApi;
    private final ConnectionApi connectionApi;

    public PnNodoPagoPaMockClientImpl(RestTemplate restTemplate, @Value("${pn.externalChannels.base-url}") String basePath) {
        ApiClient apiClient = newApiClient(restTemplate, basePath);
        this.paymentApi = new PaymentApi(apiClient);
        this.connectionApi = new ConnectionApi(apiClient);
    }

    private static ApiClient newApiClient(RestTemplate restTemplate, String basePath) {
        ApiClient newApiClient = new ApiClient(restTemplate);
        newApiClient.setBasePath(basePath);
        return newApiClient;
    }

    @Override
    public KafkaProducerResponse publishPaymentEvent(NoticePaymentRequest request) throws RestClientException {
        ensureKafkaConnectionEnabled();
        log.info("Pubblicazione evento di pagamento sul mock del Nodo pagoPA: {}", request);
        KafkaProducerResponse response = paymentApi.producePayment(request);
        log.info("Evento di pagamento pubblicato: partition {}, offset {}", response.getPartition(), response.getOffset());
        return response;
    }

    /**
     * Dopo un errore di connessione a Kafka il mock disattiva il producer (kill switch) e rifiuta ogni pubblicazione
     * finché la connessione non viene riabilitata: senza riattivarla, un disservizio passato farebbe fallire tutti
     * gli scenari successivi. Se Kafka è ancora irraggiungibile la pubblicazione fallisce e il mock si disattiva di nuovo.
     */
    private void ensureKafkaConnectionEnabled() {
        if (!Boolean.TRUE.equals(connectionApi.checkKafkaConnection().getEnabled())) {
            log.warn("Connessione a Kafka del mock del Nodo pagoPA disattivata: viene riattivata");
            connectionApi.setKafkaConnection(new KafkaConnection().enabled(true));
        }
    }
}
