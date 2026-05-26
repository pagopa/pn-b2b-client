package it.pagopa.pn.interop.cucumber.steps.journey;

import io.cucumber.java.en.Given;
import it.pagopa.interop.authorization.service.utils.voucher.domain.ClientAssertionOptions;
import it.pagopa.pn.interop.cucumber.steps.authorization.ClientCommonSteps;
import it.pagopa.pn.interop.cucumber.steps.authorization.ClientKeyReadSteps;

public class ClientJourneySteps {

    private final ClientCommonSteps clientCommonSteps;
    private final ClientKeyReadSteps clientKeyReadSteps;

    public ClientJourneySteps(
            ClientCommonSteps clientCommonSteps,
            ClientKeyReadSteps clientKeyReadSteps
    ) {
        this.clientCommonSteps = clientCommonSteps;
        this.clientKeyReadSteps = clientKeyReadSteps;
    }

    @Given("l'admin del fruitore {string} ha già creato un client di tipo {interopClientType} aggiungendo se stesso come membro e caricando una coppia di chiavi")
    public void createClient(String tenantType, ClientAssertionOptions.ClientType clientType) {
        createClient("admin", tenantType, clientType);
    }

    @Given("un {string} del fruitore {string} ha già creato un client di tipo {interopClientType} aggiungendo se stesso come membro e caricando una coppia di chiavi")
    public void createClient(String role, String tenantType, ClientAssertionOptions.ClientType clientType) {
        clientCommonSteps.createClientsForTenants(tenantType, 1, clientType.name());
        clientCommonSteps.tenantHasAlreadyAddUsersWithRole(tenantType, role);
        clientKeyReadSteps.clientPublicKeyUpload(role, tenantType);
    }
}
