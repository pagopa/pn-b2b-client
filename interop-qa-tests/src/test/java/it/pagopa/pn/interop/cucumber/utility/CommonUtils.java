package it.pagopa.pn.interop.cucumber.utility;

import it.pagopa.interop.utils.HttpCallExecutor;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class CommonUtils {
    private final HttpCallExecutor httpCallExecutor;

    private CommonUtils(SharedStepsContext sharedStepsContext) {
        httpCallExecutor = sharedStepsContext.getHttpCallExecutor();
    }

    public void assertValidResponse() {
        Assertions.assertFalse(httpCallExecutor.getClientResponse().isError(),
            "Something went wrong: " + httpCallExecutor.getClientResponse().getReasonPhrase());
    }
}