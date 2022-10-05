package it.pagopa.pn.client.b2b.pa.cucumber.test;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
        features = "src/test/resources/features",
        plugin = {"pretty", "json:target/cucumber-report.json"},
        tags = "@SmokeTest")
public class CucumberSmokeTest extends CucumberSpringIntegration {
}
