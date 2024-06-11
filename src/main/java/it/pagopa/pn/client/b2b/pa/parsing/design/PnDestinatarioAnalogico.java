package it.pagopa.pn.client.b2b.pa.parsing.design;

import it.pagopa.pn.client.b2b.pa.parsing.service.IPnParserService;
import lombok.*;
import lombok.experimental.SuperBuilder;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


@ToString
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
    public Map<String, Object> getAllDestinatarioValues() {
        Map<String, Object> map = new HashMap<>(super.getAllDestinatarioValues());
        map.put(IPnParserService.LegalFactKeyValues.INDIRIZZO_FISICO.getField(), indirizzoFisico);
        return map;
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
}