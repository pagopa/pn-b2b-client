package it.pagopa.pn.cucumber.steps.delayer.model;

import lombok.Data;

import java.util.Map;

@Data
public class DelayerPresigneUrlUpload extends DelayerPresigedUrl {

    private String uploadUrl;
    private Map<String, String> requiredHeaders;

}
