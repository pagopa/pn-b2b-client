package it.pagopa.pn.client.b2b.pa.mapper;

import it.pagopa.common.util.StringUtils;
import it.pagopa.pn.client.b2b.pa.domain.NotificationSearchParam;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

/**
 * Costruisce {@link NotificationSearchParam} a partire dalla DataTable dei filtri di ricerca.
 * <p>
 * Ogni campo assume un default (tipicamente {@code null}) se la chiave è assente dalla tabella;
 * per simulare l'assenza esplicita di un valore (es. per testare errori di validazione) è
 * sufficiente valorizzare la cella con {@code $NULL}, gestito da {@link StringUtils#resolveValue}.
 * <p>
 * {@code xPagopaPnCxType}/{@code xPagopaPnCxId} (come già {@code iunMatch}) usano invece come
 * default il valore convenzionale {@link NotificationSearchParam#ACTUAL}: quando la chiave è assente
 * dalla tabella, chi costruisce la chiamata verso l'API (che conosce il destinatario) può risolverlo
 * nel valore reale; se invece la cella è valorizzata esplicitamente a {@code NULL}, il campo resta
 * {@code null} fino alla chiamata, per simulare l'assenza del campo obbligatorio.
 */
@Component
public class NotificationSearchParamMapper {

    private static final String DEFAULT_SIZE = "10";
    private static final String NO_LIMIT_SIZE = "-1";

    public NotificationSearchParam build(Map<String, String> data, OffsetDateTime startDate, OffsetDateTime endDate, String actualIun) {
        NotificationSearchParam searchParam = new NotificationSearchParam();

        searchParam.startDate = startDate;
        searchParam.endDate = endDate;
        searchParam.xPagopaPnUid = resolveWithDefault(data, "xPagopaPnUid", "TestAutomation");
        searchParam.xPagopaPnCxType = resolveWithDefault(data, "xPagopaPnCxType", NotificationSearchParam.ACTUAL);
        searchParam.xPagopaPnCxId = resolveWithDefault(data, "xPagopaPnCxId", NotificationSearchParam.ACTUAL);
        searchParam.mandateId = resolveWithDefault(data, "mandateId", null);
        searchParam.senderId = resolveWithDefault(data, "senderId", null);
        searchParam.status = resolveWithDefault(data, "status", null);
        searchParam.subjectRegExp = resolveWithDefault(data, "subjectRegExp", null);
        searchParam.recipientId = resolveWithDefault(data, "recipientId", null);
        searchParam.xPagopaPnCxGroups = resolveGroups(resolveWithDefault(data, "xPagopaPnCxGroups", null));
        searchParam.group = resolveWithDefault(data, "group", null);
        searchParam.nextPagesKey = resolveWithDefault(data, "nextPagesKey", null);
        searchParam.communicationType = resolveWithDefault(data, "communicationType", null);
        searchParam.size = resolveSize(resolveWithDefault(data, "size", DEFAULT_SIZE));

        String iun = resolveWithDefault(data, "iunMatch", null);
        searchParam.iunMatch = NotificationSearchParam.ACTUAL.equalsIgnoreCase(iun) ? actualIun : iun;

        return searchParam;
    }

    // chiave assente in tabella -> default; valore "$NULL"/"NULL"/vuoto -> null esplicito (per simulare campi mancanti)
    private String resolveWithDefault(Map<String, String> data, String key, String defaultValue) {
        if (!data.containsKey(key)) {
            return defaultValue;
        }
        return StringUtils.resolveValue(data.get(key));
    }

    private Integer resolveSize(String value) {
        if (value == null || NO_LIMIT_SIZE.equals(value)) {
            return null;
        }
        return Integer.parseInt(value);
    }

    private List<String> resolveGroups(String value) {
        return value == null ? null : List.of(value.split(";"));
    }
}

