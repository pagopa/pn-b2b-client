package it.pagopa.pn.interop.cucumber.steps.m2m.apiv3.client_consumer;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import it.pagopa.interop.agreement.service.IM2MV3ClientsClient;
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

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

@Slf4j
public class ClientConsumerSteps {
    private final IHttpExecutor httpCallExecutor;
    private final IM2MV3ClientsClient clientsApi;
    private final ClientConsumerContext clientConsumerContext = new ClientConsumerContext();
    private final ClientConsumerResolver resolver;

    public ClientConsumerSteps(ClientTokenConfigurator clientTokenConfigurator, SharedStepsContext sharedStepsContext, ProducerKeychainsContext producerKeychainsContext, TenantContext tenantContext) {
        clientsApi = clientTokenConfigurator.getM2mV3ClientsClient();
        httpCallExecutor = sharedStepsContext.getHttpCallExecutor();
        clientsApi.setHttpCallExecutor(httpCallExecutor);
        resolver = new ClientConsumerResolver(sharedStepsContext, clientConsumerContext);
    }

    @Given("l'utente tenta di creare un client di tipo CONSUMER per il tenant {string} con:")
    public void createClient(String tenant, DataTable dataTable) {

        Map<String, String> clientSeedMap = dataTable.asMap();

        final String resolvedName = resolver.resolveClientName(clientSeedMap.get("name"));
        final String resolvedDescription = resolver.resolveDescription(clientSeedMap.get("description"));
        final List<UUID> resolvedMembers = resolver.resolveMembers(clientSeedMap.get("members"), tenant);

        ClientSeed clientSeed = new ClientSeed();
        clientSeed.setName(resolvedName);
        clientSeed.setDescription(resolvedDescription);
        clientSeed.setMembers(resolvedMembers);

        try {
            Client client = clientsApi.createClient(clientSeed);

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
}
