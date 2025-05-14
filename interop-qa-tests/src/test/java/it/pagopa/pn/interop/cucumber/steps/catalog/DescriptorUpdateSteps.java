package it.pagopa.pn.interop.cucumber.steps.catalog;

import io.cucumber.java.en.When;
import it.pagopa.interop.generated.openapi.clients.bff.model.AgreementApprovalPolicy;
import it.pagopa.interop.generated.openapi.clients.bff.model.DescriptorAttributesSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.UpdateEServiceDescriptorQuotas;
import it.pagopa.interop.generated.openapi.clients.bff.model.UpdateEServiceDescriptorSeed;
import it.pagopa.interop.utils.HttpCallExecutor;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;

import java.util.List;

public class DescriptorUpdateSteps {
    private final ClientTokenConfigurator clientTokenConfigurator;
    private final SharedStepsContext sharedStepsContext;
    private final HttpCallExecutor httpCallExecutor;

    public DescriptorUpdateSteps(ClientTokenConfigurator clientTokenConfigurator,
                                     SharedStepsContext sharedStepsContext) {
        this.clientTokenConfigurator = clientTokenConfigurator;
        this.sharedStepsContext = sharedStepsContext;
        this.httpCallExecutor = sharedStepsContext.getHttpCallExecutor();
    }

    @When("l'utente aggiorna alcuni parametri di quel descrittore")
    public void updateSomeDescriptorParams() {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());
        UpdateEServiceDescriptorSeed seed = new UpdateEServiceDescriptorSeed()
                .description("Questo è un e-service di test")
                .audience(List.of("api/v1"))
                .voucherLifespan(60)
                .dailyCallsPerConsumer(50)
                .dailyCallsTotal(2000)
                .agreementApprovalPolicy(AgreementApprovalPolicy.AUTOMATIC)
                .attributes(new DescriptorAttributesSeed());

        httpCallExecutor.performCall(
                () -> clientTokenConfigurator.getEServiceClient().updateDraftDescriptor(
                        sharedStepsContext.getEServicesCommonContext().getEserviceId(),
                        sharedStepsContext.getEServicesCommonContext().getDescriptorId(),
                        seed
                )
        );
    }

    @When("l'utente aggiorna la durata del voucher e le soglie di carico di quel descrittore")
    public void updateVoucherLifespanAndCallsLimit() {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());
        UpdateEServiceDescriptorQuotas seed = new UpdateEServiceDescriptorQuotas()
                .voucherLifespan(60)
                .dailyCallsPerConsumer(50)
                .dailyCallsTotal(2000);
        httpCallExecutor.performCall(
                () -> clientTokenConfigurator.getEServiceClient().updateDescriptor(
                        sharedStepsContext.getEServicesCommonContext().getEserviceId(),
                        sharedStepsContext.getEServicesCommonContext().getDescriptorId(),
                        seed
                )
        );
    }
}
