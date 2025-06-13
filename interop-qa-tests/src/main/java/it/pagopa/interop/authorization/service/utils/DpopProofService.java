package it.pagopa.interop.authorization.service.utils;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.ECDSASigner;
import com.nimbusds.jose.jwk.Curve;
import com.nimbusds.jose.jwk.ECKey;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.extern.slf4j.Slf4j;

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
            // 1. Costruisce la JWK pubblica (EC P-256)
            ECKey jwk = new ECKey.Builder(Curve.P_256, publicKey)
                    .privateKey(privateKey)
                    .keyIDFromThumbprint()
                    .build();

            // 2. Header JOSE con algoritmo ES256 e tipologia DPoP
            JWSHeader header = new JWSHeader.Builder(JWSAlgorithm.ES256)
                    .type(new JOSEObjectType("dpop+jwt"))
                    .jwk(jwk.toPublicJWK())
                    .build();

            // 3. Claims obbligatori RFC9449 §4.2
            Instant now = Instant.now();
            JWTClaimsSet claims = new JWTClaimsSet.Builder()
                    .jwtID(UUID.randomUUID().toString())
                    .issueTime(Date.from(now))
                    .notBeforeTime(Date.from(now))
                    .claim("htu", htu)
                    .claim("htm", httpMethod)
                    .build();

            // 4. Firma del JWT
            SignedJWT jwt = new SignedJWT(header, claims);
            jwt.sign(new ECDSASigner(privateKey));
            return jwt.serialize();

        } catch (Exception e) {
            log.error("Errore durante la generazione della proof DPoP", e);
            throw new IllegalStateException("Impossibile generare la proof DPoP", e);
        }
    }
}
