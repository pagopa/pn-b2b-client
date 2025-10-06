package it.pagopa.pn.cucumber.steps.recipient;

import it.pagopa.pn.cucumber.steps.SharedSteps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public class DelegheTemporaneeSteps {

    private final SharedSteps sharedSteps;

    @Autowired
    public DelegheTemporaneeSteps(SharedSteps sharedSteps) {
        this.sharedSteps = sharedSteps;
    }
}
