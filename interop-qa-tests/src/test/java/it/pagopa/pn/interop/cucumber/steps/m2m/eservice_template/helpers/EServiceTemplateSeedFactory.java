package it.pagopa.pn.interop.cucumber.steps.m2m.eservice_template.helpers;

import io.cucumber.spring.ScenarioScope;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.*;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;

@ScenarioScope
public class EServiceTemplateSeedFactory {

    private final SharedStepsContext sharedStepsContext;

    public EServiceTemplateSeedFactory(SharedStepsContext sharedStepsContext) {
        this.sharedStepsContext = sharedStepsContext;
    }

    public VersionSeedForEServiceTemplateCreation defaultVersionSeedForEServiceTemplateCreation() {
        return new VersionSeedForEServiceTemplateCreation()
                .voucherLifespan(86400);
    }

    public EServiceTemplateSeed defaultEServiceTemplateSeed() {
        String templateName = String.format("e-service-template-%s", sharedStepsContext.getTestSeed());
        return new EServiceTemplateSeed()
                .name(templateName)
                .intendedTarget("Audience description per il template " + templateName)
                .description("Descrizione del servizio associato al template " + templateName)
                .mode(EServiceMode.DELIVER)
                .version(this.defaultVersionSeedForEServiceTemplateCreation())
                .technology(EServiceTechnology.REST);
    }
}
