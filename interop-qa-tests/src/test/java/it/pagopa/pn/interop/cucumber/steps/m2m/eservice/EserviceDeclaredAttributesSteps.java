package it.pagopa.pn.interop.cucumber.steps.m2m.eservice;


import static it.pagopa.pn.interop.cucumber.steps.m2m.eservice.EServiceAttributesKey.from;
import static org.assertj.core.api.Assertions.assertThat;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.interop.common.IHttpExecutor;
import it.pagopa.interop.eservice.service.IM2MEServiceAttributeClient;
import it.pagopa.interop.eservice.service.EServiceAttribute;
import it.pagopa.interop.generated.openapi.clients.bff.model.Attribute;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.DeclaredAttribute;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.ResourceSnapshots;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;

public class EserviceDeclaredAttributesSteps {

    private final IM2MEServiceAttributeClient eServiceClient;
    private final SharedStepsContext sharedStepsContext;
    private final IHttpExecutor httpExecutor;

    private final ResourceSnapshots<List<EServiceAttribute<DeclaredAttribute>>> declaredAttributeSnapshots = new ResourceSnapshots<>();

    public EserviceDeclaredAttributesSteps(
        SharedStepsContext sharedStepsContext,
        ClientTokenConfigurator clientTokenConfigurator
    ) {
        this.eServiceClient = clientTokenConfigurator.getM2mEServiceAttributeClient();
        this.sharedStepsContext = sharedStepsContext;
        this.httpExecutor = sharedStepsContext.getHttpCallExecutor();
    }

    @Given("l'utente crea un gruppo di attributi contenente {int} attribut(o)(i) dichiarat(o)(i) con successo")
    public void createDeclaredAttributeGroup(int attributesQt) {
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
                "E' prevista l'esistenza di almeno %d attributi dichiarati, ma ne sono stati rilevati %d".formatted(
                    attributesQt, actualAttributesQt));
        }

        httpExecutor.performCall(
            () -> eServiceClient.createDeclaredAttributesGroup(eServiceId, descriptorId,
                attributesIds));
        List<EServiceAttribute<DeclaredAttribute>> response = (List<EServiceAttribute<DeclaredAttribute>>) httpExecutor.getResponse();

        if (httpExecutor.getResponseStatus().is2xxSuccessful()) {
            sharedStepsContext.getEServicesCommonContext()
                .setGroupId(response.get(0).getGroupIndex());
            sharedStepsContext.getEServicesCommonContext().addDeclaredAttributes(attributesIds);
        }
    }

    @And("[si prende nota dello stato degli attributi dichiarati del gruppo dell'e-service]")
    public void storeEServiceDeclaredAttribute() {
        EServiceAttributesGroupKey attributesKey = getEServiceAttributesKey();
        performGetEServiceDeclaredAttributes(from(attributesKey));
        List<EServiceAttribute<DeclaredAttribute>> declaredAttributes = (List<EServiceAttribute<DeclaredAttribute>>) httpExecutor.getResponse();
        List<EServiceAttribute<DeclaredAttribute>> declaredAttributeInGroup = declaredAttributes.stream()
            .filter(attr -> attr.getGroupIndex().equals(attributesKey.getGroupIndex()))
            .toList();
        declaredAttributeSnapshots.addSnapshot(declaredAttributeInGroup);
    }

    private void performGetEServiceDeclaredAttributes(EServiceAttributesKey key) {
        httpExecutor.performCall(() -> getDeclaredAttributes(key));
    }

    private List<EServiceAttribute<DeclaredAttribute>> getDeclaredAttributes(
        EServiceAttributesKey key) {
        return eServiceClient.getDeclaredAttributes(
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

    @When("l'utente tenta di aggiungere l'attributo dichiarato numero {collectionIndex} al gruppo dell'e-service")
    public void addEServiceDeclaredAttribute(int index) {
        this.addEServiceDeclaredAttributes(index, index);
    }

    @When("l'utente tenta di aggiungere gli attributi dichiarati numeri da {collectionIndex} a {collectionIndex} al gruppo dell'e-service")
    public void addEServiceDeclaredAttributes(int startIncludedIndex, int endIncludedIndex) {
        EServiceAttributesGroupKey attributesKey = getEServiceAttributesKey();
        List<UUID> attributeIdsToAdd = getAttributeIdsToAdd(startIncludedIndex, endIncludedIndex);

        performAddEServiceDeclaredAttributes(attributesKey, attributeIdsToAdd);
    }

    private List<UUID> getAttributeIdsToAdd(int startIncludedIndex, int endIncludedIndex) {
        return IntStream.range(startIncludedIndex, endIncludedIndex + 1)
            .mapToObj(
                i -> sharedStepsContext.getAttributeCommonContext().getCreatedAttributes().get(i))
            .map(Attribute::getId)
            .toList();
    }

    @When("l'utente tenta di aggiungere l'attributo dichiarato numero {collectionIndex} al gruppo dell'e-service indicando un e-service id inesistente")
    public void addEServiceDeclaredAttributesWithUnexistentEServiceId(int attributeIndex) {
        EServiceAttributesGroupKey attributesKey = getEServiceAttributesKey().withEServiceId(
            UUID.randomUUID());
        List<UUID> attributeIdsToAdd = getAttributeIdsToAdd(attributeIndex, attributeIndex);

        performAddEServiceDeclaredAttributes(attributesKey, attributeIdsToAdd);
    }

    @When("l'utente tenta di aggiungere l'attributo dichiarato numero {collectionIndex} al gruppo dell'e-service indicando un descriptor id inesistente")
    public void addEServiceDeclaredAttributesWithUnexistentDescriptorId(int attributeIndex) {
        EServiceAttributesGroupKey attributesKey = getEServiceAttributesKey().withDescriptorId(
            UUID.randomUUID());
        List<UUID> attributeIdsToAdd = getAttributeIdsToAdd(attributeIndex, attributeIndex);

        performAddEServiceDeclaredAttributes(attributesKey, attributeIdsToAdd);
    }

    @When("l'utente tenta di aggiungere l'attributo dichiarato numero {collectionIndex} al gruppo dell'e-service indicando un group index inesistente")
    public void addEServiceDeclaredAttributesWithUnexistentGroupIndex(int attributeIndex) {
        EServiceAttributesGroupKey attributesKey = getEServiceAttributesKey().withGroupIndex(999);
        List<UUID> attributeIdsToAdd = getAttributeIdsToAdd(attributeIndex, attributeIndex);

        performAddEServiceDeclaredAttributes(attributesKey, attributeIdsToAdd);
    }

    @When("l'utente tenta di aggiungere degli attributi dichiarati al gruppo dell'e-service indicando degli attribute ids inesistenti")
    public void addEServiceDeclaredAttributesWithUnexistentIds() {
        EServiceAttributesGroupKey attributesKey = getEServiceAttributesKey();
        List<UUID> attributeIdsToAdd = List.of(UUID.randomUUID());

        performAddEServiceDeclaredAttributes(attributesKey, attributeIdsToAdd);
    }

    private void performAddEServiceDeclaredAttributes(EServiceAttributesGroupKey key,
        List<UUID> attributeIdsToAdd) {
        httpExecutor.performCall(() -> eServiceClient.addDeclaredAttributes(
            key.getEServiceId(),
            key.getDescriptorId(),
            key.getGroupIndex(),
            attributeIdsToAdd));
        if (httpExecutor.getResponseStatus().is2xxSuccessful()) {
            sharedStepsContext.getEServicesCommonContext()
                .addDeclaredAttributes(attributeIdsToAdd);
        }
    }

    @Then("gli attributi dichiarati sono stati aggiunti correttamente al gruppo dell'e-service")
    public void checkEServiceDeclaredAttributes() {
        List<UUID> actualDeclaredAttributes = declaredAttributeSnapshots.getActualSnapshot()
            .stream()
            .map(attributeWrapper -> attributeWrapper.getAttribute().getId())
            .toList();
        List<UUID> addedDeclaredAttributes = sharedStepsContext.getEServicesCommonContext()
            .getDeclaredAttributesIds();

        assertThat(actualDeclaredAttributes)
            .as("Verifica che tra gli attributi dichiarati associati all'e-service ci siano quelli caricati")
            .containsAll(addedDeclaredAttributes);
    }

    @Then("i precedenti attributi dichiarati del gruppo dell'e-service sono rimasti invariati")
    public void checkUnmodifiedDeclaredAttributes() {
        List<EServiceAttribute<DeclaredAttribute>> previousSnapshot = declaredAttributeSnapshots.getPreviousSnapshot();
        List<EServiceAttribute<DeclaredAttribute>> actualSnapshot = declaredAttributeSnapshots.getActualSnapshot();

        assertThat(actualSnapshot)
            .as("Verifica che non ci siano state modifiche inattese agli attributi aggiunti precedentemente")
            .containsAll(previousSnapshot);
    }

    @Then("gli attributi dichiarati del gruppo dell'e-service sono rimasti invariati")
    public void checkDeclaredAttributes() {
        List<EServiceAttribute<DeclaredAttribute>> previousSnapshot = declaredAttributeSnapshots.getPreviousSnapshot();
        List<EServiceAttribute<DeclaredAttribute>> actualSnapshot = declaredAttributeSnapshots.getActualSnapshot();

        assertThat(actualSnapshot)
            .as("Verifica che non ci siano state modifiche inattese agli attributi aggiunti precedentemente")
            .containsExactlyInAnyOrderElementsOf(previousSnapshot);
    }

    @When("l'utente tenta di reperire gli attributi dichiarati dal gruppo dell'e-service")
    public void getEServiceDeclaredAttributes() {
        EServiceAttributesGroupKey attributesKey = getEServiceAttributesKey();
        performGetEServiceDeclaredAttributes(from(attributesKey));
    }

    @When("l'utente tenta di reperire gli attributi dichiarati dal gruppo dell'e-service indicando un e-service id inesistente")
    public void getEServiceDeclaredAttributesWithUnexistentEServiceId() {
        EServiceAttributesGroupKey attributesKey = getEServiceAttributesKey().withEServiceId(
            UUID.randomUUID());
        performGetEServiceDeclaredAttributes(from(attributesKey));
    }

    @When("l'utente tenta di reperire gli attributi dichiarati dal gruppo dell'e-service indicando un descriptor id inesistente")
    public void getEServiceDeclaredAttributesWithUnexistentDescriptorId() {
        EServiceAttributesGroupKey attributesKey = getEServiceAttributesKey().withDescriptorId(
            UUID.randomUUID());
        performGetEServiceDeclaredAttributes(from(attributesKey));
    }

    @When("l'utente tenta di reperire gli attributi dichiarati dal gruppo dell'e-service indicando un group index inesistente")
    public void getEServiceDeclaredAttributesWithUnexistentGroupIndex() {
        EServiceAttributesGroupKey attributesKey = getEServiceAttributesKey().withGroupIndex(999);
        performGetEServiceDeclaredAttributes(from(attributesKey));
    }

    @Then("gli attributi dichiarati ottenuti sono coerenti con quelli aggiunti")
    public void checkGotDeclaredAttributes() {
        List<UUID> expectedDeclaredAttributesIds = sharedStepsContext.getEServicesCommonContext()
            .getDeclaredAttributesIds();
        List<EServiceAttribute<DeclaredAttribute>> actualDeclaredAttributes =
            (List<EServiceAttribute<DeclaredAttribute>>) httpExecutor.getResponse();
        List<UUID> actualDeclaredAttributesIds = actualDeclaredAttributes.stream()
            .map(att -> att.getAttribute().getId())
            .toList();

        assertThat(actualDeclaredAttributesIds)
            .as("Verifica che gli attributi dichiarati reperiti siano identici a quelli precedentemente aggiunti")
            .containsExactlyInAnyOrderElementsOf(expectedDeclaredAttributesIds);
    }

    @When("l'utente tenta di rimuovere l'attributo dichiarato numero {collectionIndex} dal gruppo dell'e-service")
    public void removeEServiceDeclaredAttribute(int attributeIndex) {
        EServiceAttributesGroupKey key = getEServiceAttributesKey();
        UUID attributeId = sharedStepsContext.getEServicesCommonContext().getDeclaredAttributesIds()
            .get(attributeIndex);
        performDeleteDeclaredAttribute(key, attributeId);
    }

    private void performDeleteDeclaredAttribute(EServiceAttributesGroupKey key, UUID attributeId) {
        httpExecutor.performCall(() -> eServiceClient.deleteDeclaredAttribute(
            key.getEServiceId(),
            key.getDescriptorId(),
            key.getGroupIndex(),
            attributeId
        ));
    }

    @Then("è stato rimosso dall'e-service solo l'attributo dichiarato numero {collectionIndex}")
    public void checkRemovedDeclaredAttribute(int removedAttributeIndex) {
        EServiceAttributesGroupKey key = getEServiceAttributesKey();
        List<UUID> actualDeclaredAttributeIds =
            getDeclaredAttributes(from(key)).stream()
                .map(attribute -> attribute.getAttribute().getId())
                .toList();

        List<UUID> expectedDeclaredAttributeIds = new ArrayList<>(
            sharedStepsContext.getEServicesCommonContext().getDeclaredAttributesIds());
        expectedDeclaredAttributeIds.remove(removedAttributeIndex);

        assertThat(actualDeclaredAttributeIds)
            .as("Verifica che sia stato rimosso soltanto l'attributo dichiarato numero " + removedAttributeIndex)
            .containsExactlyInAnyOrderElementsOf(expectedDeclaredAttributeIds);
    }

    @Then("gli attributi dichiarati del gruppo sono rimasti invariati")
    public void checkSameDeclaredAttributes() {
        EServiceAttributesGroupKey key = getEServiceAttributesKey();
        List<UUID> actualDeclaredAttributeIds =
            getDeclaredAttributes(from(key)).stream()
                .map(attribute -> attribute.getAttribute().getId())
                .toList();

        List<UUID> expectedDeclaredAttributeIds = new ArrayList<>(
            sharedStepsContext.getEServicesCommonContext().getDeclaredAttributesIds());

        assertThat(actualDeclaredAttributeIds)
            .as("Verifica che gli attributi dichiarati siano rimasti invariati")
            .containsExactlyInAnyOrderElementsOf(expectedDeclaredAttributeIds);
    }

    @Given("l'utente rimuove l'attributo dichiarato numero {collectionIndex} dal gruppo dell'e-service con successo")
    public void successfullyDeleteDeclaredAttribute(int attributeToRemoveIndex) {
        removeEServiceDeclaredAttribute(attributeToRemoveIndex);
        checkRemovedDeclaredAttribute(attributeToRemoveIndex);
    }

    @When("l'utente tenta di rimuovere l'attributo dichiarato numero {collectionIndex} dal gruppo dell'e-service indicando un e-service id inesistente")
    public void removeEServiceDeclaredAttributeWithUnexistentEServiceId(int attributeIndex) {
        EServiceAttributesGroupKey key = getEServiceAttributesKey().withEServiceId(UUID.randomUUID());
        UUID attributeId = sharedStepsContext.getEServicesCommonContext().getDeclaredAttributesIds()
            .get(attributeIndex);
        performDeleteDeclaredAttribute(key, attributeId);
    }

    @When("l'utente tenta di rimuovere l'attributo dichiarato numero {collectionIndex} dal gruppo dell'e-service indicando un descriptor id inesistente")
    public void removeEServiceDeclaredAttributeWithUnexistentDescriptorId(int attributeIndex) {
        EServiceAttributesGroupKey key = getEServiceAttributesKey().withDescriptorId(UUID.randomUUID());
        UUID attributeId = sharedStepsContext.getEServicesCommonContext().getDeclaredAttributesIds()
            .get(attributeIndex);
        performDeleteDeclaredAttribute(key, attributeId);
    }

    @When("l'utente tenta di rimuovere l'attributo dichiarato numero {collectionIndex} dal gruppo dell'e-service indicando un group index inesistente")
    public void removeEServiceDeclaredAttributeWithUnexistentGroupIndex(int attributeIndex) {
        EServiceAttributesGroupKey key = getEServiceAttributesKey().withGroupIndex(999);
        UUID attributeId = sharedStepsContext.getEServicesCommonContext().getDeclaredAttributesIds()
            .get(attributeIndex);
        performDeleteDeclaredAttribute(key, attributeId);
    }

    @When("l'utente tenta di rimuovere un attributo dichiarato dal gruppo dell'e-service indicando un attribute id inesistente")
    public void removeEServiceDeclaredAttributeWithUnexistentAttributeId() {
        EServiceAttributesGroupKey key = getEServiceAttributesKey();
        UUID attributeId = UUID.randomUUID();
        performDeleteDeclaredAttribute(key, attributeId);
    }
}
