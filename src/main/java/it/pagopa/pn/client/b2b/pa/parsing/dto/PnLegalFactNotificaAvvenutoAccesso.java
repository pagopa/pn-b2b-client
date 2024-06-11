package it.pagopa.pn.client.b2b.pa.parsing.dto;

import it.pagopa.pn.client.b2b.pa.parsing.design.PnDestinatarioDigitale;
import it.pagopa.pn.client.b2b.pa.parsing.design.PnLegalFact;
import lombok.experimental.SuperBuilder;


@SuperBuilder
public class PnLegalFactNotificaAvvenutoAccesso extends PnLegalFact {
    public PnLegalFactNotificaAvvenutoAccesso(String iun,
                                              String dataAttestazioneOpponibile,
                                              String nomeCognomeRagioneSociale,
                                              String codiceFiscale) {
        super(new PnDestinatarioDigitale(nomeCognomeRagioneSociale, codiceFiscale),iun, dataAttestazioneOpponibile);
    }

    @Override
    public String toString() {
        return super.toString();
    }
}