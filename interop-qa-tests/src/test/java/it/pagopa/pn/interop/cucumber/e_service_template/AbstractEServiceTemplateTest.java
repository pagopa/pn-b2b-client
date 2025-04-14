package it.pagopa.pn.interop.cucumber.e_service_template;

import static io.cucumber.junit.platform.engine.Constants.EXECUTION_MODE_FEATURE_PROPERTY_NAME;

import it.pagopa.pn.interop.cucumber.AbstractTest;
import org.junit.platform.suite.api.ConfigurationParameter;

/**
 * Base configuration class for all EServiceTemplate tests.
 */
@SuppressWarnings("java:S2187")
@ConfigurationParameter(key = EXECUTION_MODE_FEATURE_PROPERTY_NAME, value = "concurrent")

/* È stato osservato che alcuni test generano errori se eseguiti in concorrenza; le seguenti
 * configurazioni servono a garantire che suddetti test non vengano eseguiti in parallelo. */
@ConfigurationParameter(key = "cucumber.execution.exclusive-resources.e-service-template-version-attributes-update.read-write", value = "it.pagopa.interop.resources.EServiceTemplateAttributesUpdateResources")
@ConfigurationParameter(key = "cucumber.execution.exclusive-resources.e-service-template-instance-update-concurrent-tag.read-write", value = "it.pagopa.interop.resources.EServiceTemplateInstanceUpdateResources")
/* *****************************************************************************************/
public class AbstractEServiceTemplateTest extends AbstractTest {
}
