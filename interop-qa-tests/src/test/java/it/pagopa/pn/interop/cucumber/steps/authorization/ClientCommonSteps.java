package it.pagopa.pn.interop.cucumber.steps.authorization;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import it.pagopa.interop.generated.openapi.clients.bff.model.ClientSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.CompactClients;
import it.pagopa.interop.generated.openapi.clients.bff.model.KeyUse;
import it.pagopa.interop.generated.openapi.clients.bff.model.PurposeAdditionDetailsSeed;
import it.pagopa.interop.service.utils.CommonUtils;
import it.pagopa.interop.service.utils.KeyPairGeneratorUtil;
import it.pagopa.pn.interop.cucumber.steps.utils.DataPreparationService;
import it.pagopa.pn.interop.cucumber.steps.utils.HttpCallExecutor;
import lombok.Getter;
import lombok.Setter;
import org.junit.jupiter.api.Assertions;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Getter
@Setter
public class ClientCommonSteps {
    private final DataPreparationService dataPreparationService;
    private final CommonUtils commonUtils;
    private final HttpCallExecutor httpCallExecutor;

    private List<UUID> clients;
    private String clientPublicKey;
    private PurposeAdditionDetailsSeed purposeId;

    public ClientCommonSteps(DataPreparationService dataPreparationService, CommonUtils commonUtils, HttpCallExecutor httpCallExecutor) {
        this.dataPreparationService = dataPreparationService;
        this.commonUtils = commonUtils;
        this.httpCallExecutor = httpCallExecutor;
    }

    @Given("{string} ha già creato {int} client {string}")
    public void createClientsForTenants(String tenantType, int numClient, String clientKind) {
        List<UUID> result = new ArrayList<>();
        for (int i = 0; i < numClient; i ++) {
            ClientSeed clientSeed = new ClientSeed();
            clientSeed.setName(String.format("client-%d-%d-%s", i, ThreadLocalRandom.current().nextInt(0, Integer.MAX_VALUE)));
            result.add(dataPreparationService.createClient(clientKind, clients.get(0), clientSeed));
        }
        setClients(List.of(result.get(0)));
    }

    @Given("{string} ha già inserito l'utente con ruolo {string} come membro di quel client")
    public void tenantHasAlreadyAddUsersWithRole(String tenantType, String roleOfMemberToAdd) {
        UUID clientMemberUserId = commonUtils.getUserId(tenantType, roleOfMemberToAdd);
        dataPreparationService.addMemberToClient(clients.get(0), clientMemberUserId);
    }

    @Then("si ottiene status code {int} e la lista di {int} client(s)")
    public void verifyStatusCodeAndClientList(int statusCode, int count) {
        Assertions.assertEquals(statusCode, httpCallExecutor.getClientResponse().value());
        Assertions.assertEquals(count, ((CompactClients) httpCallExecutor.getResponse()).getResults().size());
    }

    @Given("un {string} di {string} ha caricato una chiave pubblica in quel client")
    public void roleOfTenantHasAlreadyUploadClientPublicKey(String role, String tenantType) {
        dataPreparationService.addPublicKeyToClient(clients.get(0), KeyPairGeneratorUtil.createKeySeed(KeyUse.SIG, "RS256",
                KeyPairGeneratorUtil.createBase64PublicKey("RSA", 2048)).get(0));
    }

    @Then("si ottiene status code {string}")
    public void verifyStatusCode(String statusCode) {
        Assertions.assertEquals(statusCode, httpCallExecutor.getClientResponse().toString());
    }


}
