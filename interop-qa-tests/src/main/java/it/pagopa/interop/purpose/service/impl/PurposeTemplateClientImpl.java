package it.pagopa.interop.purpose.service.impl;

import it.pagopa.interop.conf.InteropClientConfigs;
import it.pagopa.interop.generated.openapi.clients.bff.ApiClient;
import it.pagopa.interop.generated.openapi.clients.bff.api.PurposeTemplatesApi;
import it.pagopa.interop.generated.openapi.clients.bff.model.*;
import it.pagopa.interop.purpose.service.IPurposeTemplateClient;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.core.io.Resource;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.util.List;
import java.util.UUID;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Retryable(
        retryFor = {HttpServerErrorException.class},
        backoff = @Backoff(delay = 2000)
)
public class PurposeTemplateClientImpl implements IPurposeTemplateClient {

    private final PurposeTemplatesApi purposesTemplateApi;
    private final RestTemplate restTemplate;
    private final String basePath;

    public PurposeTemplateClientImpl(RestTemplate restTemplate, InteropClientConfigs interopClientConfigs) {
        this.restTemplate = restTemplate;
        this.basePath = interopClientConfigs.getBaseUrl();
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
    public RiskAnalysisTemplateAnswerResponse addPurposeTemplateRiskAnalysisAnswer(UUID purposeTemplateId, RiskAnalysisTemplateAnswerRequest riskAnalysisTemplateAnswerRequest) throws RestClientException {
        return purposesTemplateApi.addPurposeTemplateRiskAnalysisAnswer(purposeTemplateId, new RiskAnalysisTemplateAnswerRequest());
    }

    @Override
    public RiskAnalysisTemplateAnswerAnnotation addPurposeTemplateRiskAnalysisAnswerAnnotation(UUID purposeTemplateId, UUID answerId, RiskAnalysisTemplateAnswerAnnotationText riskAnalysisTemplateAnswerAnnotationText) throws RestClientException {
        return purposesTemplateApi.addPurposeTemplateRiskAnalysisAnswerAnnotation(purposeTemplateId, answerId, riskAnalysisTemplateAnswerAnnotationText);
    }

    @Override
    public RiskAnalysisTemplateAnswerAnnotationDocument addRiskAnalysisTemplateAnswerAnnotationDocument(UUID purposeTemplateId, UUID answerId, String prettyName, Resource doc) throws RestClientException {
        return purposesTemplateApi.addRiskAnalysisTemplateAnswerAnnotationDocument(purposeTemplateId, answerId, prettyName, doc);
    }

    @Override
    public PurposeTemplate archivePurposeTemplate(UUID purposeTemplateId) throws RestClientException {
        return purposesTemplateApi.archivePurposeTemplate(purposeTemplateId);
    }

    @Override
    public CreatedResource createPurposeTemplate(PurposeTemplateSeed purposeTemplateSeed) throws RestClientException {
        return purposesTemplateApi.createPurposeTemplate(purposeTemplateSeed);
    }

    @Override
    public void deletePurposeTemplate(UUID purposeTemplateId) throws RestClientException {
        purposesTemplateApi.deletePurposeTemplate(purposeTemplateId);
    }

    @Override
    public void deleteRiskAnalysisTemplateAnswerAnnotation(UUID purposeTemplateId, UUID answerId) throws RestClientException {
        purposesTemplateApi.deleteRiskAnalysisTemplateAnswerAnnotation(purposeTemplateId, answerId);
    }

    @Override
    public void deleteRiskAnalysisTemplateAnswerAnnotationDocument(UUID purposeTemplateId, UUID answerId, UUID documentId) throws RestClientException {
        purposesTemplateApi.deleteRiskAnalysisTemplateAnswerAnnotationDocument(purposeTemplateId, answerId, documentId);
    }

    @Override
    public CatalogPurposeTemplates getCatalogPurposeTemplates(Integer offset, Integer limit, String q, List<UUID> creatorIds, List<UUID> eserviceIds, TenantKind targetTenantKind, Boolean excludeExpiredRiskAnalysis, Boolean handlesPersonalData) throws RestClientException {
        return purposesTemplateApi.getCatalogPurposeTemplates(offset, limit, q, creatorIds, eserviceIds, targetTenantKind, excludeExpiredRiskAnalysis, handlesPersonalData);
    }

    @Override
    public CreatorPurposeTemplates getCreatorPurposeTemplates(Integer offset, Integer limit, String q, List<UUID> eserviceIds, List<PurposeTemplateState> states) throws RestClientException {
        return purposesTemplateApi.getCreatorPurposeTemplates(offset, limit, q, eserviceIds, states);
    }

    @Override
    public PurposeTemplateWithCompactCreator getPurposeTemplate(UUID purposeTemplateId) throws RestClientException {
        return purposesTemplateApi.getPurposeTemplate(purposeTemplateId);
    }

    @Override
    public EServiceDescriptorsPurposeTemplate getPurposeTemplateEServices(UUID purposeTemplateId, Integer offset, Integer limit, List<UUID> producerIds, String eserviceName) throws RestClientException {
        return purposesTemplateApi.getPurposeTemplateEServices(purposeTemplateId, offset, limit, producerIds, eserviceName);
    }

    @Override
    public File getRiskAnalysisTemplateAnswerAnnotationDocument(UUID purposeTemplateId, UUID answerId, UUID documentId) throws RestClientException {
        return purposesTemplateApi.getRiskAnalysisTemplateAnswerAnnotationDocument(purposeTemplateId, answerId, documentId);
    }

    @Override
    public EServiceDescriptorPurposeTemplate linkEServiceToPurposeTemplate(UUID purposeTemplateId, InlineObject2 inlineObject2) throws RestClientException {
        return purposesTemplateApi.linkEServiceToPurposeTemplate(purposeTemplateId, inlineObject2);
    }

    @Override
    public PurposeTemplate publishPurposeTemplate(UUID purposeTemplateId) throws RestClientException {
        return purposesTemplateApi.publishPurposeTemplate(purposeTemplateId);
    }

    @Override
    public PurposeTemplate suspendPurposeTemplate(UUID purposeTemplateId) throws RestClientException {
        return purposesTemplateApi.suspendPurposeTemplate(purposeTemplateId);
    }

    @Override
    public void unlinkEServiceToPurposeTemplate(UUID purposeTemplateId, InlineObject3 inlineObject3) throws RestClientException {
        purposesTemplateApi.unlinkEServiceToPurposeTemplate(purposeTemplateId, inlineObject3);
    }

    @Override
    public PurposeTemplate unsuspendPurposeTemplate(UUID purposeTemplateId) throws RestClientException {
        return purposesTemplateApi.unsuspendPurposeTemplate(purposeTemplateId);
    }

    @Override
    public PurposeTemplate updatePurposeTemplate(UUID purposeTemplateId, PurposeTemplateSeed purposeTemplateSeed) throws RestClientException {
        return purposesTemplateApi.updatePurposeTemplate(purposeTemplateId, purposeTemplateSeed);
    }
}
