package it.pagopa.interop.conf.springconfig;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:qa-interop.properties")
@Getter
@Setter
public class InteropClientConfigs {
    @Value("${bff.base-url}")
    private String baseUrl;
    @Value("${remote-wellknown-url}")
    private String remoteWellknownUrl;
    @Value("${session.tokens.duration.seconds}")
    private Long sessionTokenDurationSec;
    @Value("${interop-env}")
    private String environment;
}
