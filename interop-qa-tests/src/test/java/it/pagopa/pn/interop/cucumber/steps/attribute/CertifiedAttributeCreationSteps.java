package it.pagopa.pn.interop.cucumber.steps.attribute;

import io.cucumber.java.en.When;
import it.pagopa.interop.attribute.service.IAttributeApiClient;
import it.pagopa.interop.generated.openapi.clients.bff.model.CertifiedAttributeSeed;
import it.pagopa.interop.utils.HttpCallExecutor;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import org.apache.commons.lang.math.RandomUtils;

public class CertifiedAttributeCreationSteps {
    private final SharedStepsContext sharedStepsContext;
    private final IAttributeApiClient attributeApiClient;
    private final HttpCallExecutor httpCallExecutor;

    public CertifiedAttributeCreationSteps(
        ClientTokenConfigurator clientTokenConfigurator,
        SharedStepsContext sharedStepsContext)
    {
        this.sharedStepsContext = sharedStepsContext;
        this.attributeApiClient = clientTokenConfigurator.getAttributeApiClient();
        this.httpCallExecutor = sharedStepsContext.getHttpCallExecutor();
    }

    @When("l'utente crea un attributo certificato")
    public void createCertifiedAttribute() {
        httpCallExecutor.performCall(() -> attributeApiClient.createCertifiedAttributeRE(
            new CertifiedAttributeSeed()
                .name("new certified attribute %d".formatted(RandomUtils.nextInt()))
                .description("description test")));
    }
}