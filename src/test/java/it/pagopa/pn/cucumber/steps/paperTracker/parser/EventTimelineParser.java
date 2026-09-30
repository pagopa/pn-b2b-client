package it.pagopa.pn.cucumber.steps.paperTracker.parser;

import it.pagopa.pn.cucumber.steps.paperTracker.domain.NotificationEvent;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class EventTimelineParser {

    public  Map<Integer, List<NotificationEvent>> parse(List<String> rawList) {
        Map<Integer, List<NotificationEvent>> result = new HashMap<>();

        Pattern countPattern = Pattern.compile("(.+?)_COUNT_(\\d+)$");
        Pattern attemptPattern = Pattern.compile("(.+?)_ATTEMPT_(\\d+)$");
        Pattern optionalPattern = Pattern.compile("\\[(.*?)]");

        for (String raw : rawList) {
            List<String> tags = new ArrayList<>();
            String deliveryFailureCause = null;

            // estrai info opzionali tra []
            Matcher optionalMatcher = optionalPattern.matcher(raw);
            if (optionalMatcher.find()) {
                String options = optionalMatcher.group(1);
                for (String part : options.split(";")) {
                    if (part.startsWith("DOC:")) {
                        String value = part.substring(4).trim();
                        if (List.of("7ZIP", "ZIP").contains(value)) tags.add("safestorage://PN_PRINTED");
                        else if (List.of("Plico", "Indagine", "AR", "ARCAD", "23L").contains(value))
                            tags.add("safestorage://PN_EXTERNAL_LEGAL_FACTS-");
                    }
                    if (part.startsWith("FAILCAUSE:")) {
                        deliveryFailureCause = part.substring(10).trim();
                    }
                }
            }

            String base = raw.replaceAll("\\[.*?\\]", "");

            //estrai COUNT
            int count = 1;
            Matcher countMatcher = countPattern.matcher(base);
            if (countMatcher.find()) {
                base = countMatcher.group(1);
                count = Integer.parseInt(countMatcher.group(2));
            }

            //estrai ATTEMPT
            int attempt = 0;
            Matcher attemptMatcher = attemptPattern.matcher(base);
            if (attemptMatcher.find()) {
                base = attemptMatcher.group(1);
                attempt = Integer.parseInt(attemptMatcher.group(2));
            }

            //crea NotificationEvent
            NotificationEvent event = new NotificationEvent(base, tags, deliveryFailureCause);

            //aggiungi duplicati nella mappa
            result.computeIfAbsent(attempt, k -> new ArrayList<>())
                    .addAll(Collections.nCopies(count, event));
        }

        return result;
    }

}
