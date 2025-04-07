package it.pagopa.pn.interop.cucumber.e_service_template.version.document;

import it.pagopa.pn.interop.cucumber.e_service_template.AbstractEServiceTemplateTest;
import org.junit.platform.suite.api.IncludeTags;

@SuppressWarnings("java:S2187")
/* TODO 04/04/2025 sarebbe il caso che questo test si riferisca direttamente al package al posto
    * di usare i TAG. In questo modo se una classe dovesse venire eliminata, aggiunta o
    * modificata in qualche modo, questa suite si adeguerebbe in automatico. */
@IncludeTags({
    EServiceTemplateVersionDocumentCreateTest.TAG,
    EServiceTemplateVersionDocumentUpdateTest.TAG,
    EServiceTemplateVersionDocumentDeleteTest.TAG,
    EServiceTemplateVersionDocumentReadTest.TAG
})
public class EServiceTemplateVersionDocumentTest extends AbstractEServiceTemplateTest {
}
