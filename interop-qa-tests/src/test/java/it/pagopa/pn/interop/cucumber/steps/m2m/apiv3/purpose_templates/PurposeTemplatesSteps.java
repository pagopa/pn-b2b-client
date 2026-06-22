package it.pagopa.pn.interop.cucumber.steps.m2m.apiv3.purpose_templates;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.interop.authorization.service.utils.PollingService;
import it.pagopa.interop.common.IHttpExecutor;
import it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.model.EServiceTemplate;
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

    @Given("viene salvato {int} nome di riferimento dagli e-service template collegati")
    @Given("vengono salvati {int} nomi di riferimento dagli e-service template collegati")
    public void saveNamesFromLinkableEServiceTemplates(int names) {
        Assertions.assertTrue(
                linkableResourcesContext.getReferenceEServiceTemplates().getResults().size() >= names,
                "Not enough e-service templates to save " + names + " requested names");

        for (int i = 0; i < names; i++) {
            if (i == 0) linkableResourcesContext.getReferenceEServiceTemplateNames().clear();
            String eServiceTemplateName = linkableResourcesContext.getReferenceEServiceTemplates().getResults().get(i).getName();
            linkableResourcesContext.getReferenceEServiceTemplateNames().add(eServiceTemplateName);
            log.info("Added e-service template name " + eServiceTemplateName + " to the reference");
        }
    }

    @Given("vengono salvati {int} ID creatore di riferimento dagli e-service template collegati")
    public void saveCreatorIDFromLinkableEServiceTemplates(int ids) {
        Assertions.assertTrue(
                linkableResourcesContext.getReferenceEServiceTemplates().getResults().size() >= ids,
                "Not enough e-service templates  to save " + ids + " requested IDs.");

        for (int i = 0; i < ids; i++) {
            if (i == 0) linkableResourcesContext.getReferencePublisherIds().clear();
            EServiceTemplate resource = linkableResourcesContext.getLastEServiceTemplates().getResults().get(i);
            UUID creatorId = resource.getCreatorId();
            if (linkableResourcesContext.getReferencePublisherIds().contains(creatorId)) {
                // Se l'ID pubblicatore è già presente non viene aggiunto,
                // saltando questo indice serve incrementare di 1 la ricerca degli ID
                ids++;
            } else {
                linkableResourcesContext.getReferencePublisherIds().add(creatorId);
                log.info("Added creator ID " + creatorId + " to the reference");
            }
        }
    }

    @Given("viene salvato {int} ID e-service template di riferimento dagli e-service template collegati")
    @Given("vengono salvati {int} ID e-service template di riferimento dagli e-service template collegati")
    public void saveEServiceTemplateIdFromLinkableEServiceTemplates(int ids) {
        Assertions.assertTrue(
                linkableResourcesContext.getLastEServiceTemplates().getResults().size() >= ids,
                "Not enough e-service templates to save " + ids + " requested IDs");

        for (int i = 0; i < ids; i++) {
            if (i == 0) linkableResourcesContext.getReferenceEServiceTemplateIds().clear();
            EServiceTemplate resource = linkableResourcesContext.getLastEServiceTemplates().getResults().get(i);
            linkableResourcesContext.getReferenceEServiceTemplateIds().add(resource.getId());
            log.info("Added ESERVICE_TEMPLATE resource ID " + resource.getId() + " to the reference");
        }
    }

    @When("associa un e-service template a un template finalità")
    public void linkEServiceTemplateToPurposeTemplate(DataTable dataTable) {
        LinkParameters params = getLinkParametersFromDataTable(dataTable, sharedStepsContext, linkableResourcesContext);
        httpCallExecutor.performCall(() -> m2mv3PurposeTemplateClient.linkEServiceTemplateToPurposeTemplate(
                params.purposeTemplateId(),
                params.eServiceTemplateLink())
        );
        Assertions.assertTrue(httpCallExecutor.getResponseStatus().is2xxSuccessful(), "E-service template association failed.");
    }

    @When("prova ad associare un e-service template a un template finalità")
    public void tryToLinkEServiceTemplateToPurposeTemplate(DataTable dataTable) {
        LinkParameters params = getLinkParametersFromDataTable(dataTable, sharedStepsContext, linkableResourcesContext);
        httpCallExecutor.performCall(() -> m2mv3PurposeTemplateClient.linkEServiceTemplateToPurposeTemplate(
                params.purposeTemplateId(),
                params.eServiceTemplateLink())
        );
    }

    @When("disassocia un e-service template da un template finalità")
    public void unlinkEServiceTemplateFromPurposeTemplate(DataTable dataTable) {
        LinkParameters params = getLinkParametersFromDataTable(dataTable, sharedStepsContext, linkableResourcesContext);
        httpCallExecutor.performCall(() -> m2mv3PurposeTemplateClient.unlinkEServiceTemplateFromPurposeTemplate(
                params.purposeTemplateId(),
                params.eServiceTemplateLink().getEserviceTemplateId())
        );
        Assertions.assertTrue(httpCallExecutor.getResponseStatus().is2xxSuccessful(), "E-service template disassociation failed.");
    }

    @When("prova a disassociare un e-service template da un template finalità")
    public void tryToUnlinkEServiceTemplateToPurposeTemplate(DataTable dataTable) {
        LinkParameters params = getLinkParametersFromDataTable(dataTable, sharedStepsContext, linkableResourcesContext);
        httpCallExecutor.performCall(() -> m2mv3PurposeTemplateClient.unlinkEServiceTemplateFromPurposeTemplate(
                params.purposeTemplateId(),
                params.eServiceTemplateLink().getEserviceTemplateId())
        );
    }

    @When("recupera gli e-service template collegati per suggerire il template finalità")
    public void getLinkableEServiceTemplateForPurposeTemplate(DataTable dataTable) {
        Map<String, String> data = dataTable.asMap(String.class, String.class);
        UUID purposeTemplateId = UUID.fromString(resolveDynamicData(
                data.get("id_template_finalita"), sharedStepsContext, linkableResourcesContext
        ));
        int offset = Integer.parseInt(data.getOrDefault("offset", "0"));
        int limit = Integer.parseInt(data.getOrDefault("limit", "50"));
        String eserviceTemplateName = resolveDynamicData(
                data.getOrDefault("filtro_e_service_template_name", ""), sharedStepsContext, linkableResourcesContext
        );
        String publisherIDsCommaSeparated = data.getOrDefault("filtro_id_pubblicatore", "");
        final String expectedResourceId = resolveDynamicData(
                data.getOrDefault("id_risorsa_attesa", ""), sharedStepsContext, linkableResourcesContext
        );
        List<UUID> publisherIDs = (publisherIDsCommaSeparated.isEmpty()) ? List.of() :
                Arrays.stream(publisherIDsCommaSeparated.split(","))
                        .map(id -> resolveDynamicData(id, sharedStepsContext, linkableResourcesContext))
                        .map(UUID::fromString)
                        .collect(Collectors.toList());

        if (expectedResourceId.isEmpty()) {
            httpCallExecutor.performCall(() -> m2mv3PurposeTemplateClient.getPurposeTemplateLinkableEServiceTemplate(purposeTemplateId, offset, limit, publisherIDs, eserviceTemplateName));
            if (httpCallExecutor.getResponseStatus().is2xxSuccessful()) {
                linkableResourcesContext.setLastEServiceTemplates((EServiceTemplates)((ResponseEntity)httpCallExecutor.getResponse()).getBody());
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

    @Then("gli e-service template collegati al template finalità sono una lista vuota")
    public void checkLinkableEServiceTemplatesAreEmpty(DataTable dataTable) {
        Map<String, String> data = dataTable.asMap(String.class, String.class);
        UUID purposeTemplateId = UUID.fromString(resolveDynamicData(
                data.get("id_template_finalita"), sharedStepsContext, linkableResourcesContext
        ));
        pollingService.makePolling(
                () -> (httpCallExecutor.performCall(() -> m2mv3PurposeTemplateClient.getPurposeTemplateLinkableEServiceTemplate(purposeTemplateId, 0, 10, List.of(), ""))),
                res -> (httpCallExecutor.getResponseStatus().is2xxSuccessful()),
                "Failed retrieving e-service templates for purpose template " + purposeTemplateId
        );
        Assertions.assertEquals(0, ((EServiceTemplates)((ResponseEntity)httpCallExecutor.getResponse()).getBody()).getResults().size(),
                "The retrieved e-service templates are not an empty list");
    }

    @Then("gli e-service template collegati corrispondono alla lista di risorse di riferimento ignorando il primo risultato")
    public void lastLinkableEServiceTemplatesMatchReferenceResources() {
        lastLinkableEServiceTemplatesMatchReferenceResourcesWithInitialExclusion(1);
    }

    @Then("gli e-service template collegati corrispondono alla lista di risorse di riferimento ignorando i primi {int} risultati")
    public void lastLinkableEServiceTemplatesMatchReferenceResourcesWithInitialExclusion(int excludedResults) {
        EServiceTemplate referenceResource = null, resource = null;
        boolean foundDifference = false;
        Assertions.assertTrue(
                linkableResourcesContext.getReferenceEServiceTemplates().getResults().size() > excludedResults,
                "There is no result to check!");

        int j = 0;
        for (int i = excludedResults; i < linkableResourcesContext.getReferenceEServiceTemplates().getResults().size(); i++) {
            referenceResource = linkableResourcesContext.getReferenceEServiceTemplates().getResults().get(i);
            resource = linkableResourcesContext.getLastEServiceTemplates().getResults().get(j);
            if (!doLinkableEServiceTemplatesMatch(referenceResource, resource)) {
                foundDifference = true;
                break;
            }
            j++;
        }
        assertLinkableEServiceTemplatesMatch(foundDifference, referenceResource, resource);
    }

    @Then("gli e-service template collegati corrispondono alla lista di risorse di riferimento solo per il primo risultato")
    public void lastLinkableEServiceTemplatesMatchReferenceResourcesAtBeginning() {
        lastLinkableEServiceTemplatesReferenceResourcesAtBeginning(1);
    }

    @Then("gli e-service template collegati corrispondono alla lista di risorse di riferimento solo per i primi {int} risultati")
    public void lastLinkableEServiceTemplatesReferenceResourcesAtBeginning(int includedResults) {
        EServiceTemplate referenceResource = null, resource = null;
        boolean foundDifference = false;
        Assertions.assertTrue(
                linkableResourcesContext.getLastEServiceTemplates().getResults().size() >= includedResults,
                "Available results are lesser than the results to check");

        int j = 0;
        for (int i = 0; i < includedResults; i++) {
            referenceResource = linkableResourcesContext.getReferenceEServiceTemplates().getResults().get(i);
            resource = linkableResourcesContext.getLastEServiceTemplates().getResults().get(j);
            if (!doLinkableEServiceTemplatesMatch(referenceResource, resource)) {
                foundDifference = true;
                break;
            }
            j++;
        }
        assertLinkableEServiceTemplatesMatch(foundDifference, referenceResource, resource);
    }

    @Then("gli e-service template collegati corrispondono alla lista di risorse di riferimento aventi:")
    public void lastLinkableEServiceTemplatesMatchReferenceResourcesHaving(DataTable dataTable) {
        EServiceTemplate referenceResource, currentResource;
        Map<String, String> data = dataTable.asMap(String.class, String.class);
        String eServiceName = resolveDynamicData(
                data.getOrDefault("nome_risorsa", ""), sharedStepsContext, linkableResourcesContext
        );
        String publisherIdString = resolveDynamicData(
                data.getOrDefault("id_pubblicatore", ""), sharedStepsContext, linkableResourcesContext
        );
        Assertions.assertTrue(
                !linkableResourcesContext.getLastEServiceTemplates().getResults().isEmpty(),
                "There is no e-service template to check!"
        );
        Assertions.assertTrue(
                !linkableResourcesContext.getReferenceEServiceTemplates().getResults().isEmpty(),
                "There is no reference e-service template to compare with!"
        );
        boolean mismatch = false;
        int j = 0;

        for (int i = 0; i < linkableResourcesContext.getLastEServiceTemplates().getResults().size(); i++) {
            currentResource = linkableResourcesContext.getLastEServiceTemplates().getResults().get(i);
            referenceResource = linkableResourcesContext.getReferenceEServiceTemplates().getResults().get(j);

            if (!eServiceName.isEmpty()) {
                Assertions.assertEquals(currentResource.getName(), eServiceName);
                if (!currentResource.getName().equals(eServiceName)) {
                    j++; i--; continue;
                }
            }
            if (!publisherIdString.isEmpty()) {
                Assertions.assertTrue(publisherIdString.contains(currentResource.getCreatorId().toString()));
                if (!publisherIdString.contains(referenceResource.getCreatorId().toString())) {
                    j++; i--; continue;
                }
            }
            if (!doLinkableEServiceTemplatesMatch(referenceResource, currentResource)) {
                mismatch = true;
                break;
            } else {
                log.info("Checked resource E-SERVICE TEMPLATE " + currentResource.getName());
                j++;
            }
        }
        Assertions.assertFalse(
                mismatch,
                "Applying the filter there is an e-service template not matching the reference resource"
        );
    }

    @Then("gli e-service template collegati corrispondono ad una lista vuota")
    public void lastLinkableEServiceTemplatesAreEmpty() {
        Assertions.assertEquals(
                200, httpCallExecutor.getResponseStatus().value(),
                "The last response is not successful");
        Assertions.assertTrue(
                linkableResourcesContext.getLastEServiceTemplates().getResults().isEmpty(),
                "The last linkable e-service templates are not an empty list");
        log.info("The last response is 200 successful and it is an empty list");
    }
}
