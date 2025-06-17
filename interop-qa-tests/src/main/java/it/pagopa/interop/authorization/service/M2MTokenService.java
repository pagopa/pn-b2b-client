package it.pagopa.interop.authorization.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.jwk.KeyType;
import it.pagopa.interop.authorization.domain.KeyPairDecorator;
import it.pagopa.interop.authorization.enums.M2MRole;
import it.pagopa.interop.authorization.enums.TokenKey;
import it.pagopa.interop.authorization.service.identity.IdentityService;
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

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

import static it.pagopa.interop.authorization.domain.KeyPairDecorator.of;
import static it.pagopa.interop.authorization.enums.TokenKey.of;

/* Usato per la formulazione di token di tipo m2m. Introdotto durante lo sviluppo dei test di
 * SRS API v2 https://pagopa.atlassian.net/wiki/spaces/PDNDI/pages/1607860403/DRAFT+SRS+API+V2 .
 * IDEATO PER UTILIZZO SOLO IN TEST AUTOMATICI, non come servizio di auth di utilità generale. Si
 * noti infatti che nella procedura di recupero del token vengono prese in carico tutte le ops.
 * correlate affinché la procedura vada "per forza" a buon fine (creazione del client, eventuale
 * set di adminId...).  */
@Slf4j
@ToString
@EqualsAndHashCode
public class M2MTokenService {

    private final IdentityService identityService;
    private final DataPreparationService dataPreparationService;
    private final VoucherService voucherService;
    private final Map<TokenKey, String> tokenCache = new ConcurrentHashMap<>();
    private final Map<UUID, M2MDPopTokenService.PreparedClient> preparedClientCache = new ConcurrentHashMap<>();

    public M2MTokenService(
            IdentityService identityService,
            DataPreparationService dataPreparationService,
            VoucherService voucherService
    ) {
        this.identityService = identityService;
        this.dataPreparationService = dataPreparationService;
        this.voucherService = voucherService;
    }


    public String getToken(@NonNull String tenantType, @NonNull M2MRole role) {
        return this.getToken(tenantType, role, 0);
    }

    /* DEV. NOTE 28/05/2025: è di fatto una compattazione delle procedure utilizzate nei test
     * - @voucher_generation_m2m1
     * - @voucher_generation_m2m1_admin
     * al netto dei controlli di verifica. */
    public String getToken(@NonNull String tenantType, @NonNull M2MRole role, int roleIndex) {
        TokenKey tokenKey = of(tenantType, role);
        if (!tokenCache.containsKey(tokenKey)) {
            log.info("Generating M2M token for tenantType: {}, role: {}", tenantType, role);

            String token = identityService.getToken(tenantType, "admin", roleIndex);
            dataPreparationService.setAuthToken(token);

            ClientSeed clientSeed = new ClientSeed();
            clientSeed.setName(String.format("client-%d-%d-%s", 0, 0, ThreadLocalRandom.current().nextInt(0, Integer.MAX_VALUE)));
            UUID clientId = dataPreparationService.createClient("API", clientSeed);

            UUID userId = identityService.getUserId(tenantType, "admin");
            dataPreparationService.addMemberToClient(clientId, userId);

            if (role == M2MRole.M2M_ADMIN) {
                dataPreparationService.editClientAdmin(clientId, new ClientAdminConfig(userId));
            }

            String keyType = "RSA";
            KeyPairDecorator keyPair = of(keyType, 2048);
            String encodedPublicKey = keyPair.getDelimitedPublicKeyBase64();
            KeySeed keySeed = KeyPairGeneratorUtil.createKeySeed(encodedPublicKey).get(0);
            dataPreparationService.addPublicKeyToClient(clientId, keySeed);

            ClientAssertionOptions assertionOptions = ClientAssertionOptions.builder()
                    .clientType(ClientType.API)
                    .clientId(clientId.toString())
                    .publicKey(keyPair.getPublic())
                    .privateKey(keyPair.getPrivate())
                    .build();
            String clientAssertion = this.voucherService.createClientAssertion(assertionOptions);

            VoucherRequest voucherRequest = VoucherRequest.builder()
                    .clientId(clientId.toString())
                    .clientAssertion(clientAssertion)
                    .build();
            Map<String, Object> voucher = voucherService.requestVoucher(voucherRequest);

            VoucherResponse voucherResponse = new ObjectMapper()
                    .convertValue(voucher, VoucherResponse.class);

            this.preparedClientCache.put(clientId, new M2MDPopTokenService.PreparedClient(clientId, keyPair, KeyType.parse(keyType)));
            this.tokenCache.put(tokenKey, voucherResponse.getAccessToken());
        }

        return this.tokenCache.get(tokenKey);
    }

    public M2MDPopTokenService.PreparedClient getPreparedClient(@NonNull UUID clientId) {
        return preparedClientCache.get(clientId);
    }
}