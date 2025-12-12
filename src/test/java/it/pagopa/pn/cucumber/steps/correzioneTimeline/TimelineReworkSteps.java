package it.pagopa.pn.cucumber.steps.correzioneTimeline;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.And;
import it.pagopa.pn.client.b2b.generated.openapi.clients.delivery.rework.model.ReworkItem;
import it.pagopa.pn.client.b2b.generated.openapi.clients.delivery.rework.model.ReworkItemsResponse;
import it.pagopa.pn.client.b2b.generated.openapi.clients.delivery.rework.model.ReworkRequest;
import it.pagopa.pn.client.b2b.generated.openapi.clients.delivery.rework.model.ReworkResponse;
import it.pagopa.pn.client.b2b.pa.service.impl.ReworkTimelineClientImpl;
import it.pagopa.pn.cucumber.steps.SharedSteps;
import org.junit.jupiter.api.Assertions;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpStatusCodeException;

import java.util.List;
import java.util.Map;

public class TimelineReworkSteps {
    private final ReworkTimelineClientImpl reworkTimelineClient;
    private final SharedSteps sharedSteps;

    private ReworkResponse reworkResponse;
    private HttpStatus httpStatusCode;

    public TimelineReworkSteps(ReworkTimelineClientImpl reworkTimelineClient, SharedSteps sharedSteps) {
        this.reworkTimelineClient = reworkTimelineClient;
        this.sharedSteps = sharedSteps;
    }

    @And("viene invocata una richiesta di rework per la notifica appena creata")
    public void callReworkTimeline() {
        try {
            reworkResponse = reworkTimelineClient.notificationRework(sharedSteps.getNotificationIun(), createRequestRework());
        } catch (HttpStatusCodeException e) {
            httpStatusCode = e.getStatusCode();
        }
    }

    @And("si verifica che in fase di rework non ci sono richieste appese in stato diverso da DONE o ERROR")
    public void verifyReworkStatusNotInDoneOrErrorStatus() {
        ReworkItemsResponse reworkItemsResponse = reworkTimelineClient.retrieveNotificationReworkById(sharedSteps.getNotificationIun(), null);
        reworkItemsResponse.getItems().stream()
                .filter(reworkItem -> !List.of(ReworkItem.StatusEnum.DONE, ReworkItem.StatusEnum.ERROR).contains(reworkItem.getStatus()))
                .findAny()
                .ifPresent(value -> { throw new RuntimeException("Errore ci sono richieste in sospeso con stato diverso da DONE o ERROR"); });
    }

    @And("si verifica che la richiesta di rework effettuata sia in stato {string}")
    public void verifyReworkStatusById(String status) {
        ReworkItemsResponse reworkItemsResponse = reworkTimelineClient.retrieveNotificationReworkById(sharedSteps.getNotificationIun(), reworkResponse.getReworkId());
        reworkItemsResponse.getItems().stream()
                .filter(reworkItem -> reworkItem.getStatus() == ReworkItem.StatusEnum.fromValue(status))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Errore la richiesta creata non è nello stato desiderato!"));
    }

    @And("viene invocata una richiesta di rework per la notifica appena creata con i seguenti parametri:")
    public void callReworkWithParams(DataTable params) {
        Map<String, String> inputData = params.asMaps().get(0);
        String attemptId = getParams(inputData, "attemptId", "ATTEMPT_0");
        try {
            reworkTimelineClient.notificationRework(getParams(inputData, "iun", sharedSteps.getNotificationIun()),
                    createRequestRework(
                            attemptId != null ? ReworkRequest.AttemptIdEnum.fromValue(attemptId) : null,
                            getParams(inputData, "reason", "reason"),
                            getParams(inputData, "pcRetry", "PCRETRY_0"),
                            getParams(inputData, "recIndex", "RECINDEX_0"),
                            getParams(inputData, "expectedStatusCode", "RECRI003C"),
                            getParams(inputData, "expectedDeliveryFailureCause", null)
                    ));
        } catch (HttpStatusCodeException exception) {
            httpStatusCode = exception.getStatusCode();
        }
    }

    private String getParams(Map<String, String> inputData, String key, String defaultValue) {
        String value = inputData.get(key);
        if (value == null) {
            return defaultValue;
        }
        return value.equalsIgnoreCase("EMPTY_STRING") ? null : value;
    }

    private ReworkRequest createRequestRework() {
        return createRequestRework(ReworkRequest.AttemptIdEnum._0, "reason", "PCRETRY_0", "RECINDEX_0", "RECRI003C", null);
    }

    private ReworkRequest createRequestRework(ReworkRequest.AttemptIdEnum attemptId, String reason, String pcRetry,
                                              String recIndex, String expectedStatusCode, String expectedDeliveryFailureCause) {
        ReworkRequest reworkRequest = new ReworkRequest();
        reworkRequest.setAttemptId(attemptId);
        reworkRequest.setExpectedDeliveryFailureCause(expectedDeliveryFailureCause);
        reworkRequest.setReason(reason);
        reworkRequest.setPcRetry(pcRetry);
        reworkRequest.setRecIndex(recIndex);
        reworkRequest.setExpectedStatusCode(expectedStatusCode);
        return reworkRequest;
    }

    @And("si verifica che la chiamata sia andata in errore con il seguente status code: {int}")
    public void verifyErrorResponseStatusCode(int statusCode) {
        Assertions.assertEquals(HttpStatus.resolve(statusCode), httpStatusCode);
    }
}
