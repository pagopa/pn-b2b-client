package it.pagopa.pn.interop.cucumber.steps.attribute;

import io.cucumber.java.en.When;
import it.pagopa.interop.attribute.service.IAttributeApiClient;
import it.pagopa.interop.utils.HttpCallExecutor;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.common.AttributeCommonContext;
import it.pagopa.pn.interop.cucumber.utility.CommonUtils;

public class AttributeReadSteps {
    private final SharedStepsContext sharedStepsContext;
    private final IAttributeApiClient attributeApiClient;
    private final AttributeCommonContext attributeCommonContext;
    private final CommonUtils commonUtils;
    private final HttpCallExecutor httpCallExecutor;

    public AttributeReadSteps(
        ClientTokenConfigurator clientTokenConfigurator,
        SharedStepsContext sharedStepsContext,
        CommonUtils commonUtils)
    {
        this.sharedStepsContext = sharedStepsContext;
        this.attributeApiClient = clientTokenConfigurator.getAttributeApiClient();
        this.attributeCommonContext = sharedStepsContext.getAttributeCommonContext();
        this.commonUtils = commonUtils;
        this.httpCallExecutor = sharedStepsContext.getHttpCallExecutor();
    }

    // TODO 14/04/2024 correggere in "[...]quell'attributo"
    @When("l'utente richiede una operazione di lettura di quel attributo")
    public void readAttribute() {
        httpCallExecutor.performCall(() -> attributeApiClient.getAttributeByIdRE(
            sharedStepsContext.getXCorrelationId(),
            attributeCommonContext.getAttributeId()));
        commonUtils.assertValidResponse();
    }
}