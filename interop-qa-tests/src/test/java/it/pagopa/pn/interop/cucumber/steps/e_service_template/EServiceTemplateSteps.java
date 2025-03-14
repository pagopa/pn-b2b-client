package it.pagopa.pn.interop.cucumber.steps.e_service_template;

import static java.util.Objects.nonNull;
import static java.util.Objects.requireNonNull;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.interop.authorization.service.utils.IdentityService;
import it.pagopa.interop.authorization.service.utils.PollingPredicateException;
import it.pagopa.interop.authorization.service.utils.PollingService;
import it.pagopa.interop.e_service_template.IEServiceTemplateClient;
import it.pagopa.interop.e_service_template.mapper.DescriptorAttributesMapper;
import it.pagopa.interop.generated.openapi.clients.bff.model.CatalogEServiceTemplate;
import it.pagopa.interop.generated.openapi.clients.bff.model.CatalogEServiceTemplates;
import it.pagopa.interop.generated.openapi.clients.bff.model.CompactOrganization;
import it.pagopa.interop.generated.openapi.clients.bff.model.CompactOrganizations;
import it.pagopa.interop.generated.openapi.clients.bff.model.DescriptorAttributeSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.DescriptorAttributes;
import it.pagopa.interop.generated.openapi.clients.bff.model.DescriptorAttributesSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTemplateDescriptionUpdateSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTemplateDetails;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTemplateIntendedTargetUpdateSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTemplateNameUpdateSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTemplateVersionState;
import it.pagopa.interop.generated.openapi.clients.bff.model.ProducerEServiceTemplate;
import it.pagopa.interop.generated.openapi.clients.bff.model.ProducerEServiceTemplates;
import it.pagopa.interop.utils.HttpCallExecutor;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.DataPreparationService;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.e_service_template.shared.EServiceTemplateStepContext;
import it.pagopa.pn.interop.cucumber.steps.e_service_template.shared.EServiceTemplateTestAssistant;
import java.util.List;
import java.util.UUID;
import lombok.Data;
import org.assertj.core.api.Condition;
import org.jeasy.random.EasyRandom;
import org.springframework.http.ResponseEntity;

@Data
public class EServiceTemplateSteps {
    /* TODO 13/03/2025 almeno alcuni di questi attributi resteranno inutilizzati dopo lo smistamento
     *  degli step in classi dedicate, rimuoverli */
    private final ClientTokenConfigurator clientTokenConfigurator;
    private final DataPreparationService dataPreparationService;
    private final IdentityService identityService;
    private final SharedStepsContext sharedStepsContext;
    private final IEServiceTemplateClient eServiceTemplateClient;
    private final HttpCallExecutor httpCallExecutor;
    private final PollingService pollingService;
    private final DescriptorAttributesMapper descriptorAttributesMapper;
    private final EServiceTemplateTestAssistant testAssistant;
    private final EServiceTemplateStepContext templateContext;

    // TODO alcune di queste variabili andranno incapsulate in un bean di tipo Context
    private EServiceTemplateNameUpdateSeed lastTemplateNameUpdateSeed;
    private EServiceTemplateIntendedTargetUpdateSeed lastTemplateIntendedTargetUpdateSeed;
    private EServiceTemplateDescriptionUpdateSeed lastTemplateDescriptionUpdateSeed;
    private DescriptorAttributesSeed lastAttributesUpdateSeed;
    private final EasyRandom easyRandom;

    public EServiceTemplateSteps(ClientTokenConfigurator clientTokenConfigurator,
                                DataPreparationService dataPreparationService,
                                SharedStepsContext sharedStepsContext,
                                DescriptorAttributesMapper descriptorAttributesMapper,
                                EServiceTemplateTestAssistant testAssistant,
                                EServiceTemplateStepContext templateContext
        ) {
        this.clientTokenConfigurator = clientTokenConfigurator;
        this.dataPreparationService = dataPreparationService;
        this.sharedStepsContext = sharedStepsContext;
        this.identityService = sharedStepsContext.getIdentityService();
        this.eServiceTemplateClient = clientTokenConfigurator.getEServiceTemplateClient();
        this.httpCallExecutor = sharedStepsContext.getHttpCallExecutor();
        this.pollingService = sharedStepsContext.getPollingService();
        this.descriptorAttributesMapper = descriptorAttributesMapper;
        this.testAssistant = testAssistant;
        this.templateContext = templateContext;
        this.easyRandom = new EasyRandom(templateContext.getEasyRandomParameters());
    }

    private DescriptorAttributesSeed nextDescriptorAttributesSeed() {
        return descriptorAttributesMapper.mapSeedsToSeeds(testAssistant.nextAttributesSeed());
    }

    @When("l'utente tenta la modifica del nome dell'e-service template")
    public void editEServiceTemplateName() {
        UUID eServiceTemplateId = templateContext.getLastTemplateManaged().id();
        lastTemplateNameUpdateSeed = easyRandom.nextObject(EServiceTemplateNameUpdateSeed.class);
        editEServiceTemplateName(eServiceTemplateId, lastTemplateNameUpdateSeed);
    }

    @When("l'utente tenta la modifica del nome dell'e-service template specificando lo stesso nome")
    public void editEServiceTemplateNameWithSameName() {
        editEServiceTemplateNameWith(templateContext.getLastTemplateManaged().name());
    }

    @When("l'utente tenta la modifica del nome dell'e-service template specificando la stringa vuota")
    public void editEServiceTemplateNameWithEmptyName() {
        editEServiceTemplateNameWith("");
    }

    @When("l'utente tenta la modifica del nome dell'e-service template specificando NULL")
    public void editEServiceTemplateNameWithNullName() {
        editEServiceTemplateNameWith(null);
    }

    @When("l'utente tenta la modifica del nome di un e-service template inesistente")
    public void editNonExistentEServiceTemplateName() {
        editEServiceTemplateName(UUID.randomUUID(), easyRandom.nextObject(EServiceTemplateNameUpdateSeed.class));
    }

    private void editEServiceTemplateNameWith(String name) {
        UUID eServiceTemplateId = templateContext.getLastTemplateManaged().id();
        lastTemplateNameUpdateSeed = easyRandom.nextObject(EServiceTemplateNameUpdateSeed.class)
            .name(name);
        editEServiceTemplateName(eServiceTemplateId, lastTemplateNameUpdateSeed);
    }

    private void editEServiceTemplateName(UUID eServiceTemplateId,
        EServiceTemplateNameUpdateSeed lastTemplateNameUpdateSeed) {
        String userToken = getUserToken();
        clientTokenConfigurator.setBearerToken(userToken);
        httpCallExecutor.performCall(
            () -> eServiceTemplateClient.updateEServiceTemplateNameWithHttpInfo(
                sharedStepsContext.getXCorrelationId(),
                eServiceTemplateId,
                lastTemplateNameUpdateSeed),
            ResponseEntity::getStatusCode);
    }

    @Then("la modifica del nome dell'e-service template è stata effettuata correttamente")
    public void checkEServiceTemplateNameEdited() {
        UUID eServiceTemplateId = templateContext.getLastTemplateManaged().id();
        try {
            pollingService.makePolling(
                () -> httpCallExecutor.performCall(
                    () -> eServiceTemplateClient.getEServiceTemplateWithHttpInfo(
                        sharedStepsContext.getXCorrelationId(),
                        eServiceTemplateId),
                    ResponseEntity::getStatusCode),
                res -> {
                    if(res.getStatusCode().is2xxSuccessful() && nonNull(res.getBody())) {
                        return this.areConsistent(res.getBody(), lastTemplateNameUpdateSeed);
                    }
                    return false;
                },
                "Il nome dell'e-service template non è stato modificato correttamente"
            );
        } catch (PollingPredicateException e) {
            fail("Il nome dell'e-service template non è stato modificato correttamente");
        }
    }

    @When("l'utente tenta la modifica della descrizione dello scopo dell'e-service template")
    public void editEServiceTemplateIntendedTarget() {
        UUID eServiceTemplateId = templateContext.getLastTemplateManaged().id();
        lastTemplateIntendedTargetUpdateSeed = easyRandom.nextObject(EServiceTemplateIntendedTargetUpdateSeed.class);
        editEServiceTemplateIntendedTarget(eServiceTemplateId, lastTemplateIntendedTargetUpdateSeed);
    }

    @When("l'utente tenta la modifica della descrizione dello scopo dell'e-service template specificando la stessa descrizione")
    public void editEServiceTemplateIntendedTargetWithSameIntendedTarget() {
        editEServiceTemplateIntendedTargetWith(templateContext.getLastTemplateManaged().intendedTarget());
    }

    @When("l'utente tenta la modifica della descrizione dello scopo dell'e-service template specificando la stringa vuota")
    public void editEServiceTemplateIntendedTargetWith() {
        editEServiceTemplateIntendedTargetWith("");
    }

    @When("l'utente tenta la modifica della descrizione dello scopo dell'e-service template specificando NULL")
    public void editEServiceTemplateIntendedTargetWithNullIntendedTarget() {
        editEServiceTemplateIntendedTargetWith(null);
    }

    @When("l'utente tenta la modifica della descrizione dello scopo di un e-service template inesistente")
    public void editNonExistentEServiceTemplateIntendedTarget() {
        editEServiceTemplateIntendedTarget(UUID.randomUUID(), easyRandom.nextObject(EServiceTemplateIntendedTargetUpdateSeed.class));
    }

    private void editEServiceTemplateIntendedTargetWith(String description) {
        UUID eServiceTemplateId = templateContext.getLastTemplateManaged().id();
        lastTemplateIntendedTargetUpdateSeed = easyRandom.nextObject(EServiceTemplateIntendedTargetUpdateSeed.class)
            .intendedTarget(description);
        editEServiceTemplateIntendedTarget(eServiceTemplateId, lastTemplateIntendedTargetUpdateSeed);
    }

    private void editEServiceTemplateIntendedTarget(UUID eServiceTemplateId,
        EServiceTemplateIntendedTargetUpdateSeed lastTemplateIntendedTargetUpdateSeed) {
        String userToken = getUserToken();
        clientTokenConfigurator.setBearerToken(userToken);
        httpCallExecutor.performCall(
            () -> eServiceTemplateClient.updateEServiceIntendedTargetWithHttpInfo(
                sharedStepsContext.getXCorrelationId(),
                eServiceTemplateId,
                lastTemplateIntendedTargetUpdateSeed),
            ResponseEntity::getStatusCode);
    }

    @Then("la modifica della descrizione dello scopo dell'e-service template è stata effettuata correttamente")
    public void checkEServiceTemplateIntendedTargetEdited() {
        UUID eServiceTemplateId = templateContext.getLastTemplateManaged().id();
        try {
            pollingService.makePolling(
                () -> httpCallExecutor.performCall(
                    () -> eServiceTemplateClient.getEServiceTemplateWithHttpInfo(
                        sharedStepsContext.getXCorrelationId(),
                        eServiceTemplateId),
                    ResponseEntity::getStatusCode),
                res -> {
                    if(res.getStatusCode().is2xxSuccessful() && nonNull(res.getBody())) {
                        EServiceTemplateDetails template = res.getBody();
                        return template.getIntendedTarget().equals(
                            lastTemplateIntendedTargetUpdateSeed.getIntendedTarget());
                    }
                    return false;
                },
                "La descrizione dello scopo dell'e-service template non è stata modificata correttamente"
            );
        } catch (PollingPredicateException e) {
            fail("La descrizione dello scopo dell'e-service template non è stata modificata correttamente");
        }
    }

    @When("l'utente tenta la modifica della descrizione dell'e-service template")
    public void editEServiceTemplateDescription() {
        UUID eServiceTemplateId = templateContext.getLastTemplateManaged().id();
        lastTemplateDescriptionUpdateSeed = easyRandom.nextObject(EServiceTemplateDescriptionUpdateSeed.class);
        editEServiceTemplateDescription(eServiceTemplateId, lastTemplateDescriptionUpdateSeed);
    }

    @When("l'utente tenta la modifica della descrizione dell'e-service template specificando la stessa descrizione")
    public void editEServiceTemplateDescriptionWithSameDescription() {
        editEServiceTemplateDescriptionWith(templateContext.getLastTemplateManaged().eServiceDescription());
    }

    @When("l'utente tenta la modifica della descrizione dell'e-service template specificando la stringa vuota")
    public void editEServiceTemplateDescriptionWith() {
        editEServiceTemplateDescriptionWith("");
    }

    @When("l'utente tenta la modifica della descrizione dell'e-service template specificando NULL")
    public void editEServiceTemplateDescriptionWithNullDescription() {
        editEServiceTemplateDescriptionWith(null);
    }

    @When("l'utente tenta la modifica della descrizione di un e-service template inesistente")
    public void editNonExistentEServiceTemplateDescription() {
        editEServiceTemplateDescription(UUID.randomUUID(), easyRandom.nextObject(EServiceTemplateDescriptionUpdateSeed.class));
    }

    private void editEServiceTemplateDescriptionWith(String description) {
        UUID eServiceTemplateId = templateContext.getLastTemplateManaged().id();
        lastTemplateDescriptionUpdateSeed = easyRandom.nextObject(EServiceTemplateDescriptionUpdateSeed.class)
            .description(description);
        editEServiceTemplateDescription(eServiceTemplateId, lastTemplateDescriptionUpdateSeed);
    }

    private void editEServiceTemplateDescription(UUID eServiceTemplateId,
        EServiceTemplateDescriptionUpdateSeed lastTemplateDescriptionUpdateSeed) {
        String userToken = getUserToken();
        clientTokenConfigurator.setBearerToken(userToken);
        httpCallExecutor.performCall(
            () -> eServiceTemplateClient.updateEServiceTemplateDescriptionWithHttpInfo(
                sharedStepsContext.getXCorrelationId(),
                eServiceTemplateId,
                lastTemplateDescriptionUpdateSeed),
            ResponseEntity::getStatusCode);
    }

    @Then("la modifica della descrizione dell'e-service template è stata effettuata correttamente")
    public void checkEServiceTemplateDescriptionEdited() {
        UUID eServiceTemplateId = templateContext.getLastTemplateManaged().id();
        try {
            pollingService.makePolling(
                () -> httpCallExecutor.performCall(
                    () -> eServiceTemplateClient.getEServiceTemplateWithHttpInfo(
                        sharedStepsContext.getXCorrelationId(),
                        eServiceTemplateId),
                    ResponseEntity::getStatusCode),
                res -> {
                    if(res.getStatusCode().is2xxSuccessful() && nonNull(res.getBody())) {
                        EServiceTemplateDetails template = res.getBody();
                        return template.getDescription().equals(
                            lastTemplateDescriptionUpdateSeed.getDescription());
                    }
                    return false;
                },
                "La descrizione dell'e-service template non è stata modificata correttamente"
            );
        } catch (PollingPredicateException e) {
            fail("La descrizione dell'e-service template non è stata modificata correttamente");
        }
    }

    @When("l'utente tenta la modifica degli attributi della versione dell'e-service template")
    public void editEServiceTemplateVersionAttributes() {
        UUID eServiceTemplateId = templateContext.getLastTemplateManaged().id();
        UUID eServiceTemplateVersionId = templateContext.getLastTemplateManaged().lastVersionId();

        //lastTemplateVersionUpdateSeed contiene, tra le altre, cose, gli attributi aggiunti l'ultima volta
        lastAttributesUpdateSeed = this.descriptorAttributesMapper.mapSeedsToSeeds(this.templateContext.getLastTemplateVersionUpdateSeed().getAttributes());

        Boolean explicitAttributeVerification = lastAttributesUpdateSeed.getCertified().get(0)
            .get(0).getExplicitAttributeVerification();
        lastAttributesUpdateSeed.getCertified().get(0).get(0).setExplicitAttributeVerification(!explicitAttributeVerification); // modifico 1 campo di 1 attributo
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
        String userToken = getUserToken();
        clientTokenConfigurator.setBearerToken(userToken);
        httpCallExecutor.performCall(
            () -> eServiceTemplateClient.updateEServiceTemplateVersionAttributesWithHttpInfo(
                sharedStepsContext.getXCorrelationId(),
                eServiceTemplateId,
                eServiceTemplateVersionId,
                lastAttributesUpdateSeed),
            ResponseEntity::getStatusCode);
    }

    @When("l'utente tenta la visualizzazione del catalogo degli e-service template")
    public void getEServiceTemplatesCatalog() {
        String userToken = getUserToken();
        clientTokenConfigurator.setBearerToken(userToken);
        httpCallExecutor.performCall(
            () -> eServiceTemplateClient.getEServiceTemplatesCatalog(sharedStepsContext.getXCorrelationId()),
            ResponseEntity::getStatusCode);
    }

    @Then("il catalogo degli e-service template contiene esattamente {int} elementi tutti in stato {eServiceTemplateVersionState}")
    public void checkEServiceTemplatesCatalogContainsElementsInState(int expectedCount, EServiceTemplateVersionState expectedState) {
        /* TODO la precondizione di questo metodo sarebbe che lo status code sia positivo, che il body non sia null e che il catalogo non sia vuoto.
         * Migliorare questo e altri step così che venga sempre fatto un check preventivo, eventualmente aiutandosi
         * con un framework con le Precondition come Google Guava. Spunti: https://www.sw-engineering-candies.com/blog-1/comparison-of-ways-to-check-preconditions-in-java
         */
        CatalogEServiceTemplates catalog = ((ResponseEntity<CatalogEServiceTemplates>) httpCallExecutor.getResponse()).getBody();
        List<CatalogEServiceTemplate> templatesInCatalog = catalog.getResults();

        Condition<CatalogEServiceTemplate> published = new Condition<>(
            template -> template.getPublishedVersion().getState() == expectedState,
            "of state %s", expectedState);
        assertThat(templatesInCatalog)
            .hasSize(expectedCount)
            .are(published);
    }

    @Then("il catalogo degli e-service template è vuoto")
    public void checkEServiceTemplatesCatalogIsEmpty() {
        CatalogEServiceTemplates catalog = ((ResponseEntity<CatalogEServiceTemplates>) httpCallExecutor.getResponse()).getBody();
        List<CatalogEServiceTemplate> templatesInCatalog = catalog.getResults();
        assertThat(templatesInCatalog).isEmpty();
    }

    @When("l'utente tenta la visualizzazione dell'elenco producers degli e-service templates")
    public void getEServiceTemplatesProducers() {
        String userToken = getUserToken();
        clientTokenConfigurator.setBearerToken(userToken);
        httpCallExecutor.performCall(
            () -> eServiceTemplateClient.getCreatorEServiceTemplates(sharedStepsContext.getXCorrelationId()),
            ResponseEntity::getStatusCode);
    }

    @Then("l'elenco producers degli e-service templates contiene esattamente {int} elementi")
    public void checkEServiceTemplatesProducersCount(int expectedCount) {
        List<ProducerEServiceTemplate> producers = ((ResponseEntity<ProducerEServiceTemplates>) httpCallExecutor.getResponse()).getBody().getResults();
        assertThat(producers).hasSize(expectedCount);
    }

    @When("l'utente tenta la visualizzazione dell'elenco dei creatori di e-service templates attivi")
    public void getActiveEServiceTemplatesCreators() {
        String userToken = getUserToken();
        clientTokenConfigurator.setBearerToken(userToken);
        httpCallExecutor.performCall(
            () -> eServiceTemplateClient.getEServiceTemplateCreators(sharedStepsContext.getXCorrelationId()),
            ResponseEntity::getStatusCode);
    }

    @Then("l'unico ente presente nell'elenco dei creatori di e-service templates attivi è {string}")
    public void checkActiveEServiceTemplatesCreators(String tenant) {
        List<CompactOrganization> creators = ((ResponseEntity<CompactOrganizations>) httpCallExecutor.getResponse()).getBody().getResults();
        assertThat(creators)
            .hasSize(1)
            .first()
            .extracting(CompactOrganization::getName)
            .isEqualTo(tenant);
    }

    /* TODO un'alternativa all'uso di metodi come "areConsistent" - che confrontano i campi uno a uno - potrebbe essere
     * l'uso di una libreria di mapping, da usare per mappare un oggetto nell'altro tipo, e quindi procedere con
     * un normale equals(...).
     */

    private boolean areConsistent(EServiceTemplateDetails template, EServiceTemplateNameUpdateSeed lastUpdate) {
        return template.getName().equals(lastUpdate.getName());
    }

    private String getUserToken() {
        return requireNonNull(
            sharedStepsContext.getUserToken(),
            "Il token dell'utente non è stato precedentemente impostato");
    }
}
