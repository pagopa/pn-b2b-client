package it.pagopa.pn.interop.cucumber.e_service_template;

import static io.cucumber.junit.platform.engine.Constants.EXECUTION_MODE_FEATURE_PROPERTY_NAME;

import it.pagopa.pn.interop.cucumber.AbstractTest;
import org.junit.platform.suite.api.ConfigurationParameter;

/**
 * Base configuration class for all EServiceTemplate tests.
 */
@SuppressWarnings("java:S2187")
/* TODO: tentare di ri-arrangiare i test così che questi possano essere eseguiti in concorrenza
    senza errori */
@ConfigurationParameter(key = EXECUTION_MODE_FEATURE_PROPERTY_NAME, value = "same_thread")
public class AbstractEServiceTemplateTest extends AbstractTest {
}
