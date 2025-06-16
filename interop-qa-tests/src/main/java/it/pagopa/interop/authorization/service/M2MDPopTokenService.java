package it.pagopa.interop.authorization.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.jwk.Curve;
import com.nimbusds.jose.jwk.ECKey;
import com.nimbusds.jose.util.Base64URL;
import it.pagopa.interop.authorization.domain.KeyPairDecorator;
import it.pagopa.interop.authorization.enums.M2MRole;
import it.pagopa.interop.authorization.enums.TokenKey;
import it.pagopa.interop.authorization.service.identity.IdentityService;
import it.pagopa.interop.authorization.service.utils.DpopProofService;
import it.pagopa.interop.authorization.service.utils.KeyPairGeneratorUtil;
import it.pagopa.interop.authorization.service.utils.voucher.DPopVoucherService;
import it.pagopa.interop.authorization.service.utils.voucher.domain.ClientAssertionOptions;
import it.pagopa.interop.authorization.service.utils.voucher.domain.ClientAssertionOptions.ClientType;
import it.pagopa.interop.authorization.service.utils.voucher.domain.VoucherRequest;
import it.pagopa.interop.authorization.service.utils.voucher.domain.VoucherResponse;
import it.pagopa.interop.common.client.AbstractClient;
import it.pagopa.interop.common.operation.SimpleOperation;
import it.pagopa.interop.generated.openapi.clients.bff.model.ClientSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceMode;
import it.pagopa.interop.generated.openapi.clients.bff.model.KeySeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.PurposeVersionState;
import it.pagopa.interop.purpose.domain.RiskAnalysis;
import it.pagopa.interop.purpose.domain.TEServiceMode;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;


import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@ToString
@EqualsAndHashCode
public class M2MDPopTokenService extends AbstractClient {

    @Setter private IdentityService identityService;
    private final DataPreparationService dataPreparationService;
    private final DPopVoucherService voucherService;
    private final DpopProofService dpopProofService;
    private final Map<TokenKey, String> tokenCache = new ConcurrentHashMap<>();

    @Value("${authorization.server.token.creation.url}")
    private String dpopHtu;

    public record PreparedClient(UUID clientId, KeyPairDecorator keyPair) {
    }

    public M2MDPopTokenService(
            IdentityService identityService,
            DataPreparationService dataPreparationService,
            DPopVoucherService voucherService,
            DpopProofService dpopProofService
    ) {
        this.identityService = identityService;
        this.dataPreparationService = dataPreparationService;
        this.voucherService = voucherService;
        this.dpopProofService = dpopProofService;
    }

    /**
     * Restituisce un token di accesso M2M utilizzando la protezione DPoP (Demonstration of Proof-of-Possession).
     * <p>
     * Questo metodo genera un client API fittizio con una coppia di chiavi (EC o RSA) e lo registra tramite i servizi
     * di preparazione dati. Se la chiave è di tipo EC (ellittica), viene anche costruita una proof DPoP conforme allo standard.
     * Viene quindi costruita una client assertion firmata e inviata una richiesta per ottenere il token,
     * eventualmente allegando la DPoP JWT nel caso sia disponibile.
     * <p>
     * Il token è memorizzato in cache per evitare rigenerazioni ripetute per la stessa coppia tenant/ruolo.
     *
     * @param client Il client che fa richiesta per l'access token.
     * @param tenantType Il tipo di tenant per cui ottenere il token.
     * @param purposeId L'id della finalità creata e attivata dal client.
     * @param keyType    Il tipo di chiave da utilizzare (es. "EC" o "RSA").
     * @return Il token di accesso ottenuto tramite il flusso DPoP.
     */
    public String getTokenWithDpop(@NonNull PreparedClient client, @NonNull String tenantType, @NonNull String purposeId, @NonNull String keyType) {
        TokenKey tokenKey = TokenKey.of(tenantType, M2MRole.M2M_ADMIN);

        return tokenCache.computeIfAbsent(tokenKey, key -> {
            log.info("Generating M2M DPoP token for tenantType: {}, client: {}, role: {}, keyType: {}", tenantType, client, M2MRole.M2M_ADMIN, keyType);

            ClientAssertionOptions options = ClientAssertionOptions.builder()
                    .clientType(ClientType.CONSUMER)
                    .clientId(client.clientId.toString())
                    .publicKey(client.keyPair.getPublic())
                    .privateKey(client.keyPair.getPrivate())
                    .purposeId(purposeId)
                    //.confirmationKeyThumbprint(jkt.toString())
                    .assertionTtlSeconds(300)
                    .build();

            String clientAssertion = voucherService.createClientAssertion(options);

            // 4. Genera coppia EC per la DPoP proof
            KeyPairDecorator dpopKeyPair;

            switch (keyType){
                case "EC" -> dpopKeyPair =  KeyPairDecorator.of("EC", 256);
                default -> throw new IllegalArgumentException("Invalid key type: " + keyType);
            }

            // 7. Costruisce DPoP proof con la chiave EC P-256
            String dpopJwt = dpopProofService.buildProof(
                    (ECPrivateKey) dpopKeyPair.getPrivate(),
                    (ECPublicKey) dpopKeyPair.getPublic(),
                    "POST",
                    dpopHtu
            );

            // DEBUG: verifica della firma
            dpopProofService.verifyDpopProof(dpopJwt);

            // 8. Effettua richiesta token con DPoP
            VoucherRequest request = VoucherRequest.builder()
                    .clientId(client.clientId.toString())
                    .clientAssertion(clientAssertion)
                    .build();

            return this.performOperation(SimpleOperation.of(
                    () -> voucherService.requestVoucher(request, dpopJwt),
                    response -> new ObjectMapper().convertValue(response, VoucherResponse.class).getAccessToken()
            )).orElse(null);

        });
    }

    /**
     * Genera un access token M2M simulando un DPoP proof malevolo.
     * <p>
     * Questo metodo viene utilizzato per testare il comportamento dell’Authorization Server quando riceve
     * un DPoP JWT in cui il JWK (chiave pubblica) è legittimo, ma la firma è effettuata con una chiave privata diversa.
     * È un caso di attacco in cui un attore malevolo tenta di impersonare un client legittimo.
     * <p>
     * Il server dovrebbe rifiutare la richiesta restituendo un errore.
     *
     * @param tenantType Tipo di tenant
     * @param role       Ruolo del client (M2M o M2M_ADMIN)
     * @return L'access token ottenuto (se accettato, ma ci si aspetta un errore).
     */
    /*
    public String getTokenWithForgedDpop(@NonNull String tenantType, @NonNull M2MRole role) {
        log.info("Generating forged DPoP token for tenantType: {}, role: {}", tenantType, role);

        // Chiave pubblica reale
        PreparedClient legitClient = prepareClient(tenantType, role, "EC", 256, "client-dpop-forged");

        // Chiave privata malevola
        KeyPairDecorator forgedKeyPair = KeyPairDecorator.of("EC", 256);

        String clientAssertion = buildClientAssertion(legitClient);
        String forgedProof = buildDpopProof(new PreparedClient(legitClient.clientId(), forgedKeyPair).keyPair());

        VoucherRequest request = buildVoucherRequest(legitClient.clientId(), clientAssertion);
        Map<String, Object> response = voucherService.requestVoucher(request, forgedProof);

        return new ObjectMapper().convertValue(response, VoucherResponse.class).getAccessToken();
    }
     */

    /**
     * Genera un access token M2M con header DPoP e restituisce l'intera risposta raw (Map JSON).
     * <p>
     * Usato nei test per verificare direttamente il contenuto di "token_type", "cnf.jkt", ecc.
     *
     * @param tenantType Tipo di tenant
     * @param role       Ruolo del client (es. M2M o M2M_ADMIN)
     * @return Mappa della risposta del token endpoint
     */
    @SneakyThrows
    public Map<String, Object> generateRawTokenResponse(@NonNull String tenantType, @NonNull M2MRole role) {
        log.info("Generating raw token response for tenantType: {}, role: {}", tenantType, role);

        // 1. Autenticazione admin
        String userToken = identityService.getToken(tenantType, role.name(), 0);
        dataPreparationService.setAuthToken(userToken);

        // 2. Crea client API
        String name = "client-dpop-" + ThreadLocalRandom.current().nextInt();
        ClientSeed seed = new ClientSeed();
        seed.setName(name);

        UUID clientId = dataPreparationService.createClient("API", seed);
        UUID userId = identityService.getUserId(tenantType, "admin");
        dataPreparationService.addMemberToClient(clientId, userId);

        if (role == M2MRole.M2M_ADMIN) {
            dataPreparationService.editClientAdmin(clientId, new ClientAdminConfig(userId));
        }

        // 3. Registra chiave RSA (obbligatoria per il backend)
        KeyPairDecorator rsaKeyPair = KeyPairDecorator.of("RSA", 2048);
        KeySeed rsaKeySeed = KeyPairGeneratorUtil.createKeySeed(rsaKeyPair.getDelimitedPublicKeyBase64()).get(0);
        dataPreparationService.addPublicKeyToClient(clientId, rsaKeySeed);

        // Crea una finalità qualsiasi in stato active
        /*
        UUID consumerId = identityService.getOrganizationId(tenantType);
        RiskAnalysis riskAnalysis = dataPreparationService.getRiskAnalysis(tenantType, true);
        dataPreparationService.createPurposeWithGivenState(ThreadLocalRandom.current().nextInt(0, Integer.MAX_VALUE),
                EServiceMode.DELIVER, PurposeVersionState.fromValue(purposeVersionState),
                TEServiceMode.builder()
                        .eserviceId(eserviceId)
                        .consumerId(consumerId)
                        .riskAnalysisFormSeed(riskAnalysis.getRiskAnalysisForm())
                        .build());

         */

        // 4. Genera coppia EC per la DPoP proof
        KeyPairDecorator dpopKeyPair = KeyPairDecorator.of("EC", 256);

        // 5. Calcola il thumbprint JWK (jkt) della chiave DPoP pubblica
        ECKey ecJwk = new ECKey.Builder(Curve.P_256, (ECPublicKey) dpopKeyPair.getPublic()).build();
        Base64URL jkt = ecJwk.computeThumbprint();


        // 6. Costruisce client assertion includendo il jkt della chiave DPoP
        ClientAssertionOptions options = ClientAssertionOptions.builder()
                .clientType(ClientType.API)
                .clientId(clientId.toString())
                .publicKey(rsaKeyPair.getPublic())
                .privateKey(rsaKeyPair.getPrivate())
                .purposeId(null)
                //.confirmationKeyThumbprint(jkt.toString())
                .assertionTtlSeconds(300)
                .build();
        String clientAssertion = voucherService.createClientAssertion(options);

        // 7. Costruisce DPoP proof con la chiave EC P-256
        String dpopJwt = dpopProofService.buildProof(
                (ECPrivateKey) dpopKeyPair.getPrivate(),
                (ECPublicKey) dpopKeyPair.getPublic(),
                "POST",
                dpopHtu
        );

        // DEBUG: verifica della firma
        dpopProofService.verifyDpopProof(dpopJwt);

        // 8. Effettua richiesta token con DPoP
        VoucherRequest request = VoucherRequest.builder()
                .clientId(clientId.toString())
                .clientAssertion(clientAssertion)
                .build();

        return voucherService.requestVoucher(request, dpopJwt);
    }

    public PreparedClient prepareClient(@NonNull String tenantType, @NonNull M2MRole role) {
        // 1. Autenticazione admin
        String userToken = identityService.getToken(tenantType, role.name(), 0);
        dataPreparationService.setAuthToken(userToken);

        // 2. Crea client API
        String name = "client-dpop-" + ThreadLocalRandom.current().nextInt();
        ClientSeed seed = new ClientSeed();
        seed.setName(name);

        UUID clientId = dataPreparationService.createClient("API", seed);
        UUID userId = identityService.getUserId(tenantType, "admin");
        dataPreparationService.addMemberToClient(clientId, userId);

        if (role == M2MRole.M2M_ADMIN) {
            dataPreparationService.editClientAdmin(clientId, new ClientAdminConfig(userId));
        }

        // 3. Registra chiave RSA (obbligatoria per il backend)
        KeyPairDecorator rsaKeyPair = KeyPairDecorator.of("RSA", 2048);
        KeySeed rsaKeySeed = KeyPairGeneratorUtil.createKeySeed(rsaKeyPair.getDelimitedPublicKeyBase64()).get(0);
        dataPreparationService.addPublicKeyToClient(clientId, rsaKeySeed);

        // 4. Registra un purpose
        return new PreparedClient(clientId, rsaKeyPair);
    }

    private String buildClientAssertion(PreparedClient client) {
        ClientAssertionOptions options = ClientAssertionOptions.builder()
                .clientType(ClientType.API)
                .clientId(client.clientId().toString())
                .publicKey(client.keyPair().getPublic())
                .privateKey(client.keyPair().getPrivate())
                .build();
        return voucherService.createClientAssertion(options);
    }

    private VoucherRequest buildVoucherRequest(UUID clientId, String assertion) {
        return VoucherRequest.builder()
                .clientId(clientId.toString())
                .clientAssertion(assertion)
                .build();
    }

    private String buildDpopProof(KeyPairDecorator keyPair) {
        if (!(keyPair.getPrivate() instanceof ECPrivateKey priv) || !(keyPair.getPublic() instanceof ECPublicKey pub)) {
            throw new IllegalStateException("Chiavi non compatibili: attese EC P-256 per DPoP");
        }
        return new DpopProofService().buildProof(priv, pub, "POST", dpopHtu);
    }
}
