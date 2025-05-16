package it.pagopa.pn.interop.cucumber;

import org.junit.platform.suite.api.IncludeTags;

// Classe runner di test per la modifica del campo agreementApprovalPolicy dove il feature flag è attivato
@IncludeTags("app-edit-ff-on")
//@ConfigurationParameter(key = EXECUTION_MODE_FEATURE_PROPERTY_NAME, value = "same_thread") // FIXME 15/05/2025 per facilitare la diagnostica del problema che sta impedendo il completamento della Github Action. Rimuovere una volta risolto.
public class AgreementApprovalPolicyFFONTest extends AbstractAgreementApprovalPolicyTest{

}
