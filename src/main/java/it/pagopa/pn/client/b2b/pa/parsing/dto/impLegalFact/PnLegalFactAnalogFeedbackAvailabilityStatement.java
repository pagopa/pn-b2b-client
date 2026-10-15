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
public class PnLegalFactAnalogFeedbackAvailabilityStatement implements IPnLegalFact {
    private String title;
    private String iun;
    private String dataAttestazioneOpponibile;
    private String mittente;
    private String cfMittente;

    public PnLegalFactAnalogFeedbackAvailabilityStatement(String title,
                                                          String iun,
                                                          String dataAttestazioneOpponibile,
                                                          String mittente,
                                                          String cfMittente) {
        this.title = title;
        this.iun = iun;
        this.dataAttestazioneOpponibile = dataAttestazioneOpponibile;
        this.mittente = mittente;
        this.cfMittente = cfMittente;
    }

    @Override
    public PnParserRecord.PnParserFieldValues getAllLegalFactValues() {
        Map<LegalFactField, String> legalFactValues = new HashMap<>();
        legalFactValues.put(LegalFactField.TITLE, title);
        legalFactValues.put(LegalFactField.IUN, iun);
        legalFactValues.put(LegalFactField.DATA_ATTESTAZIONE_OPPONIBILE, dataAttestazioneOpponibile);
        legalFactValues.put(LegalFactField.MITTENTE, mittente);
        legalFactValues.put(LegalFactField.CF_MITTENTE, cfMittente);
        return new PnParserRecord.PnParserFieldValues(legalFactValues);
    }
}
