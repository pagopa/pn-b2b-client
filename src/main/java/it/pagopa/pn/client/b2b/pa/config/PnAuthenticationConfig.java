package it.pagopa.pn.client.b2b.pa.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
public class PnAuthenticationConfig {

    @Value("${pn.authentication.pg.public.key}")
    private String publicKey;

    @Value("${pn.authentication.pg.public.key.rotation}")
    private String publicKeyRotation;

    @Value("${pn.authentication.pg.admin.uid}")
    private String adminUid;

    @Value("${pn.authentication.pg.admin.group.uid}")
    private String adminGroupUid;

    @Value("${pn.authentication.pg.operator.uid}")
    private String operatorUid;

    @Value("${pn.authentication.pg.organization.id}")
    private String organizationId;
}
