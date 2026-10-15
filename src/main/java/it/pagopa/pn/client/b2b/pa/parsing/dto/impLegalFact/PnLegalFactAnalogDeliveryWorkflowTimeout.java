package it.pagopa.pn.client.b2b.pa.parsing.dto.impLegalFact;

import it.pagopa.pn.client.b2b.pa.parsing.dto.IPnLegalFact;
import it.pagopa.pn.client.b2b.pa.parsing.dto.PnParserRecord;
import it.pagopa.pn.client.b2b.pa.parsing.parser.IPnParserLegalFact.LegalFactField;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@Builder
@ToString
public class PnLegalFactAnalogDeliveryWorkflowTimeout implements IPnLegalFact {
    private String title;
    private String iun;
    private String destinatarioNomeCognomeRagioneSociale;
    private String destinatarioCodiceFiscale;
    private String dataOraDecorrenza;

    public PnLegalFactAnalogDeliveryWorkflowTimeout(String title,
                                                   String iun,
                                                   String destinatarioNomeCognomeRagioneSociale,
                                                   String destinatarioCodiceFiscale,
                                                   String dataOraDecorrenza) {
        this.title = title;
        this.iun = iun;
        this.destinatarioNomeCognomeRagioneSociale = destinatarioNomeCognomeRagioneSociale;
        this.destinatarioCodiceFiscale = destinatarioCodiceFiscale;
        this.dataOraDecorrenza = dataOraDecorrenza;
    }

    @Override
    public PnParserRecord.PnParserFieldValues getAllLegalFactValues() {
        Map<LegalFactField, String> legalFactValues = new HashMap<>();
        legalFactValues.put(LegalFactField.TITLE, title);
        legalFactValues.put(LegalFactField.IUN, iun);
        legalFactValues.put(LegalFactField.DESTINATARIO_NOME_COGNOME_RAGIONE_SOCIALE, destinatarioNomeCognomeRagioneSociale);
        legalFactValues.put(LegalFactField.DESTINATARIO_CODICE_FISCALE, destinatarioCodiceFiscale);
        legalFactValues.put(LegalFactField.DATA_ORA_DECORRENZA, dataOraDecorrenza);
        return new PnParserRecord.PnParserFieldValues(legalFactValues);
    }
}
