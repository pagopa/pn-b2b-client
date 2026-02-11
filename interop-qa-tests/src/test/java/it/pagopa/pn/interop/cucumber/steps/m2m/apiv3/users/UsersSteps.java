package it.pagopa.pn.interop.cucumber.steps.m2m.apiv3.users;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.interop.common.IHttpExecutor;
import it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.model.User;
import it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.model.Users;
import it.pagopa.interop.users.service.UsersClient;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;

@Slf4j
public class UsersSteps {
    private final UsersClient usersClient;
    private final IHttpExecutor httpCallExecutor;
    private final SharedStepsContext sharedStepsContext;
    private final UsersContext usersContext;

    public UsersSteps(UsersClient usersClient, SharedStepsContext sharedStepsContext, UsersContext usersContext) {
        this.usersClient = usersClient;
        this.sharedStepsContext = sharedStepsContext;
        this.usersContext = usersContext;
        this.httpCallExecutor = sharedStepsContext.getHttpCallExecutor();
        this.usersClient.setHttpCallExecutor(this.httpCallExecutor);
    }

    @When("viene invocata l'API di recupero utenze appartenenti al tenant del richiedente con limit {string} offset {string} e roles {string}")
    public void getUsers(String limit, String offset, String roles) {
        Integer requestedLimit = parseNullableInteger(limit);
        Integer requestedOffset = parseNullableInteger(offset);
        List<String> requestedRoles = parseNullableRoles(roles);

        try {
            Users response = usersClient.getUsers(requestedLimit, requestedOffset, requestedRoles);
            Assertions.assertThat(response)
                    .as("La response contenente la lista utenti non deve essere null")
                    .isNotNull();
            usersContext.setM2mUsers(response.getResults());
        } catch (IllegalStateException e) {
            log.warn(e.getMessage());
        }
    }


    @Then("si verifica che le liste di utenze restituite coincidano")
    public void verifyUsersListsCoincide() {
        List<User> m2mUsers = usersContext.getM2mUsers();
        List<it.pagopa.interop.generated.openapi.clients.bff.model.User> selfcareUsers = usersContext.getSelfcareUsers();

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

    private Integer parseNullableInteger(String value) {
        if (value == null || "null".equalsIgnoreCase(value)) {
            return null;
        }
        return Integer.valueOf(value);
    }

    private List<String> parseNullableRoles(String value) {
        if (value == null || "null".equalsIgnoreCase(value)) {
            return null;
        }
        return Arrays.stream(value.split(","))
                .map(String::trim)
                .filter(role -> !role.isEmpty())
                .toList();
    }

    private UUID parseNullableUuid(String value) {
        if (value == null || "null".equalsIgnoreCase(value)) {
            return null;
        }
        return UUID.fromString(value);
    }
}
