package it.pagopa.pn.interop.cucumber.steps.m2m.common;

import io.cucumber.java.ParameterType;
import it.pagopa.interop.common.enums.EntityIdType;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.EServiceTemplateVersionState;
import it.pagopa.pn.interop.cucumber.steps.m2m.attribute.CertifiedAttributeSteps;
import it.pagopa.pn.interop.cucumber.steps.m2m.attribute.DeclaredAttributeSteps;
import it.pagopa.pn.interop.cucumber.steps.m2m.attribute.VerifiedAttributeSteps;
import it.pagopa.pn.interop.cucumber.steps.m2m.eservice.EserviceDescriptorSteps;
import it.pagopa.pn.interop.cucumber.steps.m2m.eservice.EserviceSteps;
import it.pagopa.pn.interop.cucumber.steps.notification.NotificationSteps;

public class ParameterTypes {

    @ParameterType("verifiedAttribute|declaredAttribute|certifiedAttribute|descriptor|eService")
    public Class<? extends ICommonSteps> entityType(String type) {
        return switch (type) {
            case "verifiedAttribute" -> VerifiedAttributeSteps.class;
            case "declaredAttribute" -> DeclaredAttributeSteps.class;
            case "certifiedAttribute" -> CertifiedAttributeSteps.class;
            case "descriptor" -> EserviceDescriptorSteps.class;
            case "eService" -> EserviceSteps.class;
            default -> throw new IllegalArgumentException("Unknown type: " + type);
        };
    }

    @ParameterType("invalid|invalido|null|nullo|inesistente|valido")
    public EntityIdType entityIdType(String idType) {
        return EntityIdType.fromString(idType);
    }

    @ParameterType("e-service|agreement|attribute|purpose|tenant|e-service template|key|client|producer key|producer keychain|producer delegation|consumer delegation")
    public InteropEntityKind interopEntityKind(String entityString) {
        return switch (entityString) {
            case "e-service" -> InteropEntityKind.E_SERVICE;
            case "agreement" -> InteropEntityKind.AGREEMENT;
            case "attribute" -> InteropEntityKind.ATTRIBUTE;
            case "purpose" -> InteropEntityKind.PURPOSE;
            case "tenant" -> InteropEntityKind.TENANT;
            case "e-service template" -> InteropEntityKind.E_SERVICE_TEMPLATE;
            case "key" -> InteropEntityKind.KEY;
            case "client" -> InteropEntityKind.CLIENT;
            case "producer key" -> InteropEntityKind.PRODUCER_KEY;
            case "producer keychain" -> InteropEntityKind.PRODUCER_KEYCHAIN;
            case "producer delegation" -> InteropEntityKind.PRODUCER_DELEGATION;
            case "consumer delegation" -> InteropEntityKind.CONSUMER_DELEGATION;
            default -> throw new IllegalArgumentException("Tipo di entità INTEROP non supportata: " + entityString);
        };
    }

    @ParameterType("DRAFT|PUBLISHED|DEPRECATED|SUSPENDED")
    public static EServiceTemplateVersionState eServiceTemplateVersionStateM2M(String value) {
        return EServiceTemplateVersionState.fromValue(value);
    }
}
