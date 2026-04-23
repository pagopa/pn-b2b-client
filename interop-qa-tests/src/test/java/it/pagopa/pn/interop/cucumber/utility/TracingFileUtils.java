package it.pagopa.pn.interop.cucumber.utility;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvException;
import it.pagopa.pn.interop.cucumber.steps.tracing.TracingSteps;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.random.RandomGenerator;

@Component
public class TracingFileUtils {
    @Value("${tracing.csv.example.filepath}")
    private String tracingExampleCsvFilePath;
    @Value("${tracing.csv.tmp.path}")
    private String tracingTemporaryCsvPath;
    @Value("${tracing.csv.errors.path}")
    private String tracingErrorCsvPath;

    private static String temporaryPath;

    @Value("${tracing.csv.tmp.path}")
    public void setTemporaryPath(String path) {
        TracingFileUtils.temporaryPath = tracingTemporaryCsvPath;
    }

    @Value("${spring.profiles.active}")
    private String envProfile;

    private final ResourceLoader resourceLoader;
    private final int randomRequestCountFrom = 1;
    private final int randomRequestCountTo = 50;

    public TracingFileUtils(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    private void createTemporaryTracingFolder() {
        new java.io.File(tracingTemporaryCsvPath).mkdir();
    }

    public static void removeTemporaryFileAndFolder(String fileName) {
        java.io.File file = new java.io.File(TracingFileUtils.temporaryPath, fileName);
        java.io.File metafile = new java.io.File(TracingFileUtils.temporaryPath, ".DS_Store");
        java.io.File folder = new java.io.File(TracingFileUtils.temporaryPath, "/");
        if (file.exists()) file.delete();
        if (metafile.exists()) metafile.delete();
        if (folder.exists()) folder.delete();
    }

    private String getTemporaryTracingFilePath() {
        return tracingTemporaryCsvPath + TracingSteps.getTemporaryTracingFileName();
    }

    private List<String[]> readCsvRows(String filepath) {
        try {
            // Read the valid example csv file to have real data for the new generation
            FileReader fileReader = new FileReader(filepath);
            CSVReader csvReader = new CSVReader(fileReader);
            // Read all the rows (including the header)
            List<String[]> csvRows = csvReader.readAll();
            csvReader.close();
            return csvRows;

        } catch (CsvException | IOException ex) {
            throw new RuntimeException("There was an error while generating the csv file: " + ex);
        }
    }

    public void generateValidAndMinimalTemporaryCsv(LocalDate date) {
        try {
            List<String[]> csvRows = readCsvRows(tracingExampleCsvFilePath);
            createTemporaryTracingFolder();
            FileWriter fileWriter = new FileWriter(getTemporaryTracingFilePath());
            CSVWriter csvWriter = new CSVWriter(fileWriter);

            // Write the header back to the CSV file
            csvWriter.writeNext(csvRows.get(0));

            // Write just one valid record using the provided date
            String[] firstRecord = csvRows.get(1);
            // date: an available date to upload the CSV file
            firstRecord[0] = date.toString();
            // purpose_id has to be taken from the valid example provided
            // token_id: a random UUID is accepted
            firstRecord[2] = UUID.randomUUID().toString();
            // status: an HTTP response code, it can be taken from the example
            // requests_count: how many requests have been tracked
            firstRecord[4] = String.valueOf(RandomGenerator.getDefault().nextInt(
                    randomRequestCountFrom, randomRequestCountTo + 1)
            );
            csvWriter.writeNext(firstRecord);
            csvWriter.close();

        } catch (IOException ex) {
            throw new RuntimeException("There was an error while generating the csv file: " + ex);
        }
    }

    public void generateTemporaryCsvWithEmptyPurposeId(LocalDate date) {
        try {
            List<String[]> csvRows = readCsvRows(tracingExampleCsvFilePath);
            createTemporaryTracingFolder();
            FileWriter fileWriter = new FileWriter(getTemporaryTracingFilePath());
            CSVWriter csvWriter = new CSVWriter(fileWriter);

            // Write the header back to the CSV file
            csvWriter.writeNext(csvRows.get(0));

            // Write just one valid record using the provided date
            String[] firstRecord = csvRows.get(1);
            // date: an available date to upload the CSV file
            firstRecord[0] = date.toString();
            // purpose_id: empty in this error case
            firstRecord[1] = "";
            // token_id: a random UUID is accepted
            firstRecord[2] = UUID.randomUUID().toString();
            // status: an HTTP response code, it can be taken from the example
            // requests_count: how many requests have been tracked
            firstRecord[4] = String.valueOf(RandomGenerator.getDefault().nextInt(
                    randomRequestCountFrom, randomRequestCountTo + 1)
            );
            csvWriter.writeNext(firstRecord);
            csvWriter.close();

        } catch (IOException ex) {
            throw new RuntimeException("There was an error while generating the csv file: " + ex);
        }
    }

    public void generateTemporaryCsvWithAllWrongFields() {
        try {
            List<String[]> csvRows = readCsvRows(tracingExampleCsvFilePath);
            createTemporaryTracingFolder();
            FileWriter fileWriter = new FileWriter(getTemporaryTracingFilePath());
            CSVWriter csvWriter = new CSVWriter(fileWriter);

            // Write the header back to the CSV file
            csvWriter.writeNext(csvRows.get(0));

            // Write just one valid record using the provided date
            String[] firstRecord = csvRows.get(1);
            // date: an old date different from the current available date
            firstRecord[0] = "2020-01-01";
            // purpose_id: empty in this error case
            firstRecord[1] = "";
            // token_id with wrong format
            firstRecord[2] = "1";
            // status: an invalid HTTP response code
            firstRecord[3] = "600";
            // requests_count wrong format
            firstRecord[4] = "x";
            csvWriter.writeNext(firstRecord);
            csvWriter.close();

        } catch (IOException ex) {
            throw new RuntimeException("There was an error while generating the csv file: " + ex);
        }
    }



    public void fixAllTheFieldsOfTemporaryCsv(LocalDate date) {
        try {
            List<String[]> csvRows = readCsvRows(tracingExampleCsvFilePath);
            List<String[]> preparedRows = readCsvRows(getTemporaryTracingFilePath());
            createTemporaryTracingFolder();
            FileWriter fileWriter = new FileWriter(getTemporaryTracingFilePath());
            CSVWriter csvWriter = new CSVWriter(fileWriter);

            // Write the header back to the CSV file
            csvWriter.writeNext(csvRows.remove(0));

            // Write just one valid record using the provided date
            String[] firstRecord = preparedRows.get(1);
            // date: the current reference date
            firstRecord[0] = date.toString();
            // purpose_id: fix with a valid purpose ID
            firstRecord[1] = csvRows.get(0)[1];
            // token_id with valid format
            firstRecord[2] = UUID.randomUUID().toString();
            // status: a valid HTTP response code
            firstRecord[3] = "200";
            // requests_count with valid format
            firstRecord[4] = "10";
            csvWriter.writeNext(firstRecord);
            csvWriter.close();

        } catch (IOException ex) {
            throw new RuntimeException("There was an error while generating the csv file: " + ex);
        }
    }

    public void generateValidTemporaryCsvWithNotCompliantPurposeId(LocalDate date) {
        try {
            List<String[]> csvRows = readCsvRows(tracingExampleCsvFilePath);
            createTemporaryTracingFolder();
            FileWriter fileWriter = new FileWriter(getTemporaryTracingFilePath());
            CSVWriter csvWriter = new CSVWriter(fileWriter);
            boolean notCompliantPurposeIdPresent = false;

            // Write the header back to the CSV file
            csvWriter.writeNext(csvRows.remove(0));

            for (String[] nextLine : csvRows) {
                // date: an available date to upload the CSV file
                nextLine[0] = date.toString();
                // purpose_id has to be not compliant, so existing but not related to the user who sent the csv
                if (!notCompliantPurposeIdPresent) {
                    if ("dev".equals(envProfile)) {
                        nextLine[1] = "90023b80-7bc3-4de6-aaed-5ccf3f5d8031";

                    } else if ("qa".equals(envProfile)) {
                        nextLine[1] = "f47ac10b-58cc-4372-a567-0e02b2c3d479";
                    } else {
                        nextLine[1] = "";
                    }
                    notCompliantPurposeIdPresent = true;
                }
                // token_id: a random UUID is accepted
                nextLine[2] = UUID.randomUUID().toString();
                // status: an HTTP response code, it can be taken from the example
                // requests_count: how many requests have been tracked
                nextLine[4] = String.valueOf(RandomGenerator.getDefault().nextInt(
                        randomRequestCountFrom, randomRequestCountTo + 1)
                );
                csvWriter.writeNext(nextLine);
            }
            csvWriter.close();

        } catch (IOException ex) {
            throw new RuntimeException("There was an error while generating the csv file: " + ex);
        }
    }

    public void generateTemporaryCsvWithSomeRecordsAndErrorOnHttpCode(LocalDate date) {
        try {
            List<String[]> csvRows = readCsvRows(tracingExampleCsvFilePath);
            createTemporaryTracingFolder();
            FileWriter fileWriter = new FileWriter(getTemporaryTracingFilePath());
            CSVWriter csvWriter = new CSVWriter(fileWriter);

            // Write the header back to the CSV file
            csvWriter.writeNext(csvRows.remove(0));

            for (int i = 0; i < csvRows.size(); i++) {
                String[] nextLine = csvRows.get(i);
                // date: an available date to upload the CSV file
                nextLine[0] = date.toString();
                // purpose_id has to be taken from the valid example provided
                // token_id: a random UUID is accepted
                nextLine[2] = UUID.randomUUID().toString();
                // status: an HTTP response code, it can be taken from the example
                if (i == 5) {
                    // A record with a not existing HTTP response code
                    nextLine[3] = "600";
                }
                // requests_count: how many requests have been tracked
                nextLine[4] = String.valueOf(RandomGenerator.getDefault().nextInt(
                        randomRequestCountFrom, randomRequestCountTo + 1)
                );
                csvWriter.writeNext(nextLine);
            }
            csvWriter.close();

        } catch (IOException ex) {
            throw new RuntimeException("There was an error while generating the csv file: " + ex);
        }
    }

    public void emptyFirstPurposeIdFieldOfTheTemporaryCsv() {
        try {
            List<String[]> csvRows = readCsvRows(tracingExampleCsvFilePath);
            createTemporaryTracingFolder();
            FileWriter fileWriter = new FileWriter(getTemporaryTracingFilePath());
            CSVWriter csvWriter = new CSVWriter(fileWriter);

            // Write the header back to the CSV file
            csvWriter.writeNext(csvRows.get(0));

            // Write just one valid record using the provided date
            String[] firstRecord = csvRows.get(1);
            // purpose_id: empty in this error case
            firstRecord[1] = "";
            // All the other fields are unchanged
            csvWriter.writeNext(firstRecord);
            csvWriter.close();

        } catch (IOException ex) {
            throw new RuntimeException("There was an error while generating the csv file: " + ex);
        }
    }

    public void generateValidTemporaryCsvOfSize(LocalDate date, int megabyte) {
        try {
            List<String[]> csvRows = readCsvRows(tracingExampleCsvFilePath);
            createTemporaryTracingFolder();
            FileWriter fileWriter = new FileWriter(getTemporaryTracingFilePath());
            CSVWriter csvWriter = new CSVWriter(fileWriter);

            // Write the header back to the CSV file
            csvWriter.writeNext(csvRows.remove(0));

            // Estimated and target file size are in bytes. The header is 57 bytes and a record is often 102.
            // The formula to estimate the size is: 57 + lineCount * 102 bytes.
            int estimatedFileSize = 57;
            int targetFileSize = megabyte * 1024 * 1024;
            int lineCount = 0;

            while (estimatedFileSize <= targetFileSize) {
                for (String[] nextLine : csvRows) {
                    // date: an available date to upload the CSV file
                    nextLine[0] = date.toString();
                    // purpose_id has to be taken from the valid example provided
                    // token_id: a random UUID is accepted
                    nextLine[2] = UUID.randomUUID().toString();
                    // status: an HTTP response code, it can be taken from the example
                    // requests_count: how many requests have been tracked
                    nextLine[4] = String.valueOf(RandomGenerator.getDefault().nextInt(
                            randomRequestCountFrom, randomRequestCountTo + 1)
                    );
                    csvWriter.writeNext(nextLine);
                    lineCount++;
                }
                estimatedFileSize = 57 + lineCount * 102;
            }
            csvWriter.close();

        } catch (IOException ex) {
            throw new RuntimeException("There was an error while generating the csv file: " + ex);
        }
    }

    public Resource getCsvFile(String file) {
        return switch (file.trim().toLowerCase()) {
            case "errato_header_campo_mancante" -> resourceLoader.getResource("file:" + tracingErrorCsvPath + "/tracing-error-header-missing-field.csv");
            case "errato_header_nome_campo" -> resourceLoader.getResource("file:" + tracingErrorCsvPath + "/tracing-error-header-wrong-field.csv");
            case "errato_header_doppia_virgola" -> resourceLoader.getResource("file:" + tracingErrorCsvPath + "/tracing-error-header-consecutive-commas.csv");
            case "preparato" -> resourceLoader.getResource("file:" + tracingTemporaryCsvPath + TracingSteps.getTemporaryTracingFileName());
            default -> throw new IllegalStateException("Unexpected value: " + file.trim().toLowerCase());
        };
    }
}
