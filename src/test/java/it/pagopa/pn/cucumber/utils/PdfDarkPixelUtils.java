package it.pagopa.pn.cucumber.utils;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Misura il contenuto visibile delle pagine di un PDF come quota di pixel scuri.
 * <p>
 * Ogni pagina viene renderizzata e si contano i pixel con media dei canali RGB inferiore a
 * {@link #DARK_LUMINANCE_THRESHOLD}. Soglia e quota minima {@link #MIN_DARK_PIXEL_RATIO} sono quelle del test
 * {@code ConvertPdfServiceTest} di pn-pdfraster introdotto con PN-21874: una pagina rimasta bianca ha quota 0.
 */
public final class PdfDarkPixelUtils {

    public static final int DARK_LUMINANCE_THRESHOLD = 128;
    public static final double MIN_DARK_PIXEL_RATIO = 0.5;
    private static final float RENDER_DPI = 72f;

    private PdfDarkPixelUtils() {
    }

    public static List<Double> darkPixelRatioPerPage(byte[] pdfContent) throws IOException {
        try (PDDocument document = Loader.loadPDF(pdfContent)) {
            PDFRenderer renderer = new PDFRenderer(document);
            List<Double> ratios = new ArrayList<>();
            for (int pageIndex = 0; pageIndex < document.getNumberOfPages(); pageIndex++) {
                ratios.add(darkPixelRatio(renderer.renderImageWithDPI(pageIndex, RENDER_DPI, ImageType.RGB)));
            }
            return ratios;
        }
    }

    private static double darkPixelRatio(BufferedImage image) {
        long darkPixels = 0;
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                int rgb = image.getRGB(x, y);
                int luminance = (((rgb >> 16) & 0xFF) + ((rgb >> 8) & 0xFF) + (rgb & 0xFF)) / 3;
                if (luminance < DARK_LUMINANCE_THRESHOLD) {
                    darkPixels++;
                }
            }
        }
        return (double) darkPixels / ((long) image.getWidth() * image.getHeight());
    }
}
