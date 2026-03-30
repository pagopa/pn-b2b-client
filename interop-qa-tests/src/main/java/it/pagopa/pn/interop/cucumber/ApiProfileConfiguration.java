package it.pagopa.pn.interop.cucumber;

import it.pagopa.interop.config.springconfig.springconfig.ApiProfile;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;

@Configuration
public class ApiProfileConfiguration {

    @Bean
    @Scope(value = "cucumber-glue", proxyMode = ScopedProxyMode.TARGET_CLASS)
    public ApiProfile apiProfile() {
        ApiProfileConfig config = ApiProfileContext.getRequired();
        return ApiProfile.from(
                config.getApiMode(),
                config.getApiM2mVersion(),
                config.getApiBffVersion(),
                config.getApiSet()
        );
    }
}

