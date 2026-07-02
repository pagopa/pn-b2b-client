package it.pagopa.pn.cucumber.steps.delayer.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class DelayerPresigneUrlUpload extends DelayerPresigedUrl {

    private String uploadUrl;
    private Map<String, String> requiredHeaders;

}
