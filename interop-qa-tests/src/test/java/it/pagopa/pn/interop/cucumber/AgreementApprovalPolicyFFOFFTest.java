package it.pagopa.pn.interop.cucumber;

import org.junit.platform.suite.api.IncludeTags;

// Classe runner di test per la modifica del campo agreementApprovalPolicy dove il feature flag è disattivato
@IncludeTags("app-edit-ff-off")
public abstract class AgreementApprovalPolicyFFOFFTest extends AbstractAgreementApprovalPolicyTest{

}
