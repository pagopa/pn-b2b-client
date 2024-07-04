package it.pagopa.pn.client.b2b.pa.parsing.dto;

import it.pagopa.pn.client.b2b.pa.parsing.model.PnParserRecord;
import it.pagopa.pn.client.b2b.pa.parsing.model.impl.PnDestinatarioAnalogico;
import it.pagopa.pn.client.b2b.pa.parsing.model.impl.PnLegalFact;
import it.pagopa.pn.client.b2b.pa.parsing.service.IPnParserService;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import java.util.Objects;


@Getter
@Setter
@SuperBuilder
public class PnLegalFactNotificaPresaInCarico extends PnLegalFact {
    private String mittente;
    private String cfMittente;


    public PnLegalFactNotificaPresaInCarico(String title,
                                            String iun,
                                            String dataAttestazioneOpponibile,
                                            String nomeCognomeRagioneSociale,
                                            String codiceFiscale,
                                            String domicilioDigitale,
                                            String tipoDomicilioDigitale,
                                            String indirizzoFisico,
                                            String mittente,
                                            String cfMittente) {
        super(new PnDestinatarioAnalogico(nomeCognomeRagioneSociale, codiceFiscale, domicilioDigitale, tipoDomicilioDigitale, indirizzoFisico), title, iun, dataAttestazioneOpponibile);
        this.mittente = mittente;
        this.cfMittente = cfMittente;
    }

    @Override
    public PnParserRecord.PnParserFieldValues getAllLegalFactValues() {
        PnParserRecord.PnParserFieldValues parserFieldValues = super.getAllLegalFactValues();
        parserFieldValues.fieldValue().put(IPnParserService.LegalFactField.MITTENTE, mittente);
        parserFieldValues.fieldValue().put(IPnParserService.LegalFactField.CF_MITTENTE, cfMittente);
        return parserFieldValues;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        if (!super.equals(object)) return false;
        PnLegalFactNotificaPresaInCarico that = (PnLegalFactNotificaPresaInCarico) object;
        return Objects.equals(mittente, that.mittente)
                && Objects.equals(cfMittente, that.cfMittente);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), mittente, cfMittente);
    }

    @Override
    public String toString() {
        return "(" + superFieldsFromToString()
                + ", mittente=" + mittente
                + ", cfMittente=" + cfMittente
                + ")";
    }

    private String superFieldsFromToString() {
        String superToString = super.toString();
        int fieldStartIdx = 0;
        int fieldEndIdx = superToString.length() - 1;
        return superToString.substring(fieldStartIdx , fieldEndIdx);
    }
}