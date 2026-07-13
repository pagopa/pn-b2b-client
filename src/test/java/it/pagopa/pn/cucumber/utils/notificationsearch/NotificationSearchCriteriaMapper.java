package it.pagopa.pn.cucumber.utils.notificationsearch;

import it.pagopa.pn.cucumber.utils.token.TokenResolver;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Costruisce i criteri di verifica per le righe di risultato della ricerca notifiche a partire dalla
 * DataTable del feature file: ogni riga della tabella è una coppia campo/valori ammessi, con valori
 * multipli separati da virgola (es. {@code communicationType | LEGAL, INFORMAL}).
 * <p>
 * Un valore può essere un token dinamico (es. {@code :group}), nel qual caso viene risolto tramite
 * il {@link TokenResolver} fornito dal chiamante anziché confrontato letteralmente.
 */
@Component
public class NotificationSearchCriteriaMapper {

    public Map<String, List<String>> build(Map<String, String> rawCriteria, TokenResolver tokenResolver) {
        Map<String, List<String>> criteria = new LinkedHashMap<>();
        rawCriteria.forEach((field, rawValues) -> criteria.put(field, resolveValues(rawValues, tokenResolver)));
        return criteria;
    }

    private List<String> resolveValues(String rawValues, TokenResolver tokenResolver) {
        return Arrays.stream(rawValues.split(","))
                .map(String::trim)
                .map(tokenResolver::resolve)
                .collect(Collectors.toList());
    }
}
