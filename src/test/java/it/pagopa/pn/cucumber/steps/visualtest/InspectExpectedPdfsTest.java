package it.pagopa.pn.cucumber.steps.visualtest;

import it.pagopa.common.pdf.visualtest.ExtractedPdfText;
import it.pagopa.common.pdf.visualtest.PdfTextExtractor;
import it.pagopa.common.pdf.visualtest.TextFragment;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Files;
import java.util.List;

public class InspectExpectedPdfsTest {

    @Test
    void inspectPdfs() throws Exception {
        String[] templates = {
                "sender-ack",
                "pec-delivery",
                "notification-viewed",
                "analog-failure",
                "notification-cancelled",
                "malfunction",
                "analog-delivery-workflow-timeout-legal-fact",
                "notification-aar",
                "notification-aar-radd-alt",
                "analog-feedback-availability-statement",
                "informal-analog-communication"
        };

        String basePath = "src/test/resources/it/pagopa/pn/cucumber/visualTest/expected-pdfs/";

        for (String t : templates) {
            File f = new File(basePath + t + "/expected.pdf");
            if (!f.exists()) {
                System.out.println("MISSING: " + t);
                continue;
            }
            byte[] bytes = Files.readAllBytes(f.toPath());
            ExtractedPdfText extracted = PdfTextExtractor.extract(bytes);
            System.out.println("==================================================");
            System.out.println("TEMPLATE: " + t + " (Fragments count: " + extracted.allFragments().size() + ")");
            List<TextFragment> frags = extracted.allFragments();
            for (int i = 0; i < Math.min(frags.size(), 15); i++) {
                System.out.println("  [" + i + "] " + frags.get(i).text());
            }
        }
    }
}
