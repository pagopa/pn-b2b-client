package it.pagopa.pn.interop.cucumber.steps.voucher;

import io.cucumber.java.ParameterType;
import it.pagopa.interop.agreement.domain.ClientType;
import it.pagopa.interop.authorization.service.utils.voucher.domain.VoucherRequestParam;

public class VoucherParameterTypes {
    @ParameterType("CONSUMER|PRODUCER")
    public ClientType clientType(String clientType) {
        return ClientType.fromValue(clientType);
    }

    @ParameterType("client_assertion_type|grant_type")
    public VoucherRequestParam voucherParam(String voucherParam) {
        return switch (voucherParam) {
            case "client_assertion_type" -> VoucherRequestParam.CLIENT_ASSERTION_TYPE;
            case "grant_type" -> VoucherRequestParam.GRANT_TYPE;
            default -> throw new IllegalArgumentException("Unknown voucher parameter: " + voucherParam);
        };
    }
}
