package it.pagopa.pn.interop.cucumber.utility;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import it.pagopa.pn.interop.cucumber.steps.dev_tools.config.DevToolsRequestConfig;

import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.Signature;
import java.util.Base64;
import java.util.List;

import static it.pagopa.interop.authorization.service.utils.JWTUtils.*;

public class CodecUtils {

    public static String decodeBase64Url(String value) {
        return new String(
                Base64.getUrlDecoder().decode(value),
                StandardCharsets.UTF_8
        );
    }

    public static String encodeBase64Url(String value) {
        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(value.getBytes(StandardCharsets.UTF_8));
    }

    public static String applyOverridesToEncodedJwt(
            String encodedJwt,
            List<DevToolsRequestConfig.JwtClaimOverride> overrides,
            KeyPair keyPair
    ) throws Exception {

        String[] jwtParts = encodedJwt.split("\\.", -1);
        if (jwtParts.length != 3) {
            throw new IllegalArgumentException("JWT non valido: attese 3 parti");
        }

        ObjectMapper mapper = new ObjectMapper();

        ObjectNode header = (ObjectNode) mapper.readTree(decodeBase64Url(jwtParts[0]));
        ObjectNode payload = (ObjectNode) mapper.readTree(decodeBase64Url(jwtParts[1]));

        String rawHeaderOverride = null;
        String rawPayloadOverride = null;

        for (DevToolsRequestConfig.JwtClaimOverride ov : overrides) {
            String claim = ov.claim();
            String raw = ov.value();

            switch (claim) {
                case "__rawHeader" -> rawHeaderOverride = raw;
                case "__rawPayload" -> rawPayloadOverride = raw;

                case "header.alg" -> header.put("alg", raw);
                case "header.kid" -> header.put("kid", raw);
                case "header.typ" -> header.put("typ", raw);
                case "__removeHeader" -> header.remove(raw);

                case "iss" -> payload.put("iss", raw);
                case "sub" -> payload.put("sub", raw);
                case "aud" -> setJsonClaim(mapper, payload, "aud", parseAud(raw));
                case "jti" -> payload.put("jti", raw);
                case "iat" -> setJsonClaim(mapper, payload, "iat", parseEpoch(raw));
                case "exp" -> setJsonClaim(mapper, payload, "exp", parseEpoch(raw));
                case "nbf" -> setJsonClaim(mapper, payload, "nbf", parseEpoch(raw));

                case "htm" -> payload.put("htm", raw);
                case "htu" -> payload.put("htu", raw);

                case "purposeId" -> setJsonClaim(mapper, payload, "purposeId", parseMaybeUuid(raw));
                case "digest" -> setJsonClaim(mapper, payload, "digest", parseMaybeJson(raw, mapper));
                case "algorithm" -> payload.put("algorithm", raw);
                case "assertionType" -> payload.put("client_assertion_type", raw);
                case "grantType" -> payload.put("grant_type", raw);

                case "urlCallback" -> payload.put("urlCallback", raw);
                case "scope" -> payload.put("scope", raw);
                case "interactionId" -> payload.put("interactionId", raw);
                case "entityNumber" -> {
                    try {
                        payload.put("entityNumber", Integer.valueOf(raw));
                    } catch(Exception e) {
                        payload.put("entityNumber", raw);
                    }
                }

                case "invalidClaim" -> payload.put("invalid_claim", raw);

                case "__remove" -> payload.remove(raw);

                default -> throw new IllegalArgumentException("Claim non supportato: " + claim);
            }
        }

        String newHeaderBase64Url = rawHeaderOverride != null
                ? encodeBase64Url(rawHeaderOverride)
                : encodeBase64Url(mapper.writeValueAsString(header));

        String newPayloadBase64Url = rawPayloadOverride != null
                ? encodeBase64Url(rawPayloadOverride)
                : encodeBase64Url(mapper.writeValueAsString(payload));

        if (keyPair != null) {
            String signingInput = newHeaderBase64Url + "." + newPayloadBase64Url;

            String keyAlg = keyPair.getPrivate().getAlgorithm();
            String sigAlg = switch (keyAlg) {
                case "RSA" -> "SHA256withRSA";
                case "EC"  -> "SHA256withECDSA";
                case "Ed25519" -> "Ed25519";
                default -> throw new IllegalArgumentException("Unsupported key");
            };

            Signature signature = Signature.getInstance(sigAlg);
            signature.initSign(keyPair.getPrivate());
            signature.update(signingInput.getBytes());
            String encodedSignature =Base64.getUrlEncoder()
                    .withoutPadding()
                    .encodeToString(signature.sign());
            return newHeaderBase64Url + "." + newPayloadBase64Url + "." + encodedSignature;
        }

        return newHeaderBase64Url + "." + newPayloadBase64Url + "." + jwtParts[2];
    }

    private static Object parseMaybeJson(String raw, ObjectMapper mapper) {
        try {
            return mapper.readTree(raw);
        } catch (Exception e) {
            return raw;
        }
    }

    private static void setJsonClaim(
            ObjectMapper mapper,
            ObjectNode node,
            String claimName,
            Object value
    ) {
        if (value == null) {
            node.putNull(claimName);
        } else {
            node.set(claimName, mapper.valueToTree(value));
        }
    }


}
