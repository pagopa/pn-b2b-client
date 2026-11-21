package it.pagopa.common.pdf.visualtest;

import java.util.Collections;
import java.util.List;

/**
 * Rappresenta un problema riscontrato durante la validazione di un campo dinamico.
 *
 * @param fieldName     nome leggibile del campo
 * @param errorMessage  descrizione dettagliata dell'errore (campo atteso vs trovato,
 *                      coordinate sfasate, ecc.)
 * @param locatedFragments frammenti che erano stati localizzati (può essere vuota se il campo
 *                         non è stato trovato)
 */
public record FieldIssue(
        String fieldName,
        String errorMessage,
        List<TextFragment> locatedFragments) {

    public FieldIssue {
        locatedFragments = Collections.unmodifiableList(locatedFragments);
    }

    @Override
    public String toString() {
        return "[CAMPO: " + fieldName + "] " + errorMessage;
    }
}
