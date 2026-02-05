package it.pagopa.interop.purpose.service;

import it.pagopa.interop.authorization.service.utils.SettableBearerToken;
import it.pagopa.interop.generated.openapi.clients.bff.model.CatalogPurposeTemplates;
import it.pagopa.interop.generated.openapi.clients.bff.model.CreatedResource;
import it.pagopa.interop.generated.openapi.clients.bff.model.CreatorPurposeTemplates;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceDescriptorPurposeTemplate;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceDescriptorsPurposeTemplate;
import it.pagopa.interop.generated.openapi.clients.bff.model.LinkEServiceToPurposeTemplateRequest;
import it.pagopa.interop.generated.openapi.clients.bff.model.PurposeTemplate;
import it.pagopa.interop.generated.openapi.clients.bff.model.PurposeTemplateSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.PurposeTemplateState;
import it.pagopa.interop.generated.openapi.clients.bff.model.PurposeTemplateWithCompactCreator;
import it.pagopa.interop.generated.openapi.clients.bff.model.RiskAnalysisTemplateAnswerAnnotation;
import it.pagopa.interop.generated.openapi.clients.bff.model.RiskAnalysisTemplateAnswerAnnotationDocument;
import it.pagopa.interop.generated.openapi.clients.bff.model.RiskAnalysisTemplateAnswerAnnotationSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.RiskAnalysisTemplateAnswerRequest;
import it.pagopa.interop.generated.openapi.clients.bff.model.RiskAnalysisTemplateAnswerResponse;
import it.pagopa.interop.generated.openapi.clients.bff.model.TenantKind;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.PurposeTemplates;
import java.io.File;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nullable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;

public interface IPurposeTemplateClient extends SettableBearerToken {
    RiskAnalysisTemplateAnswerResponse addPurposeTemplateRiskAnalysisAnswer(UUID purposeTemplateId, RiskAnalysisTemplateAnswerRequest riskAnalysisTemplateAnswerRequest) throws RestClientException;
    RiskAnalysisTemplateAnswerAnnotation addPurposeTemplateRiskAnalysisAnswerAnnotation(UUID purposeTemplateId, UUID answerId, RiskAnalysisTemplateAnswerAnnotationSeed riskAnalysisTemplateAnswerAnnotationSeed) throws RestClientException;
    RiskAnalysisTemplateAnswerAnnotationDocument addRiskAnalysisTemplateAnswerAnnotationDocument(UUID purposeTemplateId, UUID answerId, String prettyName, org.springframework.core.io.Resource doc) throws RestClientException;
    void archivePurposeTemplate(UUID purposeTemplateId) throws RestClientException;
    CreatedResource createPurposeTemplate(PurposeTemplateSeed purposeTemplateSeed) throws RestClientException;
    void deletePurposeTemplate(UUID purposeTemplateId) throws RestClientException;
    void deleteRiskAnalysisTemplateAnswerAnnotation(UUID purposeTemplateId, UUID answerId) throws RestClientException;
    void deleteRiskAnalysisTemplateAnswerAnnotationDocument(UUID purposeTemplateId, UUID answerId, UUID documentId) throws RestClientException;
    CatalogPurposeTemplates getCatalogPurposeTemplates(Integer offset, Integer limit, String q, List<UUID> creatorIds, List<UUID> eserviceIds, TenantKind targetTenantKind, Boolean excludeExpiredRiskAnalysis, Boolean handlesPersonalData) throws RestClientException;
    CreatorPurposeTemplates getCreatorPurposeTemplates(Integer offset, Integer limit, String q, List<UUID> eserviceIds, List<PurposeTemplateState> states) throws RestClientException;
    PurposeTemplateWithCompactCreator getPurposeTemplate(UUID purposeTemplateId) throws RestClientException;

    ResponseEntity<PurposeTemplateWithCompactCreator> getPurposeTemplateWithHttpInfo(
        UUID purposeTemplateId) throws RestClientException;

    EServiceDescriptorsPurposeTemplate getPurposeTemplateEServices(UUID purposeTemplateId, Integer offset, Integer limit, @Nullable List<UUID> producerIds, @Nullable String eserviceName) throws RestClientException;
    File getRiskAnalysisTemplateAnswerAnnotationDocument(UUID purposeTemplateId, UUID answerId, UUID documentId) throws RestClientException;

    EServiceDescriptorPurposeTemplate linkEServiceToPurposeTemplate(UUID purposeTemplateId, LinkEServiceToPurposeTemplateRequest linkRequest) throws RestClientException;
    void publishPurposeTemplate(UUID purposeTemplateId) throws RestClientException;
    void suspendPurposeTemplate(UUID purposeTemplateId) throws RestClientException;
    void unlinkEServiceToPurposeTemplate(UUID purposeTemplateId, LinkEServiceToPurposeTemplateRequest linkRequest) throws RestClientException;
    void unsuspendPurposeTemplate(UUID purposeTemplateId) throws RestClientException;
    PurposeTemplate updatePurposeTemplate(UUID purposeTemplateId, PurposeTemplateSeed purposeTemplateSeed) throws RestClientException;

    PurposeTemplates getPurposeTemplates(Integer offset, Integer limit, String purposeTitle, List<UUID> creatorIds, List<UUID> eserviceIds, List<it.pagopa.interop.generated.openapi.clients.m2mGateway.model.PurposeTemplateState> states, it.pagopa.interop.generated.openapi.clients.m2mGateway.model.TargetTenantKind targetTenantKind, Boolean handlesPersonalData);
}
