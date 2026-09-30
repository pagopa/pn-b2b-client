package it.pagopa.interop.purpose.service.impl;

import it.pagopa.interop.common.client.AbstractClient;
import it.pagopa.interop.conf.InteropClientConfigs;
import it.pagopa.interop.generated.openapi.clients.bff.ApiClient;
import it.pagopa.interop.generated.openapi.clients.bff.api.PurposeTemplatesApi;
import it.pagopa.interop.generated.openapi.clients.bff.model.*;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.PurposeTemplates;
import it.pagopa.interop.purpose.service.IPurposeTemplateClient;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;
import java.util.UUID;

import static it.pagopa.interop.utils.BlobFileCreationUtils.createTempFile;
import static java.util.Objects.isNull;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class PurposeTemplateClientImpl extends AbstractClient implements IPurposeTemplateClient {

    private final PurposeTemplatesApi purposesTemplateApi;
    private final it.pagopa.interop.generated.openapi.clients.m2mGateway.api.PurposeTemplatesApi m2mPurposeTemplatesApi;
    private final RestTemplate restTemplate;
    private final String basePath;
    private final String m2mBasePath;

    public PurposeTemplateClientImpl(RestTemplate restTemplate, InteropClientConfigs interopClientConfigs) {
        this.restTemplate = restTemplate;
        this.basePath = interopClientConfigs.getBaseUrl();
        this.m2mBasePath = interopClientConfigs.getM2mBaseUrl();
        this.purposesTemplateApi = new PurposeTemplatesApi(createApiClient("dummyBearer"));
        this.m2mPurposeTemplatesApi = new it.pagopa.interop.generated.openapi.clients.m2mGateway.api.PurposeTemplatesApi(createM2MApiClient("dummyBearer"));
    }

    private ApiClient createApiClient(String bearerToken) {
        ApiClient apiClient = new ApiClient(restTemplate);
        apiClient.setBasePath(basePath);
        apiClient.setBearerToken(bearerToken);
        return apiClient;
    }

    private it.pagopa.interop.generated.openapi.clients.m2mGateway.ApiClient createM2MApiClient(String bearerToken) {
        it.pagopa.interop.generated.openapi.clients.m2mGateway.ApiClient apiClient = new it.pagopa.interop.generated.openapi.clients.m2mGateway.ApiClient(restTemplate);
        apiClient.setBasePath(m2mBasePath);
        apiClient.setBearerToken(bearerToken);
        return apiClient;
    }

    @Override
    public void setBearerToken(String bearerToken) {
        this.purposesTemplateApi.setApiClient(createApiClient(bearerToken));
        this.m2mPurposeTemplatesApi.setApiClient(createM2MApiClient(bearerToken));
    }

    @Override
    public RiskAnalysisTemplateAnswerResponse addPurposeTemplateRiskAnalysisAnswer(UUID purposeTemplateId, RiskAnalysisTemplateAnswerRequest riskAnalysisTemplateAnswerRequest) throws RestClientException {
        return purposesTemplateApi.addPurposeTemplateRiskAnalysisAnswer(purposeTemplateId, riskAnalysisTemplateAnswerRequest);
    }

    @Override
    public RiskAnalysisTemplateAnswerAnnotation addPurposeTemplateRiskAnalysisAnswerAnnotation(UUID purposeTemplateId, UUID answerId, RiskAnalysisTemplateAnswerAnnotationSeed riskAnalysisTemplateAnswerAnnotationSeed) throws RestClientException {
        return purposesTemplateApi.addPurposeTemplateRiskAnalysisAnswerAnnotation(purposeTemplateId, answerId, riskAnalysisTemplateAnswerAnnotationSeed);
    }

    @Override
    public RiskAnalysisTemplateAnswerAnnotationDocument addRiskAnalysisTemplateAnswerAnnotationDocument(UUID purposeTemplateId, UUID answerId, String prettyName, Resource doc) throws RestClientException {
        return purposesTemplateApi.addRiskAnalysisTemplateAnswerAnnotationDocument(purposeTemplateId, answerId, prettyName, doc);
    }

    @Override
    public void archivePurposeTemplate(UUID purposeTemplateId) throws RestClientException {
        purposesTemplateApi.archivePurposeTemplate(purposeTemplateId);
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
        TargetTenantKind tenantKind = isNull(targetTenantKind) ? null : switch(targetTenantKind) {
            case PRIVATE -> TargetTenantKind.PRIVATE;
            default -> TargetTenantKind.PA;
        };

        return purposesTemplateApi.getCatalogPurposeTemplates(offset, limit, q, creatorIds, eserviceIds,
            tenantKind, excludeExpiredRiskAnalysis, handlesPersonalData);
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
    public ResponseEntity<PurposeTemplateWithCompactCreator> getPurposeTemplateWithHttpInfo(
        UUID purposeTemplateId) throws RestClientException {
        return purposesTemplateApi.getPurposeTemplateWithHttpInfo(purposeTemplateId);
    }

    @Override
    public Resources getPurposeTemplateEServices(UUID purposeTemplateId, Integer offset, Integer limit, @Nullable List<UUID> producerIds, @Nullable String eserviceName) throws RestClientException {
        LinkableResources resources = purposesTemplateApi.getPurposeTemplateLinkableResources(purposeTemplateId, offset, limit, eserviceName, producerIds);
        Resources result = new Resources();
        result.setResults(resources.getResults());
        return result;
    }

    @Override
    public File getRiskAnalysisTemplateAnswerAnnotationDocument(UUID purposeTemplateId, UUID answerId, UUID documentId) throws RestClientException {
        try {
            Resource resourceResponse = purposesTemplateApi.getRiskAnalysisTemplateAnswerAnnotationDocument(purposeTemplateId, answerId, documentId);
            return createTempFile("riskAnalysis-template-annotation-document-",resourceResponse.getInputStream());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public EServiceDescriptorPurposeTemplate linkEServiceToPurposeTemplate(UUID purposeTemplateId, UUID eserviceId) throws RestClientException {
        LinkableResourceRequest request = new LinkableResourceRequest();
        request
                .resourceKind(LinkableResourceRequest.ResourceKindEnum.ESERVICE)
                .eserviceId(eserviceId);

        LinkedResource resource = purposesTemplateApi.linkResourceToPurposeTemplate(purposeTemplateId, request);
        EServiceDescriptorPurposeTemplate result = new EServiceDescriptorPurposeTemplate();

        result.setPurposeTemplateId(purposeTemplateId);
        result.setEserviceId(eserviceId);
        result.setDescriptorId(resource.getDescriptorId());
        result.setCreatedAt(resource.getCreatedAt());

        return result;
    }

    @Override
    public void publishPurposeTemplate(UUID purposeTemplateId) throws RestClientException {
        purposesTemplateApi.publishPurposeTemplate(purposeTemplateId);
    }

    @Override
    public void suspendPurposeTemplate(UUID purposeTemplateId) throws RestClientException {
        purposesTemplateApi.suspendPurposeTemplate(purposeTemplateId);
    }

    @Override
    public void unlinkEServiceToPurposeTemplate(UUID purposeTemplateId, UUID eserviceId) throws RestClientException {
        LinkableResourceRequest request = new LinkableResourceRequest();
        request
                .resourceKind(LinkableResourceRequest.ResourceKindEnum.ESERVICE)
                .eserviceId(eserviceId);
        purposesTemplateApi.unlinkResourceFromPurposeTemplate(purposeTemplateId, request);
    }

    @Override
    public void unsuspendPurposeTemplate(UUID purposeTemplateId) throws RestClientException {
        purposesTemplateApi.unsuspendPurposeTemplate(purposeTemplateId);
    }

    @Override
    public PurposeTemplate updatePurposeTemplate(UUID purposeTemplateId, PurposeTemplateSeed purposeTemplateSeed) throws RestClientException {
        return purposesTemplateApi.updatePurposeTemplate(purposeTemplateId, purposeTemplateSeed);
    }

    @Override
    public PurposeTemplates getPurposeTemplates(Integer offset, Integer limit, String purposeTitle, List<UUID> creatorIds, List<UUID> eserviceIds, List<it.pagopa.interop.generated.openapi.clients.m2mGateway.model.PurposeTemplateState> states, it.pagopa.interop.generated.openapi.clients.m2mGateway.model.TargetTenantKind targetTenantKind, Boolean handlesPersonalData) {
        return m2mPurposeTemplatesApi.getPurposeTemplates(offset, limit, purposeTitle, creatorIds, eserviceIds, states, targetTenantKind, handlesPersonalData);
    }

    @Override
    public LinkableResources getPurposeTemplateLinkableResources(UUID purposeTemplateId, Integer offset, Integer limit, String q, List<UUID> publisherIds) {
        return purposesTemplateApi.getPurposeTemplateLinkableResources(purposeTemplateId, offset, limit, q, publisherIds);
    }

    @Override
    public LinkedResource linkResourceToPurposeTemplate(UUID purposeTemplateId, LinkableResourceRequest linkableResourceRequest) {
        return purposesTemplateApi.linkResourceToPurposeTemplate(purposeTemplateId, linkableResourceRequest);
    }

    @Override
    public void unlinkResourceFromPurposeTemplate(UUID purposeTemplateId, LinkableResourceRequest linkableResourceRequest) {
        purposesTemplateApi.unlinkResourceFromPurposeTemplate(purposeTemplateId, linkableResourceRequest);
    }
}
