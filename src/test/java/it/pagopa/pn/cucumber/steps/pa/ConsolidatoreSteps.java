package it.pagopa.pn.cucumber.steps.pa;

import io.cucumber.java.en.Then;
import it.pagopa.pn.cucumber.steps.SharedSteps;
import it.pagopa.pn.cucumber.steps.pa.utilityVersions.B2bUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.HttpStatusCodeException;

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
        try {
            sharedSteps.getPnExternalServiceClient().pushConsolidatoreNotification(mapInfo);
        } catch (HttpStatusCodeException e) {
            this.sharedSteps.setNotificationError(e);
        }
    }

    private Map<String, String> populateConsolidatoreMap(Instant date) {
        String iun = sharedSteps.getNotificationIun();
        Map<String, String> mapInfo = new HashMap<>();
        mapInfo.put("requestId", requestIdConsolidator);
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
