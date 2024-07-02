package it.pagopa.pn.client.b2b.pa.parsing.model.impl;

import it.pagopa.pn.client.b2b.pa.parsing.model.PnParserRecord;
import it.pagopa.pn.client.b2b.pa.parsing.service.IPnParserService;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import java.util.Objects;


@Setter
@Getter
@SuperBuilder
@NoArgsConstructor
public class PnDestinatarioAnalogico extends PnDestinatarioDigitale {
    private String indirizzoFisico;

    public PnDestinatarioAnalogico(String nomeCognomeRagioneSociale,
                                   String codiceFiscale,
                                   String domicilioDigitale,
                                   String tipoDomicilioDigitale,
                                   String indirizzoFisico) {
        super(nomeCognomeRagioneSociale, codiceFiscale, domicilioDigitale, tipoDomicilioDigitale);
        this.indirizzoFisico = indirizzoFisico;
    }

    @Override
    public PnParserRecord.PnParserFieldValues getAllDestinatarioValues() {
        PnParserRecord.PnParserFieldValues parserFieldValues = super.getAllDestinatarioValues();
        parserFieldValues.fieldValue().put(IPnParserService.LegalFactField.DESTINATARIO_INDIRIZZO_FISICO, indirizzoFisico);
        return parserFieldValues;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        if (!super.equals(object)) return false;
        PnDestinatarioAnalogico that = (PnDestinatarioAnalogico) object;
        return Objects.equals(indirizzoFisico, that.indirizzoFisico);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), indirizzoFisico);
    }

    @Override
    public String toString() {
        return super.toString()
                + ", indirizzoFisico=" + indirizzoFisico
                + ")";
    }
}