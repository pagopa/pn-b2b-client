package it.pagopa.pn.interop.cucumber.steps.m2m.eservice;


import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.interop.common.IHttpExecutor;
import it.pagopa.interop.eservice.service.EServiceAttribute;
import it.pagopa.interop.eservice.service.IM2MEServiceAttributeClient;
import it.pagopa.interop.generated.openapi.clients.bff.model.Attribute;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.VerifiedAttribute;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.ResourceSnapshots;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;

import static it.pagopa.pn.interop.cucumber.steps.m2m.eservice.EServiceAttributesKey.from;
import static org.assertj.core.api.Assertions.assertThat;

public class EserviceVerifiedAttributesSteps {

    private final IM2MEServiceAttributeClient eServiceClient;
    private final SharedStepsContext sharedStepsContext;
    private final IHttpExecutor httpExecutor;

    private final ResourceSnapshots<List<EServiceAttribute<VerifiedAttribute>>> verifiedAttributeSnapshots = new ResourceSnapshots<>();

    public EserviceVerifiedAttributesSteps(
        SharedStepsContext sharedStepsContext,
        ClientTokenConfigurator clientTokenConfigurator
    ) {
        this.eServiceClient = clientTokenConfigurator.getM2mEServiceAttributeClient();
        this.sharedStepsContext = sharedStepsContext;
        this.httpExecutor = sharedStepsContext.getHttpCallExecutor();
    }

    @Given("l'utente crea un gruppo di attributi contenente {int} attribut(o)(i) verific(a)(ati) con successo")
    public void createVerifiedAttributeGroup(int attributesQt) {
        UUID eServiceId = sharedStepsContext.getEServicesCommonContext().getEserviceId();
        UUID descriptorId = sharedStepsContext.getEServicesCommonContext().getDescriptorId();
        List<UUID> attributesIds = sharedStepsContext.getAttributeCommonContext()
            .getCreatedAttributes()
            .stream()
            .limit(attributesQt)
            .map(Attribute::getId)
            .toList();

        int actualAttributesQt = attributesIds.size();
        if (actualAttributesQt != attributesQt) {
            throw new IllegalStateException(
                "E' prevista l'esistenza di almeno %d attributi verificati, ma ne sono stati rilevati %d".formatted(
                    attributesQt, actualAttributesQt));
        }

        httpExecutor.performCall(
            () -> eServiceClient.createVerifiedAttributesGroup(eServiceId, descriptorId,
                attributesIds));
        List<EServiceAttribute<VerifiedAttribute>> response = (List<EServiceAttribute<VerifiedAttribute>>) httpExecutor.getResponse();

        if (httpExecutor.getResponseStatus().is2xxSuccessful()) {
            sharedStepsContext.getEServicesCommonContext()
                .setGroupId(response.get(0).getGroupIndex());
            sharedStepsContext.getEServicesCommonContext().addVerifiedAttributes(attributesIds);
        }
    }

    @And("[si prende nota dello stato degli attributi verificati del gruppo dell'e-service]")
    public void storeEServiceVerifiedAttribute() {
        EServiceAttributesGroupKey attributesKey = getEServiceAttributesKey();
        performGetEServiceVerifiedAttributes(from(attributesKey));
        List<EServiceAttribute<VerifiedAttribute>> verifiedAttributes = (List<EServiceAttribute<VerifiedAttribute>>) httpExecutor.getResponse();
        List<EServiceAttribute<VerifiedAttribute>> verifiedAttributeInGroup = verifiedAttributes.stream()
            .filter(attr -> attr.getGroupIndex().equals(attributesKey.getGroupIndex()))
            .toList();
        verifiedAttributeSnapshots.addSnapshot(verifiedAttributeInGroup);
    }

    private void performGetEServiceVerifiedAttributes(EServiceAttributesKey key) {
        httpExecutor.performCall(() -> getVerifiedAttributes(key));
    }

    private List<EServiceAttribute<VerifiedAttribute>> getVerifiedAttributes(
        EServiceAttributesKey key) {
        return eServiceClient.getVerifiedAttributes(
            key.getEServiceId(),
            key.getDescriptorId()
        );
    }

    private EServiceAttributesGroupKey getEServiceAttributesKey() {
        return EServiceAttributesGroupKey.builder()
            .eServiceId(sharedStepsContext.getEServicesCommonContext().getEserviceId())
            .descriptorId(sharedStepsContext.getEServicesCommonContext().getDescriptorId())
            .groupIndex(sharedStepsContext.getEServicesCommonContext().getGroupId())
            .build();
    }

    @When("l'utente tenta di aggiungere l'attributo verifica numero {collectionIndex} al gruppo dell'e-service")
    public void addEServiceVerifiedAttribute(int index) {
        this.addEServiceVerifiedAttributes(index, index);
    }

    @When("l'utente tenta di aggiungere gli attributi verificati numeri da {collectionIndex} a {collectionIndex} al gruppo dell'e-service")
    public void addEServiceVerifiedAttributes(int startIncludedIndex, int endIncludedIndex) {
        EServiceAttributesGroupKey attributesKey = getEServiceAttributesKey();
        List<UUID> attributeIdsToAdd = getAttributeIdsToAdd(startIncludedIndex, endIncludedIndex);

        performAddEServiceVerifiedAttributes(attributesKey, attributeIdsToAdd);
    }

    private List<UUID> getAttributeIdsToAdd(int startIncludedIndex, int endIncludedIndex) {
        return IntStream.range(startIncludedIndex, endIncludedIndex + 1)
            .mapToObj(
                i -> sharedStepsContext.getAttributeCommonContext().getCreatedAttributes().get(i))
            .map(Attribute::getId)
            .toList();
    }

    @When("l'utente tenta di aggiungere l'attributo verifica numero {collectionIndex} al gruppo dell'e-service indicando un e-service id inesistente")
    public void addEServiceVerifiedAttributesWithUnexistentEServiceId(int attributeIndex) {
        EServiceAttributesGroupKey attributesKey = getEServiceAttributesKey().withEServiceId(
            UUID.randomUUID());
        List<UUID> attributeIdsToAdd = getAttributeIdsToAdd(attributeIndex, attributeIndex);

        performAddEServiceVerifiedAttributes(attributesKey, attributeIdsToAdd);
    }

    @When("l'utente tenta di aggiungere l'attributo verifica numero {collectionIndex} al gruppo dell'e-service indicando un descriptor id inesistente")
    public void addEServiceVerifiedAttributesWithUnexistentDescriptorId(int attributeIndex) {
        EServiceAttributesGroupKey attributesKey = getEServiceAttributesKey().withDescriptorId(
            UUID.randomUUID());
        List<UUID> attributeIdsToAdd = getAttributeIdsToAdd(attributeIndex, attributeIndex);

        performAddEServiceVerifiedAttributes(attributesKey, attributeIdsToAdd);
    }

    @When("l'utente tenta di aggiungere l'attributo verifica numero {collectionIndex} al gruppo dell'e-service indicando un group index inesistente")
    public void addEServiceVerifiedAttributesWithUnexistentGroupIndex(int attributeIndex) {
        EServiceAttributesGroupKey attributesKey = getEServiceAttributesKey().withGroupIndex(999);
        List<UUID> attributeIdsToAdd = getAttributeIdsToAdd(attributeIndex, attributeIndex);

        performAddEServiceVerifiedAttributes(attributesKey, attributeIdsToAdd);
    }

    @When("l'utente tenta di aggiungere degli attributi verificati al gruppo dell'e-service indicando degli attribute ids inesistenti")
    public void addEServiceVerifiedAttributesWithUnexistentIds() {
        EServiceAttributesGroupKey attributesKey = getEServiceAttributesKey();
        List<UUID> attributeIdsToAdd = List.of(UUID.randomUUID());

        performAddEServiceVerifiedAttributes(attributesKey, attributeIdsToAdd);
    }

    private void performAddEServiceVerifiedAttributes(EServiceAttributesGroupKey key,
        List<UUID> attributeIdsToAdd) {
        httpExecutor.performCall(() -> eServiceClient.addVerifiedAttributes(
            key.getEServiceId(),
            key.getDescriptorId(),
            key.getGroupIndex(),
            attributeIdsToAdd));
        if (httpExecutor.getResponseStatus().is2xxSuccessful()) {
            sharedStepsContext.getEServicesCommonContext()
                .addVerifiedAttributes(attributeIdsToAdd);
        }
    }

    @Then("gli attributi verificati sono stati aggiunti correttamente al gruppo dell'e-service")
    public void checkEServiceVerifiedAttributes() {
        List<UUID> actualVerifiedAttributes = verifiedAttributeSnapshots.getActualSnapshot()
            .stream()
            .map(attributeWrapper -> attributeWrapper.getAttribute().getId())
            .toList();
        List<UUID> addedVerifiedAttributes = sharedStepsContext.getEServicesCommonContext()
            .getVerifiedAttributesIds();

        assertThat(actualVerifiedAttributes)
            .as("Verifica che tra gli attributi verificati associati all'e-service ci siano quelli caricati")
            .containsAll(addedVerifiedAttributes);
    }

    @Then("i precedenti attributi verificati del gruppo dell'e-service sono rimasti invariati")
    public void checkUnmodifiedVerifiedAttributes() {
        List<EServiceAttribute<VerifiedAttribute>> previousSnapshot = verifiedAttributeSnapshots.getPreviousSnapshot();
        List<EServiceAttribute<VerifiedAttribute>> actualSnapshot = verifiedAttributeSnapshots.getActualSnapshot();

        assertThat(actualSnapshot)
            .as("Verifica che non ci siano state modifiche inattese agli attributi aggiunti precedentemente")
            .containsAll(previousSnapshot);
    }

    @Then("gli attributi verificati del gruppo dell'e-service sono rimasti invariati")
    public void checkVerifiedAttributes() {
        List<EServiceAttribute<VerifiedAttribute>> previousSnapshot = verifiedAttributeSnapshots.getPreviousSnapshot();
        List<EServiceAttribute<VerifiedAttribute>> actualSnapshot = verifiedAttributeSnapshots.getActualSnapshot();

        assertThat(actualSnapshot)
            .as("Verifica che non ci siano state modifiche inattese agli attributi aggiunti precedentemente")
            .containsExactlyInAnyOrderElementsOf(previousSnapshot);
    }

    @When("l'utente tenta di reperire gli attributi verificati dal gruppo dell'e-service")
    public void getEServiceVerifiedAttributes() {
        EServiceAttributesGroupKey attributesKey = getEServiceAttributesKey();
        performGetEServiceVerifiedAttributes(from(attributesKey));
    }

    @When("l'utente tenta di reperire gli attributi verificati dal gruppo dell'e-service indicando un e-service id inesistente")
    public void getEServiceVerifiedAttributesWithUnexistentEServiceId() {
        EServiceAttributesGroupKey attributesKey = getEServiceAttributesKey().withEServiceId(
            UUID.randomUUID());
        performGetEServiceVerifiedAttributes(from(attributesKey));
    }

    @When("l'utente tenta di reperire gli attributi verificati dal gruppo dell'e-service indicando un descriptor id inesistente")
    public void getEServiceVerifiedAttributesWithUnexistentDescriptorId() {
        EServiceAttributesGroupKey attributesKey = getEServiceAttributesKey().withDescriptorId(
            UUID.randomUUID());
        performGetEServiceVerifiedAttributes(from(attributesKey));
    }

    @When("l'utente tenta di reperire gli attributi verificati dal gruppo dell'e-service indicando un group index inesistente")
    public void getEServiceVerifiedAttributesWithUnexistentGroupIndex() {
        EServiceAttributesGroupKey attributesKey = getEServiceAttributesKey().withGroupIndex(999);
        performGetEServiceVerifiedAttributes(from(attributesKey));
    }

    @Then("gli attributi verificati ottenuti sono coerenti con quelli aggiunti")
    public void checkGotVerifiedAttributes() {
        List<UUID> expectedVerifiedAttributesIds = sharedStepsContext.getEServicesCommonContext()
            .getVerifiedAttributesIds();
        List<EServiceAttribute<VerifiedAttribute>> actualVerifiedAttributes =
            (List<EServiceAttribute<VerifiedAttribute>>) httpExecutor.getResponse();
        List<UUID> actualVerifiedAttributesIds = actualVerifiedAttributes.stream()
            .map(att -> att.getAttribute().getId())
            .toList();

        assertThat(actualVerifiedAttributesIds)
            .as("Verifica che gli attributi verificati reperiti siano identici a quelli precedentemente aggiunti")
            .containsExactlyInAnyOrderElementsOf(expectedVerifiedAttributesIds);
    }

    @When("l'utente tenta di rimuovere l'attributo verifica numero {collectionIndex} dal gruppo dell'e-service")
    public void removeEServiceVerifiedAttribute(int attributeIndex) {
        EServiceAttributesGroupKey key = getEServiceAttributesKey();
        UUID attributeId = sharedStepsContext.getEServicesCommonContext().getVerifiedAttributesIds()
            .get(attributeIndex);
        performDeleteVerifiedAttribute(key, attributeId);
    }

    private void performDeleteVerifiedAttribute(EServiceAttributesGroupKey key, UUID attributeId) {
        httpExecutor.performCall(() -> eServiceClient.deleteVerifiedAttribute(
            key.getEServiceId(),
            key.getDescriptorId(),
            key.getGroupIndex(),
            attributeId
        ));
    }

    @Then("è stato rimosso dall'e-service solo l'attributo verifica numero {collectionIndex}")
    public void checkRemovedVerifiedAttribute(int removedAttributeIndex) {
        EServiceAttributesGroupKey key = getEServiceAttributesKey();
        List<UUID> actualVerifiedAttributeIds =
            getVerifiedAttributes(from(key)).stream()
                .map(attribute -> attribute.getAttribute().getId())
                .toList();

        List<UUID> expectedVerifiedAttributeIds = new ArrayList<>(
            sharedStepsContext.getEServicesCommonContext().getVerifiedAttributesIds());
        expectedVerifiedAttributeIds.remove(removedAttributeIndex);

        assertThat(actualVerifiedAttributeIds)
            .as("Verifica che sia stato rimosso soltanto l'attributo verifica numero " + removedAttributeIndex)
            .containsExactlyInAnyOrderElementsOf(expectedVerifiedAttributeIds);
    }

    @Then("gli attributi verificati del gruppo sono rimasti invariati")
    public void checkSameVerifiedAttributes() {
        EServiceAttributesGroupKey key = getEServiceAttributesKey();
        List<UUID> actualVerifiedAttributeIds =
            getVerifiedAttributes(from(key)).stream()
                .map(attribute -> attribute.getAttribute().getId())
                .toList();

        List<UUID> expectedVerifiedAttributeIds = new ArrayList<>(
            sharedStepsContext.getEServicesCommonContext().getVerifiedAttributesIds());

        assertThat(actualVerifiedAttributeIds)
            .as("Verifica che gli attributi verificati siano rimasti invariati")
            .containsExactlyInAnyOrderElementsOf(expectedVerifiedAttributeIds);
    }

    @Given("l'utente rimuove l'attributo verifica numero {collectionIndex} dal gruppo dell'e-service con successo")
    public void successfullyDeleteVerifiedAttribute(int attributeToRemoveIndex) {
        removeEServiceVerifiedAttribute(attributeToRemoveIndex);
        checkRemovedVerifiedAttribute(attributeToRemoveIndex);
    }

    @When("l'utente tenta di rimuovere l'attributo verifica numero {collectionIndex} dal gruppo dell'e-service indicando un e-service id inesistente")
    public void removeEServiceVerifiedAttributeWithUnexistentEServiceId(int attributeIndex) {
        EServiceAttributesGroupKey key = getEServiceAttributesKey().withEServiceId(UUID.randomUUID());
        UUID attributeId = sharedStepsContext.getEServicesCommonContext().getVerifiedAttributesIds()
            .get(attributeIndex);
        performDeleteVerifiedAttribute(key, attributeId);
    }

    @When("l'utente tenta di rimuovere l'attributo verifica numero {collectionIndex} dal gruppo dell'e-service indicando un descriptor id inesistente")
    public void removeEServiceVerifiedAttributeWithUnexistentDescriptorId(int attributeIndex) {
        EServiceAttributesGroupKey key = getEServiceAttributesKey().withDescriptorId(UUID.randomUUID());
        UUID attributeId = sharedStepsContext.getEServicesCommonContext().getVerifiedAttributesIds()
            .get(attributeIndex);
        performDeleteVerifiedAttribute(key, attributeId);
    }

    @When("l'utente tenta di rimuovere l'attributo verifica numero {collectionIndex} dal gruppo dell'e-service indicando un group index inesistente")
    public void removeEServiceVerifiedAttributeWithUnexistentGroupIndex(int attributeIndex) {
        EServiceAttributesGroupKey key = getEServiceAttributesKey().withGroupIndex(999);
        UUID attributeId = sharedStepsContext.getEServicesCommonContext().getVerifiedAttributesIds()
            .get(attributeIndex);
        performDeleteVerifiedAttribute(key, attributeId);
    }

    @When("l'utente tenta di rimuovere un attributo verifica dal gruppo dell'e-service indicando un attribute id inesistente")
    public void removeEServiceVerifiedAttributeWithUnexistentAttributeId() {
        EServiceAttributesGroupKey key = getEServiceAttributesKey();
        UUID attributeId = UUID.randomUUID();
        performDeleteVerifiedAttribute(key, attributeId);
    }
}
