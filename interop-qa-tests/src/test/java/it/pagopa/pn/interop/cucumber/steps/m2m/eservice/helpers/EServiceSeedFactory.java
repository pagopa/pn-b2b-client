package it.pagopa.pn.interop.cucumber.steps.m2m.eservice.helpers;

import io.cucumber.spring.ScenarioScope;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.*;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;

@ScenarioScope
public class EServiceSeedFactory {

    private final SharedStepsContext sharedStepsContext;

    public EServiceSeedFactory(SharedStepsContext sharedStepsContext) {
        this.sharedStepsContext = sharedStepsContext;
    }

    public DescriptorSeedForEServiceCreation defaultDescriptorSeedForEServiceCreation() {
        return new DescriptorSeedForEServiceCreation()
                .addAudienceItem("pagopa.it")
                .dailyCallsPerConsumer(10)
                .dailyCallsTotal(100)
                .agreementApprovalPolicy(AgreementApprovalPolicy.AUTOMATIC)
                .voucherLifespan(60);
    }

    public EServiceSeed defaultEServiceSeed() {
        return new EServiceSeed()
                .name(String.format("e-service-%s", sharedStepsContext.getTestSeed()))
                .description("e-service di test")
                .descriptor(this.defaultDescriptorSeedForEServiceCreation())
                .technology(EServiceTechnology.REST)
                .mode(EServiceMode.DELIVER);
    }
}
