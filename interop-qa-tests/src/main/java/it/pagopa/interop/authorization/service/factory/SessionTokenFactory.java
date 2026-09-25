package it.pagopa.interop.authorization.service.factory;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import it.pagopa.interop.authorization.domain.ExternalId;
import it.pagopa.interop.authorization.domain.Tenant;
import it.pagopa.interop.authorization.service.exception.UnsignedSTSGenerationException;
import it.pagopa.interop.authorization.service.utils.ConfigFileReader;
import it.pagopa.interop.conf.InteropClientConfigs;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.kms.KmsClient;
import software.amazon.awssdk.services.kms.model.SignRequest;
import software.amazon.awssdk.services.kms.model.SignResponse;
import software.amazon.awssdk.services.kms.model.VerifyRequest;
import software.amazon.awssdk.services.kms.model.VerifyResponse;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Instant;
import java.util.*;
import java.util.Map.Entry;

import static it.pagopa.interop.authorization.service.utils.JWTUtils.isNotExpired;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.BooleanUtils.isNotTrue;

@Slf4j
public abstract class SessionTokenFactory {
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
        SESSION_TOKEN_PAYLOAD_TEMPLATE.put("nbf", 123);
        SESSION_TOKEN_PAYLOAD_TEMPLATE.put("iat", 123);
        SESSION_TOKEN_PAYLOAD_TEMPLATE.put("exp", 456);
        SESSION_TOKEN_PAYLOAD_TEMPLATE.put("jti", "uuid");
    }

    @Getter
    private final InteropClientConfigs interopClientConfigs;
    private final ConfigFileReader configFileReader;
    private final KmsClient kmsClient;

    private String lastMaintenanceToken;

    public SessionTokenFactory(
        InteropClientConfigs interopClientConfigs,
        ConfigFileReader configFileReader,
        KmsClient kmsClient
    ) {
        this.interopClientConfigs = interopClientConfigs;
        this.configFileReader = configFileReader;
        this.kmsClient = kmsClient;
    }

    public abstract Map<String, Map<String, List<String>>> loadToken();

    public abstract String getRemoteWellknownUrl();

    public Map<String, Map<String, List<String>>> generateSessionToken() throws Exception {
        // Step 1. Read session token payload values file
        log.info("##Generating session token... ##");
        log.debug("##Step 1. Read session token payload values file ##");
        ObjectMapper objectMapper = new ObjectMapper();

        // Step 2. Parse well known
        log.debug("##Step 2. Parse well known ##");
        URL wellKnownUrl = new URL(getRemoteWellknownUrl());
        Map<String, String> wellKnownData = fetchWellKnown(wellKnownUrl.toString());
        if (!wellKnownData.containsKey("kid") || !wellKnownData.containsKey("alg")) {
            throw new IllegalStateException("Kid or alg not found.");
        }
        CONFIG.put("kms", Map.of(
                "kid", wellKnownData.get("kid"),
                "alg", "RSASSA_PKCS1_V1_5_SHA_256"
        ));
        log.debug("Got kid {} and alg {}", wellKnownData.get("kid"), wellKnownData.get("alg"));

        // Step 3. Generate STs header - Populate Session Token header from template
        log.debug("##Step 3. Generate STs header - Populate Session Token header from template ##");
        Map<String, String> stHeaderCompiled = new HashMap<>(SESSION_TOKEN_HEADER_TEMPLATE);
        stHeaderCompiled.put("kid", wellKnownData.get("kid"));
        stHeaderCompiled.put("alg", wellKnownData.get("alg"));
        log.debug("ST Header Compiled: {}", stHeaderCompiled);

        // Step 4. Generate STs payload
        log.debug("## Step 4. Generate STs payload ##");
        long epochTimeSeconds = Instant.now().getEpochSecond();
        log.debug("Time in seconds since epoch: {}", epochTimeSeconds);

        long epochTimeExpSeconds = epochTimeSeconds + interopClientConfigs.getSessionTokenDurationSec();
        log.debug("Expiration Time in seconds: {}", epochTimeExpSeconds);

        String randomUUID = UUID.randomUUID().toString();
        log.debug("Random UUID: {}", randomUUID);

        HashMap<String, Object> stPayloadCompiled = new HashMap<>(SESSION_TOKEN_PAYLOAD_TEMPLATE);
        stPayloadCompiled.put("nbf", epochTimeSeconds);
        stPayloadCompiled.put("iat", epochTimeSeconds);
        stPayloadCompiled.put("exp", epochTimeExpSeconds);
        stPayloadCompiled.put("jti", randomUUID);

        String environment = interopClientConfigs.getEnvironment();
        String stPayloadJson = objectMapper.writeValueAsString(stPayloadCompiled).replace("{{ENVIRONMENT}}", environment);
        stPayloadCompiled = objectMapper.readValue(stPayloadJson, new TypeReference<>() {});

        log.debug("ST Payload Compiled: {}", stPayloadCompiled);

        log.debug("## Step 5. Generate unsigned STs ##");
        Map<String, Map<String, List<String>>> unsignedSTs = unsignedStsGeneration(stHeaderCompiled, stPayloadCompiled, configFileReader.getTenantList(), environment);
        log.debug("Unsigned STs: {}", unsignedSTs);

        log.debug("## Step 6. Generate signed STs ##");
        Map<String, Map<String, List<String>>> signedSTs = signedStsGeneration(unsignedSTs);
        log.info("Session Token generation completed successfully.");

        return signedSTs;
    }

    public String getMaintenanceToken() throws Exception {
        if(nonNull(lastMaintenanceToken) && isNotExpired(lastMaintenanceToken, 10000)) {
            return lastMaintenanceToken;
        }

        ObjectMapper objectMapper = new ObjectMapper();

        // 1. Calcolo dei timestamp crittografici
        Instant now = Instant.now();
        long iat = now.getEpochSecond();
        long nbf = iat; // Valido da subito
        long exp = Instant.now().plusSeconds(this.interopClientConfigs.getSessionTokenDurationSec()).getEpochSecond();

        // 2. Costruzione del Decoded Header
        Map<String, String> wellKnown = fetchWellKnown(getRemoteWellknownUrl());
        Map<String, Object> header = new LinkedHashMap<>();
        header.put("alg", wellKnown.get("alg"));
        header.put("typ", "at+jwt");
        header.put("kid", wellKnown.get("kid"));
        header.put("use", "sig");

        // 3. Costruzione del Decoded Payload
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("role", "maintenance");
        payload.put("sub", "829e6aad-43c9-44fe-a0d7-2ba5ab2a616c"); // UUID v4 dell'operatore
        payload.put("iss", "qa.interop.pagopa.it");
        payload.put("aud", "qa.interop.pagopa.it/internal");
        payload.put("nbf", nbf);
        payload.put("iat", iat);
        payload.put("exp", exp);
        payload.put("jti", UUID.randomUUID()); // UUID v4 del token

        // 4. Serializzazione in JSON e codifica in Base64Url (Senza Padding "=")
        String encodedHeader = Base64.getUrlEncoder().withoutPadding()
                .encodeToString(objectMapper.writeValueAsBytes(header));

        String encodedPayload = Base64.getUrlEncoder().withoutPadding()
                .encodeToString(objectMapper.writeValueAsBytes(payload));

        // Unione dei due blocchi per formare il "currentUnsignedJwt"
        String currentUnsignedJwt = encodedHeader + "." + encodedPayload;

        // 5. Processo di firma crittografica via KMS
        Map<String, Object> kmsSignResponse = kmsSign(currentUnsignedJwt);

        // Contro-verifica Zero-Trust
        SignResponse signatureObj = (SignResponse) kmsSignResponse.get("signature");
        if (!kmsVerify(currentUnsignedJwt, signatureObj)) {
            throw new IllegalArgumentException("Signed Token generation process failed to verify signature");
        }
        log.info("JWT successfully signed.");

        // Estrazione del JWT finale firmato
        this.lastMaintenanceToken = (String) kmsSignResponse.get("signedToken");
        log.info("Token validated and ready to use.");

        return this.lastMaintenanceToken;
    }

    private static Map<String, String> fetchWellKnown(String wellKnownUrl) throws Exception {
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

        if (responseData.containsKey("keys") && !((List<?>) responseData.get("keys")).isEmpty()) {
            Map<String, Object> keyData = ((List<Map<String, Object>>) responseData.get("keys")).get(0);
            return Map.of("kid", (String) keyData.get("kid"), "alg", (String) keyData.get("alg"));
        }

        return Collections.emptyMap();
    }


    private Map<String, Map<String, List<String>>> unsignedStsGeneration(Map<String, String> stHeaderCompiled,
       HashMap<String, Object> stPayloadCompiled, List<Tenant> stPayloadValues, String environment) {
        try {
            log.debug("unsignedStsGeneration::Phase1:START: Build roles dynamic substitutions");
            Map<String, Object> stsSubOutput = new HashMap<>();

            for (Tenant tenant : stPayloadValues) {
               String organizationId = tenant.getOrganizationId().get(environment);
               String selfcareId = tenant.getSelfcareId();
               ExternalId externalId = tenant.getExternalId();
               Map<String, List<String>> userRoles = tenant.getUserRoles();

               if (organizationId == null || selfcareId == null || externalId == null || userRoles == null) {
                    throw new IllegalArgumentException(String.format("Missing values for tenant %s in env %s", tenant, environment));
               }

               Map<String, Object> stsSubOutput2 = new HashMap<>();

               for (Entry<String, List<String>> interopRole : userRoles.entrySet()) {
                   log.debug("unsignedStsGeneration::Phase1: Start dynamic substition for role {}", interopRole);
                   List<String> uids = interopRole.getValue();

                   List<Map<String, Object>> stsSubOutput3List = new ArrayList<>();
                   for (String uid : uids) {
                       Map<String, Object> stsSubOutput3 = deepCopy(stPayloadCompiled);
                       stsSubOutput3.put("externalId", externalId);
                       stsSubOutput3.put("uid", uid);
                       stsSubOutput3.put("selfcareId", selfcareId);
                       stsSubOutput3.put("organizationId", organizationId);
                       stsSubOutput3.put("user-roles", interopRole.getKey());

                       stsSubOutput3List.add(stsSubOutput3);
                   }


                   stsSubOutput2.put(interopRole.getKey(), stsSubOutput3List);
               }
               stsSubOutput.put(tenant.getName(), stsSubOutput2);

            }
            log.debug("unsignedStsGeneration::Phase1:END: Build roles dynamic substitutions");

            // Phase 2: Creation of partial JWTs for each tenant/role
            log.debug("unsignedStsGeneration::Phase2:START: Build base64 header and body for each tenant/role");

            String base64Header = b64UrlEncode(new ObjectMapper().writeValueAsString(stHeaderCompiled));
            log.debug("unsignedStsGeneration::Phase2: Build base64 header done");

            Map<String, Map<String, List<String>>> stOutputIntermediate = new HashMap<>();


            for (Entry<String, Object> tenant : stsSubOutput.entrySet()) {
                log.debug("unsignedStsGeneration::Phase2: Build partial JWT for {}", tenant);

                stOutputIntermediate.put(tenant.getKey(), new HashMap<>());

                for (String interopRole : ((Map<String, Object>)tenant.getValue()).keySet()) {
                    List<Map<String, Object>> undecodedJwts = (List<Map<String, Object>>) ((Map<String, Object>) tenant.getValue()).get(interopRole);
                    List<String> poJwtForRoles = new ArrayList<>();
                    for(Map<String, Object> undecodedJwt : undecodedJwts) {
                        String stPayloadJson = new ObjectMapper().writeValueAsString(undecodedJwt);
                        String base64Body = b64UrlEncode(stPayloadJson);
                        poJwtForRoles.add(base64Header + "." + base64Body);
                    }

                    stOutputIntermediate.get(tenant.getKey()).put(interopRole, poJwtForRoles);
                }

            }
            log.debug("unsignedStsGeneration::Phase2:END: Build base64 header and body for each tenant/role");

            return stOutputIntermediate;

        } catch (Exception ex) {
            log.error("unsignedStsGeneration::Error", ex);
            throw new UnsignedSTSGenerationException("Error during unsigned STS generation", ex);
        }
    }

    private Map<String, Map<String, List<String>>> signedStsGeneration(Map<String, Map<String, List<String>>> unsignedStValues) {
        log.debug("SignedTokenGeneration::START");
        Map<String, Map<String, List<String>>> signedTokens = new HashMap<>();

        for (Entry<String,Map<String, List<String>>> tenant : unsignedStValues.entrySet()) {
            log.debug("Building token for tenant {}", tenant);

            signedTokens.put(tenant.getKey(), new HashMap<>());

            for (String tenantRole : tenant.getValue().keySet()) {
                log.debug("Building token for role {}", tenantRole);

                List<String> currentUnsignedJwts = unsignedStValues.get(tenant.getKey()).get(tenantRole);
                List<String> signedJwts = new ArrayList<>();
                for (String currentUnsignedJwt : currentUnsignedJwts) {
                    Map<String, Object> kmsSignResponse = kmsSign(currentUnsignedJwt);
                    if (!kmsVerify(currentUnsignedJwt, (SignResponse) kmsSignResponse.get("signature"))) {
                        throw new IllegalArgumentException("Signed Token generation process failed to verify signature");
                    }
                    signedJwts.add((String) kmsSignResponse.get("signedToken"));
                }


                signedTokens.get(tenant.getKey()).put(tenantRole, signedJwts);
            }
        }
        log.debug("SignedTokenGeneration::END");
        return signedTokens;
    }

    // Base64 URL-safe encoding function (without padding)
    private static String b64UrlEncode(String str) {
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

        SignResponse response = this.kmsClient.sign(signRequest);
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

        VerifyResponse response = this.kmsClient.verify(verifyRequest);
        if (isNotTrue(response.signatureValid())) {
            throw new IllegalArgumentException("JWT Verify Signature failed");
        }
        return response.signatureValid();
    }

    public Map<String, Object> getSessionTokenPayloadTemplate() {
        return SESSION_TOKEN_PAYLOAD_TEMPLATE;
    }

}
