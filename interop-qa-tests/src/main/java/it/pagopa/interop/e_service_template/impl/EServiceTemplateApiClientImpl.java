package it.pagopa.interop.e_service_template.impl;

import it.pagopa.interop.conf.InteropClientConfigs;
import it.pagopa.interop.e_service_template.IEServiceTemplateClient;
import it.pagopa.interop.generated.openapi.clients.bff.ApiClient;
import it.pagopa.interop.generated.openapi.clients.bff.api.EserviceTemplatesApi;
import it.pagopa.interop.generated.openapi.clients.bff.model.CreatedEServiceTemplateVersion;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTemplateSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTemplateVersionDetails;
import java.util.UUID;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class EServiceTemplateApiClientImpl implements IEServiceTemplateClient {
    private final EserviceTemplatesApi eserviceTemplatesApi;
    private final RestTemplate restTemplate;
    private final String basePath;

    public EServiceTemplateApiClientImpl(RestTemplate restTemplate, InteropClientConfigs interopClientConfigs) {
        this.restTemplate = restTemplate;
        this.basePath = interopClientConfigs.getBaseUrl();
        this.eserviceTemplatesApi = new EserviceTemplatesApi(createApiClient("dummyBearer"));
    }

    private ApiClient createApiClient(String bearerToken) {
        ApiClient apiClient = new ApiClient(restTemplate);
        apiClient.setBasePath(basePath);
        apiClient.setBearerToken(bearerToken);
        return apiClient;
    }

    @Override
    public CreatedEServiceTemplateVersion createEServiceTemplate(String xCorrelationId,
        EServiceTemplateSeed eserviceSeed) {
        return eserviceTemplatesApi.createEServiceTemplate(xCorrelationId, eserviceSeed);
    }

    @Override
    public EServiceTemplateVersionDetails getEServiceTemplateVersion(String xCorrelationId,
        UUID eServiceTemplateId, UUID eServiceTemplateVersionId) {
        return eserviceTemplatesApi.getEServiceTemplateVersion(xCorrelationId, eServiceTemplateId,
            eServiceTemplateVersionId);
    }

    @Override
    public void setBearerToken(String bearerToken) {
        this.eserviceTemplatesApi.setApiClient(createApiClient(bearerToken));
    }
}
