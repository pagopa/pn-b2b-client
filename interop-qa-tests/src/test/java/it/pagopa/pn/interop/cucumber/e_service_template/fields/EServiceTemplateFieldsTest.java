package it.pagopa.pn.interop.cucumber.e_service_template.fields;

import it.pagopa.pn.interop.cucumber.e_service_template.AbstractEServiceTemplateTest;
import org.junit.platform.suite.api.IncludeTags;

/** Testing of edit of the EServiceTemplate fields.
 * It does not test edit of EServiceTemplate entity in its entirety, for which the
 * {@link it.pagopa.pn.interop.cucumber.e_service_template.crud.EServiceTemplateCrudTest}
 * class exists. */
@SuppressWarnings("java:S2187")
/* TODO 04/04/2025 sarebbe il caso che questo test si riferisca direttamente al package al posto
    * di usare i TAG. In questo modo se una classe dovesse venire eliminata, aggiunta o
    * modificata in qualche modo, questa suite si adeguerebbe in automatico. */
@IncludeTags({
    EServiceTemplateDescriptionUpdateTest.TAG,
    EServiceTemplateIntendedTargetUpdateTest.TAG,
    EServiceTemplateNameUpdateTest.TAG
})
public class EServiceTemplateFieldsTest extends AbstractEServiceTemplateTest {
}
