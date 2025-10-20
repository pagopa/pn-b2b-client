package it.pagopa.pn.interop.cucumber.steps.m2m.eservice;


import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.interop.common.IHttpExecutor;
import it.pagopa.interop.eservice.service.IM2MEServiceAttributeClient;
import it.pagopa.interop.eservice.service.IM2MEServiceAttributeClient.EServiceAttribute;
import it.pagopa.interop.generated.openapi.clients.bff.model.Attribute;
import it.pagopa.interop.generated.openapi.clients.bff.model.CertifiedAttribute;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.ResourceSnapshots;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;

public class EserviceAttributesSteps {
    private final IM2MEServiceAttributeClient eServiceClient;
    private final SharedStepsContext sharedStepsContext;
    private final IHttpExecutor httpExecutor;

    private final ResourceSnapshots<List<EServiceAttribute<CertifiedAttribute>>> certifiedAttributeSnapshots = new ResourceSnapshots<>();

    public EserviceAttributesSteps(
        SharedStepsContext sharedStepsContext,
        ClientTokenConfigurator clientTokenConfigurator
    ) {
        this.eServiceClient = clientTokenConfigurator.getM2mEServiceAttributeClient();
        this.sharedStepsContext = sharedStepsContext;
        this.httpExecutor = sharedStepsContext.getHttpCallExecutor();
    }

    @Given("l'utente crea un gruppo di attributi contenente {int} attribut(o)(i) certificat(o)(i) con successo")
    public void createCertifiedAttributeGroup(int attributesQt) {
        UUID eServiceId = sharedStepsContext.getEServicesCommonContext().getEserviceId();
        UUID descriptorId = sharedStepsContext.getEServicesCommonContext().getDescriptorId();
        List<UUID> attributesIds = sharedStepsContext.getAttributeCommonContext().getCreatedAttributes()
            .stream()
            .limit(attributesQt)
            .map(Attribute::getId)
            .toList();

        int actualAttributesQt = attributesIds.size();
        if(actualAttributesQt != attributesQt) {
            throw new IllegalStateException("E' prevista l'esistenza di almeno %d attributi certificati, ma ne sono stati rilevati %d".formatted(attributesQt, actualAttributesQt));
        }

        httpExecutor.performCall(() -> eServiceClient.createCertifiedAttributesGroup(eServiceId, descriptorId, attributesIds));
        List<EServiceAttribute<CertifiedAttribute>> response = (List<EServiceAttribute<CertifiedAttribute>>) httpExecutor.getResponse();

        if (httpExecutor.getResponseStatus().is2xxSuccessful()) {
            sharedStepsContext.getEServicesCommonContext().setGroupId(response.get(0).getGroupIndex());
            sharedStepsContext.getEServicesCommonContext().addCertifiedAttributes(attributesIds);
        }
    }

    @And("[si prende nota dello stato degli attributi certificati del gruppo dell'e-service]")
    public void storeEServiceCertifiedAttribute() {
        UUID eServiceId = sharedStepsContext.getEServicesCommonContext().getEserviceId();
        UUID descriptorId = sharedStepsContext.getEServicesCommonContext().getDescriptorId();
        int groupIndex = sharedStepsContext.getEServicesCommonContext().getGroupId();

        httpExecutor.performCall(() -> eServiceClient.getCertifiedAttributes(eServiceId, descriptorId, groupIndex));
        List<EServiceAttribute<CertifiedAttribute>> certifiedAttributes = (List<EServiceAttribute<CertifiedAttribute>>) httpExecutor.getResponse();
        List<EServiceAttribute<CertifiedAttribute>> certifiedAttributeInGroup = certifiedAttributes.stream()
            .filter(attr -> attr.getGroupIndex().equals(groupIndex))
            .toList();
        certifiedAttributeSnapshots.addSnapshot(certifiedAttributeInGroup);
    }

    @When("l'utente tenta di aggiungere l'attributo certificato numero {int} al gruppo dell'e-service")
    public void addEServiceCertifiedAttribute(int index) {
        this.addEServiceCertifiedAttributes(index, index);
    }

    @When("l'utente tenta di aggiungere gli attributi certificati numeri da {int} a {int} al gruppo dell'e-service")
    public void addEServiceCertifiedAttributes(int startIncludedIndex, int endIncludedIndex) {
        UUID eServiceId = sharedStepsContext.getEServicesCommonContext().getEserviceId();
        UUID descriptorId = sharedStepsContext.getEServicesCommonContext().getDescriptorId();
        int groupIndex = sharedStepsContext.getEServicesCommonContext().getGroupId();
        List<UUID> attributeIdsToAdd = getAttributeIdsToAdd(startIncludedIndex, endIncludedIndex);

        addEServiceCertifiedAttributes(eServiceId, descriptorId, groupIndex, attributeIdsToAdd);
    }

    private List<UUID> getAttributeIdsToAdd(int startIncludedIndex, int endIncludedIndex) {
        return IntStream.range(startIncludedIndex, endIncludedIndex + 1)
            .mapToObj(
                i -> sharedStepsContext.getAttributeCommonContext().getCreatedAttributes().get(i))
            .map(Attribute::getId)
            .toList();
    }

    @When("l'utente tenta di aggiungere l'attributo certificato numero {int} al gruppo dell'e-service indicando un e-service id inesistente")
    public void addEServiceCertifiedAttributesWithUnexistentEServiceId(int attributeIndex) {
        UUID eServiceId = UUID.randomUUID();
        UUID descriptorId = sharedStepsContext.getEServicesCommonContext().getDescriptorId();
        int groupIndex = sharedStepsContext.getEServicesCommonContext().getGroupId();
        List<UUID> attributeIdsToAdd = getAttributeIdsToAdd(attributeIndex, attributeIndex);

        addEServiceCertifiedAttributes(eServiceId, descriptorId, groupIndex, attributeIdsToAdd);
    }

    @When("l'utente tenta di aggiungere l'attributo certificato numero {int} al gruppo dell'e-service indicando un descriptor id inesistente")
    public void addEServiceCertifiedAttributesWithUnexistentDescriptorId(int attributeIndex) {
        UUID eServiceId = sharedStepsContext.getEServicesCommonContext().getEserviceId();
        UUID descriptorId = UUID.randomUUID();
        int groupIndex = sharedStepsContext.getEServicesCommonContext().getGroupId();
        List<UUID> attributeIdsToAdd = getAttributeIdsToAdd(attributeIndex, attributeIndex);

        addEServiceCertifiedAttributes(eServiceId, descriptorId, groupIndex, attributeIdsToAdd);
    }

    @When("l'utente tenta di aggiungere l'attributo certificato numero {int} al gruppo dell'e-service indicando un group index inesistente")
    public void addEServiceCertifiedAttributesWithUnexistentGroupIndex(int attributeIndex) {
        UUID eServiceId = sharedStepsContext.getEServicesCommonContext().getEserviceId();
        UUID descriptorId = sharedStepsContext.getEServicesCommonContext().getDescriptorId();
        int groupIndex = 999;
        List<UUID> attributeIdsToAdd = getAttributeIdsToAdd(attributeIndex, attributeIndex);

        addEServiceCertifiedAttributes(eServiceId, descriptorId, groupIndex, attributeIdsToAdd);
    }

    @When("l'utente tenta di aggiungere degli attributi certificati al gruppo dell'e-service indicando degli attribute ids inesistenti")
    public void addEServiceCertifiedAttributesWithUnexistentIds(int attributeIndex) {
        UUID eServiceId = sharedStepsContext.getEServicesCommonContext().getEserviceId();
        UUID descriptorId = sharedStepsContext.getEServicesCommonContext().getDescriptorId();
        int groupIndex = sharedStepsContext.getEServicesCommonContext().getGroupId();
        List<UUID> attributeIdsToAdd = List.of(UUID.randomUUID());

        addEServiceCertifiedAttributes(eServiceId, descriptorId, groupIndex, attributeIdsToAdd);
    }

    private void addEServiceCertifiedAttributes(UUID eServiceId, UUID descriptorId, int groupIndex,
        List<UUID> attributeIdsToAdd) {
        httpExecutor.performCall(() -> eServiceClient.addCertifiedAttributes(eServiceId,
            descriptorId, groupIndex, attributeIdsToAdd));
        if(httpExecutor.getResponseStatus().is2xxSuccessful()) {
            sharedStepsContext.getEServicesCommonContext().addCertifiedAttributes(attributeIdsToAdd);
        }
    }

    @Then("gli attributi certificati restituiti dell'e-service sono coerenti con quelli aggiunti")
    public void checkReturnedEServiceCertifiedAttributes() {
        List<IM2MEServiceAttributeClient.EServiceAttribute<CertifiedAttribute>> returnedAttributes =
            (List<EServiceAttribute<CertifiedAttribute>>) httpExecutor.getResponse();

        int expectedGroupIndex = sharedStepsContext.getEServicesCommonContext().getGroupId();
        List<UUID> expectedIds = sharedStepsContext.getEServicesCommonContext().getCertifiedAttributesIds();

        Integer returnedGroupIndex = returnedAttributes.get(0).getGroupIndex();
        List<UUID> returnedIds = returnedAttributes.stream()
            .map(att -> att.getAttribute().getId())
            .toList();

        assertSoftly(softly -> {
            softly.assertThat(returnedGroupIndex)
                .as("Verifica che il group index sia quello atteso")
                .isEqualTo(expectedGroupIndex);
            softly.assertThat(returnedIds)
                .as("Verifica che gli attributi restituiti sia quelli effettivamente aggiunti")
                .containsExactlyInAnyOrderElementsOf(expectedIds);
        });
    }

    @Then("gli attributi certificati sono stati aggiunti correttamente al gruppo dell'e-service")
    public void checkEServiceCertifiedAttributes() {
        List<UUID> actualCertifiedAttributes = certifiedAttributeSnapshots.getActualSnapshot()
            .stream()
            .map(attributeWrapper -> attributeWrapper.getAttribute().getId())
            .toList();
        List<UUID> addedCertifiedAttributes = sharedStepsContext.getEServicesCommonContext()
            .getCertifiedAttributesIds();

        assertThat(actualCertifiedAttributes)
            .as("Verifica che tra gli attributi certificati associati all'e-service ci siano quelli caricati")
            .containsAll(addedCertifiedAttributes);
    }

    @Then("i precedenti attributi certificati del gruppo dell'e-service sono rimasti invariati")
    public void checkUnmodifiedCertifiedAttributes() {
        List<EServiceAttribute<CertifiedAttribute>> previousSnapshot = certifiedAttributeSnapshots.getPreviousSnapshot();
        List<EServiceAttribute<CertifiedAttribute>> actualSnapshot = certifiedAttributeSnapshots.getActualSnapshot();

        assertThat(actualSnapshot)
            .as("Verifica che non ci siano state modifiche inattese agli attributi aggiunti precedentemente")
            .containsAll(previousSnapshot);
    }

    @Then("gli attributi certificati del gruppo dell'e-service sono rimasti invariati")
    public void checkCertifiedAttributes() {
        List<EServiceAttribute<CertifiedAttribute>> previousSnapshot = certifiedAttributeSnapshots.getPreviousSnapshot();
        List<EServiceAttribute<CertifiedAttribute>> actualSnapshot = certifiedAttributeSnapshots.getActualSnapshot();

        assertThat(actualSnapshot)
            .as("Verifica che non ci siano state modifiche inattese agli attributi aggiunti precedentemente")
            .containsExactlyInAnyOrderElementsOf(previousSnapshot);
    }
}
