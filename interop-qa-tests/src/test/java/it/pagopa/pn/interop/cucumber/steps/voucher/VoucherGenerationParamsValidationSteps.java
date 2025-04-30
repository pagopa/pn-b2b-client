package it.pagopa.pn.interop.cucumber.steps.voucher;

import io.cucumber.java.en.When;
import it.pagopa.interop.authorization.service.utils.IdentityService;
import it.pagopa.interop.authorization.service.utils.voucher.VoucherService;
import it.pagopa.interop.authorization.service.utils.voucher.domain.ClientAssertionOptions;
import it.pagopa.interop.authorization.service.utils.voucher.domain.ClientAssertionOptions.ClientType;
import it.pagopa.interop.authorization.service.utils.voucher.domain.VoucherRequest;
import it.pagopa.interop.authorization.service.utils.voucher.domain.VoucherRequestParam;
import it.pagopa.interop.utils.HttpCallExecutor;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.DataPreparationService;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;

public class VoucherGenerationParamsValidationSteps {

    private final ClientTokenConfigurator clientTokenConfigurator;
    private final SharedStepsContext sharedStepsContext;
    private final IdentityService identityService;
    private final DataPreparationService dataPreparationService;
    private final VoucherService voucherService;
    private final HttpCallExecutor httpCallExecutor;

    public VoucherGenerationParamsValidationSteps(ClientTokenConfigurator clientTokenConfigurator,
        SharedStepsContext sharedStepsContext,
        DataPreparationService dataPreparationService,
        VoucherService voucherService) {
        this.clientTokenConfigurator = clientTokenConfigurator;
        this.sharedStepsContext = sharedStepsContext;
        this.identityService = sharedStepsContext.getIdentityService();
        this.dataPreparationService = dataPreparationService;
        this.voucherService = voucherService;
        this.httpCallExecutor = sharedStepsContext.getHttpCallExecutor();
    }

    @When("l'utente richiede la generazione del voucher con il parametro {voucherParam} diverso da quello atteso")
    public void whenUserRequestsVoucherGenerationWithUnexpectedParameter(
        VoucherRequestParam param) {
        ClientAssertionOptions assertionOptions = ClientAssertionOptions.builder()
            .clientType(ClientType.CONSUMER)
            .clientId(sharedStepsContext.getClientCommonContext().getFirstClient().toString())
            .purposeId(sharedStepsContext.getPurposeCommonContext().getPurposeId())
            .publicKey(sharedStepsContext.getClientCommonContext().getClientPublicKeyAsObj())
            .privateKey(sharedStepsContext.getClientCommonContext().getClientPrivateKeyAsObj())
            .build();
        String clientAssertion = this.voucherService.createClientAssertion(assertionOptions);

        VoucherRequest voucherRequest = VoucherRequest.builder()
            .clientId(sharedStepsContext.getClientCommonContext().getFirstClient().toString())
            .clientAssertion(clientAssertion)
            .build();
        voucherRequest.set(param, "unknown");
        httpCallExecutor.performCall(() ->
            voucherService.requestVoucherExpectingError(voucherRequest).getBody());
    }


    /*When(
  "l'utente richiede la generazione del voucher con il parametro {string} diverso da quello atteso",
  async function (param: string) {
    assertContextSchema(this, {
      clientId: z.string(),
      purposeId: z.string(),
      privateKey: z.string(),
      publicKey: z.string(),
    });

    const tParam = z
      .union([z.literal("client_assertion_type"), z.literal("grant_type")])
      .parse(param);

    const { publicKey, privateKey, clientId, purposeId } = this;

    const clientAssertion = createClientAssertion({
      clientType: "CONSUMER",
      clientId,
      purposeId,
      publicKey,
      privateKey,
    });

    const key =
      tParam === "client_assertion_type" ? "clientAssertionType" : "grantType";

    this.response = await requestVoucher({
      clientId,
      clientAssertion,
      [key]: "unknown",
    });
  }
);*/
}
