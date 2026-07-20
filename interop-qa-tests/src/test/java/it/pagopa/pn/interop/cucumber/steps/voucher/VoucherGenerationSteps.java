package it.pagopa.pn.interop.cucumber.steps.voucher;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.interop.authorization.service.utils.JWTUtils;
import it.pagopa.interop.authorization.service.utils.voucher.VoucherService;
import it.pagopa.interop.authorization.service.utils.voucher.domain.ClientAssertionOptions;
import it.pagopa.interop.authorization.service.utils.voucher.domain.ClientAssertionOptions.ClientType;
import it.pagopa.interop.authorization.service.utils.voucher.domain.VoucherRequest;
import it.pagopa.interop.authorization.service.utils.voucher.domain.VoucherResponse;
import it.pagopa.interop.common.IHttpExecutor;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.common.AuditTokenContext;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static it.pagopa.interop.authorization.service.utils.JWTUtils.decodeJwtPayload;
import static org.assertj.core.api.Assertions.fail;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

@Slf4j
public class VoucherGenerationSteps {

    private final SharedStepsContext sharedStepsContext;
    private final VoucherService voucherService;
    private final IHttpExecutor httpCallExecutor;

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

            assertSoftly(softly -> {
                softly.assertThat(voucherResponse).isNotNull();
                softly.assertThat(voucherResponse.getAccessToken()).isNotBlank();
                softly.assertThat(voucherResponse.getExpiresIn()).isNotNull();
                softly.assertThat(voucherResponse.getTokenType()).isEqualTo("Bearer");
            });
        } catch (IllegalArgumentException e) {
            fail("La conversione dell'oggetto restituito in %s è fallita. E' possibile "
                + "che la generazione del voucher non sia andata come previsto, o che il formato "
                + "della risposta sia cambiato nel tempo. Visionare i log degli step precedenti per "
                + "maggiori dettagli. Errore: %s", VoucherResponse.class.getName(), e.getMessage());
        }
    }

    @Then("si ottiene la corretta generazione del voucher m2m admin")
    public void checkVoucherGenerationM2MAdmin() {
        try {
            Object response = httpCallExecutor.getResponse();
            VoucherResponse voucherResponse = new ObjectMapper()
                .convertValue(response, VoucherResponse.class);
            Map<String, Object> jwtClaims = decodeJwtPayload(voucherResponse.getAccessToken());
            assertSoftly(softly -> {
                softly.assertThat(voucherResponse.getTokenType()).isEqualTo("Bearer");
                softly.assertThat(jwtClaims.get("adminId").toString())
                    .isEqualTo(sharedStepsContext.getClientCommonContext().getAdminId().toString());
                softly.assertThat(jwtClaims.get("role").toString())
                    .isEqualTo("m2m-admin");
            });
        } catch (IllegalArgumentException e) {
            fail("La conversione dell'oggetto restituito in %s è fallita. E' possibile "
                + "che la generazione del voucher non sia andata come previsto, o che il formato "
                + "della risposta sia cambiato nel tempo. Visionare i log degli step precedenti per "
                + "maggiori dettagli. Errore: %s", VoucherResponse.class.getName(), e.getMessage());
        }
    }

    @Then("si ottiene la corretta generazione del voucher di tipo {string} contenente le seguenti informazioni:")
    public void checkVoucherGeneration(String tokenType, List<Map<String, String>> rows) {
        Object response = httpCallExecutor.getResponse();
        VoucherResponse voucherResponse = new ObjectMapper()
                .convertValue(response, VoucherResponse.class);

        Map<String, List<String>> expectedAuditInfo = rows.stream()
                .collect(Collectors.groupingBy(
                        row -> row.get("position"),
                        Collectors.mapping(row -> row.get("element"), Collectors.toList())
                ));

        checkVoucherData(voucherResponse, expectedAuditInfo, tokenType);
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

    private void checkVoucherData(VoucherResponse voucherResponse, Map<String, List<String>> expectedAuditInfo, String tokenType) {

        AuditTokenContext context = sharedStepsContext.getAuditTokenContext();
        JWTUtils.JWTPojo jwt = JWTUtils.decodeJwt(voucherResponse.getAccessToken());

        log.info("Checking voucher data\n  header: {}\n  payload: {}", jwt.getHeader(), jwt.getPayload());

        assertSoftly(softly -> {

            softly.assertThat(voucherResponse).isNotNull();
            softly.assertThat(voucherResponse.getAccessToken()).isNotBlank();
            softly.assertThat(voucherResponse.getExpiresIn()).isNotNull();
            softly.assertThat(voucherResponse.getTokenType()).isEqualTo(tokenType);

            expectedAuditInfo.forEach((position, fields) -> {
                for (String field : fields) {
                    switch (position) {
                        case "header" -> {
                            softly.assertThat(AuditTokenContext.hasField(jwt.getHeader(), field))
                                    .as("L'header non contiene '%s'", field)
                                    .isTrue();
                            Object value = AuditTokenContext.resolveFieldValue(jwt.getHeader(), field);
                            context.addHeader(field, value != null ? value.toString() : null);
                        }
                        case "payload" -> {
                            softly.assertThat(AuditTokenContext.hasField(jwt.getPayload(), field))
                                    .as("Il payload non contiene la chiave: %s", field)
                                    .isTrue();
                            Object value = AuditTokenContext.resolveFieldValue(jwt.getPayload(), field);
                            context.addPayload(field, value != null ? value.toString() : null);
                        }
                        default -> fail("Only 'payload' or 'header' expected, found: " + position);
                    }
                }
            });
        });
    }
}
