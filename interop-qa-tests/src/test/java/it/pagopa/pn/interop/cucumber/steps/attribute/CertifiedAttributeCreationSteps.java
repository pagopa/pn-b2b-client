package it.pagopa.pn.interop.cucumber.steps.attribute;

import io.cucumber.java.en.When;
import it.pagopa.interop.common.IHttpExecutor;
import it.pagopa.interop.generated.openapi.clients.bff.model.Attribute;
import it.pagopa.interop.generated.openapi.clients.bff.model.AttributeKind;
import it.pagopa.interop.generated.openapi.clients.bff.model.CertifiedAttributeSeed;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.datapreparationservice.BFFDataPreparationService;
import org.apache.commons.lang.math.RandomUtils;

public class CertifiedAttributeCreationSteps {
    private final SharedStepsContext sharedStepsContext;
    private final ClientTokenConfigurator clientTokenConfigurator;
    private final IHttpExecutor httpCallExecutor;
    private final BFFDataPreparationService dataPreparationService;

    public CertifiedAttributeCreationSteps(
        ClientTokenConfigurator clientTokenConfigurator,
        SharedStepsContext sharedStepsContext,
        BFFDataPreparationService dataPreparationService)
    {
        this.sharedStepsContext = sharedStepsContext;
        this.clientTokenConfigurator = clientTokenConfigurator;
        this.httpCallExecutor = sharedStepsContext.getHttpCallExecutor();
        this.dataPreparationService = dataPreparationService;
    }

    @When("l'utente crea un attributo certificato")
    public void createCertifiedAttribute() {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());
        httpCallExecutor.performCall(() -> clientTokenConfigurator.getAttributeApiClient().createCertifiedAttributeRE(
            new CertifiedAttributeSeed()
                .name("new certified attribute %d".formatted(RandomUtils.nextInt()))
                .description("description test")));
    }

    @When("l'utente crea {int} attributi certificati con successo")
    public void createCertifiedAttributes(int attributesQt) {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());
        for(int i = 0; i < attributesQt; i++) {
            Attribute attribute = dataPreparationService.createAttribute(AttributeKind.CERTIFIED);
            sharedStepsContext.getAttributeCommonContext().addCreatedAttribute(attribute);
        }
    }
}