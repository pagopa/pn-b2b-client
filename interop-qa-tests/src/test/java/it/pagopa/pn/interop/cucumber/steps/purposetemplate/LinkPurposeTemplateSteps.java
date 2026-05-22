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
import it.pagopa.pn.interop.cucumber.steps.purposetemplate.model.PurposeTemplateContext;
import it.pagopa.pn.interop.cucumber.steps.purposetemplate.utils.PurposeTemplateResolver;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;

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

    private PurposeTemplateContext purposeTemplateContext;
    private PurposeTemplateResolver resolver;
    private LinkableResources lastLinkableResources;
    private LinkableResources referenceLinkableResources;
    private List<String> referenceEServiceTemplateNames;
    private List<String> referenceEServiceNames;
    private List<UUID> referenceCreatorIds;

    /**
     * Quando voglio simulare una casistica di titolo duplicato, la prima volta ne creo uno (con timestamp) e lo setto qua.
     * La seconda volta, quando questa variabile non è più null, ri-applico lo stesso titolo.
     */
    private String duplicatedTitleForPurpose;

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
        this.resolver = new PurposeTemplateResolver(sharedStepsContext, purposeTemplateContext, sharedStepsContext.getIdentityService());
    }

    @Given("viene salvato {int} nome {resourceKind} di riferimento dalle risorse collegabili")
    @Given("vengono salvati {int} nomi {resourceKind} di riferimento dalle risorse collegabili")
    public void saveEServiceTemplateNamesFromLinkableResource(int names, String resourceKind) {
        Assertions.assertTrue(
                names >= lastLinkableResources.getResults().size(),
                "Non ci sono abbastanza risorse per salvare " + names + " nomi richiesti.");

        for (int i = 0; i < names; i++) {
            if (resourceKind.equals("e-service concreto")) {
                if (i == 0) referenceEServiceNames.clear();
                referenceEServiceNames.add(lastLinkableResources.getResults().get(i).getEservice().getName());

            } else if (resourceKind.equals("e-service template")) {
                if (i == 0) referenceEServiceTemplateNames.clear();
                referenceEServiceTemplateNames.add(lastLinkableResources.getResults().get(i).getEserviceTemplate().getName());
            }
        }
    }

    @When("recupera le risorse collegabili suggerite per un template finalità")
    public void getLinkableResourcesForPurposeTemplate(DataTable dataTable) {
        Map<String, String> data = dataTable.asMap(String.class, String.class);
        UUID purposeTemplateId = UUID.fromString(data.get("purpose_template_id"));
        int offset = Integer.parseInt(data.get("offset"));
        int limit = Integer.parseInt(data.get("limit"));
        String q = data.getOrDefault("filtro_nome_eservice", "");
        String publisherIDsCommaSeparated = data.getOrDefault("id_creatore", "");
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
            lastLinkableResources = (LinkableResources) httpCallExecutor.getResponse();
        }
    }

    @Then("^le risorse collegabili presentano un e-service (concreto|template)$")
    public void checkLinkableResourcesHaveEServiceType(String eServiceKindName) {
        eServiceKindName = "ESERVICE";
        if (eServiceKindName.equals("template")) eServiceKindName = "ESERVICE_TEMPLATE";
        // TODO cercare tra le ultime risorse recuperate se c'è una risorsa del tipo richiesto
        boolean foundRequestedResourceKind = false;
        for (int i = 0; i < lastLinkableResources.getResults().size(); i++) {
            if (lastLinkableResources.getResults().get(i).getResourceKind().getValue().equals(eServiceKindName)) {
                foundRequestedResourceKind = true;
                break;
            }
        }
        Assertions.assertTrue(foundRequestedResourceKind, "Il tipo di risorsa " + eServiceKindName + " non è stata trovata.");
    }

    @Then("vengono salvate le risorse collegabili in una lista di risorse di riferimento")
    public void saveLinkableResourcesAsAReference() {
        referenceLinkableResources = lastLinkableResources;
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
                referenceLinkableResources.getResults().size() > excludedResults,
                "Non c'è nemmeno 1 risultato da controllare.");

        for (int i = excludedResults; i < referenceLinkableResources.getResults().size(); i++) {
            referenceResource = referenceLinkableResources.getResults().get(i);
            resource = lastLinkableResources.getResults().get(j);
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
                includedResults >= referenceLinkableResources.getResults().size(),
                "I risultati disponibili sono numericamente inferiori ai risultati da controllare.");

        for (int i = 0; i < includedResults; i++) {
            referenceResource = referenceLinkableResources.getResults().get(i);
            resource = lastLinkableResources.getResults().get(j);
            if (!doLinkableResourcesMatch(referenceResource, resource)) {
                foundDifference = true;
                break;
            }
            j++;
        }
        assertLinkableResourcesMatch(foundDifference, referenceResource, resource);
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
}
