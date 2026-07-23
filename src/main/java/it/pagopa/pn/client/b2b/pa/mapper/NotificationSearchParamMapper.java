package it.pagopa.pn.client.b2b.pa.mapper;

import it.pagopa.common.util.StringUtils;
import it.pagopa.pn.client.b2b.pa.domain.NotificationSearchParam;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.UnaryOperator;

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
 * <p>
 * Ogni valore esplicitamente presente in tabella passa infine per {@code dynamicValueResolver}: questo
 * modulo non conosce il concetto di "token dinamico" (es. {@code :group}), che è responsabilità del
 * chiamante lato test (in {@code src/test}, non referenziabile da qui); riceve semplicemente una
 * funzione di risoluzione, così un valore come {@code :mandateId} può essere sostituito con l'id
 * generato durante lo scenario prima che il filtro venga inviato all'API.
 */
@Component
public class NotificationSearchParamMapper {

    private static final String DEFAULT_SIZE = "10";
    private static final String NO_LIMIT_SIZE = "-1";

    public NotificationSearchParam build(Map<String, String> data, OffsetDateTime startDate, OffsetDateTime endDate, String actualIun, UnaryOperator<String> dynamicValueResolver) {
        NotificationSearchParam searchParam = new NotificationSearchParam();

        searchParam.startDate = startDate;
        searchParam.endDate = endDate;
        searchParam.xPagopaPnUid = resolveWithDefault(data, "xPagopaPnUid", "TestAutomation", dynamicValueResolver);
        searchParam.xPagopaPnCxType = resolveWithDefault(data, "xPagopaPnCxType", NotificationSearchParam.ACTUAL, dynamicValueResolver);
        searchParam.xPagopaPnCxId = resolveWithDefault(data, "xPagopaPnCxId", NotificationSearchParam.ACTUAL, dynamicValueResolver);
        searchParam.mandateId = resolveWithDefault(data, "mandateId", null, dynamicValueResolver);
        searchParam.senderId = resolveWithDefault(data, "senderId", null, dynamicValueResolver);
        searchParam.status = resolveWithDefault(data, "status", null, dynamicValueResolver);
        searchParam.subjectRegExp = resolveWithDefault(data, "subjectRegExp", null, dynamicValueResolver);
        searchParam.recipientId = resolveWithDefault(data, "recipientId", null, dynamicValueResolver);
        searchParam.xPagopaPnCxGroups = resolveGroups(resolveWithDefault(data, "xPagopaPnCxGroups", null, dynamicValueResolver));
        searchParam.group = resolveWithDefault(data, "group", null, dynamicValueResolver);
        searchParam.nextPagesKey = resolveWithDefault(data, "nextPagesKey", null, dynamicValueResolver);
        searchParam.communicationType = resolveWithDefault(data, "communicationType", null, dynamicValueResolver);
        searchParam.campaignId = resolveWithDefault(data, "campaignId", null, dynamicValueResolver);
        searchParam.size = resolveSize(resolveWithDefault(data, "size", DEFAULT_SIZE, dynamicValueResolver));

//        String iun = resolveWithDefault(data, "iunMatch", null, dynamicValueResolver);
        searchParam.iunMatch = resolveWithDefault(data, "iunMatch", null, dynamicValueResolver);
        searchParam.viewed = Boolean.parseBoolean(resolveWithDefault(data, "viewed", "false", dynamicValueResolver));
        searchParam.delivered = Boolean.parseBoolean(resolveWithDefault(data, "delivered", "false", dynamicValueResolver));

        return searchParam;
    }

    // chiave assente in tabella -> default; valore "$NULL"/"NULL"/vuoto -> null esplicito (per simulare campi mancanti);
    // valore esplicito -> passato al resolver del chiamante, per risolvere eventuali token dinamici (es. ":mandateId")
    private String resolveWithDefault(Map<String, String> data, String key, String defaultValue, UnaryOperator<String> dynamicValueResolver) {
        if (!data.containsKey(key)) {
            return defaultValue;
        }
        String resolved = StringUtils.resolveValue(data.get(key));
        return resolved == null ? null : dynamicValueResolver.apply(resolved);
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

