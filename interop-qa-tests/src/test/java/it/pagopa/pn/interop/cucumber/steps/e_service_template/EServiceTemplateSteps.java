package it.pagopa.pn.interop.cucumber.steps.e_service_template;

import static java.util.Objects.requireNonNull;
import static org.assertj.core.api.Assertions.assertThat;

import io.cucumber.java.ParameterType;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.interop.authorization.service.utils.IdentityService;
import it.pagopa.interop.authorization.service.utils.PollingService;
import it.pagopa.interop.e_service_template.IEServiceTemplateClient;
import it.pagopa.interop.generated.openapi.clients.bff.model.CreatedEServiceTemplateVersion;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceMode;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTechnology;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTemplateSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTemplateVersionDetails;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTemplateVersionState;
import it.pagopa.interop.generated.openapi.clients.bff.model.VersionSeedForEServiceTemplateCreation;
import it.pagopa.interop.utils.HttpCallExecutor;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.DataPreparationService;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import lombok.Data;
import org.springframework.http.HttpStatus;

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
