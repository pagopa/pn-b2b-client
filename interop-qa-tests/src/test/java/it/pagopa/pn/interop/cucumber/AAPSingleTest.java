package it.pagopa.pn.interop.cucumber;

import static io.cucumber.junit.platform.engine.Constants.EXECUTION_MODE_FEATURE_PROPERTY_NAME;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeTags;

/* FIXME 15/05/2025 classe usata solo per facilitare la diagnostica del problema che sta impedendo
*   il completamento della Github Action. Rimuovere una volta risolto. */
@IncludeTags("aap-single-test")
@ConfigurationParameter(key = EXECUTION_MODE_FEATURE_PROPERTY_NAME, value = "same_thread")
public class AAPSingleTest extends AbstractAgreementApprovalPolicyTest{

}
