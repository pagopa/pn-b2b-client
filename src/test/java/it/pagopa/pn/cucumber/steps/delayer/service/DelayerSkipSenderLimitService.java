package it.pagopa.pn.cucumber.steps.delayer.service;

import io.cucumber.spring.ScenarioScope;
import it.pagopa.pn.cucumber.steps.delayer.client.DelayerLambdaClient;
import it.pagopa.pn.cucumber.steps.delayer.model.DelayerPaperDelivery;
import lombok.RequiredArgsConstructor;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.springframework.stereotype.Service;

import static it.pagopa.pn.cucumber.steps.delayer.utils.DelayerPaperDeliveryUtils.getPreviousMondayFromDate;
import static it.pagopa.pn.cucumber.steps.delayer.utils.DelayerPaperDeliveryUtils.getSenderKey;

/**
 * Calcola skipSenderLimit interrogando il backend reale (pn-PaperDeliverySenderLimit/pn-PaperDeliveryUsedSenderLimit),
 * nei due momenti in cui il valore può cambiare:
 * - al caricamento del CSV, sulla base della commessa della settimana di deposito (notificationSentAt);
 * - durante l'esecuzione, quando una spedizione non ha superato il controllo iniziale ma può comunque
 *   recuperare il bypass: sia alla prima step function, sia quando viene congelata per capacità di
 *   recapito/stampa, la settimana di riferimento è sempre la stessa, X-1 rispetto alla settimana di
 *   elaborazione corrente — la stessa data usata per il controllo del limite garantito.
 */
@Service
@ScenarioScope
@RequiredArgsConstructor
public class DelayerSkipSenderLimitService {

    private final DelayerLambdaClient lambdaClient;
    private final Map<String, Boolean> residualCapacityCache = new HashMap<>();

    /**
     * Valorizza il campo skipSenderLimit al caricamento del CSV:
     * - RS e secondi tentativi: sempre true.
     * - valore esplicito già presente nel CSV: invariato.
     * - altrimenti: quota residua sulla commessa della settimana di notificationSentAt.
     */
    public void resolveInitialValue(DelayerPaperDelivery n) {
        if (n.isRS() || n.isSecondAttempt()) {
            n.setSkipSenderLimit(true);
            return;
        }
        if (n.getSkipSenderLimit() != null) {
            return;
        }
        String monday = mondayOfWeek(n.getNotificationSentAt());
        n.setSkipSenderLimit(hasResidualCapacity(getSenderKey(n), monday));
    }

    /**
     * Da richiamare quando viene eseguita la prima step function: per le spedizioni con skipSenderLimit
     * ancora false (RS/secondi tentativi esclusi, già true), controlla la commessa della settimana X-1
     * (la settimana precedente a quella di elaborazione corrente) e, se c'è ancora quota, valorizza
     * skipSenderLimit a true in modo stabile.
     */
    public void resolveOnFirstStepFunction(List<DelayerPaperDelivery> notifications, String currentWeek) {
        applyResidualCapacityFallback(notifications, currentWeek);
    }

    /**
     * Da richiamare quando una spedizione viene congelata per capacità di recapito o di stampa (non per
     * limite mittente): stessa settimana di riferimento X-1 del controllo alla prima step function — è la
     * stessa data usata per il controllo del limite garantito, non la settimana appena valutata.
     */
    public void resolveOnFreeze(DelayerPaperDelivery notification, String currentWeek) {
        applyResidualCapacityFallback(List.of(notification), currentWeek);
    }

    private void applyResidualCapacityFallback(List<DelayerPaperDelivery> notifications, String currentWeek) {
        String weekToCheck = getPreviousMondayFromDate(currentWeek, 1);
        notifications.stream()
                .filter(n -> !n.isRS() && !n.isSecondAttempt() && !n.isInformalCommunication())
                .filter(n -> !Boolean.TRUE.equals(n.getSkipSenderLimit()))
                .forEach(n -> {
                    if (hasResidualCapacity(getSenderKey(n), weekToCheck)) {
                        n.setSkipSenderLimit(true);
                    }
                });
    }

    private boolean hasResidualCapacity(String senderKey, String monday) {
        String cacheKey = senderKey + "~" + monday;
        return residualCapacityCache.computeIfAbsent(cacheKey, k -> queryResidualCapacity(senderKey, monday));
    }

    private boolean queryResidualCapacity(String pk, String monday) {
        var senderLimits = lambdaClient.getSenderLimitByPk(monday, pk);
        if (senderLimits == null || senderLimits.getItems() == null || senderLimits.getItems().isEmpty()) {
            return false;
        }
        int weeklyEstimate = Objects.requireNonNullElse(senderLimits.getItems().get(0).getWeeklyEstimate(), 0);

        var used = lambdaClient.getUsedSenderLimitByPk(monday, pk);
        int numberOfShipment = (used == null || used.getItems() == null || used.getItems().isEmpty())
                ? 0
                : Objects.requireNonNullElse(used.getItems().get(0).getNumberOfShipment(), 0);

        return weeklyEstimate - numberOfShipment > 0;
    }

    private static String mondayOfWeek(String isoNotificationSentAt) {
        if (isoNotificationSentAt == null || isoNotificationSentAt.isBlank()) {
            throw new IllegalArgumentException("notificationSentAt mancante o vuoto");
        }
        LocalDate date = OffsetDateTime.parse(isoNotificationSentAt).toLocalDate();
        return date.with(DayOfWeek.MONDAY).format(DateTimeFormatter.ISO_LOCAL_DATE);
    }
}
