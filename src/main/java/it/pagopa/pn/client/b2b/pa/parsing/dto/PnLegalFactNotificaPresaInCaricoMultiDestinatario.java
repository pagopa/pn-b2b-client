package it.pagopa.pn.client.b2b.pa.parsing.dto;

import it.pagopa.pn.client.b2b.pa.parsing.design.PnDestinatarioAnalogico;
import it.pagopa.pn.client.b2b.pa.parsing.service.IPnParserService;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import java.util.*;


@Getter
@Setter
@SuperBuilder
public class PnLegalFactNotificaPresaInCaricoMultiDestinatario extends PnLegalFactNotificaPresaInCarico {
    private List<PnDestinatarioAnalogico> destinatariAnalogici;


    public PnLegalFactNotificaPresaInCaricoMultiDestinatario(String iun,
                                                             String dataAttestazioneOpponibile,
                                                             String nomeCognomeRagioneSociale,
                                                             String codiceFiscale,
                                                             String domicilioDigitale,
                                                             String tipoDomicilioDigitale,
                                                             String indirizzoFisico,
                                                             String mittente,
                                                             String cfMittente) {
        super(iun, dataAttestazioneOpponibile, nomeCognomeRagioneSociale, codiceFiscale, domicilioDigitale, tipoDomicilioDigitale, indirizzoFisico, mittente, cfMittente);
        this.destinatariAnalogici = new ArrayList<>();
    }

    public void addDestinatario(String nomeCognomeRagioneSocialeDestinatario,
                                String codiceFiscaleDestinatario,
                                String domicilioDigitale,
                                String tipoDomicilioDigitale,
                                String indirizzoFisico) {
        PnDestinatarioAnalogico pnDestinatarioAnalogico = new PnDestinatarioAnalogico(nomeCognomeRagioneSocialeDestinatario,
                codiceFiscaleDestinatario,
                domicilioDigitale,
                tipoDomicilioDigitale,
                indirizzoFisico);
        this.destinatariAnalogici.add(pnDestinatarioAnalogico);
    }

    public Map<String, Object> getDestinatariAnalogiciValues() {
        Map<String, Object> outputMap = new HashMap<>();
        mappingDestinatariAnalogici(outputMap);
        return outputMap;
    }

    @Override
    public Map<String, Object> getAllLegalFactValues() {
        Map<String, Object> outputMap = new HashMap<>(super.getAllLegalFactValues());
        mappingDestinatariAnalogici(outputMap);
        return outputMap;
    }

    private void  mappingDestinatariAnalogici(Map<String, Object> toMap) {
        for(int i = 0; i < destinatariAnalogici.size(); i++) {
            Map<String, Object> destinatariMap = new HashMap<>(destinatariAnalogici.get(i).getAllDestinatarioValues());
            int index = i + 1;
            destinatariMap
                    .keySet()
                    .forEach(key ->  toMap.put(IPnParserService.LegalFactKeyValues.DESTINATARIO.getField() + "_" + index + "." + key , destinatariMap.get(key)));
        }
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        if (!super.equals(object)) return false;
        PnLegalFactNotificaPresaInCaricoMultiDestinatario that = (PnLegalFactNotificaPresaInCaricoMultiDestinatario) object;
        return Objects.equals(destinatariAnalogici, that.destinatariAnalogici);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), destinatariAnalogici);
    }

    @Override
    public String toString() {
        return super.toString()
                + ", destinatariAnalogici=" + destinatariAnalogici.toString()
                + ")";
    }
}