package it.pagopa.interop.purpose.service.impl;

import it.pagopa.interop.M2MVersionsMapper;
import it.pagopa.interop.common.client.AbstractDPoPClient;
import it.pagopa.interop.common.rest_template.DpopRestTemplate;
import it.pagopa.interop.conf.InteropClientConfigs;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.Document;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.FileDownloadMultipart;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.PurposeTemplate;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.PurposeTemplateDraftUpdateSeed;
import it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.api.PurposeTemplatesApi;
import it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.model.EServiceTemplates;
import it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.model.PurposeTemplateLinkEServiceTemplate;
import it.pagopa.interop.purpose.service.IM2MV3PurposeTemplateClient;
import it.pagopa.interop.utils.ApiClientUtils;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpServerErrorException;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static it.pagopa.interop.utils.ApiClientUtils.V3_UNSUPPORTED_BEARER_MSG;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Retryable(
        retryFor = {HttpServerErrorException.class},
        backoff = @Backoff(delay = 2000)
)
public class M2MV3PurposeTemplateClientImpl extends AbstractDPoPClient implements
    IM2MV3PurposeTemplateClient {

    private final PurposeTemplatesApi purposesTemplateApi;
    private final String basePath;
    private final M2MVersionsMapper vMapper;

    public M2MV3PurposeTemplateClientImpl(
        DpopRestTemplate restTemplate,
        InteropClientConfigs interopClientConfigs,
        M2MVersionsMapper vMapper
    ) {
        super(restTemplate);
        this.basePath = interopClientConfigs.getM2mV3BaseUrl();
        this.purposesTemplateApi = new PurposeTemplatesApi(
            ApiClientUtils.createApiClient(restTemplate, basePath,
                Collections.emptyMap()));
        this.vMapper = vMapper;
    }

    @Override
    public PurposeTemplate patchPurposeTemplate(UUID id,
        PurposeTemplateDraftUpdateSeed purposePatchSeed) {
        return vMapper.mapToV2(this.purposesTemplateApi.updateDraftPurposeTemplate(id, vMapper.mapToV3(purposePatchSeed)));
    }

    @Override
    public PurposeTemplate getPurposeTemplate(UUID id) {
        return vMapper.mapToV2(this.purposesTemplateApi.getPurposeTemplate(id));
    }

    @Override
    public void setBearerToken(String bearerToken) {
        throw new UnsupportedOperationException(V3_UNSUPPORTED_BEARER_MSG);
    }

    @Override
    public void setHeaders(Map<String, String> headers) {
        this.purposesTemplateApi.setApiClient(
            ApiClientUtils.createApiClient(super.getRestTemplate(), basePath, headers));
    }

    @Override
    public ResponseEntity<EServiceTemplates> getPurposeTemplateLinkableEServiceTemplate(UUID purposeTemplateId, int offset, int limit, List<UUID> creatorIds, String eserviceTemplateName) {
        return purposesTemplateApi.getPurposeTemplateEServiceTemplatesWithHttpInfo(purposeTemplateId, offset, limit, creatorIds, eserviceTemplateName);
    }

    @Override
    public ResponseEntity<Object> linkEServiceTemplateToPurposeTemplate(UUID purposeTemplateId, PurposeTemplateLinkEServiceTemplate purposeTemplateLinkEServiceTemplate) {
        return purposesTemplateApi.addPurposeTemplateEServiceTemplateWithHttpInfo(purposeTemplateId, purposeTemplateLinkEServiceTemplate);
    }

    @Override
    public ResponseEntity<Object> unlinkEServiceTemplateFromPurposeTemplate(UUID purposeTemplateId, UUID eServiceTemplateId) {
        return purposesTemplateApi.removePurposeTemplateEServiceTemplateWithHttpInfo(purposeTemplateId, eServiceTemplateId);
    }

    @Override
    public Document uploadRiskAnalysisTemplateAnswerAnnotationDocument(UUID purposeTemplateId, UUID answerId, String prettyName, Resource file) {
        return vMapper.mapToV2(this.purposesTemplateApi.uploadRiskAnalysisTemplateAnswerAnnotationDocument(
            purposeTemplateId, file, prettyName, answerId.toString()
        ));
    }

    @Override
    public FileDownloadMultipart getRiskAnalysisTemplateAnswerAnnotationDocument(UUID purposeTemplateId, UUID documentId) {
        return vMapper.mapToV2(this.purposesTemplateApi.getRiskAnalysisTemplateAnswerAnnotationDocument(purposeTemplateId, documentId));
    }
}
