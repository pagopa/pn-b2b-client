package it.pagopa.pn.interop.cucumber;

import org.junit.platform.suite.api.IncludeTags;

// Classe runner di test per la modifica del campo agreementApprovalPolicy dove il feature flag è attivato
@IncludeTags("app-edit-ff-on")
public class AgreementApprovalPolicyFFONTest extends AbstractAgreementApprovalPolicyTest{

}
