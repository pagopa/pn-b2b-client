package it.pagopa.pn.interop.cucumber.steps.attribute;

import io.cucumber.java.en.When;
import it.pagopa.interop.attribute.service.IAttributeApiClient;
import it.pagopa.interop.generated.openapi.clients.bff.model.AttributeSeed;
import it.pagopa.interop.utils.HttpCallExecutor;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.common.AttributeCommonContext;
import org.apache.commons.lang.math.RandomUtils;

public class DeclaredAttributeCreationSteps {
    private final SharedStepsContext sharedStepsContext;
    private final IAttributeApiClient attributeApiClient;
    private final HttpCallExecutor httpCallExecutor;

    public DeclaredAttributeCreationSteps(
        ClientTokenConfigurator clientTokenConfigurator,
        SharedStepsContext sharedStepsContext)
    {
        this.sharedStepsContext = sharedStepsContext;
        this.attributeApiClient = clientTokenConfigurator.getAttributeApiClient();
        this.httpCallExecutor = sharedStepsContext.getHttpCallExecutor();
    }

    @When("l'utente crea un attributo dichiarato")
    public void createDeclaredAttribute() {
        httpCallExecutor.performCall(() -> attributeApiClient.createDeclaredAttributeRE(
            sharedStepsContext.getXCorrelationId(),
            new AttributeSeed()
                .name("new declared attribute %d".formatted(RandomUtils.nextInt()))
                .description("description test")));
    }
}