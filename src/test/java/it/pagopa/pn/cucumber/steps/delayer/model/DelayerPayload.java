package it.pagopa.pn.cucumber.steps.delayer.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class DelayerPayload implements Serializable {
    private String message;
    private String executionArn;
    private String startDate;
    private String status;
}
