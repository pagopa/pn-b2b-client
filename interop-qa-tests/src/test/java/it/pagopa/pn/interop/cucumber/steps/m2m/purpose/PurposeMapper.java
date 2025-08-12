package it.pagopa.pn.interop.cucumber.steps.m2m.purpose;

import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.Purpose;
import it.pagopa.interop.purpose.service.IM2MPurposeClient.PurposePatchRequest;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface PurposeMapper {
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    // TODO 05/08/2025 alcune properties non sono ancora esplicitamente ignorate perché
    //  PurposePatchRequest è al momento un oggetto temporaneo, di cui si attende la forma finale
    void copyPatchRequestToPurpose(PurposePatchRequest request, @MappingTarget Purpose purpose);

    Purpose copyPurpose(Purpose purpose);
}