package it.pagopa.pn.cucumber.steps.delayer.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class DelayerPresigedUrl {
    private String key;
    private Integer expiresIn;
}
