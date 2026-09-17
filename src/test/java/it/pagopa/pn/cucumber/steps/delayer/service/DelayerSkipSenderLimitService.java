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
import java.util.Map;
import java.util.Objects;
import org.springframework.stereotype.Service;

import static it.pagopa.pn.cucumber.steps.delayer.utils.DelayerPaperDeliveryUtils.getPreviousMondayFromDate;
import static it.pagopa.pn.cucumber.steps.delayer.utils.DelayerPaperDeliveryUtils.getSenderKey;

/**
 * Calcola skipSenderLimit interrogando il backend reale (pn-PaperDeliverySenderLimit/pn-PaperDeliveryUsedSenderLimit),
 * nei due momenti in cui il valore può cambiare:
 * - al caricamento del CSV, sulla base della commessa della settimana di deposito (notificationSentAt);
 * - al congelamento per capacità di recapito/stampa (non per limite mittente): il passaggio da false a
 *   true vale solo per la settimana di ricarico, mai per la valutazione corrente — chi è già true
 *   dall'ingestion ha priorità da subito, chi è ancora false resta tale finché non viene effettivamente
 *   rimandato alla settimana successiva.
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
     * Da richiamare quando una spedizione viene congelata per capacità di recapito o di stampa (non per
     * limite mittente): agisce solo sulla copia che verrà ricaricata la settimana successiva (mai
     * sull'originale di questa settimana), controllando la commessa della settimana X-1 rispetto alla
     * settimana di elaborazione corrente — la stessa data usata per il controllo del limite garantito.
     */
    public void resolveOnFreeze(DelayerPaperDelivery notification, String currentWeek) {
        if (notification.isRS() || notification.isSecondAttempt() || notification.isInformalCommunication()) {
            return;
        }
        if (Boolean.TRUE.equals(notification.getSkipSenderLimit())) {
            return;
        }
        String weekToCheck = getPreviousMondayFromDate(currentWeek, 1);
        if (hasResidualCapacity(getSenderKey(notification), weekToCheck)) {
            notification.setSkipSenderLimit(true);
        }
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
