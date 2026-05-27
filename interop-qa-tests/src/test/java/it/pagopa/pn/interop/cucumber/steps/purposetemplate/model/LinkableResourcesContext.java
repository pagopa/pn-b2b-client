package it.pagopa.pn.interop.cucumber.steps.purposetemplate.model;

import it.pagopa.interop.generated.openapi.clients.bff.model.LinkableResources;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.PurposeTemplateState;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class LinkableResourcesContext {
    private LinkableResources lastLinkableResources;
    private LinkableResources referenceLinkableResources;
    private List<String> referenceEServiceTemplateNames;
    private List<String> referenceEServiceNames;
    private List<String> referenceResourceNames;
    private List<UUID> referencePublisherIds;
    private List<UUID> referenceEServiceTemplateIds;
    private List<UUID> referenceEServiceIds;

    public void saveLastLinkableResourcesAsAReference() {
        referenceLinkableResources = lastLinkableResources;
    }
}
