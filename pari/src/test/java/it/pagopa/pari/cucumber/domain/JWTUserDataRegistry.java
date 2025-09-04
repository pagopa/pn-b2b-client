package it.pagopa.pari.cucumber.domain;

import it.pagopa.pari.cucumber.config.RdbUserRoleConfiguration;
import it.pagopa.pari.registrobeni.domain.RdbRole;
import org.springframework.stereotype.Component;

import java.util.Map;

import static it.pagopa.pari.registrobeni.domain.RdbRole.INVITALIA;
import static it.pagopa.pari.registrobeni.domain.RdbRole.PRODUTTORE_1;
import static it.pagopa.pari.registrobeni.domain.RdbRole.PRODUTTORE_2;

@Component
public class JWTUserDataRegistry {
    private final RdbUserRoleConfiguration userRoleConfiguration;
    private final Map<RdbRole, JWTUserData> jwtUserDataMap;

    public JWTUserDataRegistry(RdbUserRoleConfiguration userRoleConfiguration) {
        this.userRoleConfiguration = userRoleConfiguration;
        jwtUserDataMap = populateMap();
    }

    private Map<RdbRole, JWTUserData> populateMap() {
        return Map.of(
                PRODUTTORE_1, JWTUserData.builder()
                        .uid(userRoleConfiguration.getProductor1().getUid())
                        .name(userRoleConfiguration.getProductor1().getName())
                        .familyName(userRoleConfiguration.getProductor1().getFamilyName())
                        .orgId(userRoleConfiguration.getProductor1().getOrgId())
                        .orgRole(userRoleConfiguration.getProductor1().getOrgRole())
                        .orgFc(userRoleConfiguration.getProductor1().getOrgFc())
                        .build(),
                PRODUTTORE_2, JWTUserData.builder()
                        .uid(userRoleConfiguration.getProductor2().getUid())
                        .name(userRoleConfiguration.getProductor2().getName())
                        .familyName(userRoleConfiguration.getProductor2().getFamilyName())
                        .orgId(userRoleConfiguration.getProductor2().getOrgId())
                        .orgRole(userRoleConfiguration.getProductor2().getOrgRole())
                        .orgFc(userRoleConfiguration.getProductor2().getOrgFc())
                        .build(),
                INVITALIA, JWTUserData.builder()
                        .uid(userRoleConfiguration.getInvitalia().getUid())
                        .name(userRoleConfiguration.getInvitalia().getName())
                        .familyName(userRoleConfiguration.getInvitalia().getFamilyName())
                        .orgId(userRoleConfiguration.getInvitalia().getOrgId())
                        .orgRole(userRoleConfiguration.getInvitalia().getOrgRole())
                        .build()
        );
    }

    public JWTUserData getUserData(RdbRole key) {
        return jwtUserDataMap.get(key);
    }

    public Map<RdbRole, JWTUserData> getAll() {
        return jwtUserDataMap;
    }

}
