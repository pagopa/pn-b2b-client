package it.pagopa.interop.authorization.service.factory;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import it.pagopa.interop.authorization.domain.Tenant;
import it.pagopa.interop.authorization.domain.ExternalId;
import it.pagopa.interop.conf.springconfig.InteropClientConfigs;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.kms.KmsClient;
import software.amazon.awssdk.services.kms.model.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Instant;
import java.util.*;

@Slf4j
public class SessionTokenFactory {
    private static final Map<String, Map<String, String>> CONFIG = new HashMap<>();
    private static final Map<String, Object> SESSION_TOKEN_PAYLOAD_TEMPLATE;
    private static final Map<String, String> SESSION_TOKEN_HEADER_TEMPLATE = Map.of(
            "typ", "at+jwt",
            "alg", "WELL_KNOWN_ALG",
            "use", "sig",
            "kid", "WELL_KNOWN_KID"
    );

    static {
        SESSION_TOKEN_PAYLOAD_TEMPLATE = new HashMap<>();
        SESSION_TOKEN_PAYLOAD_TEMPLATE.put("externalId", Map.of(
                "origin", "VALUES_EXT_ID_ORIGIN",
                "value", "VALUES_EXT_ID_VALUE"
        ));
        SESSION_TOKEN_PAYLOAD_TEMPLATE.put("user-roles", "VALUES_USER_ROLES");
        SESSION_TOKEN_PAYLOAD_TEMPLATE.put("selfcareId", "VALUES_SELFCARE_ID");
        SESSION_TOKEN_PAYLOAD_TEMPLATE.put("organizationId", "VALUES_ORG_ID");
        SESSION_TOKEN_PAYLOAD_TEMPLATE.put("organization", Map.of(
                "id", UUID.randomUUID().toString(),
                "name", "PagoPA S.p.A.",
                "roles", List.of(Map.of(
                        "partyRole", "MANAGER",
                        "role", "admin"
                )),
                "fiscal_code", "15376371009",
                "ipaCode", "5N2TR557"
        ));
        SESSION_TOKEN_PAYLOAD_TEMPLATE.put("uid", "VALUES_UID");
        SESSION_TOKEN_PAYLOAD_TEMPLATE.put("iss", "{{ENVIRONMENT}}.interop.pagopa.it");
        SESSION_TOKEN_PAYLOAD_TEMPLATE.put("aud", "{{ENVIRONMENT}}.interop.pagopa.it/ui");
        SESSION_TOKEN_PAYLOAD_TEMPLATE.put("nbf", 123);
        SESSION_TOKEN_PAYLOAD_TEMPLATE.put("iat", 123);
        SESSION_TOKEN_PAYLOAD_TEMPLATE.put("exp", 456);
        SESSION_TOKEN_PAYLOAD_TEMPLATE.put("jti", "uuid");
    }

    private final InteropClientConfigs interopClientConfigs;

    public SessionTokenFactory(InteropClientConfigs interopClientConfigs) {
        this.interopClientConfigs = interopClientConfigs;
    }

    public Map<String, Map<String, String>> generateSessionToken(List<Tenant> configFile) throws Exception {
        // Step 1. Read session token payload values file
        log.info("##Generating session token... ##");
        log.debug("##Step 1. Read session token payload values file ##");
        ObjectMapper objectMapper = new ObjectMapper();

        // Step 2. Parse well known
        log.debug("##Step 2. Parse well known ##");
        URL wellKnownUrl = new URL(interopClientConfigs.getRemoteWellknownUrl());
        boolean isSecure = wellKnownUrl.getProtocol().equalsIgnoreCase("https");
        Map<String, String> wellKnownData = fetchWellKnown(isSecure, wellKnownUrl.toString());
        if (!wellKnownData.containsKey("kid") || !wellKnownData.containsKey("alg")) {
            throw new IllegalStateException("Kid or alg not found.");
        }
        CONFIG.put("kms", Map.of(
                "kid", wellKnownData.get("kid"),
                "alg", "RSASSA_PKCS1_V1_5_SHA_256"
        ));
        log.debug("Got kid " + wellKnownData.get("kid") + " and alg " + wellKnownData.get("alg"));

        // Step 3. Generate STs header - Populate Session Token header from template
        log.debug("##Step 3. Generate STs header - Populate Session Token header from template ##");
        Map<String, String> stHeaderCompiled = new HashMap<>(SESSION_TOKEN_HEADER_TEMPLATE);
        stHeaderCompiled.put("kid", wellKnownData.get("kid"));
        stHeaderCompiled.put("alg", wellKnownData.get("alg"));
        log.debug("ST Header Compiled: " + stHeaderCompiled);

        // Step 4. Generate STs payload
        log.debug("## Step 4. Generate STs payload ##");
        long epochTimeSeconds = Instant.now().getEpochSecond();
        log.debug("Time in seconds since epoch: " + epochTimeSeconds);

        long epochTimeExpSeconds = epochTimeSeconds + interopClientConfigs.getSessionTokenDurationSec();
        log.debug("Expiration Time in seconds: " + epochTimeExpSeconds);

        String randomUUID = UUID.randomUUID().toString();
        log.debug("Random UUID: " + randomUUID);

        HashMap<String, Object> stPayloadCompiled = new HashMap<>(SESSION_TOKEN_PAYLOAD_TEMPLATE);
        stPayloadCompiled.put("nbf", epochTimeSeconds);
        stPayloadCompiled.put("iat", epochTimeSeconds);
        stPayloadCompiled.put("exp", epochTimeExpSeconds);
        stPayloadCompiled.put("jti", randomUUID);

        String environment = interopClientConfigs.getEnvironment();
        String stPayloadJson = objectMapper.writeValueAsString(stPayloadCompiled).replace("{{ENVIRONMENT}}", environment);
        stPayloadCompiled = objectMapper.readValue(stPayloadJson, new TypeReference<>() {});

        log.debug("ST Payload Compiled: " + stPayloadCompiled);

        log.debug("## Step 5. Generate unsigned STs ##");
        // Map<String, Map<String, String>> unsignedSTs = unsignedStsGeneration(stHeaderCompiled, stPayloadCompiled, sessionTokenPayloadValues, environment);
        Map<String, Map<String, String>> unsignedSTs = unsignedStsGeneration(stHeaderCompiled, stPayloadCompiled, configFile, environment);
        log.debug("Unsigned STs: " + unsignedSTs);

        log.debug("## Step 6. Generate signed STs ##");
        Map<String, Map<String, String>> signedSTs = signedStsGeneration(unsignedSTs);
        log.info("Session Token generation completed successfully.");

        return signedSTs;
    }

    private static Map<String, String> fetchWellKnown(boolean isSecure, String wellKnownUrl) throws Exception {
        HttpURLConnection connection = (HttpURLConnection) new URL(wellKnownUrl).openConnection();
        connection.setRequestMethod("GET");

        if (connection.getResponseCode() != 200) {
            throw new IllegalStateException("Failed to fetch well-known URL: " + connection.getResponseCode());
        }

        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        StringBuilder response = new StringBuilder();
        String line;

        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        reader.close();

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> responseData = objectMapper.readValue(response.toString(), new TypeReference<>() {});

        if (responseData.containsKey("keys") && ((List<?>) responseData.get("keys")).size() > 0) {
            Map<String, Object> keyData = ((List<Map<String, Object>>) responseData.get("keys")).get(0);
            return Map.of("kid", (String) keyData.get("kid"), "alg", (String) keyData.get("alg"));
        }

        return Collections.emptyMap();
    }


    public  Map<String, Map<String, String>> unsignedStsGeneration(
            Map<String, String> stHeaderCompiled, HashMap<String, Object> stPayloadCompiled, List<Tenant> stPayloadValues, String environment) throws IOException {

        try {
            log.debug("unsignedStsGeneration::Phase1:START: Build roles dynamic substitutions");
            Map<String, Object> stsSubOutput = new HashMap<>();

            for (Tenant tenant : stPayloadValues) {
               String organizationId = tenant.getOrganizationId().get(environment);
               String selfcareId = tenant.getSelfcareId();
               ExternalId externalId = tenant.getExternalId();
               Map<String, String> userRoles = tenant.getUserRoles();

               if (organizationId == null || selfcareId == null || externalId == null || userRoles == null) {
                    throw new IllegalArgumentException(String.format("Missing values for tenant %s in env %s", tenant, environment));
               }

               Map<String, Object> stsSubOutput2 = new HashMap<>();

               for (String interopRole : userRoles.keySet()) {
                   log.debug("unsignedStsGeneration::Phase1: Start dynamic substition for role {}", interopRole);
                   String uid = userRoles.get(interopRole);

                   Map<String, Object> stsSubOutput3 = deepCopy(stPayloadCompiled);

                   stsSubOutput3.put("externalId", externalId);
                   stsSubOutput3.put("uid", uid);
                   stsSubOutput3.put("selfcareId", selfcareId);
                   stsSubOutput3.put("organizationId", organizationId);
                   stsSubOutput3.put("user-roles", interopRole);

                   stsSubOutput2.put(interopRole, stsSubOutput3);
               }
               stsSubOutput.put(tenant.getName(), stsSubOutput2);

            }
            log.debug("unsignedStsGeneration::Phase1:END: Build roles dynamic substitutions");

            // Phase 2: Creation of partial JWTs for each tenant/role
            log.debug("unsignedStsGeneration::Phase2:START: Build base64 header and body for each tenant/role");

            String base64Header = b64UrlEncode(new ObjectMapper().writeValueAsString(stHeaderCompiled));
            log.debug("unsignedStsGeneration::Phase2: Build base64 header done");

            Map<String, Map<String, String>> stOutputIntermediate = new HashMap<>();


            for (String tenant : stsSubOutput.keySet()) {
                log.debug(String.format("unsignedStsGeneration::Phase2: Build partial JWT for %s", tenant));

                stOutputIntermediate.put(tenant, new HashMap<String, String>());

                for (String interopRole : ((Map<String, Object>)stsSubOutput.get(tenant)).keySet()) {
                    String base64Role = b64UrlEncode(new ObjectMapper().writeValueAsString(((Map<String, Object>)stsSubOutput.get(tenant)).get(interopRole)));
                    String poJwtForRole = base64Header + "." + base64Role;

                    stOutputIntermediate.get(tenant).put(interopRole, poJwtForRole);
                }

            }
            log.debug("unsignedStsGeneration::Phase2:END: Build base64 header and body for each tenant/role");

            return stOutputIntermediate;

        } catch (Exception ex) {
            System.err.println(ex);
            throw new RuntimeException(ex);
        }
    }

    private Map<String, Map<String, String>> signedStsGeneration(Map<String, Map<String, String>> unsignedStValues) throws Exception {
        log.debug("SignedTokenGeneration::START");
        Map<String, Map<String, String>> signedTokens = new HashMap<>();

        for (String tenant : unsignedStValues.keySet()) {
            log.debug("Building token for tenant {}", tenant);

            signedTokens.put(tenant, new HashMap<>());

            for (String tenantRole : ((Map<String, String>) unsignedStValues.get(tenant)).keySet()) {
                log.debug("Building token for role {}", tenantRole);

                String currentUnsignedJwt = ((Map<String, String>) unsignedStValues.get(tenant)).get(tenantRole);
                Map<String, Object> kmsSignResponse = kmsSign(currentUnsignedJwt);

                if (!kmsVerify(currentUnsignedJwt, (SignResponse) kmsSignResponse.get("signature"))) {
                    throw new IllegalArgumentException("Signed Token generation process failed to verify signature");
                }

                signedTokens.get(tenant).put(tenantRole, (String) kmsSignResponse.get("signedToken"));


            }
        }
        log.debug("SignedTokenGeneration::END");
        return signedTokens;
    }

    // Base64 URL-safe encoding function (without padding)
    public static String b64UrlEncode(String str) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(str.getBytes());
    }

    private HashMap<String, Object> deepCopy(HashMap<String, Object> map) {
        Gson gson = new Gson();
        String jsonString = gson.toJson(map);
        Type type = new TypeToken<HashMap<String, Object>>(){}.getType();
        return gson.fromJson(jsonString, type);
    }

    private Map<String, Object> kmsSign(String serializedToken) {
        if (serializedToken == null) {
            throw new IllegalArgumentException("kmsSign: invalid input - missing");
        }

        // SignCommandInput
        SignRequest signRequest = SignRequest.builder()
                .keyId(CONFIG.get("kms").get("kid"))
                .message(SdkBytes.fromUtf8String(serializedToken))
                .signingAlgorithm(CONFIG.get("kms").get("alg"))
                .build();

        SignResponse response = KmsClient.create().sign(signRequest);
        if (response == null) {
            throw new IllegalArgumentException("JWT Signature failed. Empty signature returned");
        }

        String kmsSignature = Base64.getUrlEncoder().withoutPadding().encodeToString(response.signature().asByteArray());
        return Map.of("signedToken", serializedToken + "." + kmsSignature,
                "signature", response);
    }

    private boolean kmsVerify(String unsignedToken, SignResponse signature) {
        if (unsignedToken == null || signature == null) {
            throw new IllegalArgumentException("kmsVerify: invalid input - missing");
        }

        // VerifyCommandInput
        VerifyRequest verifyRequest = VerifyRequest.builder()
                .keyId(CONFIG.get("kms").get("kid"))
                .message(SdkBytes.fromUtf8String(unsignedToken))
                .signingAlgorithm(CONFIG.get("kms").get("alg"))
                .signature(signature.signature())
                .build();

        VerifyResponse response = KmsClient.create().verify(verifyRequest);
        if (!response.signatureValid()) {
            throw new IllegalArgumentException("JWT Verify Signature failed");
        }
        return response.signatureValid();
    }

}
