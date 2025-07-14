package it.pagopa.pn.interop.cucumber.steps.m2m.eservice_template;

import io.cucumber.java.en.And;
import it.pagopa.interop.generated.openapi.clients.bff.model.CreatedEServiceTemplateVersion;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTemplateSeed;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.datapreparationservice.M2MDataPreparationService;

public class EserviceTemplateSteps {
    private final SharedStepsContext sharedStepsContext;
    private final M2MDataPreparationService dataPreparationService;

    public EserviceTemplateSteps(SharedStepsContext sharedStepsContext,
                                 M2MDataPreparationService dataPreparationService) {
        this.sharedStepsContext = sharedStepsContext;
        this.dataPreparationService = dataPreparationService;
    }

    @And("viene effettuata la creazione dei template e-service:")
    public void createEserviceTemplate() {
        //
        EServiceTemplateSeed eServiceTemplateSeed = new EServiceTemplateSeed();

        // Esegue le creazione
        CreatedEServiceTemplateVersion version = dataPreparationService.createEServiceTemplate(eServiceTemplateSeed);

        // Aggiorna il context
    }
}
