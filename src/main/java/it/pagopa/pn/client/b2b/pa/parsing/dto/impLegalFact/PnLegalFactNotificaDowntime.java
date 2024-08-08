package it.pagopa.pn.client.b2b.pa.parsing.dto.impLegalFact;

import static it.pagopa.pn.client.b2b.pa.parsing.parser.IPnParserLegalFact.*;
import it.pagopa.pn.client.b2b.pa.parsing.dto.IPnLegalFact;
import it.pagopa.pn.client.b2b.pa.parsing.dto.PnParserRecord;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import java.util.HashMap;
import java.util.Map;


@Getter
@Setter
@Builder
public class PnLegalFactNotificaDowntime implements IPnLegalFact {
    private String title;
    private String dataOraDecorrenza;
    private String dataOraFine;


    public PnLegalFactNotificaDowntime(String title, String dataOraDecorrenza, String dataOraFine) {
        this.title = title;
        this.dataOraDecorrenza = dataOraDecorrenza;
        this.dataOraFine = dataOraFine;
    }

    @Override
    public PnParserRecord.PnParserFieldValues getAllLegalFactValues() {
        Map<LegalFactField, String> legalFactValues = new HashMap<>();
        legalFactValues.put(LegalFactField.TITLE, title);
        legalFactValues.put(LegalFactField.DATA_ORA_DECORRENZA, dataOraDecorrenza);
        legalFactValues.put(LegalFactField.DATA_ORA_FINE, dataOraFine);
        return new PnParserRecord.PnParserFieldValues(legalFactValues);
    }

    @Override
    public String toString() {
        return "("
                + "dataOraDecorrenza=" + dataOraDecorrenza
                + ", dataOraFine=" + dataOraFine
                + ")";
    }
}