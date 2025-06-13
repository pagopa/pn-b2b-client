package it.pagopa.interop.authorization.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.pagopa.interop.authorization.domain.KeyPairDecorator;
import it.pagopa.interop.authorization.enums.M2MRole;
import it.pagopa.interop.authorization.service.identity.IdentityService;
import it.pagopa.interop.authorization.service.identity.IllegalM2MRole;
import it.pagopa.interop.authorization.service.utils.KeyPairGeneratorUtil;
import it.pagopa.interop.authorization.service.utils.voucher.VoucherService;
import it.pagopa.interop.authorization.service.utils.voucher.domain.ClientAssertionOptions;
import it.pagopa.interop.authorization.service.utils.voucher.domain.ClientAssertionOptions.ClientType;
import it.pagopa.interop.authorization.service.utils.voucher.domain.VoucherRequest;
import it.pagopa.interop.authorization.service.utils.voucher.domain.VoucherResponse;
import it.pagopa.interop.generated.openapi.clients.bff.model.ClientSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.KeySeed;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

import static it.pagopa.interop.authorization.domain.KeyPairDecorator.of;
import static java.util.Objects.nonNull;

/* Usato per la formulazione di token di tipo m2m. Introdotto durante lo sviluppo dei test di
 * SRS API v2 https://pagopa.atlassian.net/wiki/spaces/PDNDI/pages/1607860403/DRAFT+SRS+API+V2 .
 * IDEATO PER UTILIZZO SOLO IN TEST AUTOMATICI, non come servizio di auth di utilità generale. Si
 * noti infatti che nella procedura di recupero del token vengono prese in carico tutte le ops.
 * correlate affinché la procedura vada "per forza" a buon fine (creazione del client, eventuale
 * set di adminId...).  */
@Slf4j
@ToString
@EqualsAndHashCode
public class M2MDPopTokenService {

    record TokenKey(String tenantType, M2MRole role) {
        public static TokenKey of(String tenantType, M2MRole role) {
            return new TokenKey(tenantType, role);
        }
    }

    private final IdentityService identityService;
    private final DataPreparationService dataPreparationService;
    private final VoucherService voucherService;

    private record PreparedClient(UUID clientId, KeyPairDecorator keyPair) {
    }


    public M2MDPopTokenService(
        IdentityService identityService,
        DataPreparationService dataPreparationService,
        VoucherService voucherService
    ) {
        this.identityService = identityService;
        this.dataPreparationService = dataPreparationService;
        this.voucherService = voucherService;
    }

    @Value("${dpop.proof.htu}")
    private String dpopHtu;

    private final Map<TokenKey, String> tokenCache = new ConcurrentHashMap<>();

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
     * @param tenantType Il tipo di tenant per cui ottenere il token.
     * @param role       Il ruolo del client (ad esempio {@code M2M} o {@code M2M_ADMIN}).
     * @param keyType    Il tipo di chiave da utilizzare (es. "EC" o "RSA").
     * @param keySize    La dimensione della chiave da generare (es. 256 per EC, 2048 per RSA).
     * @return Il token di accesso ottenuto tramite il flusso DPoP.
     */
    public String getTokenWithDpop(@NonNull String tenantType, @NonNull M2MRole role, @NonNull String keyType, int keySize) {
        TokenKey tokenKey = TokenKey.of(tenantType, role);

        if (!tokenCache.containsKey(tokenKey)) {
            log.info("Generating M2M DPoP token for tenantType: {}, role: {}, keyType: {}, keySize: {}", tenantType, role, keyType, keySize);

            PreparedClient client = prepareClient(tenantType, role, keyType, keySize, "client-dpop");

            String clientAssertion = buildClientAssertion(client.clientId(), client.keyPair());

            String dpopJwt = null;
            if ("EC".equalsIgnoreCase(keyType)) {
                dpopJwt = buildDpopProof(client.keyPair(), dpopHtu, "POST");
            }

            VoucherRequest voucherRequest = VoucherRequest.builder()
                    .clientId(client.clientId().toString())
                    .clientAssertion(clientAssertion)
                    .build();

            Map<String, Object> voucherResponseMap = (dpopJwt != null)
                    ? voucherService.requestVoucher(voucherRequest, dpopJwt)
                    : voucherService.requestVoucher(voucherRequest);

            VoucherResponse voucherResponse = new ObjectMapper().convertValue(voucherResponseMap, VoucherResponse.class);
            tokenCache.put(tokenKey, voucherResponse.getAccessToken());
        }

        return tokenCache.get(tokenKey);
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
    public String getTokenWithForgedDpop(@NonNull String tenantType, @NonNull M2MRole role) {
        log.info("Generating forged DPoP token for tenantType: {}, role: {}", tenantType, role);

        // Chiave pubblica reale
        PreparedClient legitClient = prepareClient(tenantType, role, "EC", 256, "client-dpop-forged");

        // Chiave privata malevola
        KeyPairDecorator forgedKeyPair = KeyPairDecorator.of("EC", 256);

        String clientAssertion = buildClientAssertion(legitClient.clientId(), legitClient.keyPair());

        String forgedProof = buildDpopProof(new PreparedClient(legitClient.clientId(), forgedKeyPair).keyPair(), dpopHtu, "POST");

        VoucherRequest voucherRequest = VoucherRequest.builder()
                .clientId(legitClient.clientId().toString())
                .clientAssertion(clientAssertion)
                .build();

        Map<String, Object> voucherResponse = voucherService.requestVoucher(voucherRequest, forgedProof);
        return new ObjectMapper().convertValue(voucherResponse, VoucherResponse.class).getAccessToken();
    }

    private PreparedClient prepareClient(
            @NonNull String tenantType,
            @NonNull M2MRole role,
            @NonNull String keyType,
            int keySize,
            @NonNull String clientNamePrefix
    ) {
        // 1. Recupero e set del token utente
        String userToken = identityService.getToken(tenantType, "admin", 0);
        dataPreparationService.setAuthToken(userToken);

        // 2. Crea client API
        String clientName = String.format("%s-%s", clientNamePrefix, ThreadLocalRandom.current().nextInt());
        ClientSeed clientSeed = new ClientSeed();
        clientSeed.setName(clientName);
        UUID clientId = dataPreparationService.createClient("API", clientSeed);

        // 3. Aggiunge membro + eventuale admin
        UUID userId = identityService.getUserId(tenantType, "admin");
        dataPreparationService.addMemberToClient(clientId, userId);
        if (role == M2MRole.M2M_ADMIN) {
            dataPreparationService.editClientAdmin(clientId, new ClientAdminConfig(userId));
        }

        // 4. Generazione chiavi
        KeyPairDecorator keyPair = KeyPairDecorator.of(keyType, keySize);
        String encodedPublicKey = keyPair.getDelimitedPublicKeyBase64();
        KeySeed keySeed = KeyPairGeneratorUtil.createKeySeed(encodedPublicKey).get(0);
        dataPreparationService.addPublicKeyToClient(clientId, keySeed);

        return new PreparedClient(clientId, keyPair);
    }

    private String buildClientAssertion(UUID clientId, KeyPairDecorator keyPair) {
        ClientAssertionOptions options = ClientAssertionOptions.builder()
                .clientType(ClientType.API)
                .clientId(clientId.toString())
                .publicKey(keyPair.getPublic())
                .privateKey(keyPair.getPrivate())
                .build();
        return voucherService.createClientAssertion(options);
    }

    private String buildDpopProof(KeyPairDecorator keyPair, String htu, String method) {
        if (!(keyPair.getPrivate() instanceof ECPrivateKey privateKey) ||
                !(keyPair.getPublic() instanceof ECPublicKey publicKey)) {
            throw new IllegalStateException("Chiavi non compatibili: attese EC P-256 per DPoP");
        }

        return new DpopProofService().buildProof(privateKey, publicKey, method, htu);
    }
}

