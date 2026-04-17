package it.pagopa.pn.interop.cucumber.steps.tenant;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.interop.authorization.service.identity.IdentityService;
import it.pagopa.interop.common.IHttpExecutor;
import it.pagopa.interop.tenant.service.ITenantsApi;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import org.springframework.http.HttpStatus;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class TenantReadSteps {
    private final ClientTokenConfigurator clientTokenConfigurator;
    private final SharedStepsContext sharedStepsContext;
    private final IdentityService identityService;

    public TenantReadSteps(ClientTokenConfigurator clientTokenConfigurator, SharedStepsContext sharedStepsContext) {
        this.clientTokenConfigurator = clientTokenConfigurator;
        this.sharedStepsContext = sharedStepsContext;
        this.identityService = sharedStepsContext.getIdentityService();
    }

    @When("l'utente richiede la lettura dell'aderente {string}")
    public void readConsumer(String tenantType) {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());
        UUID tenantId = identityService.getOrganizationId(tenantType);
        sharedStepsContext.getHttpCallExecutor().performCall(
                () -> clientTokenConfigurator.getTenantsApi().getTenant(tenantId)
        );
    }

    @When("l'utente visualizza se all'utente d'appartenenza è permesso partecipare a processi di delega")
    public void isTenantDelegationsAllowed() {
        String tenantType = sharedStepsContext.getTenantType();
        UUID tenantId = identityService.getOrganizationId(tenantType);
        ITenantsApi tenantsApi = clientTokenConfigurator.getTenantsApi();
        IHttpExecutor httpCallExecutor = sharedStepsContext.getHttpCallExecutor();

        httpCallExecutor.performCall(() -> tenantsApi.isTenantAllowedToDelegation(tenantId));
        Boolean tenantAllowedToDelegation = null;
        if (httpCallExecutor.getResponseStatus().is2xxSuccessful()) {
            tenantAllowedToDelegation = (Boolean) httpCallExecutor.getResponse();
        }
        sharedStepsContext.getTenantCommonContext().setIsTenantDelegationsAllowed(tenantAllowedToDelegation);
    }

    @Then("l'utente ottiene responso {booleanResponse} dal sistema sul poter partecipare a processi di delega")
    public void checkDelegationsAllowed(Boolean expectedResponse) {
        Boolean isTenantDelegationsAllowed = sharedStepsContext.getTenantCommonContext().getIsTenantDelegationsAllowed();
        HttpStatus responseStatus = sharedStepsContext.getHttpCallExecutor().getResponseStatus();

        assertThat(responseStatus.is2xxSuccessful())
                .as("Verifica la chiamata a API abbia avuto esito positivo")
                .isTrue();
        assertThat(isTenantDelegationsAllowed)
                .as("Verifica che la risposta sia valorizzata")
                .isNotNull();
        assertThat(isTenantDelegationsAllowed)
                .as("Verifica che la possibilità di prendere parte a una delega sia coerente con quanto atteso")
                .isEqualTo(expectedResponse);
    }

}
