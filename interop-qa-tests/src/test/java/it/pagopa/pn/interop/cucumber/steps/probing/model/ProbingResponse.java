package it.pagopa.pn.interop.cucumber.steps.probing.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class ProbingResponse {
    private Integer probingFrequency;
    private LocalDateTime lastResponseTime;
}
