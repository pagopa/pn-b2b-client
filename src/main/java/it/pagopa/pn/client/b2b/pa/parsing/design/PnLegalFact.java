package it.pagopa.pn.client.b2b.pa.parsing.design;

import it.pagopa.pn.client.b2b.pa.parsing.service.IPnParserService;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


@ToString
@Setter
@Getter
@SuperBuilder
public abstract class PnLegalFact implements IPnLegalFact {
    private PnDestinatario pnDestinatario;
    private String iun;
    private String dataAttestazioneOpponibile;


    protected PnLegalFact(PnDestinatario pnDestinatario, String iun, String dataAttestazioneOpponibile) {
        this.pnDestinatario = pnDestinatario;
        this.iun = iun;
        this.dataAttestazioneOpponibile = dataAttestazioneOpponibile;
    }

    @Override
    public Map<String, Object> getAllLegalFactValues() {
        Map<String, Object> map = new HashMap<>(pnDestinatario.getAllDestinatarioValues());
        map.put(IPnParserService.LegalFactKeyValues.IUN.getField(), iun);
        map.put(IPnParserService.LegalFactKeyValues.DATA_ATTESTAZIONE_OPPONIBILE.getField(), dataAttestazioneOpponibile);
        return map;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        PnLegalFact that = (PnLegalFact) object;
        return Objects.equals(pnDestinatario, that.pnDestinatario)
                && Objects.equals(iun, that.iun)
                && Objects.equals(dataAttestazioneOpponibile, that.dataAttestazioneOpponibile);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pnDestinatario, iun, dataAttestazioneOpponibile);
    }
}