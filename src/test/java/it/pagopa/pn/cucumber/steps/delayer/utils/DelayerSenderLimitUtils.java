package it.pagopa.pn.cucumber.steps.delayer.utils;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

public class DelayerSenderLimitUtils {

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

            boolean inside = weekInsideRange(monday, start, end);
            boolean intersects = weekIntersectsRange(monday, start, end);

            if (inside) {
                // settimana completamente dentro → sempre includi
                mondays.add(monday);
            } else if (isBeforeStart && includeStartOverlap && intersects) {
                mondays.add(monday);
            } else if (isAfterEnd && includeEndOverlap && intersects) {
                mondays.add(monday);
            }
            // altrimenti non aggiungere

            monday = monday.plusWeeks(1);
        }

        return mondays;
    }

    private static boolean weekInsideRange(LocalDate monday, LocalDate start, LocalDate end) {
        LocalDate sunday = monday.plusDays(6);
        return !monday.isBefore(start) && !sunday.isAfter(end);
    }


    private static boolean weekIntersectsRange(LocalDate monday, LocalDate start, LocalDate end) {
        LocalDate sunday = monday.plusDays(6);
        return !(sunday.isBefore(start) || monday.isAfter(end));
    }

    public static int countDaysInMonthWeek(LocalDate monday, YearMonth yearMonth) {
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
