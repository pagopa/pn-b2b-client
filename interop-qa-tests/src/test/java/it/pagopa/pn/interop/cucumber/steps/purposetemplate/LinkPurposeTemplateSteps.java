package it.pagopa.pn.interop.cucumber.steps.purposetemplate;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.interop.authorization.service.utils.PollingService;
import it.pagopa.interop.common.IHttpExecutor;
import it.pagopa.interop.generated.openapi.clients.bff.model.*;
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

import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

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

    @Given("viene salvato {int} nome {resourceKind} di riferimento dalle risorse collegabili")
    @Given("vengono salvati {int} nomi {resourceKind} di riferimento dalle risorse collegabili")
    public void saveResourceNamesFromLinkableResource(int names, String resourceKind) {
        
        Assertions.assertTrue(
                names >= linkableResourcesContext.getLastLinkableResources().getResults().size(),
                "Non ci sono abbastanza risorse per salvare " + names + " nomi richiesti.");

        for (int i = 0; i < names; i++) {
            if (resourceKind.equals("e-service concreto")) {
                if (i == 0) linkableResourcesContext.getReferenceEServiceNames().clear();
                linkableResourcesContext.getReferenceEServiceNames().add(linkableResourcesContext.getLastLinkableResources().getResults().get(i).getEservice().getName());

            } else if (resourceKind.equals("e-service template")) {
                if (i == 0) linkableResourcesContext.getReferenceEServiceTemplateNames().clear();
                linkableResourcesContext.getReferenceEServiceTemplateNames().add(linkableResourcesContext.getLastLinkableResources().getResults().get(i).getEserviceTemplate().getName());

            } else if (resourceKind.equals("risorsa")) {
                if (i == 0) linkableResourcesContext.getReferenceEServiceTemplateNames().clear();
                linkableResourcesContext.getReferenceResourceNames().add(getResourceName(linkableResourcesContext.getLastLinkableResources().getResults().get(i)));
            }
        }
    }

    @Given("vengono salvati {int} ID pubblicatore di riferimento dalle risorse collegabili")
    public void savePublisherIDFromLinkableResource(int ids, String resourceKind) {
        Assertions.assertTrue(
                ids >= linkableResourcesContext.getLastLinkableResources().getResults().size(),
                "Not enough resources to save " + ids + " requested IDs.");

        for (int i = 0; i < ids; i++) {
            if (i == 0) linkableResourcesContext.getReferencePublisherIds().clear();
            if (linkableResourcesContext.getLastLinkableResources().getResults().get(i).getResourceKind().getValue().equals("ESERVICE")) {
                linkableResourcesContext.getReferencePublisherIds().add(linkableResourcesContext.getLastLinkableResources().getResults().get(i).getEservice().getProducer().getId());

            } else if (linkableResourcesContext.getLastLinkableResources().getResults().get(i).getResourceKind().getValue().equals("ESERVICE_TEMPLATE")) {
                linkableResourcesContext.getReferencePublisherIds().add(linkableResourcesContext.getLastLinkableResources().getResults().get(i).getEserviceTemplate().getCreator().getId());
            }
        }
    }

    @Given("viene salvato {int} ID {resourceKind} di riferimento dalle risorse collegabili")
    public void saveResourceIDFromLinkableResource(int ids, String resourceKind) {
        Assertions.assertTrue(
                ids >= linkableResourcesContext.getLastLinkableResources().getResults().size(),
                "Non ci sono abbastanza risorse per salvare " + ids + " requested IDs.");

//        for (int i = 0; i < ids; i++) {
//            if (resourceKind.equals("e-service concreto")) {
//                if (i == 0) referenceEServiceIds.clear();
//                linkableResourcesContext.getReferencePublisherIds().add(linkableResourcesContext.getLastLinkableResources().getResults().get(i).getEservice().getId());
//            // TODO completare
//        }
    }

    @Given("vengono salvate le risorse collegabili in una lista di risorse di riferimento")
    public void saveLinkableResourcesAsAReference() {
        linkableResourcesContext.saveLastLinkableResourcesAsAReference();
    }

    @When("recupera le risorse collegabili suggerite per un template finalità")
    public void getLinkableResourcesForPurposeTemplate(DataTable dataTable) {
        Map<String, String> data = dataTable.asMap(String.class, String.class);
        UUID purposeTemplateId = UUID.fromString(resolveDynamicData(data.get("purpose_template_id")));
        int offset = Integer.parseInt(data.get("offset"));
        int limit = Integer.parseInt(data.get("limit"));
        String q = data.getOrDefault("filtro_nome_eservice", "");
        String publisherIDsCommaSeparated = data.getOrDefault("id_pubblicatore", "");
        List<UUID> publisherIDs;
        if (publisherIDsCommaSeparated.isEmpty()) {
            publisherIDs = List.of();
        } else {
            publisherIDs = Arrays.stream(publisherIDsCommaSeparated.split(","))
                    .map(UUID::fromString)
                    .collect(Collectors.toList());
        }
        httpCallExecutor.performCall(() -> purposeTemplateClient.getPurposeTemplateLinkableResources(purposeTemplateId, offset, limit, q, publisherIDs));
        if (httpCallExecutor.getResponseStatus().is2xxSuccessful()) {
            linkableResourcesContext.setLastLinkableResources((LinkableResources) httpCallExecutor.getResponse());
        }
    }

    @Then("le risorse collegabili presentano un {resourceKind}")
    public void checkLinkableResourcesHaveEServiceType(String eServiceKindName) {
        eServiceKindName = (eServiceKindName.equals("e-service template")) ? "ESERVICE_TEMPLATE" : "ESERVICE";
        boolean foundRequestedResourceKind = false;
        for (int i = 0; i < linkableResourcesContext.getLastLinkableResources().getResults().size(); i++) {
            if (linkableResourcesContext.getLastLinkableResources().getResults().get(i).get .getValue().equals(eServiceKindName)) {
                foundRequestedResourceKind = true;
                break;
            }
        }
        Assertions.assertTrue(foundRequestedResourceKind, "Resource type " + eServiceKindName + " not found.");
    }

    @Then("le risorse collegabili corrispondono ad una lista vuota")
    public void lastLinkableResourcesAreEmpty() {
        Assertions.assertEquals(
                200, httpCallExecutor.getResponseStatus().value(),
                "The last response is not successful.");
        Assertions.assertTrue(
                linkableResourcesContext.getLastLinkableResources().getResults().isEmpty(),
                "The last linkable resources are not an empty list.");
    }

    @Then("la richiesta di risorse collegabili restituisce errore di template finalità non trovato")
    public void lastResponseIsPurposeTemplateNotFound() {
        Assertions.assertEquals(
                404, httpCallExecutor.getResponseStatus().value(),
                "The last response is different from Not Found error.");
        // TODO verificare l'errore più specifico Purpose Template Not Found nel messaggio
    }
    @Then("le risorse collegabili non vengono fornite causa richiesta non valida")
    public void lastResponseIsBadRequestError() {
        Assertions.assertEquals(
                400, httpCallExecutor.getResponseStatus().value(),
                "The last response is different from Bad Request error.");
        // TODO verificare l'errore più specifico Purpose Template Not Found nel messaggio
    }
    @Then("le risorse collegabili corrispondono alla lista di risorse di riferimento ignorando il primo risultato")
    public void lastLinkableResourcesMatchReferenceResources() {
        lastLinkableResourcesMatchReferenceResourcesWithInitialExclusion(1);
    }

    @Then("le risorse collegabili corrispondono alla lista di risorse di riferimento ignorando i primi {int} risultati")
    public void lastLinkableResourcesMatchReferenceResourcesWithInitialExclusion(int excludedResults) {
        LinkableResource referenceResource = null, resource = null;
        boolean foundDifference = false;
        int j = 0;

        Assertions.assertTrue(
                linkableResourcesContext.getReferenceLinkableResources().getResults().size() > excludedResults,
                "Non c'è nemmeno 1 risultato da controllare.");

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

    @Then("le risorse collegabili corrispondono alla lista di risorse di riferimento solo per il primo risultato")
    public void lastLinkableResourcesMatchReferenceResourcesAtBeginning() {
        lastLinkableResourcesMatchReferenceResourcesAtBeginning(1);
    }

    @Then("le risorse collegabili corrispondono alla lista di risorse di riferimento solo per i primi {int} risultati")
    public void lastLinkableResourcesMatchReferenceResourcesAtBeginning(int includedResults) {
        LinkableResource referenceResource = null, resource = null;
        boolean foundDifference = false;
        int j = 0;

        Assertions.assertTrue(
                includedResults >= linkableResourcesContext.getReferenceLinkableResources().getResults().size(),
                "I risultati disponibili sono numericamente inferiori ai risultati da controllare.");

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

    @Then("le risorse collegabili corrispondono alla lista di risorse di riferimento aventi:")
    public void lastLinkableResourcesMatchReferenceResourcesHaving(DataTable dataTable) {
        LinkableResource referenceResource = null, resource = null;
        Map<String, String> data = dataTable.asMap(String.class, String.class);
        String eServiceName = data.getOrDefault("nome_eservice", "");
        String partOfName = data.getOrDefault("parte_del_nome", "");
        String publisherIdString = data.getOrDefault("id_pubblicatore", "");
        UUID publisherId = (publisherIdString.isEmpty()) ? null : UUID.fromString(publisherIdString);
        String currentResourceName, referenceResourceName;
        UUID currentResourcePublisherId, referenceResourcePublisherId;
        boolean foundDifference = false;
        int j = 0;

        for (int i = 0; i < linkableResourcesContext.getReferenceLinkableResources().getResults().size(); i++) {
            resource = linkableResourcesContext.getLastLinkableResources().getResults().get(j);
            referenceResource = linkableResourcesContext.getReferenceLinkableResources().getResults().get(i);
            currentResourceName = getResourceName(resource);
            referenceResourceName = getResourceName(referenceResource);

            if (!eServiceName.isEmpty()) {
                if (!currentResourceName.equals(eServiceName)) j++;
                if (!referenceResourceName.equals(eServiceName)) continue;
            }
            if (!partOfName.isEmpty()) {
                if (!currentResourceName.contains(partOfName)) j++;
                if (!referenceResourceName.contains(partOfName)) continue;
            }
            if (publisherId != null) {
                currentResourcePublisherId = getPublisherId(resource);
                referenceResourcePublisherId = getPublisherId(referenceResource);
                if (!currentResourcePublisherId.equals(publisherId)) j++;
                if (!referenceResourcePublisherId.equals(publisherId)) continue;
            }

            if (!doLinkableResourcesMatch(referenceResource, resource)) {
                foundDifference = true;
                break;
            } else {
                j++;
            }
        }
        // TODO verificare che l'ordine delle risorse collegabili sia sempre lo stesso, che questo ciclo
        // funzioni effettivamente, altrimenti serve un altro approccio (ciclo dentro un ciclo)
        Assertions.assertTrue(j > 0, "Non è stata trovata nessuna corrispondenza soddisfatta.");
    }

    private String getResourceName(LinkableResource resource) {
        return (resource.getResourceKind().getValue().equals("ESERVICE_TEMPLATE")) ?
                resource.getEserviceTemplate().getName() : resource.getEservice().getName();
    }

    private UUID getPublisherId(LinkableResource resource) {
        return (resource.getResourceKind().getValue().equals("ESERVICE_TEMPLATE")) ?
                resource.getEserviceTemplate().getCreator().getId() : resource.getEservice().getProducer().getId();
    }

    private boolean doLinkableResourcesMatch(LinkableResource resource1, LinkableResource resource2) {
        return resource1.getResourceKind() == resource2.getResourceKind() &&
                resource1.getPurposeTemplateId() == resource2.getPurposeTemplateId() &&
                resource1.getCreatedAt().equals(resource2.getCreatedAt());
    }

    private void assertLinkableResourcesMatch(boolean difference, LinkableResource resource1, LinkableResource resource2) {
        Assertions.assertNotNull(resource1);
        Assertions.assertNotNull(resource2);
        Assertions.assertFalse(
                difference,
                "La risorsa " + resource2.getResourceKind() + " " + resource2.getPurposeTemplateId() +
                        " non corrisponde alla risorsa " + resource1.getResourceKind() + " " +
                        resource1.getPurposeTemplateId());
    }

    private String resolveDynamicData(String value) {
        if (value.startsWith("$DA_CONTESTO(")) {
            String methodName = "";
            try {
                value = value.substring(13, value.length() - 1);
                methodName = "get" + value.substring(0, 1).toUpperCase() + value.substring(1);
                Method getterMethod = LinkableResourcesContext.class.getMethod(methodName);
                value = (String)getterMethod.invoke(linkableResourcesContext);

            } catch (NoSuchMethodException e) {
                log.error("Specified method " + methodName + " does not exist in LinkableResourcesContext.");

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return value;
    }
}
