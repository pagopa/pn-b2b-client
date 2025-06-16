package it.pagopa.interop.authorization.service.utils;

import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.ECDSASigner;
import com.nimbusds.jose.crypto.ECDSAVerifier;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.jwk.Curve;
import com.nimbusds.jose.jwk.ECKey;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

/**
 * Servizio per la generazione di proof JWT DPoP (Demonstration of Proof-of-Possession).
 * <p>
 * Conforme a RFC 9449. Usato per firmare una richiesta token M2M con una chiave EC (P-256).
 */
@Slf4j
public class DpopProofService {

    @Value("${authorization.server.token.creation.url}")
    private String dpopHtu;

    /**
     * Costruisce un DPoP JWT firmato, da includere nell’header {@code DPoP} delle richieste OAuth.
     *
     * @param privateKey La chiave privata EC per la firma (P-256).
     * @param publicKey  La chiave pubblica EC corrispondente.
     * @param httpMethod Metodo HTTP previsto (es. {@code POST}).
     * @param htu        URI di destinazione della richiesta (es. endpoint /token).
     * @return JWT DPoP firmato.
     */
    public String buildProof(ECPrivateKey privateKey, ECPublicKey publicKey, String httpMethod, String htu) {
        try {
            // 1. Crea ECKey senza "kid"
            ECKey ecPublicKey = new ECKey.Builder(Curve.P_256, publicKey)
                    .build(); // <-- Non chiamare .keyIDFromThumbprint()

            // 2. Header JWT
            JWSHeader header = new JWSHeader.Builder(JWSAlgorithm.ES256)
                    .type(new JOSEObjectType("dpop+jwt"))
                    .jwk(ecPublicKey.toPublicJWK()) // Non contiene "kid"
                    .build();

            // 3. Payload JWT conforme a RFC 9449
            Instant now = Instant.now();
            JWTClaimsSet claims = new JWTClaimsSet.Builder()
                    .jwtID(UUID.randomUUID().toString())
                    .issueTime(Date.from(now))
                    .claim("htu", htu)
                    .claim("htm", httpMethod)
                    .claim("iat", now.getEpochSecond()) // iat come numero intero
                    .build();

            // 4. Firma
            SignedJWT jwt = new SignedJWT(header, claims);
            jwt.sign(new ECDSASigner(privateKey));

            return jwt.serialize();

        } catch (Exception e) {
            log.error("Errore durante la generazione della DPoP proof", e);
            throw new IllegalStateException("Impossibile generare la proof DPoP", e);
        }
    }

    public void verifyDpopProof(String dpopJwtRaw) {
        try {
            // 1. Parsing del JWT
            SignedJWT signedJWT = SignedJWT.parse(dpopJwtRaw);
            JWSHeader header = signedJWT.getHeader();

            // 2. Controllo 'typ' = 'dpop+jwt'
            if (header.getType() == null || !"dpop+jwt".equalsIgnoreCase(header.getType().toString())) {
                throw new IllegalArgumentException("Header 'typ' must be 'dpop+jwt'");
            }

            // 3. Estrazione JWK (chiave pubblica)
            JWK jwk = header.getJWK();
            if (jwk == null) {
                throw new IllegalArgumentException("Missing JWK in DPoP header");
            }

            // 4. Costruzione del verificatore
            JWSVerifier verifier;
            if (jwk instanceof ECKey ecKey) {
                verifier = new ECDSAVerifier(ecKey);
            } else if (jwk instanceof RSAKey rsaKey) {
                verifier = new RSASSAVerifier(rsaKey);
            } else {
                throw new IllegalArgumentException("Unsupported key type: " + jwk.getKeyType());
            }

            // 5. Verifica della firma
            if (!signedJWT.verify(verifier)) {
                throw new SecurityException("DPoP proof signature is invalid");
            }

            // 6. Parsing del payload
            JWTClaimsSet claims = signedJWT.getJWTClaimsSet();

            // 7. Verifica htm = POST
            String htm = (String) claims.getClaim("htm");
            if (!"POST".equalsIgnoreCase(htm)) {
                throw new IllegalArgumentException("Invalid 'htm' claim: expected POST");
            }

            // 8. Verifica htu = expected URL
            String htu = (String) claims.getClaim("htu");
            String expectedHtu = dpopHtu;
            if (!expectedHtu.equalsIgnoreCase(htu)) {
                throw new IllegalArgumentException("Invalid 'htu' claim: unexpected URI");
            }

            // 9. Verifica iat (entro 60 secondi)
            Date issuedAt = claims.getIssueTime();
            if (issuedAt == null) {
                throw new IllegalArgumentException("Missing 'iat' claim");
            }
            long now = System.currentTimeMillis();
            long issuedAtTime = issuedAt.getTime();
            if (Math.abs(now - issuedAtTime) > 60_000) {
                throw new IllegalArgumentException("DPoP proof is outside the valid time window (60s)");
            }

            // 10. Presenza del jti
            String jti = claims.getJWTID();
            if (jti == null) {
                throw new IllegalArgumentException("Missing 'jti' claim");
            }

            // Tutte le verifiche superate
            System.out.println("✅ DPoP proof valida");

        } catch (Exception e) {
            throw new RuntimeException("Errore nella verifica della firma DPoP", e);
        }
    }


}
