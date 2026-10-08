package it.pagopa.common.pdf.visualtest;

/**
 * Strategia di mascheramento da applicare all'area di un campo dinamico prima
 * del confronto visivo pixel-level.
 *
 * <p>Le strategie determinano quanta area escludere dal diff visivo per ogni campo:</p>
 * <ul>
 *   <li>{@link #TIGHT_BOX} – maschera solo il bounding-box del frammento trovato</li>
 *   <li>{@link #FULL_LINE}  – maschera l'intera riga orizzontale in cui ricade il campo</li>
 *   <li>{@link #FULL_TABLE_CELL} – maschera una cella di tabella stimata attorno al campo</li>
 * </ul>
 */
public enum MaskStrategy {

    /**
     * Maschera solo il bounding-box preciso del campo (approccio conservativo,
     * maggiore sensibilità ai cambiamenti di layout).
     */
    TIGHT_BOX,

    /**
     * Maschera l'intera riga orizzontale che contiene il campo
     * (utile per campi di testo che occupano una riga intera).
     */
    FULL_LINE,

    /**
     * Maschera un'area rettangolare più ampia che simula una cella di tabella,
     * aggiungendo padding verticale e orizzontale attorno al campo
     * (utile per campi inseriti in griglie o tabelle).
     */
    FULL_TABLE_CELL
}
