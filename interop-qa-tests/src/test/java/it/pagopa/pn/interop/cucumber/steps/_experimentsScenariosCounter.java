package it.pagopa.pn.interop.cucumber.steps;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Data
@Slf4j
@Component
public class _experimentsScenariosCounter {
    private int freeScenarios = 0;
    private int lockedScenarios = 0;

    public int incFreeScenarios() {
        return ++freeScenarios;
    }

    public int incLockedScenarios() {
        return ++lockedScenarios;
    }

    public int decFreeScenarios() {
        return --freeScenarios;
    }

    public int decLockedScenarios() {
        return --lockedScenarios;
    }

}