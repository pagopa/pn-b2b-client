package it.pagopa.pn.interop.cucumber.steps.authorization.dpop;

import io.cucumber.java.en.And;
import io.cucumber.java.en.When;
import it.pagopa.interop.authorization.service.M2MDPopTokenService;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;

import java.util.List;

import static it.pagopa.interop.authorization.enums.M2MRole.M2M_ADMIN;


public class DpopSteps {

    private final M2MDPopTokenService m2mDpopTokenService;
    private final SharedStepsContext sharedStepsContext;

    public DpopSteps(M2MDPopTokenService m2mDpopTokenService, SharedStepsContext sharedStepsContext) {
        this.m2mDpopTokenService = m2mDpopTokenService;
        this.sharedStepsContext = sharedStepsContext;
        this.m2mDpopTokenService.setHttpCallExecutor(sharedStepsContext.getHttpCallExecutor());
        this.m2mDpopTokenService.setIdentityService(sharedStepsContext.getIdentityService());
    }


    @When("{string} genera una dpop proof con algoritmo {string} e cerca di ottenere un access token tramite richiesta con header DPoP")
    public void getAccessToken(String tenantType, String keyType) {

        M2MDPopTokenService.PreparedClient client = sharedStepsContext.getClientCommonContext().getLastPreparedClient();

        // TODO aggiungere controlli
        String purposeId = sharedStepsContext.getPurposeCommonContext().getPurposesIds().get(0);

        m2mDpopTokenService.getTokenWithDpop(client, tenantType, purposeId, keyType);
    }
}
