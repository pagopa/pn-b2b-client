package it.pagopa.interop.authorization.service;

import static it.pagopa.interop.authorization.domain.KeyPairDecorator.of;
import static it.pagopa.interop.authorization.service.M2MTokenService.TokenKey.of;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.pagopa.interop.authorization.domain.KeyPairDecorator;
import it.pagopa.interop.authorization.service.utils.IdentityService;
import it.pagopa.interop.authorization.service.utils.KeyPairGeneratorUtil;
import it.pagopa.interop.authorization.service.utils.voucher.VoucherService;
import it.pagopa.interop.authorization.service.utils.voucher.domain.ClientAssertionOptions;
import it.pagopa.interop.authorization.service.utils.voucher.domain.ClientAssertionOptions.ClientType;
import it.pagopa.interop.authorization.service.utils.voucher.domain.VoucherRequest;
import it.pagopa.interop.authorization.service.utils.voucher.domain.VoucherResponse;
import it.pagopa.interop.generated.openapi.clients.bff.model.ClientSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.KeySeed;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/* Usato per la formulazione di token di tipo m2m. Introdotto durante lo sviluppo dei test di
 * SRS API v2 https://pagopa.atlassian.net/wiki/spaces/PDNDI/pages/1607860403/DRAFT+SRS+API+V2 */
@Slf4j
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@Service
public class M2MTokenService {
    public enum M2MRole {
        M2M_ADMIN, M2M;

        public M2MRole fromValue(String value) {
            for (M2MRole role : M2MRole.values()) {
                if (role.name().equals(value)) {
                    return role;
                }
            }
            throw new IllegalArgumentException("Unsupported value '" + value + "'");
        }
    }

    record TokenKey(String tenantType, M2MRole role) {
        public static TokenKey of(String tenantType, M2MRole role) {
            return new TokenKey(tenantType, role);
        }
    }

    private final IdentityService identityService;
    private final DataPreparationService dataPreparationService;
    private final VoucherService voucherService;

    private final Map<TokenKey, String> tokenCache = new ConcurrentHashMap<>();

    /* DEV. NOTE 28/05/2025: è di fatto una compattazione delle procedure utilizzate nei test
    * - @voucher_generation_m2m1
    * - @voucher_generation_m2m1_admin
    * al netto dei controlli di verifica. */
    public String getToken(@NonNull String tenantType, @NonNull M2MRole role) {
        if(!tokenCache.containsKey(of(tenantType, role))) {
            log.info("Generating M2M token for tenantType: {}, role: {}", tenantType, role);

            String token = identityService.getToken(tenantType, "admin");
            dataPreparationService.setAuthToken(token);

            ClientSeed clientSeed = new ClientSeed();
            clientSeed.setName(String.format("client-%d-%d-%s", 0, 0, ThreadLocalRandom.current().nextInt(0, Integer.MAX_VALUE)));
            UUID clientId = dataPreparationService.createClient("API", clientSeed);

            UUID userId = identityService.getUserId(tenantType, "admin");
            dataPreparationService.addMemberToClient(clientId, userId);

            if(role == M2MRole.M2M_ADMIN) {
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

            this.tokenCache.put(of(tenantType, role), voucherResponse.getAccessToken());
        }

        return this.tokenCache.get(of(tenantType, role));
    }
}