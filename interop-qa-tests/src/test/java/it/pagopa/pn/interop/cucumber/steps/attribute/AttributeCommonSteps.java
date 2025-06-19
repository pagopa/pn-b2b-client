package it.pagopa.pn.interop.cucumber.steps.attribute;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import it.pagopa.interop.authorization.domain.TenantType;
import it.pagopa.interop.authorization.service.identity.IdentityService;
import it.pagopa.interop.common.IHttpExecutor;
import it.pagopa.interop.generated.openapi.clients.bff.model.AttributeKind;
import it.pagopa.interop.generated.openapi.clients.bff.model.Attributes;
import it.pagopa.interop.utils.HttpCallExecutor;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.datapreparationservice.BFFDataPreparationService;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.common.AttributeCommonContext;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

// TODO riformulare così da rimuovere gli inutilizzati parametri "tenantType"
public class AttributeCommonSteps {
    private final ClientTokenConfigurator clientTokenConfigurator;
    private final SharedStepsContext sharedStepsContext;
    private final AttributeCommonContext attributeCommonContext;
    private final BFFDataPreparationService dataPreparationService;
    private final IHttpExecutor httpCallExecutor;
    private final IdentityService identityService;

    public AttributeCommonSteps(ClientTokenConfigurator clientTokenConfigurator,
        SharedStepsContext sharedStepsContext,
        BFFDataPreparationService dataPreparationService)
    {
        this.clientTokenConfigurator = clientTokenConfigurator;
        this.sharedStepsContext = sharedStepsContext;
        this.attributeCommonContext = sharedStepsContext.getAttributeCommonContext();
        this.dataPreparationService = dataPreparationService;
        this.httpCallExecutor = sharedStepsContext.getHttpCallExecutor();
        this.identityService = sharedStepsContext.getIdentityService();
    }

    @Given("{tenantType} ha già creato {int} attribut(i)(o) {attributeKind}")
    public void createAttributes(TenantType tenantType, int count, AttributeKind attributeKind) {
        clientTokenConfigurator.setBearerToken(identityService.getToken(tenantType.name(), null));
        @SuppressWarnings("unchecked")
        CompletableFuture<UUID>[] futures = new CompletableFuture[count];

        for(int i = 0; i < count; i++) {
            int finalI = i;
            futures[i] = CompletableFuture.supplyAsync(() -> dataPreparationService.createAttribute(
                attributeKind,
                "attribute-%d-%d-%s".formatted(finalI, sharedStepsContext.getTestSeed(), attributeKind)));
        }

        CompletableFuture.allOf(futures).join();
        attributeCommonContext.setAttributeId(futures[0].join());
    }

    @Given("{tenantType} ha già creato un attributo {attributeKind} con nome che contiene {string}")
    public void createAttributeWithNameKeyword(TenantType tenantType, AttributeKind attributeKind, String keyword) {
        dataPreparationService.createAttribute(
            attributeKind,
            "%d-%s".formatted(sharedStepsContext.getTestSeed(), keyword)
        );
    }

    @Then("si ottiene status code {int} e la lista di {int} attribut(i)(o)")
    public void checkAttributeCreation(int statusCode, int count) {
        assertSoftly(softly -> {
            softly.assertThat(httpCallExecutor.getResponse())
                .as("Attribute response NULL check")
                .isNotNull();
            softly.assertThat(httpCallExecutor.getClientResponse().value())
                .as("Attribute response status code check")
                .isEqualTo(statusCode);
            softly.assertThat(httpCallExecutor.getResponse())
                .as("Attribute response attribute count check")
                .extracting(Attributes.class::cast)
                .extracting(Attributes::getResults)
                .asList()
                .hasSize(count);
        });
    }
}