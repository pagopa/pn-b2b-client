package it.pagopa.pn.interop.cucumber.steps.purposetemplate.model;

import it.pagopa.interop.generated.openapi.clients.bff.model.LinkableResources;
import it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.model.EServiceTemplates;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class LinkableResourcesContext {
    private LinkableResources lastLinkableResources;
    private LinkableResources referenceLinkableResources;
    private EServiceTemplates lastEServiceTemplates;
    private EServiceTemplates referenceEServiceTemplates;
    private List<String> referenceEServiceTemplateNames = new ArrayList<>();
    private List<String> referenceEServiceNames = new ArrayList<>();
    private List<String> referenceResourceNames = new ArrayList<>();
    private List<UUID> referencePublisherIds = new ArrayList<>();
    private List<UUID> referenceEServiceTemplateIds = new ArrayList<>();
    private List<UUID> referenceEServiceIds = new ArrayList<>();

    public void saveLastLinkableResourcesAsAReference() {
        referenceLinkableResources = lastLinkableResources;
    }

    public void saveLastLinkableEServiceTemplatesAsAReference() {
        referenceEServiceTemplates = lastEServiceTemplates;
    }
}
