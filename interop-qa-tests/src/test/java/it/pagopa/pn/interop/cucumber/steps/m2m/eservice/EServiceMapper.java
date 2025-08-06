package it.pagopa.pn.interop.cucumber.steps.m2m.eservice;

import it.pagopa.interop.eservice.service.impl.M2MEserviceClientImpl.EServicePatchRequest;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.EService;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface EServiceMapper {
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    // TODO 05/08/2025 alcune properties non sono ancora esplicitamente ignorate perché
    //  EServicePatchRequest è al momento un oggetto temporaneo, di cui si attende la forma finale
    void copyPatchRequestToEService(EServicePatchRequest request, @MappingTarget EService eService);

    EService copyEService(EService eService);
}
