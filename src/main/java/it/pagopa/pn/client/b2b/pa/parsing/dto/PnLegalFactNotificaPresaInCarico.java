package it.pagopa.pn.client.b2b.pa.parsing.dto;

import it.pagopa.pn.client.b2b.pa.parsing.design.PnDestinatarioAnalogico;
import it.pagopa.pn.client.b2b.pa.parsing.design.PnLegalFact;
import it.pagopa.pn.client.b2b.pa.parsing.service.IPnParserService;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


@Getter
@Setter
@SuperBuilder
public class PnLegalFactNotificaPresaInCarico extends PnLegalFact {
    private String mittente;
    private String cfMittente;


    public PnLegalFactNotificaPresaInCarico(String iun,
                                            String dataAttestazioneOpponibile,
                                            String nomeCognomeRagioneSociale,
                                            String codiceFiscale,
                                            String domicilioDigitale,
                                            String tipoDomicilioDigitale,
                                            String indirizzoFisico,
                                            String mittente,
                                            String cfMittente) {
        super(new PnDestinatarioAnalogico(nomeCognomeRagioneSociale, codiceFiscale, domicilioDigitale, tipoDomicilioDigitale, indirizzoFisico), iun, dataAttestazioneOpponibile);
        this.mittente = mittente;
        this.cfMittente = cfMittente;
    }

    @Override
    public Map<String, Object> getAllLegalFactValues() {
        Map<String, Object> map = new HashMap<>(super.getAllLegalFactValues());
        map.put(IPnParserService.LegalFactKeyValues.MITTENTE.getField(), mittente);
        map.put(IPnParserService.LegalFactKeyValues.CF_MITTENTE.getField(), cfMittente);
        return map;
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