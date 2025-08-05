package it.pagopa.pari.cucumber.steps.registrobeni;

import com.opencsv.CSVWriter;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.pari.cucumber.utils.ApiClientContext;
import it.pagopa.pari.generated.openapi.clients.registro.beni.model.CsvDTO;
import it.pagopa.pari.generated.openapi.clients.registro.beni.model.RegisterUploadResponseDTO;
import it.pagopa.pari.generated.openapi.clients.registro.beni.model.UploadDTO;
import it.pagopa.pari.generated.openapi.clients.registro.beni.model.UploadsListDTO;
import org.junit.platform.commons.util.StringUtils;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpStatusCodeException;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RegistroBeniProductsUploadSteps {
    private final ApiClientContext apiClientContext;
    private final ResourceLoader resourceLoader;

    private RegisterUploadResponseDTO uploadResponseDTO;
    private CsvDTO csvDTO;
    private UploadsListDTO uploadsListDTO;
    private UploadDTO lastUpload;

    public RegistroBeniProductsUploadSteps(ApiClientContext apiClientContext, ResourceLoader resourceLoader) {
        this.apiClientContext = apiClientContext;
        this.resourceLoader = resourceLoader;
    }

    @When("viene caricato un file NON csv con categoria: {string} e dati:")
    public void generateCsvWithWrongExtension(String categoria, List<Map<String, String>> dataCsv) throws Exception {
        Resource notCsvFile = generaCsv(dataCsv, ".txt");
        uploadResponseDTO = apiClientContext.getRegisterPortalOperationClient().uploadProductList(categoria, notCsvFile);
    }

    @When("viene caricato il csv con categoria: {string} e dati:")
    public void vieneGeneratoIlCsv(String categoria, List<Map<String, String>> dataCsv) throws Exception {
        Resource csvFile = generaCsv(dataCsv, ".csv");
        uploadResponseDTO = apiClientContext.getRegisterPortalOperationClient().uploadProductList(categoria, csvFile);
        // Viene aggiunto un delay per dare il tempo al csv di essere validato
        Thread.sleep(1000);
    }

    @When("viene verificato il csv con categoria: {string} e dati:")
    public void verifyCsv(String category, List<Map<String, String>> dataCsv) throws Exception {
        Resource csvFile = generaCsv(dataCsv, ".csv");
        uploadResponseDTO = apiClientContext.getRegisterPortalOperationClient().verifyProductList(category, csvFile);
    }

    @When("viene recuperato il report di errore appena generato")
    public void retrieveErrorReport() {
        assertNotNull(uploadResponseDTO);
        assertNotNull(uploadResponseDTO.getProductFileId());
        csvDTO = apiClientContext.getRegisterPortalOperationClient().downloadErrorReport(uploadResponseDTO.getProductFileId());
    }

    @Then("il report è correttamente popolato")
    public void verifyReportSuccess() {
        assertNotNull(csvDTO);
        assertTrue(StringUtils.isNotBlank(csvDTO.getData()));
    }

    @When("si tenta di recuperare un report di errore {string} e si ottiene status code {int}")
    public void verifyReportError(String productFileId, int expectedStatusCode) {
        String reportId = "NOT_VALID".equals(productFileId) ? UUID.randomUUID().toString() : "invalid_product_file";
        HttpStatus httpStatus = null;
        try {
            apiClientContext.getRegisterPortalOperationClient().downloadErrorReport(reportId);
        } catch (HttpStatusCodeException e) {
            httpStatus = e.getStatusCode();
        }
        assertEquals(HttpStatus.valueOf(expectedStatusCode), httpStatus);
    }

    private Resource generaCsv(List<Map<String, String>> tableRow, String suffix) throws Exception {
        Path tempPath = Files.createTempFile("products-", suffix);
        File tempFile = tempPath.toFile();
        tempFile.deleteOnExit();

        Set<String> header = tableRow.get(0).keySet();

        try (FileWriter fileWriter = new FileWriter(tempFile);
             CSVWriter csvWriter = new CSVWriter(fileWriter, ';',
                     CSVWriter.NO_QUOTE_CHARACTER,
                     CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                     CSVWriter.DEFAULT_LINE_END)) {
            csvWriter.writeNext(header.toArray(new String[0]));

            for (Map<String, String> row : tableRow) {
                String[] values = header.stream()
                        .map(h -> row.getOrDefault(h, ""))
                        .toArray(String[]::new);
                csvWriter.writeNext(values);
            }

        }
        return new FileSystemResource(tempFile);
    }

    @Then("si verifica che la risposta abbia:")
    public void verifyResponse(DataTable dataTable) {
        Map<String, String> expectedResults = dataTable.asMap();
        assertEquals(expectedResults.get("errorKey"), uploadResponseDTO.getErrorKey(), "Mismatch on errorKey!");
        assertEquals(expectedResults.get("status"), uploadResponseDTO.getStatus(), "Mismatch on status field!");
        verifyProductFileId(expectedResults.get("productFileId"));
    }

    private void verifyProductFileId(String expectedValue) {
        if ("NOT_NULL".equals(expectedValue)) {
            assertNotNull(uploadResponseDTO.getProductFileId());
            assertFalse(uploadResponseDTO.getProductFileId().isEmpty());
        } else {
            assertEquals(expectedValue, uploadResponseDTO.getProductFileId(), "Mismatch on productFileId!");
        }
    }

    @Given("viene caricato un file csv di peso maggiore a quello consentito")
    public void uploadLargeCSV() {
        Resource csvFile = resourceLoader.getResource("file:src/main/resources/registroBeni/large-csv-up-two-mb.csv");
        uploadResponseDTO = apiClientContext.getRegisterPortalOperationClient().uploadProductList("WASHINGMACHINES", csvFile);
    }

    @Given("viene caricato un file csv contente più righe di quelle accettate")
    public void uploadLongCSV() {
        Resource csvFile = resourceLoader.getResource("file:src/main/resources/registroBeni/long-csv-with-101-row.csv");
        uploadResponseDTO = apiClientContext.getRegisterPortalOperationClient().uploadProductList("WASHINGMACHINES", csvFile);

    }

    @When("si recupera l'ultimo caricamento effettuato dall'utenza")
    public void retrieveLastUpload() {
        uploadsListDTO = apiClientContext.getRegisterPortalOperationClient().getProductFilesList(0, 10, null);
        lastUpload = uploadsListDTO.getContent().stream()
                .filter(x -> StringUtils.isNotBlank(x.getDateUpload()))
                .max(Comparator.comparing(UploadDTO::getDateUpload))
                .orElse(null);
    }

    @Then("si verifica che i prodotti non siano stati aggiunti in quanto già caricati da un produttore diverso")
    public void verifyProductsAreNotLoaded() {
        assertNotNull(lastUpload);
        csvDTO = apiClientContext.getRegisterPortalOperationClient().downloadErrorReport(lastUpload.getProductFileId());
        assertNotNull(csvDTO);
        assertNotNull(csvDTO.getData());
        assertTrue(csvDTO.getData().contains("Prodotto associato ad un altro produttore"));
    }

    @Then("si verifica che nella lista dei caricamenti ne sia stato aggiunto uno nuovo")
    public void verifyUploadsListResponse() {
        uploadsListDTO = apiClientContext.getRegisterPortalOperationClient().getProductFilesList(0, 10, null);
        assertNotNull(uploadsListDTO);
        assertNotNull(uploadsListDTO.getTotalElements());
        assertTrue(uploadsListDTO.getTotalElements() > 0);
        assertTrue(
                uploadsListDTO.getContent().stream()
                        .anyMatch(x -> {
                            LocalDateTime uploadTime = LocalDateTime.parse(x.getDateUpload());
                            LocalDateTime now = LocalDateTime.now();
                            Duration diff = Duration.between(uploadTime, now);
                            return !uploadTime.isAfter(now) && diff.toMinutes() < 1;
                        })
        );    }


}
