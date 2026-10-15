package it.pagopa.pn.client.b2b.pa.parsing.dto.impLegalFact;

import it.pagopa.pn.client.b2b.pa.parsing.dto.PnParserRecord;
import it.pagopa.pn.client.b2b.pa.parsing.dto.implDestinatario.PnDestinatarioDigitale;
import static it.pagopa.pn.client.b2b.pa.parsing.parser.IPnParserLegalFact.LegalFactField;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
public class PnLegalFactNotificationCancelled extends PnLegalFact {
    private String mittente;

    public PnLegalFactNotificationCancelled(String title,
                                            String iun,
                                            String dataAttestazioneOpponibile,
                                            String nomeCognomeRagioneSociale,
                                            String codiceFiscale,
                                            String mittente) {
        super(new PnDestinatarioDigitale(nomeCognomeRagioneSociale, codiceFiscale), title, iun, dataAttestazioneOpponibile);
        this.mittente = mittente;
    }

    @Override
    public PnParserRecord.PnParserFieldValues getAllLegalFactValues() {
        PnParserRecord.PnParserFieldValues parserFieldValues = super.getAllLegalFactValues();
        parserFieldValues.fieldValue().put(LegalFactField.MITTENTE, mittente);
        return parserFieldValues;
    }

    @Override
    public String toString() {
        return super.toString() + ", mittente=" + mittente;
    }
}

