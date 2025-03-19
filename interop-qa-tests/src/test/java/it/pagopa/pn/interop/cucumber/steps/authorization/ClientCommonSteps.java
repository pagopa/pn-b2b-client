package it.pagopa.pn.interop.cucumber.steps.authorization;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import it.pagopa.interop.authorization.service.utils.IdentityService;
import it.pagopa.interop.authorization.service.utils.KeyPairGeneratorUtil;
import it.pagopa.interop.generated.openapi.clients.bff.model.ClientSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.CompactClients;
import it.pagopa.interop.generated.openapi.clients.bff.model.PurposeAdditionDetailsSeed;
import it.pagopa.interop.authorization.service.utils.KeyPairGeneratorUtil;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.DataPreparationService;
import it.pagopa.interop.utils.HttpCallExecutor;
import it.pagopa.pn.interop.cucumber.steps.DataPreparationService;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.delegate.DelegationRole;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;
import lombok.Getter;
import lombok.Setter;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;

@Getter
@Setter
@Slf4j
public class ClientCommonSteps {
    private final ClientTokenConfigurator clientTokenConfigurator;
    private final DataPreparationService dataPreparationService;
    private final IdentityService identityService;
    private final HttpCallExecutor httpCallExecutor;
    private final SharedStepsContext sharedStepsContext;

    private PurposeAdditionDetailsSeed purposeAdditionDetailsSeed;

    @Autowired
    public ClientCommonSteps(ClientTokenConfigurator clientTokenConfigurator,
                             DataPreparationService dataPreparationService,
                             SharedStepsContext sharedStepsContext) {
        this.clientTokenConfigurator = clientTokenConfigurator;
        this.dataPreparationService = dataPreparationService;
        this.identityService = sharedStepsContext.getIdentityService();
        this.httpCallExecutor = sharedStepsContext.getHttpCallExecutor();
        this.sharedStepsContext = sharedStepsContext;
    }

    @Given("il {delegationRole} ha già creato {int} client {string}")
    public void createClientsForTenants(DelegationRole delegationRole, int numClient, String clientKind) {
        String tenantType = sharedStepsContext.getDelegationCommonContext().getTenantBy(delegationRole);
        createClientsForTenants(tenantType, numClient, clientKind);
    }

    @Given("{string} ha già creato {int} client {string}")
    public void createClientsForTenants(String tenantType, int numClient, String clientKind) {
        clientTokenConfigurator.setBearerToken(identityService.getToken(tenantType, null));

        List<UUID> clientIds = IntStream.range(0, numClient)
                .mapToObj(i -> dataPreparationService.createClient(clientKind, createClientSeed(i)))
                .toList();
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
        Assertions.assertEquals(statusCode, httpCallExecutor.getClientResponse().value());
        Assertions.assertEquals(count, ((CompactClients) httpCallExecutor.getResponse()).getResults().size());
    }

    @Given("un {string} di {string} ha caricato una chiave pubblica in quel client")
    public void roleOfTenantHasAlreadyUploadClientPublicKey(String role, String tenantType) {
        clientTokenConfigurator.setBearerToken(identityService.getToken(tenantType, role));
        String userPublicKey = KeyPairGeneratorUtil.createBase64PublicKey("RSA", 2048);
        sharedStepsContext.getClientCommonContext().setClientPublicKey(userPublicKey);
        String keyId = dataPreparationService.addPublicKeyToClient(sharedStepsContext.getClientCommonContext().getFirstClient(), KeyPairGeneratorUtil.createKeySeed(
            userPublicKey).get(0));
        sharedStepsContext.getClientCommonContext().setKeyId(keyId);
    }

    @Then("si ottiene status code {int}")
    public void verifyStatusCode(int statusCode) {
        if (List.of(200, 204).contains(statusCode)) Assertions.assertEquals(200, httpCallExecutor.getClientResponse().value());
        else Assertions.assertEquals(statusCode, httpCallExecutor.getClientResponse().value());
    }

    private ClientSeed createClientSeed(int index) {
        ClientSeed clientSeed = new ClientSeed();
        clientSeed.setName(String.format("client-%d-%d-%s", index, sharedStepsContext.getTestSeed(), ThreadLocalRandom.current().nextInt(0, Integer.MAX_VALUE)));
        return clientSeed;
    }


}
