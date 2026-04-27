package it.pagopa.pn.cucumber.steps.delayer.model;

import lombok.Data;

@Data
public abstract class DelayerPresigedUrl {
    private String key;
    private Integer expiresIn;
}
