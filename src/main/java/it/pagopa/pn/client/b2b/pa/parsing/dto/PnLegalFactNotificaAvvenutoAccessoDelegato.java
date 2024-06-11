package it.pagopa.pn.client.b2b.pa.parsing.dto;

import it.pagopa.pn.client.b2b.pa.parsing.design.PnDestinatario;
import it.pagopa.pn.client.b2b.pa.parsing.service.IPnParserService;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.apache.commons.lang3.tuple.Pair;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


@Setter
@Getter
@SuperBuilder
public class PnLegalFactNotificaAvvenutoAccessoDelegato extends PnLegalFactNotificaAvvenutoAccesso {
    private PnDestinatario delegato;

    public PnLegalFactNotificaAvvenutoAccessoDelegato(String iun,
                                                      String data,
                                                      String nomeCognomeRagioneSocialeDestinatario,
                                                      String codiceFiscaleDestinatario,
                                                      String nomeCognomeRagioneSocialeDelegato,
                                                      String codiceFiscaleDelegato) {
        super(nomeCognomeRagioneSocialeDestinatario, codiceFiscaleDestinatario, iun, data);
        this.delegato = new PnDestinatario(nomeCognomeRagioneSocialeDelegato, codiceFiscaleDelegato);
    }

    public Pair<PnDestinatario, PnDestinatario> getDestinatarioAndDelegato() {
        return Pair.of(super.getPnDestinatario(), delegato);
    }

    @Override
    public Map<String, Object> getAllLegalFactValues() {
        Map<String, Object> map = new HashMap<>(super.getAllLegalFactValues());
        map.put(IPnParserService.LegalFactKeyValues.DELEGATO.getField() + "." + IPnParserService.LegalFactKeyValues.NOME_COGNOME_RAGIONE_SOCIALE, delegato.getNomeCognomeRagioneSociale());
        map.put(IPnParserService.LegalFactKeyValues.DELEGATO.getField() + "." + IPnParserService.LegalFactKeyValues.CODICE_FISCALE, delegato.getCodiceFiscale());
        return map;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        if (!super.equals(object)) return false;
        PnLegalFactNotificaAvvenutoAccessoDelegato that = (PnLegalFactNotificaAvvenutoAccessoDelegato) object;
        return Objects.equals(delegato, that.delegato);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), delegato);
    }

    @Override
    public String toString() {
        return "(" + super.toString().replace(getClass().getSuperclass().getSimpleName(), "")
                + ", delegato=" + delegato.toString()
                + ")";
    }
}