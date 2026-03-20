package it.pagopa.pn.interop.cucumber.steps.m2m.apiv3.purposes;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
// TODO Threshold
// import it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.model.RemainingDailyCallsResponse;
import it.pagopa.interop.purpose.service.IM2MV3PurposeClient;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import org.junit.jupiter.api.Assertions;

import java.util.Map;
import java.util.UUID;

public class PurposesSteps {
    private final IM2MV3PurposeClient purposeClient;
    private final SharedStepsContext sharedStepsContext;

    public PurposesSteps(ClientTokenConfigurator clientTokenConfigurator, SharedStepsContext sharedStepsContext) {
        this.purposeClient = clientTokenConfigurator.getM2mV3PurposeClient();
        this.sharedStepsContext = sharedStepsContext;
    }

    @Given("i residui relativi alle dailyCalls associati alla finalità sono pari a per m2m:")
    public void checkRemainingDailyCallsM2M(DataTable table) {

        Map<String, String> expectedData = table.asMap(String.class, String.class);
        int expectedRemainingDailyCallsPerConsumer = Integer.parseInt(expectedData.get("remainingDailyCallsPerConsumer"));
        int expectedRemainingDailyCallsTotals = Integer.parseInt(expectedData.get("remainingDailyCallsTotals"));

        UUID purposeId = sharedStepsContext.getPurposeCommonContext().getPurposeIdAsUUID();

        // TODO Threshold
        /*sharedStepsContext.getHttpCallExecutor().performCall(
                () -> this.purposeClient.getRemainingDailyCalls(purposeId)
        );
        RemainingDailyCallsResponse response = (RemainingDailyCallsResponse) sharedStepsContext.getHttpCallExecutor().getResponse();

        Assertions.assertEquals(expectedRemainingDailyCallsPerConsumer, response.getRemainingDailyCallsPerConsumer());
        Assertions.assertEquals(expectedRemainingDailyCallsTotals, response.getRemainingDailyCallsTotal());*/
    }

    @Given("l'utente cerca di recuperare le soglie rimanenti per la finalità con ID {string} per m2m")
    public void getRemainingDailyCallsM2M(String purposeId) {

        UUID purposeIdAsUUID = switch (purposeId) {
            case "%null" -> null;
            case "%random" -> UUID.randomUUID();
            default -> throw new IllegalStateException("Unexpected value: " + purposeId);
        };

        // TODO Threshold
        /*sharedStepsContext.getHttpCallExecutor().performCall(
            () -> this.purposeClient.getRemainingDailyCalls(purposeId)
        );
        */
    }
}
