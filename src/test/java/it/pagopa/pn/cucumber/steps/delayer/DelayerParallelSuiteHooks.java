package it.pagopa.pn.cucumber.steps.delayer;

import io.cucumber.java.BeforeAll;
import it.pagopa.pn.cucumber.steps.CucumberSpringIntegration;
import it.pagopa.pn.cucumber.steps.delayer.model.DelayerSuiteContext;
import it.pagopa.pn.cucumber.steps.delayer.service.DelayerSevice;
import lombok.extern.slf4j.Slf4j;
import org.junit.platform.suite.api.ConfigurationParameter;
import org.springframework.test.context.TestContextManager;

import static it.pagopa.pn.cucumber.steps.delayer.model.DelayerSuiteContext.SCENARIO_IDS_PROPERTY;

/**
 * Una tantum: legge gli id dalla suite {@code -Dtest}, configura il gate, delete tabelle.
 */
@Slf4j
public final class DelayerParallelSuiteHooks {

    private DelayerParallelSuiteHooks() {
    }

    @BeforeAll
    public static void setUpDelayerSuite() throws Exception {
        String[] scenarioIds = resolveScenarioIdsFromSurefireTestProperty();
        if (scenarioIds.length == 0) {
            return;
        }

        DelayerSuiteContext.configure(scenarioIds);
        log.info("Delayer suite config: {}", String.join(",", scenarioIds));

        TestContextManager testContextManager = new TestContextManager(CucumberSpringIntegration.class);
        testContextManager.prepareTestInstance(new CucumberSpringIntegration());
        testContextManager.getTestContext().getApplicationContext()
                .getBean(DelayerSevice.class)
                .deleteDataAll();
    }

    static String[] resolveScenarioIdsFromSurefireTestProperty() {
        String testProp = System.getProperty("test");
        if (testProp == null || testProp.isBlank()) {
            return new String[0];
        }
        String className = testProp.split("[,#]")[0].trim();
        if (className.contains("*")) {
            return new String[0];
        }
        if (!className.contains(".")) {
            className = "it.pagopa.pn.cucumber." + className;
        }
        try {
            return readScenarioIds(Class.forName(className));
        } catch (ClassNotFoundException e) {
            log.warn("Suite non trovata da -Dtest={}: {}", testProp, e.toString());
            return new String[0];
        }
    }

    static String[] readScenarioIds(Class<?> suiteClass) {
        for (ConfigurationParameter parameter : suiteClass.getAnnotationsByType(ConfigurationParameter.class)) {
            if (SCENARIO_IDS_PROPERTY.equals(parameter.key()) && !parameter.value().isBlank()) {
                return parameter.value().split("\\s*,\\s*");
            }
        }
        return new String[0];
    }
}
