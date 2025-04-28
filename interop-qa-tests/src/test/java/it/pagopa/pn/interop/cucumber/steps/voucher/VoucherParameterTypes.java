package it.pagopa.pn.interop.cucumber.steps.voucher;

import io.cucumber.java.ParameterType;
import it.pagopa.interop.agreement.domain.ClientType;

public class VoucherParameterTypes {
    @ParameterType("CERTIFIED|DECLARED|VERIFIED")
    public ClientType clientType(String clientType) {
        return ClientType.fromValue(clientType);
    }
}
