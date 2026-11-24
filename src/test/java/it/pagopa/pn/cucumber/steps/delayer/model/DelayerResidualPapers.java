package it.pagopa.pn.cucumber.steps.delayer.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class DelayerResidualPapers implements Serializable {

    private String downloadUrl;
    private Integer expiresIn;
    private String key;
}
