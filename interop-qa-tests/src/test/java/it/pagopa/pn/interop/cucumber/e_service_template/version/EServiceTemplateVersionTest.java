package it.pagopa.pn.interop.cucumber.e_service_template.version;

import it.pagopa.pn.interop.cucumber.e_service_template.AbstractEServiceTemplateTest;
import it.pagopa.pn.interop.cucumber.e_service_template.version.crud.EServiceTemplateVersionCrudCreateTest;
import it.pagopa.pn.interop.cucumber.e_service_template.version.crud.EServiceTemplateVersionCrudDeleteTest;
import it.pagopa.pn.interop.cucumber.e_service_template.version.crud.EServiceTemplateVersionCrudReadTest;
import it.pagopa.pn.interop.cucumber.e_service_template.version.crud.EServiceTemplateVersionCrudUpdateTest;
import it.pagopa.pn.interop.cucumber.e_service_template.version.lifecycle.EServiceTemplateVersionLifecycleActivateTest;
import it.pagopa.pn.interop.cucumber.e_service_template.version.lifecycle.EServiceTemplateVersionLifecyclePublishTest;
import it.pagopa.pn.interop.cucumber.e_service_template.version.lifecycle.EServiceTemplateVersionLifecycleSuspendTest;
import org.junit.platform.suite.api.IncludeTags;

@SuppressWarnings("java:S2187")
/* TODO 04/04/2025 sarebbe il caso che questo test si riferisca direttamente alle classi al posto
    * di usare i TAG. In questo modo se una classe dovesse venire eliminata, aggiunta o
    * modificata in qualche modo, questa suite si adeguerebbe in automatico. */
@IncludeTags({
    EServiceTemplateVersionCrudCreateTest.TAG,
    EServiceTemplateVersionCrudUpdateTest.TAG,
    EServiceTemplateVersionCrudDeleteTest.TAG,
    EServiceTemplateVersionCrudReadTest.TAG,

    EServiceTemplateVersionLifecyclePublishTest.TAG,
    EServiceTemplateVersionLifecycleSuspendTest.TAG,
    EServiceTemplateVersionLifecycleActivateTest.TAG,

    EServiceTemplateVersionAttributesUpdateTest.TAG,
    EServiceTemplateVersionQuotasUpdateTest.TAG
})
public class EServiceTemplateVersionTest extends AbstractEServiceTemplateTest {
}
