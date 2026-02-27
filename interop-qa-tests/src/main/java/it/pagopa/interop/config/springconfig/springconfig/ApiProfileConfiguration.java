package it.pagopa.interop.config.springconfig.springconfig;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
public class ApiProfileConfiguration {

    @Bean
    public ApiProfile apiProfile(Environment env) {
        return ApiProfile.from(

            env.getProperty("api.mode", "BEST_FIT"),
            env.getProperty("api.m2m.version", "V2"),
            env.getProperty("api.bff.version", "V1"),
            env.getProperty("api.set", "M2M")
        );
    }

}
