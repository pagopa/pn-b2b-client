package it.pagopa.pari.cucumber.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "role")
@Getter
@Setter
public class RdbUserRoleConfiguration {

    private Rdb rdb;
    private Role productor1;
    private Role productor2;
    private Role invitaliaL1;
    private Role invitaliaL2;
    private Role merchantRoot;

    @Getter
    @Setter
    public static class Rdb {
        private String baseUrl;
    }

    @Getter
    @Setter
    public static class Role {
        private String uid;
        private String name;
        private String familyName;
        private String email;
        private String acquirerId;
        private String merchantId;
        private String orgId;
        private String orgVat;
        private String orgFc;
        private String orgName;
        private String orgPartyRole;
        private String orgRole;
    }
}
