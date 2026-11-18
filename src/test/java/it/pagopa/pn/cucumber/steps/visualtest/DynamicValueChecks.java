package it.pagopa.pn.cucumber.steps.visualtest;

import it.pagopa.common.pdf.visualtest.TextFragment;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** The dynamic values of a document are the ones of the notification of the scenario, not just well formed. */
final class DynamicValueChecks {

    private static final ZoneId ROME = ZoneId.of("Europe/Rome");
    private static final DateTimeFormatter DATE_TIME = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final Pattern IUN = Pattern.compile("[A-Z]{4}-[A-Z]{4}-[A-Z]{4}-[0-9]{6}-[A-Z]-[A-Z0-9]");
    private static final Duration TOLERANCE = Duration.ofMinutes(5);

    private DynamicValueChecks() {
    }

    /** Problems found, empty when every value matches; null expectations are not checked. */
    static List<String> check(List<TextFragment> fragments, String iun, String recipientTaxId, OffsetDateTime eventTime) {
        List<String> problems = new ArrayList<>();
        if (iun != null) {
            List<String> found = fragments.stream().map(f -> match(IUN, f.text())).filter(m -> m != null).toList();
            if (found.isEmpty() || !found.stream().allMatch(iun::equals)) {
                problems.add("IUN nel documento " + found + ", atteso " + iun);
            }
        }
        if (recipientTaxId != null && fragments.stream().noneMatch(f -> f.text().contains(recipientTaxId))) {
            problems.add("codice fiscale del destinatario " + recipientTaxId + " assente dal documento");
        }
        if (eventTime != null) {
            List<LocalDateTime> stamps = dateTimes(fragments);
            LocalDateTime expected = eventTime.atZoneSameInstant(ROME).toLocalDateTime();
            if (stamps.stream().noneMatch(s -> Duration.between(s, expected).abs().compareTo(TOLERANCE) <= 0)) {
                problems.add("data e ora nel documento " + stamps + ", attese vicine a " + DATE_TIME.format(expected)
                        + " (Europe/Rome, ±" + TOLERANCE.toMinutes() + " min)");
            }
        }
        return problems;
    }

    /** A date followed within three words by a time ("17/11/2026 18:09", "17/11/2026 alle ore 18:09"). */
    private static List<LocalDateTime> dateTimes(List<TextFragment> fragments) {
        List<LocalDateTime> stamps = new ArrayList<>();
        for (int i = 0; i < fragments.size(); i++) {
            String date = match(Pattern.compile("[0-9]{2}/[0-9]{2}/[0-9]{4}"), fragments.get(i).text());
            if (date == null) {
                continue;
            }
            for (int j = i + 1; j < Math.min(fragments.size(), i + 4); j++) {
                String time = match(Pattern.compile("(?:[01][0-9]|2[0-3]):[0-5][0-9]"), fragments.get(j).text());
                if (time != null) {
                    stamps.add(LocalDateTime.parse(date + " " + time, DATE_TIME));
                    break;
                }
            }
        }
        return stamps;
    }

    private static String match(Pattern pattern, String text) {
        Matcher m = pattern.matcher(text);
        return m.find() ? m.group() : null;
    }
}
