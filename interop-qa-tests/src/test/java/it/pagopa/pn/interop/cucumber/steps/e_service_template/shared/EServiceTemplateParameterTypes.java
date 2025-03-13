package it.pagopa.pn.interop.cucumber.steps.e_service_template.shared;

import io.cucumber.java.ParameterType;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTemplateVersionState;

public class EServiceTemplateParameterTypes {
    /* TODO 10/03/2025 ora che è stato introdotto Mapstruct si potrebbe delegare a lui la
     *  conversione in tutti i ParameterType, snellendo un po' il codice */
    @ParameterType("DRAFT|PUBLISHED|DEPRECATED|SUSPENDED")
    public EServiceTemplateVersionState eServiceTemplateVersionState(String state) {
        return switch (state) {
            case "DRAFT"        -> EServiceTemplateVersionState.DRAFT;
            case "PUBLISHED"    -> EServiceTemplateVersionState.PUBLISHED;
            case "DEPRECATED"   -> EServiceTemplateVersionState.DEPRECATED;
            case "SUSPENDED"    -> EServiceTemplateVersionState.SUSPENDED;
            default             -> throw new IllegalArgumentException("Unsupported %s value: %s".formatted(
                EServiceTemplateVersionState.class.getSimpleName(),
                state));
        };
    }
}
