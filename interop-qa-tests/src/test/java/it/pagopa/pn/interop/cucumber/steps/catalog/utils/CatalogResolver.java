package it.pagopa.pn.interop.cucumber.steps.catalog.utils;

import it.pagopa.interop.agreement.domain.EServiceDescriptor;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.m2m.common.utils.AbstractResolver;

import java.util.Arrays;
import java.util.List;
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

    public List<UUID> resolveEServiceIds(String eServiceIds) {
        return Arrays.stream(eServiceIds.split(","))
                .map(String::trim)
                .flatMap(token -> this.<List<UUID>>resolveOrParse(
                        token,
                        v -> List.of(UUID.fromString(v)),
                        () -> {
                            List<EServiceDescriptor> created = sharedStepsContext.getEServicesCommonContext().getCreatedEServicesIds();
                            if (created != null && !created.isEmpty()) {
                                return created.stream().map(EServiceDescriptor::getEServiceId).toList();
                            }
                            UUID singleId = sharedStepsContext.getEServicesCommonContext().getEserviceId();
                            return singleId == null ? List.of() : List.of(singleId);
                        },
                        null,
                        () -> List.of(UUID.randomUUID()),
                        List::of
                ).stream())
                .toList();
    }

    public UUID resolveDescriptorId(String descriptorId) {
        return resolveOrParse(
                descriptorId,
                UUID::fromString,
                () -> sharedStepsContext.getEServicesCommonContext().getDescriptorId(),
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
