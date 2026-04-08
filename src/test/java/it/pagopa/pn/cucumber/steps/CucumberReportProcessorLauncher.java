package it.pagopa.pn.cucumber.steps;

public class CucumberReportProcessorLauncher {
    public static void main(String[] args) throws Exception {
        //Unisce cucumber-reports
        CucumberReportProcessor.process();
        //Crea CSV per analisi
        CucumberCsvReportGenerator.generateCsv();
    }
}