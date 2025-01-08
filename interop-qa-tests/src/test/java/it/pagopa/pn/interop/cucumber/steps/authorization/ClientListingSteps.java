package it.pagopa.pn.interop.cucumber.steps.authorization;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import it.pagopa.interop.generated.openapi.clients.bff.model.ClientKind;
import it.pagopa.interop.generated.openapi.clients.bff.model.ClientSeed;
import it.pagopa.interop.authorization.service.IAuthorizationClient;
import it.pagopa.interop.authorization.service.utils.CommonUtils;
import it.pagopa.pn.interop.cucumber.steps.DataPreparationService;
import it.pagopa.interop.utils.HttpCallExecutor;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class ClientListingSteps {
    private final IAuthorizationClient authorizationClient;
    private final CommonUtils commonUtils;
    private final DataPreparationService dataPreparationService;
    private final HttpCallExecutor httpCallExecutor;
    private final SharedStepsContext sharedStepsContext;

    public ClientListingSteps(IAuthorizationClient authorizationClient,
                              DataPreparationService dataPreparationService,
                              SharedStepsContext sharedStepsContext) {
        this.authorizationClient = authorizationClient;
        this.commonUtils = sharedStepsContext.getCommonUtils();
        this.dataPreparationService = dataPreparationService;
        this.httpCallExecutor = sharedStepsContext.getHttpCallExecutor();
        this.sharedStepsContext = sharedStepsContext;
    }

    @Given("{string} ha già creato {int} client {string} con la keyword {string} nel nome")
    public void userHasAlreadyCreatedClientWithSameKeyword(String tenantType, int numClient, String clientKind, String keyword) {
        List<UUID> result = new ArrayList<>();
        for (int i = 0; i < numClient; i ++) {
            ClientSeed clientSeed = new ClientSeed();
            clientSeed.setName(String.format("client-%d-%d-%s", i, sharedStepsContext.getTestSeed(), keyword));
            result.add(dataPreparationService.createClient(clientKind, clientSeed));
        }
        sharedStepsContext.getClientCommonContext().setClients(List.of(result.get(0)));
    }

    @When("l'utente richiede una operazione di listing dei client con offset {int}")
    public void retrieveClientsListWithOffset(int offset) {
        httpCallExecutor.performCall(() ->
                authorizationClient.getClients(sharedStepsContext.getXCorrelationId(), offset, 12, String.valueOf(sharedStepsContext.getTestSeed()), null, null));
    }

    @When("l'utente richiede una operazione di listing dei client con filtro \"CONSUMER\"")
    public void retrieveClientsListWithFilter() {
        httpCallExecutor.performCall(() ->
                authorizationClient.getClients(sharedStepsContext.getXCorrelationId(), 0, 12, String.valueOf(sharedStepsContext.getTestSeed()), null, ClientKind.CONSUMER));
    }

    @When("l'utente richiede una operazione di listing dei client filtrando per la keyword {string}")
    public void retrieveClientsListByFilterForKeyword(String keyword) {
        httpCallExecutor.performCall(() ->
                authorizationClient.getClients(sharedStepsContext.getXCorrelationId(), 0, 12, String.format("%d-%s", sharedStepsContext.getTestSeed(), keyword), null, null));
    }

    @When("l'utente richiede una operazione di listing dei client filtrando per membro utente con ruolo {string}")
    public void retrieveClientsListByFilterForUserAndRole(String role) {
        UUID userId = commonUtils.getUserId(sharedStepsContext.getTenantType(), role);
        httpCallExecutor.performCall(() ->
                authorizationClient.getClients(sharedStepsContext.getXCorrelationId(), 0, 12, String.valueOf(sharedStepsContext.getTestSeed()), List.of(userId), null));
    }

    @When("l'utente richiede una operazione di listing dei client")
    public void retrieveClientsList() {
        commonUtils.setBearerToken(commonUtils.getToken(sharedStepsContext.getTenantType(), null));
        httpCallExecutor.performCall(() ->
                authorizationClient.getClients(sharedStepsContext.getXCorrelationId(), 0, 12, String.valueOf(sharedStepsContext.getTestSeed()), null, null));
    }

    @When("l'utente richiede una operazione di listing dei client limitata a {int} risultati")
    public void retrieveTruncateClientsList(int limit) {
        httpCallExecutor.performCall(() ->
                authorizationClient.getClients(sharedStepsContext.getXCorrelationId(), 0, limit, String.valueOf(sharedStepsContext.getTestSeed()), null, null));
    }

    private int getRandomInt() {
        return ThreadLocalRandom.current().nextInt(0, Integer.MAX_VALUE);
    }




}
