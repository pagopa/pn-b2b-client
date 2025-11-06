package it.pagopa.pn.interop.cucumber;

import it.pagopa.pn.interop.cucumber.e_service_template.AbstractEServiceTemplateTest;
import org.junit.platform.suite.api.ExcludeTags;
import org.junit.platform.suite.api.IncludeTags;

@IncludeTags({"e-service-template"})
@ExcludeTags({"e-service-template-receive"})
@SuppressWarnings("java:S2187")
public class EServiceTemplateTest extends AbstractEServiceTemplateTest {
}
