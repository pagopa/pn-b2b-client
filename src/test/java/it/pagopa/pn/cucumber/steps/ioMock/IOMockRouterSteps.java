package it.pagopa.pn.cucumber.steps.ioMock;

import io.cucumber.java.en.Given;
import it.pagopa.common.util.StringUtils;
import it.pagopa.pn.cucumber.steps.ioMock.context.IoMockScenarioContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import java.util.HashMap;

@Slf4j
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class IOMockRouterSteps {

    private final IoMockScenarioContext context;

    @Autowired
    public IOMockRouterSteps(IoMockScenarioContext context) {
        this.context = context;
    }

    @Given("una richiesta di stato messaggio con identificativo standard privo di prefisso mock {string}")
    public void preparePollingRequestWithStandardRealId(String standardMessageId) {
        String resolvedId = StringUtils.resolveValue(standardMessageId);
        log.info("Impostato identificativo reale/standard per test routing trasparente: {}", resolvedId);
        context.setQueriedMessageId(resolvedId);

        if (context.getRequestHeaders() == null) {
            context.setRequestHeaders(new HashMap<>());
        }
        context.getRequestHeaders().put("Ocp-Apim-Subscription-Key", "sub-key-io-collaudo-test-12345");
    }
}
