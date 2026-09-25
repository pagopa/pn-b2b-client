package it.pagopa.pn.interop.cucumber.steps.notification.model;

public enum DeepLinkType {
    NO_DEEP_LINK(""),
    CATALOGO_E_SERVICE("/catalogo-e-service/$CONTEXT(eServiceId)/$CONTEXT(descriptorId)"),
    CATALOGO_E_SERVICE_PRIMO_DESCRITTORE("/catalogo-e-service/$CONTEXT(eServiceId)/$CONTEXT(oldDescriptorId)"),
    API_E_SERVICE("/gestione-client/api-e-service/$CONTEXT(clientId)"),
    E_SERVICE_EROGAZIONE("/erogazione/e-service/$CONTEXT(eServiceId)/$CONTEXT(descriptorId)"),
    E_SERVICE_EROGAZIONE_PRIMO_DESCRITTORE("/erogazione/e-service/$CONTEXT(eServiceId)/$CONTEXT(oldDescriptorId)"),
    TEMPLATE_E_SERVICE_EROGAZIONE("/erogazione/template-eservice/$CONTEXT(eServiceTemplateId)/$CONTEXT(eServiceTemplateVersionId)"),
    FINALITA_EROGAZIONE("/erogazione/finalita/$CONTEXT(purposeId)"),
    RICHIESTA_EROGAZIONE("/erogazione/richieste/$CONTEXT(agreementId)"),
    PORTACHIAVI_EROGAZIONE("/erogazione/portachiavi/$CONTEXT(keychainId)"),
    RICHIESTA_FRUIZIONE("/fruizione/richieste/$CONTEXT(agreementId)"),
    FINALITA_FRUIZIONE("/fruizione/finalita/$CONTEXT(purposeId)"),
    ANAGRAFICA_ADERENTE("/aderente/anagrafica"),
    DELEGA_ADERENTE("/aderente/deleghe/$CONTEXT(delegationId)");

    private final String value;

    DeepLinkType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
