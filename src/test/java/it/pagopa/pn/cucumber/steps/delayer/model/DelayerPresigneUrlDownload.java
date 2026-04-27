package it.pagopa.pn.cucumber.steps.delayer.model;

import lombok.Data;

import java.util.Map;

@Data
public class DelayerPresigneUrlDownload extends DelayerPresigedUrl {

    private String downloadUrl;

}
