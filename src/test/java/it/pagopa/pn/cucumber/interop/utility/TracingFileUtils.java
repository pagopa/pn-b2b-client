package it.pagopa.pn.cucumber.interop.utility;

import com.opencsv.CSVWriter;
import org.opentest4j.AssertionFailedError;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.io.*;

@Component
public class TracingFileUtils {
    private static final String CSV_OK_FILEPATH = "src/main/resources/interop/tracing-ok.csv";
    private static final String CSV_ERROR_FILEPATH = "src/main/resources/interop/tracing-error.csv";
    private final ResourceLoader resourceLoader;

    public TracingFileUtils(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    public void updateCsv(String date) {
        File file = new File(CSV_OK_FILEPATH);
        try {
            CSVWriter csvWriter = new CSVWriter(new FileWriter(file));
            csvWriter.writeNext(new String[] {"date", "purpose_id", "status", "requests_count"});
            csvWriter.writeNext(new String[] {date, "28874634-6ea6-4def-b200-7377182c71be", "200", "48"});
            csvWriter.close();
        } catch (IOException ex) {
            throw new AssertionFailedError("There was an error while generating the csv file: " + ex);
        }
    }

    public Resource getCsvFile(String file) {
        return switch (file.trim().toLowerCase()) {
            case "corretto" -> resourceLoader.getResource("file:" + CSV_OK_FILEPATH);
            case "errato" -> resourceLoader.getResource("file:" + CSV_ERROR_FILEPATH);
            default -> throw new IllegalStateException("Unexpected value: " + file.trim().toLowerCase());
        };
    }
}
