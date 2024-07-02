package it.pagopa.pn.client.b2b.pa.parsing.service.impl;

import it.pagopa.pn.client.b2b.pa.parsing.dto.PnParserParameter;
import it.pagopa.pn.client.b2b.pa.parsing.parser.impl.PnParser;
import it.pagopa.pn.client.b2b.pa.parsing.dto.PnParserResponse;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;


@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class PnParserService {
    private final PnParser pnParser;


    public PnParserService(PnParser pnParser) {
        this.pnParser = pnParser;
    }

    public PnParserResponse extractSingleField(byte[] source, PnParserParameter parserParameter) {
        if (parserParameter.isValidField()) {
            return pnParser.extractSingleField(source, parserParameter);
        }
        return null;
    }

    public PnParserResponse extractAllField(byte[] source, PnParserParameter parserParameter) {
        return pnParser.extractAllField(source, parserParameter);
    }
}