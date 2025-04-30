package it.pagopa.interop.authorization.service.utils.voucher.domain.jwt;

import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ConsumerClientAssertionJWTPayloadImpl extends ClientAssertionJWTPayloadImpl {
    private String purposeId;

    @Override
    public Map<String, Object> toJWTClaims() {
        Map<String, Object> jwtClaims = super.toJWTClaims();
        jwtClaims.put("purposeId", purposeId);
        return jwtClaims;
    }
}
