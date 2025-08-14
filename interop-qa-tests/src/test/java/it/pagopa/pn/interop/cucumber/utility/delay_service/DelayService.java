package it.pagopa.pn.interop.cucumber.utility.delay_service;

import java.time.Duration;

/**
 * Service to introduce a controlled delay in a workflow.
 * <p>
 * This service provides methods to pause the execution for a default or specified period of time,
 * useful for rate limiting, simulating network latency, or waiting for external resources.
 */
public interface DelayService {
    /* IDEA 14/08/2025: un comportamento alternativo di questo metodo sarebbe attendere un
    * MASSIMO periodo di tempo in funzione di un timer nel frattempo iniziato con un altro metodo
    * (per esempio start() ): questo minimizzerebbe il trascorrere di tempo superfluo per gli
    * scenari in cui sono eseguiti step intermedi prima della chiamata di questo metodo, che già
    * contribuirebbero allo scorrere del tempo necessario per allineare l'ambiente sottostante.
    * Oppure, al posto di sostituire questo metodo lo si potrebbe affiancare da un altro
    * del tipo "delayIfNeeded().
    * N.B In ogni caso, lo scope della classe concrete andrebbe settato con @ScenarioScope .  */
    /**
     * Pauses the current thread for a default period of time.
     * <p>
     * The default delay period is configured internally within the service implementation.
     *
     * @throws DelayException if an error occurs during the delay operation.
     */
    void delay();

    /**
     * Pauses the current thread for a period scaled by a given factor.
     * <p>
     * The delay duration is calculated as the default delay multiplied by the specified factor.
     * For example, a factor of 1.5 will result in a 50% longer delay.
     * <p>
     *
     * @param factor A non-negative multiplier for the default delay. A value of 1.0 results in the default delay.
     * @throws IllegalArgumentException if the factor is negative.
     * @throws DelayException if an error occurs during the delay operation.
     */
    void delayScaledBy(double factor);

    /**
     * Pauses the current thread for a specific number of seconds.
     *
     * @param seconds The number of seconds to wait. Must be non-negative.
     * @throws IllegalArgumentException if the number of seconds is negative.
     * @throws DelayException if an error occurs during the delay operation.
     */
    void delayForSeconds(int seconds);

    /**
     * Pauses the current thread for a specific number of milliseconds.
     *
     * @param millis The number of milliseconds to wait. Must be non-negative.
     * @throws IllegalArgumentException if the number of milliseconds is negative.
     * @throws DelayException if an error occurs during the delay operation.
     */
    void delayForMillis(long millis);

    /**
     * Pauses the current thread for a specified duration.
     *
     * @param duration The duration to wait. Must be a non-negative duration.
     * @throws IllegalArgumentException if the duration is negative.
     * @throws DelayException if an error occurs during the delay operation.
     */
    void delayFor(Duration duration);
}