package it.pagopa.pn.interop.cucumber.steps.attribute;

import io.cucumber.java.en.When;
import it.pagopa.interop.common.IHttpExecutor;
import it.pagopa.interop.generated.openapi.clients.bff.model.Attribute;
import it.pagopa.interop.generated.openapi.clients.bff.model.AttributeKind;
import it.pagopa.interop.generated.openapi.clients.bff.model.AttributeSeed;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.DeclaredAttribute;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.common.AttributeCommonContext;
import it.pagopa.pn.interop.cucumber.steps.datapreparationservice.BFFDataPreparationService;
import java.time.OffsetDateTime;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.http.ResponseEntity;

public class DeclaredAttributeCreationSteps {
    private final SharedStepsContext sharedStepsContext;
    private final ClientTokenConfigurator clientTokenConfigurator;
    private final IHttpExecutor httpCallExecutor;
    private final BFFDataPreparationService dataPreparationService;

    public DeclaredAttributeCreationSteps(
        ClientTokenConfigurator clientTokenConfigurator,
        SharedStepsContext sharedStepsContext,
        BFFDataPreparationService dataPreparationService)
    {
        this.sharedStepsContext = sharedStepsContext;
        this.clientTokenConfigurator = clientTokenConfigurator;
        this.httpCallExecutor = sharedStepsContext.getHttpCallExecutor();
        this.dataPreparationService = dataPreparationService;
    }

    @When("l'utente crea un attributo dichiarato")
    public void createDeclaredAttribute() {
        String attributeName = "new declared attribute %d".formatted(
            RandomUtils.insecure().randomInt());
        String attributeDescription = "description test";
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());
        httpCallExecutor.performCall(() -> clientTokenConfigurator.getAttributeApiClient().createDeclaredAttributeRE(
            new AttributeSeed()
                .name(attributeName)
                .description(attributeDescription)));
        if(httpCallExecutor.getResponseStatus().is2xxSuccessful()) {
            ResponseEntity<Attribute> response = (ResponseEntity<Attribute>) httpCallExecutor.getResponse();
            Attribute bffAttribute = response.getBody();
            AttributeCommonContext attributeCommonContext = sharedStepsContext.getAttributeCommonContext();
            attributeCommonContext.setAttributeId(bffAttribute.getId());

            /* Necessario per permettere anche ai test che fanno uso di APIs m2m di poter utilizzare
             * questo step */
            /* Intenzionalmente si evita l'uso delle informazioni contenute in bffAttribute,
             * per minimizzare la propagazione di errori qualora un bug portasse alla restituzione
             * di informazioni sbagliate. */
            DeclaredAttribute m2mAttribute = new DeclaredAttribute()
                .createdAt(OffsetDateTime.now().toString())
                .id(bffAttribute.getId())
                .name(attributeName)
                .description(attributeDescription);
            attributeCommonContext.getDeclaredPublished().add(m2mAttribute);
        }
    }

    @When("l'utente crea {int} attributi dichiarati con successo")
    public void createDeclaredAttributes(int attributesQt) {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());
        for(int i = 0; i < attributesQt; i++) {
            Attribute attribute = dataPreparationService.createAttribute(AttributeKind.DECLARED);
            sharedStepsContext.getAttributeCommonContext().addDeclaredAttribute(attribute);
        }
    }


}