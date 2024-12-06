package it.pagopa.pn.interop.cucumber.steps.authorization;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import it.pagopa.interop.generated.openapi.clients.bff.model.ClientKind;
import it.pagopa.interop.generated.openapi.clients.bff.model.ClientSeed;
import it.pagopa.interop.authorization.service.IAuthorizationClient;
import it.pagopa.interop.authorization.service.utils.CommonUtils;
import it.pagopa.pn.interop.cucumber.steps.utils.DataPreparationService;
import it.pagopa.pn.interop.cucumber.steps.utils.HttpCallExecutor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class ClientListingSteps {
    private final IAuthorizationClient authorizationClient;
    private final ClientCommonSteps clientCommonSteps;
    private final CommonUtils commonUtils;
    private final DataPreparationService dataPreparationService;
    private final HttpCallExecutor httpCallExecutor;

    public ClientListingSteps(IAuthorizationClient authorizationClient,
                              ClientCommonSteps clientCommonSteps,
                              CommonUtils commonUtils,
                              DataPreparationService dataPreparationService,
                              HttpCallExecutor httpCallExecutor) {
        this.authorizationClient = authorizationClient;
        this.clientCommonSteps = clientCommonSteps;
        this.commonUtils = commonUtils;
        this.dataPreparationService = dataPreparationService;
        this.httpCallExecutor = httpCallExecutor;
    }

    @Given("{string} ha già creato {int} client {string} con la keyword {string} nel nome")
    public void userHasAlreadyCreatedClientWithSameKeyword(String tenantType, int numClient, String clientKind, String keyword) {
        List<UUID> result = new ArrayList<>();
        for (int i = 0; i < numClient; i ++) {
            ClientSeed clientSeed = new ClientSeed();
            clientSeed.setName(String.format("client-%d-%d-%s", i, ThreadLocalRandom.current().nextInt(0, Integer.MAX_VALUE), keyword));
            result.add(dataPreparationService.createClient(clientKind, clientCommonSteps.getClients().get(0), clientSeed));
        }
        clientCommonSteps.setClients(List.of(result.get(0)));
    }


    @When("l'utente richiede una operazione di listing dei client con offset {int}")
    public void retrieveClientsListWithOffset(int offset) {
        httpCallExecutor.performCall(() ->
                authorizationClient.getClients("", offset, 12, String.valueOf(getRandomInt()), null, null));
    }

    @When("l'utente richiede una operazione di listing dei client con filtro \"CONSUMER\"")
    public void retrieveClientsListWithFilter() {
        httpCallExecutor.performCall(() ->
                authorizationClient.getClients("", 0, 12, String.valueOf(getRandomInt()), null, ClientKind.CONSUMER));
    }

    @When("l'utente richiede una operazione di listing dei client filtrando per la keyword {string}")
    public void retrieveClientsListByFilterForKeyword(String keyword) {
        httpCallExecutor.performCall(() ->
                authorizationClient.getClients("", 0, 12, String.format("%d-%s", getRandomInt(), keyword), null, null));
    }

    @When("l'utente richiede una operazione di listing dei client filtrando per membro utente con ruolo {string}")
    public void retrieveClientsListByFilterForUserAndRole(String tenantType, String role) {
        UUID userId = commonUtils.getUserId(tenantType, role);
        httpCallExecutor.performCall(() ->
                authorizationClient.getClients("", 0, 12, String.valueOf(getRandomInt()), List.of(userId), null));
    }

    @When("l'utente richiede una operazione di listing dei client")
    public void retrieveClientsList() {
        httpCallExecutor.performCall(() ->
                authorizationClient.getClients("", 0, 12, String.valueOf(getRandomInt()), null, null));
    }

    @When("l'utente richiede una operazione di listing dei client limitata a {int} risultati")
    public void retrieveTruncateClientsList(int limit) {
        httpCallExecutor.performCall(() ->
                authorizationClient.getClients("", 0, limit, String.valueOf(getRandomInt()), null, null));
    }

    private int getRandomInt() {
        return ThreadLocalRandom.current().nextInt(0, Integer.MAX_VALUE);
    }




}
