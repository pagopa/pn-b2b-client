package it.pagopa.pn.client.b2b.pa.parsing.dto;

import it.pagopa.pn.client.b2b.pa.parsing.design.IPnLegalFact;
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
    public Map<String, Object> getAllLegalFactValues() {
        Map<String, Object> map = new HashMap<>();
        map.put(IPnParserService.LegalFactKeyValues.DATA_ORA_DECORRENZA.getField(), dataOraDecorrenza);
        map.put(IPnParserService.LegalFactKeyValues.DATA_ORA_FINE.getField(), dataOraFine);
        return map;
    }

    @Override
    public String toString() {
        return "("
                + "dataOraDecorrenza=" + dataOraDecorrenza
                + ", dataOraFine=" + dataOraFine
                + ")";
    }
}