package it.pagopa.pn.interop.cucumber.steps.config.parameter_type;

import io.cucumber.java.ParameterType;
import it.pagopa.interop.authorization.service.utils.voucher.domain.ClientAssertionOptions;

public class CommonParameterType {

    @ParameterType("API|api|CONSUMER|consumer")
    public ClientAssertionOptions.ClientType interopClientType(String clientType) {
        return ClientAssertionOptions.ClientType.valueOf(clientType.toUpperCase());
    }

    @ParameterType("fruitore|erogatore")
    public String currentActor(String actor) {
        return switch (actor.toLowerCase()) {
            case "fruitore" -> "fruitore";
            case "erogatore" -> "erogatore";
            default -> throw new IllegalArgumentException("Invalid actor: " + actor);
        };
    }
}
