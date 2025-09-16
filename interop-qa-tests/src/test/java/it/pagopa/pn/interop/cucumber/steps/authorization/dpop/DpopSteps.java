package it.pagopa.pn.interop.cucumber.steps.authorization.dpop;

import io.cucumber.java.en.When;
import it.pagopa.interop.authorization.service.M2MDPopTokenService;
import it.pagopa.interop.authorization.service.utils.voucher.domain.VoucherResponse;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;

import java.util.List;


public class DpopSteps {

    private final M2MDPopTokenService m2mDpopTokenService;
    private final SharedStepsContext sharedStepsContext;
    private VoucherResponse oAuth2VoucherResponse;

    public DpopSteps(M2MDPopTokenService m2mDpopTokenService, SharedStepsContext sharedStepsContext) {
        this.m2mDpopTokenService = m2mDpopTokenService;
        this.sharedStepsContext = sharedStepsContext;
        this.m2mDpopTokenService.setHttpCallExecutor(sharedStepsContext.getHttpCallExecutor());
        this.m2mDpopTokenService.setIdentityService(sharedStepsContext.getIdentityService());
    }


    @When("{string} genera una dpop proof con algoritmo {string} e cerca di ottenere un access token tramite richiesta con header DPoP")
    public void getAccessToken(String tenantType, String keyAlgorithm) {
        // Recupero del client
        M2MDPopTokenService.PreparedClient preparedClient = sharedStepsContext
                .getClientCommonContext()
                .getLastPreparedClient();

        if (preparedClient == null) {
            throw new IllegalStateException("Nessun client trovato nel contesto condiviso.");
        }

        // Recupero del purposeId
        List<String> purposeIds = sharedStepsContext
                .getPurposeCommonContext()
                .getPurposesIds();

        if (purposeIds == null || purposeIds.isEmpty()) {
            throw new IllegalStateException("Nessuna finalità creata e associata al client.");
        }

        String purposeId = purposeIds.get(purposeIds.size() - 1);

        // Richiesta del token usando DPoP
        this.oAuth2VoucherResponse = m2mDpopTokenService.getTokenWithDpop(preparedClient, tenantType, purposeId, keyAlgorithm);
    }

}
