package it.pagopa.pn.interop.cucumber.steps.voucher;

import static org.assertj.core.api.Assertions.assertThat;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.interop.authorization.service.utils.voucher.VoucherService;
import it.pagopa.interop.authorization.service.utils.voucher.domain.ClientAssertionOptions;
import it.pagopa.interop.authorization.service.utils.voucher.domain.ClientAssertionOptions.ClientType;
import it.pagopa.interop.authorization.service.utils.voucher.domain.VoucherRequest;
import it.pagopa.interop.authorization.service.utils.voucher.domain.VoucherResponse;
import it.pagopa.interop.utils.HttpCallExecutor;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;

public class VoucherGenerationSteps {

    private final SharedStepsContext sharedStepsContext;
    private final VoucherService voucherService;
    private final HttpCallExecutor httpCallExecutor;

    public VoucherGenerationSteps(
        SharedStepsContext sharedStepsContext,
        VoucherService voucherService) {
        this.sharedStepsContext = sharedStepsContext;
        this.voucherService = voucherService;
        this.httpCallExecutor = sharedStepsContext.getHttpCallExecutor();
    }

    @When("l'utente richiede la generazione del voucher")
    public void requestVoucherGeneration() {
        ClientAssertionOptions assertionOptions = buildClientAssertionOptions();
        requestVoucher(assertionOptions);
    }

    @When("l'utente richiede la generazione del voucher con digest")
    public void requestVoucherGenerationWithDigest() {
        ClientAssertionOptions assertionOptions = buildClientAssertionOptions().toBuilder()
            .digestIncluded(true)
            .build();
        requestVoucher(assertionOptions);
    }

    @When("l'utente richiede la generazione del voucher indicando il primo client ma con la chiave caricata nel secondo")
    public void requestVoucherGenerationWithSecondClientKey() {
        ClientAssertionOptions assertionOptions = buildClientAssertionOptions().toBuilder()
            .publicKey(sharedStepsContext.getClientCommonContext().getNewClientPublicKeyAsObj())
            .privateKey(sharedStepsContext.getClientCommonContext().getNewClientPrivateKeyAsObj())
            .build();
        requestVoucher(assertionOptions);
    }

    @Then("si ottiene la corretta generazione del voucher")
    public void checkVoucherGeneration() {
        VoucherResponse voucherResponse = (VoucherResponse) httpCallExecutor.getResponse();
        assertThat(voucherResponse.getTokenType()).isEqualTo("Bearer");
    }

    private void requestVoucher(ClientAssertionOptions assertionOptions) {
        String clientAssertion = this.voucherService.createClientAssertion(assertionOptions);
        String clientId = sharedStepsContext.getClientCommonContext().getFirstClient().toString();
        VoucherRequest voucherRequest = VoucherRequest.builder().clientId(clientId)
            .clientAssertion(clientAssertion).build();
        httpCallExecutor.performCall(() -> this.voucherService.requestVoucher(voucherRequest));
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
