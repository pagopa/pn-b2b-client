package it.pagopa.pn.client.b2b.pa.parsing.dto.implDestinatario;

import it.pagopa.pn.client.b2b.pa.parsing.dto.PnParserRecord;
import it.pagopa.pn.client.b2b.pa.parsing.parser.IPnParserLegalFact;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import java.util.*;


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
        parserFieldValues.fieldValue().put(IPnParserLegalFact.LegalFactField.DESTINATARIO_INDIRIZZO_FISICO, indirizzoFisico);
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

    public static List<PnDestinatarioAnalogico> mapToDestinatarioAnalogico(List<Map<String, String>> listOfMap) {
        List<PnDestinatarioAnalogico> destinatari = new ArrayList<>();
        PnDestinatarioAnalogico destinatarioAnalogico = null;
        Set<String> processedKeys = new HashSet<>();

        for (Map<String, String> map : listOfMap) {
            for (Map.Entry<String, String> entry : map.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();

                if (isDestinatarioKey(key)) {
                    if (processedKeys.contains(key)) {
                        destinatari.add(destinatarioAnalogico);
                        destinatarioAnalogico = null;
                        processedKeys.clear();
                    }
                    processedKeys.add(key);

                    if (destinatarioAnalogico == null) {
                        destinatarioAnalogico = new PnDestinatarioAnalogico();
                    }
                    if(key.equals(IPnParserLegalFact.LegalFactField.DESTINATARIO_NOME_COGNOME_RAGIONE_SOCIALE.name())) {
                        destinatarioAnalogico.setNomeCognomeRagioneSociale(value);
                    } else if(key.equals(IPnParserLegalFact.LegalFactField.DESTINATARIO_CODICE_FISCALE.name())) {
                        destinatarioAnalogico.setCodiceFiscale(value);
                    } else if(key.equals(IPnParserLegalFact.LegalFactField.DESTINATARIO_DOMICILIO_DIGITALE.name())) {
                        destinatarioAnalogico.setDomicilioDigitale(value);
                    } else if(key.equals(IPnParserLegalFact.LegalFactField.DESTINATARIO_TIPO_DOMICILIO_DIGITALE.name())) {
                        destinatarioAnalogico.setTipoDomicilioDigitale(value);
                    } else if(key.equals(IPnParserLegalFact.LegalFactField.DESTINATARIO_INDIRIZZO_FISICO.name())) {
                        destinatarioAnalogico.setIndirizzoFisico(value);
                    }
                }
            }
        }

        if (destinatarioAnalogico != null) {
            destinatari.add(destinatarioAnalogico);
        }

        return destinatari;
    }

    private static boolean isDestinatarioKey(String key) {
        return key.equals(IPnParserLegalFact.LegalFactField.DESTINATARIO_NOME_COGNOME_RAGIONE_SOCIALE.name()) ||
                key.equals(IPnParserLegalFact.LegalFactField.DESTINATARIO_CODICE_FISCALE.name()) ||
                key.equals(IPnParserLegalFact.LegalFactField.DESTINATARIO_DOMICILIO_DIGITALE.name()) ||
                key.equals(IPnParserLegalFact.LegalFactField.DESTINATARIO_TIPO_DOMICILIO_DIGITALE.name()) ||
                key.equals(IPnParserLegalFact.LegalFactField.DESTINATARIO_INDIRIZZO_FISICO.name());
    }
}