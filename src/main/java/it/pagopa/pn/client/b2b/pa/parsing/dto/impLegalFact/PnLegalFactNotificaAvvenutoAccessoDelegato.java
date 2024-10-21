package it.pagopa.pn.client.b2b.pa.parsing.dto.impLegalFact;

import it.pagopa.pn.client.b2b.pa.parsing.dto.PnParserRecord;
import it.pagopa.pn.client.b2b.pa.parsing.dto.implDestinatario.PnDestinatario;
import static it.pagopa.pn.client.b2b.pa.parsing.parser.IPnParserLegalFact.*;
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

    public PnLegalFactNotificaAvvenutoAccessoDelegato(String title,
                                                      String iun,
                                                      String data,
                                                      String nomeCognomeRagioneSocialeDestinatario,
                                                      String codiceFiscaleDestinatario,
                                                      String nomeCognomeRagioneSocialeDelegato,
                                                      String codiceFiscaleDelegato) {
        super(nomeCognomeRagioneSocialeDestinatario, codiceFiscaleDestinatario, title, iun, data);
        this.delegato = new PnDestinatario(nomeCognomeRagioneSocialeDelegato, codiceFiscaleDelegato);
    }

    public Map<LegalFactField, String> mapDestinatarioToDelegato(PnParserRecord.PnParserFieldValues toMap) {
        Map<LegalFactField, String> delegatoMap = new HashMap<>();
        toMap.fieldValue().forEach((key, value) -> {
            if(key.equals(LegalFactField.DESTINATARIO_NOME_COGNOME_RAGIONE_SOCIALE)) {
                delegatoMap.put(LegalFactField.DELEGATO_NOME_COGNOME_RAGIONE_SOCIALE, value);
            } else if(key.equals(LegalFactField.DESTINATARIO_CODICE_FISCALE)) {
                delegatoMap.put(LegalFactField.DELEGATO_CODICE_FISCALE, value);
            } else if(key.equals(LegalFactField.DESTINATARIO_DOMICILIO_DIGITALE)) {
                delegatoMap.put(LegalFactField.DELEGATO_DOMICILIO_DIGITALE, value);
            } else if(key.equals(LegalFactField.DESTINATARIO_TIPO_DOMICILIO_DIGITALE)) {
                delegatoMap.put(LegalFactField.DELEGATO_TIPO_DOMICILIO_DIGITALE, value);
            } else if(key.equals(LegalFactField.DESTINATARIO_INDIRIZZO_FISICO)) {
                delegatoMap.put(LegalFactField.DELEGATO_INDIRIZZO_FISICO, value);
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