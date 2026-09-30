package it.pagopa.pn.interop.cucumber.steps.attribute;

import io.cucumber.java.ParameterType;
import it.pagopa.interop.authorization.domain.TenantType;
import it.pagopa.interop.generated.openapi.clients.bff.model.AttributeKind;

public class AttributeParameterTypes {
    @ParameterType("CERTIFIED_DISCRETE|CERTIFIED|DECLARED|VERIFIED")
    public AttributeKind attributeKind(String kind) {
        return AttributeKind.fromValue(kind);
    }

    /* 15/04/2025: tentato di inserirlo in una classe di utilità generale in
    * it.pagopa.pn.interop.cucumber.utility ma Cucumber non registrava correttamente il parameter type */
    @ParameterType("GSP|GSP2|PA1|PA2|Privato")
    public TenantType tenantType(String type) {
        return TenantType.valueOf(type);
    }
}
