package it.pagopa.pn.cucumber.steps.censimentoStimeMittenti.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import it.pagopa.pn.cucumber.steps.delayer.model.DelayerSenderLimit;
import lombok.Data;

import java.time.Instant;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static it.pagopa.pn.cucumber.steps.delayer.utils.DelayerSenderLimitUtils.countDaysInMonthWeek;

@Data
public class ModuloCommessa {

    private String idEnte;
    private String contractId;

    @JsonProperty("periodo_riferimento")
    private String periodoRiferimento;

    @JsonProperty("last_update")
    private Instant lastUpdate;

    private List<Prodotto> prodotti;

    public List<DelayerSenderLimit> generateSenderLimits(List<LocalDate> mondays, String provincia) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M-yyyy");
        YearMonth yearMonth = YearMonth.parse(this.periodoRiferimento, formatter);
        int giorniMeseTarget = yearMonth.lengthOfMonth();

        List<DelayerSenderLimit> results = new ArrayList<>();

        for (LocalDate lunedi : mondays) {
            int giorniValidi = countDaysInMonthWeek(lunedi, yearMonth);
            if (giorniValidi == 0) continue;

            this.prodotti.stream()
                    .filter(p ->
                            // escludi quelli con id "digitale"
                            !"digitale".equalsIgnoreCase(p.getId())
                                    // e controlla che esista almeno una variante con codice "NZ" e valoreTotale > 0
                                    && p.varianti.stream()
                                    .anyMatch(v -> "NZ".equals(v.codice) && v.valoreTotale > 0)
                    )
                    .forEach(prodotto -> {
                        int valoreTotale = prodotto.getValoreTotale();
                        double valorePerGiorno = (double) valoreTotale / giorniMeseTarget;
                        int weeklyEstimate = (int) Math.round(valorePerGiorno * giorniValidi);
                        DelayerSenderLimit limit = new DelayerSenderLimit();
                        limit.setProductType(prodotto.getId());
                        limit.setDeliveryDate(lunedi.toString());
                        limit.setMonthlyEstimate(valoreTotale);
                        limit.setOriginalEstimate(valoreTotale);
                        limit.setWeeklyEstimate(weeklyEstimate);
                        limit.setPaId(this.idEnte);
                        limit.setPk(this.idEnte + "~" + prodotto.getId() + "~" + provincia) ;

                        results.add(limit);
                    });
        }

        return results;
    }

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

