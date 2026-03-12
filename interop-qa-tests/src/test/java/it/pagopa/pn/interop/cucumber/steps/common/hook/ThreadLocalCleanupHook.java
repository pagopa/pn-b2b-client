package it.pagopa.pn.interop.cucumber.steps.common.hook;

import io.cucumber.java.After;
import it.pagopa.interop.common.interceptor.dpop.utils.DpopThreadLocalCleaner;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ThreadLocalCleanupHook {

    private final DpopThreadLocalCleaner cleaner;

    @After
    public void cleanup() {
        cleaner.clear();
    }
}
