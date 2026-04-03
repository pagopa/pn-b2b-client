package it.pagopa.pn.interop.cucumber.steps.m2m.apiv3.users;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.interop.common.IHttpExecutor;
import it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.model.User;
import it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.model.Users;
import it.pagopa.interop.users.IM2MV3UsersClient;
import it.pagopa.interop.users.service.M2MV3UsersClient;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.m2m.apiv3.users.utils.UsersResolver;
import it.pagopa.pn.interop.cucumber.steps.selfcare.model.TenantContext;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;

@Slf4j
public class UsersSteps {
    private final IM2MV3UsersClient usersClient;
    private final TenantContext tenantContext;
    private final UsersResolver resolver;

    public UsersSteps(ClientTokenConfigurator clientTokenConfigurator, SharedStepsContext sharedStepsContext, TenantContext tenantContext) {
        this.usersClient = clientTokenConfigurator.getM2mV3UsersClient();
        this.tenantContext = tenantContext;
        IHttpExecutor httpCallExecutor = sharedStepsContext.getHttpCallExecutor();
        this.usersClient.setHttpCallExecutor(httpCallExecutor);
        this.resolver = new UsersResolver(sharedStepsContext);
    }

    @When("viene invocata l'API di recupero utenze appartenenti al tenant del richiedente con limit {string} offset {string} e roles {string}")
    public void getUsers(String limit, String offset, String roles) {
        Integer requestedLimit = resolver.resolveInteger(limit);
        Integer requestedOffset = resolver.resolveInteger(offset);
        List<String> requestedRoles = resolver.resolveRoles(roles);

        try {
            Users response = usersClient.getUsers(requestedLimit, requestedOffset, requestedRoles);
            Assertions.assertThat(response)
                    .as("La response contenente la lista utenti non deve essere null")
                    .isNotNull();
            tenantContext.setM2mUsers(response.getResults());
        } catch (IllegalStateException e) {
            log.warn(e.getMessage());
        }
    }

    @When("viene invocata l'API per il recupero dell'utente {string} purchè appartenente al tenant del richiedente")
    public void getUser(String userId) {
        UUID requestedUserId = resolver.resolveUserId(userId);
        try {
            User response = usersClient.getUser(requestedUserId);
            Assertions.assertThat(response)
                    .as("La response contenente l'utente non deve essere null")
                    .isNotNull();
        } catch (IllegalStateException e) {
            log.warn(e.getMessage());
        }
    }

    @Then("si verifica che le liste di utenze restituite coincidano")
    public void verifyUsersListsCoincide() {
        List<User> m2mUsers = tenantContext.getM2mUsers();
        List<it.pagopa.interop.generated.openapi.clients.bff.model.User> selfcareUsers = tenantContext.getSelfcareUsers();

        Assertions.assertThat(m2mUsers)
                .as("La lista utenti M2M non deve essere null")
                .isNotNull();
        Assertions.assertThat(selfcareUsers)
                .as("La lista utenti Selfcare non deve essere null")
                .isNotNull();

        Set<UUID> m2mUserIds = m2mUsers.stream()
                .map(User::getUserId)
                .collect(Collectors.toSet());
        Set<UUID> selfcareUserIds = selfcareUsers.stream()
                .map(it.pagopa.interop.generated.openapi.clients.bff.model.User::getUserId)
                .collect(Collectors.toSet());

        Assertions.assertThat(m2mUserIds)
                .as("Gli userId restituiti da M2M e Selfcare devono coincidere")
                .isEqualTo(selfcareUserIds);
    }

}
