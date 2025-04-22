package it.pagopa.pn.interop.cucumber.steps.attribute;

import static it.pagopa.interop.generated.openapi.clients.bff.model.AttributeKind.CERTIFIED;
import static it.pagopa.interop.generated.openapi.clients.bff.model.AttributeKind.DECLARED;
import static it.pagopa.interop.generated.openapi.clients.bff.model.AttributeKind.VERIFIED;
import static java.lang.String.valueOf;

import io.cucumber.java.en.When;
import it.pagopa.interop.attribute.service.IAttributeApiClient;
import it.pagopa.interop.generated.openapi.clients.bff.model.AttributeKind;
import it.pagopa.interop.utils.HttpCallExecutor;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.attribute.AttributeListingSteps.AttributeListRequest.AttributeListRequestBuilder;
import java.util.List;
import lombok.Builder;
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
    private final HttpCallExecutor httpCallExecutor;
    private final IAttributeApiClient attributeApiClient;

    public AttributeListingSteps(
        SharedStepsContext sharedStepsContext,
        ClientTokenConfigurator clientTokenConfigurator)
    {
        this.sharedStepsContext = sharedStepsContext;
        this.httpCallExecutor = sharedStepsContext.getHttpCallExecutor();
        this.attributeApiClient = clientTokenConfigurator.getAttributeApiClient();
    }

    @When("l'utente richiede una operazione di listing degli attributi")
    public void listAttributes() {
        AttributeListRequest attributeListRequest = getAttributeListRequestPrototype()
            .build();
        listAttributes(attributeListRequest);
    }

    @When("l'utente richiede una operazione di listing degli attributi limitata ai primi {int} attributi")
    public void listFirstAttributes(int limit) {
        AttributeListRequest attributeListRequest = getAttributeListRequestPrototype()
            .limit(limit)
            .build();
        listAttributes(attributeListRequest);
    }

    @When("l'utente richiede una operazione di listing degli attributi con offset {int}")
    public void listAttributesWithOffset(int offset) {
        AttributeListRequest attributeListRequest = getAttributeListRequestPrototype()
            .offset(offset)
            .build();
        listAttributes(attributeListRequest);
    }

    @When("l'utente richiede una operatione di listing degli attributi filtrando per tipo \"certificato\" e \"verificato\"")
    public void listAttributesByType() {
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
            attributeApiClient.getAttributes(
                attributeListRequest.getLimit(),
                attributeListRequest.getOffset(),
                attributeListRequest.getKinds(),
                attributeListRequest.getQ(),
                attributeListRequest.getOrigin()
            )
        );
    }
}
