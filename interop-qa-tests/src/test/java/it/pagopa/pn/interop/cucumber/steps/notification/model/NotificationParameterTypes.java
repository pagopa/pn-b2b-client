package it.pagopa.pn.interop.cucumber.steps.notification.model;

import io.cucumber.java.ParameterType;

public class NotificationParameterTypes {

    @ParameterType("CATALOGO_E_SERVICE|CATALOGO_E_SERVICE_PRIMO_DESCRITTORE|API_E_SERVICE|E_SERVICE_EROGAZIONE|" +
            "E_SERVICE_EROGAZIONE_PRIMO_DESCRITTORE|TEMPLATE_E_SERVICE_EROGAZIONE|FINALITA_EROGAZIONE|" +
            "RICHIESTA_EROGAZIONE|PORTACHIAVI_EROGAZIONE|RICHIESTA_FRUIZIONE|FINALITA_FRUIZIONE|ANAGRAFICA_ADERENTE|" +
            "DELEGA_ADERENTE|NO_DEEP_LINK")
    public DeepLinkType deepLink(String value) {
        return DeepLinkType.valueOf(value);
    }
}
