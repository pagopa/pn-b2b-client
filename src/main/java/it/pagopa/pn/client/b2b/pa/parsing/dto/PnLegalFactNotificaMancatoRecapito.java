package it.pagopa.pn.client.b2b.pa.parsing.dto;

import it.pagopa.pn.client.b2b.pa.parsing.design.PnDestinatarioDigitale;
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
public class PnLegalFactNotificaMancatoRecapito extends PnLegalFact {
    private PnDestinatarioDigitale secondoDestinatarioDigitale;
    private String primaData;
    private String secondaData;

    public PnLegalFactNotificaMancatoRecapito(String iun,
                                              String dataAttestazioneOpponibile,
                                              String nomeCognomeRagioneSociale,
                                              String codiceFiscale,
                                              String domicilioDigitale,
                                              String tipoDomicilioDigitale,
                                              String primaData,
                                              String secondaData) {
        super(new PnDestinatarioDigitale(nomeCognomeRagioneSociale, codiceFiscale,domicilioDigitale, tipoDomicilioDigitale), iun, dataAttestazioneOpponibile);
        this.primaData = primaData;
        this.secondaData = secondaData;
    }

    public Map<String, Object> getSecondoDestinatarioDigitaleValues() {
        Map<String, Object> map = new HashMap<>();
        map.put(IPnParserService.LegalFactKeyValues.SECONDO_DESTINATARIO_DIGITALE.getField() + "." + IPnParserService.LegalFactKeyValues.CODICE_FISCALE.getField(), secondoDestinatarioDigitale.getCodiceFiscale());
        map.put(IPnParserService.LegalFactKeyValues.SECONDO_DESTINATARIO_DIGITALE.getField() + "." + IPnParserService.LegalFactKeyValues.NOME_COGNOME_RAGIONE_SOCIALE.getField(), secondoDestinatarioDigitale.getNomeCognomeRagioneSociale());
        map.put(IPnParserService.LegalFactKeyValues.SECONDO_DESTINATARIO_DIGITALE.getField() + "." + IPnParserService.LegalFactKeyValues.DOMICILIO_DIGITALE.getField(), secondoDestinatarioDigitale.getDomicilioDigitale());
        map.put(IPnParserService.LegalFactKeyValues.SECONDO_DESTINATARIO_DIGITALE.getField() + "." + IPnParserService.LegalFactKeyValues.TIPO_DOMICILIO_DIGITALE.getField(), secondoDestinatarioDigitale.getTipoDomicilioDigitale());
        return map;
    }

    @Override
    public Map<String, Object> getAllLegalFactValues() {
        Map<String, Object> map = new HashMap<>(super.getAllLegalFactValues());
        map.putAll(secondoDestinatarioDigitale.getAllDestinatarioValues());
        map.put(IPnParserService.LegalFactKeyValues.PRIMA_DATA.getField(), primaData);
        map.put(IPnParserService.LegalFactKeyValues.SECONDA_DATA.getField(), secondaData);
        return map;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        if (!super.equals(object)) return false;
        PnLegalFactNotificaMancatoRecapito that = (PnLegalFactNotificaMancatoRecapito) object;
        return Objects.equals(secondoDestinatarioDigitale, that.secondoDestinatarioDigitale) && Objects.equals(primaData, that.primaData) && Objects.equals(secondaData, that.secondaData);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), secondoDestinatarioDigitale, primaData, secondaData);
    }

    @Override
    public String toString() {
        return "(" + superFieldsFromToString()
                + ", secondoDestinatarioDigitale=" + secondoDestinatarioDigitale.toString()
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