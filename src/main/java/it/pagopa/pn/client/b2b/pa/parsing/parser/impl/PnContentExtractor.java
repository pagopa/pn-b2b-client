package it.pagopa.pn.client.b2b.pa.parsing.parser.impl;

import static it.pagopa.pn.client.b2b.pa.parsing.parser.utils.PnContentExtractorUtils.*;
import it.pagopa.pn.client.b2b.pa.parsing.config.PnLegalFactTokens;
import it.pagopa.pn.client.b2b.pa.parsing.exception.PnParserException;
import it.pagopa.pn.client.b2b.pa.parsing.parser.IPnParserLegalFact;
import it.pagopa.pn.client.b2b.pa.parsing.parser.utils.PnTextSlidingWindow;
import it.pagopa.pn.client.b2b.pa.parsing.dto.PnParserRecord;
import it.pagopa.pn.client.b2b.pa.parsing.parser.IPnContentExtractor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.PDFTextStripperByArea;
import org.apache.pdfbox.text.TextPosition;
import java.awt.*;
import java.io.IOException;
import java.util.*;
import java.util.List;


@Slf4j
public class PnContentExtractor implements IPnContentExtractor {
    private final PnLegalFactTokens pnLegalFactTokens;


    public PnContentExtractor(PnLegalFactTokens pnLegalFactTokens) {
        this.pnLegalFactTokens = pnLegalFactTokens;
    }

    @Override
    public PnParserRecord.PnParserContent extractContent(byte[] source, IPnParserLegalFact.LegalFactType legalFactType) {
        try (final PDDocument document = Loader.loadPDF(source)) {

            PnBoldWordExtractor boldWordExtractor = new PnBoldWordExtractor();
            boldWordExtractor.setSortByPosition(true);
            boldWordExtractor.getText(document);
            List<String> boldValueList = boldWordExtractor.getBoldWordList();
            if(boldValueList.isEmpty()) throw new PnParserException("pdf provided is not valid document because did not contain bold words.");

            PnTextExtractor pnTextExtractor = new PnTextExtractor();
            pnTextExtractor.getPdfTextStripperByArea().setSortByPosition(true);
            String extractedText = pnTextExtractor.extractByArea(document);
            if(extractedText == null) throw new PnParserException("some problems on extracting text by area.");

            return getContent(extractedText, boldValueList, legalFactType);
        } catch (IOException | RuntimeException exception) {
            log.error("PdfBox error during parsing phase: {}", exception.getMessage());
        }
        return null;
    }

    @Override
    public PnParserRecord.PnParserContent getContent(String text, List<String> valuesList, IPnParserLegalFact.LegalFactType legalFactType) {
        String cleanedText = cleanUpText(text, pnLegalFactTokens.getTokenProps());
        List<String> cleanedList = cleanUpList(valuesList, pnLegalFactTokens.getTokenProps());
        return new PnParserRecord.PnParserContent(cleanedText, composeBrokenValue(cleanedText, cleanedList, legalFactType));
    }

    @Override
    public String getField(PnTextSlidingWindow pnTextSlidingWindow, List<String> valuesList) {
        for(String value: valuesList) {
            String field = getFieldByToken(pnTextSlidingWindow, value);
            if(field != null && !pnTextSlidingWindow.isToDiscard(value)) {
                return field;
            }
            pnTextSlidingWindow.slideWindow(value);
        }
        return null;
    }

    @Override
    public String cleanUp(String text, boolean mantainWhitespace) {
        if(text == null) {
            return null;
        }

        if(mantainWhitespace) {
            return text.replaceAll(pnLegalFactTokens.getTokenProps().getRegexCarriageNewline(), " ");
        }
        return text.replaceAll(pnLegalFactTokens.getTokenProps().getRegexCarriageNewline(), "");
    }

    private List<String> composeBrokenValue(String text, List<String> toRecomposeList, IPnParserLegalFact.LegalFactType legalFactType) {
        PnTextSlidingWindow pnTextSlidingWindow = PnTextSlidingWindow.builder().slidedText(text).originalText(text).build();
        List<String> composeList = new ArrayList<>(toRecomposeList);
        LinkedList<String> linkedList = new LinkedList<>(toRecomposeList);
        ListIterator<String> iterator = linkedList.listIterator(0);

        while (iterator.hasNext()) {
            String value = iterator.next();
            for(PnLegalFactTokens.PnLegalFactTypeTokenGroup group: pnLegalFactTokens.getFieldTokenList()) {
                if(group.getLegalFactTypeList().contains(legalFactType)) {
                    pnTextSlidingWindow.setTokenStart(group.getTokenStart());
                    pnTextSlidingWindow.setTokenEnd(group.getTokenEnd());
                    List<Integer> brokenValueList = concatenateValue(pnTextSlidingWindow, value, composeList, pnLegalFactTokens.getTokenProps());
                    if(!brokenValueList.isEmpty()) {
                        for(int i = 1; i <= brokenValueList.size(); i++) {
                            iterator.next();
                        }
                        break;
                    }
                }
            }
            pnTextSlidingWindow.slideWindow(value);
        }
        return composeList;
    }

    @Getter
    private class PnTextExtractor {
        private final PDFTextStripperByArea pdfTextStripperByArea;

        public PnTextExtractor() throws IOException {
            pdfTextStripperByArea = new PDFTextStripperByArea();
        }

        public String extractByArea(PDDocument document) throws IOException {
            String extractedText = null;
            StringBuilder textToExtract = new StringBuilder();
            for(PDPage page : document.getPages()) {
                PDRectangle mediaBox = page.getMediaBox();
                float height = mediaBox.getHeight();
                float width = mediaBox.getWidth();
                float footerHeight = 100; // Altezza del footer da escludere
                Rectangle region = new Rectangle(0, 0, (int) width, (int) (height - footerHeight));

                pdfTextStripperByArea.addRegion("contentRegion", region);
                pdfTextStripperByArea.extractRegions(page);

                String text = pdfTextStripperByArea.getTextForRegion("contentRegion");
                textToExtract.append(text).append(System.lineSeparator());

                pdfTextStripperByArea.removeRegion("contentRegion");
                extractedText = textToExtract.toString();
            }
            return extractedText;
        }
    }

    @Getter
    private class PnBoldWordExtractor extends PDFTextStripper {
        private static final String BOLD = "bold";
        private final List<String> boldWordList;

        public PnBoldWordExtractor() {
            super();
            boldWordList = new ArrayList<>();
        }

        @Override
        protected void writeString(String text, List<TextPosition> textPositions)  {
            StringBuilder boldWord = new StringBuilder();
            boolean isBold = false;

            for (TextPosition textPosition: textPositions) {
                if (textPosition.getFont().getName().toLowerCase().contains(BOLD)) {
                    isBold = true;
                    boldWord.append(textPosition.getUnicode());
                } else {
                    foundBoldWord(boldWord, isBold);
                    isBold = false;
                }
            }
            foundBoldWord(boldWord, isBold);
        }

        private void foundBoldWord(StringBuilder boldWord, boolean isBold) {
            if (isBold && !boldWord.isEmpty()) {
                boldWordList.add(boldWord.toString().trim());
                boldWord.setLength(0);
            }
        }
    }
}