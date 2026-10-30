package it.pagopa.pn.cucumber.steps.tracciaturaIndagine;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

/**
 * Elementi del recapito cartaceo che tracciano la fonte dell'indirizzo e l'indagine in loco del postino,
 * con la descrizione usata nei file feature e l'elemento di timeline corrispondente.
 * Il tentativo è null per gli elementi unici per destinatario (raccomandata semplice e invio della bonaria).
 */
@Getter
@RequiredArgsConstructor
public enum ElementoCartaceo {
    PRIMO_INVIO("il primo invio cartaceo", "SEND_ANALOG_DOMICILE", 0),
    SECONDO_INVIO("il secondo invio cartaceo", "SEND_ANALOG_DOMICILE", 1),
    ESITO_PRIMO_TENTATIVO("l'esito del primo tentativo", "SEND_ANALOG_FEEDBACK", 0),
    ESITO_SECONDO_TENTATIVO("l'esito del secondo tentativo", "SEND_ANALOG_FEEDBACK", 1),
    RACCOMANDATA_SEMPLICE("la raccomandata semplice", "SEND_SIMPLE_REGISTERED_LETTER", null),
    INVIO_BONARIA("l'invio cartaceo della comunicazione bonaria", "SEND_ANALOG_MESSAGE", null);

    public static final String REGEX = "il primo invio cartaceo|il secondo invio cartaceo|l'esito del primo tentativo"
            + "|l'esito del secondo tentativo|la raccomandata semplice|l'invio cartaceo della comunicazione bonaria";

    private final String descrizione;
    private final String category;
    private final Integer sentAttemptMade;

    public static ElementoCartaceo fromDescrizione(String descrizione) {
        return Arrays.stream(values())
                .filter(elemento -> elemento.descrizione.equals(descrizione))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Elemento cartaceo non gestito: " + descrizione));
    }
}
