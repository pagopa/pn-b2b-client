package it.pagopa.pn.interop.cucumber.steps.m2m.eservice_template;

import static org.assertj.core.api.Assertions.assertThat;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.interop.common.IHttpExecutor;
import it.pagopa.interop.e_service_template.IM2MEServiceTemplateAttributeClient;
import it.pagopa.interop.eservice.service.EServiceAttribute;
import it.pagopa.interop.generated.openapi.clients.bff.model.Attribute;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.VerifiedAttribute;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.ResourceSnapshots;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.common.EServiceTemplateInfo;
import it.pagopa.pn.interop.cucumber.steps.e_service_template.shared.EServiceTemplateStepContext;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;

public class EserviceTemplateVerifiedAttributesSteps {

    private final IM2MEServiceTemplateAttributeClient templateClient;
    private final SharedStepsContext sharedStepsContext;
    private final IHttpExecutor httpExecutor;

    private final ResourceSnapshots<List<EServiceAttribute<VerifiedAttribute>>> verifiedAttributeSnapshots = new ResourceSnapshots<>();

    public EserviceTemplateVerifiedAttributesSteps(
        SharedStepsContext sharedStepsContext,
        ClientTokenConfigurator clientTokenConfigurator
    ) {
        this.templateClient = clientTokenConfigurator.getM2mEServiceTemplateAttributeClient();
        this.sharedStepsContext = sharedStepsContext;
        this.httpExecutor = sharedStepsContext.getHttpCallExecutor();
    }

    @Given("l'utente crea un gruppo di attributi associati all'e-service template contenente {int} attribut(o)(i) verificat(o)(i) con successo")
    public void createVerifiedAttributeGroup(int attributesQt) {
        EServiceTemplateInfo template = sharedStepsContext.getEServiceTemplateStepContext()
            .getLastTemplateManaged();
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
            () -> templateClient.createVerifiedAttributesGroup(template.getId(), template.getLastVersionId(),
                attributesIds));

        if (httpExecutor.getResponseStatus().is2xxSuccessful()) {
            if (attributesQt > 0) {
                List<EServiceAttribute<VerifiedAttribute>> response = (List<EServiceAttribute<VerifiedAttribute>>) httpExecutor.getResponse();
                sharedStepsContext.getEServiceTemplateStepContext()
                    .setGroupId(response.get(0).getGroupIndex());
                sharedStepsContext.getEServiceTemplateStepContext().addVerifiedAttributes(attributesIds);
            }
        } else {
            throw new IllegalStateException("Si è verificato un errore durante la creazione dei gruppi di attributi verificati: %s".formatted(httpExecutor.getErrorMessage()));
        }
    }

    @And("[si prende nota dello stato degli attributi verificati del gruppo dell'e-service template]")
    public void storeEServiceVerifiedAttribute() {
        EServiceTemplateAttributesGroupKey attributesKey = getEServiceTemplateAttributesKey();
        performGetEServiceVerifiedAttributes(attributesKey);
        List<EServiceAttribute<VerifiedAttribute>> verifiedAttributes = (List<EServiceAttribute<VerifiedAttribute>>) httpExecutor.getResponse();
        List<EServiceAttribute<VerifiedAttribute>> verifiedAttributeInGroup = verifiedAttributes.stream()
            .filter(attr -> attr.getGroupIndex().equals(attributesKey.getGroupIndex()))
            .toList();
        verifiedAttributeSnapshots.addSnapshot(verifiedAttributeInGroup);
    }

    private void performGetEServiceVerifiedAttributes(EServiceTemplateAttributesKey key) {
        httpExecutor.performCall(() -> getVerifiedAttributes(key));
    }

    private List<EServiceAttribute<VerifiedAttribute>> getVerifiedAttributes(
        EServiceTemplateAttributesKey key) {
        return templateClient.getVerifiedAttributes(
            key.getTemplateId(),
            key.getVersionId()
        );
    }

    private EServiceTemplateAttributesGroupKey getEServiceTemplateAttributesKey() {
        EServiceTemplateInfo template = sharedStepsContext.getEServiceTemplateStepContext()
            .getLastTemplateManaged();
        return EServiceTemplateAttributesGroupKey.builder()
            .templateId(template.getId())
            .versionId(template.getLastVersionId())
            .groupIndex(sharedStepsContext.getEServiceTemplateStepContext().getGroupId())
            .build();
    }

    @When("l'utente tenta di aggiungere l'attributo verificato numero {collectionIndex} al gruppo dell'e-service template")
    public void addEServiceVerifiedAttribute(int index) {
        this.addEServiceVerifiedAttributes(index, index);
    }

    @When("l'utente tenta di aggiungere gli attributi verificati numeri da {collectionIndex} a {collectionIndex} al gruppo dell'e-service template")
    public void addEServiceVerifiedAttributes(int startIncludedIndex, int endIncludedIndex) {
        EServiceTemplateAttributesGroupKey attributesKey = getEServiceTemplateAttributesKey();
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

    @When("l'utente tenta di aggiungere l'attributo verificato numero {collectionIndex} al gruppo dell'e-service template indicando un e-service template id inesistente")
    public void addEServiceVerifiedAttributesWithUnexistentEServiceId(int attributeIndex) {
        EServiceTemplateAttributesGroupKey attributesKey = getEServiceTemplateAttributesKey().withTemplateId(
            UUID.randomUUID());
        List<UUID> attributeIdsToAdd = getAttributeIdsToAdd(attributeIndex, attributeIndex);

        performAddEServiceVerifiedAttributes(attributesKey, attributeIdsToAdd);
    }

    @When("l'utente tenta di aggiungere l'attributo verificato numero {collectionIndex} al gruppo dell'e-service template indicando un descriptor id inesistente")
    public void addEServiceVerifiedAttributesWithUnexistentDescriptorId(int attributeIndex) {
        EServiceTemplateAttributesGroupKey attributesKey = getEServiceTemplateAttributesKey().withVersionId(
            UUID.randomUUID());
        List<UUID> attributeIdsToAdd = getAttributeIdsToAdd(attributeIndex, attributeIndex);

        performAddEServiceVerifiedAttributes(attributesKey, attributeIdsToAdd);
    }

    @When("l'utente tenta di aggiungere l'attributo verificato numero {collectionIndex} al gruppo dell'e-service template indicando un group index inesistente")
    public void addEServiceVerifiedAttributesWithUnexistentGroupIndex(int attributeIndex) {
        EServiceTemplateAttributesGroupKey attributesKey = getEServiceTemplateAttributesKey().withGroupIndex(999);
        List<UUID> attributeIdsToAdd = getAttributeIdsToAdd(attributeIndex, attributeIndex);

        performAddEServiceVerifiedAttributes(attributesKey, attributeIdsToAdd);
    }

    @When("l'utente tenta di aggiungere degli attributi verificati al gruppo dell'e-service template indicando degli attribute ids inesistenti")
    public void addEServiceVerifiedAttributesWithUnexistentIds() {
        EServiceTemplateAttributesGroupKey attributesKey = getEServiceTemplateAttributesKey();
        List<UUID> attributeIdsToAdd = List.of(UUID.randomUUID());

        performAddEServiceVerifiedAttributes(attributesKey, attributeIdsToAdd);
    }

    private void performAddEServiceVerifiedAttributes(EServiceTemplateAttributesGroupKey key,
        List<UUID> attributeIdsToAdd) {
        httpExecutor.performCall(() -> templateClient.addVerifiedAttributes(
            key.getTemplateId(),
            key.getVersionId(),
            key.getGroupIndex(),
            attributeIdsToAdd));
        if (httpExecutor.getResponseStatus().is2xxSuccessful()) {
            sharedStepsContext.getEServiceTemplateStepContext()
                .addVerifiedAttributes(attributeIdsToAdd);
        }
    }

    @Then("gli attributi verificati sono stati aggiunti correttamente al gruppo dell'e-service template")
    public void checkEServiceVerifiedAttributes() {
        List<UUID> actualVerifiedAttributes = verifiedAttributeSnapshots.getActualSnapshot()
            .stream()
            .map(attributeWrapper -> attributeWrapper.getAttribute().getId())
            .toList();
        List<UUID> addedVerifiedAttributes = sharedStepsContext.getEServiceTemplateStepContext()
            .getVerifiedAttributesIds();

        assertThat(actualVerifiedAttributes)
            .as("Verifica che tra gli attributi verificati associati all'e-service template ci siano quelli caricati")
            .containsAll(addedVerifiedAttributes);
    }

    @Then("i precedenti attributi verificati del gruppo dell'e-service template sono rimasti invariati")
    public void checkUnmodifiedVerifiedAttributes() {
        List<EServiceAttribute<VerifiedAttribute>> previousSnapshot = verifiedAttributeSnapshots.getPreviousSnapshot();
        List<EServiceAttribute<VerifiedAttribute>> actualSnapshot = verifiedAttributeSnapshots.getActualSnapshot();

        assertThat(actualSnapshot)
            .as("Verifica che non ci siano state modifiche inattese agli attributi aggiunti precedentemente")
            .containsAll(previousSnapshot);
    }

    @Then("gli attributi verificati del gruppo dell'e-service template sono rimasti invariati")
    public void checkVerifiedAttributes() {
        List<EServiceAttribute<VerifiedAttribute>> previousSnapshot = verifiedAttributeSnapshots.getPreviousSnapshot();
        List<EServiceAttribute<VerifiedAttribute>> actualSnapshot = verifiedAttributeSnapshots.getActualSnapshot();

        assertThat(actualSnapshot)
            .as("Verifica che non ci siano state modifiche inattese agli attributi aggiunti precedentemente")
            .containsExactlyInAnyOrderElementsOf(previousSnapshot);
    }

    @When("l'utente tenta di reperire gli attributi verificati dal gruppo dell'e-service template")
    public void getEServiceVerifiedAttributes() {
        EServiceTemplateAttributesGroupKey attributesKey = getEServiceTemplateAttributesKey();
        performGetEServiceVerifiedAttributes(attributesKey);
    }

    @When("l'utente tenta di reperire gli attributi verificati dal gruppo dell'e-service template indicando un e-service template id inesistente")
    public void getEServiceVerifiedAttributesWithUnexistentEServiceId() {
        EServiceTemplateAttributesGroupKey attributesKey = getEServiceTemplateAttributesKey().withTemplateId(
            UUID.randomUUID());
        performGetEServiceVerifiedAttributes(attributesKey);
    }

    @When("l'utente tenta di reperire gli attributi verificati dal gruppo dell'e-service template indicando un descriptor id inesistente")
    public void getEServiceVerifiedAttributesWithUnexistentDescriptorId() {
        EServiceTemplateAttributesGroupKey attributesKey = getEServiceTemplateAttributesKey().withVersionId(
            UUID.randomUUID());
        performGetEServiceVerifiedAttributes(attributesKey);
    }

    @Then("gli attributi verificati ottenuti sono coerenti con quelli aggiunti nel template")
    public void checkGotVerifiedAttributes() {
        List<UUID> expectedVerifiedAttributesIds = sharedStepsContext.getEServiceTemplateStepContext()
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

    @When("l'utente tenta di rimuovere l'attributo verificato numero {collectionIndex} dal gruppo dell'e-service template")
    public void removeEServiceVerifiedAttribute(int attributeIndex) {
        EServiceTemplateAttributesGroupKey key = getEServiceTemplateAttributesKey();
        UUID attributeId = sharedStepsContext.getEServiceTemplateStepContext().getVerifiedAttributesIds()
            .get(attributeIndex);
        performDeleteVerifiedAttribute(key, attributeId);
    }

    @When("l'utente tenta di rimuovere l'attributo verificato già eliminato numero {collectionIndex} dal gruppo dell'e-service template")
    public void removeDeletedEServiceVerifiedAttribute(int attributeIndex) {
        EServiceTemplateAttributesGroupKey key = getEServiceTemplateAttributesKey();
        UUID attributeId = sharedStepsContext.getEServiceTemplateStepContext().getRemovedVerifiedAttributesIds().get(attributeIndex);
        performDeleteVerifiedAttribute(key, attributeId);
    }

    private void performDeleteVerifiedAttribute(EServiceTemplateAttributesGroupKey key, UUID attributeId) {
        httpExecutor.performCall(() -> templateClient.deleteVerifiedAttribute(
            key.getTemplateId(),
            key.getVersionId(),
            key.getGroupIndex(),
            attributeId
        ));
    }

    @Then("è stato rimosso dall'e-service template solo l'attributo verificato numero {collectionIndex}")
    public void checkRemovedVerifiedAttribute(int removedAttributeIndex) {
        EServiceTemplateAttributesGroupKey key = getEServiceTemplateAttributesKey();
        List<UUID> actualVerifiedAttributeIds = getVerifiedAttributes(key).stream()
                .map(attribute -> attribute.getAttribute().getId())
                .toList();

        EServiceTemplateStepContext context = sharedStepsContext.getEServiceTemplateStepContext();
        List<UUID> expectedVerifiedAttributeIds = new ArrayList<>(context.getVerifiedAttributesIds());
        expectedVerifiedAttributeIds.remove(removedAttributeIndex);

        assertThat(actualVerifiedAttributeIds)
            .as("Verifica che sia stato rimosso soltanto l'attributo verificato numero " + removedAttributeIndex)
            .containsExactlyInAnyOrderElementsOf(expectedVerifiedAttributeIds);

        UUID removedAttributeId = context.getVerifiedAttributesIds().remove(removedAttributeIndex);
        context.getRemovedVerifiedAttributesIds().add(removedAttributeId);
    }

    @Then("gli attributi verificati del gruppo nel template sono rimasti invariati")
    public void checkSameVerifiedAttributes() {
        EServiceTemplateAttributesGroupKey key = getEServiceTemplateAttributesKey();
        List<UUID> actualVerifiedAttributeIds =
            getVerifiedAttributes(key).stream()
                .map(attribute -> attribute.getAttribute().getId())
                .toList();

        List<UUID> expectedVerifiedAttributeIds = new ArrayList<>(
            sharedStepsContext.getEServiceTemplateStepContext().getVerifiedAttributesIds());

        assertThat(actualVerifiedAttributeIds)
            .as("Verifica che gli attributi verificati siano rimasti invariati")
            .containsExactlyInAnyOrderElementsOf(expectedVerifiedAttributeIds);
    }

    @Given("l'utente rimuove l'attributo verificato numero {collectionIndex} dal gruppo dell'e-service template con successo")
    public void successfullyDeleteVerifiedAttribute(int attributeToRemoveIndex) {
        removeEServiceVerifiedAttribute(attributeToRemoveIndex);
        checkRemovedVerifiedAttribute(attributeToRemoveIndex);
    }

    @When("l'utente tenta di rimuovere l'attributo verificato numero {collectionIndex} dal gruppo dell'e-service template indicando un e-service template id inesistente")
    public void removeEServiceVerifiedAttributeWithUnexistentEServiceId(int attributeIndex) {
        EServiceTemplateAttributesGroupKey key = getEServiceTemplateAttributesKey().withTemplateId(UUID.randomUUID());
        UUID attributeId = sharedStepsContext.getEServiceTemplateStepContext().getVerifiedAttributesIds()
            .get(attributeIndex);
        performDeleteVerifiedAttribute(key, attributeId);
    }

    @When("l'utente tenta di rimuovere l'attributo verificato numero {collectionIndex} dal gruppo dell'e-service template indicando un descriptor id inesistente")
    public void removeEServiceVerifiedAttributeWithUnexistentDescriptorId(int attributeIndex) {
        EServiceTemplateAttributesGroupKey key = getEServiceTemplateAttributesKey().withVersionId(UUID.randomUUID());
        UUID attributeId = sharedStepsContext.getEServiceTemplateStepContext().getVerifiedAttributesIds()
            .get(attributeIndex);
        performDeleteVerifiedAttribute(key, attributeId);
    }

    @When("l'utente tenta di rimuovere l'attributo verificato numero {collectionIndex} dal gruppo dell'e-service template indicando un group index inesistente")
    public void removeEServiceVerifiedAttributeWithUnexistentGroupIndex(int attributeIndex) {
        EServiceTemplateAttributesGroupKey key = getEServiceTemplateAttributesKey().withGroupIndex(999);
        UUID attributeId = sharedStepsContext.getEServiceTemplateStepContext().getVerifiedAttributesIds()
            .get(attributeIndex);
        performDeleteVerifiedAttribute(key, attributeId);
    }

    @When("l'utente tenta di rimuovere un attributo verificato dal gruppo dell'e-service template indicando un attribute id inesistente")
    public void removeEServiceVerifiedAttributeWithUnexistentAttributeId() {
        EServiceTemplateAttributesGroupKey key = getEServiceTemplateAttributesKey();
        UUID attributeId = UUID.randomUUID();
        performDeleteVerifiedAttribute(key, attributeId);
    }
}
