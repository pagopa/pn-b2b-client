package it.pagopa.pn.interop.cucumber.config.concurrency.writer;

import it.pagopa.pn.interop.cucumber.config.concurrency.ScenariosConcurrencyReporter;
import lombok.extern.slf4j.Slf4j;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

// TODO: implementazione non ancora conclusa, sperimentazioni in corso
@Slf4j
public class ScenariosConcurrencyCsvReportWriter implements ScenariosConcurrencyReportWriter {

    private final String filePath = "target/cucumber-report-concurrency.csv";
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss.SSS")
                                                                 .withZone(ZoneId.systemDefault());

    @Override
    public void write(ScenariosConcurrencyReporter.TimelineModel model) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath, false))) {
            writer.println("--- TIMELINE REPORT ---");
            // Header: Bucket Tempo + Nomi Scenari
            writer.print("Time");
            model.scenarioNames().forEach(name -> writer.print("," + name));
            writer.println();

            // Dati
            for (int t = 0; t < model.timeBuckets().size(); t++) {
                writer.print(formatter.format(Instant.ofEpochMilli(model.timeBuckets().get(t))));
                for (int s = 0; s < model.scenarioNames().size(); s++) {
                    writer.print("," + (model.matrix()[t][s] ? "1" : "0"));
                }
                writer.println();
            }
            writer.println();
        } catch (IOException e) {
            throw new RuntimeException("Errore nella scrittura del file CSV Timeline", e);
        }
    }

    @Override
    public void write(ScenariosConcurrencyReporter.OverlapModel model) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath, true))) {
            writer.println("--- OVERLAP MATRIX ---");
            // Header
            writer.print("Scenario");
            model.scenarioNames().forEach(name -> writer.print("," + name));
            writer.println();

            // Matrice
            for (int i = 0; i < model.scenarioNames().size(); i++) {
                writer.print(model.scenarioNames().get(i));
                for (int j = 0; j < model.scenarioNames().size(); j++) {
                    writer.print("," + (model.matrix()[i][j] ? "X" : ""));
                }
                writer.println();
            }
        } catch (IOException e) {
            throw new RuntimeException("Errore nella scrittura del file CSV Overlap", e);
        }
    }
}