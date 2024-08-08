package it.pagopa.pn.client.b2b.pa.parsing.dto.implDestinatario;

import it.pagopa.pn.client.b2b.pa.parsing.dto.IPnDestinatario;
import it.pagopa.pn.client.b2b.pa.parsing.dto.PnParserRecord;
import static it.pagopa.pn.client.b2b.pa.parsing.parser.IPnParserLegalFact.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


@Setter
@Getter
@SuperBuilder
@NoArgsConstructor
public class PnDestinatario implements IPnDestinatario {
    private String nomeCognomeRagioneSociale;
    private String codiceFiscale;


    public PnDestinatario(String nomeCognomeRagioneSociale,
                          String codiceFiscale) {
        this.nomeCognomeRagioneSociale = nomeCognomeRagioneSociale;
        this.codiceFiscale = codiceFiscale;
    }

    @Override
    public PnParserRecord.PnParserFieldValues getAllDestinatarioValues() {
        Map<LegalFactField, String> destinatarioValues = new HashMap<>();
        destinatarioValues.put(LegalFactField.DESTINATARIO_NOME_COGNOME_RAGIONE_SOCIALE, nomeCognomeRagioneSociale);
        destinatarioValues.put(LegalFactField.DESTINATARIO_CODICE_FISCALE, codiceFiscale);
        return new PnParserRecord.PnParserFieldValues(destinatarioValues);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        PnDestinatario that = (PnDestinatario) object;
        return Objects.equals(nomeCognomeRagioneSociale, that.nomeCognomeRagioneSociale)
                && Objects.equals(codiceFiscale, that.codiceFiscale);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nomeCognomeRagioneSociale, codiceFiscale);
    }

    @Override
    public String toString() {
        return "("
                + "nomeCognomeRagioneSociale=" + nomeCognomeRagioneSociale
                + ", codiceFiscale=" + codiceFiscale
                + ")";
    }
}