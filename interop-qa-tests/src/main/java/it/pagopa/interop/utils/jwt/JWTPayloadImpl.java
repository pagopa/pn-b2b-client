package it.pagopa.interop.utils.jwt;

import it.pagopa.interop.utils.NonNullMap;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class JWTPayloadImpl implements JWTPayload {
    private String iss;
    private String sub;
    private String aud;
    private String jti;
    private Long iat;
    private Long exp;
    private Long nbf;

    /* TODO idea: mandare all'aria la struttura pensata fin'ora per i JWTPayload, e ripiegare
     soltando sulla lib. standard, così che nelle classi ClientAssertionOptions - che
     lascerei al netto di eventuali adeguamenti - si procede semplicemente alla sua costruzione
     con la classe Jwts, più malleabile.  */

    @Override
    public Map<String, Object> toJWTClaims() {
        Map<String, Object> claims = new NonNullMap<>(10);
        claims.put("iss", iss);
        claims.put("sub", sub);
        claims.put("aud", aud);
        claims.put("jti", jti);
        claims.put("iat", iat);
        claims.put("exp", exp);
        claims.put("nbf", nbf);

        return claims;
    }
}
