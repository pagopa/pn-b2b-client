package it.pagopa.interop.authorization.domain.dpop;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "m2m.v3.agid.jwt", ignoreUnknownFields = false)
public class AgidJwtProperties {
    private String jwksUrl;
}
