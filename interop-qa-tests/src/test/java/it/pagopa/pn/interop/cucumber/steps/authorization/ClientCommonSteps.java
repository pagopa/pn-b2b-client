package it.pagopa.pn.interop.cucumber.steps.authorization;

import com.nimbusds.jose.jwk.KeyType;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import it.pagopa.interop.authorization.service.identity.IdentityService;
import it.pagopa.interop.authorization.service.utils.KeyPairGeneratorUtil;
import it.pagopa.interop.common.IHttpExecutor;
import it.pagopa.interop.conf.api_profile.ApiProfile;
import it.pagopa.interop.generated.openapi.clients.bff.model.ClientSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.CompactClients;
import it.pagopa.interop.generated.openapi.clients.bff.model.PurposeAdditionDetailsSeed;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.ParameterTypes;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.datapreparationservice.BFFDataPreparationService;
import it.pagopa.pn.interop.cucumber.steps.delegate.DelegationRole;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;

@Getter
@Setter
@Slf4j
public class ClientCommonSteps {
    private final ClientTokenConfigurator clientTokenConfigurator;
    private final BFFDataPreparationService dataPreparationService;
    private final IdentityService identityService;
    private final IHttpExecutor httpCallExecutor;
    private final SharedStepsContext sharedStepsContext;

    private final ApiProfile apiProfile;

    private PurposeAdditionDetailsSeed purposeAdditionDetailsSeed;

    @Autowired
    public ClientCommonSteps(ClientTokenConfigurator clientTokenConfigurator,
                             BFFDataPreparationService dataPreparationService,
                             SharedStepsContext sharedStepsContext,
                             ApiProfile apiProfile) {
        this.clientTokenConfigurator = clientTokenConfigurator;
        this.dataPreparationService = dataPreparationService;
        this.identityService = sharedStepsContext.getIdentityService();
        this.httpCallExecutor = sharedStepsContext.getHttpCallExecutor();
        this.sharedStepsContext = sharedStepsContext;
        this.apiProfile = apiProfile;
    }

    @Given("il {delegationRole} ha già creato {int} client {string}")
    public void createClientsForTenants(DelegationRole delegationRole, int numClient, String clientKind) {
        String tenantType = sharedStepsContext.getDelegationCommonContext().getTenantBy(delegationRole);
        createClientsForTenants(tenantType, numClient, clientKind);
    }

    @Given("{string} ha già creato {int} client {string}")
    public void createClientsForTenants(String tenantType, int numClient, String clientKind) {
        clientTokenConfigurator.setBearerToken(identityService.getToken(tenantType, null));

        @SuppressWarnings("java:S6204") // si evita volutamente il metodo diretto toList() perché produrrebbe una lista immutabile
        List<UUID> clientIds = IntStream.range(0, numClient)
                .mapToObj(i -> dataPreparationService.createClient(clientKind, createClientSeed(i)))
                .collect(toList());
        sharedStepsContext.getClientCommonContext().setClients(clientIds);
    }

    @Given("{string} ha già inserito l'utente con ruolo {string} come membro di quel client")
    public void tenantHasAlreadyAddUsersWithRole(String tenantType, String roleOfMemberToAdd) {
        clientTokenConfigurator.setBearerToken(identityService.getToken(tenantType, null));
        UUID clientMemberUserId = identityService.getUserId(tenantType, roleOfMemberToAdd);
        dataPreparationService.addMemberToClient(sharedStepsContext.getClientCommonContext().getFirstClient(), clientMemberUserId);
        sharedStepsContext.getClientCommonContext().setUsers(List.of(clientMemberUserId));
    }

    @Then("si ottiene status code {int} e la lista di {int} client(s)")
    public void verifyStatusCodeAndClientList(int statusCode, int count) {
        Assertions.assertEquals(statusCode, httpCallExecutor.getResponseStatus().value());
        Assertions.assertEquals(count, ((CompactClients) httpCallExecutor.getResponse()).getResults().size());
    }

    @Given("un {string} di {string} ha caricato una chiave pubblica in quel client")
    public void roleOfTenantHasAlreadyUploadClientPublicKey(String role, String tenantType) {
        clientTokenConfigurator.setBearerToken(identityService.getToken(tenantType, role));
        String keyType = "RSA";
        String userPublicKey = KeyPairGeneratorUtil.createBase64PublicKey(keyType, 2048);
        sharedStepsContext.getClientCommonContext().setClientPublicKey(userPublicKey);
        sharedStepsContext.getClientCommonContext().setKeyType(keyType);
        String keyId = dataPreparationService.addPublicKeyToClient(sharedStepsContext.getClientCommonContext().getFirstClient(), KeyPairGeneratorUtil.createKeySeed(
                userPublicKey, KeyType.parse(keyType)).get(0));
        sharedStepsContext.getClientCommonContext().setKeyId(keyId);
    }

    @Then("si ottiene status code {int}")
    public void verifyStatusCode(int statusCode) {
        if (List.of(200, 204).contains(statusCode))
            Assertions.assertTrue(httpCallExecutor.getResponseStatus().is2xxSuccessful());
        else Assertions.assertEquals(statusCode, httpCallExecutor.getResponseStatus().value());
    }

    /* 26 02 2026 introdotto per risolvere la problematica 204 -> 200 m2m v3
     * https://pagopaspa.slack.com/archives/C09UKEZ2BSS/p1772121592444099?thread_ts=1772121586.566669&cid=C09UKEZ2BSS
     * Altre idee sono state scartate, per il momento lo si sta mantenendo uguale allo step soprastante
     * per mancanze di idee migliori, nell'ottica che lo si potrà migliorare in un secondo momento. */
    @Then("si ottiene http status code {int}")
    public void verifyHttpStatusCode(int statusCode) {
        if (List.of(200, 204).contains(statusCode))
            Assertions.assertEquals(200, httpCallExecutor.getResponseStatus().value());
        else Assertions.assertEquals(statusCode, httpCallExecutor.getResponseStatus().value());
    }

    /* 26 02 2026 idea scartata per risolvere la problematica m2m v3 204 -> 200
     * https://pagopaspa.slack.com/archives/C09UKEZ2BSS/p1772121592444099?thread_ts=1772121586.566669&cid=C09UKEZ2BSS */
    @Then("si ottengono i seguenti response status codes: {apiStatuses}")
    public void verifyStatusCodes(Map<ParameterTypes.ApiSpec, Integer> statusCodeMap) {
        if (apiProfile.getApiMode().equals(ApiProfile.ApiMode.BEST_FIT)) {
            log.warn("Attenzione: essendo attivata la modalità {} non è garantito che l'ultima call effettuata appartenga al set indicato in configurazione", apiProfile.getApiMode());
        }

        ParameterTypes.ApiSpec apiSpec = toApiSpec(apiProfile);
        accuratelyVerifyStatusCode(statusCodeMap.get(apiSpec));
    }

    public ParameterTypes.ApiSpec toApiSpec(ApiProfile apiProfile) {
        ParameterTypes.ApiVersion version = apiProfile.getApiSet().equals(ApiProfile.ApiSet.BFF)
                ? ParameterTypes.ApiVersion.valueOf(apiProfile.getApiBFFVersion().toString())
                : ParameterTypes.ApiVersion.valueOf(apiProfile.getApiM2MVersion().toString());
        return new ParameterTypes.ApiSpec(apiProfile.getApiSet(), version);
    }

    /* DEV. NOTE 12/03/2025: si differenzia da verifyStatusCode(int statusCode) per la verifica
     * accurata dello status anche in caso di esito positivo, bypassando quindi la normalizzazione
     * su codice 200. Questo è reso possibile dalla recente aggiunta del metodo
     * it.pagopa.interop.utils.HttpCallExecutor.performCall(java.util.function.Supplier<T>, java.util.function.Function<T,org.springframework.http.HttpStatus>)
     * che dà modo di conservare lo status code originale. */
    @Then("si ottiene response status code {int}")
    public void accuratelyVerifyStatusCode(int statusCode) {
        assertThat(httpCallExecutor.getResponseStatus().value())
                .as("Check HTTP response status risultante da ultima call effettuata attraverso %s", httpCallExecutor.getClass().getSimpleName())
                .isEqualTo(statusCode);
    }

    private ClientSeed createClientSeed(int index) {
        ClientSeed clientSeed = new ClientSeed();
        clientSeed.setName(String.format("client-%d-%d-%s", index, sharedStepsContext.getTestSeed(), ThreadLocalRandom.current().nextInt(0, Integer.MAX_VALUE)));
        return clientSeed;
    }
}