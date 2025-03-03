package it.pagopa.pn.interop.cucumber.steps.e_service_template;

import static java.util.Objects.nonNull;
import static java.util.Objects.requireNonNull;
import static org.assertj.core.api.Assertions.assertThat;

import io.cucumber.java.ParameterType;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.interop.authorization.service.utils.IdentityService;
import it.pagopa.interop.authorization.service.utils.PollingPredicateException;
import it.pagopa.interop.authorization.service.utils.PollingService;
import it.pagopa.interop.e_service_template.IEServiceTemplateClient;
import it.pagopa.interop.generated.openapi.clients.bff.model.AgreementApprovalPolicy;
import it.pagopa.interop.generated.openapi.clients.bff.model.CreatedEServiceTemplateVersion;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceMode;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTechnology;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTemplateDetails;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTemplateSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTemplateVersionDetails;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTemplateVersionState;
import it.pagopa.interop.generated.openapi.clients.bff.model.UpdateEServiceTemplateSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.UpdateEServiceTemplateVersionSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.VersionSeedForEServiceTemplateCreation;
import it.pagopa.interop.utils.HttpCallExecutor;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.DataPreparationService;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import lombok.Data;
import org.assertj.core.api.Assertions;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Data
public class EServiceTemplateSteps {


    /** Stores data on an e-service template useful for testing */
    record EServiceTemplateInfo(String name, UUID id, UUID lastVersionId){}

    private final ClientTokenConfigurator clientTokenConfigurator;
    private final DataPreparationService dataPreparationService;
    private final IdentityService identityService;
    private final SharedStepsContext sharedStepsContext;
    private final IEServiceTemplateClient eServiceTemplateClient;
    private final HttpCallExecutor httpCallExecutor;
    private final PollingService pollingService;

    private EServiceTemplateInfo lastTemplateManaged;
    private UpdateEServiceTemplateSeed lastTemplateUpdateSeed;
    private UpdateEServiceTemplateVersionSeed lastTemplateVersionUpdateSeed;

    public EServiceTemplateSteps(ClientTokenConfigurator clientTokenConfigurator,
                                DataPreparationService dataPreparationService,
                                SharedStepsContext sharedStepsContext) {
        this.clientTokenConfigurator = clientTokenConfigurator;
        this.dataPreparationService = dataPreparationService;
        this.sharedStepsContext = sharedStepsContext;
        this.identityService = sharedStepsContext.getIdentityService();
        this.eServiceTemplateClient = clientTokenConfigurator.getEServiceTemplateClient();
        this.httpCallExecutor = sharedStepsContext.getHttpCallExecutor();
        this.pollingService = sharedStepsContext.getPollingService();
    }

    @ParameterType("erogazione|ricezione")
    public EServiceMode eServiceMode(String mode) {
        return switch (mode) {
            case "erogazione"   -> EServiceMode.DELIVER;
            case "ricezione"    -> EServiceMode.RECEIVE;
            default             -> throw new IllegalArgumentException("Unsupported %s value: %s".formatted(
                                        EServiceMode.class.getSimpleName(),
                                        mode));
        };
    }

    @ParameterType("DRAFT|PUBLISHED|DEPRECATED|SUSPENDED")
    public EServiceTemplateVersionState eServiceTemplateVersionState(String state) {
        return switch (state) {
            case "DRAFT"        -> EServiceTemplateVersionState.DRAFT;
            case "PUBLISHED"    -> EServiceTemplateVersionState.PUBLISHED;
            case "DEPRECATED"   -> EServiceTemplateVersionState.DEPRECATED;
            case "SUSPENDED"    -> EServiceTemplateVersionState.SUSPENDED;
            default             -> throw new IllegalArgumentException("Unsupported %s value: %s".formatted(
                                        EServiceTemplateVersionState.class.getSimpleName(),
                                        state));
        };
    }

    @When("l'utente effettua la creazione di un e-service template in modalità {eServiceMode}")
    public void createEServiceTemplate(EServiceMode eServiceMode) {
        EServiceTemplateSeed templateSeed = getEServiceTemplateSeed(eServiceMode);
        createEServiceTemplate(templateSeed);
    }

    @When("l'utente effettua la creazione di un e-service template in modalità {eServiceMode} in stato di {eServiceTemplateVersionState}")
    public void createEServiceTemplate(EServiceMode eServiceMode, EServiceTemplateVersionState desiredState) {
        createEServiceTemplate(eServiceMode);
        switch (desiredState) {
            case DRAFT -> { /* no-op: un template appena creato è automaticamente in questo stato */ }
            case PUBLISHED -> publishEServiceTemplate();
            case SUSPENDED -> suspendEServiceTemplate();
            default -> throw new IllegalArgumentException("Stato non supportato: " + desiredState);
        }
    }

    @When("l'utente tenta delle modifiche all'e-service template")
    public void updateEServiceTemplate() {
        UUID eServiceTemplateId = lastTemplateManaged.id();
        lastTemplateUpdateSeed = new UpdateEServiceTemplateSeed()
            .name(lastTemplateManaged.name() + " - modificato")
            .audienceDescription("Nuova audience description")
            .eserviceDescription("Nuova descrizione del servizio")
            .technology(EServiceTechnology.SOAP)
            .mode(EServiceMode.RECEIVE)
            .isSignalHubEnabled(false);
        updateEServiceTemplate(eServiceTemplateId, lastTemplateUpdateSeed);
    }

    @Then("le modifiche al template sono state applicate correttamente")
    public void checkEServiceTemplateUpdate() {
        UUID eServiceTemplateId = lastTemplateManaged.id();
        UUID eServiceTemplateVersionId = lastTemplateManaged.lastVersionId();

        try {
            pollingService.makePolling(
                    () -> httpCallExecutor.performCall( // TODO è stata introdotta la API specifica per i template, refattorizzare usando quella (non solo qui) per i check che riguardano solo i template
                        () -> eServiceTemplateClient.getEServiceTemplateVersionWithHttpInfo(
                            sharedStepsContext.getXCorrelationId(),
                            eServiceTemplateId,
                            eServiceTemplateVersionId),
                        ResponseEntity::getStatusCode),
                    res -> nonNull(res.getBody()) && this.areConsistent(lastTemplateUpdateSeed, res.getBody().getEserviceTemplate()),
                    "L'e-service template non corrisponde alle modifiche apportate"
            );
        } catch (PollingPredicateException e) {
            Assertions.fail("Le modifiche all'e-service template non sono state "
                    + "applicate correttamente: le modifiche apportate '%s' non sono compatibili con il risultato ricevuto '%s'",
                lastTemplateUpdateSeed, httpCallExecutor.getResponse());
        }
    }

    @When("l'utente tenta di modificare l'e-service template specificando lo stesso nome")
    public void updateEServiceTemplateWithSameName() {
        UpdateEServiceTemplateSeed sameNameUpdateSeed = new UpdateEServiceTemplateSeed()
            .name(lastTemplateManaged.name())
            .audienceDescription("Nuova audience description")
            .eserviceDescription("Nuova descrizione del servizio")
            .technology(EServiceTechnology.SOAP)
            .mode(EServiceMode.RECEIVE);
        UUID eServiceTemplateId = lastTemplateManaged.id();
        updateEServiceTemplate(eServiceTemplateId, sameNameUpdateSeed);
    }

    @When("l'utente tenta delle modifiche a un e-service template inesistente")
    public void updateNonExistentEServiceTemplate() {
        UUID eServiceTemplateId = UUID.randomUUID();
        UpdateEServiceTemplateSeed updateSeed = new UpdateEServiceTemplateSeed()
            .name("Nuovo nome")
            .audienceDescription("Nuova audience description")
            .eserviceDescription("Nuova descrizione del servizio")
            .technology(EServiceTechnology.SOAP)
            .mode(EServiceMode.RECEIVE);
        updateEServiceTemplate(eServiceTemplateId, updateSeed);
    }

    private void updateEServiceTemplate(UUID eServiceTemplateId, UpdateEServiceTemplateSeed sameNameUpdateSeed) {
        String userToken = getUserToken();
        clientTokenConfigurator.setBearerToken(userToken);
        httpCallExecutor.performCall(
            () -> eServiceTemplateClient.updateEServiceTemplate(
                sharedStepsContext.getXCorrelationId(),
                eServiceTemplateId,
                sameNameUpdateSeed));
    }

    private boolean areConsistent(UpdateEServiceTemplateSeed lastUpdate, EServiceTemplateDetails retrievedTemplate) {
        return lastUpdate.getName().equals(retrievedTemplate.getName()) &&
            lastUpdate.getAudienceDescription().equals(retrievedTemplate.getAudienceDescription()) &&
            lastUpdate.getEserviceDescription().equals(retrievedTemplate.getEserviceDescription()) &&
            lastUpdate.getTechnology().equals(retrievedTemplate.getTechnology()) &&
            lastUpdate.getMode().equals(retrievedTemplate.getMode());
    }

    @When("l'utente tenta delle modifiche alla versione dell'e-service template")
    public void updateEServiceTemplateVersion() {
        lastTemplateVersionUpdateSeed = new UpdateEServiceTemplateVersionSeed()
            .agreementApprovalPolicy(AgreementApprovalPolicy.AUTOMATIC)
            //.attributes() <-- TODO costoso da implementare, rimandato
            .dailyCallsPerConsumer(100)
            .dailyCallsTotal(1000)
            .voucherLifespan(86400)
            .description("Nuova descrizione della versione");
        updateEServiceTemplateVersion(
            this.lastTemplateManaged.id(),
            this.lastTemplateManaged.lastVersionId(),
            lastTemplateVersionUpdateSeed);
    }

    @Then("le modifiche alla versione sono state applicate correttamente")
    public void checkEServiceTemplateVersionUpdate() {
        UUID eServiceTemplateId = lastTemplateManaged.id();
        UUID eServiceTemplateVersionId = lastTemplateManaged.lastVersionId();
        try {
            pollingService.makePolling(
                () -> httpCallExecutor.performCall(
                    () -> eServiceTemplateClient.getEServiceTemplateVersionWithHttpInfo(
                        sharedStepsContext.getXCorrelationId(),
                        eServiceTemplateId,
                        eServiceTemplateVersionId),
                    ResponseEntity::getStatusCode),
                res -> nonNull(res.getBody()) && this.areConsistent(lastTemplateVersionUpdateSeed, res.getBody()),
                "La versione dell'e-service template non corrisponde alle modifiche apportate"
            );
        } catch (PollingPredicateException e) {
            Assertions.fail("Le modifiche alla versione dell'e-service template non sono state "
                    + "applicate correttamente: le modifiche apportate '%s' non sono compatibili con il risultato ricevuto '%s'",
                lastTemplateUpdateSeed, httpCallExecutor.getResponse());
        }
    }

    @When("l'utente tenta delle modifiche alla versione di un e-service template inesistente")
    public void updateNonExistentEServiceTemplateVersion() {
        UpdateEServiceTemplateVersionSeed updateSeed = new UpdateEServiceTemplateVersionSeed()
            .agreementApprovalPolicy(AgreementApprovalPolicy.AUTOMATIC)
            //.attributes() <-- TODO costoso da implementare, rimandato
            .dailyCallsPerConsumer(500)
            .dailyCallsTotal(5000)
            .voucherLifespan(586400)
            .description("Nuova descrizione della versione");
        updateEServiceTemplateVersion(UUID.randomUUID(), UUID.randomUUID(), updateSeed);
    }

    private void updateEServiceTemplateVersion(UUID eServiceTemplateId, UUID eServiceTemplateVersionId, UpdateEServiceTemplateVersionSeed sameNameUpdateSeed) {
        String userToken = getUserToken();
        clientTokenConfigurator.setBearerToken(userToken);
        httpCallExecutor.performCall(
            () -> eServiceTemplateClient.updateEServiceTemplateVersion(
                sharedStepsContext.getXCorrelationId(),
                eServiceTemplateId,
                eServiceTemplateVersionId,
                sameNameUpdateSeed));
    }

    // TODO diverse NPE possibili, agire di conseguenza
    private boolean areConsistent(UpdateEServiceTemplateVersionSeed lastUpdate, EServiceTemplateVersionDetails retrievedTemplate) {
        return //lastUpdate.getAttributes().equals(retrievedTemplate.getAttributes()) &&  <- TODO costoso da implementare, rimandato
            lastUpdate.getDescription().equals(retrievedTemplate.getDescription()) &&
            lastUpdate.getAgreementApprovalPolicy().equals(retrievedTemplate.getAgreementApprovalPolicy()) &&
            lastUpdate.getVoucherLifespan().equals(retrievedTemplate.getVoucherLifespan()) &&
            lastUpdate.getDailyCallsTotal().equals(retrievedTemplate.getDailyCallsTotal()) &&
            lastUpdate.getDailyCallsPerConsumer().equals(retrievedTemplate.getDailyCallsPerConsumer());
    }

    /** Return a new {@link EServiceTemplateSeed} with only the mandatory fields set
     * @param eServiceMode the risk analysis mode of the e-service
     * @return a new {@link EServiceTemplateSeed} instance
     */
    private EServiceTemplateSeed getEServiceTemplateSeed(EServiceMode eServiceMode) {
        int randomInt = ThreadLocalRandom.current().nextInt(0, Integer.MAX_VALUE);
        String templateName = String.format("eservice-template-%d-%d", sharedStepsContext.getTestSeed(), randomInt);
        VersionSeedForEServiceTemplateCreation version = new VersionSeedForEServiceTemplateCreation()
            .voucherLifespan(86400);
        return new EServiceTemplateSeed()
            .audienceDescription("Audience description per il template " + templateName)
            .name(templateName)
            .eserviceDescription("Descrizione del servizio associato al template " + templateName)
            .mode(eServiceMode)
            .version(version)
            .technology(EServiceTechnology.REST);
    }

    private void createEServiceTemplate(EServiceTemplateSeed templateSeed) {
        String userToken = getUserToken();
        clientTokenConfigurator.setBearerToken(userToken);

        CreatedEServiceTemplateVersion creationResponse = this.dataPreparationService.createEServiceTemplate(
            templateSeed);
        this.lastTemplateManaged = new EServiceTemplateInfo(
            templateSeed.getName(),
            creationResponse.getId(),
            creationResponse.getVersionId());
    }

    @When("l'utente effettua la creazione di un e-service template in modalità {eServiceMode} usando lo stesso nome")
    public void createEServiceTemplateWithSameName(EServiceMode eServiceMode) {
        String lastTemplateNameUsed = this.lastTemplateManaged.name();
        EServiceTemplateSeed sameNameTemplateSeed = this.getEServiceTemplateSeed(eServiceMode)
            .name(lastTemplateNameUsed);
        createEServiceTemplate(sameNameTemplateSeed);
    }

    @Then("l'e-service template è in stato di {eServiceTemplateVersionState}")
    public void checkEServiceTemplateState(EServiceTemplateVersionState expectedState) {
        UUID eServiceTemplateId = lastTemplateManaged.id();
        UUID eServiceTemplateVersionId = lastTemplateManaged.lastVersionId();

        /* Attende qualora eventuali chiamate precedenti (creazione, pubblicazione, sospensine...)
         * non abbiano ancora completato il proprio corso */
        pollingService.makePolling(
                () -> httpCallExecutor.performCall(
                    () -> eServiceTemplateClient.getEServiceTemplateVersion(
                        sharedStepsContext.getXCorrelationId(),
                        eServiceTemplateId,
                        eServiceTemplateVersionId)),
                res -> res != HttpStatus.NOT_FOUND,
                "There was an error while retrieving the e-service template"
        );

        EServiceTemplateVersionDetails retrievedTemplateVersion = (EServiceTemplateVersionDetails) this.httpCallExecutor.getResponse();
        EServiceTemplateVersionState actualState = retrievedTemplateVersion.getState();

        assertThat(actualState)
            .as("Lo stato dell'e-service template deve corrispondere a quanto atteso dal test")
            .isEqualTo(expectedState);
    }

    @When("l'utente effettua la pubblicazione dell'e-service template")
    public void publishEServiceTemplate() {
        String userToken = getUserToken();
        clientTokenConfigurator.setBearerToken(userToken);
        this.dataPreparationService.publishEServiceTemplate(
            lastTemplateManaged.id(),
            lastTemplateManaged.lastVersionId());
    }

    @When("l'utente effettua la sospensione dell'e-service template")
    public void suspendEServiceTemplate() {
        String userToken = getUserToken();
        clientTokenConfigurator.setBearerToken(userToken);
        this.dataPreparationService.suspendEServiceTemplate(
            lastTemplateManaged.id(),
            lastTemplateManaged.lastVersionId());
    }


    @When("l'utente effettua la riattivazione dell'e-service template")
    public void activateEServiceTemplate() {
        String userToken = getUserToken();
        clientTokenConfigurator.setBearerToken(userToken);
        this.dataPreparationService.activateEServiceTemplate(
            lastTemplateManaged.id(),
            lastTemplateManaged.lastVersionId());
    }

    private String getUserToken() {
        return requireNonNull(
            sharedStepsContext.getUserToken(),
            "Il token dell'utente non è stato precedentemente impostato");
    }
}
