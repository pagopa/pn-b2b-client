package it.pagopa.interop.common.interceptor.dpop.utils;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.jwk.*;
import com.nimbusds.jwt.SignedJWT;
import it.pagopa.interop.authorization.domain.dpop.AgidJwtProperties;
import it.pagopa.interop.common.interceptor.dpop.IntegrityValidationInterceptor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.text.ParseException;
import java.util.List;

public class AgidJwtSignatureVerifier {

    private final RestTemplate restTemplate;
    private final AgidJwtProperties properties;

    public AgidJwtSignatureVerifier(RestTemplate restTemplate, AgidJwtProperties properties) {
        this.restTemplate = restTemplate;
        this.properties = properties;
    }

    public void verify(String jwt) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(jwt);

            String kid = signedJWT.getHeader().getKeyID();
            String alg = signedJWT.getHeader().getAlgorithm() != null
                    ? signedJWT.getHeader().getAlgorithm().getName()
                    : null;

            if (kid == null || kid.isBlank()) {
                throw new IntegrityValidationInterceptor.IntegrityValidationException(
                        "Agid-JWT-Signature missing kid in header"
                );
            }

            if (!JWSAlgorithm.RS256.getName().equals(alg)) {
                throw new IntegrityValidationInterceptor.IntegrityValidationException(
                        "Unsupported JWT alg: " + alg
                );
            }

            JWKSet jwkSet = loadJwkSet();

            List<JWK> matches = new JWKSelector(
                    new JWKMatcher.Builder()
                            .keyID(kid)
                            .keyType(KeyType.RSA)
                            .build()
            ).select(jwkSet);

            if (matches.isEmpty()) {
                throw new IntegrityValidationInterceptor.IntegrityValidationException(
                        "No JWK found for kid=" + kid
                );
            }

            JWK jwk = matches.get(0);

            boolean valid = signedJWT.verify(new RSASSAVerifier(jwk.toRSAKey()));

            if (!valid) {
                throw new IntegrityValidationInterceptor.IntegrityValidationException(
                        "Invalid Agid-JWT-Signature signature"
                );
            }

        } catch (ParseException e) {
            throw new IntegrityValidationInterceptor.IntegrityValidationException(
                    "Cannot parse Agid-JWT-Signature: " + e.getMessage(), e
            );
        } catch (JOSEException e) {
            throw new IntegrityValidationInterceptor.IntegrityValidationException(
                    "Cannot verify Agid-JWT-Signature: " + e.getMessage(), e
            );
        } catch (Exception e) {
            if (e instanceof IntegrityValidationInterceptor.IntegrityValidationException ive) {
                throw ive;
            }
            throw new IntegrityValidationInterceptor.IntegrityValidationException(
                    "Error while loading JWKS / verifying signature: " + e.getMessage(), e
            );
        }
    }

    private JWKSet loadJwkSet() {
        try {
            String jwksUrl = properties.getJwksUrl();

            if (jwksUrl == null || jwksUrl.isBlank()) {
                throw new IntegrityValidationInterceptor.IntegrityValidationException(
                        "agid.jwt.jwks-url is null or blank"
                );
            }

            ResponseEntity<String> response = restTemplate.getForEntity(jwksUrl, String.class);
            String body = response.getBody();

            if (body == null || body.isBlank()) {
                throw new IntegrityValidationInterceptor.IntegrityValidationException(
                        "Empty JWKS response"
                );
            }

            return JWKSet.parse(body);

        } catch (ParseException e) {
            throw new IntegrityValidationInterceptor.IntegrityValidationException(
                    "Invalid JWKS format: " + e.getMessage(), e
            );
        } catch (Exception e) {
            if (e instanceof IntegrityValidationInterceptor.IntegrityValidationException ive) {
                throw ive;
            }
            throw new IntegrityValidationInterceptor.IntegrityValidationException(
                    "Cannot load JWKS: " + e.getMessage(), e
            );
        }
    }
}