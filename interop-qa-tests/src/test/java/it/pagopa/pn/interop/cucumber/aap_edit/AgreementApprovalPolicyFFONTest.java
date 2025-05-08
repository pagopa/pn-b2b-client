package it.pagopa.pn.interop.cucumber.aap_edit;

import org.junit.platform.suite.api.IncludeTags;

// Classe runner di test per la modifica del campo agreementApprovalPolicy dove il feature flag è attivato
@IncludeTags("app-edit-ff-on")
public abstract class AgreementApprovalPolicyFFONTest extends AbstractAgreementApprovalPolicyTest{

}
