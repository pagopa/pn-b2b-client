package it.pagopa.pn.interop.cucumber.steps.notification.model;

public enum DeepLinkType {
    NO_DEEP_LINK(""),
    CATALOGO_E_SERVICE("/catalogo-e-service/$DA_CONTESTO(eServiceId)/$DA_CONTESTO(descriptorId)"),
    API_E_SERVICE("/gestione-client/api-e-service/$DA_CONTESTO(clientId)"),
    E_SERVICE_EROGAZIONE("/erogazione/e-service/$DA_CONTESTO(eServiceId)/$DA_CONTESTO(descriptorId)"),
    TEMPLATE_E_SERVICE_EROGAZIONE("/erogazione/template-eservice/$DA_CONTESTO(eServiceTemplateId)/$DA_CONTESTO(eServiceTemplateVersionId)"),
    FINALITA_EROGAZIONE("/erogazione/finalita/$DA_CONTESTO(purposeId)"),
    RICHIESTA_EROGAZIONE("/erogazione/richieste/$DA_CONTESTO(agreementId)"),
    PORTACHIAVI_EROGAZIONE("/erogazione/portachiavi/$DA_CONTESTO(keychainId)"),
    RICHIESTA_FRUIZIONE("/fruizione/richieste/$DA_CONTESTO(agreementId)"),
    FINALITA_FRUIZIONE("/fruizione/finalita/$DA_CONTESTO(purposeId)"),
    ANAGRAFICA_ADERENTE("/aderente/anagrafica"),
    DELEGA_ADERENTE("/aderente/deleghe/$DA_CONTESTO(delegationId)");

    private final String value;

    DeepLinkType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
