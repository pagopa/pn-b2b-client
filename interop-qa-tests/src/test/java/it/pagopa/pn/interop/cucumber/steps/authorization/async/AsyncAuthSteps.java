package it.pagopa.pn.interop.cucumber.steps.authorization.async;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import it.pagopa.interop.authorization.service.utils.voucher.AsyncVoucherService;
import it.pagopa.interop.generated.openapi.clients.auth.model.ClientCredentialsResponse;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.authorization.model.VoucherContext;
import it.pagopa.pn.interop.cucumber.utility.delay_service.DelayService;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import static it.pagopa.pn.interop.cucumber.utility.CodecUtils.decodeBase64Url;

@Slf4j
public class AsyncAuthSteps {

    private final static String CLIENT_ASSERTION_TYPE = "urn:ietf:params:oauth:client-assertion-type:jwt-bearer";
    private final static String GRANT_TYPE = "client_credentials";

    private final ClientTokenConfigurator clientTokenConfigurator;
    private final SharedStepsContext sharedStepsContext;
    private final VoucherContext voucherContext;
    private final AsyncVoucherService asyncVoucherService;
    private final DelayService delayService;

    public AsyncAuthSteps(
            ClientTokenConfigurator clientTokenConfigurator,
            SharedStepsContext sharedStepsContext,
            VoucherContext voucherContext,
            AsyncVoucherService asyncVoucherService,
            DelayService delayService
    ) {
        this.clientTokenConfigurator = clientTokenConfigurator;
        this.sharedStepsContext = sharedStepsContext;
        this.voucherContext = voucherContext;
        this.asyncVoucherService = asyncVoucherService;
        this.asyncVoucherService.setHttpCallExecutor(sharedStepsContext.getHttpCallExecutor());
        this.delayService = delayService;
    }

    @Given("il tentant {currentActor} {string} attende la scadenza di responseTime di {int} secondi")
    @Given("il tentant {currentActor} {string} attende la scadenza di resourceAvailableTime di {int} secondi")
    public void waitUntilExpires(String actor, String tenant, int time) {
        delayService.delayForSeconds(time);
    }

    @And("il tenant {currentActor} {string} richiede un voucher asincrono per l'e-service")
    public void requestVoucher(String actor, String tenant) {

        UUID clientId = switch (actor) {
            case "fruitore" -> sharedStepsContext.getClientCommonContext().getLastPreparedClient().clientId();
            case "erogatore" -> sharedStepsContext.getProducerKeychainCommonContext().getFirstProducerKeychainId();
            default -> throw new RuntimeException("Actor not recognized");
        };

        try {
            ClientCredentialsResponse response = this.asyncVoucherService.requestVoucher(
                    voucherContext.getActualClientAssertion(),
                    CLIENT_ASSERTION_TYPE,
                    GRANT_TYPE,
                    voucherContext.getActualDpopProof(),
                    clientId
            );

            Assertions.assertThat(response.getAccessToken()).isNotNull();
            voucherContext.setActualAsyncAccessToken(response.getAccessToken());

        } catch (Exception e) {
            log.error("Errore durante la richiesta del voucher asincrono: {}", e.getMessage());
        }
    }

    @And("il voucher contiene i seguenti dati:")
    public void checkVoucher(DataTable dataTable) {
        String accessToken = voucherContext.getActualAsyncAccessToken();
        if (accessToken == null) {
            throw new AssertionError("Access token non disponibile nel contesto");
        }

        Map<String, String> expectedClaims = dataTable.asMap(String.class, String.class);

        try {
            String[] jwtParts = accessToken.split("\\.");
            if (jwtParts.length < 2) {
                throw new AssertionError("Formato JWT non valido per l'access token");
            }

            String payloadJson = decodeBase64Url(jwtParts[1]);
            ObjectMapper mapper = new ObjectMapper();
            JsonNode payload = mapper.readTree(payloadJson);

            String interactionId = payload.get("interactionId").asText();
            Assertions.assertThat(interactionId).as("interactionId non può essere null").isNotBlank();
            if (voucherContext.getActualInteractionId() == null) {
                voucherContext.setActualInteractionId(interactionId);
            }
            Assertions.assertThat(voucherContext.getActualInteractionId()).as("interactionId non corrisponde").isEqualTo(interactionId);

            expectedClaims.forEach((claim, expectedValue) -> {
                JsonNode actualNode = payload.get(claim);

                if (actualNode == null || actualNode.isMissingNode()) {
                    throw new AssertionError("Il claim '%s' non è presente nel voucher".formatted(claim));
                }

                String actualValue = actualNode.isTextual() ? actualNode.asText() : actualNode.toString();
                if (!Objects.equals(expectedValue, actualValue)) {
                    throw new AssertionError("Valore non corrispondente per il claim '%s'. Atteso: '%s', Attuale: '%s'"
                            .formatted(claim, expectedValue, actualValue));
                }
            });
        } catch (Exception e) {
            log.error("Errore durante la verifica del voucher: {}", e.getMessage());
            throw new RuntimeException("Errore durante la verifica del voucher", e);
        }
    }

}
