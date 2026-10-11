package it.pagopa.pn.client.b2b.pa.parsing.dto.impLegalFact;

import it.pagopa.pn.client.b2b.pa.parsing.dto.implDestinatario.PnDestinatario;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
public class PnLegalFactAnalogFailure extends PnLegalFact {

    public PnLegalFactAnalogFailure(String title,
                                    String iun,
                                    String dataAttestazioneOpponibile,
                                    String nomeCognomeRagioneSociale,
                                    String codiceFiscale) {
        super(new PnDestinatario(nomeCognomeRagioneSociale, codiceFiscale), title, iun, dataAttestazioneOpponibile);
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
