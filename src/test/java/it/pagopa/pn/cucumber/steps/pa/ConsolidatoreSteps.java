package it.pagopa.pn.cucumber.steps.pa;

import io.cucumber.java.en.Then;
import it.pagopa.pn.client.b2b.pa.wrapper.ApiCallHelper;
import it.pagopa.pn.client.b2b.pa.wrapper.ApiResult;
import it.pagopa.pn.cucumber.steps.SharedSteps;
import it.pagopa.pn.cucumber.steps.pa.utilityVersions.B2bUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class ConsolidatoreSteps {

    private final SharedSteps sharedSteps;

    @Value("${pn.external.allowed.future.offset.duration}")
    private String pnEcConsAllowedFutureOffsetDuration;

    @Value("${pn.consolidatore.requestId}")
    private String requestIdConsolidator;

    @Autowired
    public ConsolidatoreSteps(SharedSteps sharedSteps) {
        this.sharedSteps = sharedSteps;
    }

    @Then("viene invocato il consolidatore con clientRequestTimeStamp e statusDateTime nel {string}")
    public void vieneInvocatoIlConsolidatore(String statusDate) {
        Instant now = statusDate.equalsIgnoreCase("Futuro") ?
                Instant.now().plusSeconds(B2bUtils.convertToSeconds(pnEcConsAllowedFutureOffsetDuration)).plusSeconds(60)
                : Instant.now();
        Map<String, String> mapInfo = populateConsolidatoreMap(now);
        this.sharedSteps.setNotificationError(ApiCallHelper.callBody(() -> sharedSteps.getPnExternalServiceClient().pushConsolidatoreNotificationAttach(mapInfo)));
    }

    @Then("viene invocato due volte il consolidatore utilizzando la stessa request")
    public void vieneInvocatoIlConsolidatoreDueVolte() throws InterruptedException {
        Map<String, String> mapInfo = populateConsolidatoreMap(Instant.now());
        ApiResult<String> firstCall = ApiCallHelper.callBody(() -> sharedSteps.getPnExternalServiceClient().pushConsolidatoreNotificationAttach(mapInfo));
        if (firstCall.isError()) {
            this.sharedSteps.setNotificationError(firstCall);
            return;
        }
        Assertions.assertTrue(firstCall.body().contains("200.00"));
        Thread.sleep(2000);
        ApiResult<String> secondCall = ApiCallHelper.callBody(() -> sharedSteps.getPnExternalServiceClient().pushConsolidatoreNotificationAttach(mapInfo));
        if (secondCall.isError()) {
            this.sharedSteps.setNotificationError(secondCall);
            return;
        }
        Assertions.assertTrue(secondCall.body().contains("400.02"));
    }

    private Map<String, String> populateConsolidatoreMap(Instant date) {
        String iun = sharedSteps.getNotificationIun();
        Map<String, String> mapInfo = new HashMap<>();
        mapInfo.put("requestId", String.format("PREPARE_ANALOG_DOMICILE.IUN_%s.RECINDEX_0.ATTEMPT_0.PCRETRY_0", iun));
        mapInfo.put("attachments", null);
        mapInfo.put("clientRequestTimeStamp", B2bUtils.getOffsetDateTimeFromDate(date));
        mapInfo.put("deliveryFailureCause", null);
        mapInfo.put("discoveredAddress", null);
        mapInfo.put("iun", iun);
        mapInfo.put("productType", "890");
        mapInfo.put("registeredLetterCode", null);
        mapInfo.put("statusCode", "CON020");
        mapInfo.put("statusDateTime", B2bUtils.getOffsetDateTimeFromDate(date));
        mapInfo.put("statusDescription", "Affido conservato");
        return mapInfo;
    }
}
