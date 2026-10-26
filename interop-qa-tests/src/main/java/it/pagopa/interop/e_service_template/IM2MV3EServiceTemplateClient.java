package it.pagopa.interop.e_service_template;

import it.pagopa.interop.authorization.service.utils.Authenticable;
import it.pagopa.interop.authorization.service.utils.SettableHeaders;
import it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.model.EService;
import it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.model.EServiceDescriptor;
import it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.model.EServiceInstanceDescriptorSeed;
import it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.model.EServiceInstanceLabelUpdateSeed;
import it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.model.InstanceEServiceSeed;
import it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.model.UpdateEServiceDescriptorTemplateInstanceSeed;
import it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.model.UpdateEServiceTemplateInstanceDescriptorQuotasSeed;
import it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.model.UpdateEServiceTemplateInstanceSeed;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

public interface IM2MV3EServiceTemplateClient extends IM2MEServiceTemplateClient, SettableHeaders,
        Authenticable {

    ResponseEntity<EService> createEServiceInstanceFromTemplate(UUID templateId, InstanceEServiceSeed seed);

    ResponseEntity<EService> updateEServiceTemplateInstance(UUID eserviceId, UpdateEServiceTemplateInstanceSeed seed);

    ResponseEntity<EService> updateEServiceTemplateInstanceLabel(UUID eserviceId, EServiceInstanceLabelUpdateSeed seed);


}
