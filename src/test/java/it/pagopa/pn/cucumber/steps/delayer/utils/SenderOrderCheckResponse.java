package it.pagopa.pn.cucumber.steps.delayer.utils;

import lombok.Data;

import java.util.List;

@Data
public class SenderOrderCheckResponse {

    private String checkResult;
    private List<String> missingMonths;

}
