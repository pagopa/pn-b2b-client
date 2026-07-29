package it.pagopa.pn.interop.cucumber.steps.attribute;

import io.cucumber.java.en.When;
import it.pagopa.interop.common.IHttpExecutor;
import it.pagopa.interop.generated.openapi.clients.bff.model.Attribute;
import it.pagopa.interop.generated.openapi.clients.bff.model.AttributeKind;
import it.pagopa.interop.generated.openapi.clients.bff.model.AttributeSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.CertifiedAttributeSeed;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.datapreparationservice.BFFDataPreparationService;
import org.apache.commons.lang3.RandomUtils;
import org.junit.jupiter.api.Assertions;

public class CertifiedAttributeCreationSteps {
    private final SharedStepsContext sharedStepsContext;
    private final ClientTokenConfigurator clientTokenConfigurator;
    private final IHttpExecutor httpCallExecutor;
    private final BFFDataPreparationService dataPreparationService;
    private String lastAttributeName;

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
        this.lastAttributeName = "new certified attribute %d".formatted(RandomUtils.insecure().randomInt());
        httpCallExecutor.performCall(() -> clientTokenConfigurator.getAttributeApiClient().createCertifiedAttributeRE(
            new CertifiedAttributeSeed()
                .name("new certified attribute %d".formatted(RandomUtils.insecure().randomInt()))
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

    @When("l'utente crea un attributo certificato discreto")
    public void createCertificateDiscreteAttribute() {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());
        AttributeSeed seed = new AttributeSeed()
            .name("new certified discrete attribute %d".formatted(RandomUtils.insecure().randomInt()))
            .description("description test");
        httpCallExecutor.performCall(
            () -> clientTokenConfigurator.getAttributeApiClient().createCertifiedDiscreteAttribute(seed)
        );

        if (httpCallExecutor.getResponseStatus().is2xxSuccessful()) {
            Attribute createdAttribute = (Attribute) httpCallExecutor.getResponse();
            Assertions.assertNotNull(createdAttribute);
            Assertions.assertNotNull(createdAttribute.getId());
            Assertions.assertTrue(createdAttribute.getCode() != null && !createdAttribute.getCode().isEmpty());
            Assertions.assertEquals(AttributeKind.CERTIFIED_DISCRETE, createdAttribute.getKind());
            Assertions.assertEquals(seed.getName(), createdAttribute.getName());
            Assertions.assertEquals(seed.getDescription(), createdAttribute.getDescription());
            Assertions.assertNotNull(createdAttribute.getOrigin());
            Assertions.assertNotNull(createdAttribute.getCreationTime());
        }
    }

    @When("l'utente tenta di creare due attributi certificati discreti con lo stesso nome")
    public void attemptToCreateTwoCertifiedDiscreteAttributesWithSameName() {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());
        String attributeName = "new certified discrete attribute %d".formatted(RandomUtils.insecure().randomInt());
        AttributeSeed seed = new AttributeSeed()
            .name(attributeName)
            .description("description test");
        httpCallExecutor.performCall(
            () -> clientTokenConfigurator.getAttributeApiClient().createCertifiedDiscreteAttribute(seed)
        );
        Assertions.assertTrue(httpCallExecutor.getResponseStatus().is2xxSuccessful());
        httpCallExecutor.performCall(
            () -> clientTokenConfigurator.getAttributeApiClient().createCertifiedDiscreteAttribute(seed)
        );
    }

    @When("l'utente crea un attributo certificato discreto utilizzando lo stesso nome dell'ultimo attributo certificato creato")
    public void attemptToCreateCertifiedDiscreteAttributeWithSameNameAsLastCreated() {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());
        AttributeSeed seed = new AttributeSeed()
            .name(lastAttributeName)
            .description("description test");
        httpCallExecutor.performCall(
            () -> clientTokenConfigurator.getAttributeApiClient().createCertifiedDiscreteAttribute(seed)
        );
    }
}