package it.pagopa.pn.interop.cucumber.steps.catalog.utils;

import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.m2m.common.utils.AbstractResolver;

import java.util.UUID;

public class CatalogResolver extends AbstractResolver {

    public CatalogResolver(SharedStepsContext sharedStepsContext) {
        super(sharedStepsContext);
    }

    public UUID resolveEServiceId(String eServiceId) {
        return resolveOrParse(
                eServiceId,
                UUID::fromString,
                () -> sharedStepsContext.getEServicesCommonContext().getEserviceId(),
                null,
                UUID::randomUUID,
                () -> null
        );
    }

    public UUID resolveOldDescriptorId(String descriptorId) {
        return resolveOrParse(
                descriptorId,
                UUID::fromString,
                () -> sharedStepsContext.getEServicesCommonContext().getOldDescriptorId(),
                null,
                UUID::randomUUID,
                () -> null
        );
    }

    public String resolveArchivingReason(String raw) {
        return resolveOrParse(
                raw,
                v -> v, 
                null,
                null,
                null,
                () -> ""
        );
    }
}
