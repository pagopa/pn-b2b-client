package it.pagopa.pn.interop.cucumber.steps.catalog;

import io.cucumber.java.en.Given;
import it.pagopa.interop.agreement.domain.EServiceDescriptor;
import it.pagopa.interop.authorization.service.utils.IdentityService;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.UpdateEServiceDescriptorSeed;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.DataPreparationService;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;

public class EServiceCatalogListingSteps {
    private final DataPreparationService dataPreparationService;
    private final ClientTokenConfigurator clientTokenConfigurator;
    private final SharedStepsContext sharedStepsContext;
    private final IdentityService identityService;

    public EServiceCatalogListingSteps(DataPreparationService dataPreparationService,
                                       ClientTokenConfigurator clientTokenConfigurator,
                                       SharedStepsContext sharedStepsContext) {
        this.dataPreparationService = dataPreparationService;
        this.clientTokenConfigurator = clientTokenConfigurator;
        this.sharedStepsContext = sharedStepsContext;
        this.identityService = sharedStepsContext.getIdentityService();
    }

    @Given("{string} ha già creato e pubblicato un e-service contenente la keyword {string}")
    public void tenantHasAlreadyCreatedEServiceWithKeyword(String tenantType, String keyword) {
        clientTokenConfigurator.setBearerToken(identityService.getToken(tenantType, null));
        String eServiceName = String.format("e-service-%s-%s", sharedStepsContext.getXCorrelationId(), keyword);

        EServiceDescriptor eServiceDescriptor = dataPreparationService.createEServiceAndDraftDescriptor(
                new EServiceSeed().name(eServiceName),
                new UpdateEServiceDescriptorSeed());

        sharedStepsContext.getEServicesCommonContext().setEserviceId(eServiceDescriptor.getEServiceId());
        sharedStepsContext.getEServicesCommonContext().setDescriptorId(eServiceDescriptor.getDescriptorId());

        dataPreparationService.addInterfaceToDescriptor(eServiceDescriptor.getEServiceId(), eServiceDescriptor.getDescriptorId());
        dataPreparationService.publishDescriptor(eServiceDescriptor.getEServiceId(), eServiceDescriptor.getDescriptorId());
    }
}
