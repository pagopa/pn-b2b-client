package it.pagopa.pn.interop.cucumber.utility;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@Component
public class TracingFileUtils {
    @Value("${tracing.success.csv.filepath}")
    private String tracingOkCsvFilePath;
    @Value("${tracing.error.csv.filepath}")
    private String tracingErrorCsvFilePath;
    @Value("${tracing.csv.errors.path}")
    private String tracingErrorCsvPath;
    private final ResourceLoader resourceLoader;

    public TracingFileUtils(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    public void updateCsv(LocalDate date) {
        try {
            // Read the csv file before overriding it with the correct date.
            FileReader fileReader = new FileReader(tracingOkCsvFilePath);
            CSVReader csvReader = new CSVReader(fileReader);
            // Read the header (first row) of the CSV file
            String[] header = csvReader.readNext();
            // Read all the rows (excluding the header)
            List<String[]> allRows = csvReader.readAll();
            csvReader.close();

            FileWriter fileWriter = new FileWriter(tracingOkCsvFilePath);
            CSVWriter csvWriter = new CSVWriter(fileWriter);
            // Write the header back to the CSV file
            csvWriter.writeNext(header);
            // Write all the rows back and update the first column with the provided date
            for (String[] nextLine : allRows) {
                nextLine[0] = date.toString();
                csvWriter.writeNext(nextLine);
            }
            csvWriter.close();

        } catch (CsvException | IOException ex) {
            throw new RuntimeException("There was an error while generating the csv file: " + ex);
        }
    }

    public Resource getCsvFile(String file) {
        return switch (file.trim().toLowerCase()) {
            case "corretto" -> resourceLoader.getResource("file:" + tracingOkCsvFilePath);
            case "errato" -> resourceLoader.getResource("file:" + tracingErrorCsvFilePath);
            case "errato_header_campo_mancante" -> resourceLoader.getResource("file:" + tracingErrorCsvPath + "/tracing-error-header-missing-field.csv");
            case "errato_header_nome_campo" -> resourceLoader.getResource("file:" + tracingErrorCsvPath + "/tracing-error-header-wrong-field.csv");
            case "errato_header_doppia_virgola" -> resourceLoader.getResource("file:" + tracingErrorCsvPath + "/tracing-error-header-consecutive-commas.csv");
            default -> throw new IllegalStateException("Unexpected value: " + file.trim().toLowerCase());
        };
    }
}
