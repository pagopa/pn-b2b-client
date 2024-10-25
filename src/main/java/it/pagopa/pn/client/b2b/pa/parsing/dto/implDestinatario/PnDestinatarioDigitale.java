package it.pagopa.pn.client.b2b.pa.parsing.dto.implDestinatario;

import it.pagopa.pn.client.b2b.pa.parsing.dto.PnParserRecord;
import static it.pagopa.pn.client.b2b.pa.parsing.parser.IPnParserLegalFact.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import java.util.Objects;


@Setter
@Getter
@SuperBuilder
@NoArgsConstructor
public class PnDestinatarioDigitale extends PnDestinatario {
    private String domicilioDigitale;
    private String tipoDomicilioDigitale;


    public PnDestinatarioDigitale(String nomeCognomeRagioneSociale,
                                  String codiceFiscale,
                                  String domicilioDigitale,
                                  String tipoDomicilioDigitale) {
        super(nomeCognomeRagioneSociale, codiceFiscale);
        this.domicilioDigitale = domicilioDigitale;
        this.tipoDomicilioDigitale = tipoDomicilioDigitale;
    }

    public PnDestinatarioDigitale(String nomeCognomeRagioneSociale, String codiceFiscale) {
        super(nomeCognomeRagioneSociale, codiceFiscale);
    }

    @Override
    public PnParserRecord.PnParserFieldValues getAllDestinatarioValues() {
        PnParserRecord.PnParserFieldValues parserFieldValues = super.getAllDestinatarioValues();
        parserFieldValues.fieldValue().put(LegalFactField.DESTINATARIO_DOMICILIO_DIGITALE, domicilioDigitale);
        parserFieldValues.fieldValue().put(LegalFactField.DESTINATARIO_TIPO_DOMICILIO_DIGITALE, tipoDomicilioDigitale);
        return parserFieldValues;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        if (!super.equals(object)) return false;
        PnDestinatarioDigitale that = (PnDestinatarioDigitale) object;
        return Objects.equals(domicilioDigitale, that.domicilioDigitale)
                && Objects.equals(tipoDomicilioDigitale, that.tipoDomicilioDigitale);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), domicilioDigitale, tipoDomicilioDigitale);
    }

    @Override
    public String toString() {
        return super.toString()
                + ", domicilioDigitale=" + domicilioDigitale
                + ", tipoDomicilioDigitale=" + tipoDomicilioDigitale
                + ")";
    }
}