package it.pagopa.pn.client.b2b.pa.service.impl;


import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import it.pagopa.pn.client.b2b.radd.generated.openapi.clients.vpce.ApiClient;
import it.pagopa.pn.client.b2b.radd.generated.openapi.clients.vpce.api_RaddNetVpce.ActOperationsApi;
import it.pagopa.pn.client.b2b.radd.generated.openapi.clients.vpce.api_RaddNetVpce.AorOperationsApi;
import it.pagopa.pn.client.b2b.radd.generated.openapi.clients.vpce.api_RaddNetVpce.DocumentOperationsApi;
import it.pagopa.pn.client.b2b.radd.generated.openapi.clients.vpce.model_RaddNetVpce.ActInquiryResponse;
import it.pagopa.pn.client.b2b.radd.generated.openapi.clients.vpce.model_RaddNetVpce.AorInquiryResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.ssl.TrustStrategy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLContext;


@Slf4j
@Component
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class PnRaddNetVpceClientImpl {

    private final ActOperationsApi actApi;
    private final AorOperationsApi aorApi;
    private final DocumentOperationsApi docApi;

    public PnRaddNetVpceClientImpl(
            @Value("${pn.radd-vpc.base-url}") String basePath) throws Exception {

        RestTemplate unsafeRestTemplate = buildUnsafeRestTemplate();
        ApiClient apiClient = new ApiClient(unsafeRestTemplate);
        apiClient.setBasePath(basePath);

        this.actApi = new ActOperationsApi(apiClient);
        this.aorApi = new AorOperationsApi(apiClient);
        this.docApi = new DocumentOperationsApi(apiClient);
    }

    public ActInquiryResponse actInquiry(String uid, String recipientTaxId, String recipientType, String qrCode, String iun) {
        log.info("CALLING VPCE CLIENT - actInquiry");
        return actApi.actInquiry(uid, recipientTaxId, recipientType, qrCode, iun);
    }

    public AorInquiryResponse aorInquiry(String uid, String recipientTaxId, String recipientType) {
        return aorApi.aorInquiry(uid, recipientTaxId, recipientType);
    }

    public byte[] documentDownload(String operationType, String operationId, String attachmentId) {
        try {
            return docApi.documentDownload(operationType, operationId, attachmentId).getInputStream().readAllBytes();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private RestTemplate buildUnsafeRestTemplate() throws Exception {

        TrustStrategy acceptingTrustStrategy = (chain, authType) -> true;
        SSLContext sslContext = SSLContexts.custom().loadTrustMaterial(null, acceptingTrustStrategy).build();
        SSLConnectionSocketFactory csf = new SSLConnectionSocketFactory(sslContext, NoopHostnameVerifier.INSTANCE);
        CloseableHttpClient httpClient = HttpClients.custom().setSSLSocketFactory(csf).build();
        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(httpClient);
        RestTemplate restTemplate = new RestTemplate(requestFactory);
        ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule()).disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS).configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter(mapper);
        restTemplate.getMessageConverters().removeIf(c -> c instanceof MappingJackson2HttpMessageConverter);
        restTemplate.getMessageConverters().add(converter);

        return restTemplate;
    }

    public it.pagopa.pn.client.b2b.radd.generated.openapi.clients.vpce.model_RaddNetVpce.StartTransactionResponse startActTransaction(String uid, it.pagopa.pn.client.b2b.radd.generated.openapi.clients.vpce.model_RaddNetVpce.ActStartTransactionRequest request) {
        log.info("CALLING VPCE CLIENT - startActTransaction");

        return actApi.startActTransaction(uid, request);
    }

    public it.pagopa.pn.client.b2b.radd.generated.openapi.clients.vpce.model_RaddNetVpce.CompleteTransactionResponse completeActTransaction(String uid, it.pagopa.pn.client.b2b.radd.generated.openapi.clients.vpce.model_RaddNetVpce.CompleteTransactionRequest request) {
        log.info("CALLING VPCE CLIENT - completeActTransaction");

        return actApi.completeActTransaction(uid, request);
    }

    public it.pagopa.pn.client.b2b.radd.generated.openapi.clients.vpce.model_RaddNetVpce.StartTransactionResponse startAorTransaction(String uid, it.pagopa.pn.client.b2b.radd.generated.openapi.clients.vpce.model_RaddNetVpce.AorStartTransactionRequest request) {
        log.info("CALLING VPCE CLIENT - startAorTransaction");

        return aorApi.startAorTransaction(uid, request);
    }

    public it.pagopa.pn.client.b2b.radd.generated.openapi.clients.vpce.model_RaddNetVpce.CompleteTransactionResponse completeAorTransaction(String uid, it.pagopa.pn.client.b2b.radd.generated.openapi.clients.vpce.model_RaddNetVpce.CompleteTransactionRequest request) {
        log.info("CALLING VPCE CLIENT - completeAorTransaction");

        return aorApi.completeAorTransaction(uid, request);
    }

    public it.pagopa.pn.client.b2b.radd.generated.openapi.clients.vpce.model_RaddNetVpce.AbortTransactionResponse abortAorTransaction(String uid, it.pagopa.pn.client.b2b.radd.generated.openapi.clients.vpce.model_RaddNetVpce.AbortTransactionRequest request) {
        log.info("CALLING VPCE CLIENT - abortAorTransaction");

        return aorApi.abortAorTransaction(uid, request);
    }

    public it.pagopa.pn.client.b2b.radd.generated.openapi.clients.vpce.model_RaddNetVpce.AbortTransactionResponse abortActTransaction(String uid, it.pagopa.pn.client.b2b.radd.generated.openapi.clients.vpce.model_RaddNetVpce.AbortTransactionRequest request) {
        log.info("CALLING VPCE CLIENT - abortActTransaction");

        return actApi.abortActTransaction(uid, request);
    }
}


