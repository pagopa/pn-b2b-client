package it.pagopa.pn.interop.cucumber.steps.e_service_template.version.crud;

import static java.util.Objects.nonNull;
import static org.assertj.core.api.Assertions.fail;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.interop.authorization.service.utils.PollingPredicateException;
import it.pagopa.interop.authorization.service.utils.PollingService;
import it.pagopa.interop.e_service_template.IEServiceTemplateClient;
import it.pagopa.interop.e_service_template.mapper.DescriptorAttributesMapper;
import it.pagopa.interop.generated.openapi.clients.bff.model.DescriptorAttributeSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.DescriptorAttributes;
import it.pagopa.interop.generated.openapi.clients.bff.model.DescriptorAttributesSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTemplateAttributesSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTemplateVersionAttributeSeed;
import it.pagopa.interop.utils.HttpCallExecutor;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.e_service_template.shared.EServiceTemplateStepContext;
import it.pagopa.pn.interop.cucumber.steps.e_service_template.shared.EServiceTemplateTestAssistant;
import java.util.List;
import java.util.UUID;
import lombok.Data;
import org.apache.commons.lang3.BooleanUtils;
import org.jeasy.random.EasyRandom;
import org.springframework.http.ResponseEntity;

/** Cucumber steps involving creation, editing, viewing or deletion
 * of E-service template versions */
@Data
public class EServiceTemplateVersionAttributeUpdateSteps {
    private final ClientTokenConfigurator clientTokenConfigurator;
    private final SharedStepsContext sharedStepsContext;
    private final IEServiceTemplateClient eServiceTemplateClient;
    private final HttpCallExecutor httpCallExecutor;
    private final PollingService pollingService;
    private final EServiceTemplateTestAssistant testAssistant;
    private final EServiceTemplateStepContext templateContext;
    private final DescriptorAttributesMapper descriptorAttributesMapper;
    private final EasyRandom easyRandom;

    private DescriptorAttributesSeed lastAttributesUpdateSeed;

    public EServiceTemplateVersionAttributeUpdateSteps(ClientTokenConfigurator clientTokenConfigurator,
        SharedStepsContext sharedStepsContext,
        EServiceTemplateTestAssistant testAssistant,
        EServiceTemplateStepContext templateContext,
        DescriptorAttributesMapper descriptorAttributesMapper
    ) {
        this.clientTokenConfigurator = clientTokenConfigurator;
        this.sharedStepsContext = sharedStepsContext;
        this.eServiceTemplateClient = clientTokenConfigurator.getEServiceTemplateClient();
        this.httpCallExecutor = sharedStepsContext.getHttpCallExecutor();
        this.pollingService = sharedStepsContext.getPollingService();
        this.testAssistant = testAssistant;
        this.templateContext = templateContext;
        this.descriptorAttributesMapper = descriptorAttributesMapper;
        this.easyRandom = new EasyRandom(templateContext.getEasyRandomParameters());
    }

    @When("l'utente tenta la modifica degli attributi della versione dell'e-service template")
    public void editEServiceTemplateVersionAttributes() {
        UUID eServiceTemplateId = templateContext.getLastTemplateManaged().id();
        UUID eServiceTemplateVersionId = templateContext.getLastTemplateManaged().lastVersionId();

        //lastTemplateVersionUpdateSeed contiene, tra le altre, cose, gli attributi aggiunti l'ultima volta
//        lastAttributesUpdateSeed = this.descriptorAttributesMapper.mapSeedsToSeeds(this.templateContext.getLastTemplateVersionUpdateSeed().getAttributes());
        lastAttributesUpdateSeed = this.descriptorAttributesMapper.mapSeedsToSeeds(new EServiceTemplateAttributesSeed()
                .addCertifiedItem(List.of(new EServiceTemplateVersionAttributeSeed().id(UUID.randomUUID()).explicitAttributeVerification(true))));

        List<List<DescriptorAttributeSeed>> certified = lastAttributesUpdateSeed.getCertified();
//        certified.add(List.of(new DescriptorAttributeSeed())); // aggiungo 1 attributo

        Boolean newAttribute = !BooleanUtils.toBoolean(certified.get(0).get(0).getExplicitAttributeVerification());

        certified.get(0).get(0).setId(UUID.randomUUID());

        certified.get(0).get(0).setExplicitAttributeVerification(newAttribute); // modifico 1 campo di 1 attributo
        editEServiceTemplateVersionAttributes(eServiceTemplateId, eServiceTemplateVersionId, lastAttributesUpdateSeed);
    }

    @When("l'utente tenta la modifica degli attributi della versione dell'e-service template indicando una specifica vuota")
    public void editEServiceTemplateVersionAttributesWithEmptySpec() {
        editEServiceTemplateVersionAttributes(
            templateContext.getLastTemplateManaged().id(),
            templateContext.getLastTemplateManaged().lastVersionId(),
            new DescriptorAttributesSeed());
    }

    @When("l'utente tenta la modifica degli attributi della versione dell'e-service template aggiungendone di nuovi")
    public void editEServiceTemplateVersionAttributesAddingNew() {
        UUID eServiceTemplateId = templateContext.getLastTemplateManaged().id();
        UUID eServiceTemplateVersionId = templateContext.getLastTemplateManaged().lastVersionId();
        lastAttributesUpdateSeed = this.descriptorAttributesMapper.mapSeedsToSeeds(this.templateContext.getLastTemplateVersionUpdateSeed().getAttributes());
        DescriptorAttributeSeed newAttribute = easyRandom.nextObject(DescriptorAttributeSeed.class);
        lastAttributesUpdateSeed.getCertified().add(List.of(newAttribute));
        editEServiceTemplateVersionAttributes(eServiceTemplateId, eServiceTemplateVersionId, lastAttributesUpdateSeed);
    }

    @When("l'utente tenta la modifica degli attributi della versione di un e-service template inesistente")
    public void editNonExistentEServiceTemplateVersionAttributes() {
        editEServiceTemplateVersionAttributes(UUID.randomUUID(), UUID.randomUUID(), nextDescriptorAttributesSeed());
    }

    @When("l'utente tenta la modifica degli attributi di una versione inesistente dell'e-service template")
    public void editEServiceTemplateNonExistentVersionAttributes() {
        editEServiceTemplateVersionAttributes(templateContext.getLastTemplateManaged().id(), UUID.randomUUID(), nextDescriptorAttributesSeed());
    }

    @Then("la modifica degli attributi della versione dell'e-service template è stata effettuata correttamente")
    public void checkEServiceTemplateVersionAttributesEdited() {
        try {
            pollingService.makePolling(
                () -> httpCallExecutor.performCall(
                    () -> eServiceTemplateClient.getEServiceTemplateVersionWithHttpInfo(
                        sharedStepsContext.getXCorrelationId(),
                        templateContext.getLastTemplateManaged().id(),
                        templateContext.getLastTemplateManaged().lastVersionId()),
                    ResponseEntity::getStatusCode),
                res -> {
                    if(res.getStatusCode().is2xxSuccessful() && nonNull(res.getBody())) {
                        DescriptorAttributes retrievedAttributes = res.getBody().getAttributes();
                        DescriptorAttributesSeed retrievedAttributesSeed = this.descriptorAttributesMapper.map(retrievedAttributes);
                        return retrievedAttributesSeed.equals(lastAttributesUpdateSeed);
                    }
                    return false;
                },
                "Gli attributi della versione dell'e-service template non sono stati modificati correttamente"
            );
        } catch (PollingPredicateException e) {
            // TODO occorrerebbero più dettagli, sul modello di quelli dati solitamente in automatico da AssertJ
            fail("Gli attributi della versione dell'e-service template non sono stati modificati correttamente");
        }
    }

    private void editEServiceTemplateVersionAttributes(UUID eServiceTemplateId,
        UUID eServiceTemplateVersionId, DescriptorAttributesSeed lastAttributesUpdateSeed) {
        String userToken = sharedStepsContext.getUserToken();
        clientTokenConfigurator.setBearerToken(userToken);
        httpCallExecutor.performCall(
            () -> eServiceTemplateClient.updateEServiceTemplateVersionAttributesWithHttpInfo(
                sharedStepsContext.getXCorrelationId(),
                eServiceTemplateId,
                eServiceTemplateVersionId,
                lastAttributesUpdateSeed),
            ResponseEntity::getStatusCode);
    }

    private DescriptorAttributesSeed nextDescriptorAttributesSeed() {
        return descriptorAttributesMapper.mapSeedsToSeeds(testAssistant.nextAttributesSeed());
    }
}
