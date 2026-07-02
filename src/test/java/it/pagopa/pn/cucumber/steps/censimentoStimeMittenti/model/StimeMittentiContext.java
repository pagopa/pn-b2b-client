package it.pagopa.pn.cucumber.steps.censimentoStimeMittenti.model;

import it.pagopa.pn.cucumber.steps.delayer.model.DelayerSenderLimit;
import it.pagopa.pn.cucumber.steps.delayer.utils.DelayerSenderLimitUtils;
import lombok.Getter;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class StimeMittentiContext {

    public static class SenderLimitsForPeriod {
        public List<DelayerSenderLimit> senderLimits = new ArrayList<>();

        private void merge(List<DelayerSenderLimit> limits) {
            if (limits == null || limits.isEmpty()) {
                return;
            }

            Map<String, DelayerSenderLimit> merged = new LinkedHashMap<>();

            // 2. aggiungi/aggiorna con i nuovi
            for (DelayerSenderLimit limit : limits) {
                String key = buildKey(limit);

                if (merged.containsKey(key)) {
                    DelayerSenderLimit overlapLimit = merged.get(key);
                    overlapLimit.setWeeklyEstimate(overlapLimit.getWeeklyEstimate() + limit.getWeeklyEstimate());
                } else {
                    merged.put(key, new DelayerSenderLimit(limit));
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
    @Getter private List<ModuloCommessa> sortedCommesseDaApplicare = new ArrayList<>();

    public void applyCommesseInExpected(String provincia, ModuloCommessa... commesseDaApplicare) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M-yyyy");

        // 1. Unisci le nuove commesse in una lista di lavoro
        List<ModuloCommessa> sortedCommesseDaApplicare = new ArrayList<>(this.sortedCommesseDaApplicare);
        sortedCommesseDaApplicare.addAll(Arrays.asList(commesseDaApplicare));

        // 2. Ordina le commesse prima per periodoRiferimento, poi per lastUpdate
        sortedCommesseDaApplicare.sort(
                Comparator.comparing(
                        (ModuloCommessa c) -> YearMonth.parse(c.getPeriodoRiferimento(), formatter)
                ).thenComparing(ModuloCommessa::getLastUpdate)
        );
        this.sortedCommesseDaApplicare = sortedCommesseDaApplicare;

        // 3. Estrai YearMonth
        List<YearMonth> months = sortedCommesseDaApplicare.stream()
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
        List<DelayerSenderLimit> newExpectedLimits = sortedCommesseDaApplicare.stream()
                .flatMap(c -> c.generateSenderLimits(
                        DelayerSenderLimitUtils.getMondaysBetween(minMonth, maxMonth, false, false), provincia
                ).stream())
                .toList();

        // 6. Tutto OK → aggiorno lo stato dell’oggetto
        this.expected.merge(newExpectedLimits);
        this.sortedCommesseDaApplicare.clear();
        this.da = minMonth;
        this.a = maxMonth;
        this.province = provincia;
    }
}
