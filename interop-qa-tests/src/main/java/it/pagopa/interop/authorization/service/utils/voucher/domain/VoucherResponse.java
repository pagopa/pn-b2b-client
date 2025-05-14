package it.pagopa.interop.authorization.service.utils.voucher.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VoucherResponse {
    private String accessToken;
    private Long expiresIn;
    private String tokenType;
}
