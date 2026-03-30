package it.pagopa.pn.interop.cucumber.steps.m2m.apiv3.client_consumer;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.PendingException;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.interop.agreement.service.IM2MV3ClientsClient;
import it.pagopa.interop.authorization.service.IAuthorizationClient;
import it.pagopa.interop.authorization.service.utils.PollingService;
import it.pagopa.interop.common.IHttpExecutor;
import it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.model.Client;
import it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.model.ClientSeed;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.m2m.apiv3.client_consumer.model.ClientConsumerContext;
import it.pagopa.pn.interop.cucumber.steps.m2m.apiv3.client_consumer.utils.ClientConsumerResolver;
import it.pagopa.pn.interop.cucumber.steps.producer_keychains.model.ProducerKeychainsContext;
import it.pagopa.pn.interop.cucumber.steps.selfcare.model.TenantContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

@Slf4j
public class ClientConsumerSteps {
    private final IHttpExecutor httpCallExecutor;
    private final IM2MV3ClientsClient clientsApi;
    private final IAuthorizationClient authorizationClient;
    private final ClientConsumerContext clientConsumerContext = new ClientConsumerContext();
    private final ClientConsumerResolver resolver;

    public ClientConsumerSteps(ClientTokenConfigurator clientTokenConfigurator, SharedStepsContext sharedStepsContext, ProducerKeychainsContext producerKeychainsContext, TenantContext tenantContext) {
        clientsApi = clientTokenConfigurator.getM2mV3ClientsClient();
        authorizationClient = clientTokenConfigurator.getAuthorizationClient();
        httpCallExecutor = sharedStepsContext.getHttpCallExecutor();
        clientsApi.setHttpCallExecutor(httpCallExecutor);
        resolver = new ClientConsumerResolver(sharedStepsContext, clientConsumerContext);
    }

    @When("l'utente tenta di creare un client di tipo CONSUMER per il tenant {string} con:")
    public void createClient(String tenant, DataTable dataTable) {

        List<Map<String, String>> rows = dataTable.asMaps(String.class, String.class);

        if (rows.isEmpty()) {
            throw new IllegalArgumentException("La DataTable è vuota");
        }

        Map<String, String> clientSeedMap = rows.get(0);

        final String resolvedName = resolver.resolveClientName(clientSeedMap.get("name"));
        final String resolvedDescription = resolver.resolveDescription(clientSeedMap.get("description"));
        final List<UUID> resolvedMembers = resolver.resolveMembers(clientSeedMap.get("members"), tenant);

        ClientSeed clientSeed = new ClientSeed();
        clientSeed.setName(resolvedName);
        clientSeed.setDescription(resolvedDescription);
        clientSeed.setMembers(resolvedMembers);

        try {
            Client client = clientsApi.createClient(clientSeed);

            clientConsumerContext.setExpectedName(client.getName());
            clientConsumerContext.setExpectedDescription(client.getDescription());
            clientConsumerContext.setExpectedMembers(resolvedMembers);

            clientConsumerContext.setActualClientId(client.getId());
            clientConsumerContext.setActualName(client.getName());
            clientConsumerContext.setActualDescription(client.getDescription());

        } catch (IllegalStateException e) {
            log.warn(httpCallExecutor.getErrorMessage());
        }
    }

    @And("l'oggetto Client restituito rispetta quanto atteso")
    public void assertCreatedClient() {
        // I membri del client non sono restituiti con l'oggetto Client e dovrà essere effettuata un ulteriore chiamata per la verifica
        assertSoftly(softly -> {
            softly.assertThat(clientConsumerContext.getActualClientId()).isNotNull();
            softly.assertThat(clientConsumerContext.getActualName()).isEqualTo(clientConsumerContext.getExpectedName());
            softly.assertThat(clientConsumerContext.getActualDescription()).isEqualTo(clientConsumerContext.getExpectedDescription());
        });
    }

    @Then("l'utente tenta l'eliminazione del client di tipo CONSUMER con id {string}")
    public void deleteClientConsumer(String rawClient) {
       final UUID resolvedClientId = resolver.resolveClientId(rawClient);

        try{
            clientsApi.deleteClient(resolvedClientId);
            httpCallExecutor.snapshot();

            PollingService.makePolling(
                   () -> {
                       try{
                           return clientsApi.getClient(resolvedClientId);
                       } catch (IllegalStateException e){
                           log.warn(httpCallExecutor.getErrorMessage());
                           return null;
                       }
                   },
                   res -> httpCallExecutor.getResponseStatus().equals(HttpStatus.NOT_FOUND),
                   "Client non eliminato!",
                   5,
                   1000
            );

           httpCallExecutor.resetFormSnapshot();
       } catch (IllegalStateException e) {
           log.warn(httpCallExecutor.getErrorMessage());
       }
    }
}
