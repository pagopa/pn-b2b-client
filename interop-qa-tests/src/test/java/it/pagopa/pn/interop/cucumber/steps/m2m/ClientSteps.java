package it.pagopa.pn.interop.cucumber.steps.m2m;

import static org.apache.commons.lang3.ObjectUtils.allNull;
import static org.assertj.core.api.Assertions.assertThat;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.interop.agreement.service.IM2MClientsClient;
import it.pagopa.interop.authorization.service.identity.IdentityService;
import it.pagopa.interop.authorization.service.utils.PollingService;
import it.pagopa.interop.common.IHttpExecutor;
import it.pagopa.interop.generated.openapi.clients.bff.model.PurposeSeed;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.Purpose;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.Purposes;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.datapreparationservice.M2MDataPreparationService;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Predicate;

public class ClientSteps {
    private final ClientTokenConfigurator clientTokenConfigurator;
    private final SharedStepsContext sharedStepsContext;
    private final IdentityService identityService;
    private final M2MDataPreparationService dataPreparationService;
    private final IM2MClientsClient clientsApis;
    private final IHttpExecutor httpCallExecutor;
    private final PollingService pollingService;

    public ClientSteps(ClientTokenConfigurator clientTokenConfigurator,
        SharedStepsContext sharedStepsContext,
        M2MDataPreparationService dataPreparationService) {
        this.clientTokenConfigurator = clientTokenConfigurator;
        this.sharedStepsContext = sharedStepsContext;
        this.identityService = sharedStepsContext.getIdentityService();
        this.dataPreparationService = dataPreparationService;
        this.clientsApis = clientTokenConfigurator.getM2MClientsClient();
        this.httpCallExecutor = sharedStepsContext.getHttpCallExecutor();
        this.pollingService = sharedStepsContext.getPollingService();
    }

    @When("l'utente tenta di ottenere le finalità associate al client")
    public void getClientPurposes() {
        UUID clientId = sharedStepsContext.getClientCommonContext().getLastClient();
        httpCallExecutor.performCall(() -> clientsApis.getClientPurposes(clientId));
    }

    @When("l'utente tenta di ottenere le finalità associate ad un client inesistente")
    public void getNonExistentClientPurposes() {
        UUID clientId = UUID.randomUUID();
        httpCallExecutor.performCall(() -> clientsApis.getClientPurposes(clientId));
    }

    @Then("le finalità associate al client sono state correttamente visualizzate")
    public void purposesVisualized() {
        List<PurposeSeed> createdPurposes = sharedStepsContext.getPurposeCommonContext()
            .getCreatedPurposes();
        List<Purpose> returnedPurposes = ((Purposes) httpCallExecutor.getResponse()).getResults();

        Predicate<Purpose> oneOfCreated = purpose -> createdPurposes.stream().anyMatch(created -> areConsistent(created, purpose));
        assertThat(returnedPurposes)
            .isNotEmpty()
            .allMatch(oneOfCreated, "each returned purpose match at least one created purpose");
    }

    private boolean areConsistent(PurposeSeed createdPurpose, Purpose returnedPurpose) {
        return  allNull(createdPurpose, returnedPurpose) ||
            Objects.equals(createdPurpose.getConsumerId(), returnedPurpose.getConsumerId()) &&
                Objects.equals(createdPurpose.getDescription(), returnedPurpose.getDescription()) &&
                Objects.equals(createdPurpose.getTitle(), returnedPurpose.getTitle()) &&
                Objects.equals(createdPurpose.getEserviceId(), returnedPurpose.getEserviceId()) &&
                Objects.equals(createdPurpose.getIsFreeOfCharge(), returnedPurpose.getIsFreeOfCharge()) &&
                Objects.equals(createdPurpose.getFreeOfChargeReason(), returnedPurpose.getFreeOfChargeReason());
    }
}