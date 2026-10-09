package it.pagopa.pn.http.executor;

import io.cucumber.core.backend.Backend;
import io.cucumber.core.backend.Glue;
import io.cucumber.core.options.CommandlineOptionsParser;
import io.cucumber.core.options.CucumberProperties;
import io.cucumber.core.options.CucumberPropertiesParser;
import io.cucumber.core.options.RuntimeOptions;
import io.cucumber.spring.SpringBackendProviderService;
import io.cucumber.spring.SpringFactory;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

class CucumberGlueConfigurationTest {

    @Test
    void directFeatureExecutionDiscoversOnlyTheSendSpringContext() {
        RuntimeOptions options = cliOptions();

        assertEquals(List.of("it.pagopa.pn.cucumber.steps.CucumberSpringIntegration"),
                discoverSpringContexts(options));
    }

    @Test
    void explicitCliGlueOverridesTheSendDefaultForLocalFeatures() {
        RuntimeOptions options = cliOptions("--glue", "it.pagopa.pn.http.executor");

        assertEquals(List.of(HttpCallExecutorTestContext.class.getName()),
                discoverSpringContexts(options));
    }

    private RuntimeOptions cliOptions(String... arguments) {
        RuntimeOptions fileOptions = new CucumberPropertiesParser()
                .parse(CucumberProperties.fromPropertiesFile())
                .build();
        return new CommandlineOptionsParser(System.out)
                .parse(arguments)
                .addDefaultGlueIfAbsent()
                .build(fileOptions);
    }

    /** Loads the same glue metadata as the CLI, without starting Spring or running hooks and steps. */
    private List<String> discoverSpringContexts(RuntimeOptions options) {
        List<String> contexts = new ArrayList<>();
        SpringFactory factory = new SpringFactory();
        Backend backend = new SpringBackendProviderService().create(factory, type -> {
            contexts.add(type.getName());
            return factory.addClass(type);
        }, () -> Thread.currentThread().getContextClassLoader());

        backend.loadGlue(mock(Glue.class), options.getGlue());
        return contexts;
    }
}
