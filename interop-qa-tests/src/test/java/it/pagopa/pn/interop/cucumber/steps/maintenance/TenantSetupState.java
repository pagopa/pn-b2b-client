package it.pagopa.pn.interop.cucumber.steps.maintenance;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.util.concurrent.locks.ReentrantLock;

@Getter
@Component
public class TenantSetupState {
    private final ReentrantLock lock = new ReentrantLock();

    @Setter
    private volatile boolean setupPerformed;

    public boolean isSetupNotPerformed() {
        return !setupPerformed;
    }
}
