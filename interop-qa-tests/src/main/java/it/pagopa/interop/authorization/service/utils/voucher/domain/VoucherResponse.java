package it.pagopa.interop.authorization.service.utils.voucher.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/* DEV. NOTE 30/04/2025: è intenzionalmente identico a VoucherResponse, si è scelto di non legarli
* perché rappresentano due diversi oggetti di dominio che casualmente sono uguali ma che potrebbero
* evolvere differentemente */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VoucherResponse {
    private String clientId;
    private String clientAssertion;
    private String clientAssertionType;
    private String grantType;

}
