package it.pagopa.pn.cucumber;


import org.junit.platform.suite.api.*;

import static io.cucumber.junit.platform.engine.Constants.*;

@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("it/pagopa/pn/cucumber")
@ConfigurationParameters({
        @ConfigurationParameter(key = PLUGIN_PROPERTY_NAME, value = "pretty"),
        @ConfigurationParameter(key = PLUGIN_PROPERTY_NAME, value = "json:target/cucumber-report.json," +
                "html:target/cucumber-report.html"),
<<<<<<<< HEAD:src/test/java/it/pagopa/pn/cucumber/AppIoTest.java
        @ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "it.pagopa.pn.cucumber.steps"),
        @ConfigurationParameter(key = EXECUTION_MODE_FEATURE_PROPERTY_NAME, value = "concurrent"),
})
@IncludeTags({"appIo"})
public class AppIoTest {
========
        @ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "it.pagopa.pn.cucumber.steps")
})
@ExcludeTags({"ignore"})
@IncludeTags({"AOO_UO"})
public class AooUoTest {
>>>>>>>> develop:src/test/java/it/pagopa/pn/cucumber/AooUoTest.java
}
