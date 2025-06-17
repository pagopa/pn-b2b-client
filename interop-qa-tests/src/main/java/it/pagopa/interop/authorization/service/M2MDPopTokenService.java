package it.pagopa.interop.authorization.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.jwk.KeyType;
import it.pagopa.interop.authorization.domain.KeyPairDecorator;
import it.pagopa.interop.authorization.enums.M2MRole;
import it.pagopa.interop.authorization.enums.TokenKey;
import it.pagopa.interop.authorization.service.identity.IdentityService;
import it.pagopa.interop.authorization.service.utils.DpopProofService;
import it.pagopa.interop.authorization.service.utils.voucher.DPopVoucherService;
import it.pagopa.interop.authorization.service.utils.voucher.domain.ClientAssertionOptions;
import it.pagopa.interop.authorization.service.utils.voucher.domain.ClientAssertionOptions.ClientType;
import it.pagopa.interop.authorization.service.utils.voucher.domain.VoucherRequest;
import it.pagopa.interop.authorization.service.utils.voucher.domain.VoucherResponse;
import it.pagopa.interop.common.client.AbstractClient;
import it.pagopa.interop.common.operation.SimpleOperation;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@ToString
@EqualsAndHashCode
public class M2MDPopTokenService extends AbstractClient {

    @Setter
    private IdentityService identityService;
    private final DataPreparationService dataPreparationService;
    private final DPopVoucherService voucherService;
    private final DpopProofService dpopProofService;
    private final Map<TokenKey, VoucherResponse> tokenCache = new ConcurrentHashMap<>();

    @Value("${authorization.server.token.creation.url}")
    private String dpopHtu;

    public record PreparedClient(UUID clientId, KeyPairDecorator keyPair, KeyType keyType) {
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
     * @param client     Il client che fa richiesta per l'access token.
     * @param tenantType Il tipo di tenant per cui ottenere il token.
     * @param purposeId  L'id della finalità creata e attivata dal client.
     * @param keyType    Il tipo di chiave da utilizzare (es. "EC" o "RSA").
     * @return Il token di accesso ottenuto tramite il flusso DPoP.
     */
    public VoucherResponse getTokenWithDpop(@NonNull PreparedClient client, @NonNull String tenantType, @NonNull String purposeId, @NonNull String keyType) {
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

            switch (keyType) {
                case "EC" -> dpopKeyPair = KeyPairDecorator.of("EC", 256);
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
                    response -> new ObjectMapper().convertValue(response, VoucherResponse.class)
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

}
