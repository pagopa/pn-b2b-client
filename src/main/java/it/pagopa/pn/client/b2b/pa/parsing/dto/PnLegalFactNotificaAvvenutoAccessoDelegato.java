package it.pagopa.pn.client.b2b.pa.parsing.dto;

import it.pagopa.pn.client.b2b.pa.parsing.model.PnParserRecord;
import it.pagopa.pn.client.b2b.pa.parsing.model.impl.PnDestinatario;
import it.pagopa.pn.client.b2b.pa.parsing.service.IPnParserService;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
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

    public Map<IPnParserService.LegalFactField, String> mapDestinatarioToDelegato(PnParserRecord.PnParserFieldValues toMap) {
        Map<IPnParserService.LegalFactField, String> delegatoMap = new HashMap<>();
        toMap.fieldValue().forEach((key, value) -> {
            if(key.equals(IPnParserService.LegalFactField.DESTINATARIO_NOME_COGNOME_RAGIONE_SOCIALE)) {
                delegatoMap.put(IPnParserService.LegalFactField.DELEGATO_NOME_COGNOME_RAGIONE_SOCIALE, value);
            } else if(key.equals(IPnParserService.LegalFactField.DESTINATARIO_CODICE_FISCALE)) {
                delegatoMap.put(IPnParserService.LegalFactField.DELEGATO_CODICE_FISCALE, value);
            } else if(key.equals(IPnParserService.LegalFactField.DESTINATARIO_DOMICILIO_DIGITALE)) {
                delegatoMap.put(IPnParserService.LegalFactField.DELEGATO_DOMICILIO_DIGITALE, value);
            } else if(key.equals(IPnParserService.LegalFactField.DESTINATARIO_TIPO_DOMICILIO_DIGITALE)) {
                delegatoMap.put(IPnParserService.LegalFactField.DELEGATO_TIPO_DOMICILIO_DIGITALE, value);
            } else if(key.equals(IPnParserService.LegalFactField.DESTINATARIO_INDIRIZZO_FISICO)) {
                delegatoMap.put(IPnParserService.LegalFactField.DELEGATO_INDIRIZZO_FISICO, value);
            }
        });
        return delegatoMap;
    }

    @Override
    public PnParserRecord.PnParserFieldValues getAllLegalFactValues() {
        PnParserRecord.PnParserFieldValues parentParserFieldValues = super.getAllLegalFactValues();
        PnParserRecord.PnParserFieldValues delegatoParserFieldValues = delegato.getAllDestinatarioValues();
        parentParserFieldValues.fieldValue().putAll(mapDestinatarioToDelegato(delegatoParserFieldValues));
        return parentParserFieldValues;
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