package it.pagopa.common.pdf.visualtest;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Debug output of the comparison: one image per page of the golden master showing what was compared.
 * Grey = ink that was compared, blue = area masked on purpose (a variable value; static words inside it are still
 * compared one by one), yellow = ink that was not verified. Written next to the diff path, only when the system
 * property {@value #PROPERTY} is true: it is evidence of the comparison, not a diff, and never makes it fail.
 */
final class CoverageMap {

    static final String PROPERTY = "vrt.coverageMap";
    private static final Logger log = LoggerFactory.getLogger(CoverageMap.class);
    static final int GREY = 0x808080;
    static final int BLUE = 0xB4B4FF;
    static final int YELLOW = 0xFFC000;
    static final int INK_UNDER_MASK = 0x6464E0;
    private static final int DPI = 100;

    private CoverageMap() {
    }

    static boolean enabled(Path diffOutputPath) {
        return diffOutputPath != null && Boolean.getBoolean(PROPERTY);
    }

    static boolean nearWhite(int rgb) {
        return ((rgb >> 16) & 0xFF) > 245 && ((rgb >> 8) & 0xFF) > 245 && (rgb & 0xFF) > 245;
    }

    /** Page-pixel masks are known (pixel-perfect comparison): everything outside them was compared. */
    static List<BufferedImage> ofMasks(byte[] goldenPdf, List<ExclusionArea> masks) throws IOException {
        List<BufferedImage> pages = new ArrayList<>();
        try (PDDocument doc = Loader.loadPDF(goldenPdf)) {
            PDFRenderer renderer = new PDFRenderer(doc);
            float scale = DPI / 72f;
            for (int p = 0; p < doc.getNumberOfPages(); p++) {
                BufferedImage golden = renderer.renderImageWithDPI(p, DPI);
                float pageHeight = doc.getPage(p).getCropBox().getHeight();
                BufferedImage map = new BufferedImage(golden.getWidth(), golden.getHeight(), BufferedImage.TYPE_INT_RGB);
                for (int y = 0; y < map.getHeight(); y++) {
                    for (int x = 0; x < map.getWidth(); x++) {
                        map.setRGB(x, y, nearWhite(golden.getRGB(x, y)) ? 0xFFFFFF : GREY);
                    }
                }
                for (ExclusionArea m : masks) {
                    if (m.pageIndex() != p) {
                        continue;
                    }
                    int x1 = Math.max(0, Math.round(m.x() * scale) - 1);
                    int x2 = Math.min(map.getWidth(), Math.round((m.x() + m.width()) * scale) + 1);
                    int y1 = Math.max(0, Math.round((pageHeight - (m.y() + m.height())) * scale) - 1);
                    int y2 = Math.min(map.getHeight(), Math.round((pageHeight - m.y()) * scale) + 1);
                    for (int y = y1; y < y2; y++) {
                        for (int x = x1; x < x2; x++) {
                            map.setRGB(x, y, nearWhite(golden.getRGB(x, y)) ? BLUE : INK_UNDER_MASK);
                        }
                    }
                }
                pages.add(map);
            }
        }
        return pages;
    }

    static void write(List<BufferedImage> pages, Path diffOutputPath) {
        String base = diffOutputPath.getFileName().toString().replaceAll("\\.pdf$", "");
        for (int i = 0; i < pages.size(); i++) {
            Path target = diffOutputPath.resolveSibling(base + "-copertura-pagina-" + (i + 1) + ".png");
            try {
                ImageIO.write(pages.get(i), "png", target.toFile());
            } catch (IOException e) {
                log.warn("CoverageMap: impossibile salvare {}: {}", target, e.getMessage());
            }
        }
    }
}
