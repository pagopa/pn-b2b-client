package it.pagopa.pn.interop.cucumber.steps.e_service_template;

import io.cucumber.java.ParameterType;
import io.cucumber.java.en.When;
import it.pagopa.interop.authorization.service.utils.IdentityService;
import it.pagopa.interop.authorization.service.utils.PollingService;
import it.pagopa.interop.e_service_template.IEServiceTemplateClient;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceMode;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTechnology;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTemplateSeed;
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
    public EServiceMode eServiceMode(String validityString) {
        return switch (validityString) {
            case "erogazione" -> EServiceMode.DELIVER;
            case "ricezione" -> EServiceMode.RECEIVE;
            default -> throw new IllegalArgumentException("Unsupported %s value: %s".formatted(
                EServiceMode.class.getSimpleName(),
                validityString));
        };
    }

    @When("l'utente effettua la creazione di un e-service template in modalità {eServiceMode}")
    public void createEServiceTemplate(EServiceMode eServiceMode) {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());
        int randomInt = ThreadLocalRandom.current().nextInt(0, Integer.MAX_VALUE);
        String templateName = String.format("eservice-template-%d-%d", sharedStepsContext.getTestSeed(), randomInt);
        VersionSeedForEServiceTemplateCreation version = new VersionSeedForEServiceTemplateCreation()
            .voucherLifespan(86400);
        EServiceTemplateSeed templateSeed = new EServiceTemplateSeed()
            .audienceDescription("Audience description per il template " + templateName)
            .name(templateName)
            .eserviceDescription("Descrizione del servizio associato al template " + templateName)
            .mode(eServiceMode)
            .version(version)
            .technology(EServiceTechnology.REST);
        dataPreparationService.createEServiceTemplate(templateSeed);
    }
}
