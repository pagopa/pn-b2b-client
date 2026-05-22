package it.pagopa.pn.interop.cucumber.steps.purposetemplate;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.interop.authorization.service.utils.PollingService;
import it.pagopa.interop.common.IHttpExecutor;
import it.pagopa.interop.generated.openapi.clients.bff.model.*;
import it.pagopa.interop.purpose.service.IPurposeApiClient;
import it.pagopa.interop.purpose.service.IPurposeTemplateClient;
import it.pagopa.interop.purpose.service.impl.PurposeTemplateClientImpl;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.purposetemplate.model.PurposeTemplateContext;
import it.pagopa.pn.interop.cucumber.steps.purposetemplate.utils.PurposeTemplateResolver;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

@Slf4j
public class LinkPurposeTemplateSteps {

    private final ClientTokenConfigurator clientTokenConfigurator;

    private final IPurposeTemplateClient purposeTemplateClient;

    private final IPurposeApiClient purposeApiClient;

    private final SharedStepsContext sharedStepsContext;

    private final IHttpExecutor httpCallExecutor;

    private final PollingService pollingService;

    private PurposeTemplateContext purposeTemplateContext;

    private PurposeTemplateResolver resolver;

    /**
     * Quando voglio simulare una casistica di titolo duplicato, la prima volta ne creo uno (con timestamp) e lo setto qua.
     * La seconda volta, quando questa variabile non è più null, ri-applico lo stesso titolo.
     */
    private String duplicatedTitleForPurpose;

    public LinkPurposeTemplateSteps(SharedStepsContext sharedStepsContext,
                                ClientTokenConfigurator clientTokenConfigurator) {
        this.clientTokenConfigurator = clientTokenConfigurator;
        this.sharedStepsContext = sharedStepsContext;
        this.httpCallExecutor = sharedStepsContext.getHttpCallExecutor();
        this.pollingService = sharedStepsContext.getPollingService();
        this.purposeTemplateClient = clientTokenConfigurator.getPurposeTemplateClient();
        ((PurposeTemplateClientImpl) this.purposeTemplateClient).setHttpCallExecutor(this.httpCallExecutor);
        this.purposeApiClient = clientTokenConfigurator.getPurposeApiClient();
        this.purposeTemplateContext = new PurposeTemplateContext();
        this.resolver = new PurposeTemplateResolver(sharedStepsContext, purposeTemplateContext, sharedStepsContext.getIdentityService());
    }

    @When("recupera le risorse collegabili suggerite per un template finalità")
    public void createPurposeTemplate() {

    }

}
