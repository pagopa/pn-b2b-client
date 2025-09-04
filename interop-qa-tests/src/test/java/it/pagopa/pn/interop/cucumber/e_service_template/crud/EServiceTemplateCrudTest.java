package it.pagopa.pn.interop.cucumber.e_service_template.crud;

import it.pagopa.pn.interop.cucumber.e_service_template.AbstractEServiceTemplateTest;
import org.junit.platform.suite.api.IncludeTags;

/** Testing of manipulation operations of the EServiceTemplate entity in its entirety.
 * It does not test specific editing operations for fields of the entity, for which the
 * {@link it.pagopa.pn.interop.cucumber.e_service_template.fields.EServiceTemplateFieldsTest}
 * class exists. */
@SuppressWarnings("java:S2187")
/* TODO 04/04/2025 sarebbe il caso che questo test si riferisca direttamente al package al posto
    * di usare i TAG. In questo modo se una classe dovesse venire eliminata, aggiunta o
    * modificata in qualche modo, questa suite si adeguerebbe in automatico. */
@IncludeTags({
    EServiceTemplateCrudCatalogTest.TAG,
    EServiceTemplateCrudCreateTest.TAG,
    EServiceTemplateCrudCreatorsTest.TAG,
    EServiceTemplateCrudProducersTest.TAG,
    EServiceTemplateCrudReadTest.TAG,
    EServiceTemplateCrudUpdateTest.TAG
})
public class EServiceTemplateCrudTest extends AbstractEServiceTemplateTest {
}
