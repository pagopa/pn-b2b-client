package it.pagopa.pn.interop.cucumber.steps.e_service_template;

import static java.util.Objects.requireNonNull;
import static org.assertj.core.api.Assertions.assertThat;

import io.cucumber.java.ParameterType;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.interop.authorization.service.utils.IdentityService;
import it.pagopa.interop.authorization.service.utils.PollingService;
import it.pagopa.interop.e_service_template.IEServiceTemplateClient;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceMode;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTechnology;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTemplateSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTemplateVersionDetails;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTemplateVersionState;
import it.pagopa.interop.generated.openapi.clients.bff.model.VersionSeedForEServiceTemplateCreation;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.DataPreparationService;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import java.util.concurrent.ThreadLocalRandom;
import lombok.Data;

@Data
public class EServiceTemplateSteps {
    private ClientTokenConfigurator clientTokenConfigurator;
    private DataPreparationService dataPreparationService;
    private IdentityService identityService;
    private SharedStepsContext sharedStepsContext;
    private IEServiceTemplateClient eServiceTemplateClient;
    private PollingService pollingService;

    private EServiceTemplateVersionDetails lastTemplateManaged;

    public EServiceTemplateSteps(ClientTokenConfigurator clientTokenConfigurator,
                                DataPreparationService dataPreparationService,
                                SharedStepsContext sharedStepsContext) {
        this.clientTokenConfigurator = clientTokenConfigurator;
        this.dataPreparationService = dataPreparationService;
        this.sharedStepsContext = sharedStepsContext;
        this.identityService = sharedStepsContext.getIdentityService();
        this.eServiceTemplateClient = clientTokenConfigurator.getEServiceTemplateClient();
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
        String userToken = requireNonNull(
            sharedStepsContext.getUserToken(),
            "Il token dell'utente non è stato precedentemente impostato");
        clientTokenConfigurator.setBearerToken(userToken);
        this.lastTemplateManaged = dataPreparationService.createEServiceTemplate(templateSeed);
    }

    @When("l'utente effettua la creazione di un e-service template in modalità {eServiceMode} usando lo stesso nome")
    public void createEServiceTemplateWithSameName(EServiceMode eServiceMode) {
        String lastTemplateNameUsed = this.lastTemplateManaged.getEserviceTemplate().getName();
        EServiceTemplateSeed sameNameTemplateSeed = this.getEServiceTemplateSeed(eServiceMode)
            .name(lastTemplateNameUsed);
        createEServiceTemplate(sameNameTemplateSeed);
    }

    @Then("l'e-service template è in stato di {eServiceTemplateVersionState}")
    public void checkEServiceTemplateState(EServiceTemplateVersionState expectedState) {
        EServiceTemplateVersionState actualState = this.lastTemplateManaged.getState();
        assertThat(actualState)
            .as("Lo stato dell'e-service template creato deve corrispondere a quanto atteso dal test")
            .isEqualTo(expectedState);
    }
}
