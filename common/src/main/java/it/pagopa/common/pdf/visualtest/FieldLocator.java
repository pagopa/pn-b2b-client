package it.pagopa.common.pdf.visualtest;

import java.util.List;

/**
 * Strategia per localizzare un campo dinamico all'interno del testo estratto di un PDF.
 *
 * <p>I {@link FieldLocator} sono agnostici rispetto al dominio di business:
 * non fanno riferimento a IUN, tipi di documento SEND o logiche applicative.</p>
 */
@FunctionalInterface
public interface FieldLocator {

    /**
     * Cerca il valore di un campo all'interno della lista di tutti i frammenti testuali
     * del documento.
     *
     * @param allFragments tutti i {@link TextFragment} del documento, in ordine di estrazione
     * @return i frammenti che rappresentano il valore trovato, oppure lista vuota se non trovato
     */
    List<TextFragment> locate(List<TextFragment> allFragments);
}
