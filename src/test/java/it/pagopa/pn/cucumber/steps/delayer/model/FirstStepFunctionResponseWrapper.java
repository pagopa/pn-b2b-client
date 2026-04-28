package it.pagopa.pn.cucumber.steps.delayer.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;

import java.io.Serializable;
import java.util.Map;

@Data
public class FirstStepFunctionResponseWrapper implements Serializable {

    private int statusCode;
    private Map<String, String> headers;
    private DelayerPayload body;

    @JsonProperty("body")
    public void setBody(String body) {
        try {
            this.body = new ObjectMapper().readValue(body, DelayerPayload.class);
        } catch (Exception e) {
            throw new RuntimeException("Errore parsing body RUN_ALGORITHM", e);
        }
    }


}