package it.pagopa.pn.client.b2b.pa.parsing.dto.impLegalFact;

import it.pagopa.pn.client.b2b.pa.parsing.dto.implDestinatario.PnDestinatarioDigitale;
import lombok.experimental.SuperBuilder;


@SuperBuilder
public class PnLegalFactNotificaAvvenutoAccesso extends PnLegalFact {
    public PnLegalFactNotificaAvvenutoAccesso(String title,
                                              String iun,
                                              String dataAttestazioneOpponibile,
                                              String nomeCognomeRagioneSociale,
                                              String codiceFiscale) {
        super(new PnDestinatarioDigitale(nomeCognomeRagioneSociale, codiceFiscale), title, iun, dataAttestazioneOpponibile);
    }

    @Override
    public String toString() {
        return super.toString();
    }
}