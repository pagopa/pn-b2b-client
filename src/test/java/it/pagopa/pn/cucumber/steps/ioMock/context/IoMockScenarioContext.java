package it.pagopa.pn.cucumber.steps.ioMock.context;

import com.fasterxml.jackson.databind.JsonNode;
import io.cucumber.java.Before;
import io.cucumber.spring.ScenarioScope;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@ScenarioScope(proxyMode = ScopedProxyMode.NO)
public class IoMockScenarioContext {

    private Map<String, Object> requestPayload;
    private ResponseEntity<String> responseEntity;
    private int actualStatusCode;
    private String responseBody;
    private JsonNode responseJson;
    private boolean isTransparentRouting;

    // Campi per la gestione sequenze e messaggi (Flussi 2, 3 e 4)
    private String createdMessageId;
    private long submitTimestamp;
    private String sequenceName;
    private Map<String, String> requestHeaders;
    private String rawPayloadString;

    // Campi dedicati al Polling Stato Messaggio (Flusso 3) e Routing (Flusso 4)
    private String queriedFiscalCode;
    private String queriedMessageId;
    private String polledStatus;
    private String polledReadStatus;
    private String polledPaymentStatus;

    public IoMockScenarioContext() {
        resetContext();
    }

    @Before
    public void resetContext() {
        requestPayload = new HashMap<>();
        requestHeaders = new HashMap<>();
        rawPayloadString = null;
        responseEntity = null;
        actualStatusCode = 0;
        responseBody = null;
        responseJson = null;
        isTransparentRouting = false;
        createdMessageId = null;
        submitTimestamp = 0L;
        sequenceName = null;
        queriedFiscalCode = null;
        queriedMessageId = null;
        polledStatus = null;
        polledReadStatus = null;
        polledPaymentStatus = null;
    }
}
