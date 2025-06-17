package it.pagopa.interop.authorization.service.utils;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.ECDSASigner;
import com.nimbusds.jose.crypto.ECDSAVerifier;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.jwk.*;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.text.ParseException;
import java.time.Instant;
import java.util.Date;
import java.util.Map;
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

    @Getter
    @RequiredArgsConstructor
    @ToString
    public static class ValidationResult {
        private final boolean valid;
        private final String message;
    }

    /**
     * Costruisce un DPoP JWT firmato con un header personalizzabile.
     *
     * @param privateKey La chiave privata EC per firmare il JWT.
     * @param publicKey  La chiave pubblica EC da includere nel JWK.
     * @param httpMethod Metodo HTTP usato nella richiesta (es. POST).
     * @param htu        URI della risorsa (es. endpoint /token).
     * @param typ        Valore da usare nel campo 'typ' dell'header (es. "dpop+jwt").
     * @return Il DPoP JWT firmato.
     */
    public String buildProofWith(PrivateKey privateKey, PublicKey publicKey, String httpMethod, String htu, String typ) {
        if (privateKey instanceof ECPrivateKey ecPrivateKey && publicKey instanceof ECPublicKey ecPublicKey)
            return buildEcDpopProof(ecPrivateKey, ecPublicKey, httpMethod, htu, typ);

        else if (privateKey instanceof RSAPrivateKey rsaPrivateKey && publicKey instanceof RSAPublicKey rsaPublicKey)
            return buildRsaDpopProof(rsaPrivateKey, rsaPublicKey, httpMethod, htu, typ);

        else
            throw new IllegalArgumentException("Unsupported key pair type for DPoP proof: " +
                    privateKey.getAlgorithm() + "/" + publicKey.getAlgorithm());

    }


    /**
     * Costruisce una DPoP proof valida, conforme alla RFC, con typ = "dpop+jwt".
     *
     * @param privateKey La chiave privata EC.
     * @param publicKey  La chiave pubblica EC.
     * @param httpMethod Metodo HTTP (es. POST).
     * @param htu        URI target della richiesta.
     * @return Il DPoP JWT firmato.
     */
    public String buildProof(PrivateKey privateKey, PublicKey publicKey, String httpMethod, String htu) {
        return buildProofWith(privateKey, publicKey, httpMethod, htu, "dpop+jwt");
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

        } catch (Exception e) {
            throw new RuntimeException("Errore nella verifica della firma DPoP: " + e.getMessage(), e);
        }
    }

    @SuppressWarnings("unchecked")
    public ValidationResult validateCnfJkt(String accessToken, String dpopJwt) {
        try {
            // Step 1: Estrai il campo cnf.jkt dal token
            SignedJWT token = SignedJWT.parse(accessToken);
            Map<String, Object> cnfClaim = (Map<String, Object>) token.getJWTClaimsSet().getClaim("cnf");

            if (cnfClaim == null || !cnfClaim.containsKey("jkt")) {
                return new ValidationResult(false, "Il campo 'cnf.jkt' non è presente nel token JWT.");
            }

            String jktFromToken = (String) cnfClaim.get("jkt");

            // Step 2: Estrai JWK dalla DPoP proof
            SignedJWT dpop = SignedJWT.parse(dpopJwt);
            Object jwkObj = dpop.getHeader().toJSONObject().get("jwk");

            if (jwkObj == null) {
                return new ValidationResult(false, "Il campo 'jwk' non è presente nell'header della DPoP proof.");
            }

            // Step 3: Calcola thumbprint secondo RFC 7638
            JWK jwk = JWK.parse((Map<String, Object>) jwkObj);
            String calculatedJkt = jwk.computeThumbprint().toString();

            // Step 4: Confronta
            if (!calculatedJkt.equals(jktFromToken)) {
                return new ValidationResult(false, String.format(
                        "Mismatch tra JKT calcolato e quello nel token. Atteso: %s, Trovato: %s",
                        calculatedJkt, jktFromToken
                ));
            }

            return new ValidationResult(true, "Il campo 'cnf.jkt' corrisponde al thumbprint calcolato del JWK.");

        } catch (ParseException | JOSEException e) {
            return new ValidationResult(false, "Errore durante la validazione del token: " + e.getMessage());
        }
    }

    private String buildEcDpopProof(ECPrivateKey privateKey, ECPublicKey publicKey, String httpMethod, String htu, String typ) {
        try {
            // 1. Costruzione JWK pubblica (senza "kid")
            ECKey ecPublicKey = new ECKey.Builder(Curve.P_256, publicKey).build();

            // 2. Header JWT con typ personalizzato
            JWSHeader header = new JWSHeader.Builder(JWSAlgorithm.ES256)
                    .type(new JOSEObjectType(typ)) // <-- typ parametrico
                    .jwk(ecPublicKey.toPublicJWK())
                    .build();

            // 3. Claims DPoP standard RFC 9449
            Instant now = Instant.now();
            JWTClaimsSet claims = new JWTClaimsSet.Builder()
                    .jwtID(UUID.randomUUID().toString())
                    .issueTime(Date.from(now))
                    .claim("htu", htu)
                    .claim("htm", httpMethod)
                    .claim("iat", now.getEpochSecond())
                    .build();

            // 4. Firma e serializzazione
            SignedJWT jwt = new SignedJWT(header, claims);
            jwt.sign(new ECDSASigner(privateKey));

            return jwt.serialize();

        } catch (Exception e) {
            log.error("Errore durante la generazione della DPoP proof EC con typ '{}'", typ, e);
            throw new IllegalStateException("Impossibile generare la proof DPoP EC con typ=" + typ, e);
        }
    }

    private String buildRsaDpopProof(RSAPrivateKey privateKey, RSAPublicKey publicKey, String httpMethod, String htu, String typ) {
        try {
            // 1. Costruzione JWK pubblica (senza "kid")
            RSAKey rsaPublicKey = new RSAKey.Builder(publicKey).build();

            // 2. Header JWT con typ personalizzato
            JWSHeader header = new JWSHeader.Builder(JWSAlgorithm.RS256)
                    .type(new JOSEObjectType(typ))
                    .jwk(rsaPublicKey.toPublicJWK())
                    .build();

            // 3. Claims DPoP standard RFC 9449
            Instant now = Instant.now();
            JWTClaimsSet claims = new JWTClaimsSet.Builder()
                    .jwtID(UUID.randomUUID().toString())
                    .issueTime(Date.from(now))
                    .claim("htu", htu)
                    .claim("htm", httpMethod)
                    .claim("iat", now.getEpochSecond())
                    .build();

            // 4. Firma e serializzazione
            SignedJWT jwt = new SignedJWT(header, claims);
            jwt.sign(new RSASSASigner(privateKey));

            return jwt.serialize();

        } catch (Exception e) {
            log.error("Errore durante la generazione della DPoP proof RSA con typ '{}'", typ, e);
            throw new IllegalStateException("Impossibile generare la proof DPoP RSA con typ=" + typ, e);
        }
    }

}
