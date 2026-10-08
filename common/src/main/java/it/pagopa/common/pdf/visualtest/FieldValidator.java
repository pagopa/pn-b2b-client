package it.pagopa.common.pdf.visualtest;

import java.util.List;

/**
 * Contratto per la validazione del valore di un campo dinamico nel PDF.
 *
 * <p>Un {@code FieldValidator} riceve i frammenti locati da un {@link FieldLocator}
 * e decide se il valore è conforme alle aspettative, restituendo un messaggio
 * d'errore descrittivo in caso di fallimento.</p>
 */
@FunctionalInterface
public interface FieldValidator {

    /**
     * Valida i frammenti che rappresentano il valore del campo.
     *
     * @param locatedFragments frammenti trovati dal {@link FieldLocator} associato
     * @return {@code null} se la validazione ha successo,
     *         oppure una stringa descrittiva dell'errore (mai vuota)
     */
    String validate(List<TextFragment> locatedFragments);
}
