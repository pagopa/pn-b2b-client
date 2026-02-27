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
    private String tracingOkCsvPath;
    @Value("${tracing.error.csv.filepath}")
    private String tracingErrorCsvPath;
    private final ResourceLoader resourceLoader;

    public TracingFileUtils(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    public void updateCsv(LocalDate date) {
        try {
            // Read the csv file before overriding it with the correct date.
            FileReader fileReader = new FileReader(tracingOkCsvPath);
            CSVReader csvReader = new CSVReader(fileReader);
            // Read the header (first row) of the CSV file
            String[] header = csvReader.readNext();
            // Read all the rows (excluding the header)
            List<String[]> allRows = csvReader.readAll();
            csvReader.close();

            FileWriter fileWriter = new FileWriter(tracingOkCsvPath);
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
            case "corretto" -> resourceLoader.getResource("file:" + tracingOkCsvPath);
            case "errato" -> resourceLoader.getResource("file:" + tracingErrorCsvPath);
            default -> throw new IllegalStateException("Unexpected value: " + file.trim().toLowerCase());
        };
    }
}
