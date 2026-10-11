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
public class PnLegalFactInformalAnalogCommunication implements IPnLegalFact {
    private String title;
    private String iun;
    private String destinatarioCodiceFiscale;
    private String mittente;
    private String destinatarioNomeCognomeRagioneSociale;

    public PnLegalFactInformalAnalogCommunication(String title,
                                                 String iun,
                                                 String destinatarioCodiceFiscale,
                                                 String mittente,
                                                 String destinatarioNomeCognomeRagioneSociale) {
        this.title = title;
        this.iun = iun;
        this.destinatarioCodiceFiscale = destinatarioCodiceFiscale;
        this.mittente = mittente;
        this.destinatarioNomeCognomeRagioneSociale = destinatarioNomeCognomeRagioneSociale;
    }

    @Override
    public PnParserRecord.PnParserFieldValues getAllLegalFactValues() {
        Map<LegalFactField, String> legalFactValues = new HashMap<>();
        legalFactValues.put(LegalFactField.TITLE, title);
        legalFactValues.put(LegalFactField.IUN, iun);
        legalFactValues.put(LegalFactField.DESTINATARIO_CODICE_FISCALE, destinatarioCodiceFiscale);
        legalFactValues.put(LegalFactField.MITTENTE, mittente);
        legalFactValues.put(LegalFactField.DESTINATARIO_NOME_COGNOME_RAGIONE_SOCIALE, destinatarioNomeCognomeRagioneSociale);
        return new PnParserRecord.PnParserFieldValues(legalFactValues);
    }
}
