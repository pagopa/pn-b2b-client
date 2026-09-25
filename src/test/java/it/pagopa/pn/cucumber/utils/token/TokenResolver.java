package it.pagopa.pn.cucumber.utils.token;

import it.pagopa.pn.cucumber.steps.SendSharedContext;
import it.pagopa.pn.cucumber.steps.SharedSteps;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Risolve, all'interno dei feature file, i valori che non sono noti a priori perché generati
 * dinamicamente durante lo scenario (es. il gruppo assegnato in automatico a una notifica, o l'id
 * di una delega appena creata). Un valore atteso identifica un token dinamico quando inizia per
 * {@value #TOKEN_PREFIX} (es. {@code :group}, {@code :mandateId}); qualunque altro valore viene
 * restituito invariato.
 * <p>
 * Alcuni token sono indicizzati (es. {@code :recipientId_0}, {@code :recipientId_1}) per riferirsi
 * all'n-esimo elemento di una collezione (es. i destinatari di una notifica multi-destinatario): il
 * suffisso {@code _<indice>} viene estratto e passato al resolver registrato in
 * {@link #INDEXED_DYNAMIC_TOKENS} con quel nome base.
 * <p>
 * Non è un bean Spring: va istanziato dalla classe di step che possiede già {@link SharedSteps}
 * (e, se serve, {@link SendSharedContext}), iniettati in sicurezza da cucumber-spring, perché non
 * sono disponibili nel contesto Spring al momento in cui verrebbero creati gli eventuali bean
 * {@code @Component}.
 * <p>
 * Per aggiungere un nuovo token dinamico è sufficiente registrare una nuova entry in {@link #DYNAMIC_TOKENS}
 * (o in {@link #INDEXED_DYNAMIC_TOKENS} se dipende da un indice), senza dover modificare gli step che
 * consumano il valore risolto.
 */
public class TokenResolver {

    private static final String TOKEN_PREFIX = ":";
    private static final Pattern INDEXED_TOKEN_PATTERN = Pattern.compile("^:(\\w+)_(\\d+)$");

    @FunctionalInterface
    private interface TokenValueResolver {
        String resolve(SharedSteps sharedSteps, SendSharedContext sendSharedContext);
    }

    @FunctionalInterface
    private interface IndexedTokenValueResolver {
        String resolve(SharedSteps sharedSteps, SendSharedContext sendSharedContext, int index);
    }

    private static final Map<String, TokenValueResolver> DYNAMIC_TOKENS = Map.ofEntries(
            Map.entry(":group", (sharedSteps, sendSharedContext) -> sharedSteps.getNotificationStepInterface().getNotificationRequestGroup()),
            Map.entry(":mandateId", (sharedSteps, sendSharedContext) -> sharedSteps.getMandateId()),
            Map.entry(":actualIun", (sharedSteps, sendSharedContext) -> sharedSteps.getNotificationIun()),
            Map.entry(":sender", (sharedSteps, sendSharedContext) -> sharedSteps.getSentNotificationLastVersion().getSenderDenomination()),
            Map.entry(":senderId", (sharedSteps, sendSharedContext) -> sharedSteps.getSentNotificationLastVersion().getSenderPaId()),
            Map.entry(":recipientId", (sharedSteps, sendSharedContext) -> sharedSteps.getSentNotificationLastVersion().getRecipients().get(0).getTaxId()),
            Map.entry(":recipientUid", (sharedSteps, sendSharedContext) ->
                    sendSharedContext.getLegalNotificationContext().getRecipient().getDestinatario().getRecipientType()
                    + "-" + sendSharedContext.getLegalNotificationContext().getRecipient().getDestinatario().getUid()),
            Map.entry(":informal_recipientId", (sharedSteps, sendSharedContext) -> sendSharedContext.getInformalNotificationContext().getRecipient().getDestinatario().getTaxId()),
            Map.entry(":informal_senderId", (sharedSteps, sendSharedContext) -> sendSharedContext.getInformalNotificationContext().getSenderId()),
            Map.entry(":informal_group", (sharedSteps, sendSharedContext) -> sendSharedContext.getInformalNotificationContext().getGroupId()),
            Map.entry(":informal_iun", (sharedSteps, sendSharedContext) -> sendSharedContext.getInformalNotificationContext().getIun())
    );

    private static final Map<String, IndexedTokenValueResolver> INDEXED_DYNAMIC_TOKENS = Map.of(
            "recipientId", (sharedSteps, sendSharedContext, index) ->
                    sharedSteps.getSentNotificationLastVersion().getRecipients().get(index).getTaxId()
    );

    private final SharedSteps sharedSteps;
    private final SendSharedContext sendSharedContext;

    public TokenResolver(SharedSteps sharedSteps, SendSharedContext sendSharedContext) {
        this.sharedSteps = sharedSteps;
        this.sendSharedContext = sendSharedContext;
    }

    public boolean isToken(String value) {
        return value != null && value.startsWith(TOKEN_PREFIX);
    }

    public String resolve(String value) {
        if (!isToken(value)) {
            return value;
        }

        TokenValueResolver resolver = DYNAMIC_TOKENS.get(value);
        if (resolver != null) {
            return resolver.resolve(sharedSteps, sendSharedContext);
        }

        Matcher indexedTokenMatcher = INDEXED_TOKEN_PATTERN.matcher(value);
        if (indexedTokenMatcher.matches()) {
            IndexedTokenValueResolver indexedResolver = INDEXED_DYNAMIC_TOKENS.get(indexedTokenMatcher.group(1));
            if (indexedResolver != null) {
                int index = Integer.parseInt(indexedTokenMatcher.group(2));
                return indexedResolver.resolve(sharedSteps, sendSharedContext, index);
            }
        }

        throw new IllegalArgumentException("Token dinamico sconosciuto: " + value);
    }

}
