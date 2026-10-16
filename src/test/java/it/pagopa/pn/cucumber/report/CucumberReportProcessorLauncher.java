package it.pagopa.pn.cucumber.report;

/**
 * Launcher per processare i report di cucumber e generare il CSV per l'analisi dei risultati.
 * Esegue due operazioni principali:
 * 1. Unisce i report JSON di Cucumber in un unico file.
 * 2. Genera un file CSV con i dettagli degli scenari falliti per facilitare l'analisi dei risultati.
 */
public class CucumberReportProcessorLauncher {
    public static void main(String[] args) throws Exception {
        //Unisce cucumber-reports
        CucumberReportProcessor.process();
        //Crea CSV per analisi
        CucumberCsvReportGenerator.generateCsv();
    }
}