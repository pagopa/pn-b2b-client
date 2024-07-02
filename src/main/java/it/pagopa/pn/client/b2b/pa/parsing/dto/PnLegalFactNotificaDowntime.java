package it.pagopa.pn.client.b2b.pa.parsing.dto;

import it.pagopa.pn.client.b2b.pa.parsing.model.IPnLegalFact;
import it.pagopa.pn.client.b2b.pa.parsing.model.PnParserRecord;
import it.pagopa.pn.client.b2b.pa.parsing.service.IPnParserService;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import java.util.HashMap;
import java.util.Map;


@Getter
@Setter
@Builder
public class PnLegalFactNotificaDowntime implements IPnLegalFact {
    private String dataOraDecorrenza;
    private String dataOraFine;


    public PnLegalFactNotificaDowntime(String dataOraDecorrenza, String dataOraFine) {
        this.dataOraDecorrenza = dataOraDecorrenza;
        this.dataOraFine = dataOraFine;
    }

    @Override
    public PnParserRecord.PnParserFieldValues getAllLegalFactValues() {
        Map<IPnParserService.LegalFactField, String> legalFactValues = new HashMap<>();
        legalFactValues.put(IPnParserService.LegalFactField.DATA_ORA_DECORRENZA, dataOraDecorrenza);
        legalFactValues.put(IPnParserService.LegalFactField.DATA_ORA_FINE, dataOraFine);
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