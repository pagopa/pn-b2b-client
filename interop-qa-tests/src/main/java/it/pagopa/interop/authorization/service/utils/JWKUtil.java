package it.pagopa.interop.authorization.service.utils;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.jwk.Curve;
import com.nimbusds.jose.jwk.ECKey;
import com.nimbusds.jose.util.Base64URL;

import java.security.interfaces.ECPublicKey;

public class JWKUtil {

    public static String calculateJwkThumbprint(ECPublicKey publicKey) {
        try {
            // Costruisce un ECKey con curva P-256 (come da specifica ES256 / RFC 9449)
            ECKey ecKey = new ECKey.Builder(Curve.P_256, publicKey).build();

            // Calcola il thumbprint usando SHA-256
            Base64URL thumbprint = ecKey.computeThumbprint("SHA-256");

            return thumbprint.toString();
        } catch (JOSEException e) {
            throw new RuntimeException("Errore durante il calcolo del JWK thumbprint", e);
        }
    }
}
