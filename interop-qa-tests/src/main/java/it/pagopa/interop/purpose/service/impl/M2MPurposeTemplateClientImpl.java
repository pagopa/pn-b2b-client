package it.pagopa.interop.purpose.service.impl;

import it.pagopa.interop.common.client.AbstractClient;
import it.pagopa.interop.conf.InteropClientConfigs;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.ApiClient;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.api.PurposeTemplatesApi;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.Document;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.FileDownloadMultipart;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.PurposeTemplate;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.PurposeTemplateDraftUpdateSeed;
import it.pagopa.interop.purpose.service.IM2MPurposeTemplateClient;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.core.io.Resource;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Retryable(
        retryFor = {HttpServerErrorException.class},
        backoff = @Backoff(delay = 2000)
)
@Primary
public class M2MPurposeTemplateClientImpl extends AbstractClient implements
    IM2MPurposeTemplateClient {

    private final PurposeTemplatesApi purposesTemplateApi;
    private final RestTemplate restTemplate;
    private final String basePath;

    public M2MPurposeTemplateClientImpl(RestTemplate restTemplate, InteropClientConfigs interopClientConfigs) {
        this.restTemplate = restTemplate;
        this.basePath = interopClientConfigs.getM2mBaseUrl();
        this.purposesTemplateApi = new PurposeTemplatesApi(createApiClient("dummyBearer"));
    }

    private ApiClient createApiClient(String bearerToken) {
        ApiClient apiClient = new ApiClient(restTemplate);
        apiClient.setBasePath(basePath);
        apiClient.setBearerToken(bearerToken);
        return apiClient;
    }

    @Override
    public void setBearerToken(String bearerToken) {
        this.purposesTemplateApi.setApiClient(createApiClient(bearerToken));
    }

    @Override
    public PurposeTemplate patchPurposeTemplate(UUID id,
        PurposeTemplateDraftUpdateSeed purposePatchSeed) {
        return this.purposesTemplateApi.updateDraftPurposeTemplate(id, purposePatchSeed);
    }

    @Override
    public PurposeTemplate getPurposeTemplate(UUID id) {
        return this.purposesTemplateApi.getPurposeTemplate(id);
    }

    @Override
    public Document uploadRiskAnalysisTemplateAnswerAnnotationDocument(UUID purposeTemplateId, UUID answerId, String prettyName, Resource file) {
        return this.purposesTemplateApi.uploadRiskAnalysisTemplateAnswerAnnotationDocument(
            purposeTemplateId, file, prettyName, answerId.toString()
        );
    }

    @Override
    public FileDownloadMultipart getRiskAnalysisTemplateAnswerAnnotationDocument(UUID purposeTemplateId, UUID documentId) {
        return this.purposesTemplateApi.getRiskAnalysisTemplateAnswerAnnotationDocument(purposeTemplateId, documentId);
    }
}