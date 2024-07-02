package it.pagopa.pn.client.b2b.pa.parsing.dto;

import it.pagopa.pn.client.b2b.pa.parsing.model.impl.PnDestinatarioDigitale;
import it.pagopa.pn.client.b2b.pa.parsing.model.impl.PnLegalFact;
import lombok.experimental.SuperBuilder;


@SuperBuilder
public class PnLegalFactNotificaDigitale extends PnLegalFact {
    public PnLegalFactNotificaDigitale(String iun,
                                       String dataAttestazioneOpponibile,
                                       String nomeCognomeRagioneSociale,
                                       String codiceFiscale,
                                       String domicilioDigitale,
                                       String tipoDomicilioDigitale) {
        super(new PnDestinatarioDigitale(nomeCognomeRagioneSociale, codiceFiscale, domicilioDigitale, tipoDomicilioDigitale), iun, dataAttestazioneOpponibile);
    }

    @Override
    public String toString() {
        return super.toString();
    }
}