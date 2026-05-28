package it.pagopa.pn.cucumber.steps.delayer.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class DelayerItems<T> implements Serializable {
    private List<T> items;
    private String lastEvaluatedKey;
}
