package it.pagopa.pn.client.b2b.pa.service.impl;


import it.pagopa.pn.client.b2b.radd.generated.openapi.clients.vpce.ApiClient;
import it.pagopa.pn.client.b2b.radd.generated.openapi.clients.vpce.api_RaddNetVpce.ActOperationsApi;
import it.pagopa.pn.client.b2b.radd.generated.openapi.clients.vpce.api_RaddNetVpce.AorOperationsApi;
import it.pagopa.pn.client.b2b.radd.generated.openapi.clients.vpce.api_RaddNetVpce.DocumentOperationsApi;
import it.pagopa.pn.client.b2b.radd.generated.openapi.clients.vpce.model_RaddNetVpce.ActInquiryResponse;
import it.pagopa.pn.client.b2b.radd.generated.openapi.clients.vpce.model_RaddNetVpce.AorInquiryResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;


@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class PnRaddNetVpceClientImpl {

    AorOperationsApi aorApi;
    private final ActOperationsApi actApi;
    private final DocumentOperationsApi docApi;

    public PnRaddNetVpceClientImpl(
            RestTemplate restTemplate,
            @Value("${pn.radd.vpce.base-url}") String basePath
    ) {
        ApiClient apiClient = new ApiClient(restTemplate);
        apiClient.setBasePath(basePath);

        this.actApi = new ActOperationsApi(apiClient);
        this.aorApi = new AorOperationsApi(apiClient);
        this.docApi = new DocumentOperationsApi(apiClient);
    }

    public ActInquiryResponse actInquiry(
            String uid,
            String recipientTaxId,
            String recipientType,
            String qrCode,
            String iun
    ) {
        return actApi.actInquiry(uid, recipientTaxId, recipientType, qrCode, iun);
    }

    public AorInquiryResponse aorInquiry(String uid, String recipientTaxId, String recipientType) {
        return aorApi.aorInquiry(uid, recipientTaxId, recipientType);
    }

    public byte[] documentDownload(String operationType, String operationId, String attachmentId) {
        try {
            return docApi.documentDownload(operationType, operationId, attachmentId)
                    .getInputStream()
                    .readAllBytes();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

