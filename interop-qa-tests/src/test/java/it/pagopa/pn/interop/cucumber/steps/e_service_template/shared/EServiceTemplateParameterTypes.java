package it.pagopa.pn.interop.cucumber.steps.e_service_template.shared;

import static it.pagopa.interop.e_service_template.IEServiceTemplateClient.EServiceTemplateDocumentKind.DOCUMENT;

import io.cucumber.java.ParameterType;
import it.pagopa.interop.e_service_template.IEServiceTemplateClient.EServiceTemplateDocumentKind;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceDescriptorState;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceMode;
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

    @ParameterType("DRAFT|PUBLISHED|DEPRECATED|SUSPENDED|ARCHIVED|WAITING_FOR_APPROVAL|ARCHIVING|ARCHIVING_SUSPENDED")
    public EServiceDescriptorState eServiceDescriptorState(String state) {
        return switch (state) {
            case "DRAFT"                -> EServiceDescriptorState.DRAFT;
            case "PUBLISHED"            -> EServiceDescriptorState.PUBLISHED;
            case "DEPRECATED"           -> EServiceDescriptorState.DEPRECATED;
            case "SUSPENDED"            -> EServiceDescriptorState.SUSPENDED;
            case "ARCHIVED"             -> EServiceDescriptorState.ARCHIVED;
            case "WAITING_FOR_APPROVAL" -> EServiceDescriptorState.WAITING_FOR_APPROVAL;
            case "ARCHIVING"            -> EServiceDescriptorState.ARCHIVING;
            case "ARCHIVING_SUSPENDED"  -> EServiceDescriptorState.ARCHIVING_SUSPENDED;
            default                     -> throw new IllegalArgumentException("Unsupported %s value: %s".formatted(
                EServiceDescriptorState.class.getSimpleName(),
                state));
        };
    }

    /* DEV.NOTE 13/03/2025 utilizzabile anche al di fuori dell'ambito degli e-service template,
     * eventualmente collocare altrove */
    @ParameterType("erogazione|ricezione")
    public EServiceMode eServiceMode(String mode) {
        return switch (mode) {
            case "erogazione"   -> EServiceMode.DELIVER;
            case "ricezione"    -> EServiceMode.RECEIVE;
            default             -> throw new IllegalArgumentException("Unsupported %s value: %s".formatted(
                EServiceMode.class.getSimpleName(),
                mode));
        };
    }

    @ParameterType("DOCUMENT|INTERFACE|ASYNC_EXCHANGE_CALLBACK_INTERFACE")
    public EServiceTemplateDocumentKind eServiceTemplateDocumentKind(String kind) {
        return switch (kind) {
            case "DOCUMENT"     -> DOCUMENT;
            case "INTERFACE"    -> EServiceTemplateDocumentKind.INTERFACE;
            case "ASYNC_EXCHANGE_CALLBACK_INTERFACE" -> EServiceTemplateDocumentKind.ASYNC_EXCHANGE_CALLBACK_INTERFACE;
            default             -> throw new IllegalArgumentException("Unsupported %s value: %s".formatted(
                EServiceTemplateDocumentKind.class.getSimpleName(),
                kind));
        };
    }
}
