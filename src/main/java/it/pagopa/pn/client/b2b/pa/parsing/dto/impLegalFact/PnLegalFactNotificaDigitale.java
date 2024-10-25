package it.pagopa.pn.client.b2b.pa.parsing.dto.impLegalFact;

import it.pagopa.pn.client.b2b.pa.parsing.dto.implDestinatario.PnDestinatarioDigitale;
import lombok.experimental.SuperBuilder;


@SuperBuilder
public class PnLegalFactNotificaDigitale extends PnLegalFact {
    public PnLegalFactNotificaDigitale(String title,
                                       String iun,
                                       String dataAttestazioneOpponibile,
                                       String nomeCognomeRagioneSociale,
                                       String codiceFiscale,
                                       String domicilioDigitale,
                                       String tipoDomicilioDigitale) {
        super(new PnDestinatarioDigitale(nomeCognomeRagioneSociale, codiceFiscale, domicilioDigitale, tipoDomicilioDigitale), title, iun, dataAttestazioneOpponibile);
    }

    @Override
    public String toString() {
        return super.toString();
    }
}