package it.pagopa.pn.interop.cucumber.steps.voucher;

import io.cucumber.java.en.When;
import it.pagopa.interop.authorization.domain.KeyPairPEM;
import it.pagopa.interop.authorization.service.utils.KeyPairGeneratorUtil;
import it.pagopa.interop.authorization.service.utils.voucher.VoucherService;
import it.pagopa.interop.authorization.service.utils.voucher.domain.ClientAssertionOptions;
import it.pagopa.interop.authorization.service.utils.voucher.domain.ClientAssertionOptions.ClientType;
import it.pagopa.interop.authorization.service.utils.voucher.domain.VoucherRequest;
import it.pagopa.interop.utils.HttpCallExecutor;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;

public class VoucherGenerationM2MSteps {

    private final SharedStepsContext sharedStepsContext;
    private final VoucherService voucherService;
    private final HttpCallExecutor httpCallExecutor;

    public VoucherGenerationM2MSteps(
        SharedStepsContext sharedStepsContext,
        VoucherService voucherService
    ) {
        this.sharedStepsContext = sharedStepsContext;
        this.voucherService = voucherService;
        this.httpCallExecutor = sharedStepsContext.getHttpCallExecutor();
    }

    @When("l'utente richiede la generazione del voucher M2M")
    public void voucherGenerationM2M() {
        ClientAssertionOptions assertionOptions = ClientAssertionOptions.builder()
            .clientType(ClientType.API)
            .clientId(sharedStepsContext.getClientCommonContext().getFirstClient().toString())
            .publicKey(sharedStepsContext.getClientCommonContext().getClientPublicKeyAsObj())
            .privateKey(sharedStepsContext.getClientCommonContext().getClientPrivateKeyAsObj())
            .build();
        String clientAssertion = this.voucherService.createClientAssertion(assertionOptions);

        VoucherRequest voucherRequest = VoucherRequest.builder()
            .clientId(sharedStepsContext.getClientCommonContext().getFirstClient().toString())
            .clientAssertion(clientAssertion)
            .build();
        httpCallExecutor.performCall(() ->
            voucherService.requestVoucherExpectingError(voucherRequest).getBody());
    }

    @When("l'utente richiede la generazione del voucher M2M indicando il primo client ma con la chiave caricata nel secondo")
    public void voucherGenerationM2MSecondClient() {
        ClientAssertionOptions assertionOptions = ClientAssertionOptions.builder()
            .clientType(ClientType.API)
            .clientId(sharedStepsContext.getClientCommonContext().getFirstClient().toString())
            .publicKey(sharedStepsContext.getClientCommonContext().getNewClientPublicKeyAsObj())
            .privateKey(sharedStepsContext.getClientCommonContext().getNewClientPrivateKeyAsObj())
            .build();
        String clientAssertion = this.voucherService.createClientAssertion(assertionOptions);

        VoucherRequest voucherRequest = VoucherRequest.builder()
            .clientId(sharedStepsContext.getClientCommonContext().getFirstClient().toString())
            .clientAssertion(clientAssertion)
            .build();
        httpCallExecutor.performCall(() ->
            voucherService.requestVoucherExpectingError(voucherRequest).getBody());
    }

    @When("l'utente richiede la generazione del voucher M2M con una chiave associata a nessun client")
    public void voucherGenerationM2MWithUnboundKey() {
        String keyType = "RSA";
        KeyPairPEM keyPair = KeyPairGeneratorUtil.createKeyPairPEM(keyType, 2048);
        ClientAssertionOptions assertionOptions = ClientAssertionOptions.builder()
            .clientType(ClientType.API)
            .clientId(sharedStepsContext.getClientCommonContext().getFirstClient().toString())
            .publicKey(keyPair.getPublicKeyAsObj(keyType))
            .privateKey(keyPair.getPrivateKeyAsObj(keyType))
            .build();
        String clientAssertion = this.voucherService.createClientAssertion(assertionOptions);

        VoucherRequest voucherRequest = VoucherRequest.builder()
            .clientId(sharedStepsContext.getClientCommonContext().getFirstClient().toString())
            .clientAssertion(clientAssertion)
            .build();
            httpCallExecutor.performCall(() ->
                voucherService.requestVoucherExpectingError(voucherRequest).getBody());
    }
}