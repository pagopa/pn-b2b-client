package it.pagopa.pn.interop.cucumber.utility;

import it.pagopa.interop.utils.HttpCallExecutor;
import lombok.AllArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class CommonUtils {
    private final HttpCallExecutor httpCallExecutor;

    public void assertValidResponse() {
        Assertions.assertFalse(httpCallExecutor.getClientResponse().isError(),
            "Something went wrong: " + httpCallExecutor.getClientResponse().getReasonPhrase());
    }
}