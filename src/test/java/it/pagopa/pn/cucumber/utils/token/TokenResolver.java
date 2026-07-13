package it.pagopa.pn.cucumber.utils.token;

import it.pagopa.pn.cucumber.steps.SendSharedContext;
import it.pagopa.pn.cucumber.steps.SharedSteps;

import java.util.Map;

/**
 * Risolve, all'interno dei feature file, i valori che non sono noti a priori perché generati
 * dinamicamente durante lo scenario (es. il gruppo assegnato in automatico a una notifica, o l'id
 * di una delega appena creata). Un valore atteso identifica un token dinamico quando inizia per
 * {@value #TOKEN_PREFIX} (es. {@code :group}, {@code :mandateId}); qualunque altro valore viene
 * restituito invariato.
 * <p>
 * Non è un bean Spring: va istanziato dalla classe di step che possiede già {@link SharedSteps}
 * (e, se serve, {@link SendSharedContext}), iniettati in sicurezza da cucumber-spring, perché non
 * sono disponibili nel contesto Spring al momento in cui verrebbero creati gli eventuali bean
 * {@code @Component}.
 * <p>
 * Per aggiungere un nuovo token dinamico è sufficiente registrare una nuova entry in {@link #DYNAMIC_TOKENS},
 * senza dover modificare gli step che consumano il valore risolto.
 */
public class TokenResolver {

    private static final String TOKEN_PREFIX = ":";

    @FunctionalInterface
    private interface TokenValueResolver {
        String resolve(SharedSteps sharedSteps, SendSharedContext sendSharedContext);
    }

    private static final Map<String, TokenValueResolver> DYNAMIC_TOKENS = Map.of(
            ":group", (sharedSteps, sendSharedContext) -> sharedSteps.getNotificationStepInterface().getNotificationRequestGroup(),
            ":mandateId", (sharedSteps, sendSharedContext) -> sharedSteps.getMandateId(),
            ":actualIun", (sharedSteps, sendSharedContext) -> sharedSteps.getNotificationIun(),
            ":sender", (sharedSteps, sendSharedContext) -> sharedSteps.getSentNotificationLastVersion().getSenderDenomination(),
            ":senderId", (sharedSteps, sendSharedContext) -> sharedSteps.getSentNotificationLastVersion().getSenderPaId(),
            ":recipientId", (sharedSteps, sendSharedContext) -> sharedSteps.getSentNotificationLastVersion().getRecipients().get(0).getTaxId(),
            ":informal_recipientId", (sharedSteps, sendSharedContext) -> sendSharedContext.getInformalNotificationContext().getRecipient().getDestinatario().getTaxId(),
            ":informal_senderId", (sharedSteps, sendSharedContext) -> sendSharedContext.getInformalNotificationContext().getSenderId()
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
        if (resolver == null) {
            throw new IllegalArgumentException("Token dinamico sconosciuto: " + value);
        }

        return resolver.resolve(sharedSteps, sendSharedContext);
    }

}
