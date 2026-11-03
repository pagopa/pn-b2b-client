package it.pagopa.pn.client.b2b.pa.service.impl;

import it.pagopa.pn.client.b2b.pa.model.kafka.PaymentEventRequest;
import it.pagopa.pn.client.b2b.pa.service.IPnKafkaMockClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class PnKafkaMockClientImpl implements IPnKafkaMockClient {

    private static final String NOTICE_PAYMENT_PATH = "/kafka-mock-private/notice-payment";

    private final RestTemplate restTemplate;
    private final String noticePaymentUrl;

    public PnKafkaMockClientImpl(RestTemplate restTemplate, @Value("${pn.kafka-mock.base-url}") String baseUrl) {
        this.restTemplate = restTemplate;
        this.noticePaymentUrl = baseUrl.replaceAll("/+$", "") + NOTICE_PAYMENT_PATH;
    }

    @Override
    public void publishPaymentEvent(PaymentEventRequest request) throws RestClientException {
        log.info("Pubblicazione evento di pagamento su pn-kafka-mock: {}", request);
        post(request);
    }

    @Override
    public void publishRawPaymentEvent(String rawPayload) throws RestClientException {
        log.info("Pubblicazione evento di pagamento grezzo su pn-kafka-mock ({} caratteri)", rawPayload.length());
        post(rawPayload);
    }

    private void post(Object body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        restTemplate.postForEntity(noticePaymentUrl, new HttpEntity<>(body, headers), Void.class);
    }
}
