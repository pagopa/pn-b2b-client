package it.pagopa.pn.client.b2b.pa.parsing.parser.impl;

import static it.pagopa.pn.client.b2b.pa.parsing.parser.utils.PnContentExtractorUtils.*;

import it.pagopa.common.util.PDFUtility;
import it.pagopa.pn.client.b2b.pa.parsing.config.PnLegalFactTokens;
import it.pagopa.pn.client.b2b.pa.parsing.dto.PnParserRecord;
import it.pagopa.pn.client.b2b.pa.parsing.exception.PnParserException;
import it.pagopa.pn.client.b2b.pa.parsing.parser.IPnContentExtractor;
import it.pagopa.pn.client.b2b.pa.parsing.parser.IPnParserLegalFact;
import it.pagopa.pn.client.b2b.pa.parsing.parser.utils.PnTextSlidingWindow;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;


@Slf4j
public class PnContentExtractor implements IPnContentExtractor {
    private final PnLegalFactTokens pnLegalFactTokens;


    public PnContentExtractor(PnLegalFactTokens pnLegalFactTokens) {
        this.pnLegalFactTokens = pnLegalFactTokens;
    }

    @Override
    public PnParserRecord.PnParserContent extractContent(byte[] source, IPnParserLegalFact.LegalFactType legalFactType) {
        try {
            PDFUtility.StructuredText structuredText = PDFUtility.extractStructuredText(source);
            if (structuredText == null) {
                return null;
            }
            List<String> boldValueList = structuredText.boldWords();
            if (boldValueList.isEmpty()) {
                throw new PnParserException("pdf provided is not valid document because did not contain bold words.");
            }
            String extractedText = structuredText.text();
            if (extractedText == null) {
                throw new PnParserException("some problems on extracting text by area.");
            }
            return getContent(extractedText, boldValueList, legalFactType);
        } catch (RuntimeException exception) {
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
        for (String value : valuesList) {
            String field = getFieldByToken(pnTextSlidingWindow, value);
            if (field != null && !pnTextSlidingWindow.isToDiscard(value)) {
                return field;
            }
            pnTextSlidingWindow.slideWindow(value);
        }
        return null;
    }

    @Override
    public String cleanUp(String text, boolean mantainWhitespace) {
        if (text == null) {
            return null;
        }

        if (mantainWhitespace) {
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
            for (PnLegalFactTokens.PnLegalFactTypeTokenGroup group : pnLegalFactTokens.getFieldTokenList()) {
                if (group.getLegalFactTypeList().contains(legalFactType)) {
                    pnTextSlidingWindow.setTokenStart(group.getTokenStart());
                    pnTextSlidingWindow.setTokenEnd(group.getTokenEnd());
                    List<Integer> brokenValueList = concatenateValue(pnTextSlidingWindow, value, composeList, pnLegalFactTokens.getTokenProps());
                    if (!brokenValueList.isEmpty()) {
                        for (int i = 1; i <= brokenValueList.size(); i++) {
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
}
