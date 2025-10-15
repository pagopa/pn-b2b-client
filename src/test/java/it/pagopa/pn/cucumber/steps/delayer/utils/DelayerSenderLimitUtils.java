package it.pagopa.pn.cucumber.steps.delayer.utils;

import it.pagopa.pn.cucumber.steps.censimentoStimeMittenti.model.ModuloCommessa;
import it.pagopa.pn.cucumber.steps.delayer.model.DelayerSenderLimit;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class DelayerSenderLimitUtils {

    public static List<DelayerSenderLimit> calculateSenderLimitByCommessa(ModuloCommessa commessa) {
        List<DelayerSenderLimit> results = new ArrayList<>();

        // 1. Parsing periodo riferimento: MM-YYYY
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M-yyyy");
        YearMonth yearMonth = YearMonth.parse(commessa.getPeriodoRiferimento(), formatter);

        LocalDate start = yearMonth.atDay(1);
        LocalDate end = yearMonth.atEndOfMonth();

        int giorniMeseTarget = yearMonth.lengthOfMonth();

        // 2. Trova i lunedì rilevanti (inclusi quelli a cavallo se intersecano il mese)
        List<LocalDate> mondays = collectMondays(start, end, false, false);

        // 3. Genera DelayerSenderLimit
        for (LocalDate lunedi : mondays) {
            int giorniValidi = countDaysInMonthWeek(lunedi, yearMonth);

            commessa.getProdotti().stream()
                    .filter(p -> !"digitale".equalsIgnoreCase(p.getId()))
                    .forEach(prodotto -> {
                        int valoreTotale = prodotto.getValoreTotale();
                        int valorePerGiorno = valoreTotale / giorniMeseTarget;
                        int weeklyEstimate = valorePerGiorno * giorniValidi;

                        DelayerSenderLimit limit = new DelayerSenderLimit();
                        limit.setProductType(prodotto.getId());
                        limit.setDeliveryDate(lunedi.toString());
                        limit.setMonthlyEstimate(valoreTotale);
                        limit.setWeeklyEstimate(weeklyEstimate);
                        limit.setPaId(commessa.getIdEnte());

                        results.add(limit);
                    });
        }

        return results;
    }

    /**
     * Restituisce tutti i lunedì compresi tra due YearMonth, con opzioni per includere o escludere i lunedì a cavallo.
     *
     * @param from                mese iniziale
     * @param to                  mese finale
     * @param includeStartOverlap true se vuoi includere il lunedì a cavallo col mese precedente al "from"
     * @param includeEndOverlap   true se vuoi includere il lunedì a cavallo col mese successivo al "to"
     * @return lista di LocalDate (lunedì)
     */
    public static List<LocalDate> getMondaysBetween(YearMonth from, YearMonth to, boolean includeStartOverlap, boolean includeEndOverlap) {
        LocalDate start = from.atDay(1);
        LocalDate end = to.atEndOfMonth();
        return collectMondays(start, end, includeStartOverlap, includeEndOverlap);
    }

    private static List<LocalDate> collectMondays(LocalDate start, LocalDate end, boolean includeStartOverlap, boolean includeEndOverlap) {
        List<LocalDate> mondays = new ArrayList<>();

        // primo lunedì <= start
        LocalDate monday = start.with(DayOfWeek.MONDAY);
        if (monday.isAfter(start)) {
            monday = monday.minusWeeks(1);
        }

        while (!monday.isAfter(end.plusWeeks(1))) {
            boolean isBeforeStart = monday.isBefore(start);
            boolean isAfterEnd = monday.isAfter(end);

            if ((isBeforeStart && !includeStartOverlap) || (isAfterEnd && !includeEndOverlap)) {
                if (weekIntersectsRange(monday, start, end)) {
                    mondays.add(monday);
                }
            } else {
                mondays.add(monday);
            }

            monday = monday.plusWeeks(1);
        }

        return mondays;
    }

    private static boolean weekIntersectsRange(LocalDate monday, LocalDate start, LocalDate end) {
        LocalDate sunday = monday.plusDays(6);
        return !(sunday.isBefore(start) || monday.isAfter(end));
    }

    private static int countDaysInMonthWeek(LocalDate monday, YearMonth yearMonth) {
        int count = 0;
        for (int i = 0; i < 7; i++) {
            LocalDate d = monday.plusDays(i);
            if (YearMonth.from(d).equals(yearMonth)) {
                count++;
            }
        }
        return count;
    }
}
