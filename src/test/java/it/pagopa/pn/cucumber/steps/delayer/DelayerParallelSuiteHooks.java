package it.pagopa.pn.cucumber.steps.delayer;

import io.cucumber.java.BeforeAll;
import it.pagopa.pn.cucumber.steps.CucumberSpringIntegration;
import it.pagopa.pn.cucumber.steps.delayer.model.DelayerSuiteContext;
import it.pagopa.pn.cucumber.steps.delayer.service.DelayerSevice;
import lombok.extern.slf4j.Slf4j;
import org.springframework.test.context.TestContextManager;

/**
 * Setup una tantum della suite {@code @delayerParallel}: delete tabelle prima degli scenari.
 * I partecipanti al gate sono configurati nello static initializer del runner.
 */
@Slf4j
public final class DelayerParallelSuiteHooks {

    private DelayerParallelSuiteHooks() {
    }

    @BeforeAll
    public static void setUpParallelSuite() throws Exception {
        if (!DelayerSuiteContext.isSuiteConfigured()) {
            return;
        }

        TestContextManager testContextManager = new TestContextManager(CucumberSpringIntegration.class);
        testContextManager.prepareTestInstance(new CucumberSpringIntegration());
        var applicationContext = testContextManager.getTestContext().getApplicationContext();

        log.info("Suite Delayer @BeforeAll: pulizia tabelle una tantum");
        applicationContext.getBean(DelayerSevice.class).deleteDataAll();
    }
}
