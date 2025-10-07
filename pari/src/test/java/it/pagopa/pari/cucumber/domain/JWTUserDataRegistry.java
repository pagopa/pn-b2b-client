package it.pagopa.pari.cucumber.domain;

import it.pagopa.pari.cucumber.config.RdbUserRoleConfiguration;
import it.pagopa.pari.registrobeni.domain.RdbRole;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Map;

import static it.pagopa.pari.registrobeni.domain.RdbRole.*;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
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
//                        .orgVat(userRoleConfiguration.getProductor2().getOrgVat())
//                        .orgFc(userRoleConfiguration.getProductor2().getOrgFc())
                        .orgName(userRoleConfiguration.getProductor2().getOrgName())
                        .orgPartyRole(userRoleConfiguration.getProductor2().getOrgPartyRole())
                        .orgRole(userRoleConfiguration.getProductor1().getOrgRole())
//                        .orgFc(userRoleConfiguration.getProductor1().getOrgFc())
                        .build(),
                PRODUTTORE_2, JWTUserData.builder()
                        .uid(userRoleConfiguration.getProductor2().getUid())
                        .name(userRoleConfiguration.getProductor2().getName())
                        .familyName(userRoleConfiguration.getProductor2().getFamilyName())
                        .orgId(userRoleConfiguration.getProductor2().getOrgId())
//                        .orgVat(userRoleConfiguration.getProductor2().getOrgVat())
//                        .orgFc(userRoleConfiguration.getProductor2().getOrgFc())
                        .orgName(userRoleConfiguration.getProductor2().getOrgName())
                        .orgPartyRole(userRoleConfiguration.getProductor2().getOrgPartyRole())
                        .orgRole(userRoleConfiguration.getProductor2().getOrgRole())
                        .build(),
                INVITALIA_L1, JWTUserData.builder()
                        .uid(userRoleConfiguration.getInvitaliaL1().getUid())
                        .name(userRoleConfiguration.getInvitaliaL1().getName())
                        .familyName(userRoleConfiguration.getInvitaliaL1().getFamilyName())
                        .orgId(userRoleConfiguration.getInvitaliaL1().getOrgId())
//                        .orgVat(userRoleConfiguration.getInvitaliaL1().getOrgVat())
//                        .orgFc(userRoleConfiguration.getInvitaliaL1().getOrgFc())
                        .orgName(userRoleConfiguration.getInvitaliaL1().getOrgName())
                        .orgPartyRole(userRoleConfiguration.getInvitaliaL1().getOrgPartyRole())
                        .orgRole(userRoleConfiguration.getInvitaliaL1().getOrgRole())
                        .build(),
                INVITALIA_L2, JWTUserData.builder()
                        .uid(userRoleConfiguration.getInvitaliaL2().getUid())
                        .name(userRoleConfiguration.getInvitaliaL2().getName())
                        .familyName(userRoleConfiguration.getInvitaliaL2().getFamilyName())
                        .orgId(userRoleConfiguration.getInvitaliaL2().getOrgId())
//                        .orgVat(userRoleConfiguration.getInvitaliaL2().getOrgVat())
//                        .orgFc(userRoleConfiguration.getInvitaliaL2().getOrgFc())
                        .orgName(userRoleConfiguration.getInvitaliaL2().getOrgName())
                        .orgPartyRole(userRoleConfiguration.getInvitaliaL2().getOrgPartyRole())
                        .orgRole(userRoleConfiguration.getInvitaliaL2().getOrgRole())
                        .build(),
                MERCHANT_ROOT, JWTUserData.builder()
                        .aud("idpay.merchant.welfare.pagopa.it")
                        .iss("https://api-io.dev.cstar.pagopa.it")
                        .uid(userRoleConfiguration.getMerchantRoot().getUid())
                        .name(userRoleConfiguration.getMerchantRoot().getName())
                        .familyName(userRoleConfiguration.getMerchantRoot().getFamilyName())
                        .email(userRoleConfiguration.getMerchantRoot().getEmail())
                        .acquirerId(userRoleConfiguration.getMerchantRoot().getAcquirerId())
                        .merchantId(userRoleConfiguration.getMerchantRoot().getMerchantId())
                        .orgId(userRoleConfiguration.getMerchantRoot().getOrgId())
//                        .orgVat(userRoleConfiguration.getMerchantRoot().getOrgVat())
                        .orgName(userRoleConfiguration.getMerchantRoot().getOrgName())
                        .orgPartyRole(userRoleConfiguration.getMerchantRoot().getOrgPartyRole())
                        .orgRole(userRoleConfiguration.getMerchantRoot().getOrgRole())
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
