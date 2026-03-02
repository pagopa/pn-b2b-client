package it.pagopa.interop.config.springconfig.springconfig;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
@Slf4j
public class ApiProfileConfiguration {

    @Bean
    public ApiProfile apiProfile(Environment env) {
        ApiProfile profile = ApiProfile.from(
                env.getProperty("api.mode", "RIGHT_FIT"),
                env.getProperty("api.m2m.version", "V3"),
                env.getProperty("api.bff.version", "V1"),
                env.getProperty("api.set", "M2M")
        );

        log.info("Api profile: {}", profile);
        return profile;
    }

}
