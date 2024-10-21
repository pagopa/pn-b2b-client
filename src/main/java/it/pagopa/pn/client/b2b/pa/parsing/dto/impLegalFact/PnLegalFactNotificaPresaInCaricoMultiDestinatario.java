package it.pagopa.pn.client.b2b.pa.parsing.dto.impLegalFact;

import it.pagopa.pn.client.b2b.pa.parsing.dto.PnParserRecord;
import it.pagopa.pn.client.b2b.pa.parsing.dto.implDestinatario.PnDestinatarioAnalogico;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


@Getter
@Setter
@SuperBuilder
public class PnLegalFactNotificaPresaInCaricoMultiDestinatario extends PnLegalFactNotificaPresaInCarico {
    private List<PnDestinatarioAnalogico> destinatariAnalogici;


    public PnLegalFactNotificaPresaInCaricoMultiDestinatario(String title,
                                                             String iun,
                                                             String dataAttestazioneOpponibile,
                                                             String nomeCognomeRagioneSociale,
                                                             String codiceFiscale,
                                                             String domicilioDigitale,
                                                             String tipoDomicilioDigitale,
                                                             String indirizzoFisico,
                                                             String mittente,
                                                             String cfMittente) {
        super(title, iun, dataAttestazioneOpponibile, nomeCognomeRagioneSociale, codiceFiscale, domicilioDigitale, tipoDomicilioDigitale, indirizzoFisico, mittente, cfMittente);
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

    @Override
    public PnParserRecord.PnParserFieldValues getAllLegalFactValues() {
        return super.getAllLegalFactValues();
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