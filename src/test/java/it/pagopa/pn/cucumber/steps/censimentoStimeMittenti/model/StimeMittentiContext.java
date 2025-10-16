package it.pagopa.pn.cucumber.steps.censimentoStimeMittenti.model;

import it.pagopa.pn.cucumber.steps.delayer.model.DelayerSenderLimit;
import it.pagopa.pn.cucumber.steps.delayer.utils.DelayerSenderLimitUtils;
import lombok.Getter;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class StimeMittentiContext {

    public class SenderLimitsForPeriod {
        public List<DelayerSenderLimit> senderLimits;

        private void merge(List<DelayerSenderLimit> other) {
            if (other == null || other.isEmpty()) {
                return;
            }

            Map<String, DelayerSenderLimit> merged = new LinkedHashMap<>();

            // 1. inserisci i record già presenti
            for (DelayerSenderLimit override : this.senderLimits) {
                String key = buildKey(override);
                merged.put(key, new DelayerSenderLimit(override));
            }

            // 2. aggiungi/aggiorna con i nuovi
            for (DelayerSenderLimit override : other) {
                String key = buildKey(override);

                if (merged.containsKey(key)) {
                    LocalDate deliveryDate = LocalDate.parse(override.getDeliveryDate());
                    YearMonth ym = YearMonth.from(deliveryDate);

                    // recupera la commessa corretta per il mese
                    ModuloCommessa mc = getCommessaByPeriodoRiferimento(ym.toString())
                            .orElseThrow(() -> new NoSuchElementException(
                                    "Nessun ModuloCommessa trovata per periodoRiferimento=" + ym));

                    // rigenera i limiti solo per quel lunedì
                    int weeklyEstimateToOverride = mc.generateSenderLimits(List.of(deliveryDate), province).stream()
                            .filter(l -> l.getProductType().equals(override.getProductType()))
                            .findFirst()
                            .orElseThrow(() -> new NoSuchElementException(
                                    "Nessun DelayerSenderLimit trovato per prodotto=" + override.getProductType()
                                            + " e data=" + deliveryDate))
                            .getWeeklyEstimate();

                    // calcola nuovo valore
                    DelayerSenderLimit existing = merged.get(key);
                    int newWeeklyEstimate = existing.getWeeklyEstimate()
                            - weeklyEstimateToOverride
                            + override.getWeeklyEstimate();

                    existing.setWeeklyEstimate(newWeeklyEstimate);

                } else {
                    merged.put(key, new DelayerSenderLimit(override));
                }
            }

            this.senderLimits = new ArrayList<>(merged.values());
        }

        private String buildKey(DelayerSenderLimit limit) {
            return limit.getDeliveryDate() + "::" + limit.getPk();
        }
    }

    public YearMonth da;
    public YearMonth a;
    public String province;
    public SenderLimitsForPeriod actual = new SenderLimitsForPeriod();
    public SenderLimitsForPeriod expected = new SenderLimitsForPeriod();
    @Getter private List<ModuloCommessa> sortedCommesseCaricate = new ArrayList<>();

    public void applyCommesseInExpected(String provincia, ModuloCommessa... commesse) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M-yyyy");

        // 1. Unisci commesse esistenti e nuove in una lista di lavoro
        List<ModuloCommessa> commesseOrdinate = new ArrayList<>(this.sortedCommesseCaricate);
        commesseOrdinate.addAll(Arrays.asList(commesse));

        // 2. Ordina le commesse per periodoRiferimento
        commesseOrdinate.sort(Comparator.comparing(
                c -> YearMonth.parse(c.getPeriodoRiferimento(), formatter)
        ));

        // 3. Estrai YearMonth
        List<YearMonth> months = commesseOrdinate.stream()
                .map(c -> YearMonth.parse(c.getPeriodoRiferimento(), formatter))
                .toList();

        YearMonth minMonth = months.get(0);
        YearMonth maxMonth = months.get(months.size() - 1);

        // 4. Verifica contiguità
        YearMonth cursor = minMonth;
        for (int i = 1; i < months.size(); i++) {
            cursor = cursor.plusMonths(1);
            if (!cursor.equals(months.get(i))) {
                throw new IllegalArgumentException("I mesi delle commesse non sono contigui: trovato buco tra "
                        + cursor.minusMonths(1) + " e " + months.get(i));
            }
        }

        // 5. Genera nuovi SenderLimits attesi
        List<DelayerSenderLimit> newExpectedLimits = commesseOrdinate.stream()
                .flatMap(c -> c.generateSenderLimits(
                        DelayerSenderLimitUtils.getMondaysBetween(minMonth, maxMonth, false, false), provincia
                ).stream())
                .toList();

        // 6. Tutto OK → aggiorno lo stato dell’oggetto
        this.sortedCommesseCaricate = commesseOrdinate;
        this.da = minMonth;
        this.a = maxMonth;
        this.province = provincia;
        this.expected.merge(newExpectedLimits);
    }

    public Optional<ModuloCommessa> getCommessaByPeriodoRiferimento(String periodoRiferimento) {
        return this.sortedCommesseCaricate.stream()
                .filter(c -> periodoRiferimento.equals(c.getPeriodoRiferimento()))
                .findFirst();
    }

}
