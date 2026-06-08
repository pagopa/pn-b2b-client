package it.pagopa.pn.interop.cucumber.steps.m2m.apiv3.purpose_templates;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.interop.authorization.service.utils.PollingService;
import it.pagopa.interop.common.IHttpExecutor;
import it.pagopa.interop.generated.openapi.clients.bff.model.LinkableResource;
import it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.model.EServiceTemplates;
import it.pagopa.interop.purpose.service.IM2MV3PurposeTemplateClient;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.m2m.apiv3.purposes.resolver.PurposeResolver;
import it.pagopa.pn.interop.cucumber.steps.purposetemplate.model.LinkableResourcesContext;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static it.pagopa.pn.interop.cucumber.steps.purposetemplate.utils.LinkableResourcesHelper.*;


@Slf4j
public class PurposeTemplatesSteps {
    private final IM2MV3PurposeTemplateClient m2mv3PurposeTemplateClient;
    private final SharedStepsContext sharedStepsContext;
    private final IHttpExecutor httpCallExecutor;
    private final PurposeResolver purposesResolver;
    private final PollingService pollingService;

    private LinkableResourcesContext linkableResourcesContext;

    public PurposeTemplatesSteps(ClientTokenConfigurator clientTokenConfigurator, SharedStepsContext sharedStepsContext) {
        this.m2mv3PurposeTemplateClient = clientTokenConfigurator.getM2mV3PurposeTemplateClient();
        this.sharedStepsContext = sharedStepsContext;
        this.httpCallExecutor = sharedStepsContext.getHttpCallExecutor();
        this.pollingService = sharedStepsContext.getPollingService();
        this.purposesResolver = new PurposeResolver(sharedStepsContext);
        this.linkableResourcesContext = new LinkableResourcesContext();
    }

    @Given("vengono salvati gli e-service template in una lista di risorse di riferimento")
    public void saveLinkableEServiceTemplatesAsAReference() {
        linkableResourcesContext.saveLastLinkableEServiceTemplatesAsAReference();
    }

//    @When("associa un e-service template a un template finalità")
//    public void linkEServiceTemplateToPurposeTemplate(DataTable dataTable) {
//        LinkParameters params = getLinkParametersFromDataTable(dataTable);
//        httpCallExecutor.performCall(() -> m2mv3PurposeTemplateClient.linkEServiceTemplateToPurposeTemplate(params.purposeTemplateId(), params.eServiceTemplateLink()));
//        Assertions.assertTrue(httpCallExecutor.getResponseStatus().is2xxSuccessful(), "E-service template association failed.");
//    }

    @When("recupera gli e-service template collegati per suggerire il template finalità")
    public void getLinkableEServiceTemplateForPurposeTemplate(DataTable dataTable) {
        Map<String, String> data = dataTable.asMap(String.class, String.class);
        UUID purposeTemplateId = UUID.fromString(resolveDynamicData(
                data.get("id_template_finalita"), sharedStepsContext, linkableResourcesContext
        ));
        int offset = Integer.parseInt(data.getOrDefault("offset", "0"));
        int limit = Integer.parseInt(data.getOrDefault("limit", "50"));
        String eserviceTemplateName = data.getOrDefault("filtro_e_service_template_name", "");
        String publisherIDsCommaSeparated = data.getOrDefault("id_pubblicatore", "");
        final String expectedResourceId = resolveDynamicData(
                data.getOrDefault("id_risorsa_attesa", ""), sharedStepsContext, linkableResourcesContext
        );
        List<UUID> publisherIDs;
        if (publisherIDsCommaSeparated.isEmpty()) {
            publisherIDs = List.of();
        } else {
            publisherIDs = Arrays.stream(publisherIDsCommaSeparated.split(","))
                    .map(id -> resolveDynamicData(id, sharedStepsContext, linkableResourcesContext))
                    .map(UUID::fromString)
                    .collect(Collectors.toList());
        }
        if (expectedResourceId.isEmpty()) {
            httpCallExecutor.performCall(() -> m2mv3PurposeTemplateClient.getPurposeTemplateLinkableEServiceTemplate(purposeTemplateId, offset, limit, publisherIDs, eserviceTemplateName));
            if (httpCallExecutor.getResponseStatus().is2xxSuccessful()) {
                linkableResourcesContext.setLastEServiceTemplates((EServiceTemplates)httpCallExecutor.getResponse());
            }
        } else {
            pollingService.makePolling(
                    () -> (httpCallExecutor.performCall(() -> m2mv3PurposeTemplateClient.getPurposeTemplateLinkableEServiceTemplate(purposeTemplateId, offset, limit, publisherIDs, eserviceTemplateName))),
                    res -> (((EServiceTemplates)((ResponseEntity)httpCallExecutor.getResponse()).getBody()).getResults().stream().anyMatch(resource -> {
                        if (resource.getId().toString().equals(expectedResourceId)) {
                            linkableResourcesContext.setLastEServiceTemplates((EServiceTemplates)((ResponseEntity)httpCallExecutor.getResponse()).getBody());
                            return true;
                        }
                        return false;
                    })),
                    "Expected resource ID in the list is missing: " + expectedResourceId
            );
        }
    }

    @Then("gli e-service template collegati corrispondono alla lista di risorse di riferimento ignorando il primo risultato")
    public void lastLinkableEServiceTemplatesMatchReferenceResources() {
        lastLinkableEServiceTemplatesMatchReferenceResourcesWithInitialExclusion(1);
    }

    @Then("gli e-service template collegati corrispondono alla lista di risorse di riferimento ignorando i primi {int} risultati")
    public void lastLinkableEServiceTemplatesMatchReferenceResourcesWithInitialExclusion(int excludedResults) {
        LinkableResource referenceResource = null, resource = null;
        boolean foundDifference = false;
        int j = 0;

        Assertions.assertTrue(
                linkableResourcesContext.getReferenceLinkableResources().getResults().size() > excludedResults,
                "There is no result to check!");

        for (int i = excludedResults; i < linkableResourcesContext.getReferenceLinkableResources().getResults().size(); i++) {
            referenceResource = linkableResourcesContext.getReferenceLinkableResources().getResults().get(i);
            resource = linkableResourcesContext.getLastLinkableResources().getResults().get(j);
            if (!doLinkableResourcesMatch(referenceResource, resource)) {
                foundDifference = true;
                break;
            }
            j++;
        }
        assertLinkableResourcesMatch(foundDifference, referenceResource, resource);
    }
}
