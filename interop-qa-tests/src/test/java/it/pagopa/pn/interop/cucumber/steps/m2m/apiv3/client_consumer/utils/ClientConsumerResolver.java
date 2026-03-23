package it.pagopa.pn.interop.cucumber.steps.m2m.apiv3.client_consumer.utils;

import it.pagopa.interop.authorization.service.identity.IdentityService;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.m2m.apiv3.client_consumer.model.ClientConsumerContext;
import it.pagopa.pn.interop.cucumber.steps.m2m.common.utils.AbstractResolver;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ClientConsumerResolver extends AbstractResolver {
    private final ClientConsumerContext clientConsumerContext;
    private final IdentityService identityService;

    public ClientConsumerResolver(SharedStepsContext sharedStepsContext, ClientConsumerContext clientConsumerContext) {
        super(sharedStepsContext);
        this.clientConsumerContext = clientConsumerContext;
        this.identityService = sharedStepsContext.getIdentityService();
    }

    public String resolveClientName(String raw) {
        return resolveOrParse(
                raw,
                (value) -> value,
                clientConsumerContext::getActualName,
                clientConsumerContext::getExpectedName,
                () -> "consumer_client_name_m2m_v3_" + ThreadLocalRandom.current().nextInt(0, Integer.MAX_VALUE),
                () -> ""
        );
    }

    public String resolveDescription(String raw) {
        return resolveOrParse(
                raw,
                (value) -> value,
                clientConsumerContext::getActualDescription,
                clientConsumerContext::getExpectedDescription,
                () -> "consumer_client_description_m2m_v3_" + ThreadLocalRandom.current().nextInt(0, Integer.MAX_VALUE),
                () -> ""
        );
    }

    public List<UUID> resolveMembers(String raw, String tenant) {

        return resolveOrParse(
                raw,
                (input) -> {
                    List<String> roles = new ArrayList<>();

                    // Per gestire input di questo tipo [%admin, %api,security, %security, %api, %support]
                    Pattern pattern = Pattern.compile("%?([^,\\[\\]]+(?:,[^,\\[\\]]+)?)");
                    Matcher matcher = pattern.matcher(input);

                    while (matcher.find()) {
                        roles.add(matcher.group(1).trim());
                    }

                    return roles.stream()
                            .map(r -> identityService.getUserId(tenant, r))
                            .toList();
                },
                clientConsumerContext::getActualMembers,
                clientConsumerContext::getExpectedMembers,
                () -> Collections.singletonList(UUID.randomUUID()),
                Collections::emptyList
        );
    }

    public UUID resolveClientId(String raw) {
        return resolveOrParse(
                raw,
                UUID::fromString,
                clientConsumerContext::getActualClientId,
                clientConsumerContext::getActualClientId,
                UUID::randomUUID,
                () -> null
        );
    }
}
