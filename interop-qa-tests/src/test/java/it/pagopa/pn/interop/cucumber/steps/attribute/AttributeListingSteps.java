package it.pagopa.pn.interop.cucumber.steps.attribute;

import static it.pagopa.interop.generated.openapi.clients.bff.model.AttributeKind.*;
import static java.lang.String.valueOf;

import io.cucumber.java.en.When;
import it.pagopa.interop.attribute.service.IAttributeApiClient;
import it.pagopa.interop.common.IHttpExecutor;
import it.pagopa.interop.generated.openapi.clients.bff.model.AttributeKind;
import it.pagopa.interop.generated.openapi.clients.bff.model.CompactAttribute;
import it.pagopa.interop.generated.openapi.clients.bff.model.Tenant;
import it.pagopa.interop.utils.HttpCallExecutor;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.attribute.AttributeListingSteps.AttributeListRequest.AttributeListRequestBuilder;
import java.util.List;
import lombok.Builder;
import org.junit.jupiter.api.Assertions;
import lombok.Data;

public class AttributeListingSteps {
    @Data
    @Builder(toBuilder = true)
    static class AttributeListRequest {
        String xCorrelationId;
        int limit;
        int offset;
        List<AttributeKind> kinds;
        String q;
        String origin;
    }

    private final SharedStepsContext sharedStepsContext;
    private final IHttpExecutor httpCallExecutor;
    private final ClientTokenConfigurator clientTokenConfigurator;

    public AttributeListingSteps(
        SharedStepsContext sharedStepsContext,
        ClientTokenConfigurator clientTokenConfigurator)
    {
        this.sharedStepsContext = sharedStepsContext;
        this.httpCallExecutor = sharedStepsContext.getHttpCallExecutor();
        this.clientTokenConfigurator = clientTokenConfigurator;
    }

    @When("l'utente richiede una operazione di listing degli attributi")
    public void listAttributes() {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());
        AttributeListRequest attributeListRequest = getAttributeListRequestPrototype()
            .build();
        listAttributes(attributeListRequest);
    }

    @When("l'utente richiede una operazione di listing degli attributi limitata ai primi {int} attributi")
    public void listFirstAttributes(int limit) {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());
        AttributeListRequest attributeListRequest = getAttributeListRequestPrototype()
            .limit(limit)
            .build();
        listAttributes(attributeListRequest);
    }

    @When("l'utente richiede una operazione di listing degli attributi con offset {int}")
    public void listAttributesWithOffset(int offset) {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());
        AttributeListRequest attributeListRequest = getAttributeListRequestPrototype()
            .offset(offset)
            .build();
        listAttributes(attributeListRequest);
    }

    @When("l'utente richiede una operatione di listing degli attributi filtrando per tipo \"certificato\" e \"verificato\"")
    public void listAttributesByType() {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());
        AttributeListRequest attributeListRequest = getAttributeListRequestPrototype()
            .kinds(List.of(CERTIFIED, VERIFIED))
            .build();
        listAttributes(attributeListRequest);
    }

    @When("l'utente richiede una operazione di listing degli attributi filtrando per keyword {string} all'interno del nome")
    public void listAttributesByNameKeyword(String keyword) {
        AttributeListRequest attributeListRequest = getAttributeListRequestPrototype()
            .q("%d-%s".formatted(sharedStepsContext.getTestSeed(), keyword))
            .build();
        listAttributes(attributeListRequest);
    }

    @When("l'utente richiede una operazione di listing degli attributi certificati discreti disponibili")
    public void listCertifiedDiscreteAttributes() {

        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());
        AttributeListRequest attributeListRequest = getAttributeListRequestPrototype()
                .kinds(List.of(CERTIFIED_DISCRETE))
                .q(null)
                .build();
        listAttributes(attributeListRequest);

        Assertions.assertTrue(sharedStepsContext.getHttpCallExecutor().getResponseStatus().is2xxSuccessful(), "Expected 2xx successful status code for attribute listing");

        List<CompactAttribute> attributes = ((it.pagopa.interop.generated.openapi.clients.bff.model.Attributes) sharedStepsContext.getHttpCallExecutor().getResponse()).getResults();
        sharedStepsContext.getAttributeCommonContext().setAvailableCertifiedDiscreteAttributes(attributes);
    }

    private AttributeListRequestBuilder getAttributeListRequestPrototype() {
        return AttributeListRequest.builder()
            .limit(50)
            .offset(0)
            .kinds(List.of(VERIFIED, CERTIFIED, DECLARED))
            .q(valueOf(sharedStepsContext.getTestSeed()))
            .build().toBuilder();
    }

    private void listAttributes(AttributeListRequest attributeListRequest) {
        httpCallExecutor.performCall(() ->
            clientTokenConfigurator.getAttributeApiClient().getAttributes(
                attributeListRequest.getLimit(),
                attributeListRequest.getOffset(),
                attributeListRequest.getKinds(),
                attributeListRequest.getQ(),
                attributeListRequest.getOrigin()
            )
        );
    }
}
