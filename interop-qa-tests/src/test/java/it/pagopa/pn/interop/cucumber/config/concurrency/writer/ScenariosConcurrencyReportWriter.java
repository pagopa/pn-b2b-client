package it.pagopa.pn.interop.cucumber.config.concurrency.writer;

import it.pagopa.pn.interop.cucumber.config.concurrency.ScenariosConcurrencyReporter;

/**
 * Interfaccia per la definizione di strategie di scrittura dei report di concorrenza.
 * <p>
 * Le classi che implementano questa interfaccia sono responsabili della persistenza o
 * della visualizzazione dei dati elaborati dal {@link ScenariosConcurrencyReporter}.
 * Ogni implementazione può decidere il formato di output (es. Console, CSV, JSON, HTML)
 * e il supporto di destinazione (Standard Output, File, API esterne).
 * </p>
 * * <p>I metodi vengono invocati tipicamente al termine della suite di test,
 * fornendo i modelli pre-calcolati per garantire coerenza tra diversi formati.</p>
 */
public interface ScenariosConcurrencyReportWriter {

    /**
     * Elabora e scrive il modello temporale degli scenari.
     * <p>
     * Questo metodo riceve una mappatura cronologica che descrive lo stato di attivazione
     * di ogni scenario rispetto a determinati bucket temporali. È ideale per la
     * generazione di grafici di Gantt o timeline testuali.
     * </p>
     * * @param model Il modello contenente i nomi degli scenari, i bucket temporali
     * e la matrice booleana delle attività.
     * @throws RuntimeException se si verificano errori durante la scrittura (es. IO nel caso di file).
     */
    void write(ScenariosConcurrencyReporter.TimelineModel model);

    /**
     * Elabora e scrive la matrice di sovrapposizione degli scenari.
     * <p>
     * Questo metodo si focalizza sulla relazione tra scenari, evidenziando quali sono
     * stati eseguiti in parallelo. È lo strumento principale per verificare
     * empiricamente il rispetto dei vincoli di mutua esclusione impostati nel sistema.
     * </p>
     * * @param model Il modello contenente i nomi degli scenari e la matrice di adiacenza
     * che indica le sovrapposizioni temporali rilevate.
     * @throws RuntimeException se si verificano errori durante la scrittura.
     */
    void write(ScenariosConcurrencyReporter.OverlapModel model);
}
