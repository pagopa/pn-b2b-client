package it.pagopa.pn.cucumber.steps.utilitySteps;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("file:config/taxids-${spring.profiles.active}.properties")
@EnableConfigurationProperties(TaxIdConfig.class)
public class TaxIdConfiguration {
}
