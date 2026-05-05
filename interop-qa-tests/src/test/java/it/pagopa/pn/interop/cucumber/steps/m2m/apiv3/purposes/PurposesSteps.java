package it.pagopa.pn.interop.cucumber.steps.m2m.apiv3.purposes;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.model.RemainingDailyCallsResponse;
import it.pagopa.interop.purpose.service.IM2MV3PurposeClient;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.m2m.apiv3.purposes.resolver.PurposeResolver;
import it.pagopa.pn.interop.cucumber.steps.m2m.common.utils.AbstractResolver;
import it.pagopa.pn.interop.cucumber.utility.enums.ResolvableToken;
import org.junit.jupiter.api.Assertions;

import java.util.Map;
import java.util.UUID;

public class PurposesSteps {
    private final IM2MV3PurposeClient purposeClient;
    private final SharedStepsContext sharedStepsContext;
    private final PurposeResolver purposesResolver;

    public PurposesSteps(ClientTokenConfigurator clientTokenConfigurator, SharedStepsContext sharedStepsContext) {
        this.purposeClient = clientTokenConfigurator.getM2mV3PurposeClient();
        this.sharedStepsContext = sharedStepsContext;
        this.purposesResolver = new PurposeResolver(sharedStepsContext);
    }

    @Given("i residui relativi alle dailyCalls associati alla finalità sono pari a per m2m:")
    public void checkRemainingDailyCallsM2M(DataTable table) {

        Map<String, String> expectedData = table.asMap(String.class, String.class);
        int expectedRemainingDailyCallsPerConsumer = Integer.parseInt(expectedData.get("remainingDailyCallsPerConsumer"));
        int expectedRemainingDailyCallsTotals = Integer.parseInt(expectedData.get("remainingDailyCallsTotal"));

        UUID purposeId = this.purposesResolver.resolveOrParse(
                ResolvableToken.ACTUAL.value(),
                null,
                () -> this.sharedStepsContext.getPurposeCommonContext().getPurposeIdAsUUID(),
                null,
                null,
                null
        );

        sharedStepsContext.getHttpCallExecutor().performCall(
                () -> this.purposeClient.getRemainingDailyCalls(purposeId)
        );
        RemainingDailyCallsResponse response = (RemainingDailyCallsResponse) sharedStepsContext.getHttpCallExecutor().getResponse();

        Assertions.assertEquals(expectedRemainingDailyCallsPerConsumer, response.getRemainingDailyCallsPerConsumer());
        Assertions.assertEquals(expectedRemainingDailyCallsTotals, response.getRemainingDailyCallsTotal());
    }

    @Given("l'utente cerca di recuperare le soglie rimanenti per la finalità con ID {string} per m2m e si ottiene uno status code {int}")
    public void getRemainingDailyCallsM2M(String purposeId, Integer statusCode) {

        UUID purposeIdAsUUID = this.purposesResolver.resolveOrParse(
                purposeId,
                null,
                () -> this.sharedStepsContext.getPurposeCommonContext().getPurposeIdAsUUID(),
                null,
                UUID::randomUUID,
                null
        );

        sharedStepsContext.getPollingService().makePolling(
                () -> sharedStepsContext.getHttpCallExecutor().performCall(
                        () -> this.purposeClient.getRemainingDailyCalls(purposeIdAsUUID)
                ),
                res -> statusCode == null || res.value() == statusCode,
                "Unexpected status code for getRemainingDailyCalls"
        );
    }
}
