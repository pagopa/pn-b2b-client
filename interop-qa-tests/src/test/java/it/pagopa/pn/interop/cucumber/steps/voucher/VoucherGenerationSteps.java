package it.pagopa.pn.interop.cucumber.steps.voucher;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

import com.fasterxml.jackson.databind.ObjectMapper;
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
        /* non serve fare il check di status code dell'ultima chiamata effettuata perché,
         * diversamente dal solito, l'ultima chiamata prevista prima di questo step non
         * dovrebbe lanciare un eccezione nemmeno in caso d'errore */

        try {
            Object response = httpCallExecutor.getResponse();
            VoucherResponse voucherResponse = new ObjectMapper()
                .convertValue(response, VoucherResponse.class);
            assertThat(voucherResponse.getTokenType()).isEqualTo("Bearer");
        } catch (IllegalArgumentException e) {
            fail("La conversione dell'oggetto restituito in %s è fallita. E' possibile "
                + "che la generazione del voucher non sia andata come previsto, o che il formato "
                + "della risposta sia cambiato nel tempo. Errore: %s", VoucherResponse.class.getName(), e.getMessage());
        }
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
