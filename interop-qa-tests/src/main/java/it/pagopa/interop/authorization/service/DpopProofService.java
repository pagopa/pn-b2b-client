package it.pagopa.interop.authorization.service;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.ECDSASigner;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.jwk.Curve;
import com.nimbusds.jose.jwk.ECKey;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

public class DpopProofService {

    /**
     * Costruisce un DPoP proof JWT, firmato con una chiave privata (EC o RSA).
     *
     * @param privateKey  Chiave privata da usare per la firma (ECPrivateKey o RSAPrivateKey)
     * @param publicKey   Chiave pubblica corrispondente
     * @param httpMethod  Metodo HTTP della richiesta (es. POST)
     * @param httpUri     URI assoluto della richiesta (es. https://auth.server/token)
     * @param typOverride Se non null, usa questo valore per il campo "typ" nell'header JWS
     * @param issueTime   Se non null, usa questo valore per il campo "iat" del payload
     * @return DPoP JWT firmato in formato compatto
     */
    public String buildProof(
            PrivateKey privateKey,
            PublicKey publicKey,
            String httpMethod,
            String httpUri,
            String typOverride,
            Date issueTime
    ) {
        try {
            JWSAlgorithm algorithm;
            JWK jwk;

            if (privateKey instanceof ECPrivateKey && publicKey instanceof ECPublicKey) {
                algorithm = JWSAlgorithm.ES256;
                jwk = new ECKey.Builder(Curve.P_256, (ECPublicKey) publicKey)
                        .keyIDFromThumbprint()
                        .build();
            } else if (privateKey instanceof RSAPrivateKey && publicKey instanceof RSAPublicKey) {
                algorithm = JWSAlgorithm.RS256;
                jwk = new RSAKey.Builder((RSAPublicKey) publicKey)
                        .keyIDFromThumbprint()
                        .build();
            } else {
                throw new IllegalArgumentException("Tipo di chiave non supportato. Usare EC o RSA.");
            }

            // Header del JWT
            JWSHeader header = new JWSHeader.Builder(algorithm)
                    .type(new JOSEObjectType(typOverride != null ? typOverride : "dpop+jwt"))
                    .jwk(jwk)
                    .build();

            // Claims richiesti da DPoP
            JWTClaimsSet claims = new JWTClaimsSet.Builder()
                    .jwtID(UUID.randomUUID().toString())
                    .issueTime(issueTime != null ? issueTime : Date.from(Instant.now()))
                    .claim("htu", httpUri)
                    .claim("htm", httpMethod.toUpperCase())
                    .build();

            // Firma del JWT
            SignedJWT signedJWT = new SignedJWT(header, claims);
            JWSSigner signer;

            if (privateKey instanceof ECPrivateKey) {
                signer = new ECDSASigner((ECPrivateKey) privateKey);
            } else if (privateKey instanceof RSAPrivateKey) {
                signer = new RSASSASigner((RSAPrivateKey) privateKey);
            } else {
                throw new JOSEException("Tipo di chiave non supportato per la firma");
            }

            signedJWT.sign(signer);
            return signedJWT.serialize();

        } catch (JOSEException | IllegalArgumentException e) {
            throw new RuntimeException("Errore durante la generazione del DPoP JWT", e);
        }
    }

    /**
     * Metodo semplificato per creare una proof con chiave EC e parametri standard.
     */
    public String buildProof(ECPrivateKey privateKey, ECPublicKey publicKey, String method, String uri) {
        return buildProof(privateKey, publicKey, method, uri, null, null);
    }

    /**
     * Calcola il thumbprint JWK (jkt) a partire da una chiave pubblica.
     *
     * @param publicKey Chiave pubblica EC o RSA
     * @return Thumbprint JWK (Base64URL)
     */
    public String computeThumbprint(PublicKey publicKey) {
        try {
            JWK jwk;
            if (publicKey instanceof ECPublicKey) {
                jwk = new ECKey.Builder(Curve.P_256, (ECPublicKey) publicKey)
                        .keyIDFromThumbprint()
                        .build();
            } else if (publicKey instanceof RSAPublicKey) {
                jwk = new RSAKey.Builder((RSAPublicKey) publicKey)
                        .keyIDFromThumbprint()
                        .build();
            } else {
                throw new IllegalArgumentException("Chiave pubblica non supportata per il thumbprint");
            }

            return jwk.computeThumbprint().toString();
        } catch (JOSEException e) {
            throw new RuntimeException("Errore nel calcolo del thumbprint JWK", e);
        }
    }
}
