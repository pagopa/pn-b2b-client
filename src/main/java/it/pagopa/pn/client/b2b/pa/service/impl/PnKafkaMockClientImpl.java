package it.pagopa.pn.client.b2b.pa.service.impl;

import com.fasterxml.jackson.databind.SerializationFeature;
import it.pagopa.pn.client.b2b.generated.openapi.clients.externalchannels.nodopagopa.ApiClient;
import it.pagopa.pn.client.b2b.generated.openapi.clients.externalchannels.nodopagopa.api.ConnectionApi;
import it.pagopa.pn.client.b2b.generated.openapi.clients.externalchannels.nodopagopa.api.OnboardingApi;
import it.pagopa.pn.client.b2b.generated.openapi.clients.externalchannels.nodopagopa.api.PaymentApi;
import it.pagopa.pn.client.b2b.generated.openapi.clients.externalchannels.nodopagopa.model.KafkaConnection;
import it.pagopa.pn.client.b2b.generated.openapi.clients.externalchannels.nodopagopa.model.NoticePaymentRequest;
import it.pagopa.pn.client.b2b.generated.openapi.clients.externalchannels.nodopagopa.model.OnboardingRequest;
import it.pagopa.pn.client.b2b.generated.openapi.clients.externalchannels.nodopagopa.model.KafkaProducerResponse;
import it.pagopa.pn.client.b2b.pa.service.IPnKafkaMockClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class PnKafkaMockClientImpl implements IPnKafkaMockClient {

    // Il client è prototype: il controllo della connessione vale per tutta l'esecuzione
    private static final AtomicBoolean KAFKA_CONNECTION_CHECKED = new AtomicBoolean(false);

    private final PaymentApi paymentApi;
    private final OnboardingApi onboardingApi;
    private final ConnectionApi connectionApi;

    public PnKafkaMockClientImpl(RestTemplate restTemplate, @Value("${pn.externalChannels.base-url}") String basePath) {
        ApiClient apiClient = newApiClient(withIsoDateSerialization(restTemplate), basePath);
        this.paymentApi = new PaymentApi(apiClient);
        this.onboardingApi = new OnboardingApi(apiClient);
        this.connectionApi = new ConnectionApi(apiClient);
    }

    /**
     * Il mock rifiuta le date serializzate come timestamp decimali, come fa il RestTemplate condiviso: questa copia
     * le serializza in ISO-8601 e mantiene request factory ed error handler.
     */
    private static RestTemplate withIsoDateSerialization(RestTemplate sharedRestTemplate) {
        List<HttpMessageConverter<?>> messageConverters = sharedRestTemplate.getMessageConverters().stream()
                .<HttpMessageConverter<?>>map(converter -> converter instanceof MappingJackson2HttpMessageConverter jacksonConverter
                        ? new MappingJackson2HttpMessageConverter(jacksonConverter.getObjectMapper().copy()
                                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS))
                        : converter)
                .toList();
        RestTemplate kafkaMockRestTemplate = new RestTemplate(sharedRestTemplate.getRequestFactory());
        kafkaMockRestTemplate.setMessageConverters(messageConverters);
        kafkaMockRestTemplate.setErrorHandler(sharedRestTemplate.getErrorHandler());
        return kafkaMockRestTemplate;
    }

    private static ApiClient newApiClient(RestTemplate restTemplate, String basePath) {
        ApiClient newApiClient = new ApiClient(restTemplate);
        newApiClient.setBasePath(basePath);
        return newApiClient;
    }

    @Override
    public KafkaProducerResponse publishPaymentEvent(NoticePaymentRequest request) throws RestClientException {
        ensureKafkaConnectionEnabled();
        log.info("Pubblicazione evento di pagamento sul mock Kafka: {}", request);
        KafkaProducerResponse response = paymentApi.producePayment(request);
        log.info("Evento di pagamento pubblicato: partition {}, offset {}", response.getPartition(), response.getOffset());
        return response;
    }

    @Override
    public KafkaProducerResponse publishOnboardingEvent(OnboardingRequest request) throws RestClientException {
        ensureKafkaConnectionEnabled();
        log.info("Pubblicazione adesione sul mock Kafka: {}", request);
        KafkaProducerResponse response = onboardingApi.produceOnboarding(request);
        log.info("Adesione pubblicata: partition {}, offset {}", response.getPartition(), response.getOffset());
        return response;
    }

    /**
     * Dopo un errore di connessione il mock disattiva il producer finché non viene riabilitato: senza riattivarlo, un
     * disservizio passato farebbe fallire tutti gli scenari. Il controllo si fa alla prima pubblicazione dell'esecuzione.
     */
    private void ensureKafkaConnectionEnabled() {
        if (KAFKA_CONNECTION_CHECKED.get()) {
            return;
        }
        synchronized (KAFKA_CONNECTION_CHECKED) {
            if (KAFKA_CONNECTION_CHECKED.get()) {
                return;
            }
            if (!Boolean.TRUE.equals(connectionApi.checkKafkaConnection().getEnabled())) {
                log.warn("Connessione a Kafka del mock disattivata: viene riattivata");
                connectionApi.setKafkaConnection(new KafkaConnection().enabled(true));
            }
            KAFKA_CONNECTION_CHECKED.set(true);
        }
    }
}
