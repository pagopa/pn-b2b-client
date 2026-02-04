package it.pagopa.interop.conf;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
@Setter
public class InteropClientConfigs {
    @Value("${bff.base-url}")
    private String baseUrl;

    @Value("${m2m.base-url}")
    private String m2mBaseUrl;

    @Value("${apiv3.base-url}")
    private String apiv3BaseUrl;

    @Value("${remote-wellknown-url}")
    private String remoteWellknownUrl;

    @Value("${session.tokens.duration.seconds}")
    private Long sessionTokenDurationSec;

    @Value("${interop-env}")
    private String environment;

    @Value("${max-polling-try-ms}")
    private int maxPollingTry;

    @Value("${max-polling-sleep-ms}")
    private int maxPollingSleep;
}
