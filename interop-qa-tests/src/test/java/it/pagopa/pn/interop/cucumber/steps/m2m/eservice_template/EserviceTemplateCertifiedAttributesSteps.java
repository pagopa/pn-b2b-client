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
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.CertifiedAttribute;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.ResourceSnapshots;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.common.EServiceTemplateInfo;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;

public class EserviceTemplateCertifiedAttributesSteps {

    private final IM2MEServiceTemplateAttributeClient templateClient;
    private final SharedStepsContext sharedStepsContext;
    private final IHttpExecutor httpExecutor;

    private final ResourceSnapshots<List<EServiceAttribute<CertifiedAttribute>>> certifiedAttributeSnapshots = new ResourceSnapshots<>();

    public EserviceTemplateCertifiedAttributesSteps(
        SharedStepsContext sharedStepsContext,
        ClientTokenConfigurator clientTokenConfigurator
    ) {
        this.templateClient = clientTokenConfigurator.getM2mEServiceTemplateAttributeClient();
        this.sharedStepsContext = sharedStepsContext;
        this.httpExecutor = sharedStepsContext.getHttpCallExecutor();
    }

    @Given("l'utente crea un gruppo di attributi associati all'e-service template contenente {int} attribut(o)(i) certificat(o)(i) con successo")
    public void createCertifiedAttributeGroup(int attributesQt) {
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
                "E' prevista l'esistenza di almeno %d attributi certificati, ma ne sono stati rilevati %d".formatted(
                    attributesQt, actualAttributesQt));
        }

        httpExecutor.performCall(
            () -> templateClient.createCertifiedAttributesGroup(template.id(), template.lastVersionId(),
                attributesIds));

        if (httpExecutor.getResponseStatus().is2xxSuccessful()) {
            if (attributesQt > 0) {
                List<EServiceAttribute<CertifiedAttribute>> response = (List<EServiceAttribute<CertifiedAttribute>>) httpExecutor.getResponse();
                sharedStepsContext.getEServiceTemplateStepContext()
                    .setGroupId(response.get(0).getGroupIndex());
                sharedStepsContext.getEServiceTemplateStepContext().addCertifiedAttributes(attributesIds);
            }
        } else {
            throw new IllegalStateException("Si è verificato un errore durante la creazione dei gruppi di attributi certificati: %s".formatted(httpExecutor.getErrorMessage()));
        }
    }

    @And("[si prende nota dello stato degli attributi certificati del gruppo dell'e-service template]")
    public void storeEServiceCertifiedAttribute() {
        EServiceTemplateAttributesGroupKey attributesKey = getEServiceTemplateAttributesKey();
        performGetEServiceCertifiedAttributes(attributesKey);
        List<EServiceAttribute<CertifiedAttribute>> certifiedAttributes = (List<EServiceAttribute<CertifiedAttribute>>) httpExecutor.getResponse();
        List<EServiceAttribute<CertifiedAttribute>> certifiedAttributeInGroup = certifiedAttributes.stream()
            .filter(attr -> attr.getGroupIndex().equals(attributesKey.getGroupIndex()))
            .toList();
        certifiedAttributeSnapshots.addSnapshot(certifiedAttributeInGroup);
    }

    private void performGetEServiceCertifiedAttributes(EServiceTemplateAttributesKey key) {
        httpExecutor.performCall(() -> getCertifiedAttributes(key));
    }

    private List<EServiceAttribute<CertifiedAttribute>> getCertifiedAttributes(
        EServiceTemplateAttributesKey key) {
        return templateClient.getCertifiedAttributes(
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

    @When("l'utente tenta di aggiungere l'attributo certificato numero {collectionIndex} al gruppo dell'e-service template")
    public void addEServiceCertifiedAttribute(int index) {
        this.addEServiceCertifiedAttributes(index, index);
    }

    @When("l'utente tenta di aggiungere gli attributi certificati numeri da {collectionIndex} a {collectionIndex} al gruppo dell'e-service template")
    public void addEServiceCertifiedAttributes(int startIncludedIndex, int endIncludedIndex) {
        EServiceTemplateAttributesGroupKey attributesKey = getEServiceTemplateAttributesKey();
        List<UUID> attributeIdsToAdd = getAttributeIdsToAdd(startIncludedIndex, endIncludedIndex);

        performAddEServiceCertifiedAttributes(attributesKey, attributeIdsToAdd);
    }

    private List<UUID> getAttributeIdsToAdd(int startIncludedIndex, int endIncludedIndex) {
        return IntStream.range(startIncludedIndex, endIncludedIndex + 1)
            .mapToObj(
                i -> sharedStepsContext.getAttributeCommonContext().getCreatedAttributes().get(i))
            .map(Attribute::getId)
            .toList();
    }

    @When("l'utente tenta di aggiungere l'attributo certificato numero {collectionIndex} al gruppo dell'e-service template indicando un e-service template id inesistente")
    public void addEServiceCertifiedAttributesWithUnexistentEServiceId(int attributeIndex) {
        EServiceTemplateAttributesGroupKey attributesKey = getEServiceTemplateAttributesKey().withTemplateId(
            UUID.randomUUID());
        List<UUID> attributeIdsToAdd = getAttributeIdsToAdd(attributeIndex, attributeIndex);

        performAddEServiceCertifiedAttributes(attributesKey, attributeIdsToAdd);
    }

    @When("l'utente tenta di aggiungere l'attributo certificato numero {collectionIndex} al gruppo dell'e-service template indicando un descriptor id inesistente")
    public void addEServiceCertifiedAttributesWithUnexistentDescriptorId(int attributeIndex) {
        EServiceTemplateAttributesGroupKey attributesKey = getEServiceTemplateAttributesKey().withVersionId(
            UUID.randomUUID());
        List<UUID> attributeIdsToAdd = getAttributeIdsToAdd(attributeIndex, attributeIndex);

        performAddEServiceCertifiedAttributes(attributesKey, attributeIdsToAdd);
    }

    @When("l'utente tenta di aggiungere l'attributo certificato numero {collectionIndex} al gruppo dell'e-service template indicando un group index inesistente")
    public void addEServiceCertifiedAttributesWithUnexistentGroupIndex(int attributeIndex) {
        EServiceTemplateAttributesGroupKey attributesKey = getEServiceTemplateAttributesKey().withGroupIndex(999);
        List<UUID> attributeIdsToAdd = getAttributeIdsToAdd(attributeIndex, attributeIndex);

        performAddEServiceCertifiedAttributes(attributesKey, attributeIdsToAdd);
    }

    @When("l'utente tenta di aggiungere degli attributi certificati al gruppo dell'e-service template indicando degli attribute ids inesistenti")
    public void addEServiceCertifiedAttributesWithUnexistentIds() {
        EServiceTemplateAttributesGroupKey attributesKey = getEServiceTemplateAttributesKey();
        List<UUID> attributeIdsToAdd = List.of(UUID.randomUUID());

        performAddEServiceCertifiedAttributes(attributesKey, attributeIdsToAdd);
    }

    private void performAddEServiceCertifiedAttributes(EServiceTemplateAttributesGroupKey key,
        List<UUID> attributeIdsToAdd) {
        httpExecutor.performCall(() -> templateClient.addCertifiedAttributes(
            key.getTemplateId(),
            key.getVersionId(),
            key.getGroupIndex(),
            attributeIdsToAdd));
        if (httpExecutor.getResponseStatus().is2xxSuccessful()) {
            sharedStepsContext.getEServiceTemplateStepContext()
                .addCertifiedAttributes(attributeIdsToAdd);
        }
    }

    @Then("gli attributi certificati sono stati aggiunti correttamente al gruppo dell'e-service template")
    public void checkEServiceCertifiedAttributes() {
        List<UUID> actualCertifiedAttributes = certifiedAttributeSnapshots.getActualSnapshot()
            .stream()
            .map(attributeWrapper -> attributeWrapper.getAttribute().getId())
            .toList();
        List<UUID> addedCertifiedAttributes = sharedStepsContext.getEServiceTemplateStepContext()
            .getCertifiedAttributesIds();

        assertThat(actualCertifiedAttributes)
            .as("Verifica che tra gli attributi certificati associati all'e-service template ci siano quelli caricati")
            .containsAll(addedCertifiedAttributes);
    }

    @Then("i precedenti attributi certificati del gruppo dell'e-service template sono rimasti invariati")
    public void checkUnmodifiedCertifiedAttributes() {
        List<EServiceAttribute<CertifiedAttribute>> previousSnapshot = certifiedAttributeSnapshots.getPreviousSnapshot();
        List<EServiceAttribute<CertifiedAttribute>> actualSnapshot = certifiedAttributeSnapshots.getActualSnapshot();

        assertThat(actualSnapshot)
            .as("Verifica che non ci siano state modifiche inattese agli attributi aggiunti precedentemente")
            .containsAll(previousSnapshot);
    }

    @Then("gli attributi certificati del gruppo dell'e-service template sono rimasti invariati")
    public void checkCertifiedAttributes() {
        List<EServiceAttribute<CertifiedAttribute>> previousSnapshot = certifiedAttributeSnapshots.getPreviousSnapshot();
        List<EServiceAttribute<CertifiedAttribute>> actualSnapshot = certifiedAttributeSnapshots.getActualSnapshot();

        assertThat(actualSnapshot)
            .as("Verifica che non ci siano state modifiche inattese agli attributi aggiunti precedentemente")
            .containsExactlyInAnyOrderElementsOf(previousSnapshot);
    }

    @When("l'utente tenta di reperire gli attributi certificati dal gruppo dell'e-service template")
    public void getEServiceCertifiedAttributes() {
        EServiceTemplateAttributesGroupKey attributesKey = getEServiceTemplateAttributesKey();
        performGetEServiceCertifiedAttributes(attributesKey);
    }

    @When("l'utente tenta di reperire gli attributi certificati dal gruppo dell'e-service template indicando un e-service template id inesistente")
    public void getEServiceCertifiedAttributesWithUnexistentEServiceId() {
        EServiceTemplateAttributesGroupKey attributesKey = getEServiceTemplateAttributesKey().withTemplateId(
            UUID.randomUUID());
        performGetEServiceCertifiedAttributes(attributesKey);
    }

    @When("l'utente tenta di reperire gli attributi certificati dal gruppo dell'e-service template indicando un descriptor id inesistente")
    public void getEServiceCertifiedAttributesWithUnexistentDescriptorId() {
        EServiceTemplateAttributesGroupKey attributesKey = getEServiceTemplateAttributesKey().withVersionId(
            UUID.randomUUID());
        performGetEServiceCertifiedAttributes(attributesKey);
    }

    @Then("gli attributi certificati ottenuti sono coerenti con quelli aggiunti nel template")
    public void checkGotCertifiedAttributes() {
        List<UUID> expectedCertifiedAttributesIds = sharedStepsContext.getEServiceTemplateStepContext()
            .getCertifiedAttributesIds();
        List<EServiceAttribute<CertifiedAttribute>> actualCertifiedAttributes =
            (List<EServiceAttribute<CertifiedAttribute>>) httpExecutor.getResponse();
        List<UUID> actualCertifiedAttributesIds = actualCertifiedAttributes.stream()
            .map(att -> att.getAttribute().getId())
            .toList();

        assertThat(actualCertifiedAttributesIds)
            .as("Verifica che gli attributi certificati reperiti siano identici a quelli precedentemente aggiunti")
            .containsExactlyInAnyOrderElementsOf(expectedCertifiedAttributesIds);
    }

    @When("l'utente tenta di rimuovere l'attributo certificato numero {collectionIndex} dal gruppo dell'e-service template")
    public void removeEServiceCertifiedAttribute(int attributeIndex) {
        EServiceTemplateAttributesGroupKey key = getEServiceTemplateAttributesKey();
        UUID attributeId = sharedStepsContext.getEServiceTemplateStepContext().getCertifiedAttributesIds()
            .get(attributeIndex);
        performDeleteCertifiedAttribute(key, attributeId);
    }

    private void performDeleteCertifiedAttribute(EServiceTemplateAttributesGroupKey key, UUID attributeId) {
        httpExecutor.performCall(() -> templateClient.deleteCertifiedAttribute(
            key.getTemplateId(),
            key.getVersionId(),
            key.getGroupIndex(),
            attributeId
        ));
    }

    @Then("è stato rimosso dall'e-service template solo l'attributo certificato numero {collectionIndex}")
    public void checkRemovedCertifiedAttribute(int removedAttributeIndex) {
        EServiceTemplateAttributesGroupKey key = getEServiceTemplateAttributesKey();
        List<UUID> actualCertifiedAttributeIds = getCertifiedAttributes(key).stream()
                .map(attribute -> attribute.getAttribute().getId())
                .toList();

        List<UUID> expectedCertifiedAttributeIds = new ArrayList<>(
            sharedStepsContext.getEServiceTemplateStepContext().getCertifiedAttributesIds());
        expectedCertifiedAttributeIds.remove(removedAttributeIndex);

        assertThat(actualCertifiedAttributeIds)
            .as("Verifica che sia stato rimosso soltanto l'attributo certificato numero " + removedAttributeIndex)
            .containsExactlyInAnyOrderElementsOf(expectedCertifiedAttributeIds);
    }

    @Then("gli attributi certificati del gruppo del template sono rimasti invariati")
    public void checkSameCertifiedAttributes() {
        EServiceTemplateAttributesGroupKey key = getEServiceTemplateAttributesKey();
        List<UUID> actualCertifiedAttributeIds =
            getCertifiedAttributes(key).stream()
                .map(attribute -> attribute.getAttribute().getId())
                .toList();

        List<UUID> expectedCertifiedAttributeIds = new ArrayList<>(
            sharedStepsContext.getEServiceTemplateStepContext().getCertifiedAttributesIds());

        assertThat(actualCertifiedAttributeIds)
            .as("Verifica che gli attributi certificati siano rimasti invariati")
            .containsExactlyInAnyOrderElementsOf(expectedCertifiedAttributeIds);
    }

    @Given("l'utente rimuove l'attributo certificato numero {collectionIndex} dal gruppo dell'e-service template con successo")
    public void successfullyDeleteCertifiedAttribute(int attributeToRemoveIndex) {
        removeEServiceCertifiedAttribute(attributeToRemoveIndex);
        checkRemovedCertifiedAttribute(attributeToRemoveIndex);
    }

    @When("l'utente tenta di rimuovere l'attributo certificato numero {collectionIndex} dal gruppo dell'e-service template indicando un e-service template id inesistente")
    public void removeEServiceCertifiedAttributeWithUnexistentEServiceId(int attributeIndex) {
        EServiceTemplateAttributesGroupKey key = getEServiceTemplateAttributesKey().withTemplateId(UUID.randomUUID());
        UUID attributeId = sharedStepsContext.getEServiceTemplateStepContext().getCertifiedAttributesIds()
            .get(attributeIndex);
        performDeleteCertifiedAttribute(key, attributeId);
    }

    @When("l'utente tenta di rimuovere l'attributo certificato numero {collectionIndex} dal gruppo dell'e-service template indicando un descriptor id inesistente")
    public void removeEServiceCertifiedAttributeWithUnexistentDescriptorId(int attributeIndex) {
        EServiceTemplateAttributesGroupKey key = getEServiceTemplateAttributesKey().withVersionId(UUID.randomUUID());
        UUID attributeId = sharedStepsContext.getEServiceTemplateStepContext().getCertifiedAttributesIds()
            .get(attributeIndex);
        performDeleteCertifiedAttribute(key, attributeId);
    }

    @When("l'utente tenta di rimuovere l'attributo certificato numero {collectionIndex} dal gruppo dell'e-service template indicando un group index inesistente")
    public void removeEServiceCertifiedAttributeWithUnexistentGroupIndex(int attributeIndex) {
        EServiceTemplateAttributesGroupKey key = getEServiceTemplateAttributesKey().withGroupIndex(999);
        UUID attributeId = sharedStepsContext.getEServiceTemplateStepContext().getCertifiedAttributesIds()
            .get(attributeIndex);
        performDeleteCertifiedAttribute(key, attributeId);
    }

    @When("l'utente tenta di rimuovere un attributo certificato dal gruppo dell'e-service template indicando un attribute id inesistente")
    public void removeEServiceCertifiedAttributeWithUnexistentAttributeId() {
        EServiceTemplateAttributesGroupKey key = getEServiceTemplateAttributesKey();
        UUID attributeId = UUID.randomUUID();
        performDeleteCertifiedAttribute(key, attributeId);
    }
}
