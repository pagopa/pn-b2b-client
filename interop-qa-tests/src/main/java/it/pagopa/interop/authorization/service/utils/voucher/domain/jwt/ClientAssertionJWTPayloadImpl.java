package it.pagopa.interop.authorization.service.utils.voucher.domain.jwt;

import it.pagopa.interop.utils.jwt.JWTPayloadImpl;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ClientAssertionJWTPayloadImpl extends JWTPayloadImpl {
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    static class Digest {
        private String algorithm;
        private String value;
    }

    private Digest digest;

    @Override
    public Map<String, Object> toJWTClaims() {
        Map<String, Object> jwtClaims = super.toJWTClaims();
        jwtClaims.put("digest", digest);
        return jwtClaims;
    }
}
