package it.pagopa.common.pdf.visualtest;

/**
 * Frammento di testo estratto da una singola pagina PDF con la sua posizione geometrica.
 * Le coordinate sono espresse in <em>punti PDF</em> con origine in basso a sinistra
 * (sistema di riferimento PDFBox/PDF standard).
 *
 * <p>Nessun tipo nativo di Apache PDFBox è esposto nelle firme pubbliche di questo record.</p>
 *
 * @param text     testo del frammento
 * @param x        coordinata X del bordo sinistro del bounding-box
 * @param y        coordinata Y del bordo inferiore del bounding-box
 * @param width    larghezza del bounding-box
 * @param height   altezza del bounding-box
 * @param pageIndex indice di pagina 0-based
 */
public record TextFragment(
        String text,
        float x,
        float y,
        float width,
        float height,
        int pageIndex) {

    /** Coordinata X del bordo destro. */
    public float xEnd() {
        return x + width;
    }

    /** Coordinata Y del bordo superiore (sistema PDF: y cresce verso l'alto). */
    public float yTop() {
        return y + height;
    }

    /**
     * Verifica se un altro frammento appartiene approssimativamente alla stessa riga di testo.
     *
     * @param other           altro frammento
     * @param tolerancePt     tolleranza in punti PDF per la comparazione verticale
     */
    public boolean isSameLineAs(TextFragment other, float tolerancePt) {
        return pageIndex == other.pageIndex
                && Math.abs(y - other.y) <= tolerancePt;
    }
}
