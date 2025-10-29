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
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.DeclaredAttribute;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.ResourceSnapshots;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.common.EServiceTemplateInfo;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;

public class EserviceTemplateDeclaredAttributesSteps {

    private final IM2MEServiceTemplateAttributeClient templateClient;
    private final SharedStepsContext sharedStepsContext;
    private final IHttpExecutor httpExecutor;

    private final ResourceSnapshots<List<EServiceAttribute<DeclaredAttribute>>> declaredAttributeSnapshots = new ResourceSnapshots<>();

    public EserviceTemplateDeclaredAttributesSteps(
        SharedStepsContext sharedStepsContext,
        ClientTokenConfigurator clientTokenConfigurator
    ) {
        this.templateClient = clientTokenConfigurator.getM2mEServiceTemplateAttributeClient();
        this.sharedStepsContext = sharedStepsContext;
        this.httpExecutor = sharedStepsContext.getHttpCallExecutor();
    }

    @Given("l'utente crea un gruppo di attributi associati all'e-service template contenente {int} attribut(o)(i) dichiarat(o)(i) con successo")
    public void createDeclaredAttributeGroup(int attributesQt) {
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
                "E' prevista l'esistenza di almeno %d attributi dichiarati, ma ne sono stati rilevati %d".formatted(
                    attributesQt, actualAttributesQt));
        }

        httpExecutor.performCall(
            () -> templateClient.createDeclaredAttributesGroup(template.id(), template.lastVersionId(),
                attributesIds));

        if (httpExecutor.getResponseStatus().is2xxSuccessful()) {
            if (attributesQt > 0) {
                List<EServiceAttribute<DeclaredAttribute>> response = (List<EServiceAttribute<DeclaredAttribute>>) httpExecutor.getResponse();
                sharedStepsContext.getEServiceTemplateStepContext()
                    .setGroupId(response.get(0).getGroupIndex());
                sharedStepsContext.getEServiceTemplateStepContext().addDeclaredAttributes(attributesIds);
            }
        } else {
            throw new IllegalStateException("Si è verificato un errore durante la creazione dei gruppi di attributi dichiarati: %s".formatted(httpExecutor.getErrorMessage()));
        }
    }

    @And("[si prende nota dello stato degli attributi dichiarati del gruppo dell'e-service template]")
    public void storeEServiceDeclaredAttribute() {
        EServiceTemplateAttributesGroupKey attributesKey = getEServiceTemplateAttributesKey();
        performGetEServiceDeclaredAttributes(attributesKey);
        List<EServiceAttribute<DeclaredAttribute>> declaredAttributes = (List<EServiceAttribute<DeclaredAttribute>>) httpExecutor.getResponse();
        List<EServiceAttribute<DeclaredAttribute>> declaredAttributeInGroup = declaredAttributes.stream()
            .filter(attr -> attr.getGroupIndex().equals(attributesKey.getGroupIndex()))
            .toList();
        declaredAttributeSnapshots.addSnapshot(declaredAttributeInGroup);
    }

    private void performGetEServiceDeclaredAttributes(EServiceTemplateAttributesKey key) {
        httpExecutor.performCall(() -> getDeclaredAttributes(key));
    }

    private List<EServiceAttribute<DeclaredAttribute>> getDeclaredAttributes(
        EServiceTemplateAttributesKey key) {
        return templateClient.getDeclaredAttributes(
            key.getTemplateId(),
            key.getVersionId()
        );
    }

    private EServiceTemplateAttributesGroupKey getEServiceTemplateAttributesKey() {
        EServiceTemplateInfo template = sharedStepsContext.getEServiceTemplateStepContext()
            .getLastTemplateManaged();
        return EServiceTemplateAttributesGroupKey.builder()
            .templateId(template.id())
            .versionId(template.lastVersionId())
            .groupIndex(sharedStepsContext.getEServiceTemplateStepContext().getGroupId())
            .build();
    }

    @When("l'utente tenta di aggiungere l'attributo dichiarato numero {collectionIndex} al gruppo dell'e-service template")
    public void addEServiceDeclaredAttribute(int index) {
        this.addEServiceDeclaredAttributes(index, index);
    }

    @When("l'utente tenta di aggiungere gli attributi dichiarati numeri da {collectionIndex} a {collectionIndex} al gruppo dell'e-service template")
    public void addEServiceDeclaredAttributes(int startIncludedIndex, int endIncludedIndex) {
        EServiceTemplateAttributesGroupKey attributesKey = getEServiceTemplateAttributesKey();
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

    @When("l'utente tenta di aggiungere l'attributo dichiarato numero {collectionIndex} al gruppo dell'e-service template indicando un e-service template id inesistente")
    public void addEServiceDeclaredAttributesWithUnexistentEServiceId(int attributeIndex) {
        EServiceTemplateAttributesGroupKey attributesKey = getEServiceTemplateAttributesKey().withTemplateId(
            UUID.randomUUID());
        List<UUID> attributeIdsToAdd = getAttributeIdsToAdd(attributeIndex, attributeIndex);

        performAddEServiceDeclaredAttributes(attributesKey, attributeIdsToAdd);
    }

    @When("l'utente tenta di aggiungere l'attributo dichiarato numero {collectionIndex} al gruppo dell'e-service template indicando un descriptor id inesistente")
    public void addEServiceDeclaredAttributesWithUnexistentDescriptorId(int attributeIndex) {
        EServiceTemplateAttributesGroupKey attributesKey = getEServiceTemplateAttributesKey().withVersionId(
            UUID.randomUUID());
        List<UUID> attributeIdsToAdd = getAttributeIdsToAdd(attributeIndex, attributeIndex);

        performAddEServiceDeclaredAttributes(attributesKey, attributeIdsToAdd);
    }

    @When("l'utente tenta di aggiungere l'attributo dichiarato numero {collectionIndex} al gruppo dell'e-service template indicando un group index inesistente")
    public void addEServiceDeclaredAttributesWithUnexistentGroupIndex(int attributeIndex) {
        EServiceTemplateAttributesGroupKey attributesKey = getEServiceTemplateAttributesKey().withGroupIndex(999);
        List<UUID> attributeIdsToAdd = getAttributeIdsToAdd(attributeIndex, attributeIndex);

        performAddEServiceDeclaredAttributes(attributesKey, attributeIdsToAdd);
    }

    @When("l'utente tenta di aggiungere degli attributi dichiarati al gruppo dell'e-service template indicando degli attribute ids inesistenti")
    public void addEServiceDeclaredAttributesWithUnexistentIds() {
        EServiceTemplateAttributesGroupKey attributesKey = getEServiceTemplateAttributesKey();
        List<UUID> attributeIdsToAdd = List.of(UUID.randomUUID());

        performAddEServiceDeclaredAttributes(attributesKey, attributeIdsToAdd);
    }

    private void performAddEServiceDeclaredAttributes(EServiceTemplateAttributesGroupKey key,
        List<UUID> attributeIdsToAdd) {
        httpExecutor.performCall(() -> templateClient.addDeclaredAttributes(
            key.getTemplateId(),
            key.getVersionId(),
            key.getGroupIndex(),
            attributeIdsToAdd));
        if (httpExecutor.getResponseStatus().is2xxSuccessful()) {
            sharedStepsContext.getEServiceTemplateStepContext()
                .addDeclaredAttributes(attributeIdsToAdd);
        }
    }

    @Then("gli attributi dichiarati sono stati aggiunti correttamente al gruppo dell'e-service template")
    public void checkEServiceDeclaredAttributes() {
        List<UUID> actualDeclaredAttributes = declaredAttributeSnapshots.getActualSnapshot()
            .stream()
            .map(attributeWrapper -> attributeWrapper.getAttribute().getId())
            .toList();
        List<UUID> addedDeclaredAttributes = sharedStepsContext.getEServiceTemplateStepContext()
            .getDeclaredAttributesIds();

        assertThat(actualDeclaredAttributes)
            .as("Verifica che tra gli attributi dichiarati associati all'e-service template ci siano quelli caricati")
            .containsAll(addedDeclaredAttributes);
    }

    @Then("i precedenti attributi dichiarati del gruppo dell'e-service template sono rimasti invariati")
    public void checkUnmodifiedDeclaredAttributes() {
        List<EServiceAttribute<DeclaredAttribute>> previousSnapshot = declaredAttributeSnapshots.getPreviousSnapshot();
        List<EServiceAttribute<DeclaredAttribute>> actualSnapshot = declaredAttributeSnapshots.getActualSnapshot();

        assertThat(actualSnapshot)
            .as("Verifica che non ci siano state modifiche inattese agli attributi aggiunti precedentemente")
            .containsAll(previousSnapshot);
    }

    @Then("gli attributi dichiarati del gruppo dell'e-service template sono rimasti invariati")
    public void checkDeclaredAttributes() {
        List<EServiceAttribute<DeclaredAttribute>> previousSnapshot = declaredAttributeSnapshots.getPreviousSnapshot();
        List<EServiceAttribute<DeclaredAttribute>> actualSnapshot = declaredAttributeSnapshots.getActualSnapshot();

        assertThat(actualSnapshot)
            .as("Verifica che non ci siano state modifiche inattese agli attributi aggiunti precedentemente")
            .containsExactlyInAnyOrderElementsOf(previousSnapshot);
    }

    @When("l'utente tenta di reperire gli attributi dichiarati dal gruppo dell'e-service template")
    public void getEServiceDeclaredAttributes() {
        EServiceTemplateAttributesGroupKey attributesKey = getEServiceTemplateAttributesKey();
        performGetEServiceDeclaredAttributes(attributesKey);
    }

    @When("l'utente tenta di reperire gli attributi dichiarati dal gruppo dell'e-service template indicando un e-service template id inesistente")
    public void getEServiceDeclaredAttributesWithUnexistentEServiceId() {
        EServiceTemplateAttributesGroupKey attributesKey = getEServiceTemplateAttributesKey().withTemplateId(
            UUID.randomUUID());
        performGetEServiceDeclaredAttributes(attributesKey);
    }

    @When("l'utente tenta di reperire gli attributi dichiarati dal gruppo dell'e-service template indicando un descriptor id inesistente")
    public void getEServiceDeclaredAttributesWithUnexistentDescriptorId() {
        EServiceTemplateAttributesGroupKey attributesKey = getEServiceTemplateAttributesKey().withVersionId(
            UUID.randomUUID());
        performGetEServiceDeclaredAttributes(attributesKey);
    }

    @Then("gli attributi dichiarati ottenuti sono coerenti con quelli aggiunti nel template")
    public void checkGotDeclaredAttributes() {
        List<UUID> expectedDeclaredAttributesIds = sharedStepsContext.getEServiceTemplateStepContext()
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

    @When("l'utente tenta di rimuovere l'attributo dichiarato numero {collectionIndex} dal gruppo dell'e-service template")
    public void removeEServiceDeclaredAttribute(int attributeIndex) {
        EServiceTemplateAttributesGroupKey key = getEServiceTemplateAttributesKey();
        UUID attributeId = sharedStepsContext.getEServiceTemplateStepContext().getDeclaredAttributesIds()
            .get(attributeIndex);
        performDeleteDeclaredAttribute(key, attributeId);
    }

    private void performDeleteDeclaredAttribute(EServiceTemplateAttributesGroupKey key, UUID attributeId) {
        httpExecutor.performCall(() -> templateClient.deleteDeclaredAttribute(
            key.getTemplateId(),
            key.getVersionId(),
            key.getGroupIndex(),
            attributeId
        ));
    }

    @Then("è stato rimosso dall'e-service template solo l'attributo dichiarato numero {collectionIndex}")
    public void checkRemovedDeclaredAttribute(int removedAttributeIndex) {
        EServiceTemplateAttributesGroupKey key = getEServiceTemplateAttributesKey();
        List<UUID> actualDeclaredAttributeIds = getDeclaredAttributes(key).stream()
                .map(attribute -> attribute.getAttribute().getId())
                .toList();

        List<UUID> expectedDeclaredAttributeIds = new ArrayList<>(
            sharedStepsContext.getEServiceTemplateStepContext().getDeclaredAttributesIds());
        expectedDeclaredAttributeIds.remove(removedAttributeIndex);

        assertThat(actualDeclaredAttributeIds)
            .as("Verifica che sia stato rimosso soltanto l'attributo dichiarato numero " + removedAttributeIndex)
            .containsExactlyInAnyOrderElementsOf(expectedDeclaredAttributeIds);
    }

    @Then("gli attributi dichiarati del gruppo del template sono rimasti invariati")
    public void checkSameDeclaredAttributes() {
        EServiceTemplateAttributesGroupKey key = getEServiceTemplateAttributesKey();
        List<UUID> actualDeclaredAttributeIds =
            getDeclaredAttributes(key).stream()
                .map(attribute -> attribute.getAttribute().getId())
                .toList();

        List<UUID> expectedDeclaredAttributeIds = new ArrayList<>(
            sharedStepsContext.getEServiceTemplateStepContext().getDeclaredAttributesIds());

        assertThat(actualDeclaredAttributeIds)
            .as("Verifica che gli attributi dichiarati siano rimasti invariati")
            .containsExactlyInAnyOrderElementsOf(expectedDeclaredAttributeIds);
    }

    @Given("l'utente rimuove l'attributo dichiarato numero {collectionIndex} dal gruppo dell'e-service template con successo")
    public void successfullyDeleteDeclaredAttribute(int attributeToRemoveIndex) {
        removeEServiceDeclaredAttribute(attributeToRemoveIndex);
        checkRemovedDeclaredAttribute(attributeToRemoveIndex);
    }

    @When("l'utente tenta di rimuovere l'attributo dichiarato numero {collectionIndex} dal gruppo dell'e-service template indicando un e-service template id inesistente")
    public void removeEServiceDeclaredAttributeWithUnexistentEServiceId(int attributeIndex) {
        EServiceTemplateAttributesGroupKey key = getEServiceTemplateAttributesKey().withTemplateId(UUID.randomUUID());
        UUID attributeId = sharedStepsContext.getEServiceTemplateStepContext().getDeclaredAttributesIds()
            .get(attributeIndex);
        performDeleteDeclaredAttribute(key, attributeId);
    }

    @When("l'utente tenta di rimuovere l'attributo dichiarato numero {collectionIndex} dal gruppo dell'e-service template indicando un descriptor id inesistente")
    public void removeEServiceDeclaredAttributeWithUnexistentDescriptorId(int attributeIndex) {
        EServiceTemplateAttributesGroupKey key = getEServiceTemplateAttributesKey().withVersionId(UUID.randomUUID());
        UUID attributeId = sharedStepsContext.getEServiceTemplateStepContext().getDeclaredAttributesIds()
            .get(attributeIndex);
        performDeleteDeclaredAttribute(key, attributeId);
    }

    @When("l'utente tenta di rimuovere l'attributo dichiarato numero {collectionIndex} dal gruppo dell'e-service template indicando un group index inesistente")
    public void removeEServiceDeclaredAttributeWithUnexistentGroupIndex(int attributeIndex) {
        EServiceTemplateAttributesGroupKey key = getEServiceTemplateAttributesKey().withGroupIndex(999);
        UUID attributeId = sharedStepsContext.getEServiceTemplateStepContext().getDeclaredAttributesIds()
            .get(attributeIndex);
        performDeleteDeclaredAttribute(key, attributeId);
    }

    @When("l'utente tenta di rimuovere un attributo dichiarato dal gruppo dell'e-service template indicando un attribute id inesistente")
    public void removeEServiceDeclaredAttributeWithUnexistentAttributeId() {
        EServiceTemplateAttributesGroupKey key = getEServiceTemplateAttributesKey();
        UUID attributeId = UUID.randomUUID();
        performDeleteDeclaredAttribute(key, attributeId);
    }
}
