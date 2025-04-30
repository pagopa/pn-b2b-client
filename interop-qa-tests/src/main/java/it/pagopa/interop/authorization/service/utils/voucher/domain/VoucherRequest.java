package it.pagopa.interop.authorization.service.utils.voucher.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VoucherRequest {
    private String clientId;

    private String clientAssertion;

    @Default
    private String clientAssertionType = "urn:ietf:params:oauth:client-assertion-type:jwt-bearer";

    @Default
    private String grantType = "client_credentials";

    public void set(VoucherRequestParam param, String value) {
        switch (param) {
            case CLIENT_ASSERTION_TYPE -> this.clientAssertionType = value;
            case GRANT_TYPE -> this.grantType = value;
            default -> throw new IllegalArgumentException("Unknown voucher parameter: " + param);
        }
    }
}
