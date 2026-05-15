package it.pagopa.pn.interop.cucumber.config.concurrency.writer;

import it.pagopa.pn.interop.cucumber.config.concurrency.ScenariosConcurrencyReporter;

public class ScenariosConcurrencyStdoutReportWriter implements ScenariosConcurrencyReportWriter {

    /**
     * Scrive la timeline in formato Gantt testuale.
     */
    @Override
    public void write(ScenariosConcurrencyReporter.TimelineModel model) {
        if (model.matrix().length == 0) return;

        StringBuilder sb = new StringBuilder();
        sb.append("\n=== Analisi della concorrenza. MATRICE 1: TIMELINE DI ESECUZIONE ===\n");

        // Header colonne (S00, S01, ...)
        sb.append("Tempo (s) | ");
        for (int i = 0; i < model.scenarioNames().size(); i++) {
            sb.append(String.format("S%02d ", i));
        }
        sb.append("\n----------|").append("----".repeat(model.scenarioNames().size())).append("\n");

        // Righe della matrice
        for (int tIdx = 0; tIdx < model.timeBuckets().size(); tIdx++) {
            long relativeSec = (model.timeBuckets().get(tIdx) - model.timeBuckets().get(0)) / 1000;
            sb.append(String.format("%8ds | ", relativeSec));

            for (int sIdx = 0; sIdx < model.scenarioNames().size(); sIdx++) {
                sb.append(model.matrix()[tIdx][sIdx] ? " X  " : " .  ");
            }
            sb.append("\n");
        }

        // Legenda finale
        sb.append("\nLEGENDA SCENARI:\n");
        for (int i = 0; i < model.scenarioNames().size(); i++) {
            sb.append(String.format("S%02d -> %s%n", i, model.scenarioNames().get(i)));
        }

        System.out.print(sb);
    }

    /**
     * Scrive la matrice di sovrapposizione tra scenari.
     */
    @Override
    public void write(ScenariosConcurrencyReporter.OverlapModel model) {
        if (model.matrix().length == 0) return;

        StringBuilder sb = new StringBuilder();
        sb.append("\n=== Analisi della concorrenza. MATRICE 2: SOVRAPPOSIZIONE SCENARI ===\n");

        // Header colonne
        sb.append("      | ");
        for (int i = 0; i < model.scenarioNames().size(); i++) {
            sb.append(String.format("S%02d ", i));
        }
        sb.append("\n------|").append("----".repeat(model.scenarioNames().size())).append("\n");

        // Righe
        for (int i = 0; i < model.scenarioNames().size(); i++) {
            sb.append(String.format("S%02d   | ", i));
            for (int j = 0; j < model.scenarioNames().size(); j++) {
                if (i == j) {
                    sb.append(" -  ");
                } else {
                    sb.append(model.matrix()[i][j] ? " X  " : " .  ");
                }
            }
            sb.append("\n");
        }

        System.out.print(sb);
    }
}
