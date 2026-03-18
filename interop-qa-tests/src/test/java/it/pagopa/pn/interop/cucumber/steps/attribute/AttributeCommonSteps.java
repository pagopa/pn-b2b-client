package it.pagopa.pn.interop.cucumber.steps.attribute;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import it.pagopa.interop.authorization.domain.TenantType;
import it.pagopa.interop.authorization.service.identity.IdentityService;
import it.pagopa.interop.authorization.service.utils.PollingService;
import it.pagopa.interop.common.IHttpExecutor;
import it.pagopa.interop.generated.openapi.clients.bff.model.*;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.datapreparationservice.BFFDataPreparationService;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.common.AttributeCommonContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.client.HttpClientErrorException;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

// TODO riformulare così da rimuovere gli inutilizzati parametri "tenantType"
@Slf4j
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

        List<Attribute> createdAttributes = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            Attribute attribute = dataPreparationService.createAttribute(
                attributeKind,
                "attribute-%d-%d-%s".formatted(i, sharedStepsContext.getTestSeed(), attributeKind));
            createdAttributes.add(attribute);
        }

        if (!createdAttributes.isEmpty()) {
            attributeCommonContext.setAttributeId(createdAttributes.get(0).getId());
        }
        attributeCommonContext.setCreatedAttributes(createdAttributes);

        List<UUID> attributeIds = createdAttributes.stream().map(Attribute::getId).toList();

        switch (attributeKind) {
            case CERTIFIED -> attributeCommonContext.setRequiredCertifiedAttributes(List.of(attributeIds));
            case DECLARED -> attributeCommonContext.setRequiredDeclaredAttributes(List.of(attributeIds));
            case VERIFIED -> attributeCommonContext.setRequiredVerifiedAttributes(List.of(attributeIds));
        }
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
            softly.assertThat(httpCallExecutor.getResponseStatus().value())
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

    @Given("l'utente associa l'attributo {attributeKind} {int}-esimo creato all'eservice")
    public void associateAttributeToEService(AttributeKind attributeType, int attributeIndex) {

        UUID eServiceId = sharedStepsContext.getEServicesCommonContext().getEserviceId();
        UUID descriptorId = sharedStepsContext.getEServicesCommonContext().getDescriptorId();

        var eServiceDescriptor = clientTokenConfigurator.getProducerClient().getProducerEServiceDescriptor(eServiceId, descriptorId);

        DescriptorAttributesSeed attributesSeed = new DescriptorAttributesSeed()
            .certified(sharedStepsContext.getAttributeCommonContext().mapAttributes(eServiceDescriptor.getAttributes().getCertified()))
            .declared(sharedStepsContext.getAttributeCommonContext().mapAttributes(eServiceDescriptor.getAttributes().getDeclared()))
            .verified(sharedStepsContext.getAttributeCommonContext().mapAttributes(eServiceDescriptor.getAttributes().getVerified()));

        switch (attributeType) {
            case CERTIFIED -> attributesSeed.getCertified().get(0).add(
                new DescriptorAttributeSeed().id(attributeCommonContext.getRequiredCertifiedAttributes().get(0).get(attributeIndex))
            );
            case VERIFIED -> attributesSeed.getVerified().get(0).add(
                new DescriptorAttributeSeed().id(attributeCommonContext.getRequiredVerifiedAttributes().get(0).get(attributeIndex))
            );
            case DECLARED -> attributesSeed.getDeclared().get(0).add(
                new DescriptorAttributeSeed().id(attributeCommonContext.getRequiredDeclaredAttributes().get(0).get(attributeIndex))
            );
        }

        if (eServiceDescriptor.getState() == EServiceDescriptorState.PUBLISHED) {
            httpCallExecutor.performCall(
                () -> clientTokenConfigurator.getEServiceClient().updateDescriptorAttributes(eServiceId, descriptorId, attributesSeed)
            );
        } else {
            UpdateEServiceDescriptorSeed seed = new UpdateEServiceDescriptorSeed()
                .description(eServiceDescriptor.getDescription())
                .audience(eServiceDescriptor.getAudience())
                .voucherLifespan(eServiceDescriptor.getVoucherLifespan())
                .dailyCallsPerConsumer(eServiceDescriptor.getDailyCallsPerConsumer())
                .dailyCallsTotal(eServiceDescriptor.getDailyCallsTotal())
                .agreementApprovalPolicy(eServiceDescriptor.getAgreementApprovalPolicy())
                .attributes(attributesSeed);
            httpCallExecutor.performCall(
                () -> clientTokenConfigurator.getEServiceClient().updateDraftDescriptor(eServiceId, descriptorId, seed)
            );
        }

        if (httpCallExecutor.getResponseStatus().isError()) {
            log.warn("Errore durante l'associazione dell'attributo {} all'e-service {}: {}", attributeType, eServiceId, httpCallExecutor.getResponse());
            return;
        }

        PollingService.makePolling(
            () -> clientTokenConfigurator.getProducerClient().getProducerEServiceDescriptor(eServiceId, descriptorId),
            res -> {
                if (res != null) {
                    return switch (attributeType) {
                        case CERTIFIED -> res.getAttributes().getCertified().get(0).stream()
                            .anyMatch(attr -> attr.getId()
                                    .equals(attributeCommonContext.getRequiredCertifiedAttributes().get(0).get(attributeIndex))
                            );
                        case VERIFIED -> res.getAttributes().getVerified().get(0).stream()
                            .anyMatch(attr -> attr.getId()
                                    .equals(attributeCommonContext.getRequiredVerifiedAttributes().get(0).get(attributeIndex))
                            );
                        case DECLARED -> res.getAttributes().getDeclared().get(0).stream()
                            .anyMatch(attr -> attr.getId()
                                    .equals(attributeCommonContext.getRequiredDeclaredAttributes().get(0).get(attributeIndex))
                            );
                    };
                }

                return true;
            } ,
            String.format("Errore durante la verifica dell'associazione dell'attributo %s all'e-service %s", attributeType, eServiceId),
            5,
            2
        );
    }
}