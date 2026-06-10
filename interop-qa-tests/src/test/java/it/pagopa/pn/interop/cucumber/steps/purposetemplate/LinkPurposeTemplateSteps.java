package it.pagopa.pn.interop.cucumber.steps.purposetemplate;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.interop.authorization.service.utils.PollingService;
import it.pagopa.interop.common.IHttpExecutor;
import it.pagopa.interop.generated.openapi.clients.bff.model.*;
import it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.model.PurposeTemplateLinkEServiceTemplate;
import it.pagopa.interop.purpose.service.IPurposeApiClient;
import it.pagopa.interop.purpose.service.IPurposeTemplateClient;
import it.pagopa.interop.purpose.service.impl.PurposeTemplateClientImpl;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.purposetemplate.model.LinkableResourcesContext;
import it.pagopa.pn.interop.cucumber.steps.purposetemplate.model.PurposeTemplateContext;
import it.pagopa.pn.interop.cucumber.steps.purposetemplate.utils.PurposeTemplateResolver;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;

import java.util.*;
import java.util.stream.Collectors;

import static it.pagopa.pn.interop.cucumber.steps.purposetemplate.utils.LinkableResourcesHelper.*;

@Slf4j
public class LinkPurposeTemplateSteps {

    private final ClientTokenConfigurator clientTokenConfigurator;
    private final IPurposeTemplateClient purposeTemplateClient;
    private final IPurposeApiClient purposeApiClient;
    private final SharedStepsContext sharedStepsContext;
    private final IHttpExecutor httpCallExecutor;
    private final PollingService pollingService;

    private LinkableResourcesContext linkableResourcesContext;
    private PurposeTemplateContext purposeTemplateContext;
    private PurposeTemplateResolver resolver;

    public LinkPurposeTemplateSteps(SharedStepsContext sharedStepsContext,
                                ClientTokenConfigurator clientTokenConfigurator) {
        this.clientTokenConfigurator = clientTokenConfigurator;
        this.sharedStepsContext = sharedStepsContext;
        this.httpCallExecutor = sharedStepsContext.getHttpCallExecutor();
        this.pollingService = sharedStepsContext.getPollingService();
        this.purposeTemplateClient = clientTokenConfigurator.getPurposeTemplateClient();
        ((PurposeTemplateClientImpl) this.purposeTemplateClient).setHttpCallExecutor(this.httpCallExecutor);
        this.purposeApiClient = clientTokenConfigurator.getPurposeApiClient();
        this.purposeTemplateContext = new PurposeTemplateContext();
        this.linkableResourcesContext = new LinkableResourcesContext();
        this.resolver = new PurposeTemplateResolver(sharedStepsContext, purposeTemplateContext, sharedStepsContext.getIdentityService());
    }

    @Given("viene salvato {int} nome {resourceKind} di riferimento dalle risorse collegate")
    @Given("vengono salvati {int} nomi {resourceKind} di riferimento dalle risorse collegate")
    public void saveResourceNamesFromLinkableResources(int names, String resourceKind) {
        Assertions.assertTrue(
                linkableResourcesContext.getReferenceLinkableResources().getResults().size() >= names,
                "Not enough resources to save " + names + " requested names");
        int j = 0;
        for (int i = 0; i < names; i++) {
            if (resourceKind.equals("e-service concreto")) {
                if (linkableResourcesContext.getReferenceLinkableResources().getResults().get(j).getEservice() == null) {
                    i--;
                } else {
                    if (i == 0) linkableResourcesContext.getReferenceEServiceNames().clear();
                    String eServiceName = linkableResourcesContext.getReferenceLinkableResources().getResults().get(j).getEservice().getName();
                    linkableResourcesContext.getReferenceEServiceNames().add(eServiceName);
                    log.info("Added e-service name " + eServiceName + " to the reference");
                }
            } else if (resourceKind.equals("e-service template")) {
                if (linkableResourcesContext.getReferenceLinkableResources().getResults().get(j).getEserviceTemplate() == null) {
                    i--;
                } else {
                    if (i == 0) linkableResourcesContext.getReferenceEServiceTemplateNames().clear();
                    String eServiceTemplateName = linkableResourcesContext.getReferenceLinkableResources().getResults().get(j).getEserviceTemplate().getName();
                    linkableResourcesContext.getReferenceEServiceTemplateNames().add(eServiceTemplateName);
                    log.info("Added e-service template name " + eServiceTemplateName + " to the reference");
                }
            } else if (resourceKind.equals("risorsa")) {
                if (i == 0) linkableResourcesContext.getReferenceResourceNames().clear();
                String resourceName = getResourceName(linkableResourcesContext.getReferenceLinkableResources().getResults().get(j));
                linkableResourcesContext.getReferenceResourceNames().add(resourceName);
                log.info("Added resource name " + resourceName + " to the reference");
            }
            j++;
        }
    }

    @Given("vengono salvati {int} ID pubblicatore di riferimento dalle risorse collegate")
    public void savePublisherIDFromLinkableResources(int ids) {
        Assertions.assertTrue(
                linkableResourcesContext.getReferenceLinkableResources().getResults().size() >= ids,
                "Not enough resources to save " + ids + " requested IDs.");

        for (int i = 0; i < ids; i++) {
            if (i == 0) linkableResourcesContext.getReferencePublisherIds().clear();
            LinkableResource resource = linkableResourcesContext.getLastLinkableResources().getResults().get(i);
            UUID publisherId = null;
            if (getResourceKind(resource).equals("ESERVICE")) {
                publisherId = resource.getEservice().getProducer().getId();
            } else if (getResourceKind(resource).equals("ESERVICE_TEMPLATE")) {
                publisherId = resource.getEserviceTemplate().getCreator().getId();
            }
            if (linkableResourcesContext.getReferencePublisherIds().contains(publisherId)) {
                // Se l'ID pubblicatore è già presente non viene aggiunto,
                // saltando questo indice serve incrementare di 1 la ricerca degli ID
                ids++;
            } else {
                linkableResourcesContext.getReferencePublisherIds().add(publisherId);
                log.info("Added publisher ID " + publisherId + " to the reference");
            }
        }
    }

    @Given("viene salvato {int} ID {resourceKind} di riferimento dalle risorse collegate")
    @Given("vengono salvati {int} ID {resourceKind} di riferimento dalle risorse collegate")
    public void saveResourceIdFromLinkableResource(int ids, String resourceKind) {
        Assertions.assertTrue(
                linkableResourcesContext.getLastLinkableResources().getResults().size() >= ids,
                "Not enough resources to save " + ids + " requested IDs");
        resourceKind = switch (resourceKind) {
            case "e-service concreto" -> "ESERVICE";
            case "e-service template" -> "ESERVICE_TEMPLATE";
            default -> "RESOURCE";
        };
        for (int i = 0; i < ids; i++) {
            if (i == 0) {
                if ("ESERVICE".equals(resourceKind)) {
                    linkableResourcesContext.getReferenceEServiceIds().clear();

                } else if ("ESERVICE_TEMPLATE".equals(resourceKind)) {
                    linkableResourcesContext.getReferenceEServiceTemplateIds().clear();
                }
            }
            LinkableResource resource = linkableResourcesContext.getLastLinkableResources().getResults().get(i);
            String currentResourceKind = getResourceKind(resource);
            if (!currentResourceKind.equals(resourceKind)) {
                // Se l'ID non è della risorsa richiesta non viene aggiunto,
                // saltando questo indice serve incrementare di 1 la ricerca degli ID
                ids++;
                if (ids > linkableResourcesContext.getLastLinkableResources().getResults().size()) {
                    Assertions.assertTrue(true,
                            "Not enough resources of kind " + resourceKind + " found");
                }
            } else {
                UUID resourceId = null;
                if (currentResourceKind.equals("ESERVICE")) {
                    resourceId = resource.getEservice().getId();
                    linkableResourcesContext.getReferenceEServiceIds().add(resourceId);

                } else if (currentResourceKind.equals("ESERVICE_TEMPLATE")) {
                    resourceId = resource.getEserviceTemplate().getId();
                    linkableResourcesContext.getReferenceEServiceTemplateIds().add(resourceId);
                }
                log.info("Added " + resourceKind + " resource ID " + resourceId + " to the reference");
            }
        }
    }

    @Given("vengono salvate le risorse collegate in una lista di risorse di riferimento")
    public void saveLinkableResourcesAsAReference() {
        linkableResourcesContext.saveLastLinkableResourcesAsAReference();
    }

    @When("associa una risorsa a un template finalità")
    public void linkResourceToPurposeTemplate(DataTable dataTable) {
        LinkParameters params = getLinkParametersFromDataTable(dataTable, sharedStepsContext, linkableResourcesContext);
        tryToLinkResourceToPurposeTemplate(dataTable);
        Assertions.assertTrue(
                httpCallExecutor.getResponseStatus().is2xxSuccessful(),
                "Resource " + params.resourceKind() + " with ID " + params.resourceId() + " association failed."
        );
    }

    @When("prova ad associare una risorsa a un template finalità")
    public void tryToLinkResourceToPurposeTemplate(DataTable dataTable) {
        LinkParameters params = getLinkParametersFromDataTable(dataTable, sharedStepsContext, linkableResourcesContext);
        httpCallExecutor.performCall(() -> purposeTemplateClient.linkResourceToPurposeTemplate(params.purposeTemplateId(), params.resourceRequest()));
    }

    @When("disassocia una risorsa da un template finalità")
    public void unlinkResourceFromPurposeTemplate(DataTable dataTable) {
        LinkParameters params = getLinkParametersFromDataTable(dataTable, sharedStepsContext, linkableResourcesContext);
        pollingService.makePolling(
                () -> (httpCallExecutor.performCall(() -> purposeTemplateClient.unlinkResourceFromPurposeTemplate(params.purposeTemplateId(), params.resourceRequest()))),
                res -> (httpCallExecutor.getResponseStatus().is2xxSuccessful()),
                "Resource " + params.resourceKind() + " with ID " + params.resourceId() + " disassociation failed."
        );
    }

    @When("prova a disassociare una risorsa da un template finalità")
    public void tryToUnlinkResourceToPurposeTemplate(DataTable dataTable) {
        LinkParameters params = getLinkParametersFromDataTable(dataTable, sharedStepsContext, linkableResourcesContext);
        httpCallExecutor.performCall(() -> purposeTemplateClient.unlinkResourceFromPurposeTemplate(params.purposeTemplateId(), params.resourceRequest()));
    }

    @When("recupera le risorse collegate per suggerire il template finalità")
    public void getLinkableResourcesForPurposeTemplate(DataTable dataTable) {
        Map<String, String> data = dataTable.asMap(String.class, String.class);
        UUID purposeTemplateId = UUID.fromString(resolveDynamicData(
                data.get("id_template_finalita"), sharedStepsContext, linkableResourcesContext
        ));
        int offset = Integer.parseInt(data.getOrDefault("offset", "0"));
        int limit = Integer.parseInt(data.getOrDefault("limit", "50"));
        String q = resolveDynamicData(
                data.getOrDefault("filtro_nome_e_service", ""), sharedStepsContext, linkableResourcesContext
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
            httpCallExecutor.performCall(() -> purposeTemplateClient.getPurposeTemplateLinkableResources(purposeTemplateId, offset, limit, q, publisherIDs));
            if (httpCallExecutor.getResponseStatus().is2xxSuccessful()) {
                linkableResourcesContext.setLastLinkableResources((LinkableResources) httpCallExecutor.getResponse());
            }
        } else {
            pollingService.makePolling(
                    () -> (httpCallExecutor.performCall(() -> purposeTemplateClient.getPurposeTemplateLinkableResources(purposeTemplateId, offset, limit, q, publisherIDs))),
                    res -> ((LinkableResources)httpCallExecutor.getResponse()).getResults().stream().anyMatch(resource -> {
                        String resourceKind = getResourceKind(resource);
                        if ((resourceKind.equals("ESERVICE") && resource.getEservice().getId().toString().equals(expectedResourceId)) || (resourceKind.equals("ESERVICE_TEMPLATE") && resource.getEserviceTemplate().getId().toString().equals(expectedResourceId))) {
                            linkableResourcesContext.setLastLinkableResources((LinkableResources) httpCallExecutor.getResponse());
                            return true;
                        }
                        return false;
                    }),
                    "Expected resource ID in the list is missing: " + expectedResourceId
            );
        }
    }

    @Then("le risorse collegate al template finalità sono una lista vuota")
    public void checkLinkableResourcesAreEmpty(DataTable dataTable) {
        Map<String, String> data = dataTable.asMap(String.class, String.class);
        UUID purposeTemplateId = UUID.fromString(resolveDynamicData(
                data.get("id_template_finalita"), sharedStepsContext, linkableResourcesContext
        ));
        pollingService.makePolling(
                () -> (httpCallExecutor.performCall(() -> purposeTemplateClient.getPurposeTemplateLinkableResources(purposeTemplateId, 0, 10, "", List.of()))),
                res -> (httpCallExecutor.getResponseStatus().is2xxSuccessful()),
                "Failed retrieving resources for purpose template " + purposeTemplateId
        );
        Assertions.assertEquals(0, ((LinkableResources)httpCallExecutor.getResponse()).getResults().size(),
                "The retrieved resources are not an empty list");
    }

    @Then("le risorse recuperate presentano un {resourceKind}")
    public void checkLinkableResourcesHaveEServiceType(String eServiceKindName) {
        Assertions.assertTrue(
                foundResourceKindInLinkableResources(
                        eServiceKindName, linkableResourcesContext.getLastLinkableResources().getResults()
                ),
                "Resource type " + eServiceKindName + " not found."
        );
        log.info("Found resource: " + eServiceKindName);
    }

    @Then("le risorse recuperate non presentano un {resourceKind}")
    public void checkLinkableResourcesDoNotHaveEServiceType(String eServiceKindName) {
        Assertions.assertFalse(
                foundResourceKindInLinkableResources(
                        eServiceKindName, linkableResourcesContext.getLastLinkableResources().getResults()
                ),
                "Found resource type " + eServiceKindName
        );
        log.info("Not found resource: " + eServiceKindName);
    }

    @Then("le risorse collegate corrispondono ad una lista vuota")
    public void lastLinkableResourcesAreEmpty() {
        Assertions.assertEquals(
                200, httpCallExecutor.getResponseStatus().value(),
                "The last response is not successful");
        Assertions.assertTrue(
                linkableResourcesContext.getLastLinkableResources().getResults().isEmpty(),
                "The last linkable resources are not an empty list");
        log.info("The last response is 200 successful and it is an empty list");
    }

    @Then("la richiesta restituisce errore di template finalità non trovato")
    public void lastResponseIsPurposeTemplateNotFound() {
        Assertions.assertEquals(
                404, httpCallExecutor.getResponseStatus().value(),
                "The last response is different from Not Found error");
        Assertions.assertTrue(
                httpCallExecutor.getErrorMessage().contains("Purpose Template Not Found"),
                "Missing error message 'Purpose Template Not Found'");
        log.info("Found response " + httpCallExecutor.getErrorMessage());
    }

    @Then("le risorse collegate non vengono fornite causa richiesta non valida")
    public void lastResponseIsBadRequestError() {
        Assertions.assertEquals(
                400, httpCallExecutor.getResponseStatus().value(),
                "The last response is different from Bad Request error");
        Assertions.assertEquals(
                "Bad Request",
                httpCallExecutor.getResponseStatus().getReasonPhrase());
        log.info("Found response " + httpCallExecutor.getErrorMessage());
    }

    @Then("^la richiesta di (associazione|disassociazione) fallisce per errore di conflitto$")
    public void lastResponseIsConflictError(String operation) {
        String expectedMessage = ("associazione".equals(operation)) ?
                "Association between e-service template and purpose template already exists":
                "Association between e-service template and purpose template does not exist";
        Assertions.assertEquals(
                409, httpCallExecutor.getResponseStatus().value(),
                "The last response is different from Conflict error");
        Assertions.assertTrue(
                httpCallExecutor.getErrorMessage().contains(expectedMessage),
                "Missing error message '" + expectedMessage + "' and found: " + httpCallExecutor.getErrorMessage());
        log.info("Found response " + httpCallExecutor.getErrorMessage());
    }

    @Then("le risorse collegate corrispondono alla lista di risorse di riferimento ignorando il primo risultato")
    public void lastLinkableResourcesMatchReferenceResources() {
        lastLinkableResourcesMatchReferenceResourcesWithInitialExclusion(1);
    }

    @Then("le risorse collegate corrispondono alla lista di risorse di riferimento ignorando i primi {int} risultati")
    public void lastLinkableResourcesMatchReferenceResourcesWithInitialExclusion(int excludedResults) {
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

    @Then("le risorse collegate corrispondono alla lista di risorse di riferimento solo per il primo risultato")
    public void lastLinkableResourcesMatchReferenceResourcesAtBeginning() {
        lastLinkableResourcesMatchReferenceResourcesAtBeginning(1);
    }

    @Then("le risorse collegate corrispondono alla lista di risorse di riferimento solo per i primi {int} risultati")
    public void lastLinkableResourcesMatchReferenceResourcesAtBeginning(int includedResults) {
        LinkableResource referenceResource = null, resource = null;
        boolean foundDifference = false;
        int j = 0;

        Assertions.assertTrue(
                linkableResourcesContext.getLastLinkableResources().getResults().size() >= includedResults,
                "Available results are lesser than the results to check");

        for (int i = 0; i < includedResults; i++) {
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

    @Then("le risorse collegate corrispondono alla lista di risorse di riferimento aventi:")
    public void lastLinkableResourcesMatchReferenceResourcesHaving(DataTable dataTable) {
        LinkableResource referenceResource, currentResource;
        Map<String, String> data = dataTable.asMap(String.class, String.class);
        String eServiceName = resolveDynamicData(
                data.getOrDefault("nome_risorsa", ""), sharedStepsContext, linkableResourcesContext
        );
        String partOfName = data.getOrDefault("parte_del_nome", "");
        String publisherIdString = resolveDynamicData(
                data.getOrDefault("id_pubblicatore", ""), sharedStepsContext, linkableResourcesContext
        );
        String currentResourceName, referenceResourceName;
        UUID currentResourcePublisherId, referenceResourcePublisherId;
        boolean mismatch = false;
        int j = 0;

        Assertions.assertTrue(
                !linkableResourcesContext.getLastLinkableResources().getResults().isEmpty(),
                "There is no resource to check!"
        );
        Assertions.assertTrue(
                !linkableResourcesContext.getReferenceLinkableResources().getResults().isEmpty(),
                "There is no reference resource to compare with!"
        );

        for (int i = 0; i < linkableResourcesContext.getLastLinkableResources().getResults().size(); i++) {
            currentResource = linkableResourcesContext.getLastLinkableResources().getResults().get(i);
            referenceResource = linkableResourcesContext.getReferenceLinkableResources().getResults().get(j);
            currentResourceName = getResourceName(currentResource);
            referenceResourceName = getResourceName(referenceResource);

            if (!eServiceName.isEmpty()) {
                Assertions.assertEquals(currentResourceName, eServiceName);
                if (!referenceResourceName.equals(eServiceName)) {
                    j++; i--; continue;
                }
            }
            if (!partOfName.isEmpty()) {
                Assertions.assertTrue(
                        currentResourceName.contains(partOfName),
                        "Current resource name " + currentResourceName + " does not contains '" + partOfName + "'."
                );
                if (!referenceResourceName.contains(partOfName)) {
                    j++; i--; continue;
                }
            }
            if (!publisherIdString.isEmpty()) {
                currentResourcePublisherId = getPublisherId(currentResource);
                referenceResourcePublisherId = getPublisherId(referenceResource);
                Assertions.assertTrue(publisherIdString.contains(currentResourcePublisherId.toString()));
                if (!publisherIdString.contains(referenceResourcePublisherId.toString())) {
                    j++; i--; continue;
                }
            }
            if (!doLinkableResourcesMatch(referenceResource, currentResource)) {
                mismatch = true;
                break;
            } else {
                log.info("Checked resource " + getResourceKind(currentResource) + " " + getResourceName(currentResource));
                j++;
            }
        }
        Assertions.assertFalse(mismatch, "Applying the filter there is a resource not matching the reference resource.");
    }

    private String getResourceName(LinkableResource resource) {
        return (getResourceKind(resource).equals("ESERVICE_TEMPLATE")) ?
                resource.getEserviceTemplate().getName() : resource.getEservice().getName();
    }

    private UUID getPublisherId(LinkableResource resource) {
        return (getResourceKind(resource).equals("ESERVICE_TEMPLATE")) ?
                resource.getEserviceTemplate().getCreator().getId() : resource.getEservice().getProducer().getId();
    }
}
