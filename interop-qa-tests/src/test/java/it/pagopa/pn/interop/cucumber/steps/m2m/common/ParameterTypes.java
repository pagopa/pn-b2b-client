package it.pagopa.pn.interop.cucumber.steps.m2m.common;

import io.cucumber.java.ParameterType;
import it.pagopa.interop.common.enums.EntityIdType;
import it.pagopa.pn.interop.cucumber.steps.m2m.attribute.CertifiedAttributeSteps;
import it.pagopa.pn.interop.cucumber.steps.m2m.attribute.DeclaredAttributeSteps;
import it.pagopa.pn.interop.cucumber.steps.m2m.attribute.VerifiedAttributeSteps;
import it.pagopa.pn.interop.cucumber.steps.m2m.eservice.EserviceDescriptorSteps;
import it.pagopa.pn.interop.cucumber.steps.m2m.eservice.EserviceSteps;
import it.pagopa.pn.interop.cucumber.steps.notification.NotificationSteps;

public class ParameterTypes {

    @ParameterType("verifiedAttribute|declaredAttribute|certifiedAttribute|descriptor|eService|notifiche")
    public Class<? extends ICommonSteps> entityType(String type) {
        return switch (type) {
            case "verifiedAttribute" -> VerifiedAttributeSteps.class;
            case "declaredAttribute" -> DeclaredAttributeSteps.class;
            case "certifiedAttribute" -> CertifiedAttributeSteps.class;
            case "descriptor" -> EserviceDescriptorSteps.class;
            case "eService" -> EserviceSteps.class;
            case "notifiche" -> NotificationSteps.class;
            default -> throw new IllegalArgumentException("Unknown type: " + type);
        };
    }

    @ParameterType("invalid|invalido|null|nullo|inesistente")
    public EntityIdType entityIdType(String idType) {
        return EntityIdType.fromString(idType);
    }

}
