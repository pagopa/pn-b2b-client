package it.pagopa.interop.config.springconfig.springconfig;

import it.pagopa.interop.authorization.service.factory.InteropTokenFactory;
import it.pagopa.interop.authorization.service.factory.TracingTokenFactory;
import it.pagopa.interop.authorization.service.utils.ConfigFileReader;
import it.pagopa.interop.authorization.service.utils.IdentityService;
import it.pagopa.interop.conf.InteropClientConfigs;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;

@Configuration
public class JwtTokenServiceConfiguration {

    @Bean
    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
    public TracingTokenFactory tracingTokenFactory(InteropClientConfigs interopClientConfigs, ConfigFileReader configFileReader) {
        return new TracingTokenFactory(interopClientConfigs, configFileReader);
    }

    @Bean
    @Primary
    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
    public InteropTokenFactory interopTokenFactory(InteropClientConfigs interopClientConfigs, ConfigFileReader configFileReader) {
        return new InteropTokenFactory(interopClientConfigs, configFileReader);
    }

    @Bean(name = "tracingIdentityService")
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public IdentityService tracingIdentityService(TracingTokenFactory tracingTokenFactory, ConfigFileReader configFileReader) {
        return new IdentityService(tracingTokenFactory, configFileReader);
    }

    @Bean(name = "interopIdentityService")
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public IdentityService interopIdentityService(InteropTokenFactory interopTokenFactory, ConfigFileReader configFileReader) {
        return new IdentityService(interopTokenFactory, configFileReader);
    }
}
