package it.pagopa.pn.interop.cucumber.steps.authorization;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import it.pagopa.interop.generated.openapi.clients.bff.model.ClientSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.CompactClients;
import it.pagopa.interop.generated.openapi.clients.bff.model.KeyUse;
import it.pagopa.interop.generated.openapi.clients.bff.model.PurposeAdditionDetailsSeed;
import it.pagopa.interop.authorization.service.utils.CommonUtils;
import it.pagopa.interop.authorization.service.utils.KeyPairGeneratorUtil;
import it.pagopa.pn.interop.cucumber.steps.DataPreparationService;
import it.pagopa.interop.utils.HttpCallExecutor;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import lombok.Getter;
import lombok.Setter;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Getter
@Setter
public class ClientCommonSteps {
    private final DataPreparationService dataPreparationService;
    private final CommonUtils commonUtils;
    private final HttpCallExecutor httpCallExecutor;
    private final SharedStepsContext sharedStepsContext;

    private PurposeAdditionDetailsSeed purposeAdditionDetailsSeed;

    @Autowired
    public ClientCommonSteps(DataPreparationService dataPreparationService,
                             CommonUtils commonUtils,
                             HttpCallExecutor httpCallExecutor,
                             SharedStepsContext sharedStepsContext) {
        this.dataPreparationService = dataPreparationService;
        this.commonUtils = commonUtils;
        this.httpCallExecutor = httpCallExecutor;
        this.sharedStepsContext = sharedStepsContext;
    }

    @Given("{string} ha già creato {int} client {string}")
    public void createClientsForTenants(String tenantType, int numClient, String clientKind) {
        commonUtils.setBearerToken(commonUtils.getToken(tenantType, null));

        List<UUID> clientIds = IntStream.range(0, numClient)
                .mapToObj(i -> dataPreparationService.createClient(clientKind, createClientSeed(i)))
                .collect(Collectors.toList());
        sharedStepsContext.getClientCommonContext().setClients(clientIds);
    }

    @Given("{string} ha già inserito l'utente con ruolo {string} come membro di quel client")
    public void tenantHasAlreadyAddUsersWithRole(String tenantType, String roleOfMemberToAdd) {
        commonUtils.setBearerToken(commonUtils.getToken(tenantType, null));
        UUID clientMemberUserId = commonUtils.getUserId(tenantType, roleOfMemberToAdd);
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
        commonUtils.setBearerToken(commonUtils.getToken(tenantType, role));
        String userPublicKey = KeyPairGeneratorUtil.createBase64PublicKey("RSA", 2048);
        sharedStepsContext.getClientCommonContext().setClientPublicKey(userPublicKey);
        String keyId = dataPreparationService.addPublicKeyToClient(sharedStepsContext.getClientCommonContext().getFirstClient(), KeyPairGeneratorUtil.createKeySeed(KeyUse.SIG, "RS256", userPublicKey).get(0));
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
