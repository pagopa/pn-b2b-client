package it.pagopa.pn.cucumber.steps.ritardatore;

import io.cucumber.java.en.Then;
import it.pagopa.pn.cucumber.utils.LambdaInvoker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Slf4j
@RequiredArgsConstructor
public class RitardatoreSteps {

    private final LambdaInvoker lambdaInvoker;

    @Then("invoco la lambda {string}")
    public void invoco_la_lambda(String lambdaName) {

        String payload = """
        {
          "type": "TOKEN",
          "authorizationToken": "Bearer test-token",
          "methodArn": "arn:aws:execute-api:eu-west-1:123456789012:example/prod/GET/resource",
          "headers": {
            "x-pagopa-cx-taxid": "ABCDEF12G34H567I"
          }
        }
        """;

        String result = lambdaInvoker.invokeMyLambda(lambdaName, payload);
        log.info(result);
    }
}
