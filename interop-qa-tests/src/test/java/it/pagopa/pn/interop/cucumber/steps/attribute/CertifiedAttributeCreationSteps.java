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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

public class CertifiedAttributeCreationSteps {
    private final SharedStepsContext sharedStepsContext;
    private final ClientTokenConfigurator clientTokenConfigurator;
    private final IHttpExecutor httpCallExecutor;
    private final BFFDataPreparationService dataPreparationService;
    private String lastAttributeName;
    private UUID lastAttributeId;

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
                .name("new certified attribute %d".formatted(RandomUtils.insecure().randomInt()))
                .description("description test")));
        if (httpCallExecutor.getResponseStatus().is2xxSuccessful()) {
            getLastAttributeCreated(((ResponseEntity<Attribute>) httpCallExecutor.getResponse()).getBody().getId());
        }
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

            UUID attributeId = ((Attribute) httpCallExecutor.getResponse()).getId();
            sharedStepsContext.getPollingService().makePolling(
                    () -> httpCallExecutor.performCall(
                            () -> clientTokenConfigurator.getAttributeApiClient().getAttributeById(attributeId)),
                    HttpStatus::is2xxSuccessful,
                    "L'attributo non è stato creato correttamente!");

            Attribute attribute = ((Attribute) httpCallExecutor.getResponse());
            Assertions.assertNotNull(attribute);
            Assertions.assertNotNull(attribute.getId());
            Assertions.assertTrue(attribute.getCode() != null && !attribute.getCode().isEmpty());
            Assertions.assertEquals(AttributeKind.CERTIFIED_DISCRETE, attribute.getKind());
            Assertions.assertEquals(seed.getName(), attribute.getName());
            Assertions.assertEquals(seed.getDescription(), attribute.getDescription());
            Assertions.assertNotNull(attribute.getOrigin());
            Assertions.assertNotNull(attribute.getCreationTime());

            this.lastAttributeName = attribute.getName();
            this.lastAttributeId = attribute.getId();
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
        Attribute attribute = ((Attribute) httpCallExecutor.getResponse());
        getLastAttributeCreated(attribute.getId());
        httpCallExecutor.performCall(
            () -> clientTokenConfigurator.getAttributeApiClient().createCertifiedDiscreteAttribute(seed)
        );
    }

    @When("l'utente crea un attributo certificato discreto utilizzando lo stesso nome dell'ultimo attributo certificato creato")
    public void attemptToCreateCertifiedDiscreteAttributeWithSameNameAsLastCreated() {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());
        AttributeSeed seed = new AttributeSeed()
            .name(this.lastAttributeName)
            .description("description test");
        httpCallExecutor.performCall(
            () -> clientTokenConfigurator.getAttributeApiClient().createCertifiedDiscreteAttribute(seed)
        );
    }

    private void getLastAttributeCreated(UUID attributeId) {
        sharedStepsContext.getPollingService().makePolling(
                () -> httpCallExecutor.performCall(
                        () -> clientTokenConfigurator.getAttributeApiClient().getAttributeById(attributeId)),
                res -> res != HttpStatus.INTERNAL_SERVER_ERROR,
                "L'attributo non è stato creato correttamente!");
        Attribute attribute = (Attribute) httpCallExecutor.getResponse();
        this.lastAttributeName = attribute.getName();
        this.lastAttributeId = attribute.getId();
    }
}