package it.pagopa.pn.interop.cucumber.steps.tenant;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.interop.common.IHttpExecutor;
import it.pagopa.interop.generated.openapi.clients.bff.model.*;
import it.pagopa.interop.utils.HttpCallExecutor;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class TenantListingSteps {
    private final ClientTokenConfigurator clientTokenConfigurator;
    private final IHttpExecutor httpCallExecutor;
    private final SharedStepsContext sharedStepsContext;

    public TenantListingSteps(ClientTokenConfigurator clientTokenConfigurator,
                              SharedStepsContext sharedStepsContext) {
        this.clientTokenConfigurator = clientTokenConfigurator;
        this.sharedStepsContext = sharedStepsContext;
        this.httpCallExecutor = sharedStepsContext.getHttpCallExecutor();
    }

    @When("l'utente richiede una operazione di listing degli aderenti limitata a {int}")
    public void requireOperationListWithLimit(int count) {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());
        httpCallExecutor.performCall(
                () -> clientTokenConfigurator.getTenantsApi().getTenants(count, null, null)
        );
    }

    @When("l'utente richiede una operazione di listing degli aderenti filtrando per la keyword {string}")
    public void requireConsumerOperationListWithKeyword(String keyword) {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());
        httpCallExecutor.performCall(
                () -> clientTokenConfigurator.getTenantsApi().getTenants(50, keyword, null)
        );
    }

    @Then("si ottiene status code {int} e la lista di {int} tenant")
    public void verifyStatusCodeAndConsumerListSize(int statusCode, int tenantNum) {
        Tenants compactOrganizations = (Tenants) sharedStepsContext.getHttpCallExecutor().getResponse();
        Assertions.assertEquals(statusCode, sharedStepsContext.getHttpCallExecutor().getResponseStatus().value());
        Assertions.assertEquals(tenantNum, compactOrganizations.getResults().size());
    }

    @Then("l'attributo certificato discreto è stato creato correttamente")
    public void certifiedDiscreteAttributeExists() {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());
        sharedStepsContext.getAttributeCommonContext().getLastCreatedAttribute();
        sharedStepsContext.getHttpCallExecutor().performCall(() -> clientTokenConfigurator.getAttributeApiClient().getAttributeById(
                sharedStepsContext.getAttributeCommonContext().getLastCreatedAttribute().getId()));

        Assertions.assertTrue(sharedStepsContext.getHttpCallExecutor().getResponseStatus().is2xxSuccessful());
        Attribute attribute = (Attribute) sharedStepsContext.getHttpCallExecutor().getResponse();
        Assertions.assertNotNull(attribute);
        Assertions.assertEquals(AttributeKind.CERTIFIED_DISCRETE, attribute.getKind());
    }

    @Then("l'utente richiede una operazione di listing dei suoi attributi certificati discreti e l'attributo assegnato è presente")
    public void requireCertifiedDiscreteAttributeOperation() {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());

        List<List<UUID>> assignedAttributes = sharedStepsContext.getAttributeCommonContext().getRequiredCertifiedAttributes();
        List<UUID> expectedAttributeIds = assignedAttributes.stream()
                .flatMap(List::stream)
                .filter(Objects::nonNull)
                .distinct()
                .toList();
        List<RequesterCertifiedAttribute> allResults = getAllRequesterCertifiedAttributes();

        Set<UUID> actualAttributeIds = allResults.stream()
                .map(RequesterCertifiedAttribute::getAttributeId)
                .collect(Collectors.toSet());

        expectedAttributeIds.forEach(attributeId -> Assertions.assertTrue(
                actualAttributeIds.contains(attributeId),
                "L'attributo certificato assegnato " + attributeId + " non è presente nella risposta paginata di getRequesterCertifiedAttributes"
        ));
    }

    @When("l'utente richiede una operazione di listing di tutti gli attributi certificati discreti e l'attributo assegnato è presente")
    public void requireCertifiedDiscreteAttributeOperationAll() {

        Attribute lastCreatedAttribute = sharedStepsContext.getAttributeCommonContext().getLastCreatedAttribute();

        sharedStepsContext.getPollingService().makePolling(
                () -> httpCallExecutor.performCall(() -> clientTokenConfigurator.getAttributeApiClient().getAttributes(
                        1, 0,
                        List.of(AttributeKind.CERTIFIED_DISCRETE),
                        lastCreatedAttribute.getName(), null)
                ),
                res -> !((Attributes) httpCallExecutor.getResponse()).getResults().isEmpty(),
                "There was an error while retrieving the attributes"
        );

        UUID attributeIdInList = ((Attributes) httpCallExecutor.getResponse()).getResults().get(0).getId();
        Assertions.assertEquals(lastCreatedAttribute.getId(), attributeIdInList);
    }

    private List<RequesterCertifiedAttribute> getAllRequesterCertifiedAttributes() {
        List<RequesterCertifiedAttribute> allResults = new ArrayList<>();

        int size = 50;
        int offset = 0;
        final int pageSize = 50;

        while (size == pageSize) {
            final int currentOffset = offset;
            log.info("Listing attributes from offset {} with size {}", currentOffset, pageSize);
            sharedStepsContext.getHttpCallExecutor().performCall(
                    () -> clientTokenConfigurator.getTenantsApi().getRequesterCertifiedAttributes(currentOffset, pageSize)
            );

            Assertions.assertTrue(
                    sharedStepsContext.getHttpCallExecutor().getResponseStatus().is2xxSuccessful(),
                    "La richiesta di listing degli attributi certificati assegnati non è andata a buon fine"
            );

            RequesterCertifiedAttributes requesterCertifiedAttributes = (RequesterCertifiedAttributes) sharedStepsContext.getHttpCallExecutor().getResponse();
            List<RequesterCertifiedAttribute> pageResults = requesterCertifiedAttributes.getResults()
                    .stream()
                    .toList();

            allResults.addAll(pageResults);
            size = pageResults.size();
            offset += size;
        }

        return allResults;
    }
}
