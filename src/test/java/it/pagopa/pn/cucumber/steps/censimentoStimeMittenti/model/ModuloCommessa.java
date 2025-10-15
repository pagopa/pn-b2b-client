package it.pagopa.pn.cucumber.steps.censimentoStimeMittenti.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
public class ModuloCommessa {

    private String idEnte;
    private String contractId;

    @JsonProperty("periodo_riferimento")
    private String periodoRiferimento;

    @JsonProperty("last_update")
    private Instant lastUpdate;

    private List<Prodotto> prodotti;

    @Data
    public static class Prodotto {
        private String id;
        private String nome;

        @JsonProperty("valore_totale")
        private int valoreTotale;

        private List<Variante> varianti;
    }

    @Data
    public static class Variante {
        private String codice;
        private String nome;

        @JsonProperty("valore_totale")
        private int valoreTotale;

        private Distribuzione distribuzione;
    }

    @Data
    public static class Distribuzione {
        private List<Regionale> regionale;
    }

    @Data
    public static class Regionale {
        private String regione;
        private int valore;
        private List<String> province;
    }
}

