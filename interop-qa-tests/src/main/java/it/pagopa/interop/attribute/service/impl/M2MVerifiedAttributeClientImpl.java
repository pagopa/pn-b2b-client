package it.pagopa.interop.attribute.service.impl;

import static java.util.function.Function.identity;

import it.pagopa.interop.attribute.service.IM2MVerifiedAttributeClient;
import it.pagopa.interop.common.client.AbstractClient;
import it.pagopa.interop.common.enums.EntityIdType;
import it.pagopa.interop.common.operation.SimpleOperation;
import it.pagopa.interop.conf.InteropClientConfigs;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.ApiClient;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.api.AttributesApi;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.VerifiedAttribute;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@ToString
@EqualsAndHashCode(callSuper = true)
@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class M2MVerifiedAttributeClientImpl extends AbstractClient implements
    IM2MVerifiedAttributeClient {
    private final AttributesApi attributesApi;
    private final RestTemplate restTemplate;
    private final String basePath;

    public M2MVerifiedAttributeClientImpl(RestTemplate restTemplate, InteropClientConfigs interopClientConfigs) {
        this.restTemplate = restTemplate;
        this.basePath = interopClientConfigs.getM2mBaseUrl();
        this.attributesApi = new AttributesApi(createApiClient("dummyBearer"));
    }

    private ApiClient createApiClient(String bearerToken) {
        ApiClient apiClient = new ApiClient(restTemplate);
        apiClient.setBasePath(basePath);
        apiClient.setBearerToken(bearerToken);
        return apiClient;
    }

    @Override
    public void setBearerToken(String bearerToken) {
        this.attributesApi.setApiClient(createApiClient(bearerToken));
    }

    @Override
    public VerifiedAttribute get(UUID id) {
       return this.performOperation(SimpleOperation.of(
               () -> this.attributesApi.getVerifiedAttribute(id),
               identity()
       )).orElse(null);
    }

    @Override
    public List<VerifiedAttribute> getAll() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public UUID getId(VerifiedAttribute entity) {
        return entity == null ? null : entity.getId();
    }

    @Override
    public UUID generateId(EntityIdType type) {
        return switch (type){
            case INVALID_ID -> UUID.fromString("00000000-0000-4000-8000-abcdefabcdef"); // La classe UUID non permette di formare un UUID malformato
            case NON_EXISTENT_ID -> UUID.fromString("00000000-0000-4000-8000-abcdefabcdef");
            default -> throw new IllegalStateException("Tipo di id non supportato: " + type.name());
        };
    }

    @Override
    public VerifiedAttribute create(VerifiedAttributeSeed agreementPayload) {
        return this.performOperation(SimpleOperation.of(
                //() -> this.attributesApi.createVerifiedAttribute(agreementPayload), <-- TODO 09/07/2025 DECOMMENTARE una volta rilasciare in QA le apis in oggetto
                VerifiedAttribute::new, // TODO 09/07/2025 placeholder, rimuovere una volta rilasciate le API in QA
                res -> res
        )).orElse(null);
    }

    // TODO 04/08/2025: specifica OpenAPI ancora non rilasciata,
    //  sostituire lo stub seguente con l'implementazione reale una volta rilasciata
    @Override
    public List<UUID> getVerifiers(UUID tenantId, UUID attributeId) {
        return Collections.emptyList();
    }

    // TODO 05/08/2025: specifica OpenAPI ancora non rilasciata,
    //  sostituire lo stub seguente con l'implementazione reale una volta rilasciata
    @Override
    public List<UUID> getRevokers(UUID organizationId, UUID verifiedAttributeId) {
        return Collections.emptyList();
    }
}
