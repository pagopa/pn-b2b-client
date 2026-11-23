package it.pagopa.pn.cucumber.steps.visualtest;

import it.pagopa.common.pdf.visualtest.TextFragment;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** The dynamic values of a document are the ones of the notification of the scenario, not just well formed. */
final class DynamicValueChecks {

    private static final ZoneId ROME = ZoneId.of("Europe/Rome");
    private static final DateTimeFormatter DATE_TIME = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final Pattern IUN = Pattern.compile("[A-Z]{4}-[A-Z]{4}-[A-Z]{4}-[0-9]{6}-[A-Z]-[A-Z0-9]");
    /** The document prints hours and minutes only. */
    private static final Duration SLACK = Duration.ofMinutes(1);

    private DynamicValueChecks() {
    }

    /**
     * Problems found, empty when every value matches; null expectations are not checked.
     *
     * <p>The time printed in a legal fact is the one of the event it attests (the sending of the request, the PEC
     * delivery, the access, ...), which comes before the timeline element is written: in TEST the gap is up to three
     * minutes. So the printed time is not compared with the timeline timestamp but must fall between {@code notBefore}
     * (the notification was sent) and {@code notAfter} (the timeline element was written), one minute of slack each
     * side for the missing seconds.</p>
     */
    static List<String> check(List<TextFragment> fragments, String iun, String recipientTaxId,
                              OffsetDateTime notBefore, OffsetDateTime notAfter) {
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
        if (notBefore != null && notAfter != null) {
            List<LocalDateTime> stamps = dateTimes(fragments);
            LocalDateTime from = notBefore.atZoneSameInstant(ROME).toLocalDateTime().minus(SLACK);
            LocalDateTime to = notAfter.atZoneSameInstant(ROME).toLocalDateTime().plus(SLACK);
            if (stamps.stream().noneMatch(s -> !s.isBefore(from) && !s.isAfter(to))) {
                problems.add("data e ora nel documento " + stamps + ", attese tra " + DATE_TIME.format(from) + " e "
                        + DATE_TIME.format(to) + " (Europe/Rome)");
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
                    try {
                        stamps.add(LocalDateTime.parse(date + " " + time, DATE_TIME));
                    } catch (DateTimeParseException e) {
                        // not a calendar date (e.g. 31/02): not a timestamp of the document
                    }
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
