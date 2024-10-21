package it.pagopa.pn.client.b2b.pa.parsing.dto.impLegalFact;

import it.pagopa.pn.client.b2b.pa.parsing.dto.PnParserRecord;
import it.pagopa.pn.client.b2b.pa.parsing.dto.implDestinatario.PnDestinatarioDigitale;
import static it.pagopa.pn.client.b2b.pa.parsing.parser.IPnParserLegalFact.*;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import java.util.Objects;


@Getter
@Setter
@SuperBuilder
public class PnLegalFactNotificaMancatoRecapito extends PnLegalFact {
    private String primaData;
    private String secondaData;

    public PnLegalFactNotificaMancatoRecapito(String title,
                                              String iun,
                                              String dataAttestazioneOpponibile,
                                              String nomeCognomeRagioneSociale,
                                              String codiceFiscale,
                                              String domicilioDigitale,
                                              String tipoDomicilioDigitale,
                                              String primaData,
                                              String secondaData) {
        super(new PnDestinatarioDigitale(nomeCognomeRagioneSociale, codiceFiscale,domicilioDigitale, tipoDomicilioDigitale), title, iun, dataAttestazioneOpponibile);
        this.primaData = primaData;
        this.secondaData = secondaData;
    }

    @Override
    public PnParserRecord.PnParserFieldValues getAllLegalFactValues() {
        PnParserRecord.PnParserFieldValues parserFieldValues = super.getAllLegalFactValues();
        parserFieldValues.fieldValue().put(LegalFactField.PRIMA_DATA, primaData);
        parserFieldValues.fieldValue().put(LegalFactField.SECONDA_DATA, secondaData);
        return parserFieldValues;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        if (!super.equals(object)) return false;
        PnLegalFactNotificaMancatoRecapito that = (PnLegalFactNotificaMancatoRecapito) object;
        return Objects.equals(primaData, that.primaData) && Objects.equals(secondaData, that.secondaData);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), primaData, secondaData);
    }

    @Override
    public String toString() {
        return "(" + superFieldsFromToString()
                + ", primaData=" + primaData
                + ", secondaData=" + secondaData
                + ")";
    }

    private String superFieldsFromToString() {
        String superToString = super.toString();
        int superClassNameLength = getClass().getSuperclass().getSimpleName().length();
        int fieldStartIdx = superClassNameLength + 1;
        int fieldEndIdx = superToString.length() - 1;
        return superToString.substring(fieldStartIdx , fieldEndIdx);
    }
}