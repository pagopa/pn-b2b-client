package it.pagopa.pn.interop.cucumber.steps.voucher;

import io.cucumber.java.en.When;
import it.pagopa.interop.authorization.service.utils.voucher.VoucherService;
import it.pagopa.interop.authorization.service.utils.voucher.domain.ClientAssertionOptions;
import it.pagopa.interop.authorization.service.utils.voucher.domain.ClientAssertionOptions.ClientType;
import it.pagopa.interop.authorization.service.utils.voucher.domain.VoucherRequest;
import it.pagopa.interop.authorization.service.utils.voucher.domain.VoucherRequestParam;
import it.pagopa.interop.utils.HttpCallExecutor;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import java.util.UUID;

public class VoucherGenerationParamsValidationSteps {

    private final SharedStepsContext sharedStepsContext;
    private final VoucherService voucherService;
    private final HttpCallExecutor httpCallExecutor;

    public VoucherGenerationParamsValidationSteps(
        SharedStepsContext sharedStepsContext,
        VoucherService voucherService
    ) {
        this.sharedStepsContext = sharedStepsContext;
        this.voucherService = voucherService;
        this.httpCallExecutor = sharedStepsContext.getHttpCallExecutor();
    }

    @When("l'utente richiede la generazione del voucher con il parametro {voucherParam} diverso da quello atteso")
    public void voucherGenerationWithUnexpectedParameter(VoucherRequestParam param) {
        ClientAssertionOptions assertionOptions = buildClientAssertionOptions();
        String clientAssertion = this.voucherService.createClientAssertion(assertionOptions);

        VoucherRequest voucherRequest = VoucherRequest.builder()
            .clientId(sharedStepsContext.getClientCommonContext().getFirstClient().toString())
            .clientAssertion(clientAssertion)
            .build();
        voucherRequest.set(param, "unknown");
        httpCallExecutor.performCall(() ->
            voucherService.requestVoucherExpectingError(voucherRequest).getBody());
    }

    @When("l'utente richiede la generazione del voucher valorizzando il parametro client_id con un valore diverso dal claim sub nella client assertion")
    public void voucherGenerationWithClientIdDifferentFromSub() {
        ClientAssertionOptions assertionOptions = buildClientAssertionOptions();
        String clientAssertion = this.voucherService.createClientAssertion(assertionOptions);

        VoucherRequest voucherRequest = VoucherRequest.builder()
            .clientId(UUID.randomUUID().toString())
            .clientAssertion(clientAssertion)
            .build();
        httpCallExecutor.performCall(() ->
            voucherService.requestVoucherExpectingError(voucherRequest).getBody());
    }

    @When("l'utente richiede la generazione del voucher inserendo una client assertion come JWT non valida")
    public void voucherGenerationWithInvalidClientAssertion() {
        VoucherRequest voucherRequest = VoucherRequest.builder()
            .clientId(sharedStepsContext.getClientCommonContext().getFirstClient().toString())
            .clientAssertion("unknown")
            .build();
        httpCallExecutor.performCall(() ->
            voucherService.requestVoucherExpectingError(voucherRequest).getBody());
    }

    private ClientAssertionOptions buildClientAssertionOptions() {
        return ClientAssertionOptions.builder()
            .clientType(ClientType.CONSUMER)
            .clientId(sharedStepsContext.getClientCommonContext().getFirstClient().toString())
            .purposeId(sharedStepsContext.getPurposeCommonContext().getPurposeId())
            .publicKey(sharedStepsContext.getClientCommonContext().getClientPublicKeyAsObj())
            .privateKey(sharedStepsContext.getClientCommonContext().getClientPrivateKeyAsObj())
            .build();
    }
}
