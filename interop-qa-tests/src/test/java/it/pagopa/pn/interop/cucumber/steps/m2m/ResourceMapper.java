package it.pagopa.pn.interop.cucumber.steps.m2m;

import org.mapstruct.BeanMapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

public interface ResourceMapper<PATCH_REQUEST, RESOURCE> {
    /* Generalmente il senso di un' op. PATCH è applicare solo le modifiche specificate; si
     * interpretano i valori NULL dell'input come "nessuna modifica attesa", e dunque
     * vengono ignorati attraverso NullValuePropertyMappingStrategy.IGNORE */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void copyPatchRequestToResource(PATCH_REQUEST request, @MappingTarget RESOURCE resource);

    PATCH_REQUEST mapResourceToPatchRequest(RESOURCE resource);

    RESOURCE copyResource(RESOURCE resource);
}