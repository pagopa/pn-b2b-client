package it.pagopa.pn.interop.cucumber.steps.m2m.apiv3.users.utils;

import it.pagopa.interop.authorization.domain.Role;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.m2m.common.utils.BaseResolver;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class UsersResolver extends BaseResolver {

    public UsersResolver(SharedStepsContext sharedStepsContext) {
        super(sharedStepsContext);
    }

    public UUID resolveUserId(String raw) {
        final Role role = sharedStepsContext.getRole();
        final String tenant = sharedStepsContext.getTenantType();

        return resolveOrParse(raw, UUID::fromString, () -> sharedStepsContext.getIdentityService().getUserId(tenant, role.getValue()), () -> sharedStepsContext.getIdentityService().getUserId(tenant, role.getValue()), UUID::randomUUID, () -> null);
    }

    public Integer resolveInteger(String raw) {
        return resolveOrParse(raw, Integer::valueOf, null, null, null, () -> null);
    }

    public List<String> resolveRoles(String raw) {
        return resolveOrParse(raw, value -> Arrays.stream(value.split(",")).map(String::trim).filter(role -> !role.isEmpty()).toList(), null, null, null, () -> null);
    }
}
