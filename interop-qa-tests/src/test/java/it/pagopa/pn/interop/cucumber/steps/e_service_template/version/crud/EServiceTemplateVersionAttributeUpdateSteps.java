package it.pagopa.pn.interop.cucumber.steps.e_service_template.version.crud;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.interop.authorization.service.utils.PollingService;
import it.pagopa.interop.common.IHttpExecutor;
import it.pagopa.interop.e_service_template.IEServiceTemplateClient;
import it.pagopa.interop.e_service_template.mapper.DescriptorAttributesMapper;
import it.pagopa.interop.generated.openapi.clients.bff.model.*;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.e_service_template.shared.EServiceTemplateTestAssistant;
import lombok.Data;
import org.jeasy.random.EasyRandom;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;

/** Cucumber steps involving creation, editing, viewing or deletion
 * of E-service template versions */
@Data
public class EServiceTemplateVersionAttributeUpdateSteps {
    private final ClientTokenConfigurator clientTokenConfigurator;
    private final SharedStepsContext sharedStepsContext;
    private final IEServiceTemplateClient eServiceTemplateClient;
    private final IHttpExecutor httpCallExecutor;
    private final PollingService pollingService;
    private final EServiceTemplateTestAssistant testAssistant;
    private final DescriptorAttributesMapper descriptorAttributesMapper;
    private final EasyRandom easyRandom;

    private DescriptorAttributesSeed lastAttributesUpdateSeed;

    public EServiceTemplateVersionAttributeUpdateSteps(ClientTokenConfigurator clientTokenConfigurator,
        SharedStepsContext sharedStepsContext,
        EServiceTemplateTestAssistant testAssistant,
        DescriptorAttributesMapper descriptorAttributesMapper
    ) {
        this.clientTokenConfigurator = clientTokenConfigurator;
        this.sharedStepsContext = sharedStepsContext;
        this.eServiceTemplateClient = clientTokenConfigurator.getEServiceTemplateClient();
        this.httpCallExecutor = sharedStepsContext.getHttpCallExecutor();
        this.pollingService = sharedStepsContext.getPollingService();
        this.testAssistant = testAssistant;
        this.descriptorAttributesMapper = descriptorAttributesMapper;
        this.easyRandom = new EasyRandom(sharedStepsContext.getEServiceTemplateStepContext().getEasyRandomParameters());
    }

    @When("l'utente tenta di aggiungere l'attributo creato alla versione dell'e-service template usando l'API specifica")
    public void editEServiceTemplateVersionAttributes() {
        // NOTA che al momento funziona solo con attributi certificati. Qualora ci fosse necessità
        // di attributi di altro tipo basterà parametrizzare.
        List<DescriptorAttributeSeed> certified = new ArrayList<>();
        List<DescriptorAttributeSeed> declared = new ArrayList<>();
        List<DescriptorAttributeSeed> verified = new ArrayList<>();

        for(Attribute attribute : sharedStepsContext.getAttributeCommonContext().getCreatedAttributes()) {
            List<DescriptorAttributeSeed> seedList = switch (attribute.getKind()) {
                case CERTIFIED, CERTIFIED_DISCRETE -> certified;
                case DECLARED -> declared;
                case VERIFIED -> verified;
                default -> throw new IllegalArgumentException("Tipo di attributo non supportato: " + attribute.getKind());
            };
            DescriptorAttributeSeed seed = new DescriptorAttributeSeed()
                .id(attribute.getId())
                .explicitAttributeVerification(false);
            seedList.add(seed);
        }

        DescriptorAttributesSeed attributesSeed = new DescriptorAttributesSeed();
        if (!certified.isEmpty()) {
            attributesSeed.addCertifiedItem(certified);
        }
        if (!declared.isEmpty()) {
            attributesSeed.addDeclaredItem(declared);
        }
        if (!verified.isEmpty()) {
            attributesSeed.addVerifiedItem(verified);
        }
        sharedStepsContext.getEServiceTemplateStepContext().setLastDescriptorAttributesSeed(attributesSeed);

        editEServiceTemplateVersionAttributes(
            this.sharedStepsContext.getEServiceTemplateStepContext().getLastTemplateManaged().getId(),
            this.sharedStepsContext.getEServiceTemplateStepContext().getLastTemplateManaged().getLastVersionId(),
            this.sharedStepsContext.getEServiceTemplateStepContext().getLastDescriptorAttributesSeed());
    }

    @When("l'utente tenta di aggiungere un nuovo gruppo di attributi alla versione dell'e-service template usando l'API specifica")
    public void editEServiceTemplateVersionAttributesAddGroup() {
        List<DescriptorAttributeSeed> certified = sharedStepsContext.getAttributeCommonContext().getCreatedAttributes().stream()
            .map(attribute -> new DescriptorAttributeSeed()
                .id(attribute.getId())
                .explicitAttributeVerification(false))
            .toList();
        DescriptorAttributesSeed attributesSeed = new DescriptorAttributesSeed()
            .certified(List.of(certified));
        sharedStepsContext.getEServiceTemplateStepContext().setLastDescriptorAttributesSeed(attributesSeed);

        editEServiceTemplateVersionAttributes(
            this.sharedStepsContext.getEServiceTemplateStepContext().getLastTemplateManaged().getId(),
            this.sharedStepsContext.getEServiceTemplateStepContext().getLastTemplateManaged().getLastVersionId(),
            this.sharedStepsContext.getEServiceTemplateStepContext().getLastDescriptorAttributesSeed());
    }

    @When("l'utente tenta la modifica degli attributi della versione dell'e-service template indicando una specifica vuota")
    public void editEServiceTemplateVersionAttributesWithEmptySpec() {
        editEServiceTemplateVersionAttributes(
            sharedStepsContext.getEServiceTemplateStepContext().getLastTemplateManaged().getId(),
            sharedStepsContext.getEServiceTemplateStepContext().getLastTemplateManaged().getLastVersionId(),
            new DescriptorAttributesSeed());
    }

    @When("l'utente tenta la modifica degli attributi della versione di un e-service template inesistente")
    public void editNonExistentEServiceTemplateVersionAttributes() {
        editEServiceTemplateVersionAttributes(UUID.randomUUID(), UUID.randomUUID(), nextDescriptorAttributesSeed());
    }

    @When("l'utente tenta la modifica degli attributi di una versione inesistente dell'e-service template")
    public void editEServiceTemplateNonExistentVersionAttributes() {
        editEServiceTemplateVersionAttributes(sharedStepsContext.getEServiceTemplateStepContext().getLastTemplateManaged().getId(), UUID.randomUUID(), nextDescriptorAttributesSeed());
    }

    @Then("la modifica degli attributi è stata effettuata correttamente")
    public void checkEServiceTemplateVersionAttributesEdited() {
        Predicate<EServiceTemplateVersionDetails> attributesMatch = version -> {
            DescriptorAttributes retrievedAttributes = version.getAttributes();
            DescriptorAttributesSeed retrievedAttributesSeed = this.descriptorAttributesMapper.map(retrievedAttributes);
            return retrievedAttributesSeed.equals(sharedStepsContext.getEServiceTemplateStepContext().getLastDescriptorAttributesSeed());
        };
        testAssistant.checkEServiceTemplateVersion(attributesMatch, "Gli attributi della versione dell'e-service template non sono stati modificati correttamente");
    }

    private void editEServiceTemplateVersionAttributes(UUID eServiceTemplateId,
        UUID eServiceTemplateVersionId, DescriptorAttributesSeed lastAttributesUpdateSeed) {
        String userToken = sharedStepsContext.getUserToken();
        clientTokenConfigurator.setBearerToken(userToken);
        httpCallExecutor.performCall(
            () -> eServiceTemplateClient.updateEServiceTemplateVersionAttributesWithHttpInfo(
                eServiceTemplateId,
                eServiceTemplateVersionId,
                lastAttributesUpdateSeed),
            ResponseEntity::getStatusCode);
    }

    private DescriptorAttributesSeed nextDescriptorAttributesSeed() {
        return descriptorAttributesMapper.mapSeedsToSeeds(testAssistant.nextAttributesSeed());
    }
}
